/*******************************************************************************
 * Copyright (C) 2025 the Eclipse BaSyx Authors
 * 
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * 
 * SPDX-License-Identifier: MIT
 ******************************************************************************/

package org.eclipse.digitaltwin.basyx.aasservice.backend;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetInformation;
import org.eclipse.digitaltwin.aas4j.v3.model.Key;
import org.eclipse.digitaltwin.aas4j.v3.model.KeyTypes;
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.aas4j.v3.model.Resource;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultAssetInformation;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultReference;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultResource;
import org.eclipse.digitaltwin.basyx.aasservice.AasServiceOperations;
import org.eclipse.digitaltwin.basyx.core.exceptions.CollidingSubmodelReferenceException;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.exceptions.FileDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.exceptions.FileHandlingException;
import org.eclipse.digitaltwin.basyx.core.filerepository.FileMetadata;
import org.eclipse.digitaltwin.basyx.core.filerepository.FileRepository;
import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.LimitOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.UnwindOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.repository.support.MappingMongoEntityInformation;

import com.mongodb.client.result.UpdateResult;

/**
 * MongoDB implementation of the {@link AasServiceOperations}
 * 
 * @author mateusmolina
 */
public class MongoDBAasServiceOperations implements AasServiceOperations {

    private static final String SMREF_KEY = "submodels";
    private static final String ASSETINFORMATION_KEY = "assetInformation";

    private final MongoOperations mongoOperations;
    private final String collectionName;
    private final FileRepository fileRepository;

    public MongoDBAasServiceOperations(MongoOperations mongoOperations, FileRepository fileRepository, MappingMongoEntityInformation<AssetAdministrationShell, String> mappingMongoEntityInformation) {
        this.mongoOperations = mongoOperations;
        this.fileRepository = fileRepository;

        collectionName = mappingMongoEntityInformation.getCollectionName();
    }

    @Override
    public CursorResult<List<Reference>> getSubmodelReferences(String aasId, PaginationInfo pInfo) {
        Integer limit = pInfo.getLimit();
        String cursor = pInfo.getCursor();

        MatchOperation matchAasId = Aggregation.match(Criteria.where("_id").is(aasId));

        UnwindOperation unwindSubmodels = Aggregation.unwind(SMREF_KEY);

        ProjectionOperation projectReference = Aggregation.project().and("submodels.keys").as("keys").and("submodels.type").as("type");

        Aggregation aggregation;
        List<AggregationOperation> aggregationOps = new ArrayList<>();
        aggregationOps.add(matchAasId);
        aggregationOps.add(unwindSubmodels);
        aggregationOps.add(projectReference);

        if (cursor != null && !cursor.isEmpty()) {
            MatchOperation matchCursor = Aggregation.match(Criteria.where("keys.value").gt(cursor));
            aggregationOps.add(matchCursor);
        }

        if (limit != null && limit > 0) {
            LimitOperation limitOperation = Aggregation.limit(limit);
            aggregationOps.add(limitOperation);
        }

        aggregation = Aggregation.newAggregation(aggregationOps);
        AggregationResults<DefaultReference> results = mongoOperations.aggregate(aggregation, collectionName, DefaultReference.class);
        List<DefaultReference> submodelReferences = results.getMappedResults();

        String nextCursor = null;
        if (!submodelReferences.isEmpty()) {
            Reference lastReference = submodelReferences.get(submodelReferences.size() - 1);
            nextCursor = extractSubmodelId(lastReference);
        }

        return new CursorResult<>(nextCursor, new ArrayList<>(submodelReferences));
    }

    @Override
    public void addSubmodelReference(String aasId, Reference submodelReference) {
        String newKeyValue = submodelReference.getKeys().get(0).getValue();

        Query query = new Query(new Criteria().andOperator(Criteria.where("_id").is(aasId), Criteria.where(SMREF_KEY).not().elemMatch(Criteria.where("keys.0.value").is(newKeyValue))));

        Update update = new Update().push(SMREF_KEY, submodelReference);
        UpdateResult result = mongoOperations.updateFirst(query, update, collectionName);

        if (result.getMatchedCount() == 0)
            throw new CollidingSubmodelReferenceException(newKeyValue);
    }

    @Override
    public void removeSubmodelReference(String aasId, String submodelId) {
        Query query = new Query(Criteria.where("_id").is(aasId));

        Update update = new Update().pull(SMREF_KEY, Query.query(Criteria.where("keys.value").is(submodelId)).getQueryObject());

        UpdateResult result = mongoOperations.updateFirst(query, update, collectionName);

        if (result.getModifiedCount() == 0)
            throw new ElementDoesNotExistException(submodelId);
    }

    @Override
    public void setAssetInformation(String aasId, AssetInformation aasInfo) {
        Query query = new Query(Criteria.where("_id").is(aasId));

        Update update = new Update().set(ASSETINFORMATION_KEY, aasInfo);

        mongoOperations.updateFirst(query, update, collectionName);
    }

    @Override
    public AssetInformation getAssetInformation(String aasId) {
        Aggregation aggregation = Aggregation.newAggregation(Aggregation.match(Criteria.where("_id").is(aasId)), Aggregation.replaceRoot().withValueOf("$" + ASSETINFORMATION_KEY));

        AggregationResults<DefaultAssetInformation> results = mongoOperations.aggregate(aggregation, collectionName, DefaultAssetInformation.class // Use concrete type
        );

        return results.getUniqueMappedResult();
    }

    @Override
    public File getThumbnail(String aasId) {
        Resource resource = getAssetInformation(aasId).getDefaultThumbnail();

        try {
            return getResourceContent(resource);
        } catch (NullPointerException e) {
            throw new FileDoesNotExistException();
        } catch (IOException e) {
            throw new FileHandlingException("Exception occurred while creating file from the InputStream." + e.getMessage());
        }
    }

    @Override
    public void setThumbnail(String aasId, String fileName, String contentType, InputStream inputStream) {
        FileMetadata thumbnailMetadata = new FileMetadata(fileName, contentType, inputStream);

        if (fileRepository.exists(thumbnailMetadata.getFileName()))
            fileRepository.delete(thumbnailMetadata.getFileName());

        String filePath = fileRepository.save(thumbnailMetadata);

        setAssetInformation(aasId, configureAssetInformationThumbnail(getAssetInformation(aasId), contentType, filePath));
    }

    @Override
    public void deleteThumbnail(String aasId) {
        try {
            String thumbnailPath = getAssetInformation(aasId).getDefaultThumbnail().getPath();
            fileRepository.delete(thumbnailPath);
        } catch (NullPointerException e) {
            throw new FileDoesNotExistException();
        } finally {
            setAssetInformation(aasId, configureAssetInformationThumbnail(getAssetInformation(aasId), "", ""));
        }
    }

    private static String extractSubmodelId(Reference reference) {
        List<Key> keys = reference.getKeys();

        for (Key key : keys) {
            if (key.getType() == KeyTypes.SUBMODEL) {
                return key.getValue();
            }
        }

        return "";
    }

    private static AssetInformation configureAssetInformationThumbnail(AssetInformation assetInformation, String contentType, String filePath) {
        Resource resource = new DefaultResource();
        resource.setContentType(contentType);
        resource.setPath(filePath);
        assetInformation.setDefaultThumbnail(resource);
        return assetInformation;
    }

    private File getResourceContent(Resource resource) throws IOException {
        String filePath = resource.getPath();

        InputStream fileIs = fileRepository.find(filePath);
        byte[] content = fileIs.readAllBytes();
        fileIs.close();

        createOutputStream(filePath, content);

        return new java.io.File(filePath);
    }

    private void createOutputStream(String filePath, byte[] content) throws IOException {

        try (OutputStream outputStream = new FileOutputStream(filePath)) {
            outputStream.write(content);
        } catch (IOException e) {
            throw new FileHandlingException("Exception occurred while creating OutputStream from byte[]." + e.getMessage());
        }

    }

}

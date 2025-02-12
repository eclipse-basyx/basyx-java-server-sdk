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
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
import org.eclipse.digitaltwin.basyx.core.filerepository.FileRepository;
import org.eclipse.digitaltwin.basyx.core.filerepository.FileRepositoryHelper;
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
import org.springframework.lang.NonNull;

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

    public MongoDBAasServiceOperations(MongoOperations mongoOperations, FileRepository fileRepository) {
        this.mongoOperations = mongoOperations;
        this.fileRepository = fileRepository;
        collectionName = mongoOperations.getCollectionName(AssetAdministrationShell.class);
    }


    @Override
    public CursorResult<List<Reference>> getSubmodelReferences(@NonNull String aasId, @NonNull PaginationInfo pInfo) throws ElementDoesNotExistException {
        MatchOperation matchAasId = Aggregation.match(Criteria.where("_id").is(aasId));
        UnwindOperation unwindSubmodels = Aggregation.unwind(SMREF_KEY);
        ProjectionOperation projectReference = Aggregation.project().and("submodels.keys").as("keys").and("submodels.type").as("type");

        List<AggregationOperation> ops = new ArrayList<>();
        ops.add(matchAasId);
        ops.add(unwindSubmodels);
        ops.add(projectReference);

        String cursor = pInfo.getCursor();
        if (cursor != null && !cursor.isEmpty()) {
            ops.add(Aggregation.match(Criteria.where("keys.value").gt(cursor)));
        }
        if (pInfo.getLimit() != null && pInfo.getLimit() > 0) {
            ops.add(new LimitOperation(pInfo.getLimit()));
        }

        Aggregation aggregation = Aggregation.newAggregation(ops);
        AggregationResults<DefaultReference> results = mongoOperations.aggregate(aggregation, collectionName, DefaultReference.class);
        List<DefaultReference> refs = results.getMappedResults();

        if (refs.isEmpty() && !existsAas(aasId))
            throw new ElementDoesNotExistException(aasId);

        String nextCursor = null;
        if (!refs.isEmpty()) {
            Reference last = refs.get(refs.size() - 1);
            nextCursor = extractSubmodelId(last);
        }

        return new CursorResult<>(nextCursor, new ArrayList<>(refs));
    }

    @Override
    public void addSubmodelReference(@NonNull String aasId, @NonNull Reference submodelReference) throws ElementDoesNotExistException, CollidingSubmodelReferenceException {
        String newKeyValue = submodelReference.getKeys().get(0).getValue();
        Query query = new Query(new Criteria().andOperator(Criteria.where("_id").is(aasId), Criteria.where(SMREF_KEY).not().elemMatch(Criteria.where("keys.0.value").is(newKeyValue))));
        Update update = new Update().push(SMREF_KEY, submodelReference);
        UpdateResult result = mongoOperations.updateFirst(query, update, collectionName);

        if (result.getMatchedCount() != 0)
            return;

        if (!existsAas(aasId))
            throw new ElementDoesNotExistException(aasId);

        throw new CollidingSubmodelReferenceException(newKeyValue);
    }

    @Override
    public void removeSubmodelReference(@NonNull String aasId, @NonNull String submodelId) throws ElementDoesNotExistException {
        Query query = new Query(Criteria.where("_id").is(aasId));
        Update update = new Update().pull(SMREF_KEY, Query.query(Criteria.where("keys.value").is(submodelId)).getQueryObject());
        UpdateResult result = mongoOperations.updateFirst(query, update, collectionName);

        if (result.getModifiedCount() != 0)
            return;

        if (!existsAas(aasId))
            throw new ElementDoesNotExistException(aasId);

        throw new ElementDoesNotExistException(submodelId);
    }

    @Override
    public void setAssetInformation(@NonNull String aasId, @NonNull AssetInformation aasInfo) {
        Query query = new Query(Criteria.where("_id").is(aasId));

        Update update = new Update().set(ASSETINFORMATION_KEY, aasInfo);

        UpdateResult result = mongoOperations.updateFirst(query, update, collectionName);

        // Second check for the case where the update was not performed because the
        // aasInfo is the
        // same as the existing one
        if (result.getModifiedCount() == 0 && !existsAas(aasId))
            throw new ElementDoesNotExistException(aasId);
    }

    @Override
    public AssetInformation getAssetInformation(@NonNull String aasId) {
        Aggregation aggregation = Aggregation.newAggregation(Aggregation.match(Criteria.where("_id").is(aasId)), Aggregation.replaceRoot().withValueOf("$" + ASSETINFORMATION_KEY));

        AggregationResults<DefaultAssetInformation> results = mongoOperations.aggregate(aggregation, collectionName, DefaultAssetInformation.class // Use concrete type
        );

        DefaultAssetInformation aasInfo = results.getUniqueMappedResult();

        if (aasInfo == null)
            throw new ElementDoesNotExistException(aasId);

        return aasInfo;
    }

    @Override
    public File getThumbnail(@NonNull String aasId) {
        return FileRepositoryHelper.fetchAndStoreFileLocally(fileRepository, getThumbnailResourcePathOrThrow(getAssetInformation(aasId)));
    }

    @Override
    public void setThumbnail(@NonNull String aasId, @NonNull String fileName, @NonNull String contentType, @NonNull InputStream inputStream) {
        String filePath = FileRepositoryHelper.saveOrOverwriteFile(fileRepository, fileName, contentType, inputStream);
        setAssetInformation(aasId, configureAssetInformationThumbnail(getAssetInformation(aasId), contentType, filePath));
    }

    @Override
    public void deleteThumbnail(@NonNull String aasId) {
        AssetInformation assetInformation = getAssetInformation(aasId);
        FileRepositoryHelper.removeFileIfExists(fileRepository, getThumbnailResourcePathOrThrow(assetInformation));
        setAssetInformation(aasId, configureAssetInformationThumbnail(assetInformation, "", ""));
    }

    private boolean existsAas(String aasId) {
        return mongoOperations.exists(new Query(Criteria.where("_id").is(aasId)), AssetAdministrationShell.class, collectionName);
    }

    private String getThumbnailResourcePathOrThrow(AssetInformation assetInformation) {
        return Optional.ofNullable(assetInformation).map(AssetInformation::getDefaultThumbnail).map(Resource::getPath).orElseThrow(FileDoesNotExistException::new);
    }
    private static AssetInformation configureAssetInformationThumbnail(AssetInformation assetInformation, String contentType, String filePath) {
        Resource resource = new DefaultResource();
        resource.setContentType(contentType);
        resource.setPath(filePath);
        assetInformation.setDefaultThumbnail(resource);
        return assetInformation;
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
}

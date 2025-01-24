/*******************************************************************************
 * Copyright (C) 2024 the Eclipse BaSyx Authors
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

package org.eclipse.digitaltwin.basyx.aasrepository.backend.mongodb;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetInformation;
import org.eclipse.digitaltwin.aas4j.v3.model.Key;
import org.eclipse.digitaltwin.aas4j.v3.model.KeyTypes;
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultReference;
import org.eclipse.digitaltwin.basyx.aasrepository.backend.AasServiceBackend;
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
 * MongoDB implementation of the {@link AasServiceBackend}
 * 
 * @author mateusmolina
 */
public class MongoDBAasServiceBackend implements AasServiceBackend {

    private static final String SMREF_KEY = "submodels";

    private final MongoOperations mongoOperations;
    private final String collectionName;

    public MongoDBAasServiceBackend(MongoOperations mongoOperations, MappingMongoEntityInformation<AssetAdministrationShell, String> mappingMongoEntityInformation) {
        this.mongoOperations = mongoOperations;

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

        Query query = new Query(Criteria.where("_id").is(aasId));

        Update update = new Update().push(SMREF_KEY, submodelReference);

        UpdateResult result = mongoOperations.updateFirst(query, update, collectionName);
    }

    @Override
    public void removeSubmodelReference(String aasId, String submodelId) {
        Query query = new Query(Criteria.where("_id").is(aasId));

        Update update = new Update().pull(SMREF_KEY, Query.query(Criteria.where("keys.value").is(submodelId)).getQueryObject());

        UpdateResult result = mongoOperations.updateFirst(query, update, collectionName);
    }

    @Override
    public void setAssetInformation(String aasId, AssetInformation aasInfo) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setAssetInformation'");
    }

    @Override
    public AssetInformation getAssetInformation(String aasId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAssetInformation'");
    }

    @Override
    public File getThumbnail(String aasId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getThumbnail'");
    }

    @Override
    public void setThumbnail(String aasId, String fileName, String contentType, InputStream inputStream) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setThumbnail'");
    }

    @Override
    public void deleteThumbnail(String aasId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteThumbnail'");
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

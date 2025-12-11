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

import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.eclipse.digitaltwin.aas4j.v3.model.*;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultAssetInformation;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultReference;
import org.eclipse.digitaltwin.basyx.core.exceptions.CollidingSubmodelReferenceException;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.lang.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * MongoDB implementation of the {@link AasOperations}
 * 
 * @author mateusmolina
 */
public class MongoDBAasOperations implements AasOperations {

    private static final String KEY_SMREF = "submodels";
    private static final String KEY_ASSETINFORMATION = "assetInformation";
    private static final String KEY_SMREF_KEY_VALUE = KEY_SMREF + ".keys.value";

    private final MongoOperations mongoOperations;
    private final String collectionName;

    public MongoDBAasOperations(MongoOperations mongoOperations) {
        this.mongoOperations = mongoOperations;
        collectionName = mongoOperations.getCollectionName(AssetAdministrationShell.class);
    }

    @Override
    public CursorResult<List<AssetAdministrationShell>> getShells(List<SpecificAssetId> assetIds, String idShort, PaginationInfo pInfo) {
        List<AggregationOperation> ops = new ArrayList<>();

        List<Criteria> criteriaList = buildAasFilterCriteria(assetIds, idShort);
        if (!criteriaList.isEmpty()) {
            ops.add(Aggregation.match(new Criteria().andOperator(criteriaList.toArray(new Criteria[0]))));
        }

        // Apply cursor (_id > cursor)
        if (hasCursor(pInfo)) {
            ops.add(Aggregation.match(Criteria.where("_id").gt(pInfo.getCursor())));
        }

        // Sort for stable pagination
        ops.add(Aggregation.sort(Sort.by(Sort.Direction.ASC, "_id")));

        // Apply limit
        if (hasLimit(pInfo)) {
            ops.add(Aggregation.limit(pInfo.getLimit()));
        }

        // Run aggregation
        Aggregation aggregation = Aggregation.newAggregation(ops);
        AggregationResults<AssetAdministrationShell> results =
                mongoOperations.aggregate(aggregation, collectionName, AssetAdministrationShell.class);

        List<AssetAdministrationShell> shells = results.getMappedResults();

        String nextCursor = shells.isEmpty()
                ? null
                : shells.get(shells.size() - 1).getId();

        return new CursorResult<>(nextCursor, shells);
    }


    @Override
    public CursorResult<List<Reference>> getSubmodelReferences(@NonNull String aasId, @NonNull PaginationInfo pInfo) throws ElementDoesNotExistException {
        List<AggregationOperation> ops = new ArrayList<>();

        ops.add(Aggregation.match(Criteria.where("_id").is(aasId)));

        if (pInfo.getCursor() != null && !pInfo.getCursor().isEmpty()) {
            Document addCursorIndex = new Document("$addFields",
                    new Document("cursorIndex", new Document("$cond", Arrays.asList(new Document("$eq", Arrays.asList(new Document("$indexOfArray", Arrays.asList("$" + KEY_SMREF_KEY_VALUE, pInfo.getCursor())), -1)), 0,
                            new Document("$add", Arrays.asList(new Document("$indexOfArray", Arrays.asList("$" + KEY_SMREF_KEY_VALUE, pInfo.getCursor())), 1))))));
            ops.add(context -> addCursorIndex);

            int limit = (pInfo.getLimit() != null && pInfo.getLimit() > 0) ? pInfo.getLimit() : Integer.MAX_VALUE;

            Document projectSlice = new Document("$project", new Document(KEY_SMREF, new Document("$slice", Arrays.asList("$" + KEY_SMREF, "$cursorIndex", limit))));
            ops.add(context -> projectSlice);
        } else {
            if (pInfo.getLimit() != null && pInfo.getLimit() > 0) {
                Document projectSlice = new Document("$project", new Document(KEY_SMREF, new Document("$slice", Arrays.asList("$" + KEY_SMREF, 0, pInfo.getLimit()))));
                ops.add(context -> projectSlice);
            }
        }

        ops.add(Aggregation.unwind(KEY_SMREF));
        ops.add(Aggregation.replaceRoot("$" + KEY_SMREF));

        Aggregation aggregation = Aggregation.newAggregation(ops);
        AggregationResults<DefaultReference> results = mongoOperations.aggregate(aggregation, collectionName, DefaultReference.class);
        List<DefaultReference> refs = results.getMappedResults();

        if (refs.isEmpty() && existsAas(aasId))
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
        Query query = new Query(new Criteria().andOperator(Criteria.where("_id").is(aasId), Criteria.where(KEY_SMREF).not().elemMatch(Criteria.where("keys.0.value").is(newKeyValue))));
        Update update = new Update().push(KEY_SMREF, submodelReference);
        UpdateResult result = mongoOperations.updateFirst(query, update, collectionName);

        if (result.getMatchedCount() != 0)
            return;

        if (existsAas(aasId))
            throw new ElementDoesNotExistException(aasId);

        throw new CollidingSubmodelReferenceException(newKeyValue);
    }

    @Override
    public void removeSubmodelReference(@NonNull String aasId, @NonNull String submodelId) throws ElementDoesNotExistException {
        Query query = new Query(Criteria.where("_id").is(aasId));
        Update update = new Update().pull(KEY_SMREF, Query.query(Criteria.where("keys.value").is(submodelId)).getQueryObject());
        UpdateResult result = mongoOperations.updateFirst(query, update, collectionName);

        if (result.getModifiedCount() != 0)
            return;

        if (existsAas(aasId))
            throw new ElementDoesNotExistException(aasId);

        throw new ElementDoesNotExistException(submodelId);
    }

    @Override
    public void setAssetInformation(@NonNull String aasId, @NonNull AssetInformation aasInfo) {
        Query query = new Query(Criteria.where("_id").is(aasId));

        Update update = new Update().set(KEY_ASSETINFORMATION, aasInfo);

        UpdateResult result = mongoOperations.updateFirst(query, update, collectionName);

        // Second check for the case where the update was not performed because the
        // aasInfo is the
        // same as the existing one
        if (result.getModifiedCount() == 0 && existsAas(aasId))
            throw new ElementDoesNotExistException(aasId);
    }

    @Override
    public AssetInformation getAssetInformation(@NonNull String aasId) {
        Aggregation aggregation = Aggregation.newAggregation(Aggregation.match(Criteria.where("_id").is(aasId)), Aggregation.replaceRoot().withValueOf("$" + KEY_ASSETINFORMATION));

        AggregationResults<DefaultAssetInformation> results = mongoOperations.aggregate(aggregation, collectionName, DefaultAssetInformation.class // Use concrete type
        );

        DefaultAssetInformation aasInfo = results.getUniqueMappedResult();

        if (aasInfo == null)
            throw new ElementDoesNotExistException(aasId);

        return aasInfo;
    }

    @Override
    public Iterable<AssetAdministrationShell> getAllAas(List<SpecificAssetId> assetIds, String idShort) {
        Query query = new Query();
        List<Criteria> criteriaList = buildAasFilterCriteria(assetIds, idShort);

        if (!criteriaList.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[0])));
        }

        return mongoOperations.find(query, AssetAdministrationShell.class, collectionName);
    }

    private boolean existsAas(String aasId) {
        return !mongoOperations.exists(new Query(Criteria.where("_id").is(aasId)), AssetAdministrationShell.class, collectionName);
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

    private List<Criteria> buildAasFilterCriteria(List<SpecificAssetId> assetIds, String idShort) {
        List<Criteria> criteriaList = new ArrayList<>();

        // Extract globalAssetId from assetIds
        List<SpecificAssetId> globalAssetIds = new ArrayList<>();
        try {
            globalAssetIds = assetIds.stream()
                    .filter(assetId -> "globalAssetId".equals(assetId.getName()))
                    .toList();

            assetIds = assetIds.stream()
                    .filter(assetId -> !"globalAssetId".equals(assetId.getName()))
                    .collect(Collectors.toList());
        } catch (Exception ignored) {}

        // Match specific assetIds (name + value pair inside array)
        if (assetIds != null && !assetIds.isEmpty()) {
            for (SpecificAssetId assetId : assetIds) {
                criteriaList.add(Criteria.where("assetInformation.specificAssetIds.name").is(assetId.getName()));
                criteriaList.add(Criteria.where("assetInformation.specificAssetIds.value").is(assetId.getValue()));
            }
        }

        // Match idShort if present
        if (idShort != null && !idShort.isEmpty()) {
            criteriaList.add(Criteria.where("idShort").is(idShort));
        }

        // Match globalAssetId if present
        for (SpecificAssetId globalAssetId : globalAssetIds) {
            criteriaList.add(Criteria.where("assetInformation.globalAssetId").is(globalAssetId.getValue()));
        }

        return criteriaList;
    }

    private static boolean hasLimit(PaginationInfo pInfo) {
        return pInfo.getLimit() != null && pInfo.getLimit() > 0;
    }

    private static boolean hasCursor(PaginationInfo pInfo) {
        return pInfo.getCursor() != null && !pInfo.getCursor().isEmpty();
    }

}

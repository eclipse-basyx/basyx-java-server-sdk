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

package org.eclipse.digitaltwin.basyx.submodelservice.backend;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bson.Document;
import org.eclipse.digitaltwin.aas4j.v3.model.Entity;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElementCollection;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElementList;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.eclipse.digitaltwin.basyx.submodelservice.backend.SubmodelOperations;
import org.eclipse.digitaltwin.basyx.submodelservice.backend.MongoFilterBuilder.MongoFilterResult;
import org.eclipse.digitaltwin.basyx.submodelservice.pathparsing.HierarchicalSubmodelElementParser;
import org.eclipse.digitaltwin.basyx.submodelservice.pathparsing.SubmodelElementIdShortHelper;
import org.eclipse.digitaltwin.basyx.submodelservice.value.SubmodelElementValue;
import org.eclipse.digitaltwin.basyx.submodelservice.value.factory.SubmodelElementValueMapperFactory;
import org.eclipse.digitaltwin.basyx.submodelservice.value.mapper.ValueMapper;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.mongodb.client.result.UpdateResult;
import org.springframework.transaction.annotation.Transactional;

/**
 * MongoDb implementation of the {@link SubmodelOperations}
 * 
 * @author mateusmolina
 */
public class MongoDbSubmodelOperations implements SubmodelOperations {

    private static final String SUBMODEL_ELEMENTS_KEY = "submodelElements";

    private final MongoOperations mongoOperations;
    private final String collectionName;

    public MongoDbSubmodelOperations(MongoOperations mongoOperations) {
        this.mongoOperations = mongoOperations;
        this.collectionName = mongoOperations.getCollectionName(Submodel.class);
    }

    @Override
    public CursorResult<List<Submodel>> getSubmodels(String semanticId, PaginationInfo pInfo) {
        List<AggregationOperation> ops = new ArrayList<>();

        ops.add(Aggregation.match(Criteria.where("semanticId.keys.value").is(semanticId)));

        if (hasCursor(pInfo)) {
            ops.add(Aggregation.match(Criteria.where("_id").gt(pInfo.getCursor())));
        }

        ops.add(Aggregation.sort(Sort.by(Sort.Direction.ASC, "_id")));

        if (hasLimit(pInfo)) {
            ops.add(Aggregation.limit(pInfo.getLimit()));
        }

        Aggregation aggregation = Aggregation.newAggregation(ops);
        AggregationResults<Submodel> results =
                mongoOperations.aggregate(aggregation, collectionName, Submodel.class);

        List<Submodel> submodels = results.getMappedResults();

        String nextCursor = submodels.isEmpty()
                ? null
                : submodels.get(submodels.size() - 1).getId();

        return new CursorResult<>(nextCursor, submodels);
    }

    @Override
    public CursorResult<List<SubmodelElement>> getSubmodelElements(String submodelId, PaginationInfo pInfo) throws ElementDoesNotExistException {
        List<AggregationOperation> ops = new ArrayList<>();

        ops.add(Aggregation.match(Criteria.where("_id").is(submodelId)));

        if (hasCursor(pInfo)) {
            Document addCursorIndex = new Document("$addFields",
                    new Document("cursorIndex", new Document("$cond", Arrays.asList(new Document("$eq", Arrays.asList(new Document("$indexOfArray", Arrays.asList("$" + SUBMODEL_ELEMENTS_KEY + ".idShort", pInfo.getCursor())), -1)), 0,
                            new Document("$add", Arrays.asList(new Document("$indexOfArray", Arrays.asList("$" + SUBMODEL_ELEMENTS_KEY + ".idShort", pInfo.getCursor())), 1))))));
            ops.add(context -> addCursorIndex);

            int limit = hasLimit(pInfo) ? pInfo.getLimit() : Integer.MAX_VALUE;

            Document projectSlice = new Document("$project", new Document(SUBMODEL_ELEMENTS_KEY, new Document("$slice", Arrays.asList("$" + SUBMODEL_ELEMENTS_KEY, "$cursorIndex", limit))));
            ops.add(context -> projectSlice);
        } else {
            if (hasLimit(pInfo)) {
                Document projectSlice = new Document("$project", new Document(SUBMODEL_ELEMENTS_KEY, new Document("$slice", Arrays.asList("$" + SUBMODEL_ELEMENTS_KEY, 0, pInfo.getLimit()))));
                ops.add(context -> projectSlice);
            }
        }

        ops.add(Aggregation.unwind(SUBMODEL_ELEMENTS_KEY));

        ops.add(Aggregation.replaceRoot("$" + SUBMODEL_ELEMENTS_KEY));

        Aggregation aggregation = Aggregation.newAggregation(ops);
        AggregationResults<SubmodelElement> results = mongoOperations.aggregate(aggregation, collectionName, SubmodelElement.class);
        List<SubmodelElement> elements = results.getMappedResults();

        if (elements.isEmpty() && !existsSubmodel(submodelId)) {
            throw new ElementDoesNotExistException(submodelId);
        }

        String nextCursor = null;
        if (!elements.isEmpty())
            nextCursor = elements.get(elements.size() - 1).getIdShort();

        return new CursorResult<>(nextCursor, elements);
    }

    @Override
    public SubmodelElement getSubmodelElement(String submodelId, String idShortPath) throws ElementDoesNotExistException {
        List<AggregationOperation> ops = MongoFilterBuilder.buildAggregationOperations(submodelId, idShortPath);
        Aggregation aggregation = Aggregation.newAggregation(ops);

        try {
            AggregationResults<SubmodelElement> results = mongoOperations.aggregate(aggregation, collectionName, SubmodelElement.class);
            SubmodelElement element = results.getUniqueMappedResult();
            if (element == null) {
                throw new ElementDoesNotExistException(idShortPath);
            }
            return element;
        } catch (Exception e) {
            throw new ElementDoesNotExistException(idShortPath);
        }
    }

    @Override
    public void createSubmodelElement(String submodelId, SubmodelElement smElement) {
        Query query = new Query(Criteria.where("_id").is(submodelId));
        Update update = new Update().push(SUBMODEL_ELEMENTS_KEY, smElement);
        UpdateResult result = mongoOperations.updateFirst(query, update, collectionName);
        if (result.getModifiedCount() == 0 && !existsSubmodel(submodelId)) {
            throw new ElementDoesNotExistException(submodelId);
        }
    }

    @Override
    public void createSubmodelElement(String submodelId, String idShortPath, SubmodelElement submodelElement) throws ElementDoesNotExistException {
        SubmodelElement parentSme = getSubmodelElement(submodelId, idShortPath);

        if (parentSme instanceof SubmodelElementList list) {
            List<SubmodelElement> submodelElements = list.getValue();
            submodelElements.add(submodelElement);
        } else if (parentSme instanceof SubmodelElementCollection collection) {
            List<SubmodelElement> submodelElements = collection.getValue();
            submodelElements.add(submodelElement);
        } else if (parentSme instanceof Entity entity) {
            List<SubmodelElement> submodelElements = entity.getStatements();
            submodelElements.add(submodelElement);
        }

        updateSubmodelElement(submodelId, idShortPath, parentSme);
    }

    @Override
    public void updateSubmodelElement(String submodelId, String idShortPath, SubmodelElement submodelElement) throws ElementDoesNotExistException {
        MongoFilterResult filterResult = MongoFilterBuilder.parse(idShortPath);

        Query query = new Query(Criteria.where("_id").is(submodelId));
        Update update = new Update().set(filterResult.key(), submodelElement);

        filterResult.filters().forEach(update::filterArray);

        UpdateResult result = mongoOperations.updateFirst(query, update, collectionName);

        if (result.getModifiedCount() == 0) {
            if (!existsSubmodel(submodelId))
                throw new ElementDoesNotExistException(submodelId);
            if (!existsSubmodelElement(submodelId, idShortPath))
                throw new ElementDoesNotExistException(idShortPath);
        }
    }

    @Override
    @Transactional
    public synchronized void deleteSubmodelElement(String submodelId, String idShortPath) throws ElementDoesNotExistException {

        Submodel submodel = getSubmodel(submodelId);
        if (!SubmodelElementIdShortHelper.isNestedIdShortPath(idShortPath)) {
            deleteFlatSubmodelElement(submodel, idShortPath);
        }else {
            deleteNestedSubmodelElement(submodel, idShortPath);
        }
        Update update = new Update().set(SUBMODEL_ELEMENTS_KEY, submodel.getSubmodelElements());
        Query query = new Query(Criteria.where("_id").is(submodelId));
        UpdateResult result = mongoOperations.updateFirst(query, update, collectionName);
        if (result.getModifiedCount() == 0) {
            if (!existsSubmodel(submodelId))
                throw new ElementDoesNotExistException(submodelId);
            if (!existsSubmodelElement(submodelId, idShortPath))
                throw new ElementDoesNotExistException(idShortPath);
        }
    }

    private Submodel getSubmodel(String submodelId) throws ElementDoesNotExistException {
        Query query = new Query(Criteria.where("_id").is(submodelId));
        Submodel submodel = mongoOperations.findOne(query, Submodel.class, collectionName);

        if (submodel == null) {
            throw new ElementDoesNotExistException(submodelId);
        }

        return submodel;
    }

    private static void deleteNestedSubmodelElement(Submodel submodel, String idShortPath) {
        HierarchicalSubmodelElementParser parser = new HierarchicalSubmodelElementParser(submodel);
        SubmodelElement sme = parser.getSubmodelElementFromIdShortPath(idShortPath);
        if (SubmodelElementIdShortHelper.isDirectParentASubmodelElementList(idShortPath)) {
            deleteNestedSubmodelElementFromList(parser, idShortPath, sme);
        } else {
            deleteNestedSubmodelElementFromCollectionOrEntity(parser, idShortPath, sme);
        }
    }
    private static void deleteNestedSubmodelElementFromList(HierarchicalSubmodelElementParser parser, String idShortPath, SubmodelElement sme) {
        String collectionId = SubmodelElementIdShortHelper.extractDirectParentSubmodelElementListIdShort(idShortPath);
        SubmodelElementList list = (SubmodelElementList) parser.getSubmodelElementFromIdShortPath(collectionId);
        list.getValue().remove(sme);
    }

    private static void deleteNestedSubmodelElementFromCollectionOrEntity(HierarchicalSubmodelElementParser parser, String idShortPath, SubmodelElement sme) {
        String collectionId = SubmodelElementIdShortHelper.extractDirectParentSubmodelElementCollectionIdShort(idShortPath);
        SubmodelElement parent = parser.getSubmodelElementFromIdShortPath(collectionId);
        if (parent instanceof SubmodelElementCollection collection) {
            collection.getValue().remove(sme);
        } else if (parent instanceof Entity entity) {
            entity.getStatements().remove(sme);
        }
    }

    private static void deleteFlatSubmodelElement(Submodel submodel, String idShortPath) throws ElementDoesNotExistException {
        int index = findIndexOfElementTobeDeleted(submodel, idShortPath);
        if (index >= 0) {
            submodel.getSubmodelElements().remove(index);
            return;
        }
        throw new ElementDoesNotExistException();
    }

    private static int findIndexOfElementTobeDeleted(Submodel submodel, String idShortPath) {
        for (SubmodelElement sme : submodel.getSubmodelElements()) {
            if (sme.getIdShort().equals(idShortPath)) {
                return submodel.getSubmodelElements().indexOf(sme);
            }
        }
        return -1;
    }

    @Override
    public void patchSubmodelElements(String submodelId, List<SubmodelElement> submodelElementList) {
        BulkOperations bulkOps = mongoOperations.bulkOps(BulkOperations.BulkMode.ORDERED, collectionName);

        for (SubmodelElement element : submodelElementList) {
            Query query = new Query(Criteria.where("_id").is(submodelId).and(SUBMODEL_ELEMENTS_KEY + ".idShort").is(element.getIdShort()));
            Update update = new Update().set(SUBMODEL_ELEMENTS_KEY + ".$", element);
            bulkOps.updateOne(query, update);
        }

        bulkOps.execute();
    }

    @Override
    public SubmodelElementValue getSubmodelElementValue(String submodelId, String smeIdShort) throws ElementDoesNotExistException {
        return SubmodelElementValueMapperFactory.create(getSubmodelElement(submodelId, smeIdShort)).getValue();
    }

    @Override
    public void setSubmodelElementValue(String submodelId, String smeIdShort, SubmodelElementValue value) throws ElementDoesNotExistException {
        SubmodelElement submodelElement = getSubmodelElement(submodelId, smeIdShort);
        ValueMapper<SubmodelElementValue> valueMapper = SubmodelElementValueMapperFactory.create(submodelElement);

        valueMapper.setValue(value);

        updateSubmodelElement(submodelId, smeIdShort, submodelElement);
    }

    private boolean existsSubmodel(String submodelId) {
        return mongoOperations.exists(new Query(Criteria.where("_id").is(submodelId)), collectionName);
    }

    private boolean existsSubmodelElement(String submodelId, String idShortPath){
        try {
            getSubmodelElement(submodelId, idShortPath);
            return true;
        } catch(ElementDoesNotExistException e) {
            return false;
        }
    }

    private static boolean hasLimit(PaginationInfo pInfo) {
        return pInfo.getLimit() != null && pInfo.getLimit() > 0;
    }

    private static boolean hasCursor(PaginationInfo pInfo) {
        return pInfo.getCursor() != null && !pInfo.getCursor().isEmpty();
    }

}

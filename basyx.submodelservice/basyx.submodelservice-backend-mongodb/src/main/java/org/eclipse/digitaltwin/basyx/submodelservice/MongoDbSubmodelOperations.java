package org.eclipse.digitaltwin.basyx.submodelservice;

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
import org.eclipse.digitaltwin.basyx.submodelservice.MongoFilterBuilder.MongoFilterResult;
import org.eclipse.digitaltwin.basyx.submodelservice.backend.SubmodelOperations;
import org.eclipse.digitaltwin.basyx.submodelservice.value.SubmodelElementValue;
import org.eclipse.digitaltwin.basyx.submodelservice.value.factory.SubmodelElementValueMapperFactory;
import org.eclipse.digitaltwin.basyx.submodelservice.value.mapper.ValueMapper;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.mongodb.client.result.UpdateResult;

public class MongoDbSubmodelOperations implements SubmodelOperations {

    private static final String SUBMODEL_ELEMENTS_KEY = "submodelElements";

    private final MongoOperations mongoOperations;
    private final String collectionName;

    public MongoDbSubmodelOperations(MongoOperations mongoOperations) {
        this.mongoOperations = mongoOperations;
        this.collectionName = mongoOperations.getCollectionName(Submodel.class);
    }

    @Override
    public CursorResult<List<SubmodelElement>> getSubmodelElements(String submodelId, PaginationInfo pInfo) throws ElementDoesNotExistException {
        List<AggregationOperation> ops = new ArrayList<>();

        ops.add(Aggregation.match(Criteria.where("_id").is(submodelId)));

        if (pInfo.getCursor() != null && !pInfo.getCursor().isEmpty()) {
            Document addCursorIndex = new Document("$addFields",
                    new Document("cursorIndex", new Document("$cond", Arrays.asList(new Document("$eq", Arrays.asList(new Document("$indexOfArray", Arrays.asList("$" + SUBMODEL_ELEMENTS_KEY + ".idShort", pInfo.getCursor())), -1)), 0,
                            new Document("$add", Arrays.asList(new Document("$indexOfArray", Arrays.asList("$" + SUBMODEL_ELEMENTS_KEY + ".idShort", pInfo.getCursor())), 1))))));
            ops.add(context -> addCursorIndex);

            int limit = (pInfo.getLimit() != null && pInfo.getLimit() > 0) ? pInfo.getLimit() : Integer.MAX_VALUE;

            Document projectSlice = new Document("$project", new Document(SUBMODEL_ELEMENTS_KEY, new Document("$slice", Arrays.asList("$" + SUBMODEL_ELEMENTS_KEY, "$cursorIndex", limit))));
            ops.add(context -> projectSlice);
        } else {
            if (pInfo.getLimit() != null && pInfo.getLimit() > 0) {
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

            throw new ElementDoesNotExistException(idShortPath);
        }
    }

    @Override
    public void deleteSubmodelElement(String submodelId, String idShortPath) throws ElementDoesNotExistException {
        MongoFilterResult filterResult = MongoFilterBuilder.parse(idShortPath);

        Query query = new Query(Criteria.where("_id").is(submodelId));
        Update update = new Update().unset(filterResult.key());

        filterResult.filters().forEach(update::filterArray);

        UpdateResult result = mongoOperations.updateFirst(query, update, collectionName);
        if (result.getModifiedCount() == 0) {
            if (!existsSubmodel(submodelId))
                throw new ElementDoesNotExistException(submodelId);
            throw new ElementDoesNotExistException(idShortPath);
        }
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

}

package org.eclipse.digitaltwin.basyx.querycore.query;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import java.util.Arrays;
import java.util.List;

/**
 * Example usage and factory class for the Query to ElasticSearch converters
 */
public class QueryConverterExample {
    
    private final QueryToElasticSearchConverter queryConverter;
    private final ElasticSearchRequestBuilder requestBuilder;
    
    public QueryConverterExample() {
        this.queryConverter = new QueryToElasticSearchConverter();
        this.requestBuilder = new ElasticSearchRequestBuilder();
    }
    
    /**
     * Example: Convert a simple equality query
     * Custom Query: { "$condition": { "$eq": [{"$field": "$aas#idShort"}, {"$strVal": "MyAAS"}] } }
     */
    public Query createSimpleEqualityQuery() {
        // Create value objects
        Value fieldValue = new Value();
        fieldValue.set$field("$aas#idShort");
        
        Value stringValue = new Value();
        stringValue.set$strVal("MyAAS");
        
        // Create logical expression with equality
        LogicalExpression condition = new LogicalExpression();
        condition.set$eq(Arrays.asList(fieldValue, stringValue));
        
        // Create query
        AASQuery customQuery = new AASQuery();
        customQuery.set$condition(condition);
        
        return queryConverter.convert(customQuery);
    }
    
    /**
     * Example: Convert a range query
     * Custom Query: { "$condition": { "$gt": [{"$field": "$sm#value"}, {"$numVal": 100}] } }
     */
    public Query createRangeQuery() {
        Value fieldValue = new Value();
        fieldValue.set$field("$sm#value");
        
        Value numValue = new Value();
        numValue.set$numVal(100.0);
        
        LogicalExpression condition = new LogicalExpression();
        condition.set$gt(Arrays.asList(fieldValue, numValue));
        
        AASQuery customQuery = new AASQuery();
        customQuery.set$condition(condition);
        
        return queryConverter.convert(customQuery);
    }
    
    /**
     * Example: Convert a complex AND query
     * Custom Query: { "$condition": { "$and": [...] } }
     */
    public Query createComplexAndQuery() {
        // First condition: name equals "Test"
        Value fieldValue1 = new Value();
        fieldValue1.set$field("$aas#idShort");
        Value stringValue1 = new Value();
        stringValue1.set$strVal("Test");
        
        LogicalExpression condition1 = new LogicalExpression();
        condition1.set$eq(Arrays.asList(fieldValue1, stringValue1));
        
        // Second condition: value > 50
        Value fieldValue2 = new Value();
        fieldValue2.set$field("$sme#value");
        Value numValue2 = new Value();
        numValue2.set$numVal(50.0);
        
        LogicalExpression condition2 = new LogicalExpression();
        condition2.set$gt(Arrays.asList(fieldValue2, numValue2));
        
        // Combine with AND
        LogicalExpression andCondition = new LogicalExpression();
        andCondition.set$and(Arrays.asList(condition1, condition2));
        
        AASQuery customQuery = new AASQuery();
        customQuery.set$condition(andCondition);
        
        return queryConverter.convert(customQuery);
    }
    
    /**
     * Example: Convert a string contains query
     * Custom Query: { "$condition": { "$contains": [{"$field": "$aas#description"}, {"$strVal": "sensor"}] } }
     */
    public Query createStringContainsQuery() {
        StringValue fieldValue = new StringValue();
        fieldValue.set$field("$aas#description");
        
        StringValue stringValue = new StringValue();
        stringValue.set$strVal("sensor");
        
        LogicalExpression condition = new LogicalExpression();
        condition.set$contains(Arrays.asList(fieldValue, stringValue));
        
        AASQuery customQuery = new AASQuery();
        customQuery.set$condition(condition);
        
        return queryConverter.convert(customQuery);
    }
    
    /**
     * Example: Create a complete SearchRequest for ElasticSearch
     */
    public SearchRequest createSearchRequest() {
        // Create a query that searches for AAS with idShort="TestAAS"
        Value fieldValue = new Value();
        fieldValue.set$field("$aas#idShort");
        
        Value stringValue = new Value();
        stringValue.set$strVal("TestAAS");
        
        LogicalExpression condition = new LogicalExpression();
        condition.set$eq(Arrays.asList(fieldValue, stringValue));
        
        AASQuery customQuery = new AASQuery();
        customQuery.set$select("id"); // Only return ID fields
        customQuery.set$condition(condition);
        
        return requestBuilder.buildSearchRequest(customQuery, "aas-index");
    }
    
    /**
     * Example: Create SearchRequest with custom field selection
     */
    public SearchRequest createSearchRequestWithCustomFields() {
        AASQuery customQuery = createSimpleQuery();
        
        List<String> sourceFields = Arrays.asList("aas.idShort", "aas.id", "aas.assetInformation");
        
        return requestBuilder.buildSearchRequestWithSources(customQuery, "aas-index", sourceFields);
    }
    
    /**
     * Example: Create SearchRequest with pagination
     */
    public SearchRequest createPaginatedSearchRequest(int page, int pageSize) {
        AASQuery customQuery = createSimpleQuery();
        
        int from = page * pageSize;
        return requestBuilder.buildSearchRequest(customQuery, "aas-index", from, pageSize);
    }
    
    private AASQuery createSimpleQuery() {
        Value fieldValue = new Value();
        fieldValue.set$field("$aas#idShort");
        
        Value stringValue = new Value();
        stringValue.set$strVal("*");
        
        LogicalExpression condition = new LogicalExpression();
        condition.set$eq(Arrays.asList(fieldValue, stringValue));
        
        AASQuery customQuery = new AASQuery();
        customQuery.set$condition(condition);
        
        return customQuery;
    }
    
    /**
     * Example: Boolean literal query
     */
    public Query createBooleanQuery(boolean value) {
        LogicalExpression condition = new LogicalExpression();
        condition.set$boolean(value);
        
        AASQuery customQuery = new AASQuery();
        customQuery.set$condition(condition);
        
        return queryConverter.convert(customQuery);
    }
    
    /**
     * Example: NOT query
     */
    public Query createNotQuery() {
        // Create inner condition: name = "Test"
        Value fieldValue = new Value();
        fieldValue.set$field("$aas#idShort");
        Value stringValue = new Value();
        stringValue.set$strVal("Test");
        
        LogicalExpression innerCondition = new LogicalExpression();
        innerCondition.set$eq(Arrays.asList(fieldValue, stringValue));
        
        // Wrap in NOT
        LogicalExpression notCondition = new LogicalExpression();
        notCondition.set$not(innerCondition);
        
        AASQuery customQuery = new AASQuery();
        customQuery.set$condition(notCondition);
        
        return queryConverter.convert(customQuery);
    }
}
package org.eclipse.digitaltwin.basyx.core.query;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;

/**
 * Converts custom Query objects to ElasticSearch QueryDSL Query objects for ElasticSearch 9.0.1
 */
public class QueryToElasticSearchConverter {
    
    private final LogicalExpressionConverter logicalExpressionConverter;
    
    public QueryToElasticSearchConverter() {
        this.logicalExpressionConverter = new LogicalExpressionConverter();
    }
    
    /**
     * Converts a custom Query object to ElasticSearch QueryDSL Query
     * 
     * @param customQuery The custom query to convert
     * @return ElasticSearch QueryDSL Query
     */
    public co.elastic.clients.elasticsearch._types.query_dsl.Query convert(AASQuery customQuery) {
        if (customQuery == null) {
            return QueryBuilders.matchAll().build()._toQuery();
        }
        
        LogicalExpression condition = customQuery.get$condition();
        if (condition == null) {
            return QueryBuilders.matchAll().build()._toQuery();
        }
        
        return logicalExpressionConverter.convert(condition);
    }
    
    /**
     * Converts a custom Query object to ElasticSearch QueryDSL Query with source filtering
     * 
     * @param customQuery The custom query to convert
     * @return ElasticSearch QueryDSL Query
     */
    public co.elastic.clients.elasticsearch._types.query_dsl.Query convertWithSelect(AASQuery customQuery) {
        // For now, we focus on the query conversion
        // Source filtering would be handled at the search request level, not in the query itself
        return convert(customQuery);
    }
}
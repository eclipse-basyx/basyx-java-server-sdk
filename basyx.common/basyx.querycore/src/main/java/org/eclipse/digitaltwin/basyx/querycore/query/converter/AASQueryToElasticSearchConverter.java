package org.eclipse.digitaltwin.basyx.querycore.query.converter;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import org.eclipse.digitaltwin.basyx.querycore.query.model.AASQuery;
import org.eclipse.digitaltwin.basyx.querycore.query.model.LogicalExpression;

/**
 * Converts custom Query objects to ElasticSearch QueryDSL Query objects for ElasticSearch 9.0.1
 */
public class AASQueryToElasticSearchConverter {
    
    private final LogicalExpressionConverter logicalExpressionConverter;
    
    public AASQueryToElasticSearchConverter() {
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

}
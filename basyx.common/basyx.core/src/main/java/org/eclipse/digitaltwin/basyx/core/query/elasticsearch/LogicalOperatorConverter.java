package org.eclipse.digitaltwin.basyx.core.query.elasticsearch;

import java.util.List;
import org.eclipse.digitaltwin.basyx.core.query.LogicalExpression;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

/**
 * Converter for logical operators (AND, OR, NOT) from AAS query structure to Elasticsearch queries
 */
public class LogicalOperatorConverter {
    
    /**
     * Converts a list of logical expressions connected with AND to an Elasticsearch query
     * 
     * @param expressions List of expressions to be combined with AND
     * @return Elasticsearch QueryBuilder representing the AND operation
     */
    public static QueryBuilder convertAndExpression(List<LogicalExpression> expressions) {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        
        for (LogicalExpression expr : expressions) {
            QueryBuilder convertedExpr = AasElasticsearchQueryConverter.convertLogicalExpression(expr);
            if (convertedExpr != null) {
                boolQuery.must(convertedExpr);
            }
        }
        
        return boolQuery;
    }
    
    /**
     * Converts a list of logical expressions connected with OR to an Elasticsearch query
     * 
     * @param expressions List of expressions to be combined with OR
     * @return Elasticsearch QueryBuilder representing the OR operation
     */
    public static QueryBuilder convertOrExpression(List<LogicalExpression> expressions) {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        
        for (LogicalExpression expr : expressions) {
            QueryBuilder convertedExpr = AasElasticsearchQueryConverter.convertLogicalExpression(expr);
            if (convertedExpr != null) {
                boolQuery.should(convertedExpr);
            }
        }
        
        // Ensure at least one should clause matches
        boolQuery.minimumShouldMatch(1);
        
        return boolQuery;
    }
    
    /**
     * Converts a NOT expression to an Elasticsearch query
     * 
     * @param expression The expression to negate
     * @return Elasticsearch QueryBuilder representing the NOT operation
     */
    public static QueryBuilder convertNotExpression(LogicalExpression expression) {
        QueryBuilder convertedExpr = AasElasticsearchQueryConverter.convertLogicalExpression(expression);
        
        if (convertedExpr != null) {
            BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
            boolQuery.mustNot(convertedExpr);
            return boolQuery;
        }
        
        return QueryBuilders.matchAllQuery();
    }
}
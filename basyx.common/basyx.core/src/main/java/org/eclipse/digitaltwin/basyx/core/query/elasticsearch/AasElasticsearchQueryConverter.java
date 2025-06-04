package org.eclipse.digitaltwin.basyx.core.query.elasticsearch;

import org.eclipse.digitaltwin.basyx.core.query.*;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

/**
 * Converter to transform AAS query structures to Elasticsearch QueryBuilder objects
 * 
 * This class provides functionality to convert Query objects from the AAS query language
 * to Elasticsearch's QueryBuilder format for executing searches in Elasticsearch.
 */
public class AasElasticsearchQueryConverter {
    
    /**
     * Converts an AAS Query object to an Elasticsearch QueryBuilder
     * 
     * @param query The AAS Query to convert
     * @return Elasticsearch QueryBuilder object
     */
    public static QueryBuilder convert(Query query) {
        if (query == null) {
            return null;
        }
        
        // The main query condition is mandatory according to the schema
        return convertLogicalExpression(query.get$condition());
    }
    
    /**
     * Converts a LogicalExpression to an Elasticsearch QueryBuilder
     * 
     * @param expression The logical expression to convert
     * @return Elasticsearch QueryBuilder
     */
    public static QueryBuilder convertLogicalExpression(LogicalExpression expression) {
        if (expression == null) {
            return null;
        }
        
        // Handle logical operators
        if (!expression.get$and().isEmpty()) {
            return LogicalOperatorConverter.convertAndExpression(expression.get$and());
        } else if (!expression.get$or().isEmpty()) {
            return LogicalOperatorConverter.convertOrExpression(expression.get$or());
        } else if (expression.get$not() != null) {
            return LogicalOperatorConverter.convertNotExpression(expression.get$not());
        } 
        
        // Handle match expressions embedded directly in logical expression
        else if (!expression.get$match().isEmpty()) {
            return MatchExpressionConverter.convertMatchExpressions(expression.get$match());
        }
        
        // Handle comparison operators
        else if (!expression.get$eq().isEmpty()) {
            return ComparisonOperatorConverter.convertEqualsExpression(expression.get$eq());
        } else if (!expression.get$ne().isEmpty()) {
            return ComparisonOperatorConverter.convertNotEqualsExpression(expression.get$ne());
        } else if (!expression.get$gt().isEmpty()) {
            return ComparisonOperatorConverter.convertGreaterThanExpression(expression.get$gt());
        } else if (!expression.get$ge().isEmpty()) {
            return ComparisonOperatorConverter.convertGreaterThanOrEqualsExpression(expression.get$ge());
        } else if (!expression.get$lt().isEmpty()) {
            return ComparisonOperatorConverter.convertLessThanExpression(expression.get$lt());
        } else if (!expression.get$le().isEmpty()) {
            return ComparisonOperatorConverter.convertLessThanOrEqualsExpression(expression.get$le());
        } 
        
        // Handle string operators
        else if (!expression.get$contains().isEmpty()) {
            return StringOperatorConverter.convertContainsExpression(expression.get$contains());
        } else if (!expression.get$startsWith().isEmpty()) {
            return StringOperatorConverter.convertStartsWithExpression(expression.get$startsWith());
        } else if (!expression.get$endsWith().isEmpty()) {
            return StringOperatorConverter.convertEndsWithExpression(expression.get$endsWith());
        } else if (!expression.get$regex().isEmpty()) {
            return StringOperatorConverter.convertRegexExpression(expression.get$regex());
        } 
        
        // Handle boolean
        else if (expression.get$boolean() != null) {
            return QueryBuilders.matchQuery("_exists_", expression.get$boolean());
        }
        
        // Default case: return a match all query
        return QueryBuilders.matchAllQuery();
    }
}
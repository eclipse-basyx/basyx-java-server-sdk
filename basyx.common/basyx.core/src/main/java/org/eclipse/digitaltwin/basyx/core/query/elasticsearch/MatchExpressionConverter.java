package org.eclipse.digitaltwin.basyx.core.query.elasticsearch;

import java.util.List;
import org.eclipse.digitaltwin.basyx.core.query.MatchExpression;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

/**
 * Converter for Match expressions from AAS query structure to Elasticsearch queries
 */
public class MatchExpressionConverter {
    
    /**
     * Converts a list of match expressions to an Elasticsearch query.
     * Match expressions in a list are implicitly AND'ed together.
     * 
     * @param expressions List of match expressions
     * @return Elasticsearch QueryBuilder
     */
    public static QueryBuilder convertMatchExpressions(List<MatchExpression> expressions) {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        
        for (MatchExpression expr : expressions) {
            QueryBuilder convertedExpr = convertMatchExpression(expr);
            if (convertedExpr != null) {
                boolQuery.must(convertedExpr);
            }
        }
        
        return boolQuery;
    }
    
    /**
     * Converts a single match expression to an Elasticsearch query
     * 
     * @param expression The match expression to convert
     * @return Elasticsearch QueryBuilder
     */
    public static QueryBuilder convertMatchExpression(MatchExpression expression) {
        if (expression == null) {
            return null;
        }
        
        // Handle nested match expressions
        if (!expression.get$match().isEmpty()) {
            return convertMatchExpressions(expression.get$match());
        }
        
        // Handle comparison operators
        if (!expression.get$eq().isEmpty()) {
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
            return QueryBuilders.termQuery("_exists_", expression.get$boolean());
        }
        
        // Default case
        return QueryBuilders.matchAllQuery();
    }
}
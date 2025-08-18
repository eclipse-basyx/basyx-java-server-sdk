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
 *****************************************************************************/

package org.eclipse.digitaltwin.basyx.querycore.query.converter;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import org.eclipse.digitaltwin.basyx.querycore.query.model.LogicalExpression;
import org.eclipse.digitaltwin.basyx.querycore.query.model.MatchExpression;

import java.util.List;

/**
 * Converts LogicalExpression objects to ElasticSearch QueryDSL Query objects
 */
public class LogicalExpressionConverter {
    
    private final MatchExpressionConverter matchExpressionConverter;
    private final ValueConverter valueConverter;
    
    public LogicalExpressionConverter() {
        this.matchExpressionConverter = new MatchExpressionConverter();
        this.valueConverter = new ValueConverter();
    }
    
    /**
     * Converts a LogicalExpression to ElasticSearch Query
     * 
     * @param logicalExpression The logical expression to convert
     * @return ElasticSearch Query
     */
    public Query convert(LogicalExpression logicalExpression) {
        if (logicalExpression == null) {
            return QueryBuilders.matchAll().build()._toQuery();
        }
        
        // Handle $and operations
        if (logicalExpression.get$and() != null && !logicalExpression.get$and().isEmpty()) {
            return convertAndExpression(logicalExpression.get$and());
        }
        
        // Handle $or operations
        if (logicalExpression.get$or() != null && !logicalExpression.get$or().isEmpty()) {
            return convertOrExpression(logicalExpression.get$or());
        }
        
        // Handle $not operations
        if (logicalExpression.get$not() != null) {
            return convertNotExpression(logicalExpression.get$not());
        }
        
        // Handle $match operations
        if (logicalExpression.get$match() != null && !logicalExpression.get$match().isEmpty()) {
            return convertMatchExpressions(logicalExpression.get$match());
        }
        
        // Handle boolean literal
        if (logicalExpression.get$boolean() != null) {
            return convertBooleanLiteral(logicalExpression.get$boolean());
        }
        
        // Handle comparison operations
        return convertComparisonOperation(logicalExpression);
    }
    
    private Query convertAndExpression(List<LogicalExpression> andExpressions) {
        BoolQuery.Builder boolBuilder = QueryBuilders.bool();
        
        for (LogicalExpression expr : andExpressions) {
            Query convertedQuery = convert(expr);
            boolBuilder.must(convertedQuery);
        }
        
        return boolBuilder.build()._toQuery();
    }
    
    private Query convertOrExpression(List<LogicalExpression> orExpressions) {
        BoolQuery.Builder boolBuilder = QueryBuilders.bool();
        
        for (LogicalExpression expr : orExpressions) {
            Query convertedQuery = convert(expr);
            boolBuilder.should(convertedQuery);
        }
        
        // Set minimum should match to 1 for OR behavior
        boolBuilder.minimumShouldMatch("1");
        
        return boolBuilder.build()._toQuery();
    }
    
    private Query convertNotExpression(LogicalExpression notExpression) {
        Query innerQuery = convert(notExpression);
        
        return QueryBuilders.bool()
            .mustNot(innerQuery)
            .build()._toQuery();
    }
    
    private Query convertMatchExpressions(List<MatchExpression> matchExpressions) {
        if (matchExpressions.size() == 1) {
            return matchExpressionConverter.convert(matchExpressions.get(0));
        }
        
        // Multiple match expressions are combined with AND
        BoolQuery.Builder boolBuilder = QueryBuilders.bool();
        for (MatchExpression matchExpr : matchExpressions) {
            Query convertedQuery = matchExpressionConverter.convert(matchExpr);
            boolBuilder.must(convertedQuery);
        }
        
        return boolBuilder.build()._toQuery();
    }
    
    private Query convertBooleanLiteral(Boolean boolValue) {
        if (boolValue) {
            return QueryBuilders.matchAll().build()._toQuery();
        } else {
            return QueryBuilders.bool().mustNot(QueryBuilders.matchAll().build()).build()._toQuery();
        }
    }
    
    private Query convertComparisonOperation(LogicalExpression logicalExpression) {
        // Handle $eq
        if (logicalExpression.get$eq() != null && logicalExpression.get$eq().size() == 2) {
            return valueConverter.convertEqualityComparison(
                logicalExpression.get$eq().get(0), 
                logicalExpression.get$eq().get(1)
            );
        }
        
        // Handle $ne
        if (logicalExpression.get$ne() != null && logicalExpression.get$ne().size() == 2) {
            return valueConverter.convertInequalityComparison(
                logicalExpression.get$ne().get(0), 
                logicalExpression.get$ne().get(1)
            );
        }
        
        // Handle $gt
        if (logicalExpression.get$gt() != null && logicalExpression.get$gt().size() == 2) {
            return valueConverter.convertRangeComparison(
                logicalExpression.get$gt().get(0), 
                logicalExpression.get$gt().get(1), 
                "gt"
            );
        }
        
        // Handle $ge
        if (logicalExpression.get$ge() != null && logicalExpression.get$ge().size() == 2) {
            return valueConverter.convertRangeComparison(
                logicalExpression.get$ge().get(0), 
                logicalExpression.get$ge().get(1), 
                "gte"
            );
        }
        
        // Handle $lt
        if (logicalExpression.get$lt() != null && logicalExpression.get$lt().size() == 2) {
            return valueConverter.convertRangeComparison(
                logicalExpression.get$lt().get(0), 
                logicalExpression.get$lt().get(1), 
                "lt"
            );
        }
        
        // Handle $le
        if (logicalExpression.get$le() != null && logicalExpression.get$le().size() == 2) {
            return valueConverter.convertRangeComparison(
                logicalExpression.get$le().get(0), 
                logicalExpression.get$le().get(1), 
                "lte"
            );
        }
        
        // Handle string operations
        return convertStringOperations(logicalExpression);
    }
    
    private Query convertStringOperations(LogicalExpression logicalExpression) {
        // Handle $contains
        if (logicalExpression.get$contains() != null && logicalExpression.get$contains().size() == 2) {
            return valueConverter.convertStringComparison(
                logicalExpression.get$contains().get(0), 
                logicalExpression.get$contains().get(1), 
                "contains"
            );
        }
        
        // Handle $starts-with
        if (logicalExpression.get$startsWith() != null && logicalExpression.get$startsWith().size() == 2) {
            return valueConverter.convertStringComparison(
                logicalExpression.get$startsWith().get(0), 
                logicalExpression.get$startsWith().get(1), 
                "starts-with"
            );
        }
        
        // Handle $ends-with
        if (logicalExpression.get$endsWith() != null && logicalExpression.get$endsWith().size() == 2) {
            return valueConverter.convertStringComparison(
                logicalExpression.get$endsWith().get(0), 
                logicalExpression.get$endsWith().get(1), 
                "ends-with"
            );
        }
        
        // Handle $regex
        if (logicalExpression.get$regex() != null && logicalExpression.get$regex().size() == 2) {
            return valueConverter.convertStringComparison(
                logicalExpression.get$regex().get(0), 
                logicalExpression.get$regex().get(1), 
                "regex"
            );
        }
        
        // Default fallback
        return QueryBuilders.matchAll().build()._toQuery();
    }
}
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
import org.eclipse.digitaltwin.basyx.querycore.query.model.MatchExpression;

import java.util.List;

/**
 * Converts MatchExpression objects to ElasticSearch QueryDSL Query objects
 */
public class MatchExpressionConverter {
    
    private final ValueConverter valueConverter;
    
    public MatchExpressionConverter() {
        this.valueConverter = new ValueConverter();
    }
    
    /**
     * Converts a MatchExpression to ElasticSearch Query
     * 
     * @param matchExpression The match expression to convert
     * @return ElasticSearch Query
     */
    public Query convert(MatchExpression matchExpression) {
        if (matchExpression == null) {
            return QueryBuilders.matchAll().build()._toQuery();
        }
        
        // Handle nested $match operations
        if (matchExpression.get$match() != null && !matchExpression.get$match().isEmpty()) {
            return convertNestedMatch(matchExpression.get$match());
        }
        
        // Handle boolean literal
        if (matchExpression.get$boolean() != null) {
            return convertBooleanLiteral(matchExpression.get$boolean());
        }
        
        // Handle comparison operations
        return convertComparisonOperation(matchExpression);
    }
    
    private Query convertNestedMatch(List<MatchExpression> matchExpressions) {
        if (matchExpressions.size() == 1) {
            return convert(matchExpressions.get(0));
        }
        
        // Multiple nested match expressions are combined with AND
        BoolQuery.Builder boolBuilder = QueryBuilders.bool();
        for (MatchExpression matchExpr : matchExpressions) {
            Query convertedQuery = convert(matchExpr);
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
    
    private Query convertComparisonOperation(MatchExpression matchExpression) {
        // Handle $eq
        if (matchExpression.get$eq() != null && matchExpression.get$eq().size() == 2) {
            return valueConverter.convertEqualityComparison(
                matchExpression.get$eq().get(0), 
                matchExpression.get$eq().get(1)
            );
        }
        
        // Handle $ne
        if (matchExpression.get$ne() != null && matchExpression.get$ne().size() == 2) {
            return valueConverter.convertInequalityComparison(
                matchExpression.get$ne().get(0), 
                matchExpression.get$ne().get(1)
            );
        }
        
        // Handle $gt
        if (matchExpression.get$gt() != null && matchExpression.get$gt().size() == 2) {
            return valueConverter.convertRangeComparison(
                matchExpression.get$gt().get(0), 
                matchExpression.get$gt().get(1), 
                "gt"
            );
        }
        
        // Handle $ge
        if (matchExpression.get$ge() != null && matchExpression.get$ge().size() == 2) {
            return valueConverter.convertRangeComparison(
                matchExpression.get$ge().get(0), 
                matchExpression.get$ge().get(1), 
                "gte"
            );
        }
        
        // Handle $lt
        if (matchExpression.get$lt() != null && matchExpression.get$lt().size() == 2) {
            return valueConverter.convertRangeComparison(
                matchExpression.get$lt().get(0), 
                matchExpression.get$lt().get(1), 
                "lt"
            );
        }
        
        // Handle $le
        if (matchExpression.get$le() != null && matchExpression.get$le().size() == 2) {
            return valueConverter.convertRangeComparison(
                matchExpression.get$le().get(0), 
                matchExpression.get$le().get(1), 
                "lte"
            );
        }
        
        // Handle string operations
        return convertStringOperations(matchExpression);
    }
    
    private Query convertStringOperations(MatchExpression matchExpression) {
        // Handle $contains
        if (matchExpression.get$contains() != null && matchExpression.get$contains().size() == 2) {
            return valueConverter.convertStringComparison(
                matchExpression.get$contains().get(0), 
                matchExpression.get$contains().get(1), 
                "contains"
            );
        }
        
        // Handle $starts-with
        if (matchExpression.get$startsWith() != null && matchExpression.get$startsWith().size() == 2) {
            return valueConverter.convertStringComparison(
                matchExpression.get$startsWith().get(0), 
                matchExpression.get$startsWith().get(1), 
                "starts-with"
            );
        }
        
        // Handle $ends-with
        if (matchExpression.get$endsWith() != null && matchExpression.get$endsWith().size() == 2) {
            return valueConverter.convertStringComparison(
                matchExpression.get$endsWith().get(0), 
                matchExpression.get$endsWith().get(1), 
                "ends-with"
            );
        }
        
        // Handle $regex
        if (matchExpression.get$regex() != null && matchExpression.get$regex().size() == 2) {
            return valueConverter.convertStringComparison(
                matchExpression.get$regex().get(0), 
                matchExpression.get$regex().get(1), 
                "regex"
            );
        }
        
        // Default fallback
        return QueryBuilders.matchAll().build()._toQuery();
    }
}
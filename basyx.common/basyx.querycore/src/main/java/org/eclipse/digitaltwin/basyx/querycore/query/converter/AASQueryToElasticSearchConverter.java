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
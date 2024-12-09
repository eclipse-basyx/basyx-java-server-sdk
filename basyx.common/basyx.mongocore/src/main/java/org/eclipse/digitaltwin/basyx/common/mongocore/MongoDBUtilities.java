/*******************************************************************************
 * Copyright (C) 2023 the Eclipse BaSyx Authors
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
 ******************************************************************************/
package org.eclipse.digitaltwin.basyx.common.mongocore;

import java.util.List;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

/**
 * 
 * @author jungjan
 *
 */
public class MongoDBUtilities {
	
	private static final String ID = "_id";

	/**
	 * Removes all documents from the specified collection.
	 * 
	 * @param template
	 * @param collection
	 */
	public static void clearCollection(MongoTemplate template, String collection) {
		template.remove(new Query(), collection);
	}
	
	/**
	 * Creates aggregation operations based on the {@link PaginationInfo}.
	 * 
	 * @param pRequest
	 * @param allAggregations
	 * 
	 * @see AggregationOperation
	 */
	public static void applyPagination(PaginationInfo pRequest, List<AggregationOperation> allAggregations) {
		
		if (pRequest.getCursor() != null) {
			allAggregations.add(Aggregation.match(Criteria.where(ID).gt(pRequest.getCursor())));
		}
		
		if (pRequest.getLimit() != null) {
			allAggregations.add(Aggregation.limit(pRequest.getLimit()));
		}
	}
	
	/**
	 * Creates sort operation criteria for ascending order sorting based on the identifier.
	 * 
	 * @param allAggregations
	 * 
	 * @see SortOperation
	 */
	public static void applySorting(List<AggregationOperation> allAggregations) {
		SortOperation sortOp = Aggregation.sort(Direction.ASC, ID);
		
		allAggregations.add(sortOp);
	}
	
}

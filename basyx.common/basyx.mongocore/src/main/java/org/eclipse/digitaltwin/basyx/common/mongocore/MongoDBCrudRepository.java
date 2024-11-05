/*******************************************************************************
 * Copyright (C) 2024 the Eclipse BaSyx Authors
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

import java.util.LinkedList;
import java.util.List;

import org.eclipse.digitaltwin.basyx.core.BaSyxCrudRepository;
import org.eclipse.digitaltwin.basyx.core.FilterResolution;
import org.eclipse.digitaltwin.basyx.core.Filter;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.repository.query.MongoEntityInformation;
import org.springframework.data.mongodb.repository.support.SimpleMongoRepository;

/**
 * The {@code MongoDBCrudRepository} class is a MongoDB-specific implementation of the {@link BaSyxCrudRepository},
 * extending the basic CRUD functionality with support for pagination and filtering using MongoDB's aggregation framework.
 * 
 * <p>This repository class allows retrieval of data with custom filtering and pagination capabilities.
 * 
 * @param <T> the type of entity that this repository manages
 * 
 * @see SimpleMongoRepository
 * @see BaSyxCrudRepository
 * 
 * @author danish
 */
public class MongoDBCrudRepository<T> extends SimpleMongoRepository<T, String> implements BaSyxCrudRepository<T>  {
	
	private MongoTemplate mongoTemplate;
	private Class<T> clazz;
	private MongoEntityInformation<T, String> metadata;
	private FilterResolution<AggregationOperation> filterResolution;

	public MongoDBCrudRepository(MongoEntityInformation<T, String> metadata, MongoTemplate mongoTemplate, Class<T> clazz) {
		super(metadata, mongoTemplate);
		
		this.metadata = metadata;
		this.mongoTemplate = mongoTemplate;
		this.clazz = clazz;
	}
	
	public MongoDBCrudRepository(MongoEntityInformation<T, String> metadata, MongoTemplate mongoTemplate, Class<T> clazz, FilterResolution<AggregationOperation> filterResolution) {
		this(metadata, mongoTemplate, clazz);
		
		this.filterResolution = filterResolution;
	}
	
	@Override
	public List<T> findAll(PaginationInfo paginationInfo, Filter filter) {
		
		List<AggregationOperation> allAggregations = new LinkedList<>();
		
		if (filterResolution != null && filter != null) {
			AggregationOperation operation = filterResolution.applyFilter(filter);
			allAggregations.add(operation);
		}
		
		MongoDBUtilities.applySorting(allAggregations);
		MongoDBUtilities.applyPagination(paginationInfo, allAggregations);
		
		AggregationResults<T> results = mongoTemplate.aggregate(Aggregation.newAggregation(allAggregations), metadata.getCollectionName(), clazz);
		
		return results.getMappedResults();
	}

}

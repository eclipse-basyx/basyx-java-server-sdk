/*******************************************************************************
 * Copyright (C) 2023 DFKI GmbH (https://www.dfki.de/en/web)
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
package org.eclipse.digitaltwin.basyx.submodelregistry.service.storage.mongodb;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.eclipse.digitaltwin.basyx.submodelregistry.model.SubmodelDescriptor;
import org.eclipse.digitaltwin.basyx.submodelregistry.service.errors.SubmodelAlreadyExistsException;
import org.eclipse.digitaltwin.basyx.submodelregistry.service.errors.SubmodelNotFoundException;
import org.eclipse.digitaltwin.basyx.submodelregistry.service.storage.SubmodelRegistryStorage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SessionScoped;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.mongodb.ClientSessionOptions;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MongoDbSubmodelRegistryStorage implements SubmodelRegistryStorage {

	// mongodb maps all id fields internally to _id
	private static final String ID = "_id";

	private final MongoTemplate template;
	
	private final String collectionName;

	@Override
	public CursorResult<List<SubmodelDescriptor>> getAllSubmodelDescriptors(@NonNull PaginationInfo pRequest) {
		List<AggregationOperation> allAggregations = new LinkedList<>();
		applySorting(allAggregations);
		applyPagination(pRequest, allAggregations);
		AggregationResults<SubmodelDescriptor> results = template.aggregate(Aggregation.newAggregation(allAggregations), collectionName, SubmodelDescriptor.class);
		List<SubmodelDescriptor> foundDescriptors = results.getMappedResults();
		String cursor = resolveCursor(pRequest, foundDescriptors);
		return new CursorResult<List<SubmodelDescriptor>>(cursor, foundDescriptors);
	}
	
	@Override
	public Set<String> clear() {
		Query query = Query.query(Criteria.where(ID).exists(true));
		query.fields().include(ID);
		List<SubmodelDescriptor> list = template.findAllAndRemove(query, SubmodelDescriptor.class, collectionName);
		return list.stream().map(SubmodelDescriptor::getId).collect(Collectors.toSet());
	}
	
	@Override
	public SubmodelDescriptor getSubmodelDescriptor(@NonNull String submodelId) throws SubmodelNotFoundException {
		SubmodelDescriptor descriptor = template.findById(submodelId, SubmodelDescriptor.class, collectionName);
		if (descriptor == null) {
			throw new SubmodelNotFoundException(submodelId);
		}
		return descriptor;
	}
	
	@Override
	public void insertSubmodelDescriptor(@NonNull SubmodelDescriptor descr) throws SubmodelAlreadyExistsException {
		try {
			template.insert(descr, collectionName);
		} catch (DuplicateKeyException ex) {
			throw new SubmodelAlreadyExistsException(descr.getId());
		}
	}

	@Override
	public void replaceSubmodelDescriptor(@NonNull String submodelId, @NonNull SubmodelDescriptor descr) throws SubmodelNotFoundException {
		if (!Objects.equals(submodelId, descr.getId())) {
			// we can not update the _id element -> delete and save
			moveInTransaction(submodelId, descr);
		} else {
			Query query = Query.query(Criteria.where(ID).is(submodelId));
			SubmodelDescriptor replaced = template.findAndReplace(query, descr, collectionName);
			if (replaced == null) {
				throw new SubmodelNotFoundException(submodelId);
			}
		}
	}

	@Override
	public void removeSubmodelDescriptor(@NonNull String submodelId) throws SubmodelNotFoundException {
		Query query = Query.query(Criteria.where(ID).is(submodelId));
		if (template.remove(query, SubmodelDescriptor.class, collectionName).getDeletedCount() == 0) {
			throw new SubmodelNotFoundException(submodelId);
		}
	}
	
	private void moveInTransaction(String submodelId, SubmodelDescriptor descriptor) {
		SessionScoped scoped = template.withSession(ClientSessionOptions.builder().build());
		boolean removed = scoped.execute(operations -> {
			Query query = Query.query(Criteria.where(ID).is(submodelId));
			if (operations.remove(query, SubmodelDescriptor.class, collectionName).getDeletedCount() == 0) {
				return false;
			}
			operations.save(descriptor, collectionName);
			return true;
		});
		if (!removed) {
			throw new SubmodelNotFoundException(submodelId);
		}
	}

	private void applySorting(List<AggregationOperation> allAggregations) {
		SortOperation sortOp = Aggregation.sort(Direction.ASC, ID);
		allAggregations.add(sortOp);
	}

	private String resolveCursor(PaginationInfo pRequest, List<SubmodelDescriptor> foundDescriptors) {
		if (foundDescriptors.isEmpty() || !pRequest.isPaged()) {
			return null;
		}
		SubmodelDescriptor last = foundDescriptors.get(foundDescriptors.size() - 1);
		return last.getId();
	}

	private void applyPagination(PaginationInfo pRequest, List<AggregationOperation> allAggregations) {
		String cursor = pRequest.getCursor();
		if (cursor != null) {
			String id2 = ID;
			Criteria gt = Criteria.where(id2).gt(cursor);
			allAggregations.add(Aggregation.match(gt));
		}
		if (pRequest.getLimit() != null) {
			allAggregations.add(Aggregation.limit(pRequest.getLimit()));
		}
	}
}
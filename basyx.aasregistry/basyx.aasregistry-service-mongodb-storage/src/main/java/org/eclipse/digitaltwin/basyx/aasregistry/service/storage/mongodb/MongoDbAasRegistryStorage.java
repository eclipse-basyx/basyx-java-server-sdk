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
package org.eclipse.digitaltwin.basyx.aasregistry.service.storage.mongodb;

import com.mongodb.ClientSessionOptions;
import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.eclipse.digitaltwin.basyx.aasregistry.model.AssetAdministrationShellDescriptor;
import org.eclipse.digitaltwin.basyx.aasregistry.model.AssetKind;
import org.eclipse.digitaltwin.basyx.aasregistry.model.ShellDescriptorQuery;
import org.eclipse.digitaltwin.basyx.aasregistry.model.ShellDescriptorSearchRequest;
import org.eclipse.digitaltwin.basyx.aasregistry.model.ShellDescriptorSearchResponse;
import org.eclipse.digitaltwin.basyx.aasregistry.model.SubmodelDescriptor;
import org.eclipse.digitaltwin.basyx.aasregistry.service.errors.AasDescriptorAlreadyExistsException;
import org.eclipse.digitaltwin.basyx.aasregistry.service.errors.AasDescriptorNotFoundException;
import org.eclipse.digitaltwin.basyx.aasregistry.service.errors.SubmodelAlreadyExistsException;
import org.eclipse.digitaltwin.basyx.aasregistry.service.errors.SubmodelNotFoundException;
import org.eclipse.digitaltwin.basyx.aasregistry.service.storage.AasRegistryStorage;
import org.eclipse.digitaltwin.basyx.aasregistry.service.storage.DescriptorFilter;
import org.eclipse.digitaltwin.basyx.aasregistry.service.storage.ShellDescriptorSearchRequests;
import org.eclipse.digitaltwin.basyx.aasregistry.service.storage.ShellDescriptorSearchRequests.GroupedQueries;
import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SessionScoped;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationExpression;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.AggregationUpdate;
import org.springframework.data.mongodb.core.aggregation.ArrayOperators;
import org.springframework.data.mongodb.core.aggregation.ComparisonOperators;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.core.query.UpdateDefinition;

@RequiredArgsConstructor
public class MongoDbAasRegistryStorage implements AasRegistryStorage {

	private static final String MATCHING_SUBMODEL_DESCRIPTORS = "submodelDescriptors.$";

	private static final String SUBMODEL_DESCRIPTORS = "submodelDescriptors";
	// mongodb maps all id fields internally to _id
	private static final String ID = "_id";
	private static final String SUBMODEL_DESCRIPTORS_ID = "submodelDescriptors._id";
	private static final String ASSET_TYPE = "assetType";
	private static final String ASSET_KIND = "assetKind";
	
	private final MongoTemplate template;

	private final String collectionName;

	@Override
	public CursorResult<List<AssetAdministrationShellDescriptor>> getAllAasDescriptors(@NonNull PaginationInfo pRequest, @NonNull DescriptorFilter filter) {
		List<AggregationOperation> allAggregations = new LinkedList<>();
		applyFilter(filter, allAggregations);
		applySorting(allAggregations);
		applyPagination(pRequest, allAggregations);
		AggregationResults<AssetAdministrationShellDescriptor> results = template.aggregate(Aggregation.newAggregation(allAggregations),  collectionName, AssetAdministrationShellDescriptor.class);
		List<AssetAdministrationShellDescriptor> foundDescriptors = results.getMappedResults();
		String cursor = resolveCursor(pRequest, foundDescriptors, AssetAdministrationShellDescriptor::getId);
		return new CursorResult<>(cursor, foundDescriptors);
	}

	private void applySorting(List<AggregationOperation> allAggregations) {
		SortOperation sortOp = Aggregation.sort(Direction.ASC, ID);
		allAggregations.add(sortOp);
	}

	private <T> String resolveCursor(@NonNull PaginationInfo pRequest, List<T> foundDescriptors, Function<T, String> idResolver) {
		if (foundDescriptors.isEmpty() || !pRequest.isPaged()) {
			return null;
		}
		T last = foundDescriptors.get(foundDescriptors.size() - 1);
		return idResolver.apply(last);
	}

	private void applyPagination(PaginationInfo pRequest, List<AggregationOperation> allAggregations) {
		if (pRequest.getCursor() != null) {
			allAggregations.add(Aggregation.match(Criteria.where(ID).gt(pRequest.getCursor())));
		}
		if (pRequest.getLimit() != null) {
			allAggregations.add(Aggregation.limit(pRequest.getLimit()));
		}
	}

	private void applyFilter(DescriptorFilter filter, List<AggregationOperation> allAggregations) {
		Optional<Criteria> filterCriteria = createFilterCriteria(filter);
		filterCriteria.map(Aggregation::match).ifPresent(allAggregations::add);
	}

	public Optional<Criteria> createFilterCriteria(DescriptorFilter filter) {
		if (!filter.isFiltered()) {
			return Optional.empty();
		}
		Criteria criteria = null;
		AssetKind kind = filter.getKind();
		String assetType = filter.getAssetType();
		if (kind == AssetKind.NOTAPPLICABLE) {
			criteria = Criteria.where(ASSET_KIND).exists(false);
		} else if (kind != null) {
			criteria = Criteria.where(ASSET_KIND).is(kind.name());
		}
		if (assetType != null) {
			if (criteria == null) {
				criteria = Criteria.where(ASSET_TYPE).is(assetType);
			} else {
				criteria.and(ASSET_TYPE).is(assetType);
			}
		}
		return Optional.of(criteria);
	}

	@Override
	public AssetAdministrationShellDescriptor getAasDescriptor(@NonNull String aasDescriptorId) throws AasDescriptorNotFoundException {
		AssetAdministrationShellDescriptor descriptor = template.findById(aasDescriptorId, AssetAdministrationShellDescriptor.class, collectionName);
		if (descriptor == null) {
			throw new AasDescriptorNotFoundException(aasDescriptorId);
		}
		return descriptor;
	}

	@Override
	public void insertAasDescriptor(@Valid AssetAdministrationShellDescriptor descr) throws AasDescriptorAlreadyExistsException {
		try {
			template.insert(descr, collectionName);
		} catch (org.springframework.dao.DuplicateKeyException ex) {
			throw new AasDescriptorAlreadyExistsException(descr.getId());
		}
	}

	@Override
	public void replaceAasDescriptor(@NonNull String aasDescriptorId, @NonNull AssetAdministrationShellDescriptor descriptor) throws AasDescriptorNotFoundException {
		if (!Objects.equals(aasDescriptorId, descriptor.getId())) {
			// we can not update the _id element -> delete and save
			moveInTransaction(aasDescriptorId, descriptor);
		} else {
			Query query = Query.query(Criteria.where(ID).is(aasDescriptorId));
			AssetAdministrationShellDescriptor replaced = template.findAndReplace(query, descriptor, collectionName);
			if (replaced == null) {
				throw new AasDescriptorNotFoundException(aasDescriptorId);
			}
		}
	}

	private void moveInTransaction(@NonNull String aasDescriptorId, @NonNull AssetAdministrationShellDescriptor descriptor) {
		SessionScoped scoped = template.withSession(ClientSessionOptions.builder().build());
		boolean removed = scoped.execute(operations -> {
			Query query = Query.query(Criteria.where(ID).is(aasDescriptorId));
			if (operations.remove(query, AssetAdministrationShellDescriptor.class, collectionName).getDeletedCount() == 0) {
				return false;
			}
			operations.save(descriptor, collectionName);
			return true;
		});
		if (!removed) {
			throw new AasDescriptorNotFoundException(aasDescriptorId);
		}
	}

	@Override
	public void removeAasDescriptor(@NonNull String aasDescriptorId) throws AasDescriptorNotFoundException {
		Query query = Query.query(Criteria.where(ID).is(aasDescriptorId));
		if (template.remove(query, AssetAdministrationShellDescriptor.class, collectionName).getDeletedCount() == 0) {
			throw new AasDescriptorNotFoundException(aasDescriptorId);
		}
	}

	@Override
	public CursorResult<List<SubmodelDescriptor>> getAllSubmodels(@NonNull String aasDescriptorId, @NonNull PaginationInfo pRequest) throws AasDescriptorNotFoundException {
		if (!template.exists(Query.query(Criteria.where(ID).is(aasDescriptorId)), collectionName)) {
			throw new AasDescriptorNotFoundException(aasDescriptorId);
		}
		List<AggregationOperation> allAggregations = new LinkedList<>();
		allAggregations.add(Aggregation.match(Criteria.where(ID).is(aasDescriptorId)));
		allAggregations.add(Aggregation.unwind(SUBMODEL_DESCRIPTORS));
		allAggregations.add(Aggregation.replaceRoot(SUBMODEL_DESCRIPTORS));
		this.applySorting(allAggregations);
		this.applyPagination(pRequest, allAggregations);
		AggregationResults<SubmodelDescriptor> results = template.aggregate(Aggregation.newAggregation(allAggregations), collectionName, SubmodelDescriptor.class);
		List<SubmodelDescriptor> submodels = results.getMappedResults();
		String cursor = resolveCursor(pRequest, submodels, SubmodelDescriptor::getId);
		return new CursorResult<>(cursor, submodels);
	}

	@Override
	public SubmodelDescriptor getSubmodel(@NonNull String aasDescriptorId, @NonNull String submodelId) throws AasDescriptorNotFoundException, SubmodelNotFoundException {
		List<AggregationOperation> all = new ArrayList<>();
		all.add(Aggregation.match(Criteria.where(ID).is(aasDescriptorId)));
		ArrayOperators.Filter filter = ArrayOperators.arrayOf(SUBMODEL_DESCRIPTORS).filter().as(SUBMODEL_DESCRIPTORS).by(ComparisonOperators.valueOf(SUBMODEL_DESCRIPTORS_ID).equalToValue(submodelId));
		all.add(Aggregation.project().and(filter).as(SUBMODEL_DESCRIPTORS));
		AggregationResults<AssetAdministrationShellDescriptor> results = template.aggregate(Aggregation.newAggregation(all), collectionName, AssetAdministrationShellDescriptor.class);
		List<AssetAdministrationShellDescriptor> aasDescriptors = results.getMappedResults();
		if (aasDescriptors.isEmpty()) {
			throw new AasDescriptorNotFoundException(aasDescriptorId);
		}
		List<SubmodelDescriptor> descriptors = aasDescriptors.get(0).getSubmodelDescriptors();
		if (descriptors == null || descriptors.isEmpty()) {
			throw new SubmodelNotFoundException(aasDescriptorId, submodelId);
		}
		return descriptors.get(0);
	}

	@Override
	public void insertSubmodel(@NonNull String aasDescriptorId, @NonNull SubmodelDescriptor submodel) throws AasDescriptorNotFoundException, SubmodelAlreadyExistsException {
		Criteria criteria = Criteria.where(ID).is(aasDescriptorId).and(SUBMODEL_DESCRIPTORS_ID).ne(submodel.getId());
		Query query = Query.query(criteria);
		UpdateDefinition def = new Update().push(SUBMODEL_DESCRIPTORS, submodel);
		AssetAdministrationShellDescriptor descr = template.findAndModify(query, def, AssetAdministrationShellDescriptor.class, collectionName);
		assertInsertPerformed(descr, aasDescriptorId, submodel.getId());
	}

	@Override
	public void replaceSubmodel(@NonNull String aasDescriptorId, @NonNull String submodelId, @NonNull SubmodelDescriptor submodel) throws AasDescriptorNotFoundException, SubmodelNotFoundException {
		Criteria criteria = Criteria.where(ID).is(aasDescriptorId).and(SUBMODEL_DESCRIPTORS).elemMatch(Criteria.where(ID).is(submodelId));
		Query query = Query.query(criteria);
		UpdateDefinition def = Update.update(MATCHING_SUBMODEL_DESCRIPTORS, submodel);
		AssetAdministrationShellDescriptor descr = template.findAndModify(query, def, AssetAdministrationShellDescriptor.class, collectionName);
		assertReplacePerformed(descr, aasDescriptorId, submodelId);
	}

	private void assertInsertPerformed(AssetAdministrationShellDescriptor descr, String aasDescriptorId, String submodelId) {
		if (descr == null) {
			getAasDescriptor(aasDescriptorId);
			throw new SubmodelAlreadyExistsException(aasDescriptorId, submodelId);
		}
	}

	private void assertReplacePerformed(AssetAdministrationShellDescriptor descr, String aasDescriptorId, String submodelId) {
		if (descr == null) {
			getAasDescriptor(aasDescriptorId);
			throw new SubmodelNotFoundException(aasDescriptorId, submodelId);
		}
	}

	@Override
	public void removeSubmodel(@NonNull String aasDescriptorId, @NonNull String submodelId) throws AasDescriptorNotFoundException, SubmodelNotFoundException {
		AggregationExpression notEquals = ComparisonOperators.valueOf(SUBMODEL_DESCRIPTORS_ID).notEqualToValue(submodelId);
		AggregationExpression filterArray = ArrayOperators.arrayOf(SUBMODEL_DESCRIPTORS).filter().as(SUBMODEL_DESCRIPTORS).by(notEquals);
		AggregationUpdate update = AggregationUpdate.update().set(SUBMODEL_DESCRIPTORS).toValue(filterArray);
		AssetAdministrationShellDescriptor old = template.findAndModify(Query.query(Criteria.where(ID).is(aasDescriptorId)), update, AssetAdministrationShellDescriptor.class, collectionName);
		if (old == null) {
			throw new AasDescriptorNotFoundException(submodelId);
		}
		boolean wasPresent = containsSubmodel(old, submodelId);
		if (!wasPresent) {
			throw new SubmodelNotFoundException(aasDescriptorId, submodelId);
		}
	}

	private boolean containsSubmodel(AssetAdministrationShellDescriptor old, String submodelId) {
		List<SubmodelDescriptor> submodels = old.getSubmodelDescriptors();
		if (submodels == null) {
			return false;
		}
		for (SubmodelDescriptor eachSubmodel : submodels) {
			if (submodelId.equals(eachSubmodel.getId())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Set<String> clear() {
		Query query = Query.query(Criteria.where(ID).exists(true));
		query.fields().include(ID);
		List<AssetAdministrationShellDescriptor> list = template.findAllAndRemove(query, AssetAdministrationShellDescriptor.class, collectionName);
		return list.stream().map(AssetAdministrationShellDescriptor::getId).collect(Collectors.toSet());
	}

	@Override
	public ShellDescriptorSearchResponse searchAasDescriptors(@NonNull ShellDescriptorSearchRequest request) {
		SearchQueryBuilder qBuilder = new SearchQueryBuilder();
		ShellDescriptorQuery dQuery = request.getQuery();
		GroupedQueries grouped = ShellDescriptorSearchRequests.groupQueries(dQuery);
		Criteria mongoCriteria = qBuilder.buildCriteria(grouped);

		long total = template.count(Query.query(mongoCriteria), AssetAdministrationShellDescriptor.class, collectionName);

		List<AggregationOperation> aggregationOps = new LinkedList<>();
		MatchOperation matchOp = Aggregation.match(mongoCriteria);
		aggregationOps.add(matchOp);
		qBuilder.withSorting(request.getSortBy(), aggregationOps);
		qBuilder.withPage(request.getPage(), aggregationOps);
		qBuilder.withProjection(grouped.getQueriesInsideSubmodel(), aggregationOps);

		Aggregation aggregation = Aggregation.newAggregation(aggregationOps);
		AggregationResults<AssetAdministrationShellDescriptor> results = template.aggregate(aggregation, collectionName, AssetAdministrationShellDescriptor.class);

		List<AssetAdministrationShellDescriptor> descriptors = results.getMappedResults();
		return new ShellDescriptorSearchResponse(total, descriptors);
	}
}

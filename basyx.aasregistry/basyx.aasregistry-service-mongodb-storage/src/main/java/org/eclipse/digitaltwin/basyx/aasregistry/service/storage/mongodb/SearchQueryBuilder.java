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

import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.eclipse.digitaltwin.basyx.aasregistry.model.AssetAdministrationShellDescriptor;
import org.eclipse.digitaltwin.basyx.aasregistry.model.Page;
import org.eclipse.digitaltwin.basyx.aasregistry.model.ShellDescriptorQuery;
import org.eclipse.digitaltwin.basyx.aasregistry.model.SortDirection;
import org.eclipse.digitaltwin.basyx.aasregistry.model.Sorting;
import org.eclipse.digitaltwin.basyx.aasregistry.model.SortingPath;
import org.eclipse.digitaltwin.basyx.aasregistry.paths.AasRegistryPaths;
import org.eclipse.digitaltwin.basyx.aasregistry.service.storage.ShellDescriptorSearchRequests.GroupedQueries;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationExpression;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.query.Criteria;

public class SearchQueryBuilder {

	private final Map<String, String> pathMappings;

	public SearchQueryBuilder() {
		pathMappings = new HashMap<>();
		// mongodb maps all id fields internally to _id
		pathMappings.put(AasRegistryPaths.id(), "_id");
		pathMappings.put(AasRegistryPaths.submodelDescriptors().id(), AasRegistryPaths.submodelDescriptors() + "." + "_id");
	}

	public Criteria buildCriteria(GroupedQueries grouped) {
		SearchPathCriteriaBuilder builder = new SearchPathCriteriaBuilder(pathMappings);
		List<Criteria> criterias = builder.buildCriterias(grouped);
		if (criterias.isEmpty()) {
			return Criteria.where(pathMappings.get(AasRegistryPaths.id())).exists(true);
		}
		if (criterias.size() == 1) {
			return criterias.get(0);
		}
		return new Criteria().andOperator(criterias);

	}

	public void withSorting(Sorting sorting, List<AggregationOperation> aggregationOps) {
		if (sorting == null) {
			withAscendingById(aggregationOps);
		} else {
			withSorting(sorting.getPath(), sorting.getDirection(), aggregationOps);
		}
	}

	public SortOperation getSort(Sorting sorting) {
		if (sorting == null) {
			return getAscendingById();
		} else {
			return getSort(sorting.getPath(), sorting.getDirection());
		}
	}

	private void withSorting(List<SortingPath> paths, @Valid SortDirection direction, List<AggregationOperation> aggregationOps) {
		Direction mongoDirection = mapDirection(direction);
		List<Order> orderList = new ArrayList<>();
		if (paths.isEmpty()) {
			String path = pathMappings.getOrDefault(AasRegistryPaths.id(), AasRegistryPaths.id());
			aggregationOps.add(Aggregation.sort(mongoDirection, path));
		} else {
			for (SortingPath eachPath : paths) {
				String path = eachPath.toString();
				path = pathMappings.getOrDefault(path, path);
				orderList.add(new Order(mongoDirection, path));
			}
			aggregationOps.add(Aggregation.sort(Sort.by(orderList)));
		}
	}

	private SortOperation getSort(List<SortingPath> paths, @Valid SortDirection direction) {
		Direction mongoDirection = mapDirection(direction);
		List<Order> orderList = new ArrayList<>();
		for (SortingPath eachPath : paths) {
			String path = eachPath.toString();
			path = pathMappings.getOrDefault(path, path);
			orderList.add(new Order(mongoDirection, path));
		}
		return Aggregation.sort(Sort.by(orderList));
	}

	private Direction mapDirection(@Valid SortDirection direction) {
		if (direction == null || direction == SortDirection.ASC) {
			return Direction.ASC;
		}
		return Direction.DESC;
	}

	private void withAscendingById(List<AggregationOperation> aggregationOps) {
		String idPath = AasRegistryPaths.id();
		idPath = pathMappings.getOrDefault(idPath, idPath);
		aggregationOps.add(Aggregation.sort(Direction.ASC, idPath));
	}

	private SortOperation getAscendingById() {
		String idPath = AasRegistryPaths.id();
		idPath = pathMappings.getOrDefault(idPath, idPath);
		return Aggregation.sort(Direction.ASC, idPath);
	}

	public void withPage(Page page, List<AggregationOperation> aggregations) {
		if (page != null) {
			int index = page.getIndex();
			long size = page.getSize();
			aggregations.add(Aggregation.skip(index * size));
			aggregations.add(Aggregation.limit(size));
		}
	}

	public void withProjection(List<ShellDescriptorQuery> submodelQueries, List<AggregationOperation> ops) {
		SearchPathProjectionBuilder projBuilder = new SearchPathProjectionBuilder(pathMappings);
		Optional<AggregationExpression> filterOpt = projBuilder.buildSubmodelFilter(submodelQueries);
		if (filterOpt.isPresent()) {
			AggregationExpression filter = filterOpt.get();
			ops.add(Aggregation.project(AssetAdministrationShellDescriptor.class).and(filter).as(AasRegistryPaths.SEGMENT_SUBMODEL_DESCRIPTORS));
		}
	}
}

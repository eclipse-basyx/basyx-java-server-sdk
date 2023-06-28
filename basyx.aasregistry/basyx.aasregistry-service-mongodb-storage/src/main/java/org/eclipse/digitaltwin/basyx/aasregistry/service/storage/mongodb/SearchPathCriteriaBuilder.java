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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.bson.BsonRegularExpression;
import org.eclipse.digitaltwin.basyx.aasregistry.model.ShellDescriptorQuery;
import org.eclipse.digitaltwin.basyx.aasregistry.model.ShellDescriptorQuery.QueryTypeEnum;
import org.eclipse.digitaltwin.basyx.aasregistry.paths.AasRegistryPaths;
import org.eclipse.digitaltwin.basyx.aasregistry.service.storage.ShellDescriptorSearchRequests.GroupedQueries;
import org.eclipse.digitaltwin.basyx.aasregistry.service.storage.mongodb.SegmentBlocksBuilder.SegmentBlock;
import org.springframework.data.mongodb.core.query.Criteria;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SearchPathCriteriaBuilder {

	private final Map<String, String> pathMappings;
	

	public List<Criteria> buildCriterias(GroupedQueries grouped) {
		List<ShellDescriptorQuery> outsideSm = grouped.getQueriesOutsideSubmodel();
		List<ShellDescriptorQuery> inSm = grouped.getQueriesInsideSubmodel();
		List<Criteria> criterias = new LinkedList<>();
		buildShellCriterias(outsideSm, criterias);
		buildSubmodelCriterias(inSm, criterias);
		return criterias;
	}
	
	private void buildShellCriterias(List<ShellDescriptorQuery> queries, List<Criteria> toAppendTo) {
		for (ShellDescriptorQuery eachQuery : queries) {
			Criteria criteria = buildSearchPathCriteria(eachQuery.getPath(), eachQuery);
			toAppendTo.add(criteria);
		}
	}

	private void buildSubmodelCriterias(List<ShellDescriptorQuery> queries, List<Criteria> criterias) {
		List<Criteria> innerCriterias = new LinkedList<>();
		queries.stream().map(this::buildInSubmodelSearchPathCriteria).forEach(innerCriterias::add);
		if (innerCriterias.size() == 1) {
			criterias.add(new Criteria(AasRegistryPaths.SEGMENT_SUBMODEL_DESCRIPTORS).elemMatch(innerCriterias.get(0)));
		} else if (innerCriterias.size() > 1) {
			criterias.add(new Criteria(AasRegistryPaths.SEGMENT_SUBMODEL_DESCRIPTORS).elemMatch(new Criteria().andOperator(innerCriterias)));
		}
	}
	
	private Criteria buildInSubmodelSearchPathCriteria(ShellDescriptorQuery query) {
		String searchPath = query.getPath();
		searchPath = searchPath.substring(AasRegistryPaths.SEGMENT_SUBMODEL_DESCRIPTORS.length() + 1);
		return buildSearchPathCriteria(searchPath, query);
	}
	
	private Criteria buildSearchPathCriteria(String searchPath, ShellDescriptorQuery query) {
		String value = query.getValue();
		QueryTypeEnum qType = query.getQueryType();
		String extensionName = query.getExtensionName();
		SegmentBlocksBuilder sbBuilder = new SegmentBlocksBuilder(pathMappings);
		List<SegmentBlock> blockSegments = sbBuilder.buildSegmentBlocks(searchPath);
		CriteriaBuilder converter = new CriteriaBuilder(value, qType, blockSegments, extensionName);
		return converter.buildCriteria();
	}

	@RequiredArgsConstructor
	private static class CriteriaBuilder {

		private final String value;
		private final QueryTypeEnum queryType;
		private final List<SegmentBlock> segments;
		private final String extensionName;

		public Criteria buildCriteria() {
			return buildCriteriaRecursively(segments.iterator());
		}

		private Criteria buildCriteriaRecursively(Iterator<SegmentBlock> segmentIter) {
			SegmentBlock segmentBlock = segmentIter.next();
			String segment = segmentBlock.getSegment();
			Criteria criteria = Criteria.where(segment);

			if (segmentBlock.isListLeaf()) {
				if (queryType == QueryTypeEnum.REGEX) {
					return Criteria.where(segment).in(new BsonRegularExpression(value));
				}
				return Criteria.where(segment).in(value);
			} else if (segmentBlock.isLeaf()) {
				if (queryType == QueryTypeEnum.REGEX) {
					return withExtensionCheck(criteria.regex(value));
				}
				return withExtensionCheck(criteria.is(value));

			} else {
				Criteria innerCriteria = buildCriteriaRecursively(segmentIter);
				return criteria.elemMatch(innerCriteria);
			}
		}

		private Criteria withExtensionCheck(Criteria criteria) {
			if (extensionName == null) {
				return criteria;
			}
			Criteria extensionNameCriteria = Criteria.where(AasRegistryPaths.SEGMENT_NAME);
			extensionNameCriteria.is(extensionName);
			return new Criteria().andOperator(criteria, extensionNameCriteria);
		}
	}

}
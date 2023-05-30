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
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.bson.Document;
import org.eclipse.digitaltwin.basyx.aasregistry.model.ShellDescriptorQuery.QueryTypeEnum;
import org.eclipse.digitaltwin.basyx.aasregistry.paths.AasRegistryPaths;
import org.eclipse.digitaltwin.basyx.aasregistry.service.storage.mongodb.SegmentBlocksBuilder.SegmentBlock;
import org.springframework.data.mongodb.core.aggregation.AggregationExpression;
import org.springframework.data.mongodb.core.aggregation.AggregationOperationContext;
import org.springframework.data.mongodb.core.aggregation.ArrayOperators;
import org.springframework.data.mongodb.core.aggregation.ArrayOperators.Filter;
import org.springframework.data.mongodb.core.aggregation.ComparisonOperators;
import org.springframework.data.mongodb.core.aggregation.ConditionalOperators;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SearchPathProjectionBuilder {

	private final Map<String, String> pathMappings;

	public Optional<AggregationExpression> buildSubmodelFilter(String searchPath, String value, QueryTypeEnum queryType) {
		if (!searchPath.startsWith(AasRegistryPaths.SEGMENT_SUBMODEL_DESCRIPTORS + ".")) {
			return Optional.empty(); // we just want to shrink the matching submodules
		}
		SegmentBlocksBuilder sbBuilder = new SegmentBlocksBuilder(pathMappings);
		List<SegmentBlock> blockSegements = sbBuilder.buildSegmentBlocks(searchPath);
		FilterBuilder builder = new FilterBuilder(value, queryType, blockSegements);
		return Optional.of(builder.buildSubmodelFilter());
		
	}

	@RequiredArgsConstructor
	private static class FilterBuilder {

		private final String value;
		private final QueryTypeEnum queryType;
		private final List<SegmentBlock> segments;

		public AggregationExpression buildSubmodelFilter() {
			SimpleVarNameProvider varNameProvider = new SimpleVarNameProvider();
			return buildFilterRecursively(varNameProvider, segments.iterator(), null);
		}

		private AggregationExpression buildFilterRecursively(SimpleVarNameProvider varNameProvider, Iterator<SegmentBlock> filters, String parentVariableName) {
			SegmentBlock segmentBlock = filters.next();
			String segment = segmentBlock.getSegment();
			String pathReference = getPathReferenceByParentVariable(segment, parentVariableName);
			String variableName = varNameProvider.next();
			if (segmentBlock.isListLeaf()) {
				String variableNameRef = "$$" + variableName;
				AggregationExpression inner;
				if (queryType == QueryTypeEnum.REGEX) {
					inner = new SimpleRegexMatch(variableNameRef, value);
				} else {
					inner = ComparisonOperators.Eq.valueOf(variableNameRef).equalToValue(value);
				}
				return ArrayOperators.Filter.filter(pathReference).as(variableName).by(inner);
			} else if (segmentBlock.isLeaf()) {
				if (queryType == QueryTypeEnum.REGEX) {
					return new SimpleRegexMatch(pathReference, value);
				} else {
					return ComparisonOperators.Eq.valueOf(pathReference).equalToValue(value);
				}
			} else {
				AggregationExpression innerFilter = buildFilterRecursively(varNameProvider, filters, variableName);
				if (innerFilter instanceof Filter) {
					ConditionalOperators.IfNull ifNullEmptySet = ConditionalOperators.ifNull(innerFilter).then(List.of());
					ArrayOperators.Size sizeOp = ArrayOperators.Size.lengthOfArray(ifNullEmptySet);
					ComparisonOperators.Ne byFilter = ComparisonOperators.Ne.valueOf(sizeOp).notEqualToValue(0);
					return ArrayOperators.Filter.filter(pathReference).as(variableName).by(byFilter);
				}
				return ArrayOperators.Filter.filter(pathReference).as(variableName).by(innerFilter);
			}
		}

		private String getPathReferenceByParentVariable(String segment, String parentVariableName) {
			if (parentVariableName == null) {
				return "$" + segment;
			} else {
				return "$$" + parentVariableName + "." + segment;
			}
		}
	}

	@RequiredArgsConstructor
	private static class SimpleRegexMatch implements AggregationExpression {

		private final String input;
		private final String regex;

		@Override
		public Document toDocument(AggregationOperationContext context) {
			Document innerDoc = new Document();
			innerDoc.put("input", input);
			innerDoc.put("regex", regex);
			return new Document("$regexMatch", innerDoc);
		}
	}
}
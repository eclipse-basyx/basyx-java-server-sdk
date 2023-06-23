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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.bson.Document;
import org.eclipse.digitaltwin.basyx.aasregistry.model.ShellDescriptorQuery;
import org.eclipse.digitaltwin.basyx.aasregistry.model.ShellDescriptorQuery.QueryTypeEnum;
import org.eclipse.digitaltwin.basyx.aasregistry.paths.AasRegistryPaths;
import org.eclipse.digitaltwin.basyx.aasregistry.service.storage.mongodb.SegmentBlocksBuilder.SegmentBlock;
import org.springframework.data.mongodb.core.aggregation.AggregationExpression;
import org.springframework.data.mongodb.core.aggregation.AggregationOperationContext;
import org.springframework.data.mongodb.core.aggregation.ArrayOperators;
import org.springframework.data.mongodb.core.aggregation.ArrayOperators.Filter;
import org.springframework.data.mongodb.core.aggregation.BooleanOperators;
import org.springframework.data.mongodb.core.aggregation.ComparisonOperators;
import org.springframework.data.mongodb.core.aggregation.ConditionalOperators;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SearchPathProjectionBuilder {

	private final Map<String, String> pathMappings;

	public Optional<AggregationExpression> buildSubmodelFilter(List<ShellDescriptorQuery> submodelQueries) {
		SimpleVarNameProvider varNameProvider = new SimpleVarNameProvider();
		String variableName = varNameProvider.next();

		String pathReference = "$" + AasRegistryPaths.SEGMENT_SUBMODEL_DESCRIPTORS;

		List<AggregationExpression> expressions = new ArrayList<>();
		for (ShellDescriptorQuery eachQuery : submodelQueries) {
			AggregationExpression expression = buildSubmodelFilter(varNameProvider, eachQuery, variableName);
			if (expression != null) {
				expressions.add(expression);
			}
		}
		if (expressions.isEmpty()) {
			return Optional.empty();
		}
		if (expressions.size() == 1) {
			AggregationExpression inner = expressions.get(0);
			return Optional.of(ArrayOperators.Filter.filter(pathReference).as(variableName).by(inner));
		}
		BooleanOperators.And and = BooleanOperators.And.and();
		for (AggregationExpression eachExpression : expressions) {
			and = and.andExpression(eachExpression);
		}
		return Optional.of(ArrayOperators.Filter.filter(pathReference).as(variableName).by(and));
	}

	private AggregationExpression buildSubmodelFilter(SimpleVarNameProvider varNameProvider, ShellDescriptorQuery query, String variableName) {
		SegmentBlocksBuilder sbBuilder = new SegmentBlocksBuilder(pathMappings);
		List<SegmentBlock> blockSegments = sbBuilder.buildSegmentBlocks(query.getPath());
		Iterator<SegmentBlock> blockSegmentIter = blockSegments.iterator();
		// skip submodelDescriptor segment
		SegmentBlock block = blockSegmentIter.next();
		if (!AasRegistryPaths.SEGMENT_SUBMODEL_DESCRIPTORS.equals(block.getSegment())) {
			return null;
		}

		FilterRecursionContext context = new FilterRecursionContext(blockSegmentIter, query.getValue(), query.getQueryType(), query.getExtensionName());

		FilterBuilder builder = new FilterBuilder(varNameProvider);
		AggregationExpression expr = builder.buildFilterRecursively(context, variableName);
		if (expr instanceof Filter) {
			ConditionalOperators.IfNull ifNullEmptySet = ConditionalOperators.ifNull(expr).then(List.of());
			ArrayOperators.Size sizeOp = ArrayOperators.Size.lengthOfArray(ifNullEmptySet);
			return ComparisonOperators.Ne.valueOf(sizeOp).notEqualToValue(0);
		}
		return expr;
	}

	@RequiredArgsConstructor
	private static class FilterBuilder {

		private final SimpleVarNameProvider varNameProvider;

		private AggregationExpression buildFilterRecursively(FilterRecursionContext context, String parentVariableName) {
			SegmentBlock segmentBlock = context.segmentsIter.next();
			String segment = segmentBlock.getSegment();
			String pathReference = getPathReferenceByParentVariable(segment, parentVariableName);

			String variableName = varNameProvider.next();
			if (segmentBlock.isListLeaf()) {
				String variableNameRef = "$$" + variableName;
				AggregationExpression inner;
				if (context.getQueryType() == QueryTypeEnum.REGEX) {
					inner = new SimpleRegexMatch(variableNameRef, context.getValue());
				} else {
					inner = ComparisonOperators.Eq.valueOf(variableNameRef).equalToValue(context.getValue());
				}
				return ArrayOperators.Filter.filter(pathReference).as(variableName).by(inner);
			} else if (segmentBlock.isLeaf()) {
				AggregationExpression expression;
				if (context.getQueryType() == QueryTypeEnum.REGEX) {
					expression = new SimpleRegexMatch(pathReference, context.value);
				} else {
					expression = ComparisonOperators.Eq.valueOf(pathReference).equalToValue(context.value);
				}
				if (context.getExtensionName() != null) {
					String extensionReference = getPathReferenceByParentVariable(AasRegistryPaths.SEGMENT_NAME, parentVariableName);
					AggregationExpression extensionNameExpr = ComparisonOperators.Eq.valueOf(extensionReference).equalToValue(context.getExtensionName());
					return BooleanOperators.And.and(extensionNameExpr, expression);
				}
				return expression;
			} else {
				AggregationExpression innerFilter = buildFilterRecursively(context, variableName);
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
	@Data
	private static class FilterRecursionContext {

		private final Iterator<SegmentBlock> segmentsIter;
		private final String value;
		private final QueryTypeEnum queryType;
		private final String extensionName;

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
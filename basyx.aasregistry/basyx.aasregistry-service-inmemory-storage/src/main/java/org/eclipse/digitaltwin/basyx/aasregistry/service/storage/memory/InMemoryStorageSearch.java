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
package org.eclipse.digitaltwin.basyx.aasregistry.service.storage.memory;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.digitaltwin.basyx.aasregistry.model.AssetAdministrationShellDescriptor;
import org.eclipse.digitaltwin.basyx.aasregistry.model.Page;
import org.eclipse.digitaltwin.basyx.aasregistry.model.ShellDescriptorQuery;
import org.eclipse.digitaltwin.basyx.aasregistry.model.ShellDescriptorQuery.QueryTypeEnum;
import org.eclipse.digitaltwin.basyx.aasregistry.model.ShellDescriptorSearchRequest;
import org.eclipse.digitaltwin.basyx.aasregistry.model.ShellDescriptorSearchResponse;
import org.eclipse.digitaltwin.basyx.aasregistry.model.SortDirection;
import org.eclipse.digitaltwin.basyx.aasregistry.model.Sorting;
import org.eclipse.digitaltwin.basyx.aasregistry.model.SortingPath;
import org.eclipse.digitaltwin.basyx.aasregistry.model.SubmodelDescriptor;
import org.eclipse.digitaltwin.basyx.aasregistry.paths.AasRegistryPathProcessor;
import org.eclipse.digitaltwin.basyx.aasregistry.paths.AasRegistryPathProcessor.AssetAdministrationShellDescriptorVisitor;
import org.eclipse.digitaltwin.basyx.aasregistry.paths.AasRegistryPathProcessor.ProcessInstruction;
import org.eclipse.digitaltwin.basyx.aasregistry.service.storage.DescriptorCopies;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class InMemoryStorageSearch {

	private final Collection<AssetAdministrationShellDescriptor> aasDecriptors;

	public ShellDescriptorSearchResponse performSearch(ShellDescriptorSearchRequest request) {
		ShellDescriptorQuery query = request.getQuery();
		Stream<AssetAdministrationShellDescriptor> matchingDescriptors = resolveMatchingDescriptors(query).stream();
		Stream<AssetAdministrationShellDescriptor> sorted = applySorting(matchingDescriptors, request.getSortBy());
		List<AssetAdministrationShellDescriptor> items = sorted.collect(Collectors.toList());
		long totalSizeOverAllPages = items.size();
		List<AssetAdministrationShellDescriptor> result = applyPagination(items, request.getPage());
		return new ShellDescriptorSearchResponse(totalSizeOverAllPages, result);
	}

	private Stream<AssetAdministrationShellDescriptor> applySorting(Stream<AssetAdministrationShellDescriptor> matchingDescriptors, Sorting sortBy) {
		if (sortBy == null) {
			return matchingDescriptors;
		}
		List<SortingPath> sortingPath = sortBy.getPath();
		Comparator<AssetAdministrationShellDescriptor> comparator = null;
		for (SortingPath eachPath : sortingPath) {
			String sortPathAsString = eachPath.toString(); // toString returns the path
			ValueExtractor extractor = new CachingValueExtractor(sortPathAsString);
			Comparator<AssetAdministrationShellDescriptor> sortPathComparator = Comparator.comparing(extractor::resolveValue);
			comparator = comparator == null ? sortPathComparator : comparator.thenComparing(sortPathComparator);
		}
		if (comparator != null) {
			if (sortBy.getDirection() == SortDirection.DESC) {
				comparator = comparator.reversed();
			}
			return matchingDescriptors.sorted(comparator);
		} else {
			return matchingDescriptors;
		}
	}

	private List<AssetAdministrationShellDescriptor> applyPagination(List<AssetAdministrationShellDescriptor> descriptors, Page page) {
		if (page == null) {
			return descriptors;
		}
		int pageIndex = page.getIndex();
		int size = page.getSize();
		long startIndex = pageIndex * (long) size;
		return descriptors.stream().skip(startIndex).limit(size).collect(Collectors.toList());
	}

	private Collection<AssetAdministrationShellDescriptor> resolveMatchingDescriptors(ShellDescriptorQuery query) {
		if (query == null) { // match all
			return aasDecriptors;
		}
		String queryPath = query.getPath();
		Predicate<String> matcher = createMatcher(query.getQueryType(), query.getValue());
		List<AssetAdministrationShellDescriptor> matchingDescriptors = new LinkedList<>();
		for (AssetAdministrationShellDescriptor eachDescriptor : aasDecriptors) {
			MatchingVisitor visitor = new MatchingVisitor(matcher);
			AasRegistryPathProcessor processor = new AasRegistryPathProcessor(eachDescriptor);
			processor.visitValuesAtPath(queryPath, visitor);
			visitor.getResultClone().ifPresent(matchingDescriptors::add);
		}
		return matchingDescriptors;
	}

	private Predicate<String> createMatcher(QueryTypeEnum queryType, String value) {
		if (queryType == QueryTypeEnum.REGEX) {
			return Pattern.compile(value).asMatchPredicate();
		} else {
			return Predicate.isEqual(value);
		}
	}

	@RequiredArgsConstructor
	private static class ValueExtractor {

		protected final String path;

		public String resolveValue(AssetAdministrationShellDescriptor descriptor) {
			AasRegistryPathProcessor processor = new AasRegistryPathProcessor(descriptor);
			ValueExtractionVisitor visitor = new ValueExtractionVisitor();
			processor.visitValuesAtPath(path, visitor);
			return visitor.value;
		}

		private static final class ValueExtractionVisitor implements AssetAdministrationShellDescriptorVisitor {

			private String value = ""; // for comparing we need non-null values so use an empty string

			@Override
			public ProcessInstruction visitResolvedPathValue(String path, Object[] objectPathToValue, String value) {
				// we do not sort by list content for now so just abort on the first (and only)
				// path
				this.value = value;
				return ProcessInstruction.ABORT;
			}
		}
	}

	public static class CachingValueExtractor extends ValueExtractor {

		private final Map<AssetAdministrationShellDescriptor, Map<String, String>> cachedValues = new HashMap<>();

		public CachingValueExtractor(String path) {
			super(path);
		}

		@Override
		public String resolveValue(AssetAdministrationShellDescriptor descriptor) {
			Map<String, String> result = cachedValues.computeIfAbsent(descriptor, k -> new HashMap<>());
			return result.computeIfAbsent(path, k -> super.resolveValue(descriptor));
		}

	}

	@RequiredArgsConstructor
	private static final class MatchingVisitor implements AssetAdministrationShellDescriptorVisitor {

		private final Predicate<String> matcher;
		private AssetAdministrationShellDescriptor aasDescriptor;
		// if multiple values of a submodel property fire (possible in list-properties)
		// we will have multiple invocations with a submodel
		// thus we need a set here to avoid duplicates
		private LinkedHashSet<SubmodelDescriptor> submodels;

		@Override
		public ProcessInstruction visitResolvedPathValue(String path, Object[] objectPathToValue, String value) {
			if (matcher.test(value)) {
				aasDescriptor = (AssetAdministrationShellDescriptor) objectPathToValue[0];
				if (wasInSubmodel(objectPathToValue)) {
					if (submodels == null) {
						submodels = new LinkedHashSet<>();
					}
					submodels.add((SubmodelDescriptor) objectPathToValue[1]);
					return ProcessInstruction.CONTINUE; // do also other submodels match?
				} else {
					// found a value -> we will choose the hole AasDescriptor
					return ProcessInstruction.ABORT;
				}
			} else { // continue searching
				return ProcessInstruction.CONTINUE;
			}
		}

		private boolean wasInSubmodel(Object[] objectPathToValue) {
			return objectPathToValue.length >= 2 && objectPathToValue[1] instanceof SubmodelDescriptor;
		}

		public Optional<AssetAdministrationShellDescriptor> getResultClone() {
			if (aasDescriptor == null) {
				return Optional.empty();
			} else {
				// we alter the storage object so we need a copy here
				AssetAdministrationShellDescriptor toReturn = DescriptorCopies.deepClone(aasDescriptor);
				if (submodels != null) {
					toReturn.setSubmodelDescriptors(new LinkedList<>(submodels));
				}
				return Optional.of(toReturn);
			}
		}
	}
}
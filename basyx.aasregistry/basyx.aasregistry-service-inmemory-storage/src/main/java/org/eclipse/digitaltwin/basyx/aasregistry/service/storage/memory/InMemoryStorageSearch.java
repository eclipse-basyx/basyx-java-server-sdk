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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.digitaltwin.basyx.aasregistry.model.AssetAdministrationShellDescriptor;
import org.eclipse.digitaltwin.basyx.aasregistry.model.Page;
import org.eclipse.digitaltwin.basyx.aasregistry.model.ShellDescriptorQuery;
import org.eclipse.digitaltwin.basyx.aasregistry.model.ShellDescriptorSearchRequest;
import org.eclipse.digitaltwin.basyx.aasregistry.model.ShellDescriptorSearchResponse;
import org.eclipse.digitaltwin.basyx.aasregistry.model.SortDirection;
import org.eclipse.digitaltwin.basyx.aasregistry.model.Sorting;
import org.eclipse.digitaltwin.basyx.aasregistry.model.SortingPath;
import org.eclipse.digitaltwin.basyx.aasregistry.paths.AasRegistryPathProcessor;
import org.eclipse.digitaltwin.basyx.aasregistry.paths.AasRegistryPathProcessor.AssetAdministrationShellDescriptorVisitor;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class InMemoryStorageSearch {

	private final Collection<AssetAdministrationShellDescriptor> aasDescriptors;

	public ShellDescriptorSearchResponse performSearch(ShellDescriptorSearchRequest request) {

		Stream<AssetAdministrationShellDescriptor> matchingDescriptors = resolveMatchingDescriptors(request).stream();

		Stream<AssetAdministrationShellDescriptor> sorted = applySorting(matchingDescriptors, request.getSortBy());
		List<AssetAdministrationShellDescriptor> items = sorted.collect(Collectors.toList());
		long totalSizeOverAllPages = items.size();
		List<AssetAdministrationShellDescriptor> result = applyPagination(items, request.getPage());
		return new ShellDescriptorSearchResponse(totalSizeOverAllPages, result);
	}

	private Stream<AssetAdministrationShellDescriptor> applySorting(Stream<AssetAdministrationShellDescriptor> matchingDescriptors, Sorting sortBy) {
		if (sortBy == null) {
			sortBy = getDefaultSorting();
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

	private Sorting getDefaultSorting() {
		 return new Sorting(List.of(SortingPath.ID)).direction(SortDirection.ASC);
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

	private Collection<AssetAdministrationShellDescriptor> resolveMatchingDescriptors(ShellDescriptorSearchRequest request) {
		Collection<AssetAdministrationShellDescriptor> toFilter = new LinkedList<>(aasDescriptors);
		if (request == null) { // match all
			return toFilter;
		}
		ShellDescriptorQuery query = request.getQuery();
		if (query == null) {
			return toFilter;
		}
		AssetAdministrationShellFilter filter = new AssetAdministrationShellFilter(aasDescriptors);
		return filter.filterByRequest(request);
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
			public void visitResolvedPathValue(String path, Object[] objectPathToValue, String value) {
				if (this.value == null || this.value.isEmpty()) {
					this.value = value;
				}
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
}
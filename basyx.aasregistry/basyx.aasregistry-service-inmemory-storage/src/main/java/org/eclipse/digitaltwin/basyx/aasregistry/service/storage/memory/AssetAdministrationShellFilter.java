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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiPredicate;

import org.eclipse.digitaltwin.basyx.aasregistry.model.AssetAdministrationShellDescriptor;
import org.eclipse.digitaltwin.basyx.aasregistry.model.ShellDescriptorQuery;
import org.eclipse.digitaltwin.basyx.aasregistry.model.ShellDescriptorSearchRequest;
import org.eclipse.digitaltwin.basyx.aasregistry.model.SubmodelDescriptor;
import org.eclipse.digitaltwin.basyx.aasregistry.paths.AasRegistryPathProcessor;
import org.eclipse.digitaltwin.basyx.aasregistry.paths.AasRegistryPathProcessor.AssetAdministrationShellDescriptorVisitor;
import org.eclipse.digitaltwin.basyx.aasregistry.service.storage.DescriptorCopies;
import org.eclipse.digitaltwin.basyx.aasregistry.service.storage.ShellDescriptorSearchRequests;
import org.eclipse.digitaltwin.basyx.aasregistry.service.storage.ShellDescriptorSearchRequests.GroupedQueries;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AssetAdministrationShellFilter {

	private final Collection<AssetAdministrationShellDescriptor> aasDescriptors;

	public List<AssetAdministrationShellDescriptor> filterByRequest(ShellDescriptorSearchRequest request) {
		GroupedQueries grouped = ShellDescriptorSearchRequests.groupQueries(request.getQuery());
		List<AssetAdministrationShellDescriptor> result = filterShellsByRootQuery(aasDescriptors, grouped.getQueriesOutsideSubmodel());
		return filterShellsBySubmodelQueries(result, grouped.getQueriesInsideSubmodel());
	}

	private List<AssetAdministrationShellDescriptor> filterShellsByRootQuery(Collection<AssetAdministrationShellDescriptor> descriptors, List<ShellDescriptorQuery> queries) {
		List<AssetAdministrationShellDescriptor> toReturn = new ArrayList<>();
		if (queries.isEmpty()) {
			toReturn.addAll(descriptors);
			return toReturn;
		}
		for (AssetAdministrationShellDescriptor eachDescriptor : descriptors) {
			if (rootPathElementMatchesValue(eachDescriptor, queries)) {
				toReturn.add(eachDescriptor);
			}
		}
		return toReturn;
	}

	private boolean rootPathElementMatchesValue(AssetAdministrationShellDescriptor eachDescriptor, List<ShellDescriptorQuery> queries) {
		AasRegistryPathProcessor processor = new AasRegistryPathProcessor(eachDescriptor);
		for (ShellDescriptorQuery eachQuery : queries) {
			BiPredicate<Object[], String> matcher = SearchMatchers.createMatcher(eachQuery);
			NonSubmodelFilterVisitor visitor = new NonSubmodelFilterVisitor(matcher);
			String path = eachQuery.getPath();
			processor.visitValuesAtPath(path, visitor);
			if (!visitor.wasMatching()) {
				return false;
			}
		}
		return true;
	}

	private List<AssetAdministrationShellDescriptor> filterShellsBySubmodelQueries(List<AssetAdministrationShellDescriptor> descriptors, List<ShellDescriptorQuery> queries) {
		if (queries.isEmpty()) { // no filtering
			return descriptors;
		}
		List<AssetAdministrationShellDescriptor> toReturn = new ArrayList<>();
		for (AssetAdministrationShellDescriptor eachDescriptor : descriptors) {
			List<SubmodelDescriptor> smList = eachDescriptor.getSubmodelDescriptors();
			if (smList == null || smList.isEmpty()) {
				continue; // not matching submodels
			}
			AssetAdministrationShellDescriptor matching = submodelPathElementMatchesValue(eachDescriptor, smList, queries);
			if (matching != null) {
				toReturn.add(matching);
			}
		}
		return toReturn;
	}

	private AssetAdministrationShellDescriptor submodelPathElementMatchesValue(AssetAdministrationShellDescriptor eachDescriptor, List<SubmodelDescriptor> smList, List<ShellDescriptorQuery> queries) {

		AasRegistryPathProcessor processor = new AasRegistryPathProcessor(eachDescriptor);
		Map<String, List<BiPredicate<Object[], String>>> pathToPredicate = SearchMatchers.createMatchers(queries);

		SubmodelFilterVisitor visitor = new SubmodelFilterVisitor(smList, pathToPredicate);
		for (Entry<String, List<BiPredicate<Object[], String>>> eachEntry : pathToPredicate.entrySet()) {
			processor.visitValuesAtPath(eachEntry.getKey(), visitor);	
		}
		List<SubmodelDescriptor> matchingSubmodels = visitor.getMatchingSubmodels();
		if (matchingSubmodels.isEmpty()) {
			return null;
		} else if (matchingSubmodels.size() == smList.size()) {
			return eachDescriptor;
		} else {
			// we remove all non matching submodels, we alter the model so we need a clone
			AssetAdministrationShellDescriptor clone = DescriptorCopies.deepClone(eachDescriptor);
			clone.getSubmodelDescriptors().retainAll(matchingSubmodels);
			return clone;
		}

	}

	@RequiredArgsConstructor
	private static final class NonSubmodelFilterVisitor implements AssetAdministrationShellDescriptorVisitor {

		private final BiPredicate<Object[], String> matcher;
		private boolean wasMatching;

		@Override
		public void visitResolvedPathValue(String path, Object[] objectPathToValue, String value) {
			if (matcher.test(objectPathToValue, value)) {
				wasMatching = true;
			}
		}

		public boolean wasMatching() {
			return wasMatching;
		}
	}

	@RequiredArgsConstructor
	private static final class SubmodelFilterVisitor implements AssetAdministrationShellDescriptorVisitor {

		private final Map<SubmodelDescriptor, List<BiPredicate<Object[], String>>> submodelsWithRemainingMatchers = new LinkedHashMap<>();

		public SubmodelFilterVisitor(List<SubmodelDescriptor> submodels, Map<String, List<BiPredicate<Object[], String>>> allToMatch) {
			for (SubmodelDescriptor eachDescriptor : submodels) {
				List<BiPredicate<Object[], String>> matcherList = submodelsWithRemainingMatchers.computeIfAbsent(eachDescriptor, p -> new ArrayList<>());
				for (List<BiPredicate<Object[], String>> eachListToMatch : allToMatch.values()) {
					matcherList.addAll(eachListToMatch);
				}
			}
		}

		@Override
		public void visitResolvedPathValue(String path, Object[] objectPathToValue, String value) {
			SubmodelDescriptor current = (SubmodelDescriptor) objectPathToValue[1];
			List<BiPredicate<Object[], String>> remaining = submodelsWithRemainingMatchers.get(current);
			Iterator<BiPredicate<Object[], String>> remainingIter = remaining.iterator();
			while (remainingIter.hasNext()) {
				BiPredicate<Object[], String> nextMatcher = remainingIter.next();
				if (nextMatcher.test(objectPathToValue, value)) {
					remainingIter.remove();
				}
			}
		}

		public List<SubmodelDescriptor> getMatchingSubmodels() {
			List<SubmodelDescriptor> retValue = new LinkedList<>();
			for (Entry<SubmodelDescriptor, List<BiPredicate<Object[], String>>> eachEntry : submodelsWithRemainingMatchers.entrySet()) {
				if (eachEntry.getValue().isEmpty()) {
					retValue.add(eachEntry.getKey());
				}
			}
			return retValue;
		}
	}
}
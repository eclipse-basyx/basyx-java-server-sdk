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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.regex.Pattern;

import org.eclipse.digitaltwin.basyx.aasregistry.model.Extension;
import org.eclipse.digitaltwin.basyx.aasregistry.model.ShellDescriptorQuery;
import org.eclipse.digitaltwin.basyx.aasregistry.model.ShellDescriptorQuery.QueryTypeEnum;

import lombok.RequiredArgsConstructor;

class SearchMatchers {

	private SearchMatchers() {		
	}

	public static Map<String, List<BiPredicate<Object[], String>>> createMatchers(List<ShellDescriptorQuery> queryList) {
		Map<String, List<BiPredicate<Object[], String>>> matchers = new HashMap<>();
		for (ShellDescriptorQuery eachQuery : queryList) {
			String path = eachQuery.getPath();
			List<BiPredicate<Object[], String>> current = matchers.computeIfAbsent(path, p -> new ArrayList<>());
			current.add(createMatcher(eachQuery));
		}
		return matchers;
	}
	
	
	public static BiPredicate<Object[], String> createMatcher(ShellDescriptorQuery query) {
		QueryTypeEnum queryType = query.getQueryType();
		String value = query.getValue();

		BiPredicate<Object[], String> matcher;
		if (queryType == QueryTypeEnum.REGEX) {
			Pattern pattern = Pattern.compile(value);
			matcher = (path, elemValue) -> pattern.matcher(elemValue).matches();
		} else {
			matcher = (path, elemValue) -> Objects.equals(elemValue, value);
		}
		String extensionName = query.getExtensionName();
		if (extensionName != null) {
			return new TagExtensionMatcher(extensionName).and(matcher);
		}
		return matcher;
	}
	
	
	@RequiredArgsConstructor
	private static class TagExtensionMatcher implements BiPredicate<Object[], String> {

		private final String extensionName;

		
		@Override
		public boolean test(Object[] objectPath, String currentElem) {
			Extension extension = getExtensionObject(objectPath);
			return extensionName.equals(extension.getName());
		}

		private Extension getExtensionObject(Object[] objectPath) {
			return (Extension) objectPath[objectPath.length - 1];
		}
	}
}

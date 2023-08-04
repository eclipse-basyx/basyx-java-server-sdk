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

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.digitaltwin.basyx.aasregistry.service.storage.DescriptorFilter;
import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PaginationSupport<T extends Object> {

	private final TreeMap<String, T> sortedDescriptorMap;
	
	private final Function<T, String> idResolver; 

	public CursorResult<List<T>> getDescriptorsPaged(PaginationInfo pInfo) {
		return getDescriptorsPagedAndFiltered(pInfo, null, null);
	}
	
	public CursorResult<List<T>> getDescriptorsPagedAndFiltered(PaginationInfo pInfo, DescriptorFilter filter, Predicate<T> filterMethod) {
				
		Map<String, T> cursorView = getCursorView(pInfo);
		Stream<Entry<String, T>> eStream = cursorView.entrySet().stream();
		
		eStream = applyFilter(filter, e -> filterMethod.test(e.getValue()), eStream);
		Stream<T> tStream = eStream.map(Entry::getValue);
		tStream = applyLimit(pInfo, tStream);
		
		List<T> descriptorList = tStream.collect(Collectors.toList());
		
		String cursor = computeNextCursor(descriptorList);
		return new CursorResult<>(cursor, Collections.unmodifiableList(descriptorList));
	}


	private Stream<Entry<String, T>> applyFilter(DescriptorFilter filter, Predicate<Entry<String, T>> filterMethod, Stream<Entry<String, T>> aStream) {
		if (filter != null && filter.isFiltered()) {
			return aStream.filter(filterMethod);
		}
		return aStream;
	}

	private Stream<T> applyLimit(PaginationInfo info, Stream<T> aStream) {
		if (info.hasLimit()) {
			return aStream.limit(info.getLimit());
		}
		return aStream;
	}

	private String computeNextCursor(List<T> descriptorList) {
		if (!descriptorList.isEmpty()) {
			T last = descriptorList.get(descriptorList.size()-1);
			String lastId = idResolver.apply(last);
			return sortedDescriptorMap.higherKey(lastId);
		}
		return null;
	}

	private Map<String, T> getCursorView(PaginationInfo info) {
		if (info.hasCursor()) {
			return sortedDescriptorMap.tailMap(info.getCursor());
		} else {
			return sortedDescriptorMap;
		}
	}
}
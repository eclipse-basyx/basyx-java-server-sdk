/*******************************************************************************
 * Copyright (C) 2023 DFKI GmbH (https://www.dfki.de/en/web)
 * Copyright (C) 2023 the Eclipse BaSyx Authors
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

package org.eclipse.digitaltwin.basyx.core.pagination;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PaginationSupport<T extends Object> {

	private final TreeMap<String, T> sortedMap;
	private final Function<T, String> idResolver;

	public PaginationSupport(TreeMap<String, T> sortedMap, Function<T, String> idResolver) {
		this.sortedMap = sortedMap;
		this.idResolver = idResolver;
	}

	public CursorResult<List<T>> getPaged(PaginationInfo pInfo) {
		Map<String, T> cursorView = getCursorView(pInfo);
		Stream<Entry<String, T>> eStream = cursorView.entrySet()
				.stream();

		Stream<T> tStream = eStream.map(Entry::getValue);
		tStream = applyLimit(pInfo, tStream);

		List<T> resultList = tStream.collect(Collectors.toList());
		String cursor;
		if (pInfo.hasLimit() && resultList.size() < pInfo.getLimit()) {
			cursor = null; // got less than requested
		} else {
			cursor = computeNextCursor(resultList, pInfo);
		}
		return new CursorResult<>(cursor, resultList);
	}

	private Stream<T> applyLimit(PaginationInfo info, Stream<T> aStream) {
		if (info.hasLimit()) {
			return aStream.limit(info.getLimit());
		}
		return aStream;
	}

	private String computeNextCursor(List<T> list, PaginationInfo pInfo) {
		if (!pInfo.hasLimit()) {
			return null;
		}
		if (!list.isEmpty()) {
			T last = list.get(list.size() - 1);
			return idResolver.apply(last);
		}
		return null;
	}

	private Map<String, T> getCursorView(PaginationInfo info) {
		if (info.hasCursor()) {
			return sortedMap.tailMap(info.getCursor(), false);
		} else {
			return sortedMap;
		}
	}

}

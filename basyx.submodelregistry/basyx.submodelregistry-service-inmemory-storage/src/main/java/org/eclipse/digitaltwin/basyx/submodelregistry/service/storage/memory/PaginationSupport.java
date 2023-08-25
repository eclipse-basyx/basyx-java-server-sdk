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
package org.eclipse.digitaltwin.basyx.submodelregistry.service.storage.memory;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.eclipse.digitaltwin.basyx.submodelregistry.model.SubmodelDescriptor;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PaginationSupport {

	private final TreeMap<String, SubmodelDescriptor> sortedMap;
	
	public CursorResult<List<SubmodelDescriptor>> getDescriptorsPaged(PaginationInfo pInfo) {
		Map<String, SubmodelDescriptor> cursorView = getCursorView(pInfo);
		Stream<Entry<String, SubmodelDescriptor>> eStream = cursorView.entrySet().stream();
		Stream<SubmodelDescriptor> tStream = eStream.map(Entry::getValue);
		tStream = applyLimit(pInfo, tStream);		
		List<SubmodelDescriptor> descriptorList = tStream.collect(Collectors.toList());
		String cursor = computeNextCursor(descriptorList);
		return new CursorResult<List<SubmodelDescriptor>>(cursor, Collections.unmodifiableList(descriptorList));
	}

	private Stream<SubmodelDescriptor> applyLimit(PaginationInfo info, Stream<SubmodelDescriptor> aStream) {
		if (info.hasLimit()) {
			return aStream.limit(info.getLimit());
		}
		return aStream;
	}

	private String computeNextCursor(List<SubmodelDescriptor> descriptorList) {
		if (!descriptorList.isEmpty()) {
			SubmodelDescriptor last = descriptorList.get(descriptorList.size()-1);
			String lastId = last.getId();
			return sortedMap.higherKey(lastId);
		}
		return null;
	}

	private Map<String, SubmodelDescriptor> getCursorView(PaginationInfo info) {
		if (info.hasCursor()) {
			return sortedMap.tailMap(info.getCursor());
		} else {
			return sortedMap;
		}
	}
}
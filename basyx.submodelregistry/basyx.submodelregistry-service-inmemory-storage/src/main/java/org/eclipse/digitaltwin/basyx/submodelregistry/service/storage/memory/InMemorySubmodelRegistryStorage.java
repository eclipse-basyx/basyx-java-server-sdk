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

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.eclipse.digitaltwin.basyx.submodelregistry.model.SubmodelDescriptor;
import org.eclipse.digitaltwin.basyx.submodelregistry.service.errors.SubmodelAlreadyExistsException;
import org.eclipse.digitaltwin.basyx.submodelregistry.service.errors.SubmodelNotFoundException;
import org.eclipse.digitaltwin.basyx.submodelregistry.service.storage.SubmodelRegistryStorage;

import lombok.NonNull;

public class InMemorySubmodelRegistryStorage implements SubmodelRegistryStorage {

	private final HashMap<String, SubmodelDescriptor> submodelLookupMap = new HashMap<>();
	private final TreeMap<String, SubmodelDescriptor> sortedSubmodelMap = new TreeMap<>();

	@Override
	public CursorResult<List<SubmodelDescriptor>> getAllSubmodelDescriptors(@NonNull PaginationInfo pRequest) {
		PaginationSupport paginationSupport = new PaginationSupport(sortedSubmodelMap);
		return paginationSupport.getDescriptorsPaged(pRequest);
	}

	@Override
	public SubmodelDescriptor getSubmodelDescriptor(@NonNull String submodelId) throws SubmodelNotFoundException {
		SubmodelDescriptor elem = submodelLookupMap.get(submodelId);
		if (elem == null) {
			throw new SubmodelNotFoundException(submodelId);
		}
		return elem;
	}

	@Override
	public void insertSubmodelDescriptor(@NonNull SubmodelDescriptor descr) throws SubmodelAlreadyExistsException {
		String id = descr.getId();
		SubmodelDescriptor previous = submodelLookupMap.putIfAbsent(id, descr);
		if (previous != null) {
			throw new SubmodelAlreadyExistsException(id);
		}
		sortedSubmodelMap.put(id, descr);
	}

	@Override
	public void removeSubmodelDescriptor(@NonNull String submodelId) throws SubmodelNotFoundException {
		SubmodelDescriptor previous = submodelLookupMap.remove(submodelId);
		if (previous == null) {
			throw new SubmodelNotFoundException(submodelId);
		}
		sortedSubmodelMap.remove(submodelId);
	}

	@Override
	public void replaceSubmodelDescriptor(@NonNull String submodelId, @NonNull SubmodelDescriptor descr) throws SubmodelNotFoundException {
		if (!submodelLookupMap.containsKey(submodelId)) {
			throw new SubmodelNotFoundException(submodelId);
		}
		String toReplaceId = descr.getId();
		if (!Objects.equals(submodelId, toReplaceId)) {
			submodelLookupMap.remove(submodelId);
			sortedSubmodelMap.remove(submodelId);
		}
		submodelLookupMap.put(toReplaceId, descr);
		sortedSubmodelMap.put(toReplaceId, descr);
	}

	@Override
	public Set<String> clear() {
		Set<String> keys = new HashSet<>(sortedSubmodelMap.keySet());
		submodelLookupMap.clear();
		sortedSubmodelMap.clear();
		return keys;
	}
}
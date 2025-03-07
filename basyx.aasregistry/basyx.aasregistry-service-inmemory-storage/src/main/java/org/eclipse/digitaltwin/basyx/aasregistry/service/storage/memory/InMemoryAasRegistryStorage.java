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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.eclipse.digitaltwin.basyx.aasregistry.model.AssetAdministrationShellDescriptor;
import org.eclipse.digitaltwin.basyx.aasregistry.model.AssetKind;
import org.eclipse.digitaltwin.basyx.aasregistry.model.ShellDescriptorSearchRequest;
import org.eclipse.digitaltwin.basyx.aasregistry.model.ShellDescriptorSearchResponse;
import org.eclipse.digitaltwin.basyx.aasregistry.model.SubmodelDescriptor;
import org.eclipse.digitaltwin.basyx.aasregistry.service.errors.AasDescriptorAlreadyExistsException;
import org.eclipse.digitaltwin.basyx.aasregistry.service.errors.AasDescriptorNotFoundException;
import org.eclipse.digitaltwin.basyx.aasregistry.service.errors.SubmodelAlreadyExistsException;
import org.eclipse.digitaltwin.basyx.aasregistry.service.errors.SubmodelNotFoundException;
import org.eclipse.digitaltwin.basyx.aasregistry.service.storage.AasRegistryStorage;
import org.eclipse.digitaltwin.basyx.aasregistry.service.storage.DescriptorFilter;
import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;

import lombok.RequiredArgsConstructor;

public class InMemoryAasRegistryStorage implements AasRegistryStorage {

	private final TreeMap<String, AssetAdministrationShellDescriptor> aasDescriptorLookupMap = new TreeMap<>();
	private final HashMap<String, TreeMap<String, SubmodelDescriptor>> submodelLookupMap = new HashMap<>();

	private boolean containsSubmodel(String aasDescriptorId, String submodelId) {
		Map<String, SubmodelDescriptor> submodels = submodelLookupMap.get(aasDescriptorId);
		return submodels != null && submodels.containsKey(submodelId);
	}

	@Override
	public CursorResult<List<AssetAdministrationShellDescriptor>> getAllAasDescriptors(PaginationInfo pRequest, DescriptorFilter filter) {
		PaginationSupport<AssetAdministrationShellDescriptor> paginationSupport = new PaginationSupport<>(aasDescriptorLookupMap, AssetAdministrationShellDescriptor::getId);

		DescriptorFilterFunction function = new DescriptorFilterFunction(filter);
		return paginationSupport.getDescriptorsPagedAndFiltered(pRequest, filter, function::matches);
	}

	@Override
	public CursorResult<List<SubmodelDescriptor>> getAllSubmodels(String aasDescriptorId, PaginationInfo pInfo) throws AasDescriptorNotFoundException {
		TreeMap<String, SubmodelDescriptor> submodels = submodelLookupMap.get(aasDescriptorId);
		if (submodels == null) {
			throw new AasDescriptorNotFoundException(aasDescriptorId);
		}
		PaginationSupport<SubmodelDescriptor> paginationSupport = new PaginationSupport<>(submodels, SubmodelDescriptor::getId);
		return paginationSupport.getDescriptorsPaged(pInfo);
	}

	@Override
	public AssetAdministrationShellDescriptor getAasDescriptor(String aasId) {
		AssetAdministrationShellDescriptor descriptor = aasDescriptorLookupMap.get(aasId);
		if (descriptor == null) {
			throw new AasDescriptorNotFoundException(aasId);
		}
		return descriptor;
	}

	@Override
	public void replaceAasDescriptor(String aasId, AssetAdministrationShellDescriptor descriptor) {
		if (!aasDescriptorLookupMap.containsKey(aasId)) {
			throw new AasDescriptorNotFoundException(aasId);
		}
		TreeMap<String, SubmodelDescriptor> newSubmodelMap = toSubmodelLookupMap(descriptor.getSubmodelDescriptors());
		String newAasDescrId = descriptor.getId();
		aasDescriptorLookupMap.remove(aasId);
		aasDescriptorLookupMap.put(newAasDescrId, descriptor);
		submodelLookupMap.remove(aasId);
		submodelLookupMap.put(newAasDescrId, newSubmodelMap);
	}

	@Override
	public void insertAasDescriptor( AssetAdministrationShellDescriptor descr) throws AasDescriptorAlreadyExistsException {
		String aasId = descr.getId();
		if (aasDescriptorLookupMap.containsKey(aasId)) {
			throw new AasDescriptorAlreadyExistsException(aasId);
		}
		TreeMap<String, SubmodelDescriptor> newSubmodelMap = toSubmodelLookupMap(descr.getSubmodelDescriptors());
		aasDescriptorLookupMap.put(aasId, descr);
		submodelLookupMap.put(aasId, newSubmodelMap);
	}
	
	private TreeMap<String, SubmodelDescriptor> toSubmodelLookupMap(List<SubmodelDescriptor> submodelDescriptors) {
		return Optional.ofNullable(submodelDescriptors).orElseGet(LinkedList::new).stream().collect(Collectors.toMap(SubmodelDescriptor::getId, Function.identity(), this::mergeSubmodels, TreeMap::new));
	}

	private SubmodelDescriptor mergeSubmodels(SubmodelDescriptor descr1, SubmodelDescriptor descr2) {
		throw new DuplicateSubmodelIds(descr1.getId());
	}

	@Override
	public void removeAasDescriptor(String aasDescriptorId) {
		boolean success = aasDescriptorLookupMap.remove(aasDescriptorId) != null && submodelLookupMap.remove(aasDescriptorId) != null;
		if (!success) {
			throw new AasDescriptorNotFoundException(aasDescriptorId);
		}
	}

	@Override
	public SubmodelDescriptor getSubmodel( String aasDescriptorId, String submodelId) {
		Map<String, SubmodelDescriptor> descriptorModels = submodelLookupMap.get(aasDescriptorId);
		if (descriptorModels == null) {
			throw new AasDescriptorNotFoundException(aasDescriptorId);
		}
		SubmodelDescriptor submodel = descriptorModels.get(submodelId);
		if (submodel == null) {
			throw new SubmodelNotFoundException(aasDescriptorId, submodelId);
		}
		return submodel;
	}

	@Override
	public void insertSubmodel(String aasDescriptorId, SubmodelDescriptor submodel) {
		AssetAdministrationShellDescriptor aasDescriptor = getAasDescriptor(aasDescriptorId);
		String submodelId = submodel.getId();
		if (containsSubmodel(aasDescriptorId, submodelId)) {
			throw new SubmodelAlreadyExistsException(aasDescriptorId, submodelId);
		} else { // just append
			aasDescriptor.addSubmodelDescriptorsItem(submodel);
		}
		// update map
		submodelLookupMap.get(aasDescriptorId).put(submodelId, submodel);
	}

	@Override
	public void replaceSubmodel(String aasDescriptorId, String submodelId, SubmodelDescriptor submodel) throws AasDescriptorNotFoundException, SubmodelNotFoundException {
		if (!aasDescriptorLookupMap.containsKey(aasDescriptorId)) {
			throw new AasDescriptorNotFoundException(submodelId);
		} else if (!containsSubmodel(aasDescriptorId, submodelId)) {
			throw new SubmodelNotFoundException(aasDescriptorId, submodelId);
		} else {
			replaceSubmodelInAasDescriptor(aasDescriptorId, submodelId, submodel);
		}
		TreeMap<String, SubmodelDescriptor> modelMap = submodelLookupMap.get(aasDescriptorId);
		modelMap.remove(submodelId);
		// could be a different id
		modelMap.put(submodel.getId(), submodel);

	}

	private void replaceSubmodelInAasDescriptor(String aasDescriptorId, String submodelId, SubmodelDescriptor submodel) {
		AssetAdministrationShellDescriptor aasDescriptor = aasDescriptorLookupMap.get(aasDescriptorId);		
		ListIterator<SubmodelDescriptor> submodels = Optional.ofNullable(aasDescriptor.getSubmodelDescriptors())
							.orElse(Collections.emptyList()).listIterator();	
		while (submodels.hasNext()) {
			SubmodelDescriptor eachItem = submodels.next();
			if (Objects.equals(eachItem.getId(), submodelId)) {
				submodels.set(submodel);
				return;
			}
		}
	}

	@Override
	public void removeSubmodel(String aasDescrId, String submodelId) {
		AssetAdministrationShellDescriptor descriptor = aasDescriptorLookupMap.get(aasDescrId);
		if (descriptor == null) {
			throw new AasDescriptorNotFoundException(aasDescrId);
		}
		boolean success = removeStoredSubmodel(aasDescrId, descriptor, submodelId);
		if (!success) {
			throw new SubmodelNotFoundException(aasDescrId, submodelId);
		}
	}

	private boolean removeStoredSubmodel(String aasDescriptorId, AssetAdministrationShellDescriptor aasDescriptor, String submodelId) {
		if (submodelLookupMap.get(aasDescriptorId).remove(submodelId) == null) {
			return false;
		} else { // found submodel so also remove it from the aasDescriptor object
			removeSubmodelFromDescriptor(aasDescriptor, submodelId);
			return true;
		}
	}

	private void removeSubmodelFromDescriptor(AssetAdministrationShellDescriptor aasDescriptor, String submodelId) {
		Iterator<SubmodelDescriptor> submodelIter = aasDescriptor.getSubmodelDescriptors().iterator();
		while (submodelIter.hasNext()) {
			SubmodelDescriptor eachItem = submodelIter.next();
			if (Objects.equals(eachItem.getId(), submodelId)) {
				submodelIter.remove();
				break;
			}
		}
	}

	@Override
	public Set<String> clear() {
		Set<String> keys = new HashSet<>(aasDescriptorLookupMap.keySet());
		aasDescriptorLookupMap.clear();
		submodelLookupMap.clear();
		return keys;
	}

	@Override
	public ShellDescriptorSearchResponse searchAasDescriptors(ShellDescriptorSearchRequest request) {
		Collection<AssetAdministrationShellDescriptor> descriptors = aasDescriptorLookupMap.values();
		InMemoryStorageSearch search = new InMemoryStorageSearch(descriptors);
		return search.performSearch(request);
	}

	public static final class DuplicateSubmodelIds extends RuntimeException {

		private static final long serialVersionUID = 1L;

		public DuplicateSubmodelIds(String id) {
			super("The submodel id '" + id + "' is stored mulitple times in the descriptor");
		}
	}

	@RequiredArgsConstructor
	private static class DescriptorFilterFunction {

		private final DescriptorFilter filter;

		public boolean matches(AssetAdministrationShellDescriptor descr) {

			return matchesKind(filter.getKind(), descr.getAssetKind()) && matchesType(filter.getAssetType(), descr.getAssetType());
		}

		private boolean matchesKind(AssetKind filterKind, AssetKind descrKind) {
			if (filterKind == null) {
				return true;
			}
			if (filterKind == AssetKind.NOTAPPLICABLE) {
				return descrKind == null;
			}
			return filterKind == descrKind;
		}

		private boolean matchesType(String filterType, String descrType) {
			if (filterType == null) {
				return true;
			}
			return filterType.equals(descrType);
		}
	}

}
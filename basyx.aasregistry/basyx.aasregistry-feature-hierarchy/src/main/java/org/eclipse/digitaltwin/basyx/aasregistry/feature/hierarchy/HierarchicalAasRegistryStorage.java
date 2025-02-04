/*******************************************************************************
 * Copyright (C) 2025 the Eclipse BaSyx Authors
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

package org.eclipse.digitaltwin.basyx.aasregistry.feature.hierarchy;

import java.util.List;
import java.util.Set;

import org.eclipse.digitaltwin.basyx.aasregistry.client.ApiException;
import org.eclipse.digitaltwin.basyx.aasregistry.client.api.RegistryAndDiscoveryInterfaceApi;
import org.eclipse.digitaltwin.basyx.aasregistry.model.AssetAdministrationShellDescriptor;
import org.eclipse.digitaltwin.basyx.aasregistry.model.ShellDescriptorSearchRequest;
import org.eclipse.digitaltwin.basyx.aasregistry.model.ShellDescriptorSearchResponse;
import org.eclipse.digitaltwin.basyx.aasregistry.model.SubmodelDescriptor;
import org.eclipse.digitaltwin.basyx.aasregistry.service.errors.AasDescriptorAlreadyExistsException;
import org.eclipse.digitaltwin.basyx.aasregistry.service.errors.AasDescriptorNotFoundException;
import org.eclipse.digitaltwin.basyx.aasregistry.service.errors.SubmodelAlreadyExistsException;
import org.eclipse.digitaltwin.basyx.aasregistry.service.errors.SubmodelNotFoundException;
import org.eclipse.digitaltwin.basyx.aasregistry.service.storage.AasRegistryStorage;
import org.eclipse.digitaltwin.basyx.aasregistry.service.storage.DescriptorFilter;
import org.eclipse.digitaltwin.basyx.common.hierarchy.delegation.DelegationStrategy;
import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;

/**
 * Decorator for Hierarchical {@link AasRegistryStorage}
 *
 * @author mateusmolina
 */
public class HierarchicalAasRegistryStorage implements AasRegistryStorage {

	private final AasRegistryStorage decorated;
	
	private final DelegationStrategy delegationStrategy;

	public HierarchicalAasRegistryStorage(AasRegistryStorage decorated, DelegationStrategy delegationStrategy) {
		this.decorated = decorated;
		this.delegationStrategy = delegationStrategy;
	}

	@Override
	public CursorResult<List<AssetAdministrationShellDescriptor>> getAllAasDescriptors(PaginationInfo pRequest, DescriptorFilter filter) {
		return decorated.getAllAasDescriptors(pRequest, filter);
	}

	@Override
	public AssetAdministrationShellDescriptor getAasDescriptor(String aasDescriptorId) throws AasDescriptorNotFoundException {
		try {
			return decorated.getAasDescriptor(aasDescriptorId);
		} catch (AasDescriptorNotFoundException e) {
			try {
				return AasRegistryModelMapper.mapEqModel(getDelegatedRegistryApi(aasDescriptorId).getAssetAdministrationShellDescriptorById(aasDescriptorId));
			} catch (ApiException e1) {
				throw AasRegistryModelMapper.mapApiException(e1, aasDescriptorId);
			}
		}
	}

	@Override
	public void insertAasDescriptor(AssetAdministrationShellDescriptor descr) throws AasDescriptorAlreadyExistsException {
		decorated.insertAasDescriptor(descr);
	}

	@Override
	public void replaceAasDescriptor(String aasDescriptorId, AssetAdministrationShellDescriptor descriptor) throws AasDescriptorNotFoundException {
		decorated.replaceAasDescriptor(aasDescriptorId, descriptor);
	}

	@Override
	public void removeAasDescriptor(String aasDescriptorId) throws AasDescriptorNotFoundException {
		decorated.removeAasDescriptor(aasDescriptorId);
	}

	@Override
	public CursorResult<List<SubmodelDescriptor>> getAllSubmodels(String aasDescriptorId, PaginationInfo pRequest) throws AasDescriptorNotFoundException {
		try {
			return decorated.getAllSubmodels(aasDescriptorId, pRequest);
		} catch (AasDescriptorNotFoundException e) {
			try {
				return AasRegistryModelMapper.mapEqModel(getDelegatedRegistryApi(aasDescriptorId).getAllSubmodelDescriptorsThroughSuperpath(aasDescriptorId, pRequest.getLimit(), pRequest.getCursor()));
			} catch (ApiException e1) {
				throw AasRegistryModelMapper.mapApiException(e1, aasDescriptorId);
			}
		}
	}

	@Override
	public SubmodelDescriptor getSubmodel(String aasDescriptorId, String submodelId) throws AasDescriptorNotFoundException, SubmodelNotFoundException {
		try {
			return decorated.getSubmodel(aasDescriptorId, submodelId);
		} catch (AasDescriptorNotFoundException e) {
			try {
				return AasRegistryModelMapper.mapEqModel(getDelegatedRegistryApi(aasDescriptorId).getSubmodelDescriptorByIdThroughSuperpath(aasDescriptorId, submodelId));
			} catch (ApiException e1) {
				throw AasRegistryModelMapper.mapApiException(e1, aasDescriptorId, submodelId);
			}
		}
	}

	@Override
	public void insertSubmodel(String aasDescriptorId, SubmodelDescriptor submodel) throws AasDescriptorNotFoundException, SubmodelAlreadyExistsException {
		decorated.insertSubmodel(aasDescriptorId, submodel);
	}

	@Override
	public void replaceSubmodel(String aasDescriptorId, String submodelId, SubmodelDescriptor submodel) throws AasDescriptorNotFoundException, SubmodelNotFoundException {
		decorated.replaceSubmodel(aasDescriptorId, submodelId, submodel);
	}

	@Override
	public void removeSubmodel(String aasDescriptorId, String submodelId) throws AasDescriptorNotFoundException, SubmodelNotFoundException {
		decorated.removeSubmodel(aasDescriptorId, submodelId);
	}

	@Override
	public Set<String> clear() {
		return decorated.clear();
	}

	@Override
	public ShellDescriptorSearchResponse searchAasDescriptors(ShellDescriptorSearchRequest request) {
		return decorated.searchAasDescriptors(request);
	}
	
	private RegistryAndDiscoveryInterfaceApi getDelegatedRegistryApi(String aasId) {
		String delegationUrl = delegationStrategy.buildDelegatedRegistryUrl(aasId).orElseThrow(() -> new AasDescriptorNotFoundException(aasId));
		return new RegistryAndDiscoveryInterfaceApi(delegationUrl);
	}
}

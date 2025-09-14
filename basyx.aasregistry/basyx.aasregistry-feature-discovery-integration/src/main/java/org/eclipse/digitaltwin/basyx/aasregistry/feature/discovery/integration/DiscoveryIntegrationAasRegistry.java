
/*******************************************************************************
 * Copyright (C) 2024 the Eclipse BaSyx Authors
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

package org.eclipse.digitaltwin.basyx.aasregistry.feature.discovery.integration;

import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.digitaltwin.aas4j.v3.model.SpecificAssetId;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSpecificAssetId;
import org.eclipse.digitaltwin.basyx.aasdiscoveryservice.core.AasDiscoveryService;
import org.eclipse.digitaltwin.basyx.aasregistry.model.*;
import org.eclipse.digitaltwin.basyx.aasregistry.service.errors.AasDescriptorAlreadyExistsException;
import org.eclipse.digitaltwin.basyx.aasregistry.service.errors.AasDescriptorNotFoundException;
import org.eclipse.digitaltwin.basyx.aasregistry.service.errors.SubmodelAlreadyExistsException;
import org.eclipse.digitaltwin.basyx.aasregistry.service.errors.SubmodelNotFoundException;
import org.eclipse.digitaltwin.basyx.aasregistry.service.storage.AasRegistryStorage;
import org.eclipse.digitaltwin.basyx.aasregistry.service.storage.DescriptorFilter;
import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;

import java.util.Base64;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class DiscoveryIntegrationAasRegistry implements AasRegistryStorage {

	private final AasRegistryStorage decorated;

	private final AasDiscoveryService discoveryApi;

	public DiscoveryIntegrationAasRegistry(AasDiscoveryService discoveryApi, AasRegistryStorage decorated) {
		this.discoveryApi = discoveryApi;
		this.decorated = decorated;
	}

	@Override
	public CursorResult<List<AssetAdministrationShellDescriptor>> getAllAasDescriptors(@NonNull PaginationInfo pRequest, @NonNull DescriptorFilter filter) {
		return decorated.getAllAasDescriptors(pRequest, filter);
	}

	@Override
	public AssetAdministrationShellDescriptor getAasDescriptor(@NonNull String aasDescriptorId) throws AasDescriptorNotFoundException {
		return decorated.getAasDescriptor(aasDescriptorId);
	}

	@Override
	public void insertAasDescriptor(AssetAdministrationShellDescriptor descr) throws AasDescriptorAlreadyExistsException {
		decorated.insertAasDescriptor(descr);
		String encodedId = Base64.getEncoder().encodeToString(descr.getId().getBytes());
				@Valid List<org.eclipse.digitaltwin.basyx.aasregistry.model.@Valid SpecificAssetId> ids = descr.getSpecificAssetIds();
				List<SpecificAssetId> specificAssetIds = ids.stream()
						.map(rId -> {
							SpecificAssetId assetId = new DefaultSpecificAssetId();
							assetId.setName(rId.getName());
							assetId.setValue(rId.getValue());
							return assetId;
						}).collect(Collectors.toList());

		discoveryApi.createAllAssetLinksById(encodedId, specificAssetIds);
	}

	@Override
	public void replaceAasDescriptor(@NonNull String aasDescriptorId, @NonNull AssetAdministrationShellDescriptor descriptor) throws AasDescriptorNotFoundException {
		decorated.replaceAasDescriptor(aasDescriptorId, descriptor);
	}

	@Override
	public void removeAasDescriptor(@NonNull String aasDescriptorId) throws AasDescriptorNotFoundException {
		decorated.removeAasDescriptor(aasDescriptorId);
		discoveryApi.deleteAllAssetLinksById(Base64.getEncoder().encodeToString(aasDescriptorId.getBytes()));
	}

	@Override
	public CursorResult<List<SubmodelDescriptor>> getAllSubmodels(@NonNull String aasDescriptorId, @NonNull PaginationInfo pRequest) throws AasDescriptorNotFoundException {
		return decorated.getAllSubmodels(aasDescriptorId, pRequest);
	}

	@Override
	public SubmodelDescriptor getSubmodel(@NonNull String aasDescriptorId, @NonNull String submodelId) throws AasDescriptorNotFoundException, SubmodelNotFoundException {
		return decorated.getSubmodel(aasDescriptorId,submodelId);
	}

	@Override
	public void insertSubmodel(@NonNull String aasDescriptorId, @NonNull SubmodelDescriptor submodel) throws AasDescriptorNotFoundException, SubmodelAlreadyExistsException {
		decorated.insertSubmodel(aasDescriptorId,submodel);
	}

	@Override
	public void replaceSubmodel(@NonNull String aasDescriptorId, @NonNull String submodelId, @NonNull SubmodelDescriptor submodel) throws AasDescriptorNotFoundException, SubmodelNotFoundException {
		decorated.replaceSubmodel(aasDescriptorId, submodelId, submodel);
	}

	@Override
	public void removeSubmodel(@NonNull String aasDescriptorId, @NonNull String submodelId) throws AasDescriptorNotFoundException, SubmodelNotFoundException {
		decorated.removeSubmodel(aasDescriptorId, submodelId);
	}

	@Override
	public Set<String> clear() {
		return decorated.clear();
	}

	@Override
	public ShellDescriptorSearchResponse searchAasDescriptors(@NonNull ShellDescriptorSearchRequest request) {
		return decorated.searchAasDescriptors(request);
	}
}

/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
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

import lombok.extern.slf4j.Slf4j;
import org.eclipse.digitaltwin.basyx.aasregistry.service.api.ShellDescriptorsApiDelegate;
import org.eclipse.digitaltwin.basyx.aasregistry.service.storage.AasRegistryStorage;
import jakarta.validation.Valid;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSpecificAssetId;
import org.eclipse.digitaltwin.aas4j.v3.model.SpecificAssetId;
import org.eclipse.digitaltwin.basyx.aasdiscoveryservice.backend.mongodb.backend.MongoDBCrudAasDiscovery;
import org.eclipse.digitaltwin.basyx.aasregistry.model.*;
import org.springframework.beans.factory.ObjectProvider;

import org.springframework.http.ResponseEntity;

import java.util.Base64;
import java.util.List;

import java.util.stream.Collectors;


@Slf4j
public class DiscoveryEnhancedShellDescriptors implements ShellDescriptorsApiDelegate {

	private final MongoDBCrudAasDiscovery mongoDBCrudAasDiscovery;
	private final AasRegistryStorage storage;
	private final ShellDescriptorsApiDelegate originalDelegate;

	public DiscoveryEnhancedShellDescriptors(
			AasRegistryStorage storage,
			ObjectProvider<ShellDescriptorsApiDelegate> delegateProvider,
			MongoDBCrudAasDiscovery mongoDBCrudAasDiscovery) {
		this.storage = storage;
		this.mongoDBCrudAasDiscovery = mongoDBCrudAasDiscovery;
		this.originalDelegate = delegateProvider.stream()
				.filter(delegate -> !(delegate instanceof DiscoveryEnhancedShellDescriptors))
				.findFirst()
				.orElseThrow(() -> new IllegalStateException("No original ShellDescriptorsApiDelegate found"));
	}

	@Override
	public ResponseEntity<GetAssetAdministrationShellDescriptorsResult> getAllAssetAdministrationShellDescriptors(
			Integer limit, String cursor, AssetKind assetKind, String assetType) {
		return originalDelegate.getAllAssetAdministrationShellDescriptors(limit, cursor, assetKind, assetType);
	}

	@Override
	public ResponseEntity<Void> deleteAllShellDescriptors() {
		ResponseEntity<GetAssetAdministrationShellDescriptorsResult> allShells = getAllAssetAdministrationShellDescriptors(null, null, null, null);
		List<AssetAdministrationShellDescriptor> allShellIdentifiers = allShells.getBody().getResult();
		allShellIdentifiers.parallelStream().forEach(assetAdministrationShellDescriptor ->
				mongoDBCrudAasDiscovery.deleteAllAssetLinksById(Base64.getEncoder().encodeToString(assetAdministrationShellDescriptor.getId().getBytes())));
		return originalDelegate.deleteAllShellDescriptors();
	}

	@Override
	public ResponseEntity<Void> deleteAssetAdministrationShellDescriptorById(String aasIdentifier) {
		return originalDelegate.deleteAssetAdministrationShellDescriptorById(aasIdentifier);
	}

	@Override
	public ResponseEntity<Void> deleteSubmodelDescriptorByIdThroughSuperpath(String aasIdentifier, String submodelIdentifier) {
		return originalDelegate.deleteSubmodelDescriptorByIdThroughSuperpath(aasIdentifier, submodelIdentifier);
	}

	@Override
	public ResponseEntity<GetSubmodelDescriptorsResult> getAllSubmodelDescriptorsThroughSuperpath(String aasIdentifier, Integer limit, String cursor) {
		return originalDelegate.getAllSubmodelDescriptorsThroughSuperpath(aasIdentifier, limit, cursor);
	}

	@Override
	public ResponseEntity<AssetAdministrationShellDescriptor> getAssetAdministrationShellDescriptorById(String aasIdentifier) {
		return originalDelegate.getAssetAdministrationShellDescriptorById(aasIdentifier);
	}

	@Override
	public ResponseEntity<SubmodelDescriptor> getSubmodelDescriptorByIdThroughSuperpath(String aasIdentifier, String submodelIdentifier) {
		return originalDelegate.getSubmodelDescriptorByIdThroughSuperpath(aasIdentifier, submodelIdentifier);
	}

	@Override
	public ResponseEntity<AssetAdministrationShellDescriptor> postAssetAdministrationShellDescriptor(AssetAdministrationShellDescriptor assetAdministrationShellDescriptor) {
		String encodedId = Base64.getEncoder().encodeToString(assetAdministrationShellDescriptor.getId().getBytes());
		@Valid List<org.eclipse.digitaltwin.basyx.aasregistry.model.@Valid SpecificAssetId> ids = assetAdministrationShellDescriptor.getSpecificAssetIds();
		List<SpecificAssetId> specificAssetIds = ids.stream()
				.map(rId -> {
					SpecificAssetId assetId = new DefaultSpecificAssetId();
					assetId.setName(rId.getName());
					assetId.setValue(rId.getValue());
					return assetId;
				}).collect(Collectors.toList());

		mongoDBCrudAasDiscovery.createAllAssetLinksById(encodedId, specificAssetIds);
		return originalDelegate.postAssetAdministrationShellDescriptor(assetAdministrationShellDescriptor);
	}

	@Override
	public ResponseEntity<SubmodelDescriptor> postSubmodelDescriptorThroughSuperpath(String aasIdentifier, SubmodelDescriptor submodelDescriptor) {
		return originalDelegate.postSubmodelDescriptorThroughSuperpath(aasIdentifier, submodelDescriptor);
	}

	@Override
	public ResponseEntity<Void> putAssetAdministrationShellDescriptorById(String aasIdentifier, AssetAdministrationShellDescriptor assetAdministrationShellDescriptor) {
		return originalDelegate.putAssetAdministrationShellDescriptorById(aasIdentifier, assetAdministrationShellDescriptor);
	}

	@Override
	public ResponseEntity<Void> putSubmodelDescriptorByIdThroughSuperpath(String aasIdentifier, String submodelIdentifier, SubmodelDescriptor submodelDescriptor) {
		return originalDelegate.putSubmodelDescriptorByIdThroughSuperpath(aasIdentifier, submodelIdentifier, submodelDescriptor);
	}
}
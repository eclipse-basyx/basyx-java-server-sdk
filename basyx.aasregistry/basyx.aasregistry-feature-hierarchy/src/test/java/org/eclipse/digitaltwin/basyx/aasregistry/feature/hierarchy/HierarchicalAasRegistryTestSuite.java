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

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.eclipse.digitaltwin.basyx.aasregistry.client.ApiException;
import org.eclipse.digitaltwin.basyx.aasregistry.client.api.RegistryAndDiscoveryInterfaceApi;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.AssetAdministrationShellDescriptor;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.GetSubmodelDescriptorsResult;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.SubmodelDescriptor;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;

/**
 * Test for {@link HierarchicalAasRegistryFeature}
 *
 * @author mateusmolina
 *
 */
public abstract class HierarchicalAasRegistryTestSuite {

	protected abstract RegistryAndDiscoveryInterfaceApi getRootRegistryApi();
	protected abstract RegistryAndDiscoveryInterfaceApi getDelegatedRegistryApi();
	protected abstract DummyDescriptorFactory getDescriptorFactory();

	@Before
	public void setUp() throws ApiException {
		cleanUpRegistries();
		setupDelegatedRegistry();
		setupRootRegistry();
	}
	
	@Test
	public void getAasDescriptor_InRoot() throws ApiException {
		AssetAdministrationShellDescriptor actualDescriptor = getRootRegistryApi().getAssetAdministrationShellDescriptorById(DummyDescriptorFactory.ROOT_ONLY_AASDESCRIPTOR_ID);
		
		AssetAdministrationShellDescriptor expectedDescriptor = getDescriptorFactory().getAasDescriptor_RootOnly();

		assertEquals(expectedDescriptor, actualDescriptor);
	}

	@Test
	public void getAasDescriptor_ThroughDelegated() throws ApiException {
		AssetAdministrationShellDescriptor actualDescriptor = getRootRegistryApi().getAssetAdministrationShellDescriptorById(getDescriptorFactory().getDelegatedOnlyAasDescriptorId());

		AssetAdministrationShellDescriptor expectedDescriptor = getDescriptorFactory().getAasDescriptor_DelegatedOnly();

		assertEquals(expectedDescriptor, actualDescriptor);
	}

	@Test
	public void getNonExistingAasDescriptor() {
		ApiException exception = assertThrows(ApiException.class, () -> getRootRegistryApi().getAssetAdministrationShellDescriptorById("nonExistingAasDescriptor"));
		assertHttpCodeIsNotFound(exception);
	}

	@Test
	public void getSubmodelDescriptor_InRoot() throws ApiException {
		SubmodelDescriptor actualDescriptor = getRootRegistryApi().getSubmodelDescriptorByIdThroughSuperpath(DummyDescriptorFactory.ROOT_ONLY_AASDESCRIPTOR_ID, DummyDescriptorFactory.ROOT_ONLY_SMDESCRIPTOR_ID);

		SubmodelDescriptor expectedDescriptor = getDescriptorFactory().getSmDescriptor_RootOnly();

		assertEquals(expectedDescriptor, actualDescriptor);
	}

	@Test
	public void getSubmodelDescriptor_ThroughDelegated() throws ApiException {
		SubmodelDescriptor actualDescriptor = getRootRegistryApi().getSubmodelDescriptorByIdThroughSuperpath(getDescriptorFactory().getDelegatedOnlyAasDescriptorId(), DummyDescriptorFactory.DELEGATED_ONLY_SMDESCRIPTOR_ID);

		SubmodelDescriptor expectedDescriptor = getDescriptorFactory().getSmDescriptor_DelegatedOnly();

		assertEquals(expectedDescriptor, actualDescriptor);
	}

	@Test
	public void getAllSubmodelDescriptor_InRoot() throws ApiException {
		GetSubmodelDescriptorsResult actualDescriptors = getRootRegistryApi().getAllSubmodelDescriptorsThroughSuperpath(DummyDescriptorFactory.ROOT_ONLY_AASDESCRIPTOR_ID, 1, null);

		SubmodelDescriptor expectedDescriptor = getDescriptorFactory().getSmDescriptor_RootOnly();

		List<SubmodelDescriptor> expectedDescriptors = Arrays.asList(expectedDescriptor);

		assertEquals(expectedDescriptors, actualDescriptors.getResult());
	}

	@Test
	public void getAllSubmodelDescriptor_ThroughDelegated() throws ApiException {
		getDescriptorFactory();
		GetSubmodelDescriptorsResult actualDescriptors = getRootRegistryApi().getAllSubmodelDescriptorsThroughSuperpath(getDescriptorFactory().getDelegatedOnlyAasDescriptorId(), 1, null);

		SubmodelDescriptor expectedDescriptor = getDescriptorFactory().getSmDescriptor_DelegatedOnly();

		List<SubmodelDescriptor> expectedDescriptors = Arrays.asList(expectedDescriptor);

		assertEquals(expectedDescriptors, actualDescriptors.getResult());
	}

	@Test
	public void getNonExistingSmDescriptor() {
		ApiException exception = assertThrows(ApiException.class, () -> getRootRegistryApi().getSubmodelDescriptorByIdThroughSuperpath(DummyDescriptorFactory.ROOT_ONLY_AASDESCRIPTOR_ID, "nonExistingSubmodel"));
		assertHttpCodeIsNotFound(exception);

		exception = assertThrows(ApiException.class, () -> getRootRegistryApi().getSubmodelDescriptorByIdThroughSuperpath(getDescriptorFactory().getDelegatedOnlyAasDescriptorId(), "nonExistingSubmodel"));
		assertHttpCodeIsNotFound(exception);

		exception = assertThrows(ApiException.class, () -> getRootRegistryApi().getSubmodelDescriptorByIdThroughSuperpath("nonExistingAas", "nonExistingSubmodel"));
		assertHttpCodeIsNotFound(exception);
	}

	private void setupRootRegistry() throws ApiException {
		getRootRegistryApi().postAssetAdministrationShellDescriptor(getDescriptorFactory().getAasDescriptor_RootOnly());
	}

	private void setupDelegatedRegistry() throws ApiException {
		AssetAdministrationShellDescriptor descriptor = getDescriptorFactory().getAasDescriptor_DelegatedOnly();
		
		getDelegatedRegistryApi().postAssetAdministrationShellDescriptor(descriptor);
	}

	private void cleanUpRegistries() throws ApiException {
		getRootRegistryApi().deleteAllShellDescriptors();
		getDelegatedRegistryApi().deleteAllShellDescriptors();
	}

	private static void assertHttpCodeIsNotFound(ApiException e) {
		assertEquals(HttpStatus.NOT_FOUND.value(), e.getCode());
	}
}

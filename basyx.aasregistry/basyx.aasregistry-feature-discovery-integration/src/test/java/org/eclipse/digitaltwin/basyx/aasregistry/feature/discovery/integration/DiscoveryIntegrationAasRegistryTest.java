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

package org.eclipse.digitaltwin.basyx.aasregistry.feature.discovery.integration;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.digitaltwin.aas4j.v3.model.SpecificAssetId;
import org.eclipse.digitaltwin.basyx.aasdiscoveryservice.core.AasDiscoveryService;
import org.eclipse.digitaltwin.basyx.aasregistry.model.*;
import org.eclipse.digitaltwin.basyx.aasregistry.service.errors.*;
import org.eclipse.digitaltwin.basyx.aasregistry.service.storage.AasRegistryStorage;
import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Slf4j
class DiscoveryIntegrationAasRegistryTest {

	@Mock
	private AasRegistryStorage decoratedStorage;

	@Mock
	private AasDiscoveryService discoveryService;

	private DiscoveryIntegrationAasRegistry registry;

	private static final String AAS_DESCRIPTOR_ID = "testAasId";
	private static final String SUBMODEL_ID = "testSubmodelId";

	@BeforeEach
	void setUp() {
		registry = new DiscoveryIntegrationAasRegistry(discoveryService, decoratedStorage);
	}

	@Test
	void insertAasDescriptorShouldCallDiscoveryServiceWithConvertedAssetIds() throws Exception {
		log.info("Started unit test - insertAasDescriptorShouldCallDiscoveryServiceWithConvertedAssetIds()");
		AssetAdministrationShellDescriptor descriptor = createTestDescriptor(AAS_DESCRIPTOR_ID);
		descriptor.setSpecificAssetIds(createRegistrySpecificAssetIds());

		registry.insertAasDescriptor(descriptor);

		verify(decoratedStorage).insertAasDescriptor(descriptor);

		ArgumentCaptor<String> idCaptor = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<List<SpecificAssetId>> assetIdsCaptor = ArgumentCaptor.forClass(List.class);
		verify(discoveryService).createAllAssetLinksById(idCaptor.capture(), assetIdsCaptor.capture());

		assertEquals(AAS_DESCRIPTOR_ID, idCaptor.getValue());
		assertEquals(2, assetIdsCaptor.getValue().size());
		log.info("Successfully conducted unit test");
	}

	@Test
	void replaceAasDescriptorShouldDeleteThenRecreateAssetLinks() throws Exception {
		log.info("Started unit test - replaceAasDescriptorShouldDeleteThenRecreateAssetLinks()");
		AssetAdministrationShellDescriptor descriptor = createTestDescriptor(AAS_DESCRIPTOR_ID);
		descriptor.setSpecificAssetIds(createRegistrySpecificAssetIds());

		registry.replaceAasDescriptor(AAS_DESCRIPTOR_ID, descriptor);

		InOrder inOrder = inOrder(discoveryService);
		verify(decoratedStorage).replaceAasDescriptor(AAS_DESCRIPTOR_ID, descriptor);
		inOrder.verify(discoveryService).deleteAllAssetLinksById(AAS_DESCRIPTOR_ID);
		inOrder.verify(discoveryService).createAllAssetLinksById(eq(AAS_DESCRIPTOR_ID), anyList());
		inOrder.verifyNoMoreInteractions();
		log.info("Successfully conducted unit test");
	}

	@Test
	void replaceAasDescriptorWithEmptyAssetIdsShouldStillInvokeDiscovery() throws Exception {
		log.info("Started unit test - replaceAasDescriptorWithEmptyAssetIdsShouldStillInvokeDiscovery()");
		AssetAdministrationShellDescriptor descriptor = createTestDescriptor(AAS_DESCRIPTOR_ID);
		descriptor.setSpecificAssetIds(Collections.emptyList());

		registry.replaceAasDescriptor(AAS_DESCRIPTOR_ID, descriptor);

		verify(discoveryService).deleteAllAssetLinksById(AAS_DESCRIPTOR_ID);
		verify(discoveryService).createAllAssetLinksById(eq(AAS_DESCRIPTOR_ID), eq(Collections.emptyList()));
		log.info("Successfully conducted unit test");
	}

	@Test
	void removeAasDescriptorShouldDeleteAssetLinks() throws Exception {
		log.info("Started unit test - removeAasDescriptorShouldDeleteAssetLinks()");
		registry.removeAasDescriptor(AAS_DESCRIPTOR_ID);

		verify(decoratedStorage).removeAasDescriptor(AAS_DESCRIPTOR_ID);
		verify(discoveryService).deleteAllAssetLinksById(AAS_DESCRIPTOR_ID);
		log.info("Successfully conducted unit test");
	}

	@Test
	void getAasDescriptorShouldDelegateToStorageOnly() throws Exception {
		log.info("Started unit test - getAasDescriptorShouldDelegateToStorageOnly()");
		AssetAdministrationShellDescriptor expected = createTestDescriptor(AAS_DESCRIPTOR_ID);
		when(decoratedStorage.getAasDescriptor(AAS_DESCRIPTOR_ID)).thenReturn(expected);

		AssetAdministrationShellDescriptor result = registry.getAasDescriptor(AAS_DESCRIPTOR_ID);

		assertEquals(expected, result);
		verifyNoInteractions(discoveryService);
		log.info("Successfully conducted unit test");
	}

	@Test
	void getAllSubmodelsShouldDelegateToStorageOnly() throws Exception {
		log.info("Started unit test - getAllSubmodelsShouldDelegateToStorageOnly()");
		PaginationInfo pagination = new PaginationInfo(10, null);
		CursorResult<List<SubmodelDescriptor>> expected =
				new CursorResult<>("cursor", List.of(createTestSubmodelDescriptor(SUBMODEL_ID)));

		when(decoratedStorage.getAllSubmodels(AAS_DESCRIPTOR_ID, pagination)).thenReturn(expected);

		CursorResult<List<SubmodelDescriptor>> result = registry.getAllSubmodels(AAS_DESCRIPTOR_ID, pagination);

		assertEquals(expected, result);
		verifyNoInteractions(discoveryService);
		log.info("Successfully conducted unit test");
	}

	@Test
	void insertSubmodelShouldDelegateToStorageOnly() throws Exception {
		log.info("Started unit test - insertSubmodelShouldDelegateToStorageOnly()");
		SubmodelDescriptor submodel = createTestSubmodelDescriptor(SUBMODEL_ID);

		registry.insertSubmodel(AAS_DESCRIPTOR_ID, submodel);

		verify(decoratedStorage).insertSubmodel(AAS_DESCRIPTOR_ID, submodel);
		verifyNoInteractions(discoveryService);
		log.info("Successfully conducted unit test");
	}

	@Test
	void clearShouldDelegateToStorageOnly() {
		log.info("Started unit test - clearShouldDelegateToStorageOnly()");
		Set<String> expected = Set.of("id1", "id2");
		when(decoratedStorage.clear()).thenReturn(expected);

		Set<String> result = registry.clear();

		assertEquals(expected, result);
		verifyNoInteractions(discoveryService);
		log.info("Successfully conducted unit test");
	}

	@Test
	void searchAasDescriptorsShouldDelegateToStorageOnly() {
		log.info("Started unit test - searchAasDescriptorsShouldDelegateToStorageOnly()");
		ShellDescriptorSearchRequest request = new ShellDescriptorSearchRequest();
		ShellDescriptorSearchResponse expected = new ShellDescriptorSearchResponse();
		when(decoratedStorage.searchAasDescriptors(request)).thenReturn(expected);

		ShellDescriptorSearchResponse result = registry.searchAasDescriptors(request);

		assertEquals(expected, result);
		verifyNoInteractions(discoveryService);
		log.info("Successfully conducted unit test");
	}

	@Test
	void insertAasDescriptorShouldPropagateStorageException() throws Exception {
		log.info("Started unit test - insertAasDescriptorShouldPropagateStorageException()");
		AssetAdministrationShellDescriptor descriptor = createTestDescriptor(AAS_DESCRIPTOR_ID);
		doThrow(new AasDescriptorAlreadyExistsException(AAS_DESCRIPTOR_ID))
				.when(decoratedStorage).insertAasDescriptor(descriptor);

		assertThrows(AasDescriptorAlreadyExistsException.class, () -> registry.insertAasDescriptor(descriptor));
		verifyNoInteractions(discoveryService);
		log.info("Successfully conducted unit test");
	}

	private AssetAdministrationShellDescriptor createTestDescriptor(String id) {
		AssetAdministrationShellDescriptor descriptor = new AssetAdministrationShellDescriptor();
		descriptor.setId(id);
		descriptor.setIdShort("testIdShort");
		descriptor.setAdministration(new AdministrativeInformation());
		return descriptor;
	}

	private List<org.eclipse.digitaltwin.basyx.aasregistry.model.SpecificAssetId> createRegistrySpecificAssetIds() {
		org.eclipse.digitaltwin.basyx.aasregistry.model.SpecificAssetId id1 =
				new org.eclipse.digitaltwin.basyx.aasregistry.model.SpecificAssetId();
		id1.setName("assetType");
		id1.setValue("type1");

		org.eclipse.digitaltwin.basyx.aasregistry.model.SpecificAssetId id2 =
				new org.eclipse.digitaltwin.basyx.aasregistry.model.SpecificAssetId();
		id2.setName("manufacturer");
		id2.setValue("companyX");

		return List.of(id1, id2);
	}

	private SubmodelDescriptor createTestSubmodelDescriptor(String id) {
		SubmodelDescriptor submodel = new SubmodelDescriptor();
		submodel.setId(id);
		submodel.setIdShort("submodelShort");
		submodel.setAdministration(new AdministrativeInformation());
		return submodel;
	}

	@Test
	void insertAasDescriptorWithGlobalAssetIdShouldPropagateToDiscovery() throws Exception {
		log.info("Started unit test - insertAasDescriptorWithGlobalAssetIdShouldPropagateToDiscovery()");
		AssetAdministrationShellDescriptor descriptor = createTestDescriptor(AAS_DESCRIPTOR_ID);
		descriptor.setGlobalAssetId("https://example.com/global-asset-123");
		descriptor.setSpecificAssetIds(createRegistrySpecificAssetIds());

		registry.insertAasDescriptor(descriptor);

		verify(decoratedStorage).insertAasDescriptor(descriptor);

		ArgumentCaptor<String> idCaptor = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<List<SpecificAssetId>> assetIdsCaptor = ArgumentCaptor.forClass(List.class);
		verify(discoveryService).createAllAssetLinksById(idCaptor.capture(), assetIdsCaptor.capture());

		assertEquals(AAS_DESCRIPTOR_ID, idCaptor.getValue());
		assertEquals(3, assetIdsCaptor.getValue().size()); // 2 specificAssetIds + 1 globalAssetId

		// Verify globalAssetId was added
		boolean hasGlobalAssetId = assetIdsCaptor.getValue().stream()
				.anyMatch(id -> "globalAssetId".equals(id.getName()) &&
						"https://example.com/global-asset-123".equals(id.getValue()));
		assertTrue(hasGlobalAssetId, "globalAssetId should be present in the asset IDs sent to discovery service");
		log.info("Successfully conducted unit test");
	}

	@Test
	void insertAasDescriptorWithOnlyGlobalAssetIdShouldPropagateToDiscovery() throws Exception {
		log.info("Started unit test - insertAasDescriptorWithOnlyGlobalAssetIdShouldPropagateToDiscovery()");
		AssetAdministrationShellDescriptor descriptor = createTestDescriptor(AAS_DESCRIPTOR_ID);
		descriptor.setGlobalAssetId("https://example.com/global-asset-456");
		descriptor.setSpecificAssetIds(Collections.emptyList());

		registry.insertAasDescriptor(descriptor);

		verify(decoratedStorage).insertAasDescriptor(descriptor);

		ArgumentCaptor<String> idCaptor = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<List<SpecificAssetId>> assetIdsCaptor = ArgumentCaptor.forClass(List.class);
		verify(discoveryService).createAllAssetLinksById(idCaptor.capture(), assetIdsCaptor.capture());

		assertEquals(AAS_DESCRIPTOR_ID, idCaptor.getValue());
		assertEquals(1, assetIdsCaptor.getValue().size()); // Only globalAssetId

		// Verify globalAssetId was added
		SpecificAssetId capturedId = assetIdsCaptor.getValue().get(0);
		assertEquals("globalAssetId", capturedId.getName());
		assertEquals("https://example.com/global-asset-456", capturedId.getValue());
		log.info("Successfully conducted unit test");
	}

	@Test
	void replaceAasDescriptorWithGlobalAssetIdShouldPropagateToDiscovery() throws Exception {
		log.info("Started unit test - replaceAasDescriptorWithGlobalAssetIdShouldPropagateToDiscovery()");
		AssetAdministrationShellDescriptor descriptor = createTestDescriptor(AAS_DESCRIPTOR_ID);
		descriptor.setGlobalAssetId("https://example.com/global-asset-789");
		descriptor.setSpecificAssetIds(createRegistrySpecificAssetIds());

		registry.replaceAasDescriptor(AAS_DESCRIPTOR_ID, descriptor);

		InOrder inOrder = inOrder(discoveryService);
		verify(decoratedStorage).replaceAasDescriptor(AAS_DESCRIPTOR_ID, descriptor);
		inOrder.verify(discoveryService).deleteAllAssetLinksById(AAS_DESCRIPTOR_ID);

		ArgumentCaptor<List<SpecificAssetId>> assetIdsCaptor = ArgumentCaptor.forClass(List.class);
		inOrder.verify(discoveryService).createAllAssetLinksById(eq(AAS_DESCRIPTOR_ID), assetIdsCaptor.capture());

		assertEquals(3, assetIdsCaptor.getValue().size()); // 2 specificAssetIds + 1 globalAssetId

		// Verify globalAssetId was added
		boolean hasGlobalAssetId = assetIdsCaptor.getValue().stream()
				.anyMatch(id -> "globalAssetId".equals(id.getName()) &&
						"https://example.com/global-asset-789".equals(id.getValue()));
		assertTrue(hasGlobalAssetId, "globalAssetId should be present in the asset IDs sent to discovery service");

		inOrder.verifyNoMoreInteractions();
		log.info("Successfully conducted unit test");
	}

	@Test
	void insertAasDescriptorWithoutGlobalAssetIdShouldStillWork() throws Exception {
		log.info("Started unit test - insertAasDescriptorWithoutGlobalAssetIdShouldStillWork()");
		AssetAdministrationShellDescriptor descriptor = createTestDescriptor(AAS_DESCRIPTOR_ID);
		descriptor.setSpecificAssetIds(createRegistrySpecificAssetIds());
		// No globalAssetId set

		registry.insertAasDescriptor(descriptor);

		verify(decoratedStorage).insertAasDescriptor(descriptor);

		ArgumentCaptor<List<SpecificAssetId>> assetIdsCaptor = ArgumentCaptor.forClass(List.class);
		verify(discoveryService).createAllAssetLinksById(eq(AAS_DESCRIPTOR_ID), assetIdsCaptor.capture());

		assertEquals(2, assetIdsCaptor.getValue().size()); // Only 2 specificAssetIds

		// Verify no globalAssetId was added
		boolean hasGlobalAssetId = assetIdsCaptor.getValue().stream()
				.anyMatch(id -> "globalAssetId".equals(id.getName()));
		assertFalse(hasGlobalAssetId, "globalAssetId should not be present when not set in descriptor");
		log.info("Successfully conducted unit test");
	}
}
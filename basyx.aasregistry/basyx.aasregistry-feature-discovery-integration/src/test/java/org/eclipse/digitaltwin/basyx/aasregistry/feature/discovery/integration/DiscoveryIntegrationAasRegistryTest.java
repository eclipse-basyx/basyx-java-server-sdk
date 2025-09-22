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
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Base64;
import java.util.List;
import java.util.Set;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.digitaltwin.aas4j.v3.model.SpecificAssetId;
import org.eclipse.digitaltwin.basyx.aasdiscoveryservice.core.AasDiscoveryService;
import org.eclipse.digitaltwin.basyx.aasregistry.model.*;
import org.eclipse.digitaltwin.basyx.aasregistry.service.errors.AasDescriptorAlreadyExistsException;
import org.eclipse.digitaltwin.basyx.aasregistry.service.errors.AasDescriptorNotFoundException;
import org.eclipse.digitaltwin.basyx.aasregistry.service.errors.SubmodelAlreadyExistsException;
import org.eclipse.digitaltwin.basyx.aasregistry.service.errors.SubmodelNotFoundException;
import org.eclipse.digitaltwin.basyx.aasregistry.service.storage.AasRegistryStorage;
import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;

@RunWith(MockitoJUnitRunner.class)
@Slf4j
public class DiscoveryIntegrationAasRegistryTest {

	@Mock
	private AasRegistryStorage decoratedStorage;

	@Mock
	private AasDiscoveryService discoveryService;

	private DiscoveryIntegrationAasRegistry registry;

	private static final String AAS_DESCRIPTOR_ID = "testAasId";
	private static final String ENCODED_AAS_ID = Base64.getEncoder().encodeToString(AAS_DESCRIPTOR_ID.getBytes());
	private static final String SUBMODEL_ID = "testSubmodelId";

	@Before
	public void setUp() {
		registry = new DiscoveryIntegrationAasRegistry(discoveryService, decoratedStorage);
	}

	@Test
	public void testInsertAasDescriptor() throws AasDescriptorAlreadyExistsException {
		log.info("Started unit test - testInsertAasDescriptor()");
		AssetAdministrationShellDescriptor descriptor = createTestDescriptor(AAS_DESCRIPTOR_ID);
		List<org.eclipse.digitaltwin.basyx.aasregistry.model.SpecificAssetId> specificAssetIds =
				createRegistrySpecificAssetIds();
		descriptor.setSpecificAssetIds(specificAssetIds);
		registry.insertAasDescriptor(descriptor);
		verify(decoratedStorage).insertAasDescriptor(descriptor);

		ArgumentCaptor<String> idCaptor = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<List<SpecificAssetId>> assetIdsCaptor = ArgumentCaptor.forClass(List.class);
		verify(discoveryService).createAllAssetLinksById(idCaptor.capture(), assetIdsCaptor.capture());

		assertEquals(ENCODED_AAS_ID, idCaptor.getValue());
		List<SpecificAssetId> capturedAssetIds = assetIdsCaptor.getValue();
		assertEquals(2, capturedAssetIds.size());
		assertEquals("assetType", capturedAssetIds.get(0).getName());
		assertEquals("type1", capturedAssetIds.get(0).getValue());
		assertEquals("manufacturer", capturedAssetIds.get(1).getName());
		assertEquals("companyX", capturedAssetIds.get(1).getValue());

		log.info("Successfully conducted unit test");
	}

	@Test
	public void testReplaceAasDescriptor() throws AasDescriptorNotFoundException {
		log.info("Started unit test - testReplaceAasDescriptor()");
		AssetAdministrationShellDescriptor descriptor = createTestDescriptor(AAS_DESCRIPTOR_ID);
		List<org.eclipse.digitaltwin.basyx.aasregistry.model.SpecificAssetId> specificAssetIds =
				createRegistrySpecificAssetIds();
		descriptor.setSpecificAssetIds(specificAssetIds);

		registry.replaceAasDescriptor(AAS_DESCRIPTOR_ID, descriptor);

		verify(decoratedStorage).replaceAasDescriptor(AAS_DESCRIPTOR_ID, descriptor);

		verify(discoveryService).deleteAllAssetLinksById(ENCODED_AAS_ID);

		ArgumentCaptor<String> idCaptor = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<List<SpecificAssetId>> assetIdsCaptor = ArgumentCaptor.forClass(List.class);
		verify(discoveryService).createAllAssetLinksById(idCaptor.capture(), assetIdsCaptor.capture());

		assertEquals(ENCODED_AAS_ID, idCaptor.getValue());
		List<SpecificAssetId> capturedAssetIds = assetIdsCaptor.getValue();
		assertEquals(2, capturedAssetIds.size());

		log.info("Successfully conducted unit test");
	}

	@Test
	public void testReplaceAasDescriptorWithEmptyAssetIds() throws AasDescriptorNotFoundException {
		log.info("Started unit test - testReplaceAasDescriptorWithEmptyAssetIds()");
		AssetAdministrationShellDescriptor descriptor = createTestDescriptor(AAS_DESCRIPTOR_ID);
		descriptor.setSpecificAssetIds(Collections.emptyList()); // Empty list

		registry.replaceAasDescriptor(AAS_DESCRIPTOR_ID, descriptor);

		verify(decoratedStorage).replaceAasDescriptor(AAS_DESCRIPTOR_ID, descriptor);
		verify(discoveryService).deleteAllAssetLinksById(ENCODED_AAS_ID);
		verify(discoveryService).createAllAssetLinksById(eq(ENCODED_AAS_ID), eq(Collections.emptyList()));

		log.info("Successfully conducted unit test");
	}

	@Test
	public void testRemoveAasDescriptor() throws AasDescriptorNotFoundException {
		log.info("Started unit test - testRemoveAasDescriptor()");
		registry.removeAasDescriptor(AAS_DESCRIPTOR_ID);

		verify(decoratedStorage).removeAasDescriptor(AAS_DESCRIPTOR_ID);
		verify(discoveryService).deleteAllAssetLinksById(ENCODED_AAS_ID);

		log.info("Successfully conducted unit test");
	}


	@Test
	public void testGetAasDescriptor() throws AasDescriptorNotFoundException {
		log.info("Started unit test - testGetAasDescriptor()");
		AssetAdministrationShellDescriptor expectedDescriptor = createTestDescriptor(AAS_DESCRIPTOR_ID);
		when(decoratedStorage.getAasDescriptor(AAS_DESCRIPTOR_ID)).thenReturn(expectedDescriptor);

		AssetAdministrationShellDescriptor result = registry.getAasDescriptor(AAS_DESCRIPTOR_ID);

		assertEquals(expectedDescriptor, result);
		verify(decoratedStorage).getAasDescriptor(AAS_DESCRIPTOR_ID);
		verifyNoInteractions(discoveryService);
		log.info("Successfully conducted unit test");
	}

	@Test
	public void testGetAllSubmodels() throws AasDescriptorNotFoundException {
		log.info("Started unit test - testGetAllSubmodels()");
		PaginationInfo paginationInfo = new PaginationInfo(10, null);
		CursorResult<List<SubmodelDescriptor>> expectedResult =
				new CursorResult<>("cursor", Collections.singletonList(createTestSubmodelDescriptor(SUBMODEL_ID)));

		when(decoratedStorage.getAllSubmodels(AAS_DESCRIPTOR_ID, paginationInfo)).thenReturn(expectedResult);

		CursorResult<List<SubmodelDescriptor>> result =
				registry.getAllSubmodels(AAS_DESCRIPTOR_ID, paginationInfo);

		assertEquals(expectedResult, result);
		verify(decoratedStorage).getAllSubmodels(AAS_DESCRIPTOR_ID, paginationInfo);
		verifyNoInteractions(discoveryService);

		log.info("Successfully conducted unit test");
	}

	@Test
	public void testGetSubmodel() throws AasDescriptorNotFoundException, SubmodelNotFoundException {
		log.info("Started unit test - testGetSubmodel()");
		SubmodelDescriptor expectedSubmodel = createTestSubmodelDescriptor(SUBMODEL_ID);
		when(decoratedStorage.getSubmodel(AAS_DESCRIPTOR_ID, SUBMODEL_ID)).thenReturn(expectedSubmodel);

		SubmodelDescriptor result = registry.getSubmodel(AAS_DESCRIPTOR_ID, SUBMODEL_ID);

		assertEquals(expectedSubmodel, result);
		verify(decoratedStorage).getSubmodel(AAS_DESCRIPTOR_ID, SUBMODEL_ID);
		verifyNoInteractions(discoveryService);

		log.info("Successfully conducted unit test");
	}

	@Test
	public void testInsertSubmodel() throws AasDescriptorNotFoundException, SubmodelAlreadyExistsException {
		log.info("Started unit test - testInsertSubmodel()");
		SubmodelDescriptor submodel = createTestSubmodelDescriptor(SUBMODEL_ID);

		registry.insertSubmodel(AAS_DESCRIPTOR_ID, submodel);

		verify(decoratedStorage).insertSubmodel(AAS_DESCRIPTOR_ID, submodel);
		verifyNoInteractions(discoveryService);

		log.info("Successfully conducted unit test");
	}

	@Test
	public void testReplaceSubmodel() throws AasDescriptorNotFoundException, SubmodelNotFoundException {
		log.info("Started unit test - testReplaceSubmodel()");
		SubmodelDescriptor submodel = createTestSubmodelDescriptor(SUBMODEL_ID);

		registry.replaceSubmodel(AAS_DESCRIPTOR_ID, SUBMODEL_ID, submodel);

		verify(decoratedStorage).replaceSubmodel(AAS_DESCRIPTOR_ID, SUBMODEL_ID, submodel);
		verifyNoInteractions(discoveryService);

		log.info("Successfully conducted unit test");
	}

	@Test
	public void testRemoveSubmodel() throws AasDescriptorNotFoundException, SubmodelNotFoundException {
		log.info("Started unit test - testRemoveSubmodel()");
		registry.removeSubmodel(AAS_DESCRIPTOR_ID, SUBMODEL_ID);

		verify(decoratedStorage).removeSubmodel(AAS_DESCRIPTOR_ID, SUBMODEL_ID);
		verifyNoInteractions(discoveryService);

		log.info("Successfully conducted unit test");
	}

	@Test
	public void testClear() {
		log.info("Started unit test - testClear()");
		Set<String> expectedResult = Set.of("id1", "id2");
		when(decoratedStorage.clear()).thenReturn(expectedResult);

		Set<String> result = registry.clear();

		assertEquals(expectedResult, result);
		verify(decoratedStorage).clear();
		verifyNoInteractions(discoveryService);

		log.info("Successfully conducted unit test");
	}

	@Test
	public void testSearchAasDescriptors() {
		log.info("Started unit test - testSearchAasDescriptors()");
		ShellDescriptorSearchRequest request = new ShellDescriptorSearchRequest();
		ShellDescriptorSearchResponse expectedResponse = new ShellDescriptorSearchResponse();

		when(decoratedStorage.searchAasDescriptors(request)).thenReturn(expectedResponse);

		ShellDescriptorSearchResponse result = registry.searchAasDescriptors(request);

		assertEquals(expectedResponse, result);
		verify(decoratedStorage).searchAasDescriptors(request);
		verifyNoInteractions(discoveryService);

		log.info("Successfully conducted unit test");
	}

	@Test(expected = AasDescriptorAlreadyExistsException.class)
	public void testInsertAasDescriptorPropagatesStorageException() throws AasDescriptorAlreadyExistsException {
		log.info("Started unit test - testInsertAasDescriptorPropagatesStorageException()");
		AssetAdministrationShellDescriptor descriptor = createTestDescriptor(AAS_DESCRIPTOR_ID);

		doThrow(new AasDescriptorAlreadyExistsException(AAS_DESCRIPTOR_ID))
				.when(decoratedStorage).insertAasDescriptor(descriptor);

		registry.insertAasDescriptor(descriptor);

		verify(decoratedStorage).insertAasDescriptor(descriptor);
		verifyNoInteractions(discoveryService);

		log.info("Successfully conducted unit test");
	}

	@Test(expected = AasDescriptorNotFoundException.class)
	public void testReplaceAasDescriptorPropagatesStorageException() throws AasDescriptorNotFoundException {
		log.info("Started unit test - testReplaceAasDescriptorPropagatesStorageException()");
		AssetAdministrationShellDescriptor descriptor = createTestDescriptor(AAS_DESCRIPTOR_ID);

		doThrow(new AasDescriptorNotFoundException(AAS_DESCRIPTOR_ID))
				.when(decoratedStorage).replaceAasDescriptor(AAS_DESCRIPTOR_ID, descriptor);

		registry.replaceAasDescriptor(AAS_DESCRIPTOR_ID, descriptor);

		verify(decoratedStorage).replaceAasDescriptor(AAS_DESCRIPTOR_ID, descriptor);
		verifyNoInteractions(discoveryService);
		log.info("Successfully conducted unit test");
	}

	@Test(expected = AasDescriptorNotFoundException.class)
	public void testRemoveAasDescriptorPropagatesStorageException() throws AasDescriptorNotFoundException {
		log.info("Started unit test - testRemoveAasDescriptorPropagatesStorageException()");
		doThrow(new AasDescriptorNotFoundException(AAS_DESCRIPTOR_ID))
				.when(decoratedStorage).removeAasDescriptor(AAS_DESCRIPTOR_ID);

		registry.removeAasDescriptor(AAS_DESCRIPTOR_ID);

		verify(decoratedStorage).removeAasDescriptor(AAS_DESCRIPTOR_ID);
		verifyNoInteractions(discoveryService);
		log.info("Successfully conducted unit test");
	}

	@Test
	public void testReplaceAasDescriptorOrderOfOperations() throws AasDescriptorNotFoundException {
		log.info("Started unit test - testReplaceAasDescriptorOrderOfOperations()");
		AssetAdministrationShellDescriptor descriptor = createTestDescriptor(AAS_DESCRIPTOR_ID);
		descriptor.setSpecificAssetIds(createRegistrySpecificAssetIds());

		registry.replaceAasDescriptor(AAS_DESCRIPTOR_ID, descriptor);

		verify(decoratedStorage).replaceAasDescriptor(AAS_DESCRIPTOR_ID, descriptor);

		verify(discoveryService).deleteAllAssetLinksById(ENCODED_AAS_ID);
		verify(discoveryService).createAllAssetLinksById(eq(ENCODED_AAS_ID), anyList());

		verifyNoMoreInteractions(discoveryService);
		log.info("Successfully conducted unit test");
	}

	private AssetAdministrationShellDescriptor createTestDescriptor(String id) {
		AssetAdministrationShellDescriptor descriptor = new AssetAdministrationShellDescriptor();
		descriptor.setId(id);
		descriptor.setIdShort("testIdShort");
		descriptor.setDescription(Collections.emptyList());
		descriptor.setAdministration(new AdministrativeInformation());
		return descriptor;
	}

	private List<org.eclipse.digitaltwin.basyx.aasregistry.model.SpecificAssetId> createRegistrySpecificAssetIds() {
		org.eclipse.digitaltwin.basyx.aasregistry.model.SpecificAssetId assetId1 =
				new org.eclipse.digitaltwin.basyx.aasregistry.model.SpecificAssetId();
		assetId1.setName("assetType");
		assetId1.setValue("type1");

		org.eclipse.digitaltwin.basyx.aasregistry.model.SpecificAssetId assetId2 =
				new org.eclipse.digitaltwin.basyx.aasregistry.model.SpecificAssetId();
		assetId2.setName("manufacturer");
		assetId2.setValue("companyX");

		return Arrays.asList(assetId1, assetId2);
	}

	private SubmodelDescriptor createTestSubmodelDescriptor(String id) {
		SubmodelDescriptor submodel = new SubmodelDescriptor();
		submodel.setId(id);
		submodel.setIdShort("testSubmodelIdShort");
		submodel.setDescription(Collections.emptyList());
		submodel.setAdministration(new AdministrativeInformation());
		return submodel;
	}
}
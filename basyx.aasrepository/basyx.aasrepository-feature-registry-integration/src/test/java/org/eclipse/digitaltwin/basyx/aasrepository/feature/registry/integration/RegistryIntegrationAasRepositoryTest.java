/*******************************************************************************
 * Copyright (C) 2026 the Eclipse BaSyx Authors
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

package org.eclipse.digitaltwin.basyx.aasrepository.feature.registry.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetInformation;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetKind;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultAssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultAssetInformation;
import org.eclipse.digitaltwin.basyx.aasregistry.client.ApiException;
import org.eclipse.digitaltwin.basyx.aasregistry.client.api.RegistryAndDiscoveryInterfaceApi;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.AssetAdministrationShellDescriptor;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.SubmodelDescriptor;
import org.eclipse.digitaltwin.basyx.aasregistry.main.client.mapper.AttributeMapper;
import org.eclipse.digitaltwin.basyx.aasrepository.AasRepository;
import org.eclipse.digitaltwin.basyx.aasrepository.backend.CrudAasRepositoryFactory;
import org.eclipse.digitaltwin.basyx.aasservice.backend.InMemoryAasBackend;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.exceptions.RepositoryRegistryLinkException;
import org.eclipse.digitaltwin.basyx.core.filerepository.InMemoryFileRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Unit tests for registry synchronization in {@link RegistryIntegrationAasRepository}.
 */
public class RegistryIntegrationAasRepositoryTest {
	private static final String AAS_ID = "aas-id";
	private static final String REPOSITORY_URL = "http://localhost:8081";

	private AasRepository decorated;
	private RegistryAndDiscoveryInterfaceApi registryApi;
	private RegistryIntegrationAasRepository repository;

	@Before
	public void setUp() {
		decorated = CrudAasRepositoryFactory.builder().backend(new InMemoryAasBackend()).fileRepository(new InMemoryFileRepository()).create();
		registryApi = mock(RegistryAndDiscoveryInterfaceApi.class);

		AasRepositoryRegistryLink registryLink = mock(AasRepositoryRegistryLink.class);
		when(registryLink.getRegistryApi()).thenReturn(registryApi);
		when(registryLink.getAasRepositoryBaseURLs()).thenReturn(List.of(REPOSITORY_URL));

		repository = new RegistryIntegrationAasRepository(decorated, registryLink, new AttributeMapper(new ObjectMapper()));
	}

	@Test
	public void updateAasAddsIdShortToDescriptor() throws ApiException {
		AssetAdministrationShell initialShell = createShell(null, AssetKind.INSTANCE);
		decorated.createAas(initialShell);
		when(registryApi.getAssetAdministrationShellDescriptorById(AAS_ID)).thenReturn(createExistingDescriptor());

		repository.updateAas(AAS_ID, createShell("added-id-short", AssetKind.INSTANCE));

		AssetAdministrationShellDescriptor updatedDescriptor = captureUpdatedDescriptor();
		assertEquals("added-id-short", updatedDescriptor.getIdShort());
	}

	@Test
	public void updateAasUpdatesAssetKindInDescriptor() throws ApiException {
		decorated.createAas(createShell("id-short", AssetKind.INSTANCE));
		when(registryApi.getAssetAdministrationShellDescriptorById(AAS_ID)).thenReturn(createExistingDescriptor());

		repository.updateAas(AAS_ID, createShell("id-short", AssetKind.TYPE));

		AssetAdministrationShellDescriptor updatedDescriptor = captureUpdatedDescriptor();
		assertEquals(org.eclipse.digitaltwin.basyx.aasregistry.client.model.AssetKind.TYPE, updatedDescriptor.getAssetKind());
	}

	@Test
	public void setAssetInformationUpdatesAssetKindInDescriptor() throws ApiException {
		decorated.createAas(createShell("id-short", AssetKind.INSTANCE));
		when(registryApi.getAssetAdministrationShellDescriptorById(AAS_ID)).thenReturn(createExistingDescriptor());

		repository.setAssetInformation(AAS_ID, createAssetInformation(AssetKind.TYPE));

		AssetAdministrationShellDescriptor updatedDescriptor = captureUpdatedDescriptor();
		assertEquals(org.eclipse.digitaltwin.basyx.aasregistry.client.model.AssetKind.TYPE, updatedDescriptor.getAssetKind());
	}

	@Test
	public void updateAasPreservesSubmodelDescriptors() throws ApiException {
		decorated.createAas(createShell("old-id-short", AssetKind.INSTANCE));
		AssetAdministrationShellDescriptor existingDescriptor = createExistingDescriptor();
		SubmodelDescriptor submodelDescriptor = new SubmodelDescriptor();
		submodelDescriptor.setId("submodel-id");
		existingDescriptor.setSubmodelDescriptors(List.of(submodelDescriptor));
		when(registryApi.getAssetAdministrationShellDescriptorById(AAS_ID)).thenReturn(existingDescriptor);

		repository.updateAas(AAS_ID, createShell("new-id-short", AssetKind.INSTANCE));

		assertEquals(List.of(submodelDescriptor), captureUpdatedDescriptor().getSubmodelDescriptors());
	}

	@Test
	public void failedDescriptorUpdateRestoresPreviousAas() throws ApiException {
		AssetAdministrationShell initialShell = createShell("old-id-short", AssetKind.INSTANCE);
		decorated.createAas(initialShell);
		when(registryApi.getAssetAdministrationShellDescriptorById(AAS_ID)).thenReturn(createExistingDescriptor());
		doThrow(new ApiException(503, "registry unavailable")).when(registryApi).putAssetAdministrationShellDescriptorById(eq(AAS_ID), any());

		assertThrows(RepositoryRegistryLinkException.class, () -> repository.updateAas(AAS_ID, createShell("new-id-short", AssetKind.TYPE)));

		AssetAdministrationShell restoredShell = decorated.getAas(AAS_ID);
		assertEquals("old-id-short", restoredShell.getIdShort());
		assertEquals(AssetKind.INSTANCE, restoredShell.getAssetInformation().getAssetKind());
	}

	@Test
	public void failedAssetInformationUpdateRestoresPreviousAssetInformation() throws ApiException {
		decorated.createAas(createShell("id-short", AssetKind.INSTANCE));
		when(registryApi.getAssetAdministrationShellDescriptorById(AAS_ID)).thenReturn(createExistingDescriptor());
		doThrow(new ApiException(503, "registry unavailable")).when(registryApi).putAssetAdministrationShellDescriptorById(eq(AAS_ID), any());

		assertThrows(RepositoryRegistryLinkException.class, () -> repository.setAssetInformation(AAS_ID, createAssetInformation(AssetKind.TYPE)));

		assertEquals(AssetKind.INSTANCE, decorated.getAssetInformation(AAS_ID).getAssetKind());
	}

	@Test
	public void createAasReportsRegistryConflictDetailsAndRollsBackRepository() throws ApiException {
		assertCreateFailureContainsRegistryDetails(409, "descriptor already exists");
	}

	@Test
	public void createAasReportsRegistryServiceFailureDetailsAndRollsBackRepository() throws ApiException {
		assertCreateFailureContainsRegistryDetails(503, "registry unavailable");
	}

	private void assertCreateFailureContainsRegistryDetails(int status, String detail) throws ApiException {
		doThrow(new ApiException(status, "registry request failed", null, detail)).when(registryApi).postAssetAdministrationShellDescriptor(any());

		RepositoryRegistryLinkException exception = assertThrows(RepositoryRegistryLinkException.class, () -> repository.createAas(createShell("id-short", AssetKind.INSTANCE)));

		assertTrue(exception.getMessage().contains(Integer.toString(status)));
		assertTrue(exception.getMessage().contains(detail));
		assertThrows(ElementDoesNotExistException.class, () -> decorated.getAas(AAS_ID));
	}

	private AssetAdministrationShellDescriptor captureUpdatedDescriptor() throws ApiException {
		ArgumentCaptor<AssetAdministrationShellDescriptor> descriptorCaptor = ArgumentCaptor.forClass(AssetAdministrationShellDescriptor.class);
		verify(registryApi).putAssetAdministrationShellDescriptorById(eq(AAS_ID), descriptorCaptor.capture());
		return descriptorCaptor.getValue();
	}

	private AssetAdministrationShellDescriptor createExistingDescriptor() {
		AssetAdministrationShellDescriptor descriptor = new AssetAdministrationShellDescriptor();
		descriptor.setId(AAS_ID);
		return descriptor;
	}

	private AssetAdministrationShell createShell(String idShort, AssetKind assetKind) {
		return new DefaultAssetAdministrationShell.Builder()
				.id(AAS_ID)
				.idShort(idShort)
				.assetInformation(createAssetInformation(assetKind))
				.build();
	}

	private AssetInformation createAssetInformation(AssetKind assetKind) {
		return new DefaultAssetInformation.Builder()
				.assetKind(assetKind)
				.globalAssetId("global-asset-id")
				.build();
	}
}

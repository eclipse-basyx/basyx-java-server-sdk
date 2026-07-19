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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetInformation;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetKind;
import org.eclipse.digitaltwin.aas4j.v3.model.KeyTypes;
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.aas4j.v3.model.ReferenceTypes;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultAssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultAssetInformation;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultKey;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultReference;
import org.eclipse.digitaltwin.basyx.aasregistry.client.ApiException;
import org.eclipse.digitaltwin.basyx.aasregistry.client.api.RegistryAndDiscoveryInterfaceApi;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.AssetAdministrationShellDescriptor;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.Endpoint;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.ProtocolInformation;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.SubmodelDescriptor;
import org.eclipse.digitaltwin.basyx.aasregistry.main.client.mapper.AttributeMapper;
import org.eclipse.digitaltwin.basyx.aasrepository.AasRepository;
import org.eclipse.digitaltwin.basyx.aasrepository.backend.CrudAasRepositoryFactory;
import org.eclipse.digitaltwin.basyx.aasservice.backend.InMemoryAasBackend;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.exceptions.RepositoryRegistryLinkException;
import org.eclipse.digitaltwin.basyx.core.exceptions.RepositoryRegistryUnlinkException;
import org.eclipse.digitaltwin.basyx.core.filerepository.InMemoryFileRepository;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
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
	public void updateAasPreservesRegistryEndpointMetadata() throws ApiException {
		decorated.createAas(createShell("old-id-short", AssetKind.INSTANCE));
		AssetAdministrationShellDescriptor existingDescriptor = createExistingDescriptor();
		ProtocolInformation protocolInformation = new ProtocolInformation()
				.href("https://registry.example/shell")
				.endpointProtocol("https")
				.endpointProtocolVersion(List.of("1.1"))
				.subprotocol("custom-subprotocol");
		Endpoint endpoint = new Endpoint()._interface("AAS-3.0").protocolInformation(protocolInformation);
		existingDescriptor.setEndpoints(List.of(endpoint));
		when(registryApi.getAssetAdministrationShellDescriptorById(AAS_ID)).thenReturn(existingDescriptor);

		repository.updateAas(AAS_ID, createShell("new-id-short", AssetKind.INSTANCE));

		assertEquals(List.of(endpoint), captureUpdatedDescriptor().getEndpoints());
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
	public void committedDescriptorUpdateWithLostResponseDoesNotRollBackAas() throws ApiException {
		decorated.createAas(createShell("old-id-short", AssetKind.INSTANCE));
		AtomicReference<AssetAdministrationShellDescriptor> registryDescriptor = new AtomicReference<>(createExistingDescriptor());
		when(registryApi.getAssetAdministrationShellDescriptorById(AAS_ID)).thenAnswer(invocation -> registryDescriptor.get());
		doAnswer(invocation -> {
			registryDescriptor.set(invocation.getArgument(1));
			throw new ApiException(503, "response lost");
		}).when(registryApi).putAssetAdministrationShellDescriptorById(eq(AAS_ID), any());

		repository.updateAas(AAS_ID, createShell("new-id-short", AssetKind.TYPE));

		assertEquals("new-id-short", decorated.getAas(AAS_ID).getIdShort());
		assertEquals("new-id-short", registryDescriptor.get().getIdShort());
	}

	@Test
	public void concurrentUpdatesThroughSameIntegrationInstanceRemainOrdered() throws Exception {
		decorated.createAas(createShell("initial", AssetKind.INSTANCE));
		AtomicReference<AssetAdministrationShellDescriptor> registryDescriptor = new AtomicReference<>(createExistingDescriptor());
		AtomicInteger putCount = new AtomicInteger();
		CountDownLatch firstPutStarted = new CountDownLatch(1);
		CountDownLatch releaseFirstPut = new CountDownLatch(1);
		CountDownLatch secondUpdateInvoked = new CountDownLatch(1);
		CountDownLatch secondPutStarted = new CountDownLatch(1);
		when(registryApi.getAssetAdministrationShellDescriptorById(AAS_ID)).thenAnswer(invocation -> registryDescriptor.get());
		doAnswer(invocation -> {
			AssetAdministrationShellDescriptor descriptor = invocation.getArgument(1);
			if (putCount.incrementAndGet() == 1) {
				firstPutStarted.countDown();
				releaseFirstPut.await(2, TimeUnit.SECONDS);
			} else {
				secondPutStarted.countDown();
			}
			registryDescriptor.set(descriptor);
			return null;
		}).when(registryApi).putAssetAdministrationShellDescriptorById(eq(AAS_ID), any());

		ExecutorService executor = Executors.newFixedThreadPool(2);
		try {
			Future<?> firstUpdate = executor.submit(() -> repository.updateAas(AAS_ID, createShell("first", AssetKind.INSTANCE)));
			assertTrue(firstPutStarted.await(1, TimeUnit.SECONDS));
			Future<?> secondUpdate = executor.submit(() -> {
				secondUpdateInvoked.countDown();
				repository.updateAas(AAS_ID, createShell("second", AssetKind.TYPE));
			});
			assertTrue(secondUpdateInvoked.await(1, TimeUnit.SECONDS));
			assertFalse(secondPutStarted.await(200, TimeUnit.MILLISECONDS));
			releaseFirstPut.countDown();

			firstUpdate.get(2, TimeUnit.SECONDS);
			secondUpdate.get(2, TimeUnit.SECONDS);

			assertEquals("second", decorated.getAas(AAS_ID).getIdShort());
			assertEquals("second", registryDescriptor.get().getIdShort());
		} finally {
			releaseFirstPut.countDown();
			executor.shutdownNow();
		}
	}

	@Test
	public void failedUpdateRollbackDoesNotOverwriteLaterUpdateThroughSameIntegrationInstance() throws Exception {
		decorated.createAas(createShell("initial", AssetKind.INSTANCE));
		AtomicReference<AssetAdministrationShellDescriptor> registryDescriptor = new AtomicReference<>(createExistingDescriptor());
		AtomicInteger putCount = new AtomicInteger();
		CountDownLatch firstPutStarted = new CountDownLatch(1);
		CountDownLatch releaseFirstPut = new CountDownLatch(1);
		CountDownLatch secondUpdateInvoked = new CountDownLatch(1);
		CountDownLatch secondPutStarted = new CountDownLatch(1);
		when(registryApi.getAssetAdministrationShellDescriptorById(AAS_ID)).thenAnswer(invocation -> registryDescriptor.get());
		doAnswer(invocation -> {
			AssetAdministrationShellDescriptor descriptor = invocation.getArgument(1);
			if (putCount.incrementAndGet() == 1) {
				firstPutStarted.countDown();
				releaseFirstPut.await(2, TimeUnit.SECONDS);
				throw new ApiException(503, "registry unavailable");
			}

			secondPutStarted.countDown();
			registryDescriptor.set(descriptor);
			return null;
		}).when(registryApi).putAssetAdministrationShellDescriptorById(eq(AAS_ID), any());

		ExecutorService executor = Executors.newFixedThreadPool(2);
		try {
			Future<?> failedUpdate = executor.submit(() -> repository.updateAas(AAS_ID, createShell("failed", AssetKind.INSTANCE)));
			assertTrue(firstPutStarted.await(1, TimeUnit.SECONDS));
			Future<?> successfulUpdate = executor.submit(() -> {
				secondUpdateInvoked.countDown();
				repository.updateAas(AAS_ID, createShell("successful", AssetKind.TYPE));
			});
			assertTrue(secondUpdateInvoked.await(1, TimeUnit.SECONDS));
			assertFalse(secondPutStarted.await(200, TimeUnit.MILLISECONDS));
			releaseFirstPut.countDown();

			ExecutionException exception = assertThrows(ExecutionException.class, () -> failedUpdate.get(2, TimeUnit.SECONDS));
			assertTrue(exception.getCause() instanceof RepositoryRegistryLinkException);
			successfulUpdate.get(2, TimeUnit.SECONDS);

			assertEquals("successful", decorated.getAas(AAS_ID).getIdShort());
			assertEquals("successful", registryDescriptor.get().getIdShort());
		} finally {
			releaseFirstPut.countDown();
			executor.shutdownNow();
		}
	}

	@Test
	public void failedUpdateRollbackDoesNotOverwriteConcurrentSubmodelReferenceChange() throws Exception {
		decorated.createAas(createShell("initial", AssetKind.INSTANCE));
		CountDownLatch putStarted = new CountDownLatch(1);
		CountDownLatch releasePut = new CountDownLatch(1);
		CountDownLatch addReferenceInvoked = new CountDownLatch(1);
		CountDownLatch submodelReferenceAdded = new CountDownLatch(1);
		when(registryApi.getAssetAdministrationShellDescriptorById(AAS_ID)).thenReturn(createExistingDescriptor());
		doAnswer(invocation -> {
			putStarted.countDown();
			releasePut.await(2, TimeUnit.SECONDS);
			throw new ApiException(503, "registry unavailable");
		}).when(registryApi).putAssetAdministrationShellDescriptorById(eq(AAS_ID), any());

		ExecutorService executor = Executors.newFixedThreadPool(2);
		try {
			Future<?> failedUpdate = executor.submit(() -> repository.updateAas(AAS_ID, createShell("failed", AssetKind.TYPE)));
			assertTrue(putStarted.await(1, TimeUnit.SECONDS));
			Future<?> addReference = executor.submit(() -> {
				addReferenceInvoked.countDown();
				repository.addSubmodelReference(AAS_ID, createSubmodelReference("submodel-id"));
				submodelReferenceAdded.countDown();
			});
			assertTrue(addReferenceInvoked.await(1, TimeUnit.SECONDS));
			assertFalse(submodelReferenceAdded.await(200, TimeUnit.MILLISECONDS));
			releasePut.countDown();

			ExecutionException exception = assertThrows(ExecutionException.class, () -> failedUpdate.get(2, TimeUnit.SECONDS));
			assertTrue(exception.getCause() instanceof RepositoryRegistryLinkException);
			addReference.get(2, TimeUnit.SECONDS);

			assertEquals(List.of(createSubmodelReference("submodel-id")), decorated.getSubmodelReferences(AAS_ID, PaginationInfo.NO_LIMIT).getResult());
		} finally {
			releasePut.countDown();
			executor.shutdownNow();
		}
	}

	@Test
	public void concurrentCreateAndDeleteThroughSameIntegrationInstanceRemainOrdered() throws Exception {
		AtomicBoolean descriptorExists = new AtomicBoolean();
		CountDownLatch registrationStarted = new CountDownLatch(1);
		CountDownLatch releaseRegistration = new CountDownLatch(1);
		CountDownLatch deleteInvoked = new CountDownLatch(1);
		CountDownLatch deletionAttempted = new CountDownLatch(1);
		doAnswer(invocation -> {
			registrationStarted.countDown();
			releaseRegistration.await(2, TimeUnit.SECONDS);
			descriptorExists.set(true);
			return null;
		}).when(registryApi).postAssetAdministrationShellDescriptor(any());
		doAnswer(invocation -> {
			deletionAttempted.countDown();
			if (!descriptorExists.getAndSet(false))
				throw new ApiException(404, "descriptor not found");
			return null;
		}).when(registryApi).deleteAssetAdministrationShellDescriptorById(AAS_ID);

		ExecutorService executor = Executors.newFixedThreadPool(2);
		try {
			Future<?> create = executor.submit(() -> repository.createAas(createShell("id-short", AssetKind.INSTANCE)));
			assertTrue(registrationStarted.await(1, TimeUnit.SECONDS));
			Future<?> delete = executor.submit(() -> {
				deleteInvoked.countDown();
				repository.deleteAas(AAS_ID);
			});
			assertTrue(deleteInvoked.await(1, TimeUnit.SECONDS));
			assertFalse(deletionAttempted.await(200, TimeUnit.MILLISECONDS));
			releaseRegistration.countDown();

			create.get(2, TimeUnit.SECONDS);
			delete.get(2, TimeUnit.SECONDS);

			assertThrows(ElementDoesNotExistException.class, () -> decorated.getAas(AAS_ID));
			assertFalse(descriptorExists.get());
		} finally {
			releaseRegistration.countDown();
			executor.shutdownNow();
		}
	}

	@Test
	public void missingRegistryDescriptorStillDeletesAasWithoutReadingIt() throws ApiException {
		decorated.createAas(createShell("id-short", AssetKind.INSTANCE));
		doThrow(new ApiException(404, "descriptor not found")).when(registryApi).deleteAssetAdministrationShellDescriptorById(AAS_ID);

		repository.deleteAas(AAS_ID);

		assertThrows(ElementDoesNotExistException.class, () -> decorated.getAas(AAS_ID));
		verify(registryApi).deleteAssetAdministrationShellDescriptorById(AAS_ID);
		verify(registryApi, never()).getAssetAdministrationShellDescriptorById(AAS_ID);
	}

	@Test
	public void registryDeleteFailureKeepsAasAndReportsDetails() throws ApiException {
		decorated.createAas(createShell("id-short", AssetKind.INSTANCE));
		doThrow(new ApiException(503, "registry request failed", null, "registry unavailable")).when(registryApi).deleteAssetAdministrationShellDescriptorById(AAS_ID);

		RepositoryRegistryUnlinkException exception = assertThrows(RepositoryRegistryUnlinkException.class, () -> repository.deleteAas(AAS_ID));

		assertTrue(exception.getMessage().contains("503"));
		assertTrue(exception.getMessage().contains("registry unavailable"));
		assertEquals("id-short", decorated.getAas(AAS_ID).getIdShort());
		verify(registryApi, never()).getAssetAdministrationShellDescriptorById(AAS_ID);
	}

	@Test
	public void registryReadPermissionFailureRollsBackAasAndReportsDetails() throws ApiException {
		decorated.createAas(createShell("old-id-short", AssetKind.INSTANCE));
		when(registryApi.getAssetAdministrationShellDescriptorById(AAS_ID)).thenThrow(new ApiException(403, "forbidden", null, "read permission required"));

		RepositoryRegistryLinkException exception = assertThrows(RepositoryRegistryLinkException.class,
				() -> repository.updateAas(AAS_ID, createShell("new-id-short", AssetKind.TYPE)));

		assertTrue(exception.getMessage().contains("403"));
		assertTrue(exception.getMessage().contains("read permission required"));
		assertEquals("old-id-short", decorated.getAas(AAS_ID).getIdShort());
		verify(registryApi, never()).putAssetAdministrationShellDescriptorById(eq(AAS_ID), any());
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

	private Reference createSubmodelReference(String submodelId) {
		return new DefaultReference.Builder()
				.type(ReferenceTypes.MODEL_REFERENCE)
				.keys(new DefaultKey.Builder().type(KeyTypes.SUBMODEL).value(submodelId).build())
				.build();
	}
}

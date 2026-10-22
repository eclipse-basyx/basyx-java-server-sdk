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
package org.eclipse.digitaltwin.basyx.submodelrepository.feature.registry.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.ConnectException;
import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSubmodel;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.exceptions.RepositoryRegistryUnlinkException;
import org.eclipse.digitaltwin.basyx.core.filerepository.InMemoryFileRepository;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.ApiException;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.api.SubmodelRegistryApi;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.mapper.AttributeMapper;
import org.eclipse.digitaltwin.basyx.submodelrepository.SubmodelRepository;
import org.eclipse.digitaltwin.basyx.submodelrepository.backend.CrudSubmodelRepositoryFactory;
import org.eclipse.digitaltwin.basyx.submodelservice.InMemorySubmodelBackend;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Unit tests for registry synchronization in
 * {@link RegistryIntegrationSubmodelRepository}.
 */
public class RegistryIntegrationSubmodelRepositoryTest {
	private static final String SUBMODEL_ID = "submodel-id";
	private static final String REPOSITORY_URL = "http://localhost:8081";

	private SubmodelRepository decorated;
	private SubmodelRegistryApi registryApi;
	private RegistryIntegrationSubmodelRepository repository;

	@Before
	public void setUp() {
		decorated = CrudSubmodelRepositoryFactory.builder().backend(new InMemorySubmodelBackend()).fileRepository(new InMemoryFileRepository()).create();
		registryApi = mock(SubmodelRegistryApi.class);

		SubmodelRepositoryRegistryLink registryLink = mock(SubmodelRepositoryRegistryLink.class);
		when(registryLink.getRegistryApi()).thenReturn(registryApi);
		when(registryLink.getSubmodelRepositoryBaseURLs()).thenReturn(List.of(REPOSITORY_URL));

		repository = new RegistryIntegrationSubmodelRepository(decorated, registryLink, new AttributeMapper(new ObjectMapper()));
	}

	@Test
	public void deleteSubmodelRemovesDescriptorWithoutReadingIt() throws ApiException {
		decorated.createSubmodel(createSubmodel());

		repository.deleteSubmodel(SUBMODEL_ID);

		assertThrows(ElementDoesNotExistException.class, () -> decorated.getSubmodel(SUBMODEL_ID));
		verify(registryApi).deleteSubmodelDescriptorById(SUBMODEL_ID);
		verify(registryApi, never()).getSubmodelDescriptorById(SUBMODEL_ID);
	}

	@Test
	public void missingRegistryDescriptorStillDeletesSubmodel() throws ApiException {
		decorated.createSubmodel(createSubmodel());
		doThrow(new ApiException(404, "descriptor not found")).when(registryApi).deleteSubmodelDescriptorById(SUBMODEL_ID);

		repository.deleteSubmodel(SUBMODEL_ID);

		assertThrows(ElementDoesNotExistException.class, () -> decorated.getSubmodel(SUBMODEL_ID));
	}

	@Test
	public void registryDeleteFailureKeepsSubmodelAndReportsDetails() throws ApiException {
		decorated.createSubmodel(createSubmodel());
		doThrow(new ApiException(503, "registry request failed", null, "registry unavailable")).when(registryApi).deleteSubmodelDescriptorById(SUBMODEL_ID);

		RepositoryRegistryUnlinkException exception = assertThrows(RepositoryRegistryUnlinkException.class, () -> repository.deleteSubmodel(SUBMODEL_ID));

		assertTrue(exception.getMessage().contains("503"));
		assertTrue(exception.getMessage().contains("registry unavailable"));
		assertEquals(SUBMODEL_ID, decorated.getSubmodel(SUBMODEL_ID).getId());
	}

	@Test
	public void unreachableRegistryKeepsSubmodel() throws ApiException {
		decorated.createSubmodel(createSubmodel());
		IOException connectionFailure = new ConnectException("Connection refused");
		doThrow(new ApiException(connectionFailure)).when(registryApi).deleteSubmodelDescriptorById(SUBMODEL_ID);

		RepositoryRegistryUnlinkException exception = assertThrows(RepositoryRegistryUnlinkException.class, () -> repository.deleteSubmodel(SUBMODEL_ID));

		assertTrue(exception.getMessage().contains("Connection refused"));
		assertEquals(SUBMODEL_ID, decorated.getSubmodel(SUBMODEL_ID).getId());
	}

	private static Submodel createSubmodel() {
		return new DefaultSubmodel.Builder().id(SUBMODEL_ID).idShort("id-short").build();
	}
}

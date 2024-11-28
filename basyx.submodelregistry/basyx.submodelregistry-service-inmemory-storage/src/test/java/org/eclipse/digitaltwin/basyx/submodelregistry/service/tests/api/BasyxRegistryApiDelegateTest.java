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
package org.eclipse.digitaltwin.basyx.submodelregistry.service.tests.api;
 
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

import org.eclipse.digitaltwin.basyx.submodelregistry.model.GetSubmodelDescriptorsResult;
import org.eclipse.digitaltwin.basyx.submodelregistry.model.SubmodelDescriptor;
import org.eclipse.digitaltwin.basyx.submodelregistry.service.api.BasyxSubmodelRegistryApiDelegate;
import org.eclipse.digitaltwin.basyx.submodelregistry.service.api.LocationBuilder;
import org.eclipse.digitaltwin.basyx.submodelregistry.service.api.SubmodelDescriptorsApiController;
import org.eclipse.digitaltwin.basyx.submodelregistry.service.configuration.InMemorySubmodelStorageConfiguration;
import org.eclipse.digitaltwin.basyx.submodelregistry.service.errors.SubmodelNotFoundException;
import org.eclipse.digitaltwin.basyx.submodelregistry.service.events.RegistryEventSink;
import org.eclipse.digitaltwin.basyx.submodelregistry.service.storage.SubmodelRegistryStorage;
import org.eclipse.digitaltwin.basyx.submodelregistry.service.tests.TestResourcesLoader;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = { BasyxSubmodelRegistryApiDelegate.class, SubmodelDescriptorsApiController.class, SubmodelDescriptorsApiController.class, InMemorySubmodelStorageConfiguration.class })
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
@TestPropertySource(properties = { "registry.type=inMemory" })
public class BasyxRegistryApiDelegateTest {

	private static final String ID_3 = "identification_3";

	private static final String ID_2_1 = "identification_2.1";

	private static final String ID_1 = "identification_1";

	private static final String ID_UNKNOWN = "unknown";

	private static final String ID_2 = "identification_2";

	private static final String ID_2_3 = "identification_2.3";

	@MockBean
	private RegistryEventSink listener;
	
	@MockBean
	private LocationBuilder locationBuilder;	

	@Autowired
	private SubmodelRegistryStorage storage;

	@Autowired
	private SubmodelDescriptorsApiController submodelController;


	@Rule
	public TestResourcesLoader testResourcesLoader = new TestResourcesLoader();

	@Before
	public void initStorage() throws IOException {
		testResourcesLoader.loadRepositoryDefinition(SubmodelDescriptor.class).forEach(storage::insertSubmodelDescriptor);
	}

	@After
	public void clearStorage() {
		storage.clear();
	}

	@Test
	public void whenDeleteSubmodelDescriptorById_thenNoContent() {
		ResponseEntity<Void> response = submodelController.deleteSubmodelDescriptorById(encode(ID_1));
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
	}

	@Test
	public void whenGetSubmodelDescriptors_thenRepoContent() throws IOException {
		List<SubmodelDescriptor> repoContent = testResourcesLoader.loadRepositoryDefinition(SubmodelDescriptor.class);
		ResponseEntity<GetSubmodelDescriptorsResult> response = submodelController.getAllSubmodelDescriptors(null, null);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody().getResult()).asList().containsExactlyInAnyOrderElementsOf(repoContent);
	}
	

	@Test
	public void whenGetSubmodelDescriptorByIdNullArg_thenNullPointer() {
		assertThrows(NullPointerException.class, () -> submodelController.getSubmodelDescriptorById(null));
	}

	@Test
	public void whenSubmodelDescriptorByIdUnknown_thenNotFound() {
		assertThrows(SubmodelNotFoundException.class, () -> submodelController.getSubmodelDescriptorById(encode(ID_UNKNOWN)));
	}

	@Test
	public void whenGetSubmodelDescriptorById_thenOk() throws IOException {
		SubmodelDescriptor expected = testResourcesLoader.load(SubmodelDescriptor.class);
		ResponseEntity<SubmodelDescriptor> response = submodelController.getSubmodelDescriptorById(encode(ID_2));
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isEqualTo(expected);
	}

	@Test
	public void whenPostSubmodelDescriptorById_thenCreated() throws IOException {
		SubmodelDescriptor descriptor = testResourcesLoader.load(SubmodelDescriptor.class);
		ResponseEntity<?> response = submodelController.postSubmodelDescriptor(descriptor);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

		ResponseEntity<SubmodelDescriptor> stored = submodelController.getSubmodelDescriptorById(encode(ID_3));
		assertThat(descriptor).isEqualTo(stored.getBody());
	}

	@Test
	public void whenPostSubmodelDescriptor_thenApplied() throws IOException {
		SubmodelDescriptor input = new SubmodelDescriptor(ID_3, List.of());
		ResponseEntity<SubmodelDescriptor> response = submodelController.postSubmodelDescriptor(input);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(response.getBody()).isEqualTo(input);

		ResponseEntity<GetSubmodelDescriptorsResult> all = submodelController.getAllSubmodelDescriptors(null, null);
		List<SubmodelDescriptor> expected = testResourcesLoader.loadList(SubmodelDescriptor.class);
		assertThat(all.getBody().getResult()).asList().containsExactlyInAnyOrderElementsOf(expected);
	}

	@Test
	public void whenPutSubmodelDescriptor_thenOverridden() throws IOException {
		SubmodelDescriptor input = new SubmodelDescriptor(ID_2, List.of());
		ResponseEntity<Void> response = submodelController.putSubmodelDescriptorById(encode(ID_2), input);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

		ResponseEntity<GetSubmodelDescriptorsResult> all = submodelController.getAllSubmodelDescriptors(null, null);
		List<SubmodelDescriptor> expected = testResourcesLoader.loadList(SubmodelDescriptor.class);
		assertThat(all.getBody().getResult()).asList().containsExactlyInAnyOrderElementsOf(expected);
	}


	@Test
	public void whenDeleteAllSubmodelDescritors_thenReturnNoContent() {
		ResponseEntity<Void> entry = submodelController.deleteAllSubmodelDescriptors();
		assertThat(entry.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
		assertThat(entry.getBody()).isNull();
	}

	private byte[] encode(String id) {
		return Base64.getUrlEncoder().encode(id.getBytes(StandardCharsets.UTF_8));
	}
}
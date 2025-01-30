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

package org.eclipse.digitaltwin.basyx.submodelregistry.feature.hierarchy;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.eclipse.digitaltwin.basyx.submodelregistry.client.ApiException;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.api.SubmodelRegistryApi;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.model.GetSubmodelDescriptorsResult;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.model.SubmodelDescriptor;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;

/**
 * Test for {@link HierarchicalSubmodelRegistryFeature}
 *
 * @author mateusmolina
 *
 */
public abstract class HierarchicalSubmodelRegistryTestSuite {

	protected abstract SubmodelRegistryApi getRootRegistryApi();

	protected abstract SubmodelRegistryApi getDelegatedRegistryApi();

	protected abstract DummyDescriptorFactory getDescriptorFactory();

	@Before
	public void setUp() throws ApiException {
		cleanUpRegistries();
		setupDelegatedRegistry();
		setupRootRegistry();
	}

	@Test
	public void getSubmodelDescriptor_InRoot() throws ApiException {
		SubmodelDescriptor actualDescriptor = getRootRegistryApi().getSubmodelDescriptorById(DummyDescriptorFactory.ROOT_ONLY_SM_ID);

		SubmodelDescriptor expectedDescriptor = getDescriptorFactory().getRootOnlySmDescriptor();

		assertEquals(expectedDescriptor, actualDescriptor);
	}

	@Test
	public void getSubmodelDescriptor_ThroughDelegated() throws ApiException {
		SubmodelDescriptor actualDescriptor = getRootRegistryApi().getSubmodelDescriptorById(getDescriptorFactory().getDelegatedOnlySmId());

		SubmodelDescriptor expectedDescriptor = getDescriptorFactory().getDelegatedOnlySmDescriptor();

		assertEquals(expectedDescriptor, actualDescriptor);
	}

	@Test
	public void getNonExistingSmDescriptor() {
		ApiException exception = assertThrows(ApiException.class, () -> getRootRegistryApi().getSubmodelDescriptorById("nonExistingSubmodel"));
		assertHttpCodeIsNotFound(exception);

		exception = assertThrows(ApiException.class, () -> getRootRegistryApi().getSubmodelDescriptorById("nonExistingSubmodel"));
		assertHttpCodeIsNotFound(exception);

	}

	private void setupRootRegistry() throws ApiException {
		getRootRegistryApi().postSubmodelDescriptor(getDescriptorFactory().getRootOnlySmDescriptor());
	}

	private void setupDelegatedRegistry() throws ApiException {
		SubmodelDescriptor descriptor = getDescriptorFactory().getDelegatedOnlySmDescriptor();

		getDelegatedRegistryApi().postSubmodelDescriptor(descriptor);
	}

	private void cleanUpRegistries() throws ApiException {
		getRootRegistryApi().deleteAllSubmodelDescriptors();
		getDelegatedRegistryApi().deleteAllSubmodelDescriptors();
	}

	private static void assertHttpCodeIsNotFound(ApiException e) {
		assertEquals(HttpStatus.NOT_FOUND.value(), e.getCode());
	}
}

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

package org.eclipse.digitaltwin.basyx.aasregistry.regression.feature.hierarchy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.eclipse.digitaltwin.basyx.aasregistry.client.ApiException;
import org.eclipse.digitaltwin.basyx.aasregistry.client.api.RegistryAndDiscoveryInterfaceApi;
import org.eclipse.digitaltwin.basyx.aasregistry.feature.hierarchy.AasRegistryModelMapper;
import org.eclipse.digitaltwin.basyx.aasregistry.feature.hierarchy.HierarchalAasRegistryFeature;
import org.eclipse.digitaltwin.basyx.aasregistry.model.AssetAdministrationShellDescriptor;
import org.eclipse.digitaltwin.basyx.aasregistry.model.SubmodelDescriptor;
import org.eclipse.digitaltwin.basyx.aasregistry.service.errors.AasDescriptorNotFoundException;
import org.eclipse.digitaltwin.basyx.aasregistry.service.errors.SubmodelNotFoundException;
import org.eclipse.digitaltwin.basyx.aasregistry.service.storage.AasRegistryStorage;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Test for {@link HierarchalAasRegistryFeature}
 *
 * @author mateusmolina
 *
 */
public class TestHierarchalAasRegistry {
	private static final String DELEGATED_REGISTRY_URL = "http://localhost:8050";
	private static final String REPO_BASE_URL = "http://127.0.0.1:8080";

	private static DummyDescriptorFactory descriptorFactory = new DummyDescriptorFactory(REPO_BASE_URL);

	private static ConfigurableApplicationContext appContext;
	private static AasRegistryStorage aasRegistryHierarchal;

	@BeforeClass
	public static void setUp() throws FileNotFoundException, IOException, ApiException {
		appContext = new SpringApplication(DummyAasRegistryComponent.class).run(new String[] {});
		
		aasRegistryHierarchal = appContext.getBean(AasRegistryStorage.class);
		
		cleanUpDelegatedRegistry();
		setupDelegatedRegistry();
		setupHierarchalRegistry();
	}
	
	@AfterClass
	public static void cleanUp() throws ApiException {
		cleanUpDelegatedRegistry();
		appContext.close();
	}

	@Test
	public void getAasDescriptor_InHierarchal() {
		AssetAdministrationShellDescriptor actualDescriptor = aasRegistryHierarchal.getAasDescriptor(DummyDescriptorFactory.AASDESCRIPTOR_ID_HIERARCHALONLY);
		
		AssetAdministrationShellDescriptor expectedDescriptor = descriptorFactory.getAasDescriptor_HierarchalOnly();

		assertEquals(expectedDescriptor, actualDescriptor);
	}

	@Test
	public void getAasDescriptor_ThroughDelegated() {
		AssetAdministrationShellDescriptor actualDescriptor = aasRegistryHierarchal.getAasDescriptor(DummyDescriptorFactory.AASDESCRIPTOR_ID_DELEGATEDONLY);

		AssetAdministrationShellDescriptor expectedDescriptor = descriptorFactory.getAasDescriptor_DelegatedOnly();

		assertEquals(expectedDescriptor, actualDescriptor);
	}

	@Test
	public void getNonExistingAasDescriptor() {
		assertThrows(AasDescriptorNotFoundException.class, () -> aasRegistryHierarchal.getAasDescriptor("nonExistingAasDescriptor"));
	}

	@Test
	public void getSubmodelDescriptor_InHierarchal() {
		SubmodelDescriptor actualDescriptor = aasRegistryHierarchal.getSubmodel(DummyDescriptorFactory.AASDESCRIPTOR_ID_HIERARCHALONLY, DummyDescriptorFactory.SMDESCRIPTOR_ID_HIERARCHALONLY);

		SubmodelDescriptor expectedDescriptor = descriptorFactory.getSmDescriptor_HierarchalOnly();

		assertEquals(expectedDescriptor, actualDescriptor);
	}

	@Test
	public void getSubmodelDescriptor_ThroughDelegated() {
		SubmodelDescriptor actualDescriptor = aasRegistryHierarchal.getSubmodel(DummyDescriptorFactory.AASDESCRIPTOR_ID_DELEGATEDONLY, DummyDescriptorFactory.SMDESCRIPTOR_ID_DELEGATEDONLY);

		SubmodelDescriptor expectedDescriptor = descriptorFactory.getSmDescriptor_DelegatedOnly();

		assertEquals(expectedDescriptor, actualDescriptor);
	}

	@Test
	public void getNonExistingSmDescriptor() {
		assertThrows(SubmodelNotFoundException.class, () -> aasRegistryHierarchal.getSubmodel(DummyDescriptorFactory.AASDESCRIPTOR_ID_HIERARCHALONLY, "nonExistingSubmodel"));
		assertThrows(SubmodelNotFoundException.class, () -> aasRegistryHierarchal.getSubmodel(DummyDescriptorFactory.AASDESCRIPTOR_ID_DELEGATEDONLY, "nonExistingSubmodel"));
		assertThrows(AasDescriptorNotFoundException.class, () -> aasRegistryHierarchal.getSubmodel("nonExistingAas", "nonExistingSubmodel"));
	}

	private static void setupHierarchalRegistry() {
		aasRegistryHierarchal.insertAasDescriptor(descriptorFactory.getAasDescriptor_HierarchalOnly());
	}

	private static void setupDelegatedRegistry() throws ApiException {
		RegistryAndDiscoveryInterfaceApi clientFacade = new RegistryAndDiscoveryInterfaceApi(DELEGATED_REGISTRY_URL);
		AssetAdministrationShellDescriptor descriptor = descriptorFactory.getAasDescriptor_DelegatedOnly();
		
		clientFacade.postAssetAdministrationShellDescriptor(AasRegistryModelMapper.mapEqModel(descriptor));
	}

	private static void cleanUpDelegatedRegistry() throws ApiException {
		RegistryAndDiscoveryInterfaceApi clientFacade = new RegistryAndDiscoveryInterfaceApi(DELEGATED_REGISTRY_URL);

		clientFacade.deleteAllShellDescriptors();
	}
}

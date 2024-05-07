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

package org.eclipse.digitaltwin.basyx.aasenvironment.client;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.basyx.aasregistry.client.ApiException;
import org.eclipse.digitaltwin.basyx.aasregistry.client.api.RegistryAndDiscoveryInterfaceApi;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.AssetAdministrationShellDescriptor;
import org.eclipse.digitaltwin.basyx.aasrepository.client.ConnectedAasRepository;
import org.eclipse.digitaltwin.basyx.aasrepository.feature.registry.integration.AasDescriptorFactory;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.api.SubmodelRegistryApi;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.model.SubmodelDescriptor;
import org.eclipse.digitaltwin.basyx.submodelrepository.client.ConnectedSubmodelRepository;
import org.eclipse.digitaltwin.basyx.submodelrepository.feature.registry.integration.SubmodelDescriptorFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;

/**
 * Test for {@link ConnectedAasManager}
 * 
 * @author mateusmolina
 *
 */
public class TestConnectedAasManager {
	private ConnectedAasRepository connectedAasRepository;
	private ConnectedSubmodelRepository connectedSmRepository;
	private RegistryAndDiscoveryInterfaceApi aasRegistryApi;
	private SubmodelRegistryApi smRegistryApi;
	private ConnectedAasManager aasManager;

	private AasDescriptorResolver aasDescriptorResolver;
	private AasDescriptorFactory aasDescriptorFactory;

	private SubmodelDescriptorResolver smDescriptorResolver;
	private SubmodelDescriptorFactory smDescriptorFactory;

	@Before
	public void setupRepositories() {
		connectedAasRepository = mock(ConnectedAasRepository.class);
		connectedSmRepository = mock(ConnectedSubmodelRepository.class);
		aasRegistryApi = mock(RegistryAndDiscoveryInterfaceApi.class);
		smRegistryApi = mock(SubmodelRegistryApi.class);
		aasDescriptorResolver = mock(AasDescriptorResolver.class);
		aasDescriptorFactory = mock(AasDescriptorFactory.class);
		smDescriptorResolver = mock(SubmodelDescriptorResolver.class);
		smDescriptorFactory = mock(SubmodelDescriptorFactory.class);

		ConnectedAasManagerFactory factory = new ConnectedAasManagerFactory();
		factory.setConnectedAasRepository(connectedAasRepository);
		factory.setConnectedSubmodelRepository(connectedSmRepository);
		factory.setRegistryAndDiscoveryInterfaceApi(aasRegistryApi);
		factory.setSubmodelRegistryApi(smRegistryApi);
		factory.setAasDescriptorFactory(aasDescriptorFactory);
		factory.setAasDescriptorResolver(aasDescriptorResolver);
		factory.setSmDescriptorResolver(smDescriptorResolver);
		factory.setSmDescriptorFactory(smDescriptorFactory);

		aasManager = factory.build();
	}

	@Test
	public void createAas() throws ApiException {
		AssetAdministrationShell expectedAas = TestFixture.buildAasPos1();
		AssetAdministrationShellDescriptor expectedDescriptor = TestFixture.buildAasPos1Descriptor();

		when(aasDescriptorFactory.create(expectedAas)).thenReturn(expectedDescriptor);

		aasManager.createAas(expectedAas);
		
		InOrder inOrder = inOrder(connectedAasRepository, aasRegistryApi);

		inOrder.verify(connectedAasRepository, times(1)).createAas(expectedAas);
		inOrder.verify(aasRegistryApi, times(1)).postAssetAdministrationShellDescriptor(expectedDescriptor);
	}

	@Test
	public void createSubmodelInAas() throws Exception {
		Submodel expectedSm = TestFixture.buildSmPos1();
		SubmodelDescriptor expectedDescriptor = TestFixture.buildSmPos1Descriptor();

		when(smDescriptorFactory.create(expectedSm)).thenReturn(expectedDescriptor);

		aasManager.createSubmodelInAas(TestFixture.AAS_POS1_ID, expectedSm);

		InOrder inOrder = inOrder(connectedSmRepository, smRegistryApi, connectedAasRepository);

		inOrder.verify(connectedSmRepository, times(1)).createSubmodel(expectedSm);
		inOrder.verify(smRegistryApi, times(1)).postSubmodelDescriptor(expectedDescriptor);
		inOrder.verify(connectedAasRepository, times(1)).addSubmodelReference(TestFixture.AAS_POS1_ID, any());
	}

	@Test
	public void deleteAas() throws ApiException {
		aasManager.deleteAas(TestFixture.AAS_POS1_ID);

		InOrder inOrder = inOrder(aasRegistryApi, connectedAasRepository);

		inOrder.verify(aasRegistryApi, times(1)).deleteAssetAdministrationShellDescriptorById(TestFixture.AAS_POS1_ID);
		inOrder.verify(connectedAasRepository, times(1)).deleteAas(TestFixture.AAS_POS1_ID);
	}

	@Test
	public void deleteSubmodelOfAas() throws Exception {
		aasManager.deleteSubmodelOfAas(TestFixture.AAS_POS1_ID, TestFixture.SM_POS1_ID);

		InOrder inOrder = inOrder(smRegistryApi, connectedAasRepository, connectedSmRepository);

		inOrder.verify(smRegistryApi, times(1)).deleteSubmodelDescriptorById(TestFixture.SM_POS1_ID);
		inOrder.verify(connectedAasRepository, times(1)).removeSubmodelReference(TestFixture.AAS_POS1_ID, TestFixture.SM_POS1_ID);
		inOrder.verify(connectedSmRepository, times(1)).deleteSubmodel(TestFixture.SM_POS1_ID);
	}

	@Test
	public void getAas() throws ApiException {
		AssetAdministrationShellDescriptor expectedDescriptor = TestFixture.buildAasPos1Descriptor();
		AssetAdministrationShell expectedAas = TestFixture.buildAasPos1();
		
		when(aasRegistryApi.getAssetAdministrationShellDescriptorById(TestFixture.AAS_POS1_ID)).thenReturn(expectedDescriptor);
		when(aasDescriptorResolver.resolveAasDescriptor(expectedDescriptor)).thenReturn(expectedAas);
		AssetAdministrationShell actualAas = aasManager.getAas(TestFixture.AAS_POS1_ID);

		assertEquals(expectedAas, actualAas);
	}

	@Test
	public void getSubmodel() throws Exception {
		SubmodelDescriptor expectedDescriptor = TestFixture.buildSmPos1Descriptor();
		Submodel expectedSm = TestFixture.buildSmPos1();

		when(smRegistryApi.getSubmodelDescriptorById(TestFixture.SM_POS1_ID)).thenReturn(expectedDescriptor);
		when(smDescriptorResolver.resolveSubmodelDescriptor(expectedDescriptor)).thenReturn(expectedSm);

		Submodel actualSm = aasManager.getSubmodel(TestFixture.SM_POS1_ID);

		assertEquals(expectedSm, actualSm);
	}

	@Test
	public void getSubmodelOfAas() throws Exception {
		AssetAdministrationShellDescriptor expectedAasDescriptor = TestFixture.buildAasPos1Descriptor();
		AssetAdministrationShell expectedAas = TestFixture.buildAasPos1();
		SubmodelDescriptor expectedSmDescriptor = TestFixture.buildSmPos1Descriptor();
		Submodel expectedSm = TestFixture.buildSmPos1();

		when(aasRegistryApi.getAssetAdministrationShellDescriptorById(TestFixture.AAS_POS1_ID)).thenReturn(expectedAasDescriptor);
		when(aasDescriptorResolver.resolveAasDescriptor(expectedAasDescriptor)).thenReturn(expectedAas);
		when(smRegistryApi.getSubmodelDescriptorById(TestFixture.SM_POS1_ID)).thenReturn(expectedSmDescriptor);
		when(smDescriptorResolver.resolveSubmodelDescriptor(expectedSmDescriptor)).thenReturn(expectedSm);

		Submodel actualSm = aasManager.getSubmodelOfAas(TestFixture.AAS_POS1_ID, TestFixture.SM_POS1_ID);

		assertEquals(expectedSm, actualSm);
	}

}

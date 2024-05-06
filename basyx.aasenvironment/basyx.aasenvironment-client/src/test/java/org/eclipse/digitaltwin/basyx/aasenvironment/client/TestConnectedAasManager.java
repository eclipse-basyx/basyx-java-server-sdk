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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.basyx.aasregistry.client.ApiException;
import org.eclipse.digitaltwin.basyx.aasregistry.client.api.RegistryAndDiscoveryInterfaceApi;
import org.eclipse.digitaltwin.basyx.aasrepository.client.ConnectedAasRepository;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.api.SubmodelRegistryApi;
import org.eclipse.digitaltwin.basyx.submodelrepository.client.ConnectedSubmodelRepository;
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
	

	protected static final String AAS_PRE1_ID = "aasPre1";
	protected static final String SUBMODEL_PRE1_ID = "smPre1";

	protected static final String AAS_POS1_ID = "aasPos1";
	protected static final String SUBMODEL_POS1_ID = "smPos1";

	private ConnectedAasRepository aasRepository;
	private ConnectedSubmodelRepository smRepository;
	private RegistryAndDiscoveryInterfaceApi aasRegistryApi;
	private SubmodelRegistryApi smRegistryApi;
	private ConnectedAasManager aasManager;

	@Before
	public void setupRepositories() {
		aasRepository = mock(ConnectedAasRepository.class);
		smRepository = mock(ConnectedSubmodelRepository.class);
		aasRegistryApi = mock(RegistryAndDiscoveryInterfaceApi.class);
		smRegistryApi = mock(SubmodelRegistryApi.class);
		
		aasManager = new ConnectedAasManager(aasRepository, aasRegistryApi, smRepository, smRegistryApi);
	}

	@Test
	public void createAas() throws ApiException {
		AssetAdministrationShell expectedAas = buildTestAas();

		aasManager.createAas(expectedAas);
		
		InOrder inOrder = inOrder(aasRepository, aasRegistryApi);

		inOrder.verify(aasRepository, times(1)).createAas(expectedAas);
		inOrder.verify(aasRegistryApi, times(1)).postAssetAdministrationShellDescriptor(any());
	}


	@Test
	public void createSubmodelOfAas() throws Exception {
		Submodel expectedSm = buildTestSm();

		aasManager.createSubmodelOfAas(AAS_PRE1_ID, expectedSm);

		InOrder inOrder = inOrder(smRepository, smRegistryApi, aasRepository);

		inOrder.verify(smRepository, times(1)).createSubmodel(expectedSm);
		inOrder.verify(smRegistryApi, times(1)).postSubmodelDescriptor(any());
		inOrder.verify(aasRepository, times(1)).addSubmodelReference(AAS_PRE1_ID, any());
	}

	@Test
	public void deleteAas() throws ApiException {
		aasManager.deleteAas(AAS_POS1_ID);

		InOrder inOrder = inOrder(aasRegistryApi, aasRepository);

		inOrder.verify(aasRegistryApi, times(1)).deleteAssetAdministrationShellDescriptorById(AAS_POS1_ID);
		inOrder.verify(aasRepository, times(1)).deleteAas(AAS_POS1_ID);
	}

	@Test
	public void deleteSubmodelOfAas() throws Exception {
		aasManager.deleteSubmodelOfAas(AAS_POS1_ID, SUBMODEL_POS1_ID);

		InOrder inOrder = inOrder(smRegistryApi, aasRepository, smRepository);

		inOrder.verify(smRegistryApi, times(1)).deleteSubmodelDescriptorById(SUBMODEL_POS1_ID);
		inOrder.verify(aasRepository, times(1)).removeSubmodelReference(AAS_POS1_ID, SUBMODEL_POS1_ID);
		inOrder.verify(smRepository, times(1)).deleteSubmodel(SUBMODEL_POS1_ID);
	}

	@Test
	public void getAas() throws ApiException {
		aasManager.getAas(AAS_POS1_ID);

		InOrder inOrder = inOrder(aasRegistryApi, aasRepository);

		inOrder.verify(aasRegistryApi, times(1)).getAssetAdministrationShellDescriptorById(AAS_POS1_ID);
		inOrder.verify(aasRepository, times(1)).getAas(AAS_POS1_ID);
	}

	@Test
	public void getSubmodel() throws Exception {
		aasManager.getSubmodel(SUBMODEL_POS1_ID);

		InOrder inOrder = inOrder(smRegistryApi, smRepository);

		inOrder.verify(smRegistryApi, times(1)).getSubmodelDescriptorById(SUBMODEL_POS1_ID);
		inOrder.verify(smRepository, times(1)).getSubmodel(SUBMODEL_POS1_ID);
	}

	@Test
	public void getSubmodelOfAas() throws Exception {
		aasManager.getSubmodelOfAas(AAS_POS1_ID, SUBMODEL_POS1_ID);

		InOrder inOrder = inOrder(aasRegistryApi, aasRepository, smRegistryApi, smRepository);

		inOrder.verify(aasRegistryApi, times(1)).getAssetAdministrationShellDescriptorById(AAS_POS1_ID);
		inOrder.verify(aasRepository, times(1)).getAas(AAS_POS1_ID);
		inOrder.verify(smRegistryApi, times(1)).getSubmodelDescriptorById(SUBMODEL_POS1_ID);
		inOrder.verify(smRepository, times(1)).getSubmodel(SUBMODEL_POS1_ID);
	}

	private static AssetAdministrationShell buildTestAas() {
		return null;
	}

	private static Submodel buildTestSm() {
		return null;
	}
}

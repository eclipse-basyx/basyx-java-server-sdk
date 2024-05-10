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
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;

import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.basyx.aasenvironment.client.exceptions.NoValidEndpointFoundException;
import org.eclipse.digitaltwin.basyx.aasregistry.client.ApiException;
import org.eclipse.digitaltwin.basyx.aasregistry.client.api.RegistryAndDiscoveryInterfaceApi;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.AssetAdministrationShellDescriptor;
import org.eclipse.digitaltwin.basyx.aasrepository.AasRepository;
import org.eclipse.digitaltwin.basyx.aasrepository.client.ConnectedAasRepository;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.api.SubmodelRegistryApi;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.model.SubmodelDescriptor;
import org.eclipse.digitaltwin.basyx.submodelrepository.SubmodelRepository;
import org.eclipse.digitaltwin.basyx.submodelrepository.client.ConnectedSubmodelRepository;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.InOrder;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Test for {@link ConnectedAasManager}
 * 
 * @author mateusmolina
 *
 */
public class TestConnectedAasManager {
	private final static String AAS_REPOSITORY_BASE_PATH = "http://localhost:8081";
	private final static String SM_REPOSITORY_BASE_PATH = "http://localhost:8081";
	private final static String AAS_REGISTRY_BASE_PATH = "http://localhost:8050";
	private final static String SM_REGISTRY_BASE_PATH = "http://localhost:8060";

	private static ConfigurableApplicationContext appContext;
	private static AasRepository aasRepository;
	private static SubmodelRepository smRepository;

	private final static TestFixture FIXTURE = new TestFixture(AAS_REPOSITORY_BASE_PATH, SM_REPOSITORY_BASE_PATH);

	private static ConnectedAasRepository connectedAasRepository;
	private static ConnectedSubmodelRepository connectedSmRepository;
	private static RegistryAndDiscoveryInterfaceApi aasRegistryApi;
	private static SubmodelRegistryApi smRegistryApi;

	private ConnectedAasManager aasManager;


	@BeforeClass
	public static void initApplication() {
		appContext = new SpringApplication(DummyAasEnvironmentComponent.class).run(new String[] {});

		aasRepository = appContext.getBean(AasRepository.class);
		smRepository = appContext.getBean(SubmodelRepository.class);
	}

	@AfterClass
	public static void cleanUpContext() {
		appContext.close();
	}

	@Before
	public void setupRepositories() {
		connectedAasRepository = spy(new ConnectedAasRepository(AAS_REPOSITORY_BASE_PATH));
		connectedSmRepository = spy(new ConnectedSubmodelRepository(SM_REPOSITORY_BASE_PATH));
		aasRegistryApi = spy(new RegistryAndDiscoveryInterfaceApi(AAS_REGISTRY_BASE_PATH));
		smRegistryApi = spy(new SubmodelRegistryApi(SM_REGISTRY_BASE_PATH));
		
		aasManager = new ConnectedAasManager(aasRegistryApi, connectedAasRepository, AAS_REPOSITORY_BASE_PATH, smRegistryApi, connectedSmRepository, SM_REPOSITORY_BASE_PATH);

		cleanUpRegistries();
		populateRepositories();
		populateRegistries();
	}

	@After
	public void cleanUpComponents() {
		cleanUpRegistries();
		cleanUpRepositories();
	}

	@Test
	public void createAas() throws ApiException {
		AssetAdministrationShell expectedAas = FIXTURE.buildAasPos1();
		AssetAdministrationShellDescriptor expectedDescriptor = FIXTURE.buildAasPos1Descriptor();

		aasManager.createAas(expectedAas);
		
		InOrder inOrder = inOrder(connectedAasRepository, aasRegistryApi);

		inOrder.verify(connectedAasRepository, times(1)).createAas(expectedAas);
		inOrder.verify(aasRegistryApi, times(1)).postAssetAdministrationShellDescriptor(expectedDescriptor);

		assertEquals(expectedAas, aasRepository.getAas(TestFixture.AAS_POS1_ID));
	}

	@Test
	public void createSubmodelInAas() throws Exception {
		Submodel expectedSm = FIXTURE.buildSmPos1();
		SubmodelDescriptor expectedDescriptor = FIXTURE.buildSmPos1Descriptor();

		aasManager.createSubmodelInAas(TestFixture.AAS_PRE1_ID, expectedSm);

		InOrder inOrder = inOrder(connectedSmRepository, smRegistryApi, connectedAasRepository);

		inOrder.verify(connectedSmRepository, times(1)).createSubmodel(expectedSm);
		inOrder.verify(smRegistryApi, times(1)).postSubmodelDescriptor(expectedDescriptor);
		inOrder.verify(connectedAasRepository, times(1)).addSubmodelReference(eq(TestFixture.AAS_PRE1_ID), any());

		assertEquals(expectedSm, smRepository.getSubmodel(TestFixture.SM_POS1_ID));
	}

	@Test
	public void deleteAas() throws ApiException {
		aasManager.deleteAas(TestFixture.AAS_PRE1_ID);

		InOrder inOrder = inOrder(aasRegistryApi, connectedAasRepository);

		inOrder.verify(aasRegistryApi, times(1)).deleteAssetAdministrationShellDescriptorById(TestFixture.AAS_PRE1_ID);
		inOrder.verify(connectedAasRepository, times(1)).deleteAas(TestFixture.AAS_PRE1_ID);

		assertThrows(ElementDoesNotExistException.class, () -> aasRepository.getAas(TestFixture.AAS_PRE1_ID));
	}

	@Test
	public void deleteSubmodelOfAas() throws Exception {
		aasManager.deleteSubmodelOfAas(TestFixture.AAS_PRE1_ID, TestFixture.SM_PRE1_ID);

		InOrder inOrder = inOrder(smRegistryApi, connectedAasRepository, connectedSmRepository);

		inOrder.verify(smRegistryApi, times(1)).deleteSubmodelDescriptorById(TestFixture.SM_PRE1_ID);
		inOrder.verify(connectedAasRepository, times(1)).removeSubmodelReference(TestFixture.AAS_PRE1_ID, TestFixture.SM_PRE1_ID);
		inOrder.verify(connectedSmRepository, times(1)).deleteSubmodel(TestFixture.SM_PRE1_ID);

		assertThrows(ElementDoesNotExistException.class, () -> smRepository.getSubmodel(TestFixture.SM_PRE1_ID));
	}

	@Test
	public void getAas() throws ApiException, NoValidEndpointFoundException {
		AssetAdministrationShell expectedAas = FIXTURE.buildAasPre1();
		
		AssetAdministrationShell actualAas = aasManager.getAas(TestFixture.AAS_PRE1_ID);

		assertEquals(expectedAas, actualAas);
	}

	@Test
	public void getSubmodel() throws Exception {
		Submodel expectedSm = FIXTURE.buildSmPre1();

		Submodel actualSm = aasManager.getSubmodel(TestFixture.SM_PRE1_ID);

		assertEquals(expectedSm, actualSm);
	}

	private void populateRegistries() {
		try {
			new RegistryAndDiscoveryInterfaceApi(AAS_REGISTRY_BASE_PATH).postAssetAdministrationShellDescriptor(FIXTURE.buildAasPre1Descriptor());
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		try {
			new SubmodelRegistryApi(SM_REGISTRY_BASE_PATH).postSubmodelDescriptor(FIXTURE.buildSmPre1Descriptor());
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	private void cleanUpRegistries() {
		try {
			new RegistryAndDiscoveryInterfaceApi(AAS_REGISTRY_BASE_PATH).deleteAllShellDescriptors();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		try {
			new SubmodelRegistryApi(SM_REGISTRY_BASE_PATH).deleteAllSubmodelDescriptors();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	private void populateRepositories() {
		try {
			aasRepository.createAas(FIXTURE.buildAasPre1());
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		try {
			smRepository.createSubmodel(FIXTURE.buildSmPre1());
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	private void cleanUpRepositories() {
		try {
			aasRepository.deleteAas(TestFixture.AAS_PRE1_ID);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		try {
			aasRepository.deleteAas(TestFixture.AAS_POS1_ID);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		try {
			smRepository.deleteSubmodel(TestFixture.SM_PRE1_ID);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		try {
			smRepository.deleteSubmodel(TestFixture.SM_POS1_ID);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

}

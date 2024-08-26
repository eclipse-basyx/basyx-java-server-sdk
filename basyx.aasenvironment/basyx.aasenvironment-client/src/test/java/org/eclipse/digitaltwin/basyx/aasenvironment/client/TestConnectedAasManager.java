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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.basyx.aasenvironment.client.exceptions.NoValidEndpointFoundException;
import org.eclipse.digitaltwin.basyx.aasenvironment.client.resolvers.AasDescriptorResolver;
import org.eclipse.digitaltwin.basyx.aasenvironment.client.resolvers.DescriptorResolverManager;
import org.eclipse.digitaltwin.basyx.aasenvironment.client.resolvers.EndpointResolver;
import org.eclipse.digitaltwin.basyx.aasenvironment.client.resolvers.SubmodelDescriptorResolver;
import org.eclipse.digitaltwin.basyx.aasregistry.client.ApiException;
import org.eclipse.digitaltwin.basyx.aasregistry.client.api.RegistryAndDiscoveryInterfaceApi;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.AssetAdministrationShellDescriptor;
import org.eclipse.digitaltwin.basyx.aasrepository.AasRepository;
import org.eclipse.digitaltwin.basyx.aasrepository.client.ConnectedAasRepository;
import org.eclipse.digitaltwin.basyx.aasservice.client.ConnectedAasService;
import org.eclipse.digitaltwin.basyx.client.internal.resolver.DescriptorResolver;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.api.SubmodelRegistryApi;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.model.SubmodelDescriptor;
import org.eclipse.digitaltwin.basyx.submodelrepository.SubmodelRepository;
import org.eclipse.digitaltwin.basyx.submodelrepository.client.ConnectedSubmodelRepository;
import org.eclipse.digitaltwin.basyx.submodelservice.client.ConnectedSubmodelService;
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
	protected static final String AAS_REPOSITORY_BASE_PATH = "http://localhost:8081";
	protected static final String SM_REPOSITORY_BASE_PATH = "http://localhost:8081";
	protected static final String AAS_REGISTRY_BASE_PATH = "http://localhost:8050";
	protected static final String SM_REGISTRY_BASE_PATH = "http://localhost:8060";

	protected static ConfigurableApplicationContext appContext;
	protected static AasRepository aasRepository;
	protected static SubmodelRepository smRepository;

	protected static final TestFixture FIXTURE = new TestFixture(AAS_REPOSITORY_BASE_PATH, SM_REPOSITORY_BASE_PATH);

	protected static ConnectedAasRepository connectedAasRepository;
	protected static ConnectedSubmodelRepository connectedSmRepository;
	protected static RegistryAndDiscoveryInterfaceApi aasRegistryApi;
	protected static SubmodelRegistryApi smRegistryApi;

	protected ConnectedAasManager aasManager;

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
		connectedAasRepository = getConnectedAasRepo();
		connectedSmRepository = getConnectedSubmodelRepo();
		aasRegistryApi = getConnectedAasRegistry();
		smRegistryApi = getConnectedSubmodelRegistry();
		
		aasManager = getConnectedAasManager();

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

		inOrder.verify(connectedAasRepository, times(1))
				.createAas(expectedAas);
		inOrder.verify(aasRegistryApi, times(1))
				.postAssetAdministrationShellDescriptor(expectedDescriptor);

		assertEquals(expectedAas, getAasFromRepo(TestFixture.AAS_POS1_ID));
		assertEquals(expectedDescriptor, getDescriptorFromAasRegistry(TestFixture.AAS_POS1_ID));
	}

	@Test
	public void createSubmodelInAas() throws Exception {
		Submodel expectedSm = FIXTURE.buildSmPos1();
		SubmodelDescriptor expectedDescriptor = FIXTURE.buildSmPos1Descriptor();
		Reference expectedRef = FIXTURE.buildSmPos1Ref();

		aasManager.createSubmodelInAas(TestFixture.AAS_PRE1_ID, expectedSm);

		InOrder inOrder = inOrder(connectedSmRepository, smRegistryApi, connectedAasRepository);

		inOrder.verify(connectedSmRepository, times(1)).createSubmodel(expectedSm);
		inOrder.verify(smRegistryApi, times(1)).postSubmodelDescriptor(expectedDescriptor);
		inOrder.verify(connectedAasRepository, times(1)).addSubmodelReference(eq(TestFixture.AAS_PRE1_ID), any());

		assertEquals(expectedSm, getSubmodelFromRepo(TestFixture.SM_POS1_ID));
		assertEquals(expectedDescriptor, getDescriptorFromSubmodelRegistry(TestFixture.SM_POS1_ID));
		assertEquals(expectedRef, getSubmodelRefFromAasRepository(TestFixture.AAS_PRE1_ID, TestFixture.SM_POS1_ID).get());
	}

	@Test
	public void deleteAas() throws ApiException {
		aasManager.deleteAas(TestFixture.AAS_PRE1_ID);

		InOrder inOrder = inOrder(aasRegistryApi, connectedAasRepository);

		inOrder.verify(aasRegistryApi, times(1))
				.deleteAssetAdministrationShellDescriptorById(TestFixture.AAS_PRE1_ID);
		inOrder.verify(connectedAasRepository, times(1))
				.deleteAas(TestFixture.AAS_PRE1_ID);

		assertThrows(ElementDoesNotExistException.class, () -> getAasFromRepo(TestFixture.AAS_PRE1_ID));
		assertThrows(Exception.class, () -> getDescriptorFromAasRegistry(TestFixture.AAS_PRE1_ID));
	}

	@Test
	public void deleteSubmodelOfAas() throws Exception {
		aasManager.deleteSubmodelOfAas(TestFixture.AAS_PRE1_ID, TestFixture.SM_PRE1_ID);

		InOrder inOrder = inOrder(smRegistryApi, connectedAasRepository, connectedSmRepository);

		inOrder.verify(smRegistryApi, times(1))
				.deleteSubmodelDescriptorById(TestFixture.SM_PRE1_ID);
		inOrder.verify(connectedAasRepository, times(1))
				.removeSubmodelReference(TestFixture.AAS_PRE1_ID, TestFixture.SM_PRE1_ID);
		inOrder.verify(connectedSmRepository, times(1))
				.deleteSubmodel(TestFixture.SM_PRE1_ID);

		assertThrows(ElementDoesNotExistException.class, () -> getSubmodelFromRepo(TestFixture.SM_PRE1_ID));
		assertThrows(Exception.class, () -> getDescriptorFromSubmodelRegistry(TestFixture.SM_PRE1_ID));
	}

	@Test
	public void getAas() throws NoValidEndpointFoundException {
		AssetAdministrationShell expectedAas = FIXTURE.buildAasPre1();

		AssetAdministrationShell actualAas = aasManager.getAasService(TestFixture.AAS_PRE1_ID)
				.getAAS();

		assertEquals(expectedAas, actualAas);
	}

	@Test
	public void getSubmodel() {
		Submodel expectedSm = FIXTURE.buildSmPre1();
		
		Submodel actualSm = aasManager.getSubmodelService(TestFixture.SM_PRE1_ID)
				.getSubmodel();

		assertEquals(expectedSm, actualSm);
	}

	protected Submodel getSubmodelFromManager(String submodelId) {
		return aasManager.getSubmodelService(submodelId).getSubmodel();
	}
	
	protected AssetAdministrationShellDescriptor getDescriptorFromAasRegistry(String shellId) throws ApiException {
		return new RegistryAndDiscoveryInterfaceApi(AAS_REGISTRY_BASE_PATH).getAssetAdministrationShellDescriptorById(shellId);
	}

	protected AssetAdministrationShell getAasFromRepo(String shellId) {
		return aasRepository.getAas(shellId);
	}
	
	protected SubmodelDescriptor getDescriptorFromSubmodelRegistry(String submodelId) throws org.eclipse.digitaltwin.basyx.submodelregistry.client.ApiException {
		return new SubmodelRegistryApi(SM_REGISTRY_BASE_PATH).getSubmodelDescriptorById(submodelId);
	}

	protected Submodel getSubmodelFromRepo(String submodelId) {
		return smRepository.getSubmodel(TestFixture.SM_POS1_ID);
	}
	
	protected AssetAdministrationShell getAasFromManager(String shellId) {
		return aasManager.getAasService(shellId).getAAS();
	}
	
	protected ConnectedAasManager getConnectedAasManager() {
		DescriptorResolver<AssetAdministrationShellDescriptor, ConnectedAasService> aasDescriptorResolver = new AasDescriptorResolver(new EndpointResolver());
		DescriptorResolver<SubmodelDescriptor, ConnectedSubmodelService> smDescriptorResolver = new SubmodelDescriptorResolver(new EndpointResolver());
		
		DescriptorResolverManager descriptorResolverManager = new DescriptorResolverManager(aasDescriptorResolver, smDescriptorResolver);
		
		return new ConnectedAasManager(aasRegistryApi, connectedAasRepository, smRegistryApi, connectedSmRepository, descriptorResolverManager);
	}

	protected SubmodelRegistryApi getConnectedSubmodelRegistry() {
		return spy(new SubmodelRegistryApi(SM_REGISTRY_BASE_PATH));
	}

	protected RegistryAndDiscoveryInterfaceApi getConnectedAasRegistry() {
		return spy(new RegistryAndDiscoveryInterfaceApi(AAS_REGISTRY_BASE_PATH));
	}

	protected ConnectedSubmodelRepository getConnectedSubmodelRepo() {
		return spy(new ConnectedSubmodelRepository(SM_REPOSITORY_BASE_PATH));
	}

	protected ConnectedAasRepository getConnectedAasRepo() {
		return spy(new ConnectedAasRepository(AAS_REPOSITORY_BASE_PATH));
	}

	@Test
	public void getAllSubmodels() {
		Submodel otherExpectedSubmodel = FIXTURE.buildSmPos1();
		Submodel[] expectedSubmodels = { FIXTURE.buildSmPre1(), otherExpectedSubmodel };

		aasManager.createSubmodelInAas(TestFixture.AAS_PRE1_ID, otherExpectedSubmodel);

		List<ConnectedSubmodelService> actualSubmodelServices = aasManager.getAllSubmodels(TestFixture.AAS_PRE1_ID);
		List<Submodel> actualSubmodels = actualSubmodelServices.stream()
				.map(submodelService -> submodelService.getSubmodel())
				.collect(Collectors.toList());
		assertEquals(Arrays.asList(expectedSubmodels), actualSubmodels);

	}

	protected void populateRegistries() {
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

	protected void cleanUpRegistries() {
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

	protected void populateRepositories() {
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

	protected void cleanUpRepositories() {
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

	private Optional<Reference> getSubmodelRefFromAasRepository(String aasId, String smId) {
		return aasManager.getAasService(aasId)
				.getAAS()
				.getSubmodels()
				.stream()
				.filter(ref -> ref.getKeys().stream()
						.anyMatch(key -> key.getValue().equals(smId)))
				.findAny();
	}

}

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

import static org.mockito.Mockito.spy;
import java.io.IOException;

import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.basyx.aasregistry.client.ApiException;
import org.eclipse.digitaltwin.basyx.aasregistry.client.api.RegistryAndDiscoveryInterfaceApi;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.AssetAdministrationShellDescriptor;
import org.eclipse.digitaltwin.basyx.aasregistry.main.client.AuthorizedConnectedAasRegistry;
import org.eclipse.digitaltwin.basyx.aasrepository.AasRepository;
import org.eclipse.digitaltwin.basyx.aasrepository.client.AuthorizedConnectedAasRepository;
import org.eclipse.digitaltwin.basyx.aasrepository.client.ConnectedAasRepository;
import org.eclipse.digitaltwin.basyx.aasservice.client.TestAuthorizedConnectedAasService;
import org.eclipse.digitaltwin.basyx.client.internal.authorization.TokenManager;
import org.eclipse.digitaltwin.basyx.client.internal.authorization.credential.ClientCredential;
import org.eclipse.digitaltwin.basyx.client.internal.authorization.grant.ClientCredentialAccessTokenProvider;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.AuthorizedConnectedSubmodelRegistry;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.api.SubmodelRegistryApi;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.model.SubmodelDescriptor;
import org.eclipse.digitaltwin.basyx.submodelrepository.SubmodelRepository;
import org.eclipse.digitaltwin.basyx.submodelrepository.client.AuthorizedConnectedSubmodelRepository;
import org.eclipse.digitaltwin.basyx.submodelrepository.client.ConnectedSubmodelRepository;
import org.junit.BeforeClass;
import org.springframework.boot.SpringApplication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Test for {@link AuthorizedConnectedAasManager}
 * 
 * @author danish
 *
 */
public class TestAuthorizedConnectedAasManager extends TestConnectedAasManager {
	
	private static final String PROFILE = "authorization";
	protected final static String AAS_REGISTRY_BASE_PATH = "http://localhost:8051";
	protected final static String SM_REGISTRY_BASE_PATH = "http://localhost:8061";
	
	private final static TokenManager TOKEN_MANAGER = new TokenManager("http://localhost:9098/realms/BaSyx/protocol/openid-connect/token", new ClientCredentialAccessTokenProvider(new ClientCredential("workstation-1", "nY0mjyECF60DGzNmQUjL81XurSl8etom")));
	private final static TokenManager TOKEN_MANAGER_REGISTRY = new TokenManager("http://localhost/realms/BaSyx/protocol/openid-connect/token", new ClientCredentialAccessTokenProvider(new ClientCredential("workstation-1", "nY0mjyECF60DGzNmQUjL81XurSl8etom")));
	
	private static AuthorizedConnectedAasRepository connectedAasRepository;
	private static AuthorizedConnectedSubmodelRepository connectedSmRepository;
	private static AuthorizedConnectedAasRegistry aasRegistryApi;
	private static AuthorizedConnectedSubmodelRegistry smRegistryApi;

	private AuthorizedConnectedAasManager aasManager;
	
	@BeforeClass
	public static void initApplication() {
		SpringApplication application = new SpringApplication(DummyAasEnvironmentComponent.class);
		application.setAdditionalProfiles(PROFILE);
		
		appContext = application.run(new String[] {});

		aasRepository = appContext.getBean(AasRepository.class);
		smRepository = appContext.getBean(SubmodelRepository.class);
	}
	
	@Override
	protected ConnectedAasManager getConnectedAasManager() {
		
		aasManager = new AuthorizedConnectedAasManager(aasRegistryApi, connectedAasRepository, smRegistryApi, connectedSmRepository);
		
		return aasManager;
	}

	@Override
	protected SubmodelRegistryApi getConnectedSubmodelRegistry() {
		 
		smRegistryApi = spy(new AuthorizedConnectedSubmodelRegistry(SM_REGISTRY_BASE_PATH,  TOKEN_MANAGER_REGISTRY));
		
		return smRegistryApi;
	}

	@Override
	protected RegistryAndDiscoveryInterfaceApi getConnectedAasRegistry() {
		
		aasRegistryApi = spy(new AuthorizedConnectedAasRegistry(AAS_REGISTRY_BASE_PATH, TOKEN_MANAGER_REGISTRY));
		
		return aasRegistryApi;
	}

	@Override
	protected ConnectedSubmodelRepository getConnectedSubmodelRepo() {
		
		connectedSmRepository = spy(new AuthorizedConnectedSubmodelRepository(SM_REPOSITORY_BASE_PATH, TOKEN_MANAGER));
		
		return connectedSmRepository;
	}

	@Override
	protected ConnectedAasRepository getConnectedAasRepo() {
		
		connectedAasRepository = spy(new AuthorizedConnectedAasRepository(AAS_REPOSITORY_BASE_PATH, TOKEN_MANAGER));
		
		return connectedAasRepository;
	}
	
	@Override
	protected Submodel getSubmodelFromManager(String submodelId) {
		return aasManager.getSubmodelService(submodelId).getSubmodel();
	}
	
	@Override
	protected AssetAdministrationShellDescriptor getDescriptorFromAasRegistry(String shellId) throws ApiException {
		return new AuthorizedConnectedAasRegistry(AAS_REGISTRY_BASE_PATH, TOKEN_MANAGER_REGISTRY).getAssetAdministrationShellDescriptorById(shellId);
	}

	@Override
	protected AssetAdministrationShell getAasFromRepo(String shellId) {
		try {
			TestAuthorizedConnectedAasService.configureSecurityContext(TestAuthorizedConnectedAasService.getTokenProvider());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		AssetAdministrationShell assetAdministrationShell = aasRepository.getAas(shellId);
		
		SecurityContextHolder.clearContext();
		
		return assetAdministrationShell;
	}
	
	@Override
	protected SubmodelDescriptor getDescriptorFromSubmodelRegistry(String submodelId) throws org.eclipse.digitaltwin.basyx.submodelregistry.client.ApiException {
		return new AuthorizedConnectedSubmodelRegistry(SM_REGISTRY_BASE_PATH, TOKEN_MANAGER_REGISTRY).getSubmodelDescriptorById(submodelId);
	}

	@Override
	protected Submodel getSubmodelFromRepo(String submodelId) {
		try {
			TestAuthorizedConnectedAasService.configureSecurityContext(TestAuthorizedConnectedAasService.getTokenProvider());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Submodel submodel = smRepository.getSubmodel(submodelId);
		
		SecurityContextHolder.clearContext();
		
		return submodel;
	}
	
	@Override
	protected AssetAdministrationShell getAasFromManager(String shellId) {
		return aasManager.getAasService(shellId).getAAS();
	}
	
	@Override
	protected void populateRegistries() {
		try {
			new AuthorizedConnectedAasRegistry(AAS_REGISTRY_BASE_PATH, TOKEN_MANAGER_REGISTRY).postAssetAdministrationShellDescriptor(FIXTURE.buildAasPre1Descriptor());
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		try {
			new AuthorizedConnectedSubmodelRegistry(SM_REGISTRY_BASE_PATH, TOKEN_MANAGER_REGISTRY).postSubmodelDescriptor(FIXTURE.buildSmPre1Descriptor());
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	@Override
	protected void cleanUpRegistries() {
		try {
			new AuthorizedConnectedAasRegistry(AAS_REGISTRY_BASE_PATH, TOKEN_MANAGER_REGISTRY).deleteAllShellDescriptors();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		try {
			new AuthorizedConnectedSubmodelRegistry(SM_REGISTRY_BASE_PATH, TOKEN_MANAGER_REGISTRY).deleteAllSubmodelDescriptors();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	@Override
	protected void populateRepositories() {
		try {
			TestAuthorizedConnectedAasService.configureSecurityContext(TestAuthorizedConnectedAasService.getTokenProvider());
			
			aasRepository.createAas(FIXTURE.buildAasPre1());
			
			SecurityContextHolder.clearContext();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		try {
			TestAuthorizedConnectedAasService.configureSecurityContext(TestAuthorizedConnectedAasService.getTokenProvider());
			
			smRepository.createSubmodel(FIXTURE.buildSmPre1());
			
			SecurityContextHolder.clearContext();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	@Override
	protected void cleanUpRepositories() {
		try {
			TestAuthorizedConnectedAasService.configureSecurityContext(TestAuthorizedConnectedAasService.getTokenProvider());
			
			aasRepository.deleteAas(TestFixture.AAS_PRE1_ID);
			
			SecurityContextHolder.clearContext();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		try {
			TestAuthorizedConnectedAasService.configureSecurityContext(TestAuthorizedConnectedAasService.getTokenProvider());
			
			aasRepository.deleteAas(TestFixture.AAS_POS1_ID);
			
			SecurityContextHolder.clearContext();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		try {
			TestAuthorizedConnectedAasService.configureSecurityContext(TestAuthorizedConnectedAasService.getTokenProvider());
			
			smRepository.deleteSubmodel(TestFixture.SM_PRE1_ID);
			
			SecurityContextHolder.clearContext();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		try {
			TestAuthorizedConnectedAasService.configureSecurityContext(TestAuthorizedConnectedAasService.getTokenProvider());
			
			smRepository.deleteSubmodel(TestFixture.SM_POS1_ID);
			
			SecurityContextHolder.clearContext();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
}

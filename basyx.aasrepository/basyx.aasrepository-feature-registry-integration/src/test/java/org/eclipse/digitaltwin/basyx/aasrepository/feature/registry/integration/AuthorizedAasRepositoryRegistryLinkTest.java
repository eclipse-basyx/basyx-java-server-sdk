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

package org.eclipse.digitaltwin.basyx.aasrepository.feature.registry.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.eclipse.digitaltwin.basyx.aasregistry.client.ApiException;
import org.eclipse.digitaltwin.basyx.aasregistry.client.api.RegistryAndDiscoveryInterfaceApi;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.AssetAdministrationShellDescriptor;
import org.eclipse.digitaltwin.basyx.aasregistry.main.client.AuthorizedConnectedAasRegistry;
import org.eclipse.digitaltwin.basyx.client.internal.authorization.TokenManager;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpStatus;

/**
 * Integration test for Authorized Registry
 * 
 * @author danish
 */
public class AuthorizedAasRepositoryRegistryLinkTest extends AasRepositoryRegistryLinkTestSuite {

	private static final String AAS_REPO_URL = "http://localhost:8081";
	private static final String AAS_REGISTRY_BASE_URL = "http://localhost:8051";
	private static ConfigurableApplicationContext appContext;
	private static AasRepositoryRegistryLink aasRepositoryRegistryLink;
	
	@BeforeClass
	public static void setUp() throws FileNotFoundException, IOException {
		SpringApplication application = new SpringApplication(DummyAasRepositoryIntegrationComponent.class);
		application.setAdditionalProfiles("authregistry");
		
		appContext = application.run(new String[] {});
		
		aasRepositoryRegistryLink = appContext.getBean(AasRepositoryRegistryLink.class);
	}
	
	@AfterClass
	public static void tearDown() {
		appContext.close();
	}
	
	@Test
	public void sendUnauthorizedRequest() throws IOException {
		TokenManager mockTokenManager = Mockito.mock(TokenManager.class);

		Mockito.when(mockTokenManager.getAccessToken()).thenReturn("mockedAccessToken");

		RegistryAndDiscoveryInterfaceApi registryApi = new AuthorizedConnectedAasRegistry(AAS_REGISTRY_BASE_URL, mockTokenManager);

		AssetAdministrationShellDescriptor descriptor = new AssetAdministrationShellDescriptor();
		descriptor.setIdShort("shortId");

		ApiException exception = assertThrows(ApiException.class, () -> {
			registryApi.postAssetAdministrationShellDescriptor(descriptor);
		});

		assertEquals(HttpStatus.UNAUTHORIZED.value(), exception.getCode());
	}

	@Override
	protected String[] getAasRepoBaseUrls() {
		return new String[] {AAS_REPO_URL};
	}

	@Override
	protected String getAasRegistryUrl() {
		return AAS_REGISTRY_BASE_URL;
	}

	@Override
	protected RegistryAndDiscoveryInterfaceApi getAasRegistryApi() {
		
		return aasRepositoryRegistryLink.getRegistryApi();
	}

}

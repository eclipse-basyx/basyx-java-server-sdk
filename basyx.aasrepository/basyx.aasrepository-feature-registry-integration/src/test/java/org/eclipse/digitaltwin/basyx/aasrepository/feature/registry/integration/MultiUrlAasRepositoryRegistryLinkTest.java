/*******************************************************************************
 * Copyright (C) 2023 the Eclipse BaSyx Authors
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

import java.io.FileNotFoundException;
import java.io.IOException;

import org.eclipse.digitaltwin.basyx.aasregistry.client.api.RegistryAndDiscoveryInterfaceApi;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Integration test for {@link RegistryIntegrationAasRepository} feature
 * 
 * @author danish
 */
public class MultiUrlAasRepositoryRegistryLinkTest extends AasRepositoryRegistryLinkTestSuite {

	private static final String[] AAS_REPO_URLS = new String []{"http://localhost:8081","https://aas-repo.example.org","http://aas-repo:8081"};
	private static final String AAS_REGISTRY_BASE_URL = "http://localhost:8050";
	private static ConfigurableApplicationContext appContext;
	private static AasRepositoryRegistryLink aasRepositoryRegistryLink;
	
	@BeforeClass
	public static void setUp() throws FileNotFoundException, IOException {
		SpringApplication application = new SpringApplication(DummyAasRepositoryIntegrationComponent.class);
		application.setAdditionalProfiles("multiurl");
		
		appContext = application.run(new String[] {});
		
		aasRepositoryRegistryLink = appContext.getBean(AasRepositoryRegistryLink.class);
	}
	
	@AfterClass
	public static void tearDown() {
		appContext.close();
	}

	@Override
	protected String[] getAasRepoBaseUrls() {
		return AAS_REPO_URLS;
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

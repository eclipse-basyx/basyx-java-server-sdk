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

package org.eclipse.digitaltwin.basyx.submodelrepository.feature.registry.integration;

import java.io.FileNotFoundException;
import java.io.IOException;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.api.SubmodelRegistryApi;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Integration test for {@link RegistryIntegrationSubmodelRepository} feature
 * 
 * @author danish
 */
public class SubmodelRepositoryRegistryLinkTest extends SubmodelRepositoryRegistryLinkTestSuite {
	
	private static final String SUBMODEL_REPO_URL = "http://localhost:8081";
	private static final String SUBMODEL_REGISTRY_BASE_URL = "http://localhost:8060";
	private static ConfigurableApplicationContext appContext;
	private static SubmodelRepositoryRegistryLink submodelRepositoryRegistryLink;
	
	@BeforeClass
	public static void setUp() throws FileNotFoundException, IOException {
		appContext = new SpringApplication(DummySubmodelRepositoryIntegrationComponent.class).run(new String[] {});
		
		submodelRepositoryRegistryLink = appContext.getBean(SubmodelRepositoryRegistryLink.class);
	}
	
	@AfterClass
	public static void tearDown() {
		appContext.close();
	}
	
	@Override
	protected String[] getSubmodelRepoBaseUrls() {
		return new String[] { SUBMODEL_REPO_URL };
	}
	@Override
	protected String getSubmodelRegistryUrl() {
		return SUBMODEL_REGISTRY_BASE_URL;
	}
	@Override
	protected SubmodelRegistryApi getSubmodelRegistryApi() {
		
		return submodelRepositoryRegistryLink.getRegistryApi();
	}


}

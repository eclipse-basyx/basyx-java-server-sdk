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

package org.eclipse.digitaltwin.basyx.aasregistry.feature.hierarchy;

import org.eclipse.digitaltwin.basyx.aasregistry.client.api.RegistryAndDiscoveryInterfaceApi;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.web.server.context.WebServerApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * LocalhostHierarchicalAasRegistryTest
 *
 * @author mateusmolina
 *
 */
public class LocalhostHierarchicalAasRegistryTest extends HierarchicalAasRegistryTestSuite {

	private static final String REPO_BASE_URL = "http://localhost:8080";

	private static RegistryAndDiscoveryInterfaceApi rootRegistryApi;
	private static RegistryAndDiscoveryInterfaceApi delegatedRegistryApi;
	private static DummyDescriptorFactory factory;

	private static ConfigurableApplicationContext rootAppContext;
	private static ConfigurableApplicationContext delegatedAppContext;

	@BeforeClass
	public static void setupRegistries() {
		delegatedAppContext = new SpringApplication(DummyAasRegistryComponent.class).run(new String[] { "--server.port=0", "--basyx.feature.hierarchy.enabled=false" });
		rootAppContext = new SpringApplication(DummyAasRegistryComponent.class).run(new String[] { "--server.port=0" });

		String rootRegistryUrl = localUrl(rootAppContext);
		String delegatedRegistryUrl = localUrl(delegatedAppContext);

		rootRegistryApi = new RegistryAndDiscoveryInterfaceApi(rootRegistryUrl);
		delegatedRegistryApi = new RegistryAndDiscoveryInterfaceApi(delegatedRegistryUrl);
		factory = new DummyDescriptorFactory(REPO_BASE_URL, delegatedRegistryUrl + "/test/aas");
	}

	@AfterClass
	public static void tearDownRegistries() {
		if (rootAppContext != null)
			rootAppContext.close();

		if (delegatedAppContext != null)
			delegatedAppContext.close();
	}

	private static String localUrl(ConfigurableApplicationContext applicationContext) {
		return "http://localhost:" + ((WebServerApplicationContext) applicationContext).getWebServer().getPort();
	}

	@Override
	protected RegistryAndDiscoveryInterfaceApi getRootRegistryApi() {
		return rootRegistryApi;
	}

	@Override
	protected RegistryAndDiscoveryInterfaceApi getDelegatedRegistryApi() {
		return delegatedRegistryApi;
	}

	@Override
	protected DummyDescriptorFactory getDescriptorFactory() {
		return factory;
	}

}

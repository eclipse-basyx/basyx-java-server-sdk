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

package org.eclipse.digitaltwin.basyx.aasregistry.feature.hierarchy.example;

import org.eclipse.digitaltwin.basyx.aasregistry.client.api.RegistryAndDiscoveryInterfaceApi;
import org.eclipse.digitaltwin.basyx.aasregistry.feature.hierarchy.DummyDescriptorFactory;
import org.eclipse.digitaltwin.basyx.aasregistry.feature.hierarchy.HierarchicalAasRegistryTestSuite;

/**
 * HierachicalAasRegistryIT
 *
 * @author mateusmolina
 *
 */
public class HierachicalAasRegistryIT extends HierarchicalAasRegistryTestSuite {

	private static final String ROOT_REGISTRY_URL = "http://localhost:8051";
	private static final String DELEGATED_REGISTRY_URL = "http://localhost:8052";

	private static final String REPO_BASE_URL = "http://localhost:8080";
	private static final String DELEGATED_AASID = "http://delegated-aas-registry:8080/test/aas";

	private static final DummyDescriptorFactory factory = new DummyDescriptorFactory(REPO_BASE_URL, DELEGATED_AASID);

	private static final RegistryAndDiscoveryInterfaceApi rootRegistryApi = new RegistryAndDiscoveryInterfaceApi(ROOT_REGISTRY_URL);
	private static final RegistryAndDiscoveryInterfaceApi delegatedRegistryApi = new RegistryAndDiscoveryInterfaceApi(DELEGATED_REGISTRY_URL);

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

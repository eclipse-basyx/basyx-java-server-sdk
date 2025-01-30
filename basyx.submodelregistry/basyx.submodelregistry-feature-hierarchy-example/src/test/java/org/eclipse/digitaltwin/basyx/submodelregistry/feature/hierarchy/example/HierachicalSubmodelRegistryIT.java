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

package org.eclipse.digitaltwin.basyx.submodelregistry.feature.hierarchy.example;

import org.eclipse.digitaltwin.basyx.submodelregistry.client.api.SubmodelRegistryApi;
import org.eclipse.digitaltwin.basyx.submodelregistry.feature.hierarchy.DummyDescriptorFactory;
import org.eclipse.digitaltwin.basyx.submodelregistry.feature.hierarchy.HierarchicalSubmodelRegistryTestSuite;

/**
 * HierachicalSubmodelRegistryIT
 *
 * @author mateusmolina
 *
 */
public class HierachicalSubmodelRegistryIT extends HierarchicalSubmodelRegistryTestSuite {

	private static final String ROOT_REGISTRY_URL = "http://localhost:8051";
	private static final String DELEGATED_REGISTRY_URL = "http://localhost:8052";

	private static final String REPO_BASE_URL = "http://localhost:8080";
	private static final String DELEGATED_SMID = "http://delegated-submodel-registry:8080/test/submodel";

	private static final DummyDescriptorFactory factory = new DummyDescriptorFactory(REPO_BASE_URL, DELEGATED_SMID);

	private static final SubmodelRegistryApi rootRegistryApi = new SubmodelRegistryApi(ROOT_REGISTRY_URL);
	private static final SubmodelRegistryApi delegatedRegistryApi = new SubmodelRegistryApi(DELEGATED_REGISTRY_URL);

	@Override
	protected SubmodelRegistryApi getRootRegistryApi() {
		return rootRegistryApi;
	}

	@Override
	protected SubmodelRegistryApi getDelegatedRegistryApi() {
		return delegatedRegistryApi;
	}

	@Override
	protected DummyDescriptorFactory getDescriptorFactory() {
		return factory;
	}

}

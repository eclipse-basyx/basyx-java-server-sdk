/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.testsuite.regression.aas.registration;

import org.eclipse.basyx.aas.registration.api.IAASRegistry;
import org.eclipse.basyx.aas.registration.proxy.AASRegistryProxy;

/**
 * Instantiate a concrete test class for the abstract test suite
 * 
 * @author zhangzai
 *
 */
public class RegistryProviderSuiteWithDefinedURL extends TestRegistryProviderSuite {

	public static String url;// for example: "http://localhost:4999/";

	@Override
	protected IAASRegistry getRegistryService() {
		return new AASRegistryProxy(url);
	}

	@Override
	public void testDeleteByAssetIdCall() {
		// Not within official specification
	}

	@Override
	public void testDeleteWithAssetExtension() {
		// Not within official specification
	}

}

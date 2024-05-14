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

package org.eclipse.digitaltwin.basyx.aasdiscoveryservice.core;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.SpecificAssetId;
import org.junit.Test;

/**
 * Persitency TestSuite for AasDiscovery
 * 
 * @author mateusmolina
 */
public abstract class AasDiscoveryPersistencyTestSuite {
	public final static String DUMMY_SHELL_IDENTIFIER = "DummyShellID";

	protected abstract AasDiscoveryService getAasDiscoveryService();

	protected abstract void restartComponent();

	@Test
	public void assetLinkIsPersisted() {
		AssetAdministrationShell aas = AasDiscoveryServiceSuite.getSingleDummyShell(DUMMY_SHELL_IDENTIFIER);
		AasDiscoveryServiceSuite.createAssetLink(aas, getAasDiscoveryService());

		List<SpecificAssetId> expectedAssetIDs = AasDiscoveryServiceSuite.buildSpecificAssetIds();

		restartComponent();

		List<SpecificAssetId> actualAssetIDs = getAasDiscoveryService().getAllAssetLinksById(DUMMY_SHELL_IDENTIFIER);

		assertEquals(expectedAssetIDs, actualAssetIDs);
	}

}

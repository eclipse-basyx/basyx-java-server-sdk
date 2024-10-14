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

package org.eclipse.digitaltwin.basyx.aasregistry.service.tests.integration;

import org.eclipse.digitaltwin.basyx.aasregistry.model.AssetAdministrationShellDescriptor;
import org.eclipse.digitaltwin.basyx.aasregistry.model.AssetKind;
import org.eclipse.digitaltwin.basyx.aasregistry.model.Endpoint;
import org.eclipse.digitaltwin.basyx.aasregistry.model.ProtocolInformation;
import org.eclipse.digitaltwin.basyx.aasregistry.service.storage.AasRegistryStorage;
import org.eclipse.digitaltwin.basyx.http.Base64UrlEncodedIdentifier;
import org.junit.Test;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.Assert.assertEquals;

/**
 * Persistency TestSuite for AasRegistry
 * 
 * @author mateusmolina, danish
 */
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public abstract class PersistencyTestSuite {
	protected static final String DESC_ID = "TestDescriptor";

	protected static final String BASE_URL = "http://localhost:8081";

	protected abstract AasRegistryStorage getStorage();

	protected abstract void restartComponent();

	@Test
	public void testAasDescriptorPersistency() {
		AssetAdministrationShellDescriptor expectedDescriptor = createDummyDescriptor();

		getStorage().insertAasDescriptor(expectedDescriptor);

		restartComponent();

		AssetAdministrationShellDescriptor actualDescriptor = getStorage().getAasDescriptor(DESC_ID);

		assertEquals(expectedDescriptor, actualDescriptor);
	}

	private static AssetAdministrationShellDescriptor createDummyDescriptor() {
		AssetAdministrationShellDescriptor descriptor = new AssetAdministrationShellDescriptor(DESC_ID);
		descriptor.setIdShort("DummyDescriptorIdShort");
		descriptor.setAssetKind(AssetKind.TYPE);
		descriptor.setAssetType("TestAsset");

		setEndpointItem(descriptor);
		descriptor.setGlobalAssetId("DummyGlobalAssetId");

		return descriptor;
	}

	private static void setEndpointItem(AssetAdministrationShellDescriptor descriptor) {
		ProtocolInformation protocolInformation = createProtocolInformation();

		Endpoint endpoint = new Endpoint("AAS-3.0", protocolInformation);
		descriptor.addEndpointsItem(endpoint);
	}

	private static ProtocolInformation createProtocolInformation() {
		String href = String.format("%s/%s", BASE_URL + "/shells", Base64UrlEncodedIdentifier.encodeIdentifier(PersistencyTestSuite.DESC_ID));

		ProtocolInformation protocolInformation = new ProtocolInformation(href);
		protocolInformation.endpointProtocol(getProtocol(href));

		return protocolInformation;
	}

	private static String getProtocol(String endpoint) {
		try {
			return new URL(endpoint).getProtocol();
		} catch (MalformedURLException e) {
			throw new RuntimeException();
		}
	}
}

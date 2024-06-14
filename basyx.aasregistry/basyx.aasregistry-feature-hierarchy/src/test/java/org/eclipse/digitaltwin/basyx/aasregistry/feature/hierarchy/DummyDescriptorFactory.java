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

package org.eclipse.digitaltwin.basyx.aasregistry.feature.hierarchy;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.eclipse.digitaltwin.basyx.aasregistry.client.model.AssetAdministrationShellDescriptor;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.AssetKind;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.Endpoint;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.ProtocolInformation;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.SubmodelDescriptor;
import org.eclipse.digitaltwin.basyx.http.Base64UrlEncodedIdentifier;

/**
 * DummyDescriptorFactory
 *
 * @author mateusmolina
 *
 */
public class DummyDescriptorFactory {
	static final String AASDESCRIPTOR_ID_ROOTONLY = "AASDESCRIPTOR_ID_ROOTONLY";
	static final String SMDESCRIPTOR_ID_ROOTONLY = "SMDESCRIPTOR_ID_ROOTONLY";

	static String AASDESCRIPTOR_ID_DELEGATEDONLY;
	static final String SMDESCRIPTOR_ID_DELEGATEDONLY = "SMDESCRIPTOR_ID_DELEGATEDONLY";

	static String REPO_BASE_URL;

	public DummyDescriptorFactory(String repoBaseUrl, String delegatedAasId) {
		AASDESCRIPTOR_ID_DELEGATEDONLY = delegatedAasId;
		REPO_BASE_URL = repoBaseUrl;
	}

	AssetAdministrationShellDescriptor getAasDescriptor_RootOnly() {
		return createDummyDescriptor(REPO_BASE_URL, AASDESCRIPTOR_ID_ROOTONLY, buildIdShort(AASDESCRIPTOR_ID_ROOTONLY), getSmDescriptor_RootOnly());
	}

	SubmodelDescriptor getSmDescriptor_RootOnly() {
		return createDummySubmodelDescriptor(REPO_BASE_URL, SMDESCRIPTOR_ID_ROOTONLY, buildIdShort(SMDESCRIPTOR_ID_ROOTONLY));
	}

	AssetAdministrationShellDescriptor getAasDescriptor_DelegatedOnly() {
		return createDummyDescriptor(REPO_BASE_URL, AASDESCRIPTOR_ID_DELEGATEDONLY, buildIdShort(AASDESCRIPTOR_ID_DELEGATEDONLY), getSmDescriptor_DelegatedOnly());
	}

	SubmodelDescriptor getSmDescriptor_DelegatedOnly() {
		return createDummySubmodelDescriptor(REPO_BASE_URL, SMDESCRIPTOR_ID_DELEGATEDONLY, buildIdShort(SMDESCRIPTOR_ID_DELEGATEDONLY));
	}

	private static AssetAdministrationShellDescriptor createDummyDescriptor(String baseUrl, String shellId, String shellIdShort, SubmodelDescriptor submodelDescriptor) {
		AssetAdministrationShellDescriptor descriptor = new AssetAdministrationShellDescriptor().id(shellId);
		descriptor.setIdShort(shellIdShort);
		descriptor.setAssetKind(AssetKind.TYPE);
		descriptor.setAssetType("TestAsset");

		setEndpointItem(baseUrl, shellId, descriptor);
		descriptor.setGlobalAssetId("DummyGlobalAssetId");
		descriptor.addSubmodelDescriptorsItem(submodelDescriptor);

		return descriptor;
	}

	private static SubmodelDescriptor createDummySubmodelDescriptor(String baseUrl, String submodelId, String submodelIdShort) {
		SubmodelDescriptor descriptor = new SubmodelDescriptor().id(submodelId);
		descriptor.setIdShort(submodelIdShort);
		descriptor.setEndpoints(List.of(new Endpoint()._interface("AAS-3.0").protocolInformation(createSmProtocolInformation(baseUrl, submodelId))));

		return descriptor;
	}

	private static void setEndpointItem(String baseUrl, String shellId, AssetAdministrationShellDescriptor descriptor) {
		ProtocolInformation protocolInformation = createProtocolInformation(baseUrl, shellId);

		Endpoint endpoint = new Endpoint()._interface("AAS-3.0").protocolInformation(protocolInformation);
		descriptor.addEndpointsItem(endpoint);
	}

	private static ProtocolInformation createProtocolInformation(String baseUrl, String shellId) {
		String href = String.format("%s/%s", baseUrl + "/shells", Base64UrlEncodedIdentifier.encodeIdentifier(shellId));

		ProtocolInformation protocolInformation = new ProtocolInformation().href(href);
		protocolInformation.endpointProtocol(getProtocol(href));

		return protocolInformation;
	}

	private static ProtocolInformation createSmProtocolInformation(String baseUrl, String smId) {
		String href = String.format("%s/%s", baseUrl + "/submodels", Base64UrlEncodedIdentifier.encodeIdentifier(smId));

		ProtocolInformation protocolInformation = new ProtocolInformation().href(href);
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

	private static String buildIdShort(String aasId) {
		return aasId + "IdShort";
	}
}

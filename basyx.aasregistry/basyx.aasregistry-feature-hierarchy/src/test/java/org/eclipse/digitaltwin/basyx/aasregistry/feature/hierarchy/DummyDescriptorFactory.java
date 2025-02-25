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
	static final String ROOT_ONLY_AASDESCRIPTOR_ID = "AASDESCRIPTOR_ID_ROOTONLY";
	static final String ROOT_ONLY_SMDESCRIPTOR_ID = "SMDESCRIPTOR_ID_ROOTONLY";
	static final String DELEGATED_ONLY_SMDESCRIPTOR_ID = "SMDESCRIPTOR_ID_DELEGATEDONLY";

	private static final String AAS_INTERFACE = "AAS-3.0";
	private static final String SM_INTERFACE = "SUBMODEL-3.0";

	private final String delegatedOnlyAasDescriptorId;
	private final String repoBaseUrl;

	public DummyDescriptorFactory(String repoBaseUrl, String delegatedAasId) {
		this.delegatedOnlyAasDescriptorId = delegatedAasId;
		this.repoBaseUrl = repoBaseUrl;
	}

	AssetAdministrationShellDescriptor getAasDescriptor_RootOnly() {
		return createDummyDescriptor(repoBaseUrl, ROOT_ONLY_AASDESCRIPTOR_ID, buildIdShort(ROOT_ONLY_AASDESCRIPTOR_ID), getSmDescriptor_RootOnly());
	}

	SubmodelDescriptor getSmDescriptor_RootOnly() {
		return createDummySubmodelDescriptor(repoBaseUrl, ROOT_ONLY_SMDESCRIPTOR_ID, buildIdShort(ROOT_ONLY_SMDESCRIPTOR_ID));
	}

	AssetAdministrationShellDescriptor getAasDescriptor_DelegatedOnly() {
		return createDummyDescriptor(repoBaseUrl, delegatedOnlyAasDescriptorId, buildIdShort(delegatedOnlyAasDescriptorId), getSmDescriptor_DelegatedOnly());
	}

	SubmodelDescriptor getSmDescriptor_DelegatedOnly() {
		return createDummySubmodelDescriptor(repoBaseUrl, DELEGATED_ONLY_SMDESCRIPTOR_ID, buildIdShort(DELEGATED_ONLY_SMDESCRIPTOR_ID));
	}

	String getDelegatedOnlyAasDescriptorId() {
		return delegatedOnlyAasDescriptorId;
	}

	String getRepoBaseUrl() {
		return repoBaseUrl;
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
		descriptor.setEndpoints(List.of(new Endpoint()._interface(SM_INTERFACE).protocolInformation(createSmProtocolInformation(baseUrl, submodelId))));

		return descriptor;
	}

	private static void setEndpointItem(String baseUrl, String shellId, AssetAdministrationShellDescriptor descriptor) {
		ProtocolInformation protocolInformation = createProtocolInformation(baseUrl, shellId);

		Endpoint endpoint = new Endpoint()._interface(AAS_INTERFACE).protocolInformation(protocolInformation);
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

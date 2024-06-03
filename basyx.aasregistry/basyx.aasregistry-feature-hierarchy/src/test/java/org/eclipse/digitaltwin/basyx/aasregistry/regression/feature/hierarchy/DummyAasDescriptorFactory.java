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

package org.eclipse.digitaltwin.basyx.aasregistry.regression.feature.hierarchy;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.digitaltwin.basyx.aasregistry.model.AssetAdministrationShellDescriptor;
import org.eclipse.digitaltwin.basyx.aasregistry.model.AssetKind;
import org.eclipse.digitaltwin.basyx.aasregistry.model.Endpoint;
import org.eclipse.digitaltwin.basyx.aasregistry.model.ProtocolInformation;
import org.eclipse.digitaltwin.basyx.aasregistry.model.SubmodelDescriptor;
import org.eclipse.digitaltwin.basyx.http.Base64UrlEncodedIdentifier;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * DummyAasDescriptorFactory
 *
 * @author mateusmolina
 *
 */
public class DummyAasDescriptorFactory {
	public static final String AASDESCRIPTOR_ID_HIERARCHALONLY = "AASDESCRIPTOR_ID_HIERARCHALONLY";
	public static final String AASDESCRIPTOR_ID_DELEGATEDONLY = "AASDESCRIPTOR_ID_DELEGATEDONLY";
	private final String repoBaseUrl;

	public DummyAasDescriptorFactory(String repoBaseUrl) {
		this.repoBaseUrl = repoBaseUrl;
	}

	public AssetAdministrationShellDescriptor getAasDescriptor_HierarchalOnly() {
		return createDummyDescriptor(repoBaseUrl, AASDESCRIPTOR_ID_HIERARCHALONLY, buildTestAasIdShort(AASDESCRIPTOR_ID_HIERARCHALONLY));
	}

	public AssetAdministrationShellDescriptor getAasDescriptor_DelegatedOnly() {
		return createDummyDescriptor(repoBaseUrl, AASDESCRIPTOR_ID_DELEGATEDONLY, buildTestAasIdShort(AASDESCRIPTOR_ID_DELEGATEDONLY));
	}

	public static org.eclipse.digitaltwin.basyx.aasregistry.client.model.AssetAdministrationShellDescriptor convertToClientAasDescriptor(ObjectMapper objectMapper, AssetAdministrationShellDescriptor aasRegistryDescriptor) {
		try {
			return objectMapper.convertValue(aasRegistryDescriptor, org.eclipse.digitaltwin.basyx.aasregistry.client.model.AssetAdministrationShellDescriptor.class);
		} catch (Exception e) {
			throw new RuntimeException("Conversion error", e);
		}
	}

	private static AssetAdministrationShellDescriptor createDummyDescriptor(String baseUrl, String shellId, String shellIdShort) {
		AssetAdministrationShellDescriptor descriptor = new AssetAdministrationShellDescriptor(shellId);
		descriptor.setIdShort(shellIdShort);
		descriptor.setAssetKind(AssetKind.TYPE);
		descriptor.setAssetType("TestAsset");

		setEndpointItem(baseUrl, shellId, descriptor);
		descriptor.setGlobalAssetId("DummyGlobalAssetId");
		descriptor.addSubmodelDescriptorsItem(createDummySubmodelDescriptor(baseUrl, shellId + "-SM", shellIdShort + "-SM"));

		return descriptor;
	}

	private static SubmodelDescriptor createDummySubmodelDescriptor(String baseUrl, String submodelId, String submodelIdShort) {
		SubmodelDescriptor descriptor = new SubmodelDescriptor(submodelId, new ArrayList<>());
		descriptor.setIdShort(submodelIdShort);
		descriptor.setEndpoints(List.of(new Endpoint("AAS-3.0", createSmProtocolInformation(baseUrl, submodelId))));

		return descriptor;
	}

	private static void setEndpointItem(String baseUrl, String shellId, AssetAdministrationShellDescriptor descriptor) {
		ProtocolInformation protocolInformation = createProtocolInformation(baseUrl, shellId);

		Endpoint endpoint = new Endpoint("AAS-3.0", protocolInformation);
		descriptor.addEndpointsItem(endpoint);
	}

	private static ProtocolInformation createProtocolInformation(String baseUrl, String shellId) {
		String href = String.format("%s/%s", baseUrl + "/shells", Base64UrlEncodedIdentifier.encodeIdentifier(shellId));

		ProtocolInformation protocolInformation = new ProtocolInformation(href);
		protocolInformation.endpointProtocol(getProtocol(href));

		return protocolInformation;
	}

	private static ProtocolInformation createSmProtocolInformation(String baseUrl, String smId) {
		String href = String.format("%s/%s", baseUrl + "/submodels", Base64UrlEncodedIdentifier.encodeIdentifier(smId));

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

	private static String buildTestAasIdShort(String aasId) {
		return aasId + "IdShort";
	}
}

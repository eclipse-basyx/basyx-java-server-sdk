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

package org.eclipse.digitaltwin.basyx.aasregistry.main.client.mapper;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.digitaltwin.basyx.aasregistry.client.model.AssetAdministrationShellDescriptor;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.AssetKind;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.Endpoint;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.ProtocolInformation;
import org.eclipse.digitaltwin.basyx.http.Base64UrlEncodedIdentifier;

/**
 * Factory for dummy AasDescriptors
 *
 * @author danish, mateusmolina
 *
 */
public class DummyAasDescriptorFactory {
	private static final String AAS_REPOSITORY_PATH = "/shells";

	public static AssetAdministrationShellDescriptor createDummyDescriptor(String aasId, String idShort, String globalAssetId, String... aasRepoBaseUrls) {

		AssetAdministrationShellDescriptor descriptor = new AssetAdministrationShellDescriptor();

		descriptor.setId(aasId);
		descriptor.setIdShort(idShort);
		descriptor.setAssetKind(AssetKind.INSTANCE);
		descriptor.setGlobalAssetId(globalAssetId);
		for (String eachUrl : aasRepoBaseUrls) {
			descriptor.addEndpointsItem(createEndpointItem(aasId, eachUrl));
		}
		return descriptor;
	}

	private static Endpoint createEndpointItem(String aasId, String aasRepoBaseUrl) {
		Endpoint endpoint = new Endpoint();
		endpoint.setInterface("AAS-3.0");
		endpoint.setProtocolInformation(createProtocolInformation(aasId, aasRepoBaseUrl));

		return endpoint;
	}

	private static ProtocolInformation createProtocolInformation(String aasId, String aasRepoBaseUrl) {
		String href = createHref(aasId, aasRepoBaseUrl);

		ProtocolInformation protocolInformation = new ProtocolInformation();
		protocolInformation.setHref(href);
		protocolInformation.endpointProtocol(getProtocol(href));

		return protocolInformation;
	}

	private static String createHref(String aasId, String aasRepoBaseUrl) {
		return String.format("%s/%s", createAasRepositoryUrl(aasRepoBaseUrl), Base64UrlEncodedIdentifier.encodeIdentifier(aasId));
	}

	private static String getProtocol(String endpoint) {
		try {
			return new URL(endpoint).getProtocol();
		} catch (MalformedURLException e) {
			throw new RuntimeException();
		}
	}

	private static String createAasRepositoryUrl(String aasRepositoryBaseURL) {

		try {
			return new URL(new URL(aasRepositoryBaseURL), AAS_REPOSITORY_PATH).toString();
		} catch (MalformedURLException e) {
			throw new RuntimeException("The AAS Repository Base url is malformed. " + e.getMessage());
		}
	}

}

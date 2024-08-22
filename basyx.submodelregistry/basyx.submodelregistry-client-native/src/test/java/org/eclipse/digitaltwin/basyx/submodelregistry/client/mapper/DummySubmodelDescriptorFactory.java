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

package org.eclipse.digitaltwin.basyx.submodelregistry.client.mapper;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

import org.eclipse.digitaltwin.basyx.http.Base64UrlEncodedIdentifier;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.model.Endpoint;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.model.Key;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.model.KeyTypes;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.model.ProtocolInformation;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.model.Reference;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.model.ReferenceTypes;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.model.SubmodelDescriptor;

/**
 * Factory for dummy SubmodelDescriptors
 *
 * @author mateusmolina
 *
 */
public class DummySubmodelDescriptorFactory {
	private static final String SUBMODEL_REPOSITORY_PATH = "/submodels";

	public static SubmodelDescriptor createDummyDescriptor(String smId, String idShort, String smRepoBaseUrl, Reference semanticId) {
		return createDummyDescriptor(smId, idShort, new String[] {smRepoBaseUrl}, semanticId);
	}
	
	public static SubmodelDescriptor createDummyDescriptor(String smId, String idShort, String[] smRepoBaseUrls, Reference semanticId) {
		SubmodelDescriptor descriptor = new SubmodelDescriptor();

		descriptor.setId(smId);
		descriptor.setIdShort(idShort);
		descriptor.setSemanticId(semanticId);
		for (String eachUrl : smRepoBaseUrls) {
			descriptor.addEndpointsItem(createEndpointItem(smId, eachUrl));
		}
		return descriptor;
	}

	public static Reference createSemanticId() {
		return new Reference().keys(Arrays.asList(new Key().type(KeyTypes.GLOBALREFERENCE).value("0173-1#01-AFZ615#016"))).type(ReferenceTypes.EXTERNALREFERENCE);
	}

	private static Endpoint createEndpointItem(String smId, String smRepoBaseUrl) {
		Endpoint endpoint = new Endpoint();
		endpoint.setInterface("SUBMODEL-3.0");
		endpoint.setProtocolInformation(createProtocolInformation(smId, smRepoBaseUrl));

		return endpoint;
	}

	private static ProtocolInformation createProtocolInformation(String smId, String smRepoBaseUrl) {
		String href = createHref(smId, smRepoBaseUrl);

		ProtocolInformation protocolInformation = new ProtocolInformation();
		protocolInformation.setHref(href);
		protocolInformation.endpointProtocol(getProtocol(href));

		return protocolInformation;
	}

	private static String createHref(String smId, String smRepoBaseUrl) {
		return String.format("%s/%s", createSubmodelRepositoryUrl(smRepoBaseUrl), Base64UrlEncodedIdentifier.encodeIdentifier(smId));
	}

	private static String getProtocol(String endpoint) {
		try {
			return new URL(endpoint).getProtocol();
		} catch (MalformedURLException e) {
			throw new RuntimeException();
		}
	}

	private static String createSubmodelRepositoryUrl(String smRepositoryBaseURL) {

		try {
			return new URL(new URL(smRepositoryBaseURL), SUBMODEL_REPOSITORY_PATH).toString();
		} catch (MalformedURLException e) {
			throw new RuntimeException("The Submodel Repository Base url is malformed.\n " + e.getMessage());
		}
	}
}

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

package org.eclipse.digitaltwin.basyx.submodelregistry.feature.hierarchy;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.eclipse.digitaltwin.basyx.http.Base64UrlEncodedIdentifier;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.model.Endpoint;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.model.ProtocolInformation;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.model.SubmodelDescriptor;

/**
 * DummyDescriptorFactory
 *
 * @author mateusmolina
 *
 */
public class DummyDescriptorFactory {
	static final String ROOT_ONLY_SM_ID = "SMDESCRIPTOR_ID_ROOTONLY";

	private static final String SUBMODEL_INTERFACE = "SUBMODEL-3.0";

	private final String repoBaseUrl;
	private final String delegatedOnlySmId;

	public DummyDescriptorFactory(String repoBaseUrl, String delegatedSmId) {
		this.repoBaseUrl = repoBaseUrl;
		this.delegatedOnlySmId = delegatedSmId;
	}

	SubmodelDescriptor getRootOnlySmDescriptor() {
		return createDummySubmodelDescriptor(repoBaseUrl, ROOT_ONLY_SM_ID, buildIdShort(ROOT_ONLY_SM_ID));
	}

	SubmodelDescriptor getDelegatedOnlySmDescriptor() {
		return createDummySubmodelDescriptor(repoBaseUrl, delegatedOnlySmId, buildIdShort(delegatedOnlySmId));
	}

	String getDelegatedOnlySmId() {
		return delegatedOnlySmId;
	}

	String getRepoBaseUrl() {
		return repoBaseUrl;
	}

	private static SubmodelDescriptor createDummySubmodelDescriptor(String baseUrl, String submodelId, String submodelIdShort) {
		SubmodelDescriptor descriptor = new SubmodelDescriptor().id(submodelId);
		descriptor.setIdShort(submodelIdShort);
		descriptor.setEndpoints(List.of(new Endpoint()._interface(SUBMODEL_INTERFACE).protocolInformation(createSmProtocolInformation(baseUrl, submodelId))));

		return descriptor;
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

	private static String buildIdShort(String smId) {
		return smId + "IdShort";
	}
}

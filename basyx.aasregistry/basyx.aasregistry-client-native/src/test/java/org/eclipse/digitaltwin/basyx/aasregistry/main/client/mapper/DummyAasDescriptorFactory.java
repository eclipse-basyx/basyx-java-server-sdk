/*******************************************************************************
 * Copyright (C) 2023 the Eclipse BaSyx Authors
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
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.digitaltwin.basyx.aasregistry.client.model.AdministrativeInformation;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.AssetAdministrationShellDescriptor;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.AssetKind;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.Endpoint;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.KeyTypes;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.ProtocolInformation;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.Reference;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.SpecificAssetId;
import org.eclipse.digitaltwin.basyx.core.RepositoryUrlHelper;
import org.eclipse.digitaltwin.basyx.http.Base64UrlEncodedIdentifier;

/**
 * Factory for dummy AasDescriptors
 *
 * @author danish, mateusmolina, zielstor
 *
 */
public class DummyAasDescriptorFactory {
	private static final String AAS_REPOSITORY_PATH = "/shells";

	public static AssetAdministrationShellDescriptor createDummyDescriptor(String aasId, String idShort, String globalAssetId, List<SpecificAssetId> specificAssetIds, AdministrativeInformation administrativeInformation, List<Endpoint> endpoints) {

		AssetAdministrationShellDescriptor descriptor = new AssetAdministrationShellDescriptor();

		descriptor.setId(aasId);
		descriptor.setIdShort(idShort);
		descriptor.setAssetKind(AssetKind.INSTANCE);
		descriptor.setGlobalAssetId(globalAssetId);
		descriptor.setSpecificAssetIds(specificAssetIds);
		descriptor.setEndpoints(endpoints);
		descriptor.setAdministration(administrativeInformation);

		return descriptor;
	}

	public static AssetAdministrationShellDescriptor createDummyDescriptor(String aasId, String idShort, String globalAssetId, List<SpecificAssetId> specificAssetIds, AdministrativeInformation administrativeInformation, String... aasRepoBaseUrls) {
		LinkedList<Endpoint> endpoints = new LinkedList<>();

		for (String eachUrl : aasRepoBaseUrls) {
			endpoints.add(createEndpoint(aasId, eachUrl, "AAS-3.0"));
		}

		return createDummyDescriptor(aasId, idShort, globalAssetId, specificAssetIds, administrativeInformation, endpoints);
	}

	public static AdministrativeInformation buildAdministrationInformation(String version, String revision, String templateId) {
		AdministrativeInformation administrativeInformation = new AdministrativeInformation();
		administrativeInformation.setVersion(version);
		administrativeInformation.setRevision(revision);
		administrativeInformation.setTemplateId(templateId);
		return administrativeInformation;
	}

	public static Endpoint createEndpoint(String endpointUrl, String endpointInterface) {
		Endpoint endpoint = new Endpoint();
		endpoint.setInterface(endpointInterface);
		endpoint.setProtocolInformation(createProtocolInformation(endpointUrl));

		return endpoint;
	}

	public static Endpoint createEndpoint(String aasId, String aasRepoBaseUrl, String endpointInterface) {
		String href = createHref(aasId, aasRepoBaseUrl);

		return createEndpoint(href, endpointInterface);
	}

	public static List<SpecificAssetId> buildSpecificAssetIds() {
		Reference externalSubjectId = new Reference();
		externalSubjectId.keys(Collections.singletonList(new org.eclipse.digitaltwin.basyx.aasregistry.client.model.Key().type(KeyTypes.BLOB).value("BlobValue"))).type(org.eclipse.digitaltwin.basyx.aasregistry.client.model.ReferenceTypes.EXTERNALREFERENCE);

		Reference semanticId = new Reference();
		semanticId.keys(Collections.singletonList(new org.eclipse.digitaltwin.basyx.aasregistry.client.model.Key().type(KeyTypes.BLOB).value("BlobValue"))).type(org.eclipse.digitaltwin.basyx.aasregistry.client.model.ReferenceTypes.EXTERNALREFERENCE);

		Reference supplementalSemanticId = new Reference();
		supplementalSemanticId.keys(Collections.singletonList(new org.eclipse.digitaltwin.basyx.aasregistry.client.model.Key().type(KeyTypes.BLOB).value("BlobValue"))).type(org.eclipse.digitaltwin.basyx.aasregistry.client.model.ReferenceTypes.EXTERNALREFERENCE);

		SpecificAssetId specificAssetId = new SpecificAssetId();
		specificAssetId.setName("name");
		specificAssetId.setValue("value");
		specificAssetId.setExternalSubjectId(externalSubjectId);
		specificAssetId.setSemanticId(semanticId);
		specificAssetId.setSupplementalSemanticIds(Collections.singletonList(supplementalSemanticId));

		List<SpecificAssetId> specificAssetIds = new LinkedList<>();
		specificAssetIds.add(specificAssetId);

		return specificAssetIds;
	}

	private static ProtocolInformation createProtocolInformation(String href) {
		ProtocolInformation protocolInformation = new ProtocolInformation();
		protocolInformation.setHref(href);
		protocolInformation.endpointProtocol(getProtocol(href));

		return protocolInformation;
	}

	private static String createHref(String aasId, String aasRepoBaseUrl) {
		return String.format("%s/%s", RepositoryUrlHelper.createRepositoryUrl(aasRepoBaseUrl, AAS_REPOSITORY_PATH), Base64UrlEncodedIdentifier.encodeIdentifier(aasId));
	}

	private static String getProtocol(String endpoint) {
		try {
			return new URL(endpoint).getProtocol();
		} catch (MalformedURLException e) {
			throw new RuntimeException();
		}
	}


}

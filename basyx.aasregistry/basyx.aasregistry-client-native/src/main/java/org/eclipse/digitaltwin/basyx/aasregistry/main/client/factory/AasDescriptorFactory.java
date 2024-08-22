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

package org.eclipse.digitaltwin.basyx.aasregistry.main.client.factory;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.model.AdministrativeInformation;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetInformation;
import org.eclipse.digitaltwin.aas4j.v3.model.Extension;
import org.eclipse.digitaltwin.aas4j.v3.model.LangStringNameType;
import org.eclipse.digitaltwin.aas4j.v3.model.LangStringTextType;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.AssetAdministrationShellDescriptor;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.Endpoint;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.ProtocolInformation;
import org.eclipse.digitaltwin.basyx.aasregistry.main.client.mapper.AttributeMapper;
import org.eclipse.digitaltwin.basyx.http.Base64UrlEncodedIdentifier;

/**
 * Factory for creating the {@link AssetAdministrationShellDescriptor}
 * 
 * @author danish
 */
public class AasDescriptorFactory {

	private static final String AAS_INTERFACE = "AAS-3.0";
	private static final String AAS_REPOSITORY_PATH = "shells";

	private AssetAdministrationShell shell;
	private List<String> aasRepositoryURLs;

	private AttributeMapper attributeMapper;

	public AasDescriptorFactory(AssetAdministrationShell shell, List<String> aasRepositoryBaseURLs, AttributeMapper attributeMapper) {
		this.shell = shell;
		this.aasRepositoryURLs = createAasRepositoryUrls(aasRepositoryBaseURLs);
		this.attributeMapper = attributeMapper;
	}

	/**
	 * Creates {@link AssetAdministrationShellDescriptor}
	 * 
	 * @return the created AssetAdministrationShellDescriptor
	 */
	public AssetAdministrationShellDescriptor create() {

		AssetAdministrationShellDescriptor descriptor = new AssetAdministrationShellDescriptor();

		setId(shell.getId(), descriptor);

		setIdShort(shell.getIdShort(), descriptor);

		setEndpointItem(shell.getId(), descriptor);

		setDescription(shell.getDescription(), descriptor);

		setDisplayName(shell.getDisplayName(), descriptor);

		setExtensions(shell.getExtensions(), descriptor);

		setAdministration(shell.getAdministration(), descriptor);

		setAssetKind(shell.getAssetInformation(), descriptor);

		setAssetType(shell.getAssetInformation(), descriptor);

		setGlobalAssetId(shell.getAssetInformation(), descriptor);

		return descriptor;
	}

	public AssetAdministrationShellDescriptor create(AssetAdministrationShell shell) {
		this.shell = shell;
		return create();
	}

	private void setDescription(List<LangStringTextType> descriptions, AssetAdministrationShellDescriptor descriptor) {

		if (descriptions == null || descriptions.isEmpty())
			return;

		descriptor.setDescription(attributeMapper.mapDescription(descriptions));
	}

	private void setDisplayName(List<LangStringNameType> displayNames, AssetAdministrationShellDescriptor descriptor) {

		if (displayNames == null || displayNames.isEmpty())
			return;

		descriptor.setDisplayName(attributeMapper.mapDisplayName(displayNames));
	}

	private void setExtensions(List<Extension> extensions, AssetAdministrationShellDescriptor descriptor) {

		if (extensions == null || extensions.isEmpty())
			return;

		descriptor.setExtensions(attributeMapper.mapExtensions(extensions));
	}

	private void setAdministration(AdministrativeInformation administration, AssetAdministrationShellDescriptor descriptor) {

		if (administration == null)
			return;

		descriptor.setAdministration(attributeMapper.mapAdministration(administration));
	}

	private void setAssetKind(AssetInformation assetInformation, AssetAdministrationShellDescriptor descriptor) {

		if (assetInformation == null || assetInformation.getAssetKind() == null)
			return;

		descriptor.setAssetKind(attributeMapper.mapAssetKind(assetInformation.getAssetKind()));
	}

	private void setAssetType(AssetInformation assetInformation, AssetAdministrationShellDescriptor descriptor) {

		if (assetInformation == null || assetInformation.getAssetType() == null)
			return;

		descriptor.setAssetType(assetInformation.getAssetType());
	}

	private void setGlobalAssetId(AssetInformation assetInformation, AssetAdministrationShellDescriptor descriptor) {

		if (assetInformation == null || assetInformation.getGlobalAssetId() == null)
			return;

		descriptor.setGlobalAssetId(assetInformation.getGlobalAssetId());
	}

	private void setEndpointItem(String shellId, AssetAdministrationShellDescriptor descriptor) {
		for (String eachUrl : aasRepositoryURLs) {
			Endpoint endpoint = new Endpoint();
			endpoint.setInterface(AAS_INTERFACE);
			ProtocolInformation protocolInformation = createProtocolInformation(shellId, eachUrl);
			endpoint.setProtocolInformation(protocolInformation);

			descriptor.addEndpointsItem(endpoint);
		}
	}

	private ProtocolInformation createProtocolInformation(String shellId, String url) {
		String href = String.format("%s/%s", url, Base64UrlEncodedIdentifier.encodeIdentifier(shellId));

		ProtocolInformation protocolInformation = new ProtocolInformation();
		protocolInformation.endpointProtocol(getProtocol(href));
		protocolInformation.setHref(href);

		return protocolInformation;
	}

	private void setIdShort(String idShort, AssetAdministrationShellDescriptor descriptor) {
		descriptor.setIdShort(idShort);
	}

	private void setId(String shellId, AssetAdministrationShellDescriptor descriptor) {
		descriptor.setId(shellId);
	}

	private String getProtocol(String endpoint) {
		try {
			return new URL(endpoint).getProtocol();
		} catch (MalformedURLException e) {
			throw new RuntimeException();
		}
	}

	private List<String> createAasRepositoryUrls(List<String> aasRepositoryBaseURLs) {
		List<String> toReturn = new ArrayList<>(aasRepositoryBaseURLs.size());
		for (String eachUrl : aasRepositoryBaseURLs) {
			toReturn.add(createAasRepositoryUrl(eachUrl));
		}
		return toReturn;
	}

	private String createAasRepositoryUrl(String aasRepositoryBaseURL) {

		try {
			return new URL(new URL(aasRepositoryBaseURL), AAS_REPOSITORY_PATH).toString();
		} catch (MalformedURLException e) {
			throw new RuntimeException("The AAS Repository Base url is malformed.\n" + e.getMessage());
		}
	}

}

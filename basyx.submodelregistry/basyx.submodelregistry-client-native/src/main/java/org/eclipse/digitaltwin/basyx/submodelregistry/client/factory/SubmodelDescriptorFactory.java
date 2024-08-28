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

package org.eclipse.digitaltwin.basyx.submodelregistry.client.factory;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.model.AdministrativeInformation;
import org.eclipse.digitaltwin.aas4j.v3.model.Extension;
import org.eclipse.digitaltwin.aas4j.v3.model.LangStringNameType;
import org.eclipse.digitaltwin.aas4j.v3.model.LangStringTextType;
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.basyx.http.Base64UrlEncodedIdentifier;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.mapper.AttributeMapper;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.model.Endpoint;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.model.ProtocolInformation;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.model.SubmodelDescriptor;

/**
 * Factory for creating the {@link SubmodelDescriptor}
 * 
 * @author danish
 */
public class SubmodelDescriptorFactory {

	private static final String SUBMODEL_INTERFACE = "SUBMODEL-3.0";
	private static final String SUBMODEL_REPOSITORY_PATH = "submodels";

	private Submodel submodel;
	private List<String> submodelRepositoryURLs;

	private AttributeMapper attributeMapper;

	public SubmodelDescriptorFactory(Submodel submodel, List<String> submodelRepositoryBaseURLs, AttributeMapper attributeMapper) {
		this.submodel = submodel;
		this.submodelRepositoryURLs = createSubmodelRepositoryUrls(submodelRepositoryBaseURLs);
		this.attributeMapper = attributeMapper;
	}

	/**
	 * Creates {@link SubmodelDescriptor}
	 * 
	 * @return the created {@link SubmodelDescriptor}
	 */
	public SubmodelDescriptor create() {

		SubmodelDescriptor descriptor = new SubmodelDescriptor();

		setId(submodel.getId(), descriptor);

		setIdShort(submodel.getIdShort(), descriptor);

		setEndpointItem(submodel.getId(), descriptor);

		setDescription(submodel.getDescription(), descriptor);

		setDisplayName(submodel.getDisplayName(), descriptor);

		setExtensions(submodel.getExtensions(), descriptor);

		setAdministration(submodel.getAdministration(), descriptor);

		setSemanticId(submodel.getSemanticId(), descriptor);

		setSupplementalSemanticId(submodel.getSupplementalSemanticIds(), descriptor);

		return descriptor;
	}

	public SubmodelDescriptor create(Submodel submodel) {
		this.submodel = submodel;
		return create();
	}

	private void setDescription(List<LangStringTextType> descriptions, SubmodelDescriptor descriptor) {

		if (descriptions == null || descriptions.isEmpty())
			return;

		descriptor.setDescription(attributeMapper.mapDescription(descriptions));
	}

	private void setDisplayName(List<LangStringNameType> displayNames, SubmodelDescriptor descriptor) {

		if (displayNames == null || displayNames.isEmpty())
			return;

		descriptor.setDisplayName(attributeMapper.mapDisplayName(displayNames));
	}

	private void setExtensions(List<Extension> extensions, SubmodelDescriptor descriptor) {

		if (extensions == null || extensions.isEmpty())
			return;

		descriptor.setExtensions(attributeMapper.mapExtensions(extensions));
	}

	private void setAdministration(AdministrativeInformation administration, SubmodelDescriptor descriptor) {

		if (administration == null)
			return;

		descriptor.setAdministration(attributeMapper.mapAdministration(administration));
	}

	private void setSemanticId(Reference reference, SubmodelDescriptor descriptor) {

		if (reference == null)
			return;

		descriptor.setSemanticId(attributeMapper.mapSemanticId(reference));
	}

	private void setSupplementalSemanticId(List<Reference> supplementalSemanticIds, SubmodelDescriptor descriptor) {

		if (supplementalSemanticIds == null || supplementalSemanticIds.isEmpty())
			return;

		descriptor.setSupplementalSemanticId(attributeMapper.mapSupplementalSemanticId(supplementalSemanticIds));
	}

	private void setEndpointItem(String shellId, SubmodelDescriptor descriptor) {

		for (String eachUrl : submodelRepositoryURLs) {
			Endpoint endpoint = new Endpoint();
			endpoint.setInterface(SUBMODEL_INTERFACE);
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

	private void setIdShort(String idShort, SubmodelDescriptor descriptor) {
		descriptor.setIdShort(idShort);
	}

	private void setId(String shellId, SubmodelDescriptor descriptor) {
		descriptor.setId(shellId);
	}

	private String getProtocol(String endpoint) {
		try {
			return new URL(endpoint).getProtocol();
		} catch (MalformedURLException e) {
			throw new RuntimeException();
		}
	}

	private List<String> createSubmodelRepositoryUrls(List<String> submodelRepositoryBaseURLs) {
		List<String> toReturn = new ArrayList<>(submodelRepositoryBaseURLs.size());
		for (String eachUrl : submodelRepositoryBaseURLs) {
			toReturn.add(createSubmodelRepositoryUrl(eachUrl));
		}
		return toReturn;
	}

	private String createSubmodelRepositoryUrl(String submodelRepositoryBaseURL) {

		try {
			return new URL(new URL(submodelRepositoryBaseURL), SUBMODEL_REPOSITORY_PATH).toString();
		} catch (MalformedURLException e) {
			throw new RuntimeException("The Submodel Repository Base url is malformed.\n" + e.getMessage());
		}
	}

}

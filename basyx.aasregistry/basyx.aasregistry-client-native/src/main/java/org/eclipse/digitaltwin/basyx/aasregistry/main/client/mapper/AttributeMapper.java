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

import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetInformation;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.AdministrativeInformation;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.AssetKind;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.Extension;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.LangStringNameType;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.LangStringTextType;
import org.eclipse.digitaltwin.basyx.http.CustomTypeCloneFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Maps the models defined in AAS4J to the AasRegistry client models
 * 
 * @author danish
 */
public class AttributeMapper {

	private Logger logger = LoggerFactory.getLogger(AttributeMapper.class);

	private ObjectMapper mapper;

	public AttributeMapper(ObjectMapper mapper) {
		this.mapper = mapper;
	}

	/**
	 * Maps {@link AssetAdministrationShell#getDescription()} from AAS4J to
	 * AasRegistry client
	 * 
	 * @param descriptions
	 * @return the mapped descriptions
	 */
	public List<LangStringTextType> mapDescription(List<org.eclipse.digitaltwin.aas4j.v3.model.LangStringTextType> descriptions) {
		CustomTypeCloneFactory<org.eclipse.digitaltwin.aas4j.v3.model.LangStringTextType, LangStringTextType> cloneFactory = new CustomTypeCloneFactory<>(LangStringTextType.class, mapper);

		List<LangStringTextType> mappedDescriptions = cloneFactory.create(descriptions);

		if (mappedDescriptions == null)
			logger.error("Descriptions could not be mapped due to a failure.");

		return mappedDescriptions;
	}

	/**
	 * Maps {@link AssetAdministrationShell#getDisplayName()} from AAS4J to
	 * AasRegistry client
	 * 
	 * @param displayNames
	 * @return the mapped displayNames
	 */
	public List<LangStringNameType> mapDisplayName(List<org.eclipse.digitaltwin.aas4j.v3.model.LangStringNameType> displayNames) {
		CustomTypeCloneFactory<org.eclipse.digitaltwin.aas4j.v3.model.LangStringNameType, LangStringNameType> cloneFactory = new CustomTypeCloneFactory<>(LangStringNameType.class, mapper);

		List<LangStringNameType> mappedDisplayNames = cloneFactory.create(displayNames);

		if (mappedDisplayNames == null)
			logger.error("DisplayNames could not be mapped due to a failure.");

		return mappedDisplayNames;
	}

	/**
	 * Maps {@link AssetAdministrationShell#getExtensions()} from AAS4J to
	 * AasRegistry client
	 * 
	 * @param extensions
	 * @return the mapped extensions
	 */
	public List<Extension> mapExtensions(List<org.eclipse.digitaltwin.aas4j.v3.model.Extension> extensions) {
		CustomTypeCloneFactory<org.eclipse.digitaltwin.aas4j.v3.model.Extension, Extension> cloneFactory = new CustomTypeCloneFactory<>(Extension.class, mapper);

		List<Extension> mappedExtensions = cloneFactory.create(extensions);

		if (mappedExtensions == null)
			logger.error("Extensions could not be mapped due to a failure.");

		return cloneFactory.create(extensions);
	}

	/**
	 * Maps {@link AssetAdministrationShell#getAdministration()} from AAS4J to
	 * AasRegistry client
	 * 
	 * @param administrativeInformation
	 * @return the mapped administrativeInformation
	 */
	public AdministrativeInformation mapAdministration(org.eclipse.digitaltwin.aas4j.v3.model.AdministrativeInformation administrativeInformation) {
		CustomTypeCloneFactory<org.eclipse.digitaltwin.aas4j.v3.model.AdministrativeInformation, AdministrativeInformation> cloneFactory = new CustomTypeCloneFactory<>(AdministrativeInformation.class, mapper);

		AdministrativeInformation mappedAdministrativeInformation = cloneFactory.create(administrativeInformation);

		if (mappedAdministrativeInformation == null)
			logger.error("AdministrativeInformation could not be mapped due to a failure.");

		return mappedAdministrativeInformation;
	}

	/**
	 * Maps {@link AssetInformation#getAssetKind()} from AAS4J to AasRegistry client
	 * 
	 * @param assetKind
	 * @return the mapped assetKind
	 */
	public AssetKind mapAssetKind(org.eclipse.digitaltwin.aas4j.v3.model.AssetKind assetKind) {

		return AssetKind.valueOf(AssetKind.class, assetKind.name());
	}

}

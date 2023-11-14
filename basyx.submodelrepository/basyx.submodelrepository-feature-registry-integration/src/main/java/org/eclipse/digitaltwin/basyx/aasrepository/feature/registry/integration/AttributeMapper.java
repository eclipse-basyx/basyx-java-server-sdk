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

package org.eclipse.digitaltwin.basyx.aasrepository.feature.registry.integration;

import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetInformation;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.AssetKind;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.LangStringNameType;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.LangStringTextType;

/**
 * Maps the models defined in AasRegistry client to the AAS4J models
 * 
 * @author danish
 */
public class AttributeMapper {

	/**
	 * Maps {@link AssetAdministrationShell#getDescription()} from AAS4J to
	 * AasRegistry client
	 * 
	 * @param descriptions
	 * @return the mapped descriptions
	 */
	public List<LangStringTextType> mapDescription(List<org.eclipse.digitaltwin.aas4j.v3.model.LangStringTextType> descriptions) {
		CloneFactory<org.eclipse.digitaltwin.aas4j.v3.model.LangStringTextType, LangStringTextType> cloneFactory = new CloneFactory<>(LangStringTextType.class);

		return cloneFactory.create(descriptions);
	}

	/**
	 * Maps {@link AssetAdministrationShell#getDisplayName()} from AAS4J to
	 * AasRegistry client
	 * 
	 * @param displayNames
	 * @return the mapped displayNames
	 */
	public List<LangStringNameType> mapDisplayName(List<org.eclipse.digitaltwin.aas4j.v3.model.LangStringNameType> displayNames) {
		CloneFactory<org.eclipse.digitaltwin.aas4j.v3.model.LangStringNameType, LangStringNameType> cloneFactory = new CloneFactory<>(LangStringNameType.class);

		return cloneFactory.create(displayNames);
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

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

package org.eclipse.digitaltwin.basyx.aasdiscoveryservice.backend;

import java.util.List;
import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Convert;
import org.eclipse.digitaltwin.aas4j.v3.model.SpecificAssetId;
import org.eclipse.digitaltwin.basyx.aasdiscoveryservice.core.model.AssetLink;

/**
 * Represents a document in the AAS discovery service
 */
@Entity
public class AasDiscoveryDocument {
	@Id
	@org.springframework.data.annotation.Id
	private String shellIdentifier;
	@Convert(converter = AssetLinkSetConverter.class)
	private Set<AssetLink> assetLinks;
	@Convert(converter = SpecificAssetIdListConverter.class)
	private List<SpecificAssetId> specificAssetIds;
	
	/**
	 * Constructor
	 * 
	 * @param shellIdentifier
	 *            The shell identifier
	 * @param assetLinks
	 *            The asset links
	 * @param specificAssetIds
	 *            The specific asset ids
	 */
	public AasDiscoveryDocument(String shellIdentifier, Set<AssetLink> assetLinks, List<SpecificAssetId> specificAssetIds) {
		this.shellIdentifier = shellIdentifier;
		this.assetLinks = assetLinks;
		this.specificAssetIds = specificAssetIds;
	}

	public AasDiscoveryDocument() {

	}
	
	/**
	 * Get the shell identifier
	 * 
	 * @return The shell identifier
	 */
	public String getShellIdentifier() {
		return shellIdentifier;
	}

	/**
	 * Get the asset links
	 * 
	 * @return The asset links
	 */
	public Set<AssetLink> getAssetLinks() {
		return assetLinks;
	}

	/**
	 * Get the specific asset ids
	 * 
	 * @return The specific asset ids
	 */
	public List<SpecificAssetId> getSpecificAssetIds() {
		return specificAssetIds;
	}
}

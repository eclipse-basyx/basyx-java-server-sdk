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

package org.eclipse.digitaltwin.basyx.aasdiscoveryservice.core;

import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.SpecificAssetId;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultAssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultAssetInformation;
import org.eclipse.digitaltwin.basyx.aasdiscoveryservice.core.model.AssetLink;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for {@link AasDiscoveryService}
 * 
 */
public class AasDiscoveryUtils {

	private AasDiscoveryUtils() {
		throw new IllegalStateException("Utility class");
	}

	public static List<AssetLink> deriveAssetLinksFromShell(AssetAdministrationShell shell) {
		List<SpecificAssetId> specificAssetIds = shell.getAssetInformation().getSpecificAssetIds();

		return deriveAssetLinksFromSpecificAssetIds(specificAssetIds);
	}

	public static List<AssetLink> deriveAssetLinksFromSpecificAssetIds(List<SpecificAssetId> specificAssetIds) {
		List<AssetLink> assetLinks = new ArrayList<>();

		specificAssetIds.forEach(assetId -> assetLinks.add(new AssetLink(assetId.getName(), assetId.getValue())));

		return assetLinks;
	}

	public static AssetAdministrationShell deriveShellFromIdAndSpecificAssetIds(String shellId, List<SpecificAssetId> specificAssetIds) {
		DefaultAssetInformation assetInformation = new DefaultAssetInformation.Builder().specificAssetIds(specificAssetIds).build();

		return new DefaultAssetAdministrationShell.Builder().id(shellId).assetInformation(assetInformation).build();
	}
}

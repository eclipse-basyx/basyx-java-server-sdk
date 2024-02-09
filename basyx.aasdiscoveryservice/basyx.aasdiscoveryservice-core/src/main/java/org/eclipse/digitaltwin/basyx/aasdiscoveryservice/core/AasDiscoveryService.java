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

import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.model.SpecificAssetId;
import org.eclipse.digitaltwin.basyx.aasdiscoveryservice.core.model.AssetLink;
import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;

/**
 * Specifies the overall {@link AasDiscoveryService} API
 * 
 * @author danish, zhangzai
 *
 */
public interface AasDiscoveryService {

	/**
	 * Returns a list of Asset Administration Shell ids based on asset identifiers
	 * 
	 * @param pInfo
	 * @param assetIds
	 * @return a list of all matching Aas Ids
	 */
	public CursorResult<List<String>> getAllAssetAdministrationShellIdsByAssetLink(PaginationInfo pInfo, List<AssetLink> assetIds);

	/**
	 * Returns a list of asset identifier key-value-pairs based on an Asset
	 * Administration Shell id
	 * 
	 * @param shellIdentifier
	 * @return a list of asset identifiers
	 */
	public List<SpecificAssetId> getAllAssetLinksById(String shellIdentifier);

	/**
	 * Creates new asset identifier key-value-pairs linked to an Asset
	 * Administration Shell for discoverable content. The existing content might
	 * have to be deleted first.
	 * 
	 * @param shellIdentifier
	 * @param assetIds
	 * @return a list of asset identifiers
	 */
	public List<SpecificAssetId> createAllAssetLinksById(String shellIdentifier, List<SpecificAssetId> assetIds);

	/**
	 * Deletes all asset identifier key-value-pairs linked to an Asset
	 * Administration Shell
	 * 
	 * @param shellIdentifier
	 */
	public void deleteAllAssetLinksById(String shellIdentifier);

	/**
	 * Returns the name of the service
	 * 
	 * @return service-name
	 */
	public default String getName() {
		return "aas-discovery-service";
	}

}

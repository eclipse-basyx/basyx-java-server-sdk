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

package org.eclipse.digitaltwin.basyx.aasdiscoveryservice.backend;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.digitaltwin.aas4j.v3.model.SpecificAssetId;

public class AssetIdValueMapper {

	private List<AssetIdsWithShellIdentifier> idList;

	public AssetIdValueMapper(List<AssetIdsWithShellIdentifier> idList) {
		this.idList = idList;
	}

	public Map<String, List<SpecificAssetId>> get() {
		Map<String, List<SpecificAssetId>> assetIds = new HashMap<>();
		for (AssetIdsWithShellIdentifier link : idList) {
			assetIds.put(link.getShellId(), link.getAssetIds());
		}
		return assetIds;
	}

	public static List<AssetIdsWithShellIdentifier> convert(Map<String, List<SpecificAssetId>> assetIds) {
		List<AssetIdsWithShellIdentifier> assetIdList = new ArrayList<>();
		for (Map.Entry<String, List<SpecificAssetId>> entry : assetIds.entrySet()) {
			assetIdList.add(new AssetIdsWithShellIdentifier(entry.getKey(), entry.getValue()));
		}
		return assetIdList;
	}
}

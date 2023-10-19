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

package org.eclipse.digitaltwin.basyx.aasdiscoveryservice;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.eclipse.digitaltwin.aas4j.v3.model.SpecificAssetID;
import org.eclipse.digitaltwin.basyx.aasdiscoveryservice.model.AssetLink;
import org.eclipse.digitaltwin.basyx.core.exceptions.AssetLinkDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.exceptions.CollidingAssetLinkException;
import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;

/**
 * In-memory implementation of the AasDiscoveryService
 *
 * @author zhangzai
 *
 */
public class InMemoryAasDiscoveryService implements AasDiscoveryService {

	/**
	 * A map that maps shell identifier to asset IDs
	 */
	private List<AssetLink> assetLinks= new ArrayList<>();

	@Override
	public CursorResult<List<String>> getAllAssetAdministrationShellIdsByAssetLink(PaginationInfo pInfo, List<String> assetIds) {
		Set<String> shellIds = new HashSet<String>();
		assetIds.forEach(id->{
			String shellID = getShellIdWithAssetId(id);
			shellIds.add(shellID);
		});
		
		List<String> result = new ArrayList<>();
		result.addAll(shellIds);
		return new CursorResult<List<String>>(pInfo.getCursor(), result);
	}
	
	private String getShellIdWithAssetId(String id) {
		return assetLinks.stream().filter(l->l.getSpecificAssetIDStrings().contains(id)).findFirst().map(l->l.getShellIdentifier()).get();
	}

	@Override
	public List<SpecificAssetID> getAllAssetLinksById(String shellIdentifier) {
		Optional<AssetLink> optional = assetLinks.stream().filter(a->a.getShellIdentifier().equals(shellIdentifier)).findFirst();
		if(optional.isEmpty())
			throw new AssetLinkDoesNotExistException(shellIdentifier);
		
		return optional.get().getSpecificAssetIDs();
	}

	@Override
	public List<SpecificAssetID> createAllAssetLinksById(String shellIdentifier, List<SpecificAssetID> assetIds) {
		if(assetLinkAlreadyExist(shellIdentifier, assetIds)) throw new CollidingAssetLinkException(shellIdentifier);
		
		AssetLink assetLink = getAssetsWithShellId(shellIdentifier);
		if(assetLink != null) {
			List<SpecificAssetID> assets = assetLink.getSpecificAssetIDs();
			assetIds.forEach(a->{
				if(!assets.contains(a)) assets.add(a);
			});
		}
		
		assetLinks.add(new AssetLink(shellIdentifier, assetIds));
		
		return assetIds;
	}

	private boolean assetLinkAlreadyExist(String shellIdentifier, List<SpecificAssetID> assetIds) {
		AssetLink assetLink = getAssetsWithShellId(shellIdentifier);
		if(assetLink==null)
		return false ;
		
		Optional<SpecificAssetID> optional = assetIds.stream().filter(assetId-> !assetLink.getSpecificAssetIDs().contains(assetId)).findAny();
		if(optional.isEmpty()) return true;
		
		return false;
	}
	
	private AssetLink getAssetsWithShellId(String shellIdentifier) {
		Optional<AssetLink> op = assetLinks.stream().filter(a->a.getShellIdentifier().equals(shellIdentifier)).findAny();
		
		if(op.isEmpty()) return null;
		
		return op.get();
	}

	@Override
	public void deleteAllAssetLinksById(String shellIdentifier) {
		AssetLink assetLink = getAssetsWithShellId(shellIdentifier);
		if(assetLink==null)
			throw new AssetLinkDoesNotExistException(shellIdentifier);
		
		assetLinks.remove(assetLink);
	}
}

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

package org.eclipse.digitaltwin.basyx.aasdiscoveryservice.backend.inmemory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.eclipse.digitaltwin.aas4j.v3.model.SpecificAssetID;
import org.eclipse.digitaltwin.basyx.aasdiscoveryservice.core.AasDiscoveryService;
import org.eclipse.digitaltwin.basyx.aasdiscoveryservice.core.model.AssetLink;
import org.eclipse.digitaltwin.basyx.core.exceptions.AssetLinkDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.exceptions.CollidingAssetLinkException;
import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationSupport;

/**
 * In-memory implementation of the {@link AasDiscoveryService}
 *
 * @author zhangzai
 *
 */
public class InMemoryAasDiscoveryService implements AasDiscoveryService {

	private String aasDiscoveryServiceName;

	private Map<String, AssetLink> assetLinks = new LinkedHashMap<>();

	/**
	 * Creates the {@link InMemoryAasDiscoveryService}
	 * 
	 */
	public InMemoryAasDiscoveryService() {
	}

	/**
	 * Creates the {@link InMemoryAasDiscoveryService}
	 * 
	 * @param name
	 *            of the Aas Discovery Service
	 */
	public InMemoryAasDiscoveryService(String aasDiscoveryServiceName) {
		this.aasDiscoveryServiceName = aasDiscoveryServiceName;
	}

	@Override
	public CursorResult<List<String>> getAllAssetAdministrationShellIdsByAssetLink(PaginationInfo pInfo, List<String> assetIds) {
		Set<String> shellIds = assetIds.stream()
				.map(this::getShellIdWithAssetId)
				.collect(Collectors.toSet());

		List<String> result = new ArrayList<>();
		result.addAll(shellIds);

		return paginateList(pInfo, result);
	}

	@Override
	public List<SpecificAssetID> getAllAssetLinksById(String shellIdentifier) {
		throwIfAssetLinkDoesNotExist(shellIdentifier);

		AssetLink assetLink = assetLinks.get(shellIdentifier);

		return assetLink.getSpecificAssetIDs();
	}

	@Override
	public List<SpecificAssetID> createAllAssetLinksById(String shellIdentifier, List<SpecificAssetID> assetIds) {
		throwIfAssetLinkExists(shellIdentifier);

		assetLinks.put(shellIdentifier, new AssetLink(shellIdentifier, assetIds));

		return assetIds;
	}

	@Override
	public void deleteAllAssetLinksById(String shellIdentifier) {
		throwIfAssetLinkDoesNotExist(shellIdentifier);

		assetLinks.remove(shellIdentifier);
	}

	@Override
	public String getName() {
		return aasDiscoveryServiceName == null ? AasDiscoveryService.super.getName() : aasDiscoveryServiceName;
	}

	private CursorResult<List<String>> paginateList(PaginationInfo pInfo, List<String> shellIdentifiers) {
		TreeMap<String, String> shellIdentifierMap = shellIdentifiers.stream()
				.collect(Collectors.toMap(Function.identity(), Function.identity(), (a, b) -> a, TreeMap::new));

		PaginationSupport<String> paginationSupport = new PaginationSupport<>(shellIdentifierMap, Function.identity());

		return paginationSupport.getPaged(pInfo);
	}

	private String getShellIdWithAssetId(String id) {
		return assetLinks.values()
				.stream()
				.filter(link -> link.getSpecificAssetIDStrings()
						.contains(id))
				.findFirst()
				.map(link -> link.getShellIdentifier())
				.get();
	}

	private void throwIfAssetLinkDoesNotExist(String shellIdentifier) {
		if (!assetLinks.containsKey(shellIdentifier))
			throw new AssetLinkDoesNotExistException(shellIdentifier);
	}

	private void throwIfAssetLinkExists(String shellIdentifier) {
		if (assetLinks.containsKey(shellIdentifier))
			throw new CollidingAssetLinkException(shellIdentifier);
	}

}

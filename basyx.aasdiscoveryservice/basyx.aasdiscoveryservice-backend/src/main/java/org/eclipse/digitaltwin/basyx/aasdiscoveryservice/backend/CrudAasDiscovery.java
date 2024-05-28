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

import static org.eclipse.digitaltwin.basyx.aasdiscoveryservice.core.AasDiscoveryUtils.deriveAssetLinksFromSpecificAssetIds;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.eclipse.digitaltwin.aas4j.v3.model.SpecificAssetId;
import org.eclipse.digitaltwin.basyx.aasdiscoveryservice.core.AasDiscoveryService;
import org.eclipse.digitaltwin.basyx.aasdiscoveryservice.core.model.AssetLink;
import org.eclipse.digitaltwin.basyx.core.exceptions.AssetLinkDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.exceptions.CollidingAssetLinkException;
import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationSupport;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.repository.CrudRepository;

/**
 * Default Implementation for the {@link AasDiscoveryService} based on Spring
 * {@link CrudRepository}
 * 
 * @author zielstor, fried
 *
 */
public class CrudAasDiscovery implements AasDiscoveryService {

	private AasDiscoveryBackendProvider provider;
	private String aasDiscoveryServiceName;

	/**
	 * Constructor
	 * 
	 * @param provider
	 *            The backend provider
	 */
	public CrudAasDiscovery(AasDiscoveryBackendProvider provider) {
		this.provider = provider;
	}

	/**
	 * Constructor
	 * 
	 * @param provider
	 *            The backend provider
	 * @param aasDiscoveryName
	 *            The name of the AAS discovery service
	 */
	public CrudAasDiscovery(AasDiscoveryBackendProvider provider,
			@Value("${basyx.aasdiscovery.name:aas-discovery}") String aasDiscoveryName) {
		this(provider);
		this.aasDiscoveryServiceName = aasDiscoveryName;
	}

	/**
	 * Returns a list of Asset Administration Shell ids based on asset identifier
	 * key-value-pairs
	 *
	 * @param pInfo
	 *            pagination information
	 * @param assetIds
	 *            a list of asset links
	 * @return a list of Asset Administration Shell ids
	 */
	@Override
	public CursorResult<List<String>> getAllAssetAdministrationShellIdsByAssetLink(PaginationInfo pInfo, List<AssetLink> assetIds) {
		Set<String> shellIds = getShellIdsWithAssetLinks(assetIds);

		return paginateList(pInfo, new ArrayList<>(shellIds));
	}

	/**
	 * Returns a list of asset identifier key-value-pairs based on an Asset
	 * Administration Shell id
	 *
	 * @param shellIdentifier
	 *            the shell identifier
	 * @return a list of asset identifiers
	 */
	@Override
	public List<SpecificAssetId> getAllAssetLinksById(String shellIdentifier) {
		Map<String, List<SpecificAssetId>> assetIds = getAssetIds();

		throwIfSpecificAssetIdLinkDoesNotExist(assetIds, shellIdentifier);

		return assetIds.get(shellIdentifier);
	}

	/**
	 * Creates new asset identifier key-value-pairs linked to an Asset
	 * Administration Shell for discoverable content. The existing content might
	 * have to be deleted first.
	 *
	 * @param shellIdentifier
	 *            the shell identifier
	 * @param specificAssetIds
	 *            a list of asset identifiers
	 * @return a list of asset identifiers
	 */
	@Override
	public List<SpecificAssetId> createAllAssetLinksById(String shellIdentifier,
			List<SpecificAssetId> specificAssetIds) {

		Map<String, Set<AssetLink>> assetLinks = getAssetLinks();

		synchronized (assetLinks) {
			throwIfAssetLinkExists(assetLinks, shellIdentifier);

			List<AssetLink> shellAssetLinks = deriveAssetLinksFromSpecificAssetIds(specificAssetIds);
			AasDiscoveryDocument aasDiscoveryDocument = new AasDiscoveryDocument(shellIdentifier,
					new HashSet<>(shellAssetLinks), specificAssetIds);
			provider.getCrudRepository().save(aasDiscoveryDocument);
		}

		return specificAssetIds;
	}

	/**
	 * Deletes all asset identifier key-value-pairs linked to an Asset
	 * Administration Shell
	 *
	 * @param shellIdentifier
	 *            the shell identifier
	 */
	@Override
	public void deleteAllAssetLinksById(String shellIdentifier) {
		Map<String, Set<AssetLink>> assetLinks = getAssetLinks();
		synchronized (assetLinks) {
			throwIfAssetLinkDoesNotExist(assetLinks, shellIdentifier);

			provider.getCrudRepository().deleteById(shellIdentifier);
		}
	}

	@Override
	public String getName(){
		return aasDiscoveryServiceName == null ? AasDiscoveryService.super.getName() : aasDiscoveryServiceName;
	}

	private void throwIfAssetLinkExists(Map<String, Set<AssetLink>> assetLinks, String shellIdentifier) {
		if (assetLinks.containsKey(shellIdentifier))
			throw new CollidingAssetLinkException(shellIdentifier);
	}

	private void throwIfAssetLinkDoesNotExist(Map<String, Set<AssetLink>> assetLinks, String shellIdentifier) {
		if (!assetLinks.containsKey(shellIdentifier))
			throw new AssetLinkDoesNotExistException(shellIdentifier);
	}

	private void throwIfSpecificAssetIdLinkDoesNotExist(Map<String, List<SpecificAssetId>> assetIds,
			String shellIdentifier) {
		if (!assetIds.containsKey(shellIdentifier))
			throw new AssetLinkDoesNotExistException(shellIdentifier);
	}

	private Map<String, List<SpecificAssetId>> getAssetIds() {
		Iterable<AasDiscoveryDocument> aasDiscoveryDocuments = provider.getCrudRepository().findAll();
		List<AasDiscoveryDocument> aasDiscoveryDocumentList = StreamSupport
				.stream(aasDiscoveryDocuments.spliterator(), false).collect(Collectors.toList());
		Map<String, List<SpecificAssetId>> assetIds = aasDiscoveryDocumentList.stream().collect(
				Collectors.toMap(AasDiscoveryDocument::getShellIdentifier, AasDiscoveryDocument::getSpecificAssetIds));
		return assetIds;
	}

	private Map<String, Set<AssetLink>> getAssetLinks() {
		Iterable<AasDiscoveryDocument> aasDiscoveryDocuments = provider.getCrudRepository().findAll();
		List<AasDiscoveryDocument> aasDiscoveryDocumentList = StreamSupport
				.stream(aasDiscoveryDocuments.spliterator(), false).collect(Collectors.toList());
		Map<String, Set<AssetLink>> assetLinks = aasDiscoveryDocumentList.stream()
				.collect(Collectors.toMap(AasDiscoveryDocument::getShellIdentifier, AasDiscoveryDocument::getAssetLinks,
						(a, b) -> a, TreeMap::new));
		return assetLinks;
	}

	private Set<String> getShellIdsWithAssetLinks(List<AssetLink> requestedLinks) {
		Map<String, Set<AssetLink>> assetLinks = getAssetLinks();
		return assetLinks.entrySet().stream().filter(entry -> entry.getValue().containsAll(requestedLinks))
				.map(Map.Entry::getKey).collect(Collectors.toSet());
	}

	private CursorResult<List<String>> paginateList(PaginationInfo pInfo, List<String> shellIdentifiers) {
		TreeMap<String, String> shellIdentifierMap = shellIdentifiers.stream()
				.collect(Collectors.toMap(Function.identity(), Function.identity(), (a, b) -> a, TreeMap::new));

		PaginationSupport<String> paginationSupport = new PaginationSupport<>(shellIdentifierMap, Function.identity());

		return paginationSupport.getPaged(pInfo);
	}
}

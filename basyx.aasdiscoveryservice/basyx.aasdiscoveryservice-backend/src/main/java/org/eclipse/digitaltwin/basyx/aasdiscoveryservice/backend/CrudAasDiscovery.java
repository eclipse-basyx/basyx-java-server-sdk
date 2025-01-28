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
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.eclipse.digitaltwin.aas4j.v3.model.SpecificAssetId;
import org.eclipse.digitaltwin.basyx.aasdiscoveryservice.core.AasDiscoveryService;
import org.eclipse.digitaltwin.basyx.aasdiscoveryservice.core.model.AssetLink;
import org.eclipse.digitaltwin.basyx.core.BaSyxCrudRepository;
import org.eclipse.digitaltwin.basyx.core.exceptions.AssetLinkDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.exceptions.CollidingAssetLinkException;
import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationUtilities;
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

	private BaSyxCrudRepository<AasDiscoveryDocument> aasDiscoveryBackend;
	private String aasDiscoveryServiceName;

	/**
	 * Constructor
	 * 
	 * @param provider
	 *            The backend provider
	 */
	public CrudAasDiscovery(AasDiscoveryBackendProvider provider) {
		this.aasDiscoveryBackend = provider.getCrudRepository();
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
		List<String> shellIds = getShellIdsWithAssetLinks(assetIds, pInfo);

		String cursor = PaginationUtilities.resolveCursor(pInfo, shellIds);
		
		return new CursorResult<>(cursor, shellIds);
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

		throwIfSpecificAssetIdLinkDoesNotExist(shellIdentifier);
		
		AasDiscoveryDocument aasDiscoveryDocuments = aasDiscoveryBackend.findById(shellIdentifier).orElseThrow(() -> new AssetLinkDoesNotExistException(shellIdentifier));
		
		return aasDiscoveryDocuments.getSpecificAssetIds();
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
	public List<SpecificAssetId> createAllAssetLinksById(String shellIdentifier, List<SpecificAssetId> specificAssetIds) {

		throwIfAssetLinkExists(shellIdentifier);

		List<AssetLink> shellAssetLinks = deriveAssetLinksFromSpecificAssetIds(specificAssetIds);
		AasDiscoveryDocument aasDiscoveryDocument = new AasDiscoveryDocument(shellIdentifier, new HashSet<>(shellAssetLinks), specificAssetIds);
		aasDiscoveryBackend.save(aasDiscoveryDocument);

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
		
		throwIfAssetLinkDoesNotExist(shellIdentifier);

		aasDiscoveryBackend.deleteById(shellIdentifier);
	}

	@Override
	public String getName(){
		return aasDiscoveryServiceName == null ? AasDiscoveryService.super.getName() : aasDiscoveryServiceName;
	}

	private void throwIfAssetLinkExists(String shellIdentifier) {
		if (aasDiscoveryBackend.existsById(shellIdentifier))
			throw new CollidingAssetLinkException(shellIdentifier);
	}

	private void throwIfAssetLinkDoesNotExist(String shellIdentifier) {
		
		if (!aasDiscoveryBackend.existsById(shellIdentifier))
			throw new AssetLinkDoesNotExistException(shellIdentifier);
		
	}

	private void throwIfSpecificAssetIdLinkDoesNotExist(
			String shellIdentifier) {
		if (!aasDiscoveryBackend.existsById(shellIdentifier))
			throw new AssetLinkDoesNotExistException(shellIdentifier);
	}

	private Map<String, Set<AssetLink>> getAssetLinks(Iterable<AasDiscoveryDocument> aasDiscoveryDocuments) {
		List<AasDiscoveryDocument> aasDiscoveryDocumentList = StreamSupport
				.stream(aasDiscoveryDocuments.spliterator(), false).collect(Collectors.toList());
		Map<String, Set<AssetLink>> assetLinks = aasDiscoveryDocumentList.stream()
				.collect(Collectors.toMap(AasDiscoveryDocument::getShellIdentifier, AasDiscoveryDocument::getAssetLinks,
						(a, b) -> a, TreeMap::new));
		return assetLinks;
	}

	private List<String> getShellIdsWithAssetLinks(List<AssetLink> requestedLinks, PaginationInfo pInfo) {
		
		Iterable<AasDiscoveryDocument> aasDiscoveryDocuments = aasDiscoveryBackend.findAll(pInfo, null);
		
		Map<String, Set<AssetLink>> assetLinks = getAssetLinks(aasDiscoveryDocuments);
		
		Set<String> assetLinksSet = assetLinks.entrySet().stream().filter(entry -> entry.getValue().containsAll(requestedLinks))
				.map(Map.Entry::getKey).collect(Collectors.toSet());
		
		return new ArrayList<>(assetLinksSet);
	}

}

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
package org.eclipse.digitaltwin.basyx.aasrepository.feature.discoveryintegration;

import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetInformation;
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.aas4j.v3.model.SpecificAssetId;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultAssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSpecificAssetId;
import org.eclipse.digitaltwin.basyx.aasdiscoveryservice.core.AasDiscoveryService;
import org.eclipse.digitaltwin.basyx.aasrepository.AasRepository;
import org.eclipse.digitaltwin.basyx.core.exceptions.CollidingIdentifierException;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.exceptions.RepositoryDiscoveryLinkException;
import org.eclipse.digitaltwin.basyx.core.exceptions.RepositoryDiscoveryUnlinkException;
import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Decorator for linking {@link AasRepository} with Discovery
 *
 * @author fried
 *
 */
public class DiscoveryIntegrationAasRepository implements AasRepository {
	private static Logger logger = LoggerFactory.getLogger(DiscoveryIntegrationAasRepository.class);

	private AasRepository decorated;

	private final AasDiscoveryService discoveryApi;

	public DiscoveryIntegrationAasRepository(AasRepository decorated, AasDiscoveryService discoveryApi) {
		this.decorated = decorated;
		this.discoveryApi = discoveryApi;
	}

	@Override
	public CursorResult<List<AssetAdministrationShell>> getAllAas(List<SpecificAssetId> assetIds, String idShort, PaginationInfo pInfo) {
		return decorated.getAllAas(assetIds, idShort, pInfo);
	}

	@Override
	public AssetAdministrationShell getAas(String shellId) throws ElementDoesNotExistException {
		return decorated.getAas(shellId);
	}

	@Override
	public void createAas(AssetAdministrationShell shell) throws CollidingIdentifierException {
		decorated.createAas(shell);

		createAssetLinksOnDiscoveryServiceIfNecessary(shell);
	}

	@Override
	public void updateAas(String shellId, AssetAdministrationShell shell) {
		updateAssetLinks(shellId, shell);
		decorated.updateAas(shellId, shell);
	}

	@Override
	public void deleteAas(String shellId) {
		decorated.deleteAas(shellId);

		try {
			discoveryApi.deleteAllAssetLinksById(shellId);
		} catch (Exception e){
			logger.error("Failed to unlink asset in discovery service for AAS ID {}", shellId, e);
			throw new RepositoryDiscoveryUnlinkException(shellId, e);
		}
	}

	@Override
	public String getName() {
		return decorated.getName();
	}

	@Override
	public CursorResult<List<Reference>> getSubmodelReferences(String shellId, PaginationInfo paginationInfo) {
		return decorated.getSubmodelReferences(shellId, paginationInfo);
	}

	@Override
	public void addSubmodelReference(String shellId, Reference submodelReference) {
		decorated.addSubmodelReference(shellId, submodelReference);
	}

	@Override
	public void removeSubmodelReference(String shellId, String submodelId) {
		decorated.removeSubmodelReference(shellId, submodelId);
	}

	@Override
	public void setAssetInformation(String shellId, AssetInformation shellInfo) throws ElementDoesNotExistException {
		updateAssetLinks(shellId, wrapAssetInformationInShell(shellId, shellInfo));
		decorated.setAssetInformation(shellId, shellInfo);
	}

	@Override
	public AssetInformation getAssetInformation(String shellId) throws ElementDoesNotExistException {
		return decorated.getAssetInformation(shellId);
	}

	@Override
	public File getThumbnail(String aasId) {
		return decorated.getThumbnail(aasId);
	}

	@Override
	public void setThumbnail(String aasId, String fileName, String contentType, InputStream inputStream) {
		decorated.setThumbnail(aasId, fileName, contentType, inputStream);
	}

	@Override
	public void deleteThumbnail(String aasId) {
		decorated.deleteThumbnail(aasId);
	}

	private static DefaultAssetAdministrationShell wrapAssetInformationInShell(String shellId, AssetInformation shellInfo) {
		return new DefaultAssetAdministrationShell.Builder().assetInformation(shellInfo).id(shellId).build();
	}

	private void updateAssetLinks(String shellId, AssetAdministrationShell shell) {
		AssetAdministrationShell oldShell = getAas(shellId);
		if(!isGlobalAssetIdUpdated(shell, oldShell) && !specificAssetIdsUpdated(shell, oldShell)){
			logger.info("No changes in asset links, skipping update on discovery service");
		} else {
			try {
				discoveryApi.deleteAllAssetLinksById(shellId);
			} catch (Exception ignored){
			}
			createAssetLinksOnDiscoveryServiceIfNecessary(shell);
		}
	}

	private static DefaultSpecificAssetId getGlobalAssetIdAsSpecificAssetId(AssetAdministrationShell shell) {
		if (shell.getAssetInformation() == null || shell.getAssetInformation().getGlobalAssetId() == null)
			throw new IllegalArgumentException("AssetInformation or GlobalAssetId is null");

		return new DefaultSpecificAssetId.Builder()
				.name("globalAssetId")
				.value(shell.getAssetInformation().getGlobalAssetId())
				.build();
	}

	private void createAssetLinksOnDiscoveryServiceIfNecessary(AssetAdministrationShell shell) {
		if (shell.getAssetInformation() == null) return;

		List<SpecificAssetId> linksToAdd;
		if (shell.getAssetInformation().getSpecificAssetIds() != null) {
			linksToAdd = new ArrayList<>(shell.getAssetInformation().getSpecificAssetIds());
		} else {
			linksToAdd = new ArrayList<>();
		}

		if (shell.getAssetInformation().getGlobalAssetId() != null) {
			linksToAdd.add(getGlobalAssetIdAsSpecificAssetId(shell));
		}

		if(!linksToAdd.isEmpty()) {
			try {
				discoveryApi.createAllAssetLinksById(shell.getId(), linksToAdd);
			} catch (Exception e) {
				decorated.deleteAas(shell.getId());
				logger.error("Failed to link asset in discovery service for AAS ID {}", shell.getId(), e);
				throw new RepositoryDiscoveryLinkException(shell.getId(), e);
			}
		}
	}

	private static boolean specificAssetIdsUpdated(AssetAdministrationShell shell, AssetAdministrationShell oldShell) {
		return !(oldShell.getAssetInformation() != null &&
				oldShell.getAssetInformation().getSpecificAssetIds() != null &&
				oldShell.getAssetInformation().getSpecificAssetIds().equals(
						shell.getAssetInformation().getSpecificAssetIds()));
	}

	private static boolean isGlobalAssetIdUpdated(AssetAdministrationShell shell, AssetAdministrationShell oldShell) {
		return !(oldShell.getAssetInformation() != null &&
				oldShell.getAssetInformation().getGlobalAssetId() != null &&
				oldShell.getAssetInformation().getGlobalAssetId().equals(
						shell.getAssetInformation().getGlobalAssetId()));
	}

}

/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
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

import java.io.File;
import java.io.InputStream;
import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetInformation;
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.basyx.aasregistry.client.ApiException;
import org.eclipse.digitaltwin.basyx.aasregistry.client.api.RegistryAndDiscoveryInterfaceApi;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.AssetAdministrationShellDescriptor;
import org.eclipse.digitaltwin.basyx.aasregistry.main.client.factory.AasDescriptorFactory;
import org.eclipse.digitaltwin.basyx.aasregistry.main.client.mapper.AttributeMapper;
import org.eclipse.digitaltwin.basyx.aasrepository.AasRepository;
import org.eclipse.digitaltwin.basyx.core.exceptions.CollidingIdentifierException;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.exceptions.RepositoryRegistryLinkException;
import org.eclipse.digitaltwin.basyx.core.exceptions.RepositoryRegistryUnlinkException;
import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Decorator for linking {@link AasRepository} with AasRegistry
 *
 * @author danish
 *
 */
public class RegistryIntegrationAasRepository implements AasRepository {
	private static Logger logger = LoggerFactory.getLogger(RegistryIntegrationAasRepository.class);

	private AasRepository decorated;
	
	private AasRepositoryRegistryLink aasRepositoryRegistryLink;
	private AttributeMapper attributeMapper;

	public RegistryIntegrationAasRepository(AasRepository decorated, AasRepositoryRegistryLink aasRepositoryRegistryLink, AttributeMapper attributeMapper) {
		this.decorated = decorated;
		this.aasRepositoryRegistryLink = aasRepositoryRegistryLink;
		this.attributeMapper = attributeMapper;
	}

	@Override
	public CursorResult<List<AssetAdministrationShell>> getAllAas(PaginationInfo pInfo) {
		return decorated.getAllAas(pInfo);
	}

	@Override
	public AssetAdministrationShell getAas(String shellId) throws ElementDoesNotExistException {
		return decorated.getAas(shellId);
	}

	@Override
	public void createAas(AssetAdministrationShell shell) throws CollidingIdentifierException {
		decorated.createAas(shell);

		integrateAasWithRegistry(shell, aasRepositoryRegistryLink.getAasRepositoryBaseURLs());
	}

	@Override
	public void updateAas(String shellId, AssetAdministrationShell shell) {
		decorated.updateAas(shellId, shell);
	}

	@Override
	public void deleteAas(String shellId) {
		deleteFromRegistry(shellId);
		
		decorated.deleteAas(shellId);
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
		decorated.setAssetInformation(shellId, shellInfo);
	}

	@Override
	public AssetInformation getAssetInformation(String shellId) throws ElementDoesNotExistException {
		return decorated.getAssetInformation(shellId);
	}

	private void integrateAasWithRegistry(AssetAdministrationShell shell, List<String> aasRepositoryURLs) {
		AssetAdministrationShellDescriptor descriptor = new AasDescriptorFactory(shell, aasRepositoryURLs, attributeMapper).create();

		RegistryAndDiscoveryInterfaceApi registryApi = aasRepositoryRegistryLink.getRegistryApi();

		try {
			registryApi.postAssetAdministrationShellDescriptor(descriptor);

			logger.info("Shell '{}' has been automatically linked with the Registry", shell.getId());
		} catch (ApiException e) {
			throw new RepositoryRegistryLinkException(shell.getId(), e);
		}
	}

	private void deleteFromRegistry(String shellId) {
		RegistryAndDiscoveryInterfaceApi registryApi = aasRepositoryRegistryLink.getRegistryApi();
		
		if (!shellExistsOnRegistry(shellId, registryApi)) {
			logger.error("Unable to un-link the AAS descriptor '{}' from the Registry because it does not exist on the Registry.", shellId);
			
			return;
		}

		try {
			registryApi.deleteAssetAdministrationShellDescriptorById(shellId);

			logger.info("Shell '{}' has been automatically un-linked from the Registry.", shellId);
		} catch (ApiException e) {
			throw new RepositoryRegistryUnlinkException(shellId, e);
		}
	}
	
	private boolean shellExistsOnRegistry(String shellId, RegistryAndDiscoveryInterfaceApi registryApi) {
		try {
			registryApi.getAssetAdministrationShellDescriptorById(shellId);
			
			return true;
		} catch (ApiException e) {
			return false;
		}
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

}

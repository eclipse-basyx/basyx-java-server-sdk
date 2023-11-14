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

import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetInformation;
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.basyx.aasregistry.client.ApiException;
import org.eclipse.digitaltwin.basyx.aasregistry.client.api.RegistryAndDiscoveryInterfaceApi;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.AssetAdministrationShellDescriptor;
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

	public RegistryIntegrationAasRepository(AasRepository decorated, AasRepositoryRegistryLink aasRepositoryRegistryLink) {
		this.decorated = decorated;
		this.aasRepositoryRegistryLink = aasRepositoryRegistryLink;
	}

	@Override
	public CursorResult<List<AssetAdministrationShell>> getAllAas(PaginationInfo pInfo) {
		return decorated.getAllAas(pInfo);
	}

	@Override
	public AssetAdministrationShell getAas(String aasId) throws ElementDoesNotExistException {
		return decorated.getAas(aasId);
	}

	@Override
	public void createAas(AssetAdministrationShell aas) throws CollidingIdentifierException {
		decorated.createAas(aas);
		
		integrateAasWithRegistry(aas, aasRepositoryRegistryLink.getAasRepositoryBaseURL());
	}

	@Override
	public void updateAas(String aasId, AssetAdministrationShell aas) {
		decorated.updateAas(aasId, aas);
	}

	@Override
	public void deleteAas(String aasId) {
		AssetAdministrationShell shell = decorated.getAas(aasId);
		decorated.deleteAas(aasId);

		deleteFromRegistry(shell.getId());
	}

	@Override
	public String getName() {
		return decorated.getName();
	}

	@Override
	public CursorResult<List<Reference>> getSubmodelReferences(String aasId, PaginationInfo pInfo) {
		return decorated.getSubmodelReferences(aasId, pInfo);
	}

	@Override
	public void addSubmodelReference(String aasId, Reference submodelReference) {
		decorated.addSubmodelReference(aasId, submodelReference);
	}

	@Override
	public void removeSubmodelReference(String aasId, String submodelId) {
		decorated.removeSubmodelReference(aasId, submodelId);
	}

	@Override
	public void setAssetInformation(String aasId, AssetInformation aasInfo) throws ElementDoesNotExistException {
		decorated.setAssetInformation(aasId, aasInfo);
	}

	@Override
	public AssetInformation getAssetInformation(String aasId) throws ElementDoesNotExistException {
		return decorated.getAssetInformation(aasId);
	}
	
	private void integrateAasWithRegistry(AssetAdministrationShell shell, String aasRepositoryURL) {
		AssetAdministrationShellDescriptor descriptor = new AasDescriptorFactory(shell, aasRepositoryURL).create();
		
		RegistryAndDiscoveryInterfaceApi registryApi = aasRepositoryRegistryLink.getRegistryApi();
		
		try {
			registryApi.postAssetAdministrationShellDescriptor(descriptor);
			
			logger.info("Shell {} has been automatically linked with the Registry", shell.getId());
		} catch (ApiException e) {
			e.printStackTrace();
			
			throw new RepositoryRegistryLinkException(shell.getId());
		}
	}
	
	private void deleteFromRegistry(String shellId) {
		RegistryAndDiscoveryInterfaceApi registryApi = aasRepositoryRegistryLink.getRegistryApi();
		
		try {
			registryApi.deleteAssetAdministrationShellDescriptorById(shellId);
			
			logger.info("Shell {} has been automatically de-registered from the Registry", shellId);
		} catch (ApiException e) {
			e.printStackTrace();
			
			throw new RepositoryRegistryUnlinkException(shellId);
		}
	}

}

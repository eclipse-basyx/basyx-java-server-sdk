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
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetInformation;
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.aas4j.v3.model.SpecificAssetId;
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
	private static final int SHELL_LOCK_COUNT = 64;
	private static Logger logger = LoggerFactory.getLogger(RegistryIntegrationAasRepository.class);

	private AasRepository decorated;
	
	private AasRepositoryRegistryLink aasRepositoryRegistryLink;
	private AttributeMapper attributeMapper;
	private final Object[] shellMutationLocks = new Object[SHELL_LOCK_COUNT];

	public RegistryIntegrationAasRepository(AasRepository decorated, AasRepositoryRegistryLink aasRepositoryRegistryLink, AttributeMapper attributeMapper) {
		this.decorated = decorated;
		this.aasRepositoryRegistryLink = aasRepositoryRegistryLink;
		this.attributeMapper = attributeMapper;
		Arrays.setAll(shellMutationLocks, index -> new Object());
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
		synchronized (getShellMutationLock(shell.getId())) {
			AssetAdministrationShellDescriptor descriptor = createDescriptor(shell);

			decorated.createAas(shell);

			boolean registrationSuccessful = false;

			try {
				registerAas(descriptor);
				registrationSuccessful = true;
			} finally {
				if (!registrationSuccessful)
					decorated.deleteAas(shell.getId());
			}
		}
	}

	@Override
	public void updateAas(String shellId, AssetAdministrationShell shell) {
		synchronized (getShellMutationLock(shellId)) {
			AssetAdministrationShell previousShell = decorated.getAas(shellId);

			decorated.updateAas(shellId, shell);

			try {
				updateDescriptor(shellId, shell);
			} catch (RuntimeException e) {
				rollbackAasUpdate(shellId, previousShell, e);
				throw e;
			}
		}
	}

	@Override
	public void deleteAas(String shellId) {
		synchronized (getShellMutationLock(shellId)) {
			deleteFromRegistry(shellId);
			decorated.deleteAas(shellId);
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
		synchronized (getShellMutationLock(shellId)) {
			decorated.addSubmodelReference(shellId, submodelReference);
		}
	}

	@Override
	public void removeSubmodelReference(String shellId, String submodelId) {
		synchronized (getShellMutationLock(shellId)) {
			decorated.removeSubmodelReference(shellId, submodelId);
		}
	}

	@Override
	public void setAssetInformation(String shellId, AssetInformation shellInfo) throws ElementDoesNotExistException {
		synchronized (getShellMutationLock(shellId)) {
			AssetInformation previousAssetInformation = decorated.getAssetInformation(shellId);

			decorated.setAssetInformation(shellId, shellInfo);

			try {
				updateDescriptor(shellId, decorated.getAas(shellId));
			} catch (RuntimeException e) {
				rollbackAssetInformationUpdate(shellId, previousAssetInformation, e);
				throw e;
			}
		}
	}

	@Override
	public AssetInformation getAssetInformation(String shellId) throws ElementDoesNotExistException {
		return decorated.getAssetInformation(shellId);
	}

	private void registerAas(AssetAdministrationShellDescriptor descriptor) {
		RegistryAndDiscoveryInterfaceApi registryApi = aasRepositoryRegistryLink.getRegistryApi();

		try {
			registryApi.postAssetAdministrationShellDescriptor(descriptor);

			logger.info("Shell '{}' has been automatically linked with the Registry", descriptor.getId());
		} catch (ApiException e) {
			throw createRegistryLinkException(descriptor.getId(), "creation", e);
		}
	}

	private void updateDescriptor(String shellId, AssetAdministrationShell shell) {
		RegistryAndDiscoveryInterfaceApi registryApi = aasRepositoryRegistryLink.getRegistryApi();
		AssetAdministrationShellDescriptor existingDescriptor;

		try {
			existingDescriptor = registryApi.getAssetAdministrationShellDescriptorById(shellId);
		} catch (ApiException e) {
			throw createRegistryLinkException(shellId, "update", e);
		}

		AssetAdministrationShellDescriptor updatedDescriptor = createDescriptor(shell);
		preserveRegistryManagedAttributes(existingDescriptor, updatedDescriptor);

		try {
			registryApi.putAssetAdministrationShellDescriptorById(shellId, updatedDescriptor);

			logger.info("Shell descriptor '{}' has been automatically updated in the Registry", shellId);
		} catch (ApiException e) {
			if (descriptorMatchesAasDerivedState(shellId, updatedDescriptor, registryApi, e)) {
				logger.warn("Registry update for shell descriptor '{}' returned an error, but a read-back confirmed the requested AAS-derived state.", shellId);
				return;
			}

			throw createRegistryLinkException(shellId, "update", e);
		}
	}

	private void preserveRegistryManagedAttributes(AssetAdministrationShellDescriptor existingDescriptor, AssetAdministrationShellDescriptor updatedDescriptor) {
		if (existingDescriptor.getEndpoints() != null)
			updatedDescriptor.setEndpoints(existingDescriptor.getEndpoints());

		updatedDescriptor.setSubmodelDescriptors(existingDescriptor.getSubmodelDescriptors());
	}

	private boolean descriptorMatchesAasDerivedState(String shellId, AssetAdministrationShellDescriptor expectedDescriptor, RegistryAndDiscoveryInterfaceApi registryApi, ApiException updateException) {
		try {
			AssetAdministrationShellDescriptor actualDescriptor = registryApi.getAssetAdministrationShellDescriptorById(shellId);
			return aasDerivedAttributesEqual(expectedDescriptor, actualDescriptor);
		} catch (ApiException verificationException) {
			updateException.addSuppressed(verificationException);
			return false;
		}
	}

	private boolean aasDerivedAttributesEqual(AssetAdministrationShellDescriptor expected, AssetAdministrationShellDescriptor actual) {
		return actual != null
				&& Objects.equals(expected.getAdministration(), actual.getAdministration())
				&& Objects.equals(expected.getAssetKind(), actual.getAssetKind())
				&& Objects.equals(expected.getAssetType(), actual.getAssetType())
				&& Objects.equals(expected.getDescription(), actual.getDescription())
				&& Objects.equals(expected.getDisplayName(), actual.getDisplayName())
				&& Objects.equals(expected.getExtensions(), actual.getExtensions())
				&& Objects.equals(expected.getGlobalAssetId(), actual.getGlobalAssetId())
				&& Objects.equals(expected.getId(), actual.getId())
				&& Objects.equals(expected.getIdShort(), actual.getIdShort())
				&& Objects.equals(expected.getSpecificAssetIds(), actual.getSpecificAssetIds());
	}

	private AssetAdministrationShellDescriptor createDescriptor(AssetAdministrationShell shell) {
		return new AasDescriptorFactory(aasRepositoryRegistryLink.getAasRepositoryBaseURLs(), attributeMapper).create(shell);
	}

	private RepositoryRegistryLinkException createRegistryLinkException(String shellId, String operation, ApiException cause) {
		return new RepositoryRegistryLinkException(shellId, createRegistryFailureDetails(operation, cause), cause);
	}

	private RepositoryRegistryUnlinkException createRegistryUnlinkException(String shellId, ApiException cause) {
		return new RepositoryRegistryUnlinkException(shellId, createRegistryFailureDetails("deletion", cause), cause);
	}

	private String createRegistryFailureDetails(String operation, ApiException cause) {
		StringBuilder details = new StringBuilder("Registry descriptor ").append(operation).append(" failed");

		if (cause.getCode() > 0)
			details.append(" with HTTP status ").append(cause.getCode());

		String responseDetails = cause.getResponseBody();
		if (responseDetails == null || responseDetails.isBlank())
			responseDetails = cause.getMessage();

		if (responseDetails != null && !responseDetails.isBlank())
			details.append(": ").append(responseDetails);

		return details.toString();
	}

	private void rollbackAasUpdate(String shellId, AssetAdministrationShell previousShell, RuntimeException updateException) {
		try {
			decorated.updateAas(shellId, previousShell);
		} catch (RuntimeException rollbackException) {
			updateException.addSuppressed(rollbackException);
			logger.error("Unable to restore AAS '{}' after Registry synchronization failed.", shellId, rollbackException);
		}
	}

	private void rollbackAssetInformationUpdate(String shellId, AssetInformation previousAssetInformation, RuntimeException updateException) {
		try {
			decorated.setAssetInformation(shellId, previousAssetInformation);
		} catch (RuntimeException rollbackException) {
			updateException.addSuppressed(rollbackException);
			logger.error("Unable to restore Asset Information for AAS '{}' after Registry synchronization failed.", shellId, rollbackException);
		}
	}

	private void deleteFromRegistry(String shellId) {
		RegistryAndDiscoveryInterfaceApi registryApi = aasRepositoryRegistryLink.getRegistryApi();

		try {
			registryApi.deleteAssetAdministrationShellDescriptorById(shellId);

			logger.info("Shell '{}' has been automatically un-linked from the Registry.", shellId);
		} catch (ApiException e) {
			if (e.getCode() == 404) {
				logger.info("Shell descriptor '{}' was already absent from the Registry.", shellId);
				return;
			}

			throw createRegistryUnlinkException(shellId, e);
		}
	}

	private Object getShellMutationLock(String shellId) {
		return shellMutationLocks[Math.floorMod(Objects.hashCode(shellId), shellMutationLocks.length)];
	}

	@Override
	public File getThumbnail(String aasId) {
		return decorated.getThumbnail(aasId);
	}

	@Override
	public InputStream getThumbnailInputStream(String aasId) {
		return decorated.getThumbnailInputStream(aasId);
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

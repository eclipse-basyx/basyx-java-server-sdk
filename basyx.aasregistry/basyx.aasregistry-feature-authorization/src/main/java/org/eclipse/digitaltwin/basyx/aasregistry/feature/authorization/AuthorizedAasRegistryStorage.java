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

package org.eclipse.digitaltwin.basyx.aasregistry.feature.authorization;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.eclipse.digitaltwin.basyx.aasregistry.feature.authorization.rbac.AasRegistryTargetPermissionVerifier;
import org.eclipse.digitaltwin.basyx.aasregistry.model.AssetAdministrationShellDescriptor;
import org.eclipse.digitaltwin.basyx.aasregistry.model.ShellDescriptorSearchRequest;
import org.eclipse.digitaltwin.basyx.aasregistry.model.ShellDescriptorSearchResponse;
import org.eclipse.digitaltwin.basyx.aasregistry.model.SubmodelDescriptor;
import org.eclipse.digitaltwin.basyx.aasregistry.service.errors.AasDescriptorAlreadyExistsException;
import org.eclipse.digitaltwin.basyx.aasregistry.service.errors.AasDescriptorNotFoundException;
import org.eclipse.digitaltwin.basyx.aasregistry.service.errors.SubmodelAlreadyExistsException;
import org.eclipse.digitaltwin.basyx.aasregistry.service.errors.SubmodelNotFoundException;
import org.eclipse.digitaltwin.basyx.aasregistry.service.storage.AasRegistryStorage;
import org.eclipse.digitaltwin.basyx.aasregistry.service.storage.DescriptorFilter;
import org.eclipse.digitaltwin.basyx.authorization.rbac.Action;
import org.eclipse.digitaltwin.basyx.authorization.rbac.RbacPermissionResolver;
import org.eclipse.digitaltwin.basyx.authorization.rbac.TargetInformation;
import org.eclipse.digitaltwin.basyx.core.exceptions.InsufficientPermissionException;
import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Decorator for authorized {@link AasRegistryStorage}
 *
 * @author geso02, danish
 */
public class AuthorizedAasRegistryStorage implements AasRegistryStorage {

	private static final Logger LOGGER = LoggerFactory.getLogger(AuthorizedAasRegistryStorage.class);
	private AasRegistryStorage decorated;
	private RbacPermissionResolver<AasRegistryTargetInformation> permissionResolver;
	
	public AuthorizedAasRegistryStorage(AasRegistryStorage decorated, RbacPermissionResolver<AasRegistryTargetInformation> permissionResolver) {
		this.decorated = decorated;
		this.permissionResolver = permissionResolver;
	}

	@Override
	public CursorResult<List<AssetAdministrationShellDescriptor>> getAllAasDescriptors(PaginationInfo pRequest, DescriptorFilter filter) {
		boolean isAuthorized = permissionResolver.hasPermission(Action.READ, new AasRegistryTargetInformation(getIdAsList(AasRegistryTargetPermissionVerifier.ALL_ALLOWED_WILDCARD)));

		if (isAuthorized)
			return decorated.getAllAasDescriptors(pRequest, filter);
		
		List<TargetInformation> targetInformations = permissionResolver.getMatchingTargetInformationInRules(Action.READ, new AasRegistryTargetInformation(getIdAsList(AasRegistryTargetPermissionVerifier.ALL_ALLOWED_WILDCARD)));
		
		List<String> allIds = targetInformations.stream().map(AasRegistryTargetInformation.class::cast)
				.map(AasRegistryTargetInformation::getAasIds).flatMap(List::stream).collect(Collectors.toList());
		
		List<AssetAdministrationShellDescriptor> aasDescriptors = allIds.stream().map(id -> {
			try {
				return getAasDescriptor(id);
			} catch (AasDescriptorNotFoundException e) {
				LOGGER.error("AAS Descriptor: '{}' not found, Error: {}", id, e.getMessage());
				return null;
			} catch (Exception e) {
				LOGGER.error("Exception occurred while retrieving the AAS Descriptor: {}, Error: {}", id, e.getMessage());
				return null;
			}
		}).filter(Objects::nonNull).collect(Collectors.toList());
		
		TreeMap<String, AssetAdministrationShellDescriptor> aasMap = aasDescriptors.stream().collect(Collectors.toMap(AssetAdministrationShellDescriptor::getId, aas -> aas, (a, b) -> a, TreeMap::new));

		PaginationSupport<AssetAdministrationShellDescriptor> paginationSupport = new PaginationSupport<>(aasMap, AssetAdministrationShellDescriptor::getId);

		return paginationSupport.getPaged(pRequest);
	}

	@Override
	public AssetAdministrationShellDescriptor getAasDescriptor(String aasDescriptorId) throws AasDescriptorNotFoundException {
		assertHasPermission(Action.READ, getIdAsList(aasDescriptorId));
		return decorated.getAasDescriptor(aasDescriptorId);
	}

	@Override
	public void insertAasDescriptor(AssetAdministrationShellDescriptor descr) throws AasDescriptorAlreadyExistsException {
		assertHasPermission(Action.CREATE, getIdAsList(descr.getId()));
		decorated.insertAasDescriptor(descr);
	}

	@Override
	public void replaceAasDescriptor(String aasDescriptorId, AssetAdministrationShellDescriptor descriptor) throws AasDescriptorNotFoundException {
		String newId = descriptor.getId();

		if (!aasDescriptorId.equals(newId)) {
			assertHasPermission(Action.DELETE, getIdAsList(aasDescriptorId));
			assertHasPermission(Action.CREATE, getIdAsList(newId));
		} else
			assertHasPermission(Action.UPDATE, getIdAsList(aasDescriptorId));

		decorated.replaceAasDescriptor(aasDescriptorId, descriptor);
	}

	@Override
	public void removeAasDescriptor(String aasDescriptorId) throws AasDescriptorNotFoundException {
		assertHasPermission(Action.DELETE, getIdAsList(aasDescriptorId));
		decorated.removeAasDescriptor(aasDescriptorId);
	}

	@Override
	public CursorResult<List<SubmodelDescriptor>> getAllSubmodels(String aasDescriptorId, PaginationInfo pRequest) throws AasDescriptorNotFoundException {
		assertHasPermission(Action.READ, getIdAsList(aasDescriptorId));
		return decorated.getAllSubmodels(aasDescriptorId, pRequest);
	}

	@Override
	public SubmodelDescriptor getSubmodel(String aasDescriptorId, String submodelId) throws AasDescriptorNotFoundException, SubmodelNotFoundException {
		assertHasPermission(Action.READ, getIdAsList(aasDescriptorId));
		return decorated.getSubmodel(aasDescriptorId, submodelId);
	}

	@Override
	public void insertSubmodel(String aasDescriptorId, SubmodelDescriptor submodel) throws AasDescriptorNotFoundException, SubmodelAlreadyExistsException {
		assertHasPermission(Action.UPDATE, getIdAsList(aasDescriptorId));
		decorated.insertSubmodel(aasDescriptorId, submodel);
	}

	@Override
	public void replaceSubmodel(String aasDescriptorId, String submodelId, SubmodelDescriptor submodel) throws AasDescriptorNotFoundException, SubmodelNotFoundException {
		assertHasPermission(Action.UPDATE, getIdAsList(aasDescriptorId));
		decorated.replaceSubmodel(aasDescriptorId, submodelId, submodel);
	}

	@Override
	public void removeSubmodel(String aasDescriptorId, String submodelId) throws AasDescriptorNotFoundException, SubmodelNotFoundException {
		assertHasPermission(Action.UPDATE, getIdAsList(aasDescriptorId));
		decorated.removeSubmodel(aasDescriptorId, submodelId);
	}

	@Override
	public Set<String> clear() {
		assertHasPermission(Action.DELETE, getIdAsList(AasRegistryTargetPermissionVerifier.ALL_ALLOWED_WILDCARD));
		return decorated.clear();
	}

	@Override
	public ShellDescriptorSearchResponse searchAasDescriptors(ShellDescriptorSearchRequest request) {
		assertHasPermission(Action.READ, getIdAsList(AasRegistryTargetPermissionVerifier.ALL_ALLOWED_WILDCARD));
		return decorated.searchAasDescriptors(request);
	}
	
	private void assertHasPermission(Action action, List<String> assIds) {
		boolean isAuthorized = permissionResolver.hasPermission(action, new AasRegistryTargetInformation(assIds));
		throwExceptionIfInsufficientPermission(isAuthorized);
	}
	
	private void throwExceptionIfInsufficientPermission(boolean isAuthorized) {
		if (!isAuthorized)
			throw new InsufficientPermissionException("Insufficient Permission: The current subject does not have the required permissions for this operation.");
	}

	private List<String> getIdAsList(String id) {
		return new ArrayList<>(Arrays.asList(id));
	}
}

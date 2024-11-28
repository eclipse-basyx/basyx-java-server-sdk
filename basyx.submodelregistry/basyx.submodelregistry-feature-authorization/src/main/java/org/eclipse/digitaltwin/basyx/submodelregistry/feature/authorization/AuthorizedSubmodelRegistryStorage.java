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

package org.eclipse.digitaltwin.basyx.submodelregistry.feature.authorization;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.eclipse.digitaltwin.basyx.authorization.rbac.Action;
import org.eclipse.digitaltwin.basyx.authorization.rbac.RbacPermissionResolver;
import org.eclipse.digitaltwin.basyx.core.exceptions.InsufficientPermissionException;
import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.eclipse.digitaltwin.basyx.submodelregistry.feature.authorization.rbac.SubmodelRegistryTargetPermissionVerifier;
import org.eclipse.digitaltwin.basyx.submodelregistry.model.SubmodelDescriptor;
import org.eclipse.digitaltwin.basyx.submodelregistry.service.errors.SubmodelAlreadyExistsException;
import org.eclipse.digitaltwin.basyx.submodelregistry.service.errors.SubmodelNotFoundException;
import org.eclipse.digitaltwin.basyx.submodelregistry.service.storage.SubmodelRegistryStorage;

/**
 * Decorator for authorized {@link SubmodelRegistryStorage}
 *
 * @author danish
 */
public class AuthorizedSubmodelRegistryStorage implements SubmodelRegistryStorage {

	private SubmodelRegistryStorage decorated;
	private RbacPermissionResolver<SubmodelRegistryTargetInformation> permissionResolver;
	
	public AuthorizedSubmodelRegistryStorage(SubmodelRegistryStorage decorated, RbacPermissionResolver<SubmodelRegistryTargetInformation> permissionResolver) {
		this.decorated = decorated;
		this.permissionResolver = permissionResolver;
	}

	@Override
	public CursorResult<List<SubmodelDescriptor>> getAllSubmodelDescriptors(PaginationInfo pRequest) {
		assertHasPermission(Action.READ, getIdAsList(SubmodelRegistryTargetPermissionVerifier.ALL_ALLOWED_WILDCARD));
		return decorated.getAllSubmodelDescriptors(pRequest);
	}

	@Override
	public SubmodelDescriptor getSubmodelDescriptor(String submodelId) throws SubmodelNotFoundException {
		assertHasPermission(Action.READ, getIdAsList(submodelId));
		return decorated.getSubmodelDescriptor(submodelId);
	}

	@Override
	public void removeSubmodelDescriptor(String submodelId) throws SubmodelNotFoundException {
		assertHasPermission(Action.DELETE, getIdAsList(submodelId));
		decorated.removeSubmodelDescriptor(submodelId);
	}

	public void replaceSubmodelDescriptor(String submodelId, SubmodelDescriptor descriptor) throws SubmodelNotFoundException {
		String newId = descriptor.getId();

		if (!submodelId.equals(newId)) {
			assertHasPermission(Action.DELETE, getIdAsList(submodelId));
			assertHasPermission(Action.CREATE, getIdAsList(newId));
		}
		else
			assertHasPermission(Action.UPDATE, getIdAsList(submodelId));

		decorated.replaceSubmodelDescriptor(submodelId, descriptor);
	}

	@Override
	public void insertSubmodelDescriptor(SubmodelDescriptor descriptor) throws SubmodelAlreadyExistsException {
		assertHasPermission(Action.CREATE, getIdAsList(descriptor.getId()));
		decorated.insertSubmodelDescriptor(descriptor);
	}

	@Override
	public Set<String> clear() {
		assertHasPermission(Action.DELETE, getIdAsList(SubmodelRegistryTargetPermissionVerifier.ALL_ALLOWED_WILDCARD));
		return decorated.clear();
	}
	
	private void assertHasPermission(Action action, List<String> submodelIds) {
		boolean isAuthorized = permissionResolver.hasPermission(action, new SubmodelRegistryTargetInformation(submodelIds));
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

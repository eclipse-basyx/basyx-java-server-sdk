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

package org.eclipse.digitaltwin.basyx.aasdiscoveryservice.feature.authorization;

import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.model.SpecificAssetId;
import org.eclipse.digitaltwin.basyx.aasdiscoveryservice.core.AasDiscoveryService;
import org.eclipse.digitaltwin.basyx.aasdiscoveryservice.core.model.AssetLink;
import org.eclipse.digitaltwin.basyx.authorization.rbac.Action;
import org.eclipse.digitaltwin.basyx.authorization.rbac.RbacPermissionResolver;
import org.eclipse.digitaltwin.basyx.core.exceptions.InsufficientPermissionException;
import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;

/**
 * Authorized version of {@link AasDiscoveryService}
 *
 * @author mateusmolina
 *
 */
public class AuthorizedAasDiscoveryService implements AasDiscoveryService {

	private final AasDiscoveryService decorated;
	private final RbacPermissionResolver<AasDiscoveryServiceTargetInformation> permissionResolver;

	public AuthorizedAasDiscoveryService(AasDiscoveryService decorated, RbacPermissionResolver<AasDiscoveryServiceTargetInformation> permissionResolver) {
		this.decorated = decorated;
		this.permissionResolver = permissionResolver;
	}

	@Override
	public CursorResult<List<String>> getAllAssetAdministrationShellIdsByAssetLink(PaginationInfo pInfo, List<AssetLink> assetIds) {
		boolean isAuthorized = permissionResolver.hasPermission(Action.READ, new AasDiscoveryServiceTargetInformation(assetIds, null));

		throwExceptionIfInsufficientPermission(isAuthorized);

		return decorated.getAllAssetAdministrationShellIdsByAssetLink(pInfo, assetIds);
	}

	@Override
	public List<SpecificAssetId> getAllAssetLinksById(String shellIdentifier) {
		boolean isAuthorized = permissionResolver.hasPermission(Action.READ, new AasDiscoveryServiceTargetInformation(null, List.of(shellIdentifier)));

		throwExceptionIfInsufficientPermission(isAuthorized);

		return decorated.getAllAssetLinksById(shellIdentifier);
	}

	@Override
	public List<SpecificAssetId> createAllAssetLinksById(String shellIdentifier, List<SpecificAssetId> assetIds) {
		boolean isAuthorized = permissionResolver.hasPermission(Action.CREATE, new AasDiscoveryServiceTargetInformation(null, List.of(shellIdentifier)));

		throwExceptionIfInsufficientPermission(isAuthorized);

		return decorated.createAllAssetLinksById(shellIdentifier, assetIds);
	}

	@Override
	public void deleteAllAssetLinksById(String shellIdentifier) {
		boolean isAuthorized = permissionResolver.hasPermission(Action.DELETE, new AasDiscoveryServiceTargetInformation(null, List.of(shellIdentifier)));

		throwExceptionIfInsufficientPermission(isAuthorized);

		decorated.deleteAllAssetLinksById(shellIdentifier);
	}

	private void throwExceptionIfInsufficientPermission(boolean isAuthorized) {
		if (!isAuthorized)
			throw new InsufficientPermissionException("Insufficient Permission: The current subject does not have the required permissions for this operation.");
	}

}

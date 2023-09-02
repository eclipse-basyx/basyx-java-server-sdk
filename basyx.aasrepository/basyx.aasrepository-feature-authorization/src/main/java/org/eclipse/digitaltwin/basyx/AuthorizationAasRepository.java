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
package org.eclipse.digitaltwin.basyx;

import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetInformation;
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.basyx.aasrepository.AasRepository;
import org.eclipse.digitaltwin.basyx.core.exceptions.CollidingIdentifierException;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.exceptions.NotAuthorizedException;
import org.eclipse.digitaltwin.basyx.Security;
import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Observer for the AASAggregator that triggers authorization for different
 * operations on the aggregator.
 *
 * @author wege
 *
 */
public class AuthorizationAasRepository implements AasRepository {
	private static Logger logger = LoggerFactory.getLogger(AuthorizationAasRepository.class);

	private AasRepository decorated;
	private PermissionResolver permissionResolver;

	private Security security;

	public AuthorizationAasRepository(AasRepository decorated, PermissionResolver permissionResolver, Security security) {
		this.decorated = decorated;
		this.permissionResolver = permissionResolver;
		this.security = security;
	}

	@Override
	public CursorResult<List<AssetAdministrationShell>> getAllAas(PaginationInfo pInfo) {
		return decorated.getAllAas(pInfo).getResult().stream().filter(aas -> permissionResolver.hasPermission(decorated, aas.getId(), Action.READ)).collect(Collectors.toList());
	}

	@Override
	public AssetAdministrationShell getAas(String aasId) throws ElementDoesNotExistException {
		if (!permissionResolver.hasPermission(decorated, aasId, Action.READ)) {
			throw new NotAuthorizedException();
		}
		return decorated.getAas(aasId);
	}

	@Override
	public void createAas(AssetAdministrationShell aas) throws CollidingIdentifierException {
		if (!permissionResolver.hasPermission(decorated, aas.getId(), Action.WRITE)) {
			throw new NotAuthorizedException();
		}
		decorated.createAas(aas);
	}

	@Override
	public void updateAas(String aasId, AssetAdministrationShell aas) {
		if (!permissionResolver.hasPermission(decorated, aas.getId(), Action.WRITE)) {
			throw new NotAuthorizedException();
		}
		decorated.updateAas(aasId, aas);
	}

	@Override
	public CursorResult<List<Reference>> getSubmodelReferences(String aasId, PaginationInfo pInfo) {
		return null;
	}

	@Override
	public void deleteAas(String aasId) {
		if (!permissionResolver.hasPermission(decorated, aasId, Action.WRITE)) {
			throw new NotAuthorizedException();
		}
		decorated.deleteAas(aasId);
	}

	@Override
	public String getName() {
		return decorated.getName();
	}

	@Override
	public List<Reference> getSubmodelReferences(String aasId) {
		if (!permissionResolver.hasPermission(decorated, aasId, Action.READ)) {
			throw new NotAuthorizedException();
		}
		return decorated.getSubmodelReferences(aasId).stream().filter(submodel -> permissionResolver.hasSubmodelPermission(this, aasId, submodel.getKeys().get(0).getValue(), Action.READ)).collect(Collectors.toList());
	}

	@Override
	public void addSubmodelReference(String aasId, Reference submodelReference) {
		if (!permissionResolver.hasSubmodelPermission(decorated, aasId, submodelReference.getKeys().get(0).getValue(), Action.WRITE)) {
			throw new NotAuthorizedException();
		}
		decorated.addSubmodelReference(aasId, submodelReference);
	}

	@Override
	public void removeSubmodelReference(String aasId, String submodelId) {
		if (!permissionResolver.hasSubmodelPermission(decorated, aasId, submodelId, Action.WRITE)) {
			throw new NotAuthorizedException();
		}
		decorated.removeSubmodelReference(aasId, submodelId);
	}

	@Override
	public void setAssetInformation(String aasId, AssetInformation aasInfo) throws ElementDoesNotExistException {
		if (!permissionResolver.hasPermission(decorated, aasInfo.getGlobalAssetID(), Action.WRITE)) {
			throw new NotAuthorizedException();
		}
		decorated.setAssetInformation(aasId, aasInfo);
	}
	
	@Override
	public AssetInformation getAssetInformation(String aasId) throws ElementDoesNotExistException {
		if (!permissionResolver.hasPermission(decorated, aasId, Action.READ)) {
			throw new NotAuthorizedException();
		}
		return decorated.getAssetInformation(aasId);
	}

}

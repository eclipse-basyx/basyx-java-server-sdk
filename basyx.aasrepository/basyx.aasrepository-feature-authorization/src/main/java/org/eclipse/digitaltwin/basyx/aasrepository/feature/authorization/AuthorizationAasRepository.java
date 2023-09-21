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
package org.eclipse.digitaltwin.basyx.aasrepository.feature.authorization;

import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetInformation;
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.basyx.aasrepository.AasRepository;
import org.eclipse.digitaltwin.basyx.core.exceptions.CollidingIdentifierException;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.filtering.FilterInfo;
import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;

import java.util.List;

/**
 * Observer for the AASAggregator that triggers authorization for different
 * operations on the aggregator.
 *
 * @author wege
 *
 */
public class AuthorizationAasRepository<AssetAdministrationShellFilterType, SubmodelReferenceFilterType> implements AasRepository<AssetAdministrationShellFilterType, SubmodelReferenceFilterType> {

	private AasRepository<AssetAdministrationShellFilterType, SubmodelReferenceFilterType> decorated;
	private PermissionResolver<AssetAdministrationShellFilterType, SubmodelReferenceFilterType> permissionResolver;

	public AuthorizationAasRepository(AasRepository<AssetAdministrationShellFilterType, SubmodelReferenceFilterType> decorated, PermissionResolver<AssetAdministrationShellFilterType, SubmodelReferenceFilterType> permissionResolver) {
		this.decorated = decorated;
		this.permissionResolver = permissionResolver;
	}

	@Override
	public CursorResult<List<AssetAdministrationShell>> getAllAas(PaginationInfo pInfo, FilterInfo<AssetAdministrationShellFilterType> filterInfo) {
		return decorated.getAllAas(pInfo, permissionResolver.getGetAllAasFilterInfo());
	}

	@Override
	public AssetAdministrationShell getAas(String aasId) throws ElementDoesNotExistException {
		permissionResolver.getAas(aasId);
		return decorated.getAas(aasId);
	}

	@Override
	public void createAas(AssetAdministrationShell aas) throws CollidingIdentifierException {
		permissionResolver.createAas(aas);
		decorated.createAas(aas);
	}

	@Override
	public void updateAas(String aasId, AssetAdministrationShell aas) {
		permissionResolver.updateAas(aasId, aas);
		decorated.updateAas(aasId, aas);
	}

	@Override
	public void deleteAas(String aasId) {
		permissionResolver.deleteAas(aasId);
		decorated.deleteAas(aasId);
	}

	@Override
	public String getName() {
		return decorated.getName();
	}

	@Override
	public CursorResult<List<Reference>> getSubmodelReferences(String aasId, PaginationInfo pInfo, FilterInfo<SubmodelReferenceFilterType> filterInfo) {
		return decorated.getSubmodelReferences(aasId, pInfo, permissionResolver.getGetSubmodelReferencesFilterInfo(aasId));
	}

	@Override
	public void addSubmodelReference(String aasId, Reference submodelReference) {
		permissionResolver.addSubmodelReference(aasId, submodelReference);
		decorated.addSubmodelReference(aasId, submodelReference);
	}

	@Override
	public void removeSubmodelReference(String aasId, String submodelId) {
		permissionResolver.removeSubmodelReference(aasId, submodelId);
		decorated.removeSubmodelReference(aasId, submodelId);
	}

	@Override
	public void setAssetInformation(String aasId, AssetInformation aasInfo) throws ElementDoesNotExistException {
		permissionResolver.setAssetInformation(aasId, aasInfo);
		decorated.setAssetInformation(aasId, aasInfo);
	}
	
	@Override
	public AssetInformation getAssetInformation(String aasId) throws ElementDoesNotExistException {
		permissionResolver.getAssetInformation(aasId);
		return decorated.getAssetInformation(aasId);
	}

}

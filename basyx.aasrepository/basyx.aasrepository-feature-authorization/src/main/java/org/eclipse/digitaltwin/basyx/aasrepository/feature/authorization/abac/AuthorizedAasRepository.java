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

package org.eclipse.digitaltwin.basyx.aasrepository.feature.authorization.abac;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetInformation;
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.basyx.aasrepository.AasRepository;
import org.eclipse.digitaltwin.basyx.authorization.abac.AbacPermissionResolver;
import org.eclipse.digitaltwin.basyx.authorization.abac.ObjectItem;
import org.eclipse.digitaltwin.basyx.authorization.abac.RightsEnum;
import org.eclipse.digitaltwin.basyx.authorization.abac.Value;
import org.eclipse.digitaltwin.basyx.core.exceptions.CollidingIdentifierException;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.exceptions.InsufficientPermissionException;
import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;

/**
 * Decorator for authorized {@link AasRepository}
 *
 * @author danish
 *
 */
public class AuthorizedAasRepository implements AasRepository {

	private AasRepository decorated;
	private AbacPermissionResolver permissionResolver;
	

	public AuthorizedAasRepository(AasRepository decorated, AbacPermissionResolver permissionResolver) {
		this.decorated = decorated;
		this.permissionResolver = permissionResolver;
	}

	@Override
	public CursorResult<List<AssetAdministrationShell>> getAllAas(PaginationInfo pInfo) {
		
		boolean isAuthorized = permissionResolver.hasPermission(null, null, null);
		
		throwExceptionIfInsufficientPermission(isAuthorized);
		
		return decorated.getAllAas(pInfo);
	}

	@Override
	public AssetAdministrationShell getAas(String shellId) throws ElementDoesNotExistException {
		
		Map<String, Value> attributesMap = new HashMap<String, Value>();
		
		AssetAdministrationShell shell = decorated.getAas(shellId);
		
		Value aasIdValue = new Value();
		aasIdValue.set$strVal(shellId);
		
		attributesMap.put("$aas#id", aasIdValue);
		
		AssetInformation assetInformation = shell.getAssetInformation();
		
		if (assetInformation != null) {
			String assetKind = assetInformation.getAssetKind().toString();
			String assetType = assetInformation.getAssetType();
			String globalAssetId = assetInformation.getGlobalAssetId();
			
			Value assetKindValue = new Value();
			assetKindValue.set$strVal(assetKind);
			
			Value assetTypeValue = new Value();
			assetTypeValue.set$strVal(assetType);
			
			Value globalAssetIdValue = new Value();
			globalAssetIdValue.set$strVal(globalAssetId);
			
			attributesMap.put("$aas#assetInformation.assetKind", assetKindValue);
			attributesMap.put("$aas#assetInformation.assetType", assetTypeValue);
			attributesMap.put("$aas#assetInformation.globalAssetId", globalAssetIdValue);
		}
   
		boolean isAuthorized = permissionResolver.hasPermission(RightsEnum.READ, new ObjectItem(null, "(AAS)" + shellId, null, null, null), attributesMap);
		
		throwExceptionIfInsufficientPermission(isAuthorized);
		
		return decorated.getAas(shellId);
	}

	@Override
	public void createAas(AssetAdministrationShell shell) throws CollidingIdentifierException {
//		
//		boolean isAuthorized = permissionResolver.hasPermission(new QueryJsonSchema(null, mainQuery, null));
//		
//		throwExceptionIfInsufficientPermission(isAuthorized);
		
		decorated.createAas(shell);
	}

	@Override
	public void updateAas(String shellId, AssetAdministrationShell shell) {
		boolean isAuthorized = permissionResolver.hasPermission(null, null, null);
		
		throwExceptionIfInsufficientPermission(isAuthorized);
		
		decorated.updateAas(shellId, shell);
	}

	@Override
	public void deleteAas(String shellId) {
		boolean isAuthorized = permissionResolver.hasPermission(null, null, null);
		
		throwExceptionIfInsufficientPermission(isAuthorized);
		
		decorated.deleteAas(shellId);
	}

	@Override
	public CursorResult<List<Reference>> getSubmodelReferences(String shellId, PaginationInfo paginationInfo) {
		boolean isAuthorized = permissionResolver.hasPermission(null, null, null);
		
		throwExceptionIfInsufficientPermission(isAuthorized);
		
		return decorated.getSubmodelReferences(shellId, paginationInfo);
	}

	@Override
	public void addSubmodelReference(String shellId, Reference submodelReference) {
		boolean isAuthorized = permissionResolver.hasPermission(null, null, null);
		
		throwExceptionIfInsufficientPermission(isAuthorized);
		
		decorated.addSubmodelReference(shellId, submodelReference);
	}

	@Override
	public void removeSubmodelReference(String shellId, String submodelId) {
		boolean isAuthorized = permissionResolver.hasPermission(null, null, null);
		
		throwExceptionIfInsufficientPermission(isAuthorized);
		
		decorated.removeSubmodelReference(shellId, submodelId);
	}

	@Override
	public void setAssetInformation(String shellId, AssetInformation shellInfo) throws ElementDoesNotExistException {
		boolean isAuthorized = permissionResolver.hasPermission(null, null, null);
		
		throwExceptionIfInsufficientPermission(isAuthorized);
		
		decorated.setAssetInformation(shellId, shellInfo);
	}

	@Override
	public AssetInformation getAssetInformation(String shellId) throws ElementDoesNotExistException {
		boolean isAuthorized = permissionResolver.hasPermission(null, null, null);
		
		throwExceptionIfInsufficientPermission(isAuthorized);
		
		return decorated.getAssetInformation(shellId);
	}

	@Override
	public File getThumbnail(String shellId) {
		boolean isAuthorized = permissionResolver.hasPermission(null, null, null);
		
		throwExceptionIfInsufficientPermission(isAuthorized);
		
		return decorated.getThumbnail(shellId);
	}

	@Override
	public void setThumbnail(String shellId, String fileName, String contentType, InputStream inputStream) {
		boolean isAuthorized = permissionResolver.hasPermission(null, null, null);
		
		throwExceptionIfInsufficientPermission(isAuthorized);
		
		decorated.setThumbnail(shellId, fileName, contentType, inputStream);
	}

	@Override
	public void deleteThumbnail(String shellId) {
		boolean isAuthorized = permissionResolver.hasPermission(null, null, null);
		
		throwExceptionIfInsufficientPermission(isAuthorized);
		
		decorated.deleteThumbnail(shellId);
	}
	
	@Override
	public String getName() {
		return decorated.getName();
	}
	
	private ArrayList<String> getIdAsList(String id) {
		return new ArrayList<>(Arrays.asList(id));
	}
	
	private void throwExceptionIfInsufficientPermission(boolean isAuthorized) {
		if (!isAuthorized)
			throw new InsufficientPermissionException("Insufficient Permission: The current subject does not have the required permissions for this operation.");
	}

}

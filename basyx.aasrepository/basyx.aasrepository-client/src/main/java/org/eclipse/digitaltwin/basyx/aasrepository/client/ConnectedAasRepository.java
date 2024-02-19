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


package org.eclipse.digitaltwin.basyx.aasrepository.client;

import java.util.Collections;
import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.basyx.aasrepository.client.internal.AssetAdministrationShellRepositoryApi;
import org.eclipse.digitaltwin.basyx.client.internal.ApiException;
import org.eclipse.digitaltwin.basyx.core.exceptions.CollidingIdentifierException;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.exceptions.IdentificationMismatchException;
import org.eclipse.digitaltwin.basyx.core.exceptions.MissingIdentifierException;
import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.springframework.http.HttpStatus;

/**
 * Provides access to an Aas Repository on a remote server
 * 
 * @author schnicke
 */
public class ConnectedAasRepository {

	private AssetAdministrationShellRepositoryApi repoApi;

	/**
	 * 
	 * @param repoUrl
	 *            the Url of the AAS Repository without the "/shells" part
	 */
	public ConnectedAasRepository(String repoUrl) {
		this.repoApi = new AssetAdministrationShellRepositoryApi(repoUrl);
	}

	public CursorResult<List<AssetAdministrationShell>> getAllAas(PaginationInfo pInfo) {
		return repoApi.getAllAssetAdministrationShells(Collections.emptyList(), "", pInfo.getLimit(), pInfo.getCursor());
	}

	public AssetAdministrationShell getAas(String aasId) throws ElementDoesNotExistException {
		try {
			return repoApi.getAssetAdministrationShellById(aasId);
		} catch (ApiException e) {
			throw mapExceptionAasAccess(aasId, e);
		}
	}

	public void createAas(AssetAdministrationShell aas) throws CollidingIdentifierException, MissingIdentifierException {
		try {
			repoApi.postAssetAdministrationShell(aas);
		} catch (ApiException e) {
			throw mapExceptionAasAccess(aas.getId(), e);
		}
	}

	public void deleteAas(String aasId) {
		try {
			repoApi.deleteAssetAdministrationShellById(aasId);
		} catch (ApiException e) {
			throw mapExceptionAasAccess(aasId, e);
		}
	}

	public void updateAas(String aasId, AssetAdministrationShell aas) {
		try {
			repoApi.putAssetAdministrationShellById(aasId, aas);
		} catch (ApiException e) {
			throw mapExceptionAasUpdate(aasId, e);
		}
	}

	private RuntimeException mapExceptionAasUpdate(String aasId, ApiException e) {
		if (e.getCode() == HttpStatus.BAD_REQUEST.value()) {
			return new IdentificationMismatchException();
		}

		return mapExceptionAasAccess(aasId, e);
	}

	private RuntimeException mapExceptionAasAccess(String aasId, ApiException e) {
		if (e.getCode() == HttpStatus.NOT_FOUND.value()) {
			return new ElementDoesNotExistException(aasId);
		} else if (e.getCode() == HttpStatus.CONFLICT.value()) {
			return new CollidingIdentifierException(aasId);
		} else if (e.getCode() == HttpStatus.BAD_REQUEST.value()) {
			return new MissingIdentifierException();
		}

		return e;
	}

}

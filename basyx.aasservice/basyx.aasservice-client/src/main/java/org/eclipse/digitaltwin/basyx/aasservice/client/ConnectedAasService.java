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


package org.eclipse.digitaltwin.basyx.aasservice.client;

import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetInformation;
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.basyx.aasservice.AasService;
import org.eclipse.digitaltwin.basyx.aasservice.client.internal.AssetAdministrationShellServiceApi;
import org.eclipse.digitaltwin.basyx.client.internal.ApiException;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.springframework.http.HttpStatus;

/**
 * Provides access to a Aas Service on a remote server - regardless if it is
 * hosted on a Aas Repository or standalone
 * 
 * @author schnicke
 */
public class ConnectedAasService implements AasService {

	private AssetAdministrationShellServiceApi serviceApi;

	public ConnectedAasService(String aasServiceUrl) {
		this.serviceApi = new AssetAdministrationShellServiceApi(aasServiceUrl);
	}

	@Override
	public AssetAdministrationShell getAAS() throws ElementDoesNotExistException {
		try {
			return serviceApi.getAssetAdministrationShell();
		} catch (ApiException e) {
			throw mapAasAccess(e);
		}
	}

	@Override
	public CursorResult<List<Reference>> getSubmodelReferences(PaginationInfo pInfo) throws ElementDoesNotExistException {
		try {
			return serviceApi.getAllSubmodelReferences(pInfo.getLimit(), pInfo.getCursor());
		} catch (ApiException e) {
			throw mapAasAccess(e);
		}
	}

	@Override
	public void addSubmodelReference(Reference submodelReference) throws ElementDoesNotExistException {
		try {
			serviceApi.postSubmodelReference(submodelReference);
		} catch (ApiException e) {
			throw mapAasAccess(e);
		}
	}

	@Override
	public void removeSubmodelReference(String submodelId) throws ElementDoesNotExistException {
		try {
			serviceApi.deleteSubmodelReferenceById(submodelId);
		} catch (ApiException e) {
			throw mapAasAccess(submodelId, e);
		}
	}

	@Override
	public void setAssetInformation(AssetInformation aasInfo) throws ElementDoesNotExistException {
		try {
			serviceApi.putAssetInformation(aasInfo);
		} catch (ApiException e) {
			throw mapAasAccess(e);
		}
	}

	@Override
	public AssetInformation getAssetInformation() throws ElementDoesNotExistException {
		try {
			return serviceApi.getAssetInformation();
		} catch (ApiException e) {
			throw mapAasAccess(e);
		}
	}

	private RuntimeException mapAasAccess(String shellId, ApiException e) {
		if (e.getCode() == HttpStatus.NOT_FOUND.value()) {
			return new ElementDoesNotExistException(shellId);
		}

		return e;
	}

	private RuntimeException mapAasAccess(ApiException e) {
		if (e.getCode() == HttpStatus.NOT_FOUND.value()) {
			return new ElementDoesNotExistException();
		}

		return e;
	}
}

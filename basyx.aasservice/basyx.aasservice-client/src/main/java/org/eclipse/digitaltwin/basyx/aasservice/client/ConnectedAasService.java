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

import java.io.File;
import java.io.InputStream;
import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetInformation;
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.basyx.aasservice.AasService;
import org.eclipse.digitaltwin.basyx.aasservice.client.internal.AssetAdministrationShellServiceApi;
import org.eclipse.digitaltwin.basyx.client.internal.ApiException;
import org.eclipse.digitaltwin.basyx.core.exceptions.CollidingSubmodelReferenceException;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementNotAFileException;
import org.eclipse.digitaltwin.basyx.core.exceptions.FileDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.eclipse.digitaltwin.basyx.http.Base64UrlEncoder;
import org.springframework.http.HttpStatus;

/**
 * Provides access to a Aas Service on a remote server - regardless if it is
 * hosted on a Aas Repository or standalone
 * 
 * @author schnicke, mateusmolina
 */
public class ConnectedAasService implements AasService {

	private AssetAdministrationShellServiceApi serviceApi;

	public ConnectedAasService(String aasServiceUrl) {
		this.serviceApi = new AssetAdministrationShellServiceApi(aasServiceUrl);
	}
	
	public ConnectedAasService(AssetAdministrationShellServiceApi assetAdministrationShellServiceApi) {
		this.serviceApi = assetAdministrationShellServiceApi;
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
			String encodedCursor = pInfo.getCursor() == null ? null : Base64UrlEncoder.encode(pInfo.getCursor());
			return serviceApi.getAllSubmodelReferences(pInfo.getLimit(), encodedCursor);
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
			throw mapSubmodelAccess(submodelId, e);
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

	@Override
	public File getThumbnail() {
		try {
			return serviceApi.getThumbnail();
		} catch (ApiException e) {
			throw mapThumbnailAccess(e);
		}
	}

	@Override
	public void setThumbnail(String fileName, String contentType, InputStream inputStream) {
		try {
			serviceApi.putThumbnail(fileName, contentType, inputStream);
		} catch (ApiException e) {
			throw mapThumbnailAccess(e);
		}

	}

	@Override
	public void deleteThumbnail() {
		try {
			serviceApi.deleteThumbnail();
		} catch (ApiException e) {
			throw mapThumbnailAccess(e);
		}
	}

	private RuntimeException mapThumbnailAccess(ApiException e) {
		if (e.getCode() == HttpStatus.NOT_FOUND.value())
			return new FileDoesNotExistException();

		if (e.getCode() == HttpStatus.PRECONDITION_FAILED.value())
			return new ElementNotAFileException();

		return e;
	}

	private RuntimeException mapSubmodelAccess(String submodelId, ApiException e) {
		if (e.getCode() == HttpStatus.NOT_FOUND.value())
			return new ElementDoesNotExistException(submodelId);
		
		return e;
	}


	private RuntimeException mapAasAccess(ApiException e) {
		if (e.getCode() == HttpStatus.NOT_FOUND.value()) {
			return new ElementDoesNotExistException();
		}

		if(e.getCode() == HttpStatus.CONFLICT.value()) {
			return new CollidingSubmodelReferenceException();
		}

		return e;
	}

}

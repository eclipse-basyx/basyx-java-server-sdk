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


package org.eclipse.digitaltwin.basyx.submodelrepository.client;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.model.OperationVariable;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.basyx.client.internal.ApiException;
import org.eclipse.digitaltwin.basyx.core.exceptions.CollidingIdentifierException;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementNotAFileException;
import org.eclipse.digitaltwin.basyx.core.exceptions.FileDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.exceptions.MissingIdentifierException;
import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.eclipse.digitaltwin.basyx.http.Base64UrlEncodedIdentifier;
import org.eclipse.digitaltwin.basyx.http.Base64UrlEncoder;
import org.eclipse.digitaltwin.basyx.submodelrepository.SubmodelRepository;
import org.eclipse.digitaltwin.basyx.submodelrepository.client.internal.SubmodelRepositoryApi;
import org.eclipse.digitaltwin.basyx.submodelservice.client.ConnectedSubmodelService;
import org.eclipse.digitaltwin.basyx.submodelservice.value.SubmodelElementValue;
import org.eclipse.digitaltwin.basyx.submodelservice.value.SubmodelValueOnly;
import org.springframework.http.HttpStatus;


/**
 * Provides access to a Submodel Repository on a remote server
 * 
 * @author schnicke, mateusmolina
 */
public class ConnectedSubmodelRepository implements SubmodelRepository {

	private SubmodelRepositoryApi repoApi;
	private String submodelRepoUrl;

	/**
	 * 
	 * @param submodelRepoUrl
	 *            the Url of the Submodel Repository without the "/submodels" part
	 */
	public ConnectedSubmodelRepository(String submodelRepoUrl) {
		this.repoApi = new SubmodelRepositoryApi(submodelRepoUrl);
		this.submodelRepoUrl = submodelRepoUrl;
	}
	
	public ConnectedSubmodelRepository(String submodelRepoUrl, SubmodelRepositoryApi submodelRepositoryApi) {
		this.submodelRepoUrl = submodelRepoUrl;
		this.repoApi = submodelRepositoryApi;
	}
	
	public String getBaseUrl() {
		return submodelRepoUrl;
	}

	/**
	 * Retrieves the Submodel with the specific id
	 * 
	 * @param submodelId
	 * @return
	 * @throws ElementDoesNotExistException
	 */
	@Override
	public Submodel getSubmodel(String submodelId) throws ElementDoesNotExistException {
		try {
			return repoApi.getSubmodelById(submodelId, "", "");
		} catch (ApiException e) {
			throw mapExceptionSubmodelAccess(submodelId, e);
		}
	}

	@Override
	public void updateSubmodel(String submodelId, Submodel submodel) throws ElementDoesNotExistException {
		try {
			repoApi.putSubmodelById(submodelId, submodel);
		} catch (ApiException e) {
			throw mapExceptionSubmodelAccess(submodel.getId(), e);
		}
	}

	@Override
	public void createSubmodel(Submodel submodel) throws CollidingIdentifierException, MissingIdentifierException {
		try {
			repoApi.postSubmodel(submodel);
		} catch (ApiException e) {
			throw mapExceptionSubmodelAccess(submodel.getId(), e);
		}
	}

	@Override
	public void deleteSubmodel(String submodelId) throws ElementDoesNotExistException {
		try {
			repoApi.deleteSubmodelById(submodelId);
		} catch (ApiException e) {
			throw mapExceptionSubmodelAccess(submodelId, e);
		}
	}

	/**
	 * Retrieves a ConnectedSubmodelService for interacting with the Submodel on the
	 * Server
	 * 
	 * @param submodelId
	 * @return
	 * @throws ElementDoesNotExistException
	 */
	public ConnectedSubmodelService getConnectedSubmodelService(String submodelId) throws ElementDoesNotExistException {
		try {
			repoApi.getSubmodelById(submodelId, "", "");
			return new ConnectedSubmodelService(getSubmodelUrl(submodelId));
		} catch (ApiException e) {
			throw mapExceptionSubmodelAccess(submodelId, e);
		}
	}

	@Override
	public CursorResult<List<Submodel>> getAllSubmodels(PaginationInfo pInfo) {
		try{
			String encodedCursor = pInfo.getCursor() == null ? null : Base64UrlEncoder.encode(pInfo.getCursor());
			return repoApi.getAllSubmodels(null, null, pInfo.getLimit(), encodedCursor, null, null);
		} catch (ApiException e) {
			if (e.getCode() == HttpStatus.INTERNAL_SERVER_ERROR.value())
				return new CursorResult<>("", new ArrayList<>());
			else
				throw e;
		}

	}

	@Override
	public CursorResult<List<Submodel>> getAllSubmodels(String semanticId, PaginationInfo pInfo) {
		try {
	        String encodedCursor = pInfo.getCursor() == null ? null : Base64UrlEncoder.encode(pInfo.getCursor());
	        String encodedSemanticId = Base64UrlEncoder.encode(semanticId);
	        return repoApi.getAllSubmodels(encodedSemanticId, null, pInfo.getLimit(), encodedCursor, null, null);
	    } catch (ApiException e) {
	        if (e.getCode() == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
	            return new CursorResult<>("", new ArrayList<>());
	        } else {
	            throw e;
	        }
	    }
	}

	@Override
	public void updateSubmodelElement(String submodelId, String idShortPath, SubmodelElement submodelElement) throws ElementDoesNotExistException {
		getConnectedSubmodelService(submodelId).updateSubmodelElement(idShortPath, submodelElement);
	}

	@Override
	public CursorResult<List<SubmodelElement>> getSubmodelElements(String submodelId, PaginationInfo pInfo) throws ElementDoesNotExistException {
		return getConnectedSubmodelService(submodelId).getSubmodelElements(pInfo);
	}

	@Override
	public SubmodelElement getSubmodelElement(String submodelId, String smeIdShort) throws ElementDoesNotExistException {
		return getConnectedSubmodelService(submodelId).getSubmodelElement(smeIdShort);
	}

	@Override
	public SubmodelElementValue getSubmodelElementValue(String submodelId, String smeIdShort) throws ElementDoesNotExistException {
		return getConnectedSubmodelService(submodelId).getSubmodelElementValue(smeIdShort);
	}

	@Override
	public void setSubmodelElementValue(String submodelId, String smeIdShort, SubmodelElementValue value) throws ElementDoesNotExistException {
		getConnectedSubmodelService(submodelId).setSubmodelElementValue(smeIdShort, value);
	}

	@Override
	public void createSubmodelElement(String submodelId, SubmodelElement smElement) {
		getConnectedSubmodelService(submodelId).createSubmodelElement(smElement);
	}

	@Override
	public void createSubmodelElement(String submodelId, String idShortPath, SubmodelElement smElement) throws ElementDoesNotExistException {
		getConnectedSubmodelService(submodelId).createSubmodelElement(idShortPath, smElement);
	}

	@Override
	public void deleteSubmodelElement(String submodelId, String idShortPath) throws ElementDoesNotExistException {
		getConnectedSubmodelService(submodelId).deleteSubmodelElement(idShortPath);
	}

	@Override
	public OperationVariable[] invokeOperation(String submodelId, String idShortPath, OperationVariable[] input) throws ElementDoesNotExistException {
		return getConnectedSubmodelService(submodelId).invokeOperation(idShortPath, input);
	}

	@Override
	public SubmodelValueOnly getSubmodelByIdValueOnly(String submodelId) throws ElementDoesNotExistException {
		return new SubmodelValueOnly(getSubmodel(submodelId).getSubmodelElements());
	}

	@Override
	public Submodel getSubmodelByIdMetadata(String submodelId) throws ElementDoesNotExistException {
		try {
			return repoApi.getSubmodelByIdMetadata(submodelId, null);
		} catch (ApiException e) {
			throw mapExceptionSubmodelAccess(submodelId, e); 
		}
	}

	@Override
	public File getFileByPathSubmodel(String submodelId, String idShortPath) throws ElementDoesNotExistException, ElementNotAFileException, FileDoesNotExistException {
		return getConnectedSubmodelService(submodelId).getFileByPath(idShortPath);
	}

	@Override
	public void setFileValue(String submodelId, String idShortPath, String fileName, String contentType, InputStream inputStream) throws ElementDoesNotExistException, ElementNotAFileException {
		getConnectedSubmodelService(submodelId).setFileValue(idShortPath, fileName, contentType, inputStream);
	}

	@Override
	public void deleteFileValue(String submodelId, String idShortPath) throws ElementDoesNotExistException, ElementNotAFileException, FileDoesNotExistException {
		getConnectedSubmodelService(submodelId).deleteFileValue(idShortPath);
	}

	protected String getSubmodelUrl(String submodelId) {
		return submodelRepoUrl + "/submodels/" + Base64UrlEncodedIdentifier.encodeIdentifier(submodelId);
	}
	
	protected RuntimeException mapExceptionSubmodelAccess(String submodelId, ApiException e) {
		if (e.getCode() == HttpStatus.NOT_FOUND.value()) {
			return new ElementDoesNotExistException(submodelId);
		} else if (e.getCode() == HttpStatus.CONFLICT.value()) {
			throw new CollidingIdentifierException(submodelId);
		} else if (e.getCode() == HttpStatus.BAD_REQUEST.value()) {
			throw new MissingIdentifierException();
		}

		return e;
	}

	@Override
	public void patchSubmodelElements(String submodelId, List<SubmodelElement> submodelElementList) {
		getConnectedSubmodelService(submodelId).patchSubmodelElements(submodelElementList);
	}

	@Override
	public InputStream getFileByFilePath(String submodelId, String filePath) {
		return getConnectedSubmodelService(submodelId).getFileByFilePath(filePath);
	}

}

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


package org.eclipse.digitaltwin.basyx.submodelservice.client;

import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.model.OperationVariable;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.basyx.client.internal.ApiException;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.exceptions.FeatureNotImplementedException;
import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.eclipse.digitaltwin.basyx.submodelservice.SubmodelService;
import org.eclipse.digitaltwin.basyx.submodelservice.client.internal.SubmodelServiceApi;
import org.eclipse.digitaltwin.basyx.submodelservice.value.SubmodelElementValue;
import org.springframework.http.HttpStatus;

/**
 * Provides access to a Submodel Service on a remote server - regardless if it
 * is hosted on a Submodel Repository or standalone
 * 
 * @author schnicke
 */
public class ConnectedSubmodelService implements SubmodelService {

	private SubmodelServiceApi serviceApi;

	/**
	 * 
	 * @param submodelServiceUrl
	 *            the Url of the submodel service. Please note that for standalone
	 *            submodels the "/submodel" part has to be included
	 */
	public ConnectedSubmodelService(String submodelServiceUrl) {
		this.serviceApi = new SubmodelServiceApi(submodelServiceUrl);
	}

	@Override
	public Submodel getSubmodel() {
		return serviceApi.getSubmodel("", "");
	}

	@Override
	public SubmodelElement getSubmodelElement(String idShortPath) throws ElementDoesNotExistException {
		try {
			return serviceApi.getSubmodelElementByPath(idShortPath, "", "");
		} catch (ApiException e) {
			throw mapExceptionSubmodelElementAccess(idShortPath, e);
		}
	}

	@Override
	public SubmodelElementValue getSubmodelElementValue(String idShortPath) throws ElementDoesNotExistException {
		try {
			return serviceApi.getSubmodelElementByPathValueOnly(idShortPath, "", "");
		} catch (ApiException e) {
			throw mapExceptionSubmodelElementAccess(idShortPath, e);
		}
	}

	@Override
	public void setSubmodelElementValue(String idShortPath, SubmodelElementValue value) throws ElementDoesNotExistException {
		try {
			serviceApi.patchSubmodelElementByPathValueOnly(idShortPath, value, 0, "", "");
		} catch (ApiException e) {
			throw mapExceptionSubmodelElementAccess(idShortPath, e);
		}
	}

	@Override
	public void createSubmodelElement(SubmodelElement submodelElement) {
		serviceApi.postSubmodelElement(submodelElement);
	}

	@Override
	public void createSubmodelElement(String idShortPath, SubmodelElement submodelElement) throws ElementDoesNotExistException {
		try {
			serviceApi.postSubmodelElementByPath(idShortPath, submodelElement);
		} catch (ApiException e) {
			throw mapExceptionSubmodelElementAccess(idShortPath, e);
		}
	}
	
	@Override
	public void updateSubmodelElement(String idShortPath, SubmodelElement submodelElement) throws ElementDoesNotExistException {
		try {
			serviceApi.putSubmodelElementByPath(idShortPath, submodelElement, "");
		} catch (ApiException e) {
			throw mapExceptionSubmodelElementAccess(idShortPath, e);
		}
	}

	@Override
	public void deleteSubmodelElement(String idShortPath) throws ElementDoesNotExistException {
		try {
			serviceApi.deleteSubmodelElementByPath(idShortPath);
		} catch (ApiException e) {
			throw mapExceptionSubmodelElementAccess(idShortPath, e);
		}
	}

	private RuntimeException mapExceptionSubmodelElementAccess(String idShortPath, ApiException e) {
		if (e.getCode() == HttpStatus.NOT_FOUND.value()) {
			return new ElementDoesNotExistException(idShortPath);
		}

		return e;
	}

	@Override
	public CursorResult<List<SubmodelElement>> getSubmodelElements(PaginationInfo pInfo) {
		throw new FeatureNotImplementedException();
	}

	@Override
	public OperationVariable[] invokeOperation(String idShortPath, OperationVariable[] input) throws ElementDoesNotExistException {
		throw new FeatureNotImplementedException();
	}

}

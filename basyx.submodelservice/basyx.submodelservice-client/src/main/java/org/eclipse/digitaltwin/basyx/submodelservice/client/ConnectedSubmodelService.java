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

import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.basyx.client.internal.ApiException;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;
import org.eclipse.digitaltwin.basyx.submodelservice.client.internal.SubmodelServiceApi;
import org.eclipse.digitaltwin.basyx.submodelservice.value.SubmodelElementValue;
import org.springframework.http.HttpStatus;

public class ConnectedSubmodelService {
	private SubmodelServiceApi repoApi;

	public ConnectedSubmodelService(String submodelServiceUrl) {
		this.repoApi = new SubmodelServiceApi(submodelServiceUrl);
	}

	/**
	 * Retrieves the Submodel contained in the service
	 * 
	 * @return
	 */
	public Submodel getSubmodel() {
		return repoApi.getSubmodel("", "");
	}

	/**
	 * Retrieve specific SubmodelElement of the Submodel
	 *
	 * @param idShortPath
	 *            the SubmodelElement IdShort
	 * @return the SubmodelElement
	 * @throws ElementDoesNotExistException
	 *             if the SubmodelElement does not exist
	 */
	public SubmodelElement getSubmodelElement(String idShortPath) throws ElementDoesNotExistException {
		try {
			return repoApi.getSubmodelElementByPath(idShortPath, "", "");
		} catch (ApiException e) {
			throw mapExceptionSubmodelElementAccess(idShortPath, e);
		}
	}

	/**
	 * Retrieves the value of a specific SubmodelElement of the Submodel
	 * 
	 * @param idShortPath
	 *            the SubmodelElement idShortPath
	 * @return the SubmodelElementValue
	 * @throws ElementDoesNotExistException
	 *             if the SubmodelElement does not exist
	 */
	public SubmodelElementValue getSubmodelElementValue(String idShortPath) throws ElementDoesNotExistException {
		try {
			return repoApi.getSubmodelElementByPathValueOnly(idShortPath, "", "");
		} catch (ApiException e) {
			throw mapExceptionSubmodelElementAccess(idShortPath, e);
		}
	}

	/**
	 * Sets the value of a specific SubmodelElement of the Submodel
	 * 
	 * @param idShortPath
	 *            the SubmodelElement IdShortPath
	 * @param value
	 *            the new value
	 * @throws ElementDoesNotExistException
	 *             if the SubmodelElement does not exist
	 */
	public void setSubmodelElementValue(String idShortPath, SubmodelElementValue value) throws ElementDoesNotExistException {
		try {
			repoApi.patchSubmodelElementByPathValueOnly(idShortPath, value, 0, "", "");
		} catch (ApiException e) {
			throw mapExceptionSubmodelElementAccess(idShortPath, e);
		}
	}

	/**
	 * Creates a Submodel Element
	 * 
	 */
	public void createSubmodelElement(SubmodelElement submodelElement) {
		repoApi.postSubmodelElement(submodelElement);
	}

	/**
	 * Create a nested submodel element
	 * 
	 * @param idShortPath
	 *            the SubmodelElement IdShortPath
	 * @param submodelElement
	 *            the submodel element to be created
	 * @throws ElementDoesNotExistException
	 *             If the submodel element defined in the path does not exist
	 */
	public void createSubmodelElement(String idShortPath, SubmodelElement submodelElement) throws ElementDoesNotExistException {
		try {
			repoApi.postSubmodelElementByPath(idShortPath, submodelElement);
		} catch (ApiException e) {
			throw mapExceptionSubmodelElementAccess(idShortPath, e);
		}
	}
	
	/**
	 * Updates a submodel element
	 * 
	 * @param idShortPath
	 * @param submodelElement
	 * @throws ElementDoesNotExistException
	 *             If the submodel element defined in the path does not exist
	 */
	public void updateSubmodelElement(String idShortPath, SubmodelElement submodelElement) throws ElementDoesNotExistException {
		try {
			repoApi.putSubmodelElementByPath(idShortPath, submodelElement, "");
		} catch (ApiException e) {
			throw mapExceptionSubmodelElementAccess(idShortPath, e);
		}
	}

	/**
	 * Delete a submodel element in a submodel
	 * 
	 * @param idShortPath
	 *            the SubmodelElement IdShortPath
	 * @throws ElementDoesNotExistException
	 *             If the submodel element defined in the path does not exist
	 */
	public void deleteSubmodelElement(String idShortPath) throws ElementDoesNotExistException {
		try {
			repoApi.deleteSubmodelElementByPath(idShortPath);
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

}

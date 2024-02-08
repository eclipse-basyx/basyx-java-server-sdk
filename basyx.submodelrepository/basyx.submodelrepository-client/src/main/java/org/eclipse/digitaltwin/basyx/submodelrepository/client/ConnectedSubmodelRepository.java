/*******************************************************************************
 * Copyright (C) 2023 the Eclipse BaSyx Authors
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

import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.basyx.client.internal.ApiException;
import org.eclipse.digitaltwin.basyx.core.exceptions.CollidingIdentifierException;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.exceptions.MissingIdentifierException;
import org.eclipse.digitaltwin.basyx.submodelrepository.client.internal.SubmodelRepositoryApi;
import org.springframework.http.HttpStatus;


/**
 * Provides access to a Submodel Repository on a remote server
 * 
 * @author schnicke
 */
public class ConnectedSubmodelRepository {

	private SubmodelRepositoryApi repoApi;

	public ConnectedSubmodelRepository(String submodelRepoUrl) {
		this.repoApi = new SubmodelRepositoryApi(submodelRepoUrl);
	}

	/**
	 * Retrieves the Submodel with the specific id
	 * 
	 * @param submodelId
	 * @return
	 * @throws ElementDoesNotExistException
	 */
	public Submodel getSubmodel(String submodelId) throws ElementDoesNotExistException {
		try {
			return repoApi.getSubmodelById(submodelId, "", "");
		} catch (ApiException e) {
			throw mapExceptionSubmodelAccess(submodelId, e);
		}
	}

	/**
	 * Updates an existing Submodel
	 * 
	 * @param submodelId
	 * @param submodel
	 * @throws ElementDoesNotExistException
	 */
	public void updateSubmodel(String submodelId, Submodel submodel) throws ElementDoesNotExistException {
		try {
			repoApi.putSubmodelById(submodelId, submodel);
		} catch (ApiException e) {
			throw mapExceptionSubmodelAccess(submodel.getId(), e);
		}
	}

	/**
	 * Creates a new submodel
	 * 
	 * @param submodel
	 * @throws CollidingIdentifierException
	 */
	public void createSubmodel(Submodel submodel) throws CollidingIdentifierException, MissingIdentifierException {
		try {
			repoApi.postSubmodel(submodel);
		} catch (ApiException e) {
			throw mapExceptionSubmodelAccess(submodel.getId(), e);
		}
	}

	/**
	 * Deletes a Submodel
	 * 
	 * @param submodelId
	 * @throws ElementDoesNotExistException
	 */
	public void deleteSubmodel(String submodelId) throws ElementDoesNotExistException {
		try {
			repoApi.deleteSubmodelById(submodelId);
		} catch (ApiException e) {
			throw mapExceptionSubmodelAccess(submodelId, e);
		}

	}

	private RuntimeException mapExceptionSubmodelAccess(String submodelId, ApiException e) {
		if (e.getCode() == HttpStatus.NOT_FOUND.value()) {
			return new ElementDoesNotExistException(submodelId);
		} else if (e.getCode() == HttpStatus.CONFLICT.value()) {
			throw new CollidingIdentifierException(submodelId);
		} else if (e.getCode() == HttpStatus.BAD_REQUEST.value()) {
			throw new MissingIdentifierException();
		}

		return e;
	}

}

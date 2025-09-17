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

package org.eclipse.digitaltwin.basyx.submodelservice.backend;

import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.eclipse.digitaltwin.basyx.submodelservice.value.SubmodelElementValue;

import java.util.List;

/**
 * This interface provides backend-level operations for managing
 * {@link Submodel}s
 * 
 * @author mateusmolina
 */
public interface SubmodelOperations {

	/**
	 * Retrieves all Submodels with pagination support.
	 *
	 * @param 	pInfo	the pagination information
	 * @return 	a {@code CursorResult} containing a list of Submodels
	 */
	CursorResult<List<Submodel>> getSubmodels(String semanticId, PaginationInfo pInfo);

	/**
	 * Retrieves all Submodel Elements for the given Submodel.
	 *
	 * @param submodelId the identifier of the Submodel
	 * @param pInfo      the pagination information
	 * @return a {@code CursorResult} containing a list of Submodel Elements
	 * @throws ElementDoesNotExistException if the Submodel with the specified {@code submodelId} does not exist
	 */
	CursorResult<List<SubmodelElement>> getSubmodelElements(String submodelId, PaginationInfo pInfo) throws ElementDoesNotExistException;

	/**
	 * Retrieves a specific Submodel Element from the given Submodel.
	 *
	 * @param submodelId    the identifier of the Submodel
	 * @param smeIdShortPath the short path of the Submodel Element
	 * @return the requested Submodel Element
	 * @throws ElementDoesNotExistException if the Submodel or Submodel Element does not exist
	 */
	SubmodelElement getSubmodelElement(String submodelId, String smeIdShortPath) throws ElementDoesNotExistException;

	/**
	 * Retrieves the value of a specific Submodel Element.
	 *
	 * @param submodelId the identifier of the Submodel
	 * @param smeIdShort the short identifier of the Submodel Element
	 * @return the value of the specified Submodel Element
	 * @throws ElementDoesNotExistException if the Submodel or Submodel Element does not exist
	 */
	SubmodelElementValue getSubmodelElementValue(String submodelId, String smeIdShort) throws ElementDoesNotExistException;

	/**
	 * Sets the value of a specific Submodel Element.
	 *
	 * @param submodelId the identifier of the Submodel
	 * @param smeIdShort the short identifier of the Submodel Element
	 * @param value      the new value to be set
	 * @throws ElementDoesNotExistException if the Submodel or Submodel Element does not exist
	 */
	void setSubmodelElementValue(String submodelId, String smeIdShort, SubmodelElementValue value) throws ElementDoesNotExistException;

	/**
	 * Creates a new Submodel Element in the specified Submodel.
	 *
	 * @param submodelId the identifier of the Submodel
	 * @param smElement  the Submodel Element to be created
	 */
	void createSubmodelElement(String submodelId, SubmodelElement smElement);

	/**
	 * Creates a new Submodel Element at the specified path within a Submodel.
	 *
	 * @param submodelId  the identifier of the Submodel
	 * @param idShortPath the short path where the Submodel Element should be created
	 * @param smElement   the Submodel Element to be created
	 * @throws ElementDoesNotExistException if the Submodel does not exist
	 */
	void createSubmodelElement(String submodelId, String idShortPath, SubmodelElement smElement) throws ElementDoesNotExistException;

	/**
	 * Updates an existing Submodel Element.
	 *
	 * @param submodelId      the identifier of the Submodel
	 * @param idShortPath     the short path of the Submodel Element to be updated
	 * @param submodelElement the new Submodel Element data
	 * @throws ElementDoesNotExistException if the Submodel or Submodel Element does not exist
	 */
	void updateSubmodelElement(String submodelId, String idShortPath, SubmodelElement submodelElement) throws ElementDoesNotExistException;

	/**
	 * Deletes a specific Submodel Element.
	 *
	 * @param submodelId  the identifier of the Submodel
	 * @param idShortPath the short path of the Submodel Element to be deleted
	 * @throws ElementDoesNotExistException if the Submodel or Submodel Element does not exist
	 */
	void deleteSubmodelElement(String submodelId, String idShortPath) throws ElementDoesNotExistException;

	/**
	 * Applies partial modifications to multiple Submodel Elements.
	 *
	 * @param submodelId          the identifier of the Submodel
	 * @param submodelElementList the list of Submodel Elements with updates
	 */
	void patchSubmodelElements(String submodelId, List<SubmodelElement> submodelElementList);
}

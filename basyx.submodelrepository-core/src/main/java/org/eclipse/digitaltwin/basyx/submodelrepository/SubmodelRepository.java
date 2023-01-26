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


package org.eclipse.digitaltwin.basyx.submodelrepository;

import java.util.Collection;

import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.basyx.core.exceptions.CollidingIdentifierException;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;

/**
 * Specifies the overall SubmodelRepository API
 * 
 * @author schnicke
 *
 */
public interface SubmodelRepository {

	/**
	 * Retrieves all Submodels from the repository
	 * 
	 * @return a collection of all found Submodels
	 */
	public Collection<Submodel> getAllSubmodels();

	/**
	 * Retrieves the Submodel with the specific id
	 * 
	 * @param submodelId
	 * @return
	 * @throws ElementDoesNotExistException
	 */
	public Submodel getSubmodel(String submodelId) throws ElementDoesNotExistException;

	/**
	 * Updates an existing Submodel
	 * 
	 * @param submodelId
	 * @param submodel
	 * @throws ElementDoesNotExistException
	 */
	public void updateSubmodel(String submodelId, Submodel submodel) throws ElementDoesNotExistException;

	/**
	 * Creates a new submodel
	 * 
	 * @param submodel
	 * @throws CollidingIdentifierException
	 */
	public void createSubmodel(Submodel submodel) throws CollidingIdentifierException;

	/**
	 * Retrieves all SubmodelElements of a Submodel
	 * 
	 * @param submodelId
	 * @return
	 */
	public Collection<SubmodelElement> getSubmodelElements(String submodelId);
	
	/**
	 * Retrieve specific SubmodelElement of a Submodel
	 *
	 * @param submodelId
	 *            the Submodel id
	 * @param smeIdShort
	 *            the SubmodelElement IdShort
	 * @return the SubmodelElement
	 * @throws ElementDoesNotExistException
	 *             if the SubmodelElement or the Submodel does not exist
	 */
	public SubmodelElement getSubmodelElement(String submodelId, String smeIdShort) throws ElementDoesNotExistException;
	
}

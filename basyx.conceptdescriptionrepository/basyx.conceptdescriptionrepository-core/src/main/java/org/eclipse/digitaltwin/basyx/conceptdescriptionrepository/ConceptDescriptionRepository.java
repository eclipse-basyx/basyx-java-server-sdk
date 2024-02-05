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

package org.eclipse.digitaltwin.basyx.conceptdescriptionrepository;

import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.model.ConceptDescription;
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.basyx.core.exceptions.CollidingIdentifierException;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.exceptions.MissingIdentifierException;
import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;

/**
 * Specifies the overall ConceptDescriptionRepository API
 * 
 * @author danish, kammognie
 *
 */
public interface ConceptDescriptionRepository {

	/**
	 * Retrieves all ConceptDescriptions from the repository
	 * 
	 * @return a list of all found ConceptDescriptions
	 */
	public CursorResult<List<ConceptDescription>> getAllConceptDescriptions(PaginationInfo pInfo);

	/**
	 * Retrieves all ConceptDescriptions from the repository matching the passed
	 * idShort
	 * 
	 * @param idShort
	 * @return a list of all matched ConceptDescriptions
	 */
	public CursorResult<List<ConceptDescription>> getAllConceptDescriptionsByIdShort(String idShort, PaginationInfo pInfo);

	/**
	 * Retrieves all ConceptDescriptions from the repository matching the passed
	 * isCaseOf
	 * 
	 * @param isCaseOf
	 * @return a list of all matched ConceptDescriptions
	 */
	public CursorResult<List<ConceptDescription>> getAllConceptDescriptionsByIsCaseOf(Reference isCaseOf, PaginationInfo pInfo);

	/**
	 * Retrieves all ConceptDescriptions from the repository matching the passed
	 * dataSpecificationReference
	 * 
	 * @param dataSpecificationReference
	 * @return a list of all matched ConceptDescriptions
	 */
	public CursorResult<List<ConceptDescription>> getAllConceptDescriptionsByDataSpecificationReference(Reference dataSpecificationReference, PaginationInfo pInfo);

	/**
	 * Retrieves the ConceptDescription with the specific id
	 * 
	 * @param conceptDescriptionId
	 * @return the requested ConceptDescription
	 * @throws ElementDoesNotExistException
	 */
	public ConceptDescription getConceptDescription(String conceptDescriptionId) throws ElementDoesNotExistException;

	/**
	 * Updates an existing ConceptDescription
	 * 
	 * @param conceptDescriptionId
	 * @param conceptDescription
	 * @throws ElementDoesNotExistException
	 */
	public void updateConceptDescription(String conceptDescriptionId, ConceptDescription conceptDescription) throws ElementDoesNotExistException;

	/**
	 * Creates a new ConceptDescription
	 * 
	 * @param conceptDescription
	 * @throws CollidingIdentifierException
	 * @throws MissingIdentifierException
	 */
	public void createConceptDescription(ConceptDescription conceptDescription) throws CollidingIdentifierException, MissingIdentifierException;

	/**
	 * Deletes a ConceptDescription
	 * 
	 * @param conceptDescriptionId
	 * @throws ElementDoesNotExistException
	 */
	public void deleteConceptDescription(String conceptDescriptionId) throws ElementDoesNotExistException;

	/**
	 * Returns the name of the repository
	 * 
	 * @return repoName
	 */
	public default String getName() {
		return "cd-repo";
	}

}

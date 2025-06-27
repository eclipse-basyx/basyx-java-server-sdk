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

package org.eclipse.digitaltwin.basyx.submodelrepository;

import java.io.InputStream;
import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.model.OperationVariable;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.basyx.core.exceptions.CollidingIdentifierException;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementNotAFileException;
import org.eclipse.digitaltwin.basyx.core.exceptions.FileDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.exceptions.MissingIdentifierException;
import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.eclipse.digitaltwin.basyx.http.Base64UrlEncodedIdentifier;
import org.eclipse.digitaltwin.basyx.submodelservice.value.SubmodelElementValue;
import org.eclipse.digitaltwin.basyx.submodelservice.value.SubmodelValueOnly;

/**
 * Specifies the overall SubmodelRepository API
 * 
 * @author schnicke, danish, kammognie, fried
 *
 */
public interface SubmodelRepository {

	/**
	 * Retrieves all Submodels from the repository
	 * 
	 * @return a list of all found Submodels
	 */
	public CursorResult<List<Submodel>> getAllSubmodels(PaginationInfo pInfo);
	
	/**
	 * Retrieves all Submodels from the repository filtered by the Semantic ID
	 * 
	 * @return a list of all found Submodels with common Semantic ID
	 */
	public CursorResult<List<Submodel>> getAllSubmodels(String semanticId, PaginationInfo pInfo);
	
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
	public void createSubmodel(Submodel submodel) throws CollidingIdentifierException, MissingIdentifierException;
	
	/**
	 * Updates a SubmodelElement
	 * 
	 * @param submodelIdentifier
	 * @param idShortPath
	 * @param submodelElement
	 * @throws ElementDoesNotExistException
	 */
	public void updateSubmodelElement(String submodelIdentifier, String idShortPath, SubmodelElement submodelElement) throws ElementDoesNotExistException;

	/**
	 * Deletes a Submodel
	 * 
	 * @param submodelId
	 * @throws ElementDoesNotExistException
	 */
	public void deleteSubmodel(String submodelId) throws ElementDoesNotExistException;

	/**
	 * Retrieves all SubmodelElements of a Submodel
	 * 
	 * @param submodelId
	 * @return
	 */
	public CursorResult<List<SubmodelElement>> getSubmodelElements(String submodelId, PaginationInfo pInfo) throws ElementDoesNotExistException;

	/**
	 * Retrieves a specific SubmodelElement of a Submodel
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

	/**
	 * Retrieves the value of a specific SubmodelElement of a Submodel
	 * 
	 * @param submodelId
	 *            the Submodel id
	 * @param smeIdShort
	 *            the SubmodelElement IdShort
	 * @return the SubmodelElement
	 * @throws ElementDoesNotExistException
	 *             if the SubmodelElement or the Submodel does not exist
	 */
	public SubmodelElementValue getSubmodelElementValue(String submodelId, String smeIdShort) throws ElementDoesNotExistException;

	/**
	 * Sets the value of a specific SubmodelElement of a Submodel
	 * 
	 * @param submodelId
	 *            the Submodel id
	 * @param smeIdShort
	 *            the SubmodelElement IdShort
	 * @param value
	 *            the new value
	 * @throws ElementDoesNotExistException
	 *             if the SubmodelElement or the Submodel does not exist
	 */
	public void setSubmodelElementValue(String submodelId, String smeIdShort, SubmodelElementValue value) throws ElementDoesNotExistException;

	/**
	 * Creates a SubmodelElement in a Submodel
	 * 
	 * @param submodelId
	 *            the Submodel id
	 * @param smElement
	 *            the SubmodelElement
	 */
	public void createSubmodelElement(String submodelId, SubmodelElement smElement);

	/**
	 * Creates a nested SubmodelElement
	 * 
	 * @param submodelId
	 *            the Submodel id
	 * @param idShortPath
	 *            the SubmodelElement IdShort
	 * @param smElement
	 *            the SubmodelElement
	 */
	public void createSubmodelElement(String submodelId, String idShortPath, SubmodelElement smElement) throws ElementDoesNotExistException;

	/**
	 * Deletes a SubmodelElement
	 * 
	 * @param submodelId
	 *            the Submodel id
	 * @param idShortPath
	 *            the SubmodelElement IdShort
	 */
	public void deleteSubmodelElement(String submodelId, String idShortPath) throws ElementDoesNotExistException;

	/**
	 * Returns the name of the repository
	 * 
	 * @return repoName
	 */
	public default String getName() {
		return "sm-repo";
	}

	/**
	 * Invokes an operation
	 * 
	 * @param submodelId
	 *            the Submodel id
	 * @param idShortPath
	 *            the Operation IdShortPath
	 * @param input
	 *            value to be passed to the invoked operation
	 * @throws ElementDoesNotExistException
	 *             If the operation defined in the idShortPath does not exist
	 * @return
	 */
	public OperationVariable[] invokeOperation(String submodelId, String idShortPath, OperationVariable[] input) throws ElementDoesNotExistException;

	/**
	 * Retrieves the Submodel as Value-Only_representation with the specific id
	 * 
	 * @param submodelId
	 * @return
	 * @throws ElementDoesNotExistException
	 */
	public SubmodelValueOnly getSubmodelByIdValueOnly(String submodelId) throws ElementDoesNotExistException;

	/**
	 * Retrieves only the Metadata of a submodel
	 * 
	 * @param submodelId
	 * @return
	 * @throws ElementDoesNotExistException
	 */
	public Submodel getSubmodelByIdMetadata(String submodelId) throws ElementDoesNotExistException;

	/**
	 * Retrieves the file of a file submodelelement
	 * 
	 * @param submodelId
	 *            the Submodel id
	 * @param idShortPath
	 *            the IdShort path of the file element
	 * @return
	 * 
	 * @throws ElementDoesNotExistException
	 * @throws ElementNotAFileException
	 * @throws FileDoesNotExistException
	 */
	public java.io.File getFileByPathSubmodel(String submodelId, String idShortPath) throws ElementDoesNotExistException, ElementNotAFileException, FileDoesNotExistException;

	/**
	 * Uploads a file to a file submodelelement
	 * 
	 * @param submodelId
	 *            the Submodel id
	 * @param idShortPath
	 *            the IdShort path of the file element
	 * @param fileName
	 *            the file name
	 * @param contentType
	 *            the content type of the file
	 * @param inputStream
	 *            the inputStream of the file to be uploaded
	 * 
	 * @throws ElementDoesNotExistException
	 * @throws ElementNotAFileException
	 */
	public void setFileValue(String submodelId, String idShortPath, String fileName, String contentType, InputStream inputStream) throws ElementDoesNotExistException, ElementNotAFileException;

	/**
	 * Deletes the file of a file submodelelement
	 * 
	 * @param submodelId
	 *            the Submodel id
	 * @param idShortPath
	 *            the IdShort path of the file element
	 * 
	 * @throws ElementDoesNotExistException
	 * @throws ElementNotAFileException
	 * @throws FileDoesNotExistException
	 */
	public void deleteFileValue(String submodelId, String idShortPath) throws ElementDoesNotExistException, ElementNotAFileException, FileDoesNotExistException;

	/**
	 * Replaces the submodel elements in a submodel
	 * 
	 * @param submodelId
	 *           the Submodel id
	 * @param submodelElementList
	 */
	public void patchSubmodelElements(String submodelId, List<SubmodelElement> submodelElementList);

	/**
	 * Retrieves the file of a file submodelelement via its absolute path
	 *
	 * @param submodelId
	 * 			  the Submodel id
	 * @param filePath
	 *            the path of the file
	 * @return File InputStream
	 */
	public InputStream getFileByFilePath(String submodelId, String filePath);
}

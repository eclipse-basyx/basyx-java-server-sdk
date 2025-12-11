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

package org.eclipse.digitaltwin.basyx.submodelservice;

import java.io.InputStream;
import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.model.OperationVariable;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementNotAFileException;
import org.eclipse.digitaltwin.basyx.core.exceptions.FileDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.eclipse.digitaltwin.basyx.submodelservice.value.SubmodelElementValue;

/**
 * Specifies the overall SubmodelService API
 * 
 * @author schnicke, mateusmolina
 *
 */
public interface SubmodelService {
	/**
	 * Retrieves the Submodel contained in the service
	 * 
	 * @return
	 */
	public Submodel getSubmodel();

	/**
	 * Retrieves all submodelElements contained in the Submodel
	 * 
	 * @return
	 */
	public CursorResult<List<SubmodelElement>> getSubmodelElements(PaginationInfo pInfo);

	/**
	 * Retrieve specific SubmodelElement of the Submodel
	 *
	 * @param idShortPath the SubmodelElement IdShort
	 * @return the SubmodelElement
	 * @throws ElementDoesNotExistException if the SubmodelElement does not exist
	 */
	public SubmodelElement getSubmodelElement(String idShortPath) throws ElementDoesNotExistException;

	/**
	 * Retrieves the value of a specific SubmodelElement of the Submodel
	 * 
	 * @param idShortPath
	 *            the SubmodelElement idShortPath
	 * @return the SubmodelElementValue
	 * @throws ElementDoesNotExistException
	 *             if the SubmodelElement does not exist
	 */
	public SubmodelElementValue getSubmodelElementValue(String idShortPath) throws ElementDoesNotExistException;

	/**
	 * Sets the value of a specific SubmodelElement of the Submodel
	 * 
	 * @param idShortPath the SubmodelElement IdShortPath
	 * @param value       the new value
	 * @throws ElementDoesNotExistException if the SubmodelElement does not exist
	 */
	public void setSubmodelElementValue(String idShortPath, SubmodelElementValue value) throws ElementDoesNotExistException;

	/**
	 * Creates a Submodel Element
	 * 
	 */
	public void createSubmodelElement(SubmodelElement submodelElement);

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
	public void createSubmodelElement(String idShortPath, SubmodelElement submodelElement) throws ElementDoesNotExistException;
	
	/**
	 * Updates a submodel element
	 * 
	 * @param idShortPath
	 * @param submodelElement
	 * @throws ElementDoesNotExistException
	 *             If the submodel element defined in the path does not exist
	 */
	public void updateSubmodelElement(String idShortPath, SubmodelElement submodelElement) throws ElementDoesNotExistException;

	/**
	 * Delete a submodel element in a submodel
	 * 
	 * @param idShortPath
	 *            the SubmodelElement IdShortPath
	 * @throws ElementDoesNotExistException
	 *             If the submodel element defined in the path does not exist
	 */
	public void deleteSubmodelElement(String idShortPath) throws ElementDoesNotExistException;

	/**
	 * Replaces the submodel elements in a submodel
	 * 
	 * @param submodelElementList
	 */
	public void patchSubmodelElements(List<SubmodelElement> submodelElementList);

	/**
	 * Invokes an operation
	 * 
	 * @param idShortPath
	 *            the Operation IdShortPath
	 * @param input
	 *            value to be passed to the invoked operation
	 * @throws ElementDoesNotExistException
	 *             If the operation defined in the idShortPath does not exist
	 * @return
	 */
	public OperationVariable[] invokeOperation(String idShortPath, OperationVariable[] input) throws ElementDoesNotExistException;

	/**
	 * Retrieves the file of a file submodelelement
	 * 
	 * @param idShortPath
	 *            the IdShort path of the file element
	 * 
	 * @throws ElementDoesNotExistException
	 * @throws ElementNotAFileException
	 * @throws FileDoesNotExistException
	 */
	public java.io.File getFileByPath(String idShortPath) throws ElementDoesNotExistException, ElementNotAFileException, FileDoesNotExistException;

	/**
	 * Uploads a file to a file submodelelement
	 * 
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
	public void setFileValue(String idShortPath, String fileName, String contentType, InputStream inputStream) throws ElementDoesNotExistException, ElementNotAFileException;

	/**
	 * Deletes the file of a file submodelelement
	 * 
	 * @param idShortPath
	 *            the IdShort path of the file element
	 * 
	 * @throws ElementDoesNotExistException
	 * @throws ElementNotAFileException
	 * @throws FileDoesNotExistException
	 */
	public void deleteFileValue(String idShortPath) throws ElementDoesNotExistException, ElementNotAFileException, FileDoesNotExistException;


	/**
	 * Retrieves the file of a file submodelelement via its absolute path
	 *
	 * @param filePath
	 *            the path of the file
	 * @return File InputStream
	 */
	public InputStream getFileByFilePath(String filePath);
}

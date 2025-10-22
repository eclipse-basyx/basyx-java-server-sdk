/*******************************************************************************
 * Copyright (C) 2024 DFKI GmbH (https://www.dfki.de/en/web)
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
 * 
 ******************************************************************************/
package org.eclipse.digitaltwin.basyx.submodelservice.feature.operationdispatching;

import java.io.File;
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
import org.eclipse.digitaltwin.basyx.submodelservice.SubmodelService;
import org.eclipse.digitaltwin.basyx.submodelservice.value.SubmodelElementValue;
/**
 * @author Gerhard Sonnenberg DFKI GmbH
 */
public class AbstractSubmodelServiceDecorator implements SubmodelService {

	private final SubmodelService decorated;

	public AbstractSubmodelServiceDecorator(SubmodelService decorated) {
		this.decorated = decorated;
	}

	@Override
	public Submodel getSubmodel() {
		return decorated.getSubmodel();
	}

	@Override
	public CursorResult<List<SubmodelElement>> getSubmodelElements(PaginationInfo pInfo) {
		return decorated.getSubmodelElements(pInfo);
	}

	@Override
	public SubmodelElement getSubmodelElement(String idShortPath) throws ElementDoesNotExistException {
		return decorated.getSubmodelElement(idShortPath);
	}

	@Override
	public SubmodelElementValue getSubmodelElementValue(String idShortPath) throws ElementDoesNotExistException {
		return decorated.getSubmodelElementValue(idShortPath);
	}

	@Override
	public void setSubmodelElementValue(String idShortPath, SubmodelElementValue value)
			throws ElementDoesNotExistException {
		decorated.setSubmodelElementValue(idShortPath, value);
	}

	@Override
	public void createSubmodelElement(SubmodelElement submodelElement) {
		decorated.createSubmodelElement(submodelElement);
	}

	@Override
	public void createSubmodelElement(String idShortPath, SubmodelElement submodelElement)
			throws ElementDoesNotExistException {
		decorated.createSubmodelElement(idShortPath, submodelElement);
	}

	@Override
	public void updateSubmodelElement(String idShortPath, SubmodelElement submodelElement)
			throws ElementDoesNotExistException {
		decorated.updateSubmodelElement(idShortPath, submodelElement);
	}

	@Override
	public void deleteSubmodelElement(String idShortPath) throws ElementDoesNotExistException {
		decorated.deleteSubmodelElement(idShortPath);
	}

	@Override
	public void patchSubmodelElements(List<SubmodelElement> submodelElementList) {
		decorated.patchSubmodelElements(submodelElementList);
	}

	@Override
	public OperationVariable[] invokeOperation(String idShortPath, OperationVariable[] input)
			throws ElementDoesNotExistException {
		return decorated.invokeOperation(idShortPath, input);
	}

	@Override
	public File getFileByPath(String idShortPath)
			throws ElementDoesNotExistException, ElementNotAFileException, FileDoesNotExistException {
		return decorated.getFileByPath(idShortPath);
	}

	@Override
	public void setFileValue(String idShortPath, String fileName, String contentType, InputStream inputStream)
			throws ElementDoesNotExistException, ElementNotAFileException {
		decorated.setFileValue(idShortPath, fileName, contentType, inputStream);
	}

	@Override
	public void deleteFileValue(String idShortPath)
			throws ElementDoesNotExistException, ElementNotAFileException, FileDoesNotExistException {
		decorated.deleteFileValue(idShortPath);
	}

	@Override
	public InputStream getFileByFilePath(String filePath) {
		return decorated.getFileByFilePath(filePath);
	}
}
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

package org.eclipse.digitaltwin.basyx.submodelrepository.core;

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
import org.eclipse.digitaltwin.basyx.submodelrepository.SubmodelRepository;
import org.eclipse.digitaltwin.basyx.submodelservice.SubmodelService;
import org.eclipse.digitaltwin.basyx.submodelservice.value.SubmodelElementValue;

/**
 * A Wrapper class for a SubmodelRepository. This is solely to remove redundancy
 * in Test Suites.
 * 
 * @author fried
 */
public class SubmodelRepositorySubmodelServiceWrapper implements SubmodelService {

	private SubmodelRepository repoApi;
	private String submodelId;

	public SubmodelRepositorySubmodelServiceWrapper(SubmodelRepository repo, Submodel submodel) {
		this.repoApi = repo;
		this.submodelId = submodel.getId();
		repoApi.createSubmodel(submodel);
	}

	@Override
	public Submodel getSubmodel() {
		return repoApi.getSubmodel(submodelId);
	}

	@Override
	public CursorResult<List<SubmodelElement>> getSubmodelElements(PaginationInfo pInfo) {
		return repoApi.getSubmodelElements(submodelId, pInfo);
	}

	@Override
	public SubmodelElement getSubmodelElement(String idShortPath) throws ElementDoesNotExistException {
		return repoApi.getSubmodelElement(submodelId, idShortPath);
	}

	@Override
	public SubmodelElementValue getSubmodelElementValue(String idShortPath) throws ElementDoesNotExistException {
		return repoApi.getSubmodelElementValue(submodelId, idShortPath);
	}

	@Override
	public void setSubmodelElementValue(String idShortPath, SubmodelElementValue value)
			throws ElementDoesNotExistException {
		repoApi.setSubmodelElementValue(submodelId, idShortPath, value);
		
	}

	@Override
	public void createSubmodelElement(SubmodelElement submodelElement) {
		repoApi.createSubmodelElement(submodelId, submodelElement);
	}

	@Override
	public void createSubmodelElement(String idShortPath, SubmodelElement submodelElement)
			throws ElementDoesNotExistException {
		repoApi.createSubmodelElement(submodelId, idShortPath, submodelElement);
		
	}

	@Override
	public void updateSubmodelElement(String idShortPath, SubmodelElement submodelElement)
			throws ElementDoesNotExistException {
		repoApi.updateSubmodelElement(submodelId, idShortPath, submodelElement);
	}

	@Override
	public void deleteSubmodelElement(String idShortPath) throws ElementDoesNotExistException {
		repoApi.deleteSubmodelElement(submodelId, idShortPath);
	}

	@Override
	public OperationVariable[] invokeOperation(String idShortPath, OperationVariable[] input)
			throws ElementDoesNotExistException {
		return repoApi.invokeOperation(submodelId, idShortPath, input);
	}

	@Override
	public void patchSubmodelElements(List<SubmodelElement> submodelElementList) {
		repoApi.patchSubmodelElements(submodelId, submodelElementList);
	}

	@Override
	public File getFileByPath(String idShortPath) throws ElementDoesNotExistException, ElementNotAFileException, FileDoesNotExistException {
		return repoApi.getFileByPathSubmodel(submodelId, idShortPath);
	}

	@Override
	public void setFileValue(String idShortPath, String fileName, String contentType, InputStream inputStream) throws ElementDoesNotExistException, ElementNotAFileException {
		repoApi.setFileValue(submodelId, idShortPath, fileName, contentType, inputStream);
	}

	@Override
	public void deleteFileValue(String idShortPath) throws ElementDoesNotExistException, ElementNotAFileException, FileDoesNotExistException {
		repoApi.deleteFileValue(submodelId, idShortPath);
	}

	@Override
	public InputStream getFileByFilePath(String filePath) {
		return repoApi.getFileByFilePath(submodelId, filePath);
	}

}

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


package org.eclipse.digitaltwin.basyx.submodelrepository.client;

import java.io.File;
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
import org.eclipse.digitaltwin.basyx.submodelrepository.SubmodelRepository;
import org.eclipse.digitaltwin.basyx.submodelservice.value.SubmodelElementValue;
import org.eclipse.digitaltwin.basyx.submodelservice.value.SubmodelValueOnly;

public class ConnectedSubmodelRepositoryWrapper implements SubmodelRepository {

	ConnectedSubmodelRepository repo;

	public ConnectedSubmodelRepositoryWrapper(ConnectedSubmodelRepository repo) {
		this.repo = repo;
	}

	@Override
	public CursorResult<List<Submodel>> getAllSubmodels(PaginationInfo pInfo) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Submodel getSubmodel(String submodelId) throws ElementDoesNotExistException {
		return repo.getSubmodel(submodelId);
	}

	@Override
	public void updateSubmodel(String submodelId, Submodel submodel) throws ElementDoesNotExistException {
		repo.updateSubmodel(submodelId, submodel);

	}

	@Override
	public void createSubmodel(Submodel submodel) throws CollidingIdentifierException, MissingIdentifierException {
		repo.createSubmodel(submodel);
	}

	@Override
	public void updateSubmodelElement(String submodelIdentifier, String idShortPath, SubmodelElement submodelElement) throws ElementDoesNotExistException {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteSubmodel(String submodelId) throws ElementDoesNotExistException {
		repo.deleteSubmodel(submodelId);
	}

	@Override
	public CursorResult<List<SubmodelElement>> getSubmodelElements(String submodelId, PaginationInfo pInfo) throws ElementDoesNotExistException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SubmodelElement getSubmodelElement(String submodelId, String smeIdShort) throws ElementDoesNotExistException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SubmodelElementValue getSubmodelElementValue(String submodelId, String smeIdShort) throws ElementDoesNotExistException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setSubmodelElementValue(String submodelId, String smeIdShort, SubmodelElementValue value) throws ElementDoesNotExistException {
		// TODO Auto-generated method stub

	}

	@Override
	public void createSubmodelElement(String submodelId, SubmodelElement smElement) {
		// TODO Auto-generated method stub

	}

	@Override
	public void createSubmodelElement(String submodelId, String idShortPath, SubmodelElement smElement) throws ElementDoesNotExistException {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteSubmodelElement(String submodelId, String idShortPath) throws ElementDoesNotExistException {
		// TODO Auto-generated method stub

	}

	@Override
	public OperationVariable[] invokeOperation(String submodelId, String idShortPath, OperationVariable[] input) throws ElementDoesNotExistException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SubmodelValueOnly getSubmodelByIdValueOnly(String submodelId) throws ElementDoesNotExistException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Submodel getSubmodelByIdMetadata(String submodelId) throws ElementDoesNotExistException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public File getFileByPathSubmodel(String submodelId, String idShortPath) throws ElementDoesNotExistException, ElementNotAFileException, FileDoesNotExistException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setFileValue(String submodelId, String idShortPath, String fileName, InputStream inputStream) throws ElementDoesNotExistException, ElementNotAFileException {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteFileValue(String submodelId, String idShortPath) throws ElementDoesNotExistException, ElementNotAFileException, FileDoesNotExistException {
		// TODO Auto-generated method stub

	}

}

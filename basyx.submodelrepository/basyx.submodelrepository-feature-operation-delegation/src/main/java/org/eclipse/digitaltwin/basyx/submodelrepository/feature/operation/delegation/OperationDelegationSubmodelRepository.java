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

package org.eclipse.digitaltwin.basyx.submodelrepository.feature.operation.delegation;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import org.eclipse.digitaltwin.aas4j.v3.model.OperationVariable;
import org.eclipse.digitaltwin.aas4j.v3.model.Qualifier;
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

/**
 * Decorator for {@link SubmodelRepository} to delegate the operation request
 *
 * @author danish, marie
 *
 */
public class OperationDelegationSubmodelRepository implements SubmodelRepository {

	private SubmodelRepository decorated;
	private OperationDelegation operationDelegation;

	public OperationDelegationSubmodelRepository(SubmodelRepository decorated, OperationDelegation operationDelegation) {
		this.decorated = decorated;
		this.operationDelegation = operationDelegation;
	}

	@Override
	public CursorResult<List<Submodel>> getAllSubmodels(PaginationInfo pInfo) {
		return decorated.getAllSubmodels(pInfo);
	}
	
	@Override
	public CursorResult<List<Submodel>> getAllSubmodels(String semanticId, PaginationInfo pInfo) {
		return decorated.getAllSubmodels(semanticId, pInfo);
	}

	@Override
	public Submodel getSubmodel(String submodelId) throws ElementDoesNotExistException {
		return decorated.getSubmodel(submodelId);
	}

	@Override
	public void updateSubmodel(String submodelId, Submodel submodel) throws ElementDoesNotExistException {
		decorated.updateSubmodel(submodelId, submodel);
	}

	@Override
	public void createSubmodel(Submodel submodel) throws CollidingIdentifierException, MissingIdentifierException {
		decorated.createSubmodel(submodel);
	}

	@Override
	public void updateSubmodelElement(String submodelIdentifier, String idShortPath, SubmodelElement submodelElement) throws ElementDoesNotExistException {
		decorated.updateSubmodelElement(submodelIdentifier, idShortPath, submodelElement);
	}

	@Override
	public void deleteSubmodel(String submodelId) throws ElementDoesNotExistException {
		decorated.deleteSubmodel(submodelId);
	}

	@Override
	public CursorResult<List<SubmodelElement>> getSubmodelElements(String submodelId, PaginationInfo pInfo) throws ElementDoesNotExistException {
		return decorated.getSubmodelElements(submodelId, pInfo);
	}

	@Override
	public SubmodelElement getSubmodelElement(String submodelId, String smeIdShort) throws ElementDoesNotExistException {
		return decorated.getSubmodelElement(submodelId, smeIdShort);
	}

	@Override
	public SubmodelElementValue getSubmodelElementValue(String submodelId, String smeIdShort) throws ElementDoesNotExistException {
		return decorated.getSubmodelElementValue(submodelId, smeIdShort);
	}

	@Override
	public void setSubmodelElementValue(String submodelId, String smeIdShort, SubmodelElementValue value) throws ElementDoesNotExistException {
		decorated.setSubmodelElementValue(submodelId, smeIdShort, value);
	}

	@Override
	public void createSubmodelElement(String submodelId, SubmodelElement smElement) {
		decorated.createSubmodelElement(submodelId, smElement);
	}

	@Override
	public void createSubmodelElement(String submodelId, String idShortPath, SubmodelElement smElement) throws ElementDoesNotExistException {
		decorated.createSubmodelElement(submodelId, idShortPath, smElement);
	}

	@Override
	public void deleteSubmodelElement(String submodelId, String idShortPath) throws ElementDoesNotExistException {
		decorated.deleteSubmodelElement(submodelId, idShortPath);
	}

	@Override
	public OperationVariable[] invokeOperation(String submodelId, String idShortPath, OperationVariable[] input) throws ElementDoesNotExistException {
		SubmodelElement submodelElement = getSubmodelElement(submodelId, idShortPath);
		
		Optional<Qualifier> optionalQualifier = submodelElement.getQualifiers().stream().filter(qualifier -> qualifier.getType().equals(HTTPOperationDelegation.INVOCATION_DELEGATION_TYPE)).findAny();
		
		if (!optionalQualifier.isPresent())
			return decorated.invokeOperation(submodelId, idShortPath, input);
		
		return operationDelegation.delegate(optionalQualifier.get(), input);
	}

	@Override
	public SubmodelValueOnly getSubmodelByIdValueOnly(String submodelId) throws ElementDoesNotExistException {
		return decorated.getSubmodelByIdValueOnly(submodelId);
	}

	@Override
	public Submodel getSubmodelByIdMetadata(String submodelId) throws ElementDoesNotExistException {
		return decorated.getSubmodelByIdMetadata(submodelId);
	}

	@Override
	public File getFileByPathSubmodel(String submodelId, String idShortPath) throws ElementDoesNotExistException, ElementNotAFileException, FileDoesNotExistException {
		return decorated.getFileByPathSubmodel(submodelId, idShortPath);
	}

	@Override
	public void setFileValue(String submodelId, String idShortPath, String fileName, String contentType, InputStream inputStream) throws ElementDoesNotExistException, ElementNotAFileException {
		decorated.setFileValue(submodelId, idShortPath, fileName, contentType, inputStream);
		
	}

	@Override
	public void deleteFileValue(String submodelId, String idShortPath) throws ElementDoesNotExistException, ElementNotAFileException, FileDoesNotExistException {
		decorated.deleteFileValue(submodelId, idShortPath);
	}
	
	@Override
	public String getName() {
		return decorated.getName();
	}

	@Override
	public void patchSubmodelElements(String submodelId, List<SubmodelElement> submodelElementList) {
		decorated.patchSubmodelElements(submodelId, submodelElementList);
	}

	@Override
	public InputStream getFileByFilePath(String submodelId, String filePath) {
		return decorated.getFileByFilePath(submodelId, filePath);
	}

}

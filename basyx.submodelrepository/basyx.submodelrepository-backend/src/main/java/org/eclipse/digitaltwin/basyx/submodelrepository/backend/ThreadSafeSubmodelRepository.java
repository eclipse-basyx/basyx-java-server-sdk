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

package org.eclipse.digitaltwin.basyx.submodelrepository.backend;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.model.OperationVariable;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.basyx.common.backend.InstanceScopedThreadSafeAccess;
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
 * A thread-safe wrapper for the {@link SubmodelRepository}
 * 
 * @author mateusmolina
 */
public class ThreadSafeSubmodelRepository implements SubmodelRepository {

    private final InstanceScopedThreadSafeAccess access = new InstanceScopedThreadSafeAccess();
    private final SubmodelRepository decoratedRepository;

    public ThreadSafeSubmodelRepository(SubmodelRepository submodelRepository) {
        this.decoratedRepository = submodelRepository;
    }

    @Override
    public CursorResult<List<Submodel>> getAllSubmodels(PaginationInfo pInfo) {
        return decoratedRepository.getAllSubmodels(pInfo);
    }

    @Override
    public CursorResult<List<Submodel>> getAllSubmodels(String semanticId, PaginationInfo pInfo) {
        return decoratedRepository.getAllSubmodels(semanticId, pInfo);
    }

    @Override
    public Submodel getSubmodel(String submodelId) throws ElementDoesNotExistException {
        return access.read(() -> decoratedRepository.getSubmodel(submodelId), submodelId);
    }

    @Override
    public void updateSubmodel(String submodelId, Submodel submodel) throws ElementDoesNotExistException {
        access.write(() -> decoratedRepository.updateSubmodel(submodelId, submodel), submodelId);
    }

    @Override
    public void createSubmodel(Submodel submodel) throws CollidingIdentifierException, MissingIdentifierException {
        decoratedRepository.createSubmodel(submodel);
    }

    @Override
    public void updateSubmodelElement(String submodelIdentifier, String idShortPath, SubmodelElement submodelElement) throws ElementDoesNotExistException {
        access.write(() -> decoratedRepository.updateSubmodelElement(submodelIdentifier, idShortPath, submodelElement), submodelIdentifier);
    }

    @Override
    public void deleteSubmodel(String submodelId) throws ElementDoesNotExistException {
        access.write(() -> decoratedRepository.deleteSubmodel(submodelId), submodelId);
    }

    @Override
    public CursorResult<List<SubmodelElement>> getSubmodelElements(String submodelId, PaginationInfo pInfo) throws ElementDoesNotExistException {
        return access.read(() -> decoratedRepository.getSubmodelElements(submodelId, pInfo), submodelId);
    }

    @Override
    public SubmodelElement getSubmodelElement(String submodelId, String smeIdShort) throws ElementDoesNotExistException {
        return access.read(() -> decoratedRepository.getSubmodelElement(submodelId, smeIdShort), submodelId);
    }

    @Override
    public SubmodelElementValue getSubmodelElementValue(String submodelId, String smeIdShort) throws ElementDoesNotExistException {
        return access.read(() -> decoratedRepository.getSubmodelElementValue(submodelId, smeIdShort), submodelId);
    }

    @Override
    public void setSubmodelElementValue(String submodelId, String smeIdShort, SubmodelElementValue value) throws ElementDoesNotExistException {
        access.write(() -> decoratedRepository.setSubmodelElementValue(submodelId, smeIdShort, value), submodelId);
    }

    @Override
    public void createSubmodelElement(String submodelId, SubmodelElement smElement) {
        access.write(() -> decoratedRepository.createSubmodelElement(submodelId, smElement), submodelId);
    }

    @Override
    public void createSubmodelElement(String submodelId, String idShortPath, SubmodelElement smElement) throws ElementDoesNotExistException {
        access.write(() -> decoratedRepository.createSubmodelElement(submodelId, idShortPath, smElement), submodelId);
    }

    @Override
    public void deleteSubmodelElement(String submodelId, String idShortPath) throws ElementDoesNotExistException {
        access.write(() -> decoratedRepository.deleteSubmodelElement(submodelId, idShortPath), submodelId);
    }

    @Override
    public OperationVariable[] invokeOperation(String submodelId, String idShortPath, OperationVariable[] input) throws ElementDoesNotExistException {
        return decoratedRepository.invokeOperation(submodelId, idShortPath, input);
    }

    @Override
    public SubmodelValueOnly getSubmodelByIdValueOnly(String submodelId) throws ElementDoesNotExistException {
        return access.read(() -> decoratedRepository.getSubmodelByIdValueOnly(submodelId), submodelId);
    }

    @Override
    public Submodel getSubmodelByIdMetadata(String submodelId) throws ElementDoesNotExistException {
        return access.read(() -> decoratedRepository.getSubmodelByIdMetadata(submodelId), submodelId);
    }

    @Override
    public File getFileByPathSubmodel(String submodelId, String idShortPath) throws ElementDoesNotExistException, ElementNotAFileException, FileDoesNotExistException {
        return access.read(() -> decoratedRepository.getFileByPathSubmodel(submodelId, idShortPath), submodelId);
    }

    @Override
    public void setFileValue(String submodelId, String idShortPath, String fileName, InputStream inputStream) throws ElementDoesNotExistException, ElementNotAFileException {
        access.write(() -> decoratedRepository.setFileValue(submodelId, idShortPath, fileName, inputStream), submodelId);
    }

    @Override
    public void deleteFileValue(String submodelId, String idShortPath) throws ElementDoesNotExistException, ElementNotAFileException, FileDoesNotExistException {
        access.write(() -> decoratedRepository.deleteFileValue(submodelId, idShortPath), submodelId);
    }

    @Override
    public void patchSubmodelElements(String submodelId, List<SubmodelElement> submodelElementList) {
        access.write(() -> decoratedRepository.patchSubmodelElements(submodelId, submodelElementList), submodelId);
    }

    @Override
    public InputStream getFileByFilePath(String submodelId, String filePath) {
        return access.read(() -> decoratedRepository.getFileByFilePath(submodelId, filePath), submodelId);
    }

    @Override
    public String getName() {
        return decoratedRepository.getName();
    }
}

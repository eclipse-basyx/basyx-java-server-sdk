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

import org.eclipse.digitaltwin.aas4j.v3.model.OperationVariable;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementNotAFileException;
import org.eclipse.digitaltwin.basyx.core.exceptions.FileDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.exceptions.NotInvokableException;
import org.eclipse.digitaltwin.basyx.core.filerepository.FileRepository;
import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.eclipse.digitaltwin.basyx.operation.InvokableOperation;
import org.eclipse.digitaltwin.basyx.submodelservice.SubmodelService;
import org.eclipse.digitaltwin.basyx.submodelservice.value.SubmodelElementValue;
import org.springframework.lang.NonNull;

import java.io.File;
import java.io.InputStream;
import java.util.List;

/**
 * Default {@link SubmodelService} based on {@link SubmodelBackend}
 * 
 * @author mateusmolina
 */
public class CrudSubmodelService implements SubmodelService {

    private final SubmodelBackend backend;
    private final String submodelId;
    private final SubmodelFileOperations submodelFileOperations;

    public CrudSubmodelService(SubmodelBackend submodelRepositoryBackend, FileRepository fileRepository, @NonNull Submodel submodel) {
        this(submodelRepositoryBackend, fileRepository, submodel.getId());
        hostSubmodel(submodel);
    }

    public CrudSubmodelService(SubmodelBackend submodelRepositoryBackend, FileRepository fileRepository, @NonNull String submodelId) {
        this.backend = submodelRepositoryBackend;
        this.submodelId = submodelId;
        this.submodelFileOperations = new SubmodelFileOperations(fileRepository, submodelRepositoryBackend);
    }

    @Override
    public Submodel getSubmodel() {
        return this.backend.findById(this.submodelId).orElseThrow(ElementDoesNotExistException::new);
    }

    @Override
    public CursorResult<List<SubmodelElement>> getSubmodelElements(PaginationInfo pInfo) {
        return backend.getSubmodelElements(submodelId, pInfo);
    }

    @Override
    public SubmodelElement getSubmodelElement(String idShortPath) throws ElementDoesNotExistException {
        return backend.getSubmodelElement(submodelId, idShortPath);
    }

    @Override
    public SubmodelElementValue getSubmodelElementValue(String idShortPath) throws ElementDoesNotExistException {
        return backend.getSubmodelElementValue(submodelId, idShortPath);
    }

    @Override
    public void setSubmodelElementValue(String idShortPath, SubmodelElementValue value) throws ElementDoesNotExistException {
        backend.setSubmodelElementValue(submodelId, idShortPath, value);
    }

    @Override
    public void createSubmodelElement(SubmodelElement submodelElement) {
        backend.createSubmodelElement(submodelId, submodelElement);
    }

    @Override
    public void createSubmodelElement(String idShortPath, SubmodelElement submodelElement) throws ElementDoesNotExistException {
        backend.createSubmodelElement(submodelId, idShortPath, submodelElement);
    }

    @Override
    public void updateSubmodelElement(String idShortPath, SubmodelElement submodelElement) throws ElementDoesNotExistException {
        deleteAssociatedFileIfAny(idShortPath);

        backend.updateSubmodelElement(submodelId, idShortPath, submodelElement);
    }

    @Override
    public void deleteSubmodelElement(String idShortPath) throws ElementDoesNotExistException {
        deleteAssociatedFileIfAny(idShortPath);

        backend.deleteSubmodelElement(submodelId, idShortPath);
    }

    @Override
    public void patchSubmodelElements(List<SubmodelElement> submodelElementList) {
        backend.patchSubmodelElements(submodelId, submodelElementList);
    }

    @Override
    public OperationVariable[] invokeOperation(String idShortPath, OperationVariable[] input) throws ElementDoesNotExistException {
        SubmodelElement sme = getSubmodelElement(idShortPath);

        if (!(sme instanceof InvokableOperation operation))
            throw new NotInvokableException(idShortPath);

        return operation.invoke(input);
    }

    @Override
    public File getFileByPath(String idShortPath) throws ElementDoesNotExistException, ElementNotAFileException, FileDoesNotExistException {
        return submodelFileOperations.getFile(submodelId, idShortPath);
    }

    @Override
    public void setFileValue(String idShortPath, String fileName, InputStream inputStream) throws ElementDoesNotExistException, ElementNotAFileException {
        submodelFileOperations.setFileValue(submodelId, idShortPath, fileName, inputStream);
    }

    @Override
    public void deleteFileValue(String idShortPath) throws ElementDoesNotExistException, ElementNotAFileException, FileDoesNotExistException {
        submodelFileOperations.deleteFileValue(submodelId, idShortPath);
    }

    @Override
    public InputStream getFileByFilePath(String filePath) {
        return submodelFileOperations.getInputStream(filePath);
    }
    
    private void hostSubmodel(Submodel submodel) {
        this.backend.save(submodel);
    }

    private void deleteAssociatedFileIfAny(String idShortPath) {
        try {
            submodelFileOperations.deleteFileValue(submodelId, idShortPath);
        } catch (Exception e) {
        }
    }
}

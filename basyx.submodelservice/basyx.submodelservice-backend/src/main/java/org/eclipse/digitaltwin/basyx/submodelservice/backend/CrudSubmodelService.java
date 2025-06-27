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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    private Logger logger = LoggerFactory.getLogger(CrudSubmodelService.class);

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
    public void updateSubmodelElement(String idShortPath, SubmodelElement newElement) throws ElementDoesNotExistException {
        SubmodelElement existingSubmodelElement = backend.getSubmodelElement(submodelId, idShortPath);
        boolean existingSubmodelElementIsFile = existingSubmodelElement instanceof org.eclipse.digitaltwin.aas4j.v3.model.File;
        boolean newSubmodelElementIsFile = newElement instanceof org.eclipse.digitaltwin.aas4j.v3.model.File;

        if (existingSubmodelElementIsFile && newSubmodelElementIsFile) {
            handleFileAttachmentUpdate(submodelId, idShortPath, newElement);
        } else if (existingSubmodelElementIsFile) {
            deleteAssociatedFileIfAny(idShortPath);
        }

        backend.updateSubmodelElement(submodelId, idShortPath, newElement);
    }

    private void handleFileAttachmentUpdate(String submodelId, String idShortPath, SubmodelElement updatedElement) {
        try {
            if (hasFilePathChanged(submodelId, idShortPath, updatedElement)) {
                deleteAssociatedFileIfAny(idShortPath);
            }
        } catch (Exception e) {
            logger.warn("Could not verify file path change for '{}'. Assuming changed. Reason: {}", idShortPath, e.getMessage());
            deleteAssociatedFileIfAny(idShortPath);
        }
    }

    private boolean hasFilePathChanged(String submodelId, String idShortPath, SubmodelElement updatedElement) {
        if (!(updatedElement instanceof org.eclipse.digitaltwin.aas4j.v3.model.File file)) {
            logger.warn("Expected File element but got: {}", updatedElement.getClass().getSimpleName());
            return true;
        }

        String newPathString = extractFilePath(file);
        if (newPathString == null) {
            return true;
        }

        String oldPathString = getExistingFilePath(submodelId, idShortPath);
        if (oldPathString == null) {
            return true;
        }

        return pathsDiffer(oldPathString, newPathString);
    }

    private boolean isFileElement(SubmodelElement element) {
        return element instanceof org.eclipse.digitaltwin.aas4j.v3.model.File;
    }

    // Extracts the new file path (value) from the updated File element
    private String extractFilePath(org.eclipse.digitaltwin.aas4j.v3.model.File fileElement) {
        return fileElement.getValue();
    }

    private String getExistingFilePath(String submodelId, String idShortPath) {
        try {
            return submodelFileOperations.getFile(submodelId, idShortPath).getPath();
        } catch (Exception e) {
            logger.warn("Failed to retrieve existing file path for '{}': {}", idShortPath, e.getMessage(), e);
            return null;
        }
    }

    private boolean pathsDiffer(String path1, String path2) {
        try {
            Path normalized1 = Paths.get(path1).normalize().toAbsolutePath();
            Path normalized2 = Paths.get(path2).normalize().toAbsolutePath();
            return !normalized1.equals(normalized2);
        } catch (Exception e) {
            logger.error("Error comparing file paths: {}", e.getMessage(), e);
            return true; // Consider paths different in case of error
        }
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
    public void setFileValue(String idShortPath, String fileName, String contentType, InputStream inputStream) throws ElementDoesNotExistException, ElementNotAFileException {
        submodelFileOperations.setFileValue(submodelId, idShortPath, fileName, contentType, inputStream);
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

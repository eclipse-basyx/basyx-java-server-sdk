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

import java.io.InputStream;

import org.eclipse.digitaltwin.aas4j.v3.model.File;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementNotAFileException;
import org.eclipse.digitaltwin.basyx.core.exceptions.FileDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.filerepository.FileRepository;
import org.eclipse.digitaltwin.basyx.core.filerepository.FileRepositoryHelper;
import org.eclipse.digitaltwin.basyx.submodelservice.value.FileBlobValue;

/**
 * Collection of methods that use the {@link FileRepository} to store and retrieve files.
 * 
 * @author mateusmolina
 */
public class SubmodelFileOperations  {
    private final FileRepository fileRepository;
    private final SubmodelOperations submodelOperations;
    
	public SubmodelFileOperations(FileRepository fileRepository, SubmodelOperations operations) {
        this.fileRepository = fileRepository;
        this.submodelOperations = operations;
    }

	/**
	 * Retrieves a file submodel element's content as a temporary local file.
	 * <p>
	 * The caller owns the returned temporary file and is responsible for deleting
	 * it when it is no longer needed.
	 *
	 * @param submodelId
	 *            the id of the Submodel
	 * @param idShortPath
	 *            the idShort path of the file element
	 * @return the file content materialized as a temporary file
	 * @throws ElementDoesNotExistException
	 *             if the SubmodelElement does not exist
	 * @throws ElementNotAFileException
	 *             if the SubmodelElement is not a File
	 * @throws FileDoesNotExistException
	 *             if the referenced file content does not exist
	 */
	public java.io.File getFile(String submodelId, String idShortPath) throws ElementDoesNotExistException, ElementNotAFileException, FileDoesNotExistException {
		SubmodelElement submodelElement = submodelOperations.getSubmodelElement(submodelId, idShortPath);

		throwIfSmElementIsNotAFile(submodelElement);

		File fileSmElement = (File) submodelElement;
		String filePath = getFilePath(fileSmElement);

		return FileRepositoryHelper.fetchAndStoreFileLocally(fileRepository, filePath);
	}

	/**
	 * Retrieves a file submodel element's content as a stream.
	 * <p>
	 * The file is resolved by submodel id and element idShort path, not by the
	 * repository storage id. The caller is responsible for closing the returned
	 * stream.
	 *
	 * @param submodelId
	 *            the id of the Submodel
	 * @param idShortPath
	 *            the idShort path of the file element
	 * @return the file content stream
	 * @throws ElementDoesNotExistException
	 *             if the SubmodelElement does not exist
	 * @throws ElementNotAFileException
	 *             if the SubmodelElement is not a File
	 * @throws FileDoesNotExistException
	 *             if the referenced file content does not exist
	 */
	public InputStream getFileAsStream(String submodelId, String idShortPath) throws ElementDoesNotExistException, ElementNotAFileException, FileDoesNotExistException {
		SubmodelElement submodelElement = submodelOperations.getSubmodelElement(submodelId, idShortPath);

		throwIfSmElementIsNotAFile(submodelElement);

		File fileSmElement = (File) submodelElement;
		String filePath = getFilePath(fileSmElement);

		return FileRepositoryHelper.getFileInputStream(fileRepository, filePath);
	}

	/**
	 * Stores new content for a file submodel element and updates the element value
	 * with the generated repository id.
	 *
	 * @param submodelId
	 *            the id of the Submodel
	 * @param idShortPath
	 *            the idShort path of the file element
	 * @param fileName
	 *            the logical display name of the uploaded file
	 * @param contentType
	 *            the file MIME type
	 * @param inputStream
	 *            the file content stream
	 * @throws ElementDoesNotExistException
	 *             if the SubmodelElement does not exist
	 * @throws ElementNotAFileException
	 *             if the SubmodelElement is not a File
	 */
	public void setFileValue(String submodelId, String idShortPath, String fileName, String contentType, InputStream inputStream) throws ElementDoesNotExistException, ElementNotAFileException {
        SubmodelElement submodelElement = submodelOperations.getSubmodelElement(submodelId, idShortPath);

        throwIfSmElementIsNotAFile(submodelElement);

        File fileSmElement = (File) submodelElement;
        String oldFilePath = fileSmElement.getValue();

        FileRepositoryHelper.saveAndUpdateReference(fileRepository, oldFilePath, fileName, contentType, inputStream, filePath -> submodelOperations.setSubmodelElementValue(submodelId, idShortPath, new FileBlobValue(contentType, filePath)));
	}

	/**
	 * Clears a file submodel element value and deletes the referenced repository
	 * content.
	 *
	 * @param submodelId
	 *            the id of the Submodel
	 * @param idShortPath
	 *            the idShort path of the file element
	 * @throws ElementDoesNotExistException
	 *             if the SubmodelElement does not exist
	 * @throws ElementNotAFileException
	 *             if the SubmodelElement is not a File
	 * @throws FileDoesNotExistException
	 *             if the referenced file content does not exist
	 */
	public void deleteFileValue(String submodelId, String idShortPath) throws ElementDoesNotExistException, ElementNotAFileException, FileDoesNotExistException {
        SubmodelElement submodelElement = submodelOperations.getSubmodelElement(submodelId, idShortPath);

        throwIfSmElementIsNotAFile(submodelElement);

        File fileSubmodelElement = (File) submodelElement;
        String filePath = fileSubmodelElement.getValue();

		FileRepositoryHelper.updateReferenceAndDeleteFile(fileRepository, filePath, () -> submodelOperations.setSubmodelElementValue(submodelId, idShortPath, new FileBlobValue(" ", " ")));
    }

	/**
	 * Retrieves repository content by repository storage id.
	 * <p>
	 * Prefer {@link #getFileAsStream(String, String)} for domain-level file
	 * downloads. The caller is responsible for closing the returned stream.
	 *
	 * @param filePath
	 *            the repository file id
	 * @return the repository content stream
	 * @throws FileDoesNotExistException
	 *             if the repository file id does not exist
	 */
	public InputStream getInputStream(String filePath) throws FileDoesNotExistException {
		return FileRepositoryHelper.getFileInputStream(fileRepository, filePath);
	}

	/**
	 * Resolves the original logical filename for a repository file id.
	 *
	 * @param filePath
	 *            the repository file id
	 * @return the original logical filename
	 */
	public String getOriginalFileName(String filePath) {
		return fileRepository.getOriginalFileName(filePath);
	}

	private static boolean isFileSubmodelElement(SubmodelElement submodelElement) {
		return submodelElement instanceof File;
	}

	private static String getFilePath(File fileSubmodelElement) {
		return fileSubmodelElement.getValue();
	}

	private static void throwIfSmElementIsNotAFile(SubmodelElement submodelElement) {

		if (!isFileSubmodelElement(submodelElement))
			throw new ElementNotAFileException(submodelElement.getIdShort());
	}

}

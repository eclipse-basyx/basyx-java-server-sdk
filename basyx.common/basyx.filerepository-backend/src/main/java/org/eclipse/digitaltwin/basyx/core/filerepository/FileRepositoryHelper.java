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

package org.eclipse.digitaltwin.basyx.core.filerepository;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.eclipse.digitaltwin.basyx.core.exceptions.FileDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.exceptions.FileHandlingException;

/**
 * Helper class for working with files in services
 * 
 * @author mateusmolina
 */
public class FileRepositoryHelper {

    private FileRepositoryHelper() {
    }

    /**
     * Retrieves the content of a file from the repository and creates a local file.
     *
     * @param fileRepository
     *            the file repository instance
     * @param filePath
     * @return the retrieved file
     * @throws FileHandlingException
     *             if an IO error occurs
     */
    public static File fetchAndStoreFileLocally(FileRepository fileRepository, String filePath) {
        try (InputStream fileIs = fileRepository.find(filePath)) {
            byte[] content = fileIs.readAllBytes();
            writeToFile(filePath, content);
            return new File(filePath);
        } catch (IOException e) {
            throw new FileHandlingException("Error while retrieving file content:" + filePath, e);
        }
    }

    /**
     * Writes byte content to a file on disk.
     *
     * @param filePath
     *            the path where the file should be created
     * @param content
     *            the byte content to be written to the file
     * @throws FileHandlingException
     *             if an IO error occurs
     */
    public static void writeToFile(String filePath, byte[] content) {
        try (OutputStream outputStream = new FileOutputStream(filePath)) {
            outputStream.write(content);
        } catch (IOException e) {
            throw new FileHandlingException("Error while writing to file: " + filePath, e);
        }
    }

    /**
     * Saves a file to the repository, deleting any existing file with the same
     * name.
     *
     * @param fileRepository
     *            the file repository instance
     * @param fileName
     *            the name of the file
     * @param contentType
     *            the MIME type of the file
     * @param inputStream
     *            the input stream containing file data
     * @return the path where the file is saved
     */
    public static String saveOrOverwriteFile(FileRepository fileRepository, String fileName, String contentType, InputStream inputStream) {
        FileMetadata fileMetadata = new FileMetadata(fileName, contentType, inputStream);

        if (fileRepository.exists(fileName)) {
            fileRepository.delete(fileName);
        }
        return fileRepository.save(fileMetadata);
    }

    /**
     * Deletes a file from the repository.
     *
     * @param fileRepository
     *            the file repository instance
     * @param filePath
     *            the path of the file to be deleted
     * @throws FileDoesNotExistException
     *             if the file does not exist
     */
    public static void removeFileIfExists(FileRepository fileRepository, String filePath) {
        if (fileRepository.exists(filePath)) {
            fileRepository.delete(filePath);
        } else {
            throw new FileDoesNotExistException();
        }
    }
}

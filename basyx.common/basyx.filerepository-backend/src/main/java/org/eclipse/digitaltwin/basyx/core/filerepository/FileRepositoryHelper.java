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
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import java.util.function.Consumer;

import org.eclipse.digitaltwin.basyx.core.exceptions.FileDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.exceptions.FileHandlingException;

/**
 * Helper class for working with files in services
 * 
 * @author mateusmolina
 */
public class FileRepositoryHelper {
    private static final int MAX_EXTENSION_LENGTH = 16;


    private FileRepositoryHelper() {
    }

    /**
     * Retrieves repository content and materializes it as a temporary local file.
     * <p>
     * The repository id is never used as a filesystem path. A conservative file
     * extension is preserved for compatibility with callers that inspect the
     * returned file name. The caller owns the returned temporary file and is
     * responsible for deleting it when it is no longer needed.
     *
     * @param fileRepository
     *            the file repository instance
     * @param filePath
     *            the repository file id
     * @return the retrieved file
     * @throws FileHandlingException
     *             if an IO error occurs
     */
    public static File fetchAndStoreFileLocally(FileRepository fileRepository, String filePath) {
        Path temporaryFile = null;

        try (InputStream fileIs = fileRepository.find(filePath)) {
            temporaryFile = Files.createTempFile("basyx-file-", getSafeTempFileSuffix(filePath));
            Files.copy(fileIs, temporaryFile, StandardCopyOption.REPLACE_EXISTING);
            return temporaryFile.toFile();
        } catch (IOException e) {
            deleteIfExists(temporaryFile);
            throw new FileHandlingException("Error while retrieving file content:" + filePath, e);
        }
    }

    /**
     * Saves a file to the repository under a server-generated id.
     * <p>
     * The supplied {@code fileName} is treated as a logical display name only. It
     * must not be blank, contain control characters, contain path separators,
     * contain {@code ..}, or start with a Windows drive prefix. The stored file id
     * is a UUID plus a conservative ASCII alphanumeric extension, if one is
     * present.
     *
     * @param fileRepository
     *            the file repository instance
     * @param fileName
     *            the logical name of the file
     * @param contentType
     *            the MIME type of the file
     * @param inputStream
     *            the input stream containing file data
     * @return the generated repository file id
     * @throws IllegalArgumentException
     *             if the logical filename is null, blank, or contains control
     *             characters
     * @throws SecurityException
     *             if the logical filename looks like a path traversal attempt
     */
    public static String saveOrOverwriteFile(FileRepository fileRepository, String fileName, String contentType, InputStream inputStream) {
        String logicalFileName = validateLogicalFileName(fileName);
        String storedFileName = createUniqueFileName(logicalFileName);
        FileMetadata fileMetadata = new FileMetadata(storedFileName, logicalFileName, contentType, inputStream);

        if (fileRepository.exists(storedFileName)) {
            fileRepository.delete(storedFileName);
        }
        return fileRepository.save(fileMetadata);
    }

    /**
     * Saves a new file, updates its owning domain reference, and then removes the
     * old repository file on success.
     * <p>
     * If {@code updateReference} fails, the newly saved repository file is deleted
     * before the exception is rethrown.
     *
     * @param fileRepository
     *            the file repository instance
     * @param oldFilePath
     *            the previous repository file id, or null if none exists
     * @param fileName
     *            the logical display name of the uploaded file
     * @param contentType
     *            the MIME type of the file
     * @param inputStream
     *            the input stream containing file data
     * @param updateReference
     *            callback that stores the new repository file id in the owning
     *            domain object
     * @return the generated repository file id
     */
    public static String saveAndUpdateReference(FileRepository fileRepository, String oldFilePath, String fileName, String contentType, InputStream inputStream, Consumer<String> updateReference) {
        String filePath = saveOrOverwriteFile(fileRepository, fileName, contentType, inputStream);

        try {
            updateReference.accept(filePath);
        } catch (RuntimeException e) {
            deleteFileIfExists(fileRepository, filePath);
            throw e;
        }

        deleteFileIfExists(fileRepository, oldFilePath);

        return filePath;
    }

    /**
     * Updates the owning domain reference to no longer point at a repository file
     * and then removes that repository file on success.
     *
     * @param fileRepository
     *            the file repository instance
     * @param filePath
     *            the repository file id to delete
     * @param updateReference
     *            callback that clears the owning domain reference
     * @throws FileDoesNotExistException
     *             if {@code filePath} does not exist in the repository
     */
    public static void updateReferenceAndDeleteFile(FileRepository fileRepository, String filePath, Runnable updateReference) {
        if (!fileRepository.exists(filePath))
            throw new FileDoesNotExistException();

        updateReference.run();

        deleteFileIfExists(fileRepository, filePath);
    }

    /**
     * Opens repository content as a stream. The caller must close the returned
     * stream.
     *
     * @param fileRepository
     *            the file repository instance
     * @param filePath
     *            the repository file id
     * @return the repository content stream
     * @throws FileDoesNotExistException
     *             if {@code filePath} does not exist in the repository
     */
    public static InputStream getFileInputStream(FileRepository fileRepository, String filePath) {
        return fileRepository.find(filePath);
    }

    /**
     * Best-effort deletion of a repository file.
     *
     * @param fileRepository
     *            the file repository instance
     * @param filePath
     *            the repository file id
     */
    public static void deleteFileIfExists(FileRepository fileRepository, String filePath) {
        if (filePath == null)
            return;

        try {
            if (fileRepository.exists(filePath))
                fileRepository.delete(filePath);
        } catch (FileDoesNotExistException e) {
        }
    }

    private static String createUniqueFileName(String logicalFileName) {
        return UUID.randomUUID() + getFileExtension(logicalFileName);
    }

    private static String getFileExtension(String fileName) {
        if (fileName == null)
            return "";

        int extensionStart = fileName.lastIndexOf('.');

        if (extensionStart <= 0 || extensionStart >= fileName.length() - 1)
            return "";

        String extension = fileName.substring(extensionStart + 1);

        if (extension.length() > MAX_EXTENSION_LENGTH || !extension.chars().allMatch(FileRepositoryHelper::isAsciiLetterOrDigit))
            return "";

        return "." + extension;
    }

    private static String getSafeTempFileSuffix(String filePath) {
        String extension = getFileExtension(filePath);

        if (extension.isEmpty())
            return ".tmp";

        return extension;
    }

    private static boolean isAsciiLetterOrDigit(int character) {
        return character >= 'a' && character <= 'z' || character >= 'A' && character <= 'Z' || character >= '0' && character <= '9';
    }

    private static String validateLogicalFileName(String fileName) {
        if (fileName == null || fileName.isBlank())
            throw new IllegalArgumentException("File name must not be null or blank.");

        String logicalFileName = fileName.replace("\r", "_").replace("\n", "_").trim();

        if (logicalFileName.chars().anyMatch(Character::isISOControl))
            throw new IllegalArgumentException("File name must not contain control characters.");

        if (logicalFileName.contains("..") || logicalFileName.contains("/") || logicalFileName.contains("\\") || startsWithWindowsDrivePrefix(logicalFileName))
            throw new SecurityException("Path traversal attempt detected.");

        return logicalFileName;
    }

    private static boolean startsWithWindowsDrivePrefix(String fileName) {
        return fileName.length() >= 2 && Character.isLetter(fileName.charAt(0)) && fileName.charAt(1) == ':';
    }

    private static void deleteIfExists(Path path) {
        if (path == null)
            return;

        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
        }
    }

    /**
     * Deletes a file from the repository.
     *
     * @param fileRepository
     *            the file repository instance
     * @param filePath
     *            the repository file id to delete
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

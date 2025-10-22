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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;

import org.apache.commons.io.IOUtils;
import org.eclipse.digitaltwin.basyx.core.exceptions.FileDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.exceptions.FileHandlingException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

/**
 * An InMemory implementation of the {@link FileRepository}
 * 
 * @author danish
 */
@Component
@ConditionalOnExpression("'${basyx.backend}'.equals('InMemory')")
public class InMemoryFileRepository implements FileRepository {

	private static final String TEMP_DIRECTORY_PREFIX = "basyx-temp";
	private String tmpDirectory = getTemporaryDirectoryPath();

	@Override
	public String save(FileMetadata fileMetadata) throws FileHandlingException {
		String filePath = createFilePath(fileMetadata.getFileName());

		java.io.File targetFile = new java.io.File(filePath);

		try (FileOutputStream outStream = new FileOutputStream(targetFile)) {
			IOUtils.copy(fileMetadata.getFileContent(), outStream);
		} catch (IOException e) {
			throw new FileHandlingException(fileMetadata.getFileName(), e);
		}

		fileMetadata.setFileName(filePath);

		return filePath;
	}

	@Override
	public InputStream find(String fileId) throws FileDoesNotExistException {

		try {
			return new FileInputStream(fileId);
		} catch (FileNotFoundException e) {
			throw new FileDoesNotExistException();
		}
	}

	@Override
	public void delete(String fileId) throws FileDoesNotExistException {

		if (!exists(fileId))
			throw new FileDoesNotExistException();

		java.io.File tmpFile = new java.io.File(fileId);

		tmpFile.delete();
	}

	@Override
	public boolean exists(String fileId) {
		if(fileId == null) return false;

		if (fileId.isBlank() || !isFilePathValid(fileId))
			return false;

		return Files.exists(Paths.get(fileId));
	}

	private boolean isFilePathValid(String filePath) {

		try {
			Paths.get(filePath);
		} catch (InvalidPathException | NullPointerException ex) {
			return false;
		}

		return true;
	}

	private String getTemporaryDirectoryPath() {
		String tempDirectoryPath = "";

		try {
			tempDirectoryPath = Files.createTempDirectory(TEMP_DIRECTORY_PREFIX).toAbsolutePath().toString();
		} catch (IOException e) {
			throw new RuntimeException(String.format("Unable to create the temporary directory with prefix: %s", TEMP_DIRECTORY_PREFIX));
		}

		return tempDirectoryPath;
	}

	private String createFilePath(String fileName) {
		return tmpDirectory + "/" + fileName;
	}

}

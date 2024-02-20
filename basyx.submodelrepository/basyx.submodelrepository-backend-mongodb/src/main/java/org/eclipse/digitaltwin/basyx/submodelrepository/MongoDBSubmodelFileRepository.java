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

package org.eclipse.digitaltwin.basyx.submodelrepository;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.bson.types.ObjectId;
import org.eclipse.digitaltwin.basyx.core.exceptions.FileDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.exceptions.FileHandlingException;
import org.eclipse.digitaltwin.basyx.core.file.FileMetadata;
import org.eclipse.digitaltwin.basyx.core.file.FileRepository;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;

import com.mongodb.client.gridfs.model.GridFSFile;

import org.springframework.data.mongodb.core.query.Query;

/**
 * A MongoDB implementation of the {@link FileRepository}
 * 
 * @author danish
 */
public class MongoDBSubmodelFileRepository implements FileRepository {

	private static final String MONGO_ID = "_id";
	private static final String GRIDFS_ID_DELIMITER = "#";
	private static final String TEMP_DIR_PREFIX = "basyx-temp";

	private GridFsTemplate gridFsTemplate;

	public MongoDBSubmodelFileRepository(GridFsTemplate gridFsTemplate) {
		this.gridFsTemplate = gridFsTemplate;
	}

	@Override
	public String save(FileMetadata fileMetadata) throws FileHandlingException {
		ObjectId id = gridFsTemplate.store(fileMetadata.getFileContent(), fileMetadata.getFileName(), fileMetadata.getContentType());

		String updatedFileName = createFilePath(id.toString(), fileMetadata.getFileName());

		fileMetadata.setFileName(updatedFileName);

		return updatedFileName;
	}

	@Override
	public InputStream find(String fileId) throws FileDoesNotExistException {

		if (!exists(fileId))
			throw new FileDoesNotExistException();
		
		String mongoDBfileId = getFileId(fileId);

		GridFSFile file = getFile(mongoDBfileId);

		return getGridFsFileAsInputStream(file);
	}

	@Override
	public void delete(String fileId) throws FileDoesNotExistException {

		if (!exists(fileId))
			throw new FileDoesNotExistException();
		
		String mongoDBfileId = getFileId(fileId);

		gridFsTemplate.delete(new Query(Criteria.where(MONGO_ID).is(mongoDBfileId)));
	}

	@Override
	public boolean exists(String fileId) {
		
		String mongoDBfileId = getFileId(fileId);
		
		if (mongoDBfileId.isBlank())
			return false;

		GridFSFile gridFSFile = getFile(mongoDBfileId);

		return gridFSFile != null;
	}
	
	private String getFileId(String value) {
		
		if (value.isBlank())
			return "";
		
		String fileName = Paths.get(value).getFileName().toString();
		
		try {
			return fileName.substring(0, fileName.indexOf(GRIDFS_ID_DELIMITER));
		} catch (IndexOutOfBoundsException e) {
			return "";
		}
		
	}

	private GridFSFile getFile(String mongoDBfileId) {
		return gridFsTemplate.findOne(new Query(Criteria.where(MONGO_ID).is(mongoDBfileId)));
	}

	private InputStream getGridFsFileAsInputStream(GridFSFile file) {

		try {
			return gridFsTemplate.getResource(file).getInputStream();
		} catch (IllegalStateException | IOException e1) {
			throw new IllegalStateException("Unable to get the file resource as input stream." + e1.getStackTrace());
		}

	}

	private String createFilePath(String id, String fileName) {
		
		Path tempDir = createTempDirectory(TEMP_DIR_PREFIX);

		String temporaryDirectoryPath = tempDir.toAbsolutePath().toString();

		return temporaryDirectoryPath + "/" + id + GRIDFS_ID_DELIMITER + fileName;
	}
	
	private Path createTempDirectory(String prefix) {
		
		try {
			return Files.createTempDirectory(prefix);
		} catch (IOException e) {
			throw new FileHandlingException("Exception occurred while creating temporary directory with prefix '" + TEMP_DIR_PREFIX + "'." + e.getMessage());
		}
		
	}

}

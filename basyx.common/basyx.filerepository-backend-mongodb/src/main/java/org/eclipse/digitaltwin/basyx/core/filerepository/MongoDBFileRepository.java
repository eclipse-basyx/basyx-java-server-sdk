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

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.digitaltwin.basyx.core.exceptions.FileDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.exceptions.FileHandlingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Component;

import com.mongodb.client.gridfs.model.GridFSFile;

/**
 * A MongoDB implementation of the {@link FileRepository}
 *
 * @author danish
 */
@Component
@ConditionalOnExpression("'${basyx.backend}'.equals('MongoDB')")
public class MongoDBFileRepository implements FileRepository {

	private static final String MONGO_FILENAME_FIELD = "filename";

	private GridFsTemplate gridFsTemplate;

	@Autowired
	public MongoDBFileRepository(MongoTemplate mongoTemplate) {
		this(buildDefaultGridFsTemplate(mongoTemplate));
	}

	public MongoDBFileRepository(GridFsTemplate gridFsTemplate) {
		this.gridFsTemplate = gridFsTemplate;
	}

	public static GridFsTemplate buildDefaultGridFsTemplate(MongoTemplate mongoTemplate) {
		return new GridFsTemplate(mongoTemplate.getMongoDatabaseFactory(), mongoTemplate.getConverter());
	}

	@Override
	public String save(FileMetadata fileMetadata) throws FileHandlingException {

		if (exists(fileMetadata.getFileName()))
			throw new FileHandlingException("File '%s' already exists.".formatted(fileMetadata.getFileName()));

		gridFsTemplate.store(fileMetadata.getFileContent(), fileMetadata.getFileName(), fileMetadata.getContentType());

		return fileMetadata.getFileName();
	}

	@Override
	public InputStream find(String fileName) throws FileDoesNotExistException {

		if (!exists(fileName))
			throw new FileDoesNotExistException();

		GridFSFile file = getFile(fileName);

		return getGridFsFileAsInputStream(file);
	}

	@Override
	public void delete(String fileName) throws FileDoesNotExistException {

		if (!exists(fileName))
			throw new FileDoesNotExistException();

		gridFsTemplate.delete(new Query(Criteria.where(MONGO_FILENAME_FIELD).is(fileName)));
	}

	@Override
	public boolean exists(String fileName) {

		if(fileName == null) return false;

		if (fileName.isBlank())
			return false;

		GridFSFile gridFSFile = getFile(fileName);

		return gridFSFile != null;
	}

	private GridFSFile getFile(String mongoDBfileId) {
		return gridFsTemplate.findOne(new Query(Criteria.where(MONGO_FILENAME_FIELD).is(mongoDBfileId)));
	}

	private InputStream getGridFsFileAsInputStream(GridFSFile file) {

		try {
			return gridFsTemplate.getResource(file).getInputStream();
		} catch (IllegalStateException | IOException e1) {
			throw new IllegalStateException("Unable to get the file resource as input stream." + e1.getStackTrace());
		}

	}

}

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
package org.eclipse.digitaltwin.basyx.core.filerepository;

import org.eclipse.digitaltwin.basyx.core.filerepository.backend.FileRepositoryTestSuite;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

/**
 * Integration Test for MongoDBFileRepository
 * 
 * Requires that a mongoDb server is running
 * 
 * @author witt
 *
 */
public class TestMongoDBFileRepository extends FileRepositoryTestSuite{
	private static MongoTemplate mongoTemplate = createMongoTemplate();
	private static GridFsTemplate gridFsTemplate = configureDefaultGridFsTemplate(mongoTemplate);
	
	@Override
	protected FileRepository getFileRepository() {
		return new MongoDBFileRepository(gridFsTemplate);
	}
	
	private static MongoTemplate createMongoTemplate() {
		String connectionURL = "mongodb://mongoAdmin:mongoPassword@localhost:27017/";
		
		MongoClient client = MongoClients.create(connectionURL);
		
		return new MongoTemplate(client, "BaSyxTestDb");
	}
	
	private static GridFsTemplate configureDefaultGridFsTemplate(MongoTemplate mongoTemplate) {
		return new GridFsTemplate(mongoTemplate.getMongoDatabaseFactory(), mongoTemplate.getConverter());
	}
}

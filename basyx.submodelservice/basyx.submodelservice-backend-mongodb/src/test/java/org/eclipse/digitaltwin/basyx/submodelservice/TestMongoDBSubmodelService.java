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


package org.eclipse.digitaltwin.basyx.submodelservice;

import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.basyx.common.mongocore.BasyxMongoMappingContext;
import org.eclipse.digitaltwin.basyx.common.mongocore.MongoDBUtilities;
import org.eclipse.digitaltwin.basyx.core.exceptions.FeatureNotSupportedException;
import org.eclipse.digitaltwin.basyx.core.filerepository.FileRepository;
import org.eclipse.digitaltwin.basyx.core.filerepository.MongoDBFileRepository;
import org.junit.After;
import org.junit.Test;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

/**
 * Test for mongoDb submodel service backend
 * 
 * @author zhangzai, mateusmolina
 *
 */
public class TestMongoDBSubmodelService extends SubmodelServiceSuite {
	private final String COLLECTION = "submodelTestCollection";
	private final String CONNECTION_URL = "mongodb://mongoAdmin:mongoPassword@localhost:27017";
	private final MongoClient CLIENT = MongoClients.create(CONNECTION_URL);
	private final MongoTemplate TEMPLATE = new MongoTemplate(CLIENT, "BaSyxTestDb");
	private final GridFsTemplate GRIDFS_TEMPLATE = new GridFsTemplate(TEMPLATE.getMongoDatabaseFactory(), TEMPLATE.getConverter());
	private FileRepository fileRepository = new MongoDBFileRepository(GRIDFS_TEMPLATE);

	@After
	public void clear() {
		MongoDBUtilities.clearCollection(TEMPLATE, COLLECTION);
	}

	@Override
	protected SubmodelService getSubmodelService(Submodel submodel) {
		BasyxMongoMappingContext mappingContext = new BasyxMongoMappingContext();
		return new MongoDBSubmodelServiceFactory(fileRepository, new SingleSubmodelMongoDBBackendProvider(mappingContext, COLLECTION, TEMPLATE)).create(submodel);
	}

	@Override
	@Test(expected = FeatureNotSupportedException.class)
	public void invokeOperation() {
		super.invokeOperation();
	}

	@Override
	@Test(expected = FeatureNotSupportedException.class)
	public void invokeNonOperation() {
		super.invokeNonOperation();
	}

	@Override
	protected boolean fileExistsInStorage(String fileValue) {
		return fileRepository.exists(fileValue);
	}
}

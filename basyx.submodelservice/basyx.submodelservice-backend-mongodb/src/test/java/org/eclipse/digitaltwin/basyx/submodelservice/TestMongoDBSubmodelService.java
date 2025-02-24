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

package org.eclipse.digitaltwin.basyx.submodelservice;

import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.basyx.common.mongocore.MongoDBUtilities;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.exceptions.FeatureNotSupportedException;
import org.eclipse.digitaltwin.basyx.core.filerepository.FileRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Test for mongoDb submodel service backend
 * 
 * @author zhangzai, mateusmolina
 *
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestMongoDBSubmodelService extends SubmodelServiceSuite {

	static final String TEST_COLLECTION = "submodelServiceTestCollection";

	@Autowired
	private FileRepository fileRepository;

	@Autowired
	private SubmodelServiceFactory submodelServiceFactory;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Before
	public void clear() {
		MongoDBUtilities.clearCollection(mongoTemplate, TEST_COLLECTION);
	}

	@Override
	protected SubmodelService getSubmodelService(Submodel submodel) {
		return submodelServiceFactory.create(submodel);
	}

	@Override
	@Test(expected = ElementDoesNotExistException.class)
	public void invokeOperation() {
		super.invokeOperation();
	}

	@Override
	protected boolean fileExistsInStorage(String fileValue) {
		return fileRepository.exists(fileValue);
	}
}

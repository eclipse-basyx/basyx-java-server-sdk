/*******************************************************************************
 * Copyright (C) 2023 the Eclipse BaSyx Authors
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

import java.util.Collection;

import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.basyx.common.mongocore.MongoDBUtilities;
import org.eclipse.digitaltwin.basyx.core.exceptions.NotInvokableException;
import org.eclipse.digitaltwin.basyx.core.filerepository.FileRepository;
import org.eclipse.digitaltwin.basyx.submodelrepository.backend.SimpleSubmodelRepositoryFactory;
import org.eclipse.digitaltwin.basyx.submodelrepository.backend.SubmodelRepositoryBackend;
import org.eclipse.digitaltwin.basyx.submodelrepository.core.SubmodelRepositorySuite;
import org.eclipse.digitaltwin.basyx.submodelservice.SubmodelServiceFactory;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TestMongoDBSubmodelRepository extends SubmodelRepositorySuite {

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private GridFsTemplate gridFsTemplate;

	@Autowired
	private SubmodelRepository submodelRepository;

	@Autowired
	private SubmodelServiceFactory submodelServiceFactory;

	@Autowired
	private SubmodelRepositoryBackend submodelRepositoryBackend;

	@Autowired
	private FileRepository fileRepository;

	@Override
	protected SubmodelRepository getSubmodelRepository() {
		return submodelRepository;
	}

	@Before
	public void cleanup() {
		MongoDBUtilities.clearCollection(mongoTemplate, DummySubmodelRepositoryConfig.TEST_COLLECTION);
	}

	@Override
	protected SubmodelRepository getSubmodelRepository(Collection<Submodel> submodels) {
		return new SimpleSubmodelRepositoryFactory(submodelRepositoryBackend, submodelServiceFactory).withRemoteCollection(submodels).create();
	}

	@Override
	protected boolean fileExistsInStorage(String fileValue) {
		return fileRepository.exists(fileValue);
	}

	@Override
	@Ignore
	public void invokeOperation() {
		// TODO Ignored due to MongoDB doesn't provide invocation
	}

	@Test(expected = NotInvokableException.class)
	@Override
	public void invokeNonOperation() {
		super.invokeNonOperation();
	}

}

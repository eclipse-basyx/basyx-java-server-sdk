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

import static org.junit.Assert.assertEquals;

import java.nio.file.Paths;
import java.util.Collection;

import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.basyx.InvokableOperation;
import org.eclipse.digitaltwin.basyx.common.mongocore.BasyxMongoMappingContext;
import org.eclipse.digitaltwin.basyx.common.mongocore.MongoDBUtilities;
import org.eclipse.digitaltwin.basyx.core.exceptions.NotInvokableException;
import org.eclipse.digitaltwin.basyx.submodelrepository.backend.SimpleSubmodelRepositoryFactory;
import org.eclipse.digitaltwin.basyx.submodelrepository.backend.SubmodelBackendProvider;
import org.eclipse.digitaltwin.basyx.submodelrepository.core.SubmodelRepositorySuite;
import org.eclipse.digitaltwin.basyx.submodelservice.InMemorySubmodelServiceFactory;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.gridfs.model.GridFSFile;

public class TestMongoDBSubmodelRepository extends SubmodelRepositorySuite {
	private final String COLLECTION = "submodelTestCollection";
	private final String CONNECTION_URL = "mongodb://mongoAdmin:mongoPassword@localhost:27017";
	private final MongoClient CLIENT = MongoClients.create(CONNECTION_URL);
	private final MongoTemplate TEMPLATE = new MongoTemplate(CLIENT, "BaSyxTestDb");
	private final GridFsTemplate GRIDFS_TEMPLATE = new GridFsTemplate(TEMPLATE.getMongoDatabaseFactory(), TEMPLATE.getConverter());
	private static final String CONFIGURED_SM_REPO_NAME = "configured-sm-repo-name";
	private static final String MONGO_ID = "_id";
	private static final String GRIDFS_ID_DELIMITER = "#";

	@Override
	protected SubmodelRepository getSubmodelRepository() {
		MongoDBUtilities.clearCollection(TEMPLATE, COLLECTION);

		SubmodelBackendProvider submodelBackendProvider = new SubmodelMongoDBBackendProvider(new BasyxMongoMappingContext(), COLLECTION, TEMPLATE);
		SubmodelRepositoryFactory submodelRepositoryFactory = new SimpleSubmodelRepositoryFactory(submodelBackendProvider, new InMemorySubmodelServiceFactory());

		return submodelRepositoryFactory.create();
	}

	@Override
	protected SubmodelRepository getSubmodelRepository(Collection<Submodel> submodels) {
		SubmodelRepository repo = getSubmodelRepository();

		addSubmodelsToRepoWithoutInvokableOperations(submodels, repo);

		return repo;
	}

	@Override
	protected boolean fileExistsInStorage(String fileValue) {
		String fileId = getFileId(fileValue);

		GridFSFile file = GRIDFS_TEMPLATE.findOne(new Query(Criteria.where(MONGO_ID).is(fileId)));

		return file != null && GRIDFS_TEMPLATE.getResource(file).exists();

	}

	@Test
	public void getConfiguredMongoDBSmRepositoryName() {
		SubmodelBackendProvider submodelBackendProvider = new SubmodelMongoDBBackendProvider(new BasyxMongoMappingContext(), COLLECTION, TEMPLATE);
		SubmodelRepository repo = new SimpleSubmodelRepositoryFactory(submodelBackendProvider, new InMemorySubmodelServiceFactory(), CONFIGURED_SM_REPO_NAME).create();

		assertEquals(CONFIGURED_SM_REPO_NAME, repo.getName());
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

	private static Submodel removeInvokableFromInvokableOperation(Submodel sm) {
		sm.getSubmodelElements().stream().filter(InvokableOperation.class::isInstance).map(InvokableOperation.class::cast).forEach(o -> o.setInvokable(null));
		return sm;
	}

	private static void addSubmodelsToRepoWithoutInvokableOperations(Collection<Submodel> submodels, SubmodelRepository repo) {
		submodels.stream()
		.map(TestMongoDBSubmodelRepository::removeInvokableFromInvokableOperation) // TODO: Remove this after MongoDB uses AAS4J serializer
		.forEach(repo::createSubmodel);
	}

	private String getFileId(String value) {

		String fileName = Paths.get(value).getFileName().toString();

		return fileName.substring(0, fileName.indexOf(GRIDFS_ID_DELIMITER));
	}

}

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

import java.util.Collection;

import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.basyx.InvokableOperation;
import org.eclipse.digitaltwin.basyx.common.mongocore.MongoDBUtilities;
import org.eclipse.digitaltwin.basyx.core.exceptions.FeatureNotSupportedException;
import org.eclipse.digitaltwin.basyx.submodelrepository.core.SubmodelRepositorySuite;
import org.eclipse.digitaltwin.basyx.submodelservice.InMemorySubmodelServiceFactory;
import org.junit.Test;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

public class TestMongoDBSubmodelRepository extends SubmodelRepositorySuite {
	private final String COLLECTION = "submodelTestCollection";
	private final String CONNECTION_URL = "mongodb://mongoAdmin:mongoPassword@localhost:27017";
	private final MongoClient CLIENT = MongoClients.create(CONNECTION_URL);
	private final MongoTemplate TEMPLATE = new MongoTemplate(CLIENT, "BaSyxTestDb");
	private final GridFsTemplate GRIDFS_TEMPLATE = new GridFsTemplate(TEMPLATE.getMongoDatabaseFactory(), TEMPLATE.getConverter(), "TestSMEFiles");
	private final InMemorySubmodelServiceFactory SUBMODEL_SERVICE_FACTORY = new InMemorySubmodelServiceFactory();
	private static final String CONFIGURED_SM_REPO_NAME = "configured-sm-repo-name";

	@Override
	protected SubmodelRepository getSubmodelRepository() {
		MongoDBUtilities.clearCollection(TEMPLATE, COLLECTION);

		return new MongoDBSubmodelRepositoryFactory(TEMPLATE, COLLECTION, SUBMODEL_SERVICE_FACTORY, GRIDFS_TEMPLATE).create();
	}

	@Override
	protected SubmodelRepository getSubmodelRepository(Collection<Submodel> submodels) {
		MongoDBUtilities.clearCollection(TEMPLATE, COLLECTION);

		// TODO: Remove this after MongoDB uses AAS4J serializer
		submodels.forEach(this::removeInvokableFromInvokableOperation);

		return new MongoDBSubmodelRepositoryFactory(TEMPLATE, COLLECTION, SUBMODEL_SERVICE_FACTORY, submodels, CONFIGURED_SM_REPO_NAME, GRIDFS_TEMPLATE).create();
	}

	@Test
	public void getConfiguredMongoDBSmRepositoryName() {
		SubmodelRepository repo = new MongoDBSubmodelRepository(TEMPLATE, COLLECTION, SUBMODEL_SERVICE_FACTORY, CONFIGURED_SM_REPO_NAME, GRIDFS_TEMPLATE);

		assertEquals(CONFIGURED_SM_REPO_NAME, repo.getName());
	}

	@Test(expected = FeatureNotSupportedException.class)
	@Override
	public void invokeOperation() {
		super.invokeOperation();
	}

	@Test(expected = FeatureNotSupportedException.class)
	@Override
	public void invokeNonOperation() {
		super.invokeNonOperation();
	}

	private void removeInvokableFromInvokableOperation(Submodel sm) {
		sm.getSubmodelElements().stream().filter(InvokableOperation.class::isInstance).map(InvokableOperation.class::cast).forEach(o -> o.setInvokable(null));
	}

}

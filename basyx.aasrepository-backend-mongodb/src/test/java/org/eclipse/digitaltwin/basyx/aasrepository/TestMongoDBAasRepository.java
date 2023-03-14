/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
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


package org.eclipse.digitaltwin.basyx.aasrepository;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultAssetAdministrationShell;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;
import org.junit.Test;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

/**
 * 
 * @author schnicke, danish
 *
 */
public class TestMongoDBAasRepository extends AasRepositorySuite {

	private final String COLLECTION = "aasTestCollection";

	@Override
	protected AasRepositoryFactory getAasRepositoryFactory() {
		String connectionURL = "mongodb://127.0.0.1:27017/";
		MongoClient client = MongoClients.create(connectionURL);
		MongoTemplate template = new MongoTemplate(client, "BaSyxTestDb");

		clearDatabase(template);

		return new MongoDBAasRepositoryFactory(template, COLLECTION);
	}

	private void clearDatabase(MongoTemplate template) {
		template.remove(new Query(), COLLECTION);
	}

	@Test
	public void aasIsPersisted() {
		AasRepositoryFactory repoFactory = getAasRepositoryFactory();
		AssetAdministrationShell expectedShell = createDummyShellOnRepo(repoFactory.create());
		AssetAdministrationShell retrievedShell = getAasFromNewBackendInstance(repoFactory, expectedShell.getId());

		assertEquals(expectedShell, retrievedShell);

	}
	
	@Test
	public void updatedAasIsPersisted() {
		AasRepositoryFactory repoFactory = getAasRepositoryFactory();
		
		AasRepository mongoDBAasRepository = repoFactory.create();
		
		AssetAdministrationShell expectedShell = createDummyShellOnRepo(mongoDBAasRepository);
		
		addSubmodelReferenceToAas(expectedShell);
		
		updateShellOnRepo(mongoDBAasRepository, expectedShell.getId(), expectedShell);
		
		AssetAdministrationShell retrievedShell = getAasFromNewBackendInstance(repoFactory, expectedShell.getId());

		assertEquals(expectedShell, retrievedShell);
	}
	
	@Test(expected = ElementDoesNotExistException.class)
	public void updateNonExistingAas() {
		AasRepositoryFactory repoFactory = getAasRepositoryFactory();
		
		AasRepository mongoDBAasRepository = repoFactory.create();
		
		AssetAdministrationShell expectedShell = createDummyShellOnRepo(mongoDBAasRepository);
		
		updateShellOnRepo(mongoDBAasRepository, "nonExistingAasId", expectedShell);
	}

	private void addSubmodelReferenceToAas(AssetAdministrationShell expectedShell) {
		expectedShell.setSubmodels(Arrays.asList(AasRepositorySuite.createDummyReference("dummySubmodel")));
	}

	private void updateShellOnRepo(AasRepository aasRepository, String aasId, AssetAdministrationShell expectedShell) {
		aasRepository.updateAas(aasId, expectedShell);
	}

	private AssetAdministrationShell getAasFromNewBackendInstance(AasRepositoryFactory repoFactory, String shellId) {
		AssetAdministrationShell retrievedShell = repoFactory.create().getAas(shellId);
		return retrievedShell;
	}

	private AssetAdministrationShell createDummyShellOnRepo(AasRepository aasRepository) {
		AssetAdministrationShell expectedShell = new DefaultAssetAdministrationShell.Builder().id("dummy").build();
		
		aasRepository.createAas(expectedShell);
		return expectedShell;
	}

}

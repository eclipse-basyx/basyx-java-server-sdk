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
import org.eclipse.digitaltwin.basyx.common.mongocore.MongoDBUtilities;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Integration Test for MongoDBAasRepository
 * 
 * Requires that a mongoDb server is running
 * 
 * @author schnicke, danish, kammognie, mateusmolina, despen
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = { MongoDBTestConfiguration.class })
public class TestMongoDBAasRepository extends AasRepositorySuite {
	private final String COLLECTION = "testAasCollection";

	@Autowired
	AasRepository repository;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	protected AasRepository getAasRepository() {
		return repository;
	}

	@Override
	protected void sanitizeRepository() {
		MongoDBUtilities.clearCollection(mongoTemplate, COLLECTION);
	}

	@Test
	public void aasIsPersisted() {
		AssetAdministrationShell expectedShell = createDummyShellOnRepo(repository);
		AssetAdministrationShell retrievedShell = getAasFromMongoTemplate(expectedShell.getId());

		assertEquals(expectedShell, retrievedShell);
	}

	@Test
	public void updatedAasIsPersisted() {
		AssetAdministrationShell expectedShell = createDummyShellOnRepo(repository);
		addSubmodelReferenceToAas(expectedShell);
		repository.updateAas(expectedShell.getId(), expectedShell);

		AssetAdministrationShell retrievedShell = getAasFromMongoTemplate(expectedShell.getId());

		assertEquals(expectedShell, retrievedShell);
	}

	private void addSubmodelReferenceToAas(AssetAdministrationShell expectedShell) {
		expectedShell.setSubmodels(Arrays.asList(AasRepositorySuite.createDummyReference("dummySubmodel")));
	}

	private AssetAdministrationShell getAasFromMongoTemplate(String shellId) {
		return mongoTemplate.findById(shellId, AssetAdministrationShell.class);
	}

	private AssetAdministrationShell createDummyShellOnRepo(AasRepository aasRepository) {
		AssetAdministrationShell expectedShell = new DefaultAssetAdministrationShell.Builder().id("dummy").build();

		aasRepository.createAas(expectedShell);
		return expectedShell;
	}

}

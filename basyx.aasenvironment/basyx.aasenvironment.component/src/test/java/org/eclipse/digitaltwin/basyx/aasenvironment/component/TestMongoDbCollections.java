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

package org.eclipse.digitaltwin.basyx.aasenvironment.component;

import static org.junit.Assert.assertTrue;

import org.eclipse.digitaltwin.basyx.common.mongocore.MongoDBUtilities;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

/**
 * TestMongoDbCollections
 *
 * @author mateusmolina
 *
 */
public class TestMongoDbCollections {
	private static ConfigurableApplicationContext appContext;

	// MongoDB configuration
	private static final String CONNECTION_URL = "mongodb://mongoAdmin:mongoPassword@localhost:27017/";
	private static final String DB_NAME = "aas-env";
	private static final String AAS_REPO_COLLECTION = "aas-repo";
	private static final String SM_REPO_COLLECTION = "submodel-repo";
	private static final String CD_REPO_COLLECTION = "cd-repo";

	private static final MongoTemplate mongoTemplate = buildMongoTemplate(CONNECTION_URL, DB_NAME);

	@BeforeClass
	public static void startAASEnvironment() throws Exception {
		appContext = new SpringApplicationBuilder(AasEnvironmentComponent.class).profiles("mongodb").run(new String[] {});
	}
	
	@AfterClass
	public static void deleteDatabase() {
		appContext.close();
		MongoDBUtilities.clearCollection(mongoTemplate, AAS_REPO_COLLECTION);
		MongoDBUtilities.clearCollection(mongoTemplate, SM_REPO_COLLECTION);
		MongoDBUtilities.clearCollection(mongoTemplate, CD_REPO_COLLECTION);
	}

	@Test
	public void aasRepoCollectionIsCorrectlyDefined() {
		assertMongoDBCollectionExists(AAS_REPO_COLLECTION);
	}

	@Test
	public void smRepoCollectionIsCorrectlyDefined() {
		assertMongoDBCollectionExists(SM_REPO_COLLECTION);
	}

	@Test
	public void cdRepoCollectionIsCorrectlyDefined() {
		assertMongoDBCollectionExists(CD_REPO_COLLECTION);
	}

	private void assertMongoDBCollectionExists(String collectionName) {
		assertTrue(mongoTemplate.collectionExists(collectionName));
	}

	private static MongoTemplate buildMongoTemplate(String connectionUrl, String dbName) {
		MongoClient client = MongoClients.create(connectionUrl);
		return new MongoTemplate(client, dbName);
	}
}

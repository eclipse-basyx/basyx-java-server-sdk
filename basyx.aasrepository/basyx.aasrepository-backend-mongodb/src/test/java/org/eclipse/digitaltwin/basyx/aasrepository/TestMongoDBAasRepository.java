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

package org.eclipse.digitaltwin.basyx.aasrepository;

import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.bson.Document;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultAssetAdministrationShell;
import org.eclipse.digitaltwin.basyx.aasservice.backend.InMemoryAasServiceFactory;
import org.eclipse.digitaltwin.basyx.common.mongocore.CustomIdentifiableMappingMongoConverter;
import org.eclipse.digitaltwin.basyx.common.mongocore.MongoDBUtilities;
import org.eclipse.digitaltwin.basyx.http.Aas4JHTTPSerializationExtension;
import org.eclipse.digitaltwin.basyx.http.BaSyxHTTPConfiguration;
import org.eclipse.digitaltwin.basyx.http.SerializationExtension;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

/**
 * Tests the {@link MongoDBAasRepository}
 * 
 * @author schnicke, danish, kammognie
 *
 */
public class TestMongoDBAasRepository extends AasRepositorySuite {
	
	private static final String CONFIGURED_AAS_REPO_NAME = "configured-aas-repo-name";
	private final String COLLECTION = "aasTestCollection";
	
	private MongoTemplate template;
	
	@Override
	protected AasRepositoryFactory getAasRepositoryFactory() {
		template = createMongoTemplate();

		MongoDBUtilities.clearCollection(template, COLLECTION);

		return new MongoDBAasRepositoryFactory(template, COLLECTION, new InMemoryAasServiceFactory());
	}
	
	@Test
	public void aasIsPersisted() {
		AasRepositoryFactory repoFactory = getAasRepositoryFactory();
		AssetAdministrationShell expectedShell = createDummyShellOnRepo(repoFactory.create(), "dummy");
		AssetAdministrationShell retrievedShell = getAasFromNewBackendInstance(repoFactory, expectedShell.getId());

		assertEquals(expectedShell, retrievedShell);
	}

	@Test
	public void updatedAasIsPersisted() {
		AasRepositoryFactory repoFactory = getAasRepositoryFactory();
		AasRepository mongoDBAasRepository = repoFactory.create();
		AssetAdministrationShell expectedShell = createDummyShellOnRepo(mongoDBAasRepository, "dummy");
		addSubmodelReferenceToAas(expectedShell);
		mongoDBAasRepository.updateAas(expectedShell.getId(), expectedShell);
		AssetAdministrationShell retrievedShell = getAasFromNewBackendInstance(repoFactory, expectedShell.getId());
		
		assertEquals(expectedShell, retrievedShell);
	}
	
	@Test
	public void getConfiguredMongoDBAasRepositoryName() {
		AasRepository repo = new MongoDBAasRepository(createMongoTemplate(), COLLECTION, new InMemoryAasServiceFactory(), CONFIGURED_AAS_REPO_NAME);
		
		assertEquals(CONFIGURED_AAS_REPO_NAME, repo.getName());
	}
	
	@Test
	public void retrieveRawAasJson() throws FileNotFoundException, IOException {
		AssetAdministrationShell dummyAas = createDummyShellOnRepo(getAasRepositoryFactory().create(), "dummyAAS");
		
		String expectedAASJson = getAasJSONString();
		
		template.save(dummyAas, COLLECTION);
		
		Document aasDocument = template.findOne(new Query().addCriteria(Criteria.where("id").is("dummyAAS")),
				Document.class, COLLECTION);
		
		assertSameJSONContent(expectedAASJson, aasDocument.toJson());
	}

	private void assertSameJSONContent(String expectedAASJson, String json) throws JsonMappingException, JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();

		assertEquals(mapper.readTree(expectedAASJson), mapper.readTree(json));
	}

	private void addSubmodelReferenceToAas(AssetAdministrationShell expectedShell) {
		expectedShell.setSubmodels(Arrays.asList(AasRepositorySuite.createDummyReference("dummySubmodel")));
	}

	private AssetAdministrationShell getAasFromNewBackendInstance(AasRepositoryFactory repoFactory, String shellId) {
		AssetAdministrationShell retrievedShell = repoFactory.create()
				.getAas(shellId);
		return retrievedShell;
	}

	private AssetAdministrationShell createDummyShellOnRepo(AasRepository aasRepository, String id) {
		AssetAdministrationShell expectedShell = new DefaultAssetAdministrationShell.Builder().id(id).idShort("dummyAASIdShort")
				.build();

		aasRepository.createAas(expectedShell);
		return expectedShell;
	}

	private MongoTemplate createMongoTemplate() {
		List<SerializationExtension> extensions = Arrays.asList(new Aas4JHTTPSerializationExtension());
		
		ObjectMapper mapper = new BaSyxHTTPConfiguration().jackson2ObjectMapperBuilder(extensions).build();
		
		MongoDatabaseFactory databaseFactory = createDatabaseFactory();
		
		return new MongoTemplate(databaseFactory, new CustomIdentifiableMappingMongoConverter(databaseFactory, new MongoMappingContext(), mapper));
	}
	
	private MongoDatabaseFactory createDatabaseFactory() {
		String connectionString = createConnectionString();

		MongoClient client = MongoClients.create(connectionString);

		return new SimpleMongoClientDatabaseFactory(client, "test");
	}

	private String createConnectionString() {
		return String.format("mongodb://%s:%s@%s:%s", "mongoAdmin", "mongoPassword", "127.0.0.1", "27017");
	}
	
	private String getAasJSONString() throws FileNotFoundException, IOException {
		ClassPathResource classPathResource = new ClassPathResource("DummyAas.json");
		InputStream in = classPathResource.getInputStream();
		
		return IOUtils.toString(in, StandardCharsets.UTF_8.name());
	}
	
}

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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.bson.Document;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.basyx.InvokableOperation;
import org.eclipse.digitaltwin.basyx.common.mongocore.CustomIdentifiableMappingMongoConverter;
import org.eclipse.digitaltwin.basyx.common.mongocore.MongoDBUtilities;
import org.eclipse.digitaltwin.basyx.core.exceptions.FeatureNotSupportedException;
import org.eclipse.digitaltwin.basyx.http.Aas4JHTTPSerializationExtension;
import org.eclipse.digitaltwin.basyx.http.BaSyxHTTPConfiguration;
import org.eclipse.digitaltwin.basyx.http.SerializationExtension;
import org.eclipse.digitaltwin.basyx.submodelrepository.core.SubmodelRepositorySuite;
import org.eclipse.digitaltwin.basyx.submodelrepository.http.SubmodelRepositoryHTTPSerializationExtension;
import org.eclipse.digitaltwin.basyx.submodelservice.InMemorySubmodelServiceFactory;
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

public class TestMongoDBSubmodelRepository extends SubmodelRepositorySuite {
	private final String COLLECTION = "submodelTestCollection";
	
	private MongoTemplate mongoTemplate;
	
	private final InMemorySubmodelServiceFactory SUBMODEL_SERVICE_FACTORY = new InMemorySubmodelServiceFactory();
	private static final String CONFIGURED_SM_REPO_NAME = "configured-sm-repo-name";

	@Override
	protected SubmodelRepository getSubmodelRepository() {
		mongoTemplate = createMongoTemplate();
		
		MongoDBUtilities.clearCollection(mongoTemplate, COLLECTION);

		return new MongoDBSubmodelRepositoryFactory(mongoTemplate, COLLECTION, SUBMODEL_SERVICE_FACTORY).create();
	}

	@Override
	protected SubmodelRepository getSubmodelRepository(Collection<Submodel> submodels) {
		mongoTemplate = createMongoTemplate();
		
		MongoDBUtilities.clearCollection(mongoTemplate, COLLECTION);

		// Remove InvokableOperation from the Submodels
		submodels.forEach(this::removeInvokableOperation);

		return new MongoDBSubmodelRepositoryFactory(mongoTemplate, COLLECTION, SUBMODEL_SERVICE_FACTORY, submodels).create();
	}
	
	@Test
	public void getConfiguredMongoDBSmRepositoryName() {
		mongoTemplate = createMongoTemplate();
		
		SubmodelRepository repo = new MongoDBSubmodelRepository(mongoTemplate, COLLECTION, SUBMODEL_SERVICE_FACTORY, CONFIGURED_SM_REPO_NAME);
		
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
	
	@Test
	public void retrieveRawSMJson() throws FileNotFoundException, IOException {
		String dummySMId = "dummySubmodelId";
		
		createDummySubmodelOnRepository(dummySMId);
		
		String expectedSMJson = getSubmodelJSONString();
		
		Document smDocument = createMongoTemplate().findOne(new Query().addCriteria(Criteria.where("id").is(dummySMId)),
				Document.class, COLLECTION);
		
		assertSameJSONContent(expectedSMJson, smDocument.toJson());
	}
	
	private void assertSameJSONContent(String expectedSMJson, String actualSMJson) throws JsonMappingException, JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();

		assertEquals(mapper.readTree(expectedSMJson), mapper.readTree(actualSMJson));
	}

	private void createDummySubmodelOnRepository(String dummySMId) {
		Submodel dummySubmodel = buildDummySubmodel(dummySMId);
		
		SubmodelRepository repository = getSubmodelRepository();
		repository.createSubmodel(dummySubmodel);
	}
	
	private void removeInvokableOperation(Submodel sm) {
	    Iterator<SubmodelElement> iterator = sm.getSubmodelElements().iterator();
	    while (iterator.hasNext()) {
	        SubmodelElement element = iterator.next();
	        if (element instanceof InvokableOperation) {
	            iterator.remove();
	        }
	    }
	}
	
	private MongoTemplate createMongoTemplate() {
		List<SerializationExtension> extensions = Arrays.asList(new Aas4JHTTPSerializationExtension(), new SubmodelRepositoryHTTPSerializationExtension());
		
		ObjectMapper mapper = new BaSyxHTTPConfiguration().jackson2ObjectMapperBuilder(extensions).build();
		
		MongoDatabaseFactory databaseFactory = createDatabaseFactory();
		
		return new MongoTemplate(databaseFactory, new CustomIdentifiableMappingMongoConverter(databaseFactory, new MongoMappingContext(), mapper));
	}
	
	private MongoDatabaseFactory createDatabaseFactory() {
		String connectionString = createConnectionString();

		MongoClient client = MongoClients.create(connectionString);

		return new SimpleMongoClientDatabaseFactory(client, "BaSyxTestDb");
	}

	private String createConnectionString() {
		return String.format("mongodb://%s:%s@%s:%s", "mongoAdmin", "mongoPassword", "127.0.0.1", "27017");
	}
	
	private String getSubmodelJSONString() throws FileNotFoundException, IOException {
		ClassPathResource classPathResource = new ClassPathResource("DummySubmodel.json");
		InputStream in = classPathResource.getInputStream();
		
		return IOUtils.toString(in, StandardCharsets.UTF_8.name());
	}

}

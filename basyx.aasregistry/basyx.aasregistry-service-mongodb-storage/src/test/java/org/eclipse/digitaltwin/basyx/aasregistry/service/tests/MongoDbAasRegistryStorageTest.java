/*******************************************************************************
 * Copyright (C) 2023 DFKI GmbH (https://www.dfki.de/en/web)
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
package org.eclipse.digitaltwin.basyx.aasregistry.service.tests;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import org.bson.Document;
import org.bson.json.JsonWriterSettings;
import org.eclipse.digitaltwin.basyx.aasregistry.model.AssetAdministrationShellDescriptor;
import org.eclipse.digitaltwin.basyx.aasregistry.model.AssetKind;
import org.eclipse.digitaltwin.basyx.aasregistry.model.SubmodelDescriptor;
import org.eclipse.digitaltwin.basyx.aasregistry.service.configuration.MongoDbConfiguration;
import org.eclipse.digitaltwin.basyx.aasregistry.service.storage.DescriptorFilter;
import org.eclipse.digitaltwin.basyx.aasregistry.service.storage.mongodb.MongoDbAasRegistryStorage;
import org.junit.ClassRule;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.utility.DockerImageName;

import com.mongodb.ExplainVerbosity;
import com.mongodb.client.MongoCollection;

@TestPropertySource(properties = { "registry.type=mongodb", "spring.data.mongodb.database=aasregistry"})
@ContextConfiguration(classes = { MongoDbConfiguration.class})
@EnableAutoConfiguration
public class MongoDbAasRegistryStorageTest extends AasRegistryStorageTest {

	@Value("${spring.data.mongodb.database}")
	private static String DATABASE_NAME;
	
	@ClassRule
	public static final MongoDBContainer MONGODB_CONTAINER = new MongoDBContainer(DockerImageName.parse("mongo:5.0.10"));
	
	@Autowired
	private MongoTemplate template;
	
	@DynamicPropertySource
	static void assignAdditionalProperties(DynamicPropertyRegistry registry) throws InterruptedException, ExecutionException {
		String uri = MONGODB_CONTAINER.getConnectionString() + "/" + DATABASE_NAME;
		registry.add("spring.data.mongodb.uri",  () -> uri);
	}
	
	@Test
	public void whenGetAllByFullFilter_NotAllDocumentsScannedButIndexUsed() {
		testIndexFilter(AssetKind.TYPE, "abc");
	}
	
	@Test
	public void whenGetAllByTypeFilter_NotAllDocumentsScannedButIndexUsed() {
		testIndexFilter(AssetKind.TYPE, null);
	}
	
	@Test
	public void whenGetByAasID_NotAllDocumentsScannedButIndexUsed() {
		MongoCollection<Document> collection = template.getCollection("aasdescriptors");
		Document doc = collection.find(new Document("_id", "11")).explain(ExplainVerbosity.QUERY_PLANNER);
		assertThat(doc.toJson()).doesNotContain("\"COLLSCAN\"");
	}
	
	private void testIndexFilter(AssetKind kind, String type) {
		MongoDbAasRegistryStorage storage = new MongoDbAasRegistryStorage(template);
		Optional<Criteria> criteriaOpt = storage.createFilterCriteria(new DescriptorFilter(kind, type));
		assertThat(criteriaOpt).isNotEmpty();
		Criteria criteria = criteriaOpt.get();
		MongoCollection<Document> collection = template.getCollection("aasdescriptors");
		Document doc = collection.find(Query.query(criteria).getQueryObject()).explain(ExplainVerbosity.QUERY_PLANNER);
		assertThat(doc.toJson()).doesNotContain("\"COLLSCAN\"");
	}
}
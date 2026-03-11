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
package org.eclipse.digitaltwin.basyx.submodelregistry.service.tests;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;

import org.bson.Document;
import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.eclipse.digitaltwin.basyx.submodelregistry.model.SubmodelDescriptor;
import org.eclipse.digitaltwin.basyx.submodelregistry.service.configuration.MongoDbConfiguration;
import org.eclipse.digitaltwin.basyx.submodelregistry.service.storage.SubmodelRegistryStorage;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import com.mongodb.ExplainVerbosity;
import com.mongodb.client.MongoCollection;

@TestPropertySource(properties = { "registry.type=mongodb", "spring.data.mongodb.database=submodelregistry" 
		, "spring.data.mongodb.uri=mongodb://mongoAdmin:mongoPassword@localhost:27017" })
@ContextConfiguration(classes = { org.eclipse.digitaltwin.basyx.submodelregistry.service.configuration.MongoDbConfiguration.class })
@EnableAutoConfiguration
public class MongoDbSubmodelRegistryStorageTest extends SubmodelRegistryStorageTest {


	@Autowired
	private MongoTemplate template;

	@Autowired
	private SubmodelRegistryStorage storage;

	@Autowired
	private MongoDbConfiguration configuration;

	@Test
	public void whenGetById_NotAllDocumentsScannedButIndexUsed() {
		MongoCollection<Document> collection = template.getCollection("submodeldescriptors");
		Document doc = collection.find(new Document("_id", "11")).explain(ExplainVerbosity.QUERY_PLANNER);
		assertThat(doc.toJson()).doesNotContain("\"COLLSCAN\"");
	}

	@Test
	public void givenLegacySupplementalSemanticId_whenGetById_thenDescriptorContainsSupplementalSemanticField() {
		template.remove(new Query(), configuration.collectionName);
		template.save(createLegacyDocument("legacy-get-1"), configuration.collectionName);

		SubmodelDescriptor descriptor = storage.getSubmodelDescriptor("legacy-get-1");
		Document writtenDescriptor = new Document();
		template.getConverter().write(descriptor, writtenDescriptor);

		assertThat(extractSupplementalSemanticField(writtenDescriptor)).isNotNull();
		assertThat(extractSupplementalSemanticField(writtenDescriptor)).isInstanceOf(List.class);
		assertThat((List<?>) extractSupplementalSemanticField(writtenDescriptor)).hasSize(1);
	}

	@Test
	public void givenLegacySupplementalSemanticId_whenGetAll_thenDescriptorContainsSupplementalSemanticField() {
		template.remove(new Query(), configuration.collectionName);
		template.save(createLegacyDocument("legacy-getall-1"), configuration.collectionName);

		CursorResult<List<SubmodelDescriptor>> result = storage.getAllSubmodelDescriptors(PaginationInfo.NO_LIMIT);
		SubmodelDescriptor descriptor = result.getResult().stream().filter(x -> "legacy-getall-1".equals(x.getId())).findFirst().orElse(null);
		assertThat(descriptor).isNotNull();

		Document writtenDescriptor = new Document();
		template.getConverter().write(descriptor, writtenDescriptor);

		assertThat(extractSupplementalSemanticField(writtenDescriptor)).isNotNull();
		assertThat(extractSupplementalSemanticField(writtenDescriptor)).isInstanceOf(List.class);
		assertThat((List<?>) extractSupplementalSemanticField(writtenDescriptor)).hasSize(1);
	}

	private Object extractSupplementalSemanticField(Document descriptorDocument) {
		if (descriptorDocument.containsKey("supplementalSemanticIds")) {
			return descriptorDocument.get("supplementalSemanticIds");
		}
		return descriptorDocument.get("supplementalSemanticId");
	}

	private Document createLegacyDocument(String id) {
		Document key = new Document("type", "GlobalReference").append("value", "urn:test:" + id);
		Document reference = new Document("type", "ExternalReference").append("keys", Arrays.asList(key));
		return new Document("_id", id).append("idShort", "short-" + id).append("endpoints", Arrays.asList()).append("supplementalSemanticId", Arrays.asList(reference));
	}
}
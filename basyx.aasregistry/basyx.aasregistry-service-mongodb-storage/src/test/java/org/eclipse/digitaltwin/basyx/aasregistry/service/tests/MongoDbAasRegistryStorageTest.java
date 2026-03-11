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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.bson.Document;
import org.eclipse.digitaltwin.basyx.aasregistry.model.AssetAdministrationShellDescriptor;
import org.eclipse.digitaltwin.basyx.aasregistry.model.AssetKind;
import org.eclipse.digitaltwin.basyx.aasregistry.model.ShellDescriptorQuery;
import org.eclipse.digitaltwin.basyx.aasregistry.model.ShellDescriptorQuery.QueryTypeEnum;
import org.eclipse.digitaltwin.basyx.aasregistry.model.SubmodelDescriptor;
import org.eclipse.digitaltwin.basyx.aasregistry.paths.AasRegistryPaths;
import org.eclipse.digitaltwin.basyx.aasregistry.service.configuration.MongoDbConfiguration;
import org.eclipse.digitaltwin.basyx.aasregistry.service.storage.DescriptorFilter;
import org.eclipse.digitaltwin.basyx.aasregistry.service.storage.ShellDescriptorSearchRequests;
import org.eclipse.digitaltwin.basyx.aasregistry.service.storage.mongodb.MongoDbAasRegistryStorage;
import org.eclipse.digitaltwin.basyx.aasregistry.service.storage.mongodb.SearchQueryBuilder;
import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import com.mongodb.ExplainVerbosity;
import com.mongodb.client.MongoCollection;

@TestPropertySource(properties = { "registry.type=mongodb", "spring.data.mongodb.database=aasregistry"
		, "spring.data.mongodb.uri=mongodb://mongoAdmin:mongoPassword@localhost:27017", "basyx.aasregistry.mongodb.collectionName=aasdescriptors" })
@ContextConfiguration(classes = { MongoDbConfiguration.class })
@EnableAutoConfiguration
public class MongoDbAasRegistryStorageTest extends AasRegistryStorageTest {

	@Autowired
	private MongoTemplate template;


	@Test
	public void whenGetAllByFullFilter_NotAllDocumentsScannedButIndexUsed() {
		testIndexFilter(AssetKind.TYPE, "abc");
	}

	@Test
	public void whenGetAllByTypeFilter_NotAllDocumentsScannedButIndexUsed() {
		testIndexFilter(AssetKind.TYPE, null);
	}

	@Test
	public void whenSearchByShellExtension_NotAllDocumentsScannedButIndexUsed() {
		SearchQueryBuilder builder = new SearchQueryBuilder();

		ShellDescriptorQuery query = new ShellDescriptorQuery(AasRegistryPaths.extensions().value(), "TAG");
		query.value("AB");
		Criteria criteria = builder.buildCriteria(ShellDescriptorSearchRequests.groupQueries(query));
		testIndexFilter(criteria);
	}

	@Test
	public void whenSearchBySmExtension_NotAllDocumentsScannedButIndexUsed() {
		SearchQueryBuilder builder = new SearchQueryBuilder();
		ShellDescriptorQuery query = new ShellDescriptorQuery(AasRegistryPaths.submodelDescriptors().extensions().value(), "TAG");
		query.value("AB");
		Criteria criteria = builder.buildCriteria(ShellDescriptorSearchRequests.groupQueries(query));
		testIndexFilter(criteria);
	}

	@Test
	public void whenSearchBySmExtensionCombinded_NotAllDocumentsScannedButIndexUsed() {
		SearchQueryBuilder builder = new SearchQueryBuilder();
		ShellDescriptorQuery shellQuery1 = new ShellDescriptorQuery(AasRegistryPaths.extensions().value(), "TAG").value("A");
		ShellDescriptorQuery shellQuery2 = new ShellDescriptorQuery(AasRegistryPaths.extensions().value(), "TAG").value("B");
		ShellDescriptorQuery smQuery1 = new ShellDescriptorQuery(AasRegistryPaths.submodelDescriptors().extensions().value(), "TAG").value("C");
		ShellDescriptorQuery smQuery2 = new ShellDescriptorQuery(AasRegistryPaths.submodelDescriptors().extensions().value(), "COLOR").value("R\\.*D").queryType(QueryTypeEnum.REGEX);
		shellQuery1.combinedWith(shellQuery2);
		shellQuery2.combinedWith(smQuery1);
		smQuery1.combinedWith(smQuery2);

		Criteria criteria = builder.buildCriteria(ShellDescriptorSearchRequests.groupQueries(shellQuery1));
		testIndexFilter(criteria);
	}

	@Test
	public void whenGetByAasID_NotAllDocumentsScannedButIndexUsed() {
		MongoCollection<Document> collection = template.getCollection("aasdescriptors");
		Document doc = collection.find(new Document("_id", "11")).explain(ExplainVerbosity.QUERY_PLANNER);
		assertThat(doc.toJson()).doesNotContain("\"COLLSCAN\"");
	}

	@Test
	public void givenLegacySupplementalSemanticIdInSubmodel_whenGetAasById_thenSubmodelContainsSupplementalSemanticField() {
		template.remove(new Query(), "aasdescriptors");
		template.save(createLegacyAasDocument("legacy-aas-1", "legacy-submodel-1"), "aasdescriptors");

		AssetAdministrationShellDescriptor descriptor = storage.getAasDescriptor("legacy-aas-1");
		SubmodelDescriptor submodelDescriptor = descriptor.getSubmodelDescriptors().get(0);

		Document writtenSubmodelDescriptor = new Document();
		template.getConverter().write(submodelDescriptor, writtenSubmodelDescriptor);

		assertThat(extractSupplementalSemanticField(writtenSubmodelDescriptor)).isNotNull();
		assertThat(extractSupplementalSemanticField(writtenSubmodelDescriptor)).isInstanceOf(List.class);
		assertThat((List<?>) extractSupplementalSemanticField(writtenSubmodelDescriptor)).hasSize(1);
	}

	@Test
	public void givenLegacySupplementalSemanticIdInSubmodel_whenGetAllSubmodels_thenSubmodelContainsSupplementalSemanticField() {
		template.remove(new Query(), "aasdescriptors");
		template.save(createLegacyAasDocument("legacy-aas-2", "legacy-submodel-2"), "aasdescriptors");

		CursorResult<List<SubmodelDescriptor>> result = storage.getAllSubmodels("legacy-aas-2", PaginationInfo.NO_LIMIT);
		SubmodelDescriptor submodelDescriptor = result.getResult().stream().filter(x -> "legacy-submodel-2".equals(x.getId())).findFirst().orElse(null);
		assertThat(submodelDescriptor).isNotNull();

		Document writtenSubmodelDescriptor = new Document();
		template.getConverter().write(submodelDescriptor, writtenSubmodelDescriptor);

		assertThat(extractSupplementalSemanticField(writtenSubmodelDescriptor)).isNotNull();
		assertThat(extractSupplementalSemanticField(writtenSubmodelDescriptor)).isInstanceOf(List.class);
		assertThat((List<?>) extractSupplementalSemanticField(writtenSubmodelDescriptor)).hasSize(1);
	}

	private void testIndexFilter(AssetKind kind, String type) {
		MongoDbAasRegistryStorage storage = new MongoDbAasRegistryStorage(template, "aasdescriptors");
		Optional<Criteria> criteriaOpt = storage.createFilterCriteria(new DescriptorFilter(kind, type));
		assertThat(criteriaOpt).isNotEmpty();
		Criteria criteria = criteriaOpt.get();
		testIndexFilter(criteria);
	}

	private void testIndexFilter(Criteria criteria) {
		MongoCollection<Document> collection = template.getCollection("aasdescriptors");
		Document doc = collection.find(Query.query(criteria).getQueryObject()).explain(ExplainVerbosity.QUERY_PLANNER);
		assertThat(doc.toJson()).doesNotContain("\"COLLSCAN\"");
	}

	private Object extractSupplementalSemanticField(Document descriptorDocument) {
		if (descriptorDocument.containsKey("supplementalSemanticIds")) {
			return descriptorDocument.get("supplementalSemanticIds");
		}
		return descriptorDocument.get("supplementalSemanticId");
	}

	private Document createLegacyAasDocument(String aasId, String submodelId) {
		Document key = new Document("type", "FILE").append("value", "urn:test:" + submodelId);
		Document reference = new Document("type", "EXTERNALREFERENCE").append("keys", Arrays.asList(key));
		Document submodelDescriptor = new Document("_id", submodelId).append("idShort", "short-" + submodelId).append("endpoints", Arrays.asList())
				.append("supplementalSemanticId", Arrays.asList(reference));
		return new Document("_id", aasId).append("idShort", "short-" + aasId).append("endpoints", Arrays.asList()).append("submodelDescriptors", Arrays.asList(submodelDescriptor));
	}
}
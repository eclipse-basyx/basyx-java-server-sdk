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

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.bson.Document;
import org.bson.json.JsonWriterSettings;
import org.eclipse.digitaltwin.basyx.aasregistry.model.ShellDescriptorQuery;
import org.eclipse.digitaltwin.basyx.aasregistry.model.ShellDescriptorQuery.QueryTypeEnum;
import org.eclipse.digitaltwin.basyx.aasregistry.paths.AasRegistryPaths;
import org.eclipse.digitaltwin.basyx.aasregistry.service.storage.mongodb.SearchPathProjectionBuilder;
import org.eclipse.digitaltwin.basyx.aasregistry.service.storage.mongodb.SegmentBlocksBuilder;
import org.eclipse.digitaltwin.basyx.aasregistry.service.storage.mongodb.SegmentBlocksBuilder.SegmentBlock;
import org.eclipse.digitaltwin.basyx.aasregistry.service.storage.mongodb.SimpleVarNameProvider;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.aggregation.AggregationExpression;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ProjectionTest {

	@Autowired
	private ObjectMapper mapper;
	
	@Rule
	public TestResourcesLoader resourceLoader = new TestResourcesLoader(ProjectionTest.class.getPackageName(), mapper);

	@Test
	public void testSegmentBlockCollectorEndpointProtocolVersion() {
		String path = AasRegistryPaths.submodelDescriptors().endpoints().protocolInformation().endpointProtocolVersion();
		assertSegmentBlocksCreated(path, true, "submodelDescriptors", "endpoints", "protocolInformation.endpointProtocolVersion");
	}
	
	@Test
	public void testSegmentBlockCollectorDataSpecificationContentDefinition() {
		String path = AasRegistryPaths.submodelDescriptors().administration().embeddedDataSpecifications().dataSpecificationContent().asDataSpecificationIec61360().definition().text();
		assertSegmentBlocksCreated(path, false, "submodelDescriptors", "administration.embeddedDataSpecifications", "dataSpecificationContent.definition", "text");
	}
	
	private void assertSegmentBlocksCreated(String path, boolean expectedIsList, String... expectedSegments) {
		SegmentBlocksBuilder builder = new SegmentBlocksBuilder(Map.of());
		List<SegmentBlock> segments = builder.buildSegmentBlocks(path);
		assertThat(segments.stream().map(SegmentBlock::getSegment).collect(Collectors.toList())).containsExactly(expectedSegments);
		assertThat(segments.get(segments.size()-1).isListLeaf()).isEqualTo(expectedIsList);	
	}

	@Test
	public void testVarNameGeneration() {
		SimpleVarNameProvider provider = new SimpleVarNameProvider();
		HashSet<String> uniqueMap = new HashSet<>();
		// just a few values to see if the ids are different (kind of smoke test)
		for (int i = 0; i < 10000; i++) {
			String varName = provider.next();
			assertThat(uniqueMap.add(varName)).withFailMessage(() -> "id '" + varName + "' already generated").isTrue();
		}
	}

	@Test
	public void testRegexSubmodelListProjection() throws IOException {
		SearchPathProjectionBuilder projBuilder = new SearchPathProjectionBuilder(Map.of("id", "_id", "submodelDescriptors.id", "submodelDescriptors._id"));
		String path = AasRegistryPaths.submodelDescriptors().endpoints().protocolInformation().endpointProtocolVersion();
		assertFilter(projBuilder, new ShellDescriptorQuery(path, "^a_.*$").queryType(QueryTypeEnum.REGEX));
	}
	
	@Test
	public void testMatchSubmodelListProjection() throws IOException {
		SearchPathProjectionBuilder projBuilder = new SearchPathProjectionBuilder(Map.of("id", "_id", "submodelDescriptors.id", "submodelDescriptors._id"));
		String path = AasRegistryPaths.submodelDescriptors().endpoints().protocolInformation().endpointProtocolVersion();
		assertFilter(projBuilder, new ShellDescriptorQuery(path, "2.0.0"));
	}
	
	@Test
	public void testProjectionOutsideSubmodel_ThenNoFilterReturned() throws IOException {
		SearchPathProjectionBuilder projBuilder = new SearchPathProjectionBuilder(Map.of("id", "_id", "submodelDescriptors.id", "submodelDescriptors._id"));
		String path = AasRegistryPaths.administration().embeddedDataSpecifications().dataSpecification().type();
		Optional<AggregationExpression> filterOpt = projBuilder.buildSubmodelFilter(List.of(new ShellDescriptorQuery(path, "type")));
		assertThat(filterOpt).isEmpty();
	}
	
	@Test
	public void testRegexSubmodelFunctionalProjection() throws IOException {
		SearchPathProjectionBuilder projBuilder = new SearchPathProjectionBuilder(Map.of("id", "_id", "submodelDescriptors.id", "submodelDescriptors._id"));
		String path = AasRegistryPaths.submodelDescriptors().endpoints().protocolInformation().endpointProtocol();
		assertFilter(projBuilder, new ShellDescriptorQuery(path, "^a_.*$").queryType(QueryTypeEnum.REGEX));
	}
	
	@Test
	public void testMatchSubmodelFunctionalProjection() throws IOException {
		SearchPathProjectionBuilder projBuilder = new SearchPathProjectionBuilder(Map.of("id", "_id", "submodelDescriptors.id", "submodelDescriptors._id"));
		String path = AasRegistryPaths.submodelDescriptors().endpoints().protocolInformation().endpointProtocol();
		assertFilter(projBuilder, new ShellDescriptorQuery(path, "2.0.0"));
	}

	private void assertFilter(SearchPathProjectionBuilder projBuilder, ShellDescriptorQuery... queries) throws IOException {
		Optional<AggregationExpression> filterOpt = projBuilder.buildSubmodelFilter(List.of(queries));
		assertThat(filterOpt).isPresent();
		Document doc = filterOpt.get().toDocument();
		String jsonString = resourceLoader.loadJsonAsString();
		Document expected = Document.parse(jsonString);
		assertThat(docToJsonPretty(doc)).isEqualTo(docToJsonPretty(expected));
	}

	private String docToJsonPretty(Document doc) {
		return doc.toJson(JsonWriterSettings.builder().indent(true).build());
	}

}

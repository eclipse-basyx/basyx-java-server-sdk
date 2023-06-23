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
import java.util.HashMap;
import java.util.Map;

import org.bson.json.JsonWriterSettings;
import org.eclipse.digitaltwin.basyx.aasregistry.model.AssetKind;
import org.eclipse.digitaltwin.basyx.aasregistry.model.ShellDescriptorQuery;
import org.eclipse.digitaltwin.basyx.aasregistry.model.ShellDescriptorQuery.QueryTypeEnum;
import org.eclipse.digitaltwin.basyx.aasregistry.paths.AasRegistryPaths;
import org.eclipse.digitaltwin.basyx.aasregistry.service.storage.ShellDescriptorSearchRequests;
import org.eclipse.digitaltwin.basyx.aasregistry.service.storage.ShellDescriptorSearchRequests.GroupedQueries;
import org.eclipse.digitaltwin.basyx.aasregistry.service.storage.mongodb.SearchQueryBuilder;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

public class SearchPathCriteriaBuilderTest {

	private static Map<String, String> PATH_MAPPINGS = new HashMap<>();

	@Rule
	public final TestResourcesLoader loader = new TestResourcesLoader();

	static {
		PATH_MAPPINGS.put(AasRegistryPaths.id(), "_id");
		PATH_MAPPINGS.put(AasRegistryPaths.submodelDescriptors().id(), AasRegistryPaths.submodelDescriptors() + "." + "_id");
	}

	private SearchQueryBuilder builder = new SearchQueryBuilder();

	@Test
	public void testId() throws IOException {
		checkSearchPathMatchResult(AasRegistryPaths.id(), "myId");	
	}
	
	@Test
	public void testIdShort() throws IOException {
		checkSearchPathMatchResult(AasRegistryPaths.idShort(), "myIdShort");
	}

	@Test
	public void testAssetKindEnum() throws IOException {
		String value = AssetKind.INSTANCE.toString();
		assertThat(value).isEqualTo("Instance");
		checkSearchPathMatchResult(AasRegistryPaths.assetKind(), value);
	}

	@Test
	public void testSubmodelIdShort() throws IOException {
		checkSearchPathMatchResult(AasRegistryPaths.submodelDescriptors().idShort(), "myId");
	}

	@Test
	public void testSubmodelId() throws IOException {
		checkSearchPathMatchResult(AasRegistryPaths.submodelDescriptors().id(), "myId");
	}

	@Test
	public void testAdministrationCreatorKeysType() throws IOException {
		checkSearchPathMatchResult(AasRegistryPaths.administration().creator().keys().type(), "key1");
	}

	@Test
	public void testSmEndpointsProtocolInformationSecurityAttributesValue() throws IOException {
		checkSearchPathMatchResult(AasRegistryPaths.submodelDescriptors().endpoints().protocolInformation().securityAttributes().value(), "attrValue");
	}

	@Test
	public void testEndpointsProtocolInformationEndpointProtocolVersion() throws IOException {
		checkSearchPathMatchResult(AasRegistryPaths.endpoints().protocolInformation().endpointProtocolVersion(), "1");
	}
	
	@Test
	public void testEndpointsProtocolInformationEndpointProtocolVersionRegex() throws IOException {
		checkSearchPathRegexResult(AasRegistryPaths.endpoints().protocolInformation().endpointProtocolVersion(), "a.*c");
	}

	
	@Test
	public void testShellExtensionName() throws IOException {
		ShellDescriptorQuery query = new ShellDescriptorQuery(AasRegistryPaths.extensions().value(), "RED").extensionName("COLOR");
		checkSearchPathResult(query);
	}

	@Test
	public void testMultipleShellExtensions() throws IOException {
		String path = AasRegistryPaths.extensions().value();
		ShellDescriptorQuery query = new ShellDescriptorQuery(path, "RED").extensionName("COLOR").combinedWith(new ShellDescriptorQuery(path, "BLUE").extensionName("COLOR"));
		checkSearchPathResult(query);
	}

	private void checkSearchPathMatchResult(String path, String value) throws IOException {
		checkSearchPathResult(new ShellDescriptorQuery(path, value));
	}
	
	private void checkSearchPathRegexResult(String path, String value) throws IOException {
		checkSearchPathResult(new ShellDescriptorQuery(path, value).queryType(QueryTypeEnum.REGEX));
	}
	
	private void checkSearchPathResult(ShellDescriptorQuery sdQuery) throws IOException {
		GroupedQueries queries = ShellDescriptorSearchRequests.groupQueries(sdQuery);
		Criteria criteria = builder.buildCriteria(queries);
		Query query = Query.query(criteria);
		String result = query.getQueryObject().toJson(JsonWriterSettings.builder().indent(true).build());
		String exprected = loader.loadJsonAsString();
		assertThat(result).isEqualToIgnoringWhitespace(exprected);
	}

}

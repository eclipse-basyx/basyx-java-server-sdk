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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.digitaltwin.basyx.aasregistry.paths.AasRegistryPaths;
import org.eclipse.digitaltwin.basyx.aasregistry.model.AssetKind;
import org.eclipse.digitaltwin.basyx.aasregistry.model.ShellDescriptorQuery.QueryTypeEnum;
import org.eclipse.digitaltwin.basyx.aasregistry.service.storage.mongodb.SearchPathCriteriaBuilder;
import org.junit.Test;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

public class SearchPathCriteriaBuilderTest {

	private static Map<String, String> PATH_MAPPINGS = new HashMap<>();

	static {
		PATH_MAPPINGS.put(AasRegistryPaths.id(), "_id");
		PATH_MAPPINGS.put(AasRegistryPaths.submodelDescriptors().id(), AasRegistryPaths.submodelDescriptors() + "." + "_id");
	}

	private SearchPathCriteriaBuilder builder = new SearchPathCriteriaBuilder(PATH_MAPPINGS);

	@Test
	public void testId() {
		String path = AasRegistryPaths.id();
		String value = "myId";

		String result = buildSearchPathQuery(path, value);
		String expected = "{\"_id\": \"myId\"}";
		assertThat(result).isEqualTo(expected);
	}

	@Test
	public void testIdShort() {
		String path = AasRegistryPaths.idShort();
		String value = "myIdShort";
		String result = buildSearchPathQuery(path, value);
		String expected = "{\"idShort\": \"myIdShort\"}";
		assertThat(result).isEqualToIgnoringWhitespace(expected);
	}

	@Test
	public void testAssetKindEnum() {
		String path = AasRegistryPaths.assetKind();
		String value = AssetKind.INSTANCE.toString();
		assertThat(value).isEqualTo("Instance");
		String result = buildSearchPathQuery(path, value);
		String expected = "{\"assetKind\": \"Instance\"}";
		assertThat(result).isEqualToIgnoringWhitespace(expected);
	}

	@Test
	public void testSubmodelIdShort() {
		String path = AasRegistryPaths.submodelDescriptors().idShort();
		String value = "myId";
		String result = buildSearchPathQuery(path, value);
		String expected = "{\"submodelDescriptors\": {\"$elemMatch\": {\"idShort\": \"myId\"}}}";
		assertThat(result).isEqualTo(expected);
	}

	@Test
	public void testSubmodelId() {
		String path = AasRegistryPaths.submodelDescriptors().id();
		String value = "myId";
		String result = buildSearchPathQuery(path, value);
		String expected = "{\"submodelDescriptors\": {\"$elemMatch\": {\"_id\": \"myId\"}}}";
		assertThat(result).isEqualToIgnoringWhitespace(expected);
	}

	@Test
	public void testAdministrationCreatorKeysType() {
		String path = AasRegistryPaths.administration().creator().keys().type();
		String value = "key1";
		String result = buildSearchPathQuery(path, value);
		String expected = "{\"administration.creator.keys\": {\"$elemMatch\": {\"type\": \"key1\"}}}";
		assertThat(result).isEqualToIgnoringWhitespace(expected);
	}

	@Test
	public void testSubmodelDescriptorsEndpointsProtocolInformationSecurityAttributesValue() {
		String path = AasRegistryPaths.submodelDescriptors().endpoints().protocolInformation().securityAttributes().value();
		String value = "attrValue";
		String result = buildSearchPathQuery(path, value);
		String expected = "{\"submodelDescriptors\": { \"$elemMatch\": {\"endpoints\": {\"$elemMatch\": { \"protocolInformation.securityAttributes\": {\"$elemMatch\": {\"value\": \"attrValue\" }}}}}}}";
		assertThat(result).isEqualToIgnoringWhitespace(expected);

	}

	@Test
	public void testEndpointsProtocolInformationEndpointProtocolVersion() {
		String path = AasRegistryPaths.endpoints().protocolInformation().endpointProtocolVersion();
		String value = "1";
		String result = buildSearchPathQuery(path, value);
		// TODO check if this is correct
		String expected = "{\"endpoints\": {\"$elemMatch\": { \"protocolInformation.endpointProtocolVersion\": {\"$in\" : [ \"1\" ]} }}}";
		assertThat(result).isEqualToIgnoringWhitespace(expected);
	}

	@Test
	public void testEndpointsProtocolInformationEndpointProtocolVersionRegex() {
		String path = AasRegistryPaths.endpoints().protocolInformation().endpointProtocolVersion();
		String value = "a.*c";
		String result = buildSearchPathQuery(path, value, QueryTypeEnum.REGEX);
		String expected = "{\"endpoints\": {\"$elemMatch\": {\"protocolInformation.endpointProtocolVersion\": {\"$in\" : [{\"$regularExpression\": {\"pattern\": \"a.*c\", \"options\": \"\"}}]}}}}";
		assertThat(result).isEqualToIgnoringWhitespace(expected);
	}

	private String buildSearchPathQuery(String path, String value) {
		return buildSearchPathQuery(path, value, QueryTypeEnum.MATCH);
	}

	private String buildSearchPathQuery(String path, String value,QueryTypeEnum queryType) {
		Criteria criteria = builder.buildSearchPathCriteria(path, value, queryType);
		Query query = Query.query(criteria);
		return query.getQueryObject().toJson();	
	}
}

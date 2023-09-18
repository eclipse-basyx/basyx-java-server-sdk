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

package org.eclipse.basyx.digitaltwin.aasenvironment.http;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ParseException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.DeserializationException;
import org.eclipse.digitaltwin.basyx.aasenvironment.TestAASEnvironmentSerialization;
import org.eclipse.digitaltwin.basyx.http.Base64UrlEncodedIdentifier;
import org.eclipse.digitaltwin.basyx.http.serialization.BaSyxHttpTestUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.util.ResourceUtils;

public class TestAasEnvironmentHTTP {

	private static final String ACCEPT_JSON = "application/json";
	private static final String ACCEPT_XML = "application/xml";
	private static final String ACCEPT_AASX = "application/asset-administration-shell-package+xml";

	private static ConfigurableApplicationContext appContext;

	@BeforeClass
	public static void startAasRepo() throws Exception {
		appContext = new SpringApplication(DummyAASEnvironmentComponent.class).run(new String[] {});
	}

	@Test
	public void testAASEnvironmentSertializationWithJSON() throws IOException, ParseException, DeserializationException {
		boolean includeConceptDescription = true;

		CloseableHttpResponse response = executeGetOnURL(createSerializationURL(includeConceptDescription), ACCEPT_JSON);
		String actual = BaSyxHttpTestUtils.getResponseAsString(response);
		TestAASEnvironmentSerialization.validateJSON(actual, includeConceptDescription);
	}

	@Test
	public void testAASEnvironmentSertializationWithXML() throws IOException, ParseException, DeserializationException {
		boolean includeConceptDescription = true;

		CloseableHttpResponse response = executeGetOnURL(createSerializationURL(includeConceptDescription), ACCEPT_XML);
		String actual = BaSyxHttpTestUtils.getResponseAsString(response);
		TestAASEnvironmentSerialization.validateXml(actual, includeConceptDescription);
	}

	@Test
	public void testAASEnvironmentSertializationWithAASX() throws IOException, ParseException, DeserializationException, InvalidFormatException {
		boolean includeConceptDescription = true;

		CloseableHttpResponse response = executeGetOnURL(createSerializationURL(includeConceptDescription), ACCEPT_AASX);
		assertEquals(HttpStatus.OK.value(), response.getCode());

		TestAASEnvironmentSerialization.checkAASX(response.getEntity()
				.getContent(), includeConceptDescription);
	}

	@Test
	public void testAASEnvironmentSertializationWithAASXExcludeCD() throws IOException, ParseException, DeserializationException, InvalidFormatException {
		boolean includeConceptDescription = false;

		CloseableHttpResponse response = executeGetOnURL(createSerializationURL(includeConceptDescription), ACCEPT_AASX);
		assertEquals(HttpStatus.OK.value(), response.getCode());

		TestAASEnvironmentSerialization.checkAASX(response.getEntity()
				.getContent(), includeConceptDescription);
	}

	@Test
	public void testAASEnvironmentWithWrongParameter() throws IOException {
		boolean includeConceptDescription = true;

		CloseableHttpResponse response = executeGetOnURL(getSerializationURL(new ArrayList<String>(), new ArrayList<String>(), includeConceptDescription), ACCEPT_JSON);
		assertEquals(HttpStatus.BAD_REQUEST.value(), response.getCode());
	}

	@Test
	public void testAASEnvironmentWithWrongAcceptHeader() throws IOException {
		boolean includeConceptDescription = true;

		CloseableHttpResponse response = executeGetOnURL(getSerializationURL(new ArrayList<String>(), new ArrayList<String>(), includeConceptDescription), "");
		assertEquals(HttpStatus.BAD_REQUEST.value(), response.getCode());
	}

	@Test
	public void testAASEnvironmentWithWrongId() throws IOException {
		boolean includeConceptDescription = true;

		List<String> aasIds = new ArrayList<>();
		List<String> submodelIds = new ArrayList<>();

		aasIds.add("wrongAasId");
		submodelIds.add("wrongSubmodelId");
		CloseableHttpResponse response = executeGetOnURL(getSerializationURL(aasIds, submodelIds, includeConceptDescription), ACCEPT_JSON);
		assertEquals(HttpStatus.NOT_FOUND.value(), response.getCode());
	}

	private String createSerializationURL(boolean includeConceptDescription) {
		return getSerializationURL(createIdCollection(DummyAASEnvironmentComponent.AAS_TECHNICAL_DATA_ID, DummyAASEnvironmentComponent.AAS_OPERATIONAL_DATA_ID),
				createIdCollection(DummyAASEnvironmentComponent.SUBMODEL_OPERATIONAL_DATA_ID, DummyAASEnvironmentComponent.SUBMODEL_TECHNICAL_DATA_ID), includeConceptDescription);
	}

	public static CloseableHttpResponse executeGetOnURL(String url, String header) throws IOException {
		CloseableHttpClient client = HttpClients.createDefault();
		HttpGet getRequest = createGetRequestWithHeader(url, header);
		return client.execute(getRequest);
	}

	private static HttpGet createGetRequestWithHeader(String url, String header) {
		HttpGet aasCreateRequest = new HttpGet(url);
		aasCreateRequest.setHeader("Accept", header);
		return aasCreateRequest;
	}

	private String getURL() {
		return "http://localhost:8081";
	}

	private String getSerializationURL(Collection<String> aasIds, Collection<String> submodelIds, boolean includeConceptDescription) {
		String aasIdsArrayString = createIdsArrayString(aasIds);
		String submodelIdsArrayString = createIdsArrayString(submodelIds);

		return getURL() + "/serialization?aasIds=" + aasIdsArrayString + "&submodelIds=" + submodelIdsArrayString + "&includeConceptDescriptions=" + includeConceptDescription;
	}

	private String createIdsArrayString(Collection<String> ids) {
		String idsArrayString = "";
		for (String id : ids) {
			if (!idsArrayString.isEmpty())
				idsArrayString = idsArrayString.concat(",");
			idsArrayString = idsArrayString.concat(Base64UrlEncodedIdentifier.encodeIdentifier(id));
		}
		return idsArrayString;
	}

	private List<String> createIdCollection(String... ids) {
		List<String> results = new ArrayList<>();
		for (String id : ids) {
			results.add(id);
		}
		return results;
	}

	public String readStringFromFile(String fileName) throws FileNotFoundException, IOException {
		File file = ResourceUtils.getFile(fileName);
		InputStream in = new FileInputStream(file);
		return IOUtils.toString(in, StandardCharsets.UTF_8.name());
	}

	@AfterClass
	public static void shutdown() {
		appContext.close();
	}

}

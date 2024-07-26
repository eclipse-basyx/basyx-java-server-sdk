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
import static org.junit.Assert.assertNotNull;

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
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.ProtocolException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.core.DeserializationException;
import org.eclipse.digitaltwin.basyx.aasenvironment.TestAASEnvironmentSerialization;
import org.eclipse.digitaltwin.basyx.aasrepository.AasRepository;
import org.eclipse.digitaltwin.basyx.conceptdescriptionrepository.ConceptDescriptionRepository;
import org.eclipse.digitaltwin.basyx.http.Base64UrlEncodedIdentifier;
import org.eclipse.digitaltwin.basyx.http.HttpBaSyxHeader;
import org.eclipse.digitaltwin.basyx.http.serialization.BaSyxHttpTestUtils;
import org.eclipse.digitaltwin.basyx.submodelrepository.SubmodelRepository;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.util.ResourceUtils;

public class TestAasEnvironmentHTTP {

	public static final String JSON_MIMETYPE = "application/json";
	public static final String XML_MIMETYPE = "application/xml";
	public static final String AASX_MIMETYPE = "application/asset-administration-shell-package+xml";

	public static final String AASX_ENV_PATH = "testEnvironment.aasx";
	private static final String JSON_ENV_PATH = "testEnvironment.json";
	private static final String XML_ENV_PATH = "testEnvironment.xml";
	private static final String WRONGEXT_ENV_PATH = "testEnvironment.txt";
	private static final String JSON_OPERATIONALDATA_ENV_PATH = "operationalDataEnvironment.json";
	private static final String AASENVIRONMENT_VALUE_ONLY_JSON = "AASEnvironmentValueOnly.json";

	private static ConfigurableApplicationContext appContext;
	private static SubmodelRepository submodelRepo;
	private static AasRepository aasRepo;
	private static ConceptDescriptionRepository conceptDescriptionRepo;

	@BeforeClass
	public static void startAasRepo() throws Exception {
		appContext = new SpringApplication(DummyAASEnvironmentComponent.class).run(new String[] {});
		submodelRepo = appContext.getBean(SubmodelRepository.class);
		aasRepo = appContext.getBean(AasRepository.class);
		conceptDescriptionRepo = appContext.getBean(ConceptDescriptionRepository.class);
	}

	@Test
	public void baSyxResponseHeader() throws IOException, ProtocolException {
		CloseableHttpResponse response = BaSyxHttpTestUtils.executeGetOnURL(getURL());
		assertEquals(HttpBaSyxHeader.HEADER_VALUE, response.getHeader(HttpBaSyxHeader.HEADER_KEY).getValue());
	}

	@Test
	public void testAASEnvironmentSertializationWithJSON() throws IOException, ParseException, DeserializationException {
		boolean includeConceptDescription = true;
		boolean aasIdsIncluded = true;
		boolean submodelIdsIncluded = true;

		CloseableHttpResponse response = executeGetOnURL(createSerializationURL(includeConceptDescription), JSON_MIMETYPE);
		String actual = BaSyxHttpTestUtils.getResponseAsString(response);
		TestAASEnvironmentSerialization.validateJSON(actual, aasIdsIncluded, submodelIdsIncluded, includeConceptDescription);
	}

	@Test
	public void testAASEnvironmentSerialization_ValueOnly() throws FileNotFoundException, IOException, ParseException {
		CloseableHttpResponse response = BaSyxHttpTestUtils.executePostRequest(HttpClients.createDefault(), createPostRequestWithFile(JSON_OPERATIONALDATA_ENV_PATH, JSON_MIMETYPE));
		assertEquals(HttpStatus.OK.value(), response.getCode());

		response = executeGetOnURL(getOperationalDataValueOnlyURL(), JSON_MIMETYPE);
		assertEquals(HttpStatus.OK.value(), response.getCode());

		BaSyxHttpTestUtils.assertSameJSONContent(BaSyxHttpTestUtils.readJSONStringFromClasspath(AASENVIRONMENT_VALUE_ONLY_JSON), BaSyxHttpTestUtils.getResponseAsString(response));
	}

	@Test
	public void testAASEnvironmentSertializationWithXML() throws IOException, ParseException, DeserializationException {
		boolean includeConceptDescription = true;
		boolean aasIdsIncluded = true;
		boolean submodelIdsIncluded = true;

		CloseableHttpResponse response = executeGetOnURL(createSerializationURL(includeConceptDescription), XML_MIMETYPE);
		String actual = BaSyxHttpTestUtils.getResponseAsString(response);
		TestAASEnvironmentSerialization.validateXml(actual, aasIdsIncluded, submodelIdsIncluded, includeConceptDescription);
	}

	@Test
	public void testAASEnvironmentSertializationWithAASX() throws IOException, ParseException, DeserializationException, InvalidFormatException {
		boolean includeConceptDescription = true;
		boolean aasIdsIncluded = true;
		boolean submodelIdsIncluded = true;

		CloseableHttpResponse response = executeGetOnURL(createSerializationURL(includeConceptDescription), AASX_MIMETYPE);
		assertEquals(HttpStatus.OK.value(), response.getCode());

		TestAASEnvironmentSerialization.checkAASX(response.getEntity().getContent(), aasIdsIncluded, submodelIdsIncluded, includeConceptDescription);
	}

	@Test
	public void testAASEnvironmentSertializationWithAASXExcludeCD() throws IOException, ParseException, DeserializationException, InvalidFormatException {
		boolean includeConceptDescription = false;
		boolean aasIdsIncluded = true;
		boolean submodelIdsIncluded = true;

		CloseableHttpResponse response = executeGetOnURL(createSerializationURL(includeConceptDescription), AASX_MIMETYPE);
		assertEquals(HttpStatus.OK.value(), response.getCode());

		TestAASEnvironmentSerialization.checkAASX(response.getEntity().getContent(), aasIdsIncluded, submodelIdsIncluded, includeConceptDescription);
	}
	
	@Test
	public void aasEnvironmentSertializationOnlyAasIds() throws IOException, ParseException, DeserializationException {
		boolean includeConceptDescription = false;
		boolean aasIdsIncluded = true;
		boolean submodelIdsIncluded = false;

		CloseableHttpResponse response = executeGetOnURL(getSerializationURLOnlyAas(createIdCollection(DummyAASEnvironmentComponent.AAS_TECHNICAL_DATA_ID, DummyAASEnvironmentComponent.AAS_OPERATIONAL_DATA_ID), includeConceptDescription), JSON_MIMETYPE);
		assertEquals(HttpStatus.OK.value(), response.getCode());
		
		String actual = BaSyxHttpTestUtils.getResponseAsString(response);
		TestAASEnvironmentSerialization.validateJSON(actual, aasIdsIncluded, submodelIdsIncluded, includeConceptDescription);
	}
	
	@Test
	public void aasEnvironmentSertializationOnlySubmodelIds() throws IOException, ParseException, DeserializationException {
		boolean includeConceptDescription = true;
		boolean aasIdsIncluded = false;
		boolean submodelIdsIncluded = true;

		CloseableHttpResponse response = executeGetOnURL(getSerializationURLOnlySubmodels(createIdCollection(DummyAASEnvironmentComponent.SUBMODEL_OPERATIONAL_DATA_ID, DummyAASEnvironmentComponent.SUBMODEL_TECHNICAL_DATA_ID), includeConceptDescription), JSON_MIMETYPE);
		assertEquals(HttpStatus.OK.value(), response.getCode());
		
		String actual = BaSyxHttpTestUtils.getResponseAsString(response);
		TestAASEnvironmentSerialization.validateJSON(actual, aasIdsIncluded, submodelIdsIncluded, includeConceptDescription);
	}

	@Test
	public void testAASEnvironmentWithWrongParameter() throws IOException {
		boolean includeConceptDescription = true;

		CloseableHttpResponse response = executeGetOnURL(getSerializationURL(new ArrayList<String>(), new ArrayList<String>(), includeConceptDescription), JSON_MIMETYPE);
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
		CloseableHttpResponse response = executeGetOnURL(getSerializationURL(aasIds, submodelIds, includeConceptDescription), JSON_MIMETYPE);
		assertEquals(HttpStatus.NOT_FOUND.value(), response.getCode());
	}

	@Test
	public void testEnvironmentUpload_AASX() throws IOException, InvalidFormatException, UnsupportedOperationException, DeserializationException, ParseException {
		CloseableHttpResponse response = BaSyxHttpTestUtils.executePostRequest(HttpClients.createDefault(), createPostRequestWithFile(AASX_ENV_PATH, AASX_MIMETYPE));

		assertEquals(HttpStatus.OK.value(), response.getCode());

		assertNotNull(aasRepo.getAas("http://customer.com/aas/9175_7013_7091_9168"));
		assertNotNull(submodelRepo.getSubmodel("http://i40.customer.com/type/1/1/7A7104BDAB57E184"));
		assertNotNull(conceptDescriptionRepo.getConceptDescription("http://www.vdi2770.com/blatt1/Entwurf/Okt18/cd/Description/Title"));
	}

	@Test
	public void testEnvironmentUpload_JSON() throws IOException, InvalidFormatException, UnsupportedOperationException, DeserializationException, ParseException {
		CloseableHttpResponse response = BaSyxHttpTestUtils.executePostRequest(HttpClients.createDefault(), createPostRequestWithFile(JSON_ENV_PATH, JSON_MIMETYPE));

		assertEquals(HttpStatus.OK.value(), response.getCode());

		assertNotNull(aasRepo.getAas("https://acplt.test/Test_AssetAdministrationShell"));
		assertNotNull(submodelRepo.getSubmodel("http://acplt.test/Submodels/Assets/TestAsset/Identification"));
		assertNotNull(conceptDescriptionRepo.getConceptDescription("https://acplt.test/Test_ConceptDescription"));
	}

	@Test
	public void testEnvironmentUpload_XML() throws IOException, InvalidFormatException, UnsupportedOperationException, DeserializationException, ParseException {
		CloseableHttpResponse response = BaSyxHttpTestUtils.executePostRequest(HttpClients.createDefault(), createPostRequestWithFile(XML_ENV_PATH, XML_MIMETYPE));

		assertEquals(HttpStatus.OK.value(), response.getCode());

		assertNotNull(aasRepo.getAas("http://customer.test/aas/9175_7013_7091_9168"));
		assertNotNull(submodelRepo.getSubmodel("http://i40.customer.test/type/1/1/7A7104BDAB57E184"));
		assertNotNull(conceptDescriptionRepo.getConceptDescription("http://www.vdi2770.test/blatt1/Entwurf/Okt18/cd/Description/Title"));
	}

	@Test
	public void testEnvironmentUpload_WrongExtension() throws IOException, InvalidFormatException, UnsupportedOperationException, DeserializationException, ParseException {
		CloseableHttpResponse response = BaSyxHttpTestUtils.executePostRequest(HttpClients.createDefault(), createPostRequestWithFile(WRONGEXT_ENV_PATH, "text/plain"));

		assertEquals(HttpStatus.BAD_REQUEST.value(), response.getCode());
	}

	public static String createSerializationURL(boolean includeConceptDescription) {
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

	private static HttpPost createPostRequestWithFile(String filepath, String contentType) throws FileNotFoundException {
		java.io.File file = ResourceUtils.getFile("classpath:" + filepath);

		return BaSyxHttpTestUtils.createPostRequestWithFile(getAASXUploadURL(), file, contentType);
	}

	public static String getURL() {
		return "http://localhost:8081";
	}

	private static String getAASXUploadURL() {
		return getURL() + "/upload";
	}

	public static String getOperationalDataValueOnlyURL() {
		return getURL() + "/submodels/d3d3LmV4YW1wbGUuY29tL2lkcy9zbS8yMjIyXzgwNDFfMTA0Ml84MDU3/$value";
	}

	public static String getSerializationURL(Collection<String> aasIds, Collection<String> submodelIds, boolean includeConceptDescription) {
		String aasIdsArrayString = createIdsArrayString(aasIds);
		String submodelIdsArrayString = createIdsArrayString(submodelIds);

		return getURL() + "/serialization?" + getAasIdsParameter(aasIdsArrayString) + "&" + getSubmodelIdsParameter(submodelIdsArrayString) + "&includeConceptDescriptions=" + includeConceptDescription;
	}
	
	private static String getSerializationURLOnlyAas(Collection<String> aasIds, boolean includeConceptDescription) {
		String aasIdsArrayString = createIdsArrayString(aasIds);

		return getURL() + "/serialization?" + getAasIdsParameter(aasIdsArrayString) + "&includeConceptDescriptions=" + includeConceptDescription;
	}
	
	private static String getSerializationURLOnlySubmodels(Collection<String> submodelIds, boolean includeConceptDescription) {
		String submodelIdsArrayString = createIdsArrayString(submodelIds);
		
		return getURL() + "/serialization?" + getSubmodelIdsParameter(submodelIdsArrayString) + "&includeConceptDescriptions=" + includeConceptDescription;
	}
	
	private static String getAasIdsParameter(String aasIdsArrayString) {
		return "aasIds=" + aasIdsArrayString;
	}
	
	private static String getSubmodelIdsParameter(String submodelIdsArrayString) {
		return "submodelIds=" + submodelIdsArrayString;
	}

	private static String createIdsArrayString(Collection<String> ids) {
		String idsArrayString = "";
		for (String id : ids) {
			if (!idsArrayString.isEmpty())
				idsArrayString = idsArrayString.concat(",");
			idsArrayString = idsArrayString.concat(Base64UrlEncodedIdentifier.encodeIdentifier(id));
		}
		return idsArrayString;
	}

	public static List<String> createIdCollection(String... ids) {
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

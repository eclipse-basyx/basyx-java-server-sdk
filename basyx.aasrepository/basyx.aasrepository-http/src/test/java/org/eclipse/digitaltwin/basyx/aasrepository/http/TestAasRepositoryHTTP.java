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

package org.eclipse.digitaltwin.basyx.aasrepository.http;

import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.ParseException;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.DeserializationException;
import org.eclipse.digitaltwin.basyx.http.Base64UrlEncodedIdentifier;
import org.eclipse.digitaltwin.basyx.http.serialization.BaSyxHttpTestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class TestAasRepositoryHTTP {
	private static final String aasAccessURL = "http://localhost:8080/shells";

	private static final String dummyAasId = "customIdentifier";

	private ConfigurableApplicationContext appContext;

	@Before
	public void startAasRepo() throws Exception {
		appContext = new SpringApplication(DummyAasRepositoryComponent.class).run(new String[] {});
	}

	@After
	public void shutdownAasRepo() {
		appContext.close();
	}

	@Test
	public void aasUpload() throws IOException, ParseException {
		String aasJsonContent = getAasJSONString();
		CloseableHttpResponse creationResponse = createAasOnServer(aasJsonContent);

		assertEquals(201, creationResponse.getCode());
		BaSyxHttpTestUtils.assertSameJSONContent(aasJsonContent, BaSyxHttpTestUtils.getResponseAsString(creationResponse));
	}

	@Test
	public void aasRoundtrip() throws JsonMappingException, ParseException, JsonProcessingException, IOException {
		String aasJsonContent = createDummyAasOnServer();

		CloseableHttpResponse retrievalResponse = getSpecificAas(dummyAasId);
		BaSyxHttpTestUtils.assertSameJSONContent(aasJsonContent, BaSyxHttpTestUtils.getResponseAsString(retrievalResponse));
	}

	@Test
	public void aasIdentifierCollision() throws FileNotFoundException, IOException {
		String aasJsonContent = createDummyAasOnServer();

		CloseableHttpResponse creationResponse = createAasOnServer(aasJsonContent);

		assertEquals(HttpStatus.CONFLICT.value(), creationResponse.getCode());
	}

	@Test
	public void getAasByIdentifier() throws FileNotFoundException, IOException, ParseException {
		String aasJsonContent = createDummyAasOnServer();

		CloseableHttpResponse response = getSpecificAas(dummyAasId);

		BaSyxHttpTestUtils.assertSameJSONContent(aasJsonContent, BaSyxHttpTestUtils.getResponseAsString(response));
	}

	@Test
	public void getNonExistingAasByIdentifier() throws IOException {
		CloseableHttpResponse response = getSpecificAas("nonExisting");

		assertEquals(HttpStatus.NOT_FOUND.value(), response.getCode());
	}

	@Test
	public void getAllAasWhenEmpty() throws IOException {
		CloseableHttpResponse retrievalResponse = getAllAas();

		assertEquals(HttpStatus.OK.value(), retrievalResponse.getCode());
	}

	@Test
	public void deleteAas() throws IOException {
		createDummyAasOnServer();

		String url = getSpecificAasAccessURL(dummyAasId);
		CloseableHttpResponse deleteResponse = BaSyxHttpTestUtils.executeDeleteOnURL(url);
		assertEquals(HttpStatus.NO_CONTENT.value(), deleteResponse.getCode());

		assertAasIsNotOnServer(dummyAasId);
	}

	@Test
	public void deleteNonExistingAas() throws IOException {
		String url = getSpecificAasAccessURL("nonExisting");
		CloseableHttpResponse deleteResponse = BaSyxHttpTestUtils.executeDeleteOnURL(url);
		assertEquals(HttpStatus.NOT_FOUND.value(), deleteResponse.getCode());
	}

	@Test
	public void getSubmodelReference()
			throws FileNotFoundException, IOException, ParseException, DeserializationException {
		String json = getSingleSubmodelReference();

		createDummyAasOnServer();
		addSubmodelReferenceToDummyAas(json);

		CloseableHttpResponse getResponse = BaSyxHttpTestUtils
				.executeGetOnURL(getSpecificAasAccessURL(dummyAasId) + "/aas/submodels");

		String responseString = BaSyxHttpTestUtils.getResponseAsString(getResponse);

		BaSyxHttpTestUtils.assertSameJSONContent(getSingleSubmodelReferenceAsJsonArray(), responseString);
	}

	@Test
	public void postSubmodelReference() throws FileNotFoundException, IOException, ParseException {
		createDummyAasOnServer();

		String json = getSingleSubmodelReference();

		addSubmodelReferenceToDummyAas(json);

		CloseableHttpResponse getResponse = BaSyxHttpTestUtils
				.executeGetOnURL(getSpecificAasAccessURL(dummyAasId) + "/aas/submodels");

		BaSyxHttpTestUtils.assertSameJSONContent(getSingleSubmodelReferenceAsJsonArray(),
				BaSyxHttpTestUtils.getResponseAsString(getResponse));
	}

	@Test
	public void removeSubmodelReference() throws FileNotFoundException, IOException, ParseException {
		createDummyAasOnServer();

		String json = getSingleSubmodelReference();

		addSubmodelReferenceToDummyAas(json);

		String url = getSpecificSubmodelReferenceUrl();

		CloseableHttpResponse deleteResponse = BaSyxHttpTestUtils.executeDeleteOnURL(url);
		CloseableHttpResponse getResponse = BaSyxHttpTestUtils
				.executeGetOnURL(getSpecificAasAccessURL(dummyAasId) + "/aas/submodels");

		assertEquals(200, deleteResponse.getCode());
		BaSyxHttpTestUtils.assertSameJSONContent("[]", BaSyxHttpTestUtils.getResponseAsString(getResponse));

	}

	@Test
	public void removeNonExistingSubmodelReference() throws FileNotFoundException, IOException {
		createDummyAasOnServer();
		String url = getSpecificSubmodelReferenceUrl();
		CloseableHttpResponse deleteResponse = BaSyxHttpTestUtils.executeDeleteOnURL(url);
		assertEquals(HttpStatus.NOT_FOUND.value(), deleteResponse.getCode());
	}

	@Test
	public void getAssetInformationByIdentifier() throws FileNotFoundException, IOException, ParseException {
		createDummyAasOnServer();
		String url = getSpecificAssetInformationAccessURL(dummyAasId);
		CloseableHttpResponse response = BaSyxHttpTestUtils.executeGetOnURL(url);

		String expected = BaSyxHttpTestUtils.readJSONStringFromFile("classpath:assetInfoSimple.json");

		BaSyxHttpTestUtils.assertSameJSONContent(expected, BaSyxHttpTestUtils.getResponseAsString(response));
	}


	@Test
	public void getNonExistingAssetInformationByIdentifier() throws FileNotFoundException, IOException, ParseException {
		String url = getSpecificAasAccessURL("nonExisting") + "/asset-information";
		CloseableHttpResponse response = BaSyxHttpTestUtils.executeGetOnURL(url);

		assertEquals(HttpStatus.NOT_FOUND.value(), response.getCode());
	}

	@Test
	public void postAssetInformationByIdentifier() throws FileNotFoundException, IOException, ParseException {
		createDummyAasOnServer();

		String json = BaSyxHttpTestUtils.readJSONStringFromFile("classpath:exampleAssetInfo.json");

		BaSyxHttpTestUtils.executePostOnServer(getSpecificAssetInformationAccessURL(dummyAasId), json);

		CloseableHttpResponse response = BaSyxHttpTestUtils
				.executeGetOnURL(getSpecificAssetInformationAccessURL(dummyAasId));

		BaSyxHttpTestUtils.assertSameJSONContent(json, BaSyxHttpTestUtils.getResponseAsString(response));
	}

	@Test
	public void postAssetInformationToNonExistingAasByIdentifier()
			throws FileNotFoundException, IOException, ParseException {

		String json = BaSyxHttpTestUtils.readJSONStringFromFile("classpath:exampleAssetInfo.json");

		CloseableHttpResponse response = BaSyxHttpTestUtils
				.executeGetOnURL(getSpecificAssetInformationAccessURL("nonExisting"));

		assertEquals(HttpStatus.NOT_FOUND.value(), response.getCode());
	}
	

	private String getSpecificAssetInformationAccessURL(String aasID) {
		return getSpecificAasAccessURL(aasID) + "/asset-information";
	}

	@Test
	public void updateAAS() throws JsonMappingException, JsonProcessingException, ParseException, IOException {
		createDummyAasOnServer();
		
		String expectedAasJSON = getUpdatedAasJSONString();

		CloseableHttpResponse creationResponse = updateSpecificAas(dummyAasId, expectedAasJSON);
		
		assertEquals(HttpStatus.NO_CONTENT.value(), creationResponse.getCode());

		String aasJson = requestSpecificAasJSON(dummyAasId);
		
		BaSyxHttpTestUtils.assertSameJSONContent(expectedAasJSON, aasJson);
	}
	
	@Test
	public void updateNonExistingAas() throws IOException {
		String url = getSpecificAasAccessURL("nonExisting");
		
		String expectedAasJSON = getAasJSONString();
		
		CloseableHttpResponse updateResponse = BaSyxHttpTestUtils.executePutOnURL(url, expectedAasJSON);
		
		assertEquals(HttpStatus.NOT_FOUND.value(), updateResponse.getCode());
	}

	private CloseableHttpResponse updateSpecificAas(String dummyaasid, String aasJsonContent) throws IOException {
		return BaSyxHttpTestUtils.executePutOnURL(getSpecificAasAccessURL(dummyaasid), aasJsonContent);
	}

	private String createDummyAasOnServer() throws FileNotFoundException, IOException {
		String aasJsonContent = getAasJSONString();
		createAasOnServer(aasJsonContent);
		return aasJsonContent;
	}
	
	private String requestSpecificAasJSON(String aasId) throws IOException, ParseException {
		CloseableHttpResponse response = getSpecificAas(aasId);

		return BaSyxHttpTestUtils.getResponseAsString(response);
	}

	private String getSpecificSubmodelReferenceUrl() {
		Base64UrlEncodedIdentifier identifier = new Base64UrlEncodedIdentifier(
				"http://i40.customer.com/type/1/1/testSubmodel");
		return getSpecificAasAccessURL(dummyAasId) + "/aas/submodels/"
				+ identifier.getEncodedIdentifier();
	}

	private void addSubmodelReferenceToDummyAas(String json)
			throws FileNotFoundException, IOException {

		BaSyxHttpTestUtils
				.executePostOnServer(getSpecificAasAccessURL(dummyAasId) + "/aas/submodels", json);
	}

	private String getSpecificAasAccessURL(String aasId) {
		return aasAccessURL + "/" + Base64UrlEncodedIdentifier.encodeIdentifier(aasId);
	}

	private void assertAasIsNotOnServer(String aasId) throws IOException {
		CloseableHttpResponse getResponse = getSpecificAas(aasId);
		assertEquals(HttpStatus.NOT_FOUND.value(), getResponse.getCode());
	}

	private CloseableHttpResponse getAllAas() throws IOException {
		return BaSyxHttpTestUtils.executeGetOnURL(aasAccessURL);
	}

	private CloseableHttpResponse getSpecificAas(String aasId) throws IOException {
		String url = getSpecificAasAccessURL(aasId);
		return BaSyxHttpTestUtils.executeGetOnURL(url);
	}

	private CloseableHttpResponse createAasOnServer(String aasJsonContent) throws IOException {
		return BaSyxHttpTestUtils.executePostOnServer(aasAccessURL, aasJsonContent);
	}

	private String getAasJSONString() throws FileNotFoundException, IOException {
		return BaSyxHttpTestUtils.readJSONStringFromFile("classpath:AasSimple.json");
	}

	private String getSingleSubmodelReference() throws FileNotFoundException, IOException {
		return BaSyxHttpTestUtils.readJSONStringFromFile("classpath:SingleSubmodelReference.json");
	}
	
	private String getUpdatedAasJSONString() throws FileNotFoundException, IOException {
		return BaSyxHttpTestUtils.readJSONStringFromFile("classpath:UpdatedAasSimple.json");
	}

	private String getSingleSubmodelReferenceAsJsonArray() throws FileNotFoundException, IOException {
		return "[" + getSingleSubmodelReference() + "]";
	}
}

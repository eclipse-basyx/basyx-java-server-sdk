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
import static org.eclipse.digitaltwin.basyx.aasrepository.http.BaSyxAASHttpTestUtils.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.ParseException;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.DeserializationException;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.basyx.aasservice.DummyAssetAdministrationShell;
import org.eclipse.digitaltwin.basyx.aasservice.DummySubmodelReference;
import org.eclipse.digitaltwin.basyx.http.Base64UrlEncodedIdentifier;
import org.eclipse.digitaltwin.basyx.http.serialization.BaSyxHttpTestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * Base testsuite for all Aas Repository HTTP tests
 * 
 * @author schnicke
 *
 */
public abstract class AasRepositoryHTTPSuite {
	private static final String dummyAasId = "customIdentifier";
	
	@Before
	public abstract void populateRepository();
	
	@After
	public abstract void resetRepository();

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
	public void getAllAasWhenEmpty() throws IOException, ParseException {
		resetRepository();
		
		CloseableHttpResponse retrievalResponse = getAllAas();

		assertEquals(HttpStatus.OK.value(), retrievalResponse.getCode());
	}
	
	@Test
	public void getAllAasWithLimitAndNoCursor() throws IOException, ParseException {
		int limit = 2;
		String expectedJsonFromFile = getAasJsonWithLimit2AndNoCursor();
		
		CloseableHttpResponse retrievalResponse = getAllAasWithLimitAndNoCursor(getAllAasURLWithLimit(limit));
		
		String actualJsonFromServer = BaSyxHttpTestUtils.getResponseAsString(retrievalResponse);
		
		BaSyxHttpTestUtils.assertSameJSONContent(expectedJsonFromFile, actualJsonFromServer);
	}
	
	@Test
	public void getAllAasWithLimitAndExistingCursor() throws IOException, ParseException {
		String expectedJsonFromFile = getAasJsonWithLimit3AndCursor();
		
		CloseableHttpResponse retrievalResponse = getAllAasWithLimitAndCursor(3, "arbitraryAAS1");
		
		String actualJsonFromServer = BaSyxHttpTestUtils.getResponseAsString(retrievalResponse);
		
		BaSyxHttpTestUtils.assertSameJSONContent(expectedJsonFromFile, actualJsonFromServer);
	}
	
	@Test
	public void getAllAasWithLimitAndNonExistingCursor() throws IOException, ParseException {		
		CloseableHttpResponse retrievalResponse = getAllAasWithLimitAndCursor(2, "nonExisting");
		
		assertEquals(HttpStatus.NOT_FOUND.value(), retrievalResponse.getCode());
	}
	
	@Test
	public void getAllAasWithNoLimitAndExistingCursor() throws IOException, ParseException {
		String expectedJsonFromFile = getAllAasJsonWithNoLimitAndExistingCursor();
		
		CloseableHttpResponse retrievalResponse = getAllAasWithNoLimitAndExistingCursor("arbitrary");
		
		String actualJsonFromServer = BaSyxHttpTestUtils.getResponseAsString(retrievalResponse);
		
		BaSyxHttpTestUtils.assertSameJSONContent(expectedJsonFromFile, actualJsonFromServer);
	}
	
	@Test
	public void getAllAasWithNoLimitAndNoCursor() throws IOException, ParseException {
		String expectedJsonFromFile = getAllAasJsonWithNoLimitAndNoCursor();
		
		CloseableHttpResponse retrievalResponse = getAllAas();
		
		String actualJsonFromServer = BaSyxHttpTestUtils.getResponseAsString(retrievalResponse);
		
		BaSyxHttpTestUtils.assertSameJSONContent(expectedJsonFromFile, actualJsonFromServer);
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
	public void getSubmodelReferenceNoLimitAndNoCursor() throws FileNotFoundException, IOException, ParseException, DeserializationException {
		String json = getSingleSubmodelReference();

		addSubmodelReferenceToDummyAas(json);

		CloseableHttpResponse getResponse = BaSyxHttpTestUtils.executeGetOnURL(getSpecificAasSubmodelRefAccessURL(DummyAssetAdministrationShell.AAS_ID));

		String responseString = BaSyxHttpTestUtils.getResponseAsString(getResponse);

		BaSyxHttpTestUtils.assertSameJSONContent(getSingleSubmodelReferenceAsJsonArray(), responseString);
	}
	
	@Test
	public void getSubmodelReferenceWithLimitAndNoCursor() throws FileNotFoundException, IOException, ParseException, DeserializationException {
		CloseableHttpResponse getResponse = BaSyxHttpTestUtils.executeGetOnURL(getSpecificAasSubmodelRefAccessURL(DummyAssetAdministrationShell.AAS_ID_4));

		String responseString = BaSyxHttpTestUtils.getResponseAsString(getResponse);

		BaSyxHttpTestUtils.assertSameJSONContent(getMultipleSubmodelReference(), responseString);
	}

	@Test
	public void createSubmodelReference() throws FileNotFoundException, IOException, ParseException {
		String json = getSingleSubmodelReference();

		addSubmodelReferenceToDummyAas(json);

		CloseableHttpResponse getResponse = BaSyxHttpTestUtils.executeGetOnURL(getSpecificAasSubmodelRefAccessURL(DummyAssetAdministrationShell.AAS_ID));

		BaSyxHttpTestUtils.assertSameJSONContent(getSingleSubmodelReferenceAsJsonArray(), BaSyxHttpTestUtils.getResponseAsString(getResponse));
	}

	@Test
	public void removeSubmodelReference() throws FileNotFoundException, IOException, ParseException {
		String json = getSingleSubmodelReference();

		addSubmodelReferenceToDummyAas(json);

		String url = getSpecificSubmodelReferenceUrl();

		CloseableHttpResponse deleteResponse = BaSyxHttpTestUtils.executeDeleteOnURL(url);
		CloseableHttpResponse getResponse = BaSyxHttpTestUtils.executeGetOnURL(getSpecificAasSubmodelRefAccessURL(DummyAssetAdministrationShell.AAS_ID));

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
	public void updateAssetInformationByIdentifier() throws FileNotFoundException, IOException, ParseException {
		createDummyAasOnServer();

		String json = BaSyxHttpTestUtils.readJSONStringFromFile("classpath:assetInfoUpdate.json");

		BaSyxHttpTestUtils.executePutOnURL(getSpecificAssetInformationAccessURL(dummyAasId), json);

		CloseableHttpResponse response = BaSyxHttpTestUtils.executeGetOnURL(getSpecificAssetInformationAccessURL(dummyAasId));

		BaSyxHttpTestUtils.assertSameJSONContent(json, BaSyxHttpTestUtils.getResponseAsString(response));
	}

	@Test
	public void updateAssetInformationToNonExistingAasByIdentifier() throws FileNotFoundException, IOException, ParseException {
		CloseableHttpResponse response = BaSyxHttpTestUtils.executeGetOnURL(getSpecificAssetInformationAccessURL("nonExisting"));

		assertEquals(HttpStatus.NOT_FOUND.value(), response.getCode());
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

	@Test
	public void updateAasWithIdMismatch() throws IOException {
		createDummyAasOnServer();

		String aasUpdateJson = getUpdatedAasIdMismatchJSONString();

		CloseableHttpResponse creationResponse = updateSpecificAas(dummyAasId, aasUpdateJson);

		assertEquals(HttpStatus.BAD_REQUEST.value(), creationResponse.getCode());
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
		Base64UrlEncodedIdentifier identifier = new Base64UrlEncodedIdentifier("http://i40.customer.com/type/1/1/testSubmodel");
		return getSpecificAasSubmodelRefAccessURL(DummyAssetAdministrationShell.AAS_ID) + "/" + identifier.getEncodedIdentifier();
	}

	private String getSpecificAasSubmodelRefAccessURL(String aasId) {
		return getSpecificAasAccessURL(aasId) + "/submodel-refs";
	}

	private String getSpecificAssetInformationAccessURL(String aasID) {
		return getSpecificAasAccessURL(aasID) + "/asset-information";
	}

	private void addSubmodelReferenceToDummyAas(String json) throws FileNotFoundException, IOException {
		BaSyxHttpTestUtils.executePostOnURL(getSpecificAasSubmodelRefAccessURL(DummyAssetAdministrationShell.AAS_ID), json);
	}

	protected String getSpecificAasAccessURL(String aasId) {
		return getURL() + "/" + Base64UrlEncodedIdentifier.encodeIdentifier(aasId);
	}

	private void assertAasIsNotOnServer(String aasId) throws IOException {
		CloseableHttpResponse getResponse = getSpecificAas(aasId);
		assertEquals(HttpStatus.NOT_FOUND.value(), getResponse.getCode());
	}

	protected CloseableHttpResponse getAllAas() throws IOException {
		return BaSyxHttpTestUtils.executeGetOnURL(getURL());
	}
	
	protected CloseableHttpResponse getAllAasWithLimitAndNoCursor(String url) throws IOException {
		return BaSyxHttpTestUtils.executeGetOnURL(url);
	}
	
	protected CloseableHttpResponse getAllAasWithLimitAndCursor(Integer limit, String cursor) throws IOException {
		return BaSyxHttpTestUtils.executeGetOnURL(getAllAasURLWithLimitAndCursor(limit, cursor));
	}
	
	protected CloseableHttpResponse getAllAasWithNoLimitAndExistingCursor(String cursor) throws IOException {
		return BaSyxHttpTestUtils.executeGetOnURL(getAllAasURLWithNoLimitAndCursor(cursor));
	}

	private CloseableHttpResponse getSpecificAas(String aasId) throws IOException {
		String url = getSpecificAasAccessURL(aasId);
		return BaSyxHttpTestUtils.executeGetOnURL(url);
	}

	private CloseableHttpResponse createAasOnServer(String aasJsonContent) throws IOException {
		return BaSyxHttpTestUtils.executePostOnURL(getURL(), aasJsonContent);
	}

	private String getAasJSONString() throws FileNotFoundException, IOException {
		return BaSyxHttpTestUtils.readJSONStringFromFile("classpath:AasSimple.json");
	}

	private String getSingleSubmodelReference() throws FileNotFoundException, IOException {
		return BaSyxHttpTestUtils.readJSONStringFromFile("classpath:SingleSubmodelReference.json");
	}
	
	private String getMultipleSubmodelReference() throws FileNotFoundException, IOException {
		return BaSyxHttpTestUtils.readJSONStringFromFile("classpath:MultipleSubmodelReference.json");
	}
	
	private String getAasJsonWithLimit2AndNoCursor() throws FileNotFoundException, IOException {
		return BaSyxHttpTestUtils.readJSONStringFromFile("classpath:AasWithLimit2.json");
	}
	
	private String getAasJsonWithLimit3AndCursor() throws FileNotFoundException, IOException {
		return BaSyxHttpTestUtils.readJSONStringFromFile("classpath:AasWithLimit3AndCursor.json");
	}
	
	private String getAllAasJsonWithNoLimitAndNoCursor() throws FileNotFoundException, IOException {
		return BaSyxHttpTestUtils.readJSONStringFromFile("classpath:AllAasNoLimit.json");
	}
	
	private String getAllAasJsonWithNoLimitAndExistingCursor() throws FileNotFoundException, IOException {
		return BaSyxHttpTestUtils.readJSONStringFromFile("classpath:AllAasNoLimitAndExistingCursor.json");
	}

	private String getUpdatedAasJSONString() throws FileNotFoundException, IOException {
		return BaSyxHttpTestUtils.readJSONStringFromFile("classpath:UpdatedAasSimple.json");
	}

	private String getUpdatedAasIdMismatchJSONString() throws FileNotFoundException, IOException {
		return BaSyxHttpTestUtils.readJSONStringFromFile("classpath:UpdatedAasIdMismatch.json");
	}

	private String getSingleSubmodelReferenceAsJsonArray() throws FileNotFoundException, IOException {
		return "[" + getSingleSubmodelReference() + "]";
	}
	
	protected List<AssetAdministrationShell> createDummyAssetAdministrationShells() {
		return DummyAssetAdministrationShell.getAllDummyAASs().stream().collect(Collectors.toList());
	}

}

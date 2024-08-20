/*******************************************************************************
 * Copyright (C) 2024 the Eclipse BaSyx Authors
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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.apache.hc.client5.http.ClientProtocolException;
import org.apache.hc.client5.http.classic.methods.HttpPut;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.ProtocolException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.core.DeserializationException;
import org.eclipse.digitaltwin.basyx.http.Base64UrlEncodedIdentifier;
import org.eclipse.digitaltwin.basyx.http.HttpBaSyxHeader;
import org.eclipse.digitaltwin.basyx.http.pagination.Base64UrlEncodedCursor;
import org.eclipse.digitaltwin.basyx.http.serialization.BaSyxHttpTestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.util.ResourceUtils;

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
	private static final String THUMBNAIL_FILE_PATH = "BaSyx-Logo.png";

	private final String CURSOR = "AasNumber3Identifier";
	private final String ENCODED_CURSOR = Base64UrlEncodedCursor.encodeCursor(CURSOR);

	protected abstract String getURL();

	@Before
	@After
	public abstract void resetRepository();

	@Test
	public void baSyxResponseHeader() throws IOException, ProtocolException {
		CloseableHttpResponse response = BaSyxHttpTestUtils.executeGetOnURL(getURL());
		assertEquals(HttpBaSyxHeader.HEADER_VALUE, response.getHeader(HttpBaSyxHeader.HEADER_KEY).getValue());
	}

	@Test
	public void aasUpload() throws IOException, ParseException {
		String aasJsonContent = getAas1JSONString();
		CloseableHttpResponse creationResponse = createAasOnServer(aasJsonContent);

		assertEquals(201, creationResponse.getCode());
		BaSyxHttpTestUtils.assertSameJSONContent(aasJsonContent, BaSyxHttpTestUtils.getResponseAsString(creationResponse));
	}

	@Test
	public void aasRoundtrip() throws JsonMappingException, ParseException, JsonProcessingException, IOException {
		String aasJsonContent = createDummyAasOnServer(getAas1JSONString());

		CloseableHttpResponse retrievalResponse = getSpecificAas(dummyAasId);
		BaSyxHttpTestUtils.assertSameJSONContent(aasJsonContent, BaSyxHttpTestUtils.getResponseAsString(retrievalResponse));
	}

	@Test
	public void aasIdentifierCollision() throws FileNotFoundException, IOException {
		String aasJsonContent = createDummyAasOnServer(getAas1JSONString());

		CloseableHttpResponse creationResponse = createAasOnServer(aasJsonContent);

		assertEquals(HttpStatus.CONFLICT.value(), creationResponse.getCode());
	}
	
	@Test
	public void createAasWithEmptyId() throws FileNotFoundException, IOException {
		String aasJsonContent = createDummyAasOnServer(getAasWithEmptyIdJSONString());

		CloseableHttpResponse creationResponse = createAasOnServer(aasJsonContent);

		assertEquals(HttpStatus.BAD_REQUEST.value(), creationResponse.getCode());
	}
	
	@Test
	public void createAasWithNullId() throws FileNotFoundException, IOException {
		String aasJsonContent = createDummyAasOnServer(getAasWithNullIdJSONString());

		CloseableHttpResponse creationResponse = createAasOnServer(aasJsonContent);

		assertEquals(HttpStatus.BAD_REQUEST.value(), creationResponse.getCode());
	}

	@Test
	public void getAasByIdentifier() throws FileNotFoundException, IOException, ParseException {
		String aasJsonContent = createDummyAasOnServer(getAas1JSONString());

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
	public void getAllPaginatedAas() throws IOException, ParseException {
		createMultipleAasOnServer();
		
		CloseableHttpResponse retrievalResponse = getAllAas();
		assertEquals(HttpStatus.OK.value(), retrievalResponse.getCode());
		
		String actualJsonFromServer = BaSyxHttpTestUtils.getResponseAsString(retrievalResponse);
		
		BaSyxHttpTestUtils.assertSameJSONContent(getPaginatedAasJSONString(), getJSONWithoutCursorInfo(actualJsonFromServer));
	}

	@Test
	public void deleteAas() throws IOException {
		createDummyAasOnServer(getAas1JSONString());

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
	public void getSubmodelReference() throws FileNotFoundException, IOException, ParseException, DeserializationException {
		String json = getSingleSubmodelReference();

		createDummyAasOnServer(getAas1JSONString());
		addSubmodelReferenceToDummyAas(json);

		CloseableHttpResponse getResponse = BaSyxHttpTestUtils.executeGetOnURL(getSpecificAasSubmodelRefAccessURL(dummyAasId));

		String responseString = BaSyxHttpTestUtils.getResponseAsString(getResponse);

		BaSyxHttpTestUtils.assertSameJSONContent(getPaginatedSingleSMReferenceJson(), responseString);
	}

	@Test
	public void createSubmodelReference() throws FileNotFoundException, IOException, ParseException {
		createDummyAasOnServer(getAas1JSONString());

		String json = getSingleSubmodelReference();

		addSubmodelReferenceToDummyAas(json);

		CloseableHttpResponse getResponse = BaSyxHttpTestUtils.executeGetOnURL(getSpecificAasSubmodelRefAccessURL(dummyAasId));

		BaSyxHttpTestUtils.assertSameJSONContent(getPaginatedSingleSMReferenceJson(), BaSyxHttpTestUtils.getResponseAsString(getResponse));
	}

	@Test
	public void duplicateSubmodelReference() throws FileNotFoundException, IOException, ParseException {
		createDummyAasOnServer(getAas1JSONString());

		String json = getSingleSubmodelReference();

		addSubmodelReferenceToDummyAas(json);
		CloseableHttpResponse response = addSubmodelReferenceToDummyAas(json);
		assertEquals(response.getCode(), HttpStatus.CONFLICT.value());
	}

	@Test
	public void removeSubmodelReference() throws FileNotFoundException, IOException, ParseException {
		createDummyAasOnServer(getAas1JSONString());

		String json = getSingleSubmodelReference();

		addSubmodelReferenceToDummyAas(json);

		String url = getSpecificSubmodelReferenceUrl();

		CloseableHttpResponse deleteResponse = BaSyxHttpTestUtils.executeDeleteOnURL(url);
		CloseableHttpResponse getResponse = BaSyxHttpTestUtils.executeGetOnURL(getSpecificAasSubmodelRefAccessURL(dummyAasId));

		assertEquals(200, deleteResponse.getCode());
		
		String response = BaSyxHttpTestUtils.getResponseAsString(getResponse);
		BaSyxHttpTestUtils.assertSameJSONContent(getSMReferenceRemovalJson(), getJSONWithoutCursorInfo(response));

	}

	@Test
	public void removeNonExistingSubmodelReference() throws FileNotFoundException, IOException {
		createDummyAasOnServer(getAas1JSONString());
		String url = getSpecificSubmodelReferenceUrl();
		CloseableHttpResponse deleteResponse = BaSyxHttpTestUtils.executeDeleteOnURL(url);
		assertEquals(HttpStatus.NOT_FOUND.value(), deleteResponse.getCode());
	}

	@Test
	public void getAssetInformationByIdentifier() throws FileNotFoundException, IOException, ParseException {
		createDummyAasOnServer(getAas1JSONString());
		String url = getSpecificAssetInformationAccessURL(dummyAasId);
		CloseableHttpResponse response = BaSyxHttpTestUtils.executeGetOnURL(url);

		String expected = BaSyxHttpTestUtils.readJSONStringFromClasspath("assetInfoSimple.json");

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
		createDummyAasOnServer(getAas1JSONString());

		String json = BaSyxHttpTestUtils.readJSONStringFromClasspath("assetInfoUpdate.json");

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
		createDummyAasOnServer(getAas1JSONString());

		String expectedAasJSON = getUpdatedAasJSONString();

		CloseableHttpResponse creationResponse = updateSpecificAas(dummyAasId, expectedAasJSON);

		assertEquals(HttpStatus.NO_CONTENT.value(), creationResponse.getCode());

		String aasJson = requestSpecificAasJSON(dummyAasId);

		BaSyxHttpTestUtils.assertSameJSONContent(expectedAasJSON, aasJson);
	}

	@Test
	public void updateNonExistingAas() throws IOException {
		String url = getSpecificAasAccessURL("nonExisting");

		String expectedAasJSON = getAas1JSONString();

		CloseableHttpResponse updateResponse = BaSyxHttpTestUtils.executePutOnURL(url, expectedAasJSON);

		assertEquals(HttpStatus.NOT_FOUND.value(), updateResponse.getCode());
	}

	@Test
	public void updateAasWithIdMismatch() throws IOException {
		createDummyAasOnServer(getAas1JSONString());

		String aasUpdateJson = getUpdatedAasIdMismatchJSONString();

		CloseableHttpResponse creationResponse = updateSpecificAas(dummyAasId, aasUpdateJson);

		assertEquals(HttpStatus.BAD_REQUEST.value(), creationResponse.getCode());
	}

	@Test
	public void paginationResult() throws FileNotFoundException, IOException, ParseException {
		createMultipleAasOnServer();

		CloseableHttpResponse httpResponse = BaSyxHttpTestUtils.executeGetOnURL(getURL() + "?limit=1&cursor=" + ENCODED_CURSOR);

		String response = BaSyxHttpTestUtils.getResponseAsString(httpResponse);
		BaSyxHttpTestUtils.assertSameJSONContent(getPaginatedAas1JSONString(), getJSONWithoutCursorInfo(response));
	}

	@Test
	public void uploadThumbnailToShell() throws IOException {
		createDummyAasOnServer(getAas1JSONString());
		CloseableHttpResponse getThumbnailResponse = uploadThumbnail(dummyAasId);

		assertEquals(HttpStatus.OK.value(), getThumbnailResponse.getCode());

		getThumbnailResponse.close();
	}

	@Test
	public void getThumbnail() throws FileNotFoundException, IOException, ParseException {
		createDummyAasOnServer(getAas1JSONString());

		byte[] expectedFile = readBytesFromClasspath(THUMBNAIL_FILE_PATH);

		uploadThumbnail(dummyAasId);

		CloseableHttpResponse response = BaSyxHttpTestUtils.executeGetOnURL(BaSyxHttpTestUtils.getThumbnailAccessURL(getURL(), dummyAasId));
		assertEquals(HttpStatus.OK.value(), response.getCode());

		byte[] actualFile = EntityUtils.toByteArray(response.getEntity());

		response.close();

		assertArrayEquals(expectedFile, actualFile);
	}

	@Test
	public void getFileFromNotExistElement() throws FileNotFoundException, UnsupportedEncodingException, ClientProtocolException, IOException {
		CloseableHttpResponse response = BaSyxHttpTestUtils.executeGetOnURL(BaSyxHttpTestUtils.getThumbnailAccessURL(getURL(), dummyAasId));

		assertEquals(HttpStatus.NOT_FOUND.value(), response.getCode());

		response.close();
	}

	@Test
	public void deleteThumbnail() throws FileNotFoundException, IOException {
		createDummyAasOnServer(getAas1JSONString());
		uploadThumbnail(dummyAasId);

		CloseableHttpResponse response = BaSyxHttpTestUtils.executeDeleteOnURL(BaSyxHttpTestUtils.getThumbnailAccessURL(getURL(), dummyAasId));
		assertEquals(HttpStatus.OK.value(), response.getCode());

		response = BaSyxHttpTestUtils.executeGetOnURL(BaSyxHttpTestUtils.getThumbnailAccessURL(getURL(), dummyAasId));
		assertEquals(HttpStatus.NOT_FOUND.value(), response.getCode());

		response.close();
	}

	@Test
	public void deleteNonExistingThumbnail() throws FileNotFoundException, UnsupportedEncodingException, ClientProtocolException, IOException {
		CloseableHttpResponse response = BaSyxHttpTestUtils.executeDeleteOnURL(BaSyxHttpTestUtils.getThumbnailAccessURL(getURL(), dummyAasId));

		assertEquals(HttpStatus.NOT_FOUND.value(), response.getCode());
	}

	private String getPaginatedAas1JSONString() throws FileNotFoundException, IOException {
		return BaSyxHttpTestUtils.readJSONStringFromClasspath("PaginatedAasSimple_1.json");
	}
	
	private String getJSONWithoutCursorInfo(String response) throws JsonMappingException, JsonProcessingException {
		return BaSyxHttpTestUtils.removeCursorFromJSON(response);
	}

	private CloseableHttpResponse updateSpecificAas(String dummyaasid, String aasJsonContent) throws IOException {
		return BaSyxHttpTestUtils.executePutOnURL(getSpecificAasAccessURL(dummyaasid), aasJsonContent);
	}
	
	private void createMultipleAasOnServer() throws FileNotFoundException, IOException {
		createAasOnServer(getAas1JSONString());
		createAasOnServer(getAas2JSONString());
		createAasOnServer(getAas3JSONString());
	}

	private String createDummyAasOnServer(String aasJsonContent) throws FileNotFoundException, IOException {
		createAasOnServer(aasJsonContent);
		return aasJsonContent;
	}

	private String requestSpecificAasJSON(String aasId) throws IOException, ParseException {
		CloseableHttpResponse response = getSpecificAas(aasId);

		return BaSyxHttpTestUtils.getResponseAsString(response);
	}

	private String getSpecificSubmodelReferenceUrl() {
		Base64UrlEncodedIdentifier identifier = new Base64UrlEncodedIdentifier("http://i40.customer.com/type/1/1/testSubmodelNew");
		return getSpecificAasSubmodelRefAccessURL(dummyAasId) + "/" + identifier.getEncodedIdentifier();
	}

	private String getSpecificAasSubmodelRefAccessURL(String aasId) {
		return getSpecificAasAccessURL(aasId) + "/submodel-refs";
	}

	private String getSpecificAssetInformationAccessURL(String aasID) {
		return getSpecificAasAccessURL(aasID) + "/asset-information";
	}

	private CloseableHttpResponse addSubmodelReferenceToDummyAas(String json) throws FileNotFoundException, IOException {
		return BaSyxHttpTestUtils.executePostOnURL(getSpecificAasSubmodelRefAccessURL(dummyAasId), json);
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

	private CloseableHttpResponse getSpecificAas(String aasId) throws IOException {
		String url = getSpecificAasAccessURL(aasId);
		return BaSyxHttpTestUtils.executeGetOnURL(url);
	}

	private CloseableHttpResponse createAasOnServer(String aasJsonContent) throws IOException {
		return BaSyxHttpTestUtils.executePostOnURL(getURL(), aasJsonContent);
	}

	private String getAas1JSONString() throws FileNotFoundException, IOException {
		return BaSyxHttpTestUtils.readJSONStringFromClasspath("AasSimple_1.json");
	}
	
	private String getAas3JSONString() throws FileNotFoundException, IOException {
		return BaSyxHttpTestUtils.readJSONStringFromClasspath("AasSimple_3.json");
	}
	
	private String getAasWithNullIdJSONString() throws FileNotFoundException, IOException {
		return BaSyxHttpTestUtils.readJSONStringFromClasspath("AasSimpleWithNullId.json");
	}

	private String getAasWithEmptyIdJSONString() throws FileNotFoundException, IOException {
		return BaSyxHttpTestUtils.readJSONStringFromClasspath("AasSimpleWithEmptyId.json");
	}

	private String getPaginatedAasJSONString() throws FileNotFoundException, IOException {
		return BaSyxHttpTestUtils.readJSONStringFromClasspath("AllPaginatedAas.json");
	}
	
	private String getAas2JSONString() throws FileNotFoundException, IOException {
		return BaSyxHttpTestUtils.readJSONStringFromClasspath("AasSimple_2.json");
	}

	private String getSingleSubmodelReference() throws FileNotFoundException, IOException {
		return BaSyxHttpTestUtils.readJSONStringFromClasspath("SingleSubmodelReference_1.json");
	}
	
	private String getSMReferenceRemovalJson() throws FileNotFoundException, IOException {
		return BaSyxHttpTestUtils.readJSONStringFromClasspath("SMReferenceRemovalResponse.json");
	}

	private String getUpdatedAasJSONString() throws FileNotFoundException, IOException {
		return BaSyxHttpTestUtils.readJSONStringFromClasspath("UpdatedAasSimple.json");
	}

	private String getUpdatedAasIdMismatchJSONString() throws FileNotFoundException, IOException {
		return BaSyxHttpTestUtils.readJSONStringFromClasspath("UpdatedAasIdMismatch.json");
	}
	
	private String getPaginatedSingleSMReferenceJson() throws FileNotFoundException, IOException {
		return BaSyxHttpTestUtils.readJSONStringFromClasspath("PaginatedSingleSMReference.json");
	}

	private CloseableHttpResponse uploadThumbnail(String aasId) throws IOException {
		CloseableHttpClient client = HttpClients.createDefault();

		java.io.File file = ResourceUtils.getFile("classpath:" + THUMBNAIL_FILE_PATH);

		HttpPut putRequest = BaSyxHttpTestUtils.createPutRequestWithFile(BaSyxHttpTestUtils.getThumbnailAccessURL(getURL(), aasId), THUMBNAIL_FILE_PATH, file);

		return BaSyxHttpTestUtils.executePutRequest(client, putRequest);
	}

	private byte[] readBytesFromClasspath(String fileName) throws FileNotFoundException, IOException {
		ClassPathResource classPathResource = new ClassPathResource(fileName);
		InputStream in = classPathResource.getInputStream();

		return in.readAllBytes();
	}


}

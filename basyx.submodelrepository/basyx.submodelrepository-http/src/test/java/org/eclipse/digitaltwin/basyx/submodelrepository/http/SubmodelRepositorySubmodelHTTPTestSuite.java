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

package org.eclipse.digitaltwin.basyx.submodelrepository.http;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.hc.client5.http.ClientProtocolException;
import org.apache.hc.client5.http.classic.methods.HttpPut;
import org.apache.hc.client5.http.entity.mime.FileBody;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.ProtocolException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.basyx.http.Base64UrlEncodedIdentifier;
import org.eclipse.digitaltwin.basyx.http.HttpBaSyxHeader;
import org.eclipse.digitaltwin.basyx.http.pagination.Base64UrlEncodedCursor;
import org.eclipse.digitaltwin.basyx.http.serialization.BaSyxHttpTestUtils;
import org.eclipse.digitaltwin.basyx.submodelservice.DummySubmodelFactory;
import org.eclipse.digitaltwin.basyx.submodelservice.SubmodelServiceHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.util.ResourceUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.common.collect.Lists;

/**
 * Base testsuite for all Submodel Repository HTTP tests
 *
 * @author schnicke
 *
 */
public abstract class SubmodelRepositorySubmodelHTTPTestSuite {
	@Before
	public abstract void populateRepository();

	@After
	public abstract void resetRepository();

	protected abstract String getURL();

	private final String CURSOR = "7A7104BDAB57E184";
	private final String ENCODED_CURSOR = Base64UrlEncodedCursor.encodeCursor(CURSOR);
	
	@Test
	public void baSyxResponseHeader() throws IOException, ProtocolException {
		CloseableHttpResponse response = BaSyxHttpTestUtils.executeGetOnURL(getURL());
		assertEquals(HttpBaSyxHeader.HEADER_VALUE, response.getHeader(HttpBaSyxHeader.HEADER_KEY).getValue());
	}

	@Test
	public void getAllSubmodelsPreconfigured() throws IOException, ParseException {
		String submodelsJSON = BaSyxSubmodelHttpTestUtils.requestAllSubmodels(getURL());
		String expectedSubmodelsJSON = getJSONValueAsString("MultipleSubmodels.json");
		
		BaSyxHttpTestUtils.assertSameJSONContent(expectedSubmodelsJSON,getJSONWithoutCursorInfo(submodelsJSON));
	}
	
	@Test
	public void getAllSubmodelsBySemanticIDPreconfigured() throws IOException, ParseException {	
		String semanticId = Base64UrlEncodedIdentifier.encodeIdentifier(DummySubmodelFactory.SUBMODEL_TECHNICAL_DATA_SEMANTIC_ID);
		String url = getURL() + "?semanticId=" + semanticId;
		
		String submodelsJSON = BaSyxSubmodelHttpTestUtils.requestAllSubmodels(url);
		
		String expectedSubmodelsJSON = getJSONValueAsString("MultipleSubmodelsWithSameSemanticId.json");
		
		BaSyxHttpTestUtils.assertSameJSONContent(expectedSubmodelsJSON,getJSONWithoutCursorInfo(submodelsJSON));
	}

	@Test
	public void getSpecificSubmodel() throws ParseException, IOException {
		String submodelJSON = requestSpecificSubmodelJSON(DummySubmodelFactory.createTechnicalDataSubmodel().getId());
		String expectedSubmodelJSON = getJSONValueAsString("SingleSubmodel.json");

		BaSyxHttpTestUtils.assertSameJSONContent(expectedSubmodelJSON, submodelJSON);
	}

	@Test
	public void getSpecificSubmodelMetadata() throws ParseException, IOException {
		String expectedSubmodelJSON = getJSONValueAsString("SingleSubmodelMetadata.json");

		CloseableHttpResponse response = BaSyxHttpTestUtils.executeGetOnURL(createSubmodelMetadataURL(DummySubmodelFactory.createTechnicalDataSubmodel().getId()));
		assertEquals(HttpStatus.OK.value(), response.getCode());

		BaSyxHttpTestUtils.assertSameJSONContent(expectedSubmodelJSON, BaSyxHttpTestUtils.getResponseAsString(response));
	}

	@Test
	public void getSpecificSubmodelNonExisting() throws IOException {
		CloseableHttpResponse response = requestSubmodel("nonExisting");

		assertEquals(HttpStatus.NOT_FOUND.value(), response.getCode());
	}

	@Test
	public void updateExistingSubmodel() throws IOException, ParseException {
		String id = "7A7104BDAB57E184";
		String expectedSubmodelJSON = getJSONValueAsString("SingleSubmodelUpdate.json");

		CloseableHttpResponse creationResponse = putSubmodel(id, expectedSubmodelJSON);

		assertEquals(HttpStatus.NO_CONTENT.value(), creationResponse.getCode());

		String submodelJSON = requestSpecificSubmodelJSON(id);
		BaSyxHttpTestUtils.assertSameJSONContent(expectedSubmodelJSON, submodelJSON);
	}

	@Test
	public void updateNonExistingSubmodel() throws IOException {
		String id = "nonExisting";
		String expectedSubmodelJSON = getJSONValueAsString("SingleSubmodelUpdate.json");

		CloseableHttpResponse updateResponse = putSubmodel(id, expectedSubmodelJSON);

		assertEquals(HttpStatus.NOT_FOUND.value(), updateResponse.getCode());
	}

	@Test
	public void updateSubmodelWithMismatchId() throws IOException, ParseException {
		String id = "7A7104BDAB57E184";
		String submodelUpdateJson = getJSONValueAsString("SingleSubmodelUpdateMismatchId.json");

		CloseableHttpResponse creationResponse = putSubmodel(id, submodelUpdateJson);

		assertEquals(HttpStatus.BAD_REQUEST.value(), creationResponse.getCode());
	}

	@Test
	public void createSubmodelNewId() throws IOException, ParseException {
		String submodelJSON = getJSONValueAsString("SingleSubmodelNew.json");
		CloseableHttpResponse creationResponse = BaSyxSubmodelHttpTestUtils.createSubmodel(getURL(), submodelJSON);

		assertSubmodelCreationReponse(submodelJSON, creationResponse);

		String requestedSubmodel = requestSpecificSubmodelJSON("newSubmodel");
		BaSyxHttpTestUtils.assertSameJSONContent(submodelJSON, requestedSubmodel);
	}

	@Test
	public void createSubmodelCollidingId() throws IOException {
		String submodelJSON = getJSONValueAsString("SingleSubmodel.json");
		CloseableHttpResponse creationResponse = BaSyxSubmodelHttpTestUtils.createSubmodel(getURL(), submodelJSON);

		assertEquals(HttpStatus.CONFLICT.value(), creationResponse.getCode());
	}
	
	@Test
	public void createSubmodelWithEmptyId() throws IOException {
		String submodelJSON = getJSONValueAsString("SingleSubmodelWithEmptyId.json");
		CloseableHttpResponse creationResponse = BaSyxSubmodelHttpTestUtils.createSubmodel(getURL(), submodelJSON);

		assertEquals(HttpStatus.BAD_REQUEST.value(), creationResponse.getCode());
	}
	
	@Test
	public void createSubmodelWithNullId() throws IOException {
		String submodelJSON = getJSONValueAsString("SingleSubmodelWithNullId.json");
		CloseableHttpResponse creationResponse = BaSyxSubmodelHttpTestUtils.createSubmodel(getURL(), submodelJSON);

		assertEquals(HttpStatus.BAD_REQUEST.value(), creationResponse.getCode());
	}

	@Test
	public void deleteSubmodel() throws IOException {
		String existingSubmodelId = DummySubmodelFactory.createTechnicalDataSubmodel().getId();

		CloseableHttpResponse deletionResponse = deleteSubmodelById(existingSubmodelId);
		assertEquals(HttpStatus.NO_CONTENT.value(), deletionResponse.getCode());

		CloseableHttpResponse getResponse = requestSubmodel(existingSubmodelId);
		assertEquals(HttpStatus.NOT_FOUND.value(), getResponse.getCode());
	}

	@Test
	public void deleteNonExistingSubmodel() throws IOException {
		CloseableHttpResponse deletionResponse = deleteSubmodelById("nonExisting");

		assertEquals(HttpStatus.NOT_FOUND.value(), deletionResponse.getCode());
	}

	@Test
	public void getPaginatedSubmodel() throws ParseException, IOException {
		String submodelsJSON = BaSyxSubmodelHttpTestUtils
				.requestAllSubmodels(getURL() + "?limit=1&cursor=" + ENCODED_CURSOR);
		String expected = getJSONValueAsString("SubmodelsPaginated.json");

		BaSyxHttpTestUtils.assertSameJSONContent(expected, getJSONWithoutCursorInfo(submodelsJSON));
	}
	
	@Test
	public void updateNonFileSME() throws FileNotFoundException, IOException, ParseException {
		String element = getJSONValueAsString("PropertySubmodelElementUpdate.json");
		
		String idShortPathPropertyInSmeCol = SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_SUBMODEL_ELEMENT_COLLECTION_ID_SHORT + "." + SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_PROPERTY_ID_SHORT;
		
		CloseableHttpResponse updatedResponse = updateElement(createSpecificSubmodelElementURL(DummySubmodelFactory.SUBMODEL_TECHNICAL_DATA_ID,idShortPathPropertyInSmeCol), element);
		assertEquals(HttpStatus.NO_CONTENT.value(), updatedResponse.getCode());

		CloseableHttpResponse fetchedResponse = BaSyxHttpTestUtils.executeGetOnURL(createSpecificSubmodelElementURL(DummySubmodelFactory.SUBMODEL_TECHNICAL_DATA_ID,idShortPathPropertyInSmeCol));
		BaSyxHttpTestUtils.assertSameJSONContent(element, BaSyxHttpTestUtils.getResponseAsString(fetchedResponse));
	}

	@Test
	public void updateNonNestedSME() throws FileNotFoundException, IOException, ParseException {        
	    String submodelJSON = getJSONValueAsString("SingleSubmodelNew.json");
	    String element = getJSONValueAsString("PropertySubmodelElementUpdate.json");	  
	    String idShortPath = "MaxRotationSpeed";

	    CloseableHttpResponse creationResponse = BaSyxSubmodelHttpTestUtils.createSubmodel(getURL(), submodelJSON);
	    assertSubmodelCreationReponse(submodelJSON, creationResponse);

	    CloseableHttpResponse updatedResponse = updateElement(createSpecificSubmodelElementURL(DummySubmodelFactory.SUBMODEL_TECHNICAL_DATA_ID,idShortPath), element);
	    assertEquals(HttpStatus.NO_CONTENT.value(), updatedResponse.getCode());

	    CloseableHttpResponse fetchedResponse = BaSyxHttpTestUtils.executeGetOnURL(createSpecificSubmodelElementURL(DummySubmodelFactory.SUBMODEL_TECHNICAL_DATA_ID,idShortPath));
	    BaSyxHttpTestUtils.assertSameJSONContent(element, BaSyxHttpTestUtils.getResponseAsString(fetchedResponse));
	}
	
	@Test
	public void updateNonFileSMEWithFileSME() throws FileNotFoundException, IOException, ParseException {
		String element = getJSONValueAsString("FileSubmodelElementUpdate.json");
		
		String idShortPathPropertyInSmeCol = SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_SUBMODEL_ELEMENT_COLLECTION_ID_SHORT + "." + SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_PROPERTY_ID_SHORT;
		
		CloseableHttpResponse updatedResponse = updateElement(createSpecificSubmodelElementURL(DummySubmodelFactory.SUBMODEL_TECHNICAL_DATA_ID,idShortPathPropertyInSmeCol), element);
		assertEquals(HttpStatus.NO_CONTENT.value(), updatedResponse.getCode());
		
		CloseableHttpResponse fetchedResponse = BaSyxHttpTestUtils.executeGetOnURL(createSpecificSubmodelElementURL(DummySubmodelFactory.SUBMODEL_TECHNICAL_DATA_ID,idShortPathPropertyInSmeCol));
		BaSyxHttpTestUtils.assertSameJSONContent(element, BaSyxHttpTestUtils.getResponseAsString(fetchedResponse));
	}
	
	@Test
	public void updateFileSMEWithNonFileSME() throws FileNotFoundException, IOException, ParseException {
		String element = getJSONValueAsString("PropertySubmodelElementUpdateWithNewIdShort.json");
		
		String idShortPathPropertyInSmeCol = SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_SUBMODEL_ELEMENT_COLLECTION_ID_SHORT + "." + SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_FILE_ID_SHORT;
		
		uploadFileToSubmodelElement(DummySubmodelFactory.SUBMODEL_TECHNICAL_DATA_ID, idShortPathPropertyInSmeCol);
		
		CloseableHttpResponse fileResponse = BaSyxHttpTestUtils.executeGetOnURL(createSMEFileGetURL(DummySubmodelFactory.SUBMODEL_TECHNICAL_DATA_ID, idShortPathPropertyInSmeCol));
		assertEquals(HttpStatus.OK.value(), fileResponse.getCode());
		
		CloseableHttpResponse updatedResponse = updateElement(createSpecificSubmodelElementURL(DummySubmodelFactory.SUBMODEL_TECHNICAL_DATA_ID, idShortPathPropertyInSmeCol), element);
		assertEquals(HttpStatus.NO_CONTENT.value(), updatedResponse.getCode());
		
		CloseableHttpResponse fetchedResponse = BaSyxHttpTestUtils.executeGetOnURL(createSpecificSubmodelElementURL(DummySubmodelFactory.SUBMODEL_TECHNICAL_DATA_ID, idShortPathPropertyInSmeCol));
		BaSyxHttpTestUtils.assertSameJSONContent(element, BaSyxHttpTestUtils.getResponseAsString(fetchedResponse));
	}
	
	@Test
	public void updateFileSMEWithFileSME() throws FileNotFoundException, IOException, ParseException {
		String element = getJSONValueAsString("FileSubmodelElementUpdateWithNewIdShort.json");
		
		String idShortPathPropertyInSmeCol = SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_SUBMODEL_ELEMENT_COLLECTION_ID_SHORT + "." + SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_FILE_ID_SHORT;
		
		uploadFileToSubmodelElement(DummySubmodelFactory.SUBMODEL_TECHNICAL_DATA_ID, idShortPathPropertyInSmeCol);
		
		CloseableHttpResponse fileResponse = BaSyxHttpTestUtils.executeGetOnURL(createSMEFileGetURL(DummySubmodelFactory.SUBMODEL_TECHNICAL_DATA_ID, idShortPathPropertyInSmeCol));
		assertEquals(HttpStatus.OK.value(), fileResponse.getCode());
		
		CloseableHttpResponse updatedResponse = updateElement(createSpecificSubmodelElementURL(DummySubmodelFactory.SUBMODEL_TECHNICAL_DATA_ID,idShortPathPropertyInSmeCol), element);
		assertEquals(HttpStatus.NO_CONTENT.value(), updatedResponse.getCode());
		
		CloseableHttpResponse fetchedResponse = BaSyxHttpTestUtils.executeGetOnURL(createSpecificSubmodelElementURL(DummySubmodelFactory.SUBMODEL_TECHNICAL_DATA_ID,idShortPathPropertyInSmeCol));
		BaSyxHttpTestUtils.assertSameJSONContent(element, BaSyxHttpTestUtils.getResponseAsString(fetchedResponse));
	}
	
	@Test
	public void updateNonExistingSME() throws FileNotFoundException, IOException, ParseException {
		String element = getJSONValueAsString("PropertySubmodelElementUpdate.json");
		
		String idShortPathPropertyInSmeCol = SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_SUBMODEL_ELEMENT_COLLECTION_ID_SHORT + ".NonExistingSMEIdShort";
		
		CloseableHttpResponse updatedResponse = updateElement(createSpecificSubmodelElementURL(DummySubmodelFactory.SUBMODEL_TECHNICAL_DATA_ID,idShortPathPropertyInSmeCol), element);
		assertEquals(HttpStatus.NOT_FOUND.value(), updatedResponse.getCode());
	}

	@Test
	public void uploadFileToFileSubmodelElement() throws IOException {
		CloseableHttpResponse submodelElementFileUploadResponse = uploadFileToSubmodelElement(DummySubmodelFactory.SUBMODEL_FOR_FILE_TEST, DummySubmodelFactory.SUBMODEL_ELEMENT_FILE_ID_SHORT);

		assertEquals(HttpStatus.NO_CONTENT.value(), submodelElementFileUploadResponse.getCode());
	}

	@Test
	public void uploadFileToNonFileSubmodelElement() throws FileNotFoundException, UnsupportedEncodingException, ClientProtocolException, IOException {
		CloseableHttpResponse submodelElementFileUploadResponse = uploadFileToSubmodelElement(DummySubmodelFactory.SUBMODEL_FOR_FILE_TEST, DummySubmodelFactory.SUBMODEL_ELEMENT_NON_FILE_ID_SHORT);
		
		assertEquals(HttpStatus.PRECONDITION_FAILED.value(), submodelElementFileUploadResponse.getCode());
	}

	@Test
	public void uploadFileToNotExistElement() throws FileNotFoundException, UnsupportedEncodingException, ClientProtocolException, IOException {
		CloseableHttpResponse submodelElementFileUploadResponse = uploadFileToSubmodelElement(DummySubmodelFactory.SUBMODEL_FOR_FILE_TEST, "ElementNotExist");
		
		assertEquals(HttpStatus.NOT_FOUND.value(), submodelElementFileUploadResponse.getCode());
	}

	@Test
	public void deleteFile() throws FileNotFoundException, IOException {
		uploadFileToSubmodelElement(DummySubmodelFactory.SUBMODEL_FOR_FILE_TEST, DummySubmodelFactory.SUBMODEL_ELEMENT_FILE_ID_SHORT);

		CloseableHttpResponse response = BaSyxHttpTestUtils.executeDeleteOnURL(createSMEFileDeleteURL(DummySubmodelFactory.SUBMODEL_FOR_FILE_TEST, DummySubmodelFactory.SUBMODEL_ELEMENT_FILE_ID_SHORT));
		assertEquals(HttpStatus.OK.value(), response.getCode());

		response = BaSyxHttpTestUtils.executeGetOnURL(createSMEFileGetURL(DummySubmodelFactory.SUBMODEL_FOR_FILE_TEST, DummySubmodelFactory.SUBMODEL_ELEMENT_FILE_ID_SHORT));
		assertEquals(HttpStatus.NOT_FOUND.value(), response.getCode());
	}

	@Test
	public void deleteFileToNonFileSubmodelElement() throws FileNotFoundException, UnsupportedEncodingException, ClientProtocolException, IOException {
		uploadFileToSubmodelElement(DummySubmodelFactory.SUBMODEL_FOR_FILE_TEST, DummySubmodelFactory.SUBMODEL_ELEMENT_FILE_ID_SHORT);
		
		CloseableHttpResponse response = BaSyxHttpTestUtils.executeDeleteOnURL(createSMEFileGetURL(DummySubmodelFactory.SUBMODEL_FOR_FILE_TEST, DummySubmodelFactory.SUBMODEL_ELEMENT_NON_FILE_ID_SHORT));
		
		assertEquals(HttpStatus.PRECONDITION_FAILED.value(), response.getCode());
	}

	@Test
	public void deleteFileFromNotExistElement() throws FileNotFoundException, UnsupportedEncodingException, ClientProtocolException, IOException {
		CloseableHttpResponse response = BaSyxHttpTestUtils.executeDeleteOnURL(createSMEFileGetURL(DummySubmodelFactory.SUBMODEL_FOR_FILE_TEST, "ElementNotExist"));
		
		assertEquals(HttpStatus.NOT_FOUND.value(), response.getCode());
	}

	@Test
	public void getFile() throws FileNotFoundException, IOException, ParseException {
		String fileName = DummySubmodelFactory.FILE_NAME;
		String expectedFileName = Base64UrlEncodedIdentifier.encodeIdentifier(DummySubmodelFactory.SUBMODEL_FOR_FILE_TEST) + "-" + DummySubmodelFactory.SUBMODEL_ELEMENT_FILE_ID_SHORT + "-" + fileName;
		
		byte[] expectedFile = readBytesFromClasspath(fileName);
		
		uploadFileToSubmodelElement(DummySubmodelFactory.SUBMODEL_FOR_FILE_TEST, DummySubmodelFactory.SUBMODEL_ELEMENT_FILE_ID_SHORT);
		
		CloseableHttpResponse response = BaSyxHttpTestUtils.executeGetOnURL(createSMEFileGetURL(DummySubmodelFactory.SUBMODEL_FOR_FILE_TEST, DummySubmodelFactory.SUBMODEL_ELEMENT_FILE_ID_SHORT));
		assertEquals(HttpStatus.OK.value(), response.getCode());

	    Header contentDispositionHeader = response.getFirstHeader("Content-Disposition");
	    assertNotNull(contentDispositionHeader);

	    String contentDisposition = contentDispositionHeader.getValue();
	    String actualFileName = extractFileNameFromContentDisposition(contentDisposition);

	    assertEquals(expectedFileName, actualFileName);

        byte[] actualFile = EntityUtils.toByteArray(response.getEntity());
        
        response.close();
        
        assertArrayEquals(expectedFile, actualFile);
	}

	@Test
	public void getFileFromNonFileSubmodelElement() throws FileNotFoundException, UnsupportedEncodingException, ClientProtocolException, IOException {
		CloseableHttpResponse response = BaSyxHttpTestUtils.executeGetOnURL(createSMEFileGetURL(DummySubmodelFactory.SUBMODEL_FOR_FILE_TEST, DummySubmodelFactory.SUBMODEL_ELEMENT_NON_FILE_ID_SHORT));
		
		assertEquals(HttpStatus.PRECONDITION_FAILED.value(), response.getCode());
	}

	@Test
	public void getFileFromNotExistElement() throws FileNotFoundException, UnsupportedEncodingException, ClientProtocolException, IOException {
		CloseableHttpResponse response = BaSyxHttpTestUtils.executeGetOnURL(createSMEFileGetURL(DummySubmodelFactory.SUBMODEL_FOR_FILE_TEST, "ElementNotExist"));
		
		assertEquals(HttpStatus.NOT_FOUND.value(), response.getCode());
	}
	
	@Test
	public void getSubmodelByIdValueOnly() throws IOException, ParseException {
	    String submodelIdentifier = Base64UrlEncodedIdentifier.encodeIdentifier(DummySubmodelFactory.createTechnicalDataSubmodel().getId());
	    String url = getURL() + "/" + submodelIdentifier + "/$value";
	    
	    String expectedSubmodelJSON = getJSONValueAsString("SingleSubmodelValueOnly.json");
	    
	    CloseableHttpResponse response = BaSyxHttpTestUtils.executeGetOnURL(url);
	    assertEquals(HttpStatus.OK.value(), response.getCode());
	    
	    String actualSubmodelJSON = BaSyxHttpTestUtils.getResponseAsString(response);
	    
	    BaSyxHttpTestUtils.assertSameJSONContent(expectedSubmodelJSON, actualSubmodelJSON);
	}

	private String extractFileNameFromContentDisposition(String contentDisposition) {
	    for (String part : contentDisposition.split(";")) {
	        part = part.trim();
	        if (part.startsWith("filename")) {
	            return part.split("=")[1].replace("\"", "").trim();
	        }
	    }
	    return null;
	}
	
	private String getJSONWithoutCursorInfo(String response) throws JsonMappingException, JsonProcessingException {
		return BaSyxHttpTestUtils.removeCursorFromJSON(response);
	}

	private String createSMEFileDeleteURL(String submodelId, String submodelElementIdShort) {
		return BaSyxSubmodelHttpTestUtils.getSpecificSubmodelAccessPath(getURL(), submodelId) + "/submodel-elements/" + submodelElementIdShort + "/attachment";
	}

	private String createSMEFileGetURL(String submodelId, String submodelElementIdShort) {
		return BaSyxSubmodelHttpTestUtils.getSpecificSubmodelAccessPath(getURL(), submodelId) + "/submodel-elements/" + submodelElementIdShort + "/attachment";
	}

	private CloseableHttpResponse uploadFileToSubmodelElement(String submodelId, String submodelElementIdShort) throws IOException {
		CloseableHttpClient client = HttpClients.createDefault();

		String fileName = DummySubmodelFactory.FILE_NAME;

		java.io.File file = ResourceUtils.getFile("classpath:" + fileName);

		HttpPut putRequest = createPutRequestWithFile(submodelId, submodelElementIdShort, fileName, file);

		return executePutRequest(client, putRequest);
	}
	
	private CloseableHttpResponse updateElement(String url, String element) throws IOException {
		return BaSyxHttpTestUtils.executePutOnURL(url, element);
	}

	private CloseableHttpResponse executePutRequest(CloseableHttpClient client, HttpPut putRequest) throws IOException {
		CloseableHttpResponse response = client.execute(putRequest);

		HttpEntity responseEntity = response.getEntity();

		EntityUtils.consume(responseEntity);
		return response;
	}

	private HttpPut createPutRequestWithFile(String submodelId, String submodelElementIdShort, String fileName, java.io.File file) {
		HttpPut putRequest = new HttpPut(createSMEFileUploadURL(submodelId, submodelElementIdShort, fileName));

		MultipartEntityBuilder builder = MultipartEntityBuilder.create();

		builder.addPart("file", new FileBody(file));
		builder.setContentType(ContentType.MULTIPART_FORM_DATA);

		HttpEntity multipart = builder.build();
		putRequest.setEntity(multipart);
		return putRequest;
	}

	private String createSMEFileUploadURL(String submodelId, String submodelElementIdShort, String fileName) {
		return BaSyxSubmodelHttpTestUtils.getSpecificSubmodelAccessPath(getURL(), submodelId) + "/submodel-elements/" + submodelElementIdShort + "/attachment?fileName=" + fileName;
	}

	private void assertSubmodelCreationReponse(String submodelJSON, CloseableHttpResponse creationResponse) throws IOException, ParseException, JsonProcessingException, JsonMappingException {
		assertEquals(HttpStatus.CREATED.value(), creationResponse.getCode());
		
		String response = BaSyxHttpTestUtils.getResponseAsString(creationResponse);
		
		BaSyxHttpTestUtils.assertSameJSONContent(submodelJSON, response);
	}

	private String createSubmodelMetadataURL(String id) {
		return BaSyxSubmodelHttpTestUtils.getSpecificSubmodelAccessPath(getURL(), id) + "/$metadata";
	}
	
	private String createSpecificSubmodelElementURL(String submodelId, String smeIdShortPath) {
		return BaSyxSubmodelHttpTestUtils.getSpecificSubmodelAccessPath(getURL(), submodelId) + "/submodel-elements/" + smeIdShortPath;
	}

	private CloseableHttpResponse deleteSubmodelById(String submodelId) throws IOException {
		return BaSyxHttpTestUtils.executeDeleteOnURL(getURL() + "/" + Base64UrlEncodedIdentifier.encodeIdentifier(submodelId));
	}

	private CloseableHttpResponse putSubmodel(String submodelId, String submodelJSON) throws IOException {
		return BaSyxHttpTestUtils.executePutOnURL(BaSyxSubmodelHttpTestUtils.getSpecificSubmodelAccessPath(getURL(), submodelId), submodelJSON);
	}

	private String requestSpecificSubmodelJSON(String submodelId) throws IOException, ParseException {
		CloseableHttpResponse response = requestSubmodel(submodelId);

		return BaSyxHttpTestUtils.getResponseAsString(response);
	}

	private CloseableHttpResponse requestSubmodel(String submodelId) throws IOException {
		return BaSyxHttpTestUtils.executeGetOnURL(BaSyxSubmodelHttpTestUtils.getSpecificSubmodelAccessPath(getURL(), submodelId));
	}
	
	private String getJSONValueAsString(String fileName) throws FileNotFoundException, IOException {
		return BaSyxHttpTestUtils.readJSONStringFromClasspath(fileName);
	}
	
	private byte[] readBytesFromClasspath(String fileName) throws FileNotFoundException, IOException {
		ClassPathResource classPathResource = new ClassPathResource(fileName);
		InputStream in = classPathResource.getInputStream();
		
		return in.readAllBytes();
	}
	
	protected List<Submodel> createSubmodels() {
		return Lists.newArrayList(DummySubmodelFactory.createTechnicalDataSubmodel(), DummySubmodelFactory.createOperationalDataSubmodel(), DummySubmodelFactory.createSimpleDataSubmodel(), DummySubmodelFactory.createSubmodelWithFileElement());
	}

}

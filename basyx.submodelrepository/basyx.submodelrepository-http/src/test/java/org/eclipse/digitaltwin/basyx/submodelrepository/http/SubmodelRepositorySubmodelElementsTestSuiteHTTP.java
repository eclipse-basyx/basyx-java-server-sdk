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

import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.apache.hc.client5.http.ClientProtocolException;
import org.apache.hc.client5.http.classic.methods.HttpPut;
import org.apache.hc.client5.http.entity.mime.FileBody;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.basyx.http.serialization.BaSyxHttpTestUtils;
import org.eclipse.digitaltwin.basyx.submodelservice.DummySubmodelFactory;
import org.eclipse.digitaltwin.basyx.submodelservice.SubmodelServiceHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.util.ResourceUtils;

/**
 * Base testsuite for all Submodel Repository HTTP tests related to a specific
 * Submodel
 * 
 * @author schnicke
 *
 */
public abstract class SubmodelRepositorySubmodelElementsTestSuiteHTTP {
	@Before
	public abstract void populateRepository();

	@After
	public abstract void resetRepository();

	protected abstract String getURL();

	@Test
	public void getSubmodelElements() throws FileNotFoundException, IOException, ParseException {
		String id = DummySubmodelFactory.SUBMODEL_TECHNICAL_DATA_ID;
		String requestedSubmodelElements = requestSubmodelElementsJSON(id);

		String submodelElementJSON = getSubmodelElementsJSON();
		BaSyxHttpTestUtils.assertSameJSONContent(submodelElementJSON, requestedSubmodelElements);
	}

	@Test
	public void getSubmodelElementsOfNonExistingSubmodel() throws ParseException, IOException {
		CloseableHttpResponse response = requestSubmodelElements("nonExisting");
		assertEquals(HttpStatus.NOT_FOUND.value(), response.getCode());
	}

	@Test
	public void getSubmodelElement() throws FileNotFoundException, IOException, ParseException {
		String expectedElement = getSubmodelElementJSON();
		CloseableHttpResponse response = requestSubmodelElement(DummySubmodelFactory.SUBMODEL_TECHNICAL_DATA_ID, SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_PROPERTY_ID_SHORT);

		assertEquals(HttpStatus.OK.value(), response.getCode());
		BaSyxHttpTestUtils.assertSameJSONContent(expectedElement, BaSyxHttpTestUtils.getResponseAsString(response));
	}

	@Test
	public void getSubmodelElementOfNonExistingSubmodel() throws FileNotFoundException, IOException, ParseException {
		CloseableHttpResponse response = requestSubmodelElement("nonExisting", "doesNotMatter");

		assertEquals(HttpStatus.NOT_FOUND.value(), response.getCode());
	}

	@Test
	public void getNonExistingSubmodelElement() throws FileNotFoundException, IOException, ParseException {
		CloseableHttpResponse response = requestSubmodelElement(DummySubmodelFactory.SUBMODEL_TECHNICAL_DATA_ID, "nonExisting");

		assertEquals(HttpStatus.NOT_FOUND.value(), response.getCode());
	}

	@Test
	public void getPropertyValue() throws IOException, ParseException {
		String expectedValue = wrapStringValue("5000");

		CloseableHttpResponse response = requestSubmodelElementValue(DummySubmodelFactory.SUBMODEL_TECHNICAL_DATA_ID, SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_PROPERTY_ID_SHORT);

		assertEquals(HttpStatus.OK.value(), response.getCode());

		BaSyxHttpTestUtils.assertSameJSONContent(expectedValue, BaSyxHttpTestUtils.getResponseAsString(response));
	}

	@Test
	public void getNonExistingSubmodelElementValue() throws IOException {
		CloseableHttpResponse response = requestSubmodelElementValue(DummySubmodelFactory.SUBMODEL_TECHNICAL_DATA_ID, "nonExisting");

		assertEquals(HttpStatus.NOT_FOUND.value(), response.getCode());
	}

	@Test
	public void getSubmodelElementValueOfNonExistingSubmodel() throws IOException {
		CloseableHttpResponse response = requestSubmodelElementValue("nonExisting", "doesNotMatter");

		assertEquals(HttpStatus.NOT_FOUND.value(), response.getCode());
	}

	@Test
	public void setPropertyValue() throws IOException, ParseException {
		String expectedValue = wrapStringValue("2567");

		CloseableHttpResponse writeResponse = writeSubmodelElementValue(DummySubmodelFactory.SUBMODEL_TECHNICAL_DATA_ID, SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_PROPERTY_ID_SHORT, expectedValue);
		assertEquals(HttpStatus.OK.value(), writeResponse.getCode());

		CloseableHttpResponse response = requestSubmodelElementValue(DummySubmodelFactory.SUBMODEL_TECHNICAL_DATA_ID, SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_PROPERTY_ID_SHORT);

		BaSyxHttpTestUtils.assertSameJSONContent(expectedValue, BaSyxHttpTestUtils.getResponseAsString(response));
	}

	@Test
	public void getMultiLanguagePropertyValue() throws IOException, ParseException {
		CloseableHttpResponse response = requestSubmodelElementValue(DummySubmodelFactory.SUBMODEL_TECHNICAL_DATA_ID, SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_MULTI_LANG_PROP_ID_SHORT);

		assertEquals(HttpStatus.OK.value(), response.getCode());

		String expectedValue = getJSONValueAsString("value/expectedMultiLanguagePropertyValue.json");

		BaSyxHttpTestUtils.assertSameJSONContent(expectedValue, BaSyxHttpTestUtils.getResponseAsString(response));
	}

	@Test
	public void setMultiLanguagePropertyValue() throws IOException, ParseException {
		String expectedValue = getJSONValueAsString("value/setMultiLanguagePropertyValue.json");

		CloseableHttpResponse writeResponse = writeSubmodelElementValue(DummySubmodelFactory.SUBMODEL_TECHNICAL_DATA_ID, SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_MULTI_LANG_PROP_ID_SHORT, expectedValue);
		assertEquals(HttpStatus.OK.value(), writeResponse.getCode());

		CloseableHttpResponse response = requestSubmodelElementValue(DummySubmodelFactory.SUBMODEL_TECHNICAL_DATA_ID, SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_MULTI_LANG_PROP_ID_SHORT);

		BaSyxHttpTestUtils.assertSameJSONContent(expectedValue, BaSyxHttpTestUtils.getResponseAsString(response));
	}

	@Test
	public void getRangeValue() throws IOException, ParseException {
		CloseableHttpResponse response = requestSubmodelElementValue(DummySubmodelFactory.SUBMODEL_TECHNICAL_DATA_ID, SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_RANGE_ID_SHORT);

		assertEquals(HttpStatus.OK.value(), response.getCode());

		String expectedValue = getJSONValueAsString("value/expectedRangeValue.json");

		BaSyxHttpTestUtils.assertSameJSONContent(expectedValue, BaSyxHttpTestUtils.getResponseAsString(response));
	}

	@Test
	public void setRangeValue() throws IOException, ParseException {
		String expectedValue = getJSONValueAsString("value/setRangeValue.json");

		CloseableHttpResponse writeResponse = writeSubmodelElementValue(DummySubmodelFactory.SUBMODEL_TECHNICAL_DATA_ID, SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_RANGE_ID_SHORT, expectedValue);
		assertEquals(HttpStatus.OK.value(), writeResponse.getCode());

		CloseableHttpResponse response = requestSubmodelElementValue(DummySubmodelFactory.SUBMODEL_TECHNICAL_DATA_ID, SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_RANGE_ID_SHORT);

		BaSyxHttpTestUtils.assertSameJSONContent(expectedValue, BaSyxHttpTestUtils.getResponseAsString(response));
	}

	@Test
	public void getFileValue() throws IOException, ParseException {
		CloseableHttpResponse response = requestSubmodelElementValue(DummySubmodelFactory.SUBMODEL_TECHNICAL_DATA_ID, SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_FILE_ID_SHORT);

		assertEquals(HttpStatus.OK.value(), response.getCode());

		String expectedValue = getJSONValueAsString("value/expectedFileValue.json");

		BaSyxHttpTestUtils.assertSameJSONContent(expectedValue, BaSyxHttpTestUtils.getResponseAsString(response));
	}

	@Test
	public void setFileValue() throws IOException, ParseException {
		String expectedValue = getJSONValueAsString("value/setFileValue.json");

		CloseableHttpResponse writeResponse = writeSubmodelElementValue(DummySubmodelFactory.SUBMODEL_TECHNICAL_DATA_ID, SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_FILE_ID_SHORT, expectedValue);
		assertEquals(HttpStatus.OK.value(), writeResponse.getCode());

		CloseableHttpResponse response = requestSubmodelElementValue(DummySubmodelFactory.SUBMODEL_TECHNICAL_DATA_ID, SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_FILE_ID_SHORT);

		BaSyxHttpTestUtils.assertSameJSONContent(expectedValue, BaSyxHttpTestUtils.getResponseAsString(response));
	}

	@Test
	public void uploadFileToFileSubmodelElement() throws IOException {
		CloseableHttpResponse submodelElementFileUploadResponse = uploadFile();

		assertEquals(HttpStatus.OK.value(), submodelElementFileUploadResponse.getCode());
	}

	@Test
	public void uploadFileToNonFileSubmodelElement() throws FileNotFoundException, UnsupportedEncodingException, ClientProtocolException, IOException {
		String submodelJSON = BaSyxHttpTestUtils.readJSONStringFromClasspath("SingleSubmodel4FileTest.json");
		BaSyxSubmodelHttpTestUtils.createSubmodel(getURL(), submodelJSON);
		CloseableHttpResponse submodelElementFileUploadResponse = uploadFileToSubmodelElement(DummySubmodelFactory.SUBMODEL_FOR_FILE_TEST, DummySubmodelFactory.SUBMODEL_ELEMENT_NON_FILE_ID_SHORT);
		assertEquals(HttpStatus.BAD_REQUEST.value(), submodelElementFileUploadResponse.getCode());
	}

	private CloseableHttpResponse uploadFileToSubmodelElement(String submodelId, String submodelElementIdShort) throws IOException {
		CloseableHttpClient client = HttpClients.createDefault();

		String fileName = "BaSyx-Logo.png";

		java.io.File file = ResourceUtils.getFile("src/test/resources/" + fileName);

		HttpPut putRequest = createPutRequestWithFile(submodelId, submodelElementIdShort, fileName, file);

		return executePutRequest(client, putRequest);
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

	@Test
	public void deleteFile() throws FileNotFoundException, IOException {
		uploadFile();

		CloseableHttpResponse response = BaSyxHttpTestUtils.executeDeleteOnURL(createSMEFileDeleteURL(DummySubmodelFactory.SUBMODEL_FOR_FILE_TEST, DummySubmodelFactory.SUBMODEL_ELEMENT_FILE_ID_SHORT));
		assertEquals(HttpStatus.OK.value(), response.getCode());

		response = BaSyxHttpTestUtils.executeGetOnURL(createSMEFileGetURL(DummySubmodelFactory.SUBMODEL_FOR_FILE_TEST, DummySubmodelFactory.SUBMODEL_ELEMENT_FILE_ID_SHORT));
		assertEquals(HttpStatus.NOT_FOUND.value(), response.getCode());
	}

	@Test
	public void deleteFileToNonFileSubmodelElement() throws FileNotFoundException, UnsupportedEncodingException, ClientProtocolException, IOException {
		uploadFile();
		CloseableHttpResponse response = BaSyxHttpTestUtils.executeGetOnURL(createSMEFileGetURL(DummySubmodelFactory.SUBMODEL_FOR_FILE_TEST, DummySubmodelFactory.SUBMODEL_ELEMENT_NON_FILE_ID_SHORT));
		assertEquals(HttpStatus.BAD_REQUEST.value(), response.getCode());
	}

	private CloseableHttpResponse uploadFile() throws FileNotFoundException, IOException {
		String submodelJSON = BaSyxHttpTestUtils.readJSONStringFromClasspath("SingleSubmodel4FileTest.json");
		BaSyxSubmodelHttpTestUtils.createSubmodel(getURL(), submodelJSON);
		return uploadFileToSubmodelElement(DummySubmodelFactory.SUBMODEL_FOR_FILE_TEST, DummySubmodelFactory.SUBMODEL_ELEMENT_FILE_ID_SHORT);
	}

	@Test
	public void getFile() throws FileNotFoundException, IOException, ParseException {
		uploadFile();
		CloseableHttpResponse response = BaSyxHttpTestUtils.executeGetOnURL(createSMEFileGetURL(DummySubmodelFactory.SUBMODEL_FOR_FILE_TEST, DummySubmodelFactory.SUBMODEL_ELEMENT_FILE_ID_SHORT));
		assertEquals(HttpStatus.OK.value(), response.getCode());
		String received = BaSyxHttpTestUtils.getResponseAsString(response);

		String fileName = "BaSyx-Logo.png";
		assertEquals(readFile("src/test/resources/" + fileName, Charset.defaultCharset()), new String(received.getBytes(), Charset.defaultCharset()));

	}

	static String readFile(String path, Charset encoding) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
	}

	@Test
	public void getFileToNonFileSubmodelElement() throws FileNotFoundException, UnsupportedEncodingException, ClientProtocolException, IOException {
		uploadFile();
		CloseableHttpResponse response = BaSyxHttpTestUtils.executeGetOnURL(createSMEFileGetURL(DummySubmodelFactory.SUBMODEL_FOR_FILE_TEST, DummySubmodelFactory.SUBMODEL_ELEMENT_NON_FILE_ID_SHORT));
		assertEquals(HttpStatus.BAD_REQUEST.value(), response.getCode());
	}

	private String createSMEFileDeleteURL(String submodelId, String submodelElementIdShort) {
		return BaSyxSubmodelHttpTestUtils.getSpecificSubmodelAccessPath(getURL(), submodelId) + "/submodel-elements/" + submodelElementIdShort + "/attachment";
	}

	private String createSMEFileGetURL(String submodelId, String submodelElementIdShort) {
		return BaSyxSubmodelHttpTestUtils.getSpecificSubmodelAccessPath(getURL(), submodelId) + "/submodel-elements/" + submodelElementIdShort + "/attachment";
	}

	@Test
	public void getBlobValue() throws IOException, ParseException {
		CloseableHttpResponse response = requestSubmodelElementValue(DummySubmodelFactory.SUBMODEL_TECHNICAL_DATA_ID, SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_BLOB_ID_SHORT);

		assertEquals(HttpStatus.OK.value(), response.getCode());

		String expectedValue = getJSONValueAsString("value/expectedBlobValue.json");

		BaSyxHttpTestUtils.assertSameJSONContent(expectedValue, BaSyxHttpTestUtils.getResponseAsString(response));
	}

	@Test
	public void setBlobValue() throws IOException, ParseException {
		String expectedValue = getJSONValueAsString("value/setBlobValue.json");

		CloseableHttpResponse writeResponse = writeSubmodelElementValue(DummySubmodelFactory.SUBMODEL_TECHNICAL_DATA_ID, SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_BLOB_ID_SHORT, expectedValue);
		assertEquals(HttpStatus.OK.value(), writeResponse.getCode());

		CloseableHttpResponse response = requestSubmodelElementValue(DummySubmodelFactory.SUBMODEL_TECHNICAL_DATA_ID, SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_BLOB_ID_SHORT);

		BaSyxHttpTestUtils.assertSameJSONContent(expectedValue, BaSyxHttpTestUtils.getResponseAsString(response));
	}

	@Test
	public void getEntityValue() throws IOException, ParseException {
		CloseableHttpResponse response = requestSubmodelElementValue(DummySubmodelFactory.SUBMODEL_TECHNICAL_DATA_ID, SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_ENTITY_ID_SHORT);

		assertEquals(HttpStatus.OK.value(), response.getCode());

		String expectedValue = getJSONValueAsString("value/expectedEntityValue.json");

		BaSyxHttpTestUtils.assertSameJSONContent(expectedValue, BaSyxHttpTestUtils.getResponseAsString(response));
	}

	@Test
	public void setEntityValue() throws IOException, ParseException {
		String expectedValue = getJSONValueAsString("value/setEntityValue.json");

		CloseableHttpResponse writeResponse = writeSubmodelElementValue(DummySubmodelFactory.SUBMODEL_TECHNICAL_DATA_ID, SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_ENTITY_ID_SHORT, expectedValue);
		assertEquals(HttpStatus.OK.value(), writeResponse.getCode());

		CloseableHttpResponse response = requestSubmodelElementValue(DummySubmodelFactory.SUBMODEL_TECHNICAL_DATA_ID, SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_ENTITY_ID_SHORT);

		BaSyxHttpTestUtils.assertSameJSONContent(expectedValue, BaSyxHttpTestUtils.getResponseAsString(response));
	}

	@Test
	public void setEntityValueMRP() throws IOException, ParseException {
		String minimumRequestPayloadValue = getJSONValueAsString("value/setEntityValueMRP.json");
		String expectedValue = getJSONValueAsString("value/expectedUpdatedMRPEntityValue.json");

		CloseableHttpResponse writeResponse = writeSubmodelElementValue(DummySubmodelFactory.SUBMODEL_TECHNICAL_DATA_ID, SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_ENTITY_ID_SHORT, minimumRequestPayloadValue);
		assertEquals(HttpStatus.OK.value(), writeResponse.getCode());

		CloseableHttpResponse response = requestSubmodelElementValue(DummySubmodelFactory.SUBMODEL_TECHNICAL_DATA_ID, SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_ENTITY_ID_SHORT);

		BaSyxHttpTestUtils.assertSameJSONContent(expectedValue, BaSyxHttpTestUtils.getResponseAsString(response));
	}

	@Test
	public void getReferenceElementValue() throws IOException, ParseException {
		CloseableHttpResponse response = requestSubmodelElementValue(DummySubmodelFactory.SUBMODEL_TECHNICAL_DATA_ID, SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_REFERENCE_ELEMENT_ID_SHORT);

		assertEquals(HttpStatus.OK.value(), response.getCode());

		String expectedValue = getJSONValueAsString("value/expectedReferenceElementValue.json");

		BaSyxHttpTestUtils.assertSameJSONContent(expectedValue, BaSyxHttpTestUtils.getResponseAsString(response));
	}

	@Test
	public void setReferenceElementValue() throws IOException, ParseException {
		String expectedValue = getJSONValueAsString("value/setReferenceElementValue.json");

		CloseableHttpResponse writeResponse = writeSubmodelElementValue(DummySubmodelFactory.SUBMODEL_TECHNICAL_DATA_ID, SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_REFERENCE_ELEMENT_ID_SHORT, expectedValue);
		assertEquals(HttpStatus.OK.value(), writeResponse.getCode());

		CloseableHttpResponse response = requestSubmodelElementValue(DummySubmodelFactory.SUBMODEL_TECHNICAL_DATA_ID, SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_REFERENCE_ELEMENT_ID_SHORT);

		BaSyxHttpTestUtils.assertSameJSONContent(expectedValue, BaSyxHttpTestUtils.getResponseAsString(response));
	}

	@Test
	public void getRelationshipElementValue() throws IOException, ParseException {
		CloseableHttpResponse response = requestSubmodelElementValue(DummySubmodelFactory.SUBMODEL_TECHNICAL_DATA_ID, SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_RELATIONSHIP_ELEMENT_ID_SHORT);

		assertEquals(HttpStatus.OK.value(), response.getCode());

		String expectedValue = getJSONValueAsString("value/expectedRelationshipElementValue.json");

		BaSyxHttpTestUtils.assertSameJSONContent(expectedValue, BaSyxHttpTestUtils.getResponseAsString(response));
	}

	@Test
	public void setRelationshipElementValue() throws IOException, ParseException {
		String expectedValue = getJSONValueAsString("value/setRelationshipElementValue.json");

		CloseableHttpResponse writeResponse = writeSubmodelElementValue(DummySubmodelFactory.SUBMODEL_TECHNICAL_DATA_ID, SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_RELATIONSHIP_ELEMENT_ID_SHORT, expectedValue);
		assertEquals(HttpStatus.OK.value(), writeResponse.getCode());

		CloseableHttpResponse response = requestSubmodelElementValue(DummySubmodelFactory.SUBMODEL_TECHNICAL_DATA_ID, SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_RELATIONSHIP_ELEMENT_ID_SHORT);

		BaSyxHttpTestUtils.assertSameJSONContent(expectedValue, BaSyxHttpTestUtils.getResponseAsString(response));
	}

	@Test
	public void getAnnotatedRelationshipElementValue() throws IOException, ParseException {
		CloseableHttpResponse response = requestSubmodelElementValue(DummySubmodelFactory.SUBMODEL_TECHNICAL_DATA_ID, SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_ANNOTATED_RELATIONSHIP_ELEMENT_ID_SHORT);

		assertEquals(HttpStatus.OK.value(), response.getCode());

		String expectedValue = getJSONValueAsString("value/expectedAnnotatedRelationshipElementValue.json");

		BaSyxHttpTestUtils.assertSameJSONContent(expectedValue, BaSyxHttpTestUtils.getResponseAsString(response));
	}

	@Test
	public void setAnnotatedRelationshipElementValue() throws IOException, ParseException {
		String expectedValue = getJSONValueAsString("value/setAnnotatedRelationshipElementValue.json");

		CloseableHttpResponse writeResponse = writeSubmodelElementValue(DummySubmodelFactory.SUBMODEL_TECHNICAL_DATA_ID, SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_ANNOTATED_RELATIONSHIP_ELEMENT_ID_SHORT, expectedValue);
		assertEquals(HttpStatus.OK.value(), writeResponse.getCode());

		CloseableHttpResponse response = requestSubmodelElementValue(DummySubmodelFactory.SUBMODEL_TECHNICAL_DATA_ID, SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_ANNOTATED_RELATIONSHIP_ELEMENT_ID_SHORT);

		BaSyxHttpTestUtils.assertSameJSONContent(expectedValue, BaSyxHttpTestUtils.getResponseAsString(response));
	}

	@Test
	public void getSubmodelElementCollectionValue() throws IOException, ParseException {
		CloseableHttpResponse response = requestSubmodelElementValue(DummySubmodelFactory.SUBMODEL_TECHNICAL_DATA_ID, SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_SUBMODEL_ELEMENT_COLLECTION_ID_SHORT);

		assertEquals(HttpStatus.OK.value(), response.getCode());

		String expectedValue = getJSONValueAsString("value/expectedSubmodelElementCollectionValue.json");

		BaSyxHttpTestUtils.assertSameJSONContent(expectedValue, BaSyxHttpTestUtils.getResponseAsString(response));
	}

	@Test
	public void setSubmodelElementCollectionValue() throws IOException, ParseException {
		String expectedValue = getJSONValueAsString("value/setSubmodelElementCollectionValue.json");

		CloseableHttpResponse writeResponse = writeSubmodelElementValue(DummySubmodelFactory.SUBMODEL_TECHNICAL_DATA_ID, SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_SUBMODEL_ELEMENT_COLLECTION_ID_SHORT, expectedValue);
		assertEquals(HttpStatus.OK.value(), writeResponse.getCode());

		CloseableHttpResponse response = requestSubmodelElementValue(DummySubmodelFactory.SUBMODEL_TECHNICAL_DATA_ID, SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_SUBMODEL_ELEMENT_COLLECTION_ID_SHORT);

		BaSyxHttpTestUtils.assertSameJSONContent(expectedValue, BaSyxHttpTestUtils.getResponseAsString(response));
	}

	@Test
	public void getSubmodelElementListValue() throws IOException, ParseException {
		CloseableHttpResponse response = requestSubmodelElementValue(DummySubmodelFactory.SUBMODEL_TECHNICAL_DATA_ID, SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_SUBMODEL_ELEMENT_LIST_ID_SHORT);

		assertEquals(HttpStatus.OK.value(), response.getCode());

		String expectedValue = getJSONValueAsString("value/expectedSubmodelElementListValue.json");

		BaSyxHttpTestUtils.assertSameJSONContent(expectedValue, BaSyxHttpTestUtils.getResponseAsString(response));
	}

	@Test
	public void setSubmodelElementListValue() throws IOException, ParseException {
		String expectedValue = getJSONValueAsString("value/setSubmodelElementListValue.json");

		CloseableHttpResponse writeResponse = writeSubmodelElementValue(DummySubmodelFactory.SUBMODEL_TECHNICAL_DATA_ID, SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_SUBMODEL_ELEMENT_LIST_ID_SHORT, expectedValue);
		assertEquals(HttpStatus.OK.value(), writeResponse.getCode());

		CloseableHttpResponse response = requestSubmodelElementValue(DummySubmodelFactory.SUBMODEL_TECHNICAL_DATA_ID, SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_SUBMODEL_ELEMENT_LIST_ID_SHORT);

		BaSyxHttpTestUtils.assertSameJSONContent(expectedValue, BaSyxHttpTestUtils.getResponseAsString(response));
	}

	@Test
	public void createSubmodelElementCollidingId() throws IOException {
		String element = getJSONValueAsString("SubmodelElement.json");
		CloseableHttpResponse createdResponse = BaSyxHttpTestUtils.executePostOnURL(createSubmodelElementsURL(DummySubmodelFactory.SUBMODEL_TECHNICAL_DATA_ID), element);

		assertEquals(HttpStatus.CONFLICT.value(), createdResponse.getCode());
	}

	@Test
	public void createSubmodelElement() throws FileNotFoundException, IOException, ParseException {
		String element = getJSONValueAsString("SubmodelElement.json");
		CloseableHttpResponse createdResponse = BaSyxHttpTestUtils.executePostOnURL(createSubmodelElementsURL(DummySubmodelFactory.SUBMODEL_SIMPLE_DATA_ID), element);

		CloseableHttpResponse fetchedResponse = requestSubmodelElement(DummySubmodelFactory.SUBMODEL_SIMPLE_DATA_ID, SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_PROPERTY_ID_SHORT);
		assertEquals(HttpStatus.OK.value(), createdResponse.getCode());
		BaSyxHttpTestUtils.assertSameJSONContent(element, BaSyxHttpTestUtils.getResponseAsString(fetchedResponse));
	}

	@Test
	public void deleteSubmodelElement() throws FileNotFoundException, IOException, ParseException {
		CloseableHttpResponse deleteResponse = BaSyxHttpTestUtils.executeDeleteOnURL(createSpecificSubmodelElementURL(DummySubmodelFactory.SUBMODEL_SIMPLE_DATA_ID, DummySubmodelFactory.SUBMODEL_ELEMENT_SIMPLE_DATA_ID_SHORT));
		assertEquals(HttpStatus.NO_CONTENT.value(), deleteResponse.getCode());

		CloseableHttpResponse fetchedResponse = requestSubmodelElement(DummySubmodelFactory.SUBMODEL_SIMPLE_DATA_ID, DummySubmodelFactory.SUBMODEL_ELEMENT_SIMPLE_DATA_ID_SHORT);
		assertEquals(HttpStatus.NOT_FOUND.value(), fetchedResponse.getCode());
	}

	@Test
	public void createNestedSubmodelElement() throws FileNotFoundException, IOException, ParseException {
		String element = getJSONValueAsString("SubmodelElement.json");
		CloseableHttpResponse createdInCollectionResponse = BaSyxHttpTestUtils.executePostOnURL(createSpecificSubmodelElementURL(DummySubmodelFactory.SUBMODEL_SIMPLE_DATA_ID, DummySubmodelFactory.SUBMODEL_ELEMENT_COLLECTION_SIMPLE),
				element);
		CloseableHttpResponse createdInListResponse = BaSyxHttpTestUtils.executePostOnURL(createSpecificSubmodelElementURL(DummySubmodelFactory.SUBMODEL_SIMPLE_DATA_ID, DummySubmodelFactory.SUBMODEL_ELEMENT_LIST_SIMPLE), element);
		assertEquals(HttpStatus.OK.value(), createdInCollectionResponse.getCode());
		assertEquals(HttpStatus.OK.value(), createdInListResponse.getCode());

		CloseableHttpResponse fetchedNestedInCollectionResponse = requestSubmodelElement(DummySubmodelFactory.SUBMODEL_SIMPLE_DATA_ID, createCollectionNestedIdShortPath(SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_PROPERTY_ID_SHORT));
		BaSyxHttpTestUtils.assertSameJSONContent(element, BaSyxHttpTestUtils.getResponseAsString(fetchedNestedInCollectionResponse));

		CloseableHttpResponse fetchedNestedInListResponse = requestSubmodelElement(DummySubmodelFactory.SUBMODEL_SIMPLE_DATA_ID, createListNestedIdShortPath(1));
		BaSyxHttpTestUtils.assertSameJSONContent(element, BaSyxHttpTestUtils.getResponseAsString(fetchedNestedInListResponse));
	}

	@Test
	public void deleteNestedSubmodelElement() throws FileNotFoundException, IOException, ParseException {
		String nestedIdShortPathInCollection = createCollectionNestedIdShortPath(DummySubmodelFactory.SUBMODEL_ELEMENT_FIRST_ID_SHORT);
		CloseableHttpResponse deleteResponse = BaSyxHttpTestUtils.executeDeleteOnURL(createSpecificSubmodelElementURL(DummySubmodelFactory.SUBMODEL_SIMPLE_DATA_ID, nestedIdShortPathInCollection));
		assertEquals(HttpStatus.NO_CONTENT.value(), deleteResponse.getCode());

		// delete first element of the submodel element list
		String nestedIdShortSmeList = createListNestedIdShortPath(0);
		BaSyxHttpTestUtils.executeDeleteOnURL(createSpecificSubmodelElementURL(DummySubmodelFactory.SUBMODEL_SIMPLE_DATA_ID, nestedIdShortSmeList));
		assertEquals(HttpStatus.NO_CONTENT.value(), deleteResponse.getCode());

		CloseableHttpResponse fetchedNestedInCollectionResponse = requestSubmodelElement(DummySubmodelFactory.SUBMODEL_ELEMENT_COLLECTION_SIMPLE, DummySubmodelFactory.SUBMODEL_ELEMENT_SIMPLE_DATA_ID_SHORT);
		assertEquals(HttpStatus.NOT_FOUND.value(), fetchedNestedInCollectionResponse.getCode());
		CloseableHttpResponse fetchedNestedInListResponse = requestSubmodelElement(DummySubmodelFactory.SUBMODEL_SIMPLE_DATA_ID, createListNestedIdShortPath(1));
		assertEquals(HttpStatus.NOT_FOUND.value(), fetchedNestedInListResponse.getCode());
	}

	@Test
	public void setNonExistingSubmodelElementValue() throws IOException {
		String valueToWrite = getJSONValueAsString("value/setFileValue.json");

		CloseableHttpResponse response = writeSubmodelElementValue(DummySubmodelFactory.SUBMODEL_TECHNICAL_DATA_ID, "nonExisting", valueToWrite);
		assertEquals(HttpStatus.NOT_FOUND.value(), response.getCode());
	}

	@Test
	public void getValuesSerializationOfSubmodel() throws IOException, ParseException {
		CloseableHttpResponse response = requestSubmodelValues(DummySubmodelFactory.SUBMODEL_TECHNICAL_DATA_ID);

		assertEquals(HttpStatus.OK.value(), response.getCode());

		String expectedValue = getJSONValueAsString("value/expectedValueOnlySerializationOfSubmodel.json");

		BaSyxHttpTestUtils.assertSameJSONContent(expectedValue, BaSyxHttpTestUtils.getResponseAsString(response));
	}

	@Test
	public void getValuesSerializationOfNonExistingSubmodel() throws IOException, ParseException {
		CloseableHttpResponse response = requestSubmodelValues("nonExisting");

		assertEquals(HttpStatus.NOT_FOUND.value(), response.getCode());
	}

	private String createCollectionNestedIdShortPath(String idShort) {
		return DummySubmodelFactory.SUBMODEL_ELEMENT_COLLECTION_SIMPLE + "." + idShort;
	}

	private String createListNestedIdShortPath(int index) {
		return DummySubmodelFactory.SUBMODEL_ELEMENT_LIST_SIMPLE.concat("%5B" + index + "%5D");
	}

	private String wrapStringValue(String value) {
		// The value needs to be wrapped to ensure that it is correctly identified as
		// string and not parsed to another primitive, e.g., int
		return "\"" + value + "\"";
	}

	private CloseableHttpResponse writeSubmodelElementValue(String submodelId, String smeIdShort, String value) throws IOException {
		return BaSyxHttpTestUtils.executePatchOnURL(createSubmodelElementValueURL(submodelId, smeIdShort), value);
	}

	private CloseableHttpResponse requestSubmodelElementValue(String submodelId, String smeIdShort) throws IOException {
		return BaSyxHttpTestUtils.executeGetOnURL(createSubmodelElementValueURL(submodelId, smeIdShort));

	}

	private CloseableHttpResponse requestSubmodelValues(String submodelId) throws IOException {
		return BaSyxHttpTestUtils.executeGetOnURL(createSubmodelValueURL(submodelId));
	}

	private String requestSubmodelElementsJSON(String id) throws IOException, ParseException {
		CloseableHttpResponse response = requestSubmodelElements(id);

		return BaSyxHttpTestUtils.getResponseAsString(response);
	}

	private CloseableHttpResponse requestSubmodelElement(String submodelId, String smeIdShort) throws IOException {
		return BaSyxHttpTestUtils.executeGetOnURL(createSpecificSubmodelElementURL(submodelId, smeIdShort));
	}

	private String createSubmodelElementValueURL(String submodelId, String smeIdShort) {
		return createSpecificSubmodelElementURL(submodelId, smeIdShort) + "/$value";
	}

	private String createSubmodelValueURL(String submodelId) {
		return createSubmodelURL(submodelId) + "/$value";
	}

	private String createSubmodelURL(String submodelId) {
		return BaSyxSubmodelHttpTestUtils.getSpecificSubmodelAccessPath(getURL(), submodelId);
	}

	private String createSpecificSubmodelElementURL(String submodelId, String smeIdShort) {
		return createSubmodelElementsURL(submodelId) + "/" + smeIdShort;
	}

	private String createSubmodelElementsURL(String submodelId) {
		return BaSyxSubmodelHttpTestUtils.getSpecificSubmodelAccessPath(getURL(), submodelId) + "/submodel-elements";
	}

	private CloseableHttpResponse requestSubmodelElements(String submodelId) throws IOException {
		return BaSyxHttpTestUtils.executeGetOnURL(createSubmodelElementsURL(submodelId));
	}

	private String getSubmodelElementsJSON() throws FileNotFoundException, IOException {
		return BaSyxHttpTestUtils.readJSONStringFromClasspath("SubmodelElements.json");
	}

	private String getSubmodelElementJSON() throws FileNotFoundException, IOException {
		return BaSyxHttpTestUtils.readJSONStringFromClasspath("SubmodelElement.json");
	}

	private String getJSONValueAsString(String fileName) throws FileNotFoundException, IOException {
		return BaSyxHttpTestUtils.readJSONStringFromClasspath(fileName);
	}

	protected List<Submodel> createSubmodels() {
		return Arrays.asList(DummySubmodelFactory.createTechnicalDataSubmodel(), DummySubmodelFactory.createOperationalDataSubmodel(), DummySubmodelFactory.createSimpleDataSubmodel());
	}

}

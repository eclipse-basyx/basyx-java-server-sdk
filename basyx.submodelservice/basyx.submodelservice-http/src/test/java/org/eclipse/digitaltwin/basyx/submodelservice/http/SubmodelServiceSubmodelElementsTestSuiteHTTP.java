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

package org.eclipse.digitaltwin.basyx.submodelservice.http;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

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
import org.eclipse.digitaltwin.basyx.http.pagination.Base64UrlEncodedCursor;
import org.eclipse.digitaltwin.basyx.http.serialization.BaSyxHttpTestUtils;
import org.eclipse.digitaltwin.basyx.submodelservice.DummySubmodelFactory;
import org.eclipse.digitaltwin.basyx.submodelservice.SubmodelServiceHelper;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.util.ResourceUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * Base testsuite for all Submodel Service HTTP tests related to Submodel
 * Elements
 * 
 * @author schnicke, fried
 *
 */
public abstract class SubmodelServiceSubmodelElementsTestSuiteHTTP {

	protected abstract String getURL();

	private final String CURSOR = "SimpleCollection";
	private final String ENCODED_CURSOR = Base64UrlEncodedCursor.encodeCursor(CURSOR);

	public static Submodel createSubmodel() {
		return DummySubmodelFactory.createSubmodelWithAllSubmodelElements();
	}

	@Test
	public void getSubmodelElements() throws FileNotFoundException, IOException, ParseException {
		String requestedSubmodelElements = requestSubmodelElementsJSON();

		String submodelElementJSON = getJSONValueAsString("SubmodelElements.json");
		BaSyxHttpTestUtils.assertSameJSONContent(submodelElementJSON, getJSONWithoutCursorInfo(requestedSubmodelElements));
	}

	@Test
	public void getPaginatedSubmodelElements() throws FileNotFoundException, IOException, ParseException {
		String actualPaginatedSubmodelElements = requestPaginatedSubmodelElementsJSON();

		String expectedPaginatedSubmodelElementJSON = getJSONValueAsString("SubmodelElementsPaginated.json");
		BaSyxHttpTestUtils.assertSameJSONContent(expectedPaginatedSubmodelElementJSON, getJSONWithoutCursorInfo(actualPaginatedSubmodelElements));
	}

	@Test
	public void getSubmodelElement() throws FileNotFoundException, IOException, ParseException {
		String expectedElement = getJSONValueAsString("SubmodelElement.json");
		CloseableHttpResponse response = requestSubmodelElement(SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_PROPERTY_ID_SHORT);

		assertEquals(HttpStatus.OK.value(), response.getCode());
		BaSyxHttpTestUtils.assertSameJSONContent(expectedElement, BaSyxHttpTestUtils.getResponseAsString(response));
	}

	@Test
	public void getPropertyValue() throws IOException, ParseException {
		String expectedValue = wrapStringValue("5000");

		CloseableHttpResponse response = requestSubmodelElementValue(SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_PROPERTY_ID_SHORT);

		assertEquals(HttpStatus.OK.value(), response.getCode());

		BaSyxHttpTestUtils.assertSameJSONContent(expectedValue, BaSyxHttpTestUtils.getResponseAsString(response));
	}

	@Test
	public void getNonExistingSubmodelElementValue() throws IOException {
		CloseableHttpResponse response = requestSubmodelElementValue("nonExisting");

		assertEquals(HttpStatus.NOT_FOUND.value(), response.getCode());
	}

	@Test
	public void setPropertyValue() throws IOException, ParseException {
		String expectedValue = wrapStringValue("2567");

		CloseableHttpResponse writeResponse = writeSubmodelElementValue(SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_PROPERTY_ID_SHORT, expectedValue);
		assertEquals(HttpStatus.NO_CONTENT.value(), writeResponse.getCode());

		CloseableHttpResponse response = requestSubmodelElementValue(SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_PROPERTY_ID_SHORT);

		BaSyxHttpTestUtils.assertSameJSONContent(expectedValue, BaSyxHttpTestUtils.getResponseAsString(response));
	}

	@Test
	public void getMultiLanguagePropertyValue() throws IOException, ParseException {
		CloseableHttpResponse response = requestSubmodelElementValue(SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_MULTI_LANG_PROP_ID_SHORT);

		assertEquals(HttpStatus.OK.value(), response.getCode());

		String expectedValue = getJSONValueAsString("value/expectedMultiLanguagePropertyValue.json");

		BaSyxHttpTestUtils.assertSameJSONContent(expectedValue, BaSyxHttpTestUtils.getResponseAsString(response));
	}

	@Test
	public void setMultiLanguagePropertyValue() throws IOException, ParseException {
		String expectedValue = getJSONValueAsString("value/setMultiLanguagePropertyValue.json");

		CloseableHttpResponse writeResponse = writeSubmodelElementValue(SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_MULTI_LANG_PROP_ID_SHORT, expectedValue);
		assertEquals(HttpStatus.NO_CONTENT.value(), writeResponse.getCode());

		CloseableHttpResponse response = requestSubmodelElementValue(SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_MULTI_LANG_PROP_ID_SHORT);

		BaSyxHttpTestUtils.assertSameJSONContent(expectedValue, BaSyxHttpTestUtils.getResponseAsString(response));
	}

	@Test
	public void getRangeValue() throws IOException, ParseException {
		CloseableHttpResponse response = requestSubmodelElementValue(SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_RANGE_ID_SHORT);

		assertEquals(HttpStatus.OK.value(), response.getCode());

		String expectedValue = getJSONValueAsString("value/expectedRangeValue.json");

		BaSyxHttpTestUtils.assertSameJSONContent(expectedValue, BaSyxHttpTestUtils.getResponseAsString(response));
	}

	@Test
	public void setRangeValue() throws IOException, ParseException {
		String expectedValue = getJSONValueAsString("value/setRangeValue.json");

		CloseableHttpResponse writeResponse = writeSubmodelElementValue(SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_RANGE_ID_SHORT, expectedValue);
		assertEquals(HttpStatus.NO_CONTENT.value(), writeResponse.getCode());

		CloseableHttpResponse response = requestSubmodelElementValue(SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_RANGE_ID_SHORT);

		BaSyxHttpTestUtils.assertSameJSONContent(expectedValue, BaSyxHttpTestUtils.getResponseAsString(response));
	}

	@Test
	public void getFileValue() throws IOException, ParseException {
		CloseableHttpResponse response = requestSubmodelElementValue(SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_FILE_ID_SHORT);

		assertEquals(HttpStatus.OK.value(), response.getCode());

		String expectedValue = getJSONValueAsString("value/expectedFileValue.json");

		BaSyxHttpTestUtils.assertSameJSONContent(expectedValue, BaSyxHttpTestUtils.getResponseAsString(response));
	}

	@Test
	public void setFileValue() throws IOException, ParseException {
		String expectedValue = getJSONValueAsString("value/setFileValue.json");

		CloseableHttpResponse writeResponse = writeSubmodelElementValue(SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_FILE_ID_SHORT, expectedValue);
		assertEquals(HttpStatus.NO_CONTENT.value(), writeResponse.getCode());

		CloseableHttpResponse response = requestSubmodelElementValue(SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_FILE_ID_SHORT);
		BaSyxHttpTestUtils.assertSameJSONContent(expectedValue, BaSyxHttpTestUtils.getResponseAsString(response));
	}

	@Test
	public void getBlobValue() throws IOException, ParseException {
		CloseableHttpResponse response = requestSubmodelElementValue(SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_BLOB_ID_SHORT);

		assertEquals(HttpStatus.OK.value(), response.getCode());

		String expectedValue = getJSONValueAsString("value/expectedBlobValue.json");

		BaSyxHttpTestUtils.assertSameJSONContent(expectedValue, BaSyxHttpTestUtils.getResponseAsString(response));
	}

	@Test
	public void setBlobValue() throws IOException, ParseException {
		String expectedValue = getJSONValueAsString("value/setBlobValue.json");

		CloseableHttpResponse writeResponse = writeSubmodelElementValue(SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_BLOB_ID_SHORT, expectedValue);
		assertEquals(HttpStatus.NO_CONTENT.value(), writeResponse.getCode());

		CloseableHttpResponse response = requestSubmodelElementValue(SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_BLOB_ID_SHORT);

		BaSyxHttpTestUtils.assertSameJSONContent(expectedValue, BaSyxHttpTestUtils.getResponseAsString(response));
	}

	@Test
	public void getEntityValue() throws IOException, ParseException {
		CloseableHttpResponse response = requestSubmodelElementValue(SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_ENTITY_ID_SHORT);

		assertEquals(HttpStatus.OK.value(), response.getCode());

		String expectedValue = getJSONValueAsString("value/expectedEntityValue.json");

		BaSyxHttpTestUtils.assertSameJSONContent(expectedValue, BaSyxHttpTestUtils.getResponseAsString(response));
	}

	@Test
	public void setEntityValue() throws IOException, ParseException {
		String expectedValue = getJSONValueAsString("value/setEntityValue.json");

		CloseableHttpResponse writeResponse = writeSubmodelElementValue(SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_ENTITY_ID_SHORT, expectedValue);
		assertEquals(HttpStatus.NO_CONTENT.value(), writeResponse.getCode());

		CloseableHttpResponse response = requestSubmodelElementValue(SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_ENTITY_ID_SHORT);

		BaSyxHttpTestUtils.assertSameJSONContent(expectedValue, BaSyxHttpTestUtils.getResponseAsString(response));
	}

	@Test
	public void setEntityValueMRP() throws IOException, ParseException {
		String minimumRequestPayloadValue = getJSONValueAsString("value/setEntityValueMRP.json");
		String expectedValue = getJSONValueAsString("value/expectedUpdatedMRPEntityValue.json");

		CloseableHttpResponse writeResponse = writeSubmodelElementValue(SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_ENTITY_ID_SHORT, minimumRequestPayloadValue);
		assertEquals(HttpStatus.NO_CONTENT.value(), writeResponse.getCode());

		CloseableHttpResponse response = requestSubmodelElementValue(SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_ENTITY_ID_SHORT);

		BaSyxHttpTestUtils.assertSameJSONContent(expectedValue, BaSyxHttpTestUtils.getResponseAsString(response));
	}

	@Test
	public void getReferenceElementValue() throws IOException, ParseException {
		CloseableHttpResponse response = requestSubmodelElementValue(SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_REFERENCE_ELEMENT_ID_SHORT);

		assertEquals(HttpStatus.OK.value(), response.getCode());

		String expectedValue = getJSONValueAsString("value/expectedReferenceElementValue.json");

		BaSyxHttpTestUtils.assertSameJSONContent(expectedValue, BaSyxHttpTestUtils.getResponseAsString(response));
	}

	@Test
	public void setReferenceElementValue() throws IOException, ParseException {
		String expectedValue = getJSONValueAsString("value/setReferenceElementValue.json");

		CloseableHttpResponse writeResponse = writeSubmodelElementValue(SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_REFERENCE_ELEMENT_ID_SHORT, expectedValue);
		assertEquals(HttpStatus.NO_CONTENT.value(), writeResponse.getCode());

		CloseableHttpResponse response = requestSubmodelElementValue(SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_REFERENCE_ELEMENT_ID_SHORT);

		BaSyxHttpTestUtils.assertSameJSONContent(expectedValue, BaSyxHttpTestUtils.getResponseAsString(response));
	}

	@Test
	public void getRelationshipElementValue() throws IOException, ParseException {
		CloseableHttpResponse response = requestSubmodelElementValue(SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_RELATIONSHIP_ELEMENT_ID_SHORT);

		assertEquals(HttpStatus.OK.value(), response.getCode());

		String expectedValue = getJSONValueAsString("value/expectedRelationshipElementValue.json");

		BaSyxHttpTestUtils.assertSameJSONContent(expectedValue, BaSyxHttpTestUtils.getResponseAsString(response));
	}

	@Test
	public void setRelationshipElementValue() throws IOException, ParseException {
		String expectedValue = getJSONValueAsString("value/setRelationshipElementValue.json");

		CloseableHttpResponse writeResponse = writeSubmodelElementValue(SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_RELATIONSHIP_ELEMENT_ID_SHORT, expectedValue);
		assertEquals(HttpStatus.NO_CONTENT.value(), writeResponse.getCode());

		CloseableHttpResponse response = requestSubmodelElementValue(SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_RELATIONSHIP_ELEMENT_ID_SHORT);

		BaSyxHttpTestUtils.assertSameJSONContent(expectedValue, BaSyxHttpTestUtils.getResponseAsString(response));
	}

	@Test
	public void getAnnotatedRelationshipElementValue() throws IOException, ParseException {
		CloseableHttpResponse response = requestSubmodelElementValue(SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_ANNOTATED_RELATIONSHIP_ELEMENT_ID_SHORT);

		assertEquals(HttpStatus.OK.value(), response.getCode());

		String expectedValue = getJSONValueAsString("value/expectedAnnotatedRelationshipElementValue.json");

		BaSyxHttpTestUtils.assertSameJSONContent(expectedValue, BaSyxHttpTestUtils.getResponseAsString(response));
	}

	@Test
	public void setAnnotatedRelationshipElementValue() throws IOException, ParseException {
		String expectedValue = getJSONValueAsString("value/setAnnotatedRelationshipElementValue.json");

		CloseableHttpResponse writeResponse = writeSubmodelElementValue(SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_ANNOTATED_RELATIONSHIP_ELEMENT_ID_SHORT, expectedValue);
		assertEquals(HttpStatus.NO_CONTENT.value(), writeResponse.getCode());

		CloseableHttpResponse response = requestSubmodelElementValue(SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_ANNOTATED_RELATIONSHIP_ELEMENT_ID_SHORT);

		BaSyxHttpTestUtils.assertSameJSONContent(expectedValue, BaSyxHttpTestUtils.getResponseAsString(response));
	}

	@Test
	public void getSubmodelElementCollectionValue() throws IOException, ParseException {
		CloseableHttpResponse response = requestSubmodelElementValue(SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_SUBMODEL_ELEMENT_COLLECTION_ID_SHORT);

		assertEquals(HttpStatus.OK.value(), response.getCode());

		String expectedValue = getJSONValueAsString("value/expectedSubmodelElementCollectionValue.json");

		BaSyxHttpTestUtils.assertSameJSONContent(expectedValue, BaSyxHttpTestUtils.getResponseAsString(response));
	}

	@Test
	public void setSubmodelElementCollectionValue() throws IOException, ParseException {
		String expectedValue = getJSONValueAsString("value/setSubmodelElementCollectionValue.json");

		CloseableHttpResponse writeResponse = writeSubmodelElementValue(SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_SUBMODEL_ELEMENT_COLLECTION_ID_SHORT, expectedValue);
		assertEquals(HttpStatus.NO_CONTENT.value(), writeResponse.getCode());

		CloseableHttpResponse response = requestSubmodelElementValue(SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_SUBMODEL_ELEMENT_COLLECTION_ID_SHORT);

		BaSyxHttpTestUtils.assertSameJSONContent(expectedValue, BaSyxHttpTestUtils.getResponseAsString(response));
	}

	@Test
	public void getSubmodelElementListValue() throws IOException, ParseException {
		CloseableHttpResponse response = requestSubmodelElementValue(SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_SUBMODEL_ELEMENT_LIST_ID_SHORT);

		assertEquals(HttpStatus.OK.value(), response.getCode());

		String expectedValue = getJSONValueAsString("value/expectedSubmodelElementListValue.json");

		BaSyxHttpTestUtils.assertSameJSONContent(expectedValue, BaSyxHttpTestUtils.getResponseAsString(response));
	}

	@Test
	public void setSubmodelElementListValue() throws IOException, ParseException {
		String expectedValue = getJSONValueAsString("value/setSubmodelElementListValue.json");

		CloseableHttpResponse writeResponse = writeSubmodelElementValue(SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_SUBMODEL_ELEMENT_LIST_ID_SHORT, expectedValue);
		assertEquals(HttpStatus.NO_CONTENT.value(), writeResponse.getCode());

		CloseableHttpResponse response = requestSubmodelElementValue(SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_SUBMODEL_ELEMENT_LIST_ID_SHORT);

		BaSyxHttpTestUtils.assertSameJSONContent(expectedValue, BaSyxHttpTestUtils.getResponseAsString(response));
	}

	@Test
	public void createSubmodelElementCollidingId() throws IOException {
		String element = getJSONValueAsString("SubmodelElement.json");
		CloseableHttpResponse createdResponse = BaSyxHttpTestUtils.executePostOnURL(createSubmodelElementsURL(), element);

		assertEquals(HttpStatus.CONFLICT.value(), createdResponse.getCode());
	}

	@Test
	public void createSubmodelElement() throws FileNotFoundException, IOException, ParseException {
		String element = getJSONValueAsString("SubmodelElementNew.json");
		CloseableHttpResponse createdResponse = BaSyxHttpTestUtils.executePostOnURL(createSubmodelElementsURL(), element);

		CloseableHttpResponse fetchedResponse = requestSubmodelElement("MaxRotationSpeedNew");
		assertEquals(HttpStatus.CREATED.value(), createdResponse.getCode());
		BaSyxHttpTestUtils.assertSameJSONContent(element, BaSyxHttpTestUtils.getResponseAsString(fetchedResponse));
	}
	
	@Test
	public void updateNonFileSME() throws FileNotFoundException, IOException, ParseException {
		String element = getJSONValueAsString("PropertySubmodelElementUpdate.json");
		
		String idShortPathPropertyInSmeCol = SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_SUBMODEL_ELEMENT_COLLECTION_ID_SHORT + "." + SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_PROPERTY_ID_SHORT;
		
		CloseableHttpResponse updatedResponse = updateElement(createSpecificSubmodelElementURL(idShortPathPropertyInSmeCol), element);
		assertEquals(HttpStatus.NO_CONTENT.value(), updatedResponse.getCode());

		CloseableHttpResponse fetchedResponse = requestSubmodelElement(idShortPathPropertyInSmeCol);
		BaSyxHttpTestUtils.assertSameJSONContent(element, BaSyxHttpTestUtils.getResponseAsString(fetchedResponse));
	}

	@Test
	public void updateNonFileSMEWithFileSME() throws FileNotFoundException, IOException, ParseException {
		String element = getJSONValueAsString("FileSubmodelElementUpdate.json");
		
		String idShortPathPropertyInSmeCol = SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_SUBMODEL_ELEMENT_COLLECTION_ID_SHORT + "." + SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_PROPERTY_ID_SHORT;
		
		CloseableHttpResponse updatedResponse = updateElement(createSpecificSubmodelElementURL(idShortPathPropertyInSmeCol), element);
		assertEquals(HttpStatus.NO_CONTENT.value(), updatedResponse.getCode());
		
		CloseableHttpResponse fetchedResponse = requestSubmodelElement(idShortPathPropertyInSmeCol);
		BaSyxHttpTestUtils.assertSameJSONContent(element, BaSyxHttpTestUtils.getResponseAsString(fetchedResponse));
	}
	
	@Test
	public void updateNonExistingSME() throws FileNotFoundException, IOException, ParseException {
		String element = getJSONValueAsString("PropertySubmodelElementUpdate.json");
		
		String idShortPathPropertyInSmeCol = SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_SUBMODEL_ELEMENT_COLLECTION_ID_SHORT + ".NonExistingSMEIdShort";
		
		CloseableHttpResponse updatedResponse = updateElement(createSpecificSubmodelElementURL(idShortPathPropertyInSmeCol), element);
		assertEquals(HttpStatus.NOT_FOUND.value(), updatedResponse.getCode());
	}

	@Test
	public void deleteSubmodelElement() throws FileNotFoundException, IOException, ParseException {
		CloseableHttpResponse deleteResponse = BaSyxHttpTestUtils.executeDeleteOnURL(createSpecificSubmodelElementURL(DummySubmodelFactory.SUBMODEL_ELEMENT_SIMPLE_DATA_ID_SHORT));
		assertEquals(HttpStatus.NO_CONTENT.value(), deleteResponse.getCode());

		CloseableHttpResponse fetchedResponse = requestSubmodelElement(DummySubmodelFactory.SUBMODEL_ELEMENT_SIMPLE_DATA_ID_SHORT);
		assertEquals(HttpStatus.NOT_FOUND.value(), fetchedResponse.getCode());
	}

	@Test
	public void createNestedSubmodelElementInSubmodelElementCollection() throws FileNotFoundException, IOException, ParseException {
		String element = getJSONValueAsString("SubmodelElementNew.json");
		CloseableHttpResponse createdInCollectionResponse = BaSyxHttpTestUtils.executePostOnURL(createSpecificSubmodelElementURL(DummySubmodelFactory.SUBMODEL_ELEMENT_COLLECTION_SIMPLE), element);
		assertEquals(HttpStatus.CREATED.value(), createdInCollectionResponse.getCode());
		CloseableHttpResponse fetchedNestedInCollectionResponse = requestSubmodelElement(createCollectionNestedIdShortPath("MaxRotationSpeedNew"));
		BaSyxHttpTestUtils.assertSameJSONContent(element, BaSyxHttpTestUtils.getResponseAsString(fetchedNestedInCollectionResponse));

	}

	@Test
	public void createNestedSubmodelElementInSubmodelElementList() throws IOException, JsonProcessingException, JsonMappingException, ParseException {
		String element = getJSONValueAsString("SubmodelElementNew.json");
		CloseableHttpResponse createdInListResponse = BaSyxHttpTestUtils.executePostOnURL(createSpecificSubmodelElementURL(DummySubmodelFactory.SUBMODEL_ELEMENT_LIST_SIMPLE), element);
		assertEquals(HttpStatus.CREATED.value(), createdInListResponse.getCode());
		CloseableHttpResponse fetchedNestedInListResponse = requestSubmodelElement(createListNestedIdShortPath(1));
		BaSyxHttpTestUtils.assertSameJSONContent(element, BaSyxHttpTestUtils.getResponseAsString(fetchedNestedInListResponse));
	}

	@Test
	public void deleteNestedSubmodelElementFromSubmodelElementCollection() throws FileNotFoundException, IOException, ParseException {
		String nestedIdShortPathInCollection = createCollectionNestedIdShortPath(DummySubmodelFactory.SUBMODEL_ELEMENT_FIRST_ID_SHORT);
		CloseableHttpResponse deleteResponse = BaSyxHttpTestUtils.executeDeleteOnURL(createSpecificSubmodelElementURL(nestedIdShortPathInCollection));
		assertEquals(HttpStatus.NO_CONTENT.value(), deleteResponse.getCode());

		CloseableHttpResponse fetchedNestedInCollectionResponse = requestSubmodelElement(nestedIdShortPathInCollection);
		assertEquals(HttpStatus.NOT_FOUND.value(), fetchedNestedInCollectionResponse.getCode());
	}

	@Test
	public void deleteNestedSubmodelElementFromSubmodelElementList() throws IOException {
		String nestedIdShortSmeList = createListNestedIdShortPath(0);
		CloseableHttpResponse deleteResponse = BaSyxHttpTestUtils.executeDeleteOnURL(createSpecificSubmodelElementURL(nestedIdShortSmeList));
		assertEquals(HttpStatus.NO_CONTENT.value(), deleteResponse.getCode());

		CloseableHttpResponse fetchedNestedInListResponse = requestSubmodelElement(createListNestedIdShortPath(1));
		assertEquals(HttpStatus.NOT_FOUND.value(), fetchedNestedInListResponse.getCode());
	}

	@Test
	public void setNonExistingSubmodelElementValue() throws IOException {
		String valueToWrite = getJSONValueAsString("value/setFileValue.json");

		CloseableHttpResponse response = writeSubmodelElementValue("nonExisting", valueToWrite);
		assertEquals(HttpStatus.NOT_FOUND.value(), response.getCode());
	}

	@Test
	public void getValuesSerializationOfSubmodel() throws IOException, ParseException {
		CloseableHttpResponse response = requestSubmodelValues();

		assertEquals(HttpStatus.OK.value(), response.getCode());

		String expectedValue = getJSONValueAsString("value/expectedValueOnlySerializationOfSubmodel.json");

		BaSyxHttpTestUtils.assertSameJSONContent(expectedValue, BaSyxHttpTestUtils.getResponseAsString(response));
	}

	@Test
	public void patchSubmodelValues() throws FileNotFoundException, IOException, ParseException {
		String patch = getJSONValueAsString("value/newSubmodelValue.json");

		CloseableHttpResponse patchResponse = BaSyxHttpTestUtils.executePatchOnURL(createSubmodelValueURL(), patch);
		assertEquals(HttpStatus.NO_CONTENT.value(), patchResponse.getCode());
		
		CloseableHttpResponse getResponse = BaSyxHttpTestUtils.executeGetOnURL(createSubmodelValueURL());

		String expected = getJSONValueAsString("value/expectedNewSubmodelValue.json");

		BaSyxHttpTestUtils.assertSameJSONContent(expected, BaSyxHttpTestUtils.getResponseAsString(getResponse));
	}

	@Test
	public void invokeOperation() throws FileNotFoundException, IOException, ParseException {
		String parameters = getJSONValueAsString("operation/parameters.json");
		CloseableHttpResponse response = requestOperationInvocation(SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_OPERATION_ID, parameters);

		assertEquals(HttpStatus.OK.value(), response.getCode());
		String expectedValue = getJSONValueAsString("operation/result.json");
		BaSyxHttpTestUtils.assertSameJSONContent(expectedValue, BaSyxHttpTestUtils.getResponseAsString(response));

	}

	@Test
	public void invokeInOutOperation() throws IOException, ParseException {
		String parameters = getJSONValueAsString("operation/parameters-inout.json");
		CloseableHttpResponse response = requestOperationInvocation(SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_OPERATIONINOUT_ID, parameters);

		assertEquals(HttpStatus.OK.value(), response.getCode());
		String expectedValue = getJSONValueAsString("operation/result-inout.json");
		BaSyxHttpTestUtils.assertSameJSONContent(expectedValue, BaSyxHttpTestUtils.getResponseAsString(response));

	}

	@Test
	public void updateFileSMEWithNonFileSME() throws FileNotFoundException, IOException, ParseException {
		String element = getJSONValueAsString("PropertySubmodelElementUpdateWithNewIdShort.json");

		String idShortPathPropertyInSmeCol = SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_SUBMODEL_ELEMENT_COLLECTION_ID_SHORT + "." + SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_FILE_ID_SHORT;

		uploadFileToSubmodelElement(idShortPathPropertyInSmeCol);

		CloseableHttpResponse fileResponse = BaSyxHttpTestUtils.executeGetOnURL(createSMEFileGetURL(idShortPathPropertyInSmeCol));
		assertEquals(HttpStatus.OK.value(), fileResponse.getCode());

		CloseableHttpResponse updatedResponse = updateElement(createSpecificSubmodelElementURL(idShortPathPropertyInSmeCol), element);
		assertEquals(HttpStatus.NO_CONTENT.value(), updatedResponse.getCode());

		CloseableHttpResponse fetchedResponse = BaSyxHttpTestUtils.executeGetOnURL(createSpecificSubmodelElementURL(idShortPathPropertyInSmeCol));
		BaSyxHttpTestUtils.assertSameJSONContent(element, BaSyxHttpTestUtils.getResponseAsString(fetchedResponse));
	}

	@Test
	public void updateFileSMEWithFileSME() throws FileNotFoundException, IOException, ParseException {
		String element = getJSONValueAsString("FileSubmodelElementUpdateWithNewIdShort.json");

		String idShortPathPropertyInSmeCol = SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_SUBMODEL_ELEMENT_COLLECTION_ID_SHORT + "." + SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_FILE_ID_SHORT;

		uploadFileToSubmodelElement(idShortPathPropertyInSmeCol);

		CloseableHttpResponse fileResponse = BaSyxHttpTestUtils.executeGetOnURL(createSMEFileGetURL(idShortPathPropertyInSmeCol));
		assertEquals(HttpStatus.OK.value(), fileResponse.getCode());

		CloseableHttpResponse updatedResponse = updateElement(createSpecificSubmodelElementURL(idShortPathPropertyInSmeCol), element);
		assertEquals(HttpStatus.NO_CONTENT.value(), updatedResponse.getCode());

		CloseableHttpResponse fetchedResponse = BaSyxHttpTestUtils.executeGetOnURL(createSpecificSubmodelElementURL(idShortPathPropertyInSmeCol));
		BaSyxHttpTestUtils.assertSameJSONContent(element, BaSyxHttpTestUtils.getResponseAsString(fetchedResponse));
	}


	@Test
	public void uploadFileToFileSubmodelElement() throws IOException {
		CloseableHttpResponse submodelElementFileUploadResponse = uploadFileToSubmodelElement(DummySubmodelFactory.SUBMODEL_ELEMENT_FILE_ID_SHORT);

		assertEquals(HttpStatus.OK.value(), submodelElementFileUploadResponse.getCode());
	}

	@Test
	public void uploadFileToNonFileSubmodelElement() throws FileNotFoundException, UnsupportedEncodingException, ClientProtocolException, IOException {
		CloseableHttpResponse submodelElementFileUploadResponse = uploadFileToSubmodelElement(SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_PROPERTY_ID_SHORT);

		assertEquals(HttpStatus.PRECONDITION_FAILED.value(), submodelElementFileUploadResponse.getCode());
	}

	@Test
	public void uploadFileToNotExistElement() throws FileNotFoundException, UnsupportedEncodingException, ClientProtocolException, IOException {
		CloseableHttpResponse submodelElementFileUploadResponse = uploadFileToSubmodelElement("ElementNotExist");

		assertEquals(HttpStatus.NOT_FOUND.value(), submodelElementFileUploadResponse.getCode());
	}

	@Test
	public void deleteFile() throws FileNotFoundException, IOException {
		uploadFileToSubmodelElement(DummySubmodelFactory.SUBMODEL_ELEMENT_FILE_ID_SHORT);

		CloseableHttpResponse response = BaSyxHttpTestUtils.executeDeleteOnURL(createSMEFileDeleteURL(DummySubmodelFactory.SUBMODEL_ELEMENT_FILE_ID_SHORT));
		assertEquals(HttpStatus.OK.value(), response.getCode());

		response = BaSyxHttpTestUtils.executeGetOnURL(createSMEFileGetURL(DummySubmodelFactory.SUBMODEL_ELEMENT_FILE_ID_SHORT));
		assertEquals(HttpStatus.NOT_FOUND.value(), response.getCode());
	}

	@Test
	public void deleteFileToNonFileSubmodelElement() throws FileNotFoundException, UnsupportedEncodingException, ClientProtocolException, IOException {
		uploadFileToSubmodelElement(DummySubmodelFactory.SUBMODEL_ELEMENT_FILE_ID_SHORT);

		CloseableHttpResponse response = BaSyxHttpTestUtils.executeDeleteOnURL(createSMEFileGetURL(SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_PROPERTY_ID_SHORT));

		assertEquals(HttpStatus.PRECONDITION_FAILED.value(), response.getCode());
	}

	@Test
	public void deleteFileFromNotExistElement() throws FileNotFoundException, UnsupportedEncodingException, ClientProtocolException, IOException {
		CloseableHttpResponse response = BaSyxHttpTestUtils.executeDeleteOnURL(createSMEFileGetURL("ElementNotExist"));

		assertEquals(HttpStatus.NOT_FOUND.value(), response.getCode());
	}

	@Test
	public void getFile() throws FileNotFoundException, IOException, ParseException {
		String fileName = DummySubmodelFactory.FILE_NAME;

		byte[] expectedFile = readBytesFromClasspath(fileName);

		uploadFileToSubmodelElement(DummySubmodelFactory.SUBMODEL_ELEMENT_FILE_ID_SHORT);

		CloseableHttpResponse response = BaSyxHttpTestUtils.executeGetOnURL(createSMEFileGetURL(DummySubmodelFactory.SUBMODEL_ELEMENT_FILE_ID_SHORT));
		assertEquals(HttpStatus.OK.value(), response.getCode());

		byte[] actualFile = EntityUtils.toByteArray(response.getEntity());

		response.close();

		assertArrayEquals(expectedFile, actualFile);
	}

	@Test
	public void getFileFromNonFileSubmodelElement() throws FileNotFoundException, UnsupportedEncodingException, ClientProtocolException, IOException {
		CloseableHttpResponse response = BaSyxHttpTestUtils.executeGetOnURL(createSMEFileGetURL(SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_PROPERTY_ID_SHORT));

		assertEquals(HttpStatus.PRECONDITION_FAILED.value(), response.getCode());
	}

	@Test
	public void getFileFromNotExistElement() throws FileNotFoundException, UnsupportedEncodingException, ClientProtocolException, IOException {
		CloseableHttpResponse response = BaSyxHttpTestUtils.executeGetOnURL(createSMEFileGetURL("ElementNotExist"));

		assertEquals(HttpStatus.NOT_FOUND.value(), response.getCode());
	}
	

	private CloseableHttpResponse updateElement(String url, String element) throws IOException {
		return BaSyxHttpTestUtils.executePutOnURL(url, element);
	}
	
	private String getJSONWithoutCursorInfo(String response) throws JsonMappingException, JsonProcessingException  {
		return BaSyxHttpTestUtils.removeCursorFromJSON(response);
	}

	private CloseableHttpResponse requestOperationInvocation(String operationId, String parameters) throws IOException {
		return BaSyxHttpTestUtils.executePostOnURL(createSpecificSubmodelElementURL(operationId) + "/invoke", parameters);
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

	private CloseableHttpResponse writeSubmodelElementValue(String smeIdShort, String value) throws IOException {
		return BaSyxHttpTestUtils.executePatchOnURL(createSubmodelElementValueURL(smeIdShort), value);
	}

	private CloseableHttpResponse requestSubmodelElementValue(String smeIdShort) throws IOException {
		return BaSyxHttpTestUtils.executeGetOnURL(createSubmodelElementValueURL(smeIdShort));

	}

	private CloseableHttpResponse requestSubmodelValues() throws IOException {
		return BaSyxHttpTestUtils.executeGetOnURL(createSubmodelValueURL());
	}

	private String requestSubmodelElementsJSON() throws IOException, ParseException, ParseException {
		CloseableHttpResponse response = requestSubmodelElements();

		return BaSyxHttpTestUtils.getResponseAsString(response);
	}

	private String requestPaginatedSubmodelElementsJSON() throws IOException, ParseException, ParseException {
		CloseableHttpResponse response = requestPaginatedSubmodelElements();

		return BaSyxHttpTestUtils.getResponseAsString(response);
	}

	private CloseableHttpResponse requestSubmodelElement(String smeIdShort) throws IOException {
		return BaSyxHttpTestUtils.executeGetOnURL(createSpecificSubmodelElementURL(smeIdShort));
	}

	private String createSubmodelElementValueURL(String smeIdShort) {
		return createSpecificSubmodelElementURL(smeIdShort) + "/$value";
	}

	private String createSubmodelValueURL() {
		return getURL() + "/$value";
	}

	private String createSpecificSubmodelElementURL(String smeIdShortPath) {
		return getURL() + "/submodel-elements/" + smeIdShortPath;
	}

	private String createSubmodelElementsURL() {
		return getURL() + "/submodel-elements";
	}

	private String createPaginatedSubmodelElementsURL() {
		return getURL() + "/submodel-elements?limit=1&cursor=" + ENCODED_CURSOR;
	}

	private CloseableHttpResponse requestSubmodelElements() throws IOException {
		return BaSyxHttpTestUtils.executeGetOnURL(createSubmodelElementsURL());
	}

	private CloseableHttpResponse requestPaginatedSubmodelElements() throws IOException {
		return BaSyxHttpTestUtils.executeGetOnURL(createPaginatedSubmodelElementsURL());
	}

	private String getJSONValueAsString(String fileName) throws FileNotFoundException, IOException {
		return BaSyxHttpTestUtils.readJSONStringFromClasspath(fileName);
	}

	private CloseableHttpResponse uploadFileToSubmodelElement(String submodelElementIdShort) throws IOException {
		CloseableHttpClient client = HttpClients.createDefault();

		String fileName = DummySubmodelFactory.FILE_NAME;

		java.io.File file = ResourceUtils.getFile("classpath:" + fileName);

		HttpPut putRequest = createPutRequestWithFile(submodelElementIdShort, fileName, file);

		return executePutRequest(client, putRequest);
	}

	private HttpPut createPutRequestWithFile(String submodelElementIdShort, String fileName, java.io.File file) {
		HttpPut putRequest = new HttpPut(createSMEFileUploadURL(submodelElementIdShort, fileName));

		MultipartEntityBuilder builder = MultipartEntityBuilder.create();

		builder.addPart("file", new FileBody(file));
		builder.setContentType(ContentType.MULTIPART_FORM_DATA);

		HttpEntity multipart = builder.build();
		putRequest.setEntity(multipart);
		return putRequest;
	}

	private String createSMEFileUploadURL(String submodelElementIdShort, String fileName) {
		return getURL() + "/submodel-elements/" + submodelElementIdShort + "/attachment?fileName=" + fileName;
	}

	private String createSMEFileDeleteURL(String submodelElementIdShort) {
		return getURL() + "/submodel-elements/" + submodelElementIdShort + "/attachment";
	}

	private String createSMEFileGetURL( String submodelElementIdShort) {
		return getURL() + "/submodel-elements/" + submodelElementIdShort + "/attachment";
	}


	private CloseableHttpResponse executePutRequest(CloseableHttpClient client, HttpPut putRequest) throws IOException {
		CloseableHttpResponse response = client.execute(putRequest);

		HttpEntity responseEntity = response.getEntity();

		EntityUtils.consume(responseEntity);
		return response;
	}

	private byte[] readBytesFromClasspath(String fileName) throws FileNotFoundException, IOException {
		ClassPathResource classPathResource = new ClassPathResource(fileName);
		InputStream in = classPathResource.getInputStream();

		return in.readAllBytes();
	}
}

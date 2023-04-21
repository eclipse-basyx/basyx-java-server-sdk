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

package org.eclipse.digitaltwin.basyx.conceptdescriptionrepository.http;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.ParseException;
import org.eclipse.digitaltwin.basyx.conceptdescriptionrepository.ConceptDescriptionRepository;
import org.eclipse.digitaltwin.basyx.conceptdescriptionrepository.core.ConceptDescriptionRepositorySuiteHelper;
import org.eclipse.digitaltwin.basyx.conceptdescriptionrepository.core.DummyConceptDescriptionFactory;
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

/**
 * Tests the ConceptDescription specific parts of the {@link ConceptDescriptionRepository} HTTP/REST API
 * 
 * @author danish
 *
 */
public class TestConceptDescriptionRepositoryHTTP {
	private ConfigurableApplicationContext appContext;

	@Before
	public void startConceptDescriptionRepo() throws Exception {
		appContext = new SpringApplication(DummyConceptDescriptionRepositoryComponent.class).run(new String[] {});
	}

	@After
	public void shutdownConceptDescriptionRepo() {
		appContext.close();
	}

	@Test
	public void getAllConceptDescriptionsPreconfigured() throws IOException, ParseException {
		String conceptDescriptionsJSON = getAllConceptDescriptionsJSON();
		String expectedConceptDescriptionsJSON = getAllConceptDescriptionJSON();
		BaSyxHttpTestUtils.assertSameJSONContent(expectedConceptDescriptionsJSON, conceptDescriptionsJSON);
	}
	
	@Test
	public void getAllConceptDescriptionsByIdShortPreconfigured() throws IOException, ParseException {
		String conceptDescriptionsJSON = getAllConceptDescriptionsByIdShortJSON("ConceptDescription");
		String expectedConceptDescriptionsJSON = getConceptDescriptionsWithIdShort();
		BaSyxHttpTestUtils.assertSameJSONContent(expectedConceptDescriptionsJSON, conceptDescriptionsJSON);
	}
	
	@Test
	public void getAllConceptDescriptionsByIsCaseOfPreconfigured() throws IOException, ParseException {
		String conceptDescriptionsJSON = getAllConceptDescriptionsByIsCaseOfJSON(getReferenceJSON());
		String expectedConceptDescriptionsJSON = getConceptDescriptionsWithIsCaseOf();
		BaSyxHttpTestUtils.assertSameJSONContent(expectedConceptDescriptionsJSON, conceptDescriptionsJSON);
	}
	
	@Test
	public void getAllConceptDescriptionsByDataSpecRefPreconfigured() throws IOException, ParseException {
		String conceptDescriptionsJSON = getAllConceptDescriptionsByDataSpecRefJSON(getDataSpecReferenceJSON());
		String expectedConceptDescriptionsJSON = getConceptDescriptionsWithDataSpecRef();
		BaSyxHttpTestUtils.assertSameJSONContent(expectedConceptDescriptionsJSON, conceptDescriptionsJSON);
	}

	@Test
	public void getSpecificConceptDescription() throws ParseException, IOException {
		String actualConceptDescriptionJSON = requestSpecificConceptDescriptionJSON(DummyConceptDescriptionFactory.createConceptDescription().getId());
		String expectedConceptDescriptionJSON = getSingleConceptDescriptionJSON();

		BaSyxHttpTestUtils.assertSameJSONContent(expectedConceptDescriptionJSON, actualConceptDescriptionJSON);
	}
	
	@Test
	public void setTwoParametersInRequest() throws IOException {
		CloseableHttpResponse response = requestWithTwoParameters("doesntMatterIdShort", "doesntMatterDataSpec");

		assertEquals(HttpStatus.BAD_REQUEST.value(), response.getCode());
	}
	
	@Test
	public void setAllParametersInRequest() throws IOException {
		CloseableHttpResponse response = requestWithAllParameters("doesntMatterIdShort", "doesntMatterIsCaseOf", "doesntMatterDataSpec");

		assertEquals(HttpStatus.BAD_REQUEST.value(), response.getCode());
	}

	@Test
	public void getSpecificConceptDescriptionNonExisting() throws IOException {
		CloseableHttpResponse response = requestConceptDescription("nonExisting");

		assertEquals(HttpStatus.NOT_FOUND.value(), response.getCode());
	}


	@Test
	public void updateExistingConceptDescription() throws IOException, ParseException {
		String id = ConceptDescriptionRepositorySuiteHelper.BASIC_CONCEPT_DESCRIPTION_ID;
		String expectedConceptDescriptionJSON = getUpdatedConceptDescriptionJSON();

		CloseableHttpResponse creationResponse = putConceptDescription(id, expectedConceptDescriptionJSON);

		assertEquals(HttpStatus.NO_CONTENT.value(), creationResponse.getCode());

		String actualConceptDescriptionJSON = requestSpecificConceptDescriptionJSON(id);
		BaSyxHttpTestUtils.assertSameJSONContent(expectedConceptDescriptionJSON, actualConceptDescriptionJSON);
	}

	@Test
	public void updateNonExistingConceptDescription() throws IOException {
		String id = "nonExisting";
		String expectedConceptDescriptionJSON = getUpdatedConceptDescriptionJSON();

		CloseableHttpResponse updateResponse = putConceptDescription(id, expectedConceptDescriptionJSON);

		assertEquals(HttpStatus.NOT_FOUND.value(), updateResponse.getCode());
	}

	@Test
	public void createConceptDescriptionNewId() throws IOException, ParseException {
		String expectedConceptDescriptionJSON = getNewConceptDescriptionJSON();
		CloseableHttpResponse creationResponse = createConceptDescription(expectedConceptDescriptionJSON);

		assertConceptDescriptionCreationReponse(expectedConceptDescriptionJSON, creationResponse);

		String actualConceptDescription = requestSpecificConceptDescriptionJSON("newConceptDescription");
		BaSyxHttpTestUtils.assertSameJSONContent(expectedConceptDescriptionJSON, actualConceptDescription);
	}

	@Test
	public void createSubmodelCollidingId() throws IOException {
		String conceptDescriptionJSON = getSingleConceptDescriptionJSON();
		CloseableHttpResponse creationResponse = createConceptDescription(conceptDescriptionJSON);

		assertEquals(HttpStatus.CONFLICT.value(), creationResponse.getCode());
	}

	@Test
	public void deleteConceptDescription() throws IOException {
		String existingConceptDescriptionId = DummyConceptDescriptionFactory.createConceptDescription().getId();

		CloseableHttpResponse deletionResponse = deleteConceptDescriptionById(existingConceptDescriptionId);
		assertEquals(HttpStatus.NO_CONTENT.value(), deletionResponse.getCode());
		
		CloseableHttpResponse getResponse = requestConceptDescription(existingConceptDescriptionId);
		assertEquals(HttpStatus.NOT_FOUND.value(), getResponse.getCode());
	}

	@Test
	public void deleteNonExistingConceptDescription() throws IOException {
		CloseableHttpResponse deletionResponse = deleteConceptDescriptionById("nonExisting");

		assertEquals(HttpStatus.NOT_FOUND.value(), deletionResponse.getCode());
	}

	private void assertConceptDescriptionCreationReponse(String conceptDescriptionJSON, CloseableHttpResponse creationResponse) throws IOException, ParseException, JsonProcessingException, JsonMappingException {
		assertEquals(HttpStatus.CREATED.value(), creationResponse.getCode());
		String response = BaSyxHttpTestUtils.getResponseAsString(creationResponse);
		BaSyxHttpTestUtils.assertSameJSONContent(conceptDescriptionJSON, response);
	}

	private CloseableHttpResponse createConceptDescription(String conceptDescriptionJSON) throws IOException {
		return BaSyxHttpTestUtils.executePostOnServer(BaSyxConceptDescriptionHttpTestUtils.CONCEPT_DESCRIPTION_ACCESS_URL, conceptDescriptionJSON);
	}

	private CloseableHttpResponse deleteConceptDescriptionById(String conceptDescriptionId) throws IOException {
		return BaSyxHttpTestUtils.executeDeleteOnServer(BaSyxConceptDescriptionHttpTestUtils.CONCEPT_DESCRIPTION_ACCESS_URL + "/" + Base64UrlEncodedIdentifier.encodeIdentifier(conceptDescriptionId));
	}

	private CloseableHttpResponse putConceptDescription(String conceptDescriptionId, String conceptDescriptionJSON) throws IOException {
		return BaSyxHttpTestUtils.executePutOnURL(BaSyxConceptDescriptionHttpTestUtils.getSpecificConceptDescriptionAccessPath(conceptDescriptionId), conceptDescriptionJSON);
	}

	private String requestSpecificConceptDescriptionJSON(String conceptDescriptionId) throws IOException, ParseException {
		CloseableHttpResponse response = requestConceptDescription(conceptDescriptionId);

		return BaSyxHttpTestUtils.getResponseAsString(response);
	}

	private CloseableHttpResponse requestConceptDescription(String conceptDescriptionId) throws IOException {
		return BaSyxHttpTestUtils.executeGetOnURL(BaSyxConceptDescriptionHttpTestUtils.getSpecificConceptDescriptionAccessPath(conceptDescriptionId));
	}

	private String getAllConceptDescriptionsJSON() throws IOException, ParseException {
		CloseableHttpResponse response = BaSyxHttpTestUtils.executeGetOnURL(BaSyxConceptDescriptionHttpTestUtils.CONCEPT_DESCRIPTION_ACCESS_URL);

		return BaSyxHttpTestUtils.getResponseAsString(response);
	}
	
	private String getAllConceptDescriptionsByIdShortJSON(String idShort) throws IOException, ParseException {
		CloseableHttpResponse response = BaSyxHttpTestUtils.executeGetOnURL(BaSyxConceptDescriptionHttpTestUtils.getAllConceptDescriptionsWithIdShortParameterAccessPath(idShort));

		return BaSyxHttpTestUtils.getResponseAsString(response);
	}
	
	private String getAllConceptDescriptionsByIsCaseOfJSON(String reference) throws IOException, ParseException {
		CloseableHttpResponse response = BaSyxHttpTestUtils.executeGetOnURL(BaSyxConceptDescriptionHttpTestUtils.getAllConceptDescriptionsWithIsCaseOfParameterAccessPath(reference));
		
		return BaSyxHttpTestUtils.getResponseAsString(response);
	}
	
	private String getAllConceptDescriptionsByDataSpecRefJSON(String dataSpecificationRef) throws IOException, ParseException {
		CloseableHttpResponse response = BaSyxHttpTestUtils.executeGetOnURL(BaSyxConceptDescriptionHttpTestUtils.getAllConceptDescriptionsWithDataSpecRefParameterAccessPath(dataSpecificationRef));
		
		return BaSyxHttpTestUtils.getResponseAsString(response);
	}
	
	private CloseableHttpResponse requestWithTwoParameters(String idShort, String dataSpecRef) throws IOException {
		return BaSyxHttpTestUtils.executeGetOnURL(BaSyxConceptDescriptionHttpTestUtils.getAllConceptDescriptionsWithTwoParametersAccessPath(idShort, dataSpecRef));
	}
	
	private CloseableHttpResponse requestWithAllParameters(String idShort, String isCaseOf, String dataSpecRef) throws IOException {
		return BaSyxHttpTestUtils.executeGetOnURL(BaSyxConceptDescriptionHttpTestUtils.getAllConceptDescriptionsWithAllParametersAccessPath(idShort, isCaseOf, dataSpecRef));
	}

	private String getUpdatedConceptDescriptionJSON() throws IOException {
		return BaSyxHttpTestUtils.readJSONStringFromFile("classpath:SingleConceptDescriptionUpdate.json");
	}

	private String getNewConceptDescriptionJSON() throws IOException {
		return BaSyxHttpTestUtils.readJSONStringFromFile("classpath:SingleConceptDescriptionNew.json");
	}

	private String getSingleConceptDescriptionJSON() throws IOException {
		return BaSyxHttpTestUtils.readJSONStringFromFile("classpath:SingleConceptDescription.json");
	}

	private String getAllConceptDescriptionJSON() throws IOException {
		return BaSyxHttpTestUtils.readJSONStringFromFile("classpath:MultipleConceptDescriptions.json");
	}
	
	private String getConceptDescriptionsWithIdShort() throws IOException {
		return BaSyxHttpTestUtils.readJSONStringFromFile("classpath:ConceptDescriptionsWithIdShort.json");
	}
	
	private String getConceptDescriptionsWithIsCaseOf() throws IOException {
		return BaSyxHttpTestUtils.readJSONStringFromFile("classpath:ConceptDescriptionWithIsCaseOf.json");
	}
	
	private String getConceptDescriptionsWithDataSpecRef() throws IOException {
		return BaSyxHttpTestUtils.readJSONStringFromFile("classpath:ConceptDescriptionWithDataSpec.json");
	}
	
	private String getReferenceJSON() throws IOException {
		return BaSyxHttpTestUtils.readJSONStringFromFile("classpath:Reference.json");
	}
	
	private String getDataSpecReferenceJSON() throws IOException {
		return BaSyxHttpTestUtils.readJSONStringFromFile("classpath:DataSpecificationReference.json");
	}

}

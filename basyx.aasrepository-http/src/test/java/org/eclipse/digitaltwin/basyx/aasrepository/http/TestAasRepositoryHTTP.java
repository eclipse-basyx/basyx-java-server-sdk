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

	private ConfigurableApplicationContext appContext;

	@Before
	public void startAASRepo() throws Exception {
		appContext = new SpringApplication(DummyAasRepositoryComponent.class).run(new String[] {});
	}

	@After
	public void shutdownAASRepo() {
		appContext.close();
	}

	@Test
	public void aasUpload() throws IOException, ParseException {
		String aasJsonContent = getAASJSONString();

		CloseableHttpResponse creationResponse = createAASOnServer(aasJsonContent);

		assertEquals(201, creationResponse.getCode());
		BaSyxHttpTestUtils.assertSameJSONContent(aasJsonContent, BaSyxHttpTestUtils.getResponseAsString(creationResponse));
	}

	@Test
	public void aasRoundtrip() throws JsonMappingException, ParseException, JsonProcessingException, IOException {
		String aasJsonContent = getAASJSONString();

		createAASOnServer(aasJsonContent);

		CloseableHttpResponse retrievalResponse = getSpecificAas("customIdentifier");
		BaSyxHttpTestUtils.assertSameJSONContent(aasJsonContent, BaSyxHttpTestUtils.getResponseAsString(retrievalResponse));
	}

	@Test
	public void aasIdentifierCollision() throws FileNotFoundException, IOException {
		String aasJsonContent = getAASJSONString();

		createAASOnServer(aasJsonContent);
		CloseableHttpResponse creationResponse = createAASOnServer(aasJsonContent);

		assertEquals(400, creationResponse.getCode());
	}

	@Test
	public void getAASByIdentifier() throws FileNotFoundException, IOException, ParseException {
		String aasJsonContent = getAASJSONString();

		createAASOnServer(aasJsonContent);

		CloseableHttpResponse response = getSpecificAas("customIdentifier");

		BaSyxHttpTestUtils.assertSameJSONContent(aasJsonContent, BaSyxHttpTestUtils.getResponseAsString(response));
	}

	@Test
	public void getNonExistingAASByIdentifier() throws IOException {
		CloseableHttpResponse response = getSpecificAas("nonExisting");

		assertEquals(404, response.getCode());
	}

	@Test
	public void getAllAasWhenEmpty() throws IOException {
		CloseableHttpResponse retrievalResponse = getAllAas();

		assertEquals(HttpStatus.OK.value(), retrievalResponse.getCode());
	}

	private CloseableHttpResponse getAllAas() throws IOException {
		return BaSyxHttpTestUtils.executeGetOnURL(aasAccessURL);
	}

	private CloseableHttpResponse getSpecificAas(String aasId) throws IOException {
		String getUrl = aasAccessURL + "/" + Base64UrlEncodedIdentifier.encodeIdentifier(aasId);
		return BaSyxHttpTestUtils.executeGetOnURL(getUrl);
	}

	private CloseableHttpResponse createAASOnServer(String aasJsonContent) throws IOException {
		return BaSyxHttpTestUtils.executePostOnServer(aasAccessURL, aasJsonContent);
	}

	private String getAASJSONString() throws FileNotFoundException, IOException {
		return BaSyxHttpTestUtils.readJSONStringFromFile("classpath:AASSimple.json");
	}
}

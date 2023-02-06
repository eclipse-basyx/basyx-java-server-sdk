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

	private String createDummyAasOnServer() throws FileNotFoundException, IOException {
		String aasJsonContent = getAasJSONString();
		createAasOnServer(aasJsonContent);
		return aasJsonContent;
	}

	@Test
	public void aasIdentifierCollision() throws FileNotFoundException, IOException {
		String aasJsonContent = createDummyAasOnServer();

		CloseableHttpResponse creationResponse = createAasOnServer(aasJsonContent);

		assertEquals(400, creationResponse.getCode());
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

		assertEquals(404, response.getCode());
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
}

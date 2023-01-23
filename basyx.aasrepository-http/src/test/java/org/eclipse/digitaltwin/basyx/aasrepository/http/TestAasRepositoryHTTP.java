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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.ResourceUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;



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
		CloseableHttpClient client = HttpClients.createDefault();
		String aasJsonContent = getAASJSONString();

		CloseableHttpResponse creationResponse = createAASOnServer(client, aasJsonContent);

		assertEquals(201, creationResponse.getCode());
		assertResponseContainsSameAAS(aasJsonContent, creationResponse);
	}

	@Test
	public void aasRoundtrip() throws JsonMappingException, ParseException, JsonProcessingException, IOException {
		CloseableHttpClient client = HttpClients.createDefault();
		String aasJsonContent = getAASJSONString();

		createAASOnServer(client, aasJsonContent);

		HttpGet aasRetrievalRequest = createSpecificAASGETRequest();
		CloseableHttpResponse retrievalResponse = client.execute(aasRetrievalRequest);
		assertResponseContainsSameAAS(aasJsonContent, retrievalResponse);
	}

	private HttpGet createSpecificAASGETRequest() {
		return new HttpGet(aasAccessURL + "/customIdentifier");
	}

	@Test
	public void aasIdentifierCollision() throws FileNotFoundException, IOException {
		CloseableHttpClient client = HttpClients.createDefault();
		String aasJsonContent = getAASJSONString();

		createAASOnServer(client, aasJsonContent);
		CloseableHttpResponse creationResponse = createAASOnServer(client, aasJsonContent);

		assertEquals(400, creationResponse.getCode());
	}

	@Test
	public void getAASByIdentifier() throws FileNotFoundException, IOException, ParseException {
		CloseableHttpClient client = HttpClients.createDefault();
		String aasJsonContent = getAASJSONString();

		createAASOnServer(client, aasJsonContent);

		HttpGet aasRetrievalRequest = createSpecificAASGETRequest();
		CloseableHttpResponse response = client.execute(aasRetrievalRequest);

		assertResponseContainsSameAAS(aasJsonContent, response);
	}

	@Test
	public void getNonExistingAASByIdentifier() throws IOException {
		CloseableHttpClient client = HttpClients.createDefault();

		String specificAccessURL = aasAccessURL + "/nonExisting";
		HttpGet aasRetrievalRequest = new HttpGet(specificAccessURL);
		CloseableHttpResponse response = client.execute(aasRetrievalRequest);

		assertEquals(404, response.getCode());
	}

	private CloseableHttpResponse createAASOnServer(CloseableHttpClient client, String aasJsonContent) throws IOException {
		HttpPost aasCreateRequest = createValidAASCreationRequest(aasJsonContent);
		CloseableHttpResponse creationResponse = client.execute(aasCreateRequest);
		return creationResponse;
	}

	private String getResponseAsString(CloseableHttpResponse retrievalResponse) throws IOException, ParseException {
		return EntityUtils.toString(retrievalResponse.getEntity(), "UTF-8");
	}

	private void assertResponseContainsSameAAS(String aasJsonContent, CloseableHttpResponse response) throws IOException, ParseException, JsonProcessingException, JsonMappingException {
		String aasResponseJSON = getResponseAsString(response);

		ObjectMapper mapper = new ObjectMapper();
		assertEquals(mapper.readTree(aasJsonContent), mapper.readTree(aasResponseJSON));
	}

	private HttpPost createValidAASCreationRequest(String aasJsonContent) {
		HttpPost aasCreateRequest = createRequestWithHeader();
		StringEntity aasEntity = new StringEntity(aasJsonContent);
		aasCreateRequest.setEntity(aasEntity);
		return aasCreateRequest;
	}

	private HttpPost createRequestWithHeader() {
		HttpPost aasCreateRequest = new HttpPost(aasAccessURL);
		aasCreateRequest.setHeader("Content-type", "application/json");
		aasCreateRequest.setHeader("Accept", "application/json");
		return aasCreateRequest;
	}

	private String getAASJSONString() throws FileNotFoundException, IOException {
		File file = ResourceUtils.getFile("classpath:AASSimple.json");
		InputStream in = new FileInputStream(file);
		return IOUtils.toString(in, StandardCharsets.UTF_8.name());
	}
}

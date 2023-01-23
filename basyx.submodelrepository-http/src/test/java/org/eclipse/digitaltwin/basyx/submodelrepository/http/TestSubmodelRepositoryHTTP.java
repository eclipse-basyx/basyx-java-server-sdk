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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.eclipse.digitaltwin.basyx.submodelrepository.core.DummySubmodelFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.util.ResourceUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TestSubmodelRepositoryHTTP {

	private String submodelAccessURL = "http://localhost:8080/submodels";

	private ConfigurableApplicationContext appContext;

	@Before
	public void startAASRepo() throws Exception {
		appContext = new SpringApplication(DummySubmodelRepositoryComponent.class).run(new String[] {});
	}

	@After
	public void shutdownAASRepo() {
		appContext.close();
	}

	@Test
	public void getAllSubmodelsPreconfigured() throws IOException, ParseException {
		String submodelsJSON = getAllSubmodelsJSON();
		String expectedSubmodelsJSON = getAllSubmodelJSONExpected();
		assertSameJSONContent(expectedSubmodelsJSON, submodelsJSON);
	}

	@Test
	public void getSpecificSubmodel() throws ParseException, IOException {
		String submodelJSON = getSpecificSubmodelJSON(DummySubmodelFactory.createTechnicalDataSubmodel().getId());
		String expectedSubmodelJSON = getSpecificSubmodelJSONExpected();

		assertSameJSONContent(expectedSubmodelJSON, submodelJSON);
	}

	@Test
	public void getSpecificSubmodelNonExisting() throws IOException {
		CloseableHttpResponse response = executeGetOnURL(submodelAccessURL + "/nonExisting/submodel");

		assertEquals(HttpStatus.NOT_FOUND.value(), response.getCode());
	}

	private String getSpecificSubmodelJSON(String id) throws IOException, ParseException {
		CloseableHttpResponse response = executeGetOnURL(submodelAccessURL + "/" + id + "/submodel");

		return getResponseAsString(response);
	}

	private CloseableHttpResponse executeGetOnURL(String url) throws IOException {
		CloseableHttpClient client = HttpClients.createDefault();
		HttpGet submodelRetrievalRequest = new HttpGet(url);
		return client.execute(submodelRetrievalRequest);
	}

	private void assertSameJSONContent(String expected, String actual) throws JsonProcessingException, JsonMappingException {
		ObjectMapper mapper = new ObjectMapper();

		assertEquals(mapper.readTree(expected), mapper.readTree(actual));
	}

	private String getAllSubmodelsJSON() throws IOException, ParseException {
		CloseableHttpResponse response = executeGetOnURL(submodelAccessURL);

		return getResponseAsString(response);
	}

	private String getSpecificSubmodelJSONExpected() throws IOException {
		return readJSONStringFromFile("classpath:SingleSubmodel.json");
	}

	private String getAllSubmodelJSONExpected() throws IOException {
		return readJSONStringFromFile("classpath:MultipleSubmodels.json");
	}

	private String readJSONStringFromFile(String fileName) throws FileNotFoundException, IOException {
		File file = ResourceUtils.getFile(fileName);
		InputStream in = new FileInputStream(file);
		return IOUtils.toString(in, StandardCharsets.UTF_8.name());
	}

	private String getResponseAsString(CloseableHttpResponse retrievalResponse) throws IOException, ParseException {
		return EntityUtils.toString(retrievalResponse.getEntity(), "UTF-8");
	}

}

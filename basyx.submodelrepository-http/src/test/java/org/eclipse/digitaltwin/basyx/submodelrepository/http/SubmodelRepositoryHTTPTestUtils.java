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
import org.apache.hc.client5.http.classic.methods.HttpPut;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.springframework.util.ResourceUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Helper class for the tests working with the SubmodelRepository's HTTP/REST
 * API
 * 
 * @author schnicke
 *
 */
public class SubmodelRepositoryHTTPTestUtils {

	/**
	 * Reads the JSON String from a JSON file
	 * 
	 * @param fileName
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static String readJSONStringFromFile(String fileName) throws FileNotFoundException, IOException {
		File file = ResourceUtils.getFile(fileName);
		InputStream in = new FileInputStream(file);
		return IOUtils.toString(in, StandardCharsets.UTF_8.name());
	}

	/**
	 * Retrieves the String content of the HttpResponse
	 * 
	 * @param retrievalResponse
	 * @return
	 * @throws IOException
	 * @throws ParseException
	 */
	public static String getResponseAsString(CloseableHttpResponse retrievalResponse) throws IOException, ParseException {
		return EntityUtils.toString(retrievalResponse.getEntity(), "UTF-8");
	}

	/**
	 * Asserts that two JSON strings are semantically equivalent
	 * 
	 * @param expected
	 * @param actual
	 * @throws JsonProcessingException
	 * @throws JsonMappingException
	 */
	public static void assertSameJSONContent(String expected, String actual) throws JsonProcessingException, JsonMappingException {
		ObjectMapper mapper = new ObjectMapper();

		assertEquals(mapper.readTree(expected), mapper.readTree(actual));
	}

	/**
	 * Performs a get request on the passed URL
	 * 
	 * @param url
	 * @return
	 * @throws IOException
	 */
	public static CloseableHttpResponse executeGetOnURL(String url) throws IOException {
		CloseableHttpClient client = HttpClients.createDefault();
		HttpGet getRequest = new HttpGet(url);
		return client.execute(getRequest);
	}

	/**
	 * Performs a set request on the passed URL with the passed value
	 * 
	 * @param url
	 * @param value
	 * @return
	 * @throws IOException
	 */
	public static CloseableHttpResponse executeSetOnURL(String url, String value) throws IOException {
		CloseableHttpClient client = HttpClients.createDefault();
		HttpPut putRequest = new HttpPut(url);
		
		putRequest.setEntity(new StringEntity("\"" + value + "\""));
		putRequest.setHeader("Content-type", "application/json");

		return client.execute(putRequest);
	}
}

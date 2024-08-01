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

package org.eclipse.digitaltwin.basyx.http.serialization;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.hc.client5.http.classic.methods.HttpDelete;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPatch;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpPut;
import org.apache.hc.client5.http.entity.mime.FileBody;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.entity.mime.StringBody;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.eclipse.digitaltwin.basyx.http.Base64UrlEncodedIdentifier;
import org.springframework.core.io.ClassPathResource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Supports the tests working with the HTTP/REST API of AAS, Submodels, etc.
 * 
 * @author schnicke
 *
 */
public class BaSyxHttpTestUtils {

	private static final String CURSOR = "cursor";
	private static final String PAGING_METADATA_KEY = "paging_metadata";

	/**
	 * Reads the JSON String from a JSON file in the classpath
	 * 
	 * @param fileName
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static String readJSONStringFromClasspath(String fileName) throws FileNotFoundException, IOException {
		ClassPathResource classPathResource = new ClassPathResource(fileName);
		InputStream in = classPathResource.getInputStream();
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
		HttpGet getRequest = createGetRequestWithHeader(url);
		return client.execute(getRequest);
	}
	
	/**
	 * Performs a get request on the passed URL
	 * 
	 * @param url
	 * @param header
	 * @return
	 * @throws IOException
	 */
	public static CloseableHttpResponse executeGetOnURL(String url, Header header) throws IOException {
		CloseableHttpClient client = HttpClients.createDefault();
		HttpGet getRequest = createGetRequestWithHeader(url);
		getRequest.setHeader(header);
		
		return client.execute(getRequest);
	}
	
	/**
	 * Performs a get request on secured endpoint
	 * 
	 * @param url
	 * @param accessToken
	 * @return
	 * @throws IOException
	 */
	public static CloseableHttpResponse executeAuthorizedGetOnURL(String url, String accessToken) throws IOException {
		CloseableHttpClient client = HttpClients.createDefault();
		HttpGet getRequest = createGetRequestWithAuthorizationHeader(url, accessToken);
		return client.execute(getRequest);
	}
	
	/**
	 * Performs a get request on secured endpoint
	 * 
	 * @param url
	 * @param accessToken
	 * @param header
	 * @return
	 * @throws IOException
	 */
	public static CloseableHttpResponse executeAuthorizedGetOnURL(String url, String accessToken, Header header) throws IOException {
		CloseableHttpClient client = HttpClients.createDefault();
		HttpGet getRequest = createGetRequestWithAuthorizationHeader(url, accessToken);
		getRequest.setHeader(header);
		
		return client.execute(getRequest);
	}

	/**
	 * Performs a delete request on the passed URL
	 * 
	 * @param url
	 * @return
	 * @throws IOException
	 */
	public static CloseableHttpResponse executeDeleteOnURL(String url) throws IOException {
		CloseableHttpClient client = HttpClients.createDefault();
		HttpDelete deleteRequest = createDeleteRequestWithHeader(url);
		return client.execute(deleteRequest);
	}
	
	public static CloseableHttpResponse executeAuthorizedDeleteOnURL(String url, String accessToken) throws IOException {
		CloseableHttpClient client = HttpClients.createDefault();
		HttpDelete deleteRequest = createDeleteRequestWithAuthorizationHeader(url, accessToken);
		return client.execute(deleteRequest);
	}

	/**
	 * Performs a set request on the passed URL with the passed content
	 * 
	 * @param url
	 * @param content
	 * @return
	 * @throws IOException
	 */
	public static CloseableHttpResponse executePutOnURL(String url, String content) throws IOException {
		CloseableHttpClient client = HttpClients.createDefault();
		HttpPut putRequest = createPutRequestWithHeader(url, content);

		return client.execute(putRequest);
	}

	public static CloseableHttpResponse executeAuthorizedPutOnURL(String url, String content, String accessToken) throws IOException {
		CloseableHttpClient client = HttpClients.createDefault();
		HttpPut putRequest = createPutRequestWithAuthorizationHeader(url, content, accessToken);
		
		return client.execute(putRequest);
	}

	/**
	 * Performs a post request on the passed URL with the passed content
	 * 
	 * @param url
	 * @param content
	 * @return
	 * @throws IOException
	 */
	public static CloseableHttpResponse executePostOnURL(String url, String content) throws IOException {
		CloseableHttpClient client = HttpClients.createDefault();
		HttpPost aasCreateRequest = createPostRequest(url, content);

		return client.execute(aasCreateRequest);
	}
	
	public static CloseableHttpResponse executeAuthorizedPostOnURL(String url, String content, String accessToken) throws IOException {
		CloseableHttpClient client = HttpClients.createDefault();
		HttpPost aasCreateRequest = createAuthorizedPostRequest(url, content, accessToken);
		
		return client.execute(aasCreateRequest);
	}

	/**
	 * Performs a patch request on the passed URL with the passed content
	 * 
	 * @param url
	 * @param content
	 * @return
	 * @throws IOException
	 */
	public static CloseableHttpResponse executePatchOnURL(String url, String content) throws IOException {
		CloseableHttpClient client = HttpClients.createDefault();
		HttpPatch patchRequest = createPatchRequestWithHeader(url, content);

		return client.execute(patchRequest);
	}
	
	public static CloseableHttpResponse executeAuthorizedPatchOnURL(String url, String content, String accessToken) throws IOException {
		CloseableHttpClient client = HttpClients.createDefault();
		HttpPatch patchRequest = createPatchRequestWithAuthorizationHeader(url, content, accessToken);
		
		return client.execute(patchRequest);
	}
	
	/**
	 * Removes cursor node from the paging_metadata provided in the JSON
	 * 
	 * @param inputJSON
	 * @return
	 * 
	 * @throws JsonMappingException
	 * @throws JsonProcessingException
	 */
	public static String removeCursorFromJSON(String inputJSON) throws JsonMappingException, JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();

        JsonNode rootNode = mapper.readTree(inputJSON);

        if (rootNode.has(PAGING_METADATA_KEY)) {
            ObjectNode pagingMetadata = (ObjectNode) rootNode.get(PAGING_METADATA_KEY);
            
            if (pagingMetadata.has(CURSOR))
            	pagingMetadata.remove(CURSOR);
        }
        
        return mapper.writeValueAsString(rootNode);
	}

	public static CloseableHttpResponse executePutRequest(CloseableHttpClient client, HttpPut putRequest) throws IOException {
		CloseableHttpResponse response = client.execute(putRequest);

		HttpEntity responseEntity = response.getEntity();

		EntityUtils.consume(responseEntity);
		return response;
	}

	public static CloseableHttpResponse executePostRequest(CloseableHttpClient client, HttpPost postRequest) throws IOException {
		CloseableHttpResponse response = client.execute(postRequest);

		HttpEntity responseEntity = response.getEntity();

		EntityUtils.consume(responseEntity);
		return response;
	}

	public static HttpPut createPutRequestWithFile(String url, String fileName, java.io.File file) {
		HttpPut putRequest = new HttpPut(url);

		MultipartEntityBuilder builder = MultipartEntityBuilder.create();

		builder.addPart("file", new FileBody(file));
		builder.addTextBody("fileName", fileName);
		builder.setContentType(ContentType.MULTIPART_FORM_DATA);

		HttpEntity multipart = builder.build();
		putRequest.setEntity(multipart);
		return putRequest;
	}
	
	public static HttpPut createPutRequestWithFileWithAuthorization(String url, String fileName, java.io.File file, String accessToken) {
		HttpPut putRequest = new HttpPut(url);
		
		putRequest.setHeader("Authorization", "Bearer " + accessToken);
		
		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		
		builder.addPart("file", new FileBody(file));
		builder.addTextBody("fileName", fileName);
		builder.setContentType(ContentType.MULTIPART_FORM_DATA);
		
		HttpEntity multipart = builder.build();
		putRequest.setEntity(multipart);
		return putRequest;
	}

	public static String getThumbnailAccessURL(String url, String aasId) {
		return url + "/" + Base64UrlEncodedIdentifier.encodeIdentifier(aasId) + "/asset-information/thumbnail";
	}

	private static HttpPatch createPatchRequestWithHeader(String url, String content) {
		HttpPatch patchRequest = new HttpPatch(url);

		patchRequest.setHeader("Content-type", "application/json");
		patchRequest.setEntity(new StringEntity(content));

		return patchRequest;
	}
	
	private static HttpPatch createPatchRequestWithAuthorizationHeader(String url, String content, String accessToken) {
		HttpPatch patchRequest = new HttpPatch(url);
		
		patchRequest.setHeader("Content-type", "application/json");
		patchRequest.setHeader("Authorization", "Bearer " + accessToken);
		patchRequest.setEntity(new StringEntity(content));
		
		return patchRequest;
	}

	private static HttpPut createPutRequestWithHeader(String url, String content) {
		HttpPut putRequest = new HttpPut(url);

		putRequest.setHeader("Content-type", "application/json");
		putRequest.setEntity(new StringEntity(content));

		return putRequest;
	}
	
	private static HttpPut createPutRequestWithAuthorizationHeader(String url, String content, String accessToken) {
		HttpPut putRequest = new HttpPut(url);
		
		putRequest.setHeader("Content-type", "application/json");
		putRequest.setHeader("Authorization", "Bearer " + accessToken);
		putRequest.setEntity(new StringEntity(content));
		
		return putRequest;
	}

	private static HttpPost createPostRequest(String url, String content) {
		HttpPost aasCreateRequest = createPostRequestWithHeader(url);

		StringEntity aasEntity = new StringEntity(content);
		aasCreateRequest.setEntity(aasEntity);

		return aasCreateRequest;
	}

	public static HttpPost createPostRequestWithFile(String url, java.io.File file, String contentType) {
		HttpPost postRequest = new HttpPost(url);

		MultipartEntityBuilder builder = MultipartEntityBuilder.create();

		builder.addPart("file", new FileBody(file, ContentType.create(contentType)));
		builder.setContentType(ContentType.MULTIPART_FORM_DATA);

		HttpEntity multipart = builder.build();
		postRequest.setEntity(multipart);
		return postRequest;
	}


	public static HttpPost createPostRequestWithFileForFileServer(String url, List<String> aasIds, java.io.File file, String fileName) {
		HttpPost postRequest = new HttpPost(url);

		MultipartEntityBuilder builder = getMultipartEntityBuilderWithAASXFile(aasIds, file, fileName);

		HttpEntity multipart = builder.build();
		postRequest.setEntity(multipart);
		return postRequest;
	}

	public static HttpPut updatePutRequestWithFileForFileServer(String url, String packageId, List<String> aasIds, java.io.File file, String fileName) {
		HttpPut putRequest = new HttpPut(url + "/" + packageId);

		MultipartEntityBuilder builder = getMultipartEntityBuilderWithAASXFile(aasIds, file, fileName);

		HttpEntity multipart = builder.build();
		putRequest.setEntity(multipart);
		return putRequest;
	}

	public static HttpPost createAuthorizedPostRequestWithFile(String url, java.io.File file, String contentType, String accessToken) {
		HttpPost postRequest = new HttpPost(url);
		postRequest.setHeader("Authorization", "Bearer " + accessToken);
		
		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		
		builder.addPart("file", new FileBody(file, ContentType.create(contentType)));
		builder.setContentType(ContentType.MULTIPART_FORM_DATA);
		
		HttpEntity multipart = builder.build();
		postRequest.setEntity(multipart);
		return postRequest;
	}
	
	private static HttpPost createAuthorizedPostRequest(String url, String content, String accessToken) {
		HttpPost aasCreateRequest = createPostRequestWithAuthorizationHeader(url, accessToken);
		
		StringEntity aasEntity = new StringEntity(content);
		aasCreateRequest.setEntity(aasEntity);
		
		return aasCreateRequest;
	}

	private static HttpPost createPostRequestWithHeader(String url) {
		HttpPost aasCreateRequest = new HttpPost(url);
		aasCreateRequest.setHeader("Content-type", "application/json");
		aasCreateRequest.setHeader("Accept", "application/json");
		return aasCreateRequest;
	}
	
	private static HttpPost createPostRequestWithAuthorizationHeader(String url, String accessToken) {
		HttpPost aasCreateRequest = new HttpPost(url);
		aasCreateRequest.setHeader("Content-type", "application/json");
		aasCreateRequest.setHeader("Authorization", "Bearer " + accessToken);
		aasCreateRequest.setHeader("Accept", "application/json");
		return aasCreateRequest;
	}

	private static HttpGet createGetRequestWithHeader(String url) {
		HttpGet aasCreateRequest = new HttpGet(url);
		aasCreateRequest.setHeader("Content-type", "application/json");
		aasCreateRequest.setHeader("Accept", "application/json");
		return aasCreateRequest;
	}
	
	private static HttpGet createGetRequestWithAuthorizationHeader(String url, String accessToken) {
		HttpGet aasCreateRequest = new HttpGet(url);
		aasCreateRequest.setHeader("Content-type", "application/json");
		aasCreateRequest.setHeader("Authorization", "Bearer " + accessToken);
		aasCreateRequest.setHeader("Accept", "application/json");
		return aasCreateRequest;
	}

	private static HttpDelete createDeleteRequestWithHeader(String url) {
		HttpDelete deleteRequest = new HttpDelete(url);
		deleteRequest.setHeader("Content-type", "application/json");
		return deleteRequest;
	}
	
	private static HttpDelete createDeleteRequestWithAuthorizationHeader(String url, String accessToken) {
		HttpDelete deleteRequest = new HttpDelete(url);
		deleteRequest.setHeader("Content-type", "application/json");
		deleteRequest.setHeader("Authorization", "Bearer " + accessToken);
		return deleteRequest;
	}

	private static MultipartEntityBuilder getMultipartEntityBuilderWithAASXFile(List<String> aasIds, File file, String fileName) {
		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		builder.addTextBody("aasIds", String.join(",", aasIds), ContentType.TEXT_PLAIN);
		builder.addPart("file", new FileBody(file, ContentType.create("application/asset-administration-shell-package+xml")));
		builder.addTextBody("fileName", fileName, ContentType.TEXT_PLAIN);
		builder.setContentType(ContentType.MULTIPART_FORM_DATA);
		return builder;
	}
}

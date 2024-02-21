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

package org.eclipse.digitaltwin.basyx.aasdiscoveryservice.http;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.ProtocolException;
import org.eclipse.digitaltwin.basyx.aasdiscoveryservice.core.AasDiscoveryService;
import org.eclipse.digitaltwin.basyx.http.Base64UrlEncodedIdentifier;
import org.eclipse.digitaltwin.basyx.http.HttpBaSyxHeader;
import org.eclipse.digitaltwin.basyx.http.serialization.BaSyxHttpTestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * Tests the Aas Discovery specific parts of the {@link AasDiscoveryService}
 * HTTP/REST API
 * 
 * @author danish
 *
 */
public abstract class AasDiscoveryServiceHTTPSuite {
	protected abstract String getURL();

	@Before
	@After
	public abstract void resetService();
	
	@Test
	public void baSyxResponseHeader() throws IOException, ProtocolException {
		CloseableHttpResponse response = BaSyxHttpTestUtils.executeGetOnURL(getURL());
		assertEquals(HttpBaSyxHeader.HEADER_VALUE, response.getHeader(HttpBaSyxHeader.HEADER_KEY).getValue());
	}

	@Test
	public void getAllAssetAdministrationShellIdsByAssetLink() throws ParseException, IOException {
		String assetLinkJSON = getStringFromFile("NewAssetLinks.json");

		createAssetLinks("NewDummyShellId", assetLinkJSON);
		
		String expectedShellIds = getStringFromFile("AllShellIds.json");

		String actualShellIds = requestAllShellIds();

		BaSyxHttpTestUtils.assertSameJSONContent(expectedShellIds, getJSONWithoutCursorInfo(actualShellIds));
	}

	@Test
	public void getAllAssetLinks() throws IOException, ParseException {
		String expectedAssetLinks = getStringFromFile("AllAssetLinks.json");

		CloseableHttpResponse response = requestAllAssetLinks("TestAasID2");

		String actualShellIds = BaSyxHttpTestUtils.getResponseAsString(response);

		BaSyxHttpTestUtils.assertSameJSONContent(expectedAssetLinks, actualShellIds);
	}

	@Test
	public void getAllAssetLinksWithNonExistingShellId() throws IOException, ParseException {
		CloseableHttpResponse response = requestAllAssetLinks("nonExisting");

		assertEquals(HttpStatus.NOT_FOUND.value(), response.getCode());
	}

	@Test
	public void createAllAssetLinks() throws IOException, ParseException {
		String assetLinkJSON = getStringFromFile("NewAssetLinks.json");

		CloseableHttpResponse creationResponse = createAssetLinks("NewDummyShellId", assetLinkJSON);

		assertAssetLinkCreationReponse(assetLinkJSON, creationResponse);

		CloseableHttpResponse retrievalResponse = requestAllAssetLinks("NewDummyShellId");
		String actualAssetLinks = BaSyxHttpTestUtils.getResponseAsString(retrievalResponse);

		BaSyxHttpTestUtils.assertSameJSONContent(assetLinkJSON, actualAssetLinks);

		removeNewlyAddedAssetLinks("NewDummyShellId");
	}

	@Test
	public void createAssetLinksCollidingId() throws IOException, ParseException {
		String assetLinkJSON = getStringFromFile("NewAssetLinks.json");
		CloseableHttpResponse creationResponse = createAssetLinks("TestAasID2", assetLinkJSON);

		assertEquals(HttpStatus.CONFLICT.value(), creationResponse.getCode());
	}

	@Test
	public void deleteAssetLinks() throws IOException, ParseException {
		String existingShellId = "TestAasID1";

		CloseableHttpResponse deletionResponse = removeAssetLink(existingShellId);
		assertEquals(HttpStatus.NO_CONTENT.value(), deletionResponse.getCode());

		CloseableHttpResponse getResponse = requestAllAssetLinks(existingShellId);
		assertEquals(HttpStatus.NOT_FOUND.value(), getResponse.getCode());
	}

	@Test
	public void deleteNonExistingAssetLinks() throws IOException {
		CloseableHttpResponse deletionResponse = removeAssetLink("nonExisting");

		assertEquals(HttpStatus.NOT_FOUND.value(), deletionResponse.getCode());
	}

	protected CloseableHttpResponse createAssetLinks(String shellId, String assetLinksJSON) throws IOException, ParseException {
		return BaSyxHttpTestUtils.executePostOnURL(getURL() + "/" + Base64UrlEncodedIdentifier.encodeIdentifier(shellId), assetLinksJSON);
	}

	protected CloseableHttpResponse deleteAssetLinkById(String shellId) throws IOException {
		return removeAssetLink(shellId);
	}

	protected CloseableHttpResponse requestAllAssetLinks(String shellIdentifier) throws IOException, ParseException {
		return BaSyxHttpTestUtils.executeGetOnURL(getURL() + "/" + Base64UrlEncodedIdentifier.encodeIdentifier(shellIdentifier));
	}
	
	protected String requestAllShellIds() throws IOException, ParseException {
		CloseableHttpResponse response = BaSyxHttpTestUtils.executeGetOnURL(getURL() + "?assetIds=ewoJCSJuYW1lIjogIkR1bW15QXNzZXROYW1lMTAwIiwKCQkidmFsdWUiOiAiRHVtbXlBc3NldF8xMDBfVmFsdWUiCn0=,ewoJCSJuYW1lIjogIkR1bW15QXNzZXROYW1lMTM1IiwKCQkidmFsdWUiOiAiRHVtbXlBc3NldF8xMzVfVmFsdWUiCn0=");

		return BaSyxHttpTestUtils.getResponseAsString(response);
	}

	private String getStringFromFile(String filePath) throws IOException {
		return BaSyxHttpTestUtils.readJSONStringFromClasspath(filePath);
	}

	private void removeNewlyAddedAssetLinks(String shellId) throws IOException {
		CloseableHttpResponse deletionResponse = removeAssetLink(shellId);

		assertEquals(HttpStatus.NO_CONTENT.value(), deletionResponse.getCode());
	}

	private CloseableHttpResponse removeAssetLink(String shellId) throws IOException {
		return BaSyxHttpTestUtils.executeDeleteOnURL(getURL() + "/" + Base64UrlEncodedIdentifier.encodeIdentifier(shellId));
	}

	private void assertAssetLinkCreationReponse(String submodelJSON, CloseableHttpResponse creationResponse) throws IOException, ParseException, JsonProcessingException, JsonMappingException {
		assertEquals(HttpStatus.CREATED.value(), creationResponse.getCode());

		String response = BaSyxHttpTestUtils.getResponseAsString(creationResponse);

		BaSyxHttpTestUtils.assertSameJSONContent(submodelJSON, response);
	}
	
	private String getJSONWithoutCursorInfo(String response) throws JsonMappingException, JsonProcessingException {
		return BaSyxHttpTestUtils.removeCursorFromJSON(response);
	}

}

/*******************************************************************************
 * Copyright (C) 2024 the Eclipse BaSyx Authors
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

package org.eclipse.digitaltwin.basyx.aasdiscoveryservice.feature.authorization;

import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.ParseException;
import org.eclipse.digitaltwin.aas4j.v3.model.SpecificAssetId;
import org.eclipse.digitaltwin.basyx.aasdiscoveryservice.core.AasDiscoveryService;
import org.eclipse.digitaltwin.basyx.authorization.AccessTokenProvider;
import org.eclipse.digitaltwin.basyx.authorization.DummyCredential;
import org.eclipse.digitaltwin.basyx.authorization.DummyCredentialStore;
import org.eclipse.digitaltwin.basyx.http.Base64UrlEncoder;
import org.eclipse.digitaltwin.basyx.http.serialization.BaSyxHttpTestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Test Suite for {@link AuthorizedAasDiscoveryService} feature
 * 
 * @author mateusmolina
 */
public abstract class AuthorizedAasDiscoveryServiceSuite {

	private static final DummyCredential INSUFFICIENT_CREDENTIAL = DummyCredentialStore.BASYX_UPDATER_CREDENTIAL;
	private static String authenticaltionServerTokenEndpoint = "http://localhost:9096/realms/BaSyx/protocol/openid-connect/token";
	private static final String SERVER_URL = "http://localhost:8081";
	private static String clientId = "basyx-client-api";

	private static AccessTokenProvider tokenProvider;

	private static final String AASID1 = "TestAasID1";
	private static final String AASID2 = "TestAasID2";
	private static final String AASID1_ENCODED = Base64UrlEncoder.encode(AASID1);
	private static final String AASID2_ENCODED = Base64UrlEncoder.encode(AASID2);
	private static final String ASSETLINKS_SET1_PATH = "authorization/AssetLinks_Set1.json";
	private static final String ASSETLINKS_SET2_PATH = "authorization/AssetLinks_Set2.json";
	private static final String HEALTHOUTPUT_PATH = "authorization/HealthOutput.json";

	protected abstract ConfigurableApplicationContext getAppContext();
	protected abstract AasDiscoveryService getUnauthDiscoveryService();

	@BeforeClass
	public static void setUpTokenProvider() throws FileNotFoundException, IOException {
		tokenProvider = new AccessTokenProvider(authenticaltionServerTokenEndpoint, clientId);
	}

	@Before
	public void initializeRepositories() throws Exception {
		String expectedAssetIds = getStringFromFile(ASSETLINKS_SET1_PATH);
		getUnauthDiscoveryService().createAllAssetLinksById(AASID1, createSpecificAssetIdsFromJson(getAppContext().getBean(ObjectMapper.class), expectedAssetIds));
	}

	@After
	public void reset() throws Exception {
		try {
			getUnauthDiscoveryService().deleteAllAssetLinksById(AASID1);
			getUnauthDiscoveryService().deleteAllAssetLinksById(AASID2);
		} catch (Exception e) {
		}
	}

	@Test
	public void healthEndpointWithoutAuthorization() throws IOException, ParseException {
		String expectedHealthEndpointOutput = getStringFromFile(HEALTHOUTPUT_PATH);

		CloseableHttpResponse healthCheckResponse = BaSyxHttpTestUtils.executeGetOnURL(Endpoints.healthEndpoint());
		assertEquals(HttpStatus.OK.value(), healthCheckResponse.getCode());

		BaSyxHttpTestUtils.assertSameJSONContent(expectedHealthEndpointOutput, BaSyxHttpTestUtils.getResponseAsString(healthCheckResponse));
	}

	@Test
	public void createAllAssetLinksById_withCorrectRoleAndPermission() throws IOException {
		String accessToken = getToken(DummyCredentialStore.BASYX_ASSETID_CREATOR);

		String expectedAssetIds = getStringFromFile(ASSETLINKS_SET2_PATH);

		CloseableHttpResponse response = BaSyxHttpTestUtils.executeAuthorizedPostOnURL(Endpoints.postAllAssetLinksById(AASID2_ENCODED), expectedAssetIds, accessToken);

		assertEquals(HttpStatus.CREATED.value(), response.getCode());
	}

	@Test
	public void createAllAssetLinksById_withInsufficientPermissionRole() throws IOException {
		String accessToken = getToken(INSUFFICIENT_CREDENTIAL);

		String expectedAssetIds = getStringFromFile(ASSETLINKS_SET2_PATH);

		CloseableHttpResponse response = BaSyxHttpTestUtils.executeAuthorizedPostOnURL(Endpoints.postAllAssetLinksById(AASID2_ENCODED), expectedAssetIds, accessToken);

		assertEquals(HttpStatus.FORBIDDEN.value(), response.getCode());
	}

	@Test
	public void createAllAssetLinksById_withNoAuthorization() throws IOException {
		String expectedAssetIds = getStringFromFile(ASSETLINKS_SET2_PATH);

		CloseableHttpResponse response = BaSyxHttpTestUtils.executePostOnURL(Endpoints.postAllAssetLinksById(AASID2_ENCODED), expectedAssetIds);

		assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getCode());
	}

	@Test
	public void getAllAssetLinkByIdUrl_withCorrectRoleAndPermission() throws IOException {
		String accessToken = getToken(DummyCredentialStore.BASYX_ASSETID_DISCOVERER);

		CloseableHttpResponse response = BaSyxHttpTestUtils.executeAuthorizedGetOnURL(Endpoints.getAllAssetLinkById(AASID1_ENCODED), accessToken);

		assertEquals(HttpStatus.OK.value(), response.getCode());
	}

	@Test
	public void getAllAssetLinkByIdUrl_withInsufficientPermissionRole() throws IOException {
		String accessToken = getToken(INSUFFICIENT_CREDENTIAL);

		CloseableHttpResponse response = BaSyxHttpTestUtils.executeAuthorizedGetOnURL(Endpoints.getAllAssetLinkById(AASID1_ENCODED), accessToken);

		assertEquals(HttpStatus.FORBIDDEN.value(), response.getCode());
	}

	@Test
	public void getAllAssetLinkByIdUrl_withNoAuthorization() throws IOException {
		CloseableHttpResponse response = BaSyxHttpTestUtils.executeGetOnURL(Endpoints.getAllAssetLinkById(AASID1_ENCODED));

		assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getCode());
	}

	@Test
	public void getAllAasIdsByAssetLink_withCorrectRoleAndPermission() throws IOException {
		String accessToken = getToken(DummyCredentialStore.BASYX_AAS_DISCOVERER);

		CloseableHttpResponse response = BaSyxHttpTestUtils.executeAuthorizedGetOnURL(Endpoints.getAllAasIdsByAssetLink(), accessToken);

		assertEquals(HttpStatus.OK.value(), response.getCode());
	}

	@Test
	public void getAllAasIdsByAssetLink_withInsufficientPermissionRole() throws IOException {
		String accessToken = getToken(INSUFFICIENT_CREDENTIAL);

		CloseableHttpResponse response = BaSyxHttpTestUtils.executeAuthorizedGetOnURL(Endpoints.getAllAasIdsByAssetLink(), accessToken);

		assertEquals(HttpStatus.FORBIDDEN.value(), response.getCode());
	}
	
	@Test
	public void getAllAasIdsByAssetLink_withNoAuthorization() throws IOException {
		CloseableHttpResponse response = BaSyxHttpTestUtils.executeGetOnURL(Endpoints.getAllAasIdsByAssetLink());
		
		assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getCode());
	}

	@Test
	public void deleteAllAssetLinksById_withCorrectRoleAndPermission() throws IOException {
		String accessToken = getToken(DummyCredentialStore.BASYX_ASSETID_DELETER);

		CloseableHttpResponse response = BaSyxHttpTestUtils.executeAuthorizedDeleteOnURL(Endpoints.deleteAllAssetLinksById(AASID1_ENCODED), accessToken);

		assertEquals(HttpStatus.NO_CONTENT.value(), response.getCode());
	}

	@Test
	public void deleteAllAssetLinksById_withInsufficientPermissionRole() throws IOException {
		String accessToken = getToken(INSUFFICIENT_CREDENTIAL);

		CloseableHttpResponse response = BaSyxHttpTestUtils.executeAuthorizedDeleteOnURL(Endpoints.deleteAllAssetLinksById(AASID1_ENCODED), accessToken);

		assertEquals(HttpStatus.FORBIDDEN.value(), response.getCode());
	}

	@Test
	public void deleteAllAssetLinksById_withNoAuthorization() throws IOException {
		CloseableHttpResponse response = BaSyxHttpTestUtils.executeDeleteOnURL(Endpoints.deleteAllAssetLinksById(AASID1_ENCODED));

		assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getCode());
	}

	private static String getToken(DummyCredential credential) {
		return tokenProvider.getAccessToken(credential.getUsername(), credential.getPassword());
	}

	private static String getStringFromFile(String fileName) throws FileNotFoundException, IOException {
		return BaSyxHttpTestUtils.readJSONStringFromClasspath(fileName);
	}

	private static List<SpecificAssetId> createSpecificAssetIdsFromJson(ObjectMapper objectMapper, String json) throws JsonMappingException, JsonProcessingException {
		return objectMapper.readValue(json, new TypeReference<List<SpecificAssetId>>() {});
	}

	private static class Endpoints {

		private static String healthEndpoint() {
			return SERVER_URL + "/actuator/health";
		}

		private static String getAllAasIdsByAssetLink() {
			return SERVER_URL + "/lookup/shells";
		}

		private static String deleteAllAssetLinksById(String aasIdentifierBase64) {
			return SERVER_URL + "/lookup/shells/" + aasIdentifierBase64;
		}

		private static String getAllAssetLinkById(String aasIdentifierBase64) {
			return SERVER_URL + "/lookup/shells/" + aasIdentifierBase64;
		}

		private static String postAllAssetLinksById(String aasIdentifierBase64) {
			return SERVER_URL + "/lookup/shells/" + aasIdentifierBase64;
		}
	}

}

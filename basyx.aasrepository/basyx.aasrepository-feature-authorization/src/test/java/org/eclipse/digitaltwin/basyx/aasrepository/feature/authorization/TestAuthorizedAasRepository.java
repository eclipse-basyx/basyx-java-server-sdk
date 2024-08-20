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

package org.eclipse.digitaltwin.basyx.aasrepository.feature.authorization;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.interfaces.RSAPublicKey;

import org.apache.hc.client5.http.classic.methods.HttpPut;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ParseException;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetKind;
import org.eclipse.digitaltwin.aas4j.v3.model.KeyTypes;
import org.eclipse.digitaltwin.aas4j.v3.model.ReferenceTypes;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultAssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultAssetInformation;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultKey;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultReference;
import org.eclipse.digitaltwin.basyx.aasrepository.AasRepository;
import org.eclipse.digitaltwin.basyx.authorization.AccessTokenProvider;
import org.eclipse.digitaltwin.basyx.authorization.DummyCredential;
import org.eclipse.digitaltwin.basyx.authorization.DummyCredentialStore;
import org.eclipse.digitaltwin.basyx.authorization.jwt.JwtTokenDecoder;
import org.eclipse.digitaltwin.basyx.authorization.jwt.PublicKeyUtils;
import org.eclipse.digitaltwin.basyx.http.Base64UrlEncodedIdentifier;
import org.eclipse.digitaltwin.basyx.http.serialization.BaSyxHttpTestUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.util.ResourceUtils;

/**
 * Integration test for {@link AuthorizedAasRepository} feature
 * 
 * @author danish
 */
public class TestAuthorizedAasRepository {

	private static final String AAS_REPOSITORY_PATH = "/shells";
	private static final String AAS_SIMPLE_2_JSON = "authorization/AasSimple_2.json";
	private static final String AAS_SIMPLE_1_JSON = "authorization/AasSimple_1.json";
	private static final String SPECIFIC_SHELL_ID_2 = "specificAasId-2";
	private static final String SPECIFIC_SHELL_ID = "specificAasId";
	private static final String THUMBNAIL_FILE_NAME = "BaSyx-Logo.png";
	private static final String THUMBNAIL_FILE_PATH = "authorization/" + THUMBNAIL_FILE_NAME;
	public static String authenticaltionServerTokenEndpoint = "http://localhost:9096/realms/BaSyx/protocol/openid-connect/token";
	public static String aasRepositoryBaseUrl = "http://127.0.0.1:8081/shells";
	public static String healthEndpointUrl = "http://127.0.0.1:8081/actuator/health";
	public static String clientId = "basyx-client-api";
	private static AccessTokenProvider tokenProvider;
	private static AasRepository aasRepo;
	private static ConfigurableApplicationContext appContext;
	
	@BeforeClass
	public static void setUp() throws FileNotFoundException, IOException {
		tokenProvider = new AccessTokenProvider(authenticaltionServerTokenEndpoint, clientId);

		appContext = new SpringApplicationBuilder(DummyAuthorizedAasRepositoryComponent.class).profiles("authorization").run(new String[] {});

		aasRepo = appContext.getBean(AasRepository.class);

		initializeRepository();
	}
	
	@AfterClass
	public static void tearDown() {
		appContext.close();
	}
	
	@Test
	public void healthEndpointWithoutAuthorization() throws IOException, ParseException {
		String expectedHealthEndpointOutput = getAasJSONString("authorization/HealthOutput.json");
		
		CloseableHttpResponse healthCheckResponse = BaSyxHttpTestUtils.executeGetOnURL(healthEndpointUrl);
		assertEquals(HttpStatus.OK.value(), healthCheckResponse.getCode());
		
		BaSyxHttpTestUtils.assertSameJSONContent(expectedHealthEndpointOutput, BaSyxHttpTestUtils.getResponseAsString(healthCheckResponse));
	}

	@Test
	public void getAllAasWithCorrectRoleAndPermission() throws IOException {
		DummyCredential dummyCredential = DummyCredentialStore.BASYX_READER_CREDENTIAL;
		
		String accessToken = tokenProvider.getAccessToken(dummyCredential.getUsername(), dummyCredential.getPassword());
		
		CloseableHttpResponse retrievalResponse = getAllAasWithAuthorization(accessToken);
		assertEquals(HttpStatus.OK.value(), retrievalResponse.getCode());
	}
	
	@Test
	public void getAllAasWithInsufficientPermissionRole() throws IOException {
		DummyCredential dummyCredential = DummyCredentialStore.BASYX_UPDATER_CREDENTIAL;
		
		String accessToken = tokenProvider.getAccessToken(dummyCredential.getUsername(), dummyCredential.getPassword());
		
		CloseableHttpResponse retrievalResponse = getAllAasWithAuthorization(accessToken);
		assertEquals(HttpStatus.FORBIDDEN.value(), retrievalResponse.getCode());
	}
	
	@Test
	public void getAllAasWithNoAuthorization() throws IOException {
		CloseableHttpResponse retrievalResponse = getAllAasNoAuthorization();
		assertEquals(HttpStatus.UNAUTHORIZED.value(), retrievalResponse.getCode());
	}
	
	@Test
	public void getAasWithCorrectRoleAndPermission() throws IOException {
		DummyCredential dummyCredential = DummyCredentialStore.BASYX_READER_CREDENTIAL;
		
		String accessToken = tokenProvider.getAccessToken(dummyCredential.getUsername(), dummyCredential.getPassword());
		
		CloseableHttpResponse retrievalResponse = getElementWithAuthorization(getSpecificAasAccessURL(SPECIFIC_SHELL_ID), accessToken);
		assertEquals(HttpStatus.OK.value(), retrievalResponse.getCode());
	}
	
	@Test
	public void getAasWithCorrectRoleAndSpecificAasPermission() throws IOException {
		DummyCredential dummyCredential = DummyCredentialStore.BASYX_READER_TWO_CREDENTIAL;
		
		String accessToken = tokenProvider.getAccessToken(dummyCredential.getUsername(), dummyCredential.getPassword());
		
		CloseableHttpResponse retrievalResponse = getElementWithAuthorization(getSpecificAasAccessURL(SPECIFIC_SHELL_ID), accessToken);
		assertEquals(HttpStatus.OK.value(), retrievalResponse.getCode());
	}
	
	@Test
	public void getAasWithCorrectRoleAndUnauthorizedSpecificAas() throws IOException {
		DummyCredential dummyCredential = DummyCredentialStore.BASYX_READER_TWO_CREDENTIAL;
		
		String accessToken = tokenProvider.getAccessToken(dummyCredential.getUsername(), dummyCredential.getPassword());
		
		CloseableHttpResponse retrievalResponse = getElementWithAuthorization(getSpecificAasAccessURL(SPECIFIC_SHELL_ID_2), accessToken);
		assertEquals(HttpStatus.FORBIDDEN.value(), retrievalResponse.getCode());
	}
	
	@Test
	public void getAasWithInsufficientPermissionRole() throws IOException {
		DummyCredential dummyCredential = DummyCredentialStore.BASYX_UPDATER_CREDENTIAL;
		
		String accessToken = tokenProvider.getAccessToken(dummyCredential.getUsername(), dummyCredential.getPassword());
		
		CloseableHttpResponse retrievalResponse = getElementWithAuthorization(getSpecificAasAccessURL(SPECIFIC_SHELL_ID), accessToken);
		assertEquals(HttpStatus.FORBIDDEN.value(), retrievalResponse.getCode());
	}
	
	@Test
	public void getAasWithNoAuthorization() throws IOException {
		CloseableHttpResponse retrievalResponse = getElementWithNoAuthorization(getSpecificAasAccessURL(SPECIFIC_SHELL_ID));
		assertEquals(HttpStatus.UNAUTHORIZED.value(), retrievalResponse.getCode());
	}
	
	@Test
	public void createAasWithCorrectRoleAndPermission() throws IOException {
		String accessToken = getAccessToken(DummyCredentialStore.BASYX_CREATOR_CREDENTIAL);
		
		CloseableHttpResponse retrievalResponse = createAasOnRepositoryWithAuthorization(getAasJSONString(AAS_SIMPLE_2_JSON), accessToken);
		assertEquals(HttpStatus.CREATED.value(), retrievalResponse.getCode());
		
		deleteElementWithAuthorization(getSpecificAasAccessURL(SPECIFIC_SHELL_ID_2), getAccessToken(DummyCredentialStore.ADMIN_CREDENTIAL));
	}
	
	@Test
	public void createAasWithInsufficientPermissionRole() throws IOException {
		String accessToken = getAccessToken(DummyCredentialStore.BASYX_READER_CREDENTIAL);
		
		CloseableHttpResponse retrievalResponse = createAasOnRepositoryWithAuthorization(getAasJSONString(AAS_SIMPLE_2_JSON), accessToken);
		assertEquals(HttpStatus.FORBIDDEN.value(), retrievalResponse.getCode());
	}
	
	@Test
	public void createAasWithNoAuthorization() throws IOException {
		CloseableHttpResponse retrievalResponse = createAasOnRepositoryWithNoAuthorization(getAasJSONString(AAS_SIMPLE_2_JSON));
		
		assertEquals(HttpStatus.UNAUTHORIZED.value(), retrievalResponse.getCode());
	}
	
	@Test
	public void updateAasWithCorrectRoleAndPermission() throws IOException {
		String accessToken = getAccessToken(DummyCredentialStore.BASYX_UPDATER_CREDENTIAL);
		
		CloseableHttpResponse retrievalResponse = updateElementWithAuthorizationPutRequest(getSpecificAasAccessURL(SPECIFIC_SHELL_ID), getAasJSONString(AAS_SIMPLE_1_JSON), accessToken);
		assertEquals(HttpStatus.NO_CONTENT.value(), retrievalResponse.getCode());
	}
	
	@Test
	public void updateAasWithCorrectRoleAndSpecificAasPermission() throws IOException {		
		String accessToken = getAccessToken(DummyCredentialStore.BASYX_UPDATER_TWO_CREDENTIAL);
		
		CloseableHttpResponse retrievalResponse = updateElementWithAuthorizationPutRequest(getSpecificAasAccessURL(SPECIFIC_SHELL_ID), getAasJSONString(AAS_SIMPLE_1_JSON), accessToken);
		assertEquals(HttpStatus.NO_CONTENT.value(), retrievalResponse.getCode());
	}
	
	@Test
	public void updateAasWithCorrectRoleAndUnauthorizedSpecificAas() throws IOException {
		String accessToken = getAccessToken(DummyCredentialStore.BASYX_UPDATER_TWO_CREDENTIAL);
		
		CloseableHttpResponse retrievalResponse = updateElementWithAuthorizationPutRequest(getSpecificAasAccessURL(SPECIFIC_SHELL_ID_2), getAasJSONString(AAS_SIMPLE_1_JSON), accessToken);
		assertEquals(HttpStatus.FORBIDDEN.value(), retrievalResponse.getCode());
	}
	
	@Test
	public void updateAasWithInsufficientPermissionRole() throws IOException {
		String accessToken = getAccessToken(DummyCredentialStore.BASYX_READER_CREDENTIAL);
		
		CloseableHttpResponse retrievalResponse = updateElementWithAuthorizationPutRequest(getSpecificAasAccessURL(SPECIFIC_SHELL_ID), getAasJSONString(AAS_SIMPLE_1_JSON), accessToken);
		assertEquals(HttpStatus.FORBIDDEN.value(), retrievalResponse.getCode());
	}
	
	@Test
	public void updateAasWithNoAuthorization() throws IOException {
		CloseableHttpResponse retrievalResponse = updateElementWithNoAuthorizationPutRequest(getSpecificAasAccessURL(SPECIFIC_SHELL_ID), getAasJSONString(AAS_SIMPLE_1_JSON));
		
		assertEquals(HttpStatus.UNAUTHORIZED.value(), retrievalResponse.getCode());
	}

	@Test
	public void deleteAasWithCorrectRoleAndPermission() throws IOException {
		createAasOnRepositoryWithAuthorization(getAasJSONString(AAS_SIMPLE_2_JSON), getAccessToken(DummyCredentialStore.ADMIN_CREDENTIAL));
		
		String accessToken = getAccessToken(DummyCredentialStore.BASYX_DELETER_CREDENTIAL);
		
		CloseableHttpResponse retrievalResponse = deleteElementWithAuthorization(getSpecificAasAccessURL(SPECIFIC_SHELL_ID_2), accessToken);
		assertEquals(HttpStatus.NO_CONTENT.value(), retrievalResponse.getCode());
		
		assertElementIsNotOnServer(getSpecificAasAccessURL(SPECIFIC_SHELL_ID_2), getAccessToken(DummyCredentialStore.ADMIN_CREDENTIAL));
		
		deleteElementWithAuthorization(getSpecificAasAccessURL(SPECIFIC_SHELL_ID_2), getAccessToken(DummyCredentialStore.ADMIN_CREDENTIAL));
	}
	
	@Test
	public void deleteAasWithCorrectRoleAndSpecificAasPermission() throws IOException {
		createAasOnRepositoryWithAuthorization(getAasJSONString(AAS_SIMPLE_2_JSON), getAccessToken(DummyCredentialStore.ADMIN_CREDENTIAL));
		
		String accessToken = getAccessToken(DummyCredentialStore.BASYX_DELETER_TWO_CREDENTIAL);
		
		CloseableHttpResponse retrievalResponse = deleteElementWithAuthorization(getSpecificAasAccessURL(SPECIFIC_SHELL_ID_2), accessToken);
		assertEquals(HttpStatus.NO_CONTENT.value(), retrievalResponse.getCode());
		
		assertElementIsNotOnServer(getSpecificAasAccessURL(SPECIFIC_SHELL_ID_2), getAccessToken(DummyCredentialStore.ADMIN_CREDENTIAL));
		
		deleteElementWithAuthorization(getSpecificAasAccessURL(SPECIFIC_SHELL_ID_2), getAccessToken(DummyCredentialStore.ADMIN_CREDENTIAL));
	}
	
	@Test
	public void deleteAasWithCorrectRoleAndUnauthorizedSpecificAas() throws IOException {
		String accessToken = getAccessToken(DummyCredentialStore.BASYX_DELETER_TWO_CREDENTIAL);
		
		CloseableHttpResponse retrievalResponse = deleteElementWithAuthorization(getSpecificAasAccessURL(SPECIFIC_SHELL_ID), accessToken);
		assertEquals(HttpStatus.FORBIDDEN.value(), retrievalResponse.getCode());
		
		assertElementExistsOnServer(getSpecificAasAccessURL(SPECIFIC_SHELL_ID), getAccessToken(DummyCredentialStore.ADMIN_CREDENTIAL));
	}
	
	@Test
	public void deleteAasWithInsufficientPermissionRole() throws IOException {
		String accessToken = getAccessToken(DummyCredentialStore.BASYX_UPDATER_CREDENTIAL);
		
		CloseableHttpResponse retrievalResponse = deleteElementWithAuthorization(getSpecificAasAccessURL(SPECIFIC_SHELL_ID), accessToken);
		assertEquals(HttpStatus.FORBIDDEN.value(), retrievalResponse.getCode());
		
		assertElementExistsOnServer(getSpecificAasAccessURL(SPECIFIC_SHELL_ID), getAccessToken(DummyCredentialStore.ADMIN_CREDENTIAL));
	}
	
	@Test
	public void deleteAasWithNoAuthorization() throws IOException {
		CloseableHttpResponse retrievalResponse = deleteElementWithNoAuthorization(getSpecificAasAccessURL(SPECIFIC_SHELL_ID));
		
		assertEquals(HttpStatus.UNAUTHORIZED.value(), retrievalResponse.getCode());
		
		assertElementExistsOnServer(getSpecificAasAccessURL(SPECIFIC_SHELL_ID), getAccessToken(DummyCredentialStore.ADMIN_CREDENTIAL));
	}
	
	@Test
	public void getSubmodelReferencesWithCorrectRoleAndPermission() throws IOException {
		String accessToken = getAccessToken(DummyCredentialStore.BASYX_READER_CREDENTIAL);
		
		CloseableHttpResponse retrievalResponse = getElementWithAuthorization(getSpecificAasSubmodelRefAccessURL(SPECIFIC_SHELL_ID), accessToken);
		assertEquals(HttpStatus.OK.value(), retrievalResponse.getCode());
	}
	
	@Test
	public void getSubmodelReferencesWithCorrectRoleAndSpecificAasPermission() throws IOException {
		DummyCredential dummyCredential = DummyCredentialStore.BASYX_READER_TWO_CREDENTIAL;
		
		String accessToken = tokenProvider.getAccessToken(dummyCredential.getUsername(), dummyCredential.getPassword());
		
		CloseableHttpResponse retrievalResponse = getElementWithAuthorization(getSpecificAasSubmodelRefAccessURL(SPECIFIC_SHELL_ID), accessToken);
		assertEquals(HttpStatus.OK.value(), retrievalResponse.getCode());
	}
	
	@Test
	public void getSubmodelReferencesWithCorrectRoleAndUnauthorizedSpecificAas() throws IOException {
		DummyCredential dummyCredential = DummyCredentialStore.BASYX_READER_TWO_CREDENTIAL;
		
		String accessToken = tokenProvider.getAccessToken(dummyCredential.getUsername(), dummyCredential.getPassword());
		
		CloseableHttpResponse retrievalResponse = getElementWithAuthorization(getSpecificAasSubmodelRefAccessURL(SPECIFIC_SHELL_ID_2), accessToken);
		assertEquals(HttpStatus.FORBIDDEN.value(), retrievalResponse.getCode());
	}
	
	@Test
	public void getSubmodelReferencesWithInsufficientPermissionRole() throws IOException {
		DummyCredential dummyCredential = DummyCredentialStore.BASYX_UPDATER_CREDENTIAL;
		
		String accessToken = tokenProvider.getAccessToken(dummyCredential.getUsername(), dummyCredential.getPassword());
		
		CloseableHttpResponse retrievalResponse = getElementWithAuthorization(getSpecificAasSubmodelRefAccessURL(SPECIFIC_SHELL_ID), accessToken);
		assertEquals(HttpStatus.FORBIDDEN.value(), retrievalResponse.getCode());
	}
	
	@Test
	public void getSubmodelReferencesWithNoAuthorization() throws IOException {
		CloseableHttpResponse retrievalResponse = getElementWithNoAuthorization(getSpecificAasSubmodelRefAccessURL(SPECIFIC_SHELL_ID));
		assertEquals(HttpStatus.UNAUTHORIZED.value(), retrievalResponse.getCode());
	}
	
	@Test
	public void addSubmodelReferenceWithCorrectRoleAndPermission() throws IOException {
		String accessToken = getAccessToken(DummyCredentialStore.BASYX_UPDATER_CREDENTIAL);
		
		CloseableHttpResponse retrievalResponse = updateElementWithAuthorizationPostRequest(getSpecificAasSubmodelRefAccessURL(SPECIFIC_SHELL_ID), getAasJSONString("SingleSubmodelReference_1.json"), accessToken);
		assertEquals(HttpStatus.CREATED.value(), retrievalResponse.getCode());
	}
	
	@Test
	public void addSubmodelReferenceWithCorrectRoleAndSpecificAasPermission() throws IOException {		
		String accessToken = getAccessToken(DummyCredentialStore.BASYX_UPDATER_TWO_CREDENTIAL);
		
		CloseableHttpResponse retrievalResponse = updateElementWithAuthorizationPostRequest(getSpecificAasSubmodelRefAccessURL(SPECIFIC_SHELL_ID), getAasJSONString("SingleSubmodelReference_2.json"), accessToken);
		assertEquals(HttpStatus.CREATED.value(), retrievalResponse.getCode());
	}
	
	@Test
	public void addSubmodelReferenceWithCorrectRoleAndUnauthorizedSpecificAas() throws IOException {
		String accessToken = getAccessToken(DummyCredentialStore.BASYX_UPDATER_TWO_CREDENTIAL);
		
		CloseableHttpResponse retrievalResponse = updateElementWithAuthorizationPostRequest(getSpecificAasSubmodelRefAccessURL(SPECIFIC_SHELL_ID_2), getAasJSONString("SingleSubmodelReference_1.json"), accessToken);
		assertEquals(HttpStatus.FORBIDDEN.value(), retrievalResponse.getCode());
	}
	
	@Test
	public void addSubmodelReferenceWithInsufficientPermissionRole() throws IOException {
		String accessToken = getAccessToken(DummyCredentialStore.BASYX_READER_CREDENTIAL);
		
		CloseableHttpResponse retrievalResponse = updateElementWithAuthorizationPostRequest(getSpecificAasSubmodelRefAccessURL(SPECIFIC_SHELL_ID), getAasJSONString("SingleSubmodelReference_1.json"), accessToken);
		assertEquals(HttpStatus.FORBIDDEN.value(), retrievalResponse.getCode());
	}
	
	@Test
	public void addSubmodelReferenceWithNoAuthorization() throws IOException {
		CloseableHttpResponse retrievalResponse = updateElementWithNoAuthorizationPostRequest(getSpecificAasSubmodelRefAccessURL(SPECIFIC_SHELL_ID), getAasJSONString("SingleSubmodelReference_1.json"));
		
		assertEquals(HttpStatus.UNAUTHORIZED.value(), retrievalResponse.getCode());
	}

	@Test
	public void removeSubmodelReferenceWithCorrectRoleAndPermission() throws IOException {
		createAasOnRepositoryWithAuthorization(getAasJSONString(AAS_SIMPLE_2_JSON), getAccessToken(DummyCredentialStore.ADMIN_CREDENTIAL));
		
		String accessToken = getAccessToken(DummyCredentialStore.BASYX_ASSET_UPDATER_CREDENTIAL);
		
		CloseableHttpResponse retrievalResponse = deleteElementWithAuthorization(getSpecificSubmodelReferenceUrl(SPECIFIC_SHELL_ID_2, "http://i40.customer.com/type/1/1/testSubmodel"), accessToken);
		assertEquals(HttpStatus.OK.value(), retrievalResponse.getCode());
		
		deleteElementWithAuthorization(getSpecificAasAccessURL(SPECIFIC_SHELL_ID_2), getAccessToken(DummyCredentialStore.ADMIN_CREDENTIAL));
	}
	
	@Test
	public void removeSubmodelReferenceWithCorrectRoleAndSpecificAasPermission() throws IOException {
		createAasOnRepositoryWithAuthorization(getAasJSONString(AAS_SIMPLE_2_JSON), getAccessToken(DummyCredentialStore.ADMIN_CREDENTIAL));
		
		String accessToken = getAccessToken(DummyCredentialStore.BASYX_ASSET_UPDATER_TWO_CREDENTIAL);
		
		CloseableHttpResponse retrievalResponse = deleteElementWithAuthorization(getSpecificSubmodelReferenceUrl(SPECIFIC_SHELL_ID_2, "http://i40.customer.com/type/1/1/testSubmodel"), accessToken);
		assertEquals(HttpStatus.OK.value(), retrievalResponse.getCode());
		
		deleteElementWithAuthorization(getSpecificAasAccessURL(SPECIFIC_SHELL_ID_2), getAccessToken(DummyCredentialStore.ADMIN_CREDENTIAL));
	}
	
	@Test
	public void removeSubmodelReferenceWithCorrectRoleAndUnauthorizedSpecificAas() throws IOException {
		String accessToken = getAccessToken(DummyCredentialStore.BASYX_ASSET_UPDATER_TWO_CREDENTIAL);
		
		CloseableHttpResponse retrievalResponse = deleteElementWithAuthorization(getSpecificSubmodelReferenceUrl(SPECIFIC_SHELL_ID, "http://i40.customer.com/type/1/1/testSubmodel"), accessToken);
		assertEquals(HttpStatus.FORBIDDEN.value(), retrievalResponse.getCode());
	}
	
	@Test
	public void removeSubmodelReferenceWithInsufficientPermissionRole() throws IOException {
		String accessToken = getAccessToken(DummyCredentialStore.BASYX_CREATOR_CREDENTIAL);
		
		CloseableHttpResponse retrievalResponse = deleteElementWithAuthorization(getSpecificSubmodelReferenceUrl(SPECIFIC_SHELL_ID, "http://i40.customer.com/type/1/1/testSubmodel"), accessToken);
		assertEquals(HttpStatus.FORBIDDEN.value(), retrievalResponse.getCode());
	}
	
	@Test
	public void removeSubmodelReferenceWithNoAuthorization() throws IOException {
		CloseableHttpResponse retrievalResponse = deleteElementWithNoAuthorization(getSpecificSubmodelReferenceUrl(SPECIFIC_SHELL_ID, "http://i40.customer.com/type/1/1/testSubmodel"));
		
		assertEquals(HttpStatus.UNAUTHORIZED.value(), retrievalResponse.getCode());
	}
	
	@Test
	public void setAssetInformationWithCorrectRoleAndPermission() throws IOException {
		createAasOnRepositoryWithAuthorization(getAasJSONString(AAS_SIMPLE_2_JSON), getAccessToken(DummyCredentialStore.ADMIN_CREDENTIAL));
		
		String accessToken = getAccessToken(DummyCredentialStore.BASYX_ASSET_UPDATER_CREDENTIAL);
		
		CloseableHttpResponse retrievalResponse = updateElementWithAuthorizationPutRequest(getSpecificAssetInformationAccessURL(SPECIFIC_SHELL_ID_2), getAasJSONString("assetInfoSimple.json"), accessToken);
		assertEquals(HttpStatus.OK.value(), retrievalResponse.getCode());
		
		deleteElementWithAuthorization(getSpecificAasAccessURL(SPECIFIC_SHELL_ID_2), getAccessToken(DummyCredentialStore.ADMIN_CREDENTIAL));
	}
	
	@Test
	public void setAssetInformationWithCorrectRoleAndSpecificAasPermission() throws IOException {	
		createAasOnRepositoryWithAuthorization(getAasJSONString(AAS_SIMPLE_2_JSON), getAccessToken(DummyCredentialStore.ADMIN_CREDENTIAL));
		
		String accessToken = getAccessToken(DummyCredentialStore.BASYX_ASSET_UPDATER_TWO_CREDENTIAL);
		
		CloseableHttpResponse retrievalResponse = updateElementWithAuthorizationPutRequest(getSpecificAssetInformationAccessURL(SPECIFIC_SHELL_ID_2), getAasJSONString("assetInfoSimple.json"), accessToken);
		assertEquals(HttpStatus.OK.value(), retrievalResponse.getCode());
		
		deleteElementWithAuthorization(getSpecificAasAccessURL(SPECIFIC_SHELL_ID_2), getAccessToken(DummyCredentialStore.ADMIN_CREDENTIAL));
	}
	
	@Test
	public void setAssetInformationWithCorrectRoleAndUnauthorizedSpecificAas() throws IOException {
		String accessToken = getAccessToken(DummyCredentialStore.BASYX_ASSET_UPDATER_TWO_CREDENTIAL);
		
		CloseableHttpResponse retrievalResponse = updateElementWithAuthorizationPutRequest(getSpecificAssetInformationAccessURL(SPECIFIC_SHELL_ID), getAasJSONString("assetInfoSimple.json"), accessToken);
		assertEquals(HttpStatus.FORBIDDEN.value(), retrievalResponse.getCode());
	}
	
	@Test
	public void setAssetInformationWithInsufficientPermissionRole() throws IOException {
		String accessToken = getAccessToken(DummyCredentialStore.BASYX_READER_CREDENTIAL);
		
		CloseableHttpResponse retrievalResponse = updateElementWithAuthorizationPutRequest(getSpecificAssetInformationAccessURL(SPECIFIC_SHELL_ID), getAasJSONString("assetInfoSimple.json"), accessToken);
		assertEquals(HttpStatus.FORBIDDEN.value(), retrievalResponse.getCode());
	}
	
	@Test
	public void setAssetInformationWithNoAuthorization() throws IOException {
		CloseableHttpResponse retrievalResponse = updateElementWithNoAuthorizationPutRequest(getSpecificAssetInformationAccessURL(SPECIFIC_SHELL_ID), getAasJSONString("assetInfoSimple.json"));
		
		assertEquals(HttpStatus.UNAUTHORIZED.value(), retrievalResponse.getCode());
	}
	
	@Test
	public void getAssetInformationWithCorrectRoleAndPermission() throws IOException {
		String accessToken = getAccessToken(DummyCredentialStore.BASYX_READER_CREDENTIAL);
		
		CloseableHttpResponse retrievalResponse = getElementWithAuthorization(getSpecificAssetInformationAccessURL(SPECIFIC_SHELL_ID), accessToken);
		assertEquals(HttpStatus.OK.value(), retrievalResponse.getCode());
	}
	
	@Test
	public void getAssetInformationWithCorrectRoleAndSpecificAasPermission() throws IOException {	
		String accessToken = getAccessToken(DummyCredentialStore.BASYX_READER_TWO_CREDENTIAL);
		
		CloseableHttpResponse retrievalResponse = getElementWithAuthorization(getSpecificAssetInformationAccessURL(SPECIFIC_SHELL_ID), accessToken);
		assertEquals(HttpStatus.OK.value(), retrievalResponse.getCode());
	}
	
	@Test
	public void getAssetInformationWithCorrectRoleAndUnauthorizedSpecificAas() throws IOException {
		String accessToken = getAccessToken(DummyCredentialStore.BASYX_READER_TWO_CREDENTIAL);
		
		CloseableHttpResponse retrievalResponse = getElementWithAuthorization(getSpecificAssetInformationAccessURL(SPECIFIC_SHELL_ID_2), accessToken);
		assertEquals(HttpStatus.FORBIDDEN.value(), retrievalResponse.getCode());
	}
	
	@Test
	public void getAssetInformationWithInsufficientPermissionRole() throws IOException {
		String accessToken = getAccessToken(DummyCredentialStore.BASYX_DELETER_CREDENTIAL);
		
		CloseableHttpResponse retrievalResponse = getElementWithAuthorization(getSpecificAssetInformationAccessURL(SPECIFIC_SHELL_ID), accessToken);
		assertEquals(HttpStatus.FORBIDDEN.value(), retrievalResponse.getCode());
	}
	
	@Test
	public void getAssetInformationWithNoAuthorization() throws IOException {
		CloseableHttpResponse retrievalResponse = getElementWithNoAuthorization(getSpecificAssetInformationAccessURL(SPECIFIC_SHELL_ID));
		
		assertEquals(HttpStatus.UNAUTHORIZED.value(), retrievalResponse.getCode());
	}

	@Test
	public void getThumbnailWithCorrectRoleAndPermission() throws IOException {
		setThumbnailToAasWithAuthorization(SPECIFIC_SHELL_ID, getAccessToken(DummyCredentialStore.ADMIN_CREDENTIAL));
		
		String accessToken = getAccessToken(DummyCredentialStore.BASYX_READER_CREDENTIAL);
		
		CloseableHttpResponse retrievalResponse = getElementWithAuthorization(BaSyxHttpTestUtils.getThumbnailAccessURL(createAasRepositoryUrl(aasRepositoryBaseUrl), SPECIFIC_SHELL_ID), accessToken);
		assertEquals(HttpStatus.OK.value(), retrievalResponse.getCode());
		
		deleteElementWithAuthorization(BaSyxHttpTestUtils.getThumbnailAccessURL(createAasRepositoryUrl(aasRepositoryBaseUrl), SPECIFIC_SHELL_ID), getAccessToken(DummyCredentialStore.ADMIN_CREDENTIAL));
	}

	@Test
	public void getThumbnailWithCorrectRoleAndSpecificAasPermission() throws IOException {
		setThumbnailToAasWithAuthorization(SPECIFIC_SHELL_ID, getAccessToken(DummyCredentialStore.ADMIN_CREDENTIAL));
		
		String accessToken = getAccessToken(DummyCredentialStore.BASYX_READER_TWO_CREDENTIAL);
		
		CloseableHttpResponse retrievalResponse = getElementWithAuthorization(BaSyxHttpTestUtils.getThumbnailAccessURL(createAasRepositoryUrl(aasRepositoryBaseUrl), SPECIFIC_SHELL_ID), accessToken);
		assertEquals(HttpStatus.OK.value(), retrievalResponse.getCode());
		
		deleteElementWithAuthorization(BaSyxHttpTestUtils.getThumbnailAccessURL(createAasRepositoryUrl(aasRepositoryBaseUrl), SPECIFIC_SHELL_ID), getAccessToken(DummyCredentialStore.ADMIN_CREDENTIAL));
	}
	
	@Test
	public void getThumbnailWithCorrectRoleAndUnauthorizedSpecificAas() throws IOException {
		String accessToken = getAccessToken(DummyCredentialStore.BASYX_READER_TWO_CREDENTIAL);
		
		CloseableHttpResponse retrievalResponse = getElementWithAuthorization(BaSyxHttpTestUtils.getThumbnailAccessURL(createAasRepositoryUrl(aasRepositoryBaseUrl), SPECIFIC_SHELL_ID_2), accessToken);
		assertEquals(HttpStatus.FORBIDDEN.value(), retrievalResponse.getCode());
	}
	
	@Test
	public void getThumbnailWithInsufficientPermissionRole() throws IOException {
		String accessToken = getAccessToken(DummyCredentialStore.BASYX_DELETER_CREDENTIAL);
		
		CloseableHttpResponse retrievalResponse = getElementWithAuthorization(BaSyxHttpTestUtils.getThumbnailAccessURL(createAasRepositoryUrl(aasRepositoryBaseUrl), SPECIFIC_SHELL_ID), accessToken);
		assertEquals(HttpStatus.FORBIDDEN.value(), retrievalResponse.getCode());
	}
	
	@Test
	public void getThumbnailWithNoAuthorization() throws IOException {
		CloseableHttpResponse retrievalResponse = getElementWithNoAuthorization(BaSyxHttpTestUtils.getThumbnailAccessURL(createAasRepositoryUrl(aasRepositoryBaseUrl), SPECIFIC_SHELL_ID));
		
		assertEquals(HttpStatus.UNAUTHORIZED.value(), retrievalResponse.getCode());
	}

	@Test
	public void setThumbnailWithCorrectRoleAndPermission() throws IOException {
		String accessToken = getAccessToken(DummyCredentialStore.BASYX_UPDATER_CREDENTIAL);
		
		CloseableHttpResponse retrievalResponse = setThumbnailToAasWithAuthorization(SPECIFIC_SHELL_ID, accessToken);
		assertEquals(HttpStatus.OK.value(), retrievalResponse.getCode());
		
		deleteElementWithAuthorization(BaSyxHttpTestUtils.getThumbnailAccessURL(createAasRepositoryUrl(aasRepositoryBaseUrl), SPECIFIC_SHELL_ID), getAccessToken(DummyCredentialStore.ADMIN_CREDENTIAL));
	}

	@Test
	public void setThumbnailWithCorrectRoleAndSpecificAasPermission() throws IOException {
		String accessToken = getAccessToken(DummyCredentialStore.BASYX_UPDATER_TWO_CREDENTIAL);
		
		CloseableHttpResponse retrievalResponse = setThumbnailToAasWithAuthorization(SPECIFIC_SHELL_ID, accessToken);
		assertEquals(HttpStatus.OK.value(), retrievalResponse.getCode());
		
		deleteElementWithAuthorization(BaSyxHttpTestUtils.getThumbnailAccessURL(createAasRepositoryUrl(aasRepositoryBaseUrl), SPECIFIC_SHELL_ID), getAccessToken(DummyCredentialStore.ADMIN_CREDENTIAL));
	}
	
	@Test
	public void setThumbnailWithCorrectRoleAndUnauthorizedSpecificAas() throws IOException {
		String accessToken = getAccessToken(DummyCredentialStore.BASYX_UPDATER_TWO_CREDENTIAL);
		
		CloseableHttpResponse retrievalResponse = setThumbnailToAasWithAuthorization(SPECIFIC_SHELL_ID_2, accessToken);
		assertEquals(HttpStatus.FORBIDDEN.value(), retrievalResponse.getCode());
	}
	
	@Test
	public void setThumbnailWithInsufficientPermissionRole() throws IOException {
		String accessToken = getAccessToken(DummyCredentialStore.BASYX_DELETER_CREDENTIAL);
		
		CloseableHttpResponse retrievalResponse = setThumbnailToAasWithAuthorization(SPECIFIC_SHELL_ID, accessToken);
		assertEquals(HttpStatus.FORBIDDEN.value(), retrievalResponse.getCode());
	}
	
	@Test
	public void setThumbnailWithNoAuthorization() throws IOException {
		CloseableHttpResponse retrievalResponse = setThumbnailToAasWithNoAuthorization(SPECIFIC_SHELL_ID);
		
		assertEquals(HttpStatus.UNAUTHORIZED.value(), retrievalResponse.getCode());
	}

	@Test
	public void deleteThumbnailWithCorrectRoleAndPermission() throws IOException {
		setThumbnailToAasWithAuthorization(SPECIFIC_SHELL_ID, getAccessToken(DummyCredentialStore.ADMIN_CREDENTIAL));
		
		String accessToken = getAccessToken(DummyCredentialStore.BASYX_ASSET_UPDATER_CREDENTIAL);
		
		CloseableHttpResponse retrievalResponse = deleteElementWithAuthorization(BaSyxHttpTestUtils.getThumbnailAccessURL(createAasRepositoryUrl(aasRepositoryBaseUrl), SPECIFIC_SHELL_ID), accessToken);
		assertEquals(HttpStatus.OK.value(), retrievalResponse.getCode());
		
		assertElementIsNotOnServer(BaSyxHttpTestUtils.getThumbnailAccessURL(createAasRepositoryUrl(aasRepositoryBaseUrl), SPECIFIC_SHELL_ID), getAccessToken(DummyCredentialStore.ADMIN_CREDENTIAL));
	}

	@Test
	public void deleteThumbnailWithCorrectRoleAndSpecificAasPermission() throws IOException {
		createAasOnRepositoryWithAuthorization(getAasJSONString(AAS_SIMPLE_2_JSON), getAccessToken(DummyCredentialStore.ADMIN_CREDENTIAL));
		
		setThumbnailToAasWithAuthorization(SPECIFIC_SHELL_ID_2, getAccessToken(DummyCredentialStore.ADMIN_CREDENTIAL));
		
		String accessToken = getAccessToken(DummyCredentialStore.BASYX_ASSET_UPDATER_TWO_CREDENTIAL);
		
		CloseableHttpResponse retrievalResponse = deleteElementWithAuthorization(BaSyxHttpTestUtils.getThumbnailAccessURL(createAasRepositoryUrl(aasRepositoryBaseUrl), SPECIFIC_SHELL_ID_2), accessToken);
		assertEquals(HttpStatus.OK.value(), retrievalResponse.getCode());
		
		assertElementIsNotOnServer(BaSyxHttpTestUtils.getThumbnailAccessURL(createAasRepositoryUrl(aasRepositoryBaseUrl), SPECIFIC_SHELL_ID_2), getAccessToken(DummyCredentialStore.ADMIN_CREDENTIAL));
		deleteElementWithAuthorization(getSpecificAasAccessURL(SPECIFIC_SHELL_ID_2), getAccessToken(DummyCredentialStore.ADMIN_CREDENTIAL));
	}
	
	@Test
	public void deleteThumbnailWithCorrectRoleAndUnauthorizedSpecificAas() throws IOException {
		String accessToken = getAccessToken(DummyCredentialStore.BASYX_ASSET_UPDATER_TWO_CREDENTIAL);
		
		CloseableHttpResponse retrievalResponse = deleteElementWithAuthorization(BaSyxHttpTestUtils.getThumbnailAccessURL(createAasRepositoryUrl(aasRepositoryBaseUrl), SPECIFIC_SHELL_ID), accessToken);
		assertEquals(HttpStatus.FORBIDDEN.value(), retrievalResponse.getCode());
	}
	
	@Test
	public void deleteThumbnailWithInsufficientPermissionRole() throws IOException {
		String accessToken = getAccessToken(DummyCredentialStore.BASYX_CREATOR_CREDENTIAL);
		
		CloseableHttpResponse retrievalResponse = deleteElementWithAuthorization(BaSyxHttpTestUtils.getThumbnailAccessURL(createAasRepositoryUrl(aasRepositoryBaseUrl), SPECIFIC_SHELL_ID), accessToken);
		assertEquals(HttpStatus.FORBIDDEN.value(), retrievalResponse.getCode());
	}
	
	@Test
	public void deleteThumbnailWithNoAuthorization() throws IOException {
		CloseableHttpResponse retrievalResponse = deleteElementWithNoAuthorization(BaSyxHttpTestUtils.getThumbnailAccessURL(createAasRepositoryUrl(aasRepositoryBaseUrl), SPECIFIC_SHELL_ID));
		
		assertEquals(HttpStatus.UNAUTHORIZED.value(), retrievalResponse.getCode());
	}
	
	private static AssetAdministrationShell createDummyShell(String id, String idShort, String submodelId) {
		return new DefaultAssetAdministrationShell.Builder().id(SPECIFIC_SHELL_ID).idShort("ExampleMotor").assetInformation(new DefaultAssetInformation.Builder().assetKind(AssetKind.INSTANCE).globalAssetId("globalAssetId").build())
				.submodels(new DefaultReference.Builder().keys(new DefaultKey.Builder().type(KeyTypes.SUBMODEL).value(submodelId).build()).type(ReferenceTypes.EXTERNAL_REFERENCE).build()).build();
	}

	private static void initializeRepository() throws FileNotFoundException, IOException {
		configureSecurityContext();

		aasRepo.createAas(createDummyShell(SPECIFIC_SHELL_ID, "ExampleMotor", "http://i40.customer.com/type/1/1/testSubmodel"));
		
		SecurityContextHolder.clearContext();
	}

	private static void configureSecurityContext() throws FileNotFoundException, IOException {
		String adminToken = getAdminAccessToken();

		String modulus = getStringFromFile("authorization/modulus.txt");
		String exponent = "AQAB";

		RSAPublicKey rsaPublicKey = PublicKeyUtils.buildPublicKey(modulus, exponent);

		Jwt jwt = JwtTokenDecoder.decodeJwt(adminToken, rsaPublicKey);

		SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(jwt));
	}

	private static String getStringFromFile(String fileName) throws FileNotFoundException, IOException {
		return BaSyxHttpTestUtils.readJSONStringFromClasspath(fileName);
	}
	
	private String getSpecificAssetInformationAccessURL(String aasID) {
		return getSpecificAasAccessURL(aasID) + "/asset-information";
	}
	
	private void assertElementIsNotOnServer(String url, String accessToken) throws IOException {
		CloseableHttpResponse getResponse = getElementWithAuthorization(url, accessToken);
		assertEquals(HttpStatus.NOT_FOUND.value(), getResponse.getCode());
	}
	
	private void assertElementExistsOnServer(String url, String accessToken) throws IOException {
		CloseableHttpResponse getResponse = getElementWithAuthorization(url, accessToken);
		assertEquals(HttpStatus.OK.value(), getResponse.getCode());
	}

	private String getSpecificAasSubmodelRefAccessURL(String shellId) {
		return getSpecificAasAccessURL(shellId) + "/submodel-refs";
	}
	
	private String getAccessToken(DummyCredential dummyCredential) {
		return tokenProvider.getAccessToken(dummyCredential.getUsername(), dummyCredential.getPassword());
	}
	
	protected CloseableHttpResponse getAllAasWithAuthorization(String accessToken) throws IOException {
		return BaSyxHttpTestUtils.executeAuthorizedGetOnURL(createAasRepositoryUrl(aasRepositoryBaseUrl), accessToken);
	}
	
	protected CloseableHttpResponse getAllAasNoAuthorization() throws IOException {
		return BaSyxHttpTestUtils.executeGetOnURL(createAasRepositoryUrl(aasRepositoryBaseUrl));
	}
	
	protected CloseableHttpResponse getElementWithAuthorization(String url, String accessToken) throws IOException {
		return BaSyxHttpTestUtils.executeAuthorizedGetOnURL(url, accessToken);
	}
	
	protected CloseableHttpResponse getElementWithNoAuthorization(String url) throws IOException {
		return BaSyxHttpTestUtils.executeGetOnURL(url);
	}
	
	protected String getSpecificAasAccessURL(String shellId) {
		return createAasRepositoryUrl(createAasRepositoryUrl(aasRepositoryBaseUrl)) + "/" + Base64UrlEncodedIdentifier.encodeIdentifier(shellId);
	}
	
	private static CloseableHttpResponse createAasOnRepositoryWithAuthorization(String aasJsonContent, String accessToken) throws IOException {
		return BaSyxHttpTestUtils.executeAuthorizedPostOnURL(createAasRepositoryUrl(aasRepositoryBaseUrl), aasJsonContent, accessToken);
	}
	
	private CloseableHttpResponse setThumbnailToAasWithAuthorization(String shellId, String accessToken) throws IOException {
		File file = ResourceUtils.getFile("classpath:" + THUMBNAIL_FILE_PATH);

		return updateElementWithFileWithAuthorization(getThumbnailAccessURL(shellId), shellId, THUMBNAIL_FILE_NAME, file, accessToken);
	}
	
	private CloseableHttpResponse setThumbnailToAasWithNoAuthorization(String shellId) throws IOException {
		File file = ResourceUtils.getFile("classpath:" + THUMBNAIL_FILE_PATH);
		
		return updateElementWithFileWithNoAuthorization(getThumbnailAccessURL(shellId), THUMBNAIL_FILE_NAME, file);
	}
	
	private static CloseableHttpResponse createAasOnRepositoryWithNoAuthorization(String aasJsonContent) throws IOException {
		return BaSyxHttpTestUtils.executePostOnURL(createAasRepositoryUrl(aasRepositoryBaseUrl), aasJsonContent);
	}
	
	private CloseableHttpResponse updateElementWithAuthorizationPutRequest(String url, String aasJsonContent, String accessToken) throws IOException {
		return BaSyxHttpTestUtils.executeAuthorizedPutOnURL(url, aasJsonContent, accessToken);
	}
	
	private CloseableHttpResponse updateElementWithAuthorizationPostRequest(String url, String aasJsonContent, String accessToken) throws IOException {
		return BaSyxHttpTestUtils.executeAuthorizedPostOnURL(url, aasJsonContent, accessToken);
	}
	
	private CloseableHttpResponse updateElementWithNoAuthorizationPutRequest(String url, String aasJsonContent) throws IOException {
		return BaSyxHttpTestUtils.executePutOnURL(url, aasJsonContent);
	}
	
	private CloseableHttpResponse updateElementWithNoAuthorizationPostRequest(String url, String aasJsonContent) throws IOException {
		return BaSyxHttpTestUtils.executePostOnURL(url, aasJsonContent);
	}
	
	private CloseableHttpResponse updateElementWithFileWithAuthorization(String url, String shellId, String fileName, File file, String accessToken) throws IOException {
		CloseableHttpClient client = HttpClients.createDefault();
		
		HttpPut putRequest = BaSyxHttpTestUtils.createPutRequestWithFileWithAuthorization(url, fileName, file, accessToken);
		
		return BaSyxHttpTestUtils.executePutRequest(client, putRequest);
	}
	
	private CloseableHttpResponse updateElementWithFileWithNoAuthorization(String url, String fileName, File file) throws IOException {
		CloseableHttpClient client = HttpClients.createDefault();
		
		HttpPut putRequest = BaSyxHttpTestUtils.createPutRequestWithFile(url, fileName, file);
		
		return BaSyxHttpTestUtils.executePutRequest(client, putRequest);
	}
	
	private CloseableHttpResponse deleteElementWithAuthorization(String url, String accessToken) throws IOException {
		return BaSyxHttpTestUtils.executeAuthorizedDeleteOnURL(url, accessToken);
	}
	
	private CloseableHttpResponse deleteElementWithNoAuthorization(String url) throws IOException {
		return BaSyxHttpTestUtils.executeDeleteOnURL(url);
	}
	
	private static String getAasJSONString(String fileName) throws FileNotFoundException, IOException {
		return BaSyxHttpTestUtils.readJSONStringFromClasspath(fileName);
	}

	private static String getAdminAccessToken() {
		DummyCredential dummyCredential = DummyCredentialStore.ADMIN_CREDENTIAL;
		
		return tokenProvider.getAccessToken(dummyCredential.getUsername(), dummyCredential.getPassword());
	}

	private String getSpecificSubmodelReferenceUrl(String shellId, String submodelId) {
		Base64UrlEncodedIdentifier identifier = new Base64UrlEncodedIdentifier(submodelId);
		return getSpecificAasSubmodelRefAccessURL(shellId) + "/" + identifier.getEncodedIdentifier();
	}
	
	private static String getThumbnailAccessURL(String aasId) {
		Base64UrlEncodedIdentifier identifier = new Base64UrlEncodedIdentifier(aasId);
		return createAasRepositoryUrl(aasRepositoryBaseUrl) + "/" + identifier.getEncodedIdentifier() + "/asset-information/thumbnail";
	}
	
	private static String createAasRepositoryUrl(String aasRepositoryBaseURL) {

		try {
			return new URL(new URL(aasRepositoryBaseURL), AAS_REPOSITORY_PATH).toString();
		} catch (MalformedURLException e) {
			throw new RuntimeException("The AAS Repository Base url is malformed. " + e.getMessage());
		}
	}
}

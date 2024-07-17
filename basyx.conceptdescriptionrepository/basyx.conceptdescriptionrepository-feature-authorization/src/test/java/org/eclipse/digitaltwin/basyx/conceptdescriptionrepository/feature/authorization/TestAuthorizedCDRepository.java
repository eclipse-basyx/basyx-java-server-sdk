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

package org.eclipse.digitaltwin.basyx.conceptdescriptionrepository.feature.authorization;

import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.interfaces.RSAPublicKey;

import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.ParseException;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.core.DeserializationException;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.json.JsonDeserializer;
import org.eclipse.digitaltwin.aas4j.v3.model.ConceptDescription;
import org.eclipse.digitaltwin.basyx.authorization.AccessTokenProvider;
import org.eclipse.digitaltwin.basyx.authorization.DummyCredential;
import org.eclipse.digitaltwin.basyx.authorization.DummyCredentialStore;
import org.eclipse.digitaltwin.basyx.authorization.jwt.JwtTokenDecoder;
import org.eclipse.digitaltwin.basyx.authorization.jwt.PublicKeyUtils;
import org.eclipse.digitaltwin.basyx.conceptdescriptionrepository.ConceptDescriptionRepository;
import org.eclipse.digitaltwin.basyx.http.Base64UrlEncodedIdentifier;
import org.eclipse.digitaltwin.basyx.http.serialization.BaSyxHttpTestUtils;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

/**
 * Integration test for {@link AuthorizedConceptDescriptionRepository} feature
 * 
 * @author danish
 */
public class TestAuthorizedCDRepository {

	private static final String CONCEPT_DESCRIPTION_REPOSITORY_PATH = "/concept-descriptions";
	private static final String CONCEPT_DESCRIPTION_SIMPLE_2_JSON = "authorization/ConceptDescriptionSimple_2.json";
	private static final String CONCEPT_DESCRIPTION_SIMPLE_1_JSON = "authorization/ConceptDescriptionSimple_1.json";
	private static final String SPECIFIC_CONCEPT_DESCRIPTION_ID_2 = "specificConceptDescriptionId-2";
	private static final String SPECIFIC_CONCEPT_DESCRIPTION_ID = "specificConceptDescriptionId";
	public static String authenticaltionServerTokenEndpoint = "http://localhost:9096/realms/BaSyx/protocol/openid-connect/token";
	public static String conceptDescriptionRepositoryBaseUrl = "http://127.0.0.1:8081";
	public static String healthEndpointUrl = "http://127.0.0.1:8081/actuator/health";
	public static String clientId = "basyx-client-api";
	private static AccessTokenProvider tokenProvider;
	private static ConceptDescriptionRepository cdRepo;
	private static ConfigurableApplicationContext appContext;
	
	@BeforeClass
	public static void setUp() throws FileNotFoundException, IOException, DeserializationException {
		tokenProvider = new AccessTokenProvider(authenticaltionServerTokenEndpoint, clientId);

		appContext = new SpringApplicationBuilder(DummyAuthorizedCDRepositoryComponent.class).profiles("authorization").run(new String[] {});

		cdRepo = appContext.getBean(ConceptDescriptionRepository.class);
		
		initializeRepository();
	}
	
	@Test
	public void healthEndpointWithoutAuthorization() throws IOException, ParseException {
		String expectedHealthEndpointOutput = getStringFromFile("authorization/HealthOutput.json");
		
		CloseableHttpResponse healthCheckResponse = BaSyxHttpTestUtils.executeGetOnURL(healthEndpointUrl);
		assertEquals(HttpStatus.OK.value(), healthCheckResponse.getCode());
		
		BaSyxHttpTestUtils.assertSameJSONContent(expectedHealthEndpointOutput, BaSyxHttpTestUtils.getResponseAsString(healthCheckResponse));
	}

	@Test
	public void getAllCDWithCorrectRoleAndPermission() throws IOException {
		DummyCredential dummyCredential = DummyCredentialStore.BASYX_READER_CREDENTIAL;
		
		String accessToken = tokenProvider.getAccessToken(dummyCredential.getUsername(), dummyCredential.getPassword());
		
		CloseableHttpResponse retrievalResponse = getAllCDWithAuthorization(accessToken);
		assertEquals(HttpStatus.OK.value(), retrievalResponse.getCode());
	}
	
	@Test
	public void getAllCDWithInsufficientPermissionRole() throws IOException {
		DummyCredential dummyCredential = DummyCredentialStore.BASYX_UPDATER_CREDENTIAL;
		
		String accessToken = tokenProvider.getAccessToken(dummyCredential.getUsername(), dummyCredential.getPassword());
		
		CloseableHttpResponse retrievalResponse = getAllCDWithAuthorization(accessToken);
		assertEquals(HttpStatus.FORBIDDEN.value(), retrievalResponse.getCode());
	}
	
	@Test
	public void getAllCDWithNoAuthorization() throws IOException {
		CloseableHttpResponse retrievalResponse = getAllCDNoAuthorization();
		assertEquals(HttpStatus.UNAUTHORIZED.value(), retrievalResponse.getCode());
	}
	
	@Test
	public void getCDWithCorrectRoleAndPermission() throws IOException {
		DummyCredential dummyCredential = DummyCredentialStore.BASYX_READER_CREDENTIAL;
		
		String accessToken = tokenProvider.getAccessToken(dummyCredential.getUsername(), dummyCredential.getPassword());
		
		CloseableHttpResponse retrievalResponse = getElementWithAuthorization(getSpecificCDAccessURL(SPECIFIC_CONCEPT_DESCRIPTION_ID), accessToken);
		assertEquals(HttpStatus.OK.value(), retrievalResponse.getCode());
	}
	
	@Test
	public void getCDWithCorrectRoleAndSpecificCDPermission() throws IOException {
		DummyCredential dummyCredential = DummyCredentialStore.BASYX_READER_TWO_CREDENTIAL;
		
		String accessToken = tokenProvider.getAccessToken(dummyCredential.getUsername(), dummyCredential.getPassword());
		
		CloseableHttpResponse retrievalResponse = getElementWithAuthorization(getSpecificCDAccessURL(SPECIFIC_CONCEPT_DESCRIPTION_ID), accessToken);
		assertEquals(HttpStatus.OK.value(), retrievalResponse.getCode());
	}
	
	@Test
	public void getCDWithCorrectRoleAndUnauthorizedSpecificCD() throws IOException {
		DummyCredential dummyCredential = DummyCredentialStore.BASYX_READER_TWO_CREDENTIAL;
		
		String accessToken = tokenProvider.getAccessToken(dummyCredential.getUsername(), dummyCredential.getPassword());
		
		CloseableHttpResponse retrievalResponse = getElementWithAuthorization(getSpecificCDAccessURL(SPECIFIC_CONCEPT_DESCRIPTION_ID_2), accessToken);
		assertEquals(HttpStatus.FORBIDDEN.value(), retrievalResponse.getCode());
	}
	
	@Test
	public void getCDWithInsufficientPermissionRole() throws IOException {
		DummyCredential dummyCredential = DummyCredentialStore.BASYX_UPDATER_CREDENTIAL;
		
		String accessToken = tokenProvider.getAccessToken(dummyCredential.getUsername(), dummyCredential.getPassword());
		
		CloseableHttpResponse retrievalResponse = getElementWithAuthorization(getSpecificCDAccessURL(SPECIFIC_CONCEPT_DESCRIPTION_ID), accessToken);
		assertEquals(HttpStatus.FORBIDDEN.value(), retrievalResponse.getCode());
	}
	
	@Test
	public void getCDWithNoAuthorization() throws IOException {
		CloseableHttpResponse retrievalResponse = getElementWithNoAuthorization(getSpecificCDAccessURL(SPECIFIC_CONCEPT_DESCRIPTION_ID));
		assertEquals(HttpStatus.UNAUTHORIZED.value(), retrievalResponse.getCode());
	}
	
	@Test
	public void createCDWithCorrectRoleAndPermission() throws IOException {
		String accessToken = getAccessToken(DummyCredentialStore.BASYX_CREATOR_CREDENTIAL);
		
		CloseableHttpResponse retrievalResponse = createConceptDescriptionOnRepositoryWithAuthorization(getStringFromFile(CONCEPT_DESCRIPTION_SIMPLE_2_JSON), accessToken);
		assertEquals(HttpStatus.CREATED.value(), retrievalResponse.getCode());
		
		deleteElementWithAuthorization(getSpecificCDAccessURL(SPECIFIC_CONCEPT_DESCRIPTION_ID_2), getAccessToken(DummyCredentialStore.ADMIN_CREDENTIAL));
	}
	
	@Test
	public void createCDWithInsufficientPermissionRole() throws IOException {
		String accessToken = getAccessToken(DummyCredentialStore.BASYX_READER_CREDENTIAL);
		
		CloseableHttpResponse retrievalResponse = createConceptDescriptionOnRepositoryWithAuthorization(getStringFromFile(CONCEPT_DESCRIPTION_SIMPLE_2_JSON), accessToken);
		assertEquals(HttpStatus.FORBIDDEN.value(), retrievalResponse.getCode());
	}
	
	@Test
	public void createCDWithNoAuthorization() throws IOException {
		CloseableHttpResponse retrievalResponse = createConceptDescriptionOnRepositoryWithNoAuthorization(getStringFromFile(CONCEPT_DESCRIPTION_SIMPLE_2_JSON));
		
		assertEquals(HttpStatus.UNAUTHORIZED.value(), retrievalResponse.getCode());
	}
	
	@Test
	public void updateCDWithCorrectRoleAndPermission() throws IOException {
		String accessToken = getAccessToken(DummyCredentialStore.BASYX_UPDATER_CREDENTIAL);
		
		CloseableHttpResponse retrievalResponse = updateElementWithAuthorizationPutRequest(getSpecificCDAccessURL(SPECIFIC_CONCEPT_DESCRIPTION_ID), getStringFromFile(CONCEPT_DESCRIPTION_SIMPLE_1_JSON), accessToken);
		assertEquals(HttpStatus.NO_CONTENT.value(), retrievalResponse.getCode());
	}
	
	@Test
	public void updateCDWithCorrectRoleAndSpecificCDPermission() throws IOException {		
		String accessToken = getAccessToken(DummyCredentialStore.BASYX_UPDATER_TWO_CREDENTIAL);
		
		CloseableHttpResponse retrievalResponse = updateElementWithAuthorizationPutRequest(getSpecificCDAccessURL(SPECIFIC_CONCEPT_DESCRIPTION_ID), getStringFromFile(CONCEPT_DESCRIPTION_SIMPLE_1_JSON), accessToken);
		assertEquals(HttpStatus.NO_CONTENT.value(), retrievalResponse.getCode());
	}
	
	@Test
	public void updateCDWithCorrectRoleAndUnauthorizedSpecificCD() throws IOException {
		String accessToken = getAccessToken(DummyCredentialStore.BASYX_UPDATER_TWO_CREDENTIAL);
		
		CloseableHttpResponse retrievalResponse = updateElementWithAuthorizationPutRequest(getSpecificCDAccessURL(SPECIFIC_CONCEPT_DESCRIPTION_ID_2), getStringFromFile(CONCEPT_DESCRIPTION_SIMPLE_1_JSON), accessToken);
		assertEquals(HttpStatus.FORBIDDEN.value(), retrievalResponse.getCode());
	}
	
	@Test
	public void updateCDWithInsufficientPermissionRole() throws IOException {
		String accessToken = getAccessToken(DummyCredentialStore.BASYX_READER_CREDENTIAL);
		
		CloseableHttpResponse retrievalResponse = updateElementWithAuthorizationPutRequest(getSpecificCDAccessURL(SPECIFIC_CONCEPT_DESCRIPTION_ID), getStringFromFile(CONCEPT_DESCRIPTION_SIMPLE_1_JSON), accessToken);
		assertEquals(HttpStatus.FORBIDDEN.value(), retrievalResponse.getCode());
	}
	
	@Test
	public void updateCDWithNoAuthorization() throws IOException {
		CloseableHttpResponse retrievalResponse = updateElementWithNoAuthorizationPutRequest(getSpecificCDAccessURL(SPECIFIC_CONCEPT_DESCRIPTION_ID), getStringFromFile(CONCEPT_DESCRIPTION_SIMPLE_1_JSON));
		
		assertEquals(HttpStatus.UNAUTHORIZED.value(), retrievalResponse.getCode());
	}

	@Test
	public void deleteCDWithCorrectRoleAndPermission() throws IOException {
		createConceptDescriptionOnRepositoryWithAuthorization(getStringFromFile(CONCEPT_DESCRIPTION_SIMPLE_2_JSON), getAccessToken(DummyCredentialStore.ADMIN_CREDENTIAL));
		
		String accessToken = getAccessToken(DummyCredentialStore.BASYX_DELETER_CREDENTIAL);
		
		CloseableHttpResponse retrievalResponse = deleteElementWithAuthorization(getSpecificCDAccessURL(SPECIFIC_CONCEPT_DESCRIPTION_ID_2), accessToken);
		assertEquals(HttpStatus.NO_CONTENT.value(), retrievalResponse.getCode());
		
		assertElementIsNotOnServer(getSpecificCDAccessURL(SPECIFIC_CONCEPT_DESCRIPTION_ID_2), getAccessToken(DummyCredentialStore.ADMIN_CREDENTIAL));
		
		deleteElementWithAuthorization(getSpecificCDAccessURL(SPECIFIC_CONCEPT_DESCRIPTION_ID_2), getAccessToken(DummyCredentialStore.ADMIN_CREDENTIAL));
	}
	
	@Test
	public void deleteCDWithCorrectRoleAndSpecificCDPermission() throws IOException {
		createConceptDescriptionOnRepositoryWithAuthorization(getStringFromFile(CONCEPT_DESCRIPTION_SIMPLE_2_JSON), getAccessToken(DummyCredentialStore.ADMIN_CREDENTIAL));
		
		String accessToken = getAccessToken(DummyCredentialStore.BASYX_DELETER_TWO_CREDENTIAL);
		
		CloseableHttpResponse retrievalResponse = deleteElementWithAuthorization(getSpecificCDAccessURL(SPECIFIC_CONCEPT_DESCRIPTION_ID_2), accessToken);
		assertEquals(HttpStatus.NO_CONTENT.value(), retrievalResponse.getCode());
		
		assertElementIsNotOnServer(getSpecificCDAccessURL(SPECIFIC_CONCEPT_DESCRIPTION_ID_2), getAccessToken(DummyCredentialStore.ADMIN_CREDENTIAL));
		
		deleteElementWithAuthorization(getSpecificCDAccessURL(SPECIFIC_CONCEPT_DESCRIPTION_ID_2), getAccessToken(DummyCredentialStore.ADMIN_CREDENTIAL));
	}
	
	@Test
	public void deleteCDWithCorrectRoleAndUnauthorizedSpecificCD() throws IOException {
		String accessToken = getAccessToken(DummyCredentialStore.BASYX_DELETER_TWO_CREDENTIAL);
		
		CloseableHttpResponse retrievalResponse = deleteElementWithAuthorization(getSpecificCDAccessURL(SPECIFIC_CONCEPT_DESCRIPTION_ID), accessToken);
		assertEquals(HttpStatus.FORBIDDEN.value(), retrievalResponse.getCode());
		
		assertElementExistsOnServer(getSpecificCDAccessURL(SPECIFIC_CONCEPT_DESCRIPTION_ID), getAccessToken(DummyCredentialStore.ADMIN_CREDENTIAL));
	}
	
	@Test
	public void deleteCDWithInsufficientPermissionRole() throws IOException {
		String accessToken = getAccessToken(DummyCredentialStore.BASYX_UPDATER_CREDENTIAL);
		
		CloseableHttpResponse retrievalResponse = deleteElementWithAuthorization(getSpecificCDAccessURL(SPECIFIC_CONCEPT_DESCRIPTION_ID), accessToken);
		assertEquals(HttpStatus.FORBIDDEN.value(), retrievalResponse.getCode());
		
		assertElementExistsOnServer(getSpecificCDAccessURL(SPECIFIC_CONCEPT_DESCRIPTION_ID), getAccessToken(DummyCredentialStore.ADMIN_CREDENTIAL));
	}
	
	@Test
	public void deleteCDWithNoAuthorization() throws IOException {
		CloseableHttpResponse retrievalResponse = deleteElementWithNoAuthorization(getSpecificCDAccessURL(SPECIFIC_CONCEPT_DESCRIPTION_ID));
		
		assertEquals(HttpStatus.UNAUTHORIZED.value(), retrievalResponse.getCode());
		
		assertElementExistsOnServer(getSpecificCDAccessURL(SPECIFIC_CONCEPT_DESCRIPTION_ID), getAccessToken(DummyCredentialStore.ADMIN_CREDENTIAL));
	}
	
	private static void initializeRepository() throws FileNotFoundException, IOException, DeserializationException {
		configureSecurityContext();
		
		ConceptDescription dummyConceptDescription = new JsonDeserializer().read(getStringFromFile(CONCEPT_DESCRIPTION_SIMPLE_1_JSON), ConceptDescription.class);

		cdRepo.createConceptDescription(dummyConceptDescription);

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
	
	private void assertElementIsNotOnServer(String url, String accessToken) throws IOException {
		CloseableHttpResponse getResponse = getElementWithAuthorization(url, accessToken);
		assertEquals(HttpStatus.NOT_FOUND.value(), getResponse.getCode());
	}
	
	private void assertElementExistsOnServer(String url, String accessToken) throws IOException {
		CloseableHttpResponse getResponse = getElementWithAuthorization(url, accessToken);
		assertEquals(HttpStatus.OK.value(), getResponse.getCode());
	}

	private String getAccessToken(DummyCredential dummyCredential) {
		return tokenProvider.getAccessToken(dummyCredential.getUsername(), dummyCredential.getPassword());
	}
	
	protected CloseableHttpResponse getAllCDWithAuthorization(String accessToken) throws IOException {
		return BaSyxHttpTestUtils.executeAuthorizedGetOnURL(createConceptDescriptionRepositoryUrl(conceptDescriptionRepositoryBaseUrl), accessToken);
	}
	
	protected CloseableHttpResponse getAllCDNoAuthorization() throws IOException {
		return BaSyxHttpTestUtils.executeGetOnURL(createConceptDescriptionRepositoryUrl(conceptDescriptionRepositoryBaseUrl));
	}
	
	protected CloseableHttpResponse getElementWithAuthorization(String url, String accessToken) throws IOException {
		return BaSyxHttpTestUtils.executeAuthorizedGetOnURL(url, accessToken);
	}
	
	protected CloseableHttpResponse getElementWithNoAuthorization(String url) throws IOException {
		return BaSyxHttpTestUtils.executeGetOnURL(url);
	}
	
	protected String getSpecificCDAccessURL(String conceptDescriptionId) {
		return createConceptDescriptionRepositoryUrl(createConceptDescriptionRepositoryUrl(conceptDescriptionRepositoryBaseUrl)) + "/" + Base64UrlEncodedIdentifier.encodeIdentifier(conceptDescriptionId);
	}
	
	private static CloseableHttpResponse createConceptDescriptionOnRepositoryWithAuthorization(String jsonContent, String accessToken) throws IOException {
		return BaSyxHttpTestUtils.executeAuthorizedPostOnURL(createConceptDescriptionRepositoryUrl(conceptDescriptionRepositoryBaseUrl), jsonContent, accessToken);
	}
	
	private static CloseableHttpResponse createConceptDescriptionOnRepositoryWithNoAuthorization(String jsonContent) throws IOException {
		return BaSyxHttpTestUtils.executePostOnURL(createConceptDescriptionRepositoryUrl(conceptDescriptionRepositoryBaseUrl), jsonContent);
	}
	
	private CloseableHttpResponse updateElementWithAuthorizationPutRequest(String url, String jsonContent, String accessToken) throws IOException {
		return BaSyxHttpTestUtils.executeAuthorizedPutOnURL(url, jsonContent, accessToken);
	}
	
	private CloseableHttpResponse updateElementWithNoAuthorizationPutRequest(String url, String jsonContent) throws IOException {
		return BaSyxHttpTestUtils.executePutOnURL(url, jsonContent);
	}
	
	private CloseableHttpResponse deleteElementWithAuthorization(String url, String accessToken) throws IOException {
		return BaSyxHttpTestUtils.executeAuthorizedDeleteOnURL(url, accessToken);
	}
	
	private CloseableHttpResponse deleteElementWithNoAuthorization(String url) throws IOException {
		return BaSyxHttpTestUtils.executeDeleteOnURL(url);
	}
	
	private static String getStringFromFile(String fileName) throws FileNotFoundException, IOException {
		return BaSyxHttpTestUtils.readJSONStringFromClasspath(fileName);
	}

	private static String getAdminAccessToken() {
		DummyCredential dummyCredential = DummyCredentialStore.ADMIN_CREDENTIAL;
		
		return tokenProvider.getAccessToken(dummyCredential.getUsername(), dummyCredential.getPassword());
	}

	private static String createConceptDescriptionRepositoryUrl(String aasRepositoryBaseURL) {

		try {
			return new URL(new URL(aasRepositoryBaseURL), CONCEPT_DESCRIPTION_REPOSITORY_PATH).toString();
		} catch (MalformedURLException e) {
			throw new RuntimeException("The Concept Description Repository Base URL is malformed. " + e.getMessage());
		}
	}
	
}

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

package org.eclipse.digitaltwin.basyx.submodelrepository.feature.authorization;

import static org.junit.Assert.assertEquals;

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
import org.eclipse.digitaltwin.aas4j.v3.dataformat.core.DeserializationException;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.json.JsonDeserializer;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.basyx.authorization.AccessTokenProvider;
import org.eclipse.digitaltwin.basyx.authorization.DummyCredential;
import org.eclipse.digitaltwin.basyx.authorization.DummyCredentialStore;
import org.eclipse.digitaltwin.basyx.authorization.jwt.JwtTokenDecoder;
import org.eclipse.digitaltwin.basyx.authorization.jwt.PublicKeyUtils;
import org.eclipse.digitaltwin.basyx.http.Base64UrlEncodedIdentifier;
import org.eclipse.digitaltwin.basyx.http.serialization.BaSyxHttpTestUtils;
import org.eclipse.digitaltwin.basyx.submodelrepository.SubmodelRepository;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.util.ResourceUtils;

/**
 * Integration test for {@link AuthorizedSubmodelRepository} feature
 * 
 * @author danish
 */
public class TestAuthorizedSubmodelRepository {

	private static final String SUBMODEL_REPOSITORY_PATH = "/submodels";
	private static final String SUBMODEL_SIMPLE_2_JSON = "authorization/SubmodelSimple_2.json";
	private static final String SUBMODEL_SIMPLE_1_JSON = "authorization/SubmodelSimple_1.json";
	private static final String SPECIFIC_SUBMODEL_ID_2 = "specificSubmodelId-2";
	private static final String SPECIFIC_SUBMODEL_ID = "specificSubmodelId";
	private static final String PARENT_SUBMODEL_ELEMENT_IDSHORT = "smc2";
	private static final String SUBMODEL_ELEMENT_IDSHORT_PATH_2 = "smc1.specificSubmodelElementIdShort-2";
	private static final String SUBMODEL_ELEMENT_IDSHORT_PATH = PARENT_SUBMODEL_ELEMENT_IDSHORT + ".specificSubmodelElementIdShort";

	private static final String OPERATION_SQUARE_SUBMODEL_ELEMENT_IDSHORT = "square";
	private static final String OPERATION_CUBE_SUBMODEL_ELEMENT_IDSHORT = "cube";
	private static final String OPERATION_SUBMODEL_ELEMENT_IDSHORT_PATH = "smc1." + OPERATION_SQUARE_SUBMODEL_ELEMENT_IDSHORT;

	private static final String NEW_SUBMODEL_ELEMENT_IDSHORT = "specificSMEProperty";
	private static final String NEW_SUBMODEL_ELEMENT_IDSHORT_PATH = PARENT_SUBMODEL_ELEMENT_IDSHORT + "." + NEW_SUBMODEL_ELEMENT_IDSHORT;
	private static final String FILE_SUBMODEL_ELEMENT_IDSHORT_PATH = PARENT_SUBMODEL_ELEMENT_IDSHORT + ".specificFileSubmodelElementIdShort";
	private static final String FILE_SUBMODEL_ELEMENT_IDSHORT_PATH_2 = PARENT_SUBMODEL_ELEMENT_IDSHORT + ".specificFileSubmodelElementIdShort-2";
	public static String authenticaltionServerTokenEndpoint = "http://localhost:9096/realms/BaSyx/protocol/openid-connect/token";
	public static String submodelRepositoryBaseUrl = "http://127.0.0.1:8081";
	public static String clientId = "basyx-client-api";
	public static String healthEndpointUrl = "http://127.0.0.1:8081/actuator/health";
	private static AccessTokenProvider tokenProvider;
	private static final String FILE_NAME = "BaSyx-Logo.png";
	private static SubmodelRepository submodelRepo;
	private static ConfigurableApplicationContext appContext;

	@BeforeClass
	public static void setUp() throws FileNotFoundException, IOException, DeserializationException {
		tokenProvider = new AccessTokenProvider(authenticaltionServerTokenEndpoint, clientId);

		appContext = new SpringApplicationBuilder(DummyAuthorizedSubmodelRepositoryComponent.class).profiles("authorization").run(new String[] {});

		submodelRepo = appContext.getBean(SubmodelRepository.class);
		
		initializeRepository();
	}

	@Test
	public void healthEndpointWithoutAuthorization() throws IOException, ParseException {
		String expectedHealthEndpointOutput = getJSONValueAsString("authorization/HealthOutput.json");

		CloseableHttpResponse healthCheckResponse = BaSyxHttpTestUtils.executeGetOnURL(healthEndpointUrl);
		assertEquals(HttpStatus.OK.value(), healthCheckResponse.getCode());

		BaSyxHttpTestUtils.assertSameJSONContent(expectedHealthEndpointOutput, BaSyxHttpTestUtils.getResponseAsString(healthCheckResponse));
	}

	@Test
	public void getAllSubmodelsWithCorrectRoleAndPermission() throws IOException {
		DummyCredential dummyCredential = DummyCredentialStore.BASYX_READER_CREDENTIAL;

		String accessToken = tokenProvider.getAccessToken(dummyCredential.getUsername(), dummyCredential.getPassword());

		CloseableHttpResponse retrievalResponse = getAllSubmodelsWithAuthorization(accessToken);
		assertEquals(HttpStatus.OK.value(), retrievalResponse.getCode());
	}

	@Test
	public void getAllSubmodelsWithInsufficientPermissionRole() throws IOException {
		DummyCredential dummyCredential = DummyCredentialStore.BASYX_UPDATER_CREDENTIAL;

		String accessToken = tokenProvider.getAccessToken(dummyCredential.getUsername(), dummyCredential.getPassword());

		CloseableHttpResponse retrievalResponse = getAllSubmodelsWithAuthorization(accessToken);
		assertEquals(HttpStatus.FORBIDDEN.value(), retrievalResponse.getCode());
	}

	@Test
	public void getAllSubmodelsWithNoAuthorization() throws IOException {
		CloseableHttpResponse retrievalResponse = getAllSubmodelsNoAuthorization();
		assertEquals(HttpStatus.UNAUTHORIZED.value(), retrievalResponse.getCode());
	}

	@Test
	public void getSubmodelWithCorrectRoleAndPermission() throws IOException {
		DummyCredential dummyCredential = DummyCredentialStore.BASYX_READER_CREDENTIAL;

		String accessToken = tokenProvider.getAccessToken(dummyCredential.getUsername(), dummyCredential.getPassword());

		CloseableHttpResponse retrievalResponse = getElementWithAuthorization(getSpecificSubmodelAccessURL(SPECIFIC_SUBMODEL_ID), accessToken);
		assertEquals(HttpStatus.OK.value(), retrievalResponse.getCode());
	}

	@Test
	public void getSubmodelWithCorrectRoleAndSpecificSubmodelPermission() throws IOException {
		DummyCredential dummyCredential = DummyCredentialStore.BASYX_READER_TWO_CREDENTIAL;

		String accessToken = tokenProvider.getAccessToken(dummyCredential.getUsername(), dummyCredential.getPassword());

		CloseableHttpResponse retrievalResponse = getElementWithAuthorization(getSpecificSubmodelAccessURL(SPECIFIC_SUBMODEL_ID), accessToken);
		assertEquals(HttpStatus.OK.value(), retrievalResponse.getCode());
	}

	@Test
	public void getSubmodelWithCorrectRoleAndUnauthorizedSpecificSubmodel() throws IOException {
		DummyCredential dummyCredential = DummyCredentialStore.BASYX_READER_TWO_CREDENTIAL;

		String accessToken = tokenProvider.getAccessToken(dummyCredential.getUsername(), dummyCredential.getPassword());

		CloseableHttpResponse retrievalResponse = getElementWithAuthorization(getSpecificSubmodelAccessURL(SPECIFIC_SUBMODEL_ID_2), accessToken);
		assertEquals(HttpStatus.FORBIDDEN.value(), retrievalResponse.getCode());
	}

	@Test
	public void getSubmodelWithInsufficientPermissionRole() throws IOException {
		DummyCredential dummyCredential = DummyCredentialStore.BASYX_UPDATER_CREDENTIAL;

		String accessToken = tokenProvider.getAccessToken(dummyCredential.getUsername(), dummyCredential.getPassword());

		CloseableHttpResponse retrievalResponse = getElementWithAuthorization(getSpecificSubmodelAccessURL(SPECIFIC_SUBMODEL_ID), accessToken);
		assertEquals(HttpStatus.FORBIDDEN.value(), retrievalResponse.getCode());
	}

	@Test
	public void getSubmodelWithNoAuthorization() throws IOException {
		CloseableHttpResponse retrievalResponse = getElementWithNoAuthorization(getSpecificSubmodelAccessURL(SPECIFIC_SUBMODEL_ID));
		assertEquals(HttpStatus.UNAUTHORIZED.value(), retrievalResponse.getCode());
	}

	@Test
	public void getSubmodelWithCorrectRoleAndSpecificSubmodelElementPermission() throws IOException {
		DummyCredential dummyCredential = DummyCredentialStore.BASYX_SME_READER_CREDENTIAL;

		String accessToken = tokenProvider.getAccessToken(dummyCredential.getUsername(), dummyCredential.getPassword());

		CloseableHttpResponse retrievalResponse = getElementWithAuthorization(getSpecificSubmodelAccessURL(SPECIFIC_SUBMODEL_ID), accessToken);
		assertEquals(HttpStatus.FORBIDDEN.value(), retrievalResponse.getCode());
	}

	@Test
	public void createSubmodelWithCorrectRoleAndPermission() throws IOException {
		String accessToken = getAccessToken(DummyCredentialStore.BASYX_CREATOR_CREDENTIAL);

		CloseableHttpResponse retrievalResponse = createElementOnRepositoryWithAuthorization(createSubmodelRepositoryUrl(submodelRepositoryBaseUrl), getStringFromFile(SUBMODEL_SIMPLE_2_JSON), accessToken);
		assertEquals(HttpStatus.CREATED.value(), retrievalResponse.getCode());

		deleteElementWithAuthorization(getSpecificSubmodelAccessURL(SPECIFIC_SUBMODEL_ID_2), getAccessToken(DummyCredentialStore.ADMIN_CREDENTIAL));
	}

	@Test
	public void createSubmodelWithInsufficientPermissionRole() throws IOException {
		String accessToken = getAccessToken(DummyCredentialStore.BASYX_READER_CREDENTIAL);

		CloseableHttpResponse retrievalResponse = createElementOnRepositoryWithAuthorization(createSubmodelRepositoryUrl(submodelRepositoryBaseUrl), getStringFromFile(SUBMODEL_SIMPLE_2_JSON), accessToken);
		assertEquals(HttpStatus.FORBIDDEN.value(), retrievalResponse.getCode());
	}

	@Test
	public void createSubmodelWithNoAuthorization() throws IOException {
		CloseableHttpResponse retrievalResponse = createElementOnRepositoryWithNoAuthorization(createSubmodelRepositoryUrl(submodelRepositoryBaseUrl), getStringFromFile(SUBMODEL_SIMPLE_2_JSON));

		assertEquals(HttpStatus.UNAUTHORIZED.value(), retrievalResponse.getCode());
	}

	@Test
	public void updateSubmodelWithCorrectRoleAndPermission() throws IOException {
		String accessToken = getAccessToken(DummyCredentialStore.BASYX_UPDATER_CREDENTIAL);

		CloseableHttpResponse retrievalResponse = updateElementWithAuthorizationPutRequest(getSpecificSubmodelAccessURL(SPECIFIC_SUBMODEL_ID), getStringFromFile(SUBMODEL_SIMPLE_1_JSON), accessToken);
		assertEquals(HttpStatus.NO_CONTENT.value(), retrievalResponse.getCode());
	}

	@Test
	public void updateSubmodelWithCorrectRoleAndSpecificSubmodelPermission() throws IOException {
		String accessToken = getAccessToken(DummyCredentialStore.BASYX_UPDATER_TWO_CREDENTIAL);

		CloseableHttpResponse retrievalResponse = updateElementWithAuthorizationPutRequest(getSpecificSubmodelAccessURL(SPECIFIC_SUBMODEL_ID), getStringFromFile(SUBMODEL_SIMPLE_1_JSON), accessToken);
		assertEquals(HttpStatus.NO_CONTENT.value(), retrievalResponse.getCode());
	}

	@Test
	public void updateSubmodelWithCorrectRoleAndUnauthorizedSpecificSubmodel() throws IOException {
		String accessToken = getAccessToken(DummyCredentialStore.BASYX_UPDATER_TWO_CREDENTIAL);

		CloseableHttpResponse retrievalResponse = updateElementWithAuthorizationPutRequest(getSpecificSubmodelAccessURL(SPECIFIC_SUBMODEL_ID_2), getStringFromFile(SUBMODEL_SIMPLE_1_JSON), accessToken);
		assertEquals(HttpStatus.FORBIDDEN.value(), retrievalResponse.getCode());
	}

	@Test
	public void updateSubmodelWithInsufficientPermissionRole() throws IOException {
		String accessToken = getAccessToken(DummyCredentialStore.BASYX_READER_CREDENTIAL);

		CloseableHttpResponse retrievalResponse = updateElementWithAuthorizationPutRequest(getSpecificSubmodelAccessURL(SPECIFIC_SUBMODEL_ID), getStringFromFile(SUBMODEL_SIMPLE_1_JSON), accessToken);
		assertEquals(HttpStatus.FORBIDDEN.value(), retrievalResponse.getCode());
	}

	@Test
	public void updateSubmodelWithNoAuthorization() throws IOException {
		CloseableHttpResponse retrievalResponse = updateElementWithNoAuthorizationPutRequest(getSpecificSubmodelAccessURL(SPECIFIC_SUBMODEL_ID), getStringFromFile(SUBMODEL_SIMPLE_1_JSON));

		assertEquals(HttpStatus.UNAUTHORIZED.value(), retrievalResponse.getCode());
	}

	@Test
	public void deleteSubmodelWithCorrectRoleAndPermission() throws IOException {
		createElementOnRepositoryWithAuthorization(createSubmodelRepositoryUrl(submodelRepositoryBaseUrl), getStringFromFile(SUBMODEL_SIMPLE_2_JSON), getAccessToken(DummyCredentialStore.ADMIN_CREDENTIAL));

		String accessToken = getAccessToken(DummyCredentialStore.BASYX_DELETER_CREDENTIAL);

		CloseableHttpResponse retrievalResponse = deleteElementWithAuthorization(getSpecificSubmodelAccessURL(SPECIFIC_SUBMODEL_ID_2), accessToken);
		assertEquals(HttpStatus.NO_CONTENT.value(), retrievalResponse.getCode());

		assertElementIsNotOnServer(getSpecificSubmodelAccessURL(SPECIFIC_SUBMODEL_ID_2), getAccessToken(DummyCredentialStore.ADMIN_CREDENTIAL));

		deleteElementWithAuthorization(getSpecificSubmodelAccessURL(SPECIFIC_SUBMODEL_ID_2), getAccessToken(DummyCredentialStore.ADMIN_CREDENTIAL));
	}

	@Test
	public void deleteSubmodelWithCorrectRoleAndSpecificSubmodelPermission() throws IOException {
		createElementOnRepositoryWithAuthorization(createSubmodelRepositoryUrl(submodelRepositoryBaseUrl), getStringFromFile(SUBMODEL_SIMPLE_2_JSON), getAccessToken(DummyCredentialStore.ADMIN_CREDENTIAL));

		String accessToken = getAccessToken(DummyCredentialStore.BASYX_DELETER_TWO_CREDENTIAL);

		CloseableHttpResponse retrievalResponse = deleteElementWithAuthorization(getSpecificSubmodelAccessURL(SPECIFIC_SUBMODEL_ID_2), accessToken);
		assertEquals(HttpStatus.NO_CONTENT.value(), retrievalResponse.getCode());

		assertElementIsNotOnServer(getSpecificSubmodelAccessURL(SPECIFIC_SUBMODEL_ID_2), getAccessToken(DummyCredentialStore.ADMIN_CREDENTIAL));

		deleteElementWithAuthorization(getSpecificSubmodelAccessURL(SPECIFIC_SUBMODEL_ID_2), getAccessToken(DummyCredentialStore.ADMIN_CREDENTIAL));
	}

	@Test
	public void deleteSubmodelWithCorrectRoleAndUnauthorizedSpecificSubmodel() throws IOException {
		String accessToken = getAccessToken(DummyCredentialStore.BASYX_DELETER_TWO_CREDENTIAL);

		CloseableHttpResponse retrievalResponse = deleteElementWithAuthorization(getSpecificSubmodelAccessURL(SPECIFIC_SUBMODEL_ID), accessToken);
		assertEquals(HttpStatus.FORBIDDEN.value(), retrievalResponse.getCode());

		assertElementExistsOnServer(getSpecificSubmodelAccessURL(SPECIFIC_SUBMODEL_ID), getAccessToken(DummyCredentialStore.ADMIN_CREDENTIAL));
	}

	@Test
	public void deleteSubmodelWithInsufficientPermissionRole() throws IOException {
		String accessToken = getAccessToken(DummyCredentialStore.BASYX_UPDATER_CREDENTIAL);

		CloseableHttpResponse retrievalResponse = deleteElementWithAuthorization(getSpecificSubmodelAccessURL(SPECIFIC_SUBMODEL_ID), accessToken);
		assertEquals(HttpStatus.FORBIDDEN.value(), retrievalResponse.getCode());

		assertElementExistsOnServer(getSpecificSubmodelAccessURL(SPECIFIC_SUBMODEL_ID), getAccessToken(DummyCredentialStore.ADMIN_CREDENTIAL));
	}

	@Test
	public void deleteSubmodelWithNoAuthorization() throws IOException {
		CloseableHttpResponse retrievalResponse = deleteElementWithNoAuthorization(getSpecificSubmodelAccessURL(SPECIFIC_SUBMODEL_ID));

		assertEquals(HttpStatus.UNAUTHORIZED.value(), retrievalResponse.getCode());

		assertElementExistsOnServer(getSpecificSubmodelAccessURL(SPECIFIC_SUBMODEL_ID), getAccessToken(DummyCredentialStore.ADMIN_CREDENTIAL));
	}

	@Test
	public void getAllSubmodelElementsWithCorrectRoleAndPermission() throws IOException {
		DummyCredential dummyCredential = DummyCredentialStore.BASYX_READER_CREDENTIAL;

		String accessToken = tokenProvider.getAccessToken(dummyCredential.getUsername(), dummyCredential.getPassword());

		CloseableHttpResponse retrievalResponse = getAllSubmodelElementsWithAuthorization(SPECIFIC_SUBMODEL_ID, accessToken);
		assertEquals(HttpStatus.OK.value(), retrievalResponse.getCode());
	}

	@Test
	public void getAllSubmodelElementsWithAuthorizedSpecificSubmodel() throws IOException {
		DummyCredential dummyCredential = DummyCredentialStore.BASYX_READER_TWO_CREDENTIAL;

		String accessToken = tokenProvider.getAccessToken(dummyCredential.getUsername(), dummyCredential.getPassword());

		CloseableHttpResponse retrievalResponse = getAllSubmodelElementsWithAuthorization(SPECIFIC_SUBMODEL_ID, accessToken);
		assertEquals(HttpStatus.OK.value(), retrievalResponse.getCode());
	}

	@Test
	public void getAllSubmodelElementsWithAuthorizedSpecificSubmodelAndSpecificSME() throws IOException {
		DummyCredential dummyCredential = DummyCredentialStore.BASYX_SME_READER_CREDENTIAL;

		String accessToken = tokenProvider.getAccessToken(dummyCredential.getUsername(), dummyCredential.getPassword());

		CloseableHttpResponse retrievalResponse = getAllSubmodelElementsWithAuthorization(SPECIFIC_SUBMODEL_ID, accessToken);
		assertEquals(HttpStatus.FORBIDDEN.value(), retrievalResponse.getCode());
	}

	@Test
	public void getAllSubmodelElementsWithInsufficientPermissionRole() throws IOException {
		DummyCredential dummyCredential = DummyCredentialStore.BASYX_UPDATER_CREDENTIAL;

		String accessToken = tokenProvider.getAccessToken(dummyCredential.getUsername(), dummyCredential.getPassword());

		CloseableHttpResponse retrievalResponse = getAllSubmodelElementsWithAuthorization(SPECIFIC_SUBMODEL_ID, accessToken);
		assertEquals(HttpStatus.FORBIDDEN.value(), retrievalResponse.getCode());
	}

	@Test
	public void getAllSubmodelElementsWithUnauthorizedSpecificSubmodel() throws IOException {
		DummyCredential dummyCredential = DummyCredentialStore.BASYX_READER_TWO_CREDENTIAL;

		String accessToken = tokenProvider.getAccessToken(dummyCredential.getUsername(), dummyCredential.getPassword());

		CloseableHttpResponse retrievalResponse = getAllSubmodelElementsWithAuthorization(SPECIFIC_SUBMODEL_ID_2, accessToken);
		assertEquals(HttpStatus.FORBIDDEN.value(), retrievalResponse.getCode());
	}

	@Test
	public void getAllSubmodelElementsWithNoAuthorization() throws IOException {
		CloseableHttpResponse retrievalResponse = getAllSubmodelElementsNoAuthorization(SPECIFIC_SUBMODEL_ID);
		assertEquals(HttpStatus.UNAUTHORIZED.value(), retrievalResponse.getCode());
	}

	@Test
	public void getSubmodelElementWithCorrectRoleAndPermission() throws IOException {
		DummyCredential dummyCredential = DummyCredentialStore.BASYX_READER_CREDENTIAL;

		String accessToken = tokenProvider.getAccessToken(dummyCredential.getUsername(), dummyCredential.getPassword());

		CloseableHttpResponse retrievalResponse = getElementWithAuthorization(getSpecificSubmodelElementAccessURL(SPECIFIC_SUBMODEL_ID, SUBMODEL_ELEMENT_IDSHORT_PATH), accessToken);
		assertEquals(HttpStatus.OK.value(), retrievalResponse.getCode());
	}

	@Test
	public void getSubmodelElementWithAuthorizedSpecificSubmodelAndSpecificSME() throws IOException {
		DummyCredential dummyCredential = DummyCredentialStore.BASYX_SME_READER_CREDENTIAL;

		String accessToken = tokenProvider.getAccessToken(dummyCredential.getUsername(), dummyCredential.getPassword());

		CloseableHttpResponse retrievalResponse = getElementWithAuthorization(getSpecificSubmodelElementAccessURL(SPECIFIC_SUBMODEL_ID, SUBMODEL_ELEMENT_IDSHORT_PATH), accessToken);
		assertEquals(HttpStatus.OK.value(), retrievalResponse.getCode());
	}

	@Test
	public void getSubmodelElementWithInsufficientPermissionRole() throws IOException {
		DummyCredential dummyCredential = DummyCredentialStore.BASYX_UPDATER_CREDENTIAL;

		String accessToken = tokenProvider.getAccessToken(dummyCredential.getUsername(), dummyCredential.getPassword());

		CloseableHttpResponse retrievalResponse = getElementWithAuthorization(getSpecificSubmodelElementAccessURL(SPECIFIC_SUBMODEL_ID, SUBMODEL_ELEMENT_IDSHORT_PATH), accessToken);
		assertEquals(HttpStatus.FORBIDDEN.value(), retrievalResponse.getCode());
	}

	@Test
	public void getSubmodelElementWithUnauthorizedSpecificSME() throws IOException {
		DummyCredential dummyCredential = DummyCredentialStore.BASYX_SME_READER_TWO_CREDENTIAL;

		String accessToken = tokenProvider.getAccessToken(dummyCredential.getUsername(), dummyCredential.getPassword());

		CloseableHttpResponse retrievalResponse = getElementWithAuthorization(getSpecificSubmodelElementAccessURL(SPECIFIC_SUBMODEL_ID, SUBMODEL_ELEMENT_IDSHORT_PATH), accessToken);
		assertEquals(HttpStatus.FORBIDDEN.value(), retrievalResponse.getCode());
	}

	@Test
	public void getSubmodelElementWithNoAuthorization() throws IOException {
		CloseableHttpResponse retrievalResponse = getSpecificSubmodelElementNoAuthorization(SPECIFIC_SUBMODEL_ID, SUBMODEL_ELEMENT_IDSHORT_PATH);
		assertEquals(HttpStatus.UNAUTHORIZED.value(), retrievalResponse.getCode());
	}

	@Test
	public void getSubmodelElementValueWithCorrectRoleAndPermission() throws IOException {
		DummyCredential dummyCredential = DummyCredentialStore.BASYX_READER_CREDENTIAL;

		String accessToken = tokenProvider.getAccessToken(dummyCredential.getUsername(), dummyCredential.getPassword());

		CloseableHttpResponse retrievalResponse = getElementWithAuthorization(getSpecificSubmodelElementValueAccessURL(SPECIFIC_SUBMODEL_ID, SUBMODEL_ELEMENT_IDSHORT_PATH), accessToken);
		assertEquals(HttpStatus.OK.value(), retrievalResponse.getCode());
	}

	@Test
	public void getSubmodelElementValueWithAuthorizedSpecificSubmodelAndSpecificSME() throws IOException {
		DummyCredential dummyCredential = DummyCredentialStore.BASYX_SME_READER_CREDENTIAL;

		String accessToken = tokenProvider.getAccessToken(dummyCredential.getUsername(), dummyCredential.getPassword());

		CloseableHttpResponse retrievalResponse = getElementWithAuthorization(getSpecificSubmodelElementValueAccessURL(SPECIFIC_SUBMODEL_ID, SUBMODEL_ELEMENT_IDSHORT_PATH), accessToken);
		assertEquals(HttpStatus.OK.value(), retrievalResponse.getCode());
	}

	@Test
	public void getSubmodelElementValueWithInsufficientPermissionRole() throws IOException {
		DummyCredential dummyCredential = DummyCredentialStore.BASYX_UPDATER_CREDENTIAL;

		String accessToken = tokenProvider.getAccessToken(dummyCredential.getUsername(), dummyCredential.getPassword());

		CloseableHttpResponse retrievalResponse = getElementWithAuthorization(getSpecificSubmodelElementValueAccessURL(SPECIFIC_SUBMODEL_ID, SUBMODEL_ELEMENT_IDSHORT_PATH), accessToken);
		assertEquals(HttpStatus.FORBIDDEN.value(), retrievalResponse.getCode());
	}

	@Test
	public void getSubmodelElementValueWithUnauthorizedSpecificSME() throws IOException {
		DummyCredential dummyCredential = DummyCredentialStore.BASYX_SME_READER_TWO_CREDENTIAL;

		String accessToken = tokenProvider.getAccessToken(dummyCredential.getUsername(), dummyCredential.getPassword());

		CloseableHttpResponse retrievalResponse = getElementWithAuthorization(getSpecificSubmodelElementValueAccessURL(SPECIFIC_SUBMODEL_ID, SUBMODEL_ELEMENT_IDSHORT_PATH), accessToken);
		assertEquals(HttpStatus.FORBIDDEN.value(), retrievalResponse.getCode());
	}

	@Test
	public void getSubmodelElementValueWithNoAuthorization() throws IOException {
		CloseableHttpResponse retrievalResponse = getSpecificSubmodelElementValueNoAuthorization(SPECIFIC_SUBMODEL_ID, SUBMODEL_ELEMENT_IDSHORT_PATH);
		assertEquals(HttpStatus.UNAUTHORIZED.value(), retrievalResponse.getCode());
	}

	@Test
	public void setSubmodelElementValueWithCorrectRoleAndPermission() throws IOException {
		String valueToWrite = getJSONValueAsString("authorization/setFileValue.json");
		DummyCredential dummyCredential = DummyCredentialStore.BASYX_UPDATER_CREDENTIAL;

		String accessToken = tokenProvider.getAccessToken(dummyCredential.getUsername(), dummyCredential.getPassword());

		CloseableHttpResponse retrievalResponse = writeSubmodelElementValueWithAuthorization(getSpecificSubmodelElementValueAccessURL(SPECIFIC_SUBMODEL_ID, FILE_SUBMODEL_ELEMENT_IDSHORT_PATH), valueToWrite, accessToken);
		assertEquals(HttpStatus.NO_CONTENT.value(), retrievalResponse.getCode());
	}

	@Test
	public void setSubmodelElementValueWithAuthorizedSpecificSubmodelAndSpecificSME() throws IOException {
		String valueToWrite = getJSONValueAsString("authorization/setFileValue.json");
		DummyCredential dummyCredential = DummyCredentialStore.BASYX_SME_UPDATER_CREDENTIAL;

		String accessToken = tokenProvider.getAccessToken(dummyCredential.getUsername(), dummyCredential.getPassword());

		CloseableHttpResponse retrievalResponse = writeSubmodelElementValueWithAuthorization(getSpecificSubmodelElementValueAccessURL(SPECIFIC_SUBMODEL_ID, FILE_SUBMODEL_ELEMENT_IDSHORT_PATH), valueToWrite, accessToken);
		assertEquals(HttpStatus.NO_CONTENT.value(), retrievalResponse.getCode());
	}

	@Test
	public void setSubmodelElementValueWithInsufficientPermissionRole() throws IOException {
		String valueToWrite = getJSONValueAsString("authorization/setFileValue.json");
		DummyCredential dummyCredential = DummyCredentialStore.BASYX_READER_CREDENTIAL;

		String accessToken = tokenProvider.getAccessToken(dummyCredential.getUsername(), dummyCredential.getPassword());

		CloseableHttpResponse retrievalResponse = writeSubmodelElementValueWithAuthorization(getSpecificSubmodelElementValueAccessURL(SPECIFIC_SUBMODEL_ID, FILE_SUBMODEL_ELEMENT_IDSHORT_PATH), valueToWrite, accessToken);
		assertEquals(HttpStatus.FORBIDDEN.value(), retrievalResponse.getCode());
	}

	@Test
	public void setSubmodelElementValueWithUnauthorizedSpecificSME() throws IOException {
		String valueToWrite = getJSONValueAsString("authorization/setFileValueRange.json");
		DummyCredential dummyCredential = DummyCredentialStore.BASYX_SME_UPDATER_CREDENTIAL;

		String accessToken = tokenProvider.getAccessToken(dummyCredential.getUsername(), dummyCredential.getPassword());

		CloseableHttpResponse retrievalResponse = writeSubmodelElementValueWithAuthorization(getSpecificSubmodelElementValueAccessURL(SPECIFIC_SUBMODEL_ID, SUBMODEL_ELEMENT_IDSHORT_PATH), valueToWrite, accessToken);
		assertEquals(HttpStatus.FORBIDDEN.value(), retrievalResponse.getCode());
	}

	@Test
	public void setSubmodelElementValueWithNoAuthorization() throws IOException {
		String valueToWrite = getJSONValueAsString("authorization/setFileValue.json");

		CloseableHttpResponse retrievalResponse = writeSubmodelElementValueNoAuthorization(getSpecificSubmodelElementValueAccessURL(SPECIFIC_SUBMODEL_ID, FILE_SUBMODEL_ELEMENT_IDSHORT_PATH), valueToWrite);
		assertEquals(HttpStatus.UNAUTHORIZED.value(), retrievalResponse.getCode());
	}

	@Test
	public void createSubmodelElementWithCorrectRoleAndPermission() throws IOException {
		String element = getJSONValueAsString("authorization/SubmodelElementNew.json");
		DummyCredential dummyCredential = DummyCredentialStore.BASYX_UPDATER_CREDENTIAL;

		String accessToken = tokenProvider.getAccessToken(dummyCredential.getUsername(), dummyCredential.getPassword());

		CloseableHttpResponse retrievalResponse = createElementOnRepositoryWithAuthorization(getAllSubmodelElementsAccessURL(SPECIFIC_SUBMODEL_ID), element, accessToken);
		assertEquals(HttpStatus.CREATED.value(), retrievalResponse.getCode());

		deleteElementWithAuthorization(getSpecificSubmodelElementAccessURL(SPECIFIC_SUBMODEL_ID, NEW_SUBMODEL_ELEMENT_IDSHORT), getAccessToken(DummyCredentialStore.ADMIN_CREDENTIAL));
	}

	@Test
	public void createSubmodelElementAtSpecifiedPathWithAuthorizedSpecificSubmodel() throws IOException {
		String element = getJSONValueAsString("authorization/SubmodelElementNew.json");
		DummyCredential dummyCredential = DummyCredentialStore.BASYX_SME_UPDATER_TWO_CREDENTIAL;

		String accessToken = tokenProvider.getAccessToken(dummyCredential.getUsername(), dummyCredential.getPassword());

		CloseableHttpResponse retrievalResponse = createElementOnRepositoryWithAuthorization(getSpecificSubmodelElementAccessURL(SPECIFIC_SUBMODEL_ID, PARENT_SUBMODEL_ELEMENT_IDSHORT), element, accessToken);
		assertEquals(HttpStatus.CREATED.value(), retrievalResponse.getCode());

		deleteElementWithAuthorization(getSpecificSubmodelElementAccessURL(SPECIFIC_SUBMODEL_ID, NEW_SUBMODEL_ELEMENT_IDSHORT_PATH), getAccessToken(DummyCredentialStore.ADMIN_CREDENTIAL));
	}

	@Test
	public void createSubmodelElementWithInsufficientPermissionRole() throws IOException {
		String element = getJSONValueAsString("authorization/SubmodelElementNew.json");
		DummyCredential dummyCredential = DummyCredentialStore.BASYX_CREATOR_CREDENTIAL;

		String accessToken = tokenProvider.getAccessToken(dummyCredential.getUsername(), dummyCredential.getPassword());

		CloseableHttpResponse retrievalResponse = createElementOnRepositoryWithAuthorization(getSpecificSubmodelElementAccessURL(SPECIFIC_SUBMODEL_ID, PARENT_SUBMODEL_ELEMENT_IDSHORT), element, accessToken);
		assertEquals(HttpStatus.FORBIDDEN.value(), retrievalResponse.getCode());
	}

	@Test
	public void createSubmodelElementWithUnauthorizedSpecificSubmodel() throws IOException {
		String element = getJSONValueAsString("authorization/SubmodelElementNew.json");
		DummyCredential dummyCredential = DummyCredentialStore.BASYX_SME_UPDATER_TWO_CREDENTIAL;

		String accessToken = tokenProvider.getAccessToken(dummyCredential.getUsername(), dummyCredential.getPassword());

		CloseableHttpResponse retrievalResponse = createElementOnRepositoryWithAuthorization(getSpecificSubmodelElementAccessURL(SPECIFIC_SUBMODEL_ID_2, PARENT_SUBMODEL_ELEMENT_IDSHORT), element, accessToken);
		assertEquals(HttpStatus.FORBIDDEN.value(), retrievalResponse.getCode());
	}

	@Test
	public void createSubmodelElementWithNoAuthorization() throws IOException {
		String element = getJSONValueAsString("authorization/SubmodelElementNew.json");

		CloseableHttpResponse retrievalResponse = createElementOnRepositoryWithNoAuthorization(getSpecificSubmodelElementAccessURL(SPECIFIC_SUBMODEL_ID, PARENT_SUBMODEL_ELEMENT_IDSHORT), element);
		assertEquals(HttpStatus.UNAUTHORIZED.value(), retrievalResponse.getCode());
	}

	@Test
	public void updateSubmodelElementWithCorrectRoleAndPermission() throws IOException {
		String element = getJSONValueAsString("authorization/FileSubmodelElementUpdate.json");
		DummyCredential dummyCredential = DummyCredentialStore.BASYX_UPDATER_CREDENTIAL;

		String accessToken = tokenProvider.getAccessToken(dummyCredential.getUsername(), dummyCredential.getPassword());

		CloseableHttpResponse retrievalResponse = updateElementWithAuthorizationPutRequest(getSpecificSubmodelElementAccessURL(SPECIFIC_SUBMODEL_ID, FILE_SUBMODEL_ELEMENT_IDSHORT_PATH), element, accessToken);
		assertEquals(HttpStatus.NO_CONTENT.value(), retrievalResponse.getCode());
	}

	@Test
	public void updateSubmodelElementAtSpecifiedPathWithAuthorizedSpecificSubmodelAndSpecificSME() throws IOException {
		String element = getJSONValueAsString("authorization/FileSubmodelElementUpdate.json");
		DummyCredential dummyCredential = DummyCredentialStore.BASYX_SME_UPDATER_CREDENTIAL;

		String accessToken = tokenProvider.getAccessToken(dummyCredential.getUsername(), dummyCredential.getPassword());

		CloseableHttpResponse retrievalResponse = updateElementWithAuthorizationPutRequest(getSpecificSubmodelElementAccessURL(SPECIFIC_SUBMODEL_ID, FILE_SUBMODEL_ELEMENT_IDSHORT_PATH), element, accessToken);
		assertEquals(HttpStatus.NO_CONTENT.value(), retrievalResponse.getCode());
	}

	@Test
	public void updateSubmodelElementWithInsufficientPermissionRole() throws IOException {
		String element = getJSONValueAsString("authorization/FileSubmodelElementUpdate.json");
		DummyCredential dummyCredential = DummyCredentialStore.BASYX_CREATOR_CREDENTIAL;

		String accessToken = tokenProvider.getAccessToken(dummyCredential.getUsername(), dummyCredential.getPassword());

		CloseableHttpResponse retrievalResponse = updateElementWithAuthorizationPutRequest(getSpecificSubmodelElementAccessURL(SPECIFIC_SUBMODEL_ID, FILE_SUBMODEL_ELEMENT_IDSHORT_PATH), element, accessToken);
		assertEquals(HttpStatus.FORBIDDEN.value(), retrievalResponse.getCode());
	}

	@Test
	public void updateSubmodelElementWithUnauthorizedSpecificSME() throws IOException {
		String element = getJSONValueAsString("authorization/FileSubmodelElementUpdate.json");
		DummyCredential dummyCredential = DummyCredentialStore.BASYX_SME_UPDATER_TWO_CREDENTIAL;

		String accessToken = tokenProvider.getAccessToken(dummyCredential.getUsername(), dummyCredential.getPassword());

		CloseableHttpResponse retrievalResponse = updateElementWithAuthorizationPutRequest(getSpecificSubmodelElementAccessURL(SPECIFIC_SUBMODEL_ID, FILE_SUBMODEL_ELEMENT_IDSHORT_PATH), element, accessToken);
		assertEquals(HttpStatus.FORBIDDEN.value(), retrievalResponse.getCode());
	}

	@Test
	public void updateSubmodelElementWithNoAuthorization() throws IOException {
		String element = getJSONValueAsString("authorization/FileSubmodelElementUpdate.json");

		CloseableHttpResponse retrievalResponse = updateElementWithNoAuthorizationPutRequest(getSpecificSubmodelElementAccessURL(SPECIFIC_SUBMODEL_ID, FILE_SUBMODEL_ELEMENT_IDSHORT_PATH), element);
		assertEquals(HttpStatus.UNAUTHORIZED.value(), retrievalResponse.getCode());
	}

	@Test
	public void deleteSubmodelElementWithCorrectRoleAndPermission() throws IOException {
		createElementOnRepositoryWithAuthorization(createSubmodelRepositoryUrl(submodelRepositoryBaseUrl), getStringFromFile(SUBMODEL_SIMPLE_2_JSON), getAccessToken(DummyCredentialStore.ADMIN_CREDENTIAL));

		String accessToken = getAccessToken(DummyCredentialStore.BASYX_UPDATER_CREDENTIAL);

		CloseableHttpResponse retrievalResponse = deleteElementWithAuthorization(getSpecificSubmodelElementAccessURL(SPECIFIC_SUBMODEL_ID_2, SUBMODEL_ELEMENT_IDSHORT_PATH_2), accessToken);
		assertEquals(HttpStatus.NO_CONTENT.value(), retrievalResponse.getCode());

		assertElementIsNotOnServer(getSpecificSubmodelElementAccessURL(SPECIFIC_SUBMODEL_ID_2, SUBMODEL_ELEMENT_IDSHORT_PATH_2), getAccessToken(DummyCredentialStore.ADMIN_CREDENTIAL));

		deleteElementWithAuthorization(getSpecificSubmodelAccessURL(SPECIFIC_SUBMODEL_ID_2), getAccessToken(DummyCredentialStore.ADMIN_CREDENTIAL));
	}

	@Test
	public void deleteSubmodelElementWithCorrectRoleAndSpecificSMEPermission() throws IOException {
		createElementOnRepositoryWithAuthorization(createSubmodelRepositoryUrl(submodelRepositoryBaseUrl), getStringFromFile(SUBMODEL_SIMPLE_2_JSON), getAccessToken(DummyCredentialStore.ADMIN_CREDENTIAL));

		String accessToken = getAccessToken(DummyCredentialStore.BASYX_SME_UPDATER_THREE_CREDENTIAL);

		CloseableHttpResponse retrievalResponse = deleteElementWithAuthorization(getSpecificSubmodelElementAccessURL(SPECIFIC_SUBMODEL_ID_2, SUBMODEL_ELEMENT_IDSHORT_PATH_2), accessToken);
		assertEquals(HttpStatus.NO_CONTENT.value(), retrievalResponse.getCode());

		assertElementIsNotOnServer(getSpecificSubmodelElementAccessURL(SPECIFIC_SUBMODEL_ID_2, SUBMODEL_ELEMENT_IDSHORT_PATH_2), getAccessToken(DummyCredentialStore.ADMIN_CREDENTIAL));

		deleteElementWithAuthorization(getSpecificSubmodelAccessURL(SPECIFIC_SUBMODEL_ID_2), getAccessToken(DummyCredentialStore.ADMIN_CREDENTIAL));
	}

	@Test
	public void deleteSubmodelElementWithCorrectRoleAndUnauthorizedSpecificSME() throws IOException {
		createElementOnRepositoryWithAuthorization(createSubmodelRepositoryUrl(submodelRepositoryBaseUrl), getStringFromFile(SUBMODEL_SIMPLE_2_JSON), getAccessToken(DummyCredentialStore.ADMIN_CREDENTIAL));

		String accessToken = getAccessToken(DummyCredentialStore.BASYX_SME_UPDATER_THREE_CREDENTIAL);

		CloseableHttpResponse retrievalResponse = deleteElementWithAuthorization(getSpecificSubmodelElementAccessURL(SPECIFIC_SUBMODEL_ID_2, SUBMODEL_ELEMENT_IDSHORT_PATH), accessToken);
		assertEquals(HttpStatus.FORBIDDEN.value(), retrievalResponse.getCode());

		assertElementExistsOnServer(getSpecificSubmodelElementAccessURL(SPECIFIC_SUBMODEL_ID_2, SUBMODEL_ELEMENT_IDSHORT_PATH_2), getAccessToken(DummyCredentialStore.ADMIN_CREDENTIAL));

		deleteElementWithAuthorization(getSpecificSubmodelAccessURL(SPECIFIC_SUBMODEL_ID_2), getAccessToken(DummyCredentialStore.ADMIN_CREDENTIAL));
	}

	@Test
	public void deleteSubmodelElementWithInsufficientPermissionRole() throws IOException {
		createElementOnRepositoryWithAuthorization(createSubmodelRepositoryUrl(submodelRepositoryBaseUrl), getStringFromFile(SUBMODEL_SIMPLE_2_JSON), getAccessToken(DummyCredentialStore.ADMIN_CREDENTIAL));

		String accessToken = getAccessToken(DummyCredentialStore.BASYX_CREATOR_CREDENTIAL);

		CloseableHttpResponse retrievalResponse = deleteElementWithAuthorization(getSpecificSubmodelElementAccessURL(SPECIFIC_SUBMODEL_ID_2, SUBMODEL_ELEMENT_IDSHORT_PATH_2), accessToken);
		assertEquals(HttpStatus.FORBIDDEN.value(), retrievalResponse.getCode());

		assertElementExistsOnServer(getSpecificSubmodelElementAccessURL(SPECIFIC_SUBMODEL_ID_2, SUBMODEL_ELEMENT_IDSHORT_PATH_2), getAccessToken(DummyCredentialStore.ADMIN_CREDENTIAL));

		deleteElementWithAuthorization(getSpecificSubmodelAccessURL(SPECIFIC_SUBMODEL_ID_2), getAccessToken(DummyCredentialStore.ADMIN_CREDENTIAL));
	}

	@Test
	public void deleteSubmodelElementWithNoAuthorization() throws IOException {
		createElementOnRepositoryWithAuthorization(createSubmodelRepositoryUrl(submodelRepositoryBaseUrl), getStringFromFile(SUBMODEL_SIMPLE_2_JSON), getAccessToken(DummyCredentialStore.ADMIN_CREDENTIAL));

		CloseableHttpResponse retrievalResponse = deleteElementWithNoAuthorization(getSpecificSubmodelElementAccessURL(SPECIFIC_SUBMODEL_ID_2, SUBMODEL_ELEMENT_IDSHORT_PATH_2));

		assertEquals(HttpStatus.UNAUTHORIZED.value(), retrievalResponse.getCode());

		assertElementExistsOnServer(getSpecificSubmodelElementAccessURL(SPECIFIC_SUBMODEL_ID_2, SUBMODEL_ELEMENT_IDSHORT_PATH_2), getAccessToken(DummyCredentialStore.ADMIN_CREDENTIAL));

		deleteElementWithAuthorization(getSpecificSubmodelAccessURL(SPECIFIC_SUBMODEL_ID_2), getAccessToken(DummyCredentialStore.ADMIN_CREDENTIAL));
	}

	@Test
	@Ignore
	public void invokeWithCorrectRoleAndPermission() throws IOException, ParseException {
		String parameters = getJSONValueAsString("authorization/parameters.json");
		DummyCredential dummyCredential = DummyCredentialStore.BASYX_EXECUTOR_CREDENTIAL;

		String accessToken = tokenProvider.getAccessToken(dummyCredential.getUsername(), dummyCredential.getPassword());

		CloseableHttpResponse retrievalResponse = requestOperationInvocationWithAuthorization(getInvocationURL(SPECIFIC_SUBMODEL_ID, OPERATION_SUBMODEL_ELEMENT_IDSHORT_PATH), parameters, accessToken);
		assertEquals(HttpStatus.OK.value(), retrievalResponse.getCode());

		String expectedValue = getJSONValueAsString("authorization/result.json");
		BaSyxHttpTestUtils.assertSameJSONContent(expectedValue, BaSyxHttpTestUtils.getResponseAsString(retrievalResponse));
	}

	@Test
	@Ignore
	public void invokeWithAuthorizedSpecificSubmodelAndSpecificSME() throws IOException, ParseException {
		String parameters = getJSONValueAsString("authorization/parameters.json");
		DummyCredential dummyCredential = DummyCredentialStore.BASYX_EXECUTOR_TWO_CREDENTIAL;

		String accessToken = tokenProvider.getAccessToken(dummyCredential.getUsername(), dummyCredential.getPassword());

		CloseableHttpResponse retrievalResponse = requestOperationInvocationWithAuthorization(getInvocationURL(SPECIFIC_SUBMODEL_ID, OPERATION_SUBMODEL_ELEMENT_IDSHORT_PATH), parameters, accessToken);
		assertEquals(HttpStatus.OK.value(), retrievalResponse.getCode());

		String expectedValue = getJSONValueAsString("authorization/result.json");
		BaSyxHttpTestUtils.assertSameJSONContent(expectedValue, BaSyxHttpTestUtils.getResponseAsString(retrievalResponse));
	}

	@Test
	@Ignore
	public void invokeWithInsufficientPermissionRole() throws IOException {
		String parameters = getJSONValueAsString("authorization/parameters.json");
		DummyCredential dummyCredential = DummyCredentialStore.BASYX_UPDATER_CREDENTIAL;

		String accessToken = tokenProvider.getAccessToken(dummyCredential.getUsername(), dummyCredential.getPassword());

		CloseableHttpResponse retrievalResponse = requestOperationInvocationWithAuthorization(getInvocationURL(SPECIFIC_SUBMODEL_ID, OPERATION_SUBMODEL_ELEMENT_IDSHORT_PATH), parameters, accessToken);
		assertEquals(HttpStatus.FORBIDDEN.value(), retrievalResponse.getCode());
	}

	@Test
	@Ignore
	public void invokeWithUnauthorizedSpecificSME() throws IOException {
		String parameters = getJSONValueAsString("authorization/parameters.json");
		DummyCredential dummyCredential = DummyCredentialStore.BASYX_EXECUTOR_TWO_CREDENTIAL;

		String accessToken = tokenProvider.getAccessToken(dummyCredential.getUsername(), dummyCredential.getPassword());

		CloseableHttpResponse retrievalResponse = requestOperationInvocationWithAuthorization(getInvocationURL(SPECIFIC_SUBMODEL_ID, OPERATION_CUBE_SUBMODEL_ELEMENT_IDSHORT), parameters, accessToken);
		assertEquals(HttpStatus.FORBIDDEN.value(), retrievalResponse.getCode());
	}

	@Test
	@Ignore
	public void invokeWithNoAuthorization() throws IOException {
		String parameters = getJSONValueAsString("authorization/parameters.json");

		CloseableHttpResponse retrievalResponse = requestOperationInvocationNoAuthorization(getInvocationURL(SPECIFIC_SUBMODEL_ID, OPERATION_SUBMODEL_ELEMENT_IDSHORT_PATH), parameters);
		assertEquals(HttpStatus.UNAUTHORIZED.value(), retrievalResponse.getCode());
	}

	@Test
	public void getSubmodelValueOnlyWithCorrectRoleAndPermission() throws IOException {
		DummyCredential dummyCredential = DummyCredentialStore.BASYX_READER_CREDENTIAL;

		String accessToken = tokenProvider.getAccessToken(dummyCredential.getUsername(), dummyCredential.getPassword());

		CloseableHttpResponse retrievalResponse = getElementWithAuthorization(getSpecificSubmodelValueOnlyAccessURL(SPECIFIC_SUBMODEL_ID), accessToken);
		assertEquals(HttpStatus.OK.value(), retrievalResponse.getCode());
	}

	@Test
	public void getSubmodelValueOnlyWithCorrectRoleAndSpecificSubmodelPermission() throws IOException {
		DummyCredential dummyCredential = DummyCredentialStore.BASYX_READER_TWO_CREDENTIAL;

		String accessToken = tokenProvider.getAccessToken(dummyCredential.getUsername(), dummyCredential.getPassword());

		CloseableHttpResponse retrievalResponse = getElementWithAuthorization(getSpecificSubmodelValueOnlyAccessURL(SPECIFIC_SUBMODEL_ID), accessToken);
		assertEquals(HttpStatus.OK.value(), retrievalResponse.getCode());
	}

	@Test
	public void getSubmodelValueOnlyWithCorrectRoleAndUnauthorizedSpecificSubmodel() throws IOException {
		DummyCredential dummyCredential = DummyCredentialStore.BASYX_READER_TWO_CREDENTIAL;

		String accessToken = tokenProvider.getAccessToken(dummyCredential.getUsername(), dummyCredential.getPassword());

		CloseableHttpResponse retrievalResponse = getElementWithAuthorization(getSpecificSubmodelValueOnlyAccessURL(SPECIFIC_SUBMODEL_ID_2), accessToken);
		assertEquals(HttpStatus.FORBIDDEN.value(), retrievalResponse.getCode());
	}

	@Test
	public void getSubmodelValueOnlyWithInsufficientPermissionRole() throws IOException {
		DummyCredential dummyCredential = DummyCredentialStore.BASYX_UPDATER_CREDENTIAL;

		String accessToken = tokenProvider.getAccessToken(dummyCredential.getUsername(), dummyCredential.getPassword());

		CloseableHttpResponse retrievalResponse = getElementWithAuthorization(getSpecificSubmodelValueOnlyAccessURL(SPECIFIC_SUBMODEL_ID), accessToken);
		assertEquals(HttpStatus.FORBIDDEN.value(), retrievalResponse.getCode());
	}

	@Test
	public void getSubmodelValueOnlyWithNoAuthorization() throws IOException {
		CloseableHttpResponse retrievalResponse = getElementWithNoAuthorization(getSpecificSubmodelValueOnlyAccessURL(SPECIFIC_SUBMODEL_ID));
		assertEquals(HttpStatus.UNAUTHORIZED.value(), retrievalResponse.getCode());
	}

	@Test
	public void getSubmodelByMetadataWithCorrectRoleAndPermission() throws IOException {
		DummyCredential dummyCredential = DummyCredentialStore.BASYX_READER_CREDENTIAL;

		String accessToken = tokenProvider.getAccessToken(dummyCredential.getUsername(), dummyCredential.getPassword());

		CloseableHttpResponse retrievalResponse = getElementWithAuthorization(getSpecificSubmodelMetadataAccessURL(SPECIFIC_SUBMODEL_ID), accessToken);
		assertEquals(HttpStatus.OK.value(), retrievalResponse.getCode());
	}

	@Test
	public void getSubmodelByMetadataWithCorrectRoleAndSpecificSubmodelPermission() throws IOException {
		DummyCredential dummyCredential = DummyCredentialStore.BASYX_READER_TWO_CREDENTIAL;

		String accessToken = tokenProvider.getAccessToken(dummyCredential.getUsername(), dummyCredential.getPassword());

		CloseableHttpResponse retrievalResponse = getElementWithAuthorization(getSpecificSubmodelMetadataAccessURL(SPECIFIC_SUBMODEL_ID), accessToken);
		assertEquals(HttpStatus.OK.value(), retrievalResponse.getCode());
	}

	@Test
	public void getSubmodelByMetadataWithCorrectRoleAndUnauthorizedSpecificSubmodel() throws IOException {
		DummyCredential dummyCredential = DummyCredentialStore.BASYX_READER_TWO_CREDENTIAL;

		String accessToken = tokenProvider.getAccessToken(dummyCredential.getUsername(), dummyCredential.getPassword());

		CloseableHttpResponse retrievalResponse = getElementWithAuthorization(getSpecificSubmodelMetadataAccessURL(SPECIFIC_SUBMODEL_ID_2), accessToken);
		assertEquals(HttpStatus.FORBIDDEN.value(), retrievalResponse.getCode());
	}

	@Test
	public void getSubmodelByMetadataWithInsufficientPermissionRole() throws IOException {
		DummyCredential dummyCredential = DummyCredentialStore.BASYX_UPDATER_CREDENTIAL;

		String accessToken = tokenProvider.getAccessToken(dummyCredential.getUsername(), dummyCredential.getPassword());

		CloseableHttpResponse retrievalResponse = getElementWithAuthorization(getSpecificSubmodelMetadataAccessURL(SPECIFIC_SUBMODEL_ID), accessToken);
		assertEquals(HttpStatus.FORBIDDEN.value(), retrievalResponse.getCode());
	}

	@Test
	public void getSubmodelByMetadataWithNoAuthorization() throws IOException {
		CloseableHttpResponse retrievalResponse = getElementWithNoAuthorization(getSpecificSubmodelMetadataAccessURL(SPECIFIC_SUBMODEL_ID));
		assertEquals(HttpStatus.UNAUTHORIZED.value(), retrievalResponse.getCode());
	}

	@Test
	public void getFileWithCorrectRoleAndPermission() throws IOException {
		createElementOnRepositoryWithAuthorization(createSubmodelRepositoryUrl(submodelRepositoryBaseUrl), getStringFromFile(SUBMODEL_SIMPLE_2_JSON), getAccessToken(DummyCredentialStore.ADMIN_CREDENTIAL));

		java.io.File file = ResourceUtils.getFile("classpath:" + FILE_NAME);

		DummyCredential dummyCredential = DummyCredentialStore.BASYX_READER_CREDENTIAL;

		String accessToken = tokenProvider.getAccessToken(dummyCredential.getUsername(), dummyCredential.getPassword());

		uploadFileToSubmodelElementWithAuthorization(getSMEFileUploadURL(SPECIFIC_SUBMODEL_ID_2, FILE_SUBMODEL_ELEMENT_IDSHORT_PATH, FILE_NAME), FILE_NAME, file, getAdminAccessToken());

		CloseableHttpResponse retrievalResponse = getElementWithAuthorization(getSMEFileDownloadURL(SPECIFIC_SUBMODEL_ID_2, FILE_SUBMODEL_ELEMENT_IDSHORT_PATH), accessToken);
		assertEquals(HttpStatus.OK.value(), retrievalResponse.getCode());

		deleteElementWithAuthorization(getSpecificSubmodelAccessURL(SPECIFIC_SUBMODEL_ID_2), getAccessToken(DummyCredentialStore.ADMIN_CREDENTIAL));
	}

	@Test
	public void getFileWithAuthorizedSpecificSubmodelAndSpecificSME() throws IOException {
		createElementOnRepositoryWithAuthorization(createSubmodelRepositoryUrl(submodelRepositoryBaseUrl), getStringFromFile(SUBMODEL_SIMPLE_2_JSON), getAccessToken(DummyCredentialStore.ADMIN_CREDENTIAL));

		java.io.File file = ResourceUtils.getFile("classpath:" + FILE_NAME);

		DummyCredential dummyCredential = DummyCredentialStore.BASYX_FILE_SME_READER_CREDENTIAL;

		String accessToken = tokenProvider.getAccessToken(dummyCredential.getUsername(), dummyCredential.getPassword());

		uploadFileToSubmodelElementWithAuthorization(getSMEFileUploadURL(SPECIFIC_SUBMODEL_ID_2, FILE_SUBMODEL_ELEMENT_IDSHORT_PATH, FILE_NAME), FILE_NAME, file, getAdminAccessToken());

		CloseableHttpResponse retrievalResponse = getElementWithAuthorization(getSMEFileDownloadURL(SPECIFIC_SUBMODEL_ID_2, FILE_SUBMODEL_ELEMENT_IDSHORT_PATH), accessToken);
		assertEquals(HttpStatus.OK.value(), retrievalResponse.getCode());

		deleteElementWithAuthorization(getSpecificSubmodelAccessURL(SPECIFIC_SUBMODEL_ID_2), getAccessToken(DummyCredentialStore.ADMIN_CREDENTIAL));
	}

	@Test
	public void getFileWithInsufficientPermissionRole() throws IOException {
		createElementOnRepositoryWithAuthorization(createSubmodelRepositoryUrl(submodelRepositoryBaseUrl), getStringFromFile(SUBMODEL_SIMPLE_2_JSON), getAccessToken(DummyCredentialStore.ADMIN_CREDENTIAL));

		java.io.File file = ResourceUtils.getFile("classpath:" + FILE_NAME);

		DummyCredential dummyCredential = DummyCredentialStore.BASYX_UPDATER_CREDENTIAL;

		String accessToken = tokenProvider.getAccessToken(dummyCredential.getUsername(), dummyCredential.getPassword());

		uploadFileToSubmodelElementWithAuthorization(getSMEFileUploadURL(SPECIFIC_SUBMODEL_ID_2, FILE_SUBMODEL_ELEMENT_IDSHORT_PATH, FILE_NAME), FILE_NAME, file, getAdminAccessToken());

		CloseableHttpResponse retrievalResponse = getElementWithAuthorization(getSMEFileDownloadURL(SPECIFIC_SUBMODEL_ID_2, FILE_SUBMODEL_ELEMENT_IDSHORT_PATH), accessToken);
		assertEquals(HttpStatus.FORBIDDEN.value(), retrievalResponse.getCode());

		deleteElementWithAuthorization(getSpecificSubmodelAccessURL(SPECIFIC_SUBMODEL_ID_2), getAccessToken(DummyCredentialStore.ADMIN_CREDENTIAL));
	}

	@Test
	public void getFileWithUnauthorizedSpecificSME() throws IOException {
		createElementOnRepositoryWithAuthorization(createSubmodelRepositoryUrl(submodelRepositoryBaseUrl), getStringFromFile(SUBMODEL_SIMPLE_2_JSON), getAccessToken(DummyCredentialStore.ADMIN_CREDENTIAL));

		DummyCredential dummyCredential = DummyCredentialStore.BASYX_FILE_SME_READER_CREDENTIAL;

		String accessToken = tokenProvider.getAccessToken(dummyCredential.getUsername(), dummyCredential.getPassword());

		CloseableHttpResponse retrievalResponse = getElementWithAuthorization(getSMEFileDownloadURL(SPECIFIC_SUBMODEL_ID_2, FILE_SUBMODEL_ELEMENT_IDSHORT_PATH_2), accessToken);
		assertEquals(HttpStatus.FORBIDDEN.value(), retrievalResponse.getCode());

		deleteElementWithAuthorization(getSpecificSubmodelAccessURL(SPECIFIC_SUBMODEL_ID_2), getAccessToken(DummyCredentialStore.ADMIN_CREDENTIAL));
	}

	@Test
	public void getFileWithNoAuthorization() throws IOException {
		createElementOnRepositoryWithAuthorization(createSubmodelRepositoryUrl(submodelRepositoryBaseUrl), getStringFromFile(SUBMODEL_SIMPLE_2_JSON), getAccessToken(DummyCredentialStore.ADMIN_CREDENTIAL));

		CloseableHttpResponse retrievalResponse = getElementWithNoAuthorization(getSMEFileDownloadURL(SPECIFIC_SUBMODEL_ID_2, FILE_SUBMODEL_ELEMENT_IDSHORT_PATH));
		assertEquals(HttpStatus.UNAUTHORIZED.value(), retrievalResponse.getCode());

		deleteElementWithAuthorization(getSpecificSubmodelAccessURL(SPECIFIC_SUBMODEL_ID_2), getAccessToken(DummyCredentialStore.ADMIN_CREDENTIAL));
	}

	@Test
	public void setFileWithCorrectRoleAndPermission() throws IOException {
		createElementOnRepositoryWithAuthorization(createSubmodelRepositoryUrl(submodelRepositoryBaseUrl), getStringFromFile(SUBMODEL_SIMPLE_2_JSON), getAccessToken(DummyCredentialStore.ADMIN_CREDENTIAL));

		java.io.File file = ResourceUtils.getFile("classpath:" + FILE_NAME);

		DummyCredential dummyCredential = DummyCredentialStore.BASYX_UPDATER_CREDENTIAL;

		String accessToken = tokenProvider.getAccessToken(dummyCredential.getUsername(), dummyCredential.getPassword());

		CloseableHttpResponse retrievalResponse = uploadFileToSubmodelElementWithAuthorization(getSMEFileUploadURL(SPECIFIC_SUBMODEL_ID_2, FILE_SUBMODEL_ELEMENT_IDSHORT_PATH, FILE_NAME), FILE_NAME, file, accessToken);
		assertEquals(HttpStatus.OK.value(), retrievalResponse.getCode());

		deleteElementWithAuthorization(getSpecificSubmodelAccessURL(SPECIFIC_SUBMODEL_ID_2), getAccessToken(DummyCredentialStore.ADMIN_CREDENTIAL));
	}

	@Test
	public void setFileWithAuthorizedSpecificSubmodelAndSpecificSME() throws IOException {
		createElementOnRepositoryWithAuthorization(createSubmodelRepositoryUrl(submodelRepositoryBaseUrl), getStringFromFile(SUBMODEL_SIMPLE_2_JSON), getAccessToken(DummyCredentialStore.ADMIN_CREDENTIAL));

		java.io.File file = ResourceUtils.getFile("classpath:" + FILE_NAME);

		DummyCredential dummyCredential = DummyCredentialStore.BASYX_FILE_SME_UPDATER_CREDENTIAL;

		String accessToken = tokenProvider.getAccessToken(dummyCredential.getUsername(), dummyCredential.getPassword());

		CloseableHttpResponse retrievalResponse = uploadFileToSubmodelElementWithAuthorization(getSMEFileUploadURL(SPECIFIC_SUBMODEL_ID_2, FILE_SUBMODEL_ELEMENT_IDSHORT_PATH, FILE_NAME), FILE_NAME, file, accessToken);
		assertEquals(HttpStatus.OK.value(), retrievalResponse.getCode());

		deleteElementWithAuthorization(getSpecificSubmodelAccessURL(SPECIFIC_SUBMODEL_ID_2), getAccessToken(DummyCredentialStore.ADMIN_CREDENTIAL));
	}

	@Test
	public void setFileWithInsufficientPermissionRole() throws IOException {
		createElementOnRepositoryWithAuthorization(createSubmodelRepositoryUrl(submodelRepositoryBaseUrl), getStringFromFile(SUBMODEL_SIMPLE_2_JSON), getAccessToken(DummyCredentialStore.ADMIN_CREDENTIAL));

		java.io.File file = ResourceUtils.getFile("classpath:" + FILE_NAME);

		DummyCredential dummyCredential = DummyCredentialStore.BASYX_READER_CREDENTIAL;

		String accessToken = tokenProvider.getAccessToken(dummyCredential.getUsername(), dummyCredential.getPassword());

		CloseableHttpResponse retrievalResponse = uploadFileToSubmodelElementWithAuthorization(getSMEFileUploadURL(SPECIFIC_SUBMODEL_ID_2, FILE_SUBMODEL_ELEMENT_IDSHORT_PATH, FILE_NAME), FILE_NAME, file, accessToken);
		assertEquals(HttpStatus.FORBIDDEN.value(), retrievalResponse.getCode());

		deleteElementWithAuthorization(getSpecificSubmodelAccessURL(SPECIFIC_SUBMODEL_ID_2), getAccessToken(DummyCredentialStore.ADMIN_CREDENTIAL));
	}

	@Test
	public void setFileWithUnauthorizedSpecificSME() throws IOException {
		createElementOnRepositoryWithAuthorization(createSubmodelRepositoryUrl(submodelRepositoryBaseUrl), getStringFromFile(SUBMODEL_SIMPLE_2_JSON), getAccessToken(DummyCredentialStore.ADMIN_CREDENTIAL));

		java.io.File file = ResourceUtils.getFile("classpath:" + FILE_NAME);

		DummyCredential dummyCredential = DummyCredentialStore.BASYX_FILE_SME_UPDATER_CREDENTIAL;

		String accessToken = tokenProvider.getAccessToken(dummyCredential.getUsername(), dummyCredential.getPassword());

		CloseableHttpResponse retrievalResponse = uploadFileToSubmodelElementWithAuthorization(getSMEFileUploadURL(SPECIFIC_SUBMODEL_ID_2, FILE_SUBMODEL_ELEMENT_IDSHORT_PATH_2, FILE_NAME), FILE_NAME, file, accessToken);
		assertEquals(HttpStatus.FORBIDDEN.value(), retrievalResponse.getCode());

		deleteElementWithAuthorization(getSpecificSubmodelAccessURL(SPECIFIC_SUBMODEL_ID_2), getAccessToken(DummyCredentialStore.ADMIN_CREDENTIAL));
	}

	@Test
	public void setFileWithNoAuthorization() throws IOException {
		createElementOnRepositoryWithAuthorization(createSubmodelRepositoryUrl(submodelRepositoryBaseUrl), getStringFromFile(SUBMODEL_SIMPLE_2_JSON), getAccessToken(DummyCredentialStore.ADMIN_CREDENTIAL));

		java.io.File file = ResourceUtils.getFile("classpath:" + FILE_NAME);

		CloseableHttpResponse retrievalResponse = uploadFileToSubmodelElementNoAuthorization(getSMEFileUploadURL(SPECIFIC_SUBMODEL_ID_2, FILE_SUBMODEL_ELEMENT_IDSHORT_PATH, FILE_NAME), FILE_NAME, file);
		assertEquals(HttpStatus.UNAUTHORIZED.value(), retrievalResponse.getCode());

		deleteElementWithAuthorization(getSpecificSubmodelAccessURL(SPECIFIC_SUBMODEL_ID_2), getAccessToken(DummyCredentialStore.ADMIN_CREDENTIAL));
	}

	@Test
	public void deleteFileWithCorrectRoleAndPermission() throws IOException {
		createElementOnRepositoryWithAuthorization(createSubmodelRepositoryUrl(submodelRepositoryBaseUrl), getStringFromFile(SUBMODEL_SIMPLE_2_JSON), getAccessToken(DummyCredentialStore.ADMIN_CREDENTIAL));

		java.io.File file = ResourceUtils.getFile("classpath:" + FILE_NAME);

		DummyCredential dummyCredential = DummyCredentialStore.BASYX_UPDATER_CREDENTIAL;

		String accessToken = tokenProvider.getAccessToken(dummyCredential.getUsername(), dummyCredential.getPassword());

		uploadFileToSubmodelElementWithAuthorization(getSMEFileUploadURL(SPECIFIC_SUBMODEL_ID_2, FILE_SUBMODEL_ELEMENT_IDSHORT_PATH, FILE_NAME), FILE_NAME, file, getAdminAccessToken());

		CloseableHttpResponse retrievalResponse = deleteElementWithAuthorization(getSMEFileDownloadURL(SPECIFIC_SUBMODEL_ID_2, FILE_SUBMODEL_ELEMENT_IDSHORT_PATH), accessToken);
		assertEquals(HttpStatus.OK.value(), retrievalResponse.getCode());

		deleteElementWithAuthorization(getSpecificSubmodelAccessURL(SPECIFIC_SUBMODEL_ID_2), getAccessToken(DummyCredentialStore.ADMIN_CREDENTIAL));
	}

	@Test
	public void deleteFileWithAuthorizedSpecificSubmodelAndSpecificSME() throws IOException {
		createElementOnRepositoryWithAuthorization(createSubmodelRepositoryUrl(submodelRepositoryBaseUrl), getStringFromFile(SUBMODEL_SIMPLE_2_JSON), getAccessToken(DummyCredentialStore.ADMIN_CREDENTIAL));

		java.io.File file = ResourceUtils.getFile("classpath:" + FILE_NAME);

		DummyCredential dummyCredential = DummyCredentialStore.BASYX_FILE_SME_UPDATER_CREDENTIAL;

		String accessToken = tokenProvider.getAccessToken(dummyCredential.getUsername(), dummyCredential.getPassword());

		uploadFileToSubmodelElementWithAuthorization(getSMEFileUploadURL(SPECIFIC_SUBMODEL_ID_2, FILE_SUBMODEL_ELEMENT_IDSHORT_PATH, FILE_NAME), FILE_NAME, file, getAdminAccessToken());

		CloseableHttpResponse retrievalResponse = deleteElementWithAuthorization(getSMEFileDownloadURL(SPECIFIC_SUBMODEL_ID_2, FILE_SUBMODEL_ELEMENT_IDSHORT_PATH), accessToken);
		assertEquals(HttpStatus.OK.value(), retrievalResponse.getCode());

		deleteElementWithAuthorization(getSpecificSubmodelAccessURL(SPECIFIC_SUBMODEL_ID_2), getAccessToken(DummyCredentialStore.ADMIN_CREDENTIAL));
	}

	@Test
	public void deleteFileWithInsufficientPermissionRole() throws IOException {
		createElementOnRepositoryWithAuthorization(createSubmodelRepositoryUrl(submodelRepositoryBaseUrl), getStringFromFile(SUBMODEL_SIMPLE_2_JSON), getAccessToken(DummyCredentialStore.ADMIN_CREDENTIAL));

		java.io.File file = ResourceUtils.getFile("classpath:" + FILE_NAME);

		DummyCredential dummyCredential = DummyCredentialStore.BASYX_READER_CREDENTIAL;

		String accessToken = tokenProvider.getAccessToken(dummyCredential.getUsername(), dummyCredential.getPassword());

		uploadFileToSubmodelElementWithAuthorization(getSMEFileUploadURL(SPECIFIC_SUBMODEL_ID_2, FILE_SUBMODEL_ELEMENT_IDSHORT_PATH, FILE_NAME), FILE_NAME, file, getAdminAccessToken());

		CloseableHttpResponse retrievalResponse = deleteElementWithAuthorization(getSMEFileDownloadURL(SPECIFIC_SUBMODEL_ID_2, FILE_SUBMODEL_ELEMENT_IDSHORT_PATH), accessToken);
		assertEquals(HttpStatus.FORBIDDEN.value(), retrievalResponse.getCode());

		deleteElementWithAuthorization(getSpecificSubmodelAccessURL(SPECIFIC_SUBMODEL_ID_2), getAccessToken(DummyCredentialStore.ADMIN_CREDENTIAL));
	}

	@Test
	public void deleteFileWithUnauthorizedSpecificSME() throws IOException {
		createElementOnRepositoryWithAuthorization(createSubmodelRepositoryUrl(submodelRepositoryBaseUrl), getStringFromFile(SUBMODEL_SIMPLE_2_JSON), getAccessToken(DummyCredentialStore.ADMIN_CREDENTIAL));

		java.io.File file = ResourceUtils.getFile("classpath:" + FILE_NAME);

		DummyCredential dummyCredential = DummyCredentialStore.BASYX_FILE_SME_UPDATER_CREDENTIAL;

		String accessToken = tokenProvider.getAccessToken(dummyCredential.getUsername(), dummyCredential.getPassword());

		uploadFileToSubmodelElementWithAuthorization(getSMEFileUploadURL(SPECIFIC_SUBMODEL_ID_2, FILE_SUBMODEL_ELEMENT_IDSHORT_PATH, FILE_NAME), FILE_NAME, file, getAdminAccessToken());

		CloseableHttpResponse retrievalResponse = deleteElementWithAuthorization(getSMEFileDownloadURL(SPECIFIC_SUBMODEL_ID_2, FILE_SUBMODEL_ELEMENT_IDSHORT_PATH_2), accessToken);
		assertEquals(HttpStatus.FORBIDDEN.value(), retrievalResponse.getCode());

		deleteElementWithAuthorization(getSpecificSubmodelAccessURL(SPECIFIC_SUBMODEL_ID_2), getAccessToken(DummyCredentialStore.ADMIN_CREDENTIAL));
	}

	@Test
	public void deleteFileWithNoAuthorization() throws IOException {
		createElementOnRepositoryWithAuthorization(createSubmodelRepositoryUrl(submodelRepositoryBaseUrl), getStringFromFile(SUBMODEL_SIMPLE_2_JSON), getAccessToken(DummyCredentialStore.ADMIN_CREDENTIAL));

		CloseableHttpResponse retrievalResponse = deleteElementWithNoAuthorization(getSMEFileDownloadURL(SPECIFIC_SUBMODEL_ID_2, FILE_SUBMODEL_ELEMENT_IDSHORT_PATH));
		assertEquals(HttpStatus.UNAUTHORIZED.value(), retrievalResponse.getCode());

		deleteElementWithAuthorization(getSpecificSubmodelAccessURL(SPECIFIC_SUBMODEL_ID_2), getAccessToken(DummyCredentialStore.ADMIN_CREDENTIAL));
	}

	@Test
	public void patchSubmodelValueWithCorrectRoleAndPermission() throws IOException {
		String accessToken = getAccessToken(DummyCredentialStore.BASYX_UPDATER_CREDENTIAL);

		CloseableHttpResponse retrievalResponse = updateElementWithAuthorizationPatchRequest(getSpecificSubmodelValueOnlyAccessURL(SPECIFIC_SUBMODEL_ID), getJSONValueAsString("authorization/newSubmodelValue.json"), accessToken);
		assertEquals(HttpStatus.NO_CONTENT.value(), retrievalResponse.getCode());
	}

	@Test
	public void patchSubmodelValueWithCorrectRoleAndSpecificSubmodelPermission() throws IOException {
		String accessToken = getAccessToken(DummyCredentialStore.BASYX_UPDATER_CREDENTIAL);

		CloseableHttpResponse retrievalResponse = updateElementWithAuthorizationPatchRequest(getSpecificSubmodelValueOnlyAccessURL(SPECIFIC_SUBMODEL_ID), getJSONValueAsString("authorization/newSubmodelValue.json"), accessToken);
		assertEquals(HttpStatus.NO_CONTENT.value(), retrievalResponse.getCode());
	}

	@Test
	public void patchSubmodelValueWithCorrectRoleAndUnauthorizedSpecificSubmodel() throws IOException {
		String accessToken = getAccessToken(DummyCredentialStore.BASYX_READER_TWO_CREDENTIAL);

		CloseableHttpResponse retrievalResponse = updateElementWithAuthorizationPatchRequest(getSpecificSubmodelValueOnlyAccessURL(SPECIFIC_SUBMODEL_ID), getJSONValueAsString("authorization/newSubmodelValue.json"), accessToken);
		assertEquals(HttpStatus.FORBIDDEN.value(), retrievalResponse.getCode());
	}

	@Test
	public void patchSubmodelValueWithInsufficientPermissionRole() throws IOException {
		String accessToken = getAccessToken(DummyCredentialStore.BASYX_READER_CREDENTIAL);

		CloseableHttpResponse retrievalResponse = updateElementWithAuthorizationPatchRequest(getSpecificSubmodelValueOnlyAccessURL(SPECIFIC_SUBMODEL_ID), getJSONValueAsString("authorization/newSubmodelValue.json"), accessToken);
		assertEquals(HttpStatus.FORBIDDEN.value(), retrievalResponse.getCode());
	}

	@Test
	public void patchSubmodelValueWithNoAuthorization() throws IOException {
		CloseableHttpResponse retrievalResponse = updateElementWithNoAuthorizationPatchRequest(getSpecificSubmodelAccessURL(SPECIFIC_SUBMODEL_ID), getStringFromFile(SUBMODEL_SIMPLE_1_JSON));

		assertEquals(HttpStatus.UNAUTHORIZED.value(), retrievalResponse.getCode());
	}
	
	private static void initializeRepository() throws FileNotFoundException, IOException, DeserializationException {
		configureSecurityContext();
		
		Submodel dummySubmodel = new JsonDeserializer().read(getStringFromFile(SUBMODEL_SIMPLE_1_JSON), Submodel.class);

		submodelRepo.createSubmodel(dummySubmodel);

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

	private CloseableHttpResponse uploadFileToSubmodelElementWithAuthorization(String url, String fileName, java.io.File file, String accessToken) throws IOException {
		CloseableHttpClient client = HttpClients.createDefault();

		HttpPut putRequest = BaSyxHttpTestUtils.createPutRequestWithFileWithAuthorization(url, fileName, file, accessToken);

		return BaSyxHttpTestUtils.executePutRequest(client, putRequest);
	}

	private CloseableHttpResponse uploadFileToSubmodelElementNoAuthorization(String url, String fileName, java.io.File file) throws IOException {
		CloseableHttpClient client = HttpClients.createDefault();

		HttpPut putRequest = BaSyxHttpTestUtils.createPutRequestWithFile(url, fileName, file);
		return BaSyxHttpTestUtils.executePutRequest(client, putRequest);
	}

	private String getSMEFileUploadURL(String submodelId, String submodelElementIdShort, String fileName) {
		return getSpecificSubmodelElementAccessURL(submodelId, submodelElementIdShort) + "/attachment?fileName=" + fileName;
	}

	private String getSMEFileDownloadURL(String submodelId, String submodelElementIdShort) {
		return getSpecificSubmodelElementAccessURL(submodelId, submodelElementIdShort) + "/attachment";
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

	private CloseableHttpResponse getAllSubmodelsWithAuthorization(String accessToken) throws IOException {
		return BaSyxHttpTestUtils.executeAuthorizedGetOnURL(createSubmodelRepositoryUrl(submodelRepositoryBaseUrl), accessToken);
	}

	private CloseableHttpResponse getAllSubmodelElementsWithAuthorization(String submodelId, String accessToken) throws IOException {
		return BaSyxHttpTestUtils.executeAuthorizedGetOnURL(getAllSubmodelElementsAccessURL(submodelId), accessToken);
	}

	private CloseableHttpResponse getAllSubmodelsNoAuthorization() throws IOException {
		return BaSyxHttpTestUtils.executeGetOnURL(createSubmodelRepositoryUrl(submodelRepositoryBaseUrl));
	}

	private CloseableHttpResponse getSpecificSubmodelElementNoAuthorization(String submodelId, String submodelElementIdShortPath) throws IOException {
		return BaSyxHttpTestUtils.executeGetOnURL(getSpecificSubmodelElementAccessURL(submodelId, submodelElementIdShortPath));
	}

	private CloseableHttpResponse getSpecificSubmodelElementValueNoAuthorization(String submodelId, String submodelElementIdShortPath) throws IOException {
		return BaSyxHttpTestUtils.executeGetOnURL(getSpecificSubmodelElementValueAccessURL(submodelId, submodelElementIdShortPath));
	}

	private CloseableHttpResponse getAllSubmodelElementsNoAuthorization(String submodelId) throws IOException {
		return BaSyxHttpTestUtils.executeGetOnURL(getAllSubmodelElementsAccessURL(submodelId));
	}

	private CloseableHttpResponse getElementWithAuthorization(String url, String accessToken) throws IOException {
		return BaSyxHttpTestUtils.executeAuthorizedGetOnURL(url, accessToken);
	}

	private CloseableHttpResponse getElementWithNoAuthorization(String url) throws IOException {
		return BaSyxHttpTestUtils.executeGetOnURL(url);
	}

	private String getSpecificSubmodelAccessURL(String submodelId) {
		return createSubmodelRepositoryUrl(createSubmodelRepositoryUrl(submodelRepositoryBaseUrl)) + "/" + Base64UrlEncodedIdentifier.encodeIdentifier(submodelId);
	}

	private String getSpecificSubmodelValueOnlyAccessURL(String submodelId) {
		return getSpecificSubmodelAccessURL(submodelId) + "/$value";
	}

	private String getSpecificSubmodelMetadataAccessURL(String submodelId) {
		return getSpecificSubmodelAccessURL(submodelId) + "/$metadata";
	}

	private String getSpecificSubmodelElementAccessURL(String submodelId, String submodelElementIdShortPath) {
		return createSubmodelRepositoryUrl(createSubmodelRepositoryUrl(submodelRepositoryBaseUrl)) + "/" + Base64UrlEncodedIdentifier.encodeIdentifier(submodelId) + "/submodel-elements/" + submodelElementIdShortPath;
	}

	private String getSpecificSubmodelElementValueAccessURL(String submodelId, String submodelElementIdShortPath) {
		return getSpecificSubmodelElementAccessURL(submodelId, submodelElementIdShortPath) + "/$value";
	}

	private String getInvocationURL(String submodelId, String submodelElementIdShortPath) {
		return getSpecificSubmodelElementAccessURL(submodelId, submodelElementIdShortPath) + "/invoke";
	}

	private CloseableHttpResponse writeSubmodelElementValueNoAuthorization(String url, String value) throws IOException {
		return BaSyxHttpTestUtils.executePatchOnURL(url, value);
	}

	private CloseableHttpResponse writeSubmodelElementValueWithAuthorization(String url, String value, String accessToken) throws IOException {
		return BaSyxHttpTestUtils.executeAuthorizedPatchOnURL(url, value, accessToken);
	}

	private CloseableHttpResponse requestOperationInvocationWithAuthorization(String url, String parameters, String accessToken) throws IOException {
		return BaSyxHttpTestUtils.executeAuthorizedPostOnURL(url, parameters, accessToken);
	}

	private CloseableHttpResponse requestOperationInvocationNoAuthorization(String url, String parameters) throws IOException {
		return BaSyxHttpTestUtils.executePostOnURL(url, parameters);
	}

	protected String getAllSubmodelElementsAccessURL(String submodelId) {
		return createSubmodelRepositoryUrl(createSubmodelRepositoryUrl(submodelRepositoryBaseUrl)) + "/" + Base64UrlEncodedIdentifier.encodeIdentifier(submodelId) + "/submodel-elements";
	}

	private static CloseableHttpResponse createElementOnRepositoryWithAuthorization(String url, String submodelJsonContent, String accessToken) throws IOException {
		return BaSyxHttpTestUtils.executeAuthorizedPostOnURL(url, submodelJsonContent, accessToken);
	}

	private static CloseableHttpResponse createElementOnRepositoryWithNoAuthorization(String url, String submodelJsonContent) throws IOException {
		return BaSyxHttpTestUtils.executePostOnURL(url, submodelJsonContent);
	}

	private CloseableHttpResponse updateElementWithAuthorizationPutRequest(String url, String content, String accessToken) throws IOException {
		return BaSyxHttpTestUtils.executeAuthorizedPutOnURL(url, content, accessToken);
	}

	private CloseableHttpResponse updateElementWithNoAuthorizationPutRequest(String url, String content) throws IOException {
		return BaSyxHttpTestUtils.executePutOnURL(url, content);
	}

	private CloseableHttpResponse updateElementWithAuthorizationPatchRequest(String url, String content, String accessToken) throws IOException {
		return BaSyxHttpTestUtils.executeAuthorizedPatchOnURL(url, content, accessToken);
	}

	private CloseableHttpResponse updateElementWithNoAuthorizationPatchRequest(String url, String content) throws IOException {
		return BaSyxHttpTestUtils.executePatchOnURL(url, content);
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

	private static String createSubmodelRepositoryUrl(String submodelRepositoryBaseURL) {

		try {
			return new URL(new URL(submodelRepositoryBaseURL), SUBMODEL_REPOSITORY_PATH).toString();
		} catch (MalformedURLException e) {
			throw new RuntimeException("The Submodel Repository Base url is malformed. " + e.getMessage());
		}
	}

	private String getJSONValueAsString(String fileName) throws FileNotFoundException, IOException {
		return BaSyxHttpTestUtils.readJSONStringFromClasspath(fileName);
	}

}

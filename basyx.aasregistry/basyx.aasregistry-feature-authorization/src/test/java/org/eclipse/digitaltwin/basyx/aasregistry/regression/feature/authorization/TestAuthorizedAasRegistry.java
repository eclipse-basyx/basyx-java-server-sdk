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

package org.eclipse.digitaltwin.basyx.aasregistry.regression.feature.authorization;

import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.ParseException;
import org.eclipse.digitaltwin.basyx.aasregistry.model.*;
import org.eclipse.digitaltwin.basyx.aasregistry.service.storage.AasRegistryStorage;
import org.eclipse.digitaltwin.basyx.aasregistry.service.storage.DescriptorFilter;
import org.eclipse.digitaltwin.basyx.authorization.AccessTokenProvider;
import org.eclipse.digitaltwin.basyx.authorization.DummyCredential;
import org.eclipse.digitaltwin.basyx.authorization.DummyCredentialStore;
import org.eclipse.digitaltwin.basyx.authorization.jwt.JwtTokenDecoder;
import org.eclipse.digitaltwin.basyx.authorization.jwt.PublicKeyUtils;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.eclipse.digitaltwin.basyx.http.Base64UrlEncodedIdentifier;
import org.eclipse.digitaltwin.basyx.http.serialization.BaSyxHttpTestUtils;
import org.eclipse.digitaltwin.basyx.aasregistry.feature.authorization.AuthorizedAasRegistryStorage;
import org.junit.*;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Tests for {@link AuthorizedAasRegistryStorage} feature
 *
 * @author danish
 */
public class TestAuthorizedAasRegistry {

	private static final String AAS_REGISTRY_PATH = "shell-descriptors";
	private static final String AAS_DESCRIPTOR_SIMPLE_2_JSON = "authorization/AasDescriptorSimple_2.json";
	private static final String AAS_DESCRIPTOR_SIMPLE_1_JSON = "authorization/AasDescriptorSimple_1.json";
	private static final String SPECIFIC_SHELL_ID_2 = "specificAasId-2";
	private static final String SPECIFIC_SHELL_ID = "dummyShellId_3";
	private static final String SPECIFIC_SUBMODEL_ID = "dummyShellId_3-SM";
	private static AccessTokenProvider tokenProvider;
	private static ConfigurableApplicationContext appContext;
	private static final String BASE_URL = "http://127.0.0.1:8080";
	public static String aasRegistryBaseUrl = BASE_URL + "/shell-descriptors";
	private static AasRegistryStorage storage;

	@BeforeClass
	public static void setUp() throws FileNotFoundException, IOException {
		String authenticaltionServerTokenEndpoint = "http://localhost:9096/realms/BaSyx/protocol/openid-connect/token";
		String clientId = "basyx-client-api";

		tokenProvider = new AccessTokenProvider(authenticaltionServerTokenEndpoint, clientId);

		appContext = new SpringApplication(DummyAasRegistryComponent.class).run(new String[] {});

		storage = appContext.getBean(AasRegistryStorage.class);
	}

	@AfterClass
	public static void tearDown() {
		appContext.close();
	}

	@Before
	public void initializeRepositories() throws FileNotFoundException, IOException {
		configureSecurityContext();

		createDummyShellDescriptorsOnRegistry(5);

		clearSecurityContext();
	}

	@After
	public void reset() throws FileNotFoundException, IOException {
		configureSecurityContext();

		Collection<AssetAdministrationShellDescriptor> descriptors = storage.getAllAasDescriptors(PaginationInfo.NO_LIMIT, new DescriptorFilter(AssetKind.TYPE, "TestAsset")).getResult();

		descriptors.forEach(descriptor -> storage.removeAasDescriptor(descriptor.getId()));

		clearSecurityContext();
	}

	@Test
	public void healthEndpointWithoutAuthorization() throws IOException, ParseException {
		String expectedHealthEndpointOutput = getStringFromFile("authorization/HealthOutput.json");

		String healthEndpointUrl = BASE_URL + "/actuator/health";

		CloseableHttpResponse healthCheckResponse = BaSyxHttpTestUtils.executeGetOnURL(healthEndpointUrl);
		assertEquals(HttpStatus.OK.value(), healthCheckResponse.getCode());

		BaSyxHttpTestUtils.assertSameJSONContent(expectedHealthEndpointOutput, BaSyxHttpTestUtils.getResponseAsString(healthCheckResponse));
	}

	@Test
	public void getAllAasDescriptorsWithCorrectRoleAndPermission() throws IOException, ParseException {
		DummyCredential dummyCredential = DummyCredentialStore.BASYX_READER_CREDENTIAL;

		String accessToken = tokenProvider.getAccessToken(dummyCredential.getUsername(), dummyCredential.getPassword());

		CloseableHttpResponse retrievalResponse = getAllElementsWithAuthorization(aasRegistryBaseUrl, accessToken);

		assertEquals(HttpStatus.OK.value(), retrievalResponse.getCode());
	}

	@Test
	public void getAllAasDescriptorsWithInsufficientPermissionRole() throws IOException {
		DummyCredential dummyCredential = DummyCredentialStore.BASYX_UPDATER_CREDENTIAL;

		String accessToken = tokenProvider.getAccessToken(dummyCredential.getUsername(), dummyCredential.getPassword());

		CloseableHttpResponse retrievalResponse = getAllElementsWithAuthorization(aasRegistryBaseUrl, accessToken);
		assertEquals(HttpStatus.OK.value(), retrievalResponse.getCode());
	}

	@Test
	public void getAllAasDescriptorsWithNoAuthorization() throws IOException {
		CloseableHttpResponse retrievalResponse = getAllElementsNoAuthorization(aasRegistryBaseUrl);
		assertEquals(HttpStatus.UNAUTHORIZED.value(), retrievalResponse.getCode());
	}

	@Test
	public void createAasDescriptorWithCorrectRoleAndPermission() throws IOException {
		String accessToken = getAccessToken(DummyCredentialStore.BASYX_CREATOR_CREDENTIAL);

		CloseableHttpResponse retrievalResponse = createAasDescriptorOnRegistryWithAuthorization(aasRegistryBaseUrl, getJSONStringFromFile(AAS_DESCRIPTOR_SIMPLE_2_JSON), accessToken);
		assertEquals(HttpStatus.CREATED.value(), retrievalResponse.getCode());

		deleteElementWithAuthorization(getSpecificAasDescriptorAccessURL(SPECIFIC_SHELL_ID_2), getAccessToken(DummyCredentialStore.ADMIN_CREDENTIAL));
	}

	@Test
	public void createAasDescriptorWithInsufficientPermissionRole() throws IOException {
		String accessToken = getAccessToken(DummyCredentialStore.BASYX_READER_CREDENTIAL);

		CloseableHttpResponse retrievalResponse = createAasDescriptorOnRegistryWithAuthorization(aasRegistryBaseUrl, getJSONStringFromFile(AAS_DESCRIPTOR_SIMPLE_2_JSON), accessToken);
		assertEquals(HttpStatus.FORBIDDEN.value(), retrievalResponse.getCode());
	}

	@Test
	public void createAasDescriptorWithNoAuthorization() throws IOException {
		CloseableHttpResponse retrievalResponse = createAasDescriptorOnRepositoryWithNoAuthorization(aasRegistryBaseUrl, getJSONStringFromFile(AAS_DESCRIPTOR_SIMPLE_2_JSON));

		assertEquals(HttpStatus.UNAUTHORIZED.value(), retrievalResponse.getCode());
	}

	@Test
	public void updateAasDescriptorWithCorrectRoleAndPermission() throws IOException {
		String accessToken = getAccessToken(DummyCredentialStore.BASYX_UPDATER_CREDENTIAL);

		CloseableHttpResponse retrievalResponse = updateElementWithAuthorizationPutRequest(getSpecificAasDescriptorAccessURL(SPECIFIC_SHELL_ID), getJSONStringFromFile(AAS_DESCRIPTOR_SIMPLE_1_JSON), accessToken);
		assertEquals(HttpStatus.NO_CONTENT.value(), retrievalResponse.getCode());
	}

	@Test
	public void updateAasDescriptorWithCorrectRoleAndSpecificAasPermission() throws IOException {
		String accessToken = getAccessToken(DummyCredentialStore.BASYX_UPDATER_TWO_CREDENTIAL);

		CloseableHttpResponse retrievalResponse = updateElementWithAuthorizationPutRequest(getSpecificAasDescriptorAccessURL(SPECIFIC_SHELL_ID), getJSONStringFromFile(AAS_DESCRIPTOR_SIMPLE_1_JSON), accessToken);
		assertEquals(HttpStatus.NO_CONTENT.value(), retrievalResponse.getCode());
	}

	@Test
	public void updateAasDescriptorsWithCorrectRoleAndUnauthorizedSpecificAas() throws IOException {
		String accessToken = getAccessToken(DummyCredentialStore.BASYX_UPDATER_TWO_CREDENTIAL);

		CloseableHttpResponse retrievalResponse = updateElementWithAuthorizationPutRequest(getSpecificAasDescriptorAccessURL(SPECIFIC_SHELL_ID_2), getJSONStringFromFile(AAS_DESCRIPTOR_SIMPLE_1_JSON), accessToken);
		assertEquals(HttpStatus.FORBIDDEN.value(), retrievalResponse.getCode());
	}

	@Test
	public void updateAasDescriptorWithInsufficientPermissionRole() throws IOException {
		String accessToken = getAccessToken(DummyCredentialStore.BASYX_READER_CREDENTIAL);

		CloseableHttpResponse retrievalResponse = updateElementWithAuthorizationPutRequest(getSpecificAasDescriptorAccessURL(SPECIFIC_SHELL_ID), getJSONStringFromFile(AAS_DESCRIPTOR_SIMPLE_1_JSON), accessToken);
		assertEquals(HttpStatus.FORBIDDEN.value(), retrievalResponse.getCode());
	}

	@Test
	public void updateAasDescriptorWithNoAuthorization() throws IOException {
		CloseableHttpResponse retrievalResponse = updateElementWithNoAuthorizationPutRequest(getSpecificAasDescriptorAccessURL(SPECIFIC_SHELL_ID), getJSONStringFromFile(AAS_DESCRIPTOR_SIMPLE_1_JSON));

		assertEquals(HttpStatus.UNAUTHORIZED.value(), retrievalResponse.getCode());
	}

	@Test
	public void deleteAasDescriptorWithCorrectRoleAndPermission() throws IOException {
		createAasDescriptorOnRegistryWithAuthorization(aasRegistryBaseUrl, getJSONStringFromFile(AAS_DESCRIPTOR_SIMPLE_2_JSON), getAccessToken(DummyCredentialStore.ADMIN_CREDENTIAL));

		String accessToken = getAccessToken(DummyCredentialStore.BASYX_DELETER_CREDENTIAL);

		CloseableHttpResponse retrievalResponse = deleteElementWithAuthorization(getSpecificAasDescriptorAccessURL(SPECIFIC_SHELL_ID_2), accessToken);
		assertEquals(HttpStatus.NO_CONTENT.value(), retrievalResponse.getCode());

		assertElementIsNotOnServer(getSpecificAasDescriptorAccessURL(SPECIFIC_SHELL_ID_2), getAccessToken(DummyCredentialStore.ADMIN_CREDENTIAL));

		deleteElementWithAuthorization(getSpecificAasDescriptorAccessURL(SPECIFIC_SHELL_ID_2), getAccessToken(DummyCredentialStore.ADMIN_CREDENTIAL));
	}

	@Test
	public void deleteAasDescriptorWithCorrectRoleAndSpecificAasPermission() throws IOException {
		createAasDescriptorOnRegistryWithAuthorization(aasRegistryBaseUrl, getJSONStringFromFile(AAS_DESCRIPTOR_SIMPLE_2_JSON), getAccessToken(DummyCredentialStore.ADMIN_CREDENTIAL));

		String accessToken = getAccessToken(DummyCredentialStore.BASYX_DELETER_TWO_CREDENTIAL);

		CloseableHttpResponse retrievalResponse = deleteElementWithAuthorization(getSpecificAasDescriptorAccessURL(SPECIFIC_SHELL_ID_2), accessToken);
		assertEquals(HttpStatus.NO_CONTENT.value(), retrievalResponse.getCode());

		assertElementIsNotOnServer(getSpecificAasDescriptorAccessURL(SPECIFIC_SHELL_ID_2), getAccessToken(DummyCredentialStore.ADMIN_CREDENTIAL));

		deleteElementWithAuthorization(getSpecificAasDescriptorAccessURL(SPECIFIC_SHELL_ID_2), getAccessToken(DummyCredentialStore.ADMIN_CREDENTIAL));
	}

	@Test
	public void deleteAasDescriptorWithCorrectRoleAndUnauthorizedSpecificAas() throws IOException {
		String accessToken = getAccessToken(DummyCredentialStore.BASYX_DELETER_TWO_CREDENTIAL);

		CloseableHttpResponse retrievalResponse = deleteElementWithAuthorization(getSpecificAasDescriptorAccessURL(SPECIFIC_SHELL_ID), accessToken);
		assertEquals(HttpStatus.FORBIDDEN.value(), retrievalResponse.getCode());

		assertElementExistsOnServer(getSpecificAasDescriptorAccessURL(SPECIFIC_SHELL_ID), getAccessToken(DummyCredentialStore.ADMIN_CREDENTIAL));
	}

	@Test
	public void deleteAasDescriptorWithInsufficientPermissionRole() throws IOException {
		String accessToken = getAccessToken(DummyCredentialStore.BASYX_UPDATER_CREDENTIAL);

		CloseableHttpResponse retrievalResponse = deleteElementWithAuthorization(getSpecificAasDescriptorAccessURL(SPECIFIC_SHELL_ID), accessToken);
		assertEquals(HttpStatus.FORBIDDEN.value(), retrievalResponse.getCode());

		assertElementExistsOnServer(getSpecificAasDescriptorAccessURL(SPECIFIC_SHELL_ID), getAccessToken(DummyCredentialStore.ADMIN_CREDENTIAL));
	}

	@Test
	public void deleteAasDescriptorWithNoAuthorization() throws IOException {
		CloseableHttpResponse retrievalResponse = deleteElementWithNoAuthorization(getSpecificAasDescriptorAccessURL(SPECIFIC_SHELL_ID));

		assertEquals(HttpStatus.UNAUTHORIZED.value(), retrievalResponse.getCode());

		assertElementExistsOnServer(getSpecificAasDescriptorAccessURL(SPECIFIC_SHELL_ID), getAccessToken(DummyCredentialStore.ADMIN_CREDENTIAL));
	}

	@Test
	public void getAasDescriptorsWithCorrectRoleAndPermission() throws IOException {
		String accessToken = getAccessToken(DummyCredentialStore.BASYX_READER_CREDENTIAL);

		CloseableHttpResponse retrievalResponse = getElementWithAuthorization(getAasDescriptorsAccessURL(SPECIFIC_SHELL_ID), accessToken);
		assertEquals(HttpStatus.OK.value(), retrievalResponse.getCode());
	}

	@Test
	public void getAasDescriptorsWithCorrectRoleAndSpecificAasDescriptorPermission() throws IOException {
		DummyCredential dummyCredential = DummyCredentialStore.BASYX_READER_TWO_CREDENTIAL;

		String accessToken = tokenProvider.getAccessToken(dummyCredential.getUsername(), dummyCredential.getPassword());

		CloseableHttpResponse retrievalResponse = getElementWithAuthorization(getAasDescriptorsAccessURL(SPECIFIC_SHELL_ID), accessToken);
		assertEquals(HttpStatus.OK.value(), retrievalResponse.getCode());
	}

	@Test
	public void getAasDescriptorsWithCorrectRoleAndUnauthorizedSpecificAasDescriptor() throws IOException {
		DummyCredential dummyCredential = DummyCredentialStore.BASYX_READER_TWO_CREDENTIAL;

		String accessToken = tokenProvider.getAccessToken(dummyCredential.getUsername(), dummyCredential.getPassword());

		CloseableHttpResponse retrievalResponse = getElementWithAuthorization(getAasDescriptorsAccessURL(SPECIFIC_SHELL_ID_2), accessToken);
		assertEquals(HttpStatus.FORBIDDEN.value(), retrievalResponse.getCode());
	}

	@Test
	public void getAasDescriptorsWithInsufficientPermissionRole() throws IOException {
		DummyCredential dummyCredential = DummyCredentialStore.BASYX_UPDATER_CREDENTIAL;

		String accessToken = tokenProvider.getAccessToken(dummyCredential.getUsername(), dummyCredential.getPassword());

		CloseableHttpResponse retrievalResponse = getElementWithAuthorization(getAasDescriptorsAccessURL(SPECIFIC_SHELL_ID), accessToken);
		assertEquals(HttpStatus.FORBIDDEN.value(), retrievalResponse.getCode());
	}

	@Test
	public void getAasDescriptorsWithNoAuthorization() throws IOException {
		CloseableHttpResponse retrievalResponse = getElementWithNoAuthorization(getAasDescriptorsAccessURL(SPECIFIC_SHELL_ID));
		assertEquals(HttpStatus.UNAUTHORIZED.value(), retrievalResponse.getCode());
	}

	@Test
	public void getAasDescriptorWithCorrectRoleAndPermission() throws IOException {
		String accessToken = getAccessToken(DummyCredentialStore.BASYX_READER_CREDENTIAL);

		CloseableHttpResponse retrievalResponse = getElementWithAuthorization(getSpecificAasDescriptorAccessURL(SPECIFIC_SHELL_ID, SPECIFIC_SUBMODEL_ID), accessToken);
		assertEquals(HttpStatus.OK.value(), retrievalResponse.getCode());
	}

	@Test
	public void getAasDescriptorWithCorrectRoleAndSpecificAasPermission() throws IOException {
		DummyCredential dummyCredential = DummyCredentialStore.BASYX_READER_TWO_CREDENTIAL;

		String accessToken = tokenProvider.getAccessToken(dummyCredential.getUsername(), dummyCredential.getPassword());

		CloseableHttpResponse retrievalResponse = getElementWithAuthorization(getSpecificAasDescriptorAccessURL(SPECIFIC_SHELL_ID, SPECIFIC_SUBMODEL_ID), accessToken);
		assertEquals(HttpStatus.OK.value(), retrievalResponse.getCode());
	}

	@Test
	public void getAasDescriptorWithCorrectRoleAndUnauthorizedSpecificAas() throws IOException {
		DummyCredential dummyCredential = DummyCredentialStore.BASYX_READER_TWO_CREDENTIAL;

		String accessToken = tokenProvider.getAccessToken(dummyCredential.getUsername(), dummyCredential.getPassword());

		CloseableHttpResponse retrievalResponse = getElementWithAuthorization(getSpecificAasDescriptorAccessURL(SPECIFIC_SHELL_ID_2, SPECIFIC_SUBMODEL_ID), accessToken);
		assertEquals(HttpStatus.FORBIDDEN.value(), retrievalResponse.getCode());
	}

	@Test
	public void getAasDescriptorWithInsufficientPermissionRole() throws IOException {
		DummyCredential dummyCredential = DummyCredentialStore.BASYX_UPDATER_CREDENTIAL;

		String accessToken = tokenProvider.getAccessToken(dummyCredential.getUsername(), dummyCredential.getPassword());

		CloseableHttpResponse retrievalResponse = getElementWithAuthorization(getSpecificAasDescriptorAccessURL(SPECIFIC_SHELL_ID, SPECIFIC_SUBMODEL_ID), accessToken);
		assertEquals(HttpStatus.FORBIDDEN.value(), retrievalResponse.getCode());
	}

	@Test
	public void getAasDescriptorWithNoAuthorization() throws IOException {
		CloseableHttpResponse retrievalResponse = getElementWithNoAuthorization(getSpecificAasDescriptorAccessURL(SPECIFIC_SHELL_ID, SPECIFIC_SUBMODEL_ID));
		assertEquals(HttpStatus.UNAUTHORIZED.value(), retrievalResponse.getCode());
	}

	@Test
	public void addAasDescriptorWithCorrectRoleAndPermission() throws IOException {
		String accessToken = getAccessToken(DummyCredentialStore.BASYX_UPDATER_CREDENTIAL);

		CloseableHttpResponse retrievalResponse = updateElementWithAuthorizationPostRequest(getAasDescriptorsAccessURL(SPECIFIC_SHELL_ID), getJSONStringFromFile("authorization/SingleSubmodelDescriptor.json"), accessToken);
		assertEquals(HttpStatus.CREATED.value(), retrievalResponse.getCode());
	}

	@Test
	public void addAasDescriptorWithCorrectRoleAndSpecificAasPermission() throws IOException {
		String accessToken = getAccessToken(DummyCredentialStore.BASYX_UPDATER_TWO_CREDENTIAL);

		CloseableHttpResponse retrievalResponse = updateElementWithAuthorizationPostRequest(getAasDescriptorsAccessURL(SPECIFIC_SHELL_ID), getJSONStringFromFile("authorization/SingleSubmodelDescriptor.json"), accessToken);
		assertEquals(HttpStatus.CREATED.value(), retrievalResponse.getCode());
	}

	@Test
	public void addAasDescriptorWithCorrectRoleAndUnauthorizedSpecificAas() throws IOException {
		String accessToken = getAccessToken(DummyCredentialStore.BASYX_UPDATER_TWO_CREDENTIAL);

		CloseableHttpResponse retrievalResponse = updateElementWithAuthorizationPostRequest(getAasDescriptorsAccessURL(SPECIFIC_SHELL_ID_2), getJSONStringFromFile("authorization/SingleSubmodelDescriptor.json"), accessToken);
		assertEquals(HttpStatus.FORBIDDEN.value(), retrievalResponse.getCode());
	}

	@Test
	public void addAasDescriptorWithInsufficientPermissionRole() throws IOException {
		String accessToken = getAccessToken(DummyCredentialStore.BASYX_READER_CREDENTIAL);

		CloseableHttpResponse retrievalResponse = updateElementWithAuthorizationPostRequest(getAasDescriptorsAccessURL(SPECIFIC_SHELL_ID), getJSONStringFromFile("authorization/SingleSubmodelDescriptor.json"), accessToken);
		assertEquals(HttpStatus.FORBIDDEN.value(), retrievalResponse.getCode());
	}

	@Test
	public void addAasDescriptorWithNoAuthorization() throws IOException {
		CloseableHttpResponse retrievalResponse = updateElementWithNoAuthorizationPostRequest(getAasDescriptorsAccessURL(SPECIFIC_SHELL_ID), getJSONStringFromFile("authorization/SingleSubmodelDescriptor.json"));

		assertEquals(HttpStatus.UNAUTHORIZED.value(), retrievalResponse.getCode());
	}

	@Test
	public void removeAasDescriptorWithCorrectRoleAndPermission() throws IOException {
		createAasDescriptorOnRegistryWithAuthorization(aasRegistryBaseUrl, getJSONStringFromFile(AAS_DESCRIPTOR_SIMPLE_2_JSON), getAccessToken(DummyCredentialStore.ADMIN_CREDENTIAL));

		String accessToken = getAccessToken(DummyCredentialStore.BASYX_ASSET_UPDATER_CREDENTIAL);

		CloseableHttpResponse retrievalResponse = deleteElementWithAuthorization(getSpecificAasDescriptorAccessURL(SPECIFIC_SHELL_ID_2, "specificAasId-2-SM"), accessToken);
		assertEquals(HttpStatus.NO_CONTENT.value(), retrievalResponse.getCode());

		deleteElementWithAuthorization(getSpecificAasDescriptorAccessURL(SPECIFIC_SHELL_ID_2), getAccessToken(DummyCredentialStore.ADMIN_CREDENTIAL));
	}

	@Test
	public void removeAasDescriptorWithCorrectRoleAndSpecificAasPermission() throws IOException {
		createAasDescriptorOnRegistryWithAuthorization(aasRegistryBaseUrl, getJSONStringFromFile(AAS_DESCRIPTOR_SIMPLE_2_JSON), getAccessToken(DummyCredentialStore.ADMIN_CREDENTIAL));

		String accessToken = getAccessToken(DummyCredentialStore.BASYX_ASSET_UPDATER_TWO_CREDENTIAL);

		CloseableHttpResponse retrievalResponse = deleteElementWithAuthorization(getSpecificAasDescriptorAccessURL(SPECIFIC_SHELL_ID_2, "specificAasId-2-SM"), accessToken);
		assertEquals(HttpStatus.NO_CONTENT.value(), retrievalResponse.getCode());

		deleteElementWithAuthorization(getSpecificAasDescriptorAccessURL(SPECIFIC_SHELL_ID_2), getAccessToken(DummyCredentialStore.ADMIN_CREDENTIAL));
	}

	@Test
	public void removeAasDescriptorWithCorrectRoleAndUnauthorizedSpecificAas() throws IOException {
		String accessToken = getAccessToken(DummyCredentialStore.BASYX_ASSET_UPDATER_TWO_CREDENTIAL);

		CloseableHttpResponse retrievalResponse = deleteElementWithAuthorization(getSpecificAasDescriptorAccessURL(SPECIFIC_SHELL_ID, "specificAasId-2-SM"), accessToken);
		assertEquals(HttpStatus.FORBIDDEN.value(), retrievalResponse.getCode());
	}

	@Test
	public void removeAasDescriptorWithInsufficientPermissionRole() throws IOException {
		String accessToken = getAccessToken(DummyCredentialStore.BASYX_CREATOR_CREDENTIAL);

		CloseableHttpResponse retrievalResponse = deleteElementWithAuthorization(getSpecificAasDescriptorAccessURL(SPECIFIC_SHELL_ID_2, "specificAasId-2-SM"), accessToken);
		assertEquals(HttpStatus.FORBIDDEN.value(), retrievalResponse.getCode());
	}

	@Test
	public void removeAasDescriptorWithNoAuthorization() throws IOException {
		CloseableHttpResponse retrievalResponse = deleteElementWithNoAuthorization(getSpecificAasDescriptorAccessURL(SPECIFIC_SHELL_ID_2, "specificAasId-2-SM"));

		assertEquals(HttpStatus.UNAUTHORIZED.value(), retrievalResponse.getCode());
	}

	@Test
	public void replaceAasDescriptorWithCorrectRoleAndPermission() throws IOException {
		createAasDescriptorOnRegistryWithAuthorization(aasRegistryBaseUrl, getJSONStringFromFile(AAS_DESCRIPTOR_SIMPLE_2_JSON), getAccessToken(DummyCredentialStore.ADMIN_CREDENTIAL));

		String accessToken = getAccessToken(DummyCredentialStore.BASYX_ASSET_UPDATER_CREDENTIAL);

		CloseableHttpResponse retrievalResponse = updateElementWithAuthorizationPutRequest(getSpecificAasDescriptorAccessURL(SPECIFIC_SHELL_ID_2, "specificAasId-2-SM"), getJSONStringFromFile("authorization/SingleSubmodelDescriptor_Update.json"), accessToken);
		assertEquals(HttpStatus.NO_CONTENT.value(), retrievalResponse.getCode());

		deleteElementWithAuthorization(getSpecificAasDescriptorAccessURL(SPECIFIC_SHELL_ID_2), getAccessToken(DummyCredentialStore.ADMIN_CREDENTIAL));
	}

	@Test
	public void replaceAasDescriptorWithCorrectRoleAndSpecificAasPermission() throws IOException {
		createAasDescriptorOnRegistryWithAuthorization(aasRegistryBaseUrl, getJSONStringFromFile(AAS_DESCRIPTOR_SIMPLE_2_JSON), getAccessToken(DummyCredentialStore.ADMIN_CREDENTIAL));

		String accessToken = getAccessToken(DummyCredentialStore.BASYX_ASSET_UPDATER_TWO_CREDENTIAL);

		CloseableHttpResponse retrievalResponse = updateElementWithAuthorizationPutRequest(getSpecificAasDescriptorAccessURL(SPECIFIC_SHELL_ID_2, "specificAasId-2-SM"), getJSONStringFromFile("authorization/SingleSubmodelDescriptor_Update.json"), accessToken);
		assertEquals(HttpStatus.NO_CONTENT.value(), retrievalResponse.getCode());

		deleteElementWithAuthorization(getSpecificAasDescriptorAccessURL(SPECIFIC_SHELL_ID_2), getAccessToken(DummyCredentialStore.ADMIN_CREDENTIAL));
	}

	@Test
	public void replaceAasDescriptorWithCorrectRoleAndUnauthorizedSpecificAas() throws IOException {
		String accessToken = getAccessToken(DummyCredentialStore.BASYX_ASSET_UPDATER_TWO_CREDENTIAL);

		CloseableHttpResponse retrievalResponse = updateElementWithAuthorizationPutRequest(getSpecificAasDescriptorAccessURL(SPECIFIC_SHELL_ID, "specificAasId-2-SM"), getJSONStringFromFile("authorization/SingleSubmodelDescriptor_Update.json"), accessToken);
		assertEquals(HttpStatus.FORBIDDEN.value(), retrievalResponse.getCode());
	}

	@Test
	public void replaceAasDescriptorWithInsufficientPermissionRole() throws IOException {
		String accessToken = getAccessToken(DummyCredentialStore.BASYX_CREATOR_CREDENTIAL);

		CloseableHttpResponse retrievalResponse = updateElementWithAuthorizationPutRequest(getSpecificAasDescriptorAccessURL(SPECIFIC_SHELL_ID_2, "specificAasId-2-SM"), getJSONStringFromFile("authorization/SingleSubmodelDescriptor_Update.json"), accessToken);
		assertEquals(HttpStatus.FORBIDDEN.value(), retrievalResponse.getCode());
	}

	@Test
	public void replaceAasDescriptorWithNoAuthorization() throws IOException {
		CloseableHttpResponse retrievalResponse = updateElementWithNoAuthorizationPutRequest(getSpecificAasDescriptorAccessURL(SPECIFIC_SHELL_ID_2, "specificAasId-2-SM"), getJSONStringFromFile("authorization/SingleSubmodelDescriptor_Update.json"));

		assertEquals(HttpStatus.UNAUTHORIZED.value(), retrievalResponse.getCode());
	}

	@Test
	public void clearAllDescriptorsWithCorrectRoleAndPermission() throws IOException {
		String accessToken = getAccessToken(DummyCredentialStore.BASYX_DELETER_CREDENTIAL);

		CloseableHttpResponse retrievalResponse = deleteElementWithAuthorization(aasRegistryBaseUrl, accessToken);
		assertEquals(HttpStatus.NO_CONTENT.value(), retrievalResponse.getCode());

		assertElementIsNotOnServer(getSpecificAasDescriptorAccessURL(SPECIFIC_SHELL_ID), getAccessToken(DummyCredentialStore.ADMIN_CREDENTIAL));
	}

	@Test
	public void clearAllDescriptorsWithCorrectRoleAndSpecificAasPermission() throws IOException {
		String accessToken = getAccessToken(DummyCredentialStore.BASYX_DELETER_TWO_CREDENTIAL);

		CloseableHttpResponse retrievalResponse = deleteElementWithAuthorization(aasRegistryBaseUrl, accessToken);
		assertEquals(HttpStatus.FORBIDDEN.value(), retrievalResponse.getCode());

		assertElementExistsOnServer(getSpecificAasDescriptorAccessURL(SPECIFIC_SHELL_ID), getAccessToken(DummyCredentialStore.ADMIN_CREDENTIAL));
	}

	@Test
	public void clearAllDescriptorsWithCorrectRoleAndUnauthorizedSpecificAas() throws IOException {
		String accessToken = getAccessToken(DummyCredentialStore.BASYX_DELETER_TWO_CREDENTIAL);

		CloseableHttpResponse retrievalResponse = deleteElementWithAuthorization(aasRegistryBaseUrl, accessToken);
		assertEquals(HttpStatus.FORBIDDEN.value(), retrievalResponse.getCode());

		assertElementExistsOnServer(getSpecificAasDescriptorAccessURL(SPECIFIC_SHELL_ID), getAccessToken(DummyCredentialStore.ADMIN_CREDENTIAL));
	}

	@Test
	public void clearAllDescriptorsWithInsufficientPermissionRole() throws IOException {
		String accessToken = getAccessToken(DummyCredentialStore.BASYX_UPDATER_CREDENTIAL);

		CloseableHttpResponse retrievalResponse = deleteElementWithAuthorization(aasRegistryBaseUrl, accessToken);
		assertEquals(HttpStatus.FORBIDDEN.value(), retrievalResponse.getCode());

		assertElementExistsOnServer(getSpecificAasDescriptorAccessURL(SPECIFIC_SHELL_ID), getAccessToken(DummyCredentialStore.ADMIN_CREDENTIAL));
	}

	@Test
	public void clearAllDescriptorsWithNoAuthorization() throws IOException {
		CloseableHttpResponse retrievalResponse = deleteElementWithNoAuthorization(getSpecificAasDescriptorAccessURL(SPECIFIC_SHELL_ID));

		assertEquals(HttpStatus.UNAUTHORIZED.value(), retrievalResponse.getCode());

		assertElementExistsOnServer(getSpecificAasDescriptorAccessURL(SPECIFIC_SHELL_ID), getAccessToken(DummyCredentialStore.ADMIN_CREDENTIAL));
	}

	@Test
	public void searchDescriptorsWithCorrectRoleAndPermission() throws IOException, ParseException {
		DummyCredential dummyCredential = DummyCredentialStore.BASYX_READER_CREDENTIAL;

		String accessToken = tokenProvider.getAccessToken(dummyCredential.getUsername(), dummyCredential.getPassword());

		CloseableHttpResponse retrievalResponse = createAasDescriptorOnRegistryWithAuthorization(getSearchUrl(), getJSONStringFromFile("authorization/ShellDescriptorSearchRequest.json"), accessToken);

		System.out.println(BaSyxHttpTestUtils.getResponseAsString(retrievalResponse));

		assertEquals(HttpStatus.OK.value(), retrievalResponse.getCode());
	}

	@Test
	public void searchDescriptorsCorrectRoleAndSpecificAasPermission() throws IOException, ParseException {
		DummyCredential dummyCredential = DummyCredentialStore.BASYX_READER_TWO_CREDENTIAL;

		String accessToken = tokenProvider.getAccessToken(dummyCredential.getUsername(), dummyCredential.getPassword());

		CloseableHttpResponse retrievalResponse = createAasDescriptorOnRegistryWithAuthorization(getSearchUrl(), getJSONStringFromFile("authorization/ShellDescriptorSearchRequest.json"), accessToken);

		System.out.println(BaSyxHttpTestUtils.getResponseAsString(retrievalResponse));

		assertEquals(HttpStatus.FORBIDDEN.value(), retrievalResponse.getCode());
	}

	@Test
	public void searchDescriptorsWithInsufficientPermissionRole() throws IOException {
		DummyCredential dummyCredential = DummyCredentialStore.BASYX_UPDATER_CREDENTIAL;

		String accessToken = tokenProvider.getAccessToken(dummyCredential.getUsername(), dummyCredential.getPassword());

		CloseableHttpResponse retrievalResponse = createAasDescriptorOnRegistryWithAuthorization(getSearchUrl(), getJSONStringFromFile("authorization/ShellDescriptorSearchRequest.json"), accessToken);

		assertEquals(HttpStatus.FORBIDDEN.value(), retrievalResponse.getCode());
	}

	@Test
	public void searchDescriptorsWithNoAuthorization() throws IOException {
		CloseableHttpResponse retrievalResponse = createAasDescriptorOnRepositoryWithNoAuthorization(getSearchUrl(), getJSONStringFromFile("authorization/ShellDescriptorSearchRequest.json"));
		assertEquals(HttpStatus.UNAUTHORIZED.value(), retrievalResponse.getCode());
	}

	private String getSearchUrl() {
		return BASE_URL + "/search";
	}

	private String getAasDescriptorsAccessURL(String shellId) {
		return getSpecificAasDescriptorAccessURL(shellId) + "/submodel-descriptors";
	}

	private String getSpecificAasDescriptorAccessURL(String shellId, String submodelId) {
		return getAasDescriptorsAccessURL(shellId) + "/" + Base64UrlEncodedIdentifier.encodeIdentifier(submodelId);
	}

	private CloseableHttpResponse deleteElementWithNoAuthorization(String url) throws IOException {
		return BaSyxHttpTestUtils.executeDeleteOnURL(url);
	}

	private void assertElementIsNotOnServer(String url, String accessToken) throws IOException {
		CloseableHttpResponse getResponse = getElementWithAuthorization(url, accessToken);
		assertEquals(HttpStatus.NOT_FOUND.value(), getResponse.getCode());
	}

	private void assertElementExistsOnServer(String url, String accessToken) throws IOException {
		CloseableHttpResponse getResponse = getElementWithAuthorization(url, accessToken);
		assertEquals(HttpStatus.OK.value(), getResponse.getCode());
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

	private static CloseableHttpResponse createAasDescriptorOnRepositoryWithNoAuthorization(String url, String aasJsonContent) throws IOException {
		return BaSyxHttpTestUtils.executePostOnURL(createAasRegistryUrl(url), aasJsonContent);
	}

	private CloseableHttpResponse deleteElementWithAuthorization(String url, String accessToken) throws IOException {
		return BaSyxHttpTestUtils.executeAuthorizedDeleteOnURL(url, accessToken);
	}

	private static String getJSONStringFromFile(String fileName) throws FileNotFoundException, IOException {
		return BaSyxHttpTestUtils.readJSONStringFromClasspath(fileName);
	}

	private static CloseableHttpResponse createAasDescriptorOnRegistryWithAuthorization(String url, String aasJsonContent, String accessToken) throws IOException {
		return BaSyxHttpTestUtils.executeAuthorizedPostOnURL(url, aasJsonContent, accessToken);
	}

	private String getAccessToken(DummyCredential dummyCredential) {
		return tokenProvider.getAccessToken(dummyCredential.getUsername(), dummyCredential.getPassword());
	}

	private void clearSecurityContext() {
		SecurityContextHolder.clearContext();
	}

	private void configureSecurityContext() throws FileNotFoundException, IOException {
		String adminToken = getAdminAccessToken();

		String modulus = getStringFromFile("authorization/modulus.txt");
		String exponent = "AQAB";

		RSAPublicKey rsaPublicKey = PublicKeyUtils.buildPublicKey(modulus, exponent);

		Jwt jwt = JwtTokenDecoder.decodeJwt(adminToken, rsaPublicKey);

		SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(jwt));
	}

	protected CloseableHttpResponse getElementWithAuthorization(String url, String accessToken) throws IOException {
		return BaSyxHttpTestUtils.executeAuthorizedGetOnURL(url, accessToken);
	}

	protected CloseableHttpResponse getElementWithNoAuthorization(String url) throws IOException {
		return BaSyxHttpTestUtils.executeGetOnURL(url);
	}

	private static String getAdminAccessToken() {
		DummyCredential dummyCredential = DummyCredentialStore.ADMIN_CREDENTIAL;

		return tokenProvider.getAccessToken(dummyCredential.getUsername(), dummyCredential.getPassword());
	}

	private static void createDummyShellDescriptorsOnRegistry(int descriptorsCount) {
		List<AssetAdministrationShellDescriptor> dummyDescriptors = new ArrayList<>();
		for(int i = 0; i < descriptorsCount; i++)
			dummyDescriptors.add(createDummyDescriptor("dummyShellId_" + i, "dummyShellIdShort_" + i));

		dummyDescriptors.forEach(descriptor -> storage.insertAasDescriptor(descriptor));
	}

	private static AssetAdministrationShellDescriptor createDummyDescriptor(String shellId, String shellIdShort) {

		AssetAdministrationShellDescriptor descriptor = new AssetAdministrationShellDescriptor(shellId);
		descriptor.setIdShort(shellIdShort);
		descriptor.setAssetKind(AssetKind.TYPE);
		descriptor.setAssetType("TestAsset");

		setEndpointItem(shellId, descriptor);
		descriptor.setGlobalAssetId("DummyGlobalAssetId");
		descriptor.addSubmodelDescriptorsItem(createDummySubmodelDescriptor(shellId + "-SM", shellIdShort + "-SM"));

		return descriptor;
	}

	private static SubmodelDescriptor createDummySubmodelDescriptor(String submodelId, String submodelIdShort) {

		SubmodelDescriptor descriptor = new SubmodelDescriptor(submodelId, new ArrayList<>());
		descriptor.setIdShort(submodelIdShort);

		return descriptor;
	}

	private static void setEndpointItem(String shellId, AssetAdministrationShellDescriptor descriptor) {

		ProtocolInformation protocolInformation = createProtocolInformation(shellId);

		Endpoint endpoint = new Endpoint("AAS-3.0", protocolInformation);
		descriptor.addEndpointsItem(endpoint);
	}

	private static ProtocolInformation createProtocolInformation(String shellId) {
		String href = String.format("%s/%s", BASE_URL + "/shells", Base64UrlEncodedIdentifier.encodeIdentifier(shellId));

		ProtocolInformation protocolInformation = new ProtocolInformation(href);
		protocolInformation.endpointProtocol(getProtocol(href));

		return protocolInformation;
	}

	private static String getProtocol(String endpoint) {
		try {
			return new URL(endpoint).getProtocol();
		} catch (MalformedURLException e) {
			throw new RuntimeException();
		}
	}

	private CloseableHttpResponse getAllElementsWithAuthorization(String url, String accessToken) throws IOException {
		return BaSyxHttpTestUtils.executeAuthorizedGetOnURL(url, accessToken);
	}

	private CloseableHttpResponse getAllElementsNoAuthorization(String url) throws IOException {
		return BaSyxHttpTestUtils.executeGetOnURL(url);
	}

	private static String getStringFromFile(String fileName) throws FileNotFoundException, IOException {
		return BaSyxHttpTestUtils.readJSONStringFromClasspath(fileName);
	}

	private String getSpecificAasDescriptorAccessURL(String shellId) {
		return createAasRegistryUrl(aasRegistryBaseUrl) + "/" + Base64UrlEncodedIdentifier.encodeIdentifier(shellId);
	}

	private static String createAasRegistryUrl(String aasRepositoryBaseURL) {

		try {
			return new URL(new URL(aasRepositoryBaseURL), AAS_REGISTRY_PATH).toString();
		} catch (MalformedURLException e) {
			throw new RuntimeException("The AAS Registry Base url is malformed. " + e.getMessage());
		}
	}

}

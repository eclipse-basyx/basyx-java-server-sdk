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

package org.eclipse.digitaltwin.basyx.submodelregistry.regression.feature.authorization;

import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.ParseException;
import org.eclipse.digitaltwin.basyx.submodelregistry.feature.authorization.AuthorizedSubmodelRegistryStorage;
import org.eclipse.digitaltwin.basyx.authorization.AccessTokenProvider;
import org.eclipse.digitaltwin.basyx.authorization.DummyCredential;
import org.eclipse.digitaltwin.basyx.authorization.DummyCredentialStore;
import org.eclipse.digitaltwin.basyx.authorization.jwt.JwtTokenDecoder;
import org.eclipse.digitaltwin.basyx.authorization.jwt.PublicKeyUtils;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.eclipse.digitaltwin.basyx.http.Base64UrlEncodedIdentifier;
import org.eclipse.digitaltwin.basyx.http.serialization.BaSyxHttpTestUtils;
import org.eclipse.digitaltwin.basyx.submodelregistry.model.Endpoint;
import org.eclipse.digitaltwin.basyx.submodelregistry.model.ProtocolInformation;
import org.eclipse.digitaltwin.basyx.submodelregistry.model.SubmodelDescriptor;
import org.eclipse.digitaltwin.basyx.submodelregistry.service.storage.SubmodelRegistryStorage;
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
 * Tests for {@link AuthorizedSubmodelRegistryStorage} feature
 *
 * @author danish
 */
public class TestAuthorizedSubmodelRegistry {

	private static final String SUBMODEL_DESCRIPTOR_SIMPLE_2_JSON = "authorization/SubmodelDescriptorSimple_2.json";
	private static final String SUBMODEL_DESCRIPTOR_SIMPLE_1_JSON = "authorization/SubmodelDescriptorSimple_1.json";
	private static final String SPECIFIC_SUBMODEL_ID_2 = "specificSubmodelId-2";
	private static final String SPECIFIC_SUBMODEL_ID = "dummySubmodelId_3";
	private static AccessTokenProvider tokenProvider;
	private static ConfigurableApplicationContext appContext;
	private static final String BASE_URL = "http://127.0.0.1:8080";
	public static String submodelRegistryBaseUrl = BASE_URL + "/submodel-descriptors";
	private static SubmodelRegistryStorage storage;

	@BeforeClass
	public static void setUp() throws FileNotFoundException, IOException {
		String authenticaltionServerTokenEndpoint = "http://localhost:9096/realms/BaSyx/protocol/openid-connect/token";
		String clientId = "basyx-client-api";

		tokenProvider = new AccessTokenProvider(authenticaltionServerTokenEndpoint, clientId);

		appContext = new SpringApplication(DummySubmodelRegistryComponent.class).run(new String[] {});

		storage = appContext.getBean(SubmodelRegistryStorage.class);
	}

	@AfterClass
	public static void tearDown() {
		appContext.close();
	}

	@Before
	public void initializeRepositories() throws IOException {
		configureSecurityContext();

		createDummySubmodelDescriptorsOnRegistry(5);

		clearSecurityContext();
	}

	@After
	public void reset() throws IOException {
		configureSecurityContext();

		Collection<SubmodelDescriptor> descriptors = storage.getAllSubmodelDescriptors(PaginationInfo.NO_LIMIT).getResult();

		descriptors.forEach(descriptor -> storage.removeSubmodelDescriptor(descriptor.getId()));

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
	public void getAllSubmodelDescriptorsWithCorrectRoleAndPermission() throws IOException, ParseException {
		DummyCredential dummyCredential = DummyCredentialStore.BASYX_READER_CREDENTIAL;

		String accessToken = tokenProvider.getAccessToken(dummyCredential.getUsername(), dummyCredential.getPassword());

		CloseableHttpResponse retrievalResponse = getAllElementsWithAuthorization(submodelRegistryBaseUrl, accessToken);

		assertEquals(HttpStatus.OK.value(), retrievalResponse.getCode());
	}

	@Test
	public void getAllSubmodelDescriptorsWithInsufficientPermissionRole() throws IOException {
		DummyCredential dummyCredential = DummyCredentialStore.BASYX_UPDATER_CREDENTIAL;

		String accessToken = tokenProvider.getAccessToken(dummyCredential.getUsername(), dummyCredential.getPassword());

		CloseableHttpResponse retrievalResponse = getAllElementsWithAuthorization(submodelRegistryBaseUrl, accessToken);
		assertEquals(HttpStatus.FORBIDDEN.value(), retrievalResponse.getCode());
	}

	@Test
	public void getAllAasDescriptorsWithNoAuthorization() throws IOException {
		CloseableHttpResponse retrievalResponse = getAllElementsNoAuthorization(submodelRegistryBaseUrl);
		assertEquals(HttpStatus.UNAUTHORIZED.value(), retrievalResponse.getCode());
	}

	@Test
	public void createSubmodelDescriptorWithCorrectRoleAndPermission() throws IOException {
		String accessToken = getAccessToken(DummyCredentialStore.BASYX_CREATOR_CREDENTIAL);

		CloseableHttpResponse retrievalResponse = createElementOnRegistryWithAuthorization(submodelRegistryBaseUrl, getJSONStringFromFile(SUBMODEL_DESCRIPTOR_SIMPLE_2_JSON), accessToken);
		assertEquals(HttpStatus.CREATED.value(), retrievalResponse.getCode());

		deleteElementWithAuthorization(getSpecificSubmodelDescriptorAccessURL(SPECIFIC_SUBMODEL_ID_2), getAccessToken(DummyCredentialStore.ADMIN_CREDENTIAL));
	}

	@Test
	public void createSubmodelDescriptorWithInsufficientPermissionRole() throws IOException {
		String accessToken = getAccessToken(DummyCredentialStore.BASYX_READER_CREDENTIAL);

		CloseableHttpResponse retrievalResponse = createElementOnRegistryWithAuthorization(submodelRegistryBaseUrl, getJSONStringFromFile(SUBMODEL_DESCRIPTOR_SIMPLE_2_JSON), accessToken);
		assertEquals(HttpStatus.FORBIDDEN.value(), retrievalResponse.getCode());
	}

	@Test
	public void createSubmodelDescriptorWithNoAuthorization() throws IOException {
		CloseableHttpResponse retrievalResponse = createSubmodelDescriptorOnRepositoryWithNoAuthorization(submodelRegistryBaseUrl, getJSONStringFromFile(SUBMODEL_DESCRIPTOR_SIMPLE_2_JSON));

		assertEquals(HttpStatus.UNAUTHORIZED.value(), retrievalResponse.getCode());
	}

	@Test
	public void updateSubmodelDescriptorWithCorrectRoleAndPermission() throws IOException {
		String accessToken = getAccessToken(DummyCredentialStore.BASYX_UPDATER_CREDENTIAL);

		CloseableHttpResponse retrievalResponse = updateElementWithAuthorizationPutRequest(getSpecificSubmodelDescriptorAccessURL(SPECIFIC_SUBMODEL_ID), getJSONStringFromFile(SUBMODEL_DESCRIPTOR_SIMPLE_1_JSON), accessToken);
		assertEquals(HttpStatus.NO_CONTENT.value(), retrievalResponse.getCode());
	}

	@Test
	public void updateSubmodelDescriptorWithCorrectRoleAndSpecificAasPermission() throws IOException {
		String accessToken = getAccessToken(DummyCredentialStore.BASYX_UPDATER_TWO_CREDENTIAL);

		CloseableHttpResponse retrievalResponse = updateElementWithAuthorizationPutRequest(getSpecificSubmodelDescriptorAccessURL(SPECIFIC_SUBMODEL_ID), getJSONStringFromFile(SUBMODEL_DESCRIPTOR_SIMPLE_1_JSON), accessToken);
		assertEquals(HttpStatus.NO_CONTENT.value(), retrievalResponse.getCode());
	}

	@Test
	public void updateSubmodelDescriptorsWithCorrectRoleAndUnauthorizedSpecificAas() throws IOException {
		String accessToken = getAccessToken(DummyCredentialStore.BASYX_UPDATER_TWO_CREDENTIAL);

		CloseableHttpResponse retrievalResponse = updateElementWithAuthorizationPutRequest(getSpecificSubmodelDescriptorAccessURL(SPECIFIC_SUBMODEL_ID_2), getJSONStringFromFile(SUBMODEL_DESCRIPTOR_SIMPLE_1_JSON), accessToken);
		assertEquals(HttpStatus.FORBIDDEN.value(), retrievalResponse.getCode());
	}

	@Test
	public void updateSubmodelDescriptorWithInsufficientPermissionRole() throws IOException {
		String accessToken = getAccessToken(DummyCredentialStore.BASYX_READER_CREDENTIAL);

		CloseableHttpResponse retrievalResponse = updateElementWithAuthorizationPutRequest(getSpecificSubmodelDescriptorAccessURL(SPECIFIC_SUBMODEL_ID), getJSONStringFromFile(SUBMODEL_DESCRIPTOR_SIMPLE_1_JSON), accessToken);
		assertEquals(HttpStatus.FORBIDDEN.value(), retrievalResponse.getCode());
	}

	@Test
	public void updateSubmodelDescriptorWithNoAuthorization() throws IOException {
		CloseableHttpResponse retrievalResponse = updateElementWithNoAuthorizationPutRequest(getSpecificSubmodelDescriptorAccessURL(SPECIFIC_SUBMODEL_ID), getJSONStringFromFile(SUBMODEL_DESCRIPTOR_SIMPLE_1_JSON));

		assertEquals(HttpStatus.UNAUTHORIZED.value(), retrievalResponse.getCode());
	}

	@Test
	public void deleteSubmodelDescriptorWithCorrectRoleAndPermission() throws IOException {
		createElementOnRegistryWithAuthorization(submodelRegistryBaseUrl, getJSONStringFromFile(SUBMODEL_DESCRIPTOR_SIMPLE_2_JSON), getAccessToken(DummyCredentialStore.ADMIN_CREDENTIAL));

		String accessToken = getAccessToken(DummyCredentialStore.BASYX_DELETER_CREDENTIAL);

		CloseableHttpResponse retrievalResponse = deleteElementWithAuthorization(getSpecificSubmodelDescriptorAccessURL(SPECIFIC_SUBMODEL_ID_2), accessToken);
		assertEquals(HttpStatus.NO_CONTENT.value(), retrievalResponse.getCode());

		assertElementIsNotOnServer(getSpecificSubmodelDescriptorAccessURL(SPECIFIC_SUBMODEL_ID_2), getAccessToken(DummyCredentialStore.ADMIN_CREDENTIAL));

		deleteElementWithAuthorization(getSpecificSubmodelDescriptorAccessURL(SPECIFIC_SUBMODEL_ID_2), getAccessToken(DummyCredentialStore.ADMIN_CREDENTIAL));
	}

	@Test
	public void deleteSubmodelDescriptorWithCorrectRoleAndSpecificAasPermission() throws IOException {
		createElementOnRegistryWithAuthorization(submodelRegistryBaseUrl, getJSONStringFromFile(SUBMODEL_DESCRIPTOR_SIMPLE_2_JSON), getAccessToken(DummyCredentialStore.ADMIN_CREDENTIAL));

		String accessToken = getAccessToken(DummyCredentialStore.BASYX_DELETER_TWO_CREDENTIAL);

		CloseableHttpResponse retrievalResponse = deleteElementWithAuthorization(getSpecificSubmodelDescriptorAccessURL(SPECIFIC_SUBMODEL_ID_2), accessToken);
		assertEquals(HttpStatus.NO_CONTENT.value(), retrievalResponse.getCode());

		assertElementIsNotOnServer(getSpecificSubmodelDescriptorAccessURL(SPECIFIC_SUBMODEL_ID_2), getAccessToken(DummyCredentialStore.ADMIN_CREDENTIAL));

		deleteElementWithAuthorization(getSpecificSubmodelDescriptorAccessURL(SPECIFIC_SUBMODEL_ID_2), getAccessToken(DummyCredentialStore.ADMIN_CREDENTIAL));
	}

	@Test
	public void deleteSubmodelDescriptorWithCorrectRoleAndUnauthorizedSpecificAas() throws IOException {
		String accessToken = getAccessToken(DummyCredentialStore.BASYX_DELETER_TWO_CREDENTIAL);

		CloseableHttpResponse retrievalResponse = deleteElementWithAuthorization(getSpecificSubmodelDescriptorAccessURL(SPECIFIC_SUBMODEL_ID), accessToken);
		assertEquals(HttpStatus.FORBIDDEN.value(), retrievalResponse.getCode());

		assertElementExistsOnServer(getSpecificSubmodelDescriptorAccessURL(SPECIFIC_SUBMODEL_ID), getAccessToken(DummyCredentialStore.ADMIN_CREDENTIAL));
	}

	@Test
	public void deleteSubmodelDescriptorWithInsufficientPermissionRole() throws IOException {
		String accessToken = getAccessToken(DummyCredentialStore.BASYX_UPDATER_CREDENTIAL);

		CloseableHttpResponse retrievalResponse = deleteElementWithAuthorization(getSpecificSubmodelDescriptorAccessURL(SPECIFIC_SUBMODEL_ID), accessToken);
		assertEquals(HttpStatus.FORBIDDEN.value(), retrievalResponse.getCode());

		assertElementExistsOnServer(getSpecificSubmodelDescriptorAccessURL(SPECIFIC_SUBMODEL_ID), getAccessToken(DummyCredentialStore.ADMIN_CREDENTIAL));
	}

	@Test
	public void deleteSubmodelDescriptorWithNoAuthorization() throws IOException {
		CloseableHttpResponse retrievalResponse = deleteElementWithNoAuthorization(getSpecificSubmodelDescriptorAccessURL(SPECIFIC_SUBMODEL_ID));

		assertEquals(HttpStatus.UNAUTHORIZED.value(), retrievalResponse.getCode());

		assertElementExistsOnServer(getSpecificSubmodelDescriptorAccessURL(SPECIFIC_SUBMODEL_ID), getAccessToken(DummyCredentialStore.ADMIN_CREDENTIAL));
	}

	@Test
	public void getSubmodelDescriptorWithCorrectRoleAndPermission() throws IOException {
		String accessToken = getAccessToken(DummyCredentialStore.BASYX_READER_CREDENTIAL);

		CloseableHttpResponse retrievalResponse = getElementWithAuthorization(getSpecificSubmodelDescriptorAccessURL(SPECIFIC_SUBMODEL_ID), accessToken);
		assertEquals(HttpStatus.OK.value(), retrievalResponse.getCode());
	}

	@Test
	public void getSubmodelDescriptorWithCorrectRoleAndSpecificAasPermission() throws IOException {
		DummyCredential dummyCredential = DummyCredentialStore.BASYX_READER_TWO_CREDENTIAL;

		String accessToken = tokenProvider.getAccessToken(dummyCredential.getUsername(), dummyCredential.getPassword());

		CloseableHttpResponse retrievalResponse = getElementWithAuthorization(getSpecificSubmodelDescriptorAccessURL(SPECIFIC_SUBMODEL_ID), accessToken);
		assertEquals(HttpStatus.OK.value(), retrievalResponse.getCode());
	}

	@Test
	public void getSubmodelDescriptorWithCorrectRoleAndUnauthorizedSpecificAas() throws IOException {
		DummyCredential dummyCredential = DummyCredentialStore.BASYX_READER_TWO_CREDENTIAL;

		String accessToken = tokenProvider.getAccessToken(dummyCredential.getUsername(), dummyCredential.getPassword());

		CloseableHttpResponse retrievalResponse = getElementWithAuthorization(getSpecificSubmodelDescriptorAccessURL(SPECIFIC_SUBMODEL_ID_2), accessToken);
		assertEquals(HttpStatus.FORBIDDEN.value(), retrievalResponse.getCode());
	}

	@Test
	public void getSubmodelDescriptorWithInsufficientPermissionRole() throws IOException {
		DummyCredential dummyCredential = DummyCredentialStore.BASYX_UPDATER_CREDENTIAL;

		String accessToken = tokenProvider.getAccessToken(dummyCredential.getUsername(), dummyCredential.getPassword());

		CloseableHttpResponse retrievalResponse = getElementWithAuthorization(getSpecificSubmodelDescriptorAccessURL(SPECIFIC_SUBMODEL_ID), accessToken);
		assertEquals(HttpStatus.FORBIDDEN.value(), retrievalResponse.getCode());
	}

	@Test
	public void getSubmodelDescriptorWithNoAuthorization() throws IOException {
		CloseableHttpResponse retrievalResponse = getElementWithNoAuthorization(getSpecificSubmodelDescriptorAccessURL(SPECIFIC_SUBMODEL_ID));
		assertEquals(HttpStatus.UNAUTHORIZED.value(), retrievalResponse.getCode());
	}

	@Test
	public void clearAllDescriptorsWithCorrectRoleAndPermission() throws IOException {
		String accessToken = getAccessToken(DummyCredentialStore.BASYX_DELETER_CREDENTIAL);

		CloseableHttpResponse retrievalResponse = deleteElementWithAuthorization(submodelRegistryBaseUrl, accessToken);
		assertEquals(HttpStatus.NO_CONTENT.value(), retrievalResponse.getCode());

		assertElementIsNotOnServer(getSpecificSubmodelDescriptorAccessURL(SPECIFIC_SUBMODEL_ID), getAccessToken(DummyCredentialStore.ADMIN_CREDENTIAL));
	}

	@Test
	public void clearAllDescriptorsWithCorrectRoleAndSpecificAasPermission() throws IOException {
		String accessToken = getAccessToken(DummyCredentialStore.BASYX_DELETER_TWO_CREDENTIAL);

		CloseableHttpResponse retrievalResponse = deleteElementWithAuthorization(submodelRegistryBaseUrl, accessToken);
		assertEquals(HttpStatus.FORBIDDEN.value(), retrievalResponse.getCode());

		assertElementExistsOnServer(getSpecificSubmodelDescriptorAccessURL(SPECIFIC_SUBMODEL_ID), getAccessToken(DummyCredentialStore.ADMIN_CREDENTIAL));
	}

	@Test
	public void clearAllDescriptorsWithCorrectRoleAndUnauthorizedSpecificAas() throws IOException {
		String accessToken = getAccessToken(DummyCredentialStore.BASYX_DELETER_TWO_CREDENTIAL);

		CloseableHttpResponse retrievalResponse = deleteElementWithAuthorization(submodelRegistryBaseUrl, accessToken);
		assertEquals(HttpStatus.FORBIDDEN.value(), retrievalResponse.getCode());

		assertElementExistsOnServer(getSpecificSubmodelDescriptorAccessURL(SPECIFIC_SUBMODEL_ID), getAccessToken(DummyCredentialStore.ADMIN_CREDENTIAL));
	}

	@Test
	public void clearAllDescriptorsWithInsufficientPermissionRole() throws IOException {
		String accessToken = getAccessToken(DummyCredentialStore.BASYX_UPDATER_CREDENTIAL);

		CloseableHttpResponse retrievalResponse = deleteElementWithAuthorization(submodelRegistryBaseUrl, accessToken);
		assertEquals(HttpStatus.FORBIDDEN.value(), retrievalResponse.getCode());

		assertElementExistsOnServer(getSpecificSubmodelDescriptorAccessURL(SPECIFIC_SUBMODEL_ID), getAccessToken(DummyCredentialStore.ADMIN_CREDENTIAL));
	}

	@Test
	public void clearAllDescriptorsWithNoAuthorization() throws IOException {
		CloseableHttpResponse retrievalResponse = deleteElementWithNoAuthorization(getSpecificSubmodelDescriptorAccessURL(SPECIFIC_SUBMODEL_ID));

		assertEquals(HttpStatus.UNAUTHORIZED.value(), retrievalResponse.getCode());

		assertElementExistsOnServer(getSpecificSubmodelDescriptorAccessURL(SPECIFIC_SUBMODEL_ID), getAccessToken(DummyCredentialStore.ADMIN_CREDENTIAL));
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

	private CloseableHttpResponse updateElementWithNoAuthorizationPutRequest(String url, String aasJsonContent) throws IOException {
		return BaSyxHttpTestUtils.executePutOnURL(url, aasJsonContent);
	}

	private static CloseableHttpResponse createSubmodelDescriptorOnRepositoryWithNoAuthorization(String url, String aasJsonContent) throws IOException {
		return BaSyxHttpTestUtils.executePostOnURL(url, aasJsonContent);
	}

	private CloseableHttpResponse deleteElementWithAuthorization(String url, String accessToken) throws IOException {
		return BaSyxHttpTestUtils.executeAuthorizedDeleteOnURL(url, accessToken);
	}

	private static String getJSONStringFromFile(String fileName) throws FileNotFoundException, IOException {
		return BaSyxHttpTestUtils.readJSONStringFromClasspath(fileName);
	}

	private static CloseableHttpResponse createElementOnRegistryWithAuthorization(String url, String aasJsonContent, String accessToken) throws IOException {
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

	private static void createDummySubmodelDescriptorsOnRegistry(int descriptorsCount) {
		List<SubmodelDescriptor> dummyDescriptors = new ArrayList<>();
		for(int i = 0; i < descriptorsCount; i++)
			dummyDescriptors.add(createDummyDescriptor("dummySubmodelId_" + i, "dummySubmodelIdShort_" + i));

		dummyDescriptors.forEach(descriptor -> storage.insertSubmodelDescriptor(descriptor));
	}

	private static SubmodelDescriptor createDummyDescriptor(String submodelId, String submodelIdShort) {
		SubmodelDescriptor descriptor = new SubmodelDescriptor(submodelId, new ArrayList<>());
		descriptor.setIdShort(submodelIdShort);

		setEndpointItem(submodelId, descriptor);

		return descriptor;
	}

	private static void setEndpointItem(String submodelId, SubmodelDescriptor descriptor) {

		ProtocolInformation protocolInformation = createProtocolInformation(submodelId);

		Endpoint endpoint = new Endpoint("AAS-3.0", protocolInformation);
		descriptor.addEndpointsItem(endpoint);
	}

	private static ProtocolInformation createProtocolInformation(String submodelId) {
		String href = String.format("%s/%s", BASE_URL + "/submodels", Base64UrlEncodedIdentifier.encodeIdentifier(submodelId));

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

	private static String getStringFromFile(String fileName) throws IOException {
		return BaSyxHttpTestUtils.readJSONStringFromClasspath(fileName);
	}

	private String getSpecificSubmodelDescriptorAccessURL(String submodelId) {
		return submodelRegistryBaseUrl + "/" + Base64UrlEncodedIdentifier.encodeIdentifier(submodelId);
	}

}

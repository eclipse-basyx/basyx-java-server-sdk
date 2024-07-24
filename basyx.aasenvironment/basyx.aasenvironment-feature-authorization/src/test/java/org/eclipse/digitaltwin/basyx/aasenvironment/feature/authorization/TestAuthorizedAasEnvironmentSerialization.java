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

package org.eclipse.digitaltwin.basyx.aasenvironment.feature.authorization;

import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.interfaces.RSAPublicKey;
import java.util.Collection;

import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.message.BasicHeader;
import org.eclipse.basyx.digitaltwin.aasenvironment.http.TestAasEnvironmentHTTP;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.ConceptDescription;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.basyx.aasenvironment.TestAASEnvironmentSerialization;
import org.eclipse.digitaltwin.basyx.aasrepository.AasRepository;
import org.eclipse.digitaltwin.basyx.authorization.AccessTokenProvider;
import org.eclipse.digitaltwin.basyx.authorization.DummyCredential;
import org.eclipse.digitaltwin.basyx.authorization.DummyCredentialStore;
import org.eclipse.digitaltwin.basyx.authorization.jwt.JwtTokenDecoder;
import org.eclipse.digitaltwin.basyx.authorization.jwt.PublicKeyUtils;
import org.eclipse.digitaltwin.basyx.conceptdescriptionrepository.ConceptDescriptionRepository;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.eclipse.digitaltwin.basyx.http.serialization.BaSyxHttpTestUtils;
import org.eclipse.digitaltwin.basyx.submodelrepository.SubmodelRepository;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

/**
 * Tests for {@link AuthorizedAasEnvironment} serialization feature
 * 
 * @author danish
 */
public class TestAuthorizedAasEnvironmentSerialization {

	private static final PaginationInfo NO_LIMIT_PAGINATION_INFO = new PaginationInfo(0, null);
	private static String authenticaltionServerTokenEndpoint = "http://localhost:9096/realms/BaSyx/protocol/openid-connect/token";
	private static String clientId = "basyx-client-api";
	private static AccessTokenProvider tokenProvider;
	private static ConfigurableApplicationContext appContext;
	private static SubmodelRepository submodelRepo;
	private static AasRepository aasRepo;
	private static ConceptDescriptionRepository conceptDescriptionRepo;
	
	private static String healthEndpointUrl = "http://127.0.0.1:8081/actuator/health";

	@BeforeClass
	public static void setUp() throws FileNotFoundException, IOException {
		tokenProvider = new AccessTokenProvider(authenticaltionServerTokenEndpoint, clientId);

		appContext = new SpringApplication(DummyAasEnvironmentComponent.class).run(new String[] {});
		
		submodelRepo = appContext.getBean(SubmodelRepository.class);
		aasRepo = appContext.getBean(AasRepository.class);
		conceptDescriptionRepo = appContext.getBean(ConceptDescriptionRepository.class);
	}
	
	@AfterClass
	public static void tearDown() {
		appContext.close();
	}
	
	@After
	public void reset() throws FileNotFoundException, IOException {
		configureSecurityContext();
		
		Collection<AssetAdministrationShell> assetAdministrationShells = aasRepo.getAllAas(NO_LIMIT_PAGINATION_INFO).getResult();
		Collection<Submodel> submodels = submodelRepo.getAllSubmodels(NO_LIMIT_PAGINATION_INFO).getResult();
		Collection<ConceptDescription> conceptDescriptions = conceptDescriptionRepo.getAllConceptDescriptions(NO_LIMIT_PAGINATION_INFO).getResult();
		
		assetAdministrationShells.stream().forEach(aas -> aasRepo.deleteAas(aas.getId()));
		submodels.stream().forEach(sm -> submodelRepo.deleteSubmodel(sm.getId()));
		conceptDescriptions.stream().forEach(cd -> conceptDescriptionRepo.deleteConceptDescription(cd.getId()));


		createDummyShellsOnRepository(TestAASEnvironmentSerialization.createDummyShells(), aasRepo);
		createDummySubmodelsOnRepository(TestAASEnvironmentSerialization.createDummySubmodels(), submodelRepo);
		createDummyConceptDescriptionsOnRepository(TestAASEnvironmentSerialization.createDummyConceptDescriptions(), conceptDescriptionRepo);

		clearSecurityContext();
	}

	@Test
	public void healthEndpointWithoutAuthorization() throws IOException, ParseException {
		String expectedHealthEndpointOutput = getStringFromFile("authorization/HealthOutput.json");

		CloseableHttpResponse healthCheckResponse = BaSyxHttpTestUtils.executeGetOnURL(healthEndpointUrl);
		assertEquals(HttpStatus.OK.value(), healthCheckResponse.getCode());

		BaSyxHttpTestUtils.assertSameJSONContent(expectedHealthEndpointOutput, BaSyxHttpTestUtils.getResponseAsString(healthCheckResponse));
	}

	@Test
	public void createSerializationWithCorrectRoleAndPermission() throws IOException {		
		DummyCredential dummyCredential = DummyCredentialStore.BASYX_READER_CREDENTIAL;

		boolean includeConceptDescription = true;

		String accessToken = tokenProvider.getAccessToken(dummyCredential.getUsername(), dummyCredential.getPassword());

		CloseableHttpResponse retrievalResponse = getElementWithAuthorization(TestAasEnvironmentHTTP.createSerializationURL(includeConceptDescription), accessToken, new BasicHeader("Accept", TestAasEnvironmentHTTP.JSON_MIMETYPE));
		assertEquals(HttpStatus.OK.value(), retrievalResponse.getCode());
	}

	@Test
	public void createSerializationWithInsufficientPermissionRole() throws IOException {
		DummyCredential dummyCredential = DummyCredentialStore.BASYX_UPDATER_CREDENTIAL;

		boolean includeConceptDescription = true;

		String accessToken = tokenProvider.getAccessToken(dummyCredential.getUsername(), dummyCredential.getPassword());

		CloseableHttpResponse retrievalResponse = getElementWithAuthorization(TestAasEnvironmentHTTP.createSerializationURL(includeConceptDescription), accessToken, new BasicHeader("Accept", TestAasEnvironmentHTTP.XML_MIMETYPE));
		assertEquals(HttpStatus.FORBIDDEN.value(), retrievalResponse.getCode());
	}

	@Test
	public void createSerializationWithNoAuthorization() throws IOException {
		boolean includeConceptDescription = true;

		CloseableHttpResponse retrievalResponse = getElementWithNoAuthorization(TestAasEnvironmentHTTP.createSerializationURL(includeConceptDescription), new BasicHeader("Accept", TestAasEnvironmentHTTP.XML_MIMETYPE));
		assertEquals(HttpStatus.UNAUTHORIZED.value(), retrievalResponse.getCode());
	}

	@Test
	public void createSerializationWithCorrectRoleAndSpecificTargetPermission() throws IOException {
		DummyCredential dummyCredential = DummyCredentialStore.BASYX_READER_SERIALIZATION_CREDENTIAL;

		boolean includeConceptDescription = true;

		String accessToken = tokenProvider.getAccessToken(dummyCredential.getUsername(), dummyCredential.getPassword());

		CloseableHttpResponse retrievalResponse = getElementWithAuthorization(TestAasEnvironmentHTTP.createSerializationURL(includeConceptDescription), accessToken, new BasicHeader("Accept", TestAasEnvironmentHTTP.AASX_MIMETYPE));
		assertEquals(HttpStatus.OK.value(), retrievalResponse.getCode());
	}

	@Test
	public void createSerializationWithCorrectRoleAndInsufficientTargetPermission() throws IOException {
		DummyCredential dummyCredential = DummyCredentialStore.BASYX_READER_SERIALIZATION_CREDENTIAL_TWO;

		boolean includeConceptDescription = true;

		String accessToken = tokenProvider.getAccessToken(dummyCredential.getUsername(), dummyCredential.getPassword());

		CloseableHttpResponse retrievalResponse = getElementWithAuthorization(TestAasEnvironmentHTTP.createSerializationURL(includeConceptDescription), accessToken, new BasicHeader("Accept", TestAasEnvironmentHTTP.JSON_MIMETYPE));
		assertEquals(HttpStatus.FORBIDDEN.value(), retrievalResponse.getCode());
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

	protected CloseableHttpResponse getElementWithAuthorization(String url, String accessToken, Header header) throws IOException {
		return BaSyxHttpTestUtils.executeAuthorizedGetOnURL(url, accessToken, header);
	}

	protected CloseableHttpResponse getElementWithNoAuthorization(String url, Header header) throws IOException {
		return BaSyxHttpTestUtils.executeGetOnURL(url, header);
	}

	private static String getAdminAccessToken() {
		DummyCredential dummyCredential = DummyCredentialStore.ADMIN_CREDENTIAL;

		return tokenProvider.getAccessToken(dummyCredential.getUsername(), dummyCredential.getPassword());
	}

	private static void createDummyConceptDescriptionsOnRepository(Collection<ConceptDescription> conceptDescriptions, ConceptDescriptionRepository conceptDescriptionRepository) {
		conceptDescriptions.stream().forEach(conceptDescriptionRepository::createConceptDescription);
	}

	private static void createDummySubmodelsOnRepository(Collection<Submodel> submodels, SubmodelRepository submodelRepository) {
		submodels.stream().forEach(submodelRepository::createSubmodel);
	}

	private static void createDummyShellsOnRepository(Collection<AssetAdministrationShell> shells, AasRepository aasRepository) {
		shells.stream().forEach(aasRepository::createAas);
	}

	private static String getStringFromFile(String fileName) throws FileNotFoundException, IOException {
		return BaSyxHttpTestUtils.readJSONStringFromClasspath(fileName);
	}

}

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

package org.eclipse.digitaltwin.basyx.aasservice.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.interfaces.RSAPublicKey;

import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.basyx.aasrepository.AasRepository;
import org.eclipse.digitaltwin.basyx.aasrepository.DummyAasFactory;
import org.eclipse.digitaltwin.basyx.aasrepository.feature.authorization.DummyAuthorizedAasRepositoryComponent;
import org.eclipse.digitaltwin.basyx.aasservice.AasService;
import org.eclipse.digitaltwin.basyx.aasservice.AasServiceSuite;
import org.eclipse.digitaltwin.basyx.aasservice.DummyAssetAdministrationShellFactory;
import org.eclipse.digitaltwin.basyx.authorization.AccessTokenProvider;
import org.eclipse.digitaltwin.basyx.authorization.DummyCredential;
import org.eclipse.digitaltwin.basyx.authorization.DummyCredentialStore;
import org.eclipse.digitaltwin.basyx.authorization.jwt.JwtTokenDecoder;
import org.eclipse.digitaltwin.basyx.authorization.jwt.PublicKeyUtils;
import org.eclipse.digitaltwin.basyx.client.internal.ApiException;
import org.eclipse.digitaltwin.basyx.client.internal.authorization.TokenManager;
import org.eclipse.digitaltwin.basyx.client.internal.authorization.credential.ClientCredential;
import org.eclipse.digitaltwin.basyx.client.internal.authorization.grant.ClientCredentialAccessTokenProvider;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.eclipse.digitaltwin.basyx.http.Base64UrlEncodedIdentifier;
import org.eclipse.digitaltwin.basyx.http.serialization.BaSyxHttpTestUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

/**
 * Tests for authorized ConnectedAasService
 * 
 * @author danish
 */
public class TestAuthorizedConnectedAasService extends AasServiceSuite {

	private static final String AAS_REPO_URL = "http://localhost:8081/shells/";
	private static ConfigurableApplicationContext appContext;
	public static String authenticaltionServerTokenEndpoint = "http://localhost:9096/realms/BaSyx/protocol/openid-connect/token";
	public static String clientId = "basyx-client-api";

	@BeforeClass
	public static void startAASRepo() throws Exception {
		appContext = new SpringApplicationBuilder(DummyAuthorizedAasRepositoryComponent.class).profiles("authorization").run(new String[] {});
	}

	@After
	public void removeAasFromRepo() {
		AasRepository repo = appContext.getBean(AasRepository.class);
		repo.getAllAas(PaginationInfo.NO_LIMIT).getResult().stream().map(s -> s.getId()).forEach(repo::deleteAas);
	}

	@AfterClass
	public static void shutdownSubmodelService() {
		appContext.close();
	}
	
	@Test
	public void sendUnauthorizedRequest() throws IOException {
		TokenManager mockTokenManager = Mockito.mock(TokenManager.class);

		Mockito.when(mockTokenManager.getAccessToken()).thenReturn("mockedAccessToken");
		
		createDummyShellOnRepo("dummyAasId");

		AasService aasService = new AuthorizedConnectedAasService(AAS_REPO_URL + "dummyAasId", mockTokenManager);

		ApiException exception = assertThrows(ApiException.class, () -> {
			aasService.getAAS();
		});

		assertEquals(HttpStatus.UNAUTHORIZED.value(), exception.getCode());
	}

	private void createDummyShellOnRepo(String shellId) throws FileNotFoundException, IOException {
		configureSecurityContext(getTokenProvider());
		
		AasRepository repo = appContext.getBean(AasRepository.class);
		
		AssetAdministrationShell expected1 = DummyAasFactory.createAasWithSubmodelReference();
		expected1.setId(shellId);
		
		repo.createAas(expected1);
	}

	@Override
	protected AasService getAasService(AssetAdministrationShell shell) {
		AasRepository repo = appContext.getBean(AasRepository.class);
		repo.createAas(shell);
		String base64UrlEncodedId = Base64UrlEncodedIdentifier.encodeIdentifier(shell.getId());
		return new AuthorizedConnectedAasService(AAS_REPO_URL + base64UrlEncodedId, new TokenManager("http://localhost:9096/realms/BaSyx/protocol/openid-connect/token", new ClientCredentialAccessTokenProvider(new ClientCredential("workstation-1", "nY0mjyECF60DGzNmQUjL81XurSl8etom"))));
	}

	@Override
	protected AasService getAasServiceWithThumbnail() throws IOException {
		AssetAdministrationShell expected = DummyAssetAdministrationShellFactory.createForThumbnail();
		AasService aasServiceWithThumbnail = getAasService(expected);
	
		aasServiceWithThumbnail.setThumbnail("dummyImgA.jpeg", "", createDummyImageIS_A());
		
		return aasServiceWithThumbnail;
	}
	
	public static void configureSecurityContext(AccessTokenProvider accessTokenProvider) throws FileNotFoundException, IOException {
		String adminToken = getAdminAccessToken(accessTokenProvider);

		String modulus = BaSyxHttpTestUtils.readJSONStringFromClasspath("authorization/modulus.txt");
		
		String exponent = "AQAB";

		RSAPublicKey rsaPublicKey = PublicKeyUtils.buildPublicKey(modulus, exponent);

		Jwt jwt = JwtTokenDecoder.decodeJwt(adminToken, rsaPublicKey);

		SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(jwt));
	}
	
	public static String getAdminAccessToken(AccessTokenProvider tokenProvider) {
		DummyCredential dummyCredential = DummyCredentialStore.ADMIN_CREDENTIAL;

		return tokenProvider.getAccessToken(dummyCredential.getUsername(), dummyCredential.getPassword());
	}
	
	public static AccessTokenProvider getTokenProvider() {
		return new AccessTokenProvider(authenticaltionServerTokenEndpoint, clientId);
	}
}

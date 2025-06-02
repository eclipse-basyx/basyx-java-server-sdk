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

package org.eclipse.digitaltwin.basyx.aasrepository.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.basyx.aasrepository.AasRepository;
import org.eclipse.digitaltwin.basyx.aasrepository.AasRepositorySuite;
import org.eclipse.digitaltwin.basyx.aasrepository.DummyAasFactory;
import org.eclipse.digitaltwin.basyx.aasrepository.feature.authorization.DummyAuthorizedAasRepositoryComponent;
import org.eclipse.digitaltwin.basyx.aasservice.AasService;
import org.eclipse.digitaltwin.basyx.aasservice.DummyAssetAdministrationShellFactory;
import org.eclipse.digitaltwin.basyx.aasservice.client.TestAuthorizedConnectedAasService;
import org.eclipse.digitaltwin.basyx.client.internal.ApiException;
import org.eclipse.digitaltwin.basyx.client.internal.authorization.TokenManager;
import org.eclipse.digitaltwin.basyx.client.internal.authorization.credential.ClientCredential;
import org.eclipse.digitaltwin.basyx.client.internal.authorization.grant.ClientCredentialAccessTokenProvider;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Tests for {@link AuthorizedConnectedAasRepository}
 * 
 * @author danish
 */
public class TestAuthorizedConnectedAasRepository extends AasRepositorySuite {

	private static ConfigurableApplicationContext appContext;
	private static final String PROFILE = "authorization";

	@BeforeClass
	public static void startAASRepo() throws Exception {
		SpringApplication application = new SpringApplication(DummyAuthorizedAasRepositoryComponent.class);
		application.setAdditionalProfiles(PROFILE);
		
		appContext = application.run(new String[] {});
	}

	@After
	public void removeAasFromRepo() throws FileNotFoundException, IOException {
		TestAuthorizedConnectedAasService.configureSecurityContext(TestAuthorizedConnectedAasService.getTokenProvider());
		
		AasRepository repo = appContext.getBean(AasRepository.class);
		repo.getAllAas(null, null, PaginationInfo.NO_LIMIT).getResult().stream().map(s -> s.getId()).forEach(repo::deleteAas);
		
		SecurityContextHolder.clearContext();
	}

	@AfterClass
	public static void shutdownAASRepo() {
		appContext.close();
	}
	
	@Test
	public void sendUnauthorizedRequest() throws IOException {
		TokenManager mockTokenManager = Mockito.mock(TokenManager.class);

		Mockito.when(mockTokenManager.getAccessToken()).thenReturn("mockedAccessToken");

		AasRepository aasRepository = new AuthorizedConnectedAasRepository("http://localhost:8081", mockTokenManager);

		AssetAdministrationShell expected = DummyAasFactory.createAasWithSubmodelReference();

		ApiException exception = assertThrows(ApiException.class, () -> {
			aasRepository.createAas(expected);
		});

		assertEquals(HttpStatus.UNAUTHORIZED.value(), exception.getCode());
	}

	@Override
	protected AasRepository getAasRepository() {
		return new AuthorizedConnectedAasRepository("http://localhost:8081", new TokenManager("http://localhost:9096/realms/BaSyx/protocol/openid-connect/token", new ClientCredentialAccessTokenProvider(new ClientCredential("workstation-1", "nY0mjyECF60DGzNmQUjL81XurSl8etom"))));
	}
	
	@Override
	protected AasService getAasServiceWithThumbnail() throws IOException {
		AssetAdministrationShell expected = DummyAssetAdministrationShellFactory.createForThumbnail();
		AasService aasServiceWithThumbnail = getAasService(expected);
	
		aasServiceWithThumbnail.setThumbnail("dummyImgA.jpeg", "", createDummyImageIS_A());
		
		return aasServiceWithThumbnail;
	}
}

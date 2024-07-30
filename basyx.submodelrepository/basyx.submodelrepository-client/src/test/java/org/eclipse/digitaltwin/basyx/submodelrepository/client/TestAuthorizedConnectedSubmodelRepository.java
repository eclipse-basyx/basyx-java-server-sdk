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

package org.eclipse.digitaltwin.basyx.submodelrepository.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;

import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.basyx.client.internal.ApiException;
import org.eclipse.digitaltwin.basyx.client.internal.authorization.TokenManager;
import org.eclipse.digitaltwin.basyx.client.internal.authorization.credential.ClientCredential;
import org.eclipse.digitaltwin.basyx.client.internal.authorization.grant.ClientCredentialAccessTokenProvider;
import org.eclipse.digitaltwin.basyx.core.exceptions.MissingIdentifierException;
import org.eclipse.digitaltwin.basyx.core.exceptions.NotInvokableException;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.eclipse.digitaltwin.basyx.submodelrepository.SubmodelRepository;
import org.eclipse.digitaltwin.basyx.submodelrepository.core.SubmodelRepositorySuite;
import org.eclipse.digitaltwin.basyx.submodelrepository.feature.authorization.DummyAuthorizedSubmodelRepositoryComponent;
import org.eclipse.digitaltwin.basyx.submodelservice.DummySubmodelFactory;
import org.eclipse.digitaltwin.basyx.submodelservice.client.TestAuthorizedConnectedSubmodelService;
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
 * Tests for {@link AuthorizedConnectedSubmodelRepository}
 * 
 * @author danish
 */
public class TestAuthorizedConnectedSubmodelRepository extends SubmodelRepositorySuite {

	private static final String SUBMODEL_REPO_URL = "http://localhost:8081";
	private static ConfigurableApplicationContext appContext;
	private static final String PROFILE = "authorization";

	@BeforeClass
	public static void startAASRepo() throws Exception {
		SpringApplication application = new SpringApplication(DummyAuthorizedSubmodelRepositoryComponent.class);
		application.setAdditionalProfiles(PROFILE);
		
		appContext = application.run(new String[] {});
	}

	@After
	public void removeSubmodelFromRepo() throws FileNotFoundException, IOException {
		TestAuthorizedConnectedSubmodelService.configureSecurityContext(TestAuthorizedConnectedSubmodelService.getTokenProvider());
		
		SubmodelRepository repo = appContext.getBean(SubmodelRepository.class);
		try {
			repo.getAllSubmodels(PaginationInfo.NO_LIMIT).getResult().stream().map(s -> s.getId()).forEach(repo::deleteSubmodel);
		} catch (Exception e) {
		}
		
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

		SubmodelRepository submodelRepository = new AuthorizedConnectedSubmodelRepository(SUBMODEL_REPO_URL, mockTokenManager);

		Submodel expected = DummySubmodelFactory.createSimpleDataSubmodel();

		ApiException exception = assertThrows(ApiException.class, () -> {
			submodelRepository.createSubmodel(expected);
		});

		assertEquals(HttpStatus.UNAUTHORIZED.value(), exception.getCode());
	}

	@Override
	protected ConnectedSubmodelRepository getSubmodelRepository() {
		return new AuthorizedConnectedSubmodelRepository(SUBMODEL_REPO_URL, new TokenManager("http://localhost:9096/realms/BaSyx/protocol/openid-connect/token", new ClientCredentialAccessTokenProvider(new ClientCredential("workstation-1", "nY0mjyECF60DGzNmQUjL81XurSl8etom"))));
	}
	
	@Override
	protected ConnectedSubmodelRepository getSubmodelRepository(Collection<Submodel> submodels) {
		try {
			TestAuthorizedConnectedSubmodelService.configureSecurityContext(TestAuthorizedConnectedSubmodelService.getTokenProvider());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		SubmodelRepository repo = appContext.getBean(SubmodelRepository.class);
		submodels.forEach(repo::createSubmodel);
		
		SecurityContextHolder.clearContext();
		
		return getSubmodelRepository();
	}
	
	@Override
	@Test(expected = MissingIdentifierException.class)
	public void updateExistingSubmodelWithMismatchId() {
		// TODO There should be a way to differentiate between both exceptions through
		// the Http response
		super.updateExistingSubmodelWithMismatchId();
	}

	@Override
	protected boolean fileExistsInStorage(String fileValue) {
		java.io.File file = new java.io.File(fileValue);

		return file.exists();
	}

	@Override
	public void invokeOperation() {
		// TODO
	}

	@Override
	public void invokeNonOperation() {
		// TODO
		throw new NotInvokableException();
	}
}

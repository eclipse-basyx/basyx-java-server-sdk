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

package org.eclipse.digitaltwin.basyx.aasregistry.service.storage.mongodb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import java.io.IOException;

import org.eclipse.digitaltwin.basyx.aasregistry.client.ApiException;
import org.eclipse.digitaltwin.basyx.aasregistry.client.api.RegistryAndDiscoveryInterfaceApi;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.AssetAdministrationShellDescriptor;
import org.eclipse.digitaltwin.basyx.aasregistry.main.client.AuthorizedConnectedAasRegistry;
import org.eclipse.digitaltwin.basyx.aasregistry.service.events.RegistryEvent;
import org.eclipse.digitaltwin.basyx.aasregistry.service.tests.integration.BaseIntegrationTest;
import org.eclipse.digitaltwin.basyx.client.internal.authorization.TokenManager;
import org.eclipse.digitaltwin.basyx.client.internal.authorization.credential.ClientCredential;
import org.eclipse.digitaltwin.basyx.client.internal.authorization.grant.ClientCredentialAccessTokenProvider;
import org.eclipse.digitaltwin.basyx.kafka.KafkaAdapter;
import org.eclipse.digitaltwin.basyx.kafka.KafkaAdapters;
import org.junit.After;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestPropertySource;

/**
 * Tests the Authorized client
 * 
 * @author danish
 */
@TestPropertySource(properties = {"spring.profiles.active=kafkaEvents,mongoDbStorage", "spring.kafka.bootstrap-servers=localhost:9092", "spring.security.oauth2.resourceserver.jwt.issuer-uri=http://localhost:9096/realms/BaSyx", "basyx.feature.authorization.enabled=true", "basyx.feature.authorization.type=rbac", "basyx.feature.authorization.jwtBearerTokenProvider=keycloak", "basyx.feature.authorization.rbac.file=classpath:rbac_rules.json", "spring.data.mongodb.database=aasregistry", "spring.data.mongodb.uri=mongodb://mongoAdmin:mongoPassword@localhost:27017/"})
public class AuthorizedClientTest extends BaseIntegrationTest {
	
	@Value("${local.server.port}")
	private int port;
		
	@Override
	public void initClient() throws Exception {
		api = new AuthorizedConnectedAasRegistry("http://127.0.0.1:" + port, new TokenManager("http://localhost:9096/realms/BaSyx/protocol/openid-connect/token", new ClientCredentialAccessTokenProvider(new ClientCredential("workstation-1", "nY0mjyECF60DGzNmQUjL81XurSl8etom"))));

		api.deleteAllShellDescriptors();
	}
	
	@Test
	public void sendUnauthorizedRequest() throws IOException {
		TokenManager mockTokenManager = Mockito.mock(TokenManager.class);

		Mockito.when(mockTokenManager.getAccessToken()).thenReturn("mockedAccessToken");

		RegistryAndDiscoveryInterfaceApi registryApi = new AuthorizedConnectedAasRegistry("http://127.0.0.1:" + port, mockTokenManager);

		AssetAdministrationShellDescriptor descriptor = new AssetAdministrationShellDescriptor();
		descriptor.setIdShort("shortId");

		ApiException exception = assertThrows(ApiException.class, () -> {
			registryApi.postAssetAdministrationShellDescriptor(descriptor);
		});

		assertEquals(HttpStatus.UNAUTHORIZED.value(), exception.getCode());
	}
	
	@Test
	@Override
	public void whenPostSubmodelDescriptor_LocationIsReturned() throws ApiException, IOException {
		// TODO: It uses normal GET unauthorized request, need to override and refactor
	}
	
	@Test
	@Override
	public void whenPostShellDescriptor_LocationIsReturned() throws ApiException, IOException {
		// TODO: It uses normal GET unauthorized request, need to override and refactor
	}

}
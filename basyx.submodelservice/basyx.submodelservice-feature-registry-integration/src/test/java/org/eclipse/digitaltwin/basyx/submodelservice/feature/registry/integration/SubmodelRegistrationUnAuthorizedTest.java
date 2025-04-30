/*******************************************************************************
 * Copyright (C) 2025 DFKI GmbH (https://www.dfki.de/en/web)
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
 * 
 ******************************************************************************/
package org.eclipse.digitaltwin.basyx.submodelservice.feature.registry.integration;

import java.util.List;

import org.eclipse.digitaltwin.basyx.client.internal.authorization.AccessTokenProviderFactory;
import org.eclipse.digitaltwin.basyx.client.internal.authorization.TokenManager;
import org.eclipse.digitaltwin.basyx.client.internal.authorization.grant.AccessTokenProvider;
import org.eclipse.digitaltwin.basyx.client.internal.authorization.grant.GrantType;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.ApiException;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.AuthorizedConnectedSubmodelRegistry;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.api.SubmodelRegistryApi;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.annotation.DirtiesContext;
/**
 * @author Gerhard Sonnenberg (DFKI GmbH)
 */
@SpringBootTest(webEnvironment = WebEnvironment.NONE, classes = SubmodelServiceTestConfiguration.class, properties = { "basyx.submodelservice.feature.registryintegration.authorization.enabled=true",
		"basyx.submodelservice.feature.registryintegration.authorization.token-endpoint=http://localhost:9097/realms/BaSyx/protocol/openid-connect/token",
		"basyx.submodelservice.feature.registryintegration.authorization.grant-type=CLIENT_CREDENTIALS", "basyx.submodelservice.feature.registryintegration.authorization.client-id=workstation-1",
		"basyx.submodelservice.feature.registryintegration.authorization.client-secret=unknown", "basyx.submodelservice.feature.registryintegration=http://localhost:8061", "basyx.externalurl=http://localhost:8765",
		"basyx.backend=InMemory" })
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class SubmodelRegistrationUnAuthorizedTest {

	@Value("${basyx.submodelservice.feature.registryintegration.authorization.token-endpoint}")
	private String tokenEndpoint;

	@Value("${basyx.submodelservice.feature.registryintegration}")
	private String registryBasePath;

	@Value("${basyx.submodelservice.feature.registryintegration.authorization.client-id}")
	private String clientId;

	@Value("${basyx.submodelservice.feature.registryintegration.authorization.client-secret}")
	private String clientSecret;

	@Value("${basyx.submodelservice.feature.registryintegration.authorization.grant-type}")
	private String grantType;

	@Test
	public void testSubmodelCouldNotBeRegistered() throws ApiException {
		TokenManager tokenManager = new TokenManager(tokenEndpoint, createAccessTokenProvider());
		SubmodelRegistryApi api = new AuthorizedConnectedSubmodelRegistry(registryBasePath, tokenManager);

		try {
			api.getSubmodelDescriptorById(SubmodelServiceTestConfiguration.SM_ID);
			Assertions.fail();
		} catch (ApiException ex) {
			if (ex.getCode() != 404) {
				Assertions.fail();
			}
		}
	}

	private AccessTokenProvider createAccessTokenProvider() {
		AccessTokenProviderFactory factory = new AccessTokenProviderFactory(GrantType.valueOf(grantType), List.of());
		factory.setClientCredentials(clientId, "nY0mjyECF60DGzNmQUjL81XurSl8etom");
		factory.setPasswordCredentials("test", "test");
		return factory.create();
	}
}

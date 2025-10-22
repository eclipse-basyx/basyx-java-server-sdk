/*******************************************************************************
 * Copyright (C) 2025 the Eclipse BaSyx Authors
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

package org.eclipse.digitaltwin.basyx.aasrepository.feature.discoveryintegration;

import org.eclipse.digitaltwin.basyx.aasdiscoveryservice.client.AuthorizedConnectedAasDiscoveryService;
import org.eclipse.digitaltwin.basyx.aasdiscoveryservice.client.ConnectedAasDiscoveryService;
import org.eclipse.digitaltwin.basyx.aasdiscoveryservice.core.AasDiscoveryService;
import org.eclipse.digitaltwin.basyx.aasrepository.AasRepository;
import org.eclipse.digitaltwin.basyx.client.internal.authorization.AccessTokenProviderFactory;
import org.eclipse.digitaltwin.basyx.client.internal.authorization.TokenManager;
import org.eclipse.digitaltwin.basyx.client.internal.authorization.grant.AccessTokenProvider;
import org.eclipse.digitaltwin.basyx.client.internal.authorization.grant.GrantType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collection;

/**
 * Configuration for integrating {@link AasRepository} with Discovery
 * 
 * @author fried
 */
@Configuration
@ConditionalOnExpression("!T(org.springframework.util.StringUtils).isEmpty('${basyx.aasrepository.feature.discoveryintegration:}')")
public class DiscoveryIntegrationAasRepositoryConfiguration {

	@Value("${basyx.aasrepository.feature.discoveryintegration:#{null}}")
	private String discoveryBasePath;

	@Value("${basyx.aasrepository.feature.discoveryintegration.authorization.enabled:false}")
	private boolean isAuthorizationEnabledOnDiscovery;

	@Value("${basyx.aasrepository.feature.discoveryintegration.authorization.token-endpoint:#{null}}")
	private String authenticationServerTokenEndpoint;

	@Value("${basyx.aasrepository.feature.discoveryintegration.authorization.grant-type:#{null}}")
	private String grantType;

	@Value("${basyx.aasrepository.feature.discoveryintegration.authorization.client-id:#{null}}")
	private String clientId;

	@Value("${basyx.aasrepository.feature.discoveryintegration.authorization.client-secret:#{null}}")
	private String clientSecret;

	@Value("${basyx.aasrepository.feature.discoveryintegration.authorization.username:#{null}}")
	private String username;

	@Value("${basyx.aasrepository.feature.discoveryintegration.authorization.password:#{null}}")
	private String password;

	@Value("${basyx.aasrepository.feature.discoveryintegration.authorization.scopes:#{null}}")
	private Collection<String> scopes;

	@Bean
	public AasDiscoveryService getConnectedAasDiscoveryService() {
		if (!isAuthorizationEnabledOnDiscovery)
			return new ConnectedAasDiscoveryService(discoveryBasePath);

		TokenManager tokenManager = new TokenManager(authenticationServerTokenEndpoint, createAccessTokenProvider());

		return new AuthorizedConnectedAasDiscoveryService(discoveryBasePath, tokenManager);
	}

	private AccessTokenProvider createAccessTokenProvider() {

		AccessTokenProviderFactory factory = new AccessTokenProviderFactory(GrantType.valueOf(grantType), scopes);
		factory.setClientCredentials(clientId, clientSecret);
		factory.setPasswordCredentials(username, password);

		return factory.create();
	}

}

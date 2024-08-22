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

package org.eclipse.digitaltwin.basyx.aasrepository.feature.registry.integration;

import java.util.Collection;
import java.util.List;

import org.eclipse.digitaltwin.basyx.aasregistry.client.api.RegistryAndDiscoveryInterfaceApi;
import org.eclipse.digitaltwin.basyx.aasregistry.main.client.AuthorizedConnectedAasRegistry;
import org.eclipse.digitaltwin.basyx.aasregistry.main.client.mapper.AttributeMapper;
import org.eclipse.digitaltwin.basyx.aasrepository.AasRepository;
import org.eclipse.digitaltwin.basyx.client.internal.authorization.AccessTokenProviderFactory;
import org.eclipse.digitaltwin.basyx.client.internal.authorization.TokenManager;
import org.eclipse.digitaltwin.basyx.client.internal.authorization.grant.AccessTokenProvider;
import org.eclipse.digitaltwin.basyx.client.internal.authorization.grant.GrantType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Configuration for integrating {@link AasRepository} with AasRegistry
 * 
 * @author danish
 */
@Configuration
@ConditionalOnExpression("!T(org.springframework.util.StringUtils).isEmpty('${basyx.aasrepository.feature.registryintegration:}') && !T(org.springframework.util.StringUtils).isEmpty('${basyx.externalurl:}')")
public class RegistryIntegrationAasRepositoryConfiguration {

	@Value("${basyx.aasrepository.feature.registryintegration:#{null}}")
	private String registryBasePath;

	@Value("#{'${basyx.externalurl}'.split(',')}")
	private List<String> aasRepositoryBaseURLs;

	@Value("${basyx.aasrepository.feature.registryintegration.authorization.enabled:false}")
	private boolean isAuthorizationEnabledOnRegistry;

	@Value("${basyx.aasrepository.feature.registryintegration.authorization.token-endpoint:#{null}}")
	private String authenticationServerTokenEndpoint;

	@Value("${basyx.aasrepository.feature.registryintegration.authorization.grant-type:#{null}}")
	private String grantType;

	@Value("${basyx.aasrepository.feature.registryintegration.authorization.client-id:#{null}}")
	private String clientId;

	@Value("${basyx.aasrepository.feature.registryintegration.authorization.client-secret:#{null}}")
	private String clientSecret;

	@Value("${basyx.aasrepository.feature.registryintegration.authorization.username:#{null}}")
	private String username;

	@Value("${basyx.aasrepository.feature.registryintegration.authorization.password:#{null}}")
	private String password;

	@Value("${basyx.aasrepository.feature.registryintegration.authorization.scopes:#{null}}")
	private Collection<String> scopes;

	@Bean
	@ConditionalOnMissingBean
	public AasRepositoryRegistryLink getAasRepositoryRegistryLink() {
		
		if (!isAuthorizationEnabledOnRegistry)
			return new AasRepositoryRegistryLink(new RegistryAndDiscoveryInterfaceApi(registryBasePath), aasRepositoryBaseURLs);

		TokenManager tokenManager = new TokenManager(authenticationServerTokenEndpoint, createAccessTokenProvider());

		return new AasRepositoryRegistryLink(new AuthorizedConnectedAasRegistry(registryBasePath, tokenManager), aasRepositoryBaseURLs);
	}

	@Bean
	@ConditionalOnMissingBean
	public AttributeMapper getAasAttributeMapper(ObjectMapper objectMapper) {

		return new AttributeMapper(objectMapper);
	}

	private AccessTokenProvider createAccessTokenProvider() {

		AccessTokenProviderFactory factory = new AccessTokenProviderFactory(GrantType.valueOf(grantType), scopes);
		factory.setClientCredentials(clientId, clientSecret);
		factory.setPasswordCredentials(username, password);

		return factory.create();
	}

}

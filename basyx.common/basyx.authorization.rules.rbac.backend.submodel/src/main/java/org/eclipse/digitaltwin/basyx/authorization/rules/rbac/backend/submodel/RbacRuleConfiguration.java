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

package org.eclipse.digitaltwin.basyx.authorization.rules.rbac.backend.submodel;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.eclipse.digitaltwin.basyx.authorization.CommonAuthorizationProperties;
import org.eclipse.digitaltwin.basyx.authorization.rbac.RbacRule;
import org.eclipse.digitaltwin.basyx.authorization.rbac.RbacRuleInitializer;
import org.eclipse.digitaltwin.basyx.authorization.rbac.RbacStorage;
import org.eclipse.digitaltwin.basyx.client.internal.authorization.AccessTokenProviderFactory;
import org.eclipse.digitaltwin.basyx.client.internal.authorization.TokenManager;
import org.eclipse.digitaltwin.basyx.client.internal.authorization.grant.AccessTokenProvider;
import org.eclipse.digitaltwin.basyx.client.internal.authorization.grant.GrantType;
import org.eclipse.digitaltwin.basyx.submodelservice.client.AuthorizedConnectedSubmodelService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 * Configurations for {@link RbacRule}
 * 
 * @author danish
 */
@Configuration
@ConditionalOnProperty("basyx.feature.authorization.enabled")
@ConditionalOnExpression(value = "'${basyx.feature.authorization.type}' == 'rbac' && '${basyx.feature.authorization.rules.backend}' == 'Submodel'")
public class RbacRuleConfiguration {
	public static final String RULES_FILE_KEY = "basyx.aasrepository.feature.authorization.rbac.file";
	
	@Value("${" + CommonAuthorizationProperties.RBAC_FILE_PROPERTY_KEY + ":}")
	private String filePath;
	
	@Value("${" + CommonAuthorizationProperties.RULES_BACKEND_TYPE_SUBMODEL_AUTHORIZATION_SUBMODEL_ENDPOINT + ":}")
	private String submodelEndpoint;
	
	@Value("${" + CommonAuthorizationProperties.RULES_BACKEND_TYPE_SUBMODEL_AUTHORIZATION_TOKEN_ENDPOINT + ":}")
	private String tokenEndpoint;
	
	@Value("${" + CommonAuthorizationProperties.RULES_BACKEND_TYPE_SUBMODEL_AUTHORIZATION_GRANT_TYPE + ":}")
	private String grantType;
	
	@Value("${" + CommonAuthorizationProperties.RULES_BACKEND_TYPE_SUBMODEL_AUTHORIZATION_CLIENT_ID + ":}")
	private String clientId;
	
	@Value("${" + CommonAuthorizationProperties.RULES_BACKEND_TYPE_SUBMODEL_AUTHORIZATION_CLIENT_SECRET + ":}")
	private String clientSecret;
	
	@Value("${" + CommonAuthorizationProperties.RULES_BACKEND_TYPE_SUBMODEL_AUTHORIZATION_USERNAME + ":}")
	private String username;
	
	@Value("${" + CommonAuthorizationProperties.RULES_BACKEND_TYPE_SUBMODEL_AUTHORIZATION_PASSWORD + ":}")
	private String password;
	
	@Value("${" + CommonAuthorizationProperties.RULES_BACKEND_TYPE_SUBMODEL_AUTHORIZATION_SCOPES + ":}")
	private List<String> scopes;
	
	private ObjectMapper objectMapper;
	private ResourceLoader resourceLoader;
	private TargetInformationAdapter targetInformationAdapter;

	public RbacRuleConfiguration(ObjectMapper objectMapper, ResourceLoader resourceLoader, TargetInformationAdapter targetInformationAdapter) {
		this.objectMapper = objectMapper;
		this.resourceLoader = resourceLoader;
		this.targetInformationAdapter = targetInformationAdapter;
	}

	@Bean
	public RbacStorage createInMemoryRbacStorage() throws IOException {
		
		TokenManager tokenManager = new TokenManager(tokenEndpoint, getTokenProvider());
		
		if (filePath.isBlank())
			return new SubmodelAuthorizationRbacStorage(new AuthorizedConnectedSubmodelService(submodelEndpoint, tokenManager), new HashMap<>(), new RbacRuleAdapter(targetInformationAdapter));
		
		HashMap<String, RbacRule> initialRules = new RbacRuleInitializer(objectMapper, filePath, resourceLoader).deserialize();
		
		return new SubmodelAuthorizationRbacStorage(new AuthorizedConnectedSubmodelService(submodelEndpoint, tokenManager), initialRules, new RbacRuleAdapter(targetInformationAdapter));
	}
	
	private AccessTokenProvider getTokenProvider() {
        AccessTokenProviderFactory factory = new AccessTokenProviderFactory(GrantType.valueOf(grantType), scopes);
        factory.setClientCredentials(clientId, clientSecret);
        factory.setPasswordCredentials(username, password);
        return factory.create();
    }

}

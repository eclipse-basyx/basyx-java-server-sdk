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

package org.eclipse.digitaltwin.basyx.submodelservice.feature.authorization;

import org.eclipse.digitaltwin.basyx.authorization.CommonAuthorizationProperties;
import org.eclipse.digitaltwin.basyx.authorization.rbac.RbacPermissionResolver;
import org.eclipse.digitaltwin.basyx.authorization.rbac.RbacStorage;
import org.eclipse.digitaltwin.basyx.authorization.rbac.SimpleRbacPermissionResolver;
import org.eclipse.digitaltwin.basyx.authorization.rbac.RoleProvider;
import org.eclipse.digitaltwin.basyx.authorization.rbac.TargetPermissionVerifier;
import org.eclipse.digitaltwin.basyx.authorization.rules.rbac.backend.submodel.TargetInformationAdapter;
import org.eclipse.digitaltwin.basyx.submodelservice.feature.authorization.rbac.SubmodelTargetPermissionVerifier;
import org.eclipse.digitaltwin.basyx.submodelservice.feature.authorization.rbac.backend.submodel.SubmodelTargetInformationAdapter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;

/**
 * Configuration for AuthorizedSubmodelService
 * 
 * @author Gerhard Sonnenberg ( DFKI GmbH )
 */
@Configuration
@ConditionalOnExpression("#{${" + CommonAuthorizationProperties.ENABLED_PROPERTY_KEY + ":false}}")
public class AuthorizedSubmodelServiceConfiguration {

	@Bean
	public TargetPermissionVerifier<SubmodelTargetInformation> getSubmodelTargetPermissionVerifier() {
		return new SubmodelTargetPermissionVerifier();
	}

	@Bean
	public RbacPermissionResolver<SubmodelTargetInformation> getSubmodelPermissionResolver(RbacStorage rbacStorage, RoleProvider roleProvider, TargetPermissionVerifier<SubmodelTargetInformation> targetPermissionVerifier) {
		return new SimpleRbacPermissionResolver<>(rbacStorage, roleProvider, targetPermissionVerifier);
	}
	
	@Bean
	public TargetInformationAdapter getSubmodelTargetInformationAdapter() {
		return new SubmodelTargetInformationAdapter();
	}
	
	@Bean
	@ConditionalOnProperty(name = "spring.security.oauth2.resourceserver.jwt.issuer-uri", matchIfMissing = false)
	public JwtDecoder jwtDecoder(@Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}") String issuerUri) {
		return JwtDecoders.fromIssuerLocation(issuerUri);
	}
}
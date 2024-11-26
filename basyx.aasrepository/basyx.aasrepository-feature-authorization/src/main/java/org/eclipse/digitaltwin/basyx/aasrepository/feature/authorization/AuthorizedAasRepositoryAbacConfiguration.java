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

package org.eclipse.digitaltwin.basyx.aasrepository.feature.authorization;

import org.eclipse.digitaltwin.basyx.aasrepository.feature.authorization.rbac.AasTargetPermissionVerifier;
import org.eclipse.digitaltwin.basyx.authorization.CommonAuthorizationProperties;
import org.eclipse.digitaltwin.basyx.authorization.SubjectInformationProvider;
import org.eclipse.digitaltwin.basyx.authorization.abac.AbacPermissionResolver;
import org.eclipse.digitaltwin.basyx.authorization.abac.AbacStorage;
import org.eclipse.digitaltwin.basyx.authorization.abac.SimpleAbacPermissionResolver;
import org.eclipse.digitaltwin.basyx.authorization.rbac.RoleProvider;
import org.eclipse.digitaltwin.basyx.authorization.rbac.TargetPermissionVerifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Configuration for authorized {@link AuthorizedAasRepository}
 * 
 * @author danish
 */
@Configuration
@ConditionalOnExpression(value = "${" + CommonAuthorizationProperties.ENABLED_PROPERTY_KEY + ":false} and ('${" + CommonAuthorizationProperties.TYPE_PROPERTY_KEY + "}'.equals('abac') or '${" + CommonAuthorizationProperties.TYPE_PROPERTY_KEY + "}'.isEmpty())")
public class AuthorizedAasRepositoryAbacConfiguration {
	
	@Bean
	public TargetPermissionVerifier<AasTargetInformation> getAasTargetPermissionVerifier() {
		return new AasTargetPermissionVerifier();
	}
	
	@Bean
	public AbacPermissionResolver getAasPermissionResolver(AbacStorage abacStorage, RoleProvider roleProvider, SubjectInformationProvider<Object> subjectInformationProvider) {

		return new SimpleAbacPermissionResolver(abacStorage, roleProvider, subjectInformationProvider);
	}
	

}

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
package org.eclipse.digitaltwin.basyx.submodelregistry.service.authorization.rbac;

import org.eclipse.digitaltwin.basyx.authorization.CommonAuthorizationConfig;
import org.eclipse.digitaltwin.basyx.authorization.rbac.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.function.Predicate;

@Configuration
@ConditionalOnProperty(CommonAuthorizationConfig.ENABLED_PROPERTY_KEY)
@ConditionalOnExpression(value = "'${" + CommonAuthorizationConfig.TYPE_PROPERTY_KEY +  "}' == '" + CommonRbacConfig.RBAC_AUTHORIZATION_TYPE + "'")
public class RbacConfig {
	public final static String RULES_FILE_KEY = "basyx.submodelregistry.authorization.rbac.file";

	private final ApplicationContext applicationContext;

	public RbacConfig(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	@Bean
	@ConditionalOnExpression(value = "'${basyx.backend}' == 'InMemory'")
	public IRbacStorage<Predicate<RbacRule>> createInMemoryRbacStorage() {
		return new InMemoryAuthorizationRbacStorage(RbacUtil.getRbacRuleSetFromFile(applicationContext.getEnvironment()));
	}

	@Bean
	@ConditionalOnExpression(value = "'${basyx.backend}' == 'MongoDB'")
	public IRbacStorage<Criteria> createMongoDBRbacStorage() {
		return new MongoDBAuthorizationRbacStorage(applicationContext.getBean(MongoTemplate.class), RbacUtil.getRbacRuleSetFromFile(applicationContext.getEnvironment()));
	}
}

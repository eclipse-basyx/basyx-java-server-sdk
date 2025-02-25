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

package org.eclipse.digitaltwin.basyx.aasrepository.feature.authorization.abac.backend.inmemory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.eclipse.digitaltwin.basyx.authorization.CommonAuthorizationProperties;
import org.eclipse.digitaltwin.basyx.authorization.abac.AbacRuleInitializer;
import org.eclipse.digitaltwin.basyx.authorization.abac.AbacStorage;
import org.eclipse.digitaltwin.basyx.authorization.rbac.RbacRule;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;

/**
 * Configurations for {@link RbacRule}
 * 
 * @author danish
 */
@Configuration
@ConditionalOnProperty("basyx.feature.authorization.enabled")
@ConditionalOnExpression(value = "'${basyx.feature.authorization.type}' == 'abac' && ('${basyx.feature.authorization.rules.backend:InMemory}' == 'InMemory' || '${basyx.feature.authorization.rules.backend}' == '')")
public class AbacRuleConfiguration {
	public static final String RULES_FILE_KEY = "basyx.aasrepository.feature.authorization.abac.file";
	
	@Value("${" + CommonAuthorizationProperties.ABAC_FILE_PROPERTY_KEY + ":}")
	private String filePath;
	
	private ObjectMapper objectMapper;
	
	private ResourceLoader resourceLoader;

	public AbacRuleConfiguration(ObjectMapper objectMapper, ResourceLoader resourceLoader) {
		this.objectMapper = objectMapper;
		this.resourceLoader = resourceLoader;
	}

	@Bean
	public AbacStorage createInMemoryRbacStorage() throws IOException {
		
		if (filePath.isBlank())
			return new InMemoryAbacStorage(null);
		
		return new InMemoryAbacStorage(new AbacRuleInitializer(objectMapper, filePath, resourceLoader).deserialize());
	}

}

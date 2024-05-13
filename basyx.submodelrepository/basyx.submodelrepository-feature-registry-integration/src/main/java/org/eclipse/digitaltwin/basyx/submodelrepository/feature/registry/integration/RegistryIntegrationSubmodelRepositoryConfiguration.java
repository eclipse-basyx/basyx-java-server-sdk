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

package org.eclipse.digitaltwin.basyx.submodelrepository.feature.registry.integration;

import java.util.List;

import org.eclipse.digitaltwin.basyx.submodelregistry.client.api.SubmodelRegistryApi;
import org.eclipse.digitaltwin.basyx.submodelrepository.SubmodelRepository;
import org.eclipse.digitaltwin.basyx.submodelrepository.feature.registry.integration.mapper.AttributeMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Configuration for integrating {@link SubmodelRepository} with SubmodelRegistry
 * 
 * @author danish
 */
@Configuration
@ConditionalOnExpression("!T(org.springframework.util.StringUtils).isEmpty('${basyx.submodelrepository.feature.registryintegration:}') && !T(org.springframework.util.StringUtils).isEmpty('${basyx.externalurl:}')")
public class RegistryIntegrationSubmodelRepositoryConfiguration {

	@Bean
	@ConditionalOnMissingBean
	public SubmodelRepositoryRegistryLink getSubmodelRepositoryRegistryLink(@Value("${basyx.submodelrepository.feature.registryintegration}") String registryBasePath, @Value("#{'${basyx.externalurl}'.split(',')}") List<String> submodelRepositoryBaseURLs) {
		return new SubmodelRepositoryRegistryLink(new SubmodelRegistryApi(registryBasePath), submodelRepositoryBaseURLs);
	}
	
	@Bean
	@ConditionalOnMissingBean
	public AttributeMapper getSubmodelAttributeMapper(ObjectMapper objectMapper) {
		
		return new AttributeMapper(objectMapper);
	}

}

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

import org.eclipse.digitaltwin.basyx.submodelregistry.client.mapper.AttributeMapper;
import org.eclipse.digitaltwin.basyx.submodelrepository.SubmodelRepository;
import org.eclipse.digitaltwin.basyx.submodelrepository.SubmodelRepositoryFactory;
import org.eclipse.digitaltwin.basyx.submodelrepository.feature.SubmodelRepositoryFeature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

/**
 * Feature for integrating Registry with {@link SubmodelRepository}
 * 
 * @author danish
 */
@Component
@ConditionalOnExpression("!T(org.springframework.util.StringUtils).isEmpty('${basyx.submodelrepository.feature.registryintegration:}') && !T(org.springframework.util.StringUtils).isEmpty('${basyx.externalurl:}')")
public class RegistryIntegrationSubmodelRepositoryFeature implements SubmodelRepositoryFeature {
	public static final String FEATURENAME = "basyx.submodelrepository.feature.registryintegration";

	private SubmodelRepositoryRegistryLink submodelRepositoryRegistryLink;
	
	@Value("${" + FEATURENAME + ":}")
	private String registryBaseURL;
	
	@Value("${basyx.externalurl:}")
	private String submodelRepositoryExternalBaseURL;
	
	private AttributeMapper attributeMapper;

	@Autowired
	public RegistryIntegrationSubmodelRepositoryFeature(SubmodelRepositoryRegistryLink submodelRepositoryRegistryLink, AttributeMapper attributeMapper) {
		this.submodelRepositoryRegistryLink = submodelRepositoryRegistryLink;
		this.attributeMapper = attributeMapper;
	}

	@Override
	public SubmodelRepositoryFactory decorate(SubmodelRepositoryFactory submodelRepositoryFactory) {
		return new RegistryIntegrationSubmodelRepositoryFactory(submodelRepositoryFactory, submodelRepositoryRegistryLink, attributeMapper);
	}

	@Override
	public void initialize() {
	}

	@Override
	public void cleanUp() {

	}

	@Override
	public String getName() {
		return "SubmodelRepository Registry Integration";
	}

	@Override
	public boolean isEnabled() {
		return !registryBaseURL.isBlank() && !submodelRepositoryExternalBaseURL.isBlank();
	}
}

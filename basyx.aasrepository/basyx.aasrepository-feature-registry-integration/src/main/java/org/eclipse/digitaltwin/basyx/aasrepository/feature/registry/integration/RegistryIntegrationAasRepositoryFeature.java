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

import org.eclipse.digitaltwin.basyx.aasregistry.main.client.mapper.AttributeMapper;
import org.eclipse.digitaltwin.basyx.aasrepository.AasRepository;
import org.eclipse.digitaltwin.basyx.aasrepository.AasRepositoryFactory;
import org.eclipse.digitaltwin.basyx.aasrepository.feature.AasRepositoryFeature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

/**
 * Feature for integrating Registry with {@link AasRepository}
 * 
 * @author danish
 */
@Component
@ConditionalOnExpression("!T(org.springframework.util.StringUtils).isEmpty('${basyx.aasrepository.feature.registryintegration:}') && !T(org.springframework.util.StringUtils).isEmpty('${basyx.externalurl:}')")
public class RegistryIntegrationAasRepositoryFeature implements AasRepositoryFeature {
	public final static String FEATURENAME = "basyx.aasrepository.feature.registryintegration";

	private AasRepositoryRegistryLink aasRepositoryRegistryLink;
	
	@Value("${" + FEATURENAME + ":}")
	private String registryBaseURL;
	
	@Value("${basyx.externalurl:}")
	private String aasRepositoryExternalBaseURL;
	
	private AttributeMapper attributeMapper;

	@Autowired
	public RegistryIntegrationAasRepositoryFeature(AasRepositoryRegistryLink aasRepositoryRegistryLink, AttributeMapper attributeMapper) {
		this.aasRepositoryRegistryLink = aasRepositoryRegistryLink;
		this.attributeMapper = attributeMapper;
	}

	@Override
	public AasRepositoryFactory decorate(AasRepositoryFactory aasRepositoryFactory) {
		return new RegistryIntegrationAasRepositoryFactory(aasRepositoryFactory, aasRepositoryRegistryLink, attributeMapper);
	}

	@Override
	public void initialize() {
	}

	@Override
	public void cleanUp() {

	}

	@Override
	public String getName() {
		return "AasRepository Registry Integration";
	}

	@Override
	public boolean isEnabled() {
		return !registryBaseURL.isBlank() && !aasRepositoryExternalBaseURL.isBlank();
	}
}

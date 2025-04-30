/*******************************************************************************
 * Copyright (C) 2025 DFKI GmbH (https://www.dfki.de/en/web)
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
 * 
 ******************************************************************************/
package org.eclipse.digitaltwin.basyx.submodelservice.feature.registry.integration;

import org.eclipse.digitaltwin.basyx.submodelservice.SubmodelService;
import org.eclipse.digitaltwin.basyx.submodelservice.SubmodelServiceFactory;
import org.eclipse.digitaltwin.basyx.submodelservice.feature.SubmodelServiceFeature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

/**
 * Feature for integrating Registry with {@link SubmodelService}
 * 
 * @author Gerhard Sonnenberg (DFKI GmbH)
 */
@Component
@ConditionalOnExpression("!T(org.springframework.util.StringUtils).isEmpty('${basyx.submodelservice.feature.registryintegration:}') && !T(org.springframework.util.StringUtils).isEmpty('${basyx.externalurl:}')")
public class RegistryIntegrationSubmodelServiceFeature implements SubmodelServiceFeature {

	public static final String FEATURENAME = "basyx.submodelservice.feature.registryintegration";

	@Value("${" + FEATURENAME + ":}")
	private String registryBaseURL;

	@Value("${basyx.externalurl:}")
	private String submodelServiceExternalBaseURL;
	
	@Autowired
	private SubmodelRegistration registration;

	@Override
	public SubmodelServiceFactory decorate(SubmodelServiceFactory submodelServiceFactory) {
		return new RegistryIntegrationSubmodelServiceFactory(submodelServiceFactory, registration);
	}

	@Override
	public void initialize() {

	}

	@Override
	public void cleanUp() {

	}

	@Override
	public String getName() {
		return "SubmodelService Registry Integration";
	}

	@Override
	public boolean isEnabled() {
		return !registryBaseURL.isBlank() && !submodelServiceExternalBaseURL.isBlank();
	}
}

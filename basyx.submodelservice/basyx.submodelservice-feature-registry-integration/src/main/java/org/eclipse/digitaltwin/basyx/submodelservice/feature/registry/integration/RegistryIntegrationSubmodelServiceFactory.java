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

import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.basyx.submodelservice.SubmodelService;
import org.eclipse.digitaltwin.basyx.submodelservice.SubmodelServiceFactory;

/**
 * Factory for to register a submodel descriptor
 * 
 * @author Gerhard Sonnenberg (DFKI GmbH)
 */
public class RegistryIntegrationSubmodelServiceFactory implements SubmodelServiceFactory {

	private final SubmodelServiceFactory decorated;
	private final SubmodelRegistration registration;

	public RegistryIntegrationSubmodelServiceFactory(SubmodelServiceFactory decorated, SubmodelRegistration registration) {
		this.decorated = decorated;
		this.registration = registration;
	}

	@Override
	public SubmodelService create(Submodel submodel) {
		SubmodelService service = decorated.create(submodel);
		registration.register(submodel);
		return service;
	}

	@Override
	public SubmodelService create(String submodelId) {
		// not relevant for submodel service, just for repositories
		return decorated.create(submodelId);
	}
}

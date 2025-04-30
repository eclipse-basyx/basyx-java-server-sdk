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

import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSubmodel;
import org.eclipse.digitaltwin.basyx.submodelservice.SubmodelService;
import org.eclipse.digitaltwin.basyx.submodelservice.SubmodelServiceFactory;
import org.eclipse.digitaltwin.basyx.submodelservice.feature.DecoratedSubmodelServiceFactory;
import org.eclipse.digitaltwin.basyx.submodelservice.feature.SubmodelServiceFeature;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
/**
 * @author Gerhard Sonnenberg (DFKI GmbH)
 */
@TestConfiguration
public class SubmodelServiceTestConfiguration {

	public static final String SM_ID = "http://aas.example.org/sm/test/submodelservice/1";
	
	@Bean
	@Primary
	public SubmodelServiceFactory getSubmodelServiceFactory(SubmodelServiceFactory smServiceFactory, List<SubmodelServiceFeature> features) {
		return new DecoratedSubmodelServiceFactory(smServiceFactory, features);
	}	
	
	@Bean
	@Primary
	public SubmodelService getTestSubmodelService(Submodel submodel, SubmodelServiceFactory submodelServiceFactory) {
		return submodelServiceFactory.create(submodel);
	}

	@Bean
	public static Submodel getSubmodel() {
		return new DefaultSubmodel.Builder().id(SM_ID).idShort("1").build();
	}
	
}

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

package org.eclipse.digitaltwin.basyx.submodelrepository.component;

import java.util.List;

import org.eclipse.digitaltwin.basyx.submodelrepository.SubmodelRepository;
import org.eclipse.digitaltwin.basyx.submodelrepository.SubmodelRepositoryFactory;
import org.eclipse.digitaltwin.basyx.submodelrepository.feature.DecoratedSubmodelRepositoryFactory;
import org.eclipse.digitaltwin.basyx.submodelrepository.feature.SubmodelRepositoryFeature;
import org.eclipse.digitaltwin.basyx.submodelservice.SubmodelService;
import org.eclipse.digitaltwin.basyx.submodelservice.SubmodelServiceFactory;
import org.eclipse.digitaltwin.basyx.submodelservice.feature.DecoratedSubmodelServiceFactory;
import org.eclipse.digitaltwin.basyx.submodelservice.feature.SubmodelServiceFeature;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Provides the spring bean configuration for the {@link SubmodelRepository} and
 * {@link SubmodelService} utilizing all found features for the respective
 * services
 * 
 * @author schnicke
 *
 */
@Configuration
public class SubmodelRepositoryConfiguration {
	@Bean
	@ConditionalOnMissingBean
	public SubmodelRepository getSubmodelRepository(SubmodelRepositoryFactory aasRepositoryFactory, List<SubmodelRepositoryFeature> features) {
		return new DecoratedSubmodelRepositoryFactory(aasRepositoryFactory, features).create();
	}

	@Primary
	@Bean
	public SubmodelServiceFactory getSubmodelServiceFactory(SubmodelServiceFactory aasServiceFactory, List<SubmodelServiceFeature> features) {
		return new DecoratedSubmodelServiceFactory(aasServiceFactory, features);
	}
}

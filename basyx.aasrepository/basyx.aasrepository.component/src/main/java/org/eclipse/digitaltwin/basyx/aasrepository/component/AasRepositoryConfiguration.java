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

package org.eclipse.digitaltwin.basyx.aasrepository.component;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.eclipse.digitaltwin.basyx.aasrepository.AasRepository;
import org.eclipse.digitaltwin.basyx.aasrepository.AasRepositoryFactory;
import org.eclipse.digitaltwin.basyx.aasrepository.feature.AasRepositoryFeature;
import org.eclipse.digitaltwin.basyx.aasrepository.feature.DecoratedAasRepositoryFactory;
import org.eclipse.digitaltwin.basyx.aasservice.AasService;
import org.eclipse.digitaltwin.basyx.aasservice.AasServiceFactory;
import org.eclipse.digitaltwin.basyx.aasservice.feature.AasServiceFeature;
import org.eclipse.digitaltwin.basyx.aasservice.feature.DecoratedAasServiceFactory;
import org.eclipse.digitaltwin.basyx.authorization.rbac.RbacRuleDeserializer;
import org.eclipse.digitaltwin.basyx.authorization.rbac.TargetInformationSubtype;
import org.reflections.Reflections;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

/**
 * Provides the spring bean configuration for the {@link AasRepository} and
 * {@link AasService} utilizing all found features for the respective services
 * 
 * @author schnicke
 *
 */
@Configuration
public class AasRepositoryConfiguration {
	@Bean
	@ConditionalOnMissingBean
	public static AasRepository getAasRepository(AasRepositoryFactory aasRepositoryFactory, List<AasRepositoryFeature> features) {
		return new DecoratedAasRepositoryFactory(aasRepositoryFactory, features).create();
	}

	@Primary
	@Bean
	public static AasServiceFactory getAasService(AasServiceFactory aasServiceFactory, List<AasServiceFeature> features) {
		return new DecoratedAasServiceFactory(aasServiceFactory, features);
	}
	
//	@Bean
//	public ObjectMapper getAasMapper(Jackson2ObjectMapperBuilder builder) {
//		ObjectMapper mapper = builder.build();
//
//		mapper.findAndRegisterModules(); // Ensure all modules are loaded
//		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//
//		return mapper;
//	}
}

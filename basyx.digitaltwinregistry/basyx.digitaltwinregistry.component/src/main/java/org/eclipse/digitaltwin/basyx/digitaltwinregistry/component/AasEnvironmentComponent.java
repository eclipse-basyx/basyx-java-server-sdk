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

package org.eclipse.digitaltwin.basyx.digitaltwinregistry.component;

import org.eclipse.digitaltwin.basyx.aasregistry.service.configuration.RestConfiguration;
import org.eclipse.digitaltwin.basyx.http.BaSyxHTTPConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.FullyQualifiedAnnotationBeanNameGenerator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Creates and starts the AasEnvironment off-shelf-component.
 * 
 * @author schnicke
 *
 */
@SpringBootApplication(nameGenerator = FullyQualifiedAnnotationBeanNameGenerator.class, scanBasePackages = { "org.eclipse.digitaltwin.basyx", "org.eclipse.digitaltwin.basyx.aasregistry.service.storage" }, exclude = {
		MongoAutoConfiguration.class, MongoDataAutoConfiguration.class, SecurityAutoConfiguration.class, ManagementWebSecurityAutoConfiguration.class, OAuth2ResourceServerAutoConfiguration.class })
@ComponentScan(basePackages = { "org.eclipse.digitaltwin.basyx.authorization", "org.eclipse.digitaltwin.basyx.authorization.rbac", "org.eclipse.digitaltwin.basyx.aasregistry.feature", "org.eclipse.digitaltwin.basyx.aasregistry.service",
		"org.eclipse.digitaltwin.basyx.aasregistry.service.api", "org.eclipse.digitaltwin.basyx.aasregistry.service.configuration", "org.eclipse.digitaltwin.basyx.aasdiscoveryservice.component", "org.eclipse.digitaltwin.basyx.aasdiscoveryservice.core", "org.eclipse.digitaltwin.basyx.aasdiscoveryservice.core.feature", "org.eclipse.digitaltwin.basyx.aasdiscoveryservice.backend", "org.eclipse.digitaltwin.basyx.aasdiscoveryservice.http", "org.eclipse.digitaltwin.basyx.digitaltwinregistry.component", "org.eclipse.digitaltwin.aas4j.v3.model" },

		excludeFilters = { 
				
				
//				@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = org.eclipse.digitaltwin.basyx.http.description.DescriptionController.class),
//				@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = org.eclipse.digitaltwin.basyx.aasregistry.service.api.SearchApiController.class),
				@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = org.eclipse.digitaltwin.basyx.aasregistry.service.OpenApiGeneratorApplication.class),
				@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = org.eclipse.digitaltwin.basyx.aasdiscoveryservice.component.AasDiscoveryServiceComponent.class),
				@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = org.eclipse.digitaltwin.basyx.aasdiscoveryservice.http.documentation.AasDiscoveryServiceApiDocumentationConfiguration.class),
				@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = org.eclipse.digitaltwin.basyx.aasregistry.service.configuration.SpringDocConfiguration.class),
				@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = RestConfiguration.class),
				@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = BaSyxHTTPConfiguration.class)


		})
// @Import({ OpenApiGeneratorApplication.class,
// AasDiscoveryServiceComponent.class })
public class AasEnvironmentComponent {

	// @Autowired
	// private ShellDescriptorsApi descriptionApiController; // Only inject one
	// @Autowired
	// private LookupApiController lookupApiController; // Only inject one

	public static void main(String[] args) throws JsonProcessingException {
		ConfigurableApplicationContext applicationContext = SpringApplication.run(AasEnvironmentComponent.class, args);

//		Map<String, ObjectMapper> mapperInstance =  applicationContext.getBeansOfType(ObjectMapper.class);
		ObjectMapper mapperInstance =  applicationContext.getBean(ObjectMapper.class);
		
		System.out.println("Mapper Ins:" + new ObjectMapper().writeValueAsString(mapperInstance));
//		String[] beanNames = applicationContext.getBeanDefinitionNames();
//		Arrays.stream(beanNames).forEach(System.out::println);
	}
}

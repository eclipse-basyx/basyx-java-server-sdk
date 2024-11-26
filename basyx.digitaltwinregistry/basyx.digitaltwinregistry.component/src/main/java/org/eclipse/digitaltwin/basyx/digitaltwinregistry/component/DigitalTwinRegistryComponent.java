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
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.FullyQualifiedAnnotationBeanNameGenerator;

import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * Creates and starts the DigitalTwin Registry off-shelf-component.
 * 
 * @author danish
 *
 */
@SpringBootApplication(nameGenerator = FullyQualifiedAnnotationBeanNameGenerator.class, scanBasePackages = { "org.eclipse.digitaltwin.basyx" }, exclude = { MongoAutoConfiguration.class, MongoDataAutoConfiguration.class,
		SecurityAutoConfiguration.class, ManagementWebSecurityAutoConfiguration.class, OAuth2ResourceServerAutoConfiguration.class })

@ComponentScan(basePackages = { "org.eclipse.digitaltwin.basyx" },

		excludeFilters = { @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = org.eclipse.digitaltwin.basyx.aasregistry.service.OpenApiGeneratorApplication.class),
				@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = org.eclipse.digitaltwin.basyx.aasdiscoveryservice.component.AasDiscoveryServiceComponent.class),
				@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = org.eclipse.digitaltwin.basyx.aasdiscoveryservice.http.documentation.AasDiscoveryServiceApiDocumentationConfiguration.class),
				@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = org.eclipse.digitaltwin.basyx.aasregistry.service.configuration.SpringDocConfiguration.class),
				@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = RestConfiguration.class), @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = BaSyxHTTPConfiguration.class),
				@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = org.eclipse.digitaltwin.basyx.aasregistry.service.errors.BasyxControllerAdvice.class),
				@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = org.eclipse.digitaltwin.basyx.http.BaSyxExceptionHandler.class) })
public class DigitalTwinRegistryComponent {

	public static void main(String[] args) throws JsonProcessingException {
		SpringApplication.run(DigitalTwinRegistryComponent.class, args);
	}
}

/*******************************************************************************
 * Copyright (C) 2025 the Eclipse BaSyx Authors
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

import org.eclipse.digitaltwin.basyx.aasdiscoveryservice.component.AasDiscoveryServiceComponent;
import org.eclipse.digitaltwin.basyx.aasregistry.service.api.*;
import org.eclipse.digitaltwin.basyx.aasregistry.service.configuration.HomeController;
import org.eclipse.digitaltwin.basyx.aasregistry.service.events.RegistryEventLogSink;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@SpringBootApplication

@ComponentScan(
		basePackages = {
				"org.eclipse.digitaltwin.basyx"
		},
		excludeFilters = {

				@ComponentScan.Filter(
						type = FilterType.ASSIGNABLE_TYPE,
						value = BasyxSearchApiDelegate.class
				),
				@ComponentScan.Filter(
						type = FilterType.ASSIGNABLE_TYPE,
						value = BasyxDescriptionApiDelegate.class
				),
				@ComponentScan.Filter(
						type = FilterType.ASSIGNABLE_TYPE,
						value = BasyxRegistryApiDelegate.class
				),
				@ComponentScan.Filter(
						type = FilterType.ASSIGNABLE_TYPE,
						value = org.eclipse.digitaltwin.basyx.aasregistry.service.api.DescriptionApiController.class
				),
				@ComponentScan.Filter(
						type = FilterType.ASSIGNABLE_TYPE,
						value = RegistryEventLogSink.class
				),
				@ComponentScan.Filter(
						type = FilterType.ASSIGNABLE_TYPE,
						value = org.eclipse.digitaltwin.basyx.aasregistry.service.api.DescriptionApi.class
				),
				@ComponentScan.Filter(
						type = FilterType.ASSIGNABLE_TYPE,
						value = org.eclipse.digitaltwin.basyx.http.description.DescriptionController.class
				),
				@ComponentScan.Filter(
						type = FilterType.ASSIGNABLE_TYPE,
						value = DescriptionApiDelegate.class
				),
				@ComponentScan.Filter(
						type = FilterType.ASSIGNABLE_TYPE,
						value = SearchApiController.class
				),
				@ComponentScan.Filter(
						type = FilterType.ASSIGNABLE_TYPE,
						value = ShellDescriptorsApiController.class
				),
				@ComponentScan.Filter(
						type = FilterType.ASSIGNABLE_TYPE,
						value = HomeController.class
				),
				@ComponentScan.Filter(
						type = FilterType.ASSIGNABLE_TYPE,
						value = AasDiscoveryServiceComponent.class
				),
				@ComponentScan.Filter(
						type = FilterType.ASSIGNABLE_TYPE,
						value = org.eclipse.digitaltwin.basyx.aasregistry.service.configuration.SpringDocConfiguration.class
				),

				@ComponentScan.Filter(
						type = FilterType.ASSIGNABLE_TYPE,
						value = org.eclipse.digitaltwin.basyx.aasdiscoveryservice.http.documentation.AasDiscoveryServiceApiDocumentationConfiguration.class
				),
				@ComponentScan.Filter(
						type = FilterType.ASSIGNABLE_TYPE,
						value = org.eclipse.digitaltwin.basyx.authorization.rbac.KeycloakRoleProvider.class
				),
				@ComponentScan.Filter(
						type = FilterType.ASSIGNABLE_TYPE,
						value = org.eclipse.digitaltwin.basyx.authorization.KeycloakSubjectInformationProvider.class
				),
		}
)
public class DigitalTwinRegistry {
	public static void main(String[] args) {
		SpringApplication.run(DigitalTwinRegistry.class, args);
	}
}
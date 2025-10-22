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

package org.eclipse.digitaltwin.basyx.digitaltwinregistry.component.tests.documentation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.digitaltwin.basyx.digitaltwinregistry.component.documentation.DTRegistryApiDocumentationConfiguration;
import org.junit.Before;
import org.junit.Test;

import io.swagger.v3.oas.models.OpenAPI;

@Slf4j
public class DTRegistryApiDocumentationConfigurationTest {

	private DTRegistryApiDocumentationConfiguration config;

	@Before
	public void setUp() {
		config = new DTRegistryApiDocumentationConfiguration();
	}

	@Test
	public void testCustomOpenAPI() {
		log.info("Started unit test - testCustomOpenAPI()");
		OpenAPI openAPI = config.customOpenAPI();

		assertNotNull(openAPI);
		assertNotNull(openAPI.getInfo());
		assertEquals("BaSyx Digital Twin Registry", openAPI.getInfo().getTitle());
		assertEquals("BaSyx Digital Twin Registry API", openAPI.getInfo().getDescription());
		log.info("Successfully conducted unit test");
	}

	@Test
	public void testBeanPrimaryAnnotation() throws NoSuchMethodException {
		log.info("Started unit test - testBeanPrimaryAnnotation()");
		java.lang.reflect.Method method = DTRegistryApiDocumentationConfiguration.class.getMethod("customOpenAPI");
		boolean hasPrimaryAnnotation = method.isAnnotationPresent(org.springframework.context.annotation.Primary.class);
		assertTrue("customOpenAPI method should have @Primary annotation", hasPrimaryAnnotation);
		log.info("Successfully conducted unit test");
	}

	@Test
	public void testConfigurationAnnotation() {
		log.info("Started unit test - testConfigurationAnnotation()");
		boolean hasConfigurationAnnotation = config.getClass().isAnnotationPresent(org.springframework.context.annotation.Configuration.class);
		assertTrue("Class should have @Configuration annotation", hasConfigurationAnnotation);
		log.info("Successfully conducted unit test");
	}
}
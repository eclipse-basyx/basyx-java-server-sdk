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
package org.eclipse.digitaltwin.basyx.submodelservice.component;

import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.aas4j.v3.model.ReferenceTypes;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.ApiException;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.api.SubmodelRegistryApi;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.model.SubmodelDescriptor;
import org.junit.AfterClass;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;


/**
 * @author Gerhard Sonnenberg DFKI GmbH
 */
@RunWith(SpringRunner.class)
@ActiveProfiles("integrationInMemory")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE
, properties= {"basyx.externalurl=http://localhost:8765",
		"basyx.submodelservice.feature.registryintegration=http://localhost:8060"
		})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class InMemorySubmodelServiceRegistryIntegrationTest {
	
	@Value("${basyx.submodelservice.feature.registryintegration}")
	private String registryUrl;
	
	
	@Test
	public void testDescriptorRegistered() throws ApiException {
		SubmodelRegistryApi api = new SubmodelRegistryApi(registryUrl);
		SubmodelDescriptor descriptor = api.getSubmodelDescriptorById("Example");
		Assertions.assertEquals(1, descriptor.getEndpoints().size());
	}
	
	@AfterClass
	public static void testSubmodelServiceIsUnregistered() {
		// the context ist closed after all a method run and thus after class
		SubmodelRegistryApi api = new SubmodelRegistryApi("http://localhost:8060");
		try {
			api.getSubmodelDescriptorById("Example");
			Assertions.fail();
		} catch (ApiException ex) {
			Assertions.assertEquals(404, ex.getCode());
		}
	}
}

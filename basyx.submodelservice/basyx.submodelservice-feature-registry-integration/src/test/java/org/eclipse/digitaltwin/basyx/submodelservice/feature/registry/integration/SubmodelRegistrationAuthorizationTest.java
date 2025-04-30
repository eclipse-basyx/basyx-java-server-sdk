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

import org.eclipse.digitaltwin.basyx.submodelregistry.client.ApiException;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.api.SubmodelRegistryApi;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.model.SubmodelDescriptor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.annotation.DirtiesContext;

/**
 * @author Gerhard Sonnenberg (DFKI GmbH)
 */
@SpringBootTest(webEnvironment = WebEnvironment.NONE, 
	classes = SubmodelServiceTestConfiguration.class,
	properties = {
		"basyx.submodelservice.feature.registryintegration.authorization.enabled=true",
		"basyx.submodelservice.feature.registryintegration.authorization.token-endpoint=http://localhost:9097/realms/BaSyx/protocol/openid-connect/token",
		"basyx.submodelservice.feature.registryintegration.authorization.grant-type=CLIENT_CREDENTIALS",
		"basyx.submodelservice.feature.registryintegration.authorization.client-id=workstation-1",
		"basyx.submodelservice.feature.registryintegration.authorization.client-secret=nY0mjyECF60DGzNmQUjL81XurSl8etom",
		"basyx.submodelservice.feature.registryintegration=http://localhost:8061",
		"basyx.externalurl=http://localhost:8765",
		"basyx.backend=InMemory" })
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class SubmodelRegistrationAuthorizationTest {
	
	@Autowired
	private SubmodelServiceRegistryLink regLink;
	
	@Test
	public void testSubmodelIsRegistered() throws ApiException {
		SubmodelRegistryApi api = regLink.getRegistryApi();
		SubmodelDescriptor descriptor = api.getSubmodelDescriptorById(SubmodelServiceTestConfiguration.SM_ID);
		Assertions.assertEquals(1, descriptor.getEndpoints().size());
	}
	
}

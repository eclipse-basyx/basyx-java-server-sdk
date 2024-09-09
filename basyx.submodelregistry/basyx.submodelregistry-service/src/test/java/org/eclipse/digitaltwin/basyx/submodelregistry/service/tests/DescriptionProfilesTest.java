/*******************************************************************************
 * Copyright (C) 2023 DFKI GmbH (https://www.dfki.de/en/web)
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
package org.eclipse.digitaltwin.basyx.submodelregistry.service.tests;

import static org.junit.Assert.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.digitaltwin.basyx.submodelregistry.service.api.BasyxDescriptionApiDelegate;
import org.eclipse.digitaltwin.basyx.submodelregistry.service.api.BasyxDescriptionApiDelegate.ProfileNotFoundException;
import org.eclipse.digitaltwin.basyx.submodelregistry.service.api.DescriptionApiController;
import org.eclipse.digitaltwin.basyx.submodelregistry.service.api.DescriptionApiDelegate;
import org.eclipse.digitaltwin.basyx.submodelregistry.service.events.RegistryEventSink;
import org.eclipse.digitaltwin.basyx.submodelregistry.service.storage.SubmodelRegistryStorage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@WebMvcTest(DescriptionApiController.class)
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
@TestPropertySource(properties = { "description.profiles=https://admin-shell.io/aas/API/3/0/SubmodelRegistryServiceSpecification/SSP-001,https://admin-shell.io/aas/API/3/0/DiscoveryServiceSpecification/SSP-001" })
public class DescriptionProfilesTest {

	@MockBean
	public SubmodelRegistryStorage storage;

	@MockBean
	public RegistryEventSink eventSink;

	@Autowired
	public DescriptionApiDelegate delegate;
	
	@Autowired
	private MockMvc mvc;
	
	@Test
	public void whenGetDescription_ThenSuccess() throws Exception {
		this.mvc.perform(get("/description").accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(content().contentType(MediaType.APPLICATION_JSON))
		.andExpect(jsonPath("$.profiles").value(getDefinedProfiles()));
	}

	@Test
	public void whenWrongConfiguration_FailedToMapToEnum() {
		assertThrows(ProfileNotFoundException.class,
				() -> ((BasyxDescriptionApiDelegate) delegate).setValues(new String[] { "Unknown-Value" }));
	}

	private List<String> getDefinedProfiles() {
		TestPropertySource src = DescriptionProfilesTest.class.getAnnotation(TestPropertySource.class);
		String profilesDef = src.properties()[0];
		String[] definedProfiles = profilesDef.split("=")[1].split(",");
		return Arrays.stream(definedProfiles).collect(Collectors.toList());
	}
}
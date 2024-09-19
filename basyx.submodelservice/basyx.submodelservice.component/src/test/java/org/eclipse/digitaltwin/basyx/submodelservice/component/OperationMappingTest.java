/*******************************************************************************
 * Copyright (C) 2024 DFKI GmbH (https://www.dfki.de/en/web)
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
/**
 * @author Gerhard Sonnenberg DFKI GmbH
 */
import org.eclipse.digitaltwin.aas4j.v3.model.OperationRequest;
import org.eclipse.digitaltwin.aas4j.v3.model.OperationResult;
import org.eclipse.digitaltwin.basyx.submodelrepository.feature.operationdispatching.java.TestOperationValues;
import org.eclipse.digitaltwin.basyx.submodelrepository.feature.operationdispatching.java.ops.MockOneArgMappingOperation;
import org.eclipse.digitaltwin.basyx.submodelservice.feature.operationdispatching.OperationDispatchingSubmodelServiceFeature;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, properties = {
		OperationDispatchingSubmodelServiceFeature.FEATURENAME + ".enabled=true",
		OperationDispatchingSubmodelServiceFeature.FEATURENAME + ".mappings[BasicOperations.AddOperation]=org.eclipse.digitaltwin.basyx.submodelrepository.feature.operationdispatching.java.ops.MockMappingOperation",
		OperationDispatchingSubmodelServiceFeature.FEATURENAME + ".mappings.SquareOperation=org.eclipse.digitaltwin.basyx.submodelrepository.feature.operationdispatching.java.ops.MockOneArgMappingOperation",
		"basyx.submodelservice.submodel.file=example/submodel.json" })
@AutoConfigureMockMvc
public class OperationMappingTest {

	@Autowired
	private MockMvc mvc;

	@Autowired
	private ObjectMapper mapper;

	@Test
	public void testMappingOperationInvoked() throws Exception {
		OperationRequest request = TestOperationValues.requestForInt(3);
		OperationResult result = TestOperationValues.resultForString("BasicOperations", "AddOperation");
		String body = mapper.writeValueAsString(request);
		String expected = mapper.writeValueAsString(result);

		mvc.perform(MockMvcRequestBuilders.post("/submodel/submodel-elements/BasicOperations.AddOperation/invoke")
				.contentType(MediaType.APPLICATION_JSON).content(body).accept(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().json(expected));
	}
	
	@Test
	public void testOneArgMethodsAreInvoked() throws Exception {
		OperationRequest request = TestOperationValues.requestForInt(3);
		OperationResult result = TestOperationValues.resultForString(MockOneArgMappingOperation.ONE_ARG);
		String body = mapper.writeValueAsString(request);
		String expected = mapper.writeValueAsString(result);

		mvc.perform(MockMvcRequestBuilders.post("/submodel/submodel-elements/SquareOperation/invoke")
				.contentType(MediaType.APPLICATION_JSON).content(body).accept(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().json(expected));
	}
}
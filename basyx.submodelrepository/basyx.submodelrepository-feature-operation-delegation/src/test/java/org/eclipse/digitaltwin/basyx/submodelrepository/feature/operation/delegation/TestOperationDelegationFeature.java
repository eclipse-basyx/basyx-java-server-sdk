/*******************************************************************************
 * Copyright (C) 2024 the Eclipse BaSyx Authors
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

package org.eclipse.digitaltwin.basyx.submodelrepository.feature.operation.delegation;

import static org.junit.Assert.assertArrayEquals;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.model.DataTypeDefXsd;
import org.eclipse.digitaltwin.aas4j.v3.model.OperationVariable;
import org.eclipse.digitaltwin.aas4j.v3.model.Property;
import org.eclipse.digitaltwin.aas4j.v3.model.Qualifier;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultOperationVariable;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultProperty;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultQualifier;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSubmodel;
import org.eclipse.digitaltwin.basyx.InvokableOperation;
import org.eclipse.digitaltwin.basyx.core.exceptions.OperationDelegationException;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.eclipse.digitaltwin.basyx.http.Aas4JHTTPSerializationExtension;
import org.eclipse.digitaltwin.basyx.http.BaSyxHTTPConfiguration;
import org.eclipse.digitaltwin.basyx.http.SerializationExtension;
import org.eclipse.digitaltwin.basyx.submodelrepository.SubmodelInMemoryBackendProvider;
import org.eclipse.digitaltwin.basyx.submodelrepository.SubmodelRepository;
import org.eclipse.digitaltwin.basyx.submodelrepository.SubmodelRepositoryFactory;
import org.eclipse.digitaltwin.basyx.submodelrepository.backend.SimpleSubmodelRepositoryFactory;
import org.eclipse.digitaltwin.basyx.submodelrepository.http.SubmodelRepositoryHTTPSerializationExtension;
import org.eclipse.digitaltwin.basyx.submodelservice.InMemorySubmodelServiceFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockserver.model.HttpStatusCode;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;

/**
 * Tests the {@link OperationDelegationSubmodelRepository} feature
 * 
 * @author danish, marie
 */
public class TestOperationDelegationFeature {

	private static final PaginationInfo NO_LIMIT_PAGINATION_INFO = new PaginationInfo(0, null);
	private static SubmodelRepository submodelRepository;
	private static HTTPMockServer httpMockServer = new HTTPMockServer(2020);
	private static WebClient webClient;
	private ObjectMapper mapper = configureObjectMapper();

	@BeforeClass
	public static void setUp() {
		httpMockServer.start();

		webClient = createWebClient();

		submodelRepository = createOperationDelegationSubmodelRepository(new HTTPOperationDelegation(webClient));
	}

	@AfterClass
	public static void tearDown() {

		if (httpMockServer == null)
			return;

		httpMockServer.stop();
	}

	@After
	public void reset() {
		if (submodelRepository == null)
			return;

		submodelRepository.getAllSubmodels(NO_LIMIT_PAGINATION_INFO).getResult().stream().forEach(sm -> submodelRepository.deleteSubmodel(sm.getId()));
	}

	@Test
	public void invokeOperationDelegation() throws FileNotFoundException, IOException {
		String expectedResponse = getExpectedOutputResponse(getInputVariable());

		createExpectationsForPost("/operationInvocation", getRequestBody(getInputVariable()), expectedResponse, HttpStatusCode.OK_200);

		String submodelId = "dummySubmodelOperationDelegation";

		createSubmodelAtRepository(submodelId);

		createInvokableSMEAtRepository(submodelId, "operationDelegationSME", "http://localhost:2020/operationInvocation");

		OperationVariable[] inputOperationVariable = getInputVariable();

		OperationVariable[] actualOutputOperationVariable = submodelRepository.invokeOperation(submodelId, "operationDelegationSME", inputOperationVariable);

		OperationVariable[] expectedOutputOperationVariable = getOutputVariable(inputOperationVariable);

		assertArrayEquals(expectedOutputOperationVariable, actualOutputOperationVariable);
	}

	@Test(expected = OperationDelegationException.class)
	public void invokeFailOperationDelegation() throws FileNotFoundException, IOException {

		createExpectationsForPost("/operationInvocationFail", getRequestBody(getInputVariable()), getExpectedOutputResponse(getInputVariable()), HttpStatusCode.BAD_REQUEST_400);

		String submodelId = "dummySubmodelOperationDelegation";

		createSubmodelAtRepository(submodelId);

		createInvokableSMEAtRepository(submodelId, "operationDelegationSME", "http://localhost:2020/operationInvocationFail");

		OperationVariable[] inputOperationVariable = getInputVariable();

		submodelRepository.invokeOperation(submodelId, "operationDelegationSME", inputOperationVariable);
	}
	
	private OperationVariable[] getInputVariable() {
		return new OperationVariable[] { createIntOperationVariable("int") };
	}

	private OperationVariable[] getOutputVariable(OperationVariable[] inputOperationVariable) {
		return square(inputOperationVariable);
	}

	private String getRequestBody(OperationVariable[] inputOperationVariable) throws JsonProcessingException {
		return mapper.writeValueAsString(inputOperationVariable);
	}

	private String getExpectedOutputResponse(OperationVariable[] inputOperationVariable) throws JsonProcessingException {
		OperationVariable[] output = getOutputVariable(inputOperationVariable);

		return mapper.writeValueAsString(output);
	}

	public static OperationVariable[] square(OperationVariable[] inputs) {
		Property in = (Property) inputs[0].getValue();
		Integer val = Integer.valueOf(in.getValue());
		Integer squared = val * val;
		in.setValue(squared.toString());
		in.setIdShort("result");

		return new OperationVariable[] { createOperationVariable(in) };
	}

	private static OperationVariable createOperationVariable(Property val) {
		return new DefaultOperationVariable.Builder().value(val).build();
	}

	private static WebClient createWebClient() {
		ExchangeStrategies strategies = ExchangeStrategies.builder().codecs(configurer -> {
			configurer.defaultCodecs().jackson2JsonEncoder(new Jackson2JsonEncoder(configureObjectMapper()));
			configurer.defaultCodecs().jackson2JsonDecoder(new Jackson2JsonDecoder(configureObjectMapper()));
		}).build();

		return WebClient.builder().exchangeStrategies(strategies).build();
	}

	private void createExpectationsForPost(String path, String requestBody, String expectedResponse, HttpStatusCode expectedResponseCode) throws FileNotFoundException, IOException {
		httpMockServer.createExpectationsForPostRequest(path, requestBody, expectedResponse, expectedResponseCode);
	}

	private static SubmodelRepository createOperationDelegationSubmodelRepository(OperationDelegation operationDelegation) {
		SubmodelRepositoryFactory repoFactory = new SimpleSubmodelRepositoryFactory(new SubmodelInMemoryBackendProvider(), new InMemorySubmodelServiceFactory());

		return new OperationDelegationSubmodelRepositoryFactory(repoFactory, operationDelegation).create();
	}

	private static Qualifier createInvocationDelegationQualifier(String delegationURL) {
		return new DefaultQualifier.Builder().type(HTTPOperationDelegation.INVOCATION_DELEGATION_TYPE).value(delegationURL).build();
	}

	private static void createSubmodelAtRepository(String submodelId) {
		Submodel submodel = createSubmodelDummy(submodelId);

		submodelRepository.createSubmodel(submodel);
	}

	private static Submodel createSubmodelDummy(String submodelId) {
		return new DefaultSubmodel.Builder().id(submodelId).build();
	}

	private static void createInvokableSMEAtRepository(String submodelId, String submodelElementIdShort, String delegationURL) {
		SubmodelElement submodelElement = new InvokableOperation.Builder().idShort(submodelElementIdShort).qualifiers(createInvocationDelegationQualifier(delegationURL)).build();

		submodelRepository.createSubmodelElement(submodelId, submodelElement);
	}

	private static DefaultOperationVariable createIntOperationVariable(String idShort) {
		return new DefaultOperationVariable.Builder().value(new DefaultProperty.Builder().idShort(idShort).valueType(DataTypeDefXsd.INT).value("5").build()).build();
	}

	private static ObjectMapper configureObjectMapper() {
		List<SerializationExtension> extensions = Arrays.asList(new Aas4JHTTPSerializationExtension(), new SubmodelRepositoryHTTPSerializationExtension());

		return new BaSyxHTTPConfiguration().jackson2ObjectMapperBuilder(extensions).build();
	}

}

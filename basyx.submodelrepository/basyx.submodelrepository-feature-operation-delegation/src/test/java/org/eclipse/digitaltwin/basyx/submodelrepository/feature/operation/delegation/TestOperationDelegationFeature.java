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

package org.eclipse.digitaltwin.basyx.submodelrepository.feature.operation.delegation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpServer;
import org.eclipse.digitaltwin.aas4j.v3.model.*;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultOperationVariable;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultProperty;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultQualifier;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSubmodel;
import org.eclipse.digitaltwin.basyx.core.exceptions.OperationDelegationException;
import org.eclipse.digitaltwin.basyx.core.filerepository.InMemoryFileRepository;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.eclipse.digitaltwin.basyx.http.Aas4JHTTPSerializationExtension;
import org.eclipse.digitaltwin.basyx.http.BaSyxHTTPConfiguration;
import org.eclipse.digitaltwin.basyx.http.SerializationExtension;
import org.eclipse.digitaltwin.basyx.operation.InvokableOperation;
import org.eclipse.digitaltwin.basyx.submodelrepository.SubmodelRepository;
import org.eclipse.digitaltwin.basyx.submodelrepository.SubmodelRepositoryFactory;
import org.eclipse.digitaltwin.basyx.submodelrepository.backend.CrudSubmodelRepositoryFactory;
import org.eclipse.digitaltwin.basyx.submodelrepository.http.SubmodelRepositoryHTTPSerializationExtension;
import org.eclipse.digitaltwin.basyx.submodelservice.InMemorySubmodelBackend;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockserver.model.HttpStatusCode;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

/**
 * Tests the {@link OperationDelegationSubmodelRepository} feature
 * 
 * @author danish, marie
 */
public class TestOperationDelegationFeature {

	private static SubmodelRepository submodelRepository;
	private static HTTPMockServer httpMockServer = new HTTPMockServer(2020);
	private static WebClient webClient;
	private ObjectMapper mapper = configureObjectMapper();

	@BeforeClass
	public static void setUp() {
		httpMockServer.start();

		webClient = createWebClient(createSecurityPropertiesWithLocalhostAllowlist());

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

		submodelRepository.getAllSubmodels(PaginationInfo.NO_LIMIT).getResult().stream().forEach(sm -> submodelRepository.deleteSubmodel(sm.getId()));
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

	@Test
	public void invokeOperationDelegationBlocksLoopbackByDefault() throws Exception {
		AtomicBoolean internalServerHit = new AtomicBoolean(false);
		HttpServer internalServer = HttpServer.create(new InetSocketAddress("127.0.0.1", 9090), 0);
		internalServer.createContext("/internal-plc-api", exchange -> {
			internalServerHit.set(true);
			byte[] response = "[]".getBytes(StandardCharsets.UTF_8);
			exchange.sendResponseHeaders(200, response.length);
			exchange.getResponseBody().write(response);
			exchange.close();
		});
		internalServer.start();

		try {
			OperationDelegationSecurityProperties strictSecurityProperties = new OperationDelegationSecurityProperties();
			WebClient strictWebClient = createWebClient(strictSecurityProperties);
			SubmodelRepository strictRepository = createOperationDelegationSubmodelRepository(new HTTPOperationDelegation(strictWebClient));

			String submodelId = "blockedLoopbackDelegationSubmodel";
			createSubmodelAtRepository(strictRepository, submodelId);
			createInvokableSMEAtRepository(strictRepository, submodelId, "operationDelegationSME", "http://127.0.0.1:9090/internal-plc-api");

			strictRepository.invokeOperation(submodelId, "operationDelegationSME", getInputVariable());
			fail("Expected OperationDelegationException for blocked loopback delegation target");
		} catch (OperationDelegationException e) {
			assertFalse("Blocked loopback request must not reach internal server", internalServerHit.get());
		} finally {
			internalServer.stop(0);
		}
	}

	@Test
	public void invokeOperationDelegationAllowsLoopbackWhenExplicitlyAllowlisted() throws FileNotFoundException, IOException {
		OperationDelegationSecurityProperties allowlistedSecurityProperties = new OperationDelegationSecurityProperties();
		allowlistedSecurityProperties.getAllowlist().setCidrs(Arrays.asList("127.0.0.0/8"));
		allowlistedSecurityProperties.getAllowlist().setPorts(Arrays.asList(2020));

		WebClient allowlistedWebClient = createWebClient(allowlistedSecurityProperties);
		SubmodelRepository allowlistedRepository = createOperationDelegationSubmodelRepository(new HTTPOperationDelegation(allowlistedWebClient));

		String submodelId = "allowlistedLoopbackDelegationSubmodel";
		OperationVariable[] inputOperationVariable = getInputVariable();
		String expectedResponse = getExpectedOutputResponse(getInputVariable());
		String path = "/operationInvocationAllowlisted";

		createExpectationsForPost(path, getRequestBody(inputOperationVariable), expectedResponse, HttpStatusCode.OK_200);
		createSubmodelAtRepository(allowlistedRepository, submodelId);
		createInvokableSMEAtRepository(allowlistedRepository, submodelId, "operationDelegationSME", "http://127.0.0.1:2020" + path);

		OperationVariable[] actualOutputOperationVariable = allowlistedRepository.invokeOperation(submodelId, "operationDelegationSME", inputOperationVariable);
		OperationVariable[] expectedOutputOperationVariable = getOutputVariable(getInputVariable());

		assertArrayEquals(expectedOutputOperationVariable, actualOutputOperationVariable);
	}

	@Test(expected = OperationDelegationException.class)
	public void invokeOperationDelegationRejectsRedirectResponses() throws FileNotFoundException, IOException {
		OperationVariable[] inputOperationVariable = getInputVariable();
		createExpectationsForPost("/operationInvocationRedirect", getRequestBody(inputOperationVariable), "[]", 302);

		String submodelId = "redirectDelegationSubmodel";
		createSubmodelAtRepository(submodelId);
		createInvokableSMEAtRepository(submodelId, "operationDelegationSME", "http://localhost:2020/operationInvocationRedirect");

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

	private static WebClient createWebClient(OperationDelegationSecurityProperties securityProperties) {
		ExchangeStrategies strategies = ExchangeStrategies.builder().codecs(configurer -> {
			configurer.defaultCodecs().jackson2JsonEncoder(new Jackson2JsonEncoder(configureObjectMapper()));
			configurer.defaultCodecs().jackson2JsonDecoder(new Jackson2JsonDecoder(configureObjectMapper()));
		}).build();

		OperationDelegationTargetValidator targetValidator = new OperationDelegationTargetValidator(securityProperties);
		return WebClient.builder().exchangeStrategies(strategies).filter((request, next) -> {
			targetValidator.validate(request.url());
			return next.exchange(request);
		}).build();
	}

	private static OperationDelegationSecurityProperties createSecurityPropertiesWithLocalhostAllowlist() {
		OperationDelegationSecurityProperties securityProperties = new OperationDelegationSecurityProperties();
		securityProperties.getAllowlist().setHosts(Arrays.asList("localhost"));
		securityProperties.getAllowlist().setCidrs(Arrays.asList("127.0.0.0/8", "::1/128"));
		securityProperties.getAllowlist().setPorts(Arrays.asList(2020));
		return securityProperties;
	}

	private void createExpectationsForPost(String path, String requestBody, String expectedResponse, HttpStatusCode expectedResponseCode) throws FileNotFoundException, IOException {
		httpMockServer.createExpectationsForPostRequest(path, requestBody, expectedResponse, expectedResponseCode);
	}

	private void createExpectationsForPost(String path, String requestBody, String expectedResponse, int expectedResponseCode) throws FileNotFoundException, IOException {
		httpMockServer.createExpectationsForPostRequest(path, requestBody, expectedResponse, expectedResponseCode);
	}

	private static SubmodelRepository createOperationDelegationSubmodelRepository(OperationDelegation operationDelegation) {
		SubmodelRepositoryFactory repoFactory = CrudSubmodelRepositoryFactory.builder().backend(new InMemorySubmodelBackend()).fileRepository(new InMemoryFileRepository()).buildFactory();

		return new OperationDelegationSubmodelRepositoryFactory(repoFactory, operationDelegation).create();
	}

	private static Qualifier createInvocationDelegationQualifier(String delegationURL) {
		return new DefaultQualifier.Builder().type(HTTPOperationDelegation.INVOCATION_DELEGATION_TYPE).value(delegationURL).build();
	}

	private static void createSubmodelAtRepository(String submodelId) {
		createSubmodelAtRepository(submodelRepository, submodelId);
	}

	private static void createSubmodelAtRepository(SubmodelRepository repository, String submodelId) {
		Submodel submodel = createSubmodelDummy(submodelId);

		repository.createSubmodel(submodel);
	}

	private static Submodel createSubmodelDummy(String submodelId) {
		return new DefaultSubmodel.Builder().id(submodelId).build();
	}

	private static void createInvokableSMEAtRepository(String submodelId, String submodelElementIdShort, String delegationURL) {
		createInvokableSMEAtRepository(submodelRepository, submodelId, submodelElementIdShort, delegationURL);
	}

	private static void createInvokableSMEAtRepository(SubmodelRepository repository, String submodelId, String submodelElementIdShort, String delegationURL) {
		SubmodelElement submodelElement = new InvokableOperation.Builder().idShort(submodelElementIdShort).qualifiers(createInvocationDelegationQualifier(delegationURL)).build();

		repository.createSubmodelElement(submodelId, submodelElement);
	}

	private static DefaultOperationVariable createIntOperationVariable(String idShort) {
		return new DefaultOperationVariable.Builder().value(new DefaultProperty.Builder().idShort(idShort).valueType(DataTypeDefXsd.INT).value("5").build()).build();
	}

	private static ObjectMapper configureObjectMapper() {
		List<SerializationExtension> extensions = Arrays.asList(new Aas4JHTTPSerializationExtension(), new SubmodelRepositoryHTTPSerializationExtension());

		return new BaSyxHTTPConfiguration().jackson2ObjectMapperBuilder(extensions).build();
	}

}

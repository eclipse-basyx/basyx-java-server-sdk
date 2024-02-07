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
import org.eclipse.digitaltwin.basyx.http.serialization.BaSyxHttpTestUtils;
import org.eclipse.digitaltwin.basyx.submodelrepository.InMemorySubmodelRepositoryFactory;
import org.eclipse.digitaltwin.basyx.submodelrepository.SubmodelRepository;
import org.eclipse.digitaltwin.basyx.submodelrepository.SubmodelRepositoryFactory;
import org.eclipse.digitaltwin.basyx.submodelrepository.http.SubmodelRepositoryHTTPSerializationExtension;
import org.eclipse.digitaltwin.basyx.submodelservice.InMemorySubmodelServiceFactory;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockserver.model.HttpStatusCode;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.http.codec.json.Jackson2JsonDecoder;

public class TestOperationDelegationFeature {

	private static final PaginationInfo NO_LIMIT_PAGINATION_INFO = new PaginationInfo(0, null);
	private static SubmodelRepository submodelRepository;
	private static HTTPMockServer httpMockServer = new HTTPMockServer(2020);
	private static WebClient webClient;

	@BeforeClass
	public static void setUpClass() throws FileNotFoundException, IOException {
		httpMockServer.start();

		webClient = createWebClient();

		submodelRepository = createOperationDelegationSubmodelRepository(new HTTPOperationDelegation(webClient));
	}

	@After
	public void reset() {
		if (submodelRepository == null)
			return;
		
		submodelRepository.getAllSubmodels(NO_LIMIT_PAGINATION_INFO).getResult().stream().forEach(sm -> submodelRepository.deleteSubmodel(sm.getId()));
	}

	private static WebClient createWebClient() {
		ExchangeStrategies strategies = ExchangeStrategies.builder().codecs(configurer -> configurer.defaultCodecs().jackson2JsonDecoder(new Jackson2JsonDecoder(configureObjectMapper()))).build();

		return WebClient.builder().exchangeStrategies(strategies).build();
	}

	@Test
	public void invokeOperationDelegation() throws FileNotFoundException, IOException {
		createExpectationsForPost("/operationInvocation", getRequestBody(), getResponseBody(), HttpStatusCode.OK_200);

		String submodelId = "dummySubmodelOperationDelegation";

		createSubmodelAtRepository(submodelId);
		
		createInvokableSMEAtRepository(submodelId, "operationDelegationSME", "http://localhost:2020/operationInvocation");

		OperationVariable[] inputOperationVariable = new OperationVariable[] { createIntOperationVariable("int") };

		OperationVariable[] expectedOutputOperationVariable = square(inputOperationVariable);

		OperationVariable[] outputOperationVariable = submodelRepository.invokeOperation("dummySubmodelOperationDelegation", "operationDelegationSME", inputOperationVariable);

		assertArrayEquals(expectedOutputOperationVariable, outputOperationVariable);
	}

	@Test(expected = OperationDelegationException.class)
	public void invokeFailOperationDelegation() throws FileNotFoundException, IOException {
		createExpectationsForPost("/operationInvocationFail", getRequestBody(), getResponseBody(), HttpStatusCode.BAD_REQUEST_400);
		
		String submodelId = "dummySubmodelOperationDelegation";

		createSubmodelAtRepository(submodelId);
		
		createInvokableSMEAtRepository(submodelId, "operationDelegationSME", "http://localhost:2020/operationInvocationFail");

		OperationVariable[] inputOperationVariable = new OperationVariable[] { createIntOperationVariable("int") };

		submodelRepository.invokeOperation("dummySubmodelOperationDelegation", "operationDelegationSME", inputOperationVariable);
	}

	private void createExpectationsForPost(String path, String requestBody, String responseBody, HttpStatusCode expectedResponseCode) throws FileNotFoundException, IOException {
		httpMockServer.createExpectationsForPostRequest(path, requestBody, responseBody, expectedResponseCode);
	}

	private static String getResponseBody() throws FileNotFoundException, IOException {
		String requestFileName = "output.json";

		return getJSONValueAsString(requestFileName);
	}

	private static String getRequestBody() throws FileNotFoundException, IOException {
		String responseFileName = "input.json";

		return getJSONValueAsString(responseFileName);
	}

	private static SubmodelRepository createOperationDelegationSubmodelRepository(OperationDelegation operationDelegation) {
		SubmodelRepositoryFactory repoFactory = new InMemorySubmodelRepositoryFactory(new InMemorySubmodelServiceFactory());

		return new OperationDelegationSubmodelRepositoryFactory(repoFactory, operationDelegation).create();
	}

	private static Qualifier createInvocationDelegationQualifier(String delegationURL) {
		return new DefaultQualifier.Builder().type(HTTPOperationDelegation.INVOCATION_DELEGATION_TYPE).value(delegationURL).build();
	}

	private static void createSubmodelAtRepository(String submodelId) {
		Submodel submodel = createSubmodelDummy("dummySubmodelOperationDelegation");
		
		submodelRepository.createSubmodel(submodel);
	}

	private static Submodel createSubmodelDummy(String submodelId) {
		return new DefaultSubmodel.Builder().id(submodelId).build();
	}

	private static void createInvokableSMEAtRepository(String submodelId, String submodelElementIdShort, String delegationURL) {
		SubmodelElement submodelElement = new InvokableOperation.Builder().idShort(submodelElementIdShort).qualifiers(createInvocationDelegationQualifier(delegationURL)).build();
		
		submodelRepository.createSubmodelElement(submodelId, submodelElement);
	}

	private static String getJSONValueAsString(String fileName) throws FileNotFoundException, IOException {
		return BaSyxHttpTestUtils.readJSONStringFromClasspath(fileName);
	}

	private static DefaultOperationVariable createIntOperationVariable(String idShort) {
		return new DefaultOperationVariable.Builder().value(new DefaultProperty.Builder().idShort(idShort).valueType(DataTypeDefXsd.INT).value("5").build()).build();
	}

	private static OperationVariable[] square(OperationVariable[] inputs) {
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

	private static ObjectMapper configureObjectMapper() {
		List<SerializationExtension> extensions = Arrays.asList(new Aas4JHTTPSerializationExtension(), new SubmodelRepositoryHTTPSerializationExtension());

		ObjectMapper mapper = new BaSyxHTTPConfiguration().jackson2ObjectMapperBuilder(extensions).build();
		return mapper;
	}

}

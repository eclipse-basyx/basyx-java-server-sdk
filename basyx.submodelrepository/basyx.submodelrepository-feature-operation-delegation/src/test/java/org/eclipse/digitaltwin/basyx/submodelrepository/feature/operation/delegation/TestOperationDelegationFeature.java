package org.eclipse.digitaltwin.basyx.submodelrepository.feature.operation.delegation;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.model.DataTypeDefXsd;
import org.eclipse.digitaltwin.aas4j.v3.model.Operation;
import org.eclipse.digitaltwin.aas4j.v3.model.OperationVariable;
import org.eclipse.digitaltwin.aas4j.v3.model.Property;
import org.eclipse.digitaltwin.aas4j.v3.model.Qualifier;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultOperation;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultOperationVariable;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultProperty;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultQualifier;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSubmodel;
import org.eclipse.digitaltwin.basyx.InvokableOperation;
import org.eclipse.digitaltwin.basyx.http.serialization.BaSyxHttpTestUtils;
import org.eclipse.digitaltwin.basyx.submodelrepository.InMemorySubmodelRepositoryFactory;
import org.eclipse.digitaltwin.basyx.submodelrepository.SubmodelRepository;
import org.eclipse.digitaltwin.basyx.submodelrepository.SubmodelRepositoryFactory;
import org.eclipse.digitaltwin.basyx.submodelservice.InMemorySubmodelServiceFactory;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestOperationDelegationFeature {
	
	private static SubmodelRepository submodelRepository;
	private static HTTPMockServer httpMockServer = new HTTPMockServer(2020);
	
	@BeforeClass
	public static void setUpClass() throws FileNotFoundException, IOException {
		httpMockServer.start();

		submodelRepository = createOperationDelegationSubmodelRepository(new HTTPOperationDelegation());
	}
	
	@Test
	public void invokeOperationDelegation() throws FileNotFoundException, IOException {
		createExpectationsForPost("operationInvocation", getRequestBody(), getResponseBody());
		
		createSubmodelAtRepository();
		
		OperationVariable[] inputOperationVariable = new OperationVariable[] { createIntOperationVariable("int") };
		
		OperationVariable[] expectedOutputOperationVariable = square(inputOperationVariable);
		
		OperationVariable[] outputOperationVariable = submodelRepository.invokeOperation("dummySubmodelOperationDelegation", "operationDelegationSME", inputOperationVariable);
		
		assertArrayEquals(expectedOutputOperationVariable, outputOperationVariable);
	}

	private void createExpectationsForPost(String path, String requestBody, String responseBody) throws FileNotFoundException, IOException {
		httpMockServer.createExpectationsForPostRequest(path , requestBody, responseBody);
	}
	
	private static String getResponseBody() throws FileNotFoundException, IOException {
		String requestFileName = "input.json";
		
		return getJSONValueAsString(requestFileName);
	}

	private static String getRequestBody() throws FileNotFoundException, IOException {
		String responseFileName = "output.json";
		
		return getJSONValueAsString(responseFileName);
	}

	private static SubmodelRepository createOperationDelegationSubmodelRepository(OperationDelegation operationDelegation) {
		SubmodelRepositoryFactory repoFactory = new InMemorySubmodelRepositoryFactory(new InMemorySubmodelServiceFactory());

		return new OperationDelegationSubmodelRepositoryFactory(repoFactory, operationDelegation).create();
	}
	
	private Qualifier createInvocationDelegationQualifierList() {
		return new DefaultQualifier.Builder().type(HTTPOperationDelegation.INVOCATION_DELEGATION_TYPE).value("serverUrl").build();
	}
	
	private void createSubmodelAtRepository() {
		Submodel submodel = createSubmodelDummy("dummySubmodelOperationDelegation");
		submodelRepository.createSubmodel(submodel);
		SubmodelElement submodelElement = createSubmodelElementDummy("operationDelegationSME");
		
		submodelRepository.createSubmodelElement(submodel.getId(), submodelElement);
	}
	
	private Submodel createSubmodelDummy(String submodelId) {
		return new DefaultSubmodel.Builder().id(submodelId).build();
	}
	
	private SubmodelElement createSubmodelElementDummy(String submodelElementIdShort) {
		return new InvokableOperation.Builder().idShort(submodelElementIdShort).qualifiers(createInvocationDelegationQualifierList()).build();
	}
	
	private static String getJSONValueAsString(String fileName) throws FileNotFoundException, IOException {
		return BaSyxHttpTestUtils.readJSONStringFromClasspath(fileName);
	}
	
	private static Operation createInvokableOperation() {
		return new InvokableOperation.Builder()
				.inputVariables(createIntOperationVariable("input"))
				.idShort("squareOperation")
				.build();
	}
	
	private static DefaultOperationVariable createIntOperationVariable(String idShort) {
		return new DefaultOperationVariable.Builder().value(new DefaultProperty.Builder().idShort(idShort).valueType(DataTypeDefXsd.INT).build()).build();
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

}

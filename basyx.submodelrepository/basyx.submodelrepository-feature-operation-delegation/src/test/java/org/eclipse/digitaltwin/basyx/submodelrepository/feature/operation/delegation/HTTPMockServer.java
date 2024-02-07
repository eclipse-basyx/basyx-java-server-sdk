package org.eclipse.digitaltwin.basyx.submodelrepository.feature.operation.delegation;

import org.eclipse.digitaltwin.aas4j.v3.model.OperationVariable;
import org.eclipse.digitaltwin.aas4j.v3.model.Property;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultOperationVariable;
import org.eclipse.digitaltwin.basyx.http.model.OperationRequest;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.model.HttpStatusCode;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class HTTPMockServer {

	private ClientAndServer clientAndServer;
	private int port;

	public HTTPMockServer(int port) {
		super();
		this.port = port;
	}

	public int getPort() {
		return port;
	}

	public void start() {
		clientAndServer = ClientAndServer.startClientAndServer(port);
	}

	public void createExpectationsForPostRequest(String path, String requestBody, String responseBody) throws JsonMappingException, JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		
		OperationRequest operationRequest = mapper.readValue(responseBody, OperationRequest.class);
		
		OperationVariable[] input = operationRequest.getInputArguments().toArray(new OperationVariable[0]);
		
		OperationVariable[] output = square(input);
		
		String outputResponse = mapper.writeValueAsString(output);
 		
		clientAndServer.when(HttpRequest.request().withMethod("POST").withBody(requestBody).withPath(path)).respond(HttpResponse.response().withStatusCode(HttpStatusCode.OK_200.code()).withBody(outputResponse));
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

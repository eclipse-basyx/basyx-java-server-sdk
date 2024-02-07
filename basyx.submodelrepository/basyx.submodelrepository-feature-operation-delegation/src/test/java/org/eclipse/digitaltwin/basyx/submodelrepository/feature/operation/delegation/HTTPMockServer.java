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

import java.util.Arrays;
import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.model.OperationVariable;
import org.eclipse.digitaltwin.aas4j.v3.model.Property;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultOperationVariable;
import org.eclipse.digitaltwin.basyx.http.Aas4JHTTPSerializationExtension;
import org.eclipse.digitaltwin.basyx.http.BaSyxHTTPConfiguration;
import org.eclipse.digitaltwin.basyx.http.SerializationExtension;
import org.eclipse.digitaltwin.basyx.http.model.OperationRequest;
import org.eclipse.digitaltwin.basyx.submodelrepository.http.SubmodelRepositoryHTTPSerializationExtension;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.model.HttpStatusCode;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mockserver.model.MediaType;

/**
 * An HTTP Mock server for mocking operation delgation feature
 * 
 * @author danish, marie
 */
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

	/**
	 * Starts the mock server
	 */
	public void start() {
		clientAndServer = ClientAndServer.startClientAndServer(port);
	}
	
	/**
	 * Stops the mock server if it is running
	 */
	public void stop() {
		
		if (clientAndServer == null || !clientAndServer.isRunning())
			return;
		
		clientAndServer.stop();
	}

	/**
	 * Creates expectations for a POST request
	 * 
	 * @param path
	 * @param requestBody
	 * @param responseBody
	 * @param expectedResponseCode
	 * 
	 * @throws JsonMappingException
	 * @throws JsonProcessingException
	 */
	public void createExpectationsForPostRequest(String path, String requestBody, String responseBody, HttpStatusCode expectedResponseCode) throws JsonMappingException, JsonProcessingException {
		ObjectMapper mapper = configureObjectMapper();
		
		OperationRequest operationRequest = mapper.readValue(requestBody, OperationRequest.class);
		
		OperationVariable[] input = operationRequest.getInputArguments().toArray(new OperationVariable[0]);
		
		OperationVariable[] output = square(input);
		
		String outputResponse = mapper.writeValueAsString(output);
 		
		clientAndServer.when(HttpRequest.request().withMethod("POST").withPath(path).withBody(new ObjectMapper().writeValueAsString(input))).respond(HttpResponse.response().withStatusCode(expectedResponseCode.code()).withBody(outputResponse).withContentType(MediaType.APPLICATION_JSON));
	}
	
	public static OperationVariable[] square(OperationVariable[] inputs) {
		Property in = (Property) inputs[0].getValue();
		Integer val = Integer.valueOf(in.getValue());
		Integer squared = val * val;
		in.setValue(squared.toString());
		in.setIdShort("result");
		
		return new OperationVariable[] { createOperationVariable(in) };
	}

	private ObjectMapper configureObjectMapper() {
		List<SerializationExtension> extensions = Arrays.asList(new Aas4JHTTPSerializationExtension(), new SubmodelRepositoryHTTPSerializationExtension());

		ObjectMapper mapper = new BaSyxHTTPConfiguration().jackson2ObjectMapperBuilder(extensions).build();
		return mapper;
	}
	
	private static OperationVariable createOperationVariable(Property val) {
		return new DefaultOperationVariable.Builder().value(val).build();
	}

}

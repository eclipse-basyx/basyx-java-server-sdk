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

import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.model.HttpStatusCode;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
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
	public void createExpectationsForPostRequest(String path, String requestBody, String expectedResponse, HttpStatusCode expectedResponseCode) throws JsonMappingException, JsonProcessingException {

		clientAndServer.when(HttpRequest.request().withMethod("POST").withPath(path).withBody(requestBody))
				.respond(HttpResponse.response().withStatusCode(expectedResponseCode.code()).withBody(expectedResponse).withContentType(MediaType.APPLICATION_JSON));
	}

}

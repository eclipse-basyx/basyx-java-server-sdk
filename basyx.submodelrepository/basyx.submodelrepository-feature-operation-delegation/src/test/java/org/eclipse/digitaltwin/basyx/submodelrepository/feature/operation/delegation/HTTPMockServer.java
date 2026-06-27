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

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * An HTTP Mock server for mocking operation delgation feature
 * 
 * @author danish, marie
 */
public class HTTPMockServer {

	private final int port;
	private final Map<String, PostExpectation> expectations = new ConcurrentHashMap<>();
	private HttpServer server;

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
	public void start() throws IOException {
		server = HttpServer.create(new InetSocketAddress(port), 0);
		server.createContext("/", this::handleRequest);
		server.start();
	}

	/**
	 * Stops the mock server if it is running
	 */
	public void stop() {

		if (server == null)
			return;

		server.stop(0);
	}

	/**
	 * Creates expectations for a POST request
	 * 
	 * @param path
	 * @param requestBody
	 * @param responseBody
	 * @param expectedResponseCode
	 * 
	 */
	public void createExpectationsForPostRequest(String path, String requestBody, String expectedResponse, int expectedResponseCode) {
		expectations.put(path, new PostExpectation(requestBody, expectedResponse, expectedResponseCode));
	}

	private void handleRequest(HttpExchange exchange) throws IOException {
		PostExpectation expectation = expectations.get(exchange.getRequestURI().getPath());

		if (expectation == null || !exchange.getRequestMethod().equals("POST")) {
			sendResponse(exchange, 404, "");
			return;
		}

		String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
		if (!expectation.requestBody.equals(requestBody)) {
			sendResponse(exchange, 400, "");
			return;
		}

		exchange.getResponseHeaders().set("Content-Type", "application/json");
		sendResponse(exchange, expectation.responseCode, expectation.responseBody);
	}

	private void sendResponse(HttpExchange exchange, int responseCode, String responseBody) throws IOException {
		byte[] response = responseBody.getBytes(StandardCharsets.UTF_8);
		exchange.sendResponseHeaders(responseCode, response.length);
		try (OutputStream responseStream = exchange.getResponseBody()) {
			responseStream.write(response);
		}
	}

	private static class PostExpectation {
		private final String requestBody;
		private final String responseBody;
		private final int responseCode;

		private PostExpectation(String requestBody, String responseBody, int responseCode) {
			this.requestBody = requestBody;
			this.responseBody = responseBody;
			this.responseCode = responseCode;
		}
	}
}

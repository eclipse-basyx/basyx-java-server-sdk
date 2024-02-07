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

import org.eclipse.digitaltwin.aas4j.v3.model.OperationVariable;
import org.eclipse.digitaltwin.aas4j.v3.model.Qualifier;
import org.eclipse.digitaltwin.basyx.core.exceptions.OperationDelegationException;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

/**
 * An implementation of the {@link OperationDelegation} for HTTP
 * 
 * @author danish, marie
 */
public class HTTPOperationDelegation implements OperationDelegation {

	public static final String INVOCATION_DELEGATION_TYPE = "invocationDelegation";

	private WebClient webClient;

	public HTTPOperationDelegation(WebClient webClient) {
		this.webClient = webClient;
	}

	@Override
	public OperationVariable[] delegate(Qualifier qualifier, OperationVariable[] input) throws OperationDelegationException {

		String uri = qualifier.getValue();

		try {
			return webClient.post().uri(uri).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).body(BodyInserters.fromValue(input)).exchangeToMono(response -> {
				if (response.statusCode().isError()) {
					throw new OperationDelegationException(String.format("Unable to delegate the invocation operation on the URI: '%s' the response code is %s", uri, response.statusCode()));
				} else {
					return response.bodyToMono(OperationVariable[].class);
				}
			}).block();

		} catch (WebClientResponseException e) {
			throw new OperationDelegationException(String.format("Exception occurred while invocing operation on the URI: '%s' the error is %s", uri, e.getStackTrace()));
		}

	}

}

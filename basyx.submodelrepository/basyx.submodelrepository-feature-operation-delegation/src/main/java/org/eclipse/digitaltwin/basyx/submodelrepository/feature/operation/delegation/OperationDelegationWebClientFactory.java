/*******************************************************************************
 * Copyright (C) 2026 the Eclipse BaSyx Authors
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

import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.codec.json.JacksonJsonDecoder;
import org.springframework.http.codec.json.JacksonJsonEncoder;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.netty.http.client.HttpClient;
import tools.jackson.databind.json.JsonMapper;

/**
 * Creates the {@link WebClient} used for outbound operation delegation
 * requests.
 * <p>
 * Delegation targets are validated twice: the request URI (scheme, host, port)
 * is validated before the exchange, and the host is resolved and validated
 * again by the HTTP client's own resolver at connection time. The connection
 * is only ever opened to an address that passed validation.
 */
public final class OperationDelegationWebClientFactory {

	private OperationDelegationWebClientFactory() {
	}

	public static WebClient create(JsonMapper mapper, OperationDelegationTargetValidator targetValidator) {
		ExchangeStrategies strategies = ExchangeStrategies.builder().codecs(configurer -> {
			configurer.defaultCodecs().jacksonJsonEncoder(new JacksonJsonEncoder(mapper));
			configurer.defaultCodecs().jacksonJsonDecoder(new JacksonJsonDecoder(mapper));
		}).build();

		HttpClient httpClient = HttpClient.create().resolver(new OperationDelegationAddressResolverGroup(targetValidator));

		return WebClient.builder().clientConnector(new ReactorClientHttpConnector(httpClient)).exchangeStrategies(strategies).filter(validateTargetFilter(targetValidator)).build();
	}

	private static ExchangeFilterFunction validateTargetFilter(OperationDelegationTargetValidator targetValidator) {
		return (request, next) -> {
			targetValidator.validate(request.url());
			return next.exchange(request);
		};
	}
}

/*******************************************************************************
 * Copyright (C) 2023 the Eclipse BaSyx Authors
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

import org.eclipse.digitaltwin.basyx.submodelrepository.SubmodelRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Configuration for the {@link SubmodelRepository} with Operation Delegation
 * 
 * @author danish
 */
@Configuration
@ConditionalOnExpression("${" + OperationDelegationSubmodelRepositoryFeature.FEATURENAME + ".enabled:true}")
public class OperationDelegationSubmodelRepositoryConfiguration {

	@Bean
	@ConditionalOnMissingBean
	public OperationDelegation getOperationDelegation(ObjectMapper mapper) {

		return new HTTPOperationDelegation(createWebClient(mapper));
	}

	private WebClient createWebClient(ObjectMapper mapper) {
		ExchangeStrategies strategies = ExchangeStrategies.builder().codecs(configurer -> {
			configurer.defaultCodecs().jackson2JsonEncoder(new Jackson2JsonEncoder(mapper));
			configurer.defaultCodecs().jackson2JsonDecoder(new Jackson2JsonDecoder(mapper));
		}).build();

		return WebClient.builder().exchangeStrategies(strategies).build();
	}

}

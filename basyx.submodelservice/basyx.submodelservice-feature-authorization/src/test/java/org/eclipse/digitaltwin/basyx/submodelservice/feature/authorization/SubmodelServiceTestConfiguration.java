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
package org.eclipse.digitaltwin.basyx.submodelservice.feature.authorization;

import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.dataformat.json.JsonDeserializer;
import org.eclipse.digitaltwin.aas4j.v3.model.Operation;
import org.eclipse.digitaltwin.aas4j.v3.model.OperationVariable;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.basyx.authorization.AccessTokenProvider;
import org.eclipse.digitaltwin.basyx.http.serialization.BaSyxHttpTestUtils;
import org.eclipse.digitaltwin.basyx.operation.InvokableOperation;
import org.eclipse.digitaltwin.basyx.submodelservice.SubmodelService;
import org.eclipse.digitaltwin.basyx.submodelservice.SubmodelServiceFactory;
import org.eclipse.digitaltwin.basyx.submodelservice.feature.DecoratedSubmodelServiceFactory;
import org.eclipse.digitaltwin.basyx.submodelservice.feature.SubmodelServiceFeature;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;


/**
 * @author Gerhard Sonnenberg (DFKI GmbH)
 */
@TestConfiguration
@EnableWebSecurity
@ComponentScan(basePackages = "org.eclipse.digitaltwin.basyx")
public class SubmodelServiceTestConfiguration {

	private static final String SUBMODEL_JSON = "authorization/submodel.json";
	
	@Primary
	@Bean
	public SubmodelServiceFactory getSubmodelServiceFactory(SubmodelServiceFactory aasServiceFactory,
			List<SubmodelServiceFeature> features) {
		return new DecoratedSubmodelServiceFactory(aasServiceFactory, features);
	}
	

	@Bean
	public Submodel getSubmodel() {		
		try {
			String json = BaSyxHttpTestUtils.readJSONStringFromClasspath(SUBMODEL_JSON);
			JsonDeserializer deserializer = new JsonDeserializer();
			deserializer.useImplementation(Operation.class, TestInvokableOperation.class);
			return deserializer.read(json, Submodel.class);
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	

	@Bean
	public SubmodelService getSubmodelService(SubmodelServiceFactory factory, Submodel submodel) {
		return factory.create(submodel);
	}
	
	@Bean
	public AccessTokenProvider accessTokenProvider() {
		String authenticaltionServerTokenEndpoint = "http://localhost:9096/realms/BaSyx/protocol/openid-connect/token";
		String clientId = "basyx-client-api";
		return new AccessTokenProvider(authenticaltionServerTokenEndpoint, clientId);
	}
	
	private static class TestInvokableOperation extends InvokableOperation {
		
		@Override
		public OperationVariable[] invoke(OperationVariable[] arguments) {
			return arguments;
		}
	}
}
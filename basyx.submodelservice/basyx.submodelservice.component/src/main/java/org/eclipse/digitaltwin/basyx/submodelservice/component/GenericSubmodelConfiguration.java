/*******************************************************************************
 * Copyright (C) 2024 DFKI GmbH (https://www.dfki.de/en/web)
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
 * 
 ******************************************************************************/
package org.eclipse.digitaltwin.basyx.submodelservice.component;

import java.util.function.Function;

import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.basyx.submodelservice.SubmodelService;
import org.eclipse.digitaltwin.basyx.submodelservice.SubmodelServiceFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;


/**
 * @author Gerhard Sonnenberg DFKI GmbH
 */
@Configuration
public class GenericSubmodelConfiguration {	
	
	@Bean
	public SubmodelService getSubmodelService(Submodel submodel, SubmodelServiceFactory factory) {		
		return factory.create(submodel);
	}

	@Bean
	public Submodel getSubmodel(GenericSubmodelFactory factory) {
		return factory.create();
	}
	
	@Bean
	@Primary
	public SubmodelServiceFactory getSubmodelServiceFactory(SubmodelServiceFactory smFactory, InvokableFactory iFactory) {		
		Function<String, OperationInvokation> invokation = iFactory.createInvokableProvider();
		return new GenericOperationSubmodelServiceFactory(smFactory, invokation);
	} 
	
	private static class GenericOperationSubmodelServiceFactory implements SubmodelServiceFactory {
		
		private final SubmodelServiceFactory decorated;
		private final Function<String, OperationInvokation> invokableProvider;
				
		public GenericOperationSubmodelServiceFactory(SubmodelServiceFactory factory, Function<String, OperationInvokation> invokableProvider) {
			this.decorated = factory;
			this.invokableProvider = invokableProvider;
		}
		
		@Override
		public SubmodelService create(Submodel submodel) {
			return new GenericOperationSubmodelService(decorated.create(submodel), invokableProvider);
		}
	}
}
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
package org.eclipse.digitaltwin.basyx.submodelservice.feature.operationdispatching;

import org.eclipse.digitaltwin.basyx.submodelservice.SubmodelServiceFactory;
import org.eclipse.digitaltwin.basyx.submodelservice.feature.SubmodelServiceFeature;
import org.eclipse.digitaltwin.basyx.submodelservice.feature.operationdispatching.execution.OperationExecutorProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @author Gerhard Sonnenberg DFKI GmbH
 */
@ConditionalOnProperty(name = OperationDispatchingSubmodelServiceFeature.FEATURENAME +".enabled", havingValue = "true", matchIfMissing = false)
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class OperationDispatchingSubmodelServiceFeature implements SubmodelServiceFeature {

	public final static String FEATURENAME = "basyx.submodelservice.feature.operation.dispatcher";

	@Value("${" + FEATURENAME + ".enabled:false}")
	private boolean enabled;
	
	private OperationExecutorProvider provider;

	@Autowired
	public OperationDispatchingSubmodelServiceFeature(OperationExecutorProvider provider) {
		this.provider = provider;
	}

	@Override
	public SubmodelServiceFactory decorate(SubmodelServiceFactory component) {
		return new OperationDispatchingServiceFactory(component, provider);
	}

	@Override
	public void initialize() {
	}

	@Override
	public void cleanUp() {

	}

	@Override
	public String getName() {
		return "SubmodelService reflection based operation dispatcher";
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}
}

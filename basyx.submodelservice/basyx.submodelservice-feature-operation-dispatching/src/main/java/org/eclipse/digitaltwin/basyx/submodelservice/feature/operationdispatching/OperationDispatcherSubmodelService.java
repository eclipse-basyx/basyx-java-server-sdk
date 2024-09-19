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

import org.eclipse.digitaltwin.aas4j.v3.model.Operation;
import org.eclipse.digitaltwin.aas4j.v3.model.OperationVariable;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;
import org.eclipse.digitaltwin.basyx.submodelservice.SubmodelService;
import org.eclipse.digitaltwin.basyx.submodelservice.feature.operationdispatching.execution.OperationExecutor;
import org.eclipse.digitaltwin.basyx.submodelservice.feature.operationdispatching.execution.OperationExecutorProvider;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

/**
 * @author Gerhard Sonnenberg DFKI GmbH
 */
public class OperationDispatcherSubmodelService extends AbstractSubmodelServiceDecorator {

	private final OperationExecutorProvider provider;
	
	public OperationDispatcherSubmodelService(SubmodelService decorated, OperationExecutorProvider provider) {
		super(decorated);
		this.provider = provider;
	}
	
	@Override
	public OperationVariable[] invokeOperation(String idShortPath, OperationVariable[] input)
			throws ElementDoesNotExistException {
		SubmodelElement elem = getSubmodelElement(idShortPath);
		if (!(elem instanceof Operation)) {
			throw new ResponseStatusException(HttpStatusCode.valueOf(404));
		} 
		OperationExecutor executor = provider.getOperationExecutor(idShortPath);
		return executor.invoke(idShortPath, (Operation)elem, input);
	}
}
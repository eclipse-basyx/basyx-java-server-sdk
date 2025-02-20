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
package org.eclipse.digitaltwin.basyx.submodelrepository.feature.operationdispatching.java;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Map;

import org.eclipse.digitaltwin.aas4j.v3.model.OperationVariable;
import org.eclipse.digitaltwin.basyx.submodelrepository.feature.operationdispatching.java.ops.MockMappingOperation;
import org.eclipse.digitaltwin.basyx.submodelrepository.feature.operationdispatching.java.ops.MockOneArgMappingOperation;
import org.eclipse.digitaltwin.basyx.submodelservice.feature.operationdispatching.execution.OperationExecutor;
import org.eclipse.digitaltwin.basyx.submodelservice.feature.operationdispatching.java.DefaultInvokableOperation;
import org.eclipse.digitaltwin.basyx.submodelservice.feature.operationdispatching.java.ReflectionBasedOperationDispatcherProvider;
import org.eclipse.digitaltwin.basyx.submodelservice.feature.operationdispatching.java.ReflectionBasedOperationDispatcherProvider.MethodDispatcher;
import org.eclipse.digitaltwin.basyx.submodelservice.feature.operationdispatching.java.ReflectionBasedOperationDispatcherProvider.MultiArgMethodDispatcher;
import org.eclipse.digitaltwin.basyx.submodelservice.feature.operationdispatching.java.ReflectionBasedOperationDispatcherProvider.SingleArgMethodDispatcher;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

/**
 * @author Gerhard Sonnenberg DFKI GmbH
 */
public class ReflectionBasedOperationDispatcherProviderTest {

	@Test
	public void testDefaultMethodDispatcher() {
		URLClassLoader loader = new URLClassLoader(new URL[] {}, getClass().getClassLoader());
		ReflectionBasedOperationDispatcherProvider provider = new ReflectionBasedOperationDispatcherProvider(loader,
				Map.of(), DefaultInvokableOperation.class.getName());
		MethodDispatcher dispatcher = provider.getDefaultMethodDispatcher();
		Assert.assertNotNull(dispatcher);
		Assert.assertEquals(MultiArgMethodDispatcher.class, dispatcher.getClass());
	}

	@Test
	public void testDefaultMethodThrowsNotFound() {
		DefaultInvokableOperation op = new DefaultInvokableOperation();
		try {
			op.invoke("Test", null, null);
			Assert.fail();
		} catch (ResponseStatusException ex) {
			Assert.assertEquals(HttpStatusCode.valueOf(404), ex.getStatusCode());
		}
	}

	@Test
	public void testOneArgMethodDispatcher() {
		URLClassLoader loader = new URLClassLoader(new URL[] {}, getClass().getClassLoader());
		ReflectionBasedOperationDispatcherProvider provider = new ReflectionBasedOperationDispatcherProvider(loader,
				Map.of("OneArgOp", MockOneArgMappingOperation.class.getName()),
				DefaultInvokableOperation.class.getName());
		OperationExecutor dispatcher = provider.getDispatchingMappings().get("OneArgOp");
		Assert.assertNotNull(dispatcher);
		Assert.assertEquals(SingleArgMethodDispatcher.class, dispatcher.getClass());
		OperationVariable[] out = dispatcher.invoke("OneArgOp", null, null);
		OperationVariable expected = TestOperationValues
				.toOperationVariable(TestOperationValues.toStringProperty(MockOneArgMappingOperation.ONE_ARG));
		Assert.assertEquals(1, out.length);
		Assert.assertEquals(expected, out[0]);
	}
	
	@Test
	public void testMultiArgMethodDispatcher() {
		URLClassLoader loader = new URLClassLoader(new URL[] {}, getClass().getClassLoader());
		ReflectionBasedOperationDispatcherProvider provider = new ReflectionBasedOperationDispatcherProvider(loader,
				Map.of("MultiArgOp", MockMappingOperation.class.getName()),
				DefaultInvokableOperation.class.getName());
		OperationExecutor dispatcher = provider.getDispatchingMappings().get("MultiArgOp");
		Assert.assertNotNull(dispatcher);
		Assert.assertEquals(MultiArgMethodDispatcher.class, dispatcher.getClass());
		OperationVariable[] out = dispatcher.invoke("MultiArgOp", null, null);
		OperationVariable expected = TestOperationValues
				.toOperationVariable(TestOperationValues.toStringProperty("MultiArgOp"));
		Assert.assertEquals(1, out.length);
		Assert.assertEquals(expected, out[0]);
	}

}

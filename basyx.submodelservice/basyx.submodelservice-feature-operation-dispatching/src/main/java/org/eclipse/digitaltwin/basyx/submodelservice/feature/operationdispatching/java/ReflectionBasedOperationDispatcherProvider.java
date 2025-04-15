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
package org.eclipse.digitaltwin.basyx.submodelservice.feature.operationdispatching.java;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLClassLoader;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.eclipse.digitaltwin.aas4j.v3.model.Operation;
import org.eclipse.digitaltwin.aas4j.v3.model.OperationVariable;
import org.eclipse.digitaltwin.basyx.submodelservice.feature.operationdispatching.execution.OperationExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Gerhard Sonnenberg DFKI GmbH
 */
public class ReflectionBasedOperationDispatcherProvider {

	private final URLClassLoader loader;
	private final Map<String, OperationExecutor> assignedInvokations;
	private final MethodDispatcher defaultInvokation;

	private static final Logger LOGGER = LoggerFactory.getLogger(DynamicJavaClassLoader.class);

	public ReflectionBasedOperationDispatcherProvider(URLClassLoader loader, Map<String, String> mappings, String defaultClass) {
		if (defaultClass == null) {
			defaultClass = DefaultInvokableOperation.class.getName();
		}
		this.loader = loader;
		this.assignedInvokations = mappings.entrySet().stream().collect(Collectors.toMap(Entry::getKey, e->getMethodDispatcher(e.getValue())));
		this.defaultInvokation = getMethodDispatcher(defaultClass);
		mappings.forEach((k,v)->LOGGER.info("Operation class '{}' assigned for idShortPath '{}'.", v, k));
	}

	public MethodDispatcher getDefaultMethodDispatcher() {
		return defaultInvokation;
	}

	public Map<String, OperationExecutor> getDispatchingMappings() {
		return assignedInvokations;
	}

	private MethodDispatcher getMethodDispatcher(String clsName) {
		try {
			Class<?> cls = loader.loadClass(clsName);
			Constructor<?> constructor = cls.getConstructor();
			constructor.setAccessible(true);
			try {
				Method mtd = cls.getDeclaredMethod("invoke", String.class, Operation.class,
						OperationVariable.class.arrayType());
				mtd.setAccessible(true);
				return new MultiArgMethodDispatcher(constructor, mtd);
			} catch (NoSuchMethodException e) {
				Method mtd = cls.getDeclaredMethod("invoke", OperationVariable.class.arrayType());
				mtd.setAccessible(true);
				return new SingleArgMethodDispatcher(constructor, mtd);
			}
		} catch (SecurityException | NoSuchMethodException | ClassNotFoundException e) {
			throw new IllegalStateException("'invoke' Method not available.", e);
		}
	}
	
	
	public static abstract class MethodDispatcher implements OperationExecutor {

		protected final Constructor<?> constructor;
		protected final Method mtd;

		public MethodDispatcher(Constructor<?> constructor, Method mtd) {
			this.constructor = constructor;
			this.mtd = mtd;
		}		
	}

	public static class SingleArgMethodDispatcher extends MethodDispatcher {

		public SingleArgMethodDispatcher(Constructor<?> constructor, Method mtd) {
			super(constructor, mtd);
		}

		@Override
		public OperationVariable[] invoke(String path, Operation op, OperationVariable[] variables) {
			try {
				Object obj = constructor.newInstance();
				return (OperationVariable[]) mtd.invoke(obj, (Object) variables);
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
				throw new IllegalStateException("Failed to invoke operation", e);
			}
		}
	}

	public static class MultiArgMethodDispatcher extends MethodDispatcher {

		public MultiArgMethodDispatcher(Constructor<?> constructor, Method mtd) {
			super(constructor, mtd);
		}

		@Override
		public OperationVariable[] invoke(String path, Operation op, OperationVariable[] variables) {
			try {
				Object obj = constructor.newInstance();
				return (OperationVariable[]) mtd.invoke(obj, path, op, variables);
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
				throw new IllegalStateException("Failed to invoke operation", e);
			}
		}
	}
}
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
package org.eclipse.digitaltwin.basyx.submodelservice.component.java;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLClassLoader;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.eclipse.digitaltwin.aas4j.v3.model.Operation;
import org.eclipse.digitaltwin.aas4j.v3.model.OperationVariable;
import org.eclipse.digitaltwin.basyx.submodelservice.component.OperationInvokation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Gerhard Sonnenberg DFKI GmbH
 */
public class ReflectionBasedOperationHandlerSupport {

	private final URLClassLoader loader;
	private final Map<String, OperationInvokation> assignedInvokations;
	private final OperationInvokation defaultInvokation;

	private static final Logger LOGGER = LoggerFactory.getLogger(DynamicJavaClassLoader.class);

	public ReflectionBasedOperationHandlerSupport(URLClassLoader loader, Map<String, String> mappings, String defaultClass) {
		this.loader = loader;
		this.assignedInvokations = mappings.entrySet().stream().collect(Collectors.toMap(Entry::getKey, e->getMethodInvoker(e.getValue())));
		this.defaultInvokation = getMethodInvoker(defaultClass);
		mappings.forEach((k,v)->LOGGER.info("Operation class '{}' assigned for idShortPath '{}'.", v, k));
	}

	public OperationInvokation getDefaultMethodInvokation() {
		return defaultInvokation;
	}

	public Map<String, OperationInvokation> getAssignedInvokations() {
		return assignedInvokations;
	}

	private OperationInvokation getMethodInvoker(String clsName) {
		try {
			Class<?> cls = loader.loadClass(clsName);
			Constructor<?> constructor = cls.getConstructor();
			constructor.setAccessible(true);
			try {
				Method mtd = cls.getDeclaredMethod("invoke", String.class, Operation.class,
						OperationVariable.class.arrayType());
				mtd.setAccessible(true);
				return new MultiArgMethodInvoker(constructor, mtd);
			} catch (NoSuchMethodException e) {
				Method mtd = cls.getDeclaredMethod("invoke", OperationVariable.class.arrayType());
				mtd.setAccessible(true);
				return new SingleArgMethodInvoker(constructor, mtd);
			}
		} catch (SecurityException | NoSuchMethodException | ClassNotFoundException e) {
			throw new IllegalStateException("'invoke' Method not available.", e);
		}
	}

	public static class SingleArgMethodInvoker implements OperationInvokation {

		protected final Constructor<?> constructor;
		protected final Method mtd;

		public SingleArgMethodInvoker(Constructor<?> constructor, Method mtd) {
			this.constructor = constructor;
			this.mtd = mtd;
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

	public static class MultiArgMethodInvoker implements OperationInvokation {

		protected final Constructor<?> constructor;
		protected final Method mtd;

		public MultiArgMethodInvoker(Constructor<?> constructor, Method mtd) {
			this.constructor = constructor;
			this.mtd = mtd;
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
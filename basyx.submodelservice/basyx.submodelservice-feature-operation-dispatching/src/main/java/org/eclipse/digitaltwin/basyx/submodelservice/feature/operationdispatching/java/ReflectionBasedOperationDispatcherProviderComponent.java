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

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import org.eclipse.digitaltwin.basyx.submodelservice.feature.operationdispatching.OperationDispatcherMapping;
import org.eclipse.digitaltwin.basyx.submodelservice.feature.operationdispatching.OperationDispatchingSubmodelServiceFeature;
import org.eclipse.digitaltwin.basyx.submodelservice.feature.operationdispatching.execution.OperationExecutor;
import org.eclipse.digitaltwin.basyx.submodelservice.feature.operationdispatching.execution.OperationExecutorProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * @author Gerhard Sonnenberg DFKI GmbH
 */
@Component
@ConditionalOnProperty(name = OperationDispatchingSubmodelServiceFeature.FEATURENAME +".enabled", havingValue = "true")
public class ReflectionBasedOperationDispatcherProviderComponent implements OperationExecutorProvider {

	private final ReflectionBasedOperationDispatcherProvider handler;

	public ReflectionBasedOperationDispatcherProviderComponent(JavaInvokableDefinition definition, OperationDispatcherMapping conf)
			throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		DynamicJavaClassLoader loader = new DynamicJavaClassLoader();
		Path sources = definition.getSources();
		Path classes = definition.getClasses();
		List<Path> additionalClasspath = definition.getAdditionalClasspath();
		if (sources != null) {
			loader.compileClasses(sources, classes, additionalClasspath);
		}
		URLClassLoader urlClassLoader = loader.loadClasses(classes, additionalClasspath);
		handler = new ReflectionBasedOperationDispatcherProvider(urlClassLoader, conf.getMappings(),
				conf.getDefaultMapping());
	}
	
	@Override
	public OperationExecutor getOperationExecutor(String idShortPath) {
		Map<String, OperationExecutor> dispatcherMap = handler.getDispatchingMappings();		
		return dispatcherMap.getOrDefault(idShortPath, handler.getDefaultMethodDispatcher());
	}
}

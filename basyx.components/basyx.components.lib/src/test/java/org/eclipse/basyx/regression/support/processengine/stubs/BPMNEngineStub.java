/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.regression.support.processengine.stubs;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.Expression;
import org.camunda.bpm.engine.impl.el.FixedValue;
import org.eclipse.basyx.components.processengine.connector.DeviceServiceDelegate;


/**
 * BPMNStub that invokes the JavaDelegate through the reflection A test that
 * ensures the right parameters are given to the DeviceServiceExecutor
 * 
 * @author Zhang, Zai
 */
public class BPMNEngineStub {
	
	private Map<String, String> fieldInjections = new HashMap<>();
	
	private String classPath = "org.eclipse.basyx.components.processengine.connector.DeviceServiceDelegate";
	
	
	/**
	 * Constructor
	 * parameters are value of the injected fields in String
	 * @param serviceParameter all requested parameters in a serialized Json-String
	 * */
	public BPMNEngineStub(String serviceName, String serviceProvider, String serviceParameter, String submodelid) {
		fieldInjections.put("serviceName", serviceName);
		fieldInjections.put("serviceProvider", serviceProvider);
		fieldInjections.put("serviceParameter", serviceParameter);
		fieldInjections.put("submodelId", submodelid);
	}
	
	public void callJavaDelegate() throws Exception {
		// create the class of the java-delegate
		@SuppressWarnings("rawtypes")
		Class clazz = Class.forName(classPath);
		DeviceServiceDelegate delegate = (DeviceServiceDelegate) clazz.newInstance();

		// get all declare field of the class, including private fields	
		Field fields[] = clazz.getDeclaredFields();

		// set expected value defined in the hashMap to each fields
		for(Field f : fields) {
			if(fieldInjections.containsKey(f.getName())) {

				// get expected value
				String value = fieldInjections.get(f.getName());

				// create expression for this field
				Expression serviceName_expression = createExpressionObejct(value);

				// set the private field accesable
				f.setAccessible(true);

				// set value to the field
				f.set(delegate, serviceName_expression);
			}
		}

		//create execution-stub
		// DelegateExecution execution = new ExecutionImpl();
		DelegateExecution execution = new ProcessInstanceStub();

	    // call the delegate function
		delegate.execute(execution);
	}
	
	/**
	 * create the Expression-instance for the delegate 
	 * */
	private Expression createExpressionObejct(String value) {
		Expression ep = new FixedValue(value);
		return ep;
	}

}

/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.regression.processengineconnector;


import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.basyx.components.processengine.connector.DeviceServiceDelegate;
import org.eclipse.basyx.regression.support.processengine.stubs.BPMNEngineStub;
import org.eclipse.basyx.regression.support.processengine.stubs.DeviceServiceExecutorStub;
import org.eclipse.basyx.vab.coder.json.serialization.DefaultTypeFactory;
import org.eclipse.basyx.vab.coder.json.serialization.GSONTools;
import org.junit.Test;

/**
 * Test functionalities of the JavaDelegate invoked by the process engine for services calls
 * 
 * @author zhangzai
 * 
 * */
public class TestJavaDelegate {
	
	/**
	 * Create the serializer/deserializer
	 */
	GSONTools gson = new GSONTools(new DefaultTypeFactory());
	
	/**
	 * Name of the service "liftTo"
	 */
	private static final String SERVICE_LIFTTO = "liftTo";
	
	/**
	 * Name of the service "moveTo"
	 */
	private static final String SERVICE_MOVETO = "moveTo";
	
	/**
	 * Service provider 
	 */
	private static final String SERVICE_PROVIDER = "coilcar";
	
	/**
	 * Test the invocation of service "moveTo" through the java-delegate
	 * 
	 * @throws Exception
	 */
	@Test
	public void testMoveToCall() throws Exception {
		// service parameter
		Object params[] = new Object[]{5};
		
		// Create stub for BPMN-Engine for test purpose
		BPMNEngineStub bpmnstub = new BPMNEngineStub(SERVICE_MOVETO,SERVICE_PROVIDER,gson.serialize(new ArrayList<Object>(Arrays.asList(params))), "submodel1");
		
		// Set the service executor to the java-delegate
		DeviceServiceExecutorStub stub = new DeviceServiceExecutorStub();
		DeviceServiceDelegate.setDeviceServiceExecutor(stub);
		// deliver the service information to the java-delegate
		bpmnstub.callJavaDelegate();

		// Asset the java-delegate gets the information from the Engine-stub
		assertEquals(SERVICE_MOVETO, stub.getServiceName());
		assertEquals(SERVICE_PROVIDER, stub.getServiceProvider());
		assertArrayEquals(new Object[] { 5 }, stub.getParams().toArray());
	}
	
	/**
	 * Test the invocation of service "liftTo" through the java-delegate
	 * 
	 * @throws Exception
	 */
	@Test
	public void testLiftToCall() throws Exception {
		// service parameter
		Object params[] = new Object[]{123}; 
		
		// Create stub for BPMN-Engine for test purpose
		BPMNEngineStub bpmnstub = new BPMNEngineStub(SERVICE_LIFTTO,SERVICE_PROVIDER,gson.serialize(new ArrayList<Object>(Arrays.asList(params))), "submodel1"); 
		
		// Set the service executor to the java-delegate
		DeviceServiceExecutorStub stub = new DeviceServiceExecutorStub();
		DeviceServiceDelegate.setDeviceServiceExecutor(stub);
			
		// deliver the service information to the java-delegate
		bpmnstub.callJavaDelegate();

		// Asset the java-delegate gets the information from the Engine-stub
		assertEquals(SERVICE_LIFTTO, stub.getServiceName());
		assertEquals(SERVICE_PROVIDER, stub.getServiceProvider());
		assertArrayEquals(new Object[] { 123 }, stub.getParams().toArray());
			
	}

}

/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.components.device;

import org.eclipse.basyx.components.service.BaseBaSyxService;
import org.eclipse.basyx.models.controlcomponent.ExecutionState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;




/**
 * Base class for integrating devices with BaSys
 * 
 * This base class provides a simple framework for integrating devices with BaSys/BaSyx. It defines callback 
 * functions that are invoked by native devices, and that are used to communicate the device status. 
 *  
 * @author kuhn
 *
 */
public abstract class BaseDevice extends BaseBaSyxService implements IBaSysNativeDeviceStatus {
	
	/**
	 * Initiates a logger using the current class
	 */
	private static final Logger logger = LoggerFactory.getLogger(BaseDevice.class);
	
	/**
	 * Device interface function: (usually native code) indicates that device has
	 * been initialized
	 */
	@Override
	public void deviceInitialized() {
		// Indicate initialization to device
		onInitialize();
		
		// Change status
		statusChange(ExecutionState.IDLE.getValue());
	}
	
	
	/**
	 * Device interface function: (usually native code) indicates that device
	 * service is running
	 */
	@Override
	public void serviceRunning() {
		// Indicate service invocation to device
		onServiceInvocation();
		
		// Change status
		statusChange(ExecutionState.EXECUTE.getValue());
	}
	
	
	/**
	 * Device interface function: (usually native code) indicates that device
	 * service execution has completed
	 */
	@Override
	public void serviceCompleted() {
		// Indicate service invocation to device
		onServiceEnd();
		
		// Change status
		statusChange(ExecutionState.COMPLETE.getValue());		
	}

	
	/**
	 * Device interface function: (usually native code) indicates that device is
	 * ready again
	 */
	@Override
	public void resetCompleted() {
		// Indicate reset to device
		onReset();
		
		// Change status
		statusChange(ExecutionState.IDLE.getValue());		
	}



	/**
	 * Indicate device status change
	 */
	protected abstract void statusChange(String newStatus);
	
	

	
	/**
	 * Indicate device initialization
	 */
	protected void onInitialize() {
		// Here: Initialize device
		logger.debug("Device " + name + " status change: initialize");
	}

	
	/**
	 * Indicate device service invocation
	 */
	protected void onServiceInvocation() {
		// Here: Invoke device service
		logger.debug("Device " + name + " status change: invoke");
	}
	
	
	/**
	 * Indicate device service end
	 */
	protected void onServiceEnd() {
		// Here: Perform device operation after device service end (if necessary)
		logger.debug("Device " + name + " status change: end");
	}
	
	
	/**
	 * Indicate device reset
	 */
	protected void onReset() {
		// Here: Reset device
		logger.debug("Device " + name + " status change: reset");
	}
}


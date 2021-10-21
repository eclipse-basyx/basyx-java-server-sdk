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


/**
 * Base interface for integrating devices with BaSys
 * 
 * This base class interface defines a simple framework for integrating devices with BaSys/BaSyx. If the device has no control 
 * component, it defines interface methods that need to be called from sub classes or native code in order to communicate the 
 * device status to BaSys. If the device has a control component, the interface methods communicate the device status as 
 * response to device status requests from the control component.
 * 
 * Depending on the device, the operations may be called asynchronously, e.g. if the device is triggered by environment sensors,
 * or they may be called synchronously as response to signals to the device.
 * 
 * It resembles the following basic state machine
 *      OFF --(deviceInitialized){@literal -->} IDLE --(serviceRunning){@literal -->} RUNNING --(serviceCompleted){@literal -->} COMPLETE --(resetCompleted){@literal -->} IDLE 
 * 
 * @author kuhn
 *
 */
public interface IBaSysNativeDeviceStatus {

	
	/**
	 * Device interface function: (usually native code) indicates that device has been initialized and is ready now
	 */
	public void deviceInitialized();
	
	
	/**
	 * Device interface function: (usually native code) indicates that device service has started and is running
	 */
	public void serviceRunning();
	
	
	/**
	 * Device interface function: (usually native code) indicates that device service execution has completed
	 */
	public void serviceCompleted();

	
	/**
	 * Device interface function: (usually native code) indicates that device is ready again
	 */
	public void resetCompleted();
}


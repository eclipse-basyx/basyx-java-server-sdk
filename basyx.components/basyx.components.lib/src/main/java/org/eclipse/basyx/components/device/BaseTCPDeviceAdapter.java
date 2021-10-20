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

import org.eclipse.basyx.components.netcomm.TCPClient;




/**
 * Base class for integrating devices with BaSys
 * 
 * This base class provides a simple framework for integrating devices with BaSys/BaSyx. It implements a string
 * based communication and connects to a device manager. The class defines interface methods that need to be
 * called from sub classes or native code.
 * 
 * The device has no control component; the device decides itself when its service is executed. This happens 
 * e.g. due to sensor inputs or MES system request
 * 
 * @author kuhn
 *
 */
public abstract class BaseTCPDeviceAdapter extends BaseDevice implements IBaSysNativeDeviceStatus {

	
	/**
	 * Communication client
	 */
	protected TCPClient communicationClient = null;
	
	
	/**
	 * Store server port
	 */
	protected int serverPort;
	
	
	
	
	/**
	 * Constructor
	 */
	public BaseTCPDeviceAdapter(int port) {
		// Store server port
		serverPort = port;
	}
	
	
	
	
	/**
	 * Indicate device service invocation to device manager
	 */
	@Override
	protected void onServiceInvocation() {
		// Invoke base implementation
		super.onServiceInvocation();

		// Write bytes to device manager
		communicationClient.sendMessage("invocation:start\n");
	}
	
	
	/**
	 * Indicate device service end
	 */
	protected void onServiceEnd() {
		// Invoke base implementation
		super.onServiceEnd();
		
		// Write bytes to device manager
		communicationClient.sendMessage("invocation:end\n");
	}


	
	/**
	 * Indicate device status change to device manager
	 */
	@Override
	protected void statusChange(String newStatus) {
		// Write bytes to device manager
		communicationClient.sendMessage("status:"+newStatus+"\n");
	}
	
	
	/**
	 * Close device communication socket
	 */
	public void closeSocket() {
		// Close socket
		communicationClient.close();
	}


	/**
	 * Start the device
	 */
	@Override
	public void start() {
		// Invoke base implementation
		super.start();
		
		// Create connection
		communicationClient = new TCPClient("localhost", serverPort);
	}


	/**
	 * Stop the device
	 */
	@Override
	public void stop() {
		// Invoke base implementation
		super.stop();
		
		// Close communication socket
		closeSocket();
	}


	/**
	 * Wait for completion of server
	 */
	@Override
	public void waitFor() {
		// Do nothing
	}
}

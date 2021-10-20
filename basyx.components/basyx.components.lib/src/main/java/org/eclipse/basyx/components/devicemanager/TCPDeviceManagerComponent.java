/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.components.devicemanager;

import org.eclipse.basyx.components.netcomm.NetworkReceiver;
import org.eclipse.basyx.components.netcomm.TCPServer;



/**
 * Base class for device managers that communicate via TCP with the connected device
 * 
 * @author kuhn
 *
 */
public abstract class TCPDeviceManagerComponent extends DeviceManagerComponent implements NetworkReceiver {

	
	/**
	 * TCP port number
	 */
	protected int tcpPortNumber = -1;
	
	
	/**
	 * TCP server reference
	 */
	protected TCPServer tcpServer = null;
	
	
	/**
	 * TCP server thread
	 */
	protected Thread tcpServerThread = null;
	
	
	
	
	
	/**
	 * Constructor
	 */
	public TCPDeviceManagerComponent(int portNumber) {
		// Store port number
		tcpPortNumber = portNumber;
	}
	
	
	/**
	 * Run this service
	 */
	@Override
	public void start() {
		// Base implementation
		super.start();
		
		
		// Create TCP thread (or any other connection) to legacy device
		tcpServer = new TCPServer(tcpPortNumber);
		
		// Register this component as network receiver
		tcpServer.addTCPMessageListener(this);
		
		// Start TCP server
		tcpServerThread = new Thread(tcpServer);
		tcpServerThread.start();
	}
	
	
	/**
	 * Stop this service
	 */
	@Override
	public void stop() {
		// Base implementation
		super.stop();

		// End server
		tcpServer.closeServer();
		tcpServer.close();
	}

	
	/**
	 * Wait for completion of all servers
	 */
	@Override
	public void waitFor() {
		// Base implementation
		super.waitFor();
		
		// Wait for server
		try {tcpServerThread.join();} catch (InterruptedException e) {e.printStackTrace();}
	}
}


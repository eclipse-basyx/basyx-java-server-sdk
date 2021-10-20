/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.components.netcomm;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;



/**
 * Implements an NIO TCP server
 * 
 * This server supports both blocking and non-blocking operation. It manages a single communication stream to a client.
 * Communication messages consist of a 32 Bit value that describes message length (bytes), followed by message length bytes 
 * with payload. 
 * 
 * @author kuhn
 *
 */
public class TCPClient extends TCPCommunicator implements Runnable {

	
	/**
	 * Server port number
	 */
	protected int port = -1;
	
	
	/**
	 * Server name
	 */
	protected String serverName = null;
	
	
	/**
	 * Implements non-blocking semantics
	 */
	protected boolean isBlocking = true;
	
	
	
	
	/**
	 * Connect to server on local host
	 */
	public TCPClient(int portNo) {
		// Delegate to constructor
		this("localhost", portNo);
	}
	
	
	/**
	 * Connect to given server
	 */
	public TCPClient(String hostName, int portNo) {
		// Store port number
		port = portNo;
		
		// Catch communication errors
		try {
			// Resolve address of this host
			InetAddress hostIPAddress = InetAddress.getByName(hostName);

			// Create selector and socket channel
			//Selector      selector = Selector.open();
			// Channel to provider
			communicationToClient = SocketChannel.open();
			// - Setup channel: set to blocking and connect to provider
			communicationToClient.configureBlocking(true);
			communicationToClient.connect(new InetSocketAddress(hostIPAddress, port));
		} catch (IOException e) {
			// Output exception
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Make socket non blocking
	 */
	public void makeNonBlocking() {
		// Set my own blocking flag
		isBlocking = false;
		
		// Change server socket
		try {communicationToClient.configureBlocking(false);} catch (IOException e) {e.printStackTrace();}
	}
	
	
	/**
	 * Server main loop (for non-blocking communication)
	 */
	@Override
	public void run() {
		// Check if we are still using blocking communication
		if (!isBlocking) throw new RuntimeException("TCP client communication thread may only be used on blocking sockets.");
		
		// Wait for a connection
		while (true) {
			// Read data
			byte[] message = readMessage();
			
			// Notify listeners
			if (message != null) notifyListeners(message);
		}
	}
}


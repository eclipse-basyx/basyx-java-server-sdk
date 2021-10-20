/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.models.controlcomponent;

import org.eclipse.basyx.components.netcomm.NetworkReceiver;
import org.eclipse.basyx.components.netcomm.TCPServer;
import org.eclipse.basyx.vab.service.api.BaSyxService;





/**
 * Simplified control component for proxy implementations. The device communicates via TCP with this component.
 * 
 * This control component implements the following behavior
 * - It initially moves to 'idle' state
 * - Operation mode change is forwarded to device
 * - Only start/clear/reset commands are supported
 * - Implement a simplified model that only consists of states idle/execute/complete/aborted/stopped
 * - Device indicates abort transition with "abort" string, transition to complete state with "complete" string
 * 
 * @author kuhn
 *
 */
public class SimpleProxyControlComponent extends SimpleControlComponent implements NetworkReceiver, BaSyxService  {

	
	/**
	 * Version information for serialized instances 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * TCP server
	 */
	protected TCPServer tcpServer = null;
	
	
	/**
	 * Component name
	 */
	protected String componentName = null;
	
	
	/**
	 * Server thread
	 */
	protected Thread serverThread = null;
	
	
	/**
	 * Flag that indicates the request for ending the execution
	 */
	protected boolean endExecution = false;



	
	
	/**
	 * Constructor
	 */
	public SimpleProxyControlComponent(int serverPort) {
		// Create TCP server
		tcpServer = new TCPServer(serverPort);
		// - Register TCP server callback for received messages
		tcpServer.addTCPMessageListener(this);
	}


	/**
	 * Received message from TCP server
	 */
	@Override
	public void onReceive(byte[] message) {
		// Convert received message to string
		String rxMessage = TCPServer.toString(message);
		
		// For now, support "abort", "idle", and "complete" messages
		if (rxMessage.equalsIgnoreCase(buildMessage(ExecutionState.IDLE))) {this.setExecutionState(ExecutionState.IDLE.getValue()); return;}
		if (rxMessage.equalsIgnoreCase(buildMessage(ExecutionOrder.ABORT))) {this.setCommand(ExecutionOrder.ABORT.getValue().toLowerCase()); return;}
		if (rxMessage.equalsIgnoreCase(buildMessage(ExecutionState.COMPLETE))) {
			// Process message based on state
			if (this.getExecutionState().equalsIgnoreCase(ExecutionState.EXECUTE.getValue()))
				this.finishState();
			else
				throw new RuntimeException("Semantic error detected");

			// Return
			return;
		}
		if (rxMessage.equalsIgnoreCase(buildMessage(ExecutionOrder.RESET))) {
			// Process message based on state
			if (this.getExecutionState().equalsIgnoreCase(ExecutionOrder.COMPLETE.getValue()))
				this.put(ControlComponent.CMD, ExecutionOrder.RESET.getValue().toLowerCase());
			else
				throw new RuntimeException("Semantic error detected");

			// Return
			return;
		}
		
		
		// End state
		if (rxMessage.equalsIgnoreCase("finishState")) {
			this.finishState();

			// Return
			return;
		}
		
		// Indicate error: unexpected message
		throw new RuntimeException("Unexpected message received");
	}
	
	
	/**
	 * Indicate an execution state change
	 */
	@Override
	protected String filterExecutionState(String newExecutionState) {
		// Invoke base implementation
		super.filterExecutionState(newExecutionState);
		
		// Only continue if TCP server exists
		// - If constructor changes state, this will be invoked before TCP server is ready
		if (tcpServer == null) return newExecutionState;
		
		// Implement a simplified model that only consists of states idle/execute/complete/aborted/stopped
		switch (ExecutionState.byValue(newExecutionState)) {
			// Only process the following states, ignore all other state transitions
			case IDLE:      tcpServer.sendMessage(buildMessage(ExecutionState.IDLE));break;
			case EXECUTE:   tcpServer.sendMessage(buildMessage(ExecutionState.EXECUTE));break;
			case COMPLETE:  tcpServer.sendMessage(buildMessage(ExecutionState.COMPLETE));break;
			case ABORTED:   tcpServer.sendMessage(buildMessage(ExecutionState.ABORTED));break;
			case STOPPED:   tcpServer.sendMessage(buildMessage(ExecutionState.STOPPED));break;
			case RESETTING: tcpServer.sendMessage(buildMessage(ExecutionState.RESETTING));break;
			default: break;
		}
		
		// Return the unchanged execution state
		return newExecutionState;
	}
	
	
	/**
	 * Indicate operation mode change
	 */
	@Override
	public String filterOperationMode(String newOperationMode) {
		// Only continue if TCP server exists
		// - If constructor changes operation mode, this will be invoked before TCP server is ready
		if (tcpServer == null) return newOperationMode;

		// Communicate new operation mode to device
		tcpServer.sendMessage("opMode:"+newOperationMode);
		
		// Return the unchanged operation mode
		return newOperationMode;
	}


	/**
	 * Start this service
	 */
	@Override
	public void start() {
		// Create and start TCP server
		serverThread = new Thread(tcpServer);
		serverThread.start();
	}


	/**
	 * Staop this service
	 */
	@Override
	public void stop() {
		// Indicate requested end of execution
		endExecution = true;

		// End TCP communication
		tcpServer.close();
		tcpServer.closeServer();
		
		// End thread
		try {serverThread.join();} catch (InterruptedException e) {e.printStackTrace();}
	}


	/**
	 * Wait for end of service
	 */
	@Override
	public void waitFor() {
		// Wait for end of thread
		try {serverThread.join();} catch (InterruptedException e) {e.printStackTrace();}
	}


	/**
	 * Change service name
	 */
	@Override
	public BaSyxService setName(String newName) {
		// Store name
		componentName = newName;
		
		// Return this
		return this;
	}


	/**
	 * Return name
	 */
	@Override
	public String getName() {
		return componentName;
	}
	
	
	/**
	 * Check end execution flag of this service
	 */
	@Override
	public boolean hasEnded() {
		// Return end execution flag
		return endExecution;
	}
	
	private String buildMessage(ExecutionState state) {
		return "state:" + state.getValue().toLowerCase();
	}
	
	private String buildMessage(ExecutionOrder state) {
		return "state:" + state.getValue().toLowerCase();
	}
	
}



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
import org.eclipse.basyx.models.controlcomponent.ControlComponentChangeListener;
import org.eclipse.basyx.models.controlcomponent.ExecutionMode;
import org.eclipse.basyx.models.controlcomponent.ExecutionState;
import org.eclipse.basyx.models.controlcomponent.OccupationState;
import org.eclipse.basyx.models.controlcomponent.SimpleControlComponent;
import org.eclipse.basyx.vab.modelprovider.map.VABMapProvider;
import org.eclipse.basyx.vab.protocol.basyx.server.BaSyxTCPServer;




/**
 * Base class for device managers that communicate via TCP with the connected controllable device
 * 
 * @author kuhn
 *
 */
public abstract class TCPControllableDeviceManagerComponent extends TCPDeviceManagerComponent implements NetworkReceiver, ControlComponentChangeListener {

		
	/**
	 * Store control component server port
	 */
	protected int controlComponentServerPort = -1;
	
	
	/**
	 * BaSyx/TCP Server that exports the control component
	 */
	protected BaSyxTCPServer<VABMapProvider> server = null;

	
	/**
	 * Device control component
	 */
	protected SimpleControlComponent simpleControlComponent = null;

	
	
	/**
	 * Constructor
	 */
	public TCPControllableDeviceManagerComponent(int portNumber, int ctrlComponentServerPort) {
		// Base constructor
		super(portNumber);
		
		// Store control component server port
		controlComponentServerPort = ctrlComponentServerPort;
		
		// Create control component
		simpleControlComponent = new SimpleControlComponent(true);
		// - Register this component as event listener
		simpleControlComponent.addControlComponentChangeListener(this);
	}
	
	
	
	
	/**
	 * Received a string from network
	 */
	@Override
	public void onReceive(byte[] rxData) {
		// Do not process null values
		if (rxData == null) return;
		
		// Convert received data to string
		String rxStr = new String(rxData); 
		// - Trim string to remove possibly trailing and leading white spaces
		rxStr = rxStr.trim();
		
		// Check what was being received. This check is performed based on a prefix that he device has to provide);
		// - Device indicates completion of service
		if (hasPrefix(rxStr, "invocation:end")) {
			// Update control component with status
			simpleControlComponent.setExecutionState(ExecutionState.COMPLETE.getValue());
			
			// End processing
			return;
		}
	}

	
	
	
	
	/**
	 * Device control component indicates a variable change
	 */
	@Override
	public void onVariableChange(String varName, Object newValue) {
		// Do nothing
	}


	/**
	 * Device control component indicates an occupier change
	 */
	@Override
	public void onNewOccupier(String occupierId) {
		// Do nothing
	}


	/**
	 * Device control component indicates an occupation state change
	 */
	@Override
	public void onNewOccupationState(OccupationState state) {
		// Do nothing
	}


	/**
	 * Device control component indicates an execution mode change
	 */
	@Override
	public void onChangedExecutionMode(ExecutionMode newExecutionMode) {
		// Do nothing
	}


	/**
	 * Device control component indicates an execution state change
	 */
	@Override
	public void onChangedExecutionState(ExecutionState newExecutionState) {
		// Do not communicate "COMPLETE" execution state, as it is communicated from device via invocationEnd signal
		if (newExecutionState == ExecutionState.COMPLETE) return;
		
		// Communicate new execution state to device
		if (tcpServer != null) tcpServer.sendMessage("state:"+newExecutionState.getValue());
	}


	/**
	 * Device control component indicates an operation mode change
	 */
	@Override
	public void onChangedOperationMode(String newOperationMode) {
		// Communicate new operation mode to device
		if (tcpServer != null) tcpServer.sendMessage("opMode:"+newOperationMode);
	}


	/**
	 * Device control component indicates a work state change
	 */
	@Override
	public void onChangedWorkState(String newWorkState) {
		// Do nothing
	}


	/**
	 * Device control component indicates an error state change
	 */
	@Override
	public void onChangedErrorState(String newWorkState) {
		// Do nothing
	}
}


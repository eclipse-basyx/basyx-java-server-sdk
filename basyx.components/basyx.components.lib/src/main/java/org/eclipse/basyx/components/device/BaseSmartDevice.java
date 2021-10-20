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

import org.eclipse.basyx.models.controlcomponent.ControlComponent;
import org.eclipse.basyx.models.controlcomponent.ControlComponentChangeListener;
import org.eclipse.basyx.models.controlcomponent.ExecutionMode;
import org.eclipse.basyx.models.controlcomponent.ExecutionState;
import org.eclipse.basyx.models.controlcomponent.OccupationState;
import org.eclipse.basyx.models.controlcomponent.SimpleControlComponent;




/**
 * Base class for BaSys smart devices
 * 
 * This base class implements a control component for a smart device with a SimpleControlComponent instance
 *  
 * @author kuhn
 *
 */
public abstract class BaseSmartDevice extends BaseDevice implements ControlComponentChangeListener, IBaSysNativeDeviceStatus {

	
	/**
	 * Device control component
	 */
	protected ControlComponent controlComponent = null;

	/**
	 * Initializes BaseSmartDevice with a SimpleControlComponent
	 */
	public BaseSmartDevice() {
		// Create control component
		controlComponent = new SimpleControlComponent(true);
		// - Register this component as event listener
		controlComponent.addControlComponentChangeListener(this);
	}

	/**
	 * Initializes BaseSmartDevice with an arbitary {@link ControlComponent}
	 * 
	 * @param component
	 */
	public BaseSmartDevice(ControlComponent component) {
		controlComponent = component;
		component.addControlComponentChangeListener(this);
	}

	/**
	 * Start smart device
	 */
	@Override
	public void start() {
	}
	
	
	/**
	 * Get control component instance
	 */
	public ControlComponent getControlComponent() {
		// Return control component instance
		return controlComponent;
	}
	
	
	/**
	 * Indicate device status change
	 */
	@Override
	public void statusChange(String newStatus) {
		// Change control component execution status
		controlComponent.setExecutionState(newStatus);
	}

	
	
	
	/**
	 * Smart device control component indicates a variable change
	 */
	@Override
	public void onVariableChange(String varName, Object newValue) {
	}


	/**
	 * Smart device control component indicates an occupier change
	 */
	@Override
	public void onNewOccupier(String occupierId) {
	}


	/**
	 * Smart device control component indicates an occupation state change
	 */
	@Override
	public void onNewOccupationState(OccupationState state) {
	}


	/**
	 * Smart device control component indicates an execution mode change
	 */
	@Override
	public void onChangedExecutionMode(ExecutionMode newExecutionMode) {
	}


	/**
	 * Smart device control component indicates an execution state change
	 */
	@Override
	public void onChangedExecutionState(ExecutionState newExecutionState) {
		// Indicate service start in "Executing" state
		if (newExecutionState == ExecutionState.EXECUTE) this.onServiceInvocation();
	}


	/**
	 * Smart device control component indicates an operation mode change
	 */
	@Override
	public void onChangedOperationMode(String newOperationMode) {
	}


	/**
	 * Smart device control component indicates a work state change
	 */
	@Override
	public void onChangedWorkState(String newWorkState) {
	}


	/**
	 * Smart device control component indicates an error state change
	 */
	@Override
	public void onChangedErrorState(String newWorkState) {
	}
}


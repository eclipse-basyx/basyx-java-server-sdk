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


/**
 * A simplified implementation of a control component for devices that offer only basic services
 * 
 * @author kuhn
 *
 */
public class SimpleControlComponent extends ControlComponent {

	
	/**
	 * Version information for serialized instances 
	 */
	private static final long serialVersionUID = 1L;

		

	
	private boolean explicitResetFinished;

	/**
	 * Constructor
	 */
	public SimpleControlComponent() {
		this(false);
	}

	/**
	 * Constructs this control component so that it can be configured if the
	 * resetting stage should finalize on its own or if it needs another
	 * confirmation through {@link ControlComponent#finishState}
	 * 
	 * @param explicitResetFinished
	 */
	public SimpleControlComponent(boolean explicitResetFinished) {
		this.explicitResetFinished = explicitResetFinished;

		// Initial execution state
		setExecutionState("idle");
	}

	
	
	/**
	 * Indicate an execution state change
	 */
	@Override
	protected String filterExecutionState(String newExecutionState) {
		// Implement a simplified model that only consists of states idle/execute/complete/aborted/stopped
		switch (newExecutionState.toLowerCase()) {
			// Move from starting state directly to execute state after notifying the device
			case "starting": 
				return ExecutionState.EXECUTE.getValue();
				
			// Move from completing state directly to complete state
			case "completing":
				return ExecutionState.COMPLETE.getValue();

			// Move from resetting state directly to idle state
			case "resetting":
			if (explicitResetFinished) {
				break;
			} else {
				return ExecutionState.IDLE.getValue();
			}

			// Move from aborting state directly to aborted state
			case "aborting":
				return ExecutionState.ABORTED.getValue();

			// Move from clearing state directly to stopped state
			case "clearing":
				return ExecutionState.STOPPED.getValue();
		}
		
		// Default behavior - leave execution state unchanged
		return newExecutionState;
	}

}


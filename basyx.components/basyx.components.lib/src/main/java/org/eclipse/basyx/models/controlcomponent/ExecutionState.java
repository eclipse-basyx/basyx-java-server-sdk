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
 * Execution state enum
 * 
 * @author kuhn
 *
 */
public enum ExecutionState {
	// Enumeration constants
	IDLE("IDLE"), STARTING("STARTING"), EXECUTE("EXECUTE"), COMPLETING("COMPLETING"), COMPLETE("COMPLETE"), RESETTING("RESETTING"), HOLDING("HOLDING"), HELD("HELD"), UNHOLDING("UNHOLDING"), 
	SUSPENDING("SUSPENDING"), SUSPENDED("SUSPENDED"), UNSUSPENDING("UNSUSPENDING"), STOPPING("STOPPING"), STOPPED("STOPPED"), ABORTING("ABORTING"), ABORTED("ABORTED"), CLEARING("CLEARING");

	
	
	/**
	 * Get execution order by its value
	 */
	public static ExecutionState byValue(String value) {
		// Switch by requested value
		switch (value.toLowerCase()) {
			case "idle":         return IDLE;
			case "starting":     return STARTING;
			case "execute":      return EXECUTE;
			case "completing":   return COMPLETING;
			case "complete":     return COMPLETE;
			case "resetting":    return RESETTING;
			case "holding":      return HOLDING;
			case "held":         return HELD;
			case "unholding":    return UNHOLDING;
			case "suspending":   return SUSPENDING;
			case "suspended":    return SUSPENDED;
			case "unsuspending": return UNSUSPENDING;
			case "stopping":     return STOPPING;
			case "stopped":      return STOPPED;
			case "aborting":     return ABORTING;
			case "aborted":      return ABORTED;
			case "clearing":     return CLEARING;
		}
		
		// Indicate error
		throw new RuntimeException("Unknown value requested");
	}

	
	
	
	/**
	 * Enumeration item value
	 */
	protected String value = null;
	
	
	
	/**
	 * Constructor
	 */
	private ExecutionState(String val) {
		this.value = val;
	}
	
	
	/**
	 * Get enumeration value
	 */
	public String getValue() {
		return value;
	}
}

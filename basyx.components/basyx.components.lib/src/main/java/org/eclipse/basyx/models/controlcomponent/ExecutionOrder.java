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
 * Execution order enum
 * 
 * @author kuhn
 *
 */
public enum ExecutionOrder {
	// Enumeration constants
	START("START"), COMPLETE("COMPLETE"), RESET("RESET"), HOLD("HOLD"), UNHOLD("UNHOLD"), SUSPEND("SUSPEND"), UNSUSPEND("UNSUSPEND"), CLEAR("CLEAR"), STOP("STOP"), ABORT("ABORT");

	
	
	/**
	 * Get execution order by its value
	 */
	public static ExecutionOrder byValue(String value) {
		// Switch by requested value
		switch (value.toLowerCase()) {
			case "start":     return START;
			case "complete":  return COMPLETE;
			case "reset":     return RESET;
			case "hold":      return HOLD;
			case "unhold":    return UNHOLD;
			case "suspend":   return SUSPEND;
			case "unsuspend": return UNSUSPEND;
			case "clear":     return CLEAR;
			case "stop":      return STOP;
			case "abort":     return ABORT;
		}
		
		// Indicate error
		throw new RuntimeException("Unknown value requested:"+value.toLowerCase());
	}

	
	
	
	/**
	 * Enumeration item value
	 */
	protected String value = null;
	
	
	
	/**
	 * Constructor
	 */
	private ExecutionOrder(String val) {
		this.value = val;
	}
	
	
	/**
	 * Get enumeration value
	 */
	public String getValue() {
		return value;
	}
}

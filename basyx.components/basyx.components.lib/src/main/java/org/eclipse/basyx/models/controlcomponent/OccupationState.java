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
 * Occupation state enum
 * 
 * @author kuhn
 *
 */
public enum OccupationState {
	// Enumeration constants
	FREE(0), OCCUPIED(1), PRIORITY(2), LOCAL(3);

	
	
	/**
	 * Get OccupationState by its value
	 */
	public static OccupationState byValue(int value) {
		// Switch by requested value
		switch (value) {
			case 0: return FREE;
			case 1: return OCCUPIED;
			case 2: return PRIORITY;
			case 3: return LOCAL;
		}
		
		// Indicate error
		throw new RuntimeException("Unknown value requested");
	}

	
	
	
	/**
	 * Enumeration item value
	 */
	protected int value = -1;
	
	
	
	/**
	 * Constructor
	 */
	private OccupationState(int val) {
		this.value = val;
	}
	
	
	/**
	 * Get enumeration value
	 */
	public int getValue() {
		return value;
	}
}

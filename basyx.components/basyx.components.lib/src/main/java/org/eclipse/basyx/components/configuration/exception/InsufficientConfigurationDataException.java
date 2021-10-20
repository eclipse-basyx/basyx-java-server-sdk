/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.components.configuration.exception;


/**
 * indicate insufficient configuration data, i.e. important configuration properties have not been set when a configurable component is instantiated.
 * 
 * @author kuhn
 *
 */
public class InsufficientConfigurationDataException extends RuntimeException {

	
	/**
	 * Version of serialized instances
	 */
	private static final long serialVersionUID = 1L;

	
	
	
	/**
	 * Default constructor
	 */
	public InsufficientConfigurationDataException() {
		// Invoke base constructor
		super();
	}

	
	/**
	 * Constructor with additional message
	 */
	public InsufficientConfigurationDataException(String message) {
		// Invoke base constructor
		super(message);
	}
}


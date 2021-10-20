/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.tools.sqlproxy.exception;




/**
 * Indicate an unknown element type
 * 
 * @author kuhn
 *
 */
public class UnknownElementTypeException extends RuntimeException {

	
	/**
	 * Version number support for serialized instances
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Error message
	 */
	protected String errorMessage = null;
	
	
	
	
	/**
	 * Constructor
	 */
	public UnknownElementTypeException(String errorMsg) {
		errorMessage = errorMsg;
	}
}


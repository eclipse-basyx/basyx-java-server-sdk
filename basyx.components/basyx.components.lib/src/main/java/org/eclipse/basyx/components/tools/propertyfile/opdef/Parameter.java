/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.components.tools.propertyfile.opdef;



/**
 * Define a parameter tuple (name/type)
 * 
 * @author kuhn
 *
 */
public class Parameter {


	/**
	 * Parameter name
	 */
	protected String name;
	
	
	/**
	 * Parameter type
	 */
	protected String type;


	/**
	 * Constructor 
	 * 
	 * @param name  Parameter name
	 * @param type  Parameter type
	 */
	public Parameter(String name, String type) {
		this.name = name;
		this.type = type;
	}


	/**
	 * Return parameter name
	 * 
	 * @return parameter name
	 */
	public String getName() {
		return name;
	}


	/**
	 * Return parameter type
	 * 
	 * @return parameter type
	 */
	public String getType() {
		return type;
	}
}

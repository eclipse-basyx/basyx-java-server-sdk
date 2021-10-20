/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.components.configuration;

import org.eclipse.basyx.vab.protocol.api.IConnectorFactory;
import org.eclipse.basyx.vab.protocol.basyx.connector.BaSyxConnectorFactory;
import org.eclipse.basyx.vab.protocol.http.connector.HTTPConnectorFactory;



/**
 * Enumerate supported BaSyx protocol types
 * 
 * @author kuhn
 *
 */
public enum CFGBaSyxProtocolType {

	/**
	 * HTTP protocol
	 */
	HTTP(),
	
	/**
	 * BaSyx protocol
	 */
	BASYX();
	
	
	
	/**
	 * Return BaSyx protocol type by value
	 */
	public static CFGBaSyxProtocolType byValue(String cfgKey) {
		// Parse configuration key
		switch (cfgKey.toLowerCase()) {
			// Parse known protocols
			case "http":  return CFGBaSyxProtocolType.HTTP;
			case "basyx": return CFGBaSyxProtocolType.BASYX;
		
			// Unknown protocol
			default: return null;
		}
	}
	
	/**
	 * Create protocol instance
	 */
	public IConnectorFactory createInstance() {
		// Create protocol instance
		if (this.equals(HTTP))  return new HTTPConnectorFactory();
		if (this.equals(BASYX)) return new BaSyxConnectorFactory();
		
		// Unknown protocol
		return null;
	}
}



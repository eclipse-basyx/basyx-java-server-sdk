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
import org.eclipse.basyx.vab.registry.api.IVABRegistryService;




/**
 * Configure a server connection
 * 
 * @author kuhn
 *
 */
public class CFGBaSyxConnection {

	
	/**
	 * Protocol type
	 */
	protected CFGBaSyxProtocolType protocol = null;
	
	
	/**
	 * Directory type for this connection
	 */
	protected String directoryProviderName = null;
	
	
	
	
	/**
	 * Constructor
	 */
	public CFGBaSyxConnection() {
		// Do nothing
	}
	
	
	/**
	 * Set protocol type
	 * 
	 * @return CFGBaSyxConnection to support builder pattern
	 */
	public CFGBaSyxConnection setProtocol(CFGBaSyxProtocolType proto) {
		// Store protocol type
		protocol = proto;
		
		// Return 'this' instance
		return this;
	}
	
	
	/**
	 * Set directory
	 * 
	 * @return CFGBaSyxConnection to support builder pattern
	 */
	public CFGBaSyxConnection setDirectoryProvider(String providerName) {
		// Store protocol type
		directoryProviderName = providerName;
		
		// Return 'this' instance
		return this;		
	}
	
	
	
	/**
	 * Create protocol provider
	 */
	public IConnectorFactory createConnectorProvider() {
		// Create connector provider instance
		return protocol.createInstance();
	}
	
	
	/**
	 * Instantiate the directory class
	 */
	public IVABRegistryService createDirectoryInstance() {
		// Try to create instance
		try {
			// Get Java class by name
			Class<?> clazz = Class.forName(directoryProviderName);
		
			// Instantiate class
			IVABRegistryService directoryService = (IVABRegistryService) clazz.newInstance();
			
			// Return directory service instance
			return directoryService;
		} catch (IllegalAccessException | ClassNotFoundException | InstantiationException e) {
			// this is more or less fatal, so just inform the user
			e.printStackTrace();
		}
		
		// Return null pointer
		return null;
	}
}


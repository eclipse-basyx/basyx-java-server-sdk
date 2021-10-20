/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.components.configuration.builder;

import org.eclipse.basyx.aas.manager.ConnectedAssetAdministrationShellManager;
import org.eclipse.basyx.aas.registration.api.IAASRegistry;
import org.eclipse.basyx.aas.registration.proxy.AASRegistryProxy;
import org.eclipse.basyx.components.configuration.CFGBaSyxProtocolType;
import org.eclipse.basyx.components.configuration.ConfigurableComponent;
import org.eclipse.basyx.vab.manager.VABConnectionManager;
import org.eclipse.basyx.vab.protocol.http.connector.HTTPConnectorFactory;
import org.eclipse.basyx.vab.registry.api.IVABRegistryService;

/**
 * Configuration builder for BaSyx services
 * 
 * @author kuhn
 *
 */
public class BaSyxServiceConfigurationBuilder<T extends BaSyxServiceConfigurationBuilder<T>> extends BaSyxConfigurationBuilder<Void> {

	
	/**
	 * BaSyx registry URL
	 */
	protected String registryURL = null;
	
	
	/**
	 * BaSyx connection manager type
	 */
	protected CFGBaSyxProtocolType protocoltype = null;
	
	protected IVABRegistryService vabDirectory = null;
	
	
	/**
	 * Constructor
	 */
	public BaSyxServiceConfigurationBuilder(ConfigurableComponent<?> component) {
		// Set configured component
		this.setConfiguredComponent(component);
	}

	
	
	/**
	 * Set registry URL
	 */
	@SuppressWarnings("unchecked")
	public T registryURL(String url) {
		// Store registry URL
		registryURL = url;
		
		// Return 'this' reference
		return (T) this;
	}

	
	/**
	 * Create registry instance based on configuration
	 */
	public IAASRegistry getRegistry() {
		// Create and return registry
		return new AASRegistryProxy(registryURL);
	}
	

	
	/**
	 * Set connection manager type
	 */
	@SuppressWarnings("unchecked")
	public T connectionManagerType(CFGBaSyxProtocolType protocol) {
		// Store protocol type
		protocoltype = protocol;
		
		// Return 'this' reference
		return (T) this;
	}

	/**
	 * Set VAB Directory
	 */
	@SuppressWarnings("unchecked")
	public T directoryService(IVABRegistryService vabDirectory) {
		// Store VAB directory
		this.vabDirectory = vabDirectory;

		// Return 'this' Refence
		return (T) this;
	}

	/**
	 * Create connection manager based on configuration
	 */
	public VABConnectionManager getConnectionManager() {
		// Create and return VABConnectionManager
		return new VABConnectionManager(vabDirectory, new HTTPConnectorFactory());
	}

	/**
	 * Create connected AAS-manager based on configuration
	 */
	public ConnectedAssetAdministrationShellManager getConnetedAASManager() {
		// Create and return connected AAS-manager
		return new ConnectedAssetAdministrationShellManager(getRegistry(), new HTTPConnectorFactory());
	}
}


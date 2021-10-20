/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.components.registry.executable;

import org.eclipse.basyx.components.configuration.BaSyxContextConfiguration;
import org.eclipse.basyx.components.registry.RegistryComponent;
import org.eclipse.basyx.components.registry.configuration.BaSyxRegistryConfiguration;

/**
 * A registry executable for a registry with any backend.
 * 
 * @author espen
 */
public class RegistryExecutable {
	private RegistryExecutable() {
	}

	public static void main(String[] args) {
		// Load context configuration from default source
		BaSyxContextConfiguration contextConfig = new BaSyxContextConfiguration();
		contextConfig.loadFromDefaultSource();

		// Load registry configuration from default source
		BaSyxRegistryConfiguration registryConfig = new BaSyxRegistryConfiguration();
		registryConfig.loadFromDefaultSource();

		// Create and start component according to the configuration
		RegistryComponent component = new RegistryComponent(contextConfig, registryConfig);
		component.startComponent();
	}
}

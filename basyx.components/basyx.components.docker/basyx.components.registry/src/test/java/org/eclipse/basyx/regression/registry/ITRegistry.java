/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.regression.registry;

import org.eclipse.basyx.components.configuration.BaSyxContextConfiguration;
import org.eclipse.basyx.components.configuration.BaSyxDockerConfiguration;
import org.eclipse.basyx.registry.api.IRegistry;
import org.eclipse.basyx.registry.proxy.RegistryProxy;
import org.eclipse.basyx.testsuite.regression.registry.TestRegistryProviderSuite;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ITRegistry extends TestRegistryProviderSuite {
	private static Logger logger = LoggerFactory.getLogger(ITRegistry.class);

	private static String registryUrl;

	@BeforeClass
	public static void setUpClass() {
		logger.info("Running integration test...");

		logger.info("Loading servlet configuration");
		// Load the servlet configuration inside of the docker configuration from
		// properties file
		BaSyxContextConfiguration contextConfig = new BaSyxContextConfiguration();
		contextConfig.loadFromResource(BaSyxContextConfiguration.DEFAULT_CONFIG_PATH);

		// Load the docker environment configuration from properties file
		logger.info("Loading docker configuration");
		BaSyxDockerConfiguration dockerConfig = new BaSyxDockerConfiguration();
		dockerConfig.loadFromResource(BaSyxDockerConfiguration.DEFAULT_CONFIG_PATH);

		registryUrl = "http://localhost:" + dockerConfig.getHostPort() + contextConfig.getContextPath();
		logger.info("Registry URL for integration test: " + registryUrl);
	}

	@Override
	protected IRegistry getRegistryService() {
		return new RegistryProxy(registryUrl);
	}
}

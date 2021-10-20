/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.components.registry.servlet;

import org.eclipse.basyx.aas.registration.memory.AASRegistry;
import org.eclipse.basyx.aas.registration.restapi.AASRegistryModelProvider;
import org.eclipse.basyx.components.configuration.BaSyxMongoDBConfiguration;
import org.eclipse.basyx.components.registry.mongodb.MongoDBRegistryHandler;
import org.eclipse.basyx.vab.protocol.http.server.VABHTTPInterface;

/**
 * A registry servlet based on an SQL database. The servlet therefore provides an implementation
 * for the IAASRegistryService interface with a permanent storage solution.
 * 
 * @author espen
 */
public class MongoDBRegistryServlet extends VABHTTPInterface<AASRegistryModelProvider> {
	private static final long serialVersionUID = 1L;

	/**
	 * Provide HTTP interface with JSONProvider to handle serialization and
	 * SQLDirectoryProvider as backend
	 */
	public MongoDBRegistryServlet() {
		super(new AASRegistryModelProvider(new AASRegistry(new MongoDBRegistryHandler())));
	}

	public MongoDBRegistryServlet(BaSyxMongoDBConfiguration config) {
		super(new AASRegistryModelProvider(new AASRegistry(new MongoDBRegistryHandler(config))));
	}
}

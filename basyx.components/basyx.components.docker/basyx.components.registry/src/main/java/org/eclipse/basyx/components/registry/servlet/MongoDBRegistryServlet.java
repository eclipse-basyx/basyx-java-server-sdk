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
import org.eclipse.basyx.components.configuration.BaSyxMqttConfiguration;
import org.eclipse.basyx.components.registry.mongodb.MongoDBRegistry;
import org.eclipse.basyx.components.registry.mongodb.MongoDBRegistryHandler;
import org.eclipse.basyx.components.registry.mqtt.MqttRegistryFactory;
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
	 * Provide HTTP interface with JSONProvider and MongoDB as backend with default
	 * configuration
	 */
	public MongoDBRegistryServlet() {
		super(new AASRegistryModelProvider(new AASRegistry(new MongoDBRegistryHandler())));
	}

	/**
	 * Provide HTTP interface with JSONProvider and MongoDB as backend
	 */
	public MongoDBRegistryServlet(BaSyxMongoDBConfiguration config) {
		super(new AASRegistryModelProvider(new MongoDBRegistry(config)));
	}

	/**
	 * Provide HTTP interface with JSONProvider and MongoDB as storage and MQTT as
	 * event backend
	 */
	public MongoDBRegistryServlet(BaSyxMongoDBConfiguration mongoDBConfig, BaSyxMqttConfiguration mqttConfig) {
		super(new AASRegistryModelProvider(
				new MqttRegistryFactory().create(new MongoDBRegistry(mongoDBConfig), mqttConfig)));
	}
}

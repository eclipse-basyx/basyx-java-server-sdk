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

import org.eclipse.basyx.aas.registration.restapi.AASRegistryModelProvider;
import org.eclipse.basyx.components.configuration.BaSyxMqttConfiguration;
import org.eclipse.basyx.components.configuration.BaSyxSQLConfiguration;
import org.eclipse.basyx.components.registry.mqtt.MqttRegistryFactory;
import org.eclipse.basyx.components.registry.sql.SQLRegistry;
import org.eclipse.basyx.vab.protocol.http.server.VABHTTPInterface;

/**
 * A registry servlet based on an SQL database. The servlet therefore provides an implementation
 * for the IAASRegistryService interface with a permanent storage solution.
 * 
 * @author kuhn, pschorn, espen
 */
public class SQLRegistryServlet extends VABHTTPInterface<AASRegistryModelProvider> {
	private static final long serialVersionUID = 1L;

	/**
	 * Provide HTTP interface with JSONProvider and SQLDirectoryProvider as backend
	 * with default configuration
	 */
	public SQLRegistryServlet() {
		super(new AASRegistryModelProvider(new SQLRegistry()));
	}

	/**
	 * Provide HTTP interface with JSONProvider and SQLDirectoryProvider as backend
	 */
	public SQLRegistryServlet(BaSyxSQLConfiguration sqlConfig) {
		super(new AASRegistryModelProvider(new SQLRegistry(sqlConfig)));
	}

	/**
	 * Provide HTTP interface with JSONProvider and SQLDirectoryProvider as storage
	 * and MQTT as event backend
	 */
	public SQLRegistryServlet(BaSyxSQLConfiguration sqlConfig, BaSyxMqttConfiguration mqttConfig) {
		super(new AASRegistryModelProvider(new MqttRegistryFactory().create(new SQLRegistry(sqlConfig), mqttConfig)));
	}
}

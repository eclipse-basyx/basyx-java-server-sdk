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

import org.eclipse.basyx.aas.registration.api.IAASRegistry;
import org.eclipse.basyx.aas.registration.memory.AASRegistry;
import org.eclipse.basyx.components.registry.mongodb.MongoDBRegistryHandler;
import org.eclipse.basyx.testsuite.regression.aas.registration.TestRegistryProviderSuite;

/**
 * Test class for a local registry provider based on SQL tables
 * 
 * @author espen
 *
 */
public class TestMongoDBRegistry extends TestRegistryProviderSuite {

	@Override
	protected IAASRegistry getRegistryService() {
		return new AASRegistry(new MongoDBRegistryHandler("mongodb.properties"));
	}
}

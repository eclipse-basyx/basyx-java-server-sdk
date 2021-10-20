/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.regression.AASServer;

import static org.junit.Assert.assertEquals;

import org.eclipse.basyx.aas.manager.ConnectedAssetAdministrationShellManager;
import org.eclipse.basyx.aas.metamodel.api.IAssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.descriptor.ModelUrn;
import org.eclipse.basyx.aas.registration.api.IAASRegistry;
import org.eclipse.basyx.aas.registration.memory.InMemoryRegistry;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.vab.protocol.api.IConnectorFactory;
import org.eclipse.basyx.vab.protocol.http.connector.HTTPConnectorFactory;
import org.junit.Before;
import org.junit.Test;

/**
 * Suite for testing that the AAS Server component is set up correctly. The
 * tests here can be used by the component test itself and the integration test
 * 
 * @author espen
 *
 */
public abstract class AASServerSuite {
	protected IAASRegistry aasRegistry;
	protected ConnectedAssetAdministrationShellManager manager;

	protected String aasId = "testId";

	protected abstract String getURL();

	@Before
	public void setUp() {
		// Create a dummy registry to test integration of XML AAS
		aasRegistry = new InMemoryRegistry();

		// Create ConnectedAASManager
		IConnectorFactory connectorFactory = new HTTPConnectorFactory();
		manager = new ConnectedAssetAdministrationShellManager(aasRegistry, connectorFactory);
	}

	@Test
	public void testAddAAS() throws Exception {
		AssetAdministrationShell shell = new AssetAdministrationShell();
		IIdentifier identifier = new ModelUrn(aasId);
		shell.setIdentification(identifier);
		shell.setIdShort("aasIdShort");
		manager.createAAS(shell, getURL());

		IAssetAdministrationShell remote = manager.retrieveAAS(identifier);
		assertEquals(shell.getIdShort(), remote.getIdShort());
	}
}

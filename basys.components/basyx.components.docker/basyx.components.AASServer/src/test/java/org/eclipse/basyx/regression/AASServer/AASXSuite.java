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
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.eclipse.basyx.aas.manager.ConnectedAssetAdministrationShellManager;
import org.eclipse.basyx.aas.metamodel.connected.ConnectedAssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.descriptor.AASDescriptor;
import org.eclipse.basyx.aas.metamodel.map.descriptor.ModelUrn;
import org.eclipse.basyx.aas.metamodel.map.descriptor.SubmodelDescriptor;
import org.eclipse.basyx.aas.registration.api.IAASRegistry;
import org.eclipse.basyx.aas.registration.memory.InMemoryRegistry;
import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElement;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElementCollection;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.dataelement.IFile;
import org.eclipse.basyx.submodel.metamodel.connected.submodelelement.ConnectedSubmodelElementCollection;
import org.eclipse.basyx.submodel.metamodel.connected.submodelelement.dataelement.ConnectedFile;
import org.eclipse.basyx.vab.protocol.api.IConnectorFactory;
import org.eclipse.basyx.vab.protocol.http.connector.HTTPConnectorFactory;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Suite for testing that the XMLAAS servlet is set up correctly. The tests here
 * can be used by the servlet test itself and the integration test
 * 
 * @author schnicke, espen
 *
 */
public abstract class AASXSuite {
	private static Logger logger = LoggerFactory.getLogger(AASXSuite.class);

	protected IAASRegistry aasRegistry;

	protected static final String aasShortId = "Festo_3S7PM0CP4BD";
	protected static final ModelUrn aasId = new ModelUrn("smart.festo.com/demo/aas/1/1/454576463545648365874");
	protected static final ModelUrn smId = new ModelUrn("www.company.com/ids/sm/4343_5072_7091_3242");
	protected static final String smShortId = "Nameplate";

	// Has to be individualized by each test inheriting from this suite
	protected static String aasEndpoint;
	protected static String smEndpoint;
	protected static String rootEndpoint;

	private ConnectedAssetAdministrationShellManager manager;

	// create a REST client
	private Client client = ClientBuilder.newClient();

	/**
	 * Before each test, a dummy registry is created and an AAS is added in the
	 * registry
	 */
	@Before
	public void setUp() {
		// Create a dummy registry to test integration of XML AAS
		aasRegistry = new InMemoryRegistry();
		AASDescriptor descriptor = new AASDescriptor(aasShortId, aasId, aasEndpoint);
		descriptor.addSubmodelDescriptor(new SubmodelDescriptor(smShortId, smId, smEndpoint));
		aasRegistry.register(descriptor);

		// Create a ConnectedAssetAdministrationShell using a
		// ConnectedAssetAdministrationShellManager
		IConnectorFactory connectorFactory = new HTTPConnectorFactory();
		manager = new ConnectedAssetAdministrationShellManager(aasRegistry, connectorFactory);
	}

	@Test
	public void testGetSingleAAS() throws Exception {
		ConnectedAssetAdministrationShell connectedAssetAdministrationShell = getConnectedAssetAdministrationShell();
		assertEquals(aasShortId, connectedAssetAdministrationShell.getIdShort());
	}

	@Test
	public void testGetSingleSubmodel() throws Exception {
		ISubmodel subModel = getConnectedSubmodel();
		assertEquals(smShortId, subModel.getIdShort());
	}

	@Test
	public void testGetSingleModule() throws Exception {
		final String FILE_ENDING = "files/aasx/Nameplate/marking_rcm.jpg";
		final String FILE_PATH = rootEndpoint + "files/aasx/Nameplate/marking_rcm.jpg";
		checkFile(FILE_PATH);

		// Get the submdoel nameplate
		ISubmodel nameplate = getConnectedSubmodel();
		// Get the submodel element collection marking_rcm
		ConnectedSubmodelElementCollection marking_rcm = (ConnectedSubmodelElementCollection) nameplate.getSubmodelElements().get("Marking_RCM");
		Collection<ISubmodelElement> values = marking_rcm.getValue();

		// navigate to the File element
		Iterator<ISubmodelElement> iter = values.iterator();
		while (iter.hasNext()) {
			ISubmodelElement element = iter.next();
			if (element instanceof ConnectedFile) {
				ConnectedFile connectedFile = (ConnectedFile) element;
				// get value of the file element

				String fileurl = connectedFile.getValue();
				assertTrue(fileurl.endsWith(FILE_ENDING));
			}
		}
	}

	@Test
	public void testAllFiles() throws Exception {
		logger.info("Checking all files");
		ConnectedAssetAdministrationShell aas = getConnectedAssetAdministrationShell();
		logger.info("AAS idShort: " + aas.getIdShort());
		logger.info("AAS identifier: " + aas.getIdentification().getId());
		Map<String, ISubmodel> submodels = aas.getSubmodels();
		logger.info("# Submodels: " + submodels.size());
		for (ISubmodel sm : submodels.values()) {
			logger.info("Checking submodel: " + sm.getIdShort());
			checkElementCollectionFiles(sm.getSubmodelElements().values());
		}

	}

	private void checkElementCollectionFiles(Collection<ISubmodelElement> elements) {
		for (ISubmodelElement element : elements) {
			if (element instanceof IFile) {
				String fileUrl = ((IFile) element).getValue();
				checkFile(fileUrl);
			} else if (element instanceof ISubmodelElementCollection) {
				ISubmodelElementCollection col = (ISubmodelElementCollection) element;
				checkElementCollectionFiles(col.getSubmodelElements().values());
			}
		}
	}

	private void checkFile(String absolutePath) {
		// connect to the url of the aas
		WebTarget webTarget = client.target(absolutePath);
		logger.info("Checking file: " + absolutePath);
		Invocation.Builder invocationBuilder = webTarget.request();
		Response response = invocationBuilder.get();

		// validate the response
		assertEquals("Path check failed: " + absolutePath, 200, response.getStatus());
	}

	/**
	 * Gets the connected Asset Administration Shell
	 * 
	 * @return connected AAS
	 * @throws Exception
	 */
	private ConnectedAssetAdministrationShell getConnectedAssetAdministrationShell() throws Exception {
		return manager.retrieveAAS(aasId);
	}

	/**
	 * Gets the connected Submodel
	 * 
	 * @return connected SM
	 * @throws Exception
	 */
	private ISubmodel getConnectedSubmodel() {
		return manager.retrieveSubmodel(aasId, smId);
	}
}

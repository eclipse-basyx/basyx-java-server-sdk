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

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.servlet.ServletException;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.basyx.aas.aggregator.restapi.AASAggregatorProvider;
import org.eclipse.basyx.aas.metamodel.map.descriptor.AASDescriptor;
import org.eclipse.basyx.aas.metamodel.map.descriptor.ModelUrn;
import org.eclipse.basyx.aas.registration.memory.InMemoryRegistry;
import org.eclipse.basyx.aas.restapi.MultiSubmodelProvider;
import org.eclipse.basyx.components.aas.AASServerComponent;
import org.eclipse.basyx.components.aas.configuration.AASServerBackend;
import org.eclipse.basyx.components.aas.configuration.BaSyxAASServerConfiguration;
import org.eclipse.basyx.components.aas.executable.AASServerExecutable;
import org.eclipse.basyx.components.configuration.BaSyxContextConfiguration;
import org.eclipse.basyx.vab.modelprovider.VABPathTools;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * Test the AASComponent registration process
 * 
 * @author espen
 *
 */
public class TestAASXRegistration {
	protected static final String AAS_SHORTID = "Festo_3S7PM0CP4BD";
	protected static final ModelUrn AAS_ID = new ModelUrn("smart.festo.com/demo/aas/1/1/454576463545648365874");
	protected static final ModelUrn SM_ID = new ModelUrn("www.company.com/ids/sm/4343_5072_7091_3242");
	protected static final String SM_SHORTID = "Nameplate";
	protected static final String AASXPATH = "aasx/01_Festo.aasx";

	protected InMemoryRegistry registry = new InMemoryRegistry();
	protected String deployedEndpoint = "https://www.eclipse.org/basyx/test";
	protected AASServerComponent component;

	@Before
	public void setUp()
			throws ParserConfigurationException, SAXException, IOException, URISyntaxException, ServletException {
		BaSyxContextConfiguration contextConfig = createContextConfig();
		BaSyxAASServerConfiguration aasConfig = new BaSyxAASServerConfiguration(AASServerBackend.INMEMORY, AASXPATH, "", deployedEndpoint);
		startAASServerComponent(contextConfig, aasConfig);
	}

	private void startAASServerComponent(BaSyxContextConfiguration contextConfig, BaSyxAASServerConfiguration aasConfig) {
		component = new AASServerComponent(contextConfig, aasConfig);
		component.setRegistry(registry);
		component.startComponent();
	}

	private BaSyxContextConfiguration createContextConfig() throws URISyntaxException {
		BaSyxContextConfiguration contextConfig = new BaSyxContextConfiguration();
		contextConfig.loadFromResource(BaSyxContextConfiguration.DEFAULT_CONFIG_PATH);
		// Load the additional file path relative to the executed jar file
		String rootPath = new File(AASServerExecutable.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile().getPath();
		contextConfig.setDocBasePath(rootPath);
		return contextConfig;
	}

	@Test
	public void testAASHasBeenRegistered() {
		AASDescriptor aasDescriptor = registry.lookupAAS(AAS_ID);
		String descriptorAASShortId = aasDescriptor.getIdShort();
		assertEquals(AAS_SHORTID, descriptorAASShortId);
	}

	@Test
	public void testAASEndpointCorrect() {
		AASDescriptor aasDescriptor = registry.lookupAAS(AAS_ID);
		String descAASEndpoint = aasDescriptor.getFirstEndpoint();

		String expectedEndpoint = VABPathTools.concatenatePaths(deployedEndpoint, AASAggregatorProvider.PREFIX,
				AAS_ID.getEncodedURN(),
				MultiSubmodelProvider.AAS);
		System.out.println(expectedEndpoint);
		System.out.println(descAASEndpoint);
		assertEquals(expectedEndpoint, descAASEndpoint);
	}

	@After
	public void tearDown() {
		component.stopComponent();
	}
}



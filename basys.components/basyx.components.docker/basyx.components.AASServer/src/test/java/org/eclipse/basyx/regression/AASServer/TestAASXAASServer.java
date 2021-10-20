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

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.servlet.ServletException;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.basyx.aas.aggregator.restapi.AASAggregatorProvider;
import org.eclipse.basyx.components.aas.AASServerComponent;
import org.eclipse.basyx.components.aas.configuration.AASServerBackend;
import org.eclipse.basyx.components.aas.configuration.BaSyxAASServerConfiguration;
import org.eclipse.basyx.components.aas.executable.AASServerExecutable;
import org.eclipse.basyx.components.configuration.BaSyxContextConfiguration;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;
/**
 * Test accessing to AAS using basys aas SDK
 * 
 * @author zhangzai
 *
 */
public class TestAASXAASServer extends AASXSuite {
	private static Logger logger = LoggerFactory.getLogger(TestAASXAASServer.class);
	private static AASServerComponent component;

	@BeforeClass
	public static void setUpClass() throws ParserConfigurationException, SAXException, IOException, URISyntaxException, ServletException {
		// Setup component's test configuration
		BaSyxContextConfiguration contextConfig = new BaSyxContextConfiguration();
		contextConfig.loadFromResource(BaSyxContextConfiguration.DEFAULT_CONFIG_PATH);
		BaSyxAASServerConfiguration aasConfig = new BaSyxAASServerConfiguration(AASServerBackend.INMEMORY, "aasx/01_Festo.aasx");
		
		// Load the additional file path relative to the executed jar file
		String rootPath = new File(AASServerExecutable.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile().getPath();
		String docBasePath = rootPath;
		contextConfig.setDocBasePath(docBasePath);

		// Start the component
		component = new AASServerComponent(contextConfig, aasConfig);
		component.startComponent();
		
		rootEndpoint = "http://" + contextConfig.getHostname() + ":" + contextConfig.getPort() + "/"
				+ contextConfig.getContextPath() + "/";
		aasEndpoint = rootEndpoint + "/" + AASAggregatorProvider.PREFIX + "/" + aasId.getEncodedURN() + "/aas";
		smEndpoint = aasEndpoint + "/submodels/" + smShortId + "/submodel";
		logger.info("AAS URL for servlet test: " + aasEndpoint);
	}

	@AfterClass
	public static void tearDownClass() {
		component.stopComponent();
	}
}



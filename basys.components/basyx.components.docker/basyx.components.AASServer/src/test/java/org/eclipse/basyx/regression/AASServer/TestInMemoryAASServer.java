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

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.basyx.components.aas.AASServerComponent;
import org.eclipse.basyx.components.configuration.BaSyxContextConfiguration;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.xml.sax.SAXException;

/**
 * Tests the component using the test suite
 * 
 * @author schnicke
 *
 */
public class TestInMemoryAASServer extends AASServerSuite {

	private static AASServerComponent component;

	@Override
	protected String getURL() {
		return component.getURL() + "/shells";
	}

	@BeforeClass
	public static void setUpClass() throws ParserConfigurationException, SAXException, IOException {
		BaSyxContextConfiguration config = new BaSyxContextConfiguration();
		config.loadFromResource(BaSyxContextConfiguration.DEFAULT_CONFIG_PATH);

		component = new AASServerComponent(config);
		component.startComponent();
	}

	@AfterClass
	public static void tearDownClass() {
		component.stopComponent();
	}
}

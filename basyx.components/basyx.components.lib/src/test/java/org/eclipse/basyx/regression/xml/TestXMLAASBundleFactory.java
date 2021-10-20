/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.regression.xml;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.basyx.components.configuration.BaSyxConfiguration;
import org.eclipse.basyx.components.xml.XMLAASBundleFactory;
import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.support.bundle.AASBundle;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * Tests for the correct creation of AASBundles from an example XML
 * 
 * @author schnicke
 *
 */
public class TestXMLAASBundleFactory {

	private static final String xmlPath = "aas/factory/xml/in.xml";

	/**
	 * Checks if:<br/>
	 * <li>the expected AAS and Submodels are contained within the bundles</li>
	 * <li>the Submodels are assigned to the correct AAS</li>
	 * 
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 */
	@Test
	public void testBundleCreation() throws IOException, ParserConfigurationException, SAXException {
		XMLAASBundleFactory factory = new XMLAASBundleFactory(BaSyxConfiguration.getResourceString(xmlPath));

		Set<AASBundle> bundle = factory.create();

		// Retrieve both AAS and check for their presence
		Optional<AASBundle> fullAASBundleOptional = bundle.stream().filter(b -> b.getAAS().getIdentification().getId().equals("www.admin-shell.io/aas-sample/1/0")).findFirst();
		Optional<AASBundle> minimalBundleOptional = bundle.stream().filter(b -> b.getAAS().getIdentification().getId().equals("www.admin-shell.io/aas-sample/1/1")).findFirst();
		assertTrue(fullAASBundleOptional.isPresent());
		assertTrue(minimalBundleOptional.isPresent());

		// Check if the correct submodels are contained
		AASBundle fullAASBundle = fullAASBundleOptional.get();
		AASBundle minimalAASBundle = minimalBundleOptional.get();

		// Check full AAS
		Set<ISubmodel> fullAASSM = fullAASBundle.getSubmodels();
		assertTrue(fullAASSM.stream().anyMatch(s -> s.getIdentification().getId().equals("http://www.zvei.de/demo/submodel/12345679")));

		// Check minimal AAS
		Set<ISubmodel> minimalAASSM = minimalAASBundle.getSubmodels();
		assertTrue(minimalAASSM.isEmpty());
	}
}

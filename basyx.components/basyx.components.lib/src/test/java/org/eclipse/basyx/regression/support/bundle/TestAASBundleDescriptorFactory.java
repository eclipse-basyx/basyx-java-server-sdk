/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.regression.support.bundle;

import static org.junit.Assert.assertEquals;

import java.util.Collections;

import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.descriptor.AASDescriptor;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.identifier.Identifier;
import org.eclipse.basyx.support.bundle.AASBundle;
import org.eclipse.basyx.support.bundle.AASBundleDescriptorFactory;
import org.eclipse.basyx.vab.modelprovider.VABPathTools;
import org.junit.Test;

/**
 * Tests the methods of AASBundleDescriptorFactory for their correctness
 * 
 * @author schnicke
 *
 */
public class TestAASBundleDescriptorFactory {
	@Test
	public void testDescriptorCreation() {
		String aasId = "aasId";
		AssetAdministrationShell shell = new AssetAdministrationShell();
		shell.setIdentification(new Identifier(IdentifierType.CUSTOM, aasId));

		String smId = "smId";
		Submodel sm = new Submodel();
		sm.setIdShort(smId);
		sm.setIdentification(IdentifierType.IRI, "aasIdIRI");

		AASBundle bundle = new AASBundle(shell, Collections.singleton(sm));
		
		String basePath = "http://localhost:4040/test";		
		AASDescriptor desc = AASBundleDescriptorFactory.createAASDescriptor(bundle, basePath);
		
		String aasPath = VABPathTools.concatenatePaths(basePath, aasId, "aas");
		String smPath = VABPathTools.concatenatePaths(aasPath, "submodels", sm.getIdShort(), "submodel");
		assertEquals(aasPath, desc.getFirstEndpoint());
		assertEquals(smPath, desc.getSubmodelDescriptorFromIdShort(smId).getFirstEndpoint());

	}

}

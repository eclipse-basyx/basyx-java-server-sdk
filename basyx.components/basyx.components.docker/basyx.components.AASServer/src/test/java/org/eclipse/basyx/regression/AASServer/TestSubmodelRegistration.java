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

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.eclipse.basyx.aas.metamodel.map.descriptor.AASDescriptor;
import org.eclipse.basyx.aas.metamodel.map.descriptor.SubmodelDescriptor;
import org.eclipse.basyx.aas.registration.api.IAASRegistry;
import org.eclipse.basyx.aas.registration.memory.InMemoryRegistry;
import org.eclipse.basyx.components.aas.AASServerComponent;
import org.eclipse.basyx.components.aas.configuration.AASServerBackend;
import org.eclipse.basyx.components.aas.configuration.BaSyxAASServerConfiguration;
import org.eclipse.basyx.components.configuration.BaSyxContextConfiguration;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.map.identifier.Identifier;
import org.eclipse.basyx.vab.modelprovider.VABPathTools;
import org.junit.Test;

/**
 * Tests if AASServerComponent registered specified submodels
 * 
 * @author espen
 *
 */
public class TestSubmodelRegistration {
	private static AASServerComponent component;
	private static InMemoryRegistry registry;
	
	private static final IIdentifier SM_ID = new Identifier(IdentifierType.IRI, "http://www.zvei.de/demo/submodel/12345679");
	private static final String SM_IDSHORT = "submodel1";
	private static final IIdentifier AAS_ID = new Identifier(IdentifierType.IRI, "www.admin-shell.io/aas-sample/2/0");

	/**
	 * Tests if AASServerComponent registers only the submodels that are listed on the whitelist
	 */
	@Test
	public void testSubmodelRegistration() {
		registry = new InMemoryRegistry();
		registerInitialAAS(registry);

		component = createAASServerComponent(registry);
		component.startComponent();

		checkUpdatedSubmodelEndpoint(registry);

		component.stopComponent();
	}

	private void checkUpdatedSubmodelEndpoint(IAASRegistry registry) {
		List<AASDescriptor> aasDescriptors = registry.lookupAll();
		assertEquals(1, aasDescriptors.size());

		Collection<SubmodelDescriptor> smDescriptors = aasDescriptors.get(0).getSubmodelDescriptors();
		assertEquals(1, smDescriptors.size());

		SubmodelDescriptor smDescriptor = smDescriptors.iterator().next();
		assertEquals(1, smDescriptor.getEndpoints().size());

		String encodedAASId = VABPathTools.encodePathElement(AAS_ID.getId());
		String expectedEndpoint = VABPathTools.concatenatePaths("http://localhost:8080/", encodedAASId, "aas", "submodels", SM_IDSHORT, "submodel");
		assertEquals(expectedEndpoint, smDescriptor.getFirstEndpoint());
	}

	private void registerInitialAAS(IAASRegistry registry) {
		AASDescriptor aasDesc = createAASDescriptor();
		registry.register(aasDesc);
	}

	private AASDescriptor createAASDescriptor() {
		String encodedAASId = VABPathTools.encodePathElement(AAS_ID.getId());
		AASDescriptor aasDesc = new AASDescriptor(AAS_ID, "http://someRemoteLocation/aasList/" + encodedAASId);
		SubmodelDescriptor smDesc = new SubmodelDescriptor(SM_IDSHORT, SM_ID, "");
		aasDesc.addSubmodelDescriptor(smDesc);
		return aasDesc;
	}

	private AASServerComponent createAASServerComponent(IAASRegistry registry) {
		BaSyxContextConfiguration contextConfig = new BaSyxContextConfiguration(8080, "");
		BaSyxAASServerConfiguration aasConfig = new BaSyxAASServerConfiguration(AASServerBackend.INMEMORY, "xml/aas.xml");
		aasConfig.setSubmodels(Arrays.asList(SM_ID.getId()));
		AASServerComponent component = new AASServerComponent(contextConfig, aasConfig);
		component.setRegistry(registry);
		return component;
	}
}

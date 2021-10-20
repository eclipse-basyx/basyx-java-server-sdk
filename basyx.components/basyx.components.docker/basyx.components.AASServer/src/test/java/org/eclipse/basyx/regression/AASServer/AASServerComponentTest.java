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
import static org.junit.Assert.fail;

import java.util.List;

import org.eclipse.basyx.aas.metamodel.map.descriptor.AASDescriptor;
import org.eclipse.basyx.aas.metamodel.map.descriptor.SubmodelDescriptor;
import org.eclipse.basyx.aas.registration.memory.InMemoryRegistry;
import org.eclipse.basyx.components.aas.AASServerComponent;
import org.eclipse.basyx.components.aas.configuration.AASServerBackend;
import org.eclipse.basyx.components.aas.configuration.BaSyxAASServerConfiguration;
import org.eclipse.basyx.components.configuration.BaSyxContextConfiguration;
import org.eclipse.basyx.vab.exception.provider.ResourceNotFoundException;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests if AASServerComponent correctly deregisteres automatically registered AASs/SMs
 * 
 * @author conradi
 *
 */
public class AASServerComponentTest {

	
	private static AASServerComponent component;
	private static InMemoryRegistry registry;
	
	@BeforeClass
	public static void setUp() {
		// Setup component's test configuration
		BaSyxContextConfiguration contextConfig = new BaSyxContextConfiguration(8080, "");
		BaSyxAASServerConfiguration aasConfig = new BaSyxAASServerConfiguration(AASServerBackend.INMEMORY, "xml/aas.xml");
		
		// Create and start AASServer component
		component = new AASServerComponent(contextConfig, aasConfig);
		registry = new InMemoryRegistry();
		component.setRegistry(registry);
		component.startComponent();
	}
	
	/**
	 * Tests if AASServerComponent deregisters all AASs/SMs that it registered automatically on startup
	 */
	@Test
	public void testServerCleanup() {
		
		List<AASDescriptor> aasDescriptors = registry.lookupAll();
		assertEquals(2, aasDescriptors.size());
		
		component.stopComponent();
		
		// Try to lookup all previously registered AASs
		for(AASDescriptor aasDescriptor: aasDescriptors) {
			try {
				registry.lookupAAS(aasDescriptor.getIdentifier());
				fail();
			} catch (ResourceNotFoundException e) {
			}
			
			// Try to lookup all previously registered SMs
			for(SubmodelDescriptor smDescriptor: aasDescriptor.getSubmodelDescriptors()) {
				try {
					registry.lookupSubmodel(aasDescriptor.getIdentifier(), smDescriptor.getIdentifier());
					fail();
				} catch (ResourceNotFoundException e) {
				}
			}
		}
		
	}
}

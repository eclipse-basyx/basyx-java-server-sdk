/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.regression.processengineconnector;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.basyx.aas.manager.ConnectedAssetAdministrationShellManager;
import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.descriptor.AASDescriptor;
import org.eclipse.basyx.aas.metamodel.map.descriptor.SubmodelDescriptor;
import org.eclipse.basyx.aas.registration.api.IAASRegistry;
import org.eclipse.basyx.aas.registration.memory.InMemoryRegistry;
import org.eclipse.basyx.aas.restapi.AASModelProvider;
import org.eclipse.basyx.aas.restapi.MultiSubmodelProvider;
import org.eclipse.basyx.components.processengine.connector.DeviceServiceExecutor;
import org.eclipse.basyx.regression.support.processengine.aas.DeviceAdministrationShellFactory;
import org.eclipse.basyx.regression.support.processengine.stubs.CoilcarStub;
import org.eclipse.basyx.regression.support.processengine.submodel.DeviceSubmodelFactory;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.identifier.Identifier;
import org.eclipse.basyx.submodel.restapi.SubmodelProvider;
import org.eclipse.basyx.testsuite.regression.vab.gateway.ConnectorProviderStub;
import org.junit.Before;
import org.junit.Test;

/**
 * Test the service invocation using the service executor
 * 
 * @author zhangzai
 *
 */
public class TestAASServicecall {
	/**
	 * Service Executor to be tested, used by the process engine
	 */
	private DeviceServiceExecutor serviceExecutor;
	
	/**
	 * A stub for the service sub-model 
	 */
	private CoilcarStub coilcar;
	
	/**
	 * Id of the device (coilcar) aas
	 */
	private static final String AAS_ID = "coilcar";
	
	/**
	 * Id of the service submodel
	 */
	private static final String SUBMODEL_ID = "submodel1";
	
	/**
	 * Name of the service "liftTo"
	 */
	private static final String SERVICE_LIFTTO = "liftTo";
	
	/**
	 * Name of the service "moveTo"
	 */
	private static final String SERVICE_MOVETO = "moveTo";
	
	/**
	 * Setup the test environment, create aas and submodels, setup VAB connection
	 */
	@Before
	public void setupDeviceServiceExecutor() {
		// Create a device-aas for coilcar device with id "coilcar" and submodelid "submodel1"
		AssetAdministrationShell aas = new DeviceAdministrationShellFactory().create( AAS_ID, SUBMODEL_ID);
		
		// Create service stub instead of real coilcar services
		coilcar = new CoilcarStub();
		
		// Create the submodel of services provided by the coilcar with id "submodel1"
		Submodel sm = new DeviceSubmodelFactory().create(SUBMODEL_ID, coilcar);
		
		// Create VAB multi-submodel provider for holding the sub-models
		MultiSubmodelProvider provider = new MultiSubmodelProvider();
		
		// Add sub-model to the provider
		provider.addSubmodel(new SubmodelProvider(sm));
		
		// Add aas to the provider
		provider.setAssetAdministrationShell(new AASModelProvider(aas));
		
		// Create registry for aas
		IAASRegistry registry = new InMemoryRegistry();
		
		// Create aas descriptor
		IIdentifier id = new Identifier(IdentifierType.CUSTOM, AAS_ID);
		AASDescriptor aasDescriptor = new AASDescriptor(id, "/aas");
		
		// create submodel descriptor
		IIdentifier smId = new Identifier(IdentifierType.CUSTOM, SUBMODEL_ID);
		SubmodelDescriptor smDescriptor = new SubmodelDescriptor("submodel1Name", smId, "/aas/submodels/" + SUBMODEL_ID + "/submodel");
		
		// Add submodel descriptor to aas descriptor
		aasDescriptor.addSubmodelDescriptor(smDescriptor);
		
		// register this aas
		registry.register(aasDescriptor);

		// setup the connection-manager with the model-provider
		ConnectorProviderStub connectorProvider = new ConnectorProviderStub();
		connectorProvider.addMapping("", provider);
		
		// create the service executor that calls the services using aas
		ConnectedAssetAdministrationShellManager manager = new ConnectedAssetAdministrationShellManager(registry,
				connectorProvider);
		serviceExecutor = new DeviceServiceExecutor(manager);
		
	}
	
	
	/**
	 * Test the service invocation of the service-executor 
	 */
	@Test
	public void testServicecall() {
		/* Execute the service "moveTo" on the device "coilcar", 
		 * the service is located in sub-model "submodel1" 
		 * and has a parameter 123*/
		serviceExecutor.executeService(SERVICE_MOVETO, AAS_ID, SUBMODEL_ID, new ArrayList<>(Arrays.asList( new Object[] {123})));
		
		// Validate the parameter and service name is delivered successfully to the device stub
		assertEquals(123, coilcar.getParameter());
		assertTrue(coilcar.getServiceCalled().equals(SERVICE_MOVETO));
		
		/* Execute the service "liftTo" on the device "coilcar", 
		 * the service is located in sub-model "submodel1" 
		 * and has a parameter 456*/
		serviceExecutor.executeService(SERVICE_LIFTTO, AAS_ID, SUBMODEL_ID, new ArrayList<>(Arrays.asList( new Object[] {456})));
		
		// Validate the parameter and service name is delivered successfully to the device stub
		assertEquals(456, coilcar.getParameter());
		assertTrue(coilcar.getServiceCalled().equals(SERVICE_LIFTTO));
		
		/* Execute the service "moveTo" on the device "coilcar", 
		 * the service is located in sub-model "submodel1" 
		 * and has a parameter 789*/
		serviceExecutor.executeService(SERVICE_MOVETO,  AAS_ID, SUBMODEL_ID, new ArrayList<>(Arrays.asList( new Object[] {789})));
		
		// Validate the parameter and service name is delivered successfully to the device stub
		assertEquals(789, coilcar.getParameter());
		assertTrue(coilcar.getServiceCalled().equals(SERVICE_MOVETO));
	}
}

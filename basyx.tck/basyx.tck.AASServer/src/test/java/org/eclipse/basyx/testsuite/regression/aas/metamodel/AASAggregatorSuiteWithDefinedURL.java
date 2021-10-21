/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.testsuite.regression.aas.metamodel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.basyx.aas.aggregator.api.IAASAggregator;
import org.eclipse.basyx.aas.aggregator.proxy.AASAggregatorProxy;
import org.eclipse.basyx.aas.bundle.AASBundle;
import org.eclipse.basyx.aas.factory.aasx.AASXToMetamodelConverter;
import org.eclipse.basyx.aas.manager.ConnectedAssetAdministrationShellManager;
import org.eclipse.basyx.aas.metamodel.api.IAssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.connected.ConnectedAssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.aas.registration.memory.InMemoryRegistry;
import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.connected.ConnectedSubmodel;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.identifier.Identifier;
import org.eclipse.basyx.testsuite.regression.aas.aggregator.AASAggregatorSuite;
import org.junit.Test;

/**
 * Instantiate a concrete test suite for AASAggregator from the abstract test
 * suite.
 * 
 * @author zhangzai
 *
 */
public class AASAggregatorSuiteWithDefinedURL extends AASAggregatorSuite {

	public static String url;
	private static final String aas1Id = "smart.festo.com/demo/aas/1/1/454576463545648365874";
	private static final String aas2Id = "www.admin-shell.io/aas-sample/1/1";

	/**
	 * A bundle of AAS extracted from the AASX package
	 */
	private Map<String, IAssetAdministrationShell> aasMap = new HashMap<>();
	private Set<AASBundle> aasBundles;

	@Override
	protected IAASAggregator getAggregator() {
		return new AASAggregatorProxy(url);
	}

	/**
	 * Fetch AAS from AASX package and create them on the server
	 * 
	 * @throws Exception
	 */
	@Test
	public void testMetaModel() throws Exception {
		// First argument is the server URL
		String serverHost = url;

		// Create a in-memory registry
		InMemoryRegistry registry = new InMemoryRegistry();

		ConnectedAssetAdministrationShellManager manager = new ConnectedAssetAdministrationShellManager(registry);

		// Get the AAS Bundle
		// Instantiate the aasx package manager
		String aasxPath = "aasx/01_Festo.aasx";
		AASXToMetamodelConverter packageManager = new AASXToMetamodelConverter(aasxPath);

		// Unpack the files referenced by the aas
		packageManager.unzipRelatedFiles();

		// Retrieve the aas from the package
		aasBundles = packageManager.retrieveAASBundles();

		// Create the AAS on the server
		aasBundles.forEach((x) -> {
			// Get the ID of the AAS
			AssetAdministrationShell aas = (AssetAdministrationShell) x.getAAS();
			IIdentifier aasid = aas.getIdentification();

			// Create the AAS on the server
			manager.createAAS(aas, serverHost);
			aasMap.put(aasid.getId(), aas);

			// create the Submodels
			x.getSubmodels().forEach(y -> {
				manager.createSubmodel(aasid, (Submodel) y);
			});

		});

		// Check the created AAS from the aasx package
		checkAAS(aas1Id, manager);
		checkAAS(aas2Id, manager);
	}

	/**
	 * Check whether the aas is created correctly
	 * 
	 * @param manager
	 * @throws Exception
	 */
	private void checkAAS(String aasid, ConnectedAssetAdministrationShellManager manager) throws Exception {
		IIdentifier aasIdentifier = new Identifier(IdentifierType.IRI, aasid);

		// Get the created AAS from the server
		IAASAggregator aasAggregator = getAggregator();
		ConnectedAssetAdministrationShell remoteAas = manager.retrieveAAS(aasIdentifier);

		// Get the expected aas from aas bundle
		IAssetAdministrationShell expected = aasMap.get(aasid);

		// compare the both aas
		AssetAdministrationShell localCopy = remoteAas.getLocalCopy();
		
		assertEquals(expected.getIdentification(), localCopy.getIdentification());
		assertEquals(expected.getAsset(), localCopy.getAsset());
		assertEquals(expected.getIdShort(), localCopy.getIdShort());
		assertEquals(expected.getIdentification(), localCopy.getIdentification());
		assertEquals(expected.getDescription(), localCopy.getDescription());
		
		// References can be built in different ways, therefore a direct comparison is not possible
		assertEquals(expected.getSubmodelReferences().size(), localCopy.getSubmodelReferences().size());

		// Get submodels from bundle
		AASBundle aasBundle = aasBundles.stream().filter(b -> b.getAAS().getIdentification().getId().equals(aasIdentifier.getId())).findFirst().get();
		aasBundle.getSubmodels().forEach(expectedSm -> {
				Map<String, ISubmodel> sms = manager.retrieveSubmodels(aasIdentifier);
				// get submodel from remote
				ConnectedSubmodel remote = (ConnectedSubmodel) sms.get(expectedSm.getIdShort());
				// compare the both submodels
				checkSM(expectedSm, remote.getLocalCopy());
			});

		
		// Delete the AAS
		aasAggregator.deleteAAS(aasIdentifier);

	}
	
	/**
	 * Checks whether two Submodels are equal
	 * 
	 * @param expected
	 * @param actual
	 */
	private void checkSM(ISubmodel expected, ISubmodel actual) {
		assertEquals(expected.getIdShort(), actual.getIdShort());
		assertEquals(expected.getIdentification(), actual.getIdentification());
		assertEquals(expected.getDescription(), actual.getDescription());
		assertEquals(expected.getReference(), actual.getReference());
		assertEquals(expected.getQualifiers(), actual.getQualifiers());
		assertEquals(expected.getSubmodelElements().size(), actual.getSubmodelElements().size());
		
		for(String id: expected.getSubmodelElements().keySet()) {
			assertTrue(actual.getSubmodelElements().containsKey(id));
		}
	}


}

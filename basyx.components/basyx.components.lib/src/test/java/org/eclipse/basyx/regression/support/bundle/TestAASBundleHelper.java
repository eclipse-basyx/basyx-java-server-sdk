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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.basyx.aas.aggregator.AASAggregator;
import org.eclipse.basyx.aas.aggregator.proxy.AASAggregatorProxy;
import org.eclipse.basyx.aas.aggregator.restapi.AASAggregatorProvider;
import org.eclipse.basyx.aas.metamodel.api.IAssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.aas.registration.api.IAASRegistry;
import org.eclipse.basyx.aas.registration.memory.InMemoryRegistry;
import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.facade.SubmodelElementMapCollectionConverter;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.identifier.Identifier;
import org.eclipse.basyx.submodel.restapi.SubmodelProvider;
import org.eclipse.basyx.support.bundle.AASBundle;
import org.eclipse.basyx.support.bundle.AASBundleHelper;
import org.eclipse.basyx.vab.modelprovider.VABElementProxy;
import org.eclipse.basyx.vab.modelprovider.api.IModelProvider;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for the AASBundelIntegrator
 * 
 * @author conradi
 *
 */
public class TestAASBundleHelper {

	private static final String AAS_ID = "TestAAS";
	private static final String SM_ID = "TestSM";
	
	
	private AASAggregatorProxy aggregator;
	private List<AASBundle> bundles;
	private AASAggregatorProvider provider;
	
	
	
	@Before
	public void init() {
		provider = new AASAggregatorProvider(new AASAggregator());
		aggregator = new AASAggregatorProxy(new VABElementProxy("/shells", provider));
		bundles = new ArrayList<>();		
	}
	
	/**
	 * This test loads an AAS and its two Submodels into the Aggregator,
	 * runs the integration with AAS and Submodels with the same IDs, but different content,
	 * checks if integration does NOT replace the models in the Aggregator. 
	 */
	@Test
	public void testIntegrationOfExistingAASAndSM() {
		AASBundle bundle = getTestBundle();
		bundles.add(bundle);
		
		// Load AAS and SM AASAggregator
		AssetAdministrationShell aas = (AssetAdministrationShell) bundle.getAAS();
		Set<ISubmodel> submodels = bundle.getSubmodels();
		Submodel sm = (Submodel) submodels.iterator().next();
		pushAAS(aas);
		pushSubmodel(sm, aas.getIdentification());
		
		assertFalse(AASBundleHelper.integrate(aggregator, bundles));
		checkAggregatorContent();
	}
	
	/**
	 * This test loads an AAS into the Aggregator,
	 * runs the integration with the AAS and a SM,
	 * checks if both is present in Aggregator afterwards. 
	 */
	@Test
	public void testIntegrationOfExistingAASAndNonexistingSM() {
		AASBundle bundle = getTestBundle();
		bundles.add(bundle);
		
		// Load only AAS into AASAggregator
		AssetAdministrationShell aas = (AssetAdministrationShell) bundle.getAAS();
		pushAAS(aas);
		
		assertTrue(AASBundleHelper.integrate(aggregator, bundles));
		checkAggregatorContent();
	}
	
	/**
	 * This test loads nothing into the Aggregator,
	 * runs the integration with the AAS and a SM,
	 * checks if both is present in Aggregator afterwards. 
	 */
	@Test
	public void testIntegrationOfNonexistingAASAndSM() {
		AASBundle bundle = getTestBundle();
		bundles.add(bundle);
		
		assertTrue(AASBundleHelper.integrate(aggregator, bundles));
		checkAggregatorContent();
	}
	
	/**
	 * This test loads nothing into the Aggregator,
	 * runs the integration with the AAS and a SM,
	 * checks if both is present in Aggregator afterwards. Furthermore,
	 * the AASAggregator has a registry for registering and resolving potential
	 * submodels.
	 */
	@Test
	public void testIntegrationOfNonexistingAASAndSMWithRegistry() {
		IAASRegistry registry = new InMemoryRegistry();
		provider = new AASAggregatorProvider(new AASAggregator(registry));
		aggregator = new AASAggregatorProxy(new VABElementProxy("/shells", provider));

		AASBundle bundle = getTestBundle();
		bundles.add(bundle);

		assertTrue(AASBundleHelper.integrate(aggregator, bundles));
		checkAggregatorContent();
	}

	@SuppressWarnings("unchecked")
	private void checkAggregatorContent() {
		IAssetAdministrationShell aas = aggregator.getAAS(new Identifier(IdentifierType.CUSTOM, AAS_ID));
		assertEquals(AAS_ID, aas.getIdShort());
		IModelProvider provider = aggregator.getAASProvider(new Identifier(IdentifierType.CUSTOM, AAS_ID));
		
		Submodel sm = SubmodelElementMapCollectionConverter.mapToSM(
				(Map<String, Object>) provider.getValue("/aas/submodels/" + SM_ID + "/" + SubmodelProvider.SUBMODEL));
		
		assertEquals(SM_ID, sm.getIdentification().getId());
	}
	
	private void pushAAS(AssetAdministrationShell aas) {
		aggregator.createAAS(aas);
	}
	
	private void pushSubmodel(Submodel sm, IIdentifier aasIdentifier) {
		provider.setValue("/" + AASAggregatorProvider.PREFIX + "/" + aasIdentifier.getId() + "/aas/submodels/" + sm.getIdShort(), sm);
	}
	
	private AASBundle getTestBundle() {
		Submodel sm = new Submodel();
		sm.setIdShort(SM_ID);
		sm.setIdentification(IdentifierType.CUSTOM, SM_ID);

		AssetAdministrationShell aas = new AssetAdministrationShell();
		aas.setIdentification(IdentifierType.CUSTOM, AAS_ID);
		aas.setIdShort(AAS_ID);
		aas.addSubmodel(sm);

		return new AASBundle(aas, new HashSet<>(Arrays.asList(sm)));
	}
}

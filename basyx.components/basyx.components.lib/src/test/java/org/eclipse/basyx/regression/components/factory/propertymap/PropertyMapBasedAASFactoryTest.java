/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/


package org.eclipse.basyx.regression.components.factory.propertymap;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.basyx.aas.metamodel.api.parts.asset.AssetKind;
import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.descriptor.CustomId;
import org.eclipse.basyx.aas.metamodel.map.parts.Asset;
import org.eclipse.basyx.components.factory.propertymap.PropertyMapBasedAASFactory;
import org.eclipse.basyx.components.factory.propertymap.PropertyMapConstants;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.junit.Test;

/**
 * 
 * @author schnicke
 *
 */
public class PropertyMapBasedAASFactoryTest {

	@Test
	public void testAASCreation() {
		String aas1IdShort = "aas1";
		String asset1Id = "asset1";
		String sm1Id = "sm1";

		Asset expectedAsset = buildAsset(asset1Id);
		Submodel expectedSubmodel = buildSubmodel(sm1Id);
		AssetAdministrationShell expected = buildAAS(aas1IdShort, expectedAsset, expectedSubmodel);
		Map<String, String> aasMap = buildAASMap(aas1IdShort, asset1Id, sm1Id);

		AssetAdministrationShell actual = new PropertyMapBasedAASFactory(buildSubmodelMapWithDummySubmodel(expectedSubmodel), buildAssetCollectionWithDummyAsset(expectedAsset)).create(aasMap);

		assertEquals(expected, actual);
	}

	/**
	 * Builds a Map containing an AAS's attributes
	 * 
	 * @param aasId
	 * @param assetId
	 * @param sm1Id
	 * @return
	 */
	public static Map<String, String> buildAASMap(String aasId, String assetId, String sm1Id) {
		Map<String, String> aasMap = new HashMap<>();
		aasMap.put(PropertyMapConstants.IDVALUE, aasId);
		aasMap.put(PropertyMapConstants.IDSHORT, getIdShortFromId(aasId));
		aasMap.put(PropertyMapBasedAASFactory.ASSET, assetId);
		aasMap.put(PropertyMapBasedAASFactory.SUBMODELS, sm1Id);

		return aasMap;
	}

	private static Submodel buildSubmodel(String sm1Id) {
		return new Submodel(getIdShortFromId(sm1Id), new CustomId(sm1Id));
	}

	private static Asset buildAsset(String asset1Id) {
		return new Asset(getIdShortFromId(asset1Id), new CustomId(asset1Id), AssetKind.INSTANCE);
	}

	private static AssetAdministrationShell buildAAS(String id, Asset asset, Submodel sm) {
		AssetAdministrationShell shell = new AssetAdministrationShell(getIdShortFromId(id), new CustomId(id), asset);
		shell.addSubmodelReference(sm.getReference());
		return shell;
	}

	private static String getIdShortFromId(String id) {
		return id + "IdShort";
	}

	private static Map<String, Asset> buildAssetCollectionWithDummyAsset(Asset expected) {
		String dummyId = "dummyAsset";
		Asset dummy = buildAsset(dummyId);

		Map<String, Asset> assets = new HashMap<>();
		assets.put(dummyId, dummy);
		assets.put(expected.getIdentification().getId(), expected);

		return assets;
	}

	private static Map<String, Submodel> buildSubmodelMapWithDummySubmodel(Submodel submodel) {
		String dummyId = "dummySubmodel";
		Submodel dummy = buildSubmodel(dummyId);
		Map<String, Submodel> submodels = new HashMap<>();
		submodels.put(dummyId, dummy);
		submodels.put(submodel.getIdentification().getId(), submodel);

		return submodels;
	}

}

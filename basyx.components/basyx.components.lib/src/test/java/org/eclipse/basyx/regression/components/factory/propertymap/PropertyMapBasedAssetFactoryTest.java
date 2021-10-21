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
import org.eclipse.basyx.aas.metamodel.map.descriptor.CustomId;
import org.eclipse.basyx.aas.metamodel.map.parts.Asset;
import org.eclipse.basyx.components.factory.propertymap.PropertyMapBasedAssetFactory;
import org.eclipse.basyx.components.factory.propertymap.PropertyMapConstants;
import org.junit.Test;

/**
 * 
 * @author schnicke
 *
 */
public class PropertyMapBasedAssetFactoryTest {
	private static final String ASSET1ID = "asset1";

	@Test
	public void testAssetCreation() {
		Asset expected = buildAsset(ASSET1ID);
		Map<String, String> assetMap = buildAssetMap(ASSET1ID);

		Asset actual = new PropertyMapBasedAssetFactory().create(assetMap);

		assertEquals(expected, actual);
	}

	/**
	 * Builds a Map containing an Asset's attributes
	 * 
	 * @param id
	 * @return
	 */
	public static Map<String, String> buildAssetMap(String id) {
		Map<String, String> assetMap = new HashMap<>();
		assetMap.put(PropertyMapConstants.IDVALUE, id);
		assetMap.put(PropertyMapConstants.IDSHORT, getIdShortFromId(id));

		return assetMap;
	}

	private static Asset buildAsset(String id) {
		return new Asset(getIdShortFromId(id), new CustomId(id), AssetKind.INSTANCE);
	}

	private static String getIdShortFromId(String id) {
		return id + "IdShort";
	}

}

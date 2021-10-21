/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/


package org.eclipse.basyx.components.factory.propertymap;

import java.util.Map;

import org.eclipse.basyx.aas.metamodel.api.parts.asset.AssetKind;
import org.eclipse.basyx.aas.metamodel.map.descriptor.CustomId;
import org.eclipse.basyx.aas.metamodel.map.parts.Asset;

/**
 * Factory enabling {@link Asset} creation from a property map
 * 
 * @author schnicke
 *
 */
public class PropertyMapBasedAssetFactory {

	/**
	 * Creates an {@link Asset} based on the data contained in the property map<br>
	 * <br>
	 * Example:
	 * 
	 * <pre>
	 * { 
	 * 		"id": "assetId", 
	 * 		"idShort": "assetIdShort"
	 * }
	 * </pre>
	 * 
	 * @param assetMap
	 * @return
	 */
	public Asset create(Map<String, String> assetMap) {
		String assetId = PropertyMapConstantsHelper.getIdValue(assetMap);
		String assetIdShort = PropertyMapConstantsHelper.getIdShort(assetMap);

		return new Asset(assetIdShort, new CustomId(assetId), AssetKind.INSTANCE);
	}

}

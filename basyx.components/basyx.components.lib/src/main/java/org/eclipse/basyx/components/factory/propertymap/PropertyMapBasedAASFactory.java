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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.descriptor.CustomId;
import org.eclipse.basyx.aas.metamodel.map.parts.Asset;
import org.eclipse.basyx.components.propertymap.PropertyMapHelper;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;

/**
 * Factory enabling {@link AssetAdministrationShell} creation from a property
 * map
 * 
 * @author schnicke
 *
 */
public class PropertyMapBasedAASFactory {
	public static final String ASSET = "asset";
	public static final String SUBMODELS = "submodels";

	private Map<String, Asset> assetMap = new HashMap<>();
	private Map<String, Submodel> submodelMap = new HashMap<>();

	/**
	 * Creates a new factory instance using the passed map of Id to {@link Submodel}
	 * and {@link Asset}. The keys of these maps have to reflect the ids used in the
	 * AAS's property map.<br>
	 * <br>
	 * Example:
	 * 
	 * <pre>
	 * { 
	 * 		"id": "AASId",
	 * 		"idShort": "AASIdShort", 
	 * 		"value": "TestValue",
	 * 		"asset": "Asset",
	 * 		"submodels": "SM1, SM2"
	 * }
	 * </pre>
	 * 
	 * @param submodels
	 * @param assets
	 */
	public PropertyMapBasedAASFactory(Map<String, Submodel> submodels, Map<String, Asset> assets) {
		this.submodelMap = submodels;
		this.assetMap = assets;
	}

	/**
	 * Creates an {@link AssetAdministrationShell} based on the data contained in
	 * the property map
	 * 
	 * @param aasMap
	 * @return
	 */
	public AssetAdministrationShell create(Map<String, String> aasMap) {
		Asset asset = getAsset(aasMap);

		AssetAdministrationShell shell = createAASWithoutSubmodels(aasMap, asset);
		addSubmodelToAAS(aasMap, shell);

		return shell;
	}

	private void addSubmodelToAAS(Map<String, String> aasMap, AssetAdministrationShell shell) {
		List<Submodel> submodels = getSubmodels(aasMap);
		addSubmodelReferences(shell, submodels);
	}

	private static AssetAdministrationShell createAASWithoutSubmodels(Map<String, String> aasMap, Asset asset) {
		String aasId = PropertyMapConstantsHelper.getIdValue(aasMap);
		String aasIdShort = PropertyMapConstantsHelper.getIdShort(aasMap);
		AssetAdministrationShell shell = new AssetAdministrationShell(aasIdShort, new CustomId(aasId), asset);

		return shell;
	}

	private static void addSubmodelReferences(AssetAdministrationShell shell, List<Submodel> submodels) {
		submodels.stream().map(sm -> sm.getReference()).forEach(shell::addSubmodelReference);
	}

	private List<Submodel> getSubmodels(Map<String, String> aasMap) {
		return getSubmodelIds(aasMap).stream().map(id -> submodelMap.get(id)).collect(Collectors.toList());
	}

	private Asset getAsset(Map<String, String> aasMap) {
		String assetId = getAssetId(aasMap);

		return assetMap.get(assetId);
	}

	private static List<String> getSubmodelIds(Map<String, String> aasMap) {
		String submodels = aasMap.get(SUBMODELS);
		return PropertyMapHelper.getListFromStringList(submodels);
	}

	private static String getAssetId(Map<String, String> aasMap) {
		return aasMap.get(ASSET);
	}

}

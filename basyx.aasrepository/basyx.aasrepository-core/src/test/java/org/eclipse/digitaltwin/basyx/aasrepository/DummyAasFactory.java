/*******************************************************************************
 * Copyright (C) 2024 the Eclipse BaSyx Authors
 * 
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * 
 * SPDX-License-Identifier: MIT
 ******************************************************************************/


package org.eclipse.digitaltwin.basyx.aasrepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetInformation;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetKind;
import org.eclipse.digitaltwin.aas4j.v3.model.KeyTypes;
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.aas4j.v3.model.SpecificAssetId;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultAssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultAssetInformation;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultKey;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultReference;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSpecificAssetId;

public class DummyAasFactory {
	public static final String DUMMY_ID_SHORT = "dummyIdShort";
	public static final String AASWITHSUBMODELREF_ID = "aas1/s";
	public static final String AASWITHASSETINFORMATION_ID = "aas2";

	public static final String DUMMY_SUBMODEL_ID = "dummySubmodelId";

	public static List<AssetAdministrationShell> createShells() {
		return Arrays.asList(createAasWithSubmodelReference(), createAasWithAssetInformation());
	}
	
	public static List<AssetAdministrationShell> createShellsForFiltering() {
		List<AssetAdministrationShell> shellsWithSameIdShort = createShellsWithSameIdShort();
		List<AssetAdministrationShell> shellsWithSpecificAssetIds = createShellsWithSpecificAssetIds();
		List<AssetAdministrationShell> shells = createShells();
		
		List<AssetAdministrationShell> allShells = new ArrayList<AssetAdministrationShell>();
		allShells.addAll(shellsWithSameIdShort);
		allShells.addAll(shellsWithSpecificAssetIds);
		allShells.addAll(shells);
		
		return allShells;
	}
	
	public static List<AssetAdministrationShell> createShellsWithSameIdShort() {		
		return Arrays.asList(createShell("aasId1", DUMMY_ID_SHORT, null), getShellWithIdShortAndSpecificAssetId());
	}

	public static AssetAdministrationShell getShellWithIdShortAndSpecificAssetId() {
		List<SpecificAssetId> specificAssetIds = getSpecificAssetIdsForFiltering();
		
		return createShell("aasId2", DUMMY_ID_SHORT, specificAssetIds);
	}
	
	public static List<AssetAdministrationShell> createShellsWithSpecificAssetIds() {
		
		List<SpecificAssetId> specificAssetIds = getSpecificAssetIdsForFiltering();
		
		return Arrays.asList(createShell("aasId3", "idShort1", specificAssetIds), createShell("aasId4", "idShort2", specificAssetIds));
	}
	
	public static List<AssetAdministrationShell> getAllShellsWithSpecificAssetIds() {
		
		List<AssetAdministrationShell> allShells = new ArrayList<AssetAdministrationShell>();
		allShells.addAll(createShellsWithSpecificAssetIds());
		allShells.add(getShellWithIdShortAndSpecificAssetId());
		
		return allShells;
	}
	
	public static List<SpecificAssetId> getSpecificAssetIdsForFiltering() {
		return Arrays.asList(createSpecificAssetId("specificAssetId1_name", "specificAssetId1_value"), createSpecificAssetId("specificAssetId2_name", "specificAssetId2_value"));
	}
	
	private static SpecificAssetId createSpecificAssetId(String name, String value) {
		return new DefaultSpecificAssetId.Builder().name(name).value(value).build();
	}

	private static AssetAdministrationShell createShell(String id, String idShort, List<SpecificAssetId> specificAssetIds) {
		DefaultAssetAdministrationShell.Builder builder = new DefaultAssetAdministrationShell.Builder()
				.id(id).idShort(idShort);
		
		if (specificAssetIds != null) {
			AssetInformation assetInformation = createDummyAssetInformation();
			assetInformation.setSpecificAssetIds(specificAssetIds);
			builder.assetInformation(assetInformation);
		}
		
		return builder.build();
		
	}

	public static AssetAdministrationShell createAasWithSubmodelReference() {
		return new DefaultAssetAdministrationShell.Builder()
				.id(AASWITHSUBMODELREF_ID)
				.submodels(createDummyReference(DUMMY_SUBMODEL_ID))
				.build();
	}
	
	public static AssetAdministrationShell createAasWithAssetInformation() {
		return new DefaultAssetAdministrationShell.Builder()
				.id(AASWITHASSETINFORMATION_ID)
				.assetInformation(createDummyAssetInformation())
				.build();
	}
	
	public static Reference createDummyReference(String submodelId) {
		return new DefaultReference.Builder()
				.keys(new DefaultKey.Builder()
						.type(KeyTypes.SUBMODEL)
						.value(submodelId).build())
				.build();
	}
	
	public static AssetInformation createDummyAssetInformation() {
		return new DefaultAssetInformation.Builder()
				.assetKind(AssetKind.INSTANCE)
				.globalAssetId("assetIDTestKey")
				.build();
	}

}

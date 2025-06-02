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

import java.util.Arrays;
import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetInformation;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetKind;
import org.eclipse.digitaltwin.aas4j.v3.model.KeyTypes;
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.*;

public class DummyAasFactory {
	public static final String AASWITHSUBMODELREF_ID = "aas1/s";
	public static final String AASWITHASSETINFORMATION_ID = "aas2";

	public static final String DUMMY_SUBMODEL_ID = "dummySubmodelId";

	public static List<AssetAdministrationShell> createShells() {
		return Arrays.asList(createAasWithSubmodelReference(), createAasWithAssetInformation());
	}

	public static AssetAdministrationShell createAasWithSubmodelReference() {
		return new DefaultAssetAdministrationShell.Builder()
				.id(AASWITHSUBMODELREF_ID)
				.idShort("aasSubmodelRefs")
				.submodels(createDummyReference(DUMMY_SUBMODEL_ID))
				.build();
	}
	
	public static AssetAdministrationShell createAasWithAssetInformation() {
		return new DefaultAssetAdministrationShell.Builder()
				.id(AASWITHASSETINFORMATION_ID)
				.idShort("aasAssetInfo")
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
				.specificAssetIds(new DefaultSpecificAssetId.Builder().name("testSpecificAssetId")
						.value("testValue").build())
				.build();
	}

}

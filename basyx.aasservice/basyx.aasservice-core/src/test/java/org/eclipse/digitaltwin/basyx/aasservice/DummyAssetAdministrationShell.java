package org.eclipse.digitaltwin.basyx.aasservice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetKind;
import org.eclipse.digitaltwin.aas4j.v3.model.KeyTypes;
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultAssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultAssetInformation;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultKey;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultReference;

/**
 * Class containing a Dummy Shell for Unit testing
 * 
 * @author fried
 *
 */
public class DummyAssetAdministrationShell {
	
	public static final String AAS_ID = "arbitrary";
	public static final String AAS_ID_1 = "arbitraryAAS1";
	public static final String AAS_ID_2 = "arbitraryAAS2";
	public static final String AAS_ID_3 = "arbitraryAAS3";
	public static final String AAS_ID_4 = "arbitraryAAS4";
	public static final String SUBMODEL_ID = "DummySubmodelID";
	public static final String SUBMODEL_ID_1 = "DummyAssetId1";
	public static final String SUBMODEL_ID_2 = "DummyAssetId2";
	public static final String SUBMODEL_ID_3 = "DummyAssetId3";
	public static final String SUBMODEL_ID_4 = "DummyAssetId4";

	public static Reference submodelReference = buildDummyReference();

	public static AssetAdministrationShell getDummyShell() {
		return new DefaultAssetAdministrationShell.Builder().id(AAS_ID)
				.assetInformation(new DefaultAssetInformation.Builder().assetKind(AssetKind.INSTANCE)
						.globalAssetID(SUBMODEL_ID).build())
				.build();
	}
	
	public static AssetAdministrationShell createDummyShell1() {
		return new DefaultAssetAdministrationShell.Builder().id(AAS_ID_1)
				.assetInformation(new DefaultAssetInformation.Builder().assetKind(AssetKind.INSTANCE)
						.globalAssetID(SUBMODEL_ID_1).build())
				.build();
	}
	
	public static AssetAdministrationShell createDummyShell2() {
		return new DefaultAssetAdministrationShell.Builder().id(AAS_ID_2)
				.assetInformation(new DefaultAssetInformation.Builder().assetKind(AssetKind.TYPE)
						.globalAssetID(SUBMODEL_ID_2).build())
				.build();
	}
	
	public static AssetAdministrationShell createDummyShell3() {
		return new DefaultAssetAdministrationShell.Builder().id(AAS_ID_3)
				.assetInformation(new DefaultAssetInformation.Builder().assetKind(AssetKind.NOT_APPLICABLE)
						.globalAssetID(SUBMODEL_ID_3).build())
				.build();
	}
	
	public static AssetAdministrationShell createDummyShell4() {
		return new DefaultAssetAdministrationShell.Builder().id(AAS_ID_4)
				.assetInformation(new DefaultAssetInformation.Builder().assetKind(AssetKind.TYPE)
						.globalAssetID(SUBMODEL_ID_4).build()).submodels(DummySubmodelReference.getAllDummySubmodelReferences())
				.build();
	}

	/**
	 * Add the Dummy SubmodelReference to the given aas
	 * 
	 * @param aas
	 */
	public static void addDummySubmodelReference(AssetAdministrationShell aas) {
		List<Reference> submodelReferences = new ArrayList<>();
		submodelReferences.add(submodelReference);
		aas.setSubmodels(submodelReferences);
	}
	
	public static Collection<AssetAdministrationShell> getAllDummyAASs() {
		return Arrays.asList(getDummyShell(), createDummyShell1(), createDummyShell2(), createDummyShell3(), createDummyShell4());
	}

	private static Reference buildDummyReference() {
		return new DefaultReference.Builder()
				.keys(new DefaultKey.Builder().type(KeyTypes.SUBMODEL).value(SUBMODEL_ID).build()).build();
	}
}

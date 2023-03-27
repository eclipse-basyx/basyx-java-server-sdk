package org.eclipse.digitaltwin.basyx.aasservice;

import java.util.ArrayList;
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
	public static final String SUBMODEL_ID = "DummySubmodelID";

	public static Reference submodelReference = buildDummyReference();

	public static AssetAdministrationShell getDummyShell() {
		return new DefaultAssetAdministrationShell.Builder().id("arbitrary")
				.assetInformation(new DefaultAssetInformation.Builder().assetKind(AssetKind.INSTANCE)
						.globalAssetId(buildDummyReference()).build())
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

	private static Reference buildDummyReference() {
		return new DefaultReference.Builder()
				.keys(new DefaultKey.Builder().type(KeyTypes.SUBMODEL).value(SUBMODEL_ID).build()).build();
	}
}

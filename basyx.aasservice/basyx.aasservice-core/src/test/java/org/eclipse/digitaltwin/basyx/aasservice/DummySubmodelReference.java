package org.eclipse.digitaltwin.basyx.aasservice;

import static org.eclipse.digitaltwin.basyx.aasservice.DummyAssetAdministrationShell.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.KeyTypes;
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.aas4j.v3.model.ReferenceTypes;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultKey;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultReference;

/**
 * Class containing a Dummy Shell for Unit testing
 * 
 * @author fried
 *
 */
public class DummySubmodelReference {

	public static Reference submodelReference = buildDummyReference();

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
	
	/**
	 * Add the Dummy SubmodelReference to the given aas
	 * 
	 * @param aas
	 */
	public static void addAllDummySubmodelReferences(AssetAdministrationShell aas) {
		List<Reference> submodelReferences = new ArrayList<>();
		submodelReferences.addAll(getAllDummySubmodelReferences());
		aas.setSubmodels(submodelReferences);
	}

	public static Reference buildDummyReference() {
		return new DefaultReference.Builder()
				.keys(new DefaultKey.Builder().type(KeyTypes.SUBMODEL).value(SUBMODEL_ID).build()).type(ReferenceTypes.EXTERNAL_REFERENCE).build();
	}
	
	public static Reference buildDummyReference1() {
		return new DefaultReference.Builder()
				.keys(new DefaultKey.Builder().type(KeyTypes.SUBMODEL).value(SUBMODEL_ID_1).build()).type(ReferenceTypes.EXTERNAL_REFERENCE).build();
	}
	
	public static Reference buildDummyReference2() {
		return new DefaultReference.Builder()
				.keys(new DefaultKey.Builder().type(KeyTypes.SUBMODEL).value(SUBMODEL_ID_2).build()).type(ReferenceTypes.EXTERNAL_REFERENCE).build();
	}
	
	public static Reference buildDummyReference3() {
		return new DefaultReference.Builder()
				.keys(new DefaultKey.Builder().type(KeyTypes.SUBMODEL).value(SUBMODEL_ID_3).build()).type(ReferenceTypes.EXTERNAL_REFERENCE).build();
	}
	
	public static Reference buildDummyReference4() {
		return new DefaultReference.Builder()
				.keys(new DefaultKey.Builder().type(KeyTypes.SUBMODEL).value(SUBMODEL_ID_4).build()).type(ReferenceTypes.EXTERNAL_REFERENCE).build();
	}
	
	public static List<Reference> getAllDummySubmodelReferences() {
		return Arrays.asList(buildDummyReference(), buildDummyReference1(), buildDummyReference2(), buildDummyReference3(), buildDummyReference4());
	}
}

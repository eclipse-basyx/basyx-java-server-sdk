package org.eclipse.digitaltwin.basyx.aasrepository.feature.registry.integration;

import java.util.List;

import org.eclipse.digitaltwin.basyx.aasregistry.client.model.AdministrativeInformation;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.AssetKind;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.Extension;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.LangStringNameType;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.LangStringTextType;

public class AttributeMapper {

	public List<LangStringTextType> mapDescription(List<org.eclipse.digitaltwin.aas4j.v3.model.LangStringTextType> langStringTextTypes) {
		// String serializedLangString = "";
		// try {
		// serializedLangString = mapper.writeValueAsString(langStringTextTypes);
		// } catch (JsonProcessingException e) {
		// e.printStackTrace();
		// }
		//
		// List<LangStringTextType> langStringList = new
		// ArrayList<LangStringTextType>();
		//
		// try {
		// langStringList = mapper.readValue(serializedLangString, new
		// TypeReference<List<LangStringTextType>>() {
		// });
		// } catch (JsonProcessingException e) {
		// e.printStackTrace();
		// }
		//
		// return langStringList;

		CloneFactory<List<org.eclipse.digitaltwin.aas4j.v3.model.LangStringTextType>, List<LangStringTextType>> cloneFactory = new CloneFactory<>(langStringTextTypes);

		return cloneFactory.create();
	}

	public List<LangStringNameType> mapDisplayName(List<org.eclipse.digitaltwin.aas4j.v3.model.LangStringNameType> langStringTextTypes) {
		CloneFactory<List<org.eclipse.digitaltwin.aas4j.v3.model.LangStringNameType>, List<LangStringNameType>> cloneFactory = new CloneFactory<>(langStringTextTypes);

		return cloneFactory.create();
	}

	public List<Extension> mapExtensions(List<org.eclipse.digitaltwin.aas4j.v3.model.Extension> langStringTextTypes) {
		CloneFactory<List<org.eclipse.digitaltwin.aas4j.v3.model.Extension>, List<Extension>> cloneFactory = new CloneFactory<>(langStringTextTypes);

		return cloneFactory.create();
	}

	public AdministrativeInformation mapAdministration(org.eclipse.digitaltwin.aas4j.v3.model.AdministrativeInformation langStringTextTypes) {
		CloneFactory<org.eclipse.digitaltwin.aas4j.v3.model.AdministrativeInformation, AdministrativeInformation> cloneFactory = new CloneFactory<>(langStringTextTypes);

		return cloneFactory.create();
	}

	public AssetKind mapAssetKind(org.eclipse.digitaltwin.aas4j.v3.model.AssetKind langStringTextTypes) {
		
		return AssetKind.valueOf(AssetKind.class, langStringTextTypes.name());
		
//		CloneFactory<org.eclipse.digitaltwin.aas4j.v3.model.AssetKind, AssetKind> cloneFactory = new CloneFactory<>(langStringTextTypes);
//
//		return cloneFactory.create();
	}

}

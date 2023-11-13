package org.eclipse.digitaltwin.basyx.aasrepository.feature.registry.integration;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.digitaltwin.basyx.aasregistry.client.model.AdministrativeInformation;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.AssetKind;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.Extension;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.LangStringNameType;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.LangStringTextType;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AttributeMapper {
	
	private ObjectMapper mapper = new ObjectMapper();

	public List<LangStringTextType> mapDescription(List<org.eclipse.digitaltwin.aas4j.v3.model.LangStringTextType> langStringTextTypes) {
//		 String serializedLangString = "";
//		 try {
//		 serializedLangString = mapper.writeValueAsString(langStringTextTypes);
//		 } catch (JsonProcessingException e) {
//		 e.printStackTrace();
//		 }
//		
//		 List<LangStringTextType> langStringList = new
//		 ArrayList<LangStringTextType>();
//		
//		 try {
//		 langStringList = mapper.readValue(serializedLangString, new
//		 TypeReference<List<LangStringTextType>>() {
//		 });
//		 } catch (JsonProcessingException e) {
//		 e.printStackTrace();
//		 }
//		
//		 return langStringList;
//		mapper.getTypeFactory().constructCollectionType(List.class, LangStringTextType.class);

		CloneFactory<org.eclipse.digitaltwin.aas4j.v3.model.LangStringTextType, LangStringTextType> cloneFactory = new CloneFactory<>(LangStringTextType.class);

		return cloneFactory.create(langStringTextTypes);
	}

	public List<LangStringNameType> mapDisplayName(List<org.eclipse.digitaltwin.aas4j.v3.model.LangStringNameType> langStringTextTypes) {
		CloneFactory<org.eclipse.digitaltwin.aas4j.v3.model.LangStringNameType, LangStringNameType> cloneFactory = new CloneFactory<>(LangStringNameType.class);

		return cloneFactory.create(langStringTextTypes);
	}

	public List<Extension> mapExtensions(List<org.eclipse.digitaltwin.aas4j.v3.model.Extension> langStringTextTypes) {
		CloneFactory<org.eclipse.digitaltwin.aas4j.v3.model.Extension, Extension> cloneFactory = new CloneFactory<>(Extension.class);

		return cloneFactory.create(langStringTextTypes);
	}

	public AdministrativeInformation mapAdministration(org.eclipse.digitaltwin.aas4j.v3.model.AdministrativeInformation langStringTextTypes) {
		CloneFactory<org.eclipse.digitaltwin.aas4j.v3.model.AdministrativeInformation, AdministrativeInformation> cloneFactory = new CloneFactory<>(AdministrativeInformation.class);

		return cloneFactory.create(langStringTextTypes);
	}

	public AssetKind mapAssetKind(org.eclipse.digitaltwin.aas4j.v3.model.AssetKind langStringTextTypes) {
		
		return AssetKind.valueOf(AssetKind.class, langStringTextTypes.name());
		
//		CloneFactory<org.eclipse.digitaltwin.aas4j.v3.model.AssetKind, AssetKind> cloneFactory = new CloneFactory<>(langStringTextTypes);
//
//		return cloneFactory.create();
	}

}

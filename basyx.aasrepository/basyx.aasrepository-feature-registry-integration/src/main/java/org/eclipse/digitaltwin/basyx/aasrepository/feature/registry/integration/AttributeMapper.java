package org.eclipse.digitaltwin.basyx.aasrepository.feature.registry.integration;

import java.util.List;

import org.eclipse.digitaltwin.basyx.aasregistry.client.model.AssetKind;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.LangStringNameType;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.LangStringTextType;

public class AttributeMapper {

	public List<LangStringTextType> mapDescription(List<org.eclipse.digitaltwin.aas4j.v3.model.LangStringTextType> langStringTextTypes) {
		CloneFactory<org.eclipse.digitaltwin.aas4j.v3.model.LangStringTextType, LangStringTextType> cloneFactory = new CloneFactory<>(LangStringTextType.class);

		return cloneFactory.create(langStringTextTypes);
	}

	public List<LangStringNameType> mapDisplayName(List<org.eclipse.digitaltwin.aas4j.v3.model.LangStringNameType> langStringTextTypes) {
		CloneFactory<org.eclipse.digitaltwin.aas4j.v3.model.LangStringNameType, LangStringNameType> cloneFactory = new CloneFactory<>(LangStringNameType.class);

		return cloneFactory.create(langStringTextTypes);
	}

	public AssetKind mapAssetKind(org.eclipse.digitaltwin.aas4j.v3.model.AssetKind langStringTextTypes) {
		
		return AssetKind.valueOf(AssetKind.class, langStringTextTypes.name());
	}

}

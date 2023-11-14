package org.eclipse.digitaltwin.basyx.aasrepository.feature.mqtt;

import java.util.Arrays;
import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.model.AssetKind;
import org.eclipse.digitaltwin.aas4j.v3.model.LangStringNameType;
import org.eclipse.digitaltwin.aas4j.v3.model.LangStringTextType;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultLangStringNameType;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultLangStringTextType;

public class RegistryIntegrationTestHelper {

	// LangStringTextType AAS4J
	private static final LangStringTextType aas4jLangStringTextType_1 = new DefaultLangStringTextType.Builder().language("de").text("Ein Beispiel").build();
	private static final LangStringTextType aas4jLangStringTextType_2 = new DefaultLangStringTextType.Builder().language("en").text("An Example").build();

	// LangStringTextType AasRegistry
	private static final org.eclipse.digitaltwin.basyx.aasregistry.client.model.LangStringTextType aasRegLangStringTextType_1 = new org.eclipse.digitaltwin.basyx.aasregistry.client.model.LangStringTextType().language("de")
			.text("Ein Beispiel");
	private static final org.eclipse.digitaltwin.basyx.aasregistry.client.model.LangStringTextType aasRegLangStringTextType_2 = new org.eclipse.digitaltwin.basyx.aasregistry.client.model.LangStringTextType().language("en")
			.text("An Example");
	
	// LangStringNameType AAS4J
	private static final LangStringNameType AAS4J_LANG_STRING_NAME_TYPE_1 = new DefaultLangStringNameType.Builder().language("en").text("Name type string").build();
	private static final LangStringNameType AAS4J_LANG_STRING_NAME_TYPE_2 = new DefaultLangStringNameType.Builder().language("de").text("Namenstypzeichenfolge").build();
	
	// LangStringTextType AasRegistry
	private static final org.eclipse.digitaltwin.basyx.aasregistry.client.model.LangStringNameType AASREG_LANG_STRING_NAME_TYPE_1 = new org.eclipse.digitaltwin.basyx.aasregistry.client.model.LangStringNameType().language("en").text("Name type string");
	private static final org.eclipse.digitaltwin.basyx.aasregistry.client.model.LangStringNameType AASREG_LANG_STRING_NAME_TYPE_2 = new org.eclipse.digitaltwin.basyx.aasregistry.client.model.LangStringNameType().language("de").text("Namenstypzeichenfolge");
	
	// AssetKind AAS4J
	public static final AssetKind AAS4J_ASSET_KIND = AssetKind.INSTANCE;
	
	// AssetKind AasRegistry
	public static final org.eclipse.digitaltwin.basyx.aasregistry.client.model.AssetKind AASREG_ASSET_KIND = org.eclipse.digitaltwin.basyx.aasregistry.client.model.AssetKind.INSTANCE;

	public static List<LangStringTextType> getAas4jLangStringTextTypes() {
		return Arrays.asList(aas4jLangStringTextType_1, aas4jLangStringTextType_2);
	}

	public static List<org.eclipse.digitaltwin.basyx.aasregistry.client.model.LangStringTextType> getAasRegLangStringTextTypes() {
		return Arrays.asList(aasRegLangStringTextType_1, aasRegLangStringTextType_2);
	}
	
	public static List<LangStringNameType> getAas4jLangStringNameTypes() {
		return Arrays.asList(AAS4J_LANG_STRING_NAME_TYPE_1, AAS4J_LANG_STRING_NAME_TYPE_2);
	}

	public static List<org.eclipse.digitaltwin.basyx.aasregistry.client.model.LangStringNameType> getAasRegLangStringNameTypes() {
		return Arrays.asList(AASREG_LANG_STRING_NAME_TYPE_1, AASREG_LANG_STRING_NAME_TYPE_2);
	}

}

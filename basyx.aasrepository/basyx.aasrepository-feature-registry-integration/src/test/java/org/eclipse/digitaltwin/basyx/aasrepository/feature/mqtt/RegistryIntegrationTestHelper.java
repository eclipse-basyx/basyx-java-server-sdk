package org.eclipse.digitaltwin.basyx.aasrepository.feature.mqtt;

import java.util.Arrays;
import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.model.LangStringTextType;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultLangStringTextType;

public class RegistryIntegrationTestHelper {
	
	// LangStringTextType AAS4J 
	private static final LangStringTextType aas4jLangStringTextType_1 = new DefaultLangStringTextType.Builder().language("de").text("Ein Beispiel").build();
	private static final LangStringTextType aas4jLangStringTextType_2 = new DefaultLangStringTextType.Builder().language("en").text("An Example").build();
	
	// LangStringTextType AasRegistry
	private static final org.eclipse.digitaltwin.basyx.aasregistry.client.model.LangStringTextType aasRegLangStringTextType_1 = new org.eclipse.digitaltwin.basyx.aasregistry.client.model.LangStringTextType().language("de").text("Ein Beispiel");
	private static final org.eclipse.digitaltwin.basyx.aasregistry.client.model.LangStringTextType aasRegLangStringTextType_2 = new org.eclipse.digitaltwin.basyx.aasregistry.client.model.LangStringTextType().language("en").text("An Example");
	
	public static List<LangStringTextType> getAas4jLangStringTextTypes() {
		return Arrays.asList(aas4jLangStringTextType_1, aas4jLangStringTextType_2);
	}
	
	public static List<org.eclipse.digitaltwin.basyx.aasregistry.client.model.LangStringTextType> getAasRegLangStringTextTypes() {
		return Arrays.asList(aasRegLangStringTextType_1, aasRegLangStringTextType_2);
	}

}

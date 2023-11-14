package org.eclipse.digitaltwin.basyx.aasrepository.feature.mqtt;

import java.util.Arrays;
import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.model.AdministrativeInformation;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetKind;
import org.eclipse.digitaltwin.aas4j.v3.model.EmbeddedDataSpecification;
import org.eclipse.digitaltwin.aas4j.v3.model.KeyTypes;
import org.eclipse.digitaltwin.aas4j.v3.model.LangStringNameType;
import org.eclipse.digitaltwin.aas4j.v3.model.LangStringTextType;
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.aas4j.v3.model.ReferenceTypes;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultAdministrativeInformation;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultEmbeddedDataSpecification;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultKey;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultLangStringNameType;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultLangStringTextType;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultReference;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.Key;

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

	// Administration AAS4J
	private static final Reference aas4jDataSpecification = new DefaultReference.Builder().keys(new DefaultKey.Builder().type(KeyTypes.BLOB).value("BlobValue").build()).type(ReferenceTypes.EXTERNAL_REFERENCE).build();
	private static final EmbeddedDataSpecification AAS4JEMB_EMBEDDED_DATA_SPECIFICATION = new DefaultEmbeddedDataSpecification.Builder().dataSpecification(aas4jDataSpecification).build();
	private static final String aas4jVersion = "1.0.0";
	private static final String aas4jRevision = "3";
	private static final String aas4jTemplateId = "ID2.0";

	// Administration AAS4J
	private static final org.eclipse.digitaltwin.basyx.aasregistry.client.model.Reference aasRegDataSpecification = new org.eclipse.digitaltwin.basyx.aasregistry.client.model.Reference()
			.keys(Arrays.asList(new Key().type(org.eclipse.digitaltwin.basyx.aasregistry.client.model.KeyTypes.BLOB).value("BlobValue"))).type(org.eclipse.digitaltwin.basyx.aasregistry.client.model.ReferenceTypes.EXTERNALREFERENCE);
	private static final org.eclipse.digitaltwin.basyx.aasregistry.client.model.EmbeddedDataSpecification AASREG_EMBEDDED_DATA_SPECIFICATION = new org.eclipse.digitaltwin.basyx.aasregistry.client.model.EmbeddedDataSpecification()
			.dataSpecification(aasRegDataSpecification);
	private static final String aasRegVersion = "1.0.0";
	private static final String aasRegRevision = "3";
	private static final String aasRegTemplateId = "ID2.0";

	public static List<LangStringTextType> getAas4jLangStringTextTypes() {
		return Arrays.asList(aas4jLangStringTextType_1, aas4jLangStringTextType_2);
	}

	public static List<org.eclipse.digitaltwin.basyx.aasregistry.client.model.LangStringTextType> getAasRegLangStringTextTypes() {
		return Arrays.asList(aasRegLangStringTextType_1, aasRegLangStringTextType_2);
	}

	public static AdministrativeInformation getAas4jAdministration() {
		return new DefaultAdministrativeInformation.Builder().embeddedDataSpecifications(AAS4JEMB_EMBEDDED_DATA_SPECIFICATION).version(aas4jVersion).revision(aas4jRevision).templateID(aas4jTemplateId).build();
	}

	public static org.eclipse.digitaltwin.basyx.aasregistry.client.model.AdministrativeInformation getAasRegAdministration() {
		return new org.eclipse.digitaltwin.basyx.aasregistry.client.model.AdministrativeInformation().embeddedDataSpecifications(Arrays.asList(AASREG_EMBEDDED_DATA_SPECIFICATION)).version(aasRegVersion).revision(aasRegRevision)
				.templateId(aasRegTemplateId);
	}
	
	public static List<LangStringNameType> getAas4jLangStringNameTypes() {
		return Arrays.asList(AAS4J_LANG_STRING_NAME_TYPE_1, AAS4J_LANG_STRING_NAME_TYPE_2);
	}

	public static List<org.eclipse.digitaltwin.basyx.aasregistry.client.model.LangStringNameType> getAasRegLangStringNameTypes() {
		return Arrays.asList(AASREG_LANG_STRING_NAME_TYPE_1, AASREG_LANG_STRING_NAME_TYPE_2);
	}

}

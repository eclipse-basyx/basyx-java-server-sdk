package org.eclipse.digitaltwin.basyx.submodelservice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.model.DataTypeDefXsd;
import org.eclipse.digitaltwin.aas4j.v3.model.File;
import org.eclipse.digitaltwin.aas4j.v3.model.KeyTypes;
import org.eclipse.digitaltwin.aas4j.v3.model.LangString;
import org.eclipse.digitaltwin.aas4j.v3.model.ModelingKind;
import org.eclipse.digitaltwin.aas4j.v3.model.MultiLanguageProperty;
import org.eclipse.digitaltwin.aas4j.v3.model.Property;
import org.eclipse.digitaltwin.aas4j.v3.model.Range;
import org.eclipse.digitaltwin.aas4j.v3.model.ReferenceTypes;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultFile;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultKey;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultLangString;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultMultiLanguageProperty;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultProperty;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultRange;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultReference;

public class SubmodelServiceUtil {
	private static final String _0173_1_02_BAA120_008 = "0173-1#02-BAA120#008";
	public static final String SUBMODEL_TECHNICAL_DATA_ID = "7A7104BDAB57E184";
	private static final String MAX_ROTATION_SPEED = "MaxRotationSpeed";
	public static final String SUBMODEL_TECHNICAL_DATA_ID_SHORT = "TechnicalData";
	public static final String SUBMODEL_TECHNICAL_DATA_SEMANTIC_ID = "0173-1#01-AFZ615#016";
	
	// SUBMODEL_ELEMENT_PROPERTY_DATA
	public static final String SUBMODEL_TECHNICAL_DATA_PROPERTY_ID_SHORT = MAX_ROTATION_SPEED;
	public static final String SUBMODEL_TECHNICAL_DATA_SEMANTIC_ID_PROPERTY = _0173_1_02_BAA120_008;
	public static final String SUBMODEL_TECHNICAL_DATA_PROPERTY_CATEGORY = "PARAMETER";
	public static final String SUBMODEL_TECHNICAL_DATA_PROPERTY_VALUE = "5000";
	public static final String SUBMODEL_TECHNICAL_DATA_PROPERTY_VALUETYPE = "integer";

	// SUBMODEL_ELEMENT_RANGE_DATA
	public static final String SUBMODEL_TECHNICAL_DATA_RANGE_ID_SHORT = "RotationSpeedRange";
	public static final String SUBMODEL_TECHNICAL_DATA_SEMANTIC_ID_RANGE = _0173_1_02_BAA120_008;
	public static final String SUBMODEL_TECHNICAL_DATA_RANGE_CATEGORY = "PARAMETER";
	public static final String SUBMODEL_TECHNICAL_DATA_RANGE_MIN_VALUE = "200";
	public static final String SUBMODEL_TECHNICAL_DATA_RANGE_MAX_VALUE = "300";
	public static final String SUBMODEL_TECHNICAL_DATA_RANGE_VALUETYPE = "integer";

	// SUBMODEL_ELEMENT_MULTI_LANGUAGE_DATA
	public static final List<LangString> MULTI_LANGUAGE_VALUE = new ArrayList<>(
			Arrays.asList(new DefaultLangString("Hello", "en"), new DefaultLangString("Hallo", "de")));
	public static final String SUBMODEL_TECHNICAL_DATA_SEMANTIC_ID_MULTI_LANG = _0173_1_02_BAA120_008;
	public static final String SUBMODEL_TECHNICAL_DATA_MULTI_LANG_PROP_ID_SHORT = "MultiLanguage";
	public static final String SUBMODEL_TECHNICAL_DATA_MULTI_LANG_PROP_CATEGORY = "PARAM";
	public static final String SUBMODEL_TECHNICAL_DATA_MULTI_LANG_PROP_VALUE = "200";

	// SUBMODEL_ELEMENT_FILE_DATA
	public static final String SUBMODEL_TECHNICAL_DATA_FILE_ID_SHORT = "FileData";
	public static final String SUBMODEL_TECHNICAL_DATA_SEMANTIC_ID_FILE = _0173_1_02_BAA120_008;
	public static final String SUBMODEL_TECHNICAL_DATA_FILE_CATEGORY = "PARAMETER";
	public static final String SUBMODEL_TECHNICAL_DATA_FILE_VALUE = "fileValue";
	public static final String SUBMODEL_TECHNICAL_DATA_FILE_CONTENT_TYPE = "application/octet-stream";

	public static SubmodelElement getDummySubmodelElement(Submodel technicalData, String idShort) {
		return technicalData.getSubmodelElements().stream().filter(sme -> sme.getIdShort().equals(idShort)).findAny()
				.get();
	}

	public static Property createPropertySubmodelElement() {
		return new DefaultProperty.Builder().kind(ModelingKind.INSTANCE)
				.semanticId(new DefaultReference.Builder()
						.keys(new DefaultKey.Builder().type(KeyTypes.CONCEPT_DESCRIPTION)
								.value(SUBMODEL_TECHNICAL_DATA_SEMANTIC_ID_PROPERTY).build())
						.type(ReferenceTypes.GLOBAL_REFERENCE).build())
				.idShort(SUBMODEL_TECHNICAL_DATA_PROPERTY_ID_SHORT).category(SUBMODEL_TECHNICAL_DATA_PROPERTY_CATEGORY)
				.value(SUBMODEL_TECHNICAL_DATA_PROPERTY_VALUE).valueType(DataTypeDefXsd.INTEGER).build();
	}

	public static Range createRangeSubmodelElement() {
		return new DefaultRange.Builder().kind(ModelingKind.INSTANCE)
				.semanticId(new DefaultReference.Builder()
						.keys(new DefaultKey.Builder().type(KeyTypes.CONCEPT_DESCRIPTION)
								.value(SUBMODEL_TECHNICAL_DATA_SEMANTIC_ID_RANGE).build())
						.type(ReferenceTypes.GLOBAL_REFERENCE).build())
				.idShort(SUBMODEL_TECHNICAL_DATA_RANGE_ID_SHORT).category(SUBMODEL_TECHNICAL_DATA_RANGE_CATEGORY)
				.min(SUBMODEL_TECHNICAL_DATA_RANGE_MIN_VALUE).valueType(DataTypeDefXsd.INTEGER)
				.max(SUBMODEL_TECHNICAL_DATA_RANGE_MAX_VALUE).valueType(DataTypeDefXsd.INTEGER).build();
	}

	public static MultiLanguageProperty createMultiLanguagePropertySubmodelElement() {
		return new DefaultMultiLanguageProperty.Builder().kind(ModelingKind.INSTANCE)
				.semanticId(new DefaultReference.Builder()
						.keys(new DefaultKey.Builder().type(KeyTypes.CONCEPT_DESCRIPTION)
								.value(SUBMODEL_TECHNICAL_DATA_SEMANTIC_ID_MULTI_LANG).build())
						.type(ReferenceTypes.GLOBAL_REFERENCE).build())
				.idShort(SUBMODEL_TECHNICAL_DATA_MULTI_LANG_PROP_ID_SHORT)
				.category(SUBMODEL_TECHNICAL_DATA_MULTI_LANG_PROP_CATEGORY).value(MULTI_LANGUAGE_VALUE).build();
	}

	public static File createFileSubmodelElement() {
		return new DefaultFile.Builder().kind(ModelingKind.INSTANCE)
				.semanticId(new DefaultReference.Builder()
						.keys(new DefaultKey.Builder().type(KeyTypes.CONCEPT_DESCRIPTION)
								.value(SUBMODEL_TECHNICAL_DATA_SEMANTIC_ID_FILE).build())
						.type(ReferenceTypes.GLOBAL_REFERENCE).build())
				.idShort(SUBMODEL_TECHNICAL_DATA_FILE_ID_SHORT).category(SUBMODEL_TECHNICAL_DATA_FILE_CATEGORY)
				.value(SUBMODEL_TECHNICAL_DATA_FILE_VALUE).contentType(SUBMODEL_TECHNICAL_DATA_FILE_CONTENT_TYPE)
				.build();
	}

	public static List<SubmodelElement> getAllSubmodelElements() {
		return Arrays.asList(createPropertySubmodelElement(), createRangeSubmodelElement(), createMultiLanguagePropertySubmodelElement(),
				createFileSubmodelElement());
	}
}

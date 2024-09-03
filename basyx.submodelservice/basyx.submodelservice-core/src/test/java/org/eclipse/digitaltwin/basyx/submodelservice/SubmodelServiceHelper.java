/*******************************************************************************
 * Copyright (C) 2023 the Eclipse BaSyx Authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * SPDX-License-Identifier: MIT
 ******************************************************************************/
package org.eclipse.digitaltwin.basyx.submodelservice;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.model.AnnotatedRelationshipElement;
import org.eclipse.digitaltwin.aas4j.v3.model.Blob;
import org.eclipse.digitaltwin.aas4j.v3.model.DataTypeDefXsd;
import org.eclipse.digitaltwin.aas4j.v3.model.Entity;
import org.eclipse.digitaltwin.aas4j.v3.model.EntityType;
import org.eclipse.digitaltwin.aas4j.v3.model.File;
import org.eclipse.digitaltwin.aas4j.v3.model.Key;
import org.eclipse.digitaltwin.aas4j.v3.model.KeyTypes;
import org.eclipse.digitaltwin.aas4j.v3.model.LangStringTextType;
import org.eclipse.digitaltwin.aas4j.v3.model.MultiLanguageProperty;
import org.eclipse.digitaltwin.aas4j.v3.model.Operation;
import org.eclipse.digitaltwin.aas4j.v3.model.OperationVariable;
import org.eclipse.digitaltwin.aas4j.v3.model.Property;
import org.eclipse.digitaltwin.aas4j.v3.model.Range;
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.aas4j.v3.model.ReferenceElement;
import org.eclipse.digitaltwin.aas4j.v3.model.ReferenceTypes;
import org.eclipse.digitaltwin.aas4j.v3.model.RelationshipElement;
import org.eclipse.digitaltwin.aas4j.v3.model.SpecificAssetId;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElementCollection;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElementList;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultAnnotatedRelationshipElement;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultBlob;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultEntity;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultFile;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultKey;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultLangStringTextType;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultMultiLanguageProperty;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultOperation;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultOperationVariable;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultProperty;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultRange;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultReference;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultReferenceElement;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultRelationshipElement;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSpecificAssetId;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSubmodelElementCollection;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSubmodelElementList;
import org.eclipse.digitaltwin.basyx.InvokableOperation;

import com.google.common.collect.Lists;

/**
 * Test helper class for SubmodelService
 * 
 * @author danish
 *
 */
public class SubmodelServiceHelper {

	private static final String _0173_1_02_BAA120_008 = "0173-1#02-BAA120#008";
	public static final String SUBMODEL_TECHNICAL_DATA_ID = "7A7104BDAB57E184";
	private static final String MAX_ROTATION_SPEED = "MaxRotationSpeed";
	public static final String SUBMODEL_TECHNICAL_DATA_ID_SHORT = "TechnicalData";
	public static final String SUBMODEL_TECHNICAL_DATA_SEMANTIC_ID = "0173-1#01-AFZ615#016";
	public static final List<Key> FIRST_KEYS = Lists.newArrayList(new DefaultKey.Builder().type(KeyTypes.DATA_ELEMENT)
			.value("DataElement")
			.build());
	public static final List<Key> SECOND_KEYS = Lists.newArrayList(new DefaultKey.Builder().type(KeyTypes.BASIC_EVENT_ELEMENT)
			.value("BasicEventElement")
			.build());
	public static final Reference FIRST_REFERENCE = createFirstReference();

	public static final Reference SECOND_REFERENCE = createSecondReference();

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
	public static final List<LangStringTextType> MULTI_LANGUAGE_VALUE = Lists.newArrayList(new DefaultLangStringTextType.Builder().text("Hello")
			.language("en")
			.build(),
			new DefaultLangStringTextType.Builder().text("Hallo")
					.language("de")
					.build());
	public static final String SUBMODEL_TECHNICAL_DATA_SEMANTIC_ID_MULTI_LANG = _0173_1_02_BAA120_008;
	public static final String SUBMODEL_TECHNICAL_DATA_MULTI_LANG_PROP_ID_SHORT = "MultiLanguage";
	public static final String SUBMODEL_TECHNICAL_DATA_MULTI_LANG_PROP_CATEGORY = "PARAM";
	public static final String SUBMODEL_TECHNICAL_DATA_MULTI_LANG_PROP_VALUE = "200";

	// SUBMODEL_ELEMENT_FILE_DATA
	public static final String SUBMODEL_TECHNICAL_DATA_FILE_ID_SHORT = "FileData";
	public static final String SUBMODEL_TECHNICAL_DATA_SEMANTIC_ID_FILE = _0173_1_02_BAA120_008;
	public static final String SUBMODEL_TECHNICAL_DATA_FILE_CATEGORY = "PARAMETER";
	public static final String SUBMODEL_TECHNICAL_DATA_FILE_VALUE = "testFile.json";
	public static final String SUBMODEL_TECHNICAL_DATA_FILE_CONTENT_TYPE = "application/json";
	public static final String SUBMODEL_TECHNICAL_NON_FILE = "application/json";

	// SUBMODEL_ELEMENT_B_DATA
	public static final String SUBMODEL_TECHNICAL_DATA_BLOB_ID_SHORT = "BlobData";
	public static final String SUBMODEL_TECHNICAL_DATA_SEMANTIC_ID_BLOB = _0173_1_02_BAA120_008;
	public static final String SUBMODEL_TECHNICAL_DATA_BLOB_CATEGORY = "PARAMETER";
	public static final String SUBMODEL_TECHNICAL_DATA_BLOB_VALUE = "Test content of XML file";
	public static final String SUBMODEL_TECHNICAL_DATA_BLOB_CONTENT_TYPE = "application/xml";

	// SUBMODEL_ELEMENT_ENTITY_DATA
	public static final String SUBMODEL_TECHNICAL_DATA_ENTITY_ID_SHORT = "EntityData";
	public static final String SUBMODEL_TECHNICAL_DATA_SEMANTIC_ID_ENTITY = _0173_1_02_BAA120_008;
	public static final String SUBMODEL_TECHNICAL_DATA_ENTITY_CATEGORY = "Entity";
	public static final String SPECIFIC_ASSET_ID_VALUE = "specificValue";
	public static final String SPECIFIC_ASSET_ID_NAME = "specificAssetIdName";
	private static final SpecificAssetId ENTITY_SPECIFIC_ASSET_ID = new DefaultSpecificAssetId.Builder().name(SPECIFIC_ASSET_ID_NAME)
			.value(SPECIFIC_ASSET_ID_VALUE)
			.build();
	public static final String GLOBAL_ASSET_ID = "globalAssetID";

	// SUBMODEL_ELEMENT_REFERENCE_ELEMENT_DATA
	public static final String SUBMODEL_TECHNICAL_DATA_REFERENCE_ELEMENT_ID_SHORT = "ReferenceElement";
	public static final String SUBMODEL_TECHNICAL_DATA_SEMANTIC_ID_REFERENCE_ELEMENT = _0173_1_02_BAA120_008;
	public static final String SUBMODEL_TECHNICAL_DATA_REFERENCE_ELEMENT_CATEGORY = "PARAMETER";

	// SUBMODEL_ELEMENT_RELATIONSHIP_ELEMENT_DATA
	public static final String SUBMODEL_TECHNICAL_DATA_RELATIONSHIP_ELEMENT_ID_SHORT = "RelationshipElement";
	public static final String SUBMODEL_TECHNICAL_DATA_SEMANTIC_ID_RELATIONSHIP_ELEMENT = _0173_1_02_BAA120_008;
	public static final String SUBMODEL_TECHNICAL_DATA_RELATIONSHIP_ELEMENT_CATEGORY = "PARAMETER";

	// SUBMODEL_ELEMENT_ANNOTATED_RELATIONSHIP_ELEMENT_DATA
	public static final String SUBMODEL_TECHNICAL_DATA_ANNOTATED_RELATIONSHIP_ELEMENT_ID_SHORT = "AnnotatedRelationshipElement";
	public static final String SUBMODEL_TECHNICAL_DATA_SEMANTIC_ID_ANNOTATED_RELATIONSHIP_ELEMENT = _0173_1_02_BAA120_008;
	public static final String SUBMODEL_TECHNICAL_DATA_ANNOTATED_RELATIONSHIP_ELEMENT_CATEGORY = "PARAMETER";

	// SUBMODEL_ELEMENT_COLLECTION_DATA
	public static final String SUBMODEL_TECHNICAL_DATA_SUBMODEL_ELEMENT_COLLECTION_ID_SHORT = "SubmodelElementCollection";
	public static final String SUBMODEL_TECHNICAL_DATA_SEMANTIC_ID_SUBMODEL_ELEMENT_COLLECTION = _0173_1_02_BAA120_008;
	public static final String SUBMODEL_TECHNICAL_DATA_SUBMODEL_ELEMENT_COLLECTION_CATEGORY = "PARAMETER";

	// SUBMODEL_ELEMENT_LIST_DATA
	public static final String SUBMODEL_TECHNICAL_DATA_SUBMODEL_ELEMENT_LIST_ID_SHORT = "SubmodelElementList";
	public static final String SUBMODEL_TECHNICAL_DATA_SEMANTIC_ID_SUBMODEL_ELEMENT_LIST = _0173_1_02_BAA120_008;
	public static final String SUBMODEL_TECHNICAL_DATA_SUBMODEL_ELEMENT_LIST_CATEGORY = "PARAMETER";

	public static final String SUBMODEL_TECHNICAL_DATA_OPERATION_ID = "square";
	public static final String SUBMODEL_TECHNICAL_DATA_OPERATIONINOUT_ID = "sum";

	public static SubmodelElement getDummySubmodelElement(Submodel technicalData, String idShort) {
		return technicalData.getSubmodelElements()
				.stream()
				.filter(sme -> sme.getIdShort()
						.equals(idShort))
				.findAny()
				.get();
	}

	public static Property createPropertySubmodelElement() {
		return new DefaultProperty.Builder().semanticId(new DefaultReference.Builder().keys(new DefaultKey.Builder().type(KeyTypes.CONCEPT_DESCRIPTION)
				.value(SUBMODEL_TECHNICAL_DATA_SEMANTIC_ID_PROPERTY)
				.build())
				.type(ReferenceTypes.EXTERNAL_REFERENCE)
				.build())
				.idShort(SUBMODEL_TECHNICAL_DATA_PROPERTY_ID_SHORT)
				.category(SUBMODEL_TECHNICAL_DATA_PROPERTY_CATEGORY)
				.value(SUBMODEL_TECHNICAL_DATA_PROPERTY_VALUE)
				.valueType(DataTypeDefXsd.INTEGER)
				.build();
	}

	public static Range createRangeSubmodelElement() {
		return new DefaultRange.Builder().semanticId(new DefaultReference.Builder().keys(new DefaultKey.Builder().type(KeyTypes.CONCEPT_DESCRIPTION)
				.value(SUBMODEL_TECHNICAL_DATA_SEMANTIC_ID_RANGE)
				.build())
				.type(ReferenceTypes.EXTERNAL_REFERENCE)
				.build())
				.idShort(SUBMODEL_TECHNICAL_DATA_RANGE_ID_SHORT)
				.category(SUBMODEL_TECHNICAL_DATA_RANGE_CATEGORY)
				.min(SUBMODEL_TECHNICAL_DATA_RANGE_MIN_VALUE)
				.valueType(DataTypeDefXsd.INTEGER)
				.max(SUBMODEL_TECHNICAL_DATA_RANGE_MAX_VALUE)
				.valueType(DataTypeDefXsd.INTEGER)
				.build();
	}

	public static MultiLanguageProperty createMultiLanguagePropertySubmodelElement() {
		return new DefaultMultiLanguageProperty.Builder().semanticId(new DefaultReference.Builder().keys(new DefaultKey.Builder().type(KeyTypes.CONCEPT_DESCRIPTION)
				.value(SUBMODEL_TECHNICAL_DATA_SEMANTIC_ID_MULTI_LANG)
				.build())
				.type(ReferenceTypes.EXTERNAL_REFERENCE)
				.build())
				.idShort(SUBMODEL_TECHNICAL_DATA_MULTI_LANG_PROP_ID_SHORT)
				.category(SUBMODEL_TECHNICAL_DATA_MULTI_LANG_PROP_CATEGORY)
				.value(MULTI_LANGUAGE_VALUE)
				.build();
	}

	public static File createFileSubmodelElement() {
		return new DefaultFile.Builder().semanticId(new DefaultReference.Builder().keys(new DefaultKey.Builder().type(KeyTypes.CONCEPT_DESCRIPTION)
				.value(SUBMODEL_TECHNICAL_DATA_SEMANTIC_ID_FILE)
				.build())
				.type(ReferenceTypes.EXTERNAL_REFERENCE)
				.build())
				.idShort(SUBMODEL_TECHNICAL_DATA_FILE_ID_SHORT)
				.category(SUBMODEL_TECHNICAL_DATA_FILE_CATEGORY)
				.value(SUBMODEL_TECHNICAL_DATA_FILE_VALUE)
				.contentType(SUBMODEL_TECHNICAL_DATA_FILE_CONTENT_TYPE)
				.build();
	}

	public static Blob createBlobSubmodelElement() {
		return new DefaultBlob.Builder().semanticId(new DefaultReference.Builder().keys(new DefaultKey.Builder().type(KeyTypes.CONCEPT_DESCRIPTION)
				.value(SUBMODEL_TECHNICAL_DATA_SEMANTIC_ID_BLOB)
				.build())
				.type(ReferenceTypes.EXTERNAL_REFERENCE)
				.build())
				.idShort(SUBMODEL_TECHNICAL_DATA_BLOB_ID_SHORT)
				.category(SUBMODEL_TECHNICAL_DATA_BLOB_CATEGORY)
				.value(SUBMODEL_TECHNICAL_DATA_BLOB_VALUE.getBytes())
				.contentType(SUBMODEL_TECHNICAL_DATA_BLOB_CONTENT_TYPE)
				.build();
	}

	public static Entity createEntitySubmodelElement() {
		return new DefaultEntity.Builder().semanticId(new DefaultReference.Builder().keys(new DefaultKey.Builder().type(KeyTypes.CONCEPT_DESCRIPTION)
				.value(SUBMODEL_TECHNICAL_DATA_SEMANTIC_ID_ENTITY)
				.build())
				.type(ReferenceTypes.EXTERNAL_REFERENCE)
				.build())
				.idShort(SUBMODEL_TECHNICAL_DATA_ENTITY_ID_SHORT)
				.category(SUBMODEL_TECHNICAL_DATA_ENTITY_CATEGORY)
				.statements(Lists.newArrayList(createPropertySubmodelElement(), createRangeSubmodelElement()))
				.entityType(EntityType.CO_MANAGED_ENTITY)
				.globalAssetId(GLOBAL_ASSET_ID)
				.specificAssetIds(ENTITY_SPECIFIC_ASSET_ID)
				.build();
	}

	public static ReferenceElement createReferenceElementSubmodelElement() {
		return new DefaultReferenceElement.Builder().semanticId(new DefaultReference.Builder().keys(new DefaultKey.Builder().type(KeyTypes.CONCEPT_DESCRIPTION)
				.value(SUBMODEL_TECHNICAL_DATA_SEMANTIC_ID_REFERENCE_ELEMENT)
				.build())
				.type(ReferenceTypes.EXTERNAL_REFERENCE)
				.build())
				.idShort(SUBMODEL_TECHNICAL_DATA_REFERENCE_ELEMENT_ID_SHORT)
				.category(SUBMODEL_TECHNICAL_DATA_REFERENCE_ELEMENT_CATEGORY)
				.value(createFirstReference())
				.build();
	}

	public static RelationshipElement createRelationshipElementSubmodelElement() {
		return new DefaultRelationshipElement.Builder().semanticId(new DefaultReference.Builder().keys(new DefaultKey.Builder().type(KeyTypes.CONCEPT_DESCRIPTION)
				.value(SUBMODEL_TECHNICAL_DATA_SEMANTIC_ID_RELATIONSHIP_ELEMENT)
				.build())
				.type(ReferenceTypes.EXTERNAL_REFERENCE)
				.build())
				.idShort(SUBMODEL_TECHNICAL_DATA_RELATIONSHIP_ELEMENT_ID_SHORT)
				.category(SUBMODEL_TECHNICAL_DATA_RELATIONSHIP_ELEMENT_CATEGORY)
				.first(createFirstReference())
				.second(createSecondReference())
				.build();
	}

	public static AnnotatedRelationshipElement createAnnotatedRelationshipElementSubmodelElement() {
		return new DefaultAnnotatedRelationshipElement.Builder().semanticId(new DefaultReference.Builder().keys(new DefaultKey.Builder().type(KeyTypes.CONCEPT_DESCRIPTION)
				.value(SUBMODEL_TECHNICAL_DATA_SEMANTIC_ID_ANNOTATED_RELATIONSHIP_ELEMENT)
				.build())
				.type(ReferenceTypes.EXTERNAL_REFERENCE)
				.build())
				.idShort(SUBMODEL_TECHNICAL_DATA_ANNOTATED_RELATIONSHIP_ELEMENT_ID_SHORT)
				.category(SUBMODEL_TECHNICAL_DATA_ANNOTATED_RELATIONSHIP_ELEMENT_CATEGORY)
				.first(createFirstReference())
				.second(createSecondReference())
				.annotations(Lists.newArrayList(createPropertySubmodelElement(), createRangeSubmodelElement()))
				.build();
	}

	public static SubmodelElementCollection createSubmodelElementCollection() {
		return new DefaultSubmodelElementCollection.Builder().semanticId(new DefaultReference.Builder().keys(new DefaultKey.Builder().type(KeyTypes.CONCEPT_DESCRIPTION)
				.value(SUBMODEL_TECHNICAL_DATA_SEMANTIC_ID_SUBMODEL_ELEMENT_COLLECTION)
				.build())
				.type(ReferenceTypes.EXTERNAL_REFERENCE)
				.build())
				.idShort(SUBMODEL_TECHNICAL_DATA_SUBMODEL_ELEMENT_COLLECTION_ID_SHORT)
				.category(SUBMODEL_TECHNICAL_DATA_SUBMODEL_ELEMENT_COLLECTION_CATEGORY)
				.value(Lists.newArrayList(createFileSubmodelElement(), createPropertySubmodelElement()))
				.build();
	}

	public static SubmodelElementList createSubmodelElementList() {
		return new DefaultSubmodelElementList.Builder().semanticId(new DefaultReference.Builder().keys(new DefaultKey.Builder().type(KeyTypes.CONCEPT_DESCRIPTION)
				.value(SUBMODEL_TECHNICAL_DATA_SEMANTIC_ID_SUBMODEL_ELEMENT_LIST)
				.build())
				.type(ReferenceTypes.EXTERNAL_REFERENCE)
				.build())
				.idShort(SUBMODEL_TECHNICAL_DATA_SUBMODEL_ELEMENT_LIST_ID_SHORT)
				.category(SUBMODEL_TECHNICAL_DATA_SUBMODEL_ELEMENT_LIST_CATEGORY)
				.value(Lists.newArrayList(createRangeSubmodelElement(), createPropertySubmodelElement()))
				.build();
	}

	public static List<SubmodelElement> getAllSubmodelElements() {
		List<SubmodelElement> list = new ArrayList<>();
		list.addAll(
				Lists.newArrayList(createPropertySubmodelElement(), createRangeSubmodelElement(), createMultiLanguagePropertySubmodelElement(), createFileSubmodelElement(), createEntitySubmodelElement(),
						createReferenceElementSubmodelElement(),
						createRelationshipElementSubmodelElement(), createAnnotatedRelationshipElementSubmodelElement(), createBlobSubmodelElement(), createSubmodelElementCollection(), createSubmodelElementList(),
						createInvokableOperation(), createInvokableInOutOperation()));
		return list;
	}
	
	public static List<SubmodelElement> getAllSubmodelElementsWithoutInvokableOperation() {
		List<SubmodelElement> list = new ArrayList<>();
		list.addAll(
				Lists.newArrayList(createPropertySubmodelElement(), createRangeSubmodelElement(), createMultiLanguagePropertySubmodelElement(), createFileSubmodelElement(), createEntitySubmodelElement(), createReferenceElementSubmodelElement(),
						createRelationshipElementSubmodelElement(), createAnnotatedRelationshipElementSubmodelElement(), createBlobSubmodelElement(), createSubmodelElementCollection(), createSubmodelElementList(), createOperation()));
		return list;
	}
	
	public static OperationVariable createOperationVariable(Property val) {
		return new DefaultOperationVariable.Builder().value(val).build();
	}
	
	public static Property createDummyProperty(String idShort, String value, DataTypeDefXsd dataType) {
		return new DefaultProperty.Builder().idShort(idShort)
				.category("cat1")
				.value(value)
				.valueType(dataType)
				.build();
	}
	
	public static File createDummyFile(String idShort, String contentType, String value) {
		return new DefaultFile.Builder().idShort(idShort)
				.category("file")
				.contentType(contentType)
				.value(value)
				.build();
	}

	private static Operation createInvokableOperation() {
		return new InvokableOperation.Builder().idShort(SUBMODEL_TECHNICAL_DATA_OPERATION_ID).inputVariables(createIntOperationVariable("input")).outputVariables(createIntOperationVariable("result"))
				.invokable(SubmodelServiceHelper::square).build();
	}

	private static Operation createInvokableInOutOperation() {
		return new InvokableOperation.Builder().idShort(SUBMODEL_TECHNICAL_DATA_OPERATIONINOUT_ID).inputVariables(createIntOperationVariable("input")).inoutputVariables(createIntOperationVariable("stack"))
				.invokable(SubmodelServiceHelper::sum).build();
	}

	private static Operation createOperation() {
		return new DefaultOperation.Builder().idShort(SUBMODEL_TECHNICAL_DATA_OPERATION_ID).inputVariables(createIntOperationVariable("input")).outputVariables(createIntOperationVariable("result"))
				.build();

	}

	private static OperationVariable[] square(OperationVariable[] inputs) {
		Property in = (Property) inputs[0].getValue();
		Integer val = Integer.valueOf(in.getValue());
		Integer squared = val * val;
		in.setValue(squared.toString());
		in.setIdShort("result");
		return new OperationVariable[] { createOperationVariable(in) };
	}

	private static OperationVariable[] sum(OperationVariable[] inputs) {
		Property in = (Property) inputs[0].getValue();
		Property stack = (Property) inputs[1].getValue();
		Integer inVal = Integer.valueOf(in.getValue());
		Integer stackVal = Integer.valueOf(stack.getValue());
		Integer sumResult = inVal + stackVal;
		stack.setValue(sumResult.toString());
		return new OperationVariable[] { createOperationVariable(stack) };
	}

	private static DefaultOperationVariable createIntOperationVariable(String idShort) {
		return new DefaultOperationVariable.Builder().value(new DefaultProperty.Builder().idShort(idShort).valueType(DataTypeDefXsd.INT).build()).build();
	}

	private static DefaultReference createFirstReference() {
		return new DefaultReference.Builder().type(ReferenceTypes.MODEL_REFERENCE)
				.keys(FIRST_KEYS)
				.build();
	}

	private static DefaultReference createSecondReference() {
		return new DefaultReference.Builder().type(ReferenceTypes.EXTERNAL_REFERENCE)
				.keys(SECOND_KEYS)
				.build();
	}
}

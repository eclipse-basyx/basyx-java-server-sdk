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
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.model.DataTypeDefXsd;
import org.eclipse.digitaltwin.aas4j.v3.model.KeyTypes;
import org.eclipse.digitaltwin.aas4j.v3.model.ModelingKind;
import org.eclipse.digitaltwin.aas4j.v3.model.ReferenceTypes;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElementCollection;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElementList;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultKey;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultProperty;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultReference;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSubmodel;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSubmodelElementCollection;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSubmodelElementList;

/**
 * 
 * @author schnicke, danish
 *
 */
public class DummySubmodelFactory {

	private static final String HTTP_CUSTOMER_COM_CD_1_1_18EBD56F6B43D895 = "http://customer.com/cd/1/1/18EBD56F6B43D895";
	private static final String ROTATION_SPEED = "RotationSpeed";
	private static final String _0173_1_02_BAA120_008 = "0173-1#02-BAA120#008";

	// AAS
	public static final String AAS_ID = "ExampleMotor";
	public static final String AAS_IDENTIFIER = "http://customer.com/aas/9175_7013_7091_9168";

	// SUBMODEL_TECHNICAL_DATA
	public static final String SUBMODEL_TECHNICAL_DATA_ID_SHORT = "TechnicalData";
	public static final String SUBMODEL_TECHNICAL_DATA_ID = "7A7104BDAB57E184";
	public static final String SUBMODEL_TECHNICAL_DATA_SEMANTIC_ID = "0173-1#01-AFZ615#016";
	public static final String SUBMODEL_TECHNICAL_DATA_SEMANTIC_ID_PROPERTY = _0173_1_02_BAA120_008;

	// SUBMODEL_OPERATIONAL_DATA
	public static final String SUBMODEL_OPERATIONAL_DATA_ID_SHORT = "OperationalData";
	public static final String SUBMODEL_OPERATIONAL_DATA_ID = "AC69B1CB44F07935";
	public static final String SUBMODEL_OPERATIONAL_DATA_SEMANTIC_ID_PROPERTY = HTTP_CUSTOMER_COM_CD_1_1_18EBD56F6B43D895;
	public static final String SUBMODEL_OPERATIONAL_DATA_PROPERTY_ID_SHORT = ROTATION_SPEED;
	public static final String SUBMODEL_OPERATIONAL_DATA_PROPERTY_CATEGORY = "VARIABLE";
	public static final String SUBMODEL_OPERATIONAL_DATA_PROPERTY_VALUE = "4370";
	public static final String SUBMODEL_OPERATIONAL_DATA_ELEMENT_COLLECTION_ID_SHORT = "OperationalElementCollection";
	public static final String SUBMODEL_OPERATIONAL_DATA_ELEMENT_LIST_ID_SHORT = "OperationalElementList";
	public static final String AAS_3_0_RC_02_DATA_SPECIFICATION_IEC_61360 = "https://admin-shell.io/aas/3/0/RC02/DataSpecificationIEC61360";

	// SUBMODEL_ELEMENTS_SUBMODEL_ELEMENT_COLLECTION
	public static final String SUBMODEL_ELEMENT_FIRST_ID_SHORT = "MyFirstSubmodelElement";
	public static final String SUBMODEL_ELEMENT_SECOND_ID_SHORT = "MySecondSubmodelElement";
	public static final String SUBMODEL_ELEMENT_FIRST_VALUE = "123";
	public static final String SUBMODEL_ELEMENT_SECOND_VALUE = "456";
	public static final String SUBMODEL_ELEMENT_COLLECTION_TOP = "TopLevelCollection";
	public static final String SUBMODEL_ELEMENT_FIRST_LIST = "FirstList";
	public static final String SUBMODEL_ELEMENT_SECOND_LIST = "SecondList";
	public static final String SUBMODEL_ELEMENT_COLLECTION_DEEP = "DeepCollection";

	public static Collection<Submodel> getSubmodels() {
		return Arrays.asList(createTechnicalDataSubmodel(), createOperationalDataSubmodel());
	}

	public static Submodel createTechnicalDataSubmodel() {
		return new DefaultSubmodel.Builder()
				.semanticId(new DefaultReference.Builder().keys(new DefaultKey.Builder().type(KeyTypes.GLOBAL_REFERENCE).value(SUBMODEL_TECHNICAL_DATA_SEMANTIC_ID).build()).type(ReferenceTypes.GLOBAL_REFERENCE).build())
				.kind(ModelingKind.INSTANCE).idShort(SUBMODEL_TECHNICAL_DATA_ID_SHORT).id(SUBMODEL_TECHNICAL_DATA_ID)
				.submodelElements(SubmodelServiceHelper.getAllSubmodelElements())
				.build();
	}

	public static Submodel createOperationalDataSubmodel() {
		return new DefaultSubmodel.Builder().kind(ModelingKind.INSTANCE).idShort(SUBMODEL_OPERATIONAL_DATA_ID_SHORT).id(SUBMODEL_OPERATIONAL_DATA_ID).submodelElements(new DefaultProperty.Builder().kind(ModelingKind.INSTANCE)
				.semanticId(new DefaultReference.Builder().keys(new DefaultKey.Builder().type(KeyTypes.CONCEPT_DESCRIPTION).value(SUBMODEL_OPERATIONAL_DATA_SEMANTIC_ID_PROPERTY).build()).type(ReferenceTypes.GLOBAL_REFERENCE).build())
				.idShort(SUBMODEL_OPERATIONAL_DATA_PROPERTY_ID_SHORT).category(SUBMODEL_OPERATIONAL_DATA_PROPERTY_CATEGORY).value(SUBMODEL_OPERATIONAL_DATA_PROPERTY_VALUE).valueType(DataTypeDefXsd.INTEGER).build()).build();
	}

	public static Submodel createOperationalDataSubmodelWithHierarchicalSubmodelElements() {
		return new DefaultSubmodel.Builder().kind(ModelingKind.INSTANCE).idShort(SUBMODEL_OPERATIONAL_DATA_ID_SHORT)
				.id(SUBMODEL_OPERATIONAL_DATA_ID).submodelElements(createOperationalDataSubmodelElements()).build();
	}

	private static List<SubmodelElement> createOperationalDataSubmodelElements() {
		ArrayList<SubmodelElement> list = new ArrayList<>();

		SubmodelElement sme1 = createProperty(SUBMODEL_OPERATIONAL_DATA_PROPERTY_ID_SHORT);
		SubmodelElement sme2 = createProperty(SUBMODEL_ELEMENT_FIRST_ID_SHORT);
		SubmodelElement sme3 = createProperty(SUBMODEL_ELEMENT_SECOND_ID_SHORT);

		SubmodelElementCollection submodelElementCollection = new DefaultSubmodelElementCollection();
		SubmodelElementList submodelElementList = new DefaultSubmodelElementList();

		Collection<SubmodelElement> submodelElementsCollection = new HashSet<>();
		List<SubmodelElement> submodelElementsList = new ArrayList<>();

		setValuesOfSubmodelElements(sme2, sme3, submodelElementCollection, submodelElementList,
				submodelElementsCollection, submodelElementsList);

		SubmodelElementCollection topLevelElementCollection = createNestedElementCollection(sme2);

		list.add(sme1);
		list.add(submodelElementCollection);
		list.add(submodelElementList);
		list.add(topLevelElementCollection);
		return list;
	}

	private static void setValuesOfSubmodelElements(SubmodelElement sme2, SubmodelElement sme3,
			SubmodelElementCollection submodelElementCollection, SubmodelElementList submodelElementList,
			Collection<SubmodelElement> submodelElementsCollection, List<SubmodelElement> submodelElementsList) {
		submodelElementList.setIdShort(SUBMODEL_OPERATIONAL_DATA_ELEMENT_LIST_ID_SHORT);
		submodelElementCollection.setIdShort(SUBMODEL_OPERATIONAL_DATA_ELEMENT_COLLECTION_ID_SHORT);
		submodelElementsList.add(sme2);
		submodelElementList.setValue(submodelElementsList);
		submodelElementsCollection.add(sme3);
		submodelElementsCollection.add(submodelElementList);
		submodelElementCollection.setValue(submodelElementsCollection);
	}

	private static SubmodelElementCollection createNestedElementCollection(SubmodelElement sme2) {
		SubmodelElementCollection topLevelElementCollection = new DefaultSubmodelElementCollection();
		SubmodelElementList submodelElementListInElementCollection = new DefaultSubmodelElementList();
		SubmodelElementList submodelElementListInElementList = new DefaultSubmodelElementList();
		SubmodelElementCollection submodelElementCollectionInElementList = new DefaultSubmodelElementCollection();
		Collection<SubmodelElement> submodelElementCollectionInElementListCollection = new HashSet<>();
		List<SubmodelElement> listForSubmodelElementListInElementList = new ArrayList<>();
		List<SubmodelElement> listForSubmodelElementListInElementCollection = new ArrayList<>();
		Collection<SubmodelElement> collectionForTopLevelElementCollection = new HashSet<>();

		submodelElementCollectionInElementListCollection.add(sme2);
		submodelElementCollectionInElementList.setValue(submodelElementCollectionInElementListCollection);
		listForSubmodelElementListInElementList.add(submodelElementCollectionInElementList);
		submodelElementListInElementList.setValue(listForSubmodelElementListInElementList);
		listForSubmodelElementListInElementCollection.add(submodelElementListInElementList);
		submodelElementListInElementCollection.setValue(listForSubmodelElementListInElementCollection);
		collectionForTopLevelElementCollection.add(submodelElementListInElementCollection);
		topLevelElementCollection.setValue(collectionForTopLevelElementCollection);

		topLevelElementCollection.setIdShort(SUBMODEL_ELEMENT_COLLECTION_TOP);
		submodelElementListInElementCollection.setIdShort(SUBMODEL_ELEMENT_FIRST_LIST);
		submodelElementListInElementList.setIdShort(SUBMODEL_ELEMENT_SECOND_LIST);
		submodelElementCollectionInElementList.setIdShort(SUBMODEL_ELEMENT_COLLECTION_DEEP);
		return topLevelElementCollection;
	}

	private static SubmodelElement createProperty(String propertyIdShort) {
		SubmodelElement sme3 = new DefaultProperty.Builder().kind(ModelingKind.INSTANCE)
				.idShort(propertyIdShort).category(SUBMODEL_OPERATIONAL_DATA_PROPERTY_CATEGORY)
				.value(SUBMODEL_OPERATIONAL_DATA_PROPERTY_VALUE).valueType(DataTypeDefXsd.INTEGER).build();
		return sme3;
	}

}

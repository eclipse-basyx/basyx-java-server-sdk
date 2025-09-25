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
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.digitaltwin.aas4j.v3.model.DataTypeDefXsd;
import org.eclipse.digitaltwin.aas4j.v3.model.File;
import org.eclipse.digitaltwin.aas4j.v3.model.KeyTypes;
import org.eclipse.digitaltwin.aas4j.v3.model.Property;
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

import com.google.common.collect.Lists;

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

	// SUBMODEL_ALL_SUBMODEL_ELEMENTS
	public static final String SUBMODEL_ALL_SUBMODEL_ELEMENTS_ID = "8A7104BDAB57E185";

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
	public static final String SUBMODEL_ELEMENT_LIST_SIMPLE = "SimpleList";
	public static final String SUBMODEL_ELEMENT_COLLECTION_SIMPLE = "SimpleCollection";

	// SUBMODEL_SIMPLE_DATA_WITH_ONE_COLLECTION
	public static final String SUBMODEL_SIMPLE_DATA_ID_SHORT = "simpleSubmodel001";
	public static final String SUBMODEL_SIMPLE_DATA_ID = "simpleSubmodel001";
	public static final String SUBMODEL_ELEMENT_SIMPLE_DATA_ID_SHORT = "elementToDelete";
	
	//SUBMODEL_FOR_FILE_TEST
	public static final String SUBMODEL_FOR_FILE_TEST = "8A6344BDAB57E184";
	public static final String SUBMODEL_FOR_FILE_TEST_ID_SHORT = "FileTests";
	public static final String SUBMODEL_ELEMENT_FILE_ID_SHORT = "FileData";
	public static final String SUBMODEL_ELEMENT_NON_FILE_ID_SHORT = "NonFileParameter";
	public static final String FILE_NAME = "BaSyx-Logo.png";

	public static Collection<Submodel> getSubmodels() {
		return Lists.newArrayList(createTechnicalDataSubmodel(), createOperationalDataSubmodel(), createSimpleDataSubmodel());
	}
	
	public static Collection<Submodel> getSubmodelsBySemanticid(String semanticId) {
		Collection<Submodel> submodels = Lists.newArrayList(createTechnicalDataSubmodel(), createOperationalDataSubmodel(), createSimpleDataSubmodel());
		
		return submodels.stream()
	    		.filter((submodel) -> {
	    			return submodel.getSemanticId() != null && 
	    				submodel.getSemanticId().getKeys().stream().filter((key) -> {
	    					return key.getValue().equals(semanticId); 
	    				}).findAny().isPresent();
	    		})
	    		.collect(Collectors.toList());
	}

	public static Submodel createSubmodelWithAllSubmodelElements() {
		List<SubmodelElement> submodelElements = getAllSubmodelElementsList();

		Submodel submodel = new DefaultSubmodel.Builder()
				.idShort("AllSubmodelElementsIdShort").id(SUBMODEL_ALL_SUBMODEL_ELEMENTS_ID)
				.submodelElements(submodelElements).build();


		return submodel;
	}

	public static Submodel createTechnicalDataSubmodel() {
		return new DefaultSubmodel.Builder().semanticId(new DefaultReference.Builder().keys(new DefaultKey.Builder().type(KeyTypes.GLOBAL_REFERENCE)
				.value(SUBMODEL_TECHNICAL_DATA_SEMANTIC_ID)
				.build())
				.type(ReferenceTypes.EXTERNAL_REFERENCE)
				.build())
				.idShort(SUBMODEL_TECHNICAL_DATA_ID_SHORT)
				.id(SUBMODEL_TECHNICAL_DATA_ID)
				.submodelElements(SubmodelServiceHelper.getAllSubmodelElementsWithoutInvokableOperation())
				.build();
	}
	
	public static Submodel createSubmodelWithFileElement() {
		return new DefaultSubmodel.Builder().semanticId(new DefaultReference.Builder().keys(new DefaultKey.Builder().type(KeyTypes.GLOBAL_REFERENCE)
				.value(SUBMODEL_TECHNICAL_DATA_SEMANTIC_ID)
				.build())
				.type(ReferenceTypes.EXTERNAL_REFERENCE)
				.build())
				.idShort(SUBMODEL_FOR_FILE_TEST_ID_SHORT)
				.id(SUBMODEL_FOR_FILE_TEST)
				.submodelElements(Lists.newArrayList(createFileElement(), createNonFileElement()))
				.build();
	}

	public static Submodel createOperationalDataSubmodel() {
		return new DefaultSubmodel.Builder().idShort(SUBMODEL_OPERATIONAL_DATA_ID_SHORT)
				.id(SUBMODEL_OPERATIONAL_DATA_ID)
				.submodelElements(getOperationalDataSubmodelElements()).build();
	}

	public static Submodel createSimpleDataSubmodel() {
		return new DefaultSubmodel.Builder().idShort(SUBMODEL_SIMPLE_DATA_ID_SHORT).id(SUBMODEL_SIMPLE_DATA_ID)
				.submodelElements(createSimpleSubmodelElements()).build();
	}

	public static Submodel createOperationalDataSubmodelWithHierarchicalSubmodelElements() {
		return new DefaultSubmodel.Builder().idShort(SUBMODEL_OPERATIONAL_DATA_ID_SHORT)
				.id(SUBMODEL_OPERATIONAL_DATA_ID).submodelElements(createOperationalDataSubmodelElements()).build();
	}

	private static List<SubmodelElement> getAllSubmodelElementsList() {
		return Stream.of(SubmodelServiceHelper.getAllSubmodelElements(), getOperationalDataSubmodelElements(),
				createSimpleSubmodelElements()).flatMap(Collection::stream).collect(Collectors.toList());
	}

	private static List<SubmodelElement> getOperationalDataSubmodelElements() {
		List<SubmodelElement> submodelElements = new ArrayList<>();
		submodelElements.add(new DefaultProperty.Builder()
				.semanticId(new DefaultReference.Builder()
						.keys(new DefaultKey.Builder().type(KeyTypes.CONCEPT_DESCRIPTION)
								.value(SUBMODEL_OPERATIONAL_DATA_SEMANTIC_ID_PROPERTY).build())
						.type(ReferenceTypes.EXTERNAL_REFERENCE).build())
				.idShort(SUBMODEL_OPERATIONAL_DATA_PROPERTY_ID_SHORT)
				.category(SUBMODEL_OPERATIONAL_DATA_PROPERTY_CATEGORY).value(SUBMODEL_OPERATIONAL_DATA_PROPERTY_VALUE)
				.valueType(DataTypeDefXsd.INTEGER)
				.build());
		return submodelElements;
	}

	private static List<SubmodelElement> createOperationalDataSubmodelElements() {
		List<SubmodelElement> list = new ArrayList<>();
		SubmodelElement sme1 = createProperty(SUBMODEL_OPERATIONAL_DATA_PROPERTY_ID_SHORT);
		SubmodelElement sme2 = createProperty(SUBMODEL_ELEMENT_FIRST_ID_SHORT);
		SubmodelElement sme3 = createProperty(SUBMODEL_ELEMENT_SECOND_ID_SHORT);

		SubmodelElementCollection submodelElementCollection = new DefaultSubmodelElementCollection();
		SubmodelElementList submodelElementList = new DefaultSubmodelElementList.Builder().orderRelevant(true).build();

		List<SubmodelElement> submodelElementsCollection = new ArrayList<>();
		List<SubmodelElement> submodelElementsList = new ArrayList<>();

		setValuesOfSubmodelElements(sme2, sme3, submodelElementCollection, submodelElementList, submodelElementsCollection, submodelElementsList);

		SubmodelElementCollection topLevelElementCollection = createNestedElementCollection(sme2);

		list.add(sme1);
		list.add(submodelElementCollection);
		list.add(submodelElementList);
		list.add(topLevelElementCollection);
		return list;
	}

	private static void setValuesOfSubmodelElements(SubmodelElement sme2, SubmodelElement sme3, SubmodelElementCollection submodelElementCollection, SubmodelElementList submodelElementList,
			List<SubmodelElement> submodelElementsCollection, List<SubmodelElement> submodelElementsList) {
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
		SubmodelElementList submodelElementListInElementCollection = new DefaultSubmodelElementList.Builder().orderRelevant(true).build();
		SubmodelElementList submodelElementListInElementList = new DefaultSubmodelElementList.Builder().orderRelevant(true).build();
		SubmodelElementCollection submodelElementCollectionInElementList = new DefaultSubmodelElementCollection();
		List<SubmodelElement> submodelElementCollectionInElementListCollection = new ArrayList<>();
		List<SubmodelElement> listForSubmodelElementListInElementList = new ArrayList<>();
		List<SubmodelElement> listForSubmodelElementListInElementCollection = new ArrayList<>();
		List<SubmodelElement> collectionForTopLevelElementCollection = new ArrayList<>();

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
		SubmodelElement sme3 = new DefaultProperty.Builder().idShort(propertyIdShort)
				.category(SUBMODEL_OPERATIONAL_DATA_PROPERTY_CATEGORY)
				.value(SUBMODEL_OPERATIONAL_DATA_PROPERTY_VALUE)
				.valueType(DataTypeDefXsd.INTEGER)
				.build();
		return sme3;
	}

	private static List<SubmodelElement> createSimpleSubmodelElements() {
		List<SubmodelElement> list = new ArrayList<>();
		List<SubmodelElement> smeCollectionValue = new ArrayList<>();
		List<SubmodelElement> smeListValue = new ArrayList<>();
		SubmodelElementCollection smeCollection = new DefaultSubmodelElementCollection();
		smeCollection.setIdShort(SUBMODEL_ELEMENT_COLLECTION_SIMPLE);
		smeCollectionValue.add(createProperty(SUBMODEL_ELEMENT_FIRST_ID_SHORT));

		SubmodelElementList smeList = new DefaultSubmodelElementList.Builder().orderRelevant(true).build();
		smeList.setIdShort(SUBMODEL_ELEMENT_LIST_SIMPLE);
		smeListValue.add(createProperty(SUBMODEL_ELEMENT_SECOND_ID_SHORT));

		smeCollection.setValue(smeCollectionValue);
		smeList.setValue(smeListValue);
		list.add(smeCollection);
		list.add(smeList);
		list.add(createProperty(SUBMODEL_ELEMENT_SIMPLE_DATA_ID_SHORT));
		return list;
	}
	
	private static Property createNonFileElement() {
		Property simpleProperty = SubmodelServiceHelper.createPropertySubmodelElement();
		simpleProperty.setIdShort(SUBMODEL_ELEMENT_NON_FILE_ID_SHORT);
		
		return simpleProperty;
	}
	
	private static File createFileElement() {
		File simpleFile = SubmodelServiceHelper.createFileSubmodelElement();
		simpleFile.setValue(FILE_NAME);
		
		return simpleFile;
	}

}

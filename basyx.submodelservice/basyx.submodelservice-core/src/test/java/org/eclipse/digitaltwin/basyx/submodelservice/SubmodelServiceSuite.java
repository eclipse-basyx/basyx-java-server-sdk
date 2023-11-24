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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.model.DataTypeDefXsd;
import org.eclipse.digitaltwin.aas4j.v3.model.Entity;
import org.eclipse.digitaltwin.aas4j.v3.model.File;
import org.eclipse.digitaltwin.aas4j.v3.model.LangStringTextType;
import org.eclipse.digitaltwin.aas4j.v3.model.OperationVariable;
import org.eclipse.digitaltwin.aas4j.v3.model.Property;
import org.eclipse.digitaltwin.aas4j.v3.model.Range;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElementCollection;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElementList;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultEntity;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultLangStringTextType;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultProperty;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSubmodelElementCollection;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSubmodelElementList;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.exceptions.NotInvokableException;
import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.eclipse.digitaltwin.basyx.submodelservice.value.FileBlobValue;
import org.eclipse.digitaltwin.basyx.submodelservice.value.MultiLanguagePropertyValue;
import org.eclipse.digitaltwin.basyx.submodelservice.value.PropertyValue;
import org.eclipse.digitaltwin.basyx.submodelservice.value.RangeValue;
import org.junit.Test;

/**
 * Testsuite for implementations of the SubmodelService interface
 * 
 * @author schnicke, danish
 *
 */
public abstract class SubmodelServiceSuite {
	private static final PaginationInfo NO_LIMIT_PAGINATION_INFO = new PaginationInfo(0, null);
	protected abstract SubmodelService getSubmodelService(Submodel submodel);

	@Test
	public void getSubmodel() {
		Submodel technicalData = DummySubmodelFactory.createTechnicalDataSubmodel();
		SubmodelService smService = getSubmodelService(technicalData);

		assertEquals(technicalData, smService.getSubmodel());
	}

	@Test
	public void getSubmodelElements() {
		Submodel technicalData = DummySubmodelFactory.createTechnicalDataSubmodel();
		SubmodelService smService = getSubmodelService(technicalData);

		assertTrue(technicalData.getSubmodelElements()
				.containsAll(smService.getSubmodelElements(NO_LIMIT_PAGINATION_INFO).getResult()));
	}

	@Test
	public void getSubmodelElement() {
		Submodel technicalData = DummySubmodelFactory.createTechnicalDataSubmodel();

		SubmodelElement smElement = getSubmodelService(technicalData).getSubmodelElement(SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_PROPERTY_ID_SHORT);

		assertEquals(SubmodelServiceHelper.getDummySubmodelElement(technicalData, SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_PROPERTY_ID_SHORT), smElement);
	}

	@Test(expected = ElementDoesNotExistException.class)
	public void getNonExistingSubmodelElement() {
		Submodel technicalData = DummySubmodelFactory.createTechnicalDataSubmodel();

		getSubmodelService(technicalData).getSubmodelElement("nonExisting");
	}

	@Test
	public void getDeepNestedSubmodelElement() {
		Submodel operationalData = DummySubmodelFactory.createOperationalDataSubmodelWithHierarchicalSubmodelElements();
		String idShortPath = generateNestedIdShortPath();
		SubmodelElement submodelElement = getSubmodelService(operationalData).getSubmodelElement(idShortPath);
		assertEquals(DummySubmodelFactory.SUBMODEL_ELEMENT_FIRST_ID_SHORT, submodelElement.getIdShort());
	}

	@Test
	public void getHierachicalSubmodelElementValue() {
		Submodel operationalData = DummySubmodelFactory.createOperationalDataSubmodelWithHierarchicalSubmodelElements();
		String idShortPath = generateIdShortPath();
		PropertyValue submodelElementValue = (PropertyValue) getSubmodelService(operationalData).getSubmodelElementValue(idShortPath);
		assertEquals(DummySubmodelFactory.SUBMODEL_OPERATIONAL_DATA_PROPERTY_VALUE, submodelElementValue.getValue());
	}

	@Test
	public void getHierarchicalSubmodelElementWhenFirstElementIsList() {
		Submodel operationalData = DummySubmodelFactory.createOperationalDataSubmodelWithHierarchicalSubmodelElements();

		List<SubmodelElement> submodelElementsList = new ArrayList<>();

		SubmodelElementList submodelElementList = new DefaultSubmodelElementList();
		submodelElementList.setIdShort("testList");
		List<SubmodelElement> listElements = new ArrayList<>();
		Property testProperty = new DefaultProperty.Builder().idShort("propIdShort")
				.category("cat1")
				.value("123")
				.valueType(DataTypeDefXsd.INTEGER)
				.build();
		listElements.add(testProperty);
		submodelElementList.setValue(listElements);
		submodelElementsList.add(submodelElementList);

		operationalData.setSubmodelElements(submodelElementsList);
		SubmodelElement submodelElement = getSubmodelService(operationalData).getSubmodelElement("testList[0]");
		assertTrue(submodelElement instanceof Property);
	}

	@Test
	public void getHierarchicalSubmodelElementWithFirstAndSecondSameIdShort() {
		Submodel operationalData = DummySubmodelFactory.createOperationalDataSubmodelWithHierarchicalSubmodelElements();

		List<SubmodelElement> submodelElementsCollection = new ArrayList<>();

		SubmodelElementCollection submodelElementCollection = createDummySubmodelElementCollection("test");

		List<SubmodelElement> listElements = new ArrayList<>();
		Property testProperty = createDummyProperty("test");

		listElements.add(testProperty);
		submodelElementCollection.setValue(listElements);
		submodelElementsCollection.add(submodelElementCollection);
		operationalData.setSubmodelElements(submodelElementsCollection);

		SubmodelElement submodelElement = getSubmodelService(operationalData).getSubmodelElement("test.test");

		assertTrue(submodelElement instanceof DefaultProperty);
	}

	@Test
	public void getHierarchicalSubmodelElementFromEntity() {
		Submodel operationalData = DummySubmodelFactory.createOperationalDataSubmodelWithHierarchicalSubmodelElements();

		List<SubmodelElement> submodelElementsCollection = createHierarchicalSubmodelElement();

		operationalData.setSubmodelElements(submodelElementsCollection);

		SubmodelElement submodelElement = getSubmodelService(operationalData).getSubmodelElement("test.testList[0].testProperty");

		assertTrue(submodelElement instanceof DefaultProperty);
	}

	@Test(expected = ElementDoesNotExistException.class)
	public void getNonExistentHierachicalSubmodelElementValue() {
		Submodel operationalData = DummySubmodelFactory.createOperationalDataSubmodelWithHierarchicalSubmodelElements();
		String idShortPath = generateNonExistentIdShortPath();
		getSubmodelService(operationalData).getSubmodelElementValue(idShortPath);
	}

	@Test
	public void setHierachicalSubmodelElementValue() {
		String expected = "205";
		Submodel operationalData = DummySubmodelFactory.createOperationalDataSubmodelWithHierarchicalSubmodelElements();
		String idShortPath = generateIdShortPath();
		Property submodelElement = (Property) getSubmodelService(operationalData).getSubmodelElement(idShortPath);
		submodelElement.setValue(expected);
		submodelElement = (Property) getSubmodelService(operationalData).getSubmodelElement(idShortPath);
		assertEquals(expected, submodelElement.getValue());
	}

	@Test(expected = ElementDoesNotExistException.class)
	public void setNonExistentHierachicalSubmodelElementValue() {
		String expected = "205";
		Submodel operationalData = DummySubmodelFactory.createOperationalDataSubmodelWithHierarchicalSubmodelElements();
		String idShortPath = generateNonExistentIdShortPath();
		Property submodelElement = (Property) getSubmodelService(operationalData).getSubmodelElement(idShortPath);
		submodelElement.setValue(expected);
	}

	@Test
	public void getPropertyValue() {
		Submodel technicalData = DummySubmodelFactory.createTechnicalDataSubmodel();

		String expected = ((Property) SubmodelServiceHelper.getDummySubmodelElement(technicalData, SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_PROPERTY_ID_SHORT)).getValue();

		PropertyValue submodelElementValue = (PropertyValue) getSubmodelService(technicalData).getSubmodelElementValue(SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_PROPERTY_ID_SHORT);

		assertEquals(expected, submodelElementValue.getValue());
	}

	@Test(expected = ElementDoesNotExistException.class)
	public void getNonExistingSubmodelElementValue() {
		Submodel technicalData = DummySubmodelFactory.createTechnicalDataSubmodel();

		getSubmodelService(technicalData).getSubmodelElementValue("nonExisting");
	}

	@Test
	public void setPropertyValue() {
		Submodel technicalData = DummySubmodelFactory.createTechnicalDataSubmodel();
		SubmodelService smService = getSubmodelService(technicalData);

		String expected = "200";

		PropertyValue submodelElementValue = new PropertyValue(expected);

		smService.setSubmodelElementValue(SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_PROPERTY_ID_SHORT, submodelElementValue);

		PropertyValue propertyElementValue = (PropertyValue) smService.getSubmodelElementValue(SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_PROPERTY_ID_SHORT);

		assertEquals(expected, propertyElementValue.getValue());
	}

	@Test(expected = ElementDoesNotExistException.class)
	public void setNonExistingSubmodelElementValue() {
		Submodel technicalData = DummySubmodelFactory.createTechnicalDataSubmodel();

		String expected = "doesNotMatter";

		PropertyValue submodelElementValue = new PropertyValue(expected);

		getSubmodelService(technicalData).setSubmodelElementValue("nonExisting", submodelElementValue);
	}

	@Test
	public void getRangeValue() {
		Submodel technicalData = DummySubmodelFactory.createTechnicalDataSubmodel();

		Range expected = ((Range) SubmodelServiceHelper.getDummySubmodelElement(technicalData, SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_RANGE_ID_SHORT));

		RangeValue retrievedValue = (RangeValue) getSubmodelService(technicalData).getSubmodelElementValue(SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_RANGE_ID_SHORT);

		assertEquals(expected.getMin(), String.valueOf(retrievedValue.getMin()));

		assertEquals(expected.getMax(), String.valueOf(retrievedValue.getMax()));
	}

	@Test
	public void getMultiLanguagePropertyValue() {
		Submodel technicalData = DummySubmodelFactory.createTechnicalDataSubmodel();

		List<LangStringTextType> expectedValue = Arrays.asList(new DefaultLangStringTextType.Builder().text("Hello")
				.language("en")
				.build(),
				new DefaultLangStringTextType.Builder().text("Hallo")
						.language("de")
						.build());

		MultiLanguagePropertyValue submodelElementValue = (MultiLanguagePropertyValue) getSubmodelService(technicalData).getSubmodelElementValue(SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_MULTI_LANG_PROP_ID_SHORT);

		assertEquals(expectedValue, submodelElementValue.getValue());
	}

	@Test
	public void getFileValue() {
		Submodel technicalData = DummySubmodelFactory.createTechnicalDataSubmodel();

		String expectedValue = ((File) SubmodelServiceHelper.getDummySubmodelElement(technicalData, SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_FILE_ID_SHORT)).getValue();

		FileBlobValue submodelElementValue = (FileBlobValue) getSubmodelService(technicalData).getSubmodelElementValue(SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_FILE_ID_SHORT);

		assertEquals(expectedValue, submodelElementValue.getValue());
	}

	@Test
	public void createSubmodelElement() {
		Submodel technicalData = DummySubmodelFactory.createTechnicalDataSubmodel();
		Property property = new DefaultProperty();
		property.setIdShort("test123");
		property.setValue("205");

		SubmodelService submodelService = getSubmodelService(technicalData);
		submodelService.createSubmodelElement(property);

		SubmodelElement submodelEl = submodelService.getSubmodelElement("test123");
		assertEquals("test123", submodelEl.getIdShort());
	}

	@Test(expected = ElementDoesNotExistException.class)
	public void deleteSubmodelElement() {
		Submodel technicalData = DummySubmodelFactory.createTechnicalDataSubmodel();
		SubmodelService submodelService = getSubmodelService(technicalData);
		submodelService.deleteSubmodelElement("test123");

		try {
			submodelService.getSubmodelElement("test123");
			fail();
		} catch (ElementDoesNotExistException expected) {
		}
	}

	@Test
	public void createNestedSubmodelElement() {
		Submodel operationDataSubmodel = DummySubmodelFactory.createOperationalDataSubmodelWithHierarchicalSubmodelElements();
		SubmodelService submodelService = getSubmodelService(operationDataSubmodel);

		Property propertyInSmeCol = new DefaultProperty.Builder().idShort("test123")
				.category("cat1")
				.value("305")
				.valueType(DataTypeDefXsd.INTEGER)
				.build();

		Property propertyInSmeList = new DefaultProperty.Builder().idShort("test456")
				.category("cat1")
				.value("305")
				.valueType(DataTypeDefXsd.INTEGER)
				.build();

		String idShortPathPropertyInSmeCol = DummySubmodelFactory.SUBMODEL_OPERATIONAL_DATA_ELEMENT_COLLECTION_ID_SHORT;
		submodelService.createSubmodelElement(idShortPathPropertyInSmeCol, propertyInSmeCol);

		String idShortPathPropertyInSmeList = DummySubmodelFactory.SUBMODEL_OPERATIONAL_DATA_ELEMENT_COLLECTION_ID_SHORT + "." + DummySubmodelFactory.SUBMODEL_OPERATIONAL_DATA_ELEMENT_LIST_ID_SHORT;
		submodelService.createSubmodelElement(idShortPathPropertyInSmeList, propertyInSmeList);

		idShortPathPropertyInSmeCol = idShortPathPropertyInSmeCol.concat(".test123");
		SubmodelElement propertyInCollectionCreated = submodelService.getSubmodelElement(idShortPathPropertyInSmeCol);
		assertEquals("test123", propertyInCollectionCreated.getIdShort());

		idShortPathPropertyInSmeList = idShortPathPropertyInSmeList.concat("[1]");
		SubmodelElement propertyInSmeListCreated = submodelService.getSubmodelElement(idShortPathPropertyInSmeList);
		assertEquals("test456", propertyInSmeListCreated.getIdShort());
	}

	@Test(expected = ElementDoesNotExistException.class)
	public void deleteNestedSubmodelElementInSubmodelElementCollection() {
		Submodel operationDataSubmodel = DummySubmodelFactory.createOperationalDataSubmodelWithHierarchicalSubmodelElements();
		SubmodelService submodelService = getSubmodelService(operationDataSubmodel);

		String idShortPathPropertyInSmeCol = DummySubmodelFactory.SUBMODEL_OPERATIONAL_DATA_ELEMENT_COLLECTION_ID_SHORT + DummySubmodelFactory.SUBMODEL_ELEMENT_SECOND_ID_SHORT;

		submodelService.deleteSubmodelElement(idShortPathPropertyInSmeCol);

		try {
			submodelService.getSubmodelElement(idShortPathPropertyInSmeCol);
			fail();
		} catch (ElementDoesNotExistException expected) {
			throw expected;
		}
	}

	@Test(expected = ElementDoesNotExistException.class)
	public void deleteNestedSubmodelElementInSubmodelElementList() {
		Submodel operationDataSubmodel = DummySubmodelFactory.createOperationalDataSubmodelWithHierarchicalSubmodelElements();
		SubmodelService submodelService = getSubmodelService(operationDataSubmodel);

		submodelService.deleteSubmodelElement(generateIdShortPath());

		try {
			submodelService.getSubmodelElement(generateIdShortPath());
			fail();
		} catch (ElementDoesNotExistException expected) {
			throw expected;
		}
	}

	@Test
	public void getPaginatedSubmodelElement() {
		Submodel technicalData = DummySubmodelFactory.createTechnicalDataSubmodel();
		SubmodelService submodelService = getSubmodelService(technicalData);
		CursorResult<List<SubmodelElement>> cursorResult = submodelService
				.getSubmodelElements(new PaginationInfo(1, ""));
		assertEquals(1, cursorResult.getResult().size());
	}

	@Test
	public void paginationCursor() {
		Submodel technicalData = DummySubmodelFactory.createTechnicalDataSubmodel();
		SubmodelService submodelService = getSubmodelService(technicalData);
		CursorResult<List<SubmodelElement>> cursorResult = submodelService.getSubmodelElements(new PaginationInfo(1,
				SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_ANNOTATED_RELATIONSHIP_ELEMENT_ID_SHORT));
		assertEquals(SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_BLOB_ID_SHORT, cursorResult.getCursor());
	}

	// Has to be overwritten if backend does not support operations
	@Test
	public void invokeOperation() {
		Submodel invokableSubmodel = DummySubmodelFactory.createSubmodelWithAllSubmodelElements();
		SubmodelService submodelService = getSubmodelService(invokableSubmodel);
		
		Property val = new DefaultProperty.Builder().idShort("in").value("2").build();
		
		OperationVariable[] result = submodelService.invokeOperation(SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_OPERATION_ID, new OperationVariable[] { SubmodelServiceHelper.createOperationVariable(val) });

		Property ret = (Property) result[0].getValue();
		
		assertEquals("4", ret.getValue());
	}

	// Has to be overwritten if backend does not support operations
	@Test(expected = NotInvokableException.class)
	public void invokeNonOperation() {
		Submodel invokableSubmodel = DummySubmodelFactory.createSubmodelWithAllSubmodelElements();
		SubmodelService submodelService = getSubmodelService(invokableSubmodel);

		submodelService.invokeOperation(SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_ANNOTATED_RELATIONSHIP_ELEMENT_ID_SHORT, new OperationVariable[0]);
	}

	private List<SubmodelElement> createHierarchicalSubmodelElement() {
		List<SubmodelElement> submodelElementsCollection = new ArrayList<>();

		SubmodelElementCollection submodelElementCollection = createDummySubmodelElementCollection("test");

		SubmodelElementList submodelElementList = createDummySubmodelElementList("testList");

		Property testProperty = createDummyProperty("testProperty");

		Entity entity = createDummyEntityWithStatement(testProperty, "entityIdShort");

		submodelElementList.setValue(Arrays.asList(entity));

		submodelElementCollection.setValue(Arrays.asList(submodelElementList));

		submodelElementsCollection.add(submodelElementCollection);

		return submodelElementsCollection;
	}

	private String generateIdShortPath() {
		return DummySubmodelFactory.SUBMODEL_OPERATIONAL_DATA_ELEMENT_COLLECTION_ID_SHORT + "." + DummySubmodelFactory.SUBMODEL_OPERATIONAL_DATA_ELEMENT_LIST_ID_SHORT + "[0]";
	}

	private String generateNonExistentIdShortPath() {
		return DummySubmodelFactory.SUBMODEL_OPERATIONAL_DATA_ELEMENT_COLLECTION_ID_SHORT + "." + DummySubmodelFactory.SUBMODEL_OPERATIONAL_DATA_ELEMENT_LIST_ID_SHORT + "[1]";
	}

	private String generateNestedIdShortPath() {
		String idShortPath = DummySubmodelFactory.SUBMODEL_ELEMENT_COLLECTION_TOP + "." + DummySubmodelFactory.SUBMODEL_ELEMENT_FIRST_LIST + "[0][0]." + DummySubmodelFactory.SUBMODEL_ELEMENT_FIRST_ID_SHORT;
		return idShortPath;
	}

	private DefaultSubmodelElementList createDummySubmodelElementList(String idShort) {
		return new DefaultSubmodelElementList.Builder().idShort(idShort)
				.build();
	}

	private SubmodelElementCollection createDummySubmodelElementCollection(String idShort) {
		return new DefaultSubmodelElementCollection.Builder().idShort(idShort)
				.build();
	}

	private DefaultEntity createDummyEntityWithStatement(SubmodelElement submodelElement, String idShort) {
		return new DefaultEntity.Builder().idShort(idShort)
				.category("cat1")
				.statements(submodelElement)
				.build();
	}

	private DefaultProperty createDummyProperty(String idShort) {
		return new DefaultProperty.Builder().idShort(idShort)
				.category("cat1")
				.value("123")
				.valueType(DataTypeDefXsd.INTEGER)
				.build();
	}

}

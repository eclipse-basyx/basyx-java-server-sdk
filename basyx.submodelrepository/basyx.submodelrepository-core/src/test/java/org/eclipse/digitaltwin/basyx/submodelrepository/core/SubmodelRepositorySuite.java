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

package org.eclipse.digitaltwin.basyx.submodelrepository.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.eclipse.digitaltwin.aas4j.v3.model.DataTypeDefXSD;
import org.eclipse.digitaltwin.aas4j.v3.model.OperationVariable;
import org.eclipse.digitaltwin.aas4j.v3.model.Property;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultProperty;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSubmodel;
import org.eclipse.digitaltwin.basyx.core.exceptions.CollidingIdentifierException;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementNotAFileException;
import org.eclipse.digitaltwin.basyx.core.exceptions.FileDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.exceptions.IdentificationMismatchException;
import org.eclipse.digitaltwin.basyx.core.exceptions.NotInvokableException;
import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.eclipse.digitaltwin.basyx.submodelrepository.SubmodelRepository;
import org.eclipse.digitaltwin.basyx.submodelservice.DummySubmodelFactory;
import org.eclipse.digitaltwin.basyx.submodelservice.SubmodelServiceHelper;
import org.eclipse.digitaltwin.basyx.submodelservice.value.PropertyValue;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

/**
 * Testsuite for implementations of the SubmodelRepository interface
 * 
 * @author schnicke, danish, kammognie, zhangzai
 *
 */
public abstract class SubmodelRepositorySuite {
	private static final PaginationInfo NO_LIMIT_PAGINATION_INFO = new PaginationInfo(0, null);
	private static final String DUMMY_FILE_CONTENT = "this is a file";

	protected abstract SubmodelRepository getSubmodelRepository();

	protected abstract SubmodelRepository getSubmodelRepository(Collection<Submodel> submodels);

	@Test
	public void getAllSubmodelsPreconfigured() {
		Collection<Submodel> expectedSubmodels = DummySubmodelFactory.getSubmodels();

		SubmodelRepository repo = getSubmodelRepository(expectedSubmodels);
		Collection<Submodel> submodels = repo.getAllSubmodels(NO_LIMIT_PAGINATION_INFO).getResult();

		assertSubmodelsAreContained(expectedSubmodels, submodels);
	}

	private void assertSubmodelsAreContained(Collection<Submodel> expectedSubmodels, Collection<Submodel> submodels) {
		assertEquals(3, submodels.size());
		assertTrue(submodels.containsAll(expectedSubmodels));
	}

	@Test
	public void getAllSubmodelsEmpty() {
		SubmodelRepository repo = getSubmodelRepository();
		Collection<Submodel> submodels = repo.getAllSubmodels(NO_LIMIT_PAGINATION_INFO).getResult();

		assertIsEmpty(submodels);
	}

	@Test
	public void getSpecificSubmodel() {
		SubmodelRepository repo = getSubmodelRepositoryWithDummySubmodels();

		Submodel operationalDataSm = DummySubmodelFactory.createOperationalDataSubmodel();
		Submodel retrieved = repo.getSubmodel(operationalDataSm.getId());

		assertEquals(operationalDataSm, retrieved);
	}

	@Test(expected = ElementDoesNotExistException.class)
	public void getSpecificNonExistingSubmodel() {
		SubmodelRepository repo = getSubmodelRepositoryWithDummySubmodels();
		repo.getSubmodel("doesNotExist");
	}

	@Test
	public void updateExistingSubmodel() {
		String id = DummySubmodelFactory.SUBMODEL_OPERATIONAL_DATA_ID;
		Submodel expected = buildDummySubmodel(id);

		SubmodelRepository repo = getSubmodelRepositoryWithDummySubmodels();
		repo.updateSubmodel(id, expected);

		assertEquals(expected, repo.getSubmodel(id));
	}

	@Test(expected = ElementDoesNotExistException.class)
	public void updateNonExistingSubmodel() {
		String id = "notExisting";
		Submodel doesNotExist = buildDummySubmodel(id);

		SubmodelRepository repo = getSubmodelRepositoryWithDummySubmodels();
		repo.updateSubmodel(id, doesNotExist);
	}

	@Test(expected = IdentificationMismatchException.class)
	public void updateExistingSubmodelWithMismatchId() {
		String id = DummySubmodelFactory.SUBMODEL_OPERATIONAL_DATA_ID;
		Submodel newSm = buildDummySubmodel("mismatchId");

		SubmodelRepository repo = getSubmodelRepositoryWithDummySubmodels();
		repo.updateSubmodel(id, newSm);
	}

	@Test
	public void createSubmodel() {
		String id = "newSubmodel";
		Submodel expectedSubmodel = buildDummySubmodel(id);

		SubmodelRepository repo = getSubmodelRepositoryWithDummySubmodels();
		repo.createSubmodel(expectedSubmodel);

		Submodel retrieved = repo.getSubmodel(id);
		assertEquals(expectedSubmodel, retrieved);
	}

	@Test(expected = CollidingIdentifierException.class)
	public void createSubmodelWithCollidingId() {
		SubmodelRepository repo = getSubmodelRepositoryWithDummySubmodels();
		Submodel submodel = repo.getSubmodel(DummySubmodelFactory.SUBMODEL_OPERATIONAL_DATA_ID);

		repo.createSubmodel(submodel);
	}

	@Test
	public void deleteSubmodel() {
		SubmodelRepository repo = getSubmodelRepositoryWithDummySubmodels();
		repo.deleteSubmodel(DummySubmodelFactory.SUBMODEL_OPERATIONAL_DATA_ID);

		try {
			repo.getSubmodel(DummySubmodelFactory.SUBMODEL_OPERATIONAL_DATA_ID);
			fail();
		} catch (ElementDoesNotExistException expected) {
		}
	}

	@Test(expected = ElementDoesNotExistException.class)
	public void deleteNonExistingSubmodel() {
		SubmodelRepository repo = getSubmodelRepositoryWithDummySubmodels();
		repo.deleteSubmodel("nonExisting");
	}

	@Test
	public void getSubmodelElements() {
		SubmodelRepository repo = getSubmodelRepositoryWithDummySubmodels();
		Collection<SubmodelElement> elements = repo
				.getSubmodelElements(DummySubmodelFactory.SUBMODEL_OPERATIONAL_DATA_ID, NO_LIMIT_PAGINATION_INFO)
				.getResult();
		Collection<SubmodelElement> expectedElements = DummySubmodelFactory.createOperationalDataSubmodel()
				.getSubmodelElements();
		assertEquals(expectedElements, elements);
	}

	@Test(expected = ElementDoesNotExistException.class)
	public void getSubmodelElementsOfNonExistingSubmodel() {
		SubmodelRepository repo = getSubmodelRepositoryWithDummySubmodels();
		repo.getSubmodelElements("notExisting", NO_LIMIT_PAGINATION_INFO).getResult();
	}

	@Test
	public void getSubmodelElement() {
		SubmodelRepository repo = getSubmodelRepositoryWithDummySubmodels();
		SubmodelElement element = repo.getSubmodelElement(DummySubmodelFactory.SUBMODEL_OPERATIONAL_DATA_ID, DummySubmodelFactory.SUBMODEL_OPERATIONAL_DATA_PROPERTY_ID_SHORT);
		SubmodelElement expectedElement = getExpectedSubmodelElement();

		assertEquals(expectedElement, element);
	}

	@Test(expected = ElementDoesNotExistException.class)
	public void getNonExistingSubmodelElement() {
		SubmodelRepository repo = getSubmodelRepositoryWithDummySubmodels();
		repo.getSubmodelElement(DummySubmodelFactory.SUBMODEL_OPERATIONAL_DATA_ID, "nonExisting");
	}

	@Test(expected = ElementDoesNotExistException.class)
	public void getSubmodelElementOfNonExistingSubmodel() {
		SubmodelRepository repo = getSubmodelRepositoryWithDummySubmodels();
		repo.getSubmodelElement("nonExisting", "doesNotMatter");
	}

	@Test
	public void getPropertyValue() {
		SubmodelRepository repo = getSubmodelRepositoryWithDummySubmodels();
		Object expected = ((Property) getExpectedSubmodelElement()).getValue();
		Object value = repo.getSubmodelElementValue(DummySubmodelFactory.SUBMODEL_OPERATIONAL_DATA_ID, DummySubmodelFactory.SUBMODEL_OPERATIONAL_DATA_PROPERTY_ID_SHORT);

		assertEquals(expected, ((PropertyValue) value).getValue());
	}

	@Test(expected = ElementDoesNotExistException.class)
	public void getNonExistingSubmodelElementValue() {
		SubmodelRepository repo = getSubmodelRepositoryWithDummySubmodels();
		repo.getSubmodelElementValue(DummySubmodelFactory.SUBMODEL_OPERATIONAL_DATA_ID, "nonExisting");
	}

	@Test(expected = ElementDoesNotExistException.class)
	public void getSubmodelElementValueOfNonExistingSubmodel() {
		SubmodelRepository repo = getSubmodelRepositoryWithDummySubmodels();
		repo.getSubmodelElementValue("nonExisting", "doesNotMatter");
	}

	@Test
	public void setPropertyValue() {
		SubmodelRepository repo = getSubmodelRepositoryWithDummySubmodels();
		String expected = "200";

		PropertyValue valueToWrite = new PropertyValue(expected);

		repo.setSubmodelElementValue(DummySubmodelFactory.SUBMODEL_OPERATIONAL_DATA_ID, DummySubmodelFactory.SUBMODEL_OPERATIONAL_DATA_PROPERTY_ID_SHORT, valueToWrite);
		PropertyValue retrievedValue = (PropertyValue) repo.getSubmodelElementValue(DummySubmodelFactory.SUBMODEL_OPERATIONAL_DATA_ID, DummySubmodelFactory.SUBMODEL_OPERATIONAL_DATA_PROPERTY_ID_SHORT);

		assertEquals(expected, retrievedValue.getValue());
	}
	
	@Test
	public void updateFile() {
		SubmodelRepository repo = getSubmodelRepositoryWithDummySubmodels();
		
		// Set the value of the file-submodelelement for the first time
		try {
			repo.setFileValue(DummySubmodelFactory.SUBMODEL_TECHNICAL_DATA_ID, SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_FILE_ID_SHORT, "SampleJsonFile.json", getInputStreamOfFileFromClasspath("SampleJsonFile.json"));
		} catch (IOException e1) {
			fail();
			e1.printStackTrace();
		}
		
		// Set the value of the file-submodel element again with a dummy text file
		try {
			repo.setFileValue(DummySubmodelFactory.SUBMODEL_TECHNICAL_DATA_ID, SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_FILE_ID_SHORT, "newFile.txt", getInputStreamOfDummyFile());
		} catch (IOException e1) {
			fail();
			e1.printStackTrace();
		}
		
		// Get the file from the file submodel element
		File retrievedValue = repo.getFileByPathSubmodel(DummySubmodelFactory.SUBMODEL_TECHNICAL_DATA_ID, SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_FILE_ID_SHORT);
		
		try {
			String actual = new String(FileUtils.openInputStream(retrievedValue).readAllBytes());
			assertEquals(DUMMY_FILE_CONTENT, actual);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void getFile(){
		SubmodelRepository repo = getSubmodelRepositoryWithDummySubmodels();
		String expectedFileExtension = "json";
		
		InputStream expectedFile = null;
		try {
			expectedFile = getInputStreamOfFileFromClasspath("SampleJsonFile.json");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		try {
			repo.setFileValue(DummySubmodelFactory.SUBMODEL_TECHNICAL_DATA_ID, SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_FILE_ID_SHORT, "SampleJsonFile.json", getInputStreamOfFileFromClasspath("SampleJsonFile.json"));
		} catch (IOException e) {
			fail();
			e.printStackTrace();
		}

		File retrievedValue = repo.getFileByPathSubmodel(DummySubmodelFactory.SUBMODEL_TECHNICAL_DATA_ID, SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_FILE_ID_SHORT);

		assertEquals(expectedFileExtension, getExtension(retrievedValue.getName()));
		
		try {
			assertTrue(IOUtils.contentEquals(expectedFile, FileUtils.openInputStream(retrievedValue)));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test(expected = FileDoesNotExistException.class)
	public void getNonExistingFile(){
		SubmodelRepository repo = getSubmodelRepositoryWithDummySubmodels();
		deleteFileIfExisted(repo);
		
		repo.getFileByPathSubmodel(DummySubmodelFactory.SUBMODEL_TECHNICAL_DATA_ID, SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_FILE_ID_SHORT);
	}


	@Test(expected = ElementNotAFileException.class)
	public void getFileFromNonFileSME(){
		SubmodelRepository repo = getSubmodelRepositoryWithDummySubmodels();
		
		repo.getFileByPathSubmodel(DummySubmodelFactory.SUBMODEL_TECHNICAL_DATA_ID, SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_MULTI_LANG_PROP_ID_SHORT);
	}
	
	@Test
	public void deleteFile() {
		SubmodelRepository repo = getSubmodelRepositoryWithDummySubmodels();
		
		try {
			repo.setFileValue(DummySubmodelFactory.SUBMODEL_TECHNICAL_DATA_ID, SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_FILE_ID_SHORT, "SampleJsonFile.json", getInputStreamOfFileFromClasspath("SampleJsonFile.json"));
		} catch (IOException e1) {
			fail();
			e1.printStackTrace();
		} 
		
		repo.deleteFileValue(DummySubmodelFactory.SUBMODEL_TECHNICAL_DATA_ID, SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_FILE_ID_SHORT);
		
		try {
			repo.getFileByPathSubmodel(DummySubmodelFactory.SUBMODEL_TECHNICAL_DATA_ID, SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_FILE_ID_SHORT);
			fail();
		} catch (Exception e) {
		}
	}
	
	@Test(expected = FileDoesNotExistException.class)
	public void deleteNonExistingFile() throws IOException {
		SubmodelRepository repo = getSubmodelRepositoryWithDummySubmodels();
		deleteFileIfExisted(repo);
		
		repo.deleteFileValue(DummySubmodelFactory.SUBMODEL_TECHNICAL_DATA_ID, SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_FILE_ID_SHORT);
	}

	@Test(expected = ElementDoesNotExistException.class)
	public void setNonExistingSubmodelElementValue() {
		SubmodelRepository repo = getSubmodelRepositoryWithDummySubmodels();

		PropertyValue valueToWrite = new PropertyValue("400");

		repo.setSubmodelElementValue(DummySubmodelFactory.SUBMODEL_OPERATIONAL_DATA_ID, "nonExisting", valueToWrite);
	}

	@Test(expected = ElementDoesNotExistException.class)
	public void setSubmodelElementValueOfNonExistingSubmodel() {
		SubmodelRepository repo = getSubmodelRepositoryWithDummySubmodels();

		PropertyValue valueToWrite = new PropertyValue("400");

		repo.setSubmodelElementValue("nonExisting", "doesNotMatter", valueToWrite);
	}

	@Test
	public void createSubmodelElement() {
		SubmodelRepository repo = getSubmodelRepositoryWithDummySubmodels();

		Property property = new DefaultProperty.Builder().idShort("test321")
				.category("cat1")
				.value("305")
				.valueType(DataTypeDefXSD.INTEGER)
				.build();
		repo.createSubmodelElement(DummySubmodelFactory.SUBMODEL_SIMPLE_DATA_ID, property);

		SubmodelElement sme = repo.getSubmodelElement(DummySubmodelFactory.SUBMODEL_SIMPLE_DATA_ID, "test321");
		assertEquals("test321", sme.getIdShort());

	}

	@Test(expected = ElementDoesNotExistException.class)
	public void deleteSubmodeleElement() {
		SubmodelRepository repo = getSubmodelRepositoryWithDummySubmodels();
		repo.deleteSubmodelElement(DummySubmodelFactory.SUBMODEL_SIMPLE_DATA_ID, "test123");

		try {
			repo.getSubmodelElement(DummySubmodelFactory.SUBMODEL_SIMPLE_DATA_ID, "test123");
			fail();
		} catch (ElementDoesNotExistException expected) {
		}
	}

	@Test
	public void createNestedSubmodelELement() {
		SubmodelRepository repo = getSubmodelRepositoryWithDummySubmodels();
		Property propertyInCollection = new DefaultProperty.Builder().idShort("test654")
				.category("cat1")
				.value("305")
				.valueType(DataTypeDefXSD.INTEGER)
				.build();
		Property propertyInList = new DefaultProperty.Builder().idShort("test987")
				.category("cat1")
				.value("305")
				.valueType(DataTypeDefXSD.INTEGER)
				.build();

		String idShortPathPropertyInSmeCol = DummySubmodelFactory.SUBMODEL_ELEMENT_COLLECTION_SIMPLE;
		String idShortPathPropertyInSmeList = DummySubmodelFactory.SUBMODEL_ELEMENT_LIST_SIMPLE;
		repo.createSubmodelElement(DummySubmodelFactory.SUBMODEL_SIMPLE_DATA_ID, idShortPathPropertyInSmeCol, propertyInCollection);
		repo.createSubmodelElement(DummySubmodelFactory.SUBMODEL_SIMPLE_DATA_ID, idShortPathPropertyInSmeList, propertyInList);

		idShortPathPropertyInSmeCol = idShortPathPropertyInSmeCol.concat(".test654");
		SubmodelElement smeInCollection = repo.getSubmodelElement(DummySubmodelFactory.SUBMODEL_SIMPLE_DATA_ID, idShortPathPropertyInSmeCol);
		assertEquals("test654", smeInCollection.getIdShort());

		idShortPathPropertyInSmeList = idShortPathPropertyInSmeList.concat("[1]");
		SubmodelElement propertyInSmeListCreated = repo.getSubmodelElement(DummySubmodelFactory.SUBMODEL_SIMPLE_DATA_ID, idShortPathPropertyInSmeList);
		assertEquals("test987", propertyInSmeListCreated.getIdShort());
	}

	@Test(expected = ElementDoesNotExistException.class)
	public void deleteNestedSubmodelElementInSubmodelElementCollection() {
		SubmodelRepository repo = getSubmodelRepositoryWithDummySubmodels();

		String idShortPathPropertyInSmeCol = DummySubmodelFactory.SUBMODEL_OPERATIONAL_DATA_ELEMENT_COLLECTION_ID_SHORT + DummySubmodelFactory.SUBMODEL_ELEMENT_SECOND_ID_SHORT;

		repo.deleteSubmodelElement(DummySubmodelFactory.SUBMODEL_OPERATIONAL_DATA_ID, idShortPathPropertyInSmeCol);

		try {
			repo.getSubmodelElement(DummySubmodelFactory.SUBMODEL_OPERATIONAL_DATA_ID, idShortPathPropertyInSmeCol);
			fail();
		} catch (ElementDoesNotExistException expected) {
			throw expected;
		}
	}

	@Test(expected = ElementDoesNotExistException.class)
	public void deleteNestedSubmodelElementInSubmodelElementList() {
		SubmodelRepository repo = getSubmodelRepositoryWithDummySubmodels();

		repo.deleteSubmodelElement(DummySubmodelFactory.SUBMODEL_OPERATIONAL_DATA_ID, generateIdShortPath());

		try {
			repo.getSubmodelElement(DummySubmodelFactory.SUBMODEL_OPERATIONAL_DATA_ID, generateIdShortPath());
			fail();
		} catch (ElementDoesNotExistException expected) {
			throw expected;
		}
	}
	
	@Test
	public void getDefaultSubmodelRepositoryName() {
		SubmodelRepository repo = getSubmodelRepository();
		
		assertEquals("sm-repo", repo.getName());
	}

	@Test
	public void getPaginatedSubmodel() {
		Collection<Submodel> expectedSubmodels = DummySubmodelFactory.getSubmodels();

		SubmodelRepository repo = getSubmodelRepository(expectedSubmodels);
		CursorResult<List<Submodel>> cursorResult = repo
				.getAllSubmodels(new PaginationInfo(1, ""));
		assertEquals(1, cursorResult.getResult().size());
	}

	// Has to be overwritten if backend does not support operations
	@Test
	public void invokeOperation() {
		SubmodelRepository submodelRepo = getSubmodelRepositoryWithDummySubmodels();

		Property val = new DefaultProperty.Builder().idShort("in").value("2").build();

		OperationVariable[] result = submodelRepo.invokeOperation(DummySubmodelFactory.SUBMODEL_TECHNICAL_DATA_ID, SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_OPERATION_ID,
				new OperationVariable[] { SubmodelServiceHelper.createOperationVariable(val) });

		Property ret = (Property) result[0].getValue();

		assertEquals("4", ret.getValue());
	}

	// Has to be overwritten if backend does not support operations
	@Test(expected = NotInvokableException.class)
	public void invokeNonOperation() {
		SubmodelRepository submodelRepo = getSubmodelRepositoryWithDummySubmodels();

		submodelRepo.invokeOperation(DummySubmodelFactory.SUBMODEL_TECHNICAL_DATA_ID, SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_ANNOTATED_RELATIONSHIP_ELEMENT_ID_SHORT, new OperationVariable[0]);
	}
	
	private void deleteFileIfExisted(SubmodelRepository repo) {
		try {
			repo.getFileByPathSubmodel(DummySubmodelFactory.SUBMODEL_TECHNICAL_DATA_ID, SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_FILE_ID_SHORT);
			repo.deleteFileValue(DummySubmodelFactory.SUBMODEL_TECHNICAL_DATA_ID, SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_FILE_ID_SHORT);
		}catch(FileDoesNotExistException e) {
			return;
		}
		
	}

	private SubmodelElement getExpectedSubmodelElement() {
		return DummySubmodelFactory.createOperationalDataSubmodel()
				.getSubmodelElements()
				.stream()
				.filter(sme -> sme.getIdShort()
						.equals(DummySubmodelFactory.SUBMODEL_OPERATIONAL_DATA_PROPERTY_ID_SHORT))
				.findAny()
				.get();
	}

	private Submodel buildDummySubmodel(String id) {
		return new DefaultSubmodel.Builder().id(id)
				.submodelElements(new DefaultProperty.Builder().idShort("prop")
						.value("testValue")
						.valueType(DataTypeDefXSD.STRING)
						.build())
				.build();
	}

	private SubmodelRepository getSubmodelRepositoryWithDummySubmodels() {
		Collection<Submodel> expectedSubmodels = DummySubmodelFactory.getSubmodels();
		SubmodelRepository repo = getSubmodelRepository(expectedSubmodels);
		return repo;
	}

	private void assertIsEmpty(Collection<Submodel> submodels) {
		assertTrue(submodels.isEmpty());
	}

	private String generateIdShortPath() {
		return DummySubmodelFactory.SUBMODEL_OPERATIONAL_DATA_ELEMENT_COLLECTION_ID_SHORT + "." + DummySubmodelFactory.SUBMODEL_OPERATIONAL_DATA_ELEMENT_LIST_ID_SHORT + "[0]";
	}
	
	private InputStream getInputStreamOfFileFromClasspath(String fileName) throws FileNotFoundException, IOException {
		ClassPathResource classPathResource = new ClassPathResource(fileName);
		
		return classPathResource.getInputStream();
	}
	
	private InputStream getInputStreamOfDummyFile() throws FileNotFoundException, IOException {
		return new ByteArrayInputStream(DUMMY_FILE_CONTENT.getBytes());
	}
	
	private String getExtension(String filename) {
	    return FilenameUtils.getExtension(filename);
	}

}

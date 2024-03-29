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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.eclipse.digitaltwin.aas4j.v3.model.DataTypeDefXsd;
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
import org.eclipse.digitaltwin.basyx.core.exceptions.MissingIdentifierException;
import org.eclipse.digitaltwin.basyx.core.exceptions.NotInvokableException;
import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.eclipse.digitaltwin.basyx.submodelrepository.SubmodelRepository;
import org.eclipse.digitaltwin.basyx.submodelservice.DummySubmodelFactory;
import org.eclipse.digitaltwin.basyx.submodelservice.SubmodelService;
import org.eclipse.digitaltwin.basyx.submodelservice.SubmodelServiceHelper;
import org.eclipse.digitaltwin.basyx.submodelservice.SubmodelServiceSuite;
import org.eclipse.digitaltwin.basyx.submodelservice.value.PropertyValue;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

/**
 * Testsuite for implementations of the SubmodelRepository interface
 * 
 * @author schnicke, danish, kammognie, zhangzai, mateusmolina
 *
 */
public abstract class SubmodelRepositorySuite extends SubmodelServiceSuite {
	private static final PaginationInfo NO_LIMIT_PAGINATION_INFO = new PaginationInfo(0, null);
	private static final String DUMMY_FILE_CONTENT = "this is a file";
	private static final String EMPTY_ID = " ";
	private static final String NULL_ID = null;

	protected abstract SubmodelRepository getSubmodelRepository();

	protected SubmodelRepository getSubmodelRepository(Collection<Submodel> submodels) {
		SubmodelRepository repo = getSubmodelRepository();
		submodels.forEach(repo::createSubmodel);
		return repo;
	}
	
	protected abstract boolean fileExistsInStorage(String fileValue);

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

	@Test(expected = MissingIdentifierException.class)
	public void createSubmodelWithEmptyId() {
		SubmodelRepository repo = getSubmodelRepository();
		Submodel submodel = buildDummySubmodel(EMPTY_ID);

		repo.createSubmodel(submodel);
	}

	@Test(expected = MissingIdentifierException.class)
	public void createSubmodelWithNullId() {
		SubmodelRepository repo = getSubmodelRepository();
		Submodel submodel = buildDummySubmodel(NULL_ID);

		repo.createSubmodel(submodel);
	}

	@Test(expected = MissingIdentifierException.class)
	public void createSubmodelCollectionWithMissingId() {
		Collection<Submodel> submodels = Arrays.asList(buildDummySubmodel(EMPTY_ID), buildDummySubmodel(DummySubmodelFactory.SUBMODEL_OPERATIONAL_DATA_ID), buildDummySubmodel(NULL_ID));

		getSubmodelRepository(submodels);
	}
	
	@Test
	public void updateNonFileSME() {
		SubmodelRepository repo = getSubmodelRepositoryWithDummySubmodels();
		
		String idShortPathPropertyInSmeCol = SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_SUBMODEL_ELEMENT_COLLECTION_ID_SHORT + "." + SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_PROPERTY_ID_SHORT;
		
		Property newProperty = SubmodelServiceHelper.createDummyProperty(SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_PROPERTY_ID_SHORT, "arbitraryValue", DataTypeDefXsd.STRING);
		
		repo.updateSubmodelElement(DummySubmodelFactory.SUBMODEL_TECHNICAL_DATA_ID, idShortPathPropertyInSmeCol, newProperty);
		
		Property updatedProperty = (Property) repo.getSubmodelElement(DummySubmodelFactory.SUBMODEL_TECHNICAL_DATA_ID, idShortPathPropertyInSmeCol);
		
		assertEquals(newProperty, updatedProperty);
	}
	
	@Test
	public void updateNonFileSMEWithFileSME() {
		SubmodelRepository repo = getSubmodelRepositoryWithDummySubmodels();
		
		String idShortPathPropertyInSmeCol = SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_SUBMODEL_ELEMENT_COLLECTION_ID_SHORT + "." + SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_PROPERTY_ID_SHORT;
		
		org.eclipse.digitaltwin.aas4j.v3.model.File newFileSME = SubmodelServiceHelper.createDummyFile(SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_PROPERTY_ID_SHORT, "text/plain", "arbitraryFileValue");
		
		repo.updateSubmodelElement(DummySubmodelFactory.SUBMODEL_TECHNICAL_DATA_ID, idShortPathPropertyInSmeCol, newFileSME);
		
		org.eclipse.digitaltwin.aas4j.v3.model.File updatedFile = (org.eclipse.digitaltwin.aas4j.v3.model.File) repo.getSubmodelElement(DummySubmodelFactory.SUBMODEL_TECHNICAL_DATA_ID, idShortPathPropertyInSmeCol);
		
		assertEquals(newFileSME, updatedFile);
	}
	
	@Test
	public void updateFileSMEWithNonFileSME() throws FileNotFoundException, IOException {
		SubmodelRepository repo = getSubmodelRepositoryWithDummySubmodels();
		
		String idShortPathPropertyInSmeCol = SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_SUBMODEL_ELEMENT_COLLECTION_ID_SHORT + "." + SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_FILE_ID_SHORT;
		
		String fileName = "SampleJsonFile.json";
		
		repo.setFileValue(DummySubmodelFactory.SUBMODEL_TECHNICAL_DATA_ID, idShortPathPropertyInSmeCol, fileName, getInputStreamOfFileFromClasspath(fileName));
		
		File file = repo.getFileByPathSubmodel(DummySubmodelFactory.SUBMODEL_TECHNICAL_DATA_ID, idShortPathPropertyInSmeCol);
		
		assertFileExistsOnPath(file);
		
		Property newProperty = SubmodelServiceHelper.createDummyProperty(SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_FILE_ID_SHORT, "4005", DataTypeDefXsd.INT);
		
		repo.updateSubmodelElement(DummySubmodelFactory.SUBMODEL_TECHNICAL_DATA_ID, idShortPathPropertyInSmeCol, newProperty);
		
		Property updatedProperty = (Property) repo.getSubmodelElement(DummySubmodelFactory.SUBMODEL_TECHNICAL_DATA_ID, idShortPathPropertyInSmeCol);
		
		assertEquals(newProperty, updatedProperty);
	}
	
	@Test
	public void updateFileSMEWithFileSME() throws FileNotFoundException, IOException {
		SubmodelRepository repo = getSubmodelRepositoryWithDummySubmodels();
		
		String idShortPathPropertyInSmeCol = SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_SUBMODEL_ELEMENT_COLLECTION_ID_SHORT + "." + SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_FILE_ID_SHORT;
		
		String fileName = "SampleJsonFile.json";
		
		repo.setFileValue(DummySubmodelFactory.SUBMODEL_TECHNICAL_DATA_ID, idShortPathPropertyInSmeCol, fileName, getInputStreamOfFileFromClasspath(fileName));
		
		File file = repo.getFileByPathSubmodel(DummySubmodelFactory.SUBMODEL_TECHNICAL_DATA_ID, idShortPathPropertyInSmeCol);
		
		assertFileExistsOnPath(file);
		
		org.eclipse.digitaltwin.aas4j.v3.model.File newFileSME = SubmodelServiceHelper.createDummyFile(SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_FILE_ID_SHORT, "text/plain", "someArbitraryPlainText");
		
		repo.updateSubmodelElement(DummySubmodelFactory.SUBMODEL_TECHNICAL_DATA_ID, idShortPathPropertyInSmeCol, newFileSME);
		
		org.eclipse.digitaltwin.aas4j.v3.model.File updatedFileSME = (org.eclipse.digitaltwin.aas4j.v3.model.File) repo.getSubmodelElement(DummySubmodelFactory.SUBMODEL_TECHNICAL_DATA_ID, idShortPathPropertyInSmeCol);
		
		assertEquals(newFileSME, updatedFileSME);
		assertFileExistsOnPath(file);
	}
	
	@Test(expected = ElementDoesNotExistException.class)
	public void updateNonExistingSME() {
		SubmodelRepository repo = getSubmodelRepositoryWithDummySubmodels();
		
		String idShortPathPropertyInSmeCol = SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_SUBMODEL_ELEMENT_COLLECTION_ID_SHORT + ".NonExistingSMEIdShort";
		
		Property newNonExistingProperty = SubmodelServiceHelper.createDummyProperty(SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_PROPERTY_ID_SHORT, "arbitraryPropertyValue", DataTypeDefXsd.STRING);
		
		repo.updateSubmodelElement(DummySubmodelFactory.SUBMODEL_TECHNICAL_DATA_ID, idShortPathPropertyInSmeCol, newNonExistingProperty);
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

	@Test(expected = ElementDoesNotExistException.class)
	public void getSubmodelElementsOfNonExistingSubmodel() {
		SubmodelRepository repo = getSubmodelRepositoryWithDummySubmodels();
		repo.getSubmodelElements("notExisting", NO_LIMIT_PAGINATION_INFO).getResult();
	}

	@Test(expected = ElementDoesNotExistException.class)
	public void getSubmodelElementOfNonExistingSubmodel() {
		SubmodelRepository repo = getSubmodelRepositoryWithDummySubmodels();
		repo.getSubmodelElement("nonExisting", "doesNotMatter");
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
	public void getFile() {
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
	public void getNonExistingFile() {
		SubmodelRepository repo = getSubmodelRepositoryWithDummySubmodels();
		deleteFileIfExisted(repo);

		repo.getFileByPathSubmodel(DummySubmodelFactory.SUBMODEL_TECHNICAL_DATA_ID, SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_FILE_ID_SHORT);
	}

	@Test(expected = ElementNotAFileException.class)
	public void getFileFromNonFileSME() {
		SubmodelRepository repo = getSubmodelRepositoryWithDummySubmodels();

		repo.getFileByPathSubmodel(DummySubmodelFactory.SUBMODEL_TECHNICAL_DATA_ID, SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_MULTI_LANG_PROP_ID_SHORT);
	}

	@Test
	public void deleteFile() throws ElementDoesNotExistException, ElementNotAFileException, FileNotFoundException, IOException {
		SubmodelRepository repo = getSubmodelRepositoryWithDummySubmodels();

		repo.setFileValue(DummySubmodelFactory.SUBMODEL_TECHNICAL_DATA_ID, SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_FILE_ID_SHORT, "SampleJsonFile.json", getInputStreamOfFileFromClasspath("SampleJsonFile.json"));

		repo.deleteFileValue(DummySubmodelFactory.SUBMODEL_TECHNICAL_DATA_ID, SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_FILE_ID_SHORT);

		try {
			repo.getFileByPathSubmodel(DummySubmodelFactory.SUBMODEL_TECHNICAL_DATA_ID, SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_FILE_ID_SHORT);
			fail();
		} catch (FileDoesNotExistException expected) {
		}
	}

	@Test(expected = FileDoesNotExistException.class)
	public void deleteNonExistingFile() throws IOException {
		SubmodelRepository repo = getSubmodelRepositoryWithDummySubmodels();
		deleteFileIfExisted(repo);

		repo.deleteFileValue(DummySubmodelFactory.SUBMODEL_TECHNICAL_DATA_ID, SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_FILE_ID_SHORT);
	}

	@Test(expected = ElementDoesNotExistException.class)
	public void setSubmodelElementValueOfNonExistingSubmodel() {
		SubmodelRepository repo = getSubmodelRepositoryWithDummySubmodels();

		PropertyValue valueToWrite = new PropertyValue("400");

		repo.setSubmodelElementValue("nonExisting", "doesNotMatter", valueToWrite);
	}

	@Test
	public void deleteFileSubmodelElementDeletesFile() throws ElementDoesNotExistException, ElementNotAFileException, FileNotFoundException, IOException {
		SubmodelRepository repo = getSubmodelRepositoryWithDummySubmodels();

		final String filename = "SampleJsonFile.json";

		repo.setFileValue(DummySubmodelFactory.SUBMODEL_TECHNICAL_DATA_ID, SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_FILE_ID_SHORT, filename, getInputStreamOfFileFromClasspath(filename));

		SubmodelElement submodelElement = repo.getSubmodelElement(DummySubmodelFactory.SUBMODEL_TECHNICAL_DATA_ID, SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_FILE_ID_SHORT);
		String fileValue = ((org.eclipse.digitaltwin.aas4j.v3.model.File) submodelElement).getValue();

		assertTrue(fileExistsInStorage(fileValue));

		repo.deleteSubmodelElement(DummySubmodelFactory.SUBMODEL_TECHNICAL_DATA_ID, SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_FILE_ID_SHORT);

		assertFalse(fileExistsInStorage(fileValue));
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
		CursorResult<List<Submodel>> cursorResult = repo.getAllSubmodels(new PaginationInfo(1, ""));
		assertEquals(1, cursorResult.getResult().size());
	}

	// Has to be overwritten if backend does not support operations
	@Test
	public void invokeOperation() {
		SubmodelRepository submodelRepo = getSubmodelRepositoryWithInvokableOperation();

		Property val = new DefaultProperty.Builder().idShort("in").value("2").build();

		OperationVariable[] result = submodelRepo.invokeOperation(DummySubmodelFactory.SUBMODEL_ALL_SUBMODEL_ELEMENTS_ID, SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_OPERATION_ID,
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
	
	private void assertFileExistsOnPath(File file) {
		
		assertTrue(file.exists());
	}

	private void deleteFileIfExisted(SubmodelRepository repo) {
		try {
			repo.getFileByPathSubmodel(DummySubmodelFactory.SUBMODEL_TECHNICAL_DATA_ID, SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_FILE_ID_SHORT);
			repo.deleteFileValue(DummySubmodelFactory.SUBMODEL_TECHNICAL_DATA_ID, SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_FILE_ID_SHORT);
		} catch (FileDoesNotExistException e) {
			return;
		}

	}

	private Submodel buildDummySubmodel(String id) {
		return new DefaultSubmodel.Builder().id(id).submodelElements(new DefaultProperty.Builder().idShort("prop").value("testValue").valueType(DataTypeDefXsd.STRING).build()).build();
	}

	private SubmodelRepository getSubmodelRepositoryWithDummySubmodels() {
		Collection<Submodel> expectedSubmodels = DummySubmodelFactory.getSubmodels();
		SubmodelRepository repo = getSubmodelRepository(expectedSubmodels);
		return repo;
	}

	private SubmodelRepository getSubmodelRepositoryWithInvokableOperation() {
		return getSubmodelRepository(Collections.singleton(DummySubmodelFactory.createSubmodelWithAllSubmodelElements()));
	}

	private void assertIsEmpty(Collection<Submodel> submodels) {
		assertTrue(submodels.isEmpty());
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

	@Override
	public SubmodelService getSubmodelService(Submodel submodel) {
		return new SubmodelRepositorySubmodelServiceWrapper(getSubmodelRepository(), submodel);
	}
}

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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.eclipse.digitaltwin.aas4j.v3.model.DataTypeDefXsd;
import org.eclipse.digitaltwin.aas4j.v3.model.OperationVariable;
import org.eclipse.digitaltwin.aas4j.v3.model.Property;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultProperty;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSubmodel;
import org.eclipse.digitaltwin.basyx.core.exceptions.CollidingIdentifierException;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;
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
import org.eclipse.digitaltwin.basyx.submodelservice.value.SubmodelValueOnly;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Testsuite for implementations of the SubmodelRepository interface
 * 
 * @author schnicke, danish, kammognie, zhangzai, mateusmolina
 *
 */
public abstract class SubmodelRepositorySuite extends SubmodelServiceSuite {
	private static final PaginationInfo NO_LIMIT_PAGINATION_INFO = new PaginationInfo(null, null);
	private static final String EMPTY_ID = " ";
	private static final String NULL_ID = null;
	private static final String ID = "testId";

	protected abstract SubmodelRepository getSubmodelRepository();

	protected SubmodelRepository getSubmodelRepository(Collection<Submodel> submodels) {
		SubmodelRepository repo = getSubmodelRepository();
		submodels.forEach(repo::createSubmodel);
		return repo;
	}

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

	@Test(expected = ElementDoesNotExistException.class)
	public void setSubmodelElementValueOfNonExistingSubmodel() {
		SubmodelRepository repo = getSubmodelRepositoryWithDummySubmodels();

		PropertyValue valueToWrite = new PropertyValue("400");

		repo.setSubmodelElementValue("nonExisting", "doesNotMatter", valueToWrite);
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

	@Test
	public void getSubmodelByIdMetadata() throws JsonProcessingException {
		SubmodelRepository repo = getSubmodelRepository();
		Submodel expectedSubmodel = buildDummySubmodelWithNoSmElement(ID);
		expectedSubmodel.setSubmodelElements(null);
		repo.createSubmodel(expectedSubmodel);

		Submodel retrievedSubmodelMetadata = repo.getSubmodelByIdMetadata(ID);
		retrievedSubmodelMetadata.setSubmodelElements(null);

		assertEquals(expectedSubmodel, retrievedSubmodelMetadata);
	}

	@Test
	public void getSubmodelByIdValueOnly() throws JsonProcessingException {
		SubmodelRepository repo = getSubmodelRepository();
		Submodel submodel = buildDummySubmodelWithNoSmElement(ID);

		List<SubmodelElement> submodelElements = buildDummySubmodelElements();
		submodel.setSubmodelElements(submodelElements);
		repo.createSubmodel(submodel);

		SubmodelValueOnly expectedSmValueOnly = new SubmodelValueOnly(submodelElements);
		SubmodelValueOnly retrievedSmValueOnly = repo.getSubmodelByIdValueOnly(ID);

		ObjectMapper mapper = new ObjectMapper();
		String expectedSmValueOnlyJSONContent = mapper.writeValueAsString(expectedSmValueOnly);
		String retrievedSmValueOnlyJSONContent = mapper.writeValueAsString(retrievedSmValueOnly);

		assertEquals(expectedSmValueOnlyJSONContent, retrievedSmValueOnlyJSONContent);
	}

	@Override
	@Test
	public void patchSubmodelElements() {
		SubmodelRepository repo = getSubmodelRepository();
		Submodel submodel = buildDummySubmodelWithNoSmElement(ID);

		List<SubmodelElement> submodelElements = buildDummySubmodelElements();
		submodel.setSubmodelElements(submodelElements);
		repo.createSubmodel(submodel);

		List<SubmodelElement> submodelElementsPatch = buildDummySubmodelElementsToPatch();
		repo.patchSubmodelElements(ID, submodelElementsPatch);

		Submodel patchedSubmodel = repo.getSubmodel(ID);

		assertEquals(submodel.getSubmodelElements().size(), patchedSubmodel.getSubmodelElements().size());
		assertEquals(submodelElementsPatch, patchedSubmodel.getSubmodelElements());
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

	@Override
	public SubmodelService getSubmodelService(Submodel submodel) {
		return new SubmodelRepositorySubmodelServiceWrapper(getSubmodelRepository(), submodel);
	}
}

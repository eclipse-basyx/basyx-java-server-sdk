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

import java.util.Collection;

import org.eclipse.digitaltwin.aas4j.v3.model.DataTypeDefXsd;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultProperty;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSubmodel;
import org.eclipse.digitaltwin.basyx.core.exceptions.CollidingIdentifierException;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;
import org.eclipse.digitaltwin.basyx.submodelrepository.SubmodelRepository;
import org.eclipse.digitaltwin.basyx.submodelservice.DummySubmodelFactory;
import org.junit.Test;

/**
 * Testsuite for implementations of the SubmodelRepository interface
 * 
 * @author schnicke
 *
 */
public abstract class SubmodelRepositorySuite {
	protected abstract SubmodelRepository getSubmodelRepository();

	protected abstract SubmodelRepository getSubmodelRepository(Collection<Submodel> submodels);

	@Test
	public void getAllSubmodelsPreconfigured() {
		Collection<Submodel> expectedSubmodels = DummySubmodelFactory.getSubmodels();

		SubmodelRepository repo = getSubmodelRepository(expectedSubmodels);
		Collection<Submodel> submodels = repo.getAllSubmodels();

		assertSubmodelsAreContained(expectedSubmodels, submodels);
	}

	private void assertSubmodelsAreContained(Collection<Submodel> expectedSubmodels, Collection<Submodel> submodels) {
		assertEquals(2, submodels.size());
		assertTrue(submodels.containsAll(expectedSubmodels));
	}

	@Test
	public void getAllSubmodelsEmpty() {
		SubmodelRepository repo = getSubmodelRepository();
		Collection<Submodel> submodels = repo.getAllSubmodels();

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
	public void getSubmodelElements() {
		SubmodelRepository repo = getSubmodelRepositoryWithDummySubmodels();
		Collection<SubmodelElement> elements = repo.getSubmodelElements(DummySubmodelFactory.SUBMODEL_OPERATIONAL_DATA_ID);
		Collection<SubmodelElement> expectedElements = DummySubmodelFactory.createOperationalDataSubmodel().getSubmodelElements();
		assertEquals(expectedElements, elements);
	}

	@Test(expected = ElementDoesNotExistException.class)
	public void getSubmodelElementsOfNonExistingSubmodel() {
		SubmodelRepository repo = getSubmodelRepositoryWithDummySubmodels();
		repo.getSubmodelElements("notExisting");

	}

	private Submodel buildDummySubmodel(String id) {
		return new DefaultSubmodel.Builder()
				.id(id)
				.submodelElements(
					new DefaultProperty.Builder()
					.idShort("prop")
					.value("testValue")
					.valueType(DataTypeDefXsd.STRING).build()
				).build();
	}

	private SubmodelRepository getSubmodelRepositoryWithDummySubmodels() {
		Collection<Submodel> expectedSubmodels = DummySubmodelFactory.getSubmodels();
		SubmodelRepository repo = getSubmodelRepository(expectedSubmodels);
		return repo;
	}

	private void assertIsEmpty(Collection<Submodel> submodels) {
		assertTrue(submodels.isEmpty());
	}


}

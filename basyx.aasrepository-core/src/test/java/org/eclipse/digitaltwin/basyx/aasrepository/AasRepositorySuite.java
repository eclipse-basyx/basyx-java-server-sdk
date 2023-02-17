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
package org.eclipse.digitaltwin.basyx.aasrepository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.KeyTypes;
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultAssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultKey;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultReference;
import org.eclipse.digitaltwin.basyx.core.exceptions.CollidingIdentifierException;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;
import org.junit.Before;
import org.junit.Test;

/**
 * Testsuite for implementations of the AasRepository interface
 * 
 * @author schnicke
 *
 */
public abstract class AasRepositorySuite {

	private AssetAdministrationShell aas1;
	private AssetAdministrationShell aas2;
	private List<AssetAdministrationShell> preconfiguredShells = new ArrayList<>();

	private static final String DUMMY_SUBMODEL_ID = "dummySubmodelId";

	private AasRepository aasRepo;

	protected abstract AasRepositoryFactory getAasRepositoryFactory();

	@Before
	public void createAasRepoWithDummyAas() {
		aasRepo = getAasRepositoryFactory().create();

		aas1 = new DefaultAssetAdministrationShell.Builder().id("aas1/s")
				.build();

		aas2 = new DefaultAssetAdministrationShell.Builder().id("aas2")
				.build();

		preconfiguredShells.add(aas1);
		preconfiguredShells.add(aas2);

		preconfiguredShells.forEach(shell -> aasRepo.createAas(shell));
	}

	@Test
	public void allAasRetrieval() throws Exception {
		Collection<AssetAdministrationShell> coll = aasRepo.getAllAas();
		assertEquals(preconfiguredShells, coll);
	}

	@Test
	public void getAasByIdentifier() throws CollidingIdentifierException, ElementDoesNotExistException {
		AssetAdministrationShell retrieved = aasRepo.getAas(aas1.getId());
		assertEquals(aas1, retrieved);
	}

	@Test(expected = ElementDoesNotExistException.class)
	public void getNonExistingAasByIdentifier() throws ElementDoesNotExistException {
		aasRepo.getAas("nonExisting");
	}

	@Test(expected = CollidingIdentifierException.class)
	public void createWithCollidingAasIdentifiers() throws CollidingIdentifierException {
		aasRepo.createAas(aas1);
	}
	
	@Test
	public void deleteAas() {
		aasRepo.deleteAas(aas1.getId());
		
		try {
			aasRepo.getAas(aas1.getId());
			fail();
		} catch (ElementDoesNotExistException expected) {
		}
	}

	@Test(expected = ElementDoesNotExistException.class)
	public void deleteNonExistingAas() {
		aasRepo.deleteAas("nonExisting");
	}

	@Test
	public void getSubmodelReferences() {
		addDummyReferenceToShell(aas1);
		List<Reference> submodelReferences = aasRepo.getSubmodelReferences(aas1.getId());

		assertEquals(DUMMY_SUBMODEL_ID, submodelReferences.get(0).getKeys().get(0).getValue());
	}

	@Test(expected = ElementDoesNotExistException.class)
	public void getSubmodelReferencesOfNonExistingAas() {
		aasRepo.getSubmodelReferences("doesNotMatter");
	}

	@Test
	public void addSubmodelReference() {
		Reference reference = createDummyReference();
		aasRepo.addSubmodelReference(aas1.getId(), reference);

		List<Reference> submodelReferences = aasRepo.getSubmodelReferences(aas1.getId());

		assertEquals(DUMMY_SUBMODEL_ID, submodelReferences.get(0).getKeys().get(0).getValue());
	}

	@Test(expected = ElementDoesNotExistException.class)
	public void addSubmodelReferenceToNonExistingAas() {
		Reference reference = createDummyReference();
		aasRepo.addSubmodelReference("doesNotMatter", reference);
	}

	@Test
	public void removeSubmodelReference() {
		addDummyReferenceToShell(aas1);
		aasRepo.removeSubmodelReference(aas1.getId(), DUMMY_SUBMODEL_ID);

		List<Reference> submodelReferences = aasRepo.getSubmodelReferences(aas1.getId());

		assertEquals(0, submodelReferences.size());
	}

	@Test(expected = ElementDoesNotExistException.class)
	public void removeNonExistingSubmodelReference() {
		aasRepo.removeSubmodelReference(aas1.getId(), "doesNotMatter");
	}

	@Test(expected = ElementDoesNotExistException.class)
	public void removeSubmodelReferenceOfNonExistingAas() {
		aasRepo.removeSubmodelReference("doesNotMatter", "trivial");
	}

	private void addDummyReferenceToShell(AssetAdministrationShell aas) {
		List<Reference> submodelReferences = new ArrayList<>();
		Reference dummyReference = createDummyReference();
		submodelReferences.add(dummyReference);
		aas.setSubmodels(submodelReferences);
	}

	private Reference createDummyReference() {
		return new DefaultReference.Builder()
				.keys(new DefaultKey.Builder().type(KeyTypes.SUBMODEL).value(DUMMY_SUBMODEL_ID).build()).build();
	}

}

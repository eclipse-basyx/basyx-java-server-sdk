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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetInformation;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetKind;
import org.eclipse.digitaltwin.aas4j.v3.model.KeyTypes;
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultAssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultAssetInformation;
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

	private static final String AAS_1_ID = "aas1/s";
	private AssetAdministrationShell aas1;
	private AssetAdministrationShell aas2;
	private AssetAdministrationShell aas3;
	private List<AssetAdministrationShell> preconfiguredShells = new ArrayList<>();

	private static final String DUMMY_SUBMODEL_ID = "dummySubmodelId";

	private AasRepository aasRepo;

	protected abstract AasRepositoryFactory getAasRepositoryFactory();

	@Before
	public void createAasRepoWithDummyAas() {
		aasRepo = getAasRepositoryFactory().create();

		aas1 = new DefaultAssetAdministrationShell.Builder().id(AAS_1_ID).submodels(createDummyReference(DUMMY_SUBMODEL_ID))
				.build();

		aas2 = new DefaultAssetAdministrationShell.Builder().id("aas2").build();
		AssetInformation assetInfo = createDummyAssetInformation();
		aas2.setAssetInformation(assetInfo);

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
		Reference reference = createDummyReference(DUMMY_SUBMODEL_ID);
		List<Reference> submodelReferences = aasRepo.getSubmodelReferences(aas1.getId());

		assertTrue(submodelReferences.contains(reference));
	}

	@Test(expected = ElementDoesNotExistException.class)
	public void getSubmodelReferencesOfNonExistingAas() {
		aasRepo.getSubmodelReferences("doesNotMatter");
	}

	@Test
	public void addSubmodelReference() {
		Reference reference = createDummyReference(DUMMY_SUBMODEL_ID);
		aasRepo.addSubmodelReference(aas1.getId(), reference);

		List<Reference> submodelReferences = aasRepo.getSubmodelReferences(aas1.getId());

		assertTrue(submodelReferences.contains(reference));
	}

	@Test(expected = ElementDoesNotExistException.class)
	public void addSubmodelReferenceToNonExistingAas() {
		Reference reference = createDummyReference(DUMMY_SUBMODEL_ID);
		aasRepo.addSubmodelReference("doesNotMatter", reference);
	}

	@Test
	public void removeSubmodelReference() {
		Reference reference = createDummyReference(DUMMY_SUBMODEL_ID);
		aasRepo.removeSubmodelReference(aas1.getId(), DUMMY_SUBMODEL_ID);

		List<Reference> submodelReferences = aasRepo.getSubmodelReferences(aas1.getId());

		assertFalse(submodelReferences.contains(reference));
	}

	@Test(expected = ElementDoesNotExistException.class)
	public void removeNonExistingSubmodelReference() {
		aasRepo.removeSubmodelReference(aas1.getId(), "doesNotMatter");
	}

	@Test(expected = ElementDoesNotExistException.class)
	public void removeSubmodelReferenceOfNonExistingAas() {
		aasRepo.removeSubmodelReference("nonExisting", "doesNotMatter");
	}
	
	@Test
	public void updateAas() {
		List<Reference> submodelReferences = Arrays.asList(createDummyReference("dummySubmodelId1"), createDummyReference("dummySubmodelId2"));
		aas1.setSubmodels(submodelReferences);
		
		aasRepo.updateAas(AAS_1_ID, aas1);

		assertEquals(aas1, aasRepo.getAas(AAS_1_ID));
	}
	
	@Test(expected = ElementDoesNotExistException.class)
	public void updateNonExistingAas() {
		aasRepo.updateAas("nonExisting", aas1);
	}

	@Test
	public void getAssetInformation() {
		assertEquals(aas2.getAssetInformation(), aasRepo.getAssetInformation(aas2.getId()));
	}

	@Test(expected = ElementDoesNotExistException.class)
	public void getAssetInformationOfNonExistingAas() {
		aasRepo.getAssetInformation("nonExisting");
	}

	@Test
	public void setAssetInformation() {
		AssetInformation assetInfo = createDummyAssetInformation();
		aasRepo.setAssetInformation(aas2.getId(), assetInfo);
		assertEquals(assetInfo, aasRepo.getAssetInformation(aas2.getId()));
	}

	@Test(expected = ElementDoesNotExistException.class)
	public void setAssetInformationOfNonExistingAas() {
		aasRepo.setAssetInformation("nonExisting", createDummyAssetInformation());
	}	

	public static Reference createDummyReference(String submodelId) {
		return new DefaultReference.Builder()
				.keys(new DefaultKey.Builder().type(KeyTypes.SUBMODEL).value(submodelId).build()).build();
	}

	private AssetInformation createDummyAssetInformation() {
		return new DefaultAssetInformation.Builder().assetKind(AssetKind.INSTANCE).globalAssetId(
				new DefaultReference.Builder().keys(new DefaultKey.Builder().value("assetIDTestKey").build()).build())
				.build();
	}
}

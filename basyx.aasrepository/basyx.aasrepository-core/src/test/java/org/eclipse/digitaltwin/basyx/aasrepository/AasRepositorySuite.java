/*******************************************************************************
 * Copyright (C) 2024 the Eclipse BaSyx Authors
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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetInformation;
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultAssetAdministrationShell;
import org.eclipse.digitaltwin.basyx.aasservice.AasService;
import org.eclipse.digitaltwin.basyx.aasservice.AasServiceSuite;
import org.eclipse.digitaltwin.basyx.core.exceptions.*;
import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.junit.Test;

/**
 * Testsuite for implementations of the AasRepository interface
 * 
 * @author schnicke, kammognie, mateusmolina, despen
 *
 */
public abstract class AasRepositorySuite extends AasServiceSuite {
	private final PaginationInfo noLimitPaginationInfo = new PaginationInfo(0, "");
	private static final String AAS_EMPTY_ID = " ";
	private static final String AAS_NULL_ID = null;

	protected abstract AasRepository getAasRepository();

	protected AasRepository getAasRepository(Collection<AssetAdministrationShell> shells) {
		AasRepository repo = getAasRepository();
		shells.forEach(repo::createAas);

		return repo;
	}
	@Test
	public void getDefaultAasRepositoryName() {
		assertEquals("aas-repo", getAasRepository().getName());
	}

	@Test
	public void allAasRetrieval() throws Exception {
		List<AssetAdministrationShell> expected = DummyAasFactory.createShells();
		AasRepository aasRepo = getAasRepository(expected);
		PaginationInfo pInfo = new PaginationInfo(2, null);
		Collection<AssetAdministrationShell> coll = aasRepo.getAllAas(pInfo).getResult();
		assertEquals(expected, coll);
	}

	@Test
	public void getAasByIdentifier() throws CollidingIdentifierException, ElementDoesNotExistException {
		AssetAdministrationShell expected = DummyAasFactory.createAasWithSubmodelReference();
		AasRepository aasRepo = getAasRepository(Collections.singleton(expected));
		AssetAdministrationShell retrieved = aasRepo.getAas(expected.getId());
		assertEquals(expected, retrieved);
	}

	@Test(expected = ElementDoesNotExistException.class)
	public void getNonExistingAasByIdentifier() throws ElementDoesNotExistException {
		AasRepository aasRepo = getAasRepository();
		aasRepo.getAas("nonExisting");
	}

	@Test(expected = CollidingIdentifierException.class)
	public void createWithCollidingAasIdentifiers() throws CollidingIdentifierException {
		AssetAdministrationShell colliding = DummyAasFactory.createAasWithSubmodelReference();
		AasRepository aasRepo = getAasRepository(Collections.singleton(colliding));
		aasRepo.createAas(colliding);
	}
	
	@Test(expected = MissingIdentifierException.class)
	public void createWithEmptyAasIdentifier() {
		AasRepository aasRepo = getAasRepository();
		aasRepo.createAas(new DefaultAssetAdministrationShell.Builder().id(AAS_EMPTY_ID).build());
	}
	
	@Test(expected = MissingIdentifierException.class)
	public void createWithNullAasIdentifier() {
		AasRepository aasRepo = getAasRepository();
		aasRepo.createAas(new DefaultAssetAdministrationShell.Builder().id(AAS_NULL_ID).build());
	}

	@Test
	public void deleteAas() {
		AssetAdministrationShell toDelete = DummyAasFactory.createAasWithSubmodelReference();
		AasRepository aasRepo = getAasRepository(Collections.singleton(toDelete));
		aasRepo.deleteAas(toDelete.getId());

		try {
			aasRepo.getAas(toDelete.getId());
			fail();
		} catch (ElementDoesNotExistException expected) {
		}
	}

	@Test(expected = ElementDoesNotExistException.class)
	public void deleteNonExistingAas() {
		AasRepository aasRepo = getAasRepository();
		aasRepo.deleteAas("nonExisting");
	}

	@Test(expected = ElementDoesNotExistException.class)
	public void getSubmodelReferencesOfNonExistingAas() {
		AasRepository aasRepo = getAasRepository();
		aasRepo.getSubmodelReferences("doesNotMatter", noLimitPaginationInfo).getResult();
	}

	@Test(expected = ElementDoesNotExistException.class)
	public void addSubmodelReferenceToNonExistingAas() {
		AasRepository aasRepo = getAasRepository();
		Reference reference = DummyAasFactory.createDummyReference(DummyAasFactory.DUMMY_SUBMODEL_ID);
		aasRepo.addSubmodelReference("doesNotMatter", reference);
	}

	@Test(expected = CollidingSubmodelReferenceException.class)
	public void duplicateSubmodelReference() {
		AssetAdministrationShell aas = DummyAasFactory.createAasWithSubmodelReference();
		AasRepository aasRepo = getAasRepository(Collections.singleton(aas));
		Reference reference = DummyAasFactory.createDummyReference(DummyAasFactory.DUMMY_SUBMODEL_ID);
		aasRepo.addSubmodelReference(aas.getId(), reference);
	}

	@Test(expected = ElementDoesNotExistException.class)
	public void removeSubmodelReferenceOfNonExistingAas() {
		AasRepository aasRepo = getAasRepository();

		aasRepo.removeSubmodelReference("nonExisting", "doesNotMatter");
	}

	@Test
	public void updateAas() {
		AssetAdministrationShell aas = DummyAasFactory.createAasWithSubmodelReference();
		AasRepository aasRepo = getAasRepository(Collections.singleton(aas));

		List<Reference> submodelReferences = Arrays.asList(DummyAasFactory.createDummyReference("dummySubmodelId1"), DummyAasFactory.createDummyReference("dummySubmodelId2"));
		aas.setSubmodels(submodelReferences);

		aasRepo.updateAas(aas.getId(), aas);

		assertEquals(aas, aasRepo.getAas(aas.getId()));
	}

	@Test(expected = ElementDoesNotExistException.class)
	public void updateNonExistingAas() {
		AssetAdministrationShell aas = DummyAasFactory.createAasWithSubmodelReference();
		AasRepository aasRepo = getAasRepository();

		aasRepo.updateAas("nonExisting", aas);
	}

	@Test(expected = IdentificationMismatchException.class)
	public void updateExistingAasWithMismatchedIdentifier() {
		AssetAdministrationShell aas = DummyAasFactory.createAasWithSubmodelReference();
		AasRepository aasRepo = getAasRepository(Collections.singleton(aas));

		AssetAdministrationShell aasOtherId = new DefaultAssetAdministrationShell.Builder().id("mismatchId").submodels(DummyAasFactory.createDummyReference(DummyAasFactory.DUMMY_SUBMODEL_ID)).build();

		aasRepo.updateAas(aas.getId(), aasOtherId);
	}

	@Test
	public void getAssetInformation() {
		AssetAdministrationShell aas = DummyAasFactory.createAasWithAssetInformation();
		AasRepository aasRepo = getAasRepository(Collections.singleton(aas));

		assertEquals(aas.getAssetInformation(), aasRepo.getAssetInformation(aas.getId()));
	}

	@Test(expected = ElementDoesNotExistException.class)
	public void getAssetInformationOfNonExistingAas() {
		AasRepository aasRepo = getAasRepository();
		aasRepo.getAssetInformation("nonExisting");
	}

	@Test
	public void setAssetInformation() {
		AssetAdministrationShell aas = DummyAasFactory.createAasWithAssetInformation();
		AasRepository aasRepo = getAasRepository(Collections.singleton(aas));

		AssetInformation assetInfo = DummyAasFactory.createDummyAssetInformation();
		aasRepo.setAssetInformation(aas.getId(), assetInfo);
		assertEquals(assetInfo, aasRepo.getAssetInformation(aas.getId()));
	}

	@Test(expected = ElementDoesNotExistException.class)
	public void setAssetInformationOfNonExistingAas() {
		AasRepository aasRepo = getAasRepository();
		aasRepo.setAssetInformation("nonExisting", DummyAasFactory.createDummyAssetInformation());
	}

	@Test
	public void getPaginatedAssetAdministrationShell() {
		List<AssetAdministrationShell> expected = DummyAasFactory.createShells();
		AasRepository aasRepo = getAasRepository(expected);

		CursorResult<List<AssetAdministrationShell>> result = aasRepo.getAllAas(new PaginationInfo(1, null));
		List<AssetAdministrationShell> resultList = result.getResult();
		assertEquals(1, resultList.size());
		assertEquals(DummyAasFactory.AASWITHSUBMODELREF_ID, resultList.stream().findFirst().get().getId());
	}

	@Test
	public void getPaginatedAssetAdministrationShellIterating() {
		List<AssetAdministrationShell> expected = DummyAasFactory.createShells();
		AasRepository aasRepo = getAasRepository(expected);
		List<AssetAdministrationShell> retrieved = new ArrayList<>();

		CursorResult<List<AssetAdministrationShell>> result = aasRepo.getAllAas(new PaginationInfo(1, null));
		retrieved.addAll(result.getResult());

		String cursor = result.getCursor();
		result = aasRepo.getAllAas(new PaginationInfo(1, cursor));
		retrieved.addAll(result.getResult());

		assertEquals(expected, retrieved);
	}

	@Override
	public AasService getAasService(AssetAdministrationShell shell) {
		return new AasRepositoryAasServiceWrapper(getAasRepository(), shell);
	}
}

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

package org.eclipse.digitaltwin.basyx.conceptdescriptionrepository.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.model.ConceptDescription;
import org.eclipse.digitaltwin.aas4j.v3.model.KeyTypes;
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.aas4j.v3.model.ReferenceTypes;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultAdministrativeInformation;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultConceptDescription;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultKey;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultReference;
import org.eclipse.digitaltwin.basyx.conceptdescriptionrepository.ConceptDescriptionRepository;
import org.eclipse.digitaltwin.basyx.core.exceptions.CollidingIdentifierException;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.exceptions.IdentificationMismatchException;
import org.eclipse.digitaltwin.basyx.core.exceptions.MissingIdentifierException;
import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.junit.Test;

/**
 * Testsuite for implementations of the ConceptDescriptionRepository interface
 * 
 * @author schnicke, danish, kammognie
 *
 */
public abstract class ConceptDescriptionRepositorySuite {
	protected abstract ConceptDescriptionRepository getConceptDescriptionRepository();

	protected abstract ConceptDescriptionRepository getConceptDescriptionRepository(Collection<ConceptDescription> conceptDescriptions);

	private static final String EMPTY_ID = " ";
	private static final String NULL_ID = null;
	
	@Test
	public void getAllConceptDescriptionsPreconfigured() {
		Collection<ConceptDescription> expectedConceptDescriptions = DummyConceptDescriptionFactory.getConceptDescriptions();

		ConceptDescriptionRepository repo = getConceptDescriptionRepository(expectedConceptDescriptions);
		Collection<ConceptDescription> actualConceptDescriptions = repo.getAllConceptDescriptions(PaginationInfo.NO_LIMIT)
				.getResult();

		assertEquals(4, actualConceptDescriptions.size());
		assertConceptDescriptionsAreContained(expectedConceptDescriptions, actualConceptDescriptions);
	}

	@Test
	public void getAllConceptDescriptionsWithIdShortPreconfigured() {
		Collection<ConceptDescription> allConceptDescriptions = DummyConceptDescriptionFactory.getConceptDescriptions();
		Collection<ConceptDescription> expectedDescriptions = Arrays.asList(DummyConceptDescriptionFactory.createBasicConceptDescription());

		ConceptDescriptionRepository repo = getConceptDescriptionRepository(allConceptDescriptions);
		Collection<ConceptDescription> actualConceptDescriptions = repo.getAllConceptDescriptionsByIdShort("BasicConceptDescription", PaginationInfo.NO_LIMIT)
				.getResult();

		assertEquals(1, actualConceptDescriptions.size());
		assertConceptDescriptionsAreContained(expectedDescriptions, actualConceptDescriptions);
	}

	@Test
	public void getAllConceptDescriptionsWithIsCaseOfPreconfigured() {
		Reference reference = new DefaultReference.Builder().keys(Arrays.asList(new DefaultKey.Builder().type(KeyTypes.DATA_ELEMENT)
				.value("DataElement")
				.build()))
				.type(ReferenceTypes.MODEL_REFERENCE)
				.build();

		Collection<ConceptDescription> allConceptDescriptions = DummyConceptDescriptionFactory.getConceptDescriptions();
		Collection<ConceptDescription> expectedDescriptions = Arrays.asList(DummyConceptDescriptionFactory.createConceptDescription(), DummyConceptDescriptionFactory.createBasicConceptDescriptionWithDataSpecification());

		ConceptDescriptionRepository repo = getConceptDescriptionRepository(allConceptDescriptions);
		Collection<ConceptDescription> actualConceptDescriptions = repo.getAllConceptDescriptionsByIsCaseOf(reference, PaginationInfo.NO_LIMIT)
				.getResult();

		assertEquals(2, actualConceptDescriptions.size());
		assertConceptDescriptionsAreContained(expectedDescriptions, actualConceptDescriptions);
	}

	@Test
	public void getAllConceptDescriptionsWithDataSpecPreconfigured() {
		Reference reference = new DefaultReference.Builder().keys(Arrays.asList(new DefaultKey.Builder().type(KeyTypes.REFERENCE_ELEMENT)
				.value("ReferenceElementKey")
				.build()))
				.type(ReferenceTypes.EXTERNAL_REFERENCE)
				.build();

		Collection<ConceptDescription> allConceptDescriptions = DummyConceptDescriptionFactory.getConceptDescriptions();
		Collection<ConceptDescription> expectedDescriptions = Arrays.asList(DummyConceptDescriptionFactory.createBasicConceptDescriptionWithDataSpecification());

		ConceptDescriptionRepository repo = getConceptDescriptionRepository(allConceptDescriptions);
		Collection<ConceptDescription> actualConceptDescriptions = repo.getAllConceptDescriptionsByDataSpecificationReference(reference, PaginationInfo.NO_LIMIT)
				.getResult();

		assertEquals(1, actualConceptDescriptions.size());
		assertConceptDescriptionsAreContained(expectedDescriptions, actualConceptDescriptions);
	}

	@Test
	public void getAllConceptDescriptionsEmpty() {
		ConceptDescriptionRepository repo = getConceptDescriptionRepository();
		Collection<ConceptDescription> conceptDescriptions = repo.getAllConceptDescriptions(PaginationInfo.NO_LIMIT)
				.getResult();

		assertIsEmpty(conceptDescriptions);
	}

	@Test
	public void getSpecificConceptDescription() {
		ConceptDescriptionRepository repo = getConceptDescriptionRepositoryWithDummyConceptDescriptions();

		ConceptDescription conceptDescription = DummyConceptDescriptionFactory.createConceptDescription();
		ConceptDescription retrieved = repo.getConceptDescription(conceptDescription.getId());

		assertEquals(conceptDescription, retrieved);
	}

	@Test(expected = ElementDoesNotExistException.class)
	public void getSpecificNonExistingConceptDescription() {
		ConceptDescriptionRepository repo = getConceptDescriptionRepositoryWithDummyConceptDescriptions();
		repo.getConceptDescription("doesNotExist");
	}

	@Test
	public void updateExistingConceptDescription() {
		String id = ConceptDescriptionRepositorySuiteHelper.CONCEPT_DESCRIPTION_ID;
		ConceptDescription expected = createDummyConceptDescription(id);

		ConceptDescriptionRepository repo = getConceptDescriptionRepositoryWithDummyConceptDescriptions();
		repo.updateConceptDescription(id, expected);

		assertEquals(expected, repo.getConceptDescription(id));
	}

	@Test(expected = ElementDoesNotExistException.class)
	public void updateNonExistingConceptDescription() {
		String id = "notExisting";
		ConceptDescription doesNotExist = createDummyConceptDescription(id);

		ConceptDescriptionRepository repo = getConceptDescriptionRepositoryWithDummyConceptDescriptions();
		repo.updateConceptDescription(id, doesNotExist);
	}

	@Test(expected = IdentificationMismatchException.class)
	public void updateExistingCDWithMismatchId() {
		String id = ConceptDescriptionRepositorySuiteHelper.CONCEPT_DESCRIPTION_ID;
		ConceptDescription newCd = createDummyConceptDescription("mismatchId");

		ConceptDescriptionRepository repo = getConceptDescriptionRepositoryWithDummyConceptDescriptions();
		repo.updateConceptDescription(id, newCd);
	}

	@Test
	public void createConceptDescription() {
		String id = "newConceptDescription";
		ConceptDescription expectedConceptDescription = createDummyConceptDescription(id);

		ConceptDescriptionRepository repo = getConceptDescriptionRepositoryWithDummyConceptDescriptions();
		repo.createConceptDescription(expectedConceptDescription);

		ConceptDescription retrieved = repo.getConceptDescription(id);
		assertEquals(expectedConceptDescription, retrieved);
	}

	@Test(expected = CollidingIdentifierException.class)
	public void createConceptDescriptionWithCollidingId() {
		ConceptDescriptionRepository repo = getConceptDescriptionRepositoryWithDummyConceptDescriptions();
		ConceptDescription conceptDescription = repo.getConceptDescription(ConceptDescriptionRepositorySuiteHelper.CONCEPT_DESCRIPTION_ID);

		repo.createConceptDescription(conceptDescription);
	}
	
	@Test(expected = MissingIdentifierException.class)
	public void createConceptDescriptionWithEmptyId() {
		ConceptDescriptionRepository repo  = getConceptDescriptionRepository();
		ConceptDescription conceptDescription = createDummyConceptDescription(EMPTY_ID);
		
		repo.createConceptDescription(conceptDescription);
	}
	
	@Test(expected = MissingIdentifierException.class)
	public void createConceptDescriptionWithNullId() {
		ConceptDescriptionRepository repo  = getConceptDescriptionRepository();
		ConceptDescription conceptDescription = createDummyConceptDescription(NULL_ID);
		
		repo.createConceptDescription(conceptDescription);
	}
	
	@Test(expected = MissingIdentifierException.class)
	public void createConceptDescriptionCollectionWithMissingId() {
		Collection<ConceptDescription> conceptDescriptions = Arrays.asList(createDummyConceptDescription(EMPTY_ID), createDummyConceptDescription(NULL_ID), createDummyConceptDescription(ConceptDescriptionRepositorySuiteHelper.CONCEPT_DESCRIPTION_ID));

		getConceptDescriptionRepository(conceptDescriptions);
	}

	@Test
	public void deleteConceptDescription() {
		ConceptDescriptionRepository repo = getConceptDescriptionRepositoryWithDummyConceptDescriptions();
		repo.deleteConceptDescription(ConceptDescriptionRepositorySuiteHelper.CONCEPT_DESCRIPTION_ID);

		try {
			repo.getConceptDescription(ConceptDescriptionRepositorySuiteHelper.CONCEPT_DESCRIPTION_ID);
			fail();
		} catch (ElementDoesNotExistException expected) {
		}
	}

	@Test(expected = ElementDoesNotExistException.class)
	public void deleteNonExistingConceptDescription() {
		ConceptDescriptionRepository repo = getConceptDescriptionRepositoryWithDummyConceptDescriptions();
		repo.deleteConceptDescription("nonExisting");
	}
	
	@Test
	public void getDefaultConceptDescriptionRepositoryName() {
		ConceptDescriptionRepository repo = getConceptDescriptionRepository();
		
		assertEquals("cd-repo", repo.getName());
	}

	@Test
	public void allContextDescriptionPaginated() {
		ConceptDescriptionRepository repo = getConceptDescriptionRepositoryWithDummyConceptDescriptions();
		PaginationInfo pInfo = new PaginationInfo(1, "");
		CursorResult<List<ConceptDescription>> paginatedConceptDescriptions = repo.getAllConceptDescriptions(pInfo);
		assertEquals(1, paginatedConceptDescriptions.getResult()
				.size());
	}

	@Test
	public void paginatedGetAllConceptDescriptionsWithIsCaseOfPreconfigured() {
		Reference reference = new DefaultReference.Builder().keys(Arrays.asList(new DefaultKey.Builder().type(KeyTypes.DATA_ELEMENT)
				.value("DataElement")
				.build()))
				.type(ReferenceTypes.MODEL_REFERENCE)
				.build();

		Collection<ConceptDescription> allConceptDescriptions = DummyConceptDescriptionFactory.getConceptDescriptions();
		ConceptDescription expectedDescription = DummyConceptDescriptionFactory.createConceptDescription();

		ConceptDescriptionRepository repo = getConceptDescriptionRepository(allConceptDescriptions);
		PaginationInfo pInfo = new PaginationInfo(1, "");
		Collection<ConceptDescription> actualConceptDescriptions = repo.getAllConceptDescriptionsByIsCaseOf(reference, pInfo)
				.getResult();

		assertEquals(1, actualConceptDescriptions.size());
		assertEquals(expectedDescription, actualConceptDescriptions.stream()
				.findFirst()
				.get());
	}

	private void assertConceptDescriptionsAreContained(Collection<ConceptDescription> expectedConceptDescriptions, Collection<ConceptDescription> actualConceptDescriptions) {
		assertTrue(actualConceptDescriptions.containsAll(expectedConceptDescriptions));
	}

	private ConceptDescription createDummyConceptDescription(String id) {
		return new DefaultConceptDescription.Builder().id(id)
				.isCaseOf(new DefaultReference.Builder().type(ReferenceTypes.EXTERNAL_REFERENCE)
						.build())
				.administration(new DefaultAdministrativeInformation.Builder().revision("6")
						.version("2.4.5")
						.build())
				.build();
	}

	private ConceptDescriptionRepository getConceptDescriptionRepositoryWithDummyConceptDescriptions() {
		Collection<ConceptDescription> expectedConceptDescriptions = DummyConceptDescriptionFactory.getConceptDescriptions();
		ConceptDescriptionRepository repo = getConceptDescriptionRepository(expectedConceptDescriptions);
		return repo;
	}

	private void assertIsEmpty(Collection<ConceptDescription> conceptDescriptions) {
		assertTrue(conceptDescriptions.isEmpty());
	}

}

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

package org.eclipse.digitaltwin.basyx.conceptdescriptionrepository;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.eclipse.digitaltwin.aas4j.v3.model.ConceptDescription;
import org.eclipse.digitaltwin.aas4j.v3.model.EmbeddedDataSpecification;
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.basyx.core.exceptions.CollidingIdentifierException;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.exceptions.IdentificationMismatchException;
import org.eclipse.digitaltwin.basyx.core.exceptions.MissingIdentifierException;
import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationSupport;

/**
 * In-memory implementation of the ConceptDescriptionRepository
 *
 * @author danish, kammognie
 *
 */
public class InMemoryConceptDescriptionRepository implements ConceptDescriptionRepository {

	private Map<String, ConceptDescription> conceptDescriptions = new LinkedHashMap<>();

	private String cdRepositoryName;

	/**
	 * Creates the InMemoryConceptDescriptionRepository
	 * 
	 */
	public InMemoryConceptDescriptionRepository() {
	}

	/**
	 * Creates the InMemoryConceptDescriptionRepository
	 * 
	 * @param cdRepositoryName
	 *            Name of the CDRepository
	 */
	public InMemoryConceptDescriptionRepository(String cdRepositoryName) {
		this.cdRepositoryName = cdRepositoryName;
	}

	/**
	 * Creates the InMemoryConceptDescriptionRepository and preconfiguring it with
	 * the passed ConceptDescriptions
	 * 
	 * @param conceptDescriptions
	 */
	public InMemoryConceptDescriptionRepository(Collection<ConceptDescription> conceptDescriptions) {
		throwIfMissingId(conceptDescriptions);

		assertIdUniqueness(conceptDescriptions);

		this.conceptDescriptions.putAll(mapConceptDescriptions(conceptDescriptions));
	}

	/**
	 * Creates the InMemoryConceptDescriptionRepository and preconfiguring it with
	 * the passed ConceptDescriptions
	 * 
	 * @param conceptDescriptions
	 * @param cdRepositoryName Name of the CDRepository
	 */
	public InMemoryConceptDescriptionRepository(Collection<ConceptDescription> conceptDescriptions, String cdRepositoryName) {
		this(conceptDescriptions);
		this.cdRepositoryName = cdRepositoryName;
	}

	@Override
	public CursorResult<List<ConceptDescription>> getAllConceptDescriptions(PaginationInfo pInfo) {
		List<ConceptDescription> conceptDescriptionList = conceptDescriptions.values().stream().collect(Collectors.toList());

		CursorResult<List<ConceptDescription>> paginatedCD = paginateList(pInfo, conceptDescriptionList);
		return paginatedCD;
	}

	@Override
	public CursorResult<List<ConceptDescription>> getAllConceptDescriptionsByIdShort(String idShort, PaginationInfo pInfo) {
		List<ConceptDescription> allDescriptions = conceptDescriptions.values().stream().collect(Collectors.toList());

		List<ConceptDescription> filtered = allDescriptions.stream().filter(conceptDescription -> conceptDescription.getIdShort().equals(idShort)).collect(Collectors.toList());
		CursorResult<List<ConceptDescription>> result = paginateList(pInfo, filtered);
		return result;
	}

	@Override
	public CursorResult<List<ConceptDescription>> getAllConceptDescriptionsByIsCaseOf(Reference reference, PaginationInfo pInfo) {
		List<ConceptDescription> allDescriptions = conceptDescriptions.values().stream().collect(Collectors.toList());
		List<ConceptDescription> filtered = allDescriptions.stream().filter(conceptDescription -> hasMatchingReference(conceptDescription, reference)).collect(Collectors.toList());

		CursorResult<List<ConceptDescription>> result = paginateList(pInfo, filtered);
		return result;
	}

	@Override
	public CursorResult<List<ConceptDescription>> getAllConceptDescriptionsByDataSpecificationReference(Reference reference, PaginationInfo pInfo) {
		List<ConceptDescription> allDescriptions = conceptDescriptions.values().stream().collect(Collectors.toList());

		List<ConceptDescription> filtered = allDescriptions.stream().filter(conceptDescription -> hasMatchingDataSpecificationReference(conceptDescription, reference)).collect(Collectors.toList());

		CursorResult<List<ConceptDescription>> result = paginateList(pInfo, filtered);
		return result;
	}

	@Override
	public ConceptDescription getConceptDescription(String conceptDescriptionId) throws ElementDoesNotExistException {
		throwIfConceptDescriptionDoesNotExist(conceptDescriptionId);

		return conceptDescriptions.get(conceptDescriptionId);
	}

	@Override
	public void updateConceptDescription(String conceptDescriptionId, ConceptDescription conceptDescription) throws ElementDoesNotExistException {
		throwIfConceptDescriptionDoesNotExist(conceptDescriptionId);

		throwIfMismatchingIds(conceptDescriptionId, conceptDescription);

		conceptDescriptions.put(conceptDescriptionId, conceptDescription);
	}

	@Override
	public void createConceptDescription(ConceptDescription conceptDescription) throws CollidingIdentifierException, MissingIdentifierException {
		throwIfConceptDescriptionIdEmptyOrNull(conceptDescription.getId());

		throwIfConceptDescriptionExists(conceptDescription.getId());

		conceptDescriptions.put(conceptDescription.getId(), conceptDescription);
	}

	@Override
	public void deleteConceptDescription(String conceptDescriptionId) throws ElementDoesNotExistException {
		throwIfConceptDescriptionDoesNotExist(conceptDescriptionId);

		conceptDescriptions.remove(conceptDescriptionId);
	}

	@Override
	public String getName() {
		return cdRepositoryName == null ? ConceptDescriptionRepository.super.getName() : cdRepositoryName;
	}

	private static void assertIdUniqueness(Collection<ConceptDescription> conceptDescriptionsToCheck) {
		Set<String> ids = new HashSet<>();

		for (ConceptDescription conceptDescription : conceptDescriptionsToCheck) {
			String conceptDescriptionId = conceptDescription.getId();
			boolean unique = ids.add(conceptDescriptionId);

			if (!unique) {
				throw new CollidingIdentifierException(conceptDescriptionId);
			}
		}
	}

	private Map<String, ConceptDescription> mapConceptDescriptions(Collection<ConceptDescription> conceptDescriptions) {
		return conceptDescriptions.stream().collect(Collectors.toMap(ConceptDescription::getId, conceptDescription -> conceptDescription));
	}

	private void throwIfConceptDescriptionExists(String id) {
		if (conceptDescriptions.containsKey(id))
			throw new CollidingIdentifierException(id);
	}
	
	private void throwIfMissingId(Collection<ConceptDescription> conceptDescriptions) {
		conceptDescriptions.stream().map(ConceptDescription::getId).forEach(this::throwIfConceptDescriptionIdEmptyOrNull);
    }

	private void throwIfConceptDescriptionIdEmptyOrNull(String id) {
		if (id == null || id.isBlank())
			throw new MissingIdentifierException(id);
	}

	private void throwIfConceptDescriptionDoesNotExist(String id) {
		if (!conceptDescriptions.containsKey(id))
			throw new ElementDoesNotExistException(id);
	}

	private boolean hasMatchingReference(ConceptDescription cd, Reference reference) {
		Optional<Reference> optionalReference = cd.getIsCaseOf().stream().filter(ref -> ref.equals(reference)).findAny();

		return optionalReference.isPresent();
	}

	private boolean hasMatchingDataSpecificationReference(ConceptDescription cd, Reference reference) {
		Optional<EmbeddedDataSpecification> optionalReference = cd.getEmbeddedDataSpecifications().stream().filter(eds -> eds.getDataSpecification().equals(reference)).findAny();

		return optionalReference.isPresent();
	}

	private void throwIfMismatchingIds(String conceptDescriptionId, ConceptDescription newConceptDescription) {
		String newConceptDescriptionId = newConceptDescription.getId();

		if (!conceptDescriptionId.equals(newConceptDescriptionId))
			throw new IdentificationMismatchException();
	}

	private CursorResult<List<ConceptDescription>> paginateList(PaginationInfo pInfo, List<ConceptDescription> conceptDescriptionList) {
		TreeMap<String, ConceptDescription> cdMap = conceptDescriptionList.stream().collect(Collectors.toMap(ConceptDescription::getId, cd -> cd, (a, b) -> a, TreeMap::new));

		PaginationSupport<ConceptDescription> paginationSupport = new PaginationSupport<>(cdMap, ConceptDescription::getId);
		CursorResult<List<ConceptDescription>> paginatedCD = paginationSupport.getPaged(pInfo);
		return paginatedCD;
	}

}

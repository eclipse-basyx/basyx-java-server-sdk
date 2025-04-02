/*******************************************************************************
 * Copyright (C) 2025 the Eclipse BaSyx Authors
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

package org.eclipse.digitaltwin.basyx.conceptdescriptionrepository.backend;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.eclipse.digitaltwin.aas4j.v3.model.ConceptDescription;
import org.eclipse.digitaltwin.aas4j.v3.model.EmbeddedDataSpecification;
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.basyx.conceptdescriptionrepository.ConceptDescriptionRepository;
import org.eclipse.digitaltwin.basyx.core.exceptions.CollidingIdentifierException;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.exceptions.IdentificationMismatchException;
import org.eclipse.digitaltwin.basyx.core.exceptions.MissingIdentifierException;
import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationSupport;
import org.springframework.lang.NonNull;

/**
 * Default Implementation for the {@link ConceptDescription} based on
 * {@link ConceptDescriptionBackend}
 * 
 * @author mateusmolina, despen, zhangzai, kammognie, danish
 *
 */

public class CrudConceptDescriptionRepository implements ConceptDescriptionRepository {

	private final ConceptDescriptionBackend conceptDescriptionBackend;
	private final String conceptDescriptionRepositoryName;

	public CrudConceptDescriptionRepository(ConceptDescriptionBackend conceptDescriptionBackend, String cdRepositoryNameString) {
		this.conceptDescriptionBackend = conceptDescriptionBackend;
		conceptDescriptionRepositoryName = cdRepositoryNameString;
	}

	public CrudConceptDescriptionRepository(ConceptDescriptionBackend conceptDescriptionBackend, String cdRepositoryNameString, Collection<ConceptDescription> remoteCollection) {
		this(conceptDescriptionBackend, cdRepositoryNameString);

		initializeRemoteCollection(remoteCollection);
	}

	@Override
	public CursorResult<List<ConceptDescription>> getAllConceptDescriptions(PaginationInfo pInfo) {
		Iterable<ConceptDescription> iterable = conceptDescriptionBackend.findAll();
		List<ConceptDescription> conceptDescriptions = StreamSupport.stream(iterable.spliterator(), false).collect(Collectors.toList());

		TreeMap<String, ConceptDescription> conceptDescriptionMap = conceptDescriptions.stream().collect(Collectors.toMap(ConceptDescription::getId, conceptDescription -> conceptDescription, (a, b) -> a, TreeMap::new));

		PaginationSupport<ConceptDescription> paginationSupport = new PaginationSupport<>(conceptDescriptionMap, ConceptDescription::getId);

		return paginationSupport.getPaged(pInfo);
	}

	@Override
	public CursorResult<List<ConceptDescription>> getAllConceptDescriptionsByIdShort(String idShort, PaginationInfo pInfo) {
		Iterable<ConceptDescription> iterable = conceptDescriptionBackend.findAll();

		List<ConceptDescription> filtered = StreamSupport.stream(iterable.spliterator(), false).filter(conceptDescription -> conceptDescription.getIdShort().equals(idShort)).collect(Collectors.toList());

		TreeMap<String, ConceptDescription> conceptDescriptionMap = filtered.stream().collect(Collectors.toMap(ConceptDescription::getId, conceptDescription -> conceptDescription, (a, b) -> a, TreeMap::new));

		PaginationSupport<ConceptDescription> paginationSupport = new PaginationSupport<>(conceptDescriptionMap, ConceptDescription::getId);

		return paginationSupport.getPaged(pInfo);
	}

	@Override
	public CursorResult<List<ConceptDescription>> getAllConceptDescriptionsByIsCaseOf(Reference isCaseOf, PaginationInfo pInfo) {
		Iterable<ConceptDescription> iterable = conceptDescriptionBackend.findAll();

		List<ConceptDescription> filtered = StreamSupport.stream(iterable.spliterator(), false).filter(conceptDescription -> hasMatchingReference(conceptDescription, isCaseOf)).collect(Collectors.toList());

		TreeMap<String, ConceptDescription> conceptDescriptionMap = filtered.stream().collect(Collectors.toMap(ConceptDescription::getId, conceptDescription -> conceptDescription, (a, b) -> a, TreeMap::new));

		PaginationSupport<ConceptDescription> paginationSupport = new PaginationSupport<>(conceptDescriptionMap, ConceptDescription::getId);

		return paginationSupport.getPaged(pInfo);
	}

	@Override
	public CursorResult<List<ConceptDescription>> getAllConceptDescriptionsByDataSpecificationReference(Reference dataSpecificationReference, PaginationInfo pInfo) {
		Iterable<ConceptDescription> iterable = conceptDescriptionBackend.findAll();

		List<ConceptDescription> filtered = StreamSupport.stream(iterable.spliterator(), false).filter(conceptDescription -> hasMatchingDataSpecificationReference(conceptDescription, dataSpecificationReference))
				.collect(Collectors.toList());

		TreeMap<String, ConceptDescription> conceptDescriptionMap = filtered.stream().collect(Collectors.toMap(ConceptDescription::getId, conceptDescription -> conceptDescription, (a, b) -> a, TreeMap::new));

		PaginationSupport<ConceptDescription> paginationSupport = new PaginationSupport<>(conceptDescriptionMap, ConceptDescription::getId);

		return paginationSupport.getPaged(pInfo);
	}

	@Override
	public ConceptDescription getConceptDescription(String conceptDescriptionId) throws ElementDoesNotExistException {
		return conceptDescriptionBackend.findById(conceptDescriptionId).orElseThrow(() -> new ElementDoesNotExistException(conceptDescriptionId));
	}

	@Override
	public void updateConceptDescription(String conceptDescriptionId, ConceptDescription conceptDescription) throws ElementDoesNotExistException {
		throwIfConceptDescriptionDoesNotExist(conceptDescriptionId);

		throwIfMismatchingIds(conceptDescriptionId, conceptDescription.getId());

		conceptDescriptionBackend.save(conceptDescription);
	}

	@Override
	public void createConceptDescription(ConceptDescription conceptDescription) throws CollidingIdentifierException, MissingIdentifierException {
		throwIfConceptDescriptionIdEmptyOrNull(conceptDescription.getId());

		throwIfConceptDescriptionExists(conceptDescription.getId());

		conceptDescriptionBackend.save(conceptDescription);
	}

	@Override
	public void deleteConceptDescription(String conceptDescriptionId) throws ElementDoesNotExistException {
		throwIfConceptDescriptionDoesNotExist(conceptDescriptionId);

		conceptDescriptionBackend.deleteById(conceptDescriptionId);
	}

	@Override
	public String getName() {
		return conceptDescriptionRepositoryName == null ? ConceptDescriptionRepository.super.getName() : conceptDescriptionRepositoryName;
	}

	private void initializeRemoteCollection(@NonNull Collection<ConceptDescription> conceptDescriptions) {
		if (conceptDescriptions.isEmpty())
			return;

		throwIfMissingId(conceptDescriptions);

		assertIdUniqueness(conceptDescriptions);

		conceptDescriptions.stream().forEach(this::createConceptDescription);
	}

	private void throwIfMismatchingIds(String existingId, String newId) {

		if (!existingId.equals(newId))
			throw new IdentificationMismatchException();
	}

	private void throwIfConceptDescriptionDoesNotExist(String conceptDescriptionId) {

		if (!conceptDescriptionBackend.existsById(conceptDescriptionId))
			throw new ElementDoesNotExistException(conceptDescriptionId);
	}

	private void throwIfConceptDescriptionExists(String conceptDescriptionId) {

		if (conceptDescriptionBackend.existsById(conceptDescriptionId))
			throw new CollidingIdentifierException(conceptDescriptionId);
	}

	private void throwIfMissingId(Collection<ConceptDescription> conceptDescriptions) {
		conceptDescriptions.stream().map(ConceptDescription::getId).forEach(this::throwIfConceptDescriptionIdEmptyOrNull);
	}

	private void throwIfConceptDescriptionIdEmptyOrNull(String id) {
		if (id == null || id.isBlank())
			throw new MissingIdentifierException(id);
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

	private boolean hasMatchingReference(ConceptDescription cd, Reference reference) {
		Optional<Reference> optionalReference = cd.getIsCaseOf().stream().filter(ref -> ref.equals(reference)).findAny();

		return optionalReference.isPresent();
	}

	private boolean hasMatchingDataSpecificationReference(ConceptDescription cd, Reference reference) {
		Optional<EmbeddedDataSpecification> optionalReference = cd.getEmbeddedDataSpecifications().stream().filter(eds -> eds.getDataSpecification().equals(reference)).findAny();

		return optionalReference.isPresent();
	}

}

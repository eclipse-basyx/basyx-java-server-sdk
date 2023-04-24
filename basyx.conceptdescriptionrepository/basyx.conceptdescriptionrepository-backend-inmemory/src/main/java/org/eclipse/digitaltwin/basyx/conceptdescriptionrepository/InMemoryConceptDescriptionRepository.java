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
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.digitaltwin.aas4j.v3.model.ConceptDescription;
import org.eclipse.digitaltwin.aas4j.v3.model.EmbeddedDataSpecification;
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.basyx.core.exceptions.CollidingIdentifierException;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;

/**
 * In-memory implementation of the ConceptDescriptionRepository
 *
 * @author danish
 *
 */
public class InMemoryConceptDescriptionRepository implements ConceptDescriptionRepository {

	private Map<String, ConceptDescription> conceptDescriptions = new LinkedHashMap<>();
	
	/**
	 * Creates the InMemoryConceptDescriptionRepository
	 * 
	 */
	public InMemoryConceptDescriptionRepository() { }

	/**
	 * Creates the InMemoryConceptDescriptionRepository and preconfiguring
	 * it with the passed ConceptDescriptions
	 * 
	 * @param conceptDescriptions
	 */
	public InMemoryConceptDescriptionRepository(Collection<ConceptDescription> conceptDescriptions) {
		assertIdUniqueness(conceptDescriptions);

	    this.conceptDescriptions.putAll(mapConceptDescriptions(conceptDescriptions));
	}

	@Override
	public Collection<ConceptDescription> getAllConceptDescriptions() {
		return conceptDescriptions.values().stream().collect(Collectors.toList());
	}
	
	@Override
	public Collection<ConceptDescription> getAllConceptDescriptionsByIdShort(String idShort) {
		return conceptDescriptions.values().stream().filter(conceptDescription -> conceptDescription.getIdShort().equals(idShort)).collect(Collectors.toList());
	}

	@Override
	public Collection<ConceptDescription> getAllConceptDescriptionsByIsCaseOf(Reference reference) {
		return conceptDescriptions.values().stream().filter(conceptDescription -> hasMatchingReference(conceptDescription, reference)).collect(Collectors.toList());
	}

	@Override
	public Collection<ConceptDescription> getAllConceptDescriptionsByDataSpecificationReference(Reference reference) {
		return conceptDescriptions.values().stream().filter(conceptDescription -> hasMatchingDataSpecificationReference(conceptDescription, reference)).collect(Collectors.toList());
	}

	@Override
	public ConceptDescription getConceptDescription(String conceptDescriptionId) throws ElementDoesNotExistException {
		throwIfConceptDescriptionDoesNotExist(conceptDescriptionId);

		return conceptDescriptions.get(conceptDescriptionId);
	}

	@Override
	public void updateConceptDescription(String conceptDescriptionId, ConceptDescription conceptDescription)
			throws ElementDoesNotExistException {
		throwIfConceptDescriptionDoesNotExist(conceptDescriptionId);

		conceptDescriptions.put(conceptDescriptionId, conceptDescription);
	}

	@Override
	public void createConceptDescription(ConceptDescription conceptDescription) throws CollidingIdentifierException {
		throwIfConceptDescriptionExists(conceptDescription.getId());

		conceptDescriptions.put(conceptDescription.getId(), conceptDescription);
	}

	@Override
	public void deleteConceptDescription(String conceptDescriptionId) throws ElementDoesNotExistException {
		throwIfConceptDescriptionDoesNotExist(conceptDescriptionId);

		conceptDescriptions.remove(conceptDescriptionId);
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
	
	private Map<String, ConceptDescription> mapConceptDescriptions(
			Collection<ConceptDescription> conceptDescriptions) {
		return conceptDescriptions.stream().collect(Collectors.toMap(ConceptDescription::getId, conceptDescription -> conceptDescription));
	}

	private void throwIfConceptDescriptionExists(String id) {
		if (conceptDescriptions.containsKey(id))
			throw new CollidingIdentifierException(id);
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

}

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
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.digitaltwin.aas4j.v3.model.ConceptDescription;
import org.eclipse.digitaltwin.basyx.conceptdescriptionservice.ConceptDescriptionService;
import org.eclipse.digitaltwin.basyx.conceptdescriptionservice.ConceptDescriptionServiceFactory;
import org.eclipse.digitaltwin.basyx.core.exceptions.CollidingIdentifierException;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;

/**
 * In-memory implementation of the ConceptDescriptionRepository
 *
 * @author danish
 *
 */
public class InMemoryConceptDescriptionRepository implements ConceptDescriptionRepository {

	private Map<String, ConceptDescriptionService> conceptDescriptionServices = new LinkedHashMap<>();
	private ConceptDescriptionServiceFactory conceptDescriptionServiceFactory;

	/**
	 * Creates the InMemoryConceptDescriptionRepository utilizing the passed
	 * {@link ConceptDescriptionServiceFactory} for creating new ConceptDescription services
	 * 
	 * @param conceptDescriptionServiceFactory
	 */
	public InMemoryConceptDescriptionRepository(ConceptDescriptionServiceFactory conceptDescriptionServiceFactory) {
		this.conceptDescriptionServiceFactory = conceptDescriptionServiceFactory;
	}

	/**
	 * Creates the InMemoryConceptDescriptionRepository utilizing the passed
	 * {@link ConceptDescriptionServiceFactory} for creating new ConceptDescription services and preconfiguring
	 * it with the passed ConceptDescriptions
	 * 
	 * @param conceptDescriptionServiceFactory
	 * @param conceptDescriptions
	 */
	public InMemoryConceptDescriptionRepository(ConceptDescriptionServiceFactory conceptDescriptionServiceFactory, Collection<ConceptDescription> conceptDescriptions) {
		this(conceptDescriptionServiceFactory);
		assertIdUniqueness(conceptDescriptions);

		conceptDescriptionServices = createServices(conceptDescriptions);
	}

	@Override
	public Collection<ConceptDescription> getAllConceptDescriptions() {
		return conceptDescriptionServices.values().stream().map(ConceptDescriptionService::getConceptDescription).collect(Collectors.toList());
	}

	@Override
	public ConceptDescription getConceptDescription(String conceptDescriptionId) throws ElementDoesNotExistException {
		throwIfConceptDescriptionDoesNotExist(conceptDescriptionId);

		return conceptDescriptionServices.get(conceptDescriptionId).getConceptDescription();
	}

	@Override
	public void updateConceptDescription(String conceptDescriptionId, ConceptDescription conceptDescription)
			throws ElementDoesNotExistException {
		throwIfConceptDescriptionDoesNotExist(conceptDescriptionId);

		conceptDescriptionServices.put(conceptDescriptionId, conceptDescriptionServiceFactory.create(conceptDescription));
	}

	@Override
	public void createConceptDescription(ConceptDescription conceptDescription) throws CollidingIdentifierException {
		throwIfConceptDescriptionExists(conceptDescription.getId());

		conceptDescriptionServices.put(conceptDescription.getId(), conceptDescriptionServiceFactory.create(conceptDescription));
	}

	@Override
	public void deleteConceptDescription(String conceptDescriptionId) throws ElementDoesNotExistException {
		throwIfConceptDescriptionDoesNotExist(conceptDescriptionId);

		conceptDescriptionServices.remove(conceptDescriptionId);
	}
	
	private Map<String, ConceptDescriptionService> createServices(Collection<ConceptDescription> conceptDescriptions) {
		Map<String, ConceptDescriptionService> map = new LinkedHashMap<>();
		conceptDescriptions.forEach(conceptDescription -> map.put(conceptDescription.getId(), conceptDescriptionServiceFactory.create(conceptDescription)));

		return map;
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

	private void throwIfConceptDescriptionExists(String id) {
		if (conceptDescriptionServices.containsKey(id))
			throw new CollidingIdentifierException(id);
	}

	private void throwIfConceptDescriptionDoesNotExist(String id) {
		if (!conceptDescriptionServices.containsKey(id))
			throw new ElementDoesNotExistException(id);
	}

}

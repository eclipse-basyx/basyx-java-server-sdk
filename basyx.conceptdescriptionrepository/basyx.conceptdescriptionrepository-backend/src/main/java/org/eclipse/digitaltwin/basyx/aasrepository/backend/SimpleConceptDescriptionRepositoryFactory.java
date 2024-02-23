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

package org.eclipse.digitaltwin.basyx.aasrepository.backend;

import java.util.Collection;

import org.eclipse.digitaltwin.aas4j.v3.model.ConceptDescription;
import org.eclipse.digitaltwin.basyx.conceptdescriptionrepository.ConceptDescriptionRepository;
import org.eclipse.digitaltwin.basyx.conceptdescriptionrepository.ConceptDescriptionRepositoryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

/**
 * Simple Concept Description repository factory that creates a {@link CrudConceptDescriptionRepository} with
 * a backend provider
 * 
 * @author mateusmolina, danish
 * 
 */
@Component
@ConditionalOnExpression("!T(org.springframework.util.StringUtils).isEmpty('${basyx.backend:}')")
public class SimpleConceptDescriptionRepositoryFactory implements ConceptDescriptionRepositoryFactory {

	private ConceptDescriptionBackendProvider conceptDescriptionBackend;
	private String conceptDescriptionRepositoryName = null;
	private Collection<ConceptDescription> conceptDescriptions;

	@Autowired(required = false)
	public SimpleConceptDescriptionRepositoryFactory(ConceptDescriptionBackendProvider conceptDescriptionBackend) {
		this.conceptDescriptionBackend = conceptDescriptionBackend;
	}
	
	@Autowired(required = false)
	public SimpleConceptDescriptionRepositoryFactory(ConceptDescriptionBackendProvider conceptDescriptionBackendProvider, @Value("${basyx.cdrepo.name:cd-repo}") String conceptDescriptionRepositoryName) {
		this(conceptDescriptionBackendProvider);

		this.conceptDescriptionRepositoryName = conceptDescriptionRepositoryName;
	}

	@Autowired(required = false)
	public SimpleConceptDescriptionRepositoryFactory(ConceptDescriptionBackendProvider conceptDescriptionBackendProvider, Collection<ConceptDescription> conceptDescriptions) {
		this(conceptDescriptionBackendProvider);
		
		this.conceptDescriptions = conceptDescriptions;
	}
	
	@Autowired(required = false)
	public SimpleConceptDescriptionRepositoryFactory(ConceptDescriptionBackendProvider conceptDescriptionBackendProvider, Collection<ConceptDescription> conceptDescriptions, @Value("${basyx.cdrepo.name:cd-repo}") String conceptDescriptionRepositoryName) {
		this(conceptDescriptionBackendProvider, conceptDescriptions);

		this.conceptDescriptionRepositoryName = conceptDescriptionRepositoryName;
	}

	@Override
	public ConceptDescriptionRepository create() {
		
		if (conceptDescriptions == null)
			return new CrudConceptDescriptionRepository(conceptDescriptionBackend, conceptDescriptionRepositoryName);

		return new CrudConceptDescriptionRepository(conceptDescriptionBackend, conceptDescriptions, conceptDescriptionRepositoryName);
	}

}

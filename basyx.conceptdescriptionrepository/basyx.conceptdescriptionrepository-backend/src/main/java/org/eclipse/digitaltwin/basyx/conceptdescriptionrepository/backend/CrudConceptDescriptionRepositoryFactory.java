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
import java.util.Optional;

import org.eclipse.digitaltwin.aas4j.v3.model.ConceptDescription;
import org.eclipse.digitaltwin.basyx.conceptdescriptionrepository.ConceptDescriptionRepository;
import org.eclipse.digitaltwin.basyx.conceptdescriptionrepository.ConceptDescriptionRepositoryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

/**
 * {@link CrudConceptDescriptionRepository} factory using a
 * {@link ConceptDescriptionBackend}
 * 
 * @author mateusmolina, danish
 * 
 */
@Component
@ConditionalOnExpression("!T(org.springframework.util.StringUtils).isEmpty('${basyx.backend:}')")
public class CrudConceptDescriptionRepositoryFactory implements ConceptDescriptionRepositoryFactory {

	private final ConceptDescriptionBackend backend;
	private final String repositoryName;

	private Optional<Collection<ConceptDescription>> remoteCollection = Optional.empty();

	@Autowired
	public CrudConceptDescriptionRepositoryFactory(ConceptDescriptionBackend backend, @Value("${basyx.cdrepo.name:cd-repo}") String conceptDescriptionRepositoryName) {
		this.backend = backend;
		this.repositoryName = conceptDescriptionRepositoryName;
	}

	public CrudConceptDescriptionRepositoryFactory(ConceptDescriptionBackend backend) {
		this(backend, "cd-repo");
	}

	public CrudConceptDescriptionRepositoryFactory withRemoteCollection(Collection<ConceptDescription> conceptDescriptions) {
		this.remoteCollection = Optional.of(conceptDescriptions);
		return this;
	}

	@Override
	public ConceptDescriptionRepository create() {
		CrudConceptDescriptionRepository repo;

		if (remoteCollection.isPresent())
			repo = new CrudConceptDescriptionRepository(backend, repositoryName, remoteCollection.get());
		else
			repo = new CrudConceptDescriptionRepository(backend, repositoryName);

		return repo;
	}

}

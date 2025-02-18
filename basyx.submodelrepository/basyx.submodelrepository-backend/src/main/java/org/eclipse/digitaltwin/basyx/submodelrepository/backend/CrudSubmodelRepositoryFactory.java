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

package org.eclipse.digitaltwin.basyx.submodelrepository.backend;

import java.util.Collection;
import java.util.Optional;

import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.basyx.submodelrepository.SubmodelRepository;
import org.eclipse.digitaltwin.basyx.submodelrepository.SubmodelRepositoryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

/**
 * Simple Submodel repository factory that creates a
 * {@link CrudSubmodelRepository} with a backend provider and a service factory
 * 
 * @author mateusmolina, danish
 * 
 */
@Component
@ConditionalOnExpression("!T(org.springframework.util.StringUtils).isEmpty('${basyx.backend:}')")
public class CrudSubmodelRepositoryFactory implements SubmodelRepositoryFactory {

	static final String DEFAULT_REPOSITORY_NAME = "sm-repo";

	private final SubmodelBackend backend;
	private final String submodelRepositoryName;
	private Optional<Collection<Submodel>> submodels = Optional.empty();

	@Autowired
	public CrudSubmodelRepositoryFactory(SubmodelBackend submodelRepositoryBackend, @Value("${basyx.smrepo.name:" + DEFAULT_REPOSITORY_NAME + "}") String submodelRepositoryName) {
		this.backend = submodelRepositoryBackend;
		this.submodelRepositoryName = submodelRepositoryName;
	}

	public CrudSubmodelRepositoryFactory(SubmodelRepositoryBackend submodelRepositoryBackend) {
		this(submodelRepositoryBackend, DEFAULT_REPOSITORY_NAME);
	}

	public CrudSubmodelRepositoryFactory withRemoteCollection(Collection<Submodel> submodels) {
		this.submodels = Optional.of(submodels);
		return this;
	}

	@Override
	public SubmodelRepository create() {
		if (!submodels.isPresent())
			return new CrudSubmodelRepository(backend, submodelRepositoryName);

		return new CrudSubmodelRepository(backend, submodelRepositoryName, submodels.get());
	}

}

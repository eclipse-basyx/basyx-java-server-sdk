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

import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.basyx.submodelrepository.SubmodelRepository;
import org.eclipse.digitaltwin.basyx.submodelrepository.SubmodelRepositoryFactory;
import org.eclipse.digitaltwin.basyx.submodelservice.SubmodelServiceFactory;
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
public class SimpleSubmodelRepositoryFactory implements SubmodelRepositoryFactory {

	private SubmodelBackendProvider submodelBackendProvider;
	private SubmodelServiceFactory submodelServiceFactory;
	private String submodelRepositoryName = null;
	private Collection<Submodel> submodels;

	@Autowired(required = false)
	public SimpleSubmodelRepositoryFactory(SubmodelBackendProvider submodelBackendProvider, SubmodelServiceFactory submodelServiceFactory) {
		this.submodelBackendProvider = submodelBackendProvider;
		this.submodelServiceFactory = submodelServiceFactory;
	}

	@Autowired(required = false)
	public SimpleSubmodelRepositoryFactory(SubmodelBackendProvider submodelBackendProvider, SubmodelServiceFactory submodelServiceFactory, @Value("${basyx.smrepo.name:sm-repo}") String submodelRepositoryName) {
		this(submodelBackendProvider, submodelServiceFactory);

		this.submodelRepositoryName = submodelRepositoryName;
	}

	@Autowired(required = false)
	public SimpleSubmodelRepositoryFactory(SubmodelBackendProvider submodelBackendProvider, SubmodelServiceFactory submodelServiceFactory, Collection<Submodel> submodels) {
		this(submodelBackendProvider, submodelServiceFactory);

		this.submodels = submodels;
	}

	@Autowired(required = false)
	public SimpleSubmodelRepositoryFactory(SubmodelBackendProvider submodelBackendProvider, SubmodelServiceFactory submodelServiceFactory, Collection<Submodel> submodels,
			@Value("${basyx.smrepo.name:sm-repo}") String submodelRepositoryName) {
		this(submodelBackendProvider, submodelServiceFactory, submodels);

		this.submodelRepositoryName = submodelRepositoryName;
	}

	@Override
	public SubmodelRepository create() {

		if (submodels == null)
			return new CrudSubmodelRepository(submodelBackendProvider, submodelServiceFactory, submodelRepositoryName);

		return new CrudSubmodelRepository(submodelBackendProvider, submodelServiceFactory, submodels, submodelRepositoryName);
	}

}

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

package org.eclipse.digitaltwin.basyx.aasrepository.backend;

import org.eclipse.digitaltwin.basyx.aasrepository.AasRepository;
import org.eclipse.digitaltwin.basyx.aasrepository.AasRepositoryFactory;
import org.eclipse.digitaltwin.basyx.aasservice.AasServiceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

/**
 * Simple AAS repository factory that creates a {@link CrudAasRepository} with
 * a backend provider and a service factory
 * 
 * @author mateusmolina
 * 
 */
@Component
@ConditionalOnExpression("!T(org.springframework.util.StringUtils).isEmpty('${basyx.backend:}')")
public class SimpleAasRepositoryFactory implements AasRepositoryFactory {

	private AasBackendProvider aasBackendProvider;

	private AasServiceFactory aasServiceFactory;

	private String aasRepositoryName = null;

	@Autowired(required = false)
	public SimpleAasRepositoryFactory(AasBackendProvider aasBackendProvider, AasServiceFactory aasServiceFactory) {
		this.aasBackendProvider = aasBackendProvider;
		this.aasServiceFactory = aasServiceFactory;
	}

	@Autowired(required = false)
	public SimpleAasRepositoryFactory(AasBackendProvider aasBackendProvider, AasServiceFactory aasServiceFactory, @Value("${basyx.aasrepo.name:aas-repo}") String aasRepositoryName) {
		this(aasBackendProvider, aasServiceFactory);
		this.aasRepositoryName = aasRepositoryName;
	}

	@Override
	public AasRepository create() {
		return new CrudAasRepository(aasBackendProvider, aasServiceFactory, aasRepositoryName);
	}

}

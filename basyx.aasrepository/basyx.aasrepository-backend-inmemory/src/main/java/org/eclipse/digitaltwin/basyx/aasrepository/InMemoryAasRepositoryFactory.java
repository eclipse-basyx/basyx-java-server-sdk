/*******************************************************************************
 * Copyright (C) 2022 the Eclipse BaSyx Authors
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

package org.eclipse.digitaltwin.basyx.aasrepository;

import org.eclipse.digitaltwin.basyx.aasservice.AasServiceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

/**
 * AasRepository factory returning an in-memory backend AasRepository
 * 
 * @author schnicke, kammognie
 */
@ConditionalOnExpression("'${basyx.aasrepository.backend}'.equals('InMemory') or '${basyx.backend}'.equals('InMemory')")
@Component
public class InMemoryAasRepositoryFactory implements AasRepositoryFactory {

	private AasServiceFactory aasApiFactory;
	
	private String aasRepositoryName;

	@Autowired(required = false)
	public InMemoryAasRepositoryFactory(AasServiceFactory aasApiFactory) {
		this.aasApiFactory = aasApiFactory;
	}
	
	@Autowired(required = false)
	public InMemoryAasRepositoryFactory(AasServiceFactory aasApiFactory, @Value("${basyx.aasrepo.name:aas-repo}") String aasRepositoryName) {
		this(aasApiFactory);
		this.aasRepositoryName = aasRepositoryName;
	}

	@Override
	public AasRepository create() {
		return new InMemoryAasRepository(aasApiFactory, aasRepositoryName);
	}

}

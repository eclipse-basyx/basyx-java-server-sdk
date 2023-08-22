/*******************************************************************************
 * Copyright (C) 2023 the Eclipse BaSyx Authors
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 * 
 * SPDX-License-Identifier: MIT
 ******************************************************************************/

package org.eclipse.digitaltwin.basyx.submodelrepository;

import org.eclipse.digitaltwin.basyx.submodelservice.SubmodelServiceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

/**
 * SubmodelRepository factory returning an in-memory backend AasRepository
 * 
 * @author schnicke
 */
@ConditionalOnExpression("'${basyx.submodelrepository.backend}'.equals('InMemory') or '${basyx.backend}'.equals('InMemory')")
@Component
public class InMemorySubmodelRepositoryFactory implements SubmodelRepositoryFactory {
	private SubmodelServiceFactory submodelServiceFactory;
	
	private String smRepositoryName;

	@Autowired(required = false)
	public InMemorySubmodelRepositoryFactory(SubmodelServiceFactory submodelServiceFactory) {
		this.submodelServiceFactory = submodelServiceFactory;
	}
	
	@Autowired(required = false)
	public InMemorySubmodelRepositoryFactory(SubmodelServiceFactory submodelServiceFactory, @Value("${basyx.smrepo.name:sm-repo}") String smRepositoryName) {
		this(submodelServiceFactory);
		this.smRepositoryName = smRepositoryName;
	}

	@Override
	public SubmodelRepository create() {
		return new InMemorySubmodelRepository(submodelServiceFactory, smRepositoryName);
	}

}

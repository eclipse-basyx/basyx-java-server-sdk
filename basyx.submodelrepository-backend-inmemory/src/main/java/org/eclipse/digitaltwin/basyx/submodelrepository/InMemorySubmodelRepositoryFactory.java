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

import java.util.Collection;
import java.util.HashSet;

import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.springframework.stereotype.Component;

/**
 * SubmodelRepository factory returning an in-memory backend AasRepository
 * 
 * @author schnicke
 */
@Component
public class InMemorySubmodelRepositoryFactory implements SubmodelRepositoryFactory {

	private Collection<Submodel> submodels;

	public InMemorySubmodelRepositoryFactory() {
		submodels = new HashSet<>();
	}

	/**
	 * Configures passed submodels as preconfigured for the created
	 * SubmodelRepository
	 * 
	 * @param submodels
	 */
	public InMemorySubmodelRepositoryFactory(Collection<Submodel> submodels) {
		this.submodels = submodels;
	}

	@Override
	public SubmodelRepository create() {
		return new InMemorySubmodelRepository(submodels);
	}

}

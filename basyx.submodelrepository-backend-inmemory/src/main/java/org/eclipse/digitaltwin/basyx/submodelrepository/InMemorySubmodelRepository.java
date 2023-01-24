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

package org.eclipse.digitaltwin.basyx.submodelrepository;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.basyx.core.exceptions.CollidingIdentifierException;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;

/**
 * In-memory implementation of the SubmodelRepository
 *
 * @author schnicke
 *
 */
public class InMemorySubmodelRepository implements SubmodelRepository {

	private Map<String, Submodel> submodels = new LinkedHashMap<>();

	public InMemorySubmodelRepository() {
	}

	/**
	 * Creates an in-memory submodel repository containing the passed submodels
	 * 
	 * @param submodels
	 */
	public InMemorySubmodelRepository(Collection<Submodel> submodels) {
		assertIdUniqueness(submodels);

		this.submodels = convertToMap(submodels);
	}


	private Map<String, Submodel> convertToMap(Collection<Submodel> submodels) {
		Map<String, Submodel> map = new LinkedHashMap<>();
		submodels.forEach(s -> map.put(s.getId(), s));

		return map;
	}

	private static void assertIdUniqueness(Collection<Submodel> submodelsToCheck) {
		Set<String> ids = new HashSet<>();

		for (Submodel submodel : submodelsToCheck) {
			String submodelId =submodel.getId();
			boolean unique = ids.add(submodelId);

			if (!unique) {
				throw new CollidingIdentifierException(submodelId);
			}
		}
	}

	@Override
	public Collection<Submodel> getAllSubmodels() {
		return submodels.values();
	}

	@Override
	public Submodel getSubmodel(String id) throws ElementDoesNotExistException {
		throwIfSubmodelDoesNotExist(id);

		return submodels.get(id);
	}

	@Override
	public void updateSubmodel(String id, Submodel submodel) throws ElementDoesNotExistException {
		throwIfSubmodelDoesNotExist(id);

		submodels.put(id, submodel);
	}

	@Override
	public void createSubmodel(Submodel submodel) throws CollidingIdentifierException {
		throwIfSubmodelExists(submodel.getId());

		submodels.put(submodel.getId(), submodel);
	}

	private void throwIfSubmodelExists(String id) {
		if (submodels.containsKey(id))
			throw new CollidingIdentifierException(id);
	}

	private void throwIfSubmodelDoesNotExist(String id) {
		if (!submodels.containsKey(id))
			throw new ElementDoesNotExistException(id);
	}
}

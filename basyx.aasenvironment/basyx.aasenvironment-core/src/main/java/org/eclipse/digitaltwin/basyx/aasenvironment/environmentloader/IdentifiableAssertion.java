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
package org.eclipse.digitaltwin.basyx.aasenvironment.environmentloader;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import org.eclipse.digitaltwin.aas4j.v3.model.Environment;
import org.eclipse.digitaltwin.aas4j.v3.model.Identifiable;
import org.eclipse.digitaltwin.basyx.aasrepository.AasRepository;
import org.eclipse.digitaltwin.basyx.core.exceptions.CollidingIdentifierException;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;
import org.eclipse.digitaltwin.basyx.submodelrepository.SubmodelRepository;

/**
 * Assertion to ensure that no duplicates ids are loaded from file
 *
 * @author Gerhard Sonnenberg DFKI GmbH, danish
 *
 */
public class IdentifiableAssertion {

	private AasRepository aasRepo;
	private SubmodelRepository smRepo;
	private final Set<String> currentShellIds = new HashSet<>();
	private final Set<String> currentSubmodelIds = new HashSet<>();

	public IdentifiableAssertion(AasRepository aasRepo, SubmodelRepository smRepo) {
		this.aasRepo = aasRepo;
		this.smRepo = smRepo;
	}

	public void assertNoDuplicateIds(Environment environment) {
		assertNoDuplicateIds(environment.getAssetAdministrationShells(), currentShellIds, id -> {
		    aasRepo.getAas(id);
		    return null;
		});
		assertNoDuplicateIds(environment.getSubmodels(), currentSubmodelIds, id -> {
		    smRepo.getSubmodel(id);
		    return null;
		});
	}

	private <T extends Identifiable> void assertNoDuplicateIds(List<T> identifiables, Set<String> currentIds,
			Function<String, Void> retrieveElementFunction) throws CollidingIdentifierException {

		if (identifiables == null) {
			return;
		}

		for (T eachIdentifiable : identifiables) {
			String id = eachIdentifiable.getId();
			boolean success = currentIds.add(id);
			if (!success) {
				try {
					retrieveElementFunction.apply(id);
					
					throw new CollidingIdentifierException(id);
				} catch (ElementDoesNotExistException e) {
					return;
				}
			}
		}
	}

}
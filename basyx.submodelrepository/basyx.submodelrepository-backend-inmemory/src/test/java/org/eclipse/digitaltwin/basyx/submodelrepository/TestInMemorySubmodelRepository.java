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

package org.eclipse.digitaltwin.basyx.submodelrepository;

import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.basyx.core.exceptions.CollidingIdentifierException;
import org.eclipse.digitaltwin.basyx.core.filerepository.InMemoryFileRepository;
import org.eclipse.digitaltwin.basyx.submodelrepository.backend.CrudSubmodelRepository;
import org.eclipse.digitaltwin.basyx.submodelrepository.backend.CrudSubmodelRepositoryFactory;
import org.eclipse.digitaltwin.basyx.submodelrepository.core.SubmodelRepositorySuite;
import org.eclipse.digitaltwin.basyx.submodelservice.DummySubmodelFactory;
import org.eclipse.digitaltwin.basyx.submodelservice.InMemorySubmodelBackend;
import org.junit.Test;
import org.junit.Test.None;

import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Tests the {@link CrudSubmodelRepository} with InMemory backend
 * 
 * @author schnicke, kammognie, danish
 *
 */
public class TestInMemorySubmodelRepository extends SubmodelRepositorySuite {

	private static final String CONFIGURED_SM_REPO_NAME = "configured-sm-repo-name";
	
	@Override
	protected SubmodelRepository getSubmodelRepository() {
		return CrudSubmodelRepositoryFactory.builder().backend(new InMemorySubmodelBackend()).fileRepository(new InMemoryFileRepository()).create();
	}

	@Override
	protected SubmodelRepository getSubmodelRepository(Collection<Submodel> submodels) {
		return CrudSubmodelRepositoryFactory.builder().backend(new InMemorySubmodelBackend()).fileRepository(new InMemoryFileRepository()).remoteCollection(submodels).create();
	}
	
	@Override
	protected boolean fileExistsInStorage(String fileValue) {
		java.io.File file = new java.io.File(fileValue);
		
		return file.exists();
	}

	@Test
	public void getConfiguredInMemorySmRepositoryName() {
		SubmodelRepository repo = CrudSubmodelRepositoryFactory.builder().backend(new InMemorySubmodelBackend()).fileRepository(new InMemoryFileRepository()).repositoryName(CONFIGURED_SM_REPO_NAME).create();

		assertEquals(CONFIGURED_SM_REPO_NAME, repo.getName());
	}

	@Test(expected = CollidingIdentifierException.class)
	public void idCollisionDuringConstruction() {
		Collection<Submodel> submodelsWithCollidingIds = createSubmodelCollectionWithCollidingIds();
		
		getSubmodelRepository(submodelsWithCollidingIds);
	}
	
	@Test(expected = None.class)
	public void assertIdUniqueness() {
		Collection<Submodel> submodelsWithUniqueIds = createSubmodelCollectionWithUniqueIds();
		
		getSubmodelRepository(submodelsWithUniqueIds);
	}

	private Collection<Submodel> createSubmodelCollectionWithCollidingIds() {
		return List.of(DummySubmodelFactory.createTechnicalDataSubmodel(), DummySubmodelFactory.createTechnicalDataSubmodel());
	}
	
	private Collection<Submodel> createSubmodelCollectionWithUniqueIds() {
		return List.of(DummySubmodelFactory.createSimpleDataSubmodel(), DummySubmodelFactory.createTechnicalDataSubmodel());
	}

}

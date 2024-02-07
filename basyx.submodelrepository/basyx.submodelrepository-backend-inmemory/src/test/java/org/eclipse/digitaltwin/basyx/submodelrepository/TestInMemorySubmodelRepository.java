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

import static org.junit.Assert.assertEquals;

import java.util.Collection;

import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.basyx.core.exceptions.CollidingIdentifierException;
import org.eclipse.digitaltwin.basyx.submodelrepository.core.SubmodelRepositorySuite;
import org.eclipse.digitaltwin.basyx.submodelservice.DummySubmodelFactory;
import org.eclipse.digitaltwin.basyx.submodelservice.InMemorySubmodelServiceFactory;
import org.junit.Test;
import org.junit.Test.None;

import com.google.common.collect.Lists;

/**
 * 
 * @author schnicke, kammognie
 *
 */
public class TestInMemorySubmodelRepository extends SubmodelRepositorySuite {

	private static final String CONFIGURED_SM_REPO_NAME = "configured-sm-repo-name";
	
	@Override
	protected SubmodelRepository getSubmodelRepository() {
		return new InMemorySubmodelRepository(new InMemorySubmodelServiceFactory());
	}

	@Override
	protected SubmodelRepository getSubmodelRepository(Collection<Submodel> submodels) {
		return new InMemorySubmodelRepository(new InMemorySubmodelServiceFactory(), submodels);
	}

	@Override
	protected boolean fileExistsInStorage(String fileValue) {
		java.io.File file = new java.io.File(fileValue);
		return file.exists();
	}

	@Test
	public void getConfiguredInMemorySmRepositoryName() {
		SubmodelRepository repo = new InMemorySubmodelRepository(new InMemorySubmodelServiceFactory(), CONFIGURED_SM_REPO_NAME);

		assertEquals(CONFIGURED_SM_REPO_NAME, repo.getName());
	}

	@Test(expected = CollidingIdentifierException.class)
	public void idCollisionDuringConstruction() {
		Collection<Submodel> submodelsWithCollidingIds = createSubmodelCollectionWithCollidingIds();
		new InMemorySubmodelRepository(new InMemorySubmodelServiceFactory(), submodelsWithCollidingIds);
	}
	
	@Test(expected = None.class)
	public void assertIdUniqueness() {
		Collection<Submodel> submodelsWithUniqueIds = createSubmodelCollectionWithUniqueIds();
		new InMemorySubmodelRepository(new InMemorySubmodelServiceFactory(), submodelsWithUniqueIds);
	}

	private Collection<Submodel> createSubmodelCollectionWithCollidingIds() {
		return Lists.newArrayList(DummySubmodelFactory.createTechnicalDataSubmodel(), DummySubmodelFactory.createTechnicalDataSubmodel());
	}
	
	private Collection<Submodel> createSubmodelCollectionWithUniqueIds() {
		return Lists.newArrayList(DummySubmodelFactory.createSimpleDataSubmodel(), DummySubmodelFactory.createTechnicalDataSubmodel());
	}

}

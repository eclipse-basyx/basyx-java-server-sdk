/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
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


package org.eclipse.digitaltwin.basyx.aasrepository.backend.inmemory;

import static org.junit.Assert.assertEquals;

import org.eclipse.digitaltwin.basyx.aasrepository.AasRepository;
import org.eclipse.digitaltwin.basyx.aasrepository.AasRepositorySuite;
import org.eclipse.digitaltwin.basyx.aasrepository.backend.AasBackendProvider;
import org.eclipse.digitaltwin.basyx.aasrepository.backend.CrudAasRepository;
import org.eclipse.digitaltwin.basyx.aasrepository.backend.SimpleAasRepositoryFactory;
import org.eclipse.digitaltwin.basyx.aasservice.backend.InMemoryAasServiceFactory;
import org.junit.Test;

/**
 * Tests the {@link InMemoryAasRepository} name
 * 
 * @author schnicke, kammognie, mateusmolina
 *
 */
public class TestInMemoryAasRepository extends AasRepositorySuite {
	private static final String CONFIGURED_AAS_REPO_NAME = "configured-aas-repo-name";
	
	private AasBackendProvider backendProvider = new AasInMemoryBackendProvider();

	@Override
	protected AasRepository getAasRepository() {
		return new SimpleAasRepositoryFactory(backendProvider, new InMemoryAasServiceFactory()).create();
	}

	@Override
	protected void sanitizeRepository() {
		backendProvider.getCrudRepository().deleteAll();
	}

	@Test
    public void getConfiguredInMemoryAasRepositoryName() {
		AasRepository repo = new CrudAasRepository(backendProvider, new InMemoryAasServiceFactory(), CONFIGURED_AAS_REPO_NAME);
		
		assertEquals(CONFIGURED_AAS_REPO_NAME, repo.getName());
	}

}

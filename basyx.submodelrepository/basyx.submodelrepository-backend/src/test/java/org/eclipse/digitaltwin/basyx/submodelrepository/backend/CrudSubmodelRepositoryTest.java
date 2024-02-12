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

import static org.junit.Assert.assertEquals;

import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.basyx.core.file.FileRepository;
import org.eclipse.digitaltwin.basyx.submodelrepository.SubmodelRepository;
import org.junit.Test;
import org.springframework.data.repository.CrudRepository;

/**
 * Tests {@link CrudSubmodelRepository}
 * 
 * @author mateusmolina, despen, danish
 */
public class CrudSubmodelRepositoryTest {

	private static final String CONFIGURED_SUBMODEL_REPO_NAME = "test-sm-repo";

	@Test
	public void getConfiguredAasRepositoryName() {
		SubmodelRepository repo = new CrudSubmodelRepository(createSubmodelProvider(), null, CONFIGURED_SUBMODEL_REPO_NAME);

		assertEquals(CONFIGURED_SUBMODEL_REPO_NAME, repo.getName());
	}

	private SubmodelBackendProvider createSubmodelProvider() {
		
		return new SubmodelBackendProvider() {
			@Override
			public CrudRepository<Submodel, String> getCrudRepository() {
				return null;
			}

			@Override
			public FileRepository getFileRepository() {
				return null;
			}
		};
	}

}

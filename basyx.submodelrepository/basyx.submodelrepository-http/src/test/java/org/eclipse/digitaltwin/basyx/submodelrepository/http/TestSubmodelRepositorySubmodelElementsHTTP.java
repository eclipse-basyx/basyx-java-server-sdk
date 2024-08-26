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

package org.eclipse.digitaltwin.basyx.submodelrepository.http;

import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.eclipse.digitaltwin.basyx.submodelrepository.SubmodelRepository;
import org.eclipse.digitaltwin.basyx.submodelservice.http.SubmodelServiceSubmodelElementsTestSuiteHTTP;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Tests the SubmodelElement specific parts of the SubmodelRepository HTTP/REST
 * API
 * 
 * @author schnicke, danish
 *
 */
public class TestSubmodelRepositorySubmodelElementsHTTP extends SubmodelServiceSubmodelElementsTestSuiteHTTP {
	private static final PaginationInfo NO_LIMIT_PAGINATION_INFO = new PaginationInfo(0, null);
	private static ConfigurableApplicationContext appContext;

	@BeforeClass
	public static void startAASRepo() throws Exception {
		appContext = new SpringApplicationBuilder(DummySubmodelRepositoryComponent.class).profiles("httptests").run(new String[] {});
	}

	@Before
	public void createSubmodelOnRepo() {
		SubmodelRepository repo = appContext.getBean(SubmodelRepository.class);
		repo.createSubmodel(createSubmodel());
	}

	@After
	public void removeSubmodelFromRepo() {
		SubmodelRepository repo = appContext.getBean(SubmodelRepository.class);
		repo.getAllSubmodels(NO_LIMIT_PAGINATION_INFO).getResult().stream().map(s -> s.getId()).forEach(repo::deleteSubmodel);
	}

	@AfterClass
	public static void shutdownAASRepo() {
		appContext.close();
	}

	@Override
	protected String getURL() {

		return BaSyxSubmodelHttpTestUtils.getSpecificSubmodelAccessPath("http://localhost:8080/submodels", createSubmodel().getId());
	}

}

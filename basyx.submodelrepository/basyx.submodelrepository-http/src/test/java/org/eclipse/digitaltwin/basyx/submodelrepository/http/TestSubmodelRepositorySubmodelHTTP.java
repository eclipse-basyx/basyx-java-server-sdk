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

import java.util.Collection;

import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.eclipse.digitaltwin.basyx.submodelrepository.SubmodelRepository;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Tests the Submodel specific parts of the SubmodelRepository HTTP/REST API
 * 
 * @author schnicke, danish
 *
 */
public class TestSubmodelRepositorySubmodelHTTP extends SubmodelRepositorySubmodelHTTPTestSuite {
	private static final PaginationInfo NO_LIMIT_PAGINATION_INFO = new PaginationInfo(0, null);
	private static ConfigurableApplicationContext appContext;

	@BeforeClass
	public static void startAASRepo() throws Exception {
		appContext = new SpringApplicationBuilder(DummySubmodelRepositoryComponent.class).profiles("httptests").run(new String[] {});
	}

	@Override
	public void resetRepository() {
		SubmodelRepository repo = appContext.getBean(SubmodelRepository.class);
		repo.getAllSubmodels(NO_LIMIT_PAGINATION_INFO).getResult().stream().map(s -> s.getId()).forEach(repo::deleteSubmodel);
	}

	@Override
	public void populateRepository() {
		SubmodelRepository repo = appContext.getBean(SubmodelRepository.class);
		Collection<Submodel> submodels = createSubmodels();
		submodels.forEach(repo::createSubmodel);
	}

	@AfterClass
	public static void shutdownAASRepo() {
		appContext.close();
	}

	@Override
	protected String getURL() {
		return "http://localhost:8080/submodels";
	}

}
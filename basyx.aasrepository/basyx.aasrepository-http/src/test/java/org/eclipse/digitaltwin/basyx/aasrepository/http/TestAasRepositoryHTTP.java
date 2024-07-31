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

package org.eclipse.digitaltwin.basyx.aasrepository.http;

import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.basyx.aasrepository.AasRepository;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Tests the {@link AssetAdministrationShell} specific parts of the
 * {@link AasRepository} HTTP/REST API
 * 
 * @author schnicke, danish
 *
 */
public class TestAasRepositoryHTTP extends AasRepositoryHTTPSuite {
	private static final PaginationInfo NO_LIMIT_PAGINATION_INFO = new PaginationInfo(0, null);
	private static ConfigurableApplicationContext appContext;

	@BeforeClass
	public static void startAasRepo() throws Exception {
		appContext = new SpringApplicationBuilder(DummyAasRepositoryComponent.class).profiles("httptests").run(new String[] {});
	}

	@Override
	public void resetRepository() {
		AasRepository repo = appContext.getBean(AasRepository.class);
		repo.getAllAas(NO_LIMIT_PAGINATION_INFO)
				.getResult()
				.stream()
				.map(a -> a.getId())
				.forEach(repo::deleteAas);
	}

	@AfterClass
	public static void shutdownAasRepo() {
		appContext.close();
	}

	@Override
	protected String getURL() {
		return "http://localhost:8080/shells";
	}

}

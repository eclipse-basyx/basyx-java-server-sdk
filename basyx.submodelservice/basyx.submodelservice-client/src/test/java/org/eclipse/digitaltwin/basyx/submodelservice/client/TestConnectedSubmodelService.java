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


package org.eclipse.digitaltwin.basyx.submodelservice.client;

import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.basyx.http.Base64UrlEncodedIdentifier;
import org.eclipse.digitaltwin.basyx.submodelrepository.SubmodelRepository;
import org.eclipse.digitaltwin.basyx.submodelrepository.http.DummySubmodelRepositoryComponent;
import org.eclipse.digitaltwin.basyx.submodelservice.SubmodelService;
import org.eclipse.digitaltwin.basyx.submodelservice.SubmodelServiceSuite;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Features of the client not implemented but existing in the test suite are
 * overwritten to pass. This is required to enable reuse of the test suite.
 * Whenever a feature is implemented, the respective test here has to be
 * removed.
 * 
 * @author schnicke, mateusmolina
 */
public class TestConnectedSubmodelService extends SubmodelServiceSuite {

	private static ConfigurableApplicationContext appContext;

	@BeforeClass
	public static void startSubmodelService() throws Exception {
		appContext = new SpringApplicationBuilder(DummySubmodelRepositoryComponent.class).profiles("httptests").run(new String[] {});
	}

	@AfterClass
	public static void shutdownSubmodelService() {
		appContext.close();
	}

	@After
	public void removeSubmodelFromRepo() {
		SubmodelRepository repo = appContext.getBean(SubmodelRepository.class);
		repo.getAllSubmodels(NO_LIMIT_PAGINATION_INFO).getResult().stream().map(s -> s.getId()).forEach(repo::deleteSubmodel);
	}

	@Override
	protected SubmodelService getSubmodelService(Submodel submodel) {
		SubmodelRepository repo = appContext.getBean(SubmodelRepository.class);
		repo.createSubmodel(submodel);
		String base64UrlEncodedId = Base64UrlEncodedIdentifier.encodeIdentifier(submodel.getId());
		return new ConnectedSubmodelService("http://localhost:8081/submodels/" + base64UrlEncodedId);
	}

	@Override
	protected boolean fileExistsInStorage(String fileValue) {
		java.io.File file = new java.io.File(fileValue);

		return file.exists();
	}
}

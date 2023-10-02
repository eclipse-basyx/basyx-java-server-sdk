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

package authorization;

import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.eclipse.digitaltwin.basyx.submodelrepository.SubmodelRepository;
import org.eclipse.digitaltwin.basyx.submodelrepository.feature.authorization.AuthorizationSubmodelRepository;
import org.eclipse.digitaltwin.basyx.submodelrepository.http.SubmodelRepositoryApiHTTPController;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collection;

/**
 * Tests the Submodel specific parts of the SubmodelRepository HTTP/REST API
 * 
 * @author schnicke, danish
 *
 */
//@WebMvcTest(SubmodelRepositoryApiHTTPController.class)
//@TestPropertySource(properties = { "basyx.backend = InMemory" })
//@TestPropertySource(properties = { "basyx.submodelrepository.feature.authorization.enabled = false" })
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration
//@SpringBootApplication(scanBasePackages = "org.eclipse.digitaltwin.basyx")
public class TestSubmodelRepositorySubmodelHTTP extends SubmodelRepositorySubmodelHTTPTestSuite {
	private static final PaginationInfo NO_LIMIT_PAGINATION_INFO = new PaginationInfo(0, null);
	private static ConfigurableApplicationContext appContext;

	@BeforeClass
	public static void startAASRepo() throws Exception {
		appContext = new SpringApplication(DummySubmodelRepositoryComponent.class).run(new String[] {});
	}

	@Override
	public void resetRepository() {
		final SubmodelRepository<?, ?> repo = getSubmodelRepository();
		repo.getAllSubmodels(NO_LIMIT_PAGINATION_INFO, null).getResult().stream().map(s -> s.getId())
				.forEach(repo::deleteSubmodel);
	}

	@Override
	public void populateRepository() {
		final SubmodelRepository<?, ?> repo = getSubmodelRepository();
		Collection<Submodel> submodels = createSubmodels();
		submodels.forEach(repo::createSubmodel);
	}

	private SubmodelRepository<?, ?> getSubmodelRepository() {
		SubmodelRepository<?, ?> repo = appContext.getBean(SubmodelRepository.class);
		if (repo instanceof AuthorizationSubmodelRepository) {
			repo = ((AuthorizationSubmodelRepository<?, ?>) repo).getDecorated();
		}
		return repo;
	}

	@AfterClass
	public static void shutdownAASRepo() {
		appContext.close();
	}

	@Override
	protected String getURL() {
		return "http://localhost:8079/submodels";
	}
}
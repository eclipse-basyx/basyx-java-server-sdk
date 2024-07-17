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

package org.eclipse.digitaltwin.basyx.conceptdescriptionrepository.http;

import java.util.Collection;

import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.ConceptDescription;
import org.eclipse.digitaltwin.basyx.conceptdescriptionrepository.ConceptDescriptionRepository;
import org.eclipse.digitaltwin.basyx.conceptdescriptionrepository.core.DummyConceptDescriptionFactory;
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
public class TestConceptDescriptionRepositoryHTTP extends ConceptDescriptionRepositoryHTTPSuite {
	private static final PaginationInfo NO_LIMIT_PAGINATION_INFO = new PaginationInfo(0, null);
	private static ConfigurableApplicationContext appContext;

	@BeforeClass
	public static void startConceptDescriptionRepo() throws Exception {
		appContext = new SpringApplicationBuilder(DummyConceptDescriptionRepositoryComponent.class).profiles("httptests").run(new String[] {});
	}

	@Override
	public void resetRepository() {
		ConceptDescriptionRepository repo = appContext.getBean(ConceptDescriptionRepository.class);
		Collection<ConceptDescription> conceptDescriptions = DummyConceptDescriptionFactory.getConceptDescriptions();
		resetRepoToDefaultConceptDescriptions(repo, conceptDescriptions);
	}

	private void resetRepoToDefaultConceptDescriptions(ConceptDescriptionRepository repo, Collection<ConceptDescription> conceptDescriptions) {
		repo.getAllConceptDescriptions(NO_LIMIT_PAGINATION_INFO)
				.getResult()
				.stream()
				.map(s -> s.getId())
				.forEach(repo::deleteConceptDescription);

		conceptDescriptions.forEach(repo::createConceptDescription);
	}

	@AfterClass
	public static void shutdownAasRepo() {
		appContext.close();
	}

	@Override
	protected String getURL() {
		return "http://localhost:8080/concept-descriptions";
	}

}

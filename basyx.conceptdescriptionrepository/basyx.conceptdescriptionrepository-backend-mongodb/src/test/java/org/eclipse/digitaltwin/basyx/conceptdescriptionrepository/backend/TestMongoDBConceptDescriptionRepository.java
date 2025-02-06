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

package org.eclipse.digitaltwin.basyx.conceptdescriptionrepository.backend;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collection;

import org.eclipse.digitaltwin.aas4j.v3.model.ConceptDescription;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultConceptDescription;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultLangStringTextType;
import org.eclipse.digitaltwin.basyx.common.mongocore.MongoDBUtilities;
import org.eclipse.digitaltwin.basyx.conceptdescriptionrepository.ConceptDescriptionRepository;
import org.eclipse.digitaltwin.basyx.conceptdescriptionrepository.core.ConceptDescriptionRepositorySuite;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Tests the {@link MongoDBConceptDescriptionRepository}
 * 
 * @author danish, kammognie, mateusmolina
 *
 */
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestMongoDBConceptDescriptionRepository extends ConceptDescriptionRepositorySuite {
	private static final String CONFIGURED_CD_REPO_NAME = "configured-cd-repo-name";
	private static final String COLLECTION = "conceptDescTestCollection";

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private ConceptDescriptionRepository conceptDescriptionRepository;

	@Autowired
	private ConceptDescriptionRepositoryBackend conceptDescriptionRepositoryBackend;

	@Override
	protected ConceptDescriptionRepository getConceptDescriptionRepository() {
		return conceptDescriptionRepository;
	}

	@Override
	protected ConceptDescriptionRepository getConceptDescriptionRepository(Collection<ConceptDescription> conceptDescriptions) {
		MongoDBUtilities.clearCollection(mongoTemplate, COLLECTION);

		return new CrudConceptDescriptionRepositoryFactory(conceptDescriptionRepositoryBackend).withRemoteCollection(conceptDescriptions).create();
	}

	@Test
	public void testConfiguredMongoDBConceptDescriptionRepositoryName() {
		MongoDBUtilities.clearCollection(mongoTemplate, COLLECTION);

		ConceptDescriptionRepository repo = new CrudConceptDescriptionRepository(conceptDescriptionRepositoryBackend, CONFIGURED_CD_REPO_NAME);

		assertEquals(CONFIGURED_CD_REPO_NAME, repo.getName());
	}

	@Test
	public void conceptDescriptionIsPersisted() {
		ConceptDescription expectedConceptDescription = createDummyConceptDescriptionOnRepo(conceptDescriptionRepository);
		ConceptDescription retrievedConceptDescription = getConceptDescriptionFromNewBackendInstance(conceptDescriptionRepository, expectedConceptDescription.getId());

		assertEquals(expectedConceptDescription, retrievedConceptDescription);
	}

	@Test
	public void updatedConceptDescriptionIsPersisted() {
		ConceptDescriptionRepository mongoDBConceptDescriptionRepository = getConceptDescriptionRepository();

		ConceptDescription expectedConceptDescription = createDummyConceptDescriptionOnRepo(mongoDBConceptDescriptionRepository);

		addDescriptionToConceptDescription(expectedConceptDescription);

		mongoDBConceptDescriptionRepository.updateConceptDescription(expectedConceptDescription.getId(), expectedConceptDescription);

		ConceptDescription retrievedConceptDescription = getConceptDescriptionFromNewBackendInstance(mongoDBConceptDescriptionRepository, expectedConceptDescription.getId());

		assertEquals(expectedConceptDescription, retrievedConceptDescription);
	}

	private void addDescriptionToConceptDescription(ConceptDescription expectedConceptDescription) {
		expectedConceptDescription.setDescription(Arrays.asList(new DefaultLangStringTextType.Builder().text("description").language("en").build()));
	}

	private ConceptDescription getConceptDescriptionFromNewBackendInstance(ConceptDescriptionRepository conceptDescriptionRepository, String conceptDescriptionId) {
		return conceptDescriptionRepository.getConceptDescription(conceptDescriptionId);
	}

	private ConceptDescription createDummyConceptDescriptionOnRepo(ConceptDescriptionRepository conceptDescriptionRepository) {
		ConceptDescription expectedConceptDescription = new DefaultConceptDescription.Builder().id("dummy").build();

		conceptDescriptionRepository.createConceptDescription(expectedConceptDescription);

		return expectedConceptDescription;
	}

}

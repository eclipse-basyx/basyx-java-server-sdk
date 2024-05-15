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

package org.eclipse.digitaltwin.basyx.aasrepository.backend.mongodb;

import org.eclipse.digitaltwin.basyx.aasrepository.AasRepository;
import org.eclipse.digitaltwin.basyx.aasrepository.AasRepositoryPersistencyTestSuite;
import org.eclipse.digitaltwin.basyx.common.mongocore.MongoDBUtilities;
import org.junit.Before;
import org.junit.BeforeClass;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * Test persistency for {@link AasRepository} with MongoDB storage backend
 * 
 * @author mateusmolina, danish
 */
public class TestMongoDBAasRepositoryPersistency extends AasRepositoryPersistencyTestSuite {
	private static ConfigurableApplicationContext applicationContext;

	@BeforeClass
	public static void initComponent() {
		applicationContext = new SpringApplication(DummyAasRepositoryComponent.class).run();
	}

	@Before
	public void clearTemplate() {
		MongoTemplate mongoTemplate = applicationContext.getBean(MongoTemplate.class);
		MongoDBUtilities.clearCollection(mongoTemplate, DummyAasRepositoryConfig.COLLECTION);
	}

	@Override
	protected AasRepository getAasRepository() {
		return applicationContext.getBean(AasRepository.class);
	}

	@Override
	protected void restartComponent() {
		applicationContext.close();
		initComponent();
	}

}

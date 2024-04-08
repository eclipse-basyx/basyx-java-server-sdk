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

package org.eclipse.digitaltwin.basyx.submodelregistry.service.tests;

import org.eclipse.digitaltwin.basyx.submodelregistry.service.storage.SubmodelRegistryStorage;
import org.eclipse.digitaltwin.basyx.submodelregistry.service.tests.integration.PersistencyTestSuite;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Persistency test for MongoDbSubmodelRegistry
 * 
 * @author mateusmolina
 */
@TestPropertySource(properties = { "registry.type=mongodb", "spring.data.mongodb.database=submodelregistry", "spring.data.mongodb.uri=mongodb://mongoAdmin:mongoPassword@localhost:27017" })
@ContextConfiguration(classes = { org.eclipse.digitaltwin.basyx.submodelregistry.service.configuration.MongoDbConfiguration.class })
@EnableAutoConfiguration
@RunWith(SpringRunner.class)
public class MongoDbSubmodelRegistryPersistencyTest extends PersistencyTestSuite {

	@Autowired
	private ConfigurableApplicationContext context;

	@Autowired
	private SubmodelRegistryStorage storage;

	@Override
	protected SubmodelRegistryStorage getStorage() {
		return storage;
	}

	@Override
	protected void restartComponent() {
		context.stop();

		context.start();
	}
}
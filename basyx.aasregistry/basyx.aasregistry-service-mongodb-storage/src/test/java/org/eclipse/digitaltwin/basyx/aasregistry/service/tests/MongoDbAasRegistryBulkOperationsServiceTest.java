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

package org.eclipse.digitaltwin.basyx.aasregistry.service.tests;

import org.eclipse.digitaltwin.basyx.aasregistry.service.configuration.MongoDbConfiguration;
import org.eclipse.digitaltwin.basyx.aasregistry.service.storage.AasRegistryStorage;
import org.eclipse.digitaltwin.basyx.aasregistry.service.storage.AasRegistryBulkOperationsService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * Test for MongoDb AAS Transactions Service
 * 
 * @author mateusmolina
 */
@TestPropertySource(properties = { "registry.type=mongodb", "spring.data.mongodb.database=aasregistry", "spring.data.mongodb.uri=mongodb://localhost:27018" })
@ContextConfiguration(classes = { MongoDbConfiguration.class })
@EnableAutoConfiguration
@ExtendWith(SpringExtension.class)
public class MongoDbAasRegistryBulkOperationsServiceTest extends AasRegistryBulkOperationsServiceTestSuite {
        @Autowired
        private AasRegistryStorage aasRegistryStorage;

        @Autowired
        private AasRegistryBulkOperationsService bulkOperationsService;

        @Override
        AasRegistryStorage getAasRegistryStorage() {
                return aasRegistryStorage;
        }

        @Override
        AasRegistryBulkOperationsService getBulkOperationsService() {
                return bulkOperationsService;
        }
}
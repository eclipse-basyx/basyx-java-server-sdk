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

package org.eclipse.digitaltwin.basyx.aasdigitaltwinregistry.config;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.digitaltwin.basyx.aasdigitaltwinregistry.component.configurations.BeanExclusionConfig;
import org.eclipse.digitaltwin.basyx.aasdiscoveryservice.backend.AasDiscoveryDocumentBackend;
import org.eclipse.digitaltwin.basyx.aasdiscoveryservice.backend.mongodb.backend.MongoDBCrudAasDiscovery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
class BeanExclusionConfigTest {

    private AasDiscoveryDocumentBackend backend;
    private BeanExclusionConfig config;

    @BeforeEach
    void setUp() {
        backend = mock(AasDiscoveryDocumentBackend.class);
        config = new BeanExclusionConfig(backend);
    }

    @Test
    void testRemoveDuplicateControllersDoesNotFailWhenBeansAbsent() {
        log.info("Started unit test - testRemoveDuplicateControllersDoesNotFailWhenBeansAbsent()");
        DefaultListableBeanFactory beanFactory = spy(new DefaultListableBeanFactory());
        BeanFactoryPostProcessor processor = BeanExclusionConfig.removeDuplicateControllers();
        assertDoesNotThrow(() -> processor.postProcessBeanFactory(beanFactory));
        log.info("Successfully conducted unit test");
    }

    @Test
    void testMongoDBCrudAasDiscoveryBeanCreation() {
        log.info("Started unit test - testMongoDBCrudAasDiscoveryBeanCreation()");
        MongoDBCrudAasDiscovery mongoBean = config.mongoDBCrudAasDiscoveryBean();
        assertNotNull(mongoBean, "MongoDBCrudAasDiscovery bean should not be null");
        log.info("Successfully conducted unit test");
    }
}

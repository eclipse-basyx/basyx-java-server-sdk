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

package org.eclipse.digitaltwin.basyx.aasrepository.feature.discoveryintegration;

import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.basyx.aasdiscoveryservice.client.ConnectedAasDiscoveryService;
import org.eclipse.digitaltwin.basyx.aasdiscoveryservice.core.AasDiscoveryService;
import org.eclipse.digitaltwin.basyx.aasrepository.AasRepository;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

public class AasRepositoryDiscoveryIntegrationTest extends AasRepositoryDiscoveryIntegrationTestSuite {
    private static ConfigurableApplicationContext appContext;

    @BeforeClass
    public static void setUp() {
        appContext = SpringApplication.run(DummyAasRepositoryDiscoveryIntegrationComponent.class);
    }

    @AfterClass
    public static void tearDown() {
        appContext.close();
    }

    @After
    public void resetRepository() {
        AssetAdministrationShell shell = getDemoAAS(false, false);
        try {
            appContext.getBean(AasRepository.class).deleteAas(shell.getId());
        } catch (Exception e) {}
    }

    @Override
    protected AasDiscoveryService getDiscoveryService() {
        return new ConnectedAasDiscoveryService("http://localhost:8049");
    }

    @Override
    protected String getAASRepositoryURL() {
        return "http://localhost:4753/shells";
    }
}

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

package org.eclipse.digitaltwin.basyx.aasenvironment.client;

import org.eclipse.digitaltwin.basyx.aasregistry.client.api.RegistryAndDiscoveryInterfaceApi;
import org.eclipse.digitaltwin.basyx.aasrepository.AasRepository;
import org.eclipse.digitaltwin.basyx.aasrepository.client.ConnectedAasRepository;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.api.SubmodelRegistryApi;
import org.eclipse.digitaltwin.basyx.submodelrepository.client.ConnectedSubmodelRepository;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

public class TestConnectedAasManagerMultithreadingInMemory extends ConnectedAasManagerMultithreadingTestSuite {
    static final String AAS_REPOSITORY_BASE_PATH = "http://localhost:8081";
    static final String SM_REPOSITORY_BASE_PATH = "http://localhost:8081";
    static final String AAS_REGISTRY_BASE_PATH = "http://localhost:8050";
    static final String SM_REGISTRY_BASE_PATH = "http://localhost:8060";

    static ConnectedAasManager aasManager;
    static ConnectedAasRepository connectedAasRepository;
    static ConnectedSubmodelRepository connectedSmRepository;
    static RegistryAndDiscoveryInterfaceApi aasRegistryApi;
    static SubmodelRegistryApi smRegistryApi;
    static ConfigurableApplicationContext appContext;

    @BeforeClass
    public static void setupRepositories() {
        appContext = new SpringApplication(DummyAasEnvironmentComponent.class).run(new String[] {});
        connectedAasRepository = new ConnectedAasRepository(AAS_REPOSITORY_BASE_PATH);
        connectedSmRepository = new ConnectedSubmodelRepository(SM_REPOSITORY_BASE_PATH);
        aasRegistryApi = new RegistryAndDiscoveryInterfaceApi(AAS_REGISTRY_BASE_PATH);
        smRegistryApi = new SubmodelRegistryApi(SM_REGISTRY_BASE_PATH);
        aasManager = new ConnectedAasManager(AAS_REGISTRY_BASE_PATH, AAS_REPOSITORY_BASE_PATH, SM_REGISTRY_BASE_PATH, SM_REPOSITORY_BASE_PATH);

        cleanUpRegistries();
    }

    @After
    public void cleanUpComponents() {
        cleanUpRegistries();
    }

    @AfterClass
    public static void stopContext() {
        appContext.close();
    }

    private static void cleanUpRegistries() {
        try {
            aasRegistryApi.deleteAllShellDescriptors();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        try {
            smRegistryApi.deleteAllSubmodelDescriptors();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public AasRepository getAasRepository() {
        return connectedAasRepository;
    }

    @Override
    public ConnectedAasManager getConnectedAasManager() {
        return aasManager;
    }
}

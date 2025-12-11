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

package org.eclipse.digitaltwin.basyx.aasenvironment.component;

import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.core.DeserializationException;
import org.eclipse.digitaltwin.basyx.aasenvironment.AasEnvironment;
import org.eclipse.digitaltwin.basyx.aasenvironment.environmentloader.CompleteEnvironment;
import org.eclipse.digitaltwin.basyx.aasenvironment.environmentloader.CompleteEnvironment.EnvironmentType;
import org.eclipse.digitaltwin.basyx.aasregistry.client.ApiException;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.AssetAdministrationShellDescriptor;
import org.eclipse.digitaltwin.basyx.aasrepository.AasRepository;
import org.eclipse.digitaltwin.basyx.aasrepository.feature.registry.integration.AasRepositoryRegistryLink;
import org.eclipse.digitaltwin.basyx.core.exceptions.RepositoryRegistryLinkException;
import org.eclipse.digitaltwin.basyx.core.exceptions.ZipBombException;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.eclipse.digitaltwin.basyx.submodelrepository.SubmodelRepository;
import org.eclipse.digitaltwin.basyx.submodelrepository.feature.registry.integration.SubmodelRepositoryRegistryLink;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.ClassPathResource;

/**
 * 
 * Test the {@link AasEnvironment} with aas and submodel registry integration
 * features enabled
 * 
 * @author mateusmolina
 */
public class TestEnvironmentWithRegistryIntegration {

    static final String ENV_PATH = "testEnvironment.json";

    static ConfigurableApplicationContext appContext;

    static AasEnvironment aasEnvironment;
    static AasRepositoryRegistryLink aasRepositoryRegistryLink;
    static SubmodelRepositoryRegistryLink smRepositoryRegistryLink;
    static AasRepository aasRepository;
    static SubmodelRepository smRepository;

    @BeforeClass
    public static void startAASEnvironment() {
        appContext = new SpringApplicationBuilder(AasEnvironmentComponent.class).profiles("reginteg").run(new String[] {});

        aasEnvironment = appContext.getBean(AasEnvironment.class);
        aasRepositoryRegistryLink = appContext.getBean(AasRepositoryRegistryLink.class);
        smRepositoryRegistryLink = appContext.getBean(SubmodelRepositoryRegistryLink.class);
        aasRepository = appContext.getBean(AasRepository.class);
        smRepository = appContext.getBean(SubmodelRepository.class);

        assertRepositoriesAreEmpty();
    }

    @AfterClass
    public static void stopAASEnvironment() {
        appContext.close();
    }

    @AfterClass
    public static void clearRegistries() throws Exception {
        smRepositoryRegistryLink.getRegistryApi().deleteAllSubmodelDescriptors();
        aasRepositoryRegistryLink.getRegistryApi().deleteAllShellDescriptors();
    }

    @Test
    public void whenUploadDescriptorToRegistryFails_thenNoAasOrSmAreAddedToRepository() throws InvalidFormatException, DeserializationException, IOException, ApiException, ZipBombException {
        // simulate descriptor already being in registry
        aasRepositoryRegistryLink.getRegistryApi().postAssetAdministrationShellDescriptor(buildTestAasDescriptor());

        CompleteEnvironment completeEnvironment = CompleteEnvironment.fromInputStream(getIsFromClasspath(ENV_PATH), EnvironmentType.JSON);

        assertThrows(RepositoryRegistryLinkException.class, () -> aasEnvironment.loadEnvironment(completeEnvironment));

        assertRepositoriesAreEmpty();
    }

    private static AssetAdministrationShellDescriptor buildTestAasDescriptor() {
        AssetAdministrationShellDescriptor descriptor = new AssetAdministrationShellDescriptor();
        descriptor.setId("https://acplt.test/Test_AssetAdministrationShell");
        return descriptor;
    }

    private static InputStream getIsFromClasspath(String fileName) throws IOException {
        return new ClassPathResource(fileName).getInputStream();
    }

    private static void assertRepositoriesAreEmpty() {
        assertTrue(aasRepository.getAllAas(null, null, PaginationInfo.NO_LIMIT).getResult().isEmpty());
        assertTrue(smRepository.getAllSubmodels(PaginationInfo.NO_LIMIT).getResult().isEmpty());
    }

}

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

package org.eclipse.digitaltwin.basyx.aasrepository.feature.registry.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultAssetAdministrationShell;
import org.eclipse.digitaltwin.basyx.aasregistry.client.ApiException;
import org.eclipse.digitaltwin.basyx.aasregistry.client.api.RegistryAndDiscoveryInterfaceApi;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.AssetAdministrationShellDescriptor;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.eclipse.digitaltwin.basyx.aasregistry.main.client.factory.AasDescriptorFactory;
import org.eclipse.digitaltwin.basyx.aasregistry.main.client.mapper.AttributeMapper;
import org.eclipse.digitaltwin.basyx.aasregistry.main.client.mapper.DummyAasDescriptorFactory;
import org.eclipse.digitaltwin.basyx.aasrepository.AasRepository;
import org.eclipse.digitaltwin.basyx.aasrepository.backend.SimpleAasRepositoryFactory;
import org.eclipse.digitaltwin.basyx.aasrepository.backend.inmemory.AasInMemoryBackendProvider;
import org.eclipse.digitaltwin.basyx.aasservice.backend.InMemoryAasServiceFactory;
import org.eclipse.digitaltwin.basyx.core.filerepository.InMemoryFileRepository;

/**
 * Test suite for {@link RegistryIntegrationAasRepository} feature
 */
public class AasRepositoryRegistryLinkDescriptorGenerationTest {	
	private static final String DUMMY_IDSHORT = "ExampleMotor";
	private static final String DUMMY_AAS_ID = "customIdentifier";

	private static final String BASE_URL = "http://localhost:8081";
	
	// Mock dependencies
    private RegistryIntegrationAasRepository registryIntegrationAasRepository;
    private AasRepository mockedAasRepository;
    private AasRepositoryRegistryLink mockedRegistryLink;
    private AttributeMapper mockedAttributeMapper;
    private RegistryAndDiscoveryInterfaceApi mockedRegistryApi;
    
    @Before
    public void setUp() {
    	mockedAasRepository = getAasRepository();
        mockedRegistryLink = Mockito.mock(AasRepositoryRegistryLink.class);
        mockedAttributeMapper = Mockito.mock(AttributeMapper.class);
        mockedRegistryApi = Mockito.mock(RegistryAndDiscoveryInterfaceApi.class);

        Mockito.when(mockedRegistryLink.getRegistryApi()).thenReturn(mockedRegistryApi);
        
        registryIntegrationAasRepository = new RegistryIntegrationAasRepository(mockedAasRepository, mockedRegistryLink, mockedAttributeMapper);
    }

    @Test
    public void testExternalUrlWithTrailingSlashReflectedInDescriptor() throws FileNotFoundException, IOException, ApiException {
        String externalUrl = BASE_URL + "/";
        Mockito.when(mockedRegistryLink.getAasRepositoryBaseURLs()).thenReturn(List.of(externalUrl));

        AssetAdministrationShellDescriptor descriptor = createAndRetrieveDescriptor(createDummyAas());
        
        assertTrue("Endpoint address should start with externalUrl", descriptor.getEndpoints().get(0)
                .getProtocolInformation().getHref().startsWith(externalUrl));
    }

    @Test
    public void testExternalUrlWithoutTrailingSlashReflectedInDescriptor() throws FileNotFoundException, IOException, ApiException {
        String externalUrl = BASE_URL;
        Mockito.when(mockedRegistryLink.getAasRepositoryBaseURLs()).thenReturn(List.of(externalUrl));

        AssetAdministrationShellDescriptor descriptor = createAndRetrieveDescriptor(createDummyAas());
        
        String expectedUrl = externalUrl + "/";
        assertTrue("Endpoint address should start with externalUrl", descriptor.getEndpoints().get(0)
                .getProtocolInformation().getHref().startsWith(expectedUrl));
    }

    @Test
    public void testExternalUrlWithContextPathWithoutTrailingSlashReflectedInDescriptor() throws FileNotFoundException, IOException, ApiException {
        String externalUrl = BASE_URL + "/context";
        Mockito.when(mockedRegistryLink.getAasRepositoryBaseURLs()).thenReturn(List.of(externalUrl));

        AssetAdministrationShellDescriptor descriptor = createAndRetrieveDescriptor(createDummyAas());

        String expectedUrl = externalUrl;
        assertTrue("Endpoint address should start with externalUrl including context path", descriptor.getEndpoints().get(0)
                .getProtocolInformation().getHref().startsWith(expectedUrl));
    }
    
    @Test
    public void testExternalUrlWithContextPathWithTrailingSlashReflectedInDescriptor() throws FileNotFoundException, IOException, ApiException {
        String externalUrl = BASE_URL + "/context";
        Mockito.when(mockedRegistryLink.getAasRepositoryBaseURLs()).thenReturn(List.of(externalUrl));

        AssetAdministrationShellDescriptor descriptor = createAndRetrieveDescriptor(createDummyAas());

        String expectedUrl = externalUrl + "/";
        assertTrue("Endpoint address should start with externalUrl including context path", descriptor.getEndpoints().get(0)
                .getProtocolInformation().getHref().startsWith(expectedUrl));
    }
    
    private AssetAdministrationShellDescriptor createAndRetrieveDescriptor(AssetAdministrationShell shell) throws ApiException {
        // Simulate the process of creating an AAS in the repository
        registryIntegrationAasRepository.createAas(shell);

        // Simulate retrieving the descriptor from the registry after creation
        AasDescriptorFactory descriptorFactory = new AasDescriptorFactory(shell, mockedRegistryLink.getAasRepositoryBaseURLs(), mockedAttributeMapper);
        return descriptorFactory.create();
    }
    
    private AssetAdministrationShell createDummyAas() {
        // You can create a mock or a real AAS object
        return new DefaultAssetAdministrationShell.Builder()
                .id(DUMMY_AAS_ID)
                .idShort(DUMMY_IDSHORT)
                .build();
    }
    
	protected AasRepository getAasRepository() {
		return new SimpleAasRepositoryFactory(new AasInMemoryBackendProvider(), new InMemoryAasServiceFactory(new InMemoryFileRepository())).create();
	}

}

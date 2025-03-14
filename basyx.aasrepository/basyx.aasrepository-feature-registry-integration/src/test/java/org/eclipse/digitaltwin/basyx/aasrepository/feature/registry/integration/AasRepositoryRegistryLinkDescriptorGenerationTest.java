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

package org.eclipse.digitaltwin.basyx.aasrepository.feature.registry.integration;

import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultAssetAdministrationShell;
import org.eclipse.digitaltwin.basyx.aasregistry.client.api.RegistryAndDiscoveryInterfaceApi;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.AssetAdministrationShellDescriptor;
import org.eclipse.digitaltwin.basyx.aasregistry.main.client.factory.AasDescriptorFactory;
import org.eclipse.digitaltwin.basyx.aasregistry.main.client.mapper.AttributeMapper;
import org.eclipse.digitaltwin.basyx.aasrepository.AasRepository;
import org.eclipse.digitaltwin.basyx.aasrepository.backend.CrudAasRepositoryFactory;
import org.eclipse.digitaltwin.basyx.aasservice.backend.InMemoryAasBackend;
import org.eclipse.digitaltwin.basyx.core.filerepository.InMemoryFileRepository;
import org.eclipse.digitaltwin.basyx.http.Base64UrlEncodedIdentifier;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Test suite for {@link RegistryIntegrationAasRepository} feature
 */
@RunWith(Parameterized.class)
public class AasRepositoryRegistryLinkDescriptorGenerationTest {	
	private static final String DUMMY_IDSHORT = "ExampleMotor";
	private static final String DUMMY_AAS_ID = "customIdentifier";

	private static final String BASE_URL = "http://localhost:8081";
	
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

    @Parameterized.Parameter(0)
    public String externalUrl;

    @Parameterized.Parameter(1)
    public String expectedUrl;

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] { { BASE_URL + "/context", BASE_URL + "/context/shells/" + Base64UrlEncodedIdentifier.encodeIdentifier(DUMMY_AAS_ID) },
                { BASE_URL, BASE_URL + "/shells/" + Base64UrlEncodedIdentifier.encodeIdentifier(DUMMY_AAS_ID) }, { BASE_URL + "/", BASE_URL + "/shells/" + Base64UrlEncodedIdentifier.encodeIdentifier(DUMMY_AAS_ID) },
                { BASE_URL + "/context/", BASE_URL + "/context/shells/" + Base64UrlEncodedIdentifier.encodeIdentifier(DUMMY_AAS_ID) } });
    }

    @Test
    public void testExternalUrl() {
        Mockito.when(mockedRegistryLink.getAasRepositoryBaseURLs()).thenReturn(List.of(externalUrl));

        AssetAdministrationShellDescriptor descriptor = createAndRetrieveDescriptor(createDummyAas());
        String actualUrl = descriptor.getEndpoints().get(0).getProtocolInformation().getHref();

        assertEquals(expectedUrl, actualUrl);
    }

    private AssetAdministrationShellDescriptor createAndRetrieveDescriptor(AssetAdministrationShell shell) {
        registryIntegrationAasRepository.createAas(shell);

        AasDescriptorFactory descriptorFactory = new AasDescriptorFactory(mockedRegistryLink.getAasRepositoryBaseURLs(), mockedAttributeMapper);
        return descriptorFactory.create(shell);
    }
    
    private AssetAdministrationShell createDummyAas() {
        return new DefaultAssetAdministrationShell.Builder()
                .id(DUMMY_AAS_ID)
                .idShort(DUMMY_IDSHORT)
                .build();
    }
    
	protected AasRepository getAasRepository() {
        return CrudAasRepositoryFactory.builder().backend(new InMemoryAasBackend()).fileRepository( new InMemoryFileRepository()).create();
	}

}

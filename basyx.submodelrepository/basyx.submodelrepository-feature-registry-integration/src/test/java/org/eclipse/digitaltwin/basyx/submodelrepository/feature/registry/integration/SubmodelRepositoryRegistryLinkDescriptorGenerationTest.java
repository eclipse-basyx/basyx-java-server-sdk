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

package org.eclipse.digitaltwin.basyx.submodelrepository.feature.registry.integration;

import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSubmodel;
import org.eclipse.digitaltwin.basyx.client.internal.ApiException;
import org.eclipse.digitaltwin.basyx.core.filerepository.InMemoryFileRepository;
import org.eclipse.digitaltwin.basyx.http.Base64UrlEncodedIdentifier;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.api.SubmodelRegistryApi;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.factory.SubmodelDescriptorFactory;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.mapper.AttributeMapper;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.model.SubmodelDescriptor;
import org.eclipse.digitaltwin.basyx.submodelrepository.SubmodelRepository;
import org.eclipse.digitaltwin.basyx.submodelrepository.backend.CrudSubmodelRepositoryFactory;
import org.eclipse.digitaltwin.basyx.submodelservice.InMemorySubmodelBackend;
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
 * Test suite for RegistryIntegrationAasRepository feature
 */
@RunWith(Parameterized.class)
public class SubmodelRepositoryRegistryLinkDescriptorGenerationTest {	
	private static final String DUMMY_SUBMODEL_IDSHORT = "TechnicalData";
	private static final String DUMMY_SUBMODEL_ID = "7A7104BDAB57E184";

	private static final String BASE_URL = "http://localhost:8081";
	
    private RegistryIntegrationSubmodelRepository registryIntegrationSubmodelRepository;
    private SubmodelRepository mockedSubmodelRepository;
    private SubmodelRepositoryRegistryLink mockedRegistryLink;
    private AttributeMapper mockedAttributeMapper;
    private SubmodelRegistryApi mockedRegistryApi;
    
    @Before
    public void setUp() {    	
    	mockedSubmodelRepository = getSubmodelRepository();
    	mockedRegistryLink = Mockito.mock(SubmodelRepositoryRegistryLink.class);
        mockedAttributeMapper = Mockito.mock(AttributeMapper.class);
        mockedRegistryApi = Mockito.mock(SubmodelRegistryApi.class);

        Mockito.when(mockedRegistryLink.getRegistryApi()).thenReturn(mockedRegistryApi);
        
        registryIntegrationSubmodelRepository = new RegistryIntegrationSubmodelRepository(mockedSubmodelRepository, mockedRegistryLink, mockedAttributeMapper);
    }

    @Parameterized.Parameter(0)
    public String externalUrl;

    @Parameterized.Parameter(1)
    public String expectedUrl;

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] { { BASE_URL + "/context", BASE_URL + "/context/submodels/" + Base64UrlEncodedIdentifier.encodeIdentifier(DUMMY_SUBMODEL_ID) },
                { BASE_URL, BASE_URL + "/submodels/" + Base64UrlEncodedIdentifier.encodeIdentifier(DUMMY_SUBMODEL_ID) }, { BASE_URL + "/", BASE_URL + "/submodels/" + Base64UrlEncodedIdentifier.encodeIdentifier(DUMMY_SUBMODEL_ID) },
                { BASE_URL + "/context/", BASE_URL + "/context/submodels/" + Base64UrlEncodedIdentifier.encodeIdentifier(DUMMY_SUBMODEL_ID) } });
    }

    @Test
    public void testExternalUrl() throws ApiException {
        Mockito.when(mockedRegistryLink.getSubmodelRepositoryBaseURLs()).thenReturn(List.of(externalUrl));

        SubmodelDescriptor descriptor = createAndRetrieveDescriptor(createDummySubmodel());
        String actualUrl = descriptor.getEndpoints().get(0).getProtocolInformation().getHref();

        assertEquals(expectedUrl, actualUrl);
    }

    private SubmodelDescriptor createAndRetrieveDescriptor(Submodel submodel) throws ApiException {
        registryIntegrationSubmodelRepository.createSubmodel(submodel);

        SubmodelDescriptorFactory descriptorFactory = new SubmodelDescriptorFactory(mockedRegistryLink.getSubmodelRepositoryBaseURLs(), mockedAttributeMapper);
        return descriptorFactory.create(submodel);
    }
    
    private Submodel createDummySubmodel() {
        return new DefaultSubmodel.Builder().id(DUMMY_SUBMODEL_ID).idShort(DUMMY_SUBMODEL_IDSHORT).build();
    }
    
	protected SubmodelRepository getSubmodelRepository() {
        return CrudSubmodelRepositoryFactory.builder().backend(new InMemorySubmodelBackend()).fileRepository(new InMemoryFileRepository()).create();
	}

}

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

package org.eclipse.digitaltwin.basyx.gateway.core.registryintegration;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.basyx.aasregistry.client.ApiException;
import org.eclipse.digitaltwin.basyx.aasregistry.client.api.RegistryAndDiscoveryInterfaceApi;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.AssetAdministrationShellDescriptor;
import org.eclipse.digitaltwin.basyx.aasregistry.main.client.factory.AasDescriptorFactory;
import org.eclipse.digitaltwin.basyx.aasregistry.main.client.mapper.AttributeMapper;
import org.eclipse.digitaltwin.basyx.core.exceptions.RepositoryRegistryLinkException;
import org.eclipse.digitaltwin.basyx.http.Aas4JHTTPSerializationExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.util.List;

/**
 * Helper class to integrate AAS into a Registry
 *
 * @author fried
 */
public class AASRegistryIntegrator {
    private final String aasRegistryUrl;
    private Logger logger = LoggerFactory.getLogger(AASRegistryIntegrator.class);


    public AASRegistryIntegrator(String aasRegistryURL){
        this.aasRegistryUrl = aasRegistryURL;
    }

    public void registerAAS(AssetAdministrationShell aas, String aasURL){
        integrateAasWithRegistry(aas, aasRegistryUrl, aasURL);
    }


    private static AttributeMapper getAttributeMapper() {
        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder().serializationInclusion(JsonInclude.Include.NON_NULL);
        Aas4JHTTPSerializationExtension extension = new Aas4JHTTPSerializationExtension();
        extension.extend(builder);
        ObjectMapper objectMapper = builder.build();
        AttributeMapper attributeMapper = new AttributeMapper(objectMapper);
        return attributeMapper;
    }

    private void integrateAasWithRegistry(AssetAdministrationShell shell,String aasRegistryUrl, String aasRepositoryUrl) throws RepositoryRegistryLinkException {
        List<String> aasRepositoryURLs = List.of(aasRepositoryUrl);
        AttributeMapper attributeMapper = getAttributeMapper();

        AssetAdministrationShellDescriptor descriptor = new AasDescriptorFactory(shell, aasRepositoryURLs, attributeMapper).create();

        RegistryAndDiscoveryInterfaceApi registryApi = new RegistryAndDiscoveryInterfaceApi(aasRegistryUrl);

        try {
            registryApi.postAssetAdministrationShellDescriptor(descriptor);

        } catch (ApiException e) {
            throw new RepositoryRegistryLinkException(shell.getId(), e);
        }
        logger.info("Shell '{}' has been automatically linked with the Registry", shell.getId());
    }
}

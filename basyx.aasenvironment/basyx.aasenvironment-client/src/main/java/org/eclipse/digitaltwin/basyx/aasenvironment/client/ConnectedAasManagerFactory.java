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

package org.eclipse.digitaltwin.basyx.aasenvironment.client;

import org.eclipse.digitaltwin.basyx.aasregistry.client.api.RegistryAndDiscoveryInterfaceApi;
import org.eclipse.digitaltwin.basyx.aasrepository.client.ConnectedAasRepository;
import org.eclipse.digitaltwin.basyx.aasrepository.feature.registry.integration.AasDescriptorFactory;
import org.eclipse.digitaltwin.basyx.aasrepository.feature.registry.integration.mapper.AttributeMapper;
import org.eclipse.digitaltwin.basyx.http.Aas4JHTTPSerializationExtension;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.api.SubmodelRegistryApi;
import org.eclipse.digitaltwin.basyx.submodelrepository.client.ConnectedSubmodelRepository;
import org.eclipse.digitaltwin.basyx.submodelrepository.feature.registry.integration.SubmodelDescriptorFactory;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Factory that builds a {@link ConnectedAasManager} via Repositories/Registry
 * Urls
 *
 * @author mateusmolina
 *
 */
public class ConnectedAasManagerFactory {

	private RegistryAndDiscoveryInterfaceApi registryAndDiscoveryInterfaceApi;
	private ConnectedAasRepository connectedAasRepository;

	private SubmodelRegistryApi submodelRegistryApi;
	private ConnectedSubmodelRepository connectedSubmodelRepository;

	private AasDescriptorResolver aasDescriptorResolver;
	private AasDescriptorFactory aasDescriptorFactory;

	private SubmodelDescriptorResolver smDescriptorResolver;
	private SubmodelDescriptorFactory smDescriptorFactory;
	
	private ReferenceResolver referenceResolver;

	public ConnectedAasManagerFactory(String aasRegistryBaseUrl, String aasRepositoryBaseUrl, String submodelRegistryBaseUrl, String submodelBaseRepositoryUrl) {
		registryAndDiscoveryInterfaceApi = new RegistryAndDiscoveryInterfaceApi(aasRegistryBaseUrl);
		connectedAasRepository = new ConnectedAasRepository(aasRegistryBaseUrl);

		submodelRegistryApi = new SubmodelRegistryApi(submodelRegistryBaseUrl);
		connectedSubmodelRepository = new ConnectedSubmodelRepository(submodelBaseRepositoryUrl);
			
		EndpointResolver endpointResolver = new EndpointResolver();

		aasDescriptorResolver = new AasDescriptorResolver(endpointResolver);
		smDescriptorResolver = new SubmodelDescriptorResolver(endpointResolver);
		
		ObjectMapper objectMapper = buildObjectMapper();

		aasDescriptorFactory = buildAasDescriptorFactory(aasRepositoryBaseUrl, objectMapper);
		smDescriptorFactory = buildSmDescriptorFactory(aasRepositoryBaseUrl, objectMapper);

		referenceResolver = new ReferenceResolver(endpointResolver);
	}

	public ConnectedAasManagerFactory() {
	}

	public ConnectedAasManager build() {
		return new ConnectedAasManager(connectedAasRepository, registryAndDiscoveryInterfaceApi, connectedSubmodelRepository, submodelRegistryApi, aasDescriptorResolver, aasDescriptorFactory, smDescriptorResolver, smDescriptorFactory,
				referenceResolver);
	}

	public void setConnectedAasRepository(ConnectedAasRepository connectedAasRepository) {
		this.connectedAasRepository = connectedAasRepository;
	}

	public void setRegistryAndDiscoveryInterfaceApi(RegistryAndDiscoveryInterfaceApi registryAndDiscoveryInterfaceApi) {
		this.registryAndDiscoveryInterfaceApi = registryAndDiscoveryInterfaceApi;
	}

	public void setConnectedSubmodelRepository(ConnectedSubmodelRepository connectedSubmodelRepository) {
		this.connectedSubmodelRepository = connectedSubmodelRepository;
	}

	public void setSubmodelRegistryApi(SubmodelRegistryApi submodelRegistryApi) {
		this.submodelRegistryApi = submodelRegistryApi;
	}

	public void setAasDescriptorResolver(AasDescriptorResolver aasDescriptorResolver) {
		this.aasDescriptorResolver = aasDescriptorResolver;
	}

	public void setAasDescriptorFactory(AasDescriptorFactory aasDescriptorFactory) {
		this.aasDescriptorFactory = aasDescriptorFactory;
	}

	public void setSmDescriptorResolver(SubmodelDescriptorResolver smDescriptorResolver) {
		this.smDescriptorResolver = smDescriptorResolver;
	}

	public void setSmDescriptorFactory(SubmodelDescriptorFactory smDescriptorFactory) {
		this.smDescriptorFactory = smDescriptorFactory;
	}

	public void setReferenceResolver(ReferenceResolver referenceResolver) {
		this.referenceResolver = referenceResolver;
	}

	private static ObjectMapper buildObjectMapper() {
		Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder().serializationInclusion(JsonInclude.Include.NON_NULL);
		Aas4JHTTPSerializationExtension extension = new Aas4JHTTPSerializationExtension();
		extension.extend(builder);
		return builder.build();
	}

	private AasDescriptorFactory buildAasDescriptorFactory(String aasRepositoryBaseUrl, ObjectMapper objectMapper) {
		AttributeMapper attributeMapper = new AttributeMapper(objectMapper);

		return new AasDescriptorFactory(null, aasRepositoryBaseUrl, attributeMapper);
	}

	private SubmodelDescriptorFactory buildSmDescriptorFactory(String aasRepositoryBaseUrl, ObjectMapper objectMapper) {
		org.eclipse.digitaltwin.basyx.submodelrepository.feature.registry.integration.mapper.AttributeMapper attributeMapperSm = new org.eclipse.digitaltwin.basyx.submodelrepository.feature.registry.integration.mapper.AttributeMapper(
				objectMapper);
		return new SubmodelDescriptorFactory(null, aasRepositoryBaseUrl, attributeMapperSm);
	}

}

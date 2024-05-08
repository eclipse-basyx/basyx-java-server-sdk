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

import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.basyx.aasenvironment.client.exceptions.NoValidEndpointFoundException;
import org.eclipse.digitaltwin.basyx.aasenvironment.client.exceptions.RegistryHttpRequestException;
import org.eclipse.digitaltwin.basyx.aasenvironment.client.resolvers.AasDescriptorResolver;
import org.eclipse.digitaltwin.basyx.aasenvironment.client.resolvers.ReferenceResolver;
import org.eclipse.digitaltwin.basyx.aasenvironment.client.resolvers.SubmodelDescriptorResolver;
import org.eclipse.digitaltwin.basyx.aasregistry.client.api.RegistryAndDiscoveryInterfaceApi;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.AssetAdministrationShellDescriptor;
import org.eclipse.digitaltwin.basyx.aasrepository.client.ConnectedAasRepository;
import org.eclipse.digitaltwin.basyx.aasrepository.feature.registry.integration.AasDescriptorFactory;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.api.SubmodelRegistryApi;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.model.SubmodelDescriptor;
import org.eclipse.digitaltwin.basyx.submodelrepository.client.ConnectedSubmodelRepository;
import org.eclipse.digitaltwin.basyx.submodelrepository.feature.registry.integration.SubmodelDescriptorFactory;

/**
 * Connected variant of the {@link AasManager}
 *
 * @author mateusmolina
 *
 */
public class ConnectedAasManager implements AasManager {

	private final ConnectedAasRepository aasRepository;
	private final ConnectedSubmodelRepository smRepository;

	private final RegistryAndDiscoveryInterfaceApi aasRegistryApi;
	private final SubmodelRegistryApi smRegistryApi;

	private final AasDescriptorResolver aasDescriptorResolver;
	private final AasDescriptorFactory aasDescriptorFactory;

	private final SubmodelDescriptorResolver smDescriptorResolver;
	private final SubmodelDescriptorFactory smDescriptorFactory;

	private final ReferenceResolver referenceResolver;

	public static ConnectedAasManager fromUrls(String aasRegistryBaseUrl, String aasRepositoryBaseUrl, String submodelRegistryBaseUrl, String submodelBaseRepositoryUrl) {
		return new ConnectedAasManagerFactory(aasRegistryBaseUrl, aasRepositoryBaseUrl, submodelRegistryBaseUrl, submodelBaseRepositoryUrl).build();
	}

	public ConnectedAasManager(ConnectedAasRepository aasRepository, RegistryAndDiscoveryInterfaceApi aasRegistryApi, ConnectedSubmodelRepository smRepository, SubmodelRegistryApi smRegistryApi,
			AasDescriptorResolver aasDescriptorResolver, AasDescriptorFactory aasDescriptorFactory, SubmodelDescriptorResolver smDescriptorResolver, SubmodelDescriptorFactory smDescriptorFactory, ReferenceResolver referenceResolver) {
		this.aasRepository = aasRepository;
		this.aasRegistryApi = aasRegistryApi;
		this.smRepository = smRepository;
		this.smRegistryApi = smRegistryApi;
		this.aasDescriptorResolver = aasDescriptorResolver;
		this.aasDescriptorFactory = aasDescriptorFactory;
		this.smDescriptorResolver = smDescriptorResolver;
		this.smDescriptorFactory = smDescriptorFactory;
		this.referenceResolver = referenceResolver;
	}

	@Override
	public AssetAdministrationShell getAas(String identifier) throws NoValidEndpointFoundException {
		AssetAdministrationShellDescriptor descriptor;

		try {
			descriptor = aasRegistryApi.getAssetAdministrationShellDescriptorById(identifier);
		} catch (Exception e) {
			throw new RegistryHttpRequestException(identifier, e);
		}
		return aasDescriptorResolver.resolveAasDescriptor(descriptor);
	}

	@Override
	public Submodel getSubmodel(String identifier) {
		SubmodelDescriptor descriptor;

		try {
			descriptor = smRegistryApi.getSubmodelDescriptorById(identifier);
		} catch (Exception e) {
			throw new RegistryHttpRequestException(identifier, e);
		}

		return smDescriptorResolver.resolveSubmodelDescriptor(descriptor);
	}

	@Override
	public Submodel getSubmodelOfAas(String aasIdentifier, String smIdentifier) {
		AssetAdministrationShellDescriptor aasDescriptor;

		try {
			aasDescriptor = aasRegistryApi.getAssetAdministrationShellDescriptorById(aasIdentifier);
		} catch (Exception e) {
			throw new RegistryHttpRequestException(aasIdentifier, e);
		}

		AssetAdministrationShell aas = aasDescriptorResolver.resolveAasDescriptor(aasDescriptor);
		List<Reference> references = aas.getSubmodels();

		return referenceResolver.resolveSubmodelFromReferences(smIdentifier, references);
	}

	@Override
	public void deleteAas(String identifier) {
		try {
			aasRegistryApi.deleteAssetAdministrationShellDescriptorById(identifier);
		} catch (Exception e) {
			throw new RegistryHttpRequestException(identifier, e);
		}

		aasRepository.deleteAas(identifier);
	}

	@Override
	public void deleteSubmodelOfAas(String aasIdentifier, String smIdentifier) {
		try {
			smRegistryApi.deleteSubmodelDescriptorById(smIdentifier);
		} catch (Exception e) {
			throw new RegistryHttpRequestException(aasIdentifier, e);
		}

		aasRepository.removeSubmodelReference(aasIdentifier, smIdentifier);
		smRepository.deleteSubmodel(smIdentifier);
	}

	@Override
	public void createAas(AssetAdministrationShell aas) {
		aasRepository.createAas(aas);
		AssetAdministrationShellDescriptor descriptor = aasDescriptorFactory.create(aas);

		try {
			aasRegistryApi.postAssetAdministrationShellDescriptor(descriptor);
		} catch (Exception e) {
			throw new RegistryHttpRequestException(aas.getId(), e);
		}
	}

	@Override
	public void createSubmodelInAas(String aasIdentifier, Submodel submodel) {
		smRepository.createSubmodel(submodel);
		SubmodelDescriptor descriptor = smDescriptorFactory.create(submodel);

		try {
			smRegistryApi.postSubmodelDescriptor(descriptor);
		} catch (Exception e) {
			throw new RegistryHttpRequestException(aasIdentifier, e);
		}

		Reference ref = smDescriptorResolver.deriveReferenceFromSubmodelDescriptor(descriptor);
		aasRepository.addSubmodelReference(aasIdentifier, ref);
	}

}


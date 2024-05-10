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

import org.eclipse.digitaltwin.aas4j.v3.dataformat.core.util.AasUtils;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.basyx.aasenvironment.client.exceptions.NoValidEndpointFoundException;
import org.eclipse.digitaltwin.basyx.aasenvironment.client.exceptions.RegistryHttpRequestException;
import org.eclipse.digitaltwin.basyx.aasenvironment.client.resolvers.AasDescriptorResolver;
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
 * Client component for executing consolidated Repository and Registry requests
 *
 * @author mateusmolina
 *
 */
public class ConnectedAasManager {

	private final ConnectedAasRepository aasRepository;
	private final ConnectedSubmodelRepository smRepository;

	private final RegistryAndDiscoveryInterfaceApi aasRegistryApi;
	private final SubmodelRegistryApi smRegistryApi;

	private final AasDescriptorResolver aasDescriptorResolver;
	private final AasDescriptorFactory aasDescriptorFactory;

	private final SubmodelDescriptorResolver smDescriptorResolver;
	private final SubmodelDescriptorFactory smDescriptorFactory;

	/**
	 * Default constructor for a {@link ConnectedAasManager} instance
	 * 
	 * @param aasRegistryBaseUrl
	 * @param aasRepositoryBaseUrl
	 * @param submodelRegistryBaseUrl
	 * @param submodelBaseRepositoryUrl
	 */
	public ConnectedAasManager(String aasRegistryBaseUrl, String aasRepositoryBaseUrl, String submodelRegistryBaseUrl, String submodelBaseRepositoryUrl) {
		this(new RegistryAndDiscoveryInterfaceApi(aasRegistryBaseUrl), new ConnectedAasRepository(aasRepositoryBaseUrl), aasRepositoryBaseUrl, new SubmodelRegistryApi(submodelRegistryBaseUrl),
				new ConnectedSubmodelRepository(submodelBaseRepositoryUrl), submodelBaseRepositoryUrl);
	}

	ConnectedAasManager(RegistryAndDiscoveryInterfaceApi aasRegistryApi, ConnectedAasRepository aasRepository, String aasRepositoryBaseUrl, SubmodelRegistryApi smRegistryApi, ConnectedSubmodelRepository smRepository,
			String submodelBaseRepositoryUrl) {
		this.aasRepository = aasRepository;
		this.aasRegistryApi = aasRegistryApi;
		this.smRepository = smRepository;
		this.smRegistryApi = smRegistryApi;
		this.aasDescriptorResolver = ConnectedAasManagerHelper.buildAasDescriptorResolver();
		this.aasDescriptorFactory = ConnectedAasManagerHelper.buildAasDescriptorFactory(aasRepositoryBaseUrl);
		this.smDescriptorResolver = ConnectedAasManagerHelper.buildSubmodelDescriptorResolver();
		this.smDescriptorFactory = ConnectedAasManagerHelper.buildSmDescriptorFactory(submodelBaseRepositoryUrl);
	}

	/**
	 * Retrieves an AAS in an AAS registry by its identifier.
	 *
	 * @param identifier
	 *            The identifier of the AAS to retrieve.
	 * @return The retrieved AAS object.
	 */
	public AssetAdministrationShell getAas(String identifier) throws NoValidEndpointFoundException {
		AssetAdministrationShellDescriptor descriptor;

		try {
			descriptor = aasRegistryApi.getAssetAdministrationShellDescriptorById(identifier);
		} catch (Exception e) {
			throw new RegistryHttpRequestException(identifier, e);
		}
		return aasDescriptorResolver.resolveAasDescriptor(descriptor);
	}

	/**
	 * Retrieves a Submodel in a Submodel registry by its identifier.
	 *
	 * @param identifier
	 *            The identifier of the submodel to retrieve.
	 * @return The retrieved Submodel object.
	 */
	public Submodel getSubmodel(String identifier) {
		SubmodelDescriptor descriptor;

		try {
			descriptor = smRegistryApi.getSubmodelDescriptorById(identifier);
		} catch (Exception e) {
			throw new RegistryHttpRequestException(identifier, e);
		}

		return smDescriptorResolver.resolveSubmodelDescriptor(descriptor);
	}

	/**
	 * Deletes an AAS by its identifier.
	 *
	 * @param identifier
	 *            The identifier of the AAS to delete.
	 */
	public void deleteAas(String identifier) {
		try {
			aasRegistryApi.deleteAssetAdministrationShellDescriptorById(identifier);
		} catch (Exception e) {
			throw new RegistryHttpRequestException(identifier, e);
		}

		aasRepository.deleteAas(identifier);
	}

	/**
	 * Deletes a submodel associated with a specified AAS.
	 *
	 * @param aasIdentifier
	 *            The identifier of the AAS.
	 * @param smIdentifier
	 *            The identifier of the submodel to delete.
	 */
	public void deleteSubmodelOfAas(String aasIdentifier, String smIdentifier) {
		try {
			smRegistryApi.deleteSubmodelDescriptorById(smIdentifier);
		} catch (Exception e) {
			throw new RegistryHttpRequestException(aasIdentifier, e);
		}

		aasRepository.removeSubmodelReference(aasIdentifier, smIdentifier);
		smRepository.deleteSubmodel(smIdentifier);
	}

	/**
	 * Creates a new AAS
	 *
	 * @param aas
	 *            The AAS object to create.
	 */
	public void createAas(AssetAdministrationShell aas) {
		aasRepository.createAas(aas);
		AssetAdministrationShellDescriptor descriptor = aasDescriptorFactory.create(aas);

		try {
			aasRegistryApi.postAssetAdministrationShellDescriptor(descriptor);
		} catch (Exception e) {
			throw new RegistryHttpRequestException(aas.getId(), e);
		}
	}

	/**
	 * Creates a submodel under a specified AAS.
	 *
	 * @param aasIdentifier
	 *            The identifier of the AAS.
	 * @param submodel
	 *            The Submodel object to create under the specified AAS.
	 */
	public void createSubmodelInAas(String aasIdentifier, Submodel submodel) {
		smRepository.createSubmodel(submodel);
		SubmodelDescriptor descriptor = smDescriptorFactory.create(submodel);

		try {
			smRegistryApi.postSubmodelDescriptor(descriptor);
		} catch (Exception e) {
			throw new RegistryHttpRequestException(aasIdentifier, e);
		}

		aasRepository.addSubmodelReference(aasIdentifier, AasUtils.toReference(submodel));
	}

}


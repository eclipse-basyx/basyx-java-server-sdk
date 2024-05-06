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

import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.basyx.aasregistry.client.api.RegistryAndDiscoveryInterfaceApi;
import org.eclipse.digitaltwin.basyx.aasrepository.client.ConnectedAasRepository;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.api.SubmodelRegistryApi;
import org.eclipse.digitaltwin.basyx.submodelrepository.client.ConnectedSubmodelRepository;

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

	public ConnectedAasManager(String aasRepositoryUrl, String aasRegistryUrl, String smRepositoryUrl, String smRegistryUrl) {
		this(new ConnectedAasRepository(aasRepositoryUrl), new RegistryAndDiscoveryInterfaceApi(aasRegistryUrl), new ConnectedSubmodelRepository(smRepositoryUrl), new SubmodelRegistryApi(smRegistryUrl));
	}

	public ConnectedAasManager(ConnectedAasRepository aasRepository, RegistryAndDiscoveryInterfaceApi aasRegistryApi, ConnectedSubmodelRepository smRepository, SubmodelRegistryApi smRegistryApi) {
		this.aasRepository = aasRepository;
		this.aasRegistryApi = aasRegistryApi;
		this.smRepository = smRepository;
		this.smRegistryApi = smRegistryApi;
	}

	@Override
	public AssetAdministrationShell getAas(String identifier) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Submodel getSubmodel(String identifier) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Submodel getSubmodelOfAas(String aasIdentifier, String smIdentifier) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteAas(String identifier) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteSubmodelOfAas(String aasIdentifier, String smIdentifier) {
		// TODO Auto-generated method stub

	}

	@Override
	public void createAas(AssetAdministrationShell aas) {
		// TODO Auto-generated method stub

	}

	@Override
	public void createSubmodelOfAas(String aasIdentifier, Submodel submodel) {
		// TODO Auto-generated method stub

	}
}


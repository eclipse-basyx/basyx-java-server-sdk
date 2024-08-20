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

import java.util.List;

import org.eclipse.digitaltwin.basyx.aasregistry.client.api.RegistryAndDiscoveryInterfaceApi;
import org.eclipse.digitaltwin.basyx.aasrepository.AasRepository;

/**
 * Represents information for linking {@link AasRepository} with AasRegistry
 * 
 * @author danish
 */
public class AasRepositoryRegistryLink {
	
	private RegistryAndDiscoveryInterfaceApi registryApi;
	private List<String> aasRepositoryBaseURLs;
	
	public AasRepositoryRegistryLink(RegistryAndDiscoveryInterfaceApi registryApi, List<String> aasRepositoryBaseURLs) {
		super();
		this.registryApi = registryApi;
		this.aasRepositoryBaseURLs = aasRepositoryBaseURLs;
	}

	public RegistryAndDiscoveryInterfaceApi getRegistryApi() {
		return registryApi;
	}

	public List<String> getAasRepositoryBaseURLs() {
		return aasRepositoryBaseURLs;
	}

}

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

import org.eclipse.digitaltwin.basyx.aasenvironment.client.resolvers.EndpointResolver;
import org.eclipse.digitaltwin.basyx.aasenvironment.client.resolvers.DescriptorResolverManager;
import org.eclipse.digitaltwin.basyx.aasenvironment.client.resolvers.authorization.AuthorizedAasDescriptorResolver;
import org.eclipse.digitaltwin.basyx.aasenvironment.client.resolvers.authorization.AuthorizedSubmodelDescriptorResolver;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.AssetAdministrationShellDescriptor;
import org.eclipse.digitaltwin.basyx.aasregistry.main.client.AuthorizedConnectedAasRegistry;
import org.eclipse.digitaltwin.basyx.aasrepository.client.AuthorizedConnectedAasRepository;
import org.eclipse.digitaltwin.basyx.aasservice.client.ConnectedAasService;
import org.eclipse.digitaltwin.basyx.client.internal.authorization.TokenManager;
import org.eclipse.digitaltwin.basyx.client.internal.resolver.DescriptorResolver;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.AuthorizedConnectedSubmodelRegistry;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.model.SubmodelDescriptor;
import org.eclipse.digitaltwin.basyx.submodelrepository.client.AuthorizedConnectedSubmodelRepository;
import org.eclipse.digitaltwin.basyx.submodelservice.client.ConnectedSubmodelService;

/**
 * Authorized client component for executing consolidated Repository and Registry requests
 *
 * @author danish
 *
 */
public class AuthorizedConnectedAasManager extends ConnectedAasManager {

	public AuthorizedConnectedAasManager(AuthorizedConnectedAasRegistry authorizedAasRegistryApi, AuthorizedConnectedAasRepository authorizedAasRepository, AuthorizedConnectedSubmodelRegistry authorizedSubmodelRegistryApi, AuthorizedConnectedSubmodelRepository authorizedSubmodelRepository) {
		super(authorizedAasRegistryApi, authorizedAasRepository, authorizedSubmodelRegistryApi, authorizedSubmodelRepository, getAuthorizedResolver(authorizedAasRepository.getTokenManager(), authorizedSubmodelRepository.getTokenManager()));
	}
	
	private static DescriptorResolverManager getAuthorizedResolver(TokenManager authorizedAasRepoTokenManager, TokenManager authorizedSubmodelRepoTokenManager) {
		DescriptorResolver<AssetAdministrationShellDescriptor, ConnectedAasService> aasDescriptorResolver = new AuthorizedAasDescriptorResolver(new EndpointResolver(), authorizedAasRepoTokenManager);
		DescriptorResolver<SubmodelDescriptor, ConnectedSubmodelService> smDescriptorResolver = new AuthorizedSubmodelDescriptorResolver(new EndpointResolver(), authorizedSubmodelRepoTokenManager);
		
		return new DescriptorResolverManager(aasDescriptorResolver, smDescriptorResolver);
	}
	
}
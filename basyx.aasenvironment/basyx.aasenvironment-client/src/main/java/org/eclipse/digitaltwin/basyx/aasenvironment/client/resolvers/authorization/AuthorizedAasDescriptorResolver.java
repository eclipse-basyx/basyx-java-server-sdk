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

package org.eclipse.digitaltwin.basyx.aasenvironment.client.resolvers.authorization;

import org.eclipse.digitaltwin.basyx.aasenvironment.client.resolvers.AasDescriptorResolver;
import org.eclipse.digitaltwin.basyx.aasenvironment.client.resolvers.EndpointResolver;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.AssetAdministrationShellDescriptor;
import org.eclipse.digitaltwin.basyx.aasservice.client.AuthorizedConnectedAasService;
import org.eclipse.digitaltwin.basyx.aasservice.client.ConnectedAasService;
import org.eclipse.digitaltwin.basyx.client.internal.authorization.TokenManager;
import org.eclipse.digitaltwin.basyx.client.internal.resolver.DescriptorResolver;

/**
 * Resolves an AasDescriptor into a {@link AuthorizedConnectedAasService}
 *
 * @author danish
 *
 */
public class AuthorizedAasDescriptorResolver implements DescriptorResolver<AssetAdministrationShellDescriptor, ConnectedAasService> {

	private final EndpointResolver endpointResolver;
	private final TokenManager tokenManager;

	public AuthorizedAasDescriptorResolver(EndpointResolver endpointResolver, TokenManager tokenManager) {
		this.endpointResolver = endpointResolver;
		this.tokenManager = tokenManager;
	}
	
	@Override
	public AuthorizedConnectedAasService resolveDescriptor(AssetAdministrationShellDescriptor descriptor) {
		String endpoint = endpointResolver.resolveFirst(descriptor.getEndpoints(), AasDescriptorResolver::parseEndpoint);

		return new AuthorizedConnectedAasService(endpoint, tokenManager);
	}

}

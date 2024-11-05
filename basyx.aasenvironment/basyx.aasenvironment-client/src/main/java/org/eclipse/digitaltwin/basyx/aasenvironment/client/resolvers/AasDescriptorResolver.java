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

package org.eclipse.digitaltwin.basyx.aasenvironment.client.resolvers;

import java.net.URI;
import java.util.Optional;

import org.eclipse.digitaltwin.basyx.aasregistry.client.model.AssetAdministrationShellDescriptor;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.Endpoint;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.ProtocolInformation;
import org.eclipse.digitaltwin.basyx.aasservice.client.ConnectedAasService;
import org.eclipse.digitaltwin.basyx.client.internal.resolver.DescriptorResolver;

/**
 * Resolves an AasDescriptor into a {@link ConnectedAasService}
 * 
 * @author mateusmolina, danish
 *
 */
public class AasDescriptorResolver implements DescriptorResolver<AssetAdministrationShellDescriptor, ConnectedAasService> {

	static final String SPEC_INTERFACE = "AAS-3.0";

	private final EndpointResolver endpointResolver;

	/**
	 * Creates an AASDescriptorResolver
	 * 
	 * @param endpointResolver
	 */
	public AasDescriptorResolver(EndpointResolver endpointResolver) {
		this.endpointResolver = endpointResolver;
	}
	
	/**
	 * Resolves an AASDescriptor to a ConnectedAasService
	 * 
	 * @param aasDescriptor
	 * @return
	 */
	public ConnectedAasService resolveDescriptor(AssetAdministrationShellDescriptor aasDescriptor) {
		String endpoint = endpointResolver.resolveFirst(aasDescriptor.getEndpoints(), AasDescriptorResolver::parseEndpoint);

		return new ConnectedAasService(endpoint);
	}

	public static Optional<URI> parseEndpoint(Endpoint endpoint) {
		try {
			return Optional.ofNullable(endpoint).filter(ep -> ep.getInterface().equals(SPEC_INTERFACE)).map(Endpoint::getProtocolInformation).map(ProtocolInformation::getHref).map(URI::create);
		} catch (Exception e) {
			return Optional.empty();
		}
	}
}

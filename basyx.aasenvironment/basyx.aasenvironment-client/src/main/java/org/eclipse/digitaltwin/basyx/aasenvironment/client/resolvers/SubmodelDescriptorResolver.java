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

import org.eclipse.digitaltwin.basyx.submodelregistry.client.model.ProtocolInformation;
import org.eclipse.digitaltwin.basyx.client.internal.resolver.DescriptorResolver;
import java.net.URI;
import java.util.Optional;

import org.eclipse.digitaltwin.basyx.submodelregistry.client.model.Endpoint;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.model.SubmodelDescriptor;
import org.eclipse.digitaltwin.basyx.submodelservice.client.ConnectedSubmodelService;

/**
 * Resolves a SubmodelDescriptor into a {@link ConnectedSubmodelService}
 *
 * @author mateusmolina, danish
 *
 */
public class SubmodelDescriptorResolver implements DescriptorResolver<SubmodelDescriptor, ConnectedSubmodelService> {

	static final String SPEC_INTERFACE = "SUBMODEL-3.0";

	private final EndpointResolver endpointResolver;

	/**
	 * Constructs a SubmodelDescriptorResolver
	 * 
	 * @param endpointResolver
	 */
	public SubmodelDescriptorResolver(EndpointResolver endpointResolver) {
		this.endpointResolver = endpointResolver;
	}

	/**
	 * Resolves a Submodel Descriptor to a Connected SubmodelService
	 * 
	 * @param smDescriptor
	 *            the Submodel Descriptor to be resolved
	 * @return the Connected submodelserver
	 */
	public ConnectedSubmodelService resolveDescriptor(SubmodelDescriptor smDescriptor) {
		String endpoint = endpointResolver.resolveFirst(smDescriptor.getEndpoints(), SubmodelDescriptorResolver::parseEndpoint);

		return new ConnectedSubmodelService(endpoint);
	}

	public static Optional<URI> parseEndpoint(Endpoint endpoint) {
		try {
			return Optional.ofNullable(endpoint).filter(ep -> ep.getInterface().equals(SPEC_INTERFACE)).map(Endpoint::getProtocolInformation).map(ProtocolInformation::getHref).map(URI::create);
		} catch (Exception e) {
			return Optional.empty();
		}
	}
}

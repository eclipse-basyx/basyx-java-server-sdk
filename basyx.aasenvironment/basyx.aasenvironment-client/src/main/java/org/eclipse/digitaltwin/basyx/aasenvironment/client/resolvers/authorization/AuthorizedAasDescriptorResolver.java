package org.eclipse.digitaltwin.basyx.aasenvironment.client.resolvers.authorization;

import org.eclipse.digitaltwin.basyx.aasenvironment.client.resolvers.AasDescriptorResolver;
import org.eclipse.digitaltwin.basyx.aasenvironment.client.resolvers.EndpointResolver;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.AssetAdministrationShellDescriptor;
import org.eclipse.digitaltwin.basyx.aasservice.client.AuthorizedConnectedAasService;
import org.eclipse.digitaltwin.basyx.aasservice.client.ConnectedAasService;
import org.eclipse.digitaltwin.basyx.client.internal.authorization.TokenManager;
import org.eclipse.digitaltwin.basyx.client.internal.resolver.DescriptorResolver;

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

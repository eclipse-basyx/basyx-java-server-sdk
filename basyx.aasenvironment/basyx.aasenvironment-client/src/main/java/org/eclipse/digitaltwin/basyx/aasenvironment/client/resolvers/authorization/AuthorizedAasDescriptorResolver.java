package org.eclipse.digitaltwin.basyx.aasenvironment.client.resolvers.authorization;

import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.basyx.aasenvironment.client.resolvers.EndpointResolver;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.AssetAdministrationShellDescriptor;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.Endpoint;
import org.eclipse.digitaltwin.basyx.aasservice.client.AuthorizedConnectedAasService;
import org.eclipse.digitaltwin.basyx.client.internal.authorization.TokenManager;
import org.eclipse.digitaltwin.basyx.client.internal.resolver.DescriptorResolver;

public class AuthorizedAasDescriptorResolver implements DescriptorResolver<AssetAdministrationShellDescriptor, AssetAdministrationShell> {

	private final EndpointResolver<Endpoint> endpointResolver;
	private final TokenManager tokenManager;

	public AuthorizedAasDescriptorResolver(EndpointResolver<Endpoint> endpointResolver, TokenManager tokenManager) {
		this.endpointResolver = endpointResolver;
		this.tokenManager = tokenManager;
	}
	
	@Override
	public AssetAdministrationShell resolveDescriptor(AssetAdministrationShellDescriptor descriptor) {
		String endpoint = endpointResolver.resolveFirst(descriptor.getEndpoints());

		AuthorizedConnectedAasService aasService = new AuthorizedConnectedAasService(endpoint, tokenManager);

		return aasService.getAAS();
	}

}

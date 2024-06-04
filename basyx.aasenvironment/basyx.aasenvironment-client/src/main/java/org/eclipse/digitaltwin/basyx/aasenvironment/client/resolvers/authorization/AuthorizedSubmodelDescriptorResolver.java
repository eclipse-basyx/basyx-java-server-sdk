package org.eclipse.digitaltwin.basyx.aasenvironment.client.resolvers.authorization;

import org.eclipse.digitaltwin.aas4j.v3.model.Key;
import org.eclipse.digitaltwin.aas4j.v3.model.KeyTypes;
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.aas4j.v3.model.ReferenceTypes;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultKey;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultReference;
import org.eclipse.digitaltwin.basyx.aasenvironment.client.resolvers.EndpointResolver;
import org.eclipse.digitaltwin.basyx.aasenvironment.client.resolvers.SubmodelDescriptorResolver;
import org.eclipse.digitaltwin.basyx.client.internal.authorization.TokenManager;
import org.eclipse.digitaltwin.basyx.client.internal.resolver.DescriptorResolver;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.model.SubmodelDescriptor;
import org.eclipse.digitaltwin.basyx.submodelservice.client.AuthorizedConnectedSubmodelService;
import org.eclipse.digitaltwin.basyx.submodelservice.client.ConnectedSubmodelService;

public class AuthorizedSubmodelDescriptorResolver implements DescriptorResolver<SubmodelDescriptor, ConnectedSubmodelService> {

	private final EndpointResolver endpointResolver;
	private final TokenManager tokenManager;

	public AuthorizedSubmodelDescriptorResolver(EndpointResolver endpointResolver, TokenManager tokenManager) {
		this.endpointResolver = endpointResolver;
		this.tokenManager = tokenManager;
	}
	
	@Override
	public AuthorizedConnectedSubmodelService resolveDescriptor(SubmodelDescriptor descriptor) {
		String endpoint = endpointResolver.resolveFirst(descriptor.getEndpoints(), SubmodelDescriptorResolver::parseEndpoint);

		return new AuthorizedConnectedSubmodelService(endpoint, tokenManager);
	}
	
	public Reference deriveReferenceFromSubmodelDescriptor(SubmodelDescriptor smDescriptor) {
		return new DefaultReference.Builder().type(ReferenceTypes.EXTERNAL_REFERENCE).keys(generateKeyFromId(smDescriptor.getId())).build();
	}

	private static Key generateKeyFromId(String smId) {
		return new DefaultKey.Builder().type(KeyTypes.SUBMODEL).value(smId).build();
	}

}

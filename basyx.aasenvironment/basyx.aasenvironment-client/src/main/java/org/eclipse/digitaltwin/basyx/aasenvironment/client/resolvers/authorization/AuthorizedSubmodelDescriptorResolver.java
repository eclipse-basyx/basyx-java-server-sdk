package org.eclipse.digitaltwin.basyx.aasenvironment.client.resolvers.authorization;

import org.eclipse.digitaltwin.aas4j.v3.model.Key;
import org.eclipse.digitaltwin.aas4j.v3.model.KeyTypes;
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.aas4j.v3.model.ReferenceTypes;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultKey;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultReference;
import org.eclipse.digitaltwin.basyx.aasenvironment.client.resolvers.EndpointResolver;
import org.eclipse.digitaltwin.basyx.client.internal.authorization.TokenManager;
import org.eclipse.digitaltwin.basyx.client.internal.resolver.DescriptorResolver;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.model.Endpoint;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.model.SubmodelDescriptor;
import org.eclipse.digitaltwin.basyx.submodelservice.client.AuthorizedConnectedSubmodelService;

public class AuthorizedSubmodelDescriptorResolver implements DescriptorResolver<SubmodelDescriptor, Submodel> {

	private final EndpointResolver<Endpoint> endpointResolver;
	private final TokenManager tokenManager;

	public AuthorizedSubmodelDescriptorResolver(EndpointResolver<Endpoint> endpointResolver, TokenManager tokenManager) {
		this.endpointResolver = endpointResolver;
		this.tokenManager = tokenManager;
	}
	
	@Override
	public Submodel resolveDescriptor(SubmodelDescriptor descriptor) {
		String endpoint = endpointResolver.resolveFirst(descriptor.getEndpoints());

		AuthorizedConnectedSubmodelService smService = new AuthorizedConnectedSubmodelService(endpoint, tokenManager);

		return smService.getSubmodel();
	}
	
	public Reference deriveReferenceFromSubmodelDescriptor(SubmodelDescriptor smDescriptor) {
		return new DefaultReference.Builder().type(ReferenceTypes.EXTERNAL_REFERENCE).keys(generateKeyFromId(smDescriptor.getId())).build();
	}

	private static Key generateKeyFromId(String smId) {
		return new DefaultKey.Builder().type(KeyTypes.SUBMODEL).value(smId).build();
	}

}

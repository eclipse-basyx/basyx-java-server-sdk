package org.eclipse.digitaltwin.basyx.aasenvironment.client.resolvers;

import org.eclipse.digitaltwin.basyx.aasregistry.client.model.AssetAdministrationShellDescriptor;
import org.eclipse.digitaltwin.basyx.aasservice.client.ConnectedAasService;
import org.eclipse.digitaltwin.basyx.client.internal.resolver.DescriptorResolver;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.model.SubmodelDescriptor;
import org.eclipse.digitaltwin.basyx.submodelservice.client.ConnectedSubmodelService;

public class Resolver {
	
	private DescriptorResolver<AssetAdministrationShellDescriptor, ConnectedAasService> aasDescriptorResolver;
	private DescriptorResolver<SubmodelDescriptor, ConnectedSubmodelService> submodelDescriptorResolver;
	
	public Resolver(DescriptorResolver<AssetAdministrationShellDescriptor, ConnectedAasService> aasDescriptorResolver, DescriptorResolver<SubmodelDescriptor, ConnectedSubmodelService> submodelDescriptorResolver) {
		super();
		this.aasDescriptorResolver = aasDescriptorResolver;
		this.submodelDescriptorResolver = submodelDescriptorResolver;
	}

	public DescriptorResolver<AssetAdministrationShellDescriptor, ConnectedAasService> getAasDescriptorResolver() {
		return aasDescriptorResolver;
	}

	public DescriptorResolver<SubmodelDescriptor, ConnectedSubmodelService> getSubmodelDescriptorResolver() {
		return submodelDescriptorResolver;
	}

}

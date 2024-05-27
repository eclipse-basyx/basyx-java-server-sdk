package org.eclipse.digitaltwin.basyx.aasenvironment.client.resolvers;

import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.AssetAdministrationShellDescriptor;
import org.eclipse.digitaltwin.basyx.client.internal.resolver.DescriptorResolver;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.model.SubmodelDescriptor;

public class Resolver {
	
	private DescriptorResolver<AssetAdministrationShellDescriptor, AssetAdministrationShell> aasDescriptorResolver;
	private DescriptorResolver<SubmodelDescriptor, Submodel> submodelDescriptorResolver;
	
	public Resolver(DescriptorResolver<AssetAdministrationShellDescriptor, AssetAdministrationShell> aasDescriptorResolver, DescriptorResolver<SubmodelDescriptor, Submodel> submodelDescriptorResolver) {
		super();
		this.aasDescriptorResolver = aasDescriptorResolver;
		this.submodelDescriptorResolver = submodelDescriptorResolver;
	}

	public DescriptorResolver<AssetAdministrationShellDescriptor, AssetAdministrationShell> getAasDescriptorResolver() {
		return aasDescriptorResolver;
	}

	public DescriptorResolver<SubmodelDescriptor, Submodel> getSubmodelDescriptorResolver() {
		return submodelDescriptorResolver;
	}

}

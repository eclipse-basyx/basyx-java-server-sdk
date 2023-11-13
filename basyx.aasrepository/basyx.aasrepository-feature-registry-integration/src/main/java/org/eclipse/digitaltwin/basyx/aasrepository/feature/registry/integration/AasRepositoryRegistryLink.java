package org.eclipse.digitaltwin.basyx.aasrepository.feature.registry.integration;

import org.eclipse.digitaltwin.basyx.aasregistry.client.api.RegistryAndDiscoveryInterfaceApi;

public class AasRepositoryRegistryLink {
	
	private RegistryAndDiscoveryInterfaceApi registryApi;
	private String aasRepositoryURL;
	
	public AasRepositoryRegistryLink(RegistryAndDiscoveryInterfaceApi registryApi, String aasRepositoryURL) {
		super();
		this.registryApi = registryApi;
		this.aasRepositoryURL = aasRepositoryURL;
	}

	public RegistryAndDiscoveryInterfaceApi getRegistryApi() {
		return registryApi;
	}

	public String getAasRepositoryURL() {
		return aasRepositoryURL;
	}

}

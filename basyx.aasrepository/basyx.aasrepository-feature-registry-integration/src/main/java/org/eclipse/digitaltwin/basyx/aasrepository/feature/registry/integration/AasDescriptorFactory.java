package org.eclipse.digitaltwin.basyx.aasrepository.feature.registry.integration;

import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultAssetAdministrationShell;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.AssetAdministrationShellDescriptor;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.Endpoint;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.ProtocolInformation;
import org.eclipse.digitaltwin.basyx.common.mqttcore.encoding.Base64URLEncoder;
import org.eclipse.digitaltwin.basyx.http.Base64UrlEncodedIdentifier;

public class AasDescriptorFactory {
	
	private AssetAdministrationShell shell;
	private String aasRepositoryURL;
	
	public AasDescriptorFactory(AssetAdministrationShell shell, String aasRepositoryURL) {
		super();
		this.shell = shell;
		this.aasRepositoryURL = aasRepositoryURL;
	}
	
	public AssetAdministrationShellDescriptor create() {
		
		if (shell instanceof DefaultAssetAdministrationShell)
			return createDescriptorFromDefaultAas(shell);
		
		return createDescriptor(shell);
	}

	private AssetAdministrationShellDescriptor createDescriptor(AssetAdministrationShell shell) {
		return null;
	}

	private AssetAdministrationShellDescriptor createDescriptorFromDefaultAas(AssetAdministrationShell shell) {
		String endpoint = aasRepositoryURL + "/" + Base64UrlEncodedIdentifier.encodeIdentifier(shell.getId());
		
		AssetAdministrationShellDescriptor descriptor = new AssetAdministrationShellDescriptor();
		descriptor.setId(shell.getId());
		descriptor.setIdShort(shell.getIdShort());
//		descriptor.addEndpointsItem(new Endpoint());
		
		Endpoint endpoint2 = new Endpoint();
		endpoint2.setInterface(endpoint);
		ProtocolInformation protocolInformation = new ProtocolInformation();
		protocolInformation.endpointProtocol("http");
		protocolInformation.setHref("http");
		endpoint2.setProtocolInformation(protocolInformation);
		
		descriptor.addEndpointsItem(endpoint2);
		
		return descriptor;
	}

}

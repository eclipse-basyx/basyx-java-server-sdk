/*******************************************************************************
 * Copyright (C) 2023 DFKI GmbH (https://www.dfki.de/en/web)
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
package org.eclipse.digitaltwin.basyx.aasregistry.service.tests.integration;


import org.eclipse.digitaltwin.basyx.aasregistry.client.model.AssetAdministrationShellDescriptor;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.Endpoint;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.LangStringTextType;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.ProtocolInformation;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.SubmodelDescriptor;

public class ClientRegistryTestObjects {

	private ClientRegistryTestObjects() {
	}
	
	public static AssetAdministrationShellDescriptor newAssetAdministrationShellDescriptor(String id) {
		AssetAdministrationShellDescriptor descriptor = new AssetAdministrationShellDescriptor();
		descriptor.setId(id);
		return descriptor;
	}

	public static SubmodelDescriptor newSubmodelDescriptor(String id) {
		return newSubmodelDescriptorWithIdShort(id, null);
	}

	public static SubmodelDescriptor newSubmodelDescriptorWithDescription(String id, String description) {
		SubmodelDescriptor descriptor = new SubmodelDescriptor();
		descriptor.setId(id);
		addDescription(descriptor, description);
		return descriptor;
	}

	private static void addDescription(SubmodelDescriptor descriptor, String description) {
		if (description != null) {
			LangStringTextType lString = newDescription(description);
			descriptor.addDescriptionItem(lString);
		}
	}

	private static LangStringTextType newDescription(String sDescr) {
		LangStringTextType descr = new LangStringTextType();
		descr.setLanguage("de-DE");
		descr.setText(sDescr);
		return descr;
	}

	public static SubmodelDescriptor newSubmodelDescriptorWithIdShort(String id, String idShort) {
		SubmodelDescriptor descriptor = new SubmodelDescriptor();
		descriptor.setId(id);
		descriptor.setIdShort(idShort);
		
		return descriptor;
	}

	public static void addDefaultEndpoint(SubmodelDescriptor descriptor) {
		Endpoint endpoint = new Endpoint();
		endpoint.setInterface("https://admin-shell.io/aas/API/3/0/SubmodelServiceSpecification/SSP-003");
		ProtocolInformation protocolInfo = new ProtocolInformation();
		protocolInfo.setHref("http://127.0.0.1:8099/submodel");
		protocolInfo.setEndpointProtocol("HTTP");
		protocolInfo.setSubprotocol("AAS");
		endpoint.setProtocolInformation(protocolInfo);
		descriptor.addEndpointsItem(endpoint);	
	}
}
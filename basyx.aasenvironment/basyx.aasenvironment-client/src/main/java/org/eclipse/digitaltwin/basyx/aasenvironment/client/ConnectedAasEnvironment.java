/*******************************************************************************
 * Copyright (C) 2025 the Eclipse BaSyx Authors
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

package org.eclipse.digitaltwin.basyx.aasenvironment.client;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.commons.lang3.NotImplementedException;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.core.SerializationException;
import org.eclipse.digitaltwin.basyx.aasenvironment.AasEnvironment;
import org.eclipse.digitaltwin.basyx.aasenvironment.client.internal.SerializationApi;
import org.eclipse.digitaltwin.basyx.aasenvironment.environmentloader.CompleteEnvironment;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

public class ConnectedAasEnvironment implements AasEnvironment {
	protected static final String ENVIRONMENT_BASE_PATH = "http://localhost:8081";
	
	private final SerializationApi serializationApi;
	
	ConnectedAasEnvironment() {
		this.serializationApi = new SerializationApi(ENVIRONMENT_BASE_PATH);
	}
	
	public ConnectedAasEnvironment(String environmentBaseUrl) {
		this.serializationApi = new SerializationApi(environmentBaseUrl);
	}
	
	ConnectedAasEnvironment(SerializationApi serializationApi) {
		this.serializationApi = serializationApi;
	}
	
	/**
     * Generates serialization by IDs
     * 
     * @param serializationFormat the format in which the serialization is required
     * @param aasIds the list of AAS IDs to be serialized
     * @param submodelIds the list of Submodel IDs to be serialized
     * @param includeConceptDescriptions whether to include concept descriptions
     * @return The serialized object as a byte array or stream
     */
    public Resource generateSerializationByIds(String serializationFormat, List<String> aasIds, List<String> submodelIds, boolean includeConceptDescriptions) {
        try {
            return serializationApi.generateSerializationByIds(serializationFormat, aasIds, submodelIds, includeConceptDescriptions);
        } catch (Exception e) {
            // Handle the exception, wrap it, or rethrow it as appropriate
            throw new RuntimeException("Error generating serialization", e);
        }
    }

	@Override
	public String createJSONAASEnvironmentSerialization(List<String> aasIds, List<String> submodelIds,
			boolean includeConceptDescriptions) throws SerializationException {
		try {
			Resource res = serializationApi.generateSerializationByIds("application/json", aasIds, submodelIds, includeConceptDescriptions);
			
			String jsonStr = new String(((ByteArrayResource) res).getByteArray(), StandardCharsets.UTF_8);

            return jsonStr;
        } catch (Exception e) {
            throw new RuntimeException("Error generating serialization", e);
        }
	}

	@Override
	public String createXMLAASEnvironmentSerialization(List<String> aasIds, List<String> submodelIds,
			boolean includeConceptDescriptions) throws SerializationException {
		try {
			Resource res = serializationApi.generateSerializationByIds("application/xml", aasIds, submodelIds, includeConceptDescriptions);

			String jsonStr = new String(((ByteArrayResource) res).getByteArray(), StandardCharsets.UTF_8);

            return jsonStr;
        } catch (Exception e) {
            throw new RuntimeException("Error generating serialization", e);
        }
	}

	@Override
	public byte[] createAASXAASEnvironmentSerialization(List<String> aasIds, List<String> submodelIds,
			boolean includeConceptDescriptions) throws SerializationException, IOException {
		try {
			Resource res = serializationApi.generateSerializationByIds("application/asset-administration-shell-package+xml", aasIds, submodelIds, includeConceptDescriptions);
            return ((ByteArrayResource) res).getByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating serialization", e);
        }
	}

	@Override
	public void loadEnvironment(CompleteEnvironment completeEnvironment, boolean ignoreDuplicates) {
		throw new NotImplementedException(
				"loadEnvironment(CompleteEnvironment completeEnvironment, boolean ignoreDuplicates) is not yet implemented");
	}

	@Override
	public void loadEnvironment(CompleteEnvironment completeEnvironment) {
		throw new NotImplementedException(
				"loadEnvironment(CompleteEnvironment completeEnvironment) is not yet implemented");
	}
}
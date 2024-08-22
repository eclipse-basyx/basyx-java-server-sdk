/*******************************************************************************
 * Copyright (C) 2024 the Eclipse BaSyx Authors
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


import java.util.List;

import org.eclipse.digitaltwin.basyx.aasregistry.main.client.factory.AasDescriptorFactory;
import org.eclipse.digitaltwin.basyx.aasregistry.main.client.mapper.AttributeMapper;
import org.eclipse.digitaltwin.basyx.http.Aas4JHTTPSerializationExtension;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.factory.SubmodelDescriptorFactory;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Provides builder methods for {@link ConnectedAasManager} dependencies
 *
 * @author mateusmolina, danish
 *
 */
class ConnectedAasManagerHelper {

	public static final ObjectMapper objectMapper = buildObjectMapper();

	private ConnectedAasManagerHelper() {
	}

	static ObjectMapper buildObjectMapper() {
		Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder().serializationInclusion(JsonInclude.Include.NON_NULL);
		Aas4JHTTPSerializationExtension extension = new Aas4JHTTPSerializationExtension();
		extension.extend(builder);
		return builder.build();
	}

	static AasDescriptorFactory buildAasDescriptorFactory(String... aasRepositoryBaseUrls) {
		AttributeMapper attributeMapper = new AttributeMapper(objectMapper);

		return new AasDescriptorFactory(null, List.of(aasRepositoryBaseUrls), attributeMapper);
	}

	static SubmodelDescriptorFactory buildSmDescriptorFactory(String... aasRepositoryBaseUrls) {
		org.eclipse.digitaltwin.basyx.submodelregistry.client.mapper.AttributeMapper attributeMapperSm = new org.eclipse.digitaltwin.basyx.submodelregistry.client.mapper.AttributeMapper(
				objectMapper);
		return new SubmodelDescriptorFactory(null, List.of(aasRepositoryBaseUrls), attributeMapperSm);
	}

}

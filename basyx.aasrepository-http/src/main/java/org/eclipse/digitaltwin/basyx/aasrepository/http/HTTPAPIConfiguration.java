/*******************************************************************************
 * Copyright (C) 2023 the Eclipse BaSyx Authors
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


package org.eclipse.digitaltwin.basyx.aasrepository.http;

import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.Referable;
import org.eclipse.digitaltwin.basyx.aasrepository.http.serialization.AASJsonDeserializer;
import org.eclipse.digitaltwin.basyx.aasrepository.http.serialization.ReferableJsonSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Handles the mapping between Spring and AAS4J
 * 
 * @author schnicke
 *
 */
@Configuration
public class HTTPAPIConfiguration {

	@Bean
	public Jackson2ObjectMapperBuilder jackson2ObjectMapperBuilder() {
		Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder().serializationInclusion(JsonInclude.Include.NON_NULL);

		builder.serializerByType(Referable.class, new ReferableJsonSerializer());

		// TODO: Extend with further Referable types
		builder.deserializerByType(AssetAdministrationShell.class, new AASJsonDeserializer());

		return builder;
	}
}

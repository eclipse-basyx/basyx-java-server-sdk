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


package org.eclipse.digitaltwin.basyx.submodelrepository.http.deserialization.factory;

import java.util.List;

import org.eclipse.digitaltwin.basyx.submodelservice.value.SubmodelElementCollectionValue;
import org.eclipse.digitaltwin.basyx.submodelservice.value.ValueOnly;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Factory class to create deserialized {@link SubmodelElementCollectionValue} based on
 * the content
 * 
 * @author danish
 *
 */
public class SubmodelElementCollectionValueDeserializationFactory {
	
	private ObjectMapper objectMapper;
	private JsonNode node;
	
	public SubmodelElementCollectionValueDeserializationFactory(ObjectMapper objectMapper, JsonNode node) {
		this.objectMapper = objectMapper;
		this.node = node;
	}
	
	/**
	 * Deserializes the corresponding {@link SubmodelElementCollectionValue} based on the
	 * JSON content
	 * 
	 * @return {@link SubmodelElementCollectionValue}
	 * 
	 */
	public SubmodelElementCollectionValue create() throws JsonProcessingException {
		List<ValueOnly> valueOnlies = objectMapper.readValue(node.toString(), new TypeReference<List<ValueOnly>>() {});

		return new SubmodelElementCollectionValue(valueOnlies);
	}

}

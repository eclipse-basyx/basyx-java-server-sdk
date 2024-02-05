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

package org.eclipse.digitaltwin.basyx.deserialization;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map.Entry;

import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.basyx.submodelservice.value.ReferenceValue;
import org.eclipse.digitaltwin.basyx.submodelservice.value.SpecificAssetIdValue;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author jungjan, witt
 */
public class SpecificAssetIdValueJsonDeserializer extends JsonDeserializer<SpecificAssetIdValue> {
	private final String EXTERNAL_SUBJECT_ID_KEY = "externalSubjectId";

	@Override
	public SpecificAssetIdValue deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
		try {
			ObjectMapper mapper = (ObjectMapper) p.getCodec();
			JsonNode node = mapper.readTree(p);

			Iterator<Entry<String, JsonNode>> jsnonFields = node.fields();
			Entry<String, JsonNode> nameValueMap = jsnonFields.next();

			String name = nameValueMap.getKey();
			String value = nameValueMap.getValue()
					.asText();
			ReferenceValue externalSubjectIdValue = null;

			if (jsonContainsExternalSubjectId(node)) {
				externalSubjectIdValue = handleExternalSubjectId(mapper, node);
			}

			return new SpecificAssetIdValue(name, value, externalSubjectIdValue);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private boolean jsonContainsExternalSubjectId(JsonNode node) {
		return node.get(EXTERNAL_SUBJECT_ID_KEY) != null;
	}

	private ReferenceValue handleExternalSubjectId(ObjectMapper mapper, JsonNode node) throws JsonProcessingException, JsonMappingException {
		JsonNode externalSubjectIdNode = node.get(EXTERNAL_SUBJECT_ID_KEY);
		Reference externalSubjectId = mapper.readValue(externalSubjectIdNode.toString(), new TypeReference<Reference>() {
		});
		return new ReferenceValue(externalSubjectId.getType(), externalSubjectId.getKeys());
	}
}
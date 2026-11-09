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

import java.util.Iterator;
import java.util.Map.Entry;

import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.basyx.submodelservice.value.ReferenceValue;
import org.eclipse.digitaltwin.basyx.submodelservice.value.SpecificAssetIdValue;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.JsonNode;

/**
 * @author jungjan, witt
 */
public class SpecificAssetIdValueJsonDeserializer extends ValueDeserializer<SpecificAssetIdValue> {
	private final String EXTERNAL_SUBJECT_ID_KEY = "externalSubjectId";

	@Override
	public SpecificAssetIdValue deserialize(JsonParser p, DeserializationContext ctxt) {
		JsonNode node = ctxt.readTree(p);

		Iterator<Entry<String, JsonNode>> jsnonFields = node.properties().iterator();
		Entry<String, JsonNode> nameValueMap = jsnonFields.next();

		String name = nameValueMap.getKey();
		String value = nameValueMap.getValue()
				.asString();
		ReferenceValue externalSubjectIdValue = null;

		if (jsonContainsExternalSubjectId(node)) {
			externalSubjectIdValue = handleExternalSubjectId(ctxt, node);
		}

		return new SpecificAssetIdValue(name, value, externalSubjectIdValue);
	}

	private boolean jsonContainsExternalSubjectId(JsonNode node) {
		return node.get(EXTERNAL_SUBJECT_ID_KEY) != null;
	}

	private ReferenceValue handleExternalSubjectId(DeserializationContext ctxt, JsonNode node) throws JacksonException {
		JsonNode externalSubjectIdNode = node.get(EXTERNAL_SUBJECT_ID_KEY);
		Reference externalSubjectId = ctxt.readTreeAsValue(externalSubjectIdNode, Reference.class);
		return new ReferenceValue(externalSubjectId.getType(), externalSubjectId.getKeys());
	}
}
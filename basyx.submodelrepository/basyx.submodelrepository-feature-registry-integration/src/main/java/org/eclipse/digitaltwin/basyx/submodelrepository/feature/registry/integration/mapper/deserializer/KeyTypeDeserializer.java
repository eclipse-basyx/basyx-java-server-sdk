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

package org.eclipse.digitaltwin.basyx.submodelrepository.feature.registry.integration.mapper.deserializer;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.model.KeyTypes;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Custom deserializer for {@link KeyTypes}
 * 
 * @author danish
 */
public class KeyTypeDeserializer extends JsonDeserializer<KeyTypes> {

	@Override
	public KeyTypes deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException, JacksonException {
		try {
			JsonNode node = parser.getCodec()
					.readTree(parser);

			String value = node.asText();

			String compatibleEnumValue = StringUtils.remove(value, '_');

			return KeyTypes.valueOf(KeyTypes.class, compatibleEnumValue);

		} catch (Exception e) {
			throw new RuntimeException("Unable to deserialize the KeyTypes Enum.");
		}
	}

}

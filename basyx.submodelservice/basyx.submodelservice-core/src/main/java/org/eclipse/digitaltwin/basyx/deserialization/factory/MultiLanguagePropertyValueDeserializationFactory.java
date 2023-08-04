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


package org.eclipse.digitaltwin.basyx.deserialization.factory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.model.LangStringTextType;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultLangStringTextType;
import org.eclipse.digitaltwin.basyx.submodelservice.value.MultiLanguagePropertyValue;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Factory class to create deserialized {@link MultiLanguagePropertyValue} based on
 * the content
 * 
 * @author danish
 *
 */
public class MultiLanguagePropertyValueDeserializationFactory {
	
	private JsonNode node;
	
	public MultiLanguagePropertyValueDeserializationFactory(JsonNode node) {
		this.node = node;
	}
	
	/**
	 * Deserializes the corresponding {@link MultiLanguagePropertyValue} based on the
	 * JSON content
	 * 
	 * @return {@link MultiLanguagePropertyValue}
	 * 
	 */
	public MultiLanguagePropertyValue create() {
		List<LangStringTextType> langStrings = createLangStrings(node);

		return new MultiLanguagePropertyValue(langStrings);
	}
	
	private static List<LangStringTextType> createLangStrings(JsonNode node) {
		List<LangStringTextType> langStrings = new ArrayList<>();

		for (JsonNode element : node) {
			langStrings.add(createLangString(element));
		}

		return langStrings;
	}

	private static DefaultLangStringTextType createLangString(JsonNode arrayNode) {
		Iterator<String> fieldNames = arrayNode.fieldNames();
		String language = fieldNames.next();
		String text = arrayNode.get(language).asText();

		return new DefaultLangStringTextType.Builder().text(text).language(language).build();
	}

}

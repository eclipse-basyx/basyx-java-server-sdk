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


package org.eclipse.digitaltwin.basyx.submodelrepository.http.deserialization.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.eclipse.digitaltwin.aas4j.v3.model.LangString;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultLangString;
import org.eclipse.digitaltwin.basyx.submodelservice.value.FileValue;
import org.eclipse.digitaltwin.basyx.submodelservice.value.MultiLanguagePropertyValue;
import org.eclipse.digitaltwin.basyx.submodelservice.value.PropertyValue;
import org.eclipse.digitaltwin.basyx.submodelservice.value.RangeValue;
import org.eclipse.digitaltwin.basyx.submodelservice.value.SubmodelElementValue;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * An utility class to check and validate the type for Deserialization
 * 
 * @author danish
 *
 */
public class SubmodelElementValueDeserializationUtil {

	private SubmodelElementValueDeserializationUtil() {
		throw new IllegalStateException("Utility class");
	}

	public static SubmodelElementValue createMultiLanguagePropertyValue(JsonNode node) {
		List<LangString> langStrings = createLangStrings(node);

		return new MultiLanguagePropertyValue(langStrings);
	}

	public static boolean isTypeOfPropertyValue(JsonNode node) {
		return isTypeOf(PropertyValue.class, node);
	}

	public static boolean isTypeOfFileValue(JsonNode node) {
		return isTypeOf(FileValue.class, node);
	}

	public static boolean isTypeOfRangeValue(JsonNode node) {
		return isTypeOf(RangeValue.class, node);
	}

	public static boolean isTypeOfMultiLanguagePropertyValue(JsonNode node) {
		return node.isArray() && hasStructureOfMultiLanguagePropertyValue(node);
	}
	
	private static List<LangString> createLangStrings(JsonNode node) {
		List<LangString> langStrings = new ArrayList<>();
		
        for (JsonNode element : node) {
        	langStrings.add(createLangString(element));
        }
        
		return langStrings;
	}

	private static DefaultLangString createLangString(JsonNode arrayNode) {
		Iterator<String> fieldNames = arrayNode.fieldNames();
		String language = fieldNames.next();
		String text = arrayNode.get(language).asText();
		
		return new DefaultLangString(text, language);
	}

	private static boolean isTypeOf(Class<? extends SubmodelElementValue> clazz, JsonNode node) {
		Field[] fields = clazz.getDeclaredFields();
		int numOfAttributes = fields.length;
		
		if (node.size() != numOfAttributes)
			return false;

		for (Field field : fields) {
			if (!node.has(field.getName()))
				return false;
		}

		return true;
	}

	private static boolean hasStructureOfMultiLanguagePropertyValue(JsonNode node) {
		for (JsonNode element : node) {
			if (!isValidLanguagePropertyValue(element))
				return false;
		}

		return true;
	}

	private static boolean isValidLanguagePropertyValue(JsonNode element) {
		return element.isObject() && ((ObjectNode) element).size() == 1 && isLanguageTextOfTypeText(element);
	}

	private static boolean isLanguageTextOfTypeText(JsonNode element) {
		String language = element.fieldNames().next();

		return element.get(language).isTextual();
	}

}

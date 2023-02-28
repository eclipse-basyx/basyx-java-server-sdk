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

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.eclipse.digitaltwin.aas4j.v3.model.LangString;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultLangString;
import org.eclipse.digitaltwin.basyx.submodelservice.value.AnnotatedRelationshipElementValue;
import org.eclipse.digitaltwin.basyx.submodelservice.value.EntityValue;
import org.eclipse.digitaltwin.basyx.submodelservice.value.FileBlobValue;
import org.eclipse.digitaltwin.basyx.submodelservice.value.MultiLanguagePropertyValue;
import org.eclipse.digitaltwin.basyx.submodelservice.value.RangeValue;
import org.eclipse.digitaltwin.basyx.submodelservice.value.ReferenceValue;
import org.eclipse.digitaltwin.basyx.submodelservice.value.RelationshipElementValue;
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

	public static boolean isTypeOfPropertyValue(JsonNode node) {
		return node.isValueNode() && node.isTextual();
	}

	public static boolean isTypeOfFileBlobValue(JsonNode node) {
		return isTypeOf(FileBlobValue.class, node);
	}

	public static boolean isTypeOfRangeValue(JsonNode node) {
		return isTypeOf(RangeValue.class, node);
	}

	public static boolean isTypeOfMultiLanguagePropertyValue(JsonNode node) {
		return node.isArray() && hasStructureOfMultiLanguagePropertyValue(node);
	}
	
	public static boolean isTypeOfEntityValue(JsonNode node) {
		return isTypeOf(EntityValue.class, node);
	}
	
	public static boolean isTypeOfReferenceElementValue(JsonNode node) {
		return isTypeOf(ReferenceValue.class, node);
	}
	
	public static boolean isTypeOfRelationshipElementValue(JsonNode node) {
		return isTypeOf(RelationshipElementValue.class, node);
	}
	
	public static boolean isTypeOfAnnotatedRelationshipElementValue(JsonNode node) {
		return isTypeOf(AnnotatedRelationshipElementValue.class, node);
	}
	
	public static SubmodelElementValue createMultiLanguagePropertyValue(JsonNode node) {
		List<LangString> langStrings = createLangStrings(node);

		return new MultiLanguagePropertyValue(langStrings);
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

	private static boolean isTypeOf(Class<?> clazz, JsonNode node) {
		Field[] superClassFields = clazz.getSuperclass().getDeclaredFields();
		Field[] fields = clazz.getDeclaredFields();
		
		if (superClassFields.length != 0)
			fields = merge(superClassFields, fields);
		
		int countOfOptionalAttributes = getOptionalAttributesCount(fields);
		int totalCountOfAttributes = fields.length;
		
		if(!isSizeOfAttributesValid(node.size(), countOfOptionalAttributes, totalCountOfAttributes)) 
			return false;

		for (Field field : fields) {
			if (!isOptionalAttribute(field) && !node.has(field.getName()))
				return false;
		}

		return true;
	}

	private static boolean isSizeOfAttributesValid(int nodeSize, int countOfOptionalAttributes,
			int totalCountOfAttributes) {
		if(countOfOptionalAttributes == 0)
			return nodeSize == totalCountOfAttributes;
			
		return nodeSize >= (totalCountOfAttributes - countOfOptionalAttributes);
	}

	private static int getOptionalAttributesCount(Field[] fields) {
		int count = 0;
		
		for (Field field : fields) {
			if (isOptionalAttribute(field))
				count++;
		}
		
		return count;
	}

	private static boolean isOptionalAttribute(Field field) {
		return Optional.class.isAssignableFrom(field.getType());
	}
	
	private static Field[] merge(Field[] fieldArray1, Field[] fieldArray2) {
        int lengthFieldArray1 = Array.getLength(fieldArray1);
        int lengthFieldArray2 = Array.getLength(fieldArray2);
        Field[] result = (Field[]) Array.newInstance(Field.class, lengthFieldArray1 + lengthFieldArray2);
        System.arraycopy(fieldArray1, 0, result, 0, lengthFieldArray1);
        System.arraycopy(fieldArray2, 0, result, lengthFieldArray1, lengthFieldArray2);
        
        return result;
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

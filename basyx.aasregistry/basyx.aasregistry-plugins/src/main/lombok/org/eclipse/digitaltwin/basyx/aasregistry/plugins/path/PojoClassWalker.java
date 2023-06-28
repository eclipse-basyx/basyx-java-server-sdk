/*******************************************************************************
 * Copyright (C) 2023 DFKI GmbH (https://www.dfki.de/en/web)
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
package org.eclipse.digitaltwin.basyx.aasregistry.plugins.path;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.digitaltwin.basyx.aasregistry.plugins.path.PojoClassVisitor.PojoRelation;
import org.eclipse.digitaltwin.basyx.aasregistry.plugins.path.PojoClassVisitor.PojoRelation.PojoRelationType;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class PojoClassWalker {

	private final Class<?> root;
	private final PojoClassVisitor visitor;

	public void walkClass() {
		walkClass(root, null);
		visitor.stop();
	}

	private void walkClass(Class<?> cls, String path) {
		String name = cls.getSimpleName();
		if (visitor.startType(name, cls == root)) {
			for (Field field : getFields(cls)) {
				walkField(cls, field, path);
			}
		}
		visitor.endType();
	}

	private void walkField(Class<?> subjectcls, Field field, String path) {
		if (Modifier.isStatic(field.getModifiers())) {
			return;
		}
		String methodName = field.getName();
		String fieldName = getModelPropertyOrFieldName(field);

		PojoRelation.PojoRelationBuilder builder = PojoRelation.builder().methodName(methodName).fieldName(fieldName).isRootRelation(subjectcls == root).subject(subjectcls.getSimpleName());
		String newPath = generateNewPath(path, fieldName);
		Class<?> type = getFieldTypeAndAssignRange(field, builder);

		if (type.isPrimitive() || type.equals(String.class) || type.equals(Boolean.class) || Number.class.isAssignableFrom(type) || Enum.class.isAssignableFrom(type)) {
			builder = builder.range(type.getSimpleName()).isPrimitive(true);
			walkPrimitiveRelation(builder);
		} else {
			walkComplexRelation(type, newPath, builder);
		}
	}

	private Class<?> getFieldTypeAndAssignRange(Field field, PojoRelation.PojoRelationBuilder builder) {
		Class<?> type = field.getType();
		if (List.class.isAssignableFrom(type)) {
			type = getGenericClass(field, 0);
			builder.type(PojoRelationType.LIST);
		} else if (Map.class.isAssignableFrom(type)) {
			type = getGenericClass(field, 1);
			builder.type(PojoRelationType.MAP);
		} else {
			builder.type(PojoRelationType.FUNCTIONAL);
		}
		return type;
	}

	private void walkComplexRelation(Class<?> type, String newPath, PojoRelation.PojoRelationBuilder builder) {
		String typeName = type.getSimpleName();
		List<Class<?>> subTypes = getSubTypes(type);
		PojoRelation relation = builder.range(typeName).build();
		visitor.startRelation(relation);
		if (subTypes.isEmpty()) {
			walkClass(type, newPath);
		} else {
			List<String> subTypeNames = subTypes.stream().map(Class::getSimpleName).collect(Collectors.toList());
			visitor.onSubTypeRelation(typeName, subTypeNames);
			walkClass(type, newPath);
			for (Class<?> eachSubtype : subTypes) {
				walkClass(eachSubtype, newPath);
			}
		}
		visitor.endRelation(relation);
	}

	private void walkPrimitiveRelation(PojoRelation.PojoRelationBuilder builder) {
		PojoRelation relation = builder.build();
		visitor.startRelation(relation);
		visitor.endRelation(relation);
	}

	private static List<Field> getFields(Class<?> cls) {
		List<Field> fields = new LinkedList<>();
		Set<String> duplicateFieldNameFilter = new HashSet<>();
		getFields(cls, fields, duplicateFieldNameFilter);
		return fields;
	}

	private static void getFields(Class<?> cls, List<Field> fields, Set<String> duplicateFieldNameFilter) {
		if (cls != null) {
			for (Field eachField: cls.getDeclaredFields()) {
				if (duplicateFieldNameFilter.add(eachField.getName())) {
					fields.add(eachField);
				}
			}
			getFields(cls.getSuperclass(), fields, duplicateFieldNameFilter);
		}
	}

	private String getModelPropertyOrFieldName(Field eachField) {
		JsonProperty jsonProp = eachField.getDeclaredAnnotation(JsonProperty.class);
		if (jsonProp != null) {
			String name = jsonProp.value();
			if (!StringUtils.isEmpty(name)) {
				return name;
			}
		}
		return eachField.getName();
	}

	private String generateNewPath(String currentPath, String fieldName) {
		return currentPath == null ? fieldName : String.join(".", currentPath, fieldName);
	}

	private Class<?> getGenericClass(Field field, int pos) {
		ParameterizedType genType = (ParameterizedType) field.getGenericType();
		return (Class<?>) genType.getActualTypeArguments()[pos];
	}

	private List<Class<?>> getSubTypes(Class<?> type) {
		JsonSubTypes types = type.getAnnotation(JsonSubTypes.class);
		if (types == null) {
			return Collections.emptyList();
		}
		JsonSubTypes.Type[] referencedTypes = types.value();
		if (referencedTypes == null) {
			return Collections.emptyList();
		}
		List<Class<?>> toReturn = new ArrayList<>(referencedTypes.length);
		for (JsonSubTypes.Type eachType : referencedTypes) {
			Class<?> subType = eachType.value();
			toReturn.add(subType);
		}
		return toReturn;
	}

}

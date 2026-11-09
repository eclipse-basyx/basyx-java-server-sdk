/*******************************************************************************
 * Copyright (C) 2026 the Eclipse BaSyx Authors
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

package org.eclipse.digitaltwin.basyx.http.documentation;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.digitaltwin.aas4j.v3.dataformat.core.internal.util.ReflectionHelper;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
import com.fasterxml.jackson.databind.jsontype.impl.StdTypeResolverBuilder;

/**
 * Jackson 2 counterpart of AAS4J's ReflectionAnnotationIntrospector.
 * 
 * AAS4J serializes with Jackson 3, but swagger-core (used by springdoc to
 * derive the OpenAPI schemas) still relies on Jackson 2. This introspector
 * provides the AAS model type information to swagger-core so that the generated
 * API documentation matches the actual payloads.
 *
 * @author zielstor
 */
public class Aas4JOpenApiAnnotationIntrospector extends JacksonAnnotationIntrospector {

	private static final long serialVersionUID = 1L;

	private static final String MODEL_TYPE_PROPERTY = "modelType";
	private static final String GETTER_PREFIX = "get";

	@Override
	public String findTypeName(AnnotatedClass ac) {
		String customType = ReflectionHelper.getModelType(ac.getRawType());
		return customType != null ? customType : super.findTypeName(ac);
	}

	@Override
	public TypeResolverBuilder<?> findTypeResolver(MapperConfig<?> config, AnnotatedClass ac, com.fasterxml.jackson.databind.JavaType baseType) {
		String modelType = ReflectionHelper.getModelType(ac.getRawType());
		if (modelType != null) {
			JsonTypeInfo.Value typeInfo = JsonTypeInfo.Value.construct(JsonTypeInfo.Id.NAME, JsonTypeInfo.As.PROPERTY, MODEL_TYPE_PROPERTY, null, false, null);
			return new StdTypeResolverBuilder(typeInfo);
		}
		return super.findTypeResolver(config, ac, baseType);
	}

	@Override
	public List<NamedType> findSubtypes(Annotated a) {
		if (ReflectionHelper.SUBTYPES.containsKey(a.getRawType()) && !ReflectionHelper.SUBTYPES.get(a.getRawType()).isEmpty()) {
			return ReflectionHelper.SUBTYPES.get(a.getRawType()).stream().map(x -> new NamedType(x, x.getSimpleName())).collect(Collectors.toList());
		}
		return super.findSubtypes(a);
	}

	@Override
	public JsonInclude.Value findPropertyInclusion(Annotated a) {
		JsonInclude.Value result = super.findPropertyInclusion(a);
		if (result != JsonInclude.Value.empty()) {
			return result;
		}
		if (a instanceof AnnotatedMethod method && method.getParameterCount() == 0 && method.getName().startsWith(GETTER_PREFIX) && Collection.class.isAssignableFrom(method.getRawReturnType())
				&& ReflectionHelper.isModelInterfaceOrDefaultImplementation(method.getDeclaringClass())) {
			return result.withValueInclusion(JsonInclude.Include.NON_EMPTY);
		}
		return result;
	}
}

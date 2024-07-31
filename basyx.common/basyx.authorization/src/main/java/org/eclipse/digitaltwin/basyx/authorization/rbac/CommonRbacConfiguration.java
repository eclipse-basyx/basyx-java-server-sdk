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

package org.eclipse.digitaltwin.basyx.authorization.rbac;

import java.util.HashMap;
import java.util.Objects;
import java.util.Set;

import org.eclipse.digitaltwin.basyx.authorization.CommonAuthorizationProperties;
import org.reflections.Reflections;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.module.SimpleModule;

/**
 * A base configuration for Rbac based authorization
 * 
 * @author danish
 */
@Configuration
@ConditionalOnExpression(value = "${" + CommonAuthorizationProperties.ENABLED_PROPERTY_KEY + ":false} and ('${" + CommonAuthorizationProperties.TYPE_PROPERTY_KEY + "}'.equals('rbac') or '${" + CommonAuthorizationProperties.TYPE_PROPERTY_KEY + "}'.isEmpty())")
public class CommonRbacConfiguration {

	@Bean
	public ObjectMapper getAasMapper(Jackson2ObjectMapperBuilder builder) {
		ObjectMapper mapper = builder.build();

		Reflections reflections = new Reflections("org.eclipse.digitaltwin.basyx");
		Set<Class<?>> subtypes = reflections.getTypesAnnotatedWith(TargetInformationSubtype.class);
		
		subtypes.stream().map(this::createNamedType).filter(Objects::nonNull).forEach(mapper::registerSubtypes);
		
		SimpleModule module = new SimpleModule();
        module.addDeserializer(HashMap.class, new RbacRuleDeserializer());
        mapper.registerModule(module);

		return mapper;
	}

	private NamedType createNamedType(Class<?> subType) {
		TargetInformationSubtype annotation = subType.getAnnotation(TargetInformationSubtype.class);
		
		return (annotation != null) ? new NamedType(subType, annotation.getValue()) : null;
	}

}

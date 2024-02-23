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

package org.eclipse.digitaltwin.basyx.http;

import org.eclipse.digitaltwin.aas4j.v3.dataformat.core.internal.deserialization.EnumDeserializer;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.core.internal.serialization.EnumSerializer;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.core.internal.util.ReflectionHelper;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.json.internal.ReflectionAnnotationIntrospector;
import org.eclipse.digitaltwin.basyx.core.StandardizedLiteralEnum;
import org.eclipse.digitaltwin.basyx.http.description.Profile;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.module.SimpleAbstractTypeResolver;
import com.fasterxml.jackson.databind.module.SimpleModule;

/**
 * SerializationExtension integrating the AAS4J serialization in BaSyx
 *
 * @author schnicke
 */
@Component
public class Aas4JHTTPSerializationExtension implements SerializationExtension {

  protected SimpleAbstractTypeResolver typeResolver;

  public Aas4JHTTPSerializationExtension() {
    initTypeResolver();
  }

  @Override
  public void extend(Jackson2ObjectMapperBuilder builder) {
    builder.featuresToEnable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
        .serializationInclusion(JsonInclude.Include.NON_NULL)
        .featuresToDisable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        .annotationIntrospector(new ReflectionAnnotationIntrospector())
        .modulesToInstall(buildEnumModule(), buildImplementationModule());
    ReflectionHelper.JSON_MIXINS.entrySet().forEach(x -> builder.mixIn(x.getKey(), x.getValue()));
  }

  @SuppressWarnings("unchecked")
  private void initTypeResolver() {
    typeResolver = new SimpleAbstractTypeResolver();
    ReflectionHelper.DEFAULT_IMPLEMENTATIONS.stream()
        .forEach(x -> typeResolver.addMapping(x.getInterfaceType(), x.getImplementationType()));
  }

  protected SimpleModule buildEnumModule() {
    SimpleModule module = new SimpleModule();
    module.addSerializer(StandardizedLiteralEnum.class, new StandardizedLiteralEnumSerializer<>());
    module.addDeserializer(Profile.class, new StandardizedLiteralEnumDeserializer<>(Profile.class));
	ReflectionHelper.ENUMS.forEach(x -> module.addSerializer(x, new EnumSerializer()));
    ReflectionHelper.ENUMS.forEach(x -> module.addDeserializer(x, new EnumDeserializer<>(x)));
    return module;
  }

  protected SimpleModule buildImplementationModule() {
    SimpleModule module = new SimpleModule();
    module.setAbstractTypes(typeResolver);
    return module;
  }

}

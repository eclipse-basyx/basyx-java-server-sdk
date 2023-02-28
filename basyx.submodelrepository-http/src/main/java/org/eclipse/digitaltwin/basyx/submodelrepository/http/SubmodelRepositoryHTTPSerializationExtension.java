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


package org.eclipse.digitaltwin.basyx.submodelrepository.http;

import org.eclipse.digitaltwin.basyx.http.SerializationExtension;
import org.eclipse.digitaltwin.basyx.submodelrepository.http.deserialization.SpecificAssetIdValueDeserializer;
import org.eclipse.digitaltwin.basyx.submodelrepository.http.deserialization.SubmodelElementValueJsonDeserializer;
import org.eclipse.digitaltwin.basyx.submodelrepository.http.deserialization.ValueOnlyJsonDeserializer;
import org.eclipse.digitaltwin.basyx.submodelrepository.http.mixins.ReferenceElementValueMixIn;
import org.eclipse.digitaltwin.basyx.submodelrepository.http.serialization.MultiLanguagePropertyValueSerializer;
import org.eclipse.digitaltwin.basyx.submodelrepository.http.serialization.PropertyValueSerializer;
import org.eclipse.digitaltwin.basyx.submodelrepository.http.serialization.SpecificAssetIdValueSerializer;
import org.eclipse.digitaltwin.basyx.submodelrepository.http.serialization.ValueOnlySerializer;
import org.eclipse.digitaltwin.basyx.submodelservice.value.MultiLanguagePropertyValue;
import org.eclipse.digitaltwin.basyx.submodelservice.value.PropertyValue;
import org.eclipse.digitaltwin.basyx.submodelservice.value.ReferenceElementValue;
import org.eclipse.digitaltwin.basyx.submodelservice.value.SpecificAssetIdValue;
import org.eclipse.digitaltwin.basyx.submodelservice.value.SubmodelElementValue;
import org.eclipse.digitaltwin.basyx.submodelservice.value.ValueOnly;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.stereotype.Component;

/**
 * SerializationExtension integrating the additional SubmodelRepository
 * serialization
 * 
 * @author schnicke, danish
 *
 */
@Component
public class SubmodelRepositoryHTTPSerializationExtension implements SerializationExtension {

	@Override
	public void extend(Jackson2ObjectMapperBuilder builder) {
		builder.serializerByType(MultiLanguagePropertyValue.class, new MultiLanguagePropertyValueSerializer());
		builder.deserializerByType(SubmodelElementValue.class, new SubmodelElementValueJsonDeserializer());
		builder.deserializerByType(ValueOnly.class, new ValueOnlyJsonDeserializer());
		builder.deserializerByType(SpecificAssetIdValue.class, new SpecificAssetIdValueDeserializer());
		builder.serializerByType(SpecificAssetIdValue.class, new SpecificAssetIdValueSerializer());
		builder.serializerByType(ValueOnly.class, new ValueOnlySerializer());
		builder.serializerByType(PropertyValue.class, new PropertyValueSerializer());
		builder.mixIn(ReferenceElementValue.class, ReferenceElementValueMixIn.class);
	}

}

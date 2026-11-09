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

package org.eclipse.digitaltwin.basyx.submodelservice.http;

import org.eclipse.digitaltwin.basyx.deserialization.SubmodelElementValueJsonDeserializer;
import org.eclipse.digitaltwin.basyx.deserialization.SpecificAssetIdValueJsonDeserializer;
import org.eclipse.digitaltwin.basyx.deserialization.ValueOnlyJsonDeserializer;
import org.eclipse.digitaltwin.basyx.http.SerializationExtension;
import org.eclipse.digitaltwin.basyx.mixins.ReferenceElementValueMixIn;
import org.eclipse.digitaltwin.basyx.serialization.MultiLanguagePropertyValueSerializer;
import org.eclipse.digitaltwin.basyx.serialization.PropertyValueSerializer;
import org.eclipse.digitaltwin.basyx.serialization.SpecificAssetIdValueSerializer;
import org.eclipse.digitaltwin.basyx.serialization.SubmodelElementCollectionValueSerializer;
import org.eclipse.digitaltwin.basyx.serialization.SubmodelElementListValueSerializer;
import org.eclipse.digitaltwin.basyx.serialization.SubmodelValueOnlySerializer;
import org.eclipse.digitaltwin.basyx.serialization.ValueOnlySerializer;
import org.eclipse.digitaltwin.basyx.submodelservice.value.MultiLanguagePropertyValue;
import org.eclipse.digitaltwin.basyx.submodelservice.value.PropertyValue;
import org.eclipse.digitaltwin.basyx.submodelservice.value.ReferenceElementValue;
import org.eclipse.digitaltwin.basyx.submodelservice.value.SpecificAssetIdValue;
import org.eclipse.digitaltwin.basyx.submodelservice.value.SubmodelElementCollectionValue;
import org.eclipse.digitaltwin.basyx.submodelservice.value.SubmodelElementListValue;
import org.eclipse.digitaltwin.basyx.submodelservice.value.SubmodelElementValue;
import org.eclipse.digitaltwin.basyx.submodelservice.value.SubmodelValueOnly;
import org.eclipse.digitaltwin.basyx.submodelservice.value.ValueOnly;
import org.springframework.stereotype.Component;

import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.module.SimpleModule;

/**
 * SerializationExtension integrating the additional SubmodelRepository
 * serialization
 * 
 * @author schnicke, danish
 * 
 */
@Component
public class SubmodelServiceHTTPSerializationExtension implements SerializationExtension {

	@Override
	public void extend(JsonMapper.Builder builder) {
		SimpleModule module = new SimpleModule();
		module.addSerializer(MultiLanguagePropertyValue.class, new MultiLanguagePropertyValueSerializer());
		module.addDeserializer(SubmodelElementValue.class, new SubmodelElementValueJsonDeserializer());
		module.addDeserializer(ValueOnly.class, new ValueOnlyJsonDeserializer());
		module.addSerializer(ValueOnly.class, new ValueOnlySerializer());
		module.addSerializer(PropertyValue.class, new PropertyValueSerializer());
		module.addSerializer(SubmodelElementCollectionValue.class, new SubmodelElementCollectionValueSerializer());
		module.addSerializer(SubmodelElementListValue.class, new SubmodelElementListValueSerializer());
		builder.addMixIn(ReferenceElementValue.class, ReferenceElementValueMixIn.class);
		module.addSerializer(SubmodelValueOnly.class, new SubmodelValueOnlySerializer());
		module.addDeserializer(SpecificAssetIdValue.class, new SpecificAssetIdValueJsonDeserializer());
		module.addSerializer(SpecificAssetIdValue.class, new SpecificAssetIdValueSerializer());
		builder.addModule(module);
	}

}

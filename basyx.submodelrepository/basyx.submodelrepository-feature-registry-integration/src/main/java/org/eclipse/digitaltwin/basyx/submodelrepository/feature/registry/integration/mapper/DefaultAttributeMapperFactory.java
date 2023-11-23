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

package org.eclipse.digitaltwin.basyx.submodelrepository.feature.registry.integration.mapper;

import org.eclipse.digitaltwin.basyx.submodelregistry.client.model.DataTypeDefXsd;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.model.KeyTypes;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.model.ReferenceTypes;
import org.eclipse.digitaltwin.basyx.submodelrepository.feature.registry.integration.mapper.deserializer.DataTypeDefXsdDeserializer;
import org.eclipse.digitaltwin.basyx.submodelrepository.feature.registry.integration.mapper.deserializer.KeyTypeDeserializer;
import org.eclipse.digitaltwin.basyx.submodelrepository.feature.registry.integration.mapper.deserializer.ReferenceTypeDeserializer;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

/**
 * Factory for creating {@link AttributeMapper} with default {@link ObjectMapper} configurations
 * 
 * @author danish
 */
public class DefaultAttributeMapperFactory {
	
	/**
	 * Creates the {@link AttributeMapper} with default configurations of the {@link ObjectMapper}
	 * 
	 * @return default attribute mapper
	 */
	public AttributeMapper create() {
		ObjectMapper mapper = new ObjectMapper();
        
        SimpleModule keyTypesDeserModule = createDeserializerModule(KeyTypes.class, new KeyTypeDeserializer());
        
        SimpleModule refTypesDeserModule = createDeserializerModule(ReferenceTypes.class, new ReferenceTypeDeserializer());
        
        SimpleModule dataTypeDeserModule = createDeserializerModule(DataTypeDefXsd.class, new DataTypeDefXsdDeserializer());
        
        mapper.registerModules(keyTypesDeserModule, refTypesDeserModule, dataTypeDeserModule);
        
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        
        return new AttributeMapper(mapper);
	}
	
	private <T> SimpleModule createDeserializerModule(Class<T> type, JsonDeserializer<? extends T> deser) {
		SimpleModule moduleDeser = new SimpleModule();
        moduleDeser.addDeserializer(type, deser);
        
		return moduleDeser;
	}

}

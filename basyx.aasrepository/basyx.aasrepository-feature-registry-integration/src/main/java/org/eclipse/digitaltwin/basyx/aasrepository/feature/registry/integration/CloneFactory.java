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

package org.eclipse.digitaltwin.basyx.aasrepository.feature.registry.integration;

import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.model.AdministrativeInformation;
import org.eclipse.digitaltwin.aas4j.v3.model.Extension;
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.DataTypeDefXsd;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.KeyTypes;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.ReferenceTypes;
import org.eclipse.digitaltwin.basyx.aasrepository.feature.registry.integration.deserializer.DataTypeDefXsdDeserializer;
import org.eclipse.digitaltwin.basyx.aasrepository.feature.registry.integration.deserializer.KeyTypeDeserializer;
import org.eclipse.digitaltwin.basyx.aasrepository.feature.registry.integration.deserializer.ReferenceTypeDeserializer;
import org.eclipse.digitaltwin.basyx.aasrepository.feature.registry.integration.mixin.AdministrativeInformationMixin;
import org.eclipse.digitaltwin.basyx.aasrepository.feature.registry.integration.mixin.ExtensionMixin;
import org.eclipse.digitaltwin.basyx.aasrepository.feature.registry.integration.mixin.ReferenceMixin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Factory for creating clones for the defined Input and Output type
 * 
 * @param <I> the Input type
 * @param <O> the Output type
 * 
 * @author danish
 */
public class CloneFactory<I, O> {
	
	private ObjectMapper mapper = new ObjectMapper();
	
	private Class<O> elementType;
	
	public CloneFactory(Class<O> outputElementType) {
		super();
		this.elementType = outputElementType;
		
		configureMixins();
	}
	
	/**
	 * Creates clone for the provided input of {@link List} type
	 * 
	 * @param input
	 * @return the cloned result
	 */
	private void configureMixins() {
		SimpleModule module = new SimpleModule();
        module.setMixInAnnotation(AdministrativeInformation.class, AdministrativeInformationMixin.class);
        mapper.registerModule(module);
        
        SimpleModule module2 = new SimpleModule();
        module2.setMixInAnnotation(Reference.class, ReferenceMixin.class);
        mapper.registerModule(module2);
        
        SimpleModule module3 = new SimpleModule();
        module3.setMixInAnnotation(Extension.class, ExtensionMixin.class);
        mapper.registerModule(module3);
        
        SimpleModule moduleDeser = new SimpleModule();
        moduleDeser.addDeserializer(KeyTypes.class, new KeyTypeDeserializer());
        mapper.registerModule(moduleDeser);
        
        SimpleModule moduleDeser2 = new SimpleModule();
        moduleDeser2.addDeserializer(ReferenceTypes.class, new ReferenceTypeDeserializer());
        mapper.registerModule(moduleDeser2);
        
        SimpleModule moduleDeser3 = new SimpleModule();
        moduleDeser3.addDeserializer(DataTypeDefXsd.class, new DataTypeDefXsdDeserializer());
        mapper.registerModule(moduleDeser3);
        
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
	}
	
	public List<O> create(List<I> input) {
	    String serializedLangString = "";
	    
	    try {
	        serializedLangString = mapper.writeValueAsString(input);
	    } catch (JsonProcessingException e) {
	        e.printStackTrace();
	    }

	    List<O> resultList = null;

	    try {
	        resultList = mapper.readValue(serializedLangString, mapper.getTypeFactory().constructCollectionType(List.class, elementType));
	    } catch (JsonProcessingException e) {
	        e.printStackTrace();
	    }

	    return resultList;
	}
	
	/**
	 * Creates clone for the provided input
	 * 
	 * @param input
	 * @return the cloned result
	 */
	public O create(I input) {
		String serializedLangString = "";
		try {
			serializedLangString = mapper.writeValueAsString(input);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		
		try {
			return mapper.readValue(serializedLangString, elementType);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
	}
	
}

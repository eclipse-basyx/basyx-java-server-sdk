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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

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
	}
	
	/**
	 * Creates clone for the provided input of {@link List} type
	 * 
	 * @param input
	 * @return the cloned result
	 */
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

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

package org.eclipse.digitaltwin.basyx.common.mongocore;

import java.io.IOException;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.eclipse.digitaltwin.aas4j.v3.model.Identifiable;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Custom abstract mongodb mapping converter for applying mixins to the
 * metamodels for conformance to the specification.
 * 
 * @author danish
 *
 */
@Component
@ConditionalOnProperty(prefix = "basyx", name = "backend", havingValue = "MongoDB")
public class CustomIdentifiableMappingMongoConverter extends MappingMongoConverter {

	private static final String MONGODB_ID_KEY = "_id";

	private ObjectMapper mapper;

	public CustomIdentifiableMappingMongoConverter(MongoDatabaseFactory databaseFactory, MongoMappingContext mappingContext,
			ObjectMapper mapper) {
		super(new DefaultDbRefResolver(databaseFactory), mappingContext);
		this.mapper = mapper;
	}

	@Override
	public <S> S read(Class<S> clazz, Bson source) {
		Document doc = (Document) source;

		doc.remove(MONGODB_ID_KEY);

		String metamodelJson = doc.toJson();

		return deserializeMetamodel(clazz, metamodelJson);
	}

	@Override
	public void write(Object source, Bson target) {
		Document document = (Document) target;

		String id = ((Identifiable) source).getId();

		String json = serializeMetamodel(source);

		document.put(MONGODB_ID_KEY, id);
		document.putAll(Document.parse(json));
	}

	private String serializeMetamodel(Object source) {
		try {
			return mapper.writeValueAsString(source);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

	private <S> S deserializeMetamodel(Class<S> clazz, String string) {
		try {
			return mapper.readValue(string, clazz);
		} catch (IOException e) {
			throw new RuntimeException(string, e);
		}
	}

}

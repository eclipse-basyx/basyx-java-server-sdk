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

import java.util.HashMap;
import java.util.Map;

import org.springframework.data.mongodb.core.mapping.BasicMongoPersistentEntity;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.util.TypeInformation;
import org.springframework.stereotype.Component;

/**
 * Custom BaSyx Mongo Mapping Context Necessary for configuring MongoDB
 * collection names for AAS, SM and CM
 * 
 * @author mateusmolina, despen
 */
@Component
public class BasyxMongoMappingContext extends MongoMappingContext {
	private final Map<Class<?>, String> customCollectionNames = new HashMap<>();

	public BasyxMongoMappingContext() {
		super();
	}

	public void addEntityMapping(Class<?> clazz, String collectionName) {
		customCollectionNames.put(clazz, collectionName);

	}

	@Override
	protected <T> BasicMongoPersistentEntity<T> createPersistentEntity(TypeInformation<T> typeInformation) {
		return new BasicMongoPersistentEntity<T>(typeInformation) {
			@Override
			public String getCollection() {
				return customCollectionNames.getOrDefault(typeInformation.getType(), super.getCollection());
			}
		};
	}
}

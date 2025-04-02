/*******************************************************************************
 * Copyright (C) 2025 the Eclipse BaSyx Authors
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

import java.util.List;

import org.springframework.data.mongodb.core.mapping.BasicMongoPersistentEntity;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.util.TypeInformation;
import org.springframework.stereotype.Component;
import org.springframework.lang.NonNull;

/**
 * Custom BaSyx Mongo Mapping Context Necessary for configuring MongoDB
 * collection names for AAS, SM and CD
 * 
 * This component retrieves all {@link MappingEntry} defined in the Spring Context,
 * 
 * @author mateusmolina, despen
 */
@Component
public class BasyxMongoMappingContext extends MongoMappingContext {

	private final List<MappingEntry> entries;

	public BasyxMongoMappingContext(List<MappingEntry> entries) {
		this.entries = entries;
	}

	@Override
	protected @NonNull <T> BasicMongoPersistentEntity<T> createPersistentEntity(@NonNull TypeInformation<T> typeInformation) {
		return new BasicMongoPersistentEntity<T>(typeInformation) {
			@Override
			public @NonNull String getCollection() {
				return entries.stream().filter(e -> e.getEntityClass().equals(typeInformation.getType())).findFirst().map(MappingEntry::getCollectionName).orElseGet(super::getCollection);
			}
		};
	}
}

/*******************************************************************************
 * Copyright (C) 2024 the Eclipse BaSyx Authors
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

package org.eclipse.digitaltwin.basyx.conceptdescriptionrepository;

import org.eclipse.digitaltwin.aas4j.v3.model.ConceptDescription;
import org.eclipse.digitaltwin.basyx.aasrepository.backend.ConceptDescriptionBackendProvider;
import org.eclipse.digitaltwin.basyx.common.mongocore.BasyxMongoMappingContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.MongoPersistentEntity;
import org.springframework.data.mongodb.repository.support.MappingMongoEntityInformation;
import org.springframework.data.mongodb.repository.support.SimpleMongoRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

/**
 * 
 * MongoDB Backend Provider for the {@link ConceptDescription}
 * 
 * @author mateusmolina, despen, danish
 */
@ConditionalOnExpression("'${basyx.backend}'.equals('MongoDB')")
@Component
public class ConceptDescriptionMongoDBBackendProvider implements ConceptDescriptionBackendProvider {
	
	private BasyxMongoMappingContext mappingContext;
	private MongoTemplate template;
	
	@Autowired
	public ConceptDescriptionMongoDBBackendProvider(BasyxMongoMappingContext mappingContext, @Value("${basyx.cdrepository.mongodb.collectionName:cd-repo}") String collectionName, MongoTemplate template) {
		super();
		this.mappingContext = mappingContext;
		this.template = template;
		
		mappingContext.addEntityMapping(ConceptDescription.class, collectionName);
	}

	@Override
	public CrudRepository<ConceptDescription, String> getCrudRepository() {
		@SuppressWarnings("unchecked")
		MongoPersistentEntity<ConceptDescription> entity = (MongoPersistentEntity<ConceptDescription>) mappingContext.getPersistentEntity(ConceptDescription.class);
		
		return new SimpleMongoRepository<>(new MappingMongoEntityInformation<>(entity), template);
	}

}

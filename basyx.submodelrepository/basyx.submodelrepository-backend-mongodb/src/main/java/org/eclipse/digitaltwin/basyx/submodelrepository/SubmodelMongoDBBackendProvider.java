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

package org.eclipse.digitaltwin.basyx.submodelrepository;

import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.basyx.common.mongocore.BasyxMongoMappingContext;
import org.eclipse.digitaltwin.basyx.core.file.FileRepository;
import org.eclipse.digitaltwin.basyx.submodelrepository.backend.SubmodelBackendProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.MongoPersistentEntity;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.data.mongodb.repository.support.MappingMongoEntityInformation;
import org.springframework.data.mongodb.repository.support.SimpleMongoRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

/**
 * 
 * MongoDB Backend Provider for the {@link Submodel}
 * 
 * @author mateusmolina, despen, danish
 */
@ConditionalOnExpression("'${basyx.backend}'.equals('MongoDB')")
@Component
public class SubmodelMongoDBBackendProvider implements SubmodelBackendProvider {
	
	private BasyxMongoMappingContext mappingContext;
	
	private MongoTemplate template;
	
	@Autowired
	public SubmodelMongoDBBackendProvider(BasyxMongoMappingContext mappingContext, @Value("${basyx.submodelrepository.mongodb.collectionName:submodel-repo}") String collectionName, MongoTemplate template) {
		super();
		this.mappingContext = mappingContext;
		this.template = template;
		
		mappingContext.addEntityMapping(Submodel.class, collectionName);
	}

	@Override
	public CrudRepository<Submodel, String> getCrudRepository() {
		@SuppressWarnings("unchecked")
		MongoPersistentEntity<Submodel> entity = (MongoPersistentEntity<Submodel>) mappingContext.getPersistentEntity(Submodel.class);
		
		return new SimpleMongoRepository<>(new MappingMongoEntityInformation<>(entity), template);
	}

	@Override
	public FileRepository getFileRepository() {
		return new MongoDBSubmodelFileRepository(configureDefaultGridFsTemplate(template));
	}
	
	private GridFsTemplate configureDefaultGridFsTemplate(MongoTemplate mongoTemplate) {
		return new GridFsTemplate(mongoTemplate.getMongoDatabaseFactory(), mongoTemplate.getConverter());
	}

}

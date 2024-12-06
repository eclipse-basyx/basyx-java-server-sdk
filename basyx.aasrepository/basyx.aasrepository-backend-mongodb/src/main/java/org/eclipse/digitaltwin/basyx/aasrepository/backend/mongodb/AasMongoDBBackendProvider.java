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

package org.eclipse.digitaltwin.basyx.aasrepository.backend.mongodb;

import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.basyx.aasrepository.backend.AasBackendProvider;
import org.eclipse.digitaltwin.basyx.common.mongocore.BasyxMongoMappingContext;
import org.eclipse.digitaltwin.basyx.common.mongocore.MongoDBCrudRepository;
import org.eclipse.digitaltwin.basyx.core.BaSyxCrudRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.IndexOperations;
import org.springframework.data.mongodb.core.mapping.MongoPersistentEntity;
import org.springframework.data.mongodb.repository.support.MappingMongoEntityInformation;
import org.springframework.stereotype.Component;

/**
 * 
 * MongoDB Backend Provider for the AAS
 * 
 * @author mateusmolina, despen, danish
 */
@ConditionalOnExpression("'${basyx.backend}'.equals('MongoDB')")
@Component
public class AasMongoDBBackendProvider implements AasBackendProvider {

	private BasyxMongoMappingContext mappingContext;

	private MongoTemplate template;

	@Autowired
	public AasMongoDBBackendProvider(BasyxMongoMappingContext mappingContext, @Value("${basyx.aasrepository.mongodb.collectionName:aas-repo}") String collectionName, MongoTemplate template) {
		super();
		this.mappingContext = mappingContext;
		this.template = template;

		configureIndices(this.template);

		mappingContext.addEntityMapping(AssetAdministrationShell.class, collectionName);
	}

	@Override
	public BaSyxCrudRepository<AssetAdministrationShell> getCrudRepository() {
		@SuppressWarnings("unchecked")
		MongoPersistentEntity<AssetAdministrationShell> entity = (MongoPersistentEntity<AssetAdministrationShell>) mappingContext.getPersistentEntity(AssetAdministrationShell.class);

		return new MongoDBCrudRepository<AssetAdministrationShell>(new MappingMongoEntityInformation<>(entity), template, AssetAdministrationShell.class, new AasMongoDBFilterResolution());
	}
	
	private void configureIndices(MongoTemplate template) {
		IndexOperations ops = template.indexOps(AssetAdministrationShell.class);

		ops.ensureIndex(new Index(AasMongoDBFilterResolution.ASSET_KIND, Direction.ASC));
		ops.ensureIndex(new Index(AasMongoDBFilterResolution.ASSET_TYPE, Direction.ASC));
		ops.ensureIndex(new Index(AasMongoDBFilterResolution.IDENTIFIER, Direction.ASC));

		ops.ensureIndex(new Index(AasMongoDBFilterResolution.ID_SHORT, Direction.ASC));

		ops.ensureIndex(new Index().on(AasMongoDBFilterResolution.SPECIFIC_ASSET_ID_NAME, Direction.ASC).on(AasMongoDBFilterResolution.SPECIFIC_ASSET_ID_VALUE, Direction.ASC));
	}

}

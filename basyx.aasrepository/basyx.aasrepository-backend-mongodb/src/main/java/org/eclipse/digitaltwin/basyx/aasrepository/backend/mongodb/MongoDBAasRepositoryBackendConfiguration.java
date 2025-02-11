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

package org.eclipse.digitaltwin.basyx.aasrepository.backend.mongodb;

import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.basyx.aasrepository.AasRepository;
import org.eclipse.digitaltwin.basyx.aasservice.AasServiceOperations;
import org.eclipse.digitaltwin.basyx.aasservice.backend.MongoDBAasServiceOperations;
import org.eclipse.digitaltwin.basyx.common.mongocore.BasyxMongoMappingContext;
import org.eclipse.digitaltwin.basyx.core.filerepository.FileRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.mapping.MongoPersistentEntity;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.data.mongodb.repository.support.MappingMongoEntityInformation;

/**
 * 
 * Provides the MongoDB configuration for the {@link AasRepository}
 * 
 * @author schnicke, mateusmolina
 *
 */
@Configuration
@ConditionalOnExpression("'${basyx.backend}'.equals('MongoDB')")
@EnableMongoRepositories(basePackages = "org.eclipse.digitaltwin.basyx.aasrepository.backend")
public class MongoDBAasRepositoryBackendConfiguration {
	@Bean
	MappingMongoEntityInformation<AssetAdministrationShell, String> mappingMongoEntityInformation(BasyxMongoMappingContext mappingContext, @Value("${basyx.aasrepository.mongodb.collectionName:aas-repo}") String collectionName) {
		mappingContext.addEntityMapping(AssetAdministrationShell.class, collectionName);

		@SuppressWarnings("unchecked")
		MongoPersistentEntity<AssetAdministrationShell> entity = (MongoPersistentEntity<AssetAdministrationShell>) mappingContext.getPersistentEntity(AssetAdministrationShell.class);
		return new MappingMongoEntityInformation<>(entity);
	}

	@Bean
	@DependsOn("mappingMongoEntityInformation")
	AasServiceOperations aasServiceOperations(MongoOperations template, FileRepository fileRepository) {
		return new MongoDBAasServiceOperations(template, fileRepository);
	}
}

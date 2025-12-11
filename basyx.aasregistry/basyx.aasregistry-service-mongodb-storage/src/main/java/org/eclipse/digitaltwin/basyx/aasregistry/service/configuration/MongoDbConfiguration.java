/*******************************************************************************
 * Copyright (C) 2023 DFKI GmbH (https://www.dfki.de/en/web)
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
package org.eclipse.digitaltwin.basyx.aasregistry.service.configuration;

import java.util.List;

import org.eclipse.digitaltwin.basyx.aasregistry.model.AssetAdministrationShellDescriptor;
import org.eclipse.digitaltwin.basyx.aasregistry.paths.AasRegistryPaths;
import org.eclipse.digitaltwin.basyx.aasregistry.service.storage.AasRegistryStorage;
import org.eclipse.digitaltwin.basyx.aasregistry.service.storage.AasRegistryStorageFeature;
import org.eclipse.digitaltwin.basyx.aasregistry.service.storage.CursorEncodingRegistryStorage;
import org.eclipse.digitaltwin.basyx.aasregistry.service.storage.mongodb.MongoDbAasRegistryStorage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.IndexOperations;
import org.springframework.scheduling.annotation.EnableAsync;

import lombok.extern.log4j.Log4j2;

@Configuration
@ConditionalOnProperty(prefix = "registry", name = "type", havingValue = "mongodb")
@EnableAsync
@Log4j2
public class MongoDbConfiguration {

	@Value("${basyx.aasregistry.mongodb.collectionName:assetAdministrationShellDescriptor}")
	private String collectionName;
	
	@Bean
	public AasRegistryStorage createStorage(MongoTemplate template, List<AasRegistryStorageFeature> features) {
		log.info("Creating mongodb storage");
		log.info("Creating mongodb indices");
		initializeIndices(template);
		AasRegistryStorage storage = new CursorEncodingRegistryStorage(new MongoDbAasRegistryStorage(template, collectionName));
		return applyFeatures(storage, features);

	}
	
	private AasRegistryStorage applyFeatures(AasRegistryStorage storage, List<AasRegistryStorageFeature> features) {
		for (AasRegistryStorageFeature eachFeature : features) {
			log.info("Activating feature " + eachFeature.getName());
			storage = eachFeature.decorate(storage);
		}
		return storage;
	}

	private void initializeIndices(MongoTemplate template) {
		IndexOperations ops = template.indexOps(collectionName);
		initializeGetShellDescriptorsIndices(ops);
		initializeExtensionIndices(ops);
	}

	private void initializeGetShellDescriptorsIndices(IndexOperations ops) {
		initializeSingleAscIndex(ops, AasRegistryPaths.assetKind());
		initializeSingleAscIndex(ops, AasRegistryPaths.assetType());
	}

	private void initializeExtensionIndices(IndexOperations ops) {
		initializeShellExtensionIndices(ops);
		initializeSubmodelExtensionIndices(ops);
	}

	private void initializeShellExtensionIndices(IndexOperations ops) {
		initializeSingleAscIndex(ops, AasRegistryPaths.extensions().name());
		initializeSingleAscIndex(ops, AasRegistryPaths.extensions().value());
	}

	private void initializeSubmodelExtensionIndices(IndexOperations ops) {
		initializeSingleAscIndex(ops, AasRegistryPaths.submodelDescriptors().extensions().name());
		initializeSingleAscIndex(ops, AasRegistryPaths.submodelDescriptors().extensions().value());
	}

	private void initializeSingleAscIndex(IndexOperations ops, String path) {
		Index smValueIndex = new Index(path, Direction.ASC);
		ops.ensureIndex(smValueIndex);
	}

	@Bean
	public MongoTransactionManager mongoDbTransactionManager(MongoDatabaseFactory dbFactory) {
		return new MongoTransactionManager(dbFactory);
	}
}
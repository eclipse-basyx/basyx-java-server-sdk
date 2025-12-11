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
package org.eclipse.digitaltwin.basyx.submodelregistry.service.configuration;

import lombok.extern.log4j.Log4j2;
import org.eclipse.digitaltwin.basyx.submodelregistry.service.storage.CursorEncodingRegistryStorage;
import org.eclipse.digitaltwin.basyx.submodelregistry.service.storage.SubmodelRegistryStorage;
import org.eclipse.digitaltwin.basyx.submodelregistry.service.storage.SubmodelRegistryStorageFeature;
import org.eclipse.digitaltwin.basyx.submodelregistry.service.storage.mongodb.MongoDbSubmodelRegistryStorage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.List;

@Configuration
@ConditionalOnProperty(prefix = "registry", name = "type", havingValue = "mongodb")
@EnableAsync
@Log4j2
public class MongoDbConfiguration {


	@Value("${basyx.submodelregistry.mongodb.collectionName:submodelDescriptor}")
	public String collectionName;
	
	@Bean
	public SubmodelRegistryStorage createSubmodelRegistryStorage(MongoTemplate template, List<SubmodelRegistryStorageFeature> features) {
		log.info("Creating mongodb storage");

		SubmodelRegistryStorage storage = new CursorEncodingRegistryStorage(new MongoDbSubmodelRegistryStorage(template, collectionName));

		return applyFeatures(storage, features);
	}

	@Bean
	public MongoTransactionManager transactionManager(MongoDatabaseFactory dbFactory) {
		return new MongoTransactionManager(dbFactory);
	}

	private SubmodelRegistryStorage applyFeatures(SubmodelRegistryStorage storage, List<SubmodelRegistryStorageFeature> features) {
		for (SubmodelRegistryStorageFeature eachFeature : features) {
			log.info("Activating feature " + eachFeature.getName());
			storage = eachFeature.decorate(storage);
		}

		return storage;
	}

}

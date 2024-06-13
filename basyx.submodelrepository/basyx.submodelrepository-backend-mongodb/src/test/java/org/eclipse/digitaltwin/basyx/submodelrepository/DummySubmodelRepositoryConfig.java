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

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.eclipse.digitaltwin.basyx.common.mongocore.BasyxMongoMappingContext;
import org.eclipse.digitaltwin.basyx.core.filerepository.MongoDBFileRepository;
import org.eclipse.digitaltwin.basyx.submodelrepository.backend.SimpleSubmodelRepositoryFactory;
import org.eclipse.digitaltwin.basyx.submodelservice.InMemorySubmodelServiceFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;

/**
 * Configuration for tests
 * 
 * @author mateusmolina, danish
 *
 */
@Configuration
public class DummySubmodelRepositoryConfig {

	public final static String COLLECTION = "submodelRepositoryPersistencyTestCollection";
	public final static String DB = "BaSyxTestDb";

	@Bean
	public SubmodelRepository createSubmodelRepository(MongoTemplate template) {
		return new SimpleSubmodelRepositoryFactory(new SubmodelMongoDBBackendProvider(new BasyxMongoMappingContext(), COLLECTION, template), new InMemorySubmodelServiceFactory(new MongoDBFileRepository(configureDefaultGridFsTemplate(template)))).create();
	}

	@Bean
	public MongoTemplate createSMMongoTemplate() {
		String connectionURL = "mongodb://mongoAdmin:mongoPassword@localhost:27017/";

		MongoClient client = MongoClients.create(connectionURL);

		return new MongoTemplate(client, DB);
	}

	private GridFsTemplate configureDefaultGridFsTemplate(MongoTemplate mongoTemplate) {
		return new GridFsTemplate(mongoTemplate.getMongoDatabaseFactory(), mongoTemplate.getConverter());
	}

}
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
package org.eclipse.digitaltwin.basyx.submodelrepository;

import java.util.Collection;

import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.basyx.submodelservice.SubmodelServiceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Component;

/**
 * SubmodelRepository factory returning a MongoDb backend SubmodelRepository
 * 
 * @author jungjan, zhangzai
 *
 */
@Component
@ConditionalOnExpression("'${basyx.backend}'.equals('MongoDB')")
public class MongoDBSubmodelRepositoryFactory implements SubmodelRepositoryFactory {

	private MongoTemplate mongoTemplate;
	private String collectionName;
	private SubmodelServiceFactory submodelServiceFactory;
	private Collection<Submodel> submodels;

	private String smRepositoryName;
	private GridFsTemplate gridFsTemplate;

	@Autowired(required = false)
	public MongoDBSubmodelRepositoryFactory(MongoTemplate mongoTemplate, @Value("${basyx.submodelrepository.mongodb.collectionName:submodel-repo}") String collectionName, SubmodelServiceFactory submodelServiceFactory) {
		this.mongoTemplate = mongoTemplate;
		this.collectionName = collectionName;
		this.submodelServiceFactory = submodelServiceFactory;
	}
	
	@Autowired(required = false)
	public MongoDBSubmodelRepositoryFactory(MongoTemplate mongoTemplate, @Value("${basyx.submodelrepository.mongodb.collectionName:submodel-repo}") String collectionName, SubmodelServiceFactory submodelServiceFactory,
			GridFsTemplate gridFsTemplate) {
		this(mongoTemplate, collectionName, submodelServiceFactory);
		this.gridFsTemplate = gridFsTemplate;
	}
	
	@Autowired(required = false)
	public MongoDBSubmodelRepositoryFactory(MongoTemplate mongoTemplate, @Value("${basyx.submodelrepository.mongodb.collectionName:submodel-repo}") String collectionName, SubmodelServiceFactory submodelServiceFactory, 
			GridFsTemplate gridFsTemplate, @Value("${basyx.smrepo.name:sm-repo}") String smRepositoryName) {
		this(mongoTemplate, collectionName, submodelServiceFactory, gridFsTemplate);
		this.smRepositoryName = smRepositoryName;
	}

	@Autowired(required = false)
	public MongoDBSubmodelRepositoryFactory(MongoTemplate mongoTemplate, @Value("${basyx.submodelrepository.mongodb.collectionName:submodel-repo}") String collectionName, SubmodelServiceFactory submodelServiceFactory,
			Collection<Submodel> submodels) {
		this(mongoTemplate, collectionName, submodelServiceFactory);
		this.submodels = submodels;
	}

	@Autowired(required = false)
	public MongoDBSubmodelRepositoryFactory(MongoTemplate mongoTemplate, @Value("${basyx.submodelrepository.mongodb.collectionName:submodel-repo}") String collectionName, SubmodelServiceFactory submodelServiceFactory,
			Collection<Submodel> submodels, @Value("${basyx.smrepo.name:sm-repo}") String smRepositoryName, GridFsTemplate gridFsTemplate) {
		this(mongoTemplate, collectionName, submodelServiceFactory, submodels);
		this.smRepositoryName = smRepositoryName;
		this.gridFsTemplate = gridFsTemplate;
	}

	@Override
	public SubmodelRepository create() {
		if (this.submodels == null || this.submodels.isEmpty()) {
			return new MongoDBSubmodelRepository(mongoTemplate, collectionName, submodelServiceFactory, smRepositoryName, gridFsTemplate);
		}
		return new MongoDBSubmodelRepository(mongoTemplate, collectionName, submodelServiceFactory, submodels, smRepositoryName, gridFsTemplate);

	}
}

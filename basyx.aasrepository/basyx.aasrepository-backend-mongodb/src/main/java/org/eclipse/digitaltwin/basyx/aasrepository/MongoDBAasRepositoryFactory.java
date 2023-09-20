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

package org.eclipse.digitaltwin.basyx.aasrepository;

import org.eclipse.digitaltwin.basyx.aasservice.AasServiceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

/**
 * AasRepository factory returning a MongoDb backend AasRepository
 * 
 * @author schnicke
 */
@Component
@ConditionalOnExpression("'${basyx.backend}'.equals('MongoDB')")
public class MongoDBAasRepositoryFactory implements AasRepositoryFactory {
	private MongoTemplate mongoTemplate;
	private String collectionName;
	private AasServiceFactory aasServiceFactory;
	
	private String aasRepositoryName;

	@Autowired(required = false)
	public MongoDBAasRepositoryFactory(MongoTemplate mongoTemplate,
			@Value("${basyx.aasrepository.mongodb.collectionName:aas-repo}") String collectionName,
			AasServiceFactory aasServiceFactory) {
		this.mongoTemplate = mongoTemplate;
		this.collectionName = collectionName;
		this.aasServiceFactory = aasServiceFactory;
	}
	
	@Autowired(required = false)
	public MongoDBAasRepositoryFactory(MongoTemplate mongoTemplate,
			@Value("${basyx.aasrepository.mongodb.collectionName:aas-repo}") String collectionName,
			AasServiceFactory aasServiceFactory, @Value("${basyx.aasrepo.name:aas-repo}") String aasRepositoryName) {
		this(mongoTemplate, collectionName, aasServiceFactory);
		this.aasRepositoryName = aasRepositoryName;
	}

	@Override
	public AasRepository create() {
		return new MongoDBAasRepository(mongoTemplate, collectionName, aasServiceFactory, aasRepositoryName);
	}
}

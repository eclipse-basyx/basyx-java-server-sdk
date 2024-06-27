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

package org.eclipse.digitaltwin.basyx.aasxfileserver;

import org.eclipse.digitaltwin.basyx.aasxfileserver.backend.AASXFileServerBackendProvider;
import org.eclipse.digitaltwin.basyx.aasxfileserver.model.Package;
import org.eclipse.digitaltwin.basyx.common.mongocore.BasyxMongoMappingContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.MongoPersistentEntity;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.cdi.MongoRepositoryExtension;
import org.springframework.data.mongodb.repository.support.MappingMongoEntityInformation;
import org.springframework.data.mongodb.repository.support.SimpleMongoRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

@Service
public class AASXFileServerMongoDBBackendProvider implements AASXFileServerBackendProvider {

    private BasyxMongoMappingContext mappingContext;

    private GridFsTemplate template;

    @Autowired
    public AASXFileServerMongoDBBackendProvider(BasyxMongoMappingContext mappingContext,
                                              @Value("${basyx.aasxfileserver.mongodb.collectionName:aasdiscovery-service}") String collectionName,
                                              MongoTemplate template) {
        super();
        this.mappingContext = mappingContext;
        this.template = gridFsTemplate(template);

        mappingContext.addEntityMapping(Package.class, collectionName);
    }

    public GridFsTemplate gridFsTemplate(MongoTemplate mongoTemplate) {
        return new GridFsTemplate(mongoTemplate.getMongoDatabaseFactory(), mongoTemplate.getConverter());
    }


   @Override
    public CrudRepository<Package, String> getCrudRepository() {
        @SuppressWarnings("unchecked")
        MongoPersistentEntity<Package> entity = (MongoPersistentEntity<Package>) mappingContext
                .getPersistentEntity(Package.class);
       
        return new SimpleMongoRepository<>(new MappingMongoEntityInformation<>(entity), template);
    }
}

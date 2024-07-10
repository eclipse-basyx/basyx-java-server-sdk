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
import com.mongodb.client.*;
import com.mongodb.client.gridfs.*;
import com.mongodb.client.gridfs.model.*;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.eclipse.digitaltwin.aas4j.v3.model.PackageDescription;
import org.eclipse.digitaltwin.basyx.aasxfileserver.model.Package;
import org.eclipse.digitaltwin.basyx.aasxfileserver.model.PackagesBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Implementation of {@link CrudRepository} for MongoDB Backend
 *
 * @author zielstor, fried
 */
public class AASXFileServerMongoDBCrudRepository implements CrudRepository<Package, String> {

	private final MongoTemplate mongoTemplate;
	private final String collectionName;
	private final GridFSBucket gridFSBucket;

	public AASXFileServerMongoDBCrudRepository(MongoTemplate mongoTemplate, String collectionName, String gridfsBucketName) {
		this.mongoTemplate = mongoTemplate;
		this.collectionName = collectionName;
		this.gridFSBucket = GridFSBuckets.create(mongoTemplate.getDb(), gridfsBucketName);
	}

	@Override
	public <S extends Package> S save(S entity) {
		if (entity.getPackagesBody().getFile() != null) {
			GridFSUploadOptions options = new GridFSUploadOptions();
			ObjectId fileId = gridFSBucket.uploadFromStream(entity.getPackagesBody().getFileName(), entity.getPackagesBody().getFile(), options);
			entity.getPackagesBody().setFile(null);
			entity.getPackagesBody().setPackageId(fileId.toString());
		}
		mongoTemplate.save(entity, collectionName);
		return entity;
	}

	@Override
	public <S extends Package> Iterable<S> saveAll(Iterable<S> entities) {
		List<S> result = new ArrayList<>();
		for (S entity : entities) {
			result.add(save(entity));
		}
		return result;
	}

	@Override
	public Optional<Package> findById(String id) {
		Package pkg = mongoTemplate.findById(id, Package.class, collectionName);
		if (pkg != null && pkg.getPackagesBody().getPackageId() != null) {
			InputStream file = gridFSBucket.openDownloadStream(new ObjectId(pkg.getPackagesBody().getPackageId()));
			pkg.getPackagesBody().setFile(file);
		}
		return Optional.ofNullable(pkg);
	}

	@Override
	public boolean existsById(String id) {
		Query query = new Query(Criteria.where("packageId").is(id));
		return mongoTemplate.exists(query, Package.class, collectionName);
	}

	@Override
	public Iterable<Package> findAll() {
		return mongoTemplate.findAll(Package.class, collectionName);
	}

	@Override
	public Iterable<Package> findAllById(Iterable<String> ids) {
		Query query = new Query(Criteria.where("packageId").in(ids));
		return mongoTemplate.find(query, Package.class, collectionName);
	}

	@Override
	public long count() {
		return mongoTemplate.count(new Query(), Package.class, collectionName);
	}

	@Override
	public void deleteById(String id) {
		Package pkg = mongoTemplate.findAndRemove(new Query(Criteria.where("packageId").is(id)), Package.class, collectionName);
		if (pkg != null && pkg.getPackagesBody().getPackageId() != null) {
			gridFSBucket.delete(new ObjectId(pkg.getPackagesBody().getPackageId()));
		}
	}

	@Override
	public void delete(Package entity) {
		deleteById(entity.getPackageId());
	}

	@Override
	public void deleteAllById(Iterable<? extends String> ids) {
		for (String id : ids) {
			deleteById(id);
		}
	}

	@Override
	public void deleteAll(Iterable<? extends Package> entities) {
		for (Package entity : entities) {
			deleteById(entity.getPackageId());
		}
	}

	@Override
	public void deleteAll() {
		mongoTemplate.dropCollection(collectionName);
		gridFSBucket.drop();
	}
}

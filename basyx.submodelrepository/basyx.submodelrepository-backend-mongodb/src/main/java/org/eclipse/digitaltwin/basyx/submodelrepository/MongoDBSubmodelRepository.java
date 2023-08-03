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
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.basyx.core.exceptions.CollidingIdentifierException;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.exceptions.IdentificationMismatchException;
import org.eclipse.digitaltwin.basyx.submodelservice.SubmodelService;
import org.eclipse.digitaltwin.basyx.submodelservice.SubmodelServiceFactory;
import org.eclipse.digitaltwin.basyx.submodelservice.value.SubmodelElementValue;
import org.eclipse.digitaltwin.basyx.submodelservice.value.SubmodelValueOnly;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.mongodb.client.result.DeleteResult;

/**
 * MongoDB implementation of the SubmodelRepository
 * 
 * @author jungjan
 *
 */
public class MongoDBSubmodelRepository implements SubmodelRepository {
	private static String ID_JSON_PATH = "id";

	private MongoTemplate mongoTemplate;
	private String collectionName;
	private SubmodelServiceFactory submodelServiceFactory;

	/**
	 * Creates the MongoDBSubmodelRepository utilizing the passed
	 * SubmodelServiceFactory for creating new SubmodelServices and uses a
	 * collectionName and a mongoTemplate for operating MongoDB
	 * 
	 * @param mongoTemplate
	 * @param collectionName
	 * @param submodelServiceFactory
	 */
	public MongoDBSubmodelRepository(MongoTemplate mongoTemplate, String collectionName, SubmodelServiceFactory submodelServiceFactory) {
		this.mongoTemplate = mongoTemplate;
		this.collectionName = collectionName;
		this.submodelServiceFactory = submodelServiceFactory;
		configureIndexForSubmodelId(mongoTemplate);
	}

	/**
	 * Creates the MongoDBSubmodelRepository utilizing the passed
	 * SubmodelServiceFactory for creating new SubmodelServices and uses a
	 * collectionName and a mongoTemplate for operating MongoDB. Additionally
	 * initializes the MongoDB collection with a collection of submodels.
	 * 
	 * @param submodelServiceFactory
	 * @param submodels
	 */
	public MongoDBSubmodelRepository(MongoTemplate mongoTemplate, String collectionName, SubmodelServiceFactory submodelServiceFactory, Collection<Submodel> submodels) {
		this(mongoTemplate, collectionName, submodelServiceFactory);
		initializeRemoteCollection(submodels);
	}

	private void initializeRemoteCollection(Collection<Submodel> submodels) {
		if (submodels == null || submodels.isEmpty()) {
			return;
		}
		submodels.forEach(this::createSubmodel);
	}

	private void configureIndexForSubmodelId(MongoTemplate mongoTemplate) {
		Index idIndex = new Index().on(ID_JSON_PATH, Direction.ASC);
		mongoTemplate.indexOps(Submodel.class)
				.ensureIndex(idIndex);
	}

	@Override
	public Collection<Submodel> getAllSubmodels() {
		return mongoTemplate.findAll(Submodel.class, collectionName);
	}

	@Override
	public Submodel getSubmodel(String submodelId) throws ElementDoesNotExistException {
		Submodel submodel = mongoTemplate.findOne(new Query().addCriteria(Criteria.where(ID_JSON_PATH)
				.is(submodelId)), Submodel.class, collectionName);
		if (submodel == null) {
			throw new ElementDoesNotExistException(submodelId);
		}
		return submodel;
	}

	@Override
	public void updateSubmodel(String submodelId, Submodel submodel) throws ElementDoesNotExistException {
		Query query = new Query().addCriteria(Criteria.where(ID_JSON_PATH)
				.is(submodelId));

		throwIfSubmodelDoesNotExist(query, submodelId);
		throwIfMismatchingIds(submodelId, submodel);

		mongoTemplate.remove(query, Submodel.class, collectionName);
		mongoTemplate.save(submodel, collectionName);
	}

	private void throwIfSubmodelDoesNotExist(Query query, String submodelId) {
		if (!mongoTemplate.exists(query, Submodel.class, collectionName))
			throw new ElementDoesNotExistException(submodelId);

	}

	private void throwIfMismatchingIds(String submodelId, Submodel submodel) {
		String newSubmodelId = submodel.getId();

		if (!submodelId.equals(newSubmodelId))
			throw new IdentificationMismatchException();

	}

	@Override
	public void createSubmodel(Submodel submodel) throws CollidingIdentifierException {
		throwIfCollidesWithRemoteId(submodel);
		mongoTemplate.save(submodel, collectionName);
	}

	private void throwIfCollidesWithRemoteId(Submodel submodel) {
		if (mongoTemplate.exists(new Query().addCriteria(Criteria.where(ID_JSON_PATH)
				.is(submodel.getId())), Submodel.class, collectionName)) {
			throw new CollidingIdentifierException(submodel.getId());
		}
	}

	private SubmodelService getSubmodelService(String submodelId) {
		return submodelServiceFactory.create(getSubmodel(submodelId));
	}

	@Override
	public Collection<SubmodelElement> getSubmodelElements(String submodelId) throws ElementDoesNotExistException {
		return getSubmodelService(submodelId).getSubmodelElements();
	}

	@Override
	public SubmodelElement getSubmodelElement(String submodelId, String submodelElementIdShort) throws ElementDoesNotExistException {
		return getSubmodelService(submodelId).getSubmodelElement(submodelElementIdShort);
	}

	@Override
	public SubmodelElementValue getSubmodelElementValue(String submodelId, String submodelElementIdShort) throws ElementDoesNotExistException {
		return getSubmodelService(submodelId).getSubmodelElementValue(submodelElementIdShort);
	}

	@Override
	public void setSubmodelElementValue(String submodelId, String submodelElementIdShort, SubmodelElementValue value) throws ElementDoesNotExistException {
		SubmodelService submodelService = getSubmodelService(submodelId);
		submodelService.setSubmodelElementValue(submodelElementIdShort, value);

		updateSubmodel(submodelId, submodelService.getSubmodel());
	}

	@Override
	public void deleteSubmodel(String submodelId) throws ElementDoesNotExistException {
		DeleteResult result = mongoTemplate.remove(new Query().addCriteria(Criteria.where(ID_JSON_PATH)
				.is(submodelId)), Submodel.class, collectionName);

		if (result.getDeletedCount() == 0) {
			throw new ElementDoesNotExistException(submodelId);
		}

	}

	@Override
	public void createSubmodelElement(String submodelId, SubmodelElement submodelElement) {
		SubmodelService submodelService = getSubmodelService(submodelId);
		submodelService.createSubmodelElement(submodelElement);

		updateSubmodel(submodelId, submodelService.getSubmodel());
	}

	@Override
	public void createSubmodelElement(String submodelId, String idShortPath, SubmodelElement submodelElement) throws ElementDoesNotExistException {
		SubmodelService submodelService = getSubmodelService(submodelId);
		submodelService.createSubmodelElement(idShortPath, submodelElement);

		updateSubmodel(submodelId, submodelService.getSubmodel());
	}

	@Override
	public void deleteSubmodelElement(String submodelId, String idShortPath) throws ElementDoesNotExistException {
		SubmodelService submodelService = getSubmodelService(submodelId);
		submodelService.deleteSubmodelElement(idShortPath);

		updateSubmodel(submodelId, submodelService.getSubmodel());
	}

	@Override
	public SubmodelValueOnly getSubmodelByIdValueOnly(String submodelId) throws ElementDoesNotExistException {
		return new SubmodelValueOnly(getSubmodelElements(submodelId));
	}

	@Override
	public Submodel getSubmodelByIdMetadata(String submodelId) throws ElementDoesNotExistException {
		Submodel submodel = getSubmodel(submodelId);
		submodel.setSubmodelElements(null);
		return submodel;
	}

}

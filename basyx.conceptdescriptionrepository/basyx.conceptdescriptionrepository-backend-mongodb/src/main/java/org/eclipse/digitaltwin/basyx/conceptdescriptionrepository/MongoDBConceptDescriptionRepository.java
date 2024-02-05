/*******************************************************************************
 * Copyright (C) 2023 the Eclipse BaSyx Authors
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 * 
 * SPDX-License-Identifier: MIT
 ******************************************************************************/
package org.eclipse.digitaltwin.basyx.conceptdescriptionrepository;

import java.util.List;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.eclipse.digitaltwin.aas4j.v3.model.ConceptDescription;
import org.eclipse.digitaltwin.aas4j.v3.model.EmbeddedDataSpecification;
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.basyx.core.exceptions.CollidingIdentifierException;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.exceptions.IdentificationMismatchException;
import org.eclipse.digitaltwin.basyx.core.exceptions.MissingIdentifierException;
import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationSupport;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.mongodb.client.result.DeleteResult;

/**
 * 
 * MongoDB implementation of the ConceptDescriptionRepository
 *
 * @author danish, kammognie
 *
 */
public class MongoDBConceptDescriptionRepository implements ConceptDescriptionRepository {
	
	private static final String IDJSONPATH = "id";

	private MongoTemplate mongoTemplate;
	private String collectionName;
	private String cdRepositoryName;

	public MongoDBConceptDescriptionRepository(MongoTemplate mongoTemplate, String collectionName) {
		this.mongoTemplate = mongoTemplate;
		this.collectionName = collectionName;
	}

	public MongoDBConceptDescriptionRepository(MongoTemplate mongoTemplate, String collectionName, String cdRepositoryName) {
		this(mongoTemplate, collectionName);
		this.cdRepositoryName = cdRepositoryName;
	}

	@Override
	public CursorResult<List<ConceptDescription>> getAllConceptDescriptions(PaginationInfo pInfo) {
		List<ConceptDescription> cdList = mongoTemplate.findAll(ConceptDescription.class, collectionName);
		CursorResult<List<ConceptDescription>> paginatedCD = paginateList(pInfo, cdList);
		return paginatedCD;
	}

	@Override
	public CursorResult<List<ConceptDescription>> getAllConceptDescriptionsByIdShort(String idShort, PaginationInfo pInfo) {
		List<ConceptDescription> allDescriptions = mongoTemplate.findAll(ConceptDescription.class, collectionName);

		List<ConceptDescription> filtered = allDescriptions.stream().filter(conceptDescription -> conceptDescription.getIdShort().equals(idShort)).collect(Collectors.toList());
		CursorResult<List<ConceptDescription>> result = paginateList(pInfo, filtered);
		return result;
	}

	@Override
	public CursorResult<List<ConceptDescription>> getAllConceptDescriptionsByIsCaseOf(Reference reference, PaginationInfo pInfo) {
		List<ConceptDescription> allDescriptions = mongoTemplate.findAll(ConceptDescription.class, collectionName);
		List<ConceptDescription> filtered = allDescriptions.stream().filter(conceptDescription -> hasMatchingReference(conceptDescription, reference)).collect(Collectors.toList());

		CursorResult<List<ConceptDescription>> result = paginateList(pInfo, filtered);
		return result;
	}

	@Override
	public CursorResult<List<ConceptDescription>> getAllConceptDescriptionsByDataSpecificationReference(Reference reference, PaginationInfo pInfo) {
		List<ConceptDescription> allDescriptions = mongoTemplate.findAll(ConceptDescription.class, collectionName);

		List<ConceptDescription> filtered = allDescriptions.stream().filter(conceptDescription -> hasMatchingDataSpecificationReference(conceptDescription, reference)).collect(Collectors.toList());

		CursorResult<List<ConceptDescription>> result = paginateList(pInfo, filtered);
		return result;
	}

	@Override
	public ConceptDescription getConceptDescription(String conceptDescriptionId) throws ElementDoesNotExistException {
		ConceptDescription conceptDescription = mongoTemplate.findOne(new Query().addCriteria(Criteria.where(IDJSONPATH).is(conceptDescriptionId)), ConceptDescription.class, collectionName);

		if (conceptDescription == null)
			throw new ElementDoesNotExistException(conceptDescriptionId);

		return conceptDescription;
	}

	@Override
	public void updateConceptDescription(String conceptDescriptionId, ConceptDescription conceptDescription) throws ElementDoesNotExistException {

		Query query = new Query().addCriteria(Criteria.where(IDJSONPATH).is(conceptDescriptionId));

		throwIfConceptDescriptionDoesNotExist(query, conceptDescriptionId);

		throwIfMismatchingIds(conceptDescriptionId, conceptDescription);

		mongoTemplate.remove(query, ConceptDescription.class, collectionName);
		mongoTemplate.save(conceptDescription, collectionName);
	}

	@Override
	public void createConceptDescription(ConceptDescription conceptDescription) throws CollidingIdentifierException, MissingIdentifierException {
		throwIfConceptDescriptionIdEmptyOrNull(conceptDescription.getId());

		throwIfCollidesWithExistingId(conceptDescription);

		mongoTemplate.save(conceptDescription, collectionName);
	}

	@Override
	public void deleteConceptDescription(String conceptDescriptionId) throws ElementDoesNotExistException {
		Query query = new Query().addCriteria(Criteria.where(IDJSONPATH).is(conceptDescriptionId));

		DeleteResult result = mongoTemplate.remove(query, ConceptDescription.class, collectionName);

		if (result.getDeletedCount() == 0)
			throw new ElementDoesNotExistException(conceptDescriptionId);
	}

	@Override
	public String getName() {
		return cdRepositoryName == null ? ConceptDescriptionRepository.super.getName() : cdRepositoryName;
	}

	private void throwIfConceptDescriptionIdEmptyOrNull(String id) {
		if (id == null || id.isBlank())
			throw new MissingIdentifierException(id);
	}

	private void throwIfCollidesWithExistingId(ConceptDescription conceptDescription) {
		Query query = new Query().addCriteria(Criteria.where(IDJSONPATH).is(conceptDescription.getId()));

		if (mongoTemplate.exists(query, ConceptDescription.class, collectionName))
			throw new CollidingIdentifierException(conceptDescription.getId());
	}

	private boolean hasMatchingReference(ConceptDescription cd, Reference reference) {
		Optional<Reference> optionalReference = cd.getIsCaseOf().stream().filter(ref -> ref.equals(reference)).findAny();

		return optionalReference.isPresent();
	}

	private boolean hasMatchingDataSpecificationReference(ConceptDescription cd, Reference reference) {
		Optional<EmbeddedDataSpecification> optionalReference = cd.getEmbeddedDataSpecifications().stream().filter(eds -> eds.getDataSpecification().equals(reference)).findAny();

		return optionalReference.isPresent();
	}

	private void throwIfConceptDescriptionDoesNotExist(Query query, String conceptDescriptionId) {
		if (!mongoTemplate.exists(query, ConceptDescription.class, collectionName))
			throw new ElementDoesNotExistException(conceptDescriptionId);
	}

	private void throwIfMismatchingIds(String conceptDescriptionId, ConceptDescription newConceptDescription) {
		String newConceptDescriptionId = newConceptDescription.getId();

		if (!conceptDescriptionId.equals(newConceptDescriptionId))
			throw new IdentificationMismatchException();
	}

	private CursorResult<List<ConceptDescription>> paginateList(PaginationInfo pInfo, List<ConceptDescription> cdList) {
		TreeMap<String, ConceptDescription> cdMap = cdList.stream().collect(Collectors.toMap(ConceptDescription::getId, aas -> aas, (a, b) -> a, TreeMap::new));

		PaginationSupport<ConceptDescription> paginationSupport = new PaginationSupport<>(cdMap, ConceptDescription::getId);
		CursorResult<List<ConceptDescription>> paginatedCD = paginationSupport.getPaged(pInfo);
		return paginatedCD;
	}

}

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
package org.eclipse.digitaltwin.basyx.aasrepository;

import java.util.List;
import java.util.function.Function;

import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetInformation;
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.basyx.aasservice.AasService;
import org.eclipse.digitaltwin.basyx.aasservice.AasServiceFactory;
import org.eclipse.digitaltwin.basyx.core.exceptions.CollidingIdentifierException;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.exceptions.IdentificationMismatchException;
import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.mongodb.client.result.DeleteResult;

/**
 * 
 * MongoDB implementation of the AasRepository
 *
 * @author schnicke, kevinbck, kammognie
 *
 */
public class MongoDBAasRepository implements AasRepository {
	private static String IDJSONPATH = "id";
	private static final String ID = "_id";
	private MongoTemplate mongoTemplate;
	private String collectionName;
	private AasServiceFactory aasServiceFactory;
	private String aasRepositoryName;

	public MongoDBAasRepository(MongoTemplate mongoTemplate, String collectionName, AasServiceFactory aasServiceFactory) {
		this.mongoTemplate = mongoTemplate;
		this.collectionName = collectionName;
		this.aasServiceFactory = aasServiceFactory;
		configureIndexForAasId(mongoTemplate);
	}
	
	public MongoDBAasRepository(MongoTemplate mongoTemplate, String collectionName, AasServiceFactory aasServiceFactory, String aasRepositoryName) {
		this(mongoTemplate, collectionName, aasServiceFactory);
		this.aasRepositoryName = aasRepositoryName;
	}

	private void configureIndexForAasId(MongoTemplate mongoTemplate) {
		Index idIndex = new Index().on(IDJSONPATH, Direction.ASC);
		mongoTemplate.indexOps(AssetAdministrationShell.class).ensureIndex(idIndex);
	}

	@Override
	public CursorResult<List<AssetAdministrationShell>> getAllAas(PaginationInfo pInfo) {
		Query query = new Query();
		applySorting(query, pInfo);
		applyPagination(query, pInfo);
		List<AssetAdministrationShell> foundDescriptors = mongoTemplate.find(query, AssetAdministrationShell.class,
				collectionName);

		String cursor = resolveCursor(pInfo, foundDescriptors, AssetAdministrationShell::getId);
		return new CursorResult<List<AssetAdministrationShell>>(cursor, foundDescriptors);
	}

	@Override
	public AssetAdministrationShell getAas(String aasId) throws ElementDoesNotExistException {
		AssetAdministrationShell aas = mongoTemplate.findOne(new Query().addCriteria(Criteria.where(IDJSONPATH)
				.is(aasId)), AssetAdministrationShell.class, collectionName);
		if (aas == null) {
			throw new ElementDoesNotExistException(aasId);
		}
		return aas;
	}

	@Override
	public void createAas(AssetAdministrationShell aas) throws CollidingIdentifierException {
		if (mongoTemplate.exists(new Query().addCriteria(Criteria.where(IDJSONPATH)
				.is(aas.getId())), AssetAdministrationShell.class, collectionName)) {
			throw new CollidingIdentifierException(aas.getId());
		} else {
			mongoTemplate.save(aas, collectionName);
		}
	}

	@Override
	public void deleteAas(String aasId) {
		DeleteResult result = mongoTemplate.remove(new Query().addCriteria(Criteria.where(IDJSONPATH)
				.is(aasId)), AssetAdministrationShell.class, collectionName);

		if (result.getDeletedCount() == 0) {
			throw new ElementDoesNotExistException(aasId);
		}
	}

	@Override
	public void updateAas(String aasId, AssetAdministrationShell aas) {
		Query query = new Query().addCriteria(Criteria.where(IDJSONPATH)
				.is(aasId));

		throwIfAasDoesNotExist(query, aasId);

		throwIfMismatchingIds(aasId, aas);

		mongoTemplate.remove(query, AssetAdministrationShell.class, collectionName);
		mongoTemplate.save(aas, collectionName);
	}

	@Override
	public CursorResult<List<Reference>> getSubmodelReferences(String aasId, PaginationInfo pInfo) {
		CursorResult<List<Reference>> paginatedSubmodelReferences = aasServiceFactory.create(getAas(aasId))
				.getSubmodelReferences(pInfo);

		return paginatedSubmodelReferences;
	}

	@Override
	public void addSubmodelReference(String aasId, Reference submodelReference) {
		AasService service = getAasService(aasId);
		service.addSubmodelReference(submodelReference);

		updateAas(aasId, service.getAAS());
	}

	@Override
	public void removeSubmodelReference(String aasId, String submodelId) {
		AasService service = getAasService(aasId);
		service.removeSubmodelReference(submodelId);

		updateAas(aasId, service.getAAS());
	}

	@Override
	public void setAssetInformation(String aasId, AssetInformation aasInfo) throws ElementDoesNotExistException {
		AasService service = getAasService(aasId);
		service.setAssetInformation(aasInfo);

		updateAas(aasId, service.getAAS());
	}

	@Override
	public AssetInformation getAssetInformation(String aasId) throws ElementDoesNotExistException {
		return this.getAas(aasId)
				.getAssetInformation();
	}
	
	@Override
	public String getName() {
		return aasRepositoryName == null ? AasRepository.super.getName() : aasRepositoryName;
	}

	private AasService getAasService(String aasId) {
		return aasServiceFactory.create(getAas(aasId));
	}

	private void throwIfAasDoesNotExist(Query query, String aasId) {
		if (!mongoTemplate.exists(query, AssetAdministrationShell.class, collectionName))
			throw new ElementDoesNotExistException(aasId);
	}

	private void throwIfMismatchingIds(String aasId, AssetAdministrationShell newAas) {
		String newAasId = newAas.getId();

		if (!aasId.equals(newAasId))
			throw new IdentificationMismatchException();
	}

	private <T> String resolveCursor(PaginationInfo pRequest, List<T> foundDescriptors,
			Function<T, String> idResolver) {
		if (foundDescriptors.isEmpty() || !pRequest.isPaged()) {
			return null;
		}
		T last = foundDescriptors.get(foundDescriptors.size() - 1);
		return idResolver.apply(last);
	}

	private void applySorting(Query query, PaginationInfo pInfo) {
		query.with(Sort.by(Direction.ASC, ID));
	}

	private void applyPagination(Query query, PaginationInfo pInfo) {
		if (pInfo.getCursor() != null) {
			query.addCriteria(Criteria.where(ID).gt(pInfo.getCursor()));
		}
		if (pInfo.getLimit() != null) {
			query.limit(pInfo.getLimit());
		}
	}

}

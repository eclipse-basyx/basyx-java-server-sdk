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

import java.util.Collection;
import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetInformation;
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.basyx.aasservice.backend.InMemoryAasService;
import org.eclipse.digitaltwin.basyx.core.exceptions.CollidingIdentifierException;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.TextIndexDefinition;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.mongodb.client.result.DeleteResult;

/**
 * 
 * MongoDB implementation of the AasRepository
 *
 * @author schnicke, kevinbck
 *
 */
public class MongoDBAasRepository implements AasRepository {
	private MongoTemplate mongoTemplate;
	private String collectionName;
	private static String IDJSONPATH = "id";

	public MongoDBAasRepository(MongoTemplate mongoTemplate, String collectionName) {
		this.mongoTemplate = mongoTemplate;
		this.collectionName = collectionName;
		configureIndexForAasId(mongoTemplate);
	}

	private void configureIndexForAasId(MongoTemplate mongoTemplate) {
		TextIndexDefinition idIndex = TextIndexDefinition.builder().onField(IDJSONPATH).build();
		mongoTemplate.indexOps(AssetAdministrationShell.class).ensureIndex(idIndex);
	}

	@Override
	public Collection<AssetAdministrationShell> getAllAas() {
		return mongoTemplate.findAll(AssetAdministrationShell.class, collectionName);
	}

	@Override
	public AssetAdministrationShell getAas(String aasId) throws ElementDoesNotExistException {
		AssetAdministrationShell aas = mongoTemplate.findOne(new Query().addCriteria(Criteria.where(IDJSONPATH).is(aasId)), AssetAdministrationShell.class, collectionName);
		if (aas == null) {
			throw new ElementDoesNotExistException(aasId);
		}
		return aas;
	}

	@Override
	public void createAas(AssetAdministrationShell aas) throws CollidingIdentifierException {
		if (mongoTemplate.exists(new Query().addCriteria(Criteria.where(IDJSONPATH).is(aas.getId())), AssetAdministrationShell.class, collectionName)) {
			throw new CollidingIdentifierException(aas.getId());
		} else {
			mongoTemplate.save(aas, collectionName);
		}
	}

	@Override
	public void deleteAas(String aasId) {
		DeleteResult result = mongoTemplate.remove(new Query().addCriteria(Criteria.where(IDJSONPATH).is(aasId)), AssetAdministrationShell.class, collectionName);

		if (result.getDeletedCount() == 0) {
			throw new ElementDoesNotExistException(aasId);
		}
	}


	@Override
	public void updateAas(String aasId, AssetAdministrationShell aas) {
		Query query = new Query().addCriteria(Criteria.where(IDJSONPATH).is(aasId));
		if (!mongoTemplate.exists(query, AssetAdministrationShell.class, collectionName)) {
			throw new ElementDoesNotExistException(aas.getId());
		} else {
			mongoTemplate.remove(query, AssetAdministrationShell.class, collectionName);
			mongoTemplate.save(aas, collectionName);
		}
	}

	@Override
	public List<Reference> getSubmodelReferences(String aasId) {
		return new InMemoryAasService(getAas(aasId)).getSubmodelReferences();
	}

	@Override
	public void addSubmodelReference(String aasId, Reference submodelReference) {
		InMemoryAasService service = new InMemoryAasService(getAas(aasId));
		service.addSubmodelReference(submodelReference);

		updateAas(aasId, service.getAAS());
	}

	@Override
	public void removeSubmodelReference(String aasId, String submodelId) {
		InMemoryAasService service = new InMemoryAasService(getAas(aasId));
		service.removeSubmodelReference(submodelId);

		updateAas(aasId, service.getAAS());
	}

	@Override
	public void setAssetInformation(String aasId, AssetInformation aasInfo) throws ElementDoesNotExistException {
		InMemoryAasService service = new InMemoryAasService(getAas(aasId));
		service.setAssetInformation(aasInfo);

		updateAas(aasId, service.getAAS());
	}
	
	@Override
	public AssetInformation getAssetInformation(String aasId) throws ElementDoesNotExistException{
		return this.getAas(aasId).getAssetInformation();
	}

}

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

import java.util.Collection;
import org.eclipse.digitaltwin.aas4j.v3.model.ConceptDescription;
import org.eclipse.digitaltwin.basyx.core.exceptions.CollidingIdentifierException;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.TextIndexDefinition;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.mongodb.client.result.DeleteResult;

/**
 * 
 * MongoDB implementation of the ConceptDescriptionRepository
 *
 * @author danish
 *
 */
public class MongoDBConceptDescriptionRepository implements ConceptDescriptionRepository {
	private static final String IDJSONPATH = "id";

	private MongoTemplate mongoTemplate;
	private String collectionName;

	public MongoDBConceptDescriptionRepository(MongoTemplate mongoTemplate, String collectionName) {
		this.mongoTemplate = mongoTemplate;
		this.collectionName = collectionName;
		configureIndexForConceptDescriptionId(mongoTemplate);
	}

	@Override
	public Collection<ConceptDescription> getAllConceptDescriptions() {
		return mongoTemplate.findAll(ConceptDescription.class, collectionName);
	}

	@Override
	public ConceptDescription getConceptDescription(String conceptDescriptionId) throws ElementDoesNotExistException {
		ConceptDescription conceptDescription = mongoTemplate.findOne(new Query().addCriteria(Criteria.where(IDJSONPATH).is(conceptDescriptionId)), ConceptDescription.class, collectionName);
		
		if (conceptDescription == null)
			throw new ElementDoesNotExistException(conceptDescriptionId);
		
		return conceptDescription;
	}

	@Override
	public void updateConceptDescription(String conceptDescriptionId, ConceptDescription conceptDescription)
			throws ElementDoesNotExistException {
		
		Query query = new Query().addCriteria(Criteria.where(IDJSONPATH).is(conceptDescriptionId));
		
		if (!mongoTemplate.exists(query, ConceptDescription.class, collectionName))
			throw new ElementDoesNotExistException(conceptDescription.getId());
		
		mongoTemplate.remove(query, ConceptDescription.class, collectionName);
		mongoTemplate.save(conceptDescription, collectionName);
	}

	@Override
	public void createConceptDescription(ConceptDescription conceptDescription) throws CollidingIdentifierException {
		Query query = new Query().addCriteria(Criteria.where(IDJSONPATH).is(conceptDescription.getId()));
		
		if (mongoTemplate.exists(query, ConceptDescription.class, collectionName))
			throw new CollidingIdentifierException(conceptDescription.getId());
			
		mongoTemplate.save(conceptDescription, collectionName);
	}

	@Override
	public void deleteConceptDescription(String conceptDescriptionId) throws ElementDoesNotExistException {
		Query query = new Query().addCriteria(Criteria.where(IDJSONPATH).is(conceptDescriptionId));
		
		DeleteResult result = mongoTemplate.remove(query, ConceptDescription.class, collectionName);

		if (result.getDeletedCount() == 0)
			throw new ElementDoesNotExistException(conceptDescriptionId);
		
	}
	
	private void configureIndexForConceptDescriptionId(MongoTemplate mongoTemplate) {
		TextIndexDefinition idIndex = TextIndexDefinition.builder().onField(IDJSONPATH).build();
		mongoTemplate.indexOps(ConceptDescription.class).ensureIndex(idIndex);
	}
	
}

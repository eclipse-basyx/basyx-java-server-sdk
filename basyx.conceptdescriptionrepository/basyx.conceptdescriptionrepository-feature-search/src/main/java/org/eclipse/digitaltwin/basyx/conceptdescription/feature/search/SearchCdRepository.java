/*******************************************************************************
 * Copyright (C) 2025 the Eclipse BaSyx Authors
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

package org.eclipse.digitaltwin.basyx.conceptdescription.feature.search;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.mapping.Property;
import co.elastic.clients.elasticsearch._types.mapping.TypeMapping;
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;
import co.elastic.clients.elasticsearch.indices.ExistsRequest;
import org.eclipse.digitaltwin.aas4j.v3.model.*;
import org.eclipse.digitaltwin.basyx.conceptdescriptionrepository.ConceptDescriptionRepository;
import org.eclipse.digitaltwin.basyx.core.exceptions.CollidingIdentifierException;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.exceptions.MissingIdentifierException;
import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

public class SearchCdRepository implements ConceptDescriptionRepository {
	private static final Logger logger = LoggerFactory.getLogger(SearchCdRepository.class);
	private final ElasticsearchClient esclient;
	private final String indexName;

	private ConceptDescriptionRepository decorated;

	public SearchCdRepository(ConceptDescriptionRepository decorated, ElasticsearchClient esclient, String indexName) {
		this.decorated = decorated;
		this.esclient = esclient;
		this.indexName = indexName;
	}

	@Override
	public CursorResult<List<ConceptDescription>> getAllConceptDescriptions(PaginationInfo pInfo) {
		return decorated.getAllConceptDescriptions(pInfo);
	}

	@Override
	public CursorResult<List<ConceptDescription>> getAllConceptDescriptionsByIdShort(String idShort, PaginationInfo pInfo) {
		return decorated.getAllConceptDescriptionsByIdShort(idShort, pInfo);
	}

	@Override
	public CursorResult<List<ConceptDescription>> getAllConceptDescriptionsByIsCaseOf(Reference isCaseOf, PaginationInfo pInfo) {
		return decorated.getAllConceptDescriptionsByIsCaseOf(isCaseOf, pInfo);
	}

	@Override
	public CursorResult<List<ConceptDescription>> getAllConceptDescriptionsByDataSpecificationReference(Reference dataSpecificationReference, PaginationInfo pInfo) {
		return decorated.getAllConceptDescriptionsByDataSpecificationReference(dataSpecificationReference, pInfo);
	}

	@Override
	public ConceptDescription getConceptDescription(String conceptDescriptionId) throws ElementDoesNotExistException {
		return decorated.getConceptDescription(conceptDescriptionId);
	}

	@Override
	public void updateConceptDescription(String conceptDescriptionId, ConceptDescription conceptDescription) throws ElementDoesNotExistException {
		decorated.updateConceptDescription(conceptDescriptionId, conceptDescription);
		updateCDIndex(conceptDescription);
	}

	@Override
	public void createConceptDescription(ConceptDescription conceptDescription) throws CollidingIdentifierException, MissingIdentifierException {
		decorated.createConceptDescription(conceptDescription);
		indexCD(conceptDescription);
	}

	@Override
	public void deleteConceptDescription(String conceptDescriptionId) throws ElementDoesNotExistException {
		decorated.deleteConceptDescription(conceptDescriptionId);
		deindexCD(conceptDescriptionId);
	}

	private void indexCD(ConceptDescription cd) {
		try {
			esclient.create(
					c -> c.index(indexName)
							.id(cd.getId())
							.document(cd)
			);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void updateCDIndex(ConceptDescription cd) {
		try {
			esclient.update(
					u -> u.index(indexName)
							.id(cd.getId())
							.doc(cd),
					ConceptDescription.class
			);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void deindexCD(String cdId) {
		try {
			esclient.delete(
					d -> d.index(indexName)
							.id(cdId)
			);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}

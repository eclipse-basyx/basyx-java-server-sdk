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

package org.eclipse.digitaltwin.basyx.submodelrepository.feature.search;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.mapping.DynamicMapping;
import co.elastic.clients.elasticsearch._types.mapping.Property;
import co.elastic.clients.elasticsearch._types.mapping.TypeMapping;
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;
import co.elastic.clients.elasticsearch.indices.ExistsRequest;
import com.fasterxml.jackson.databind.JsonNode;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.core.SerializationException;
import org.eclipse.digitaltwin.aas4j.v3.model.*;
import org.eclipse.digitaltwin.basyx.core.exceptions.*;
import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.eclipse.digitaltwin.basyx.submodelrepository.SubmodelRepository;
import org.eclipse.digitaltwin.basyx.submodelservice.value.SubmodelElementValue;
import org.eclipse.digitaltwin.basyx.submodelservice.value.SubmodelValueOnly;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.List;

public class SearchSubmodelRepository implements SubmodelRepository {
	private static final Logger logger = LoggerFactory.getLogger(SearchSubmodelRepository.class);
	private final ElasticsearchClient esclient;
	private final String indexName;

	private SubmodelRepository decorated;

	public SearchSubmodelRepository(SubmodelRepository decorated, ElasticsearchClient esclient, String indexName) {
		this.decorated = decorated;
		this.esclient = esclient;
		this.indexName = indexName;
	}

	@Override
	public CursorResult<List<Submodel>> getAllSubmodels(PaginationInfo pInfo) {
		return decorated.getAllSubmodels(pInfo);
	}

	@Override
	public CursorResult<List<Submodel>> getAllSubmodels(String semanticId, PaginationInfo pInfo) {
		return decorated.getAllSubmodels(semanticId, pInfo);
	}

	@Override
	public Submodel getSubmodel(String submodelId) throws ElementDoesNotExistException {
		return decorated.getSubmodel(submodelId);
	}

	@Override
	public void updateSubmodel(String submodelId, Submodel submodel) throws ElementDoesNotExistException {
		decorated.updateSubmodel(submodelId, submodel);
		updateSMIndex(submodel);
	}

	@Override
	public void createSubmodel(Submodel submodel) throws CollidingIdentifierException, MissingIdentifierException {
		decorated.createSubmodel(submodel);
		indexSM(submodel);
	}

	@Override
	public void updateSubmodelElement(String submodelIdentifier, String idShortPath, SubmodelElement submodelElement) throws ElementDoesNotExistException {
		decorated.updateSubmodelElement(submodelIdentifier, idShortPath, submodelElement);
		reindexSM(submodelIdentifier);
	}

	@Override
	public void deleteSubmodel(String submodelId) throws ElementDoesNotExistException {
		decorated.deleteSubmodel(submodelId);
		deindexSM(submodelId);
	}

	@Override
	public CursorResult<List<SubmodelElement>> getSubmodelElements(String submodelId, PaginationInfo pInfo) throws ElementDoesNotExistException {
		return decorated.getSubmodelElements(submodelId, pInfo);
	}

	@Override
	public SubmodelElement getSubmodelElement(String submodelId, String smeIdShort) throws ElementDoesNotExistException {
		return decorated.getSubmodelElement(submodelId, smeIdShort);
	}

	@Override
	public SubmodelElementValue getSubmodelElementValue(String submodelId, String smeIdShort) throws ElementDoesNotExistException {
		return decorated.getSubmodelElementValue(submodelId, smeIdShort);
	}

	@Override
	public void setSubmodelElementValue(String submodelId, String smeIdShort, SubmodelElementValue value) throws ElementDoesNotExistException {
		decorated.setSubmodelElementValue(submodelId, smeIdShort, value);
		reindexSM(submodelId);
	}

	@Override
	public void createSubmodelElement(String submodelId, SubmodelElement smElement) {
		decorated.createSubmodelElement(submodelId, smElement);
		reindexSM(submodelId);
	}

	@Override
	public void createSubmodelElement(String submodelId, String idShortPath, SubmodelElement smElement) throws ElementDoesNotExistException {
		decorated.createSubmodelElement(submodelId, idShortPath, smElement);
		reindexSM(submodelId);
	}

	@Override
	public void deleteSubmodelElement(String submodelId, String idShortPath) throws ElementDoesNotExistException {
		decorated.deleteSubmodelElement(submodelId, idShortPath);
		reindexSM(submodelId);
	}

	@Override
	public OperationVariable[] invokeOperation(String submodelId, String idShortPath, OperationVariable[] input) throws ElementDoesNotExistException {
		return decorated.invokeOperation(submodelId, idShortPath, input);
	}

	@Override
	public SubmodelValueOnly getSubmodelByIdValueOnly(String submodelId) throws ElementDoesNotExistException {
		return decorated.getSubmodelByIdValueOnly(submodelId);
	}

	@Override
	public Submodel getSubmodelByIdMetadata(String submodelId) throws ElementDoesNotExistException {
		return decorated.getSubmodelByIdMetadata(submodelId);
	}

	@Override
	public File getFileByPathSubmodel(String submodelId, String idShortPath) throws ElementDoesNotExistException, ElementNotAFileException, FileDoesNotExistException {
		return decorated.getFileByPathSubmodel(submodelId, idShortPath);
	}

	@Override
	public void setFileValue(String submodelId, String idShortPath, String fileName, String contentType, InputStream inputStream) throws ElementDoesNotExistException, ElementNotAFileException {
		decorated.setFileValue(submodelId, idShortPath, fileName, contentType, inputStream);
		reindexSM(submodelId);
	}

	@Override
	public void deleteFileValue(String submodelId, String idShortPath) throws ElementDoesNotExistException, ElementNotAFileException, FileDoesNotExistException {
		decorated.deleteFileValue(submodelId, idShortPath);
		reindexSM(submodelId);
	}

	@Override
	public void patchSubmodelElements(String submodelId, List<SubmodelElement> submodelElementList) {
		decorated.patchSubmodelElements(submodelId, submodelElementList);
		reindexSM(submodelId);
	}

	@Override
	public InputStream getFileByFilePath(String submodelId, String filePath) {
		return decorated.getFileByFilePath(submodelId, filePath);
	}

	private void indexSM(Submodel submodel) {
		try {
			JsonNode normalizedSubmodel = IndexNormalizer.toIndexable(submodel);
			esclient.create(
					c -> c.index(indexName)
							.id(submodel.getId())
							.document(normalizedSubmodel)
			);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (SerializationException e) {
            throw new RuntimeException(e);
        }
    }

	private void updateSMIndex(Submodel submodel) {
		try {
			JsonNode normalizedSubmodel = IndexNormalizer.toIndexable(submodel);
			esclient.update(
					u -> u.index(indexName)
							.id(submodel.getId())
							.doc(normalizedSubmodel),
					JsonNode.class
			);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (SerializationException e) {
            throw new RuntimeException(e);
        }
    }

	private void deindexSM(String smID) {
		try {
			esclient.delete(
					d -> d.index(indexName)
							.id(smID)
			);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void reindexSM(String smId) {
		Submodel submodel = getSubmodel(smId);
		deindexSM(smId);
		indexSM(submodel);
	}

}

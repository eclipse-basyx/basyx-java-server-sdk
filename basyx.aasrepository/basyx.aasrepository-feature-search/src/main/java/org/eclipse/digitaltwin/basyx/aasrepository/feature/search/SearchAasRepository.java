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
package org.eclipse.digitaltwin.basyx.aasrepository.feature.search;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetInformation;
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.aas4j.v3.model.SpecificAssetId;
import org.eclipse.digitaltwin.basyx.aasrepository.AasRepository;
import org.eclipse.digitaltwin.basyx.core.exceptions.CollidingIdentifierException;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class SearchAasRepository implements AasRepository {
	private static final Logger logger = LoggerFactory.getLogger(SearchAasRepository.class);
	private final ElasticsearchClient esclient;
	private final String indexName;

	private AasRepository decorated;

	public SearchAasRepository(AasRepository decorated, ElasticsearchClient esclient, String indexName) {
		this.decorated = decorated;
		this.esclient = esclient;
		this.indexName = indexName;
	}

	@Override
	public CursorResult<List<AssetAdministrationShell>> getAllAas(List<SpecificAssetId> assetIds, String idShort, PaginationInfo pInfo) {
		return decorated.getAllAas(assetIds, idShort, pInfo);
	}

	@Override
	public AssetAdministrationShell getAas(String aasId) throws ElementDoesNotExistException {
		return decorated.getAas(aasId);
	}

	@Override
	public void createAas(AssetAdministrationShell aas) throws CollidingIdentifierException {
		decorated.createAas(aas);
		indexAAS(aas);
	}

	@Override
	public void updateAas(String aasId, AssetAdministrationShell aas) {
		decorated.updateAas(aasId, aas);
		updateAASIndex(aas);
	}

	@Override
	public void deleteAas(String aasId) {
		decorated.deleteAas(aasId);
		deindexAAS(aasId);
	}

	@Override
	public String getName() {
		return decorated.getName();
	}

	@Override
	public CursorResult<List<Reference>> getSubmodelReferences(String aasId, PaginationInfo pInfo) {
		return decorated.getSubmodelReferences(aasId, pInfo);
	}

	@Override
	public void addSubmodelReference(String aasId, Reference submodelReference) {
		decorated.addSubmodelReference(aasId, submodelReference);
		reindexAAS(aasId);
	}

	@Override
	public void removeSubmodelReference(String aasId, String submodelId) {
		decorated.removeSubmodelReference(aasId, submodelId);
		reindexAAS(aasId);
	}

	@Override
	public void setAssetInformation(String aasId, AssetInformation aasInfo) throws ElementDoesNotExistException {
		decorated.setAssetInformation(aasId, aasInfo);
		reindexAAS(aasId);
	}

	@Override
	public AssetInformation getAssetInformation(String aasId) throws ElementDoesNotExistException {
		return decorated.getAssetInformation(aasId);
	}

	@Override
	public File getThumbnail(String aasId) {
		return decorated.getThumbnail(aasId);
	}

	@Override
	public void setThumbnail(String aasId, String fileName, String contentType, InputStream inputStream) {
		decorated.setThumbnail(aasId, fileName, contentType, inputStream);
		reindexAAS(aasId);
	}

	@Override
	public void deleteThumbnail(String aasId) {
		decorated.deleteThumbnail(aasId);
		reindexAAS(aasId);
	}

	private void indexAAS(AssetAdministrationShell aas) {
		try {
			esclient.create(
					c -> c.index(indexName)
							.id(aas.getId())
							.document(aas)
			);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void updateAASIndex(AssetAdministrationShell aas) {
		try {
			esclient.update(
					u -> u.index(indexName)
							.id(aas.getId())
							.doc(aas),
					AssetAdministrationShell.class
			);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void deindexAAS(String aasId) {
		try {
			esclient.delete(
					d -> d.index(indexName)
							.id(aasId)
			);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void reindexAAS(String aasId) {
		AssetAdministrationShell shell = getAas(aasId);
		deindexAAS(aasId);
		indexAAS(shell);
	}

}

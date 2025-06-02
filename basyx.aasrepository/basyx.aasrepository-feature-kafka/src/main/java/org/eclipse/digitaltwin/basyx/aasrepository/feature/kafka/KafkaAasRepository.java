/*******************************************************************************
 * Copyright (C) 2024 DFKI GmbH (https://www.dfki.de/en/web)
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
 * 
 ******************************************************************************/
package org.eclipse.digitaltwin.basyx.aasrepository.feature.kafka;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetInformation;
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.aas4j.v3.model.SpecificAssetId;
import org.eclipse.digitaltwin.basyx.aasrepository.AasRepository;
import org.eclipse.digitaltwin.basyx.aasrepository.feature.kafka.events.AasEventHandler;
import org.eclipse.digitaltwin.basyx.core.exceptions.CollidingIdentifierException;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;

/**
 * @author geso02 (Sonnenberg DFKI GmbH)
 */
public class KafkaAasRepository implements AasRepository {

	private AasRepository decorated;
	private AasEventHandler eventHandler;

	public KafkaAasRepository(AasRepository decorated, AasEventHandler handler) {
		this.decorated = decorated;
		this.eventHandler = handler;
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
		eventHandler.onAasCreated(aas);
	}

	@Override
	public void updateAas(String aasId, AssetAdministrationShell aas) {
		decorated.updateAas(aasId, aas);
		eventHandler.onAasUpdated(aasId, aas);
	}

	@Override
	public void deleteAas(String aasId) {
		decorated.deleteAas(aasId);
		eventHandler.onAasDeleted(aasId);
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
		eventHandler.onSubmodelRefAdded(aasId, submodelReference);
	}

	@Override
	public void removeSubmodelReference(String aasId, String submodelId) {
		decorated.removeSubmodelReference(aasId, submodelId);
		eventHandler.onSubmodelRefDeleted(aasId, submodelId);
	}

	@Override
	public void setAssetInformation(String aasId, AssetInformation aasInfo) throws ElementDoesNotExistException {
		decorated.setAssetInformation(aasId, aasInfo);
		eventHandler.onAssetInformationSet(aasId, aasInfo);
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
	}

	@Override
	public void deleteThumbnail(String aasId) {
		decorated.deleteThumbnail(aasId);
	}
}

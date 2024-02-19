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


package org.eclipse.digitaltwin.basyx.aasrepository.client;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetInformation;
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.basyx.aasrepository.AasRepository;
import org.eclipse.digitaltwin.basyx.core.exceptions.CollidingIdentifierException;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.exceptions.MissingIdentifierException;
import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;

/**
 * Wraps a ConnectedAasRepository into the AasRepository interface. This wrapper
 * is needed for leveraging the existing test suite.
 * 
 * @author schnicke
 */
public class ConnectedAasRepositoryWrapper implements AasRepository {

	ConnectedAasRepository aasRepo;

	public ConnectedAasRepositoryWrapper(ConnectedAasRepository aasRepo) {
		this.aasRepo = aasRepo;
	}

	@Override
	public CursorResult<List<AssetAdministrationShell>> getAllAas(PaginationInfo pInfo) {
		return aasRepo.getAllAas(pInfo);
	}

	@Override
	public AssetAdministrationShell getAas(String aasId) throws ElementDoesNotExistException {
		return aasRepo.getAas(aasId);
	}

	@Override
	public void createAas(AssetAdministrationShell aas) throws CollidingIdentifierException, MissingIdentifierException {
		aasRepo.createAas(aas);
	}

	@Override
	public void deleteAas(String aasId) {
		aasRepo.deleteAas(aasId);
	}

	@Override
	public void updateAas(String aasId, AssetAdministrationShell aas) {
		aasRepo.updateAas(aasId, aas);
	}

	@Override
	public CursorResult<List<Reference>> getSubmodelReferences(String aasId, PaginationInfo pInfo) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addSubmodelReference(String aasId, Reference submodelReference) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeSubmodelReference(String aasId, String submodelId) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setAssetInformation(String aasId, AssetInformation aasInfo) throws ElementDoesNotExistException {
		// TODO Auto-generated method stub

	}

	@Override
	public AssetInformation getAssetInformation(String aasId) throws ElementDoesNotExistException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public File getThumbnail(String aasId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setThumbnail(String aasId, String fileName, String contentType, InputStream inputStream) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteThumbnail(String aasId) {
		// TODO Auto-generated method stub

	}

}

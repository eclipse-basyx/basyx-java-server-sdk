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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetInformation;
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.aas4j.v3.model.SpecificAssetId;
import org.eclipse.digitaltwin.basyx.aasrepository.AasRepository;
import org.eclipse.digitaltwin.basyx.aasrepository.client.internal.AssetAdministrationShellRepositoryApi;
import org.eclipse.digitaltwin.basyx.aasservice.client.ConnectedAasService;
import org.eclipse.digitaltwin.basyx.client.internal.ApiException;
import org.eclipse.digitaltwin.basyx.core.exceptions.CollidingIdentifierException;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.exceptions.IdentificationMismatchException;
import org.eclipse.digitaltwin.basyx.core.exceptions.MissingIdentifierException;
import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.eclipse.digitaltwin.basyx.http.Base64UrlEncodedIdentifier;
import org.eclipse.digitaltwin.basyx.http.Base64UrlEncoder;
import org.springframework.http.HttpStatus;

/**
 * Provides access to an Aas Repository on a remote server
 * 
 * @author schnicke, mateusmolina
 */
public class ConnectedAasRepository implements AasRepository {

	private AssetAdministrationShellRepositoryApi repoApi;
	private String aasRepoUrl;

	/**
	 * 
	 * @param repoUrl
	 *            the Url of the AAS Repository without the "/shells" part
	 */
	public ConnectedAasRepository(String repoUrl) {
		this.aasRepoUrl = repoUrl;
		this.repoApi = new AssetAdministrationShellRepositoryApi(repoUrl);
	}
	
	public ConnectedAasRepository(String repoUrl, AssetAdministrationShellRepositoryApi assetAdministrationShellRepositoryApi) {
		this.aasRepoUrl = repoUrl;
		this.repoApi = assetAdministrationShellRepositoryApi;
	}
	
	public String getBaseUrl() {
		return aasRepoUrl;
	}

	@Override
	public CursorResult<List<AssetAdministrationShell>> getAllAas(List<SpecificAssetId> assetIds, String idShort, PaginationInfo pInfo) {
		String encodedCursor = pInfo.getCursor() == null ? null : Base64UrlEncoder.encode(pInfo.getCursor());
		ObjectMapper mapper = new ObjectMapper();
		if(assetIds == null || assetIds.isEmpty()) {
			return repoApi.getAllAssetAdministrationShells(null, idShort, pInfo.getLimit(), encodedCursor);
		}
		List<String> encodedAssetIds = assetIds.stream()
				.map(assetId -> {
					try {
						String value = mapper.writeValueAsString(assetId);
						return Base64UrlEncoder.encode(value);
					} catch (JsonProcessingException e) {
						throw new RuntimeException(e);
					}
				}).toList();
		return repoApi.getAllAssetAdministrationShells(encodedAssetIds, idShort, pInfo.getLimit(), encodedCursor);
	}

	@Override
	public AssetAdministrationShell getAas(String aasId) throws ElementDoesNotExistException {
		try {
			return repoApi.getAssetAdministrationShellById(aasId);
		} catch (ApiException e) {
			throw mapExceptionAasAccess(aasId, e);
		}
	}

	@Override
	public void createAas(AssetAdministrationShell aas) throws CollidingIdentifierException, MissingIdentifierException {
		try {
			repoApi.postAssetAdministrationShell(aas);
		} catch (ApiException e) {
			throw mapExceptionAasAccess(aas.getId(), e);
		}
	}

	@Override
	public void deleteAas(String aasId) {
		try {
			repoApi.deleteAssetAdministrationShellById(aasId);
		} catch (ApiException e) {
			throw mapExceptionAasAccess(aasId, e);
		}
	}

	@Override
	public void updateAas(String aasId, AssetAdministrationShell aas) {
		try {
			repoApi.putAssetAdministrationShellById(aasId, aas);
		} catch (ApiException e) {
			throw mapExceptionAasUpdate(aasId, e);
		}
	}

	/**
	 * Retrieves a ConnectedAasService for interacting with the Aas on the Server
	 * 
	 * @param aasId
	 * @return
	 * @throws ElementDoesNotExistException
	 */
	public ConnectedAasService getConnectedAasService(String aasId) throws ElementDoesNotExistException {
		try {
			repoApi.getAssetAdministrationShellById(aasId);
			return new ConnectedAasService(getAasUrl(aasId));
		} catch (ApiException e) {
			throw mapExceptionAasAccess(aasId, e);
		}
	}

	@Override
	public CursorResult<List<Reference>> getSubmodelReferences(String aasId, PaginationInfo pInfo) {
		return getConnectedAasService(aasId).getSubmodelReferences(pInfo);
	}

	@Override
	public void addSubmodelReference(String aasId, Reference submodelReference) {
		getConnectedAasService(aasId).addSubmodelReference(submodelReference);
	}

	@Override
	public void removeSubmodelReference(String aasId, String submodelId) {
		getConnectedAasService(aasId).removeSubmodelReference(submodelId);
	}

	@Override
	public void setAssetInformation(String aasId, AssetInformation aasInfo) throws ElementDoesNotExistException {
		getConnectedAasService(aasId).setAssetInformation(aasInfo);
	}

	@Override
	public AssetInformation getAssetInformation(String aasId) throws ElementDoesNotExistException {
		return getConnectedAasService(aasId).getAssetInformation();
	}

	@Override
	public File getThumbnail(String aasId) {
		return getConnectedAasService(aasId).getThumbnail();
	}

	@Override
	public void setThumbnail(String aasId, String fileName, String contentType, InputStream inputStream) {
		getConnectedAasService(aasId).setThumbnail(fileName, contentType, inputStream);
	}

	@Override
	public void deleteThumbnail(String aasId) {
		getConnectedAasService(aasId).deleteThumbnail();
	}

	protected String getAasUrl(String aasId) {
		return aasRepoUrl + "/shells/" + Base64UrlEncodedIdentifier.encodeIdentifier(aasId);
	}

	private RuntimeException mapExceptionAasUpdate(String aasId, ApiException e) {
		if (e.getCode() == HttpStatus.BAD_REQUEST.value()) {
			return new IdentificationMismatchException();
		}

		return mapExceptionAasAccess(aasId, e);
	}

	protected RuntimeException mapExceptionAasAccess(String aasId, ApiException e) {
		if (e.getCode() == HttpStatus.NOT_FOUND.value()) {
			return new ElementDoesNotExistException(aasId);
		} else if (e.getCode() == HttpStatus.CONFLICT.value()) {
			return new CollidingIdentifierException(aasId);
		} else if (e.getCode() == HttpStatus.BAD_REQUEST.value()) {
			return new MissingIdentifierException();
		}

		return e;
	}

}

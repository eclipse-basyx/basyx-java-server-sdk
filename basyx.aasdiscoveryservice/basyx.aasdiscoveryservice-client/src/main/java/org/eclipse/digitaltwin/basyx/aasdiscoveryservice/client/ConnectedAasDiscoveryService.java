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

package org.eclipse.digitaltwin.basyx.aasdiscoveryservice.client;

import org.eclipse.digitaltwin.aas4j.v3.model.SpecificAssetId;
import org.eclipse.digitaltwin.basyx.aasdiscoveryservice.client.internal.AssetAdministrationShellDiscoveryApi;
import org.eclipse.digitaltwin.basyx.aasdiscoveryservice.core.AasDiscoveryService;
import org.eclipse.digitaltwin.basyx.aasdiscoveryservice.core.model.AssetLink;
import org.eclipse.digitaltwin.basyx.client.internal.ApiException;
import org.eclipse.digitaltwin.basyx.client.internal.authorization.TokenManager;
import org.eclipse.digitaltwin.basyx.core.exceptions.*;
import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.springframework.http.HttpStatus;

import java.util.List;

/**
 * Provides access to an AAS Discovery Service on a remote server
 *
 * @author fried
 */
public class ConnectedAasDiscoveryService implements AasDiscoveryService {

    private final AssetAdministrationShellDiscoveryApi discoveryApi;

    public ConnectedAasDiscoveryService(String baseURL){
        this.discoveryApi = new AssetAdministrationShellDiscoveryApi(baseURL);
    }

    public ConnectedAasDiscoveryService(String baseURL, TokenManager tokenManager){
        this.discoveryApi = new AssetAdministrationShellDiscoveryApi(baseURL, tokenManager);
    }

    @Override
    public CursorResult<List<String>> getAllAssetAdministrationShellIdsByAssetLink(PaginationInfo pInfo, List<AssetLink> assetIds) {
        try{
            return discoveryApi.getAllAssetAdministrationShellIdsByAssetLink(assetIds, pInfo.getLimit(), pInfo.getCursor());
        } catch (ApiException e) {
            if(e.getCode() == HttpStatus.NOT_FOUND.value()){
                throw new AssetLinkDoesNotExistException();
            } else {
                throw new RuntimeException("Error while getting all Asset Administration Shell IDs by Asset Link", e);
            }
        }
    }

    @Override
    public List<SpecificAssetId> getAllAssetLinksById(String shellIdentifier) {
        try{
            return discoveryApi.getAllAssetLinksById(shellIdentifier);
        } catch (ApiException e) {
            if(e.getCode() == HttpStatus.NOT_FOUND.value()){
                throw new AssetLinkDoesNotExistException(shellIdentifier);
            } else {
                throw new RuntimeException("Error while getting all Asset Links by ID", e);
            }
        }
    }

    @Override
    public List<SpecificAssetId> createAllAssetLinksById(String shellIdentifier, List<SpecificAssetId> assetIds) {
        try{
            return discoveryApi.postAllAssetLinksById(shellIdentifier, assetIds);
        } catch (ApiException e) {
            if(e.getCode() == HttpStatus.CONFLICT.value()){
                throw new CollidingAssetLinkException(shellIdentifier);
            } else {
                throw new RuntimeException("Error while creating all Asset Links by ID", e);
            }
        }
    }

    @Override
    public void deleteAllAssetLinksById(String shellIdentifier) {
        try{
            discoveryApi.deleteAllAssetLinksById(shellIdentifier);
        } catch (ApiException e) {
            if(e.getCode() == HttpStatus.NOT_FOUND.value()){
                throw new AssetLinkDoesNotExistException(shellIdentifier);
            } else {
                throw new RuntimeException("Error while deleting all Asset Links by ID", e);
            }
        }
    }
}

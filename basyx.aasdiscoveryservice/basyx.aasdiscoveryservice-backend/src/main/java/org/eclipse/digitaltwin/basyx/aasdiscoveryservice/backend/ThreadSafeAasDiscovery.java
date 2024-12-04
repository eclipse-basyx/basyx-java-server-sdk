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

package org.eclipse.digitaltwin.basyx.aasdiscoveryservice.backend;

import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.model.SpecificAssetId;
import org.eclipse.digitaltwin.basyx.aasdiscoveryservice.core.AasDiscoveryService;
import org.eclipse.digitaltwin.basyx.aasdiscoveryservice.core.model.AssetLink;
import org.eclipse.digitaltwin.basyx.common.backend.ThreadSafeAccess;
import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;

/**
 * A thread-safe wrapper for the {@link AasDiscoveryService}
 * 
 * @author mateusmolina
 */
public class ThreadSafeAasDiscovery implements AasDiscoveryService {

    private final AasDiscoveryService decoratedAasDiscovery;
    private final ThreadSafeAccess access = new ThreadSafeAccess();

    public ThreadSafeAasDiscovery(AasDiscoveryService decoratedAasDiscovery) {
        this.decoratedAasDiscovery = decoratedAasDiscovery;
    }

    @Override
    public CursorResult<List<String>> getAllAssetAdministrationShellIdsByAssetLink(PaginationInfo pInfo, List<AssetLink> assetIds) {
        return access.read(decoratedAasDiscovery::getAllAssetAdministrationShellIdsByAssetLink, pInfo, assetIds);
    }

    @Override
    public List<SpecificAssetId> getAllAssetLinksById(String shellIdentifier) {
        return access.read(decoratedAasDiscovery::getAllAssetLinksById, shellIdentifier);
    }

    @Override
    public List<SpecificAssetId> createAllAssetLinksById(String shellIdentifier, List<SpecificAssetId> assetIds) {
        return access.write(decoratedAasDiscovery::createAllAssetLinksById, shellIdentifier, assetIds);
    }

    @Override
    public void deleteAllAssetLinksById(String shellIdentifier) {
        access.write(decoratedAasDiscovery::deleteAllAssetLinksById, shellIdentifier);
    }

    @Override
    public String getName() {
        return decoratedAasDiscovery.getName();
    }
}

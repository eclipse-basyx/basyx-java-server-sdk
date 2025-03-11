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

import com.querydsl.core.types.dsl.BooleanExpression;
import org.eclipse.digitaltwin.basyx.aasdiscoveryservice.core.model.AssetLink;
import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MongoDBAasDiscoveryService extends CrudAasDiscovery{

    public MongoDBAasDiscoveryService(AasDiscoveryDocumentBackend backend, String aasDiscoveryServiceName) {
        super(backend, aasDiscoveryServiceName);
    }

    @Override
    public CursorResult<List<String>> getAllAssetAdministrationShellIdsByAssetLink(PaginationInfo pInfo, List<AssetLink> assetIds) {
        QAasDiscoveryDocument qDoc = QAasDiscoveryDocument.aasDiscoveryDocument;
        BooleanExpression predicate = qDoc.assetLinks.any().in(assetIds);
        Iterable<AasDiscoveryDocument> result = backend.findAll(predicate);

        List<AasDiscoveryDocument> aasDiscoveryDocuments = convertIterableToList(result);

        Set<String> shellIds = new HashSet<>(aasDiscoveryDocuments.stream().map(AasDiscoveryDocument::getShellIdentifier).toList());
        return paginateList(pInfo, new ArrayList<>(shellIds));
    }
}

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

package org.eclipse.digitaltwin.basyx.submodelregistry.feature.search;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import org.eclipse.digitaltwin.basyx.submodelregistry.model.SubmodelDescriptor;
import org.eclipse.digitaltwin.basyx.submodelregistry.service.errors.SubmodelAlreadyExistsException;
import org.eclipse.digitaltwin.basyx.submodelregistry.service.errors.SubmodelNotFoundException;
import org.eclipse.digitaltwin.basyx.submodelregistry.service.storage.SubmodelRegistryStorage;
import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;

import java.io.IOException;
import java.util.List;
import java.util.Set;

public class SearchSubmodelRegistryStorage implements SubmodelRegistryStorage {
    private final ElasticsearchClient esclient;
    private final SubmodelRegistryStorage decorated;

    private final String indexName;


    SearchSubmodelRegistryStorage(SubmodelRegistryStorage decorated, ElasticsearchClient esclient, String indexName) {
        this.decorated = decorated;
        this.esclient = esclient;
        this.indexName = indexName;
    }

    @Override
    public CursorResult<List<SubmodelDescriptor>> getAllSubmodelDescriptors(PaginationInfo pRequest) {
        return decorated.getAllSubmodelDescriptors(pRequest);
    }

    @Override
    public SubmodelDescriptor getSubmodelDescriptor(String submodelId) throws SubmodelNotFoundException {
        return decorated.getSubmodelDescriptor(submodelId);
    }

    @Override
    public void insertSubmodelDescriptor(SubmodelDescriptor descr) throws SubmodelAlreadyExistsException {
        decorated.insertSubmodelDescriptor(descr);
        indexSubmodelDescriptor(descr);
    }

    @Override
    public void replaceSubmodelDescriptor(String submodelId, SubmodelDescriptor descr) throws SubmodelNotFoundException {
        decorated.replaceSubmodelDescriptor(submodelId, descr);
        reindexSubmodelDescriptor(submodelId);
    }

    @Override
    public void removeSubmodelDescriptor(String submodelId) throws SubmodelNotFoundException {
        decorated.removeSubmodelDescriptor(submodelId);
        deindexSubmodelDescriptor(submodelId);
    }

    @Override
    public Set<String> clear() {
        return Set.of();
    }

    private void indexSubmodelDescriptor(SubmodelDescriptor descr) {
        try {
            esclient.create(
                    c -> c.index(indexName)
                            .id(descr.getId())
                            .document(descr)
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void deindexSubmodelDescriptor(String smDescrId) {
        try {
            esclient.delete(
                    d -> d.index(indexName)
                            .id(smDescrId)
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void reindexSubmodelDescriptor(String smDescrId) {
        SubmodelDescriptor descr = getSubmodelDescriptor(smDescrId);
        deindexSubmodelDescriptor(smDescrId);
        indexSubmodelDescriptor(descr);
    }
}

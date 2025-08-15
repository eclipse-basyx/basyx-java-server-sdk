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

package org.eclipse.digitaltwin.basyx.aasregistry.feature.search;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import org.eclipse.digitaltwin.basyx.aasregistry.service.errors.AasDescriptorAlreadyExistsException;
import org.eclipse.digitaltwin.basyx.aasregistry.service.errors.AasDescriptorNotFoundException;
import org.eclipse.digitaltwin.basyx.aasregistry.service.errors.SubmodelAlreadyExistsException;
import org.eclipse.digitaltwin.basyx.aasregistry.service.errors.SubmodelNotFoundException;
import org.eclipse.digitaltwin.basyx.aasregistry.service.storage.AasRegistryStorage;
import org.eclipse.digitaltwin.basyx.aasregistry.service.storage.DescriptorFilter;
import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.eclipse.digitaltwin.basyx.aasregistry.model.AssetAdministrationShellDescriptor;
import org.eclipse.digitaltwin.basyx.aasregistry.model.SubmodelDescriptor;
import org.eclipse.digitaltwin.basyx.aasregistry.model.ShellDescriptorSearchRequest;
import org.eclipse.digitaltwin.basyx.aasregistry.model.ShellDescriptorSearchResponse;


import java.io.IOException;
import java.util.List;
import java.util.Set;

public class SearchAasRegistryStorage implements AasRegistryStorage {
    private final ElasticsearchClient esclient;
    private final AasRegistryStorage decorated;

    private final String indexName;


    SearchAasRegistryStorage(AasRegistryStorage decorated, ElasticsearchClient esclient, String indexName) {
        this.decorated = decorated;
        this.esclient = esclient;
        this.indexName = indexName;
    }

    @Override
    public CursorResult<List<AssetAdministrationShellDescriptor>> getAllAasDescriptors(PaginationInfo pRequest, DescriptorFilter filter) {
        return decorated.getAllAasDescriptors(pRequest, filter);
    }

    @Override
    public AssetAdministrationShellDescriptor getAasDescriptor(String aasDescriptorId) throws AasDescriptorNotFoundException {
        return decorated.getAasDescriptor(aasDescriptorId);
    }

    @Override
    public void removeAasDescriptor(String aasDescriptorId) throws AasDescriptorNotFoundException {
        decorated.removeAasDescriptor(aasDescriptorId);
        deindexAasDescriptor(aasDescriptorId);
    }

    @Override
    public CursorResult<List<SubmodelDescriptor>> getAllSubmodels(String aasDescriptorId, PaginationInfo pRequest) throws AasDescriptorNotFoundException {
        return decorated.getAllSubmodels(aasDescriptorId, pRequest);
    }

    @Override
    public SubmodelDescriptor getSubmodel(String aasDescriptorId, String submodelId) throws AasDescriptorNotFoundException, SubmodelNotFoundException {
        return decorated.getSubmodel(aasDescriptorId, submodelId);
    }

    @Override
    public void removeSubmodel(String aasDescriptorId, String submodelId) throws AasDescriptorNotFoundException, SubmodelNotFoundException {
        decorated.removeSubmodel(aasDescriptorId, submodelId);
        reindexAasDescriptor(aasDescriptorId);
    }

    @Override
    public Set<String> clear() {
        clearIndex();
        return decorated.clear();
    }

    @Override
    public ShellDescriptorSearchResponse searchAasDescriptors(ShellDescriptorSearchRequest request) {
        return null;
    }

    @Override
    public void replaceSubmodel(String aasDescriptorId, String submodelId, SubmodelDescriptor submodel) throws AasDescriptorNotFoundException, SubmodelNotFoundException {
        decorated.replaceSubmodel(aasDescriptorId, submodelId, submodel);
        reindexAasDescriptor(aasDescriptorId);
    }

    @Override
    public void insertSubmodel(String aasDescriptorId, SubmodelDescriptor submodel) throws AasDescriptorNotFoundException, SubmodelAlreadyExistsException {
        decorated.insertSubmodel(aasDescriptorId, submodel);
        reindexAasDescriptor(aasDescriptorId);
    }

    @Override
    public void replaceAasDescriptor(String aasDescriptorId, AssetAdministrationShellDescriptor descriptor) throws AasDescriptorNotFoundException {
        updateAasDescriptorIndex(descriptor);
        reindexAasDescriptor(aasDescriptorId);
    }

    @Override
    public void insertAasDescriptor(AssetAdministrationShellDescriptor descr) throws AasDescriptorAlreadyExistsException {
        decorated.insertAasDescriptor(descr);
        indexAasDescriptor(descr);
    }

    private void indexAasDescriptor(AssetAdministrationShellDescriptor descr) {
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

    private void updateAasDescriptorIndex(AssetAdministrationShellDescriptor descr) {
        try {
            esclient.update(
                    u -> u.index(indexName)
                            .id(descr.getId())
                            .doc(descr),
                    AssetAdministrationShellDescriptor.class
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void deindexAasDescriptor(String aasDescrId) {
        try {
            esclient.delete(
                    d -> d.index(indexName)
                            .id(aasDescrId)
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void reindexAasDescriptor(String aasDescrId) {
        AssetAdministrationShellDescriptor descr = getAasDescriptor(aasDescrId);
        deindexAasDescriptor(aasDescrId);
        indexAasDescriptor(descr);
    }

    private void clearIndex() {
        try {
            esclient.deleteByQuery(d -> d
                .index(indexName)
                .query(q -> q
                        .matchAll(m -> m)
                )
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

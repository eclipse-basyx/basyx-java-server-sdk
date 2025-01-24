/*******************************************************************************
 * Copyright (C) 2023 the Eclipse BaSyx Authors
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

package org.eclipse.digitaltwin.basyx.aasrepository.backend.inmemory;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.function.Consumer;

import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetInformation;
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.basyx.aasrepository.backend.AasRepositoryBackend;
import org.eclipse.digitaltwin.basyx.aasservice.AasService;
import org.eclipse.digitaltwin.basyx.aasservice.AasServiceFactory;
import org.eclipse.digitaltwin.basyx.common.backend.inmemory.core.InMemoryCrudRepository;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;

/**
 * InMemory implementation of the {@link AasRepositoryBackend} based on the
 * {@link AasService}
 * 
 * @author mateusmolina
 */
public class InMemoryAasBackend extends InMemoryCrudRepository<AssetAdministrationShell> implements AasRepositoryBackend {

    private final AasServiceFactory aasServiceFactory;

    public InMemoryAasBackend(AasServiceFactory aasServiceFactory) {
        super(aas -> aas.getId());
        this.aasServiceFactory = aasServiceFactory;
    }

    @Override
    public CursorResult<List<Reference>> getSubmodelReferences(String aasId, PaginationInfo pInfo) {
        return getAasService(aasId).getSubmodelReferences(pInfo);
    }

    @Override
    public void addSubmodelReference(String aasId, Reference submodelReference) {
        doWithService(aasId, s -> s.addSubmodelReference(submodelReference));
    }

    @Override
    public void removeSubmodelReference(String aasId, String submodelId) {
        doWithService(aasId, s -> s.removeSubmodelReference(submodelId));
    }

    @Override
    public void setAssetInformation(String aasId, AssetInformation aasInfo) {
        doWithService(aasId, s -> s.setAssetInformation(aasInfo));
    }

    @Override
    public AssetInformation getAssetInformation(String aasId) {
        return getAasService(aasId).getAssetInformation();
    }

    @Override
    public File getThumbnail(String aasId) {
        return getAasService(aasId).getThumbnail();
    }

    @Override
    public void setThumbnail(String aasId, String fileName, String contentType, InputStream inputStream) {
        doWithService(aasId, s -> s.setThumbnail(fileName, contentType, inputStream));
    }

    @Override
    public void deleteThumbnail(String aasId) {
        doWithService(aasId, s -> s.deleteThumbnail());
    }

    private AasService getAasService(String aasId) {
        return findById(aasId).map(aasServiceFactory::create).orElseThrow(() -> new ElementDoesNotExistException(aasId));
    }

    private void doWithService(String aasId, Consumer<AasService> consumer) {
        AasService aasService = getAasService(aasId);
        consumer.accept(aasService);
        save(aasService.getAAS());
    }
}

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

package org.eclipse.digitaltwin.basyx.aasrepository.backend;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetInformation;
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.basyx.aasrepository.AasRepository;
import org.eclipse.digitaltwin.basyx.common.backend.InstanceScopedThreadSafeAccess;
import org.eclipse.digitaltwin.basyx.core.exceptions.CollidingIdentifierException;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.exceptions.MissingIdentifierException;
import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;

/**
 * A thread-safe wrapper for the {@link AasRepository}
 * 
 * @author mateusmolina
 */
public class ThreadSafeAasRepository implements AasRepository {

    private final AasRepository decoratedAasRepository;
    private final InstanceScopedThreadSafeAccess access = new InstanceScopedThreadSafeAccess();

    public ThreadSafeAasRepository(AasRepository decoratedRepository) {
        this.decoratedAasRepository = decoratedRepository;
    }

    @Override
    public CursorResult<List<AssetAdministrationShell>> getAllAas(PaginationInfo pInfo) {
        return decoratedAasRepository.getAllAas(pInfo);
    }

    @Override
    public AssetAdministrationShell getAas(String aasId) throws ElementDoesNotExistException {
        return access.read(() -> decoratedAasRepository.getAas(aasId), aasId);
    }

    @Override
    public void createAas(AssetAdministrationShell aas) throws CollidingIdentifierException, MissingIdentifierException {
        decoratedAasRepository.createAas(aas);
    }

    @Override
    public void deleteAas(String aasId) {
        access.write(() -> decoratedAasRepository.deleteAas(aasId), aasId);
    }

    @Override
    public void updateAas(String aasId, AssetAdministrationShell aas) {
        access.write(() -> decoratedAasRepository.updateAas(aasId, aas), aasId);
    }

    @Override
    public CursorResult<List<Reference>> getSubmodelReferences(String aasId, PaginationInfo pInfo) {
        return access.read(() -> decoratedAasRepository.getSubmodelReferences(aasId, pInfo), aasId);
    }

    @Override
    public void addSubmodelReference(String aasId, Reference submodelReference) {
        access.write(() -> decoratedAasRepository.addSubmodelReference(aasId, submodelReference), aasId);
    }

    @Override
    public void removeSubmodelReference(String aasId, String submodelId) {
        access.write(() -> decoratedAasRepository.removeSubmodelReference(aasId, submodelId), aasId);
    }

    @Override
    public void setAssetInformation(String aasId, AssetInformation aasInfo) throws ElementDoesNotExistException {
        access.write(() -> decoratedAasRepository.setAssetInformation(aasId, aasInfo), aasId);
    }

    @Override
    public AssetInformation getAssetInformation(String aasId) throws ElementDoesNotExistException {
        return access.read(() -> decoratedAasRepository.getAssetInformation(aasId), aasId);
    }

    @Override
    public File getThumbnail(String aasId) {
        return access.read(() -> decoratedAasRepository.getThumbnail(aasId), aasId);
    }

    @Override
    public void setThumbnail(String aasId, String fileName, String contentType, InputStream inputStream) {
        access.write(() -> decoratedAasRepository.setThumbnail(aasId, fileName, contentType, inputStream), aasId);
    }

    @Override
    public void deleteThumbnail(String aasId) {
        access.write(() -> decoratedAasRepository.deleteThumbnail(aasId), aasId);
    }

    @Override
    public String getName() {
        return decoratedAasRepository.getName();
    }

}

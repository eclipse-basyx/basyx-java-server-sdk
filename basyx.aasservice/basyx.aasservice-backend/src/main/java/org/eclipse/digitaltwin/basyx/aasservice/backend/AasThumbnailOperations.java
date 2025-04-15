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

package org.eclipse.digitaltwin.basyx.aasservice.backend;

import org.eclipse.digitaltwin.aas4j.v3.model.AssetInformation;
import org.eclipse.digitaltwin.aas4j.v3.model.Resource;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultResource;
import org.eclipse.digitaltwin.basyx.core.exceptions.FileDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.filerepository.FileRepository;
import org.eclipse.digitaltwin.basyx.core.filerepository.FileRepositoryHelper;

import java.io.File;
import java.io.InputStream;
import java.util.Optional;

/**
 * Collection of methods for handling AAS Thumbnails
 * 
 * @author mateusmolina
 */
public class AasThumbnailOperations {

    private final AasOperations aasOperations;
    private final FileRepository fileRepository;

    public AasThumbnailOperations(AasOperations aasOperations, FileRepository fileRepository) {
        this.aasOperations = aasOperations;
        this.fileRepository = fileRepository;
    }

    public File getThumbnail(String aasId) {
        return FileRepositoryHelper.fetchAndStoreFileLocally(fileRepository, getThumbnailResourcePathOrThrow(aasOperations.getAssetInformation(aasId)));
    }

    public void setThumbnail(String aasId, String fileName, String contentType, InputStream inputStream) {
        String filePath = FileRepositoryHelper.saveOrOverwriteFile(fileRepository, fileName, contentType, inputStream);
       aasOperations.setAssetInformation(aasId, configureAssetInformationThumbnail(aasOperations.getAssetInformation(aasId), contentType, filePath));
    }

    public void deleteThumbnail(String aasId) {
        AssetInformation assetInformation = aasOperations.getAssetInformation(aasId);
        FileRepositoryHelper.removeFileIfExists(fileRepository, getThumbnailResourcePathOrThrow(assetInformation));
        aasOperations.setAssetInformation(aasId, configureAssetInformationThumbnail(assetInformation, "", ""));
    }

    private static String getThumbnailResourcePathOrThrow(AssetInformation assetInformation) {
        return Optional.ofNullable(assetInformation).map(AssetInformation::getDefaultThumbnail).map(Resource::getPath).orElseThrow(FileDoesNotExistException::new);
    }

    private static AssetInformation configureAssetInformationThumbnail(AssetInformation assetInformation, String contentType, String filePath) {
        Resource resource = new DefaultResource();
        resource.setContentType(contentType);
        resource.setPath(filePath);
        assetInformation.setDefaultThumbnail(resource);
        return assetInformation;
    }
}

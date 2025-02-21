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

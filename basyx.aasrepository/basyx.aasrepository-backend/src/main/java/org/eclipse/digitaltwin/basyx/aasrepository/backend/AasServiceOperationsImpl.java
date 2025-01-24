package org.eclipse.digitaltwin.basyx.aasrepository.backend;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.model.AssetInformation;
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.springframework.beans.factory.annotation.Qualifier;

public class AasServiceOperationsImpl implements AasServiceOperations {

    private final AasServiceOperations aasServiceOperations;

    public AasServiceOperationsImpl(@Qualifier("backend") AasServiceOperations aasServiceBackend) {
        this.aasServiceOperations = aasServiceBackend;
    }

    @Override
    public CursorResult<List<Reference>> getSubmodelReferences(String aasId, PaginationInfo pInfo) {
        return aasServiceOperations.getSubmodelReferences(aasId, pInfo);
    }

    @Override
    public void addSubmodelReference(String aasId, Reference submodelReference) {
        aasServiceOperations.addSubmodelReference(aasId, submodelReference);
    }

    @Override
    public void removeSubmodelReference(String aasId, String submodelId) {
        aasServiceOperations.removeSubmodelReference(aasId, submodelId);
    }

    @Override
    public void setAssetInformation(String aasId, AssetInformation aasInfo) {
        aasServiceOperations.setAssetInformation(aasId, aasInfo);
    }

    @Override
    public AssetInformation getAssetInformation(String aasId) {
        return aasServiceOperations.getAssetInformation(aasId);
    }

    @Override
    public File getThumbnail(String aasId) {
        return aasServiceOperations.getThumbnail(aasId);
    }

    @Override
    public void setThumbnail(String aasId, String fileName, String contentType, InputStream inputStream) {
        aasServiceOperations.setThumbnail(aasId, fileName, contentType, inputStream);
    }

    @Override
    public void deleteThumbnail(String aasId) {
        aasServiceOperations.deleteThumbnail(aasId);
    }

}

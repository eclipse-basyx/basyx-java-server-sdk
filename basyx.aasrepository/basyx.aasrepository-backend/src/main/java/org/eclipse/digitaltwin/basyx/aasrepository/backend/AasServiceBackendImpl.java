package org.eclipse.digitaltwin.basyx.aasrepository.backend;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.model.AssetInformation;
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.springframework.beans.factory.annotation.Qualifier;

public class AasServiceBackendImpl implements AasServiceBackend {

    private final AasServiceBackend aasServiceBackend;

    public AasServiceBackendImpl(@Qualifier("backend") AasServiceBackend aasServiceBackend) {
        this.aasServiceBackend = aasServiceBackend;
    }

    @Override
    public CursorResult<List<Reference>> getSubmodelReferences(String aasId, PaginationInfo pInfo) {
        return aasServiceBackend.getSubmodelReferences(aasId, pInfo);
    }

    @Override
    public void addSubmodelReference(String aasId, Reference submodelReference) {
        aasServiceBackend.addSubmodelReference(aasId, submodelReference);
    }

    @Override
    public void removeSubmodelReference(String aasId, String submodelId) {
        aasServiceBackend.removeSubmodelReference(aasId, submodelId);
    }

    @Override
    public void setAssetInformation(String aasId, AssetInformation aasInfo) {
        aasServiceBackend.setAssetInformation(aasId, aasInfo);
    }

    @Override
    public AssetInformation getAssetInformation(String aasId) {
        return aasServiceBackend.getAssetInformation(aasId);
    }

    @Override
    public File getThumbnail(String aasId) {
        return aasServiceBackend.getThumbnail(aasId);
    }

    @Override
    public void setThumbnail(String aasId, String fileName, String contentType, InputStream inputStream) {
        aasServiceBackend.setThumbnail(aasId, fileName, contentType, inputStream);
    }

    @Override
    public void deleteThumbnail(String aasId) {
        aasServiceBackend.deleteThumbnail(aasId);
    }

}

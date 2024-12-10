package org.eclipse.digitaltwin.basyx.aasrepository.backend.mongodb;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.model.AssetInformation;
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.basyx.aasrepository.backend.AasServiceBackend;
import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.springframework.data.mongodb.core.MongoOperations;

public class AasServiceBackendImpl implements AasServiceBackend {

    private final MongoOperations mongoOperations;

    public AasServiceBackendImpl(MongoOperations mongoOperations) {
        this.mongoOperations = mongoOperations;
    }

    @Override
    public CursorResult<List<Reference>> getSubmodelReferences(String aasId, PaginationInfo pInfo) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getSubmodelReferences'");
    }

    @Override
    public void addSubmodelReference(String aasId, Reference submodelReference) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'addSubmodelReference'");
    }

    @Override
    public void removeSubmodelReference(String aasId, String submodelId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'removeSubmodelReference'");
    }

    @Override
    public void setAssetInformation(String aasId, AssetInformation aasInfo) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setAssetInformation'");
    }

    @Override
    public AssetInformation getAssetInformation(String aasId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAssetInformation'");
    }

    @Override
    public File getThumbnail(String aasId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getThumbnail'");
    }

    @Override
    public void setThumbnail(String aasId, String fileName, String contentType, InputStream inputStream) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setThumbnail'");
    }

    @Override
    public void deleteThumbnail(String aasId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteThumbnail'");
    }

}

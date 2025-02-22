package org.eclipse.digitaltwin.basyx.aasservice.backend;

import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetInformation;
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.basyx.aasservice.AasService;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.filerepository.FileRepository;
import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;

import java.io.File;
import java.io.InputStream;
import java.util.List;

public class CrudAasService implements AasService {

    private final AasBackend aasBackend;
    private final AasThumbnailOperations thumbnailOperations;
    private final String aasId;

    public CrudAasService(AasBackend aasBackend, FileRepository fileRepository, AssetAdministrationShell aas) {
        this(aasBackend, fileRepository, aas.getId());
        hostAas(aas);
    }

    public CrudAasService(AasBackend aasBackend, FileRepository fileRepository, String aasId) {
        this.aasBackend = aasBackend;
        this.thumbnailOperations = new AasThumbnailOperations(aasBackend, fileRepository);
        this.aasId = aasId;
    }

    @Override
    public AssetAdministrationShell getAAS() {
        return aasBackend.findById(aasId).orElseThrow(() -> new ElementDoesNotExistException(aasId));
    }

    @Override
    public CursorResult<List<Reference>> getSubmodelReferences(PaginationInfo pInfo) {
        return aasBackend.getSubmodelReferences(aasId, pInfo);
    }

    @Override
    public void addSubmodelReference(Reference submodelReference) {
        aasBackend.addSubmodelReference(aasId, submodelReference);
    }

    @Override
    public void removeSubmodelReference(String submodelId) {
        aasBackend.removeSubmodelReference(aasId, submodelId);
    }

    @Override
    public void setAssetInformation(AssetInformation aasInfo) {
        aasBackend.setAssetInformation(aasId, aasInfo);
    }

    @Override
    public AssetInformation getAssetInformation() {
        return aasBackend.getAssetInformation(aasId);
    }

    @Override
    public File getThumbnail() {
       return thumbnailOperations.getThumbnail(aasId);
    }

    @Override
    public void setThumbnail(String fileName, String contentType, InputStream inputStream) {
        thumbnailOperations.setThumbnail(aasId, fileName, contentType, inputStream);
    }

    @Override
    public void deleteThumbnail() {
        thumbnailOperations.deleteThumbnail(aasId);
    }

    private void hostAas(AssetAdministrationShell aas){
        aasBackend.save(aas);
    }
}

package org.eclipse.digitaltwin.basyx.aasrepository.backend;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetInformation;
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.basyx.aasrepository.AasRepository;
import org.eclipse.digitaltwin.basyx.common.backend.ThreadSafeAccess;
import org.eclipse.digitaltwin.basyx.core.exceptions.CollidingIdentifierException;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.exceptions.MissingIdentifierException;
import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;

public class ThreadSafeAasRepository implements AasRepository {

    private final AasRepository decoratedAasRepository;
    private final ThreadSafeAccess access = new ThreadSafeAccess();

    public ThreadSafeAasRepository(AasRepository decoratedRepository) {
        this.decoratedAasRepository = decoratedRepository;
    }

    @Override
    public CursorResult<List<AssetAdministrationShell>> getAllAas(PaginationInfo pInfo) {
        return access.read(decoratedAasRepository::getAllAas, pInfo);
    }

    @Override
    public AssetAdministrationShell getAas(String aasId) throws ElementDoesNotExistException {
        return access.read(decoratedAasRepository::getAas, aasId);
    }

    @Override
    public void createAas(AssetAdministrationShell aas) throws CollidingIdentifierException, MissingIdentifierException {
        access.write(decoratedAasRepository::createAas, aas);
    }

    @Override
    public void deleteAas(String aasId) {
        access.write(decoratedAasRepository::deleteAas, aasId);
    }

    @Override
    public void updateAas(String aasId, AssetAdministrationShell aas) {
        access.write(decoratedAasRepository::updateAas, aasId, aas);
    }

    @Override
    public CursorResult<List<Reference>> getSubmodelReferences(String aasId, PaginationInfo pInfo) {
        return access.read(decoratedAasRepository::getSubmodelReferences, aasId, pInfo);
    }

    @Override
    public void addSubmodelReference(String aasId, Reference submodelReference) {
        access.write(decoratedAasRepository::addSubmodelReference, aasId, submodelReference);
    }

    @Override
    public void removeSubmodelReference(String aasId, String submodelId) {
        access.write(decoratedAasRepository::removeSubmodelReference, aasId, submodelId);
    }

    @Override
    public void setAssetInformation(String aasId, AssetInformation aasInfo) throws ElementDoesNotExistException {
        access.write(decoratedAasRepository::setAssetInformation, aasId, aasInfo);
    }

    @Override
    public AssetInformation getAssetInformation(String aasId) throws ElementDoesNotExistException {
        return access.read(decoratedAasRepository::getAssetInformation, aasId);
    }

    @Override
    public File getThumbnail(String aasId) {
        return access.read(decoratedAasRepository::getThumbnail, aasId);
    }

    @Override
    public void setThumbnail(String aasId, String fileName, String contentType, InputStream inputStream) {
        access.write(decoratedAasRepository::setThumbnail, aasId, fileName, contentType, inputStream);
    }

    @Override
    public void deleteThumbnail(String aasId) {
        access.write(decoratedAasRepository::deleteThumbnail, aasId);
    }

    @Override
    public String getName() {
        return decoratedAasRepository.getName();
    }

}

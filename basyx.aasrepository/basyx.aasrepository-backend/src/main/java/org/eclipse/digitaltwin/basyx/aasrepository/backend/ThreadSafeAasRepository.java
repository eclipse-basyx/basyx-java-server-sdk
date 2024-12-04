package org.eclipse.digitaltwin.basyx.aasrepository.backend;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetInformation;
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.basyx.aasrepository.AasRepository;
import org.eclipse.digitaltwin.basyx.core.exceptions.CollidingIdentifierException;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.exceptions.MissingIdentifierException;
import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;

public class ThreadSafeAasRepository implements AasRepository {

    private final AasRepository decoratedAasRepository;
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public ThreadSafeAasRepository(AasRepository decoratedRepository) {
        this.decoratedAasRepository = decoratedRepository;
    }

    @Override
    public CursorResult<List<AssetAdministrationShell>> getAllAas(PaginationInfo pInfo) {
        lock.readLock().lock();
        try {
            return decoratedAasRepository.getAllAas(pInfo);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public AssetAdministrationShell getAas(String aasId) throws ElementDoesNotExistException {
        lock.readLock().lock();
        try {
            return decoratedAasRepository.getAas(aasId);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void createAas(AssetAdministrationShell aas) throws CollidingIdentifierException, MissingIdentifierException {
        lock.writeLock().lock();
        try {
            decoratedAasRepository.createAas(aas);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void deleteAas(String aasId) {
        lock.writeLock().lock();
        try {
            decoratedAasRepository.deleteAas(aasId);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void updateAas(String aasId, AssetAdministrationShell aas) {
        lock.writeLock().lock();
        try {
            decoratedAasRepository.updateAas(aasId, aas);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public CursorResult<List<Reference>> getSubmodelReferences(String aasId, PaginationInfo pInfo) {
        lock.readLock().lock();
        try {
            return decoratedAasRepository.getSubmodelReferences(aasId, pInfo);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void addSubmodelReference(String aasId, Reference submodelReference) {
        lock.writeLock().lock();
        try {
            decoratedAasRepository.addSubmodelReference(aasId, submodelReference);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void removeSubmodelReference(String aasId, String submodelId) {
        lock.writeLock().lock();
        try {
            decoratedAasRepository.removeSubmodelReference(aasId, submodelId);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void setAssetInformation(String aasId, AssetInformation aasInfo) throws ElementDoesNotExistException {
        lock.writeLock().lock();
        try {
            decoratedAasRepository.setAssetInformation(aasId, aasInfo);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public AssetInformation getAssetInformation(String aasId) throws ElementDoesNotExistException {
        lock.readLock().lock();
        try {
            return decoratedAasRepository.getAssetInformation(aasId);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public File getThumbnail(String aasId) {
        lock.readLock().lock();
        try {
            return decoratedAasRepository.getThumbnail(aasId);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void setThumbnail(String aasId, String fileName, String contentType, InputStream inputStream) {
        lock.writeLock().lock();
        try {
            decoratedAasRepository.setThumbnail(aasId, fileName, contentType, inputStream);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void deleteThumbnail(String aasId) {
        lock.writeLock().lock();
        try {
            decoratedAasRepository.deleteThumbnail(aasId);
        } finally {
            lock.writeLock().unlock();
        }
    }
}

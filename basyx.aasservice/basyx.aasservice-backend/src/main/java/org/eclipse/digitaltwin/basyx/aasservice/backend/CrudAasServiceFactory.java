package org.eclipse.digitaltwin.basyx.aasservice.backend;

import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.basyx.aasservice.AasService;
import org.eclipse.digitaltwin.basyx.aasservice.AasServiceFactory;
import org.eclipse.digitaltwin.basyx.core.filerepository.FileRepository;
import org.springframework.stereotype.Component;

@Component
public class CrudAasServiceFactory implements AasServiceFactory {

    private final AasBackend aasBackend;
    private final FileRepository fileRepository;

    public CrudAasServiceFactory(AasBackend aasBackend, FileRepository fileRepository) {
        this.aasBackend = aasBackend;
        this.fileRepository = fileRepository;
    }

    @Override
    public AasService create(AssetAdministrationShell aas) {
        return new CrudAasService(aasBackend, fileRepository, aas);
    }

    @Override
    public AasService create(String aasId) {
        return new CrudAasService(aasBackend, fileRepository, aasId);
    }
}

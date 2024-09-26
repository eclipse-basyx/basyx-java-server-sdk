package org.eclipse.digitaltwin.basyx.examples.basyxclient.processing;

import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.basyx.aasenvironment.client.ConnectedAasManager;
import org.eclipse.digitaltwin.basyx.aasservice.AasService;
import org.eclipse.digitaltwin.basyx.examples.basyxclient.model.MotorEntry;
import org.eclipse.digitaltwin.basyx.submodelservice.client.ConnectedSubmodelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class BaSyxSyncService implements EntryProcessor {

    private final Logger logger = LoggerFactory.getLogger(BaSyxSyncService.class);

    private final ConnectedAasManager connectedAasManager;

    public BaSyxSyncService(ConnectedAasManager connectedAasManager) {
        this.connectedAasManager = connectedAasManager;
    }

    @Override
    public void process(MotorEntry entry) {
        syncWithBaSyx(entry);
    }

    public void syncWithBaSyx(MotorEntry motorEntry) {
        if (!entryAlreadyPushed(motorEntry))
            pushEntryAasAndSubmodels(motorEntry);

        updateSubmodelsBasedOnEntry(motorEntry);
    }

    public AasService pushEntryAasAndSubmodels(MotorEntry entry) {
        logger.info("Pushing entry: {}", entry.getMotorId());

        AssetAdministrationShell aas = MotorAasBuilder.fromEntry(entry);
        connectedAasManager.createAas(aas);

        MotorAasBuilder.buildSubmodelsFromEntry(entry).forEach(sm -> connectedAasManager.createSubmodelInAas(aas.getId(), sm));

        return connectedAasManager.getAasService(aas.getId());
    }

    public boolean entryAlreadyPushed(MotorEntry entry) {
        try {
            connectedAasManager.getAasService(MotorAasBuilder.buildIdFromEntry(entry));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void updateSubmodelsBasedOnEntry(MotorEntry motorEntry) {
        logger.info("Updating submodels for entry: {}", motorEntry.getMotorId());

        List<Submodel> toUpdateSms = MotorAasBuilder.buildSubmodelsFromEntry(motorEntry);
        toUpdateSms.forEach(sm -> {
            ConnectedSubmodelService service = connectedAasManager.getSubmodelService(sm.getId());
            sm.getSubmodelElements().forEach(se -> service.updateSubmodelElement(se.getIdShort(), se));
        });
    }

}

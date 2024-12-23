/*******************************************************************************
 * Copyright (C) 2024 the Eclipse BaSyx Authors
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

public class BaSyxSyncService implements EntryProcessor {

    private final Logger logger = LoggerFactory.getLogger(BaSyxSyncService.class);

    private final ConnectedAasManager connectedAasManager;

    public BaSyxSyncService(ConnectedAasManager connectedAasManager) {
        this.connectedAasManager = connectedAasManager;
    }

    @Override
    public void process(List<MotorEntry> entries) {
        entries.forEach(this::syncWithBaSyx);
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
        logger.debug("Updating submodels for entry: {}", motorEntry.getMotorId());

        List<Submodel> toUpdateSms = MotorAasBuilder.buildSubmodelsFromEntry(motorEntry);
        toUpdateSms.forEach(sm -> {
            ConnectedSubmodelService service = connectedAasManager.getSubmodelService(sm.getId());
            sm.getSubmodelElements().forEach(se -> service.updateSubmodelElement(se.getIdShort(), se));
        });
    }

}

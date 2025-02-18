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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.KeyTypes;
import org.eclipse.digitaltwin.aas4j.v3.model.ReferenceElement;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElementCollection;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultKey;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultReference;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultReferenceElement;
import org.eclipse.digitaltwin.basyx.aasenvironment.client.ConnectedAasManager;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.eclipse.digitaltwin.basyx.examples.basyxclient.model.MotorEntry;
import org.eclipse.digitaltwin.basyx.submodelservice.client.ConnectedSubmodelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaSyxWarehouseService implements EntryProcessor {

    private final Logger logger = LoggerFactory.getLogger(BaSyxWarehouseService.class);

    private final ConnectedAasManager connectedAasManager;

    // managing only one warehouse
    private static final int WAREHOUSE_NUM = 0;

    private ConnectedSubmodelService overviewSubmodelService;

    public BaSyxWarehouseService(ConnectedAasManager connectedAasManager) {
        this.connectedAasManager = connectedAasManager;
    }

    @Override
    public void process(List<MotorEntry> entries) {
        if (!isWarehouldAlreadyPushed(WAREHOUSE_NUM)) {
            logger.info("No Warehouse found yet. Pushing a new warehouse to BaSyx...");
            pushWarehouseToBaSyx();
        }

        if (overviewSubmodelService == null)
            return;

        entries.forEach(this::updateEntryAisle);
    }

    public void pushWarehouseToBaSyx() {

        AssetAdministrationShell warehouseAas = WarehouseAasBuilder.build(WAREHOUSE_NUM);
        connectedAasManager.createAas(warehouseAas);

        Submodel overviewSm = WarehouseAasBuilder.buildOverviewSm(WAREHOUSE_NUM);
        connectedAasManager.createSubmodelInAas(warehouseAas.getId(), overviewSm);

        overviewSubmodelService = connectedAasManager.getSubmodelService(overviewSm.getId());
    }

    public void updateEntryAisle(MotorEntry entry) {
        String actualAisleIdShort = entry.getLocation();
        String motorId = MotorAasBuilder.buildIdFromEntry(entry);

        if (!isInWarehouse(actualAisleIdShort)) {
            removeMotorFromWarehouseIfPresent(motorId);
            return;
        }

        SubmodelElementCollection aisle = (SubmodelElementCollection) overviewSubmodelService.getSubmodelElement(actualAisleIdShort);
        List<ReferenceElement> motorRefs = getMotorRefs(aisle);

        if (!isNewMotor(motorRefs, motorId))
            return;

        List<ReferenceElement> newMotorRefs = addMotorToAisle(motorRefs, motorId);
        List<SubmodelElement> newSE = newMotorRefs.stream().map(SubmodelElement.class::cast).toList();

        aisle.setValue(newSE);
        overviewSubmodelService.updateSubmodelElement(actualAisleIdShort, aisle);

        logger.info("Motor {} added to warehouse.", motorId);
    }

    private void removeMotorFromWarehouseIfPresent(String motorId) {
        overviewSubmodelService.getSubmodelElements(new PaginationInfo(100, null)).getResult().stream().forEach(aisle -> removeMotorFromAisleIfPresent((SubmodelElementCollection) aisle, motorId));
    }

    private void removeMotorFromAisleIfPresent(SubmodelElementCollection aisle, String motorId) {
        List<ReferenceElement> motorRefs = getMotorRefs(aisle);
        Optional<ReferenceElement> foundReference = motorRefs.stream().filter(refEl -> refEl.getIdShort().equals(motorId)).findFirst();

        if (!foundReference.isPresent())
            return;

        List<ReferenceElement> newMotorRefs = new ArrayList<>(motorRefs);
        newMotorRefs.remove(foundReference.get());
        List<SubmodelElement> newRefs = newMotorRefs.stream().map(SubmodelElement.class::cast).toList();
        aisle.setValue(newRefs);
        overviewSubmodelService.updateSubmodelElement(aisle.getIdShort(), aisle);

        logger.info("Motor {} removed from {}.", motorId, aisle.getIdShort());
    }

    private List<ReferenceElement> getMotorRefs(SubmodelElement aisle) {
        SubmodelElementCollection aisleCol = (SubmodelElementCollection) aisle;
        return aisleCol.getValue().stream().map(ReferenceElement.class::cast).toList();
    }

    private boolean isNewMotor(List<ReferenceElement> motorRefs, String motorId) {
        return motorRefs.stream().map(ReferenceElement::getIdShort).noneMatch(motorId::equals);
    }

    private List<ReferenceElement> addMotorToAisle(List<ReferenceElement> motorRefs, String motorId) {
        DefaultReference ref = new DefaultReference.Builder().keys(new DefaultKey.Builder().type(KeyTypes.ASSET_ADMINISTRATION_SHELL).value(motorId).build()).build();
        List<ReferenceElement> newList = new ArrayList<>(motorRefs);
        newList.add(new DefaultReferenceElement.Builder().idShort(motorId).value(ref).build());
        return newList;
    }

    private boolean isInWarehouse(String actualAisle) {
        return actualAisle.startsWith("Aisle");
    }

    private boolean isWarehouldAlreadyPushed(int warehouseId) {
        try {
            connectedAasManager.getAasService(WarehouseAasBuilder.buildId(warehouseId));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}

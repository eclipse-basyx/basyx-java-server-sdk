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
import org.eclipse.digitaltwin.aas4j.v3.model.KeyTypes;
import org.eclipse.digitaltwin.aas4j.v3.model.ReferenceTypes;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultAssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultKey;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultProperty;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultReference;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSubmodel;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSubmodelElementCollection;
import org.eclipse.digitaltwin.basyx.examples.basyxclient.model.MotorEntry;

public final class MotorAasBuilder {
    public static final String ID_PREFIX = "http://example.com/motors/";

    private MotorAasBuilder() {
    }

    public static AssetAdministrationShell fromEntry(MotorEntry entry) {
        return new DefaultAssetAdministrationShell.Builder().id(buildIdFromEntry(entry)).idShort(entry.getMotorId()).build();
    }

    public static String buildIdFromEntry(MotorEntry entry) {
        return ID_PREFIX + entry.getMotorId();
    }

    public static List<Submodel> buildSubmodelsFromEntry(MotorEntry entry) {
        return List.of(buildNameplateSmFromEntry(entry), buildMaintenanceDataSmFromEntry(entry), buildStatusSmFromEntry(entry));
    }

    private static Submodel buildMaintenanceDataSmFromEntry(MotorEntry entry) {
        String smId = buildIdFromEntry(entry) + "/maintenanceData";

        List<SubmodelElement> seList = List.of(buildProperty("MaintenanceSchedule", toStringOrEmptyIfNull(entry.getMaintenanceSchedule())), buildProperty("LastMaintenance", toStringOrEmptyIfNull(entry.getLastMaintenance())),
                buildProperty("WarrantyPeriod", toStringOrEmptyIfNull(entry.getWarrantyPeriod())));

        return new DefaultSubmodel.Builder().id(smId).idShort("MaintenanceData").submodelElements(seList).build();
    }

    private static Submodel buildStatusSmFromEntry(MotorEntry entry) {
        String smId = buildIdFromEntry(entry) + "/status";

        List<SubmodelElement> seList = List.of(buildProperty("Status", entry.getStatus()), buildProperty("DateSold", toStringOrEmptyIfNull(entry.getDateSold())));

        return new DefaultSubmodel.Builder().id(smId).idShort("Status").submodelElements(seList).build();
    }

    private static Submodel buildNameplateSmFromEntry(MotorEntry entry) {
        String smId = buildIdFromEntry(entry) + "/nameplate";
        DefaultKey key = new DefaultKey.Builder().type(KeyTypes.CONCEPT_DESCRIPTION).value("https://admin-shell.io/zvei/nameplate/2/0/Nameplate").build();
        DefaultReference ref = new DefaultReference.Builder().type(ReferenceTypes.EXTERNAL_REFERENCE).keys(key).build();

        SubmodelElement assetSpecificProperties = new DefaultSubmodelElementCollection.Builder().idShort("AssetSpecificProperties").value(buildProperty("MotorType", entry.getMotorType())).build();

        List<SubmodelElement> seList = List.of(buildProperty("ManufacturerName", entry.getManufacturer()), buildProperty("DateOfManufacture", toStringOrEmptyIfNull(entry.getPurchaseDate())),
                buildProperty("SerialNumber", entry.getMotorId()), assetSpecificProperties);

        return new DefaultSubmodel.Builder().id(smId).idShort("Nameplate").semanticId(ref).submodelElements(seList).build();
    }

    private static SubmodelElement buildProperty(String idShort, String value) {
        return new DefaultProperty.Builder().idShort(idShort).value(value).build();
    }

    private static String toStringOrEmptyIfNull(Object o) {
        return o == null ? "" : o.toString();
    }
}

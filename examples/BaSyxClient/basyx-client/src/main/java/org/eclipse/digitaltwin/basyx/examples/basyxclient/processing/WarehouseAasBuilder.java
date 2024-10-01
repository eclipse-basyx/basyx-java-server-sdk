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
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElementCollection;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultAssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSubmodel;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSubmodelElementCollection;

public final class WarehouseAasBuilder {
    public static final String ID_PREFIX = "http://example.com/warehouse/";

    private WarehouseAasBuilder() {
    }

    public static AssetAdministrationShell build(int warehouseNum) {
        return new DefaultAssetAdministrationShell.Builder().id(buildId(warehouseNum)).idShort("Warehouse").build();
    }

    public static Submodel buildOverviewSm(int warehouseNum) {
        String smId = buildId(warehouseNum) + "/overview";
        List<SubmodelElement> aisles = IntStream.range(1, 11).mapToObj(WarehouseAasBuilder::buildAisle).collect(Collectors.toList());

        return new DefaultSubmodel.Builder().id(smId).idShort("Overview").submodelElements(aisles).build();
    }

    public static String buildId(int warehouseNum) {
        return ID_PREFIX + warehouseNum;
    }

    private static SubmodelElementCollection buildAisle(int aisleNum) {
        String aisleIdShort = "Aisle_" + aisleNum;
        return new DefaultSubmodelElementCollection.Builder().idShort(aisleIdShort).build();
    }

}

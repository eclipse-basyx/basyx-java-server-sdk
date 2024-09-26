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

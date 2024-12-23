package org.eclipse.digitaltwin.basyx.examples.basyxclient;

import java.nio.file.Path;
import java.util.List;

import org.eclipse.digitaltwin.basyx.aasenvironment.client.ConnectedAasManager;
import org.eclipse.digitaltwin.basyx.examples.basyxclient.configuration.BasyxSettings;
import org.eclipse.digitaltwin.basyx.examples.basyxclient.processing.BaSyxSyncService;
import org.eclipse.digitaltwin.basyx.examples.basyxclient.processing.BaSyxWarehouseService;
import org.eclipse.digitaltwin.basyx.examples.basyxclient.processing.DirectoryWatcher;
import org.eclipse.digitaltwin.basyx.examples.basyxclient.processing.EntryProcessor;

public final class DirectoryWatcherFactory {

    private DirectoryWatcherFactory() {
    }

    public static DirectoryWatcher build(Path pathToWatch) {
        BasyxSettings settings = buildBasyxSettingsFromEnv();
        ConnectedAasManager connectedAasManager = buildConnectedAasManager(settings);
        List<EntryProcessor> entryProcessors = buildEntryProcessors(connectedAasManager);

        return new DirectoryWatcher(entryProcessors, pathToWatch);
    }

    static BasyxSettings buildBasyxSettingsFromEnv() {
        String aasRegistryBaseUrl = System.getenv("AAS_REGISTRY_BASE_URL");
        String aasRepositoryBaseUrl = System.getenv("AAS_REPOSITORY_BASE_URL");
        String submodelRegistryBaseUrl = System.getenv("SUBMODEL_REGISTRY_BASE_URL");
        String submodelRepositoryBaseUrl = System.getenv("SUBMODEL_REPOSITORY_BASE_URL");

        return new BasyxSettings(aasRepositoryBaseUrl, submodelRepositoryBaseUrl, aasRegistryBaseUrl, submodelRegistryBaseUrl);
    }

    static ConnectedAasManager buildConnectedAasManager(BasyxSettings settings) {
        return new ConnectedAasManager(settings.aasRegistryBaseUrl(), settings.aasRepositoryBaseUrl(), settings.submodelRegistryBaseUrl(), settings.submodelRepositoryBaseUrl());
    }

    static List<EntryProcessor> buildEntryProcessors(ConnectedAasManager connectedAasManager) {
        BaSyxSyncService syncService = new BaSyxSyncService(connectedAasManager);
        BaSyxWarehouseService warehouseService = new BaSyxWarehouseService(connectedAasManager);

        return List.of(syncService, warehouseService);
    }
}

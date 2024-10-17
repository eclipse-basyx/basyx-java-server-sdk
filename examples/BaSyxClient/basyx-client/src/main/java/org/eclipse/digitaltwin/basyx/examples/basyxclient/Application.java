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

package org.eclipse.digitaltwin.basyx.examples.basyxclient;

import java.nio.file.Path;
import java.util.List;

import org.eclipse.digitaltwin.basyx.aasenvironment.client.ConnectedAasManager;
import org.eclipse.digitaltwin.basyx.examples.basyxclient.configuration.BasyxSettings;
import org.eclipse.digitaltwin.basyx.examples.basyxclient.processing.BaSyxSyncService;
import org.eclipse.digitaltwin.basyx.examples.basyxclient.processing.BaSyxWarehouseService;
import org.eclipse.digitaltwin.basyx.examples.basyxclient.processing.DirectoryWatcher;
import org.eclipse.digitaltwin.basyx.examples.basyxclient.processing.EntryProcessor;

public class Application {

	public static void main(String[] args) {
		BasyxSettings settings = buildBasyxSettingsFromEnv();
		ConnectedAasManager connectedAasManager = buildConnectedAasManager(settings);
		List<EntryProcessor> entryProcessors = buildEntryProcessors(connectedAasManager);

		Path path = Path.of("/ingest");
		DirectoryWatcher directoryWatcher = new DirectoryWatcher(entryProcessors, path);

		Thread thread = new Thread(directoryWatcher);
		thread.start();
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

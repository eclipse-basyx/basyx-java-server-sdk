/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.components.aas.executable;

import java.io.File;
import java.net.URISyntaxException;

import org.eclipse.basyx.components.aas.AASServerComponent;
import org.eclipse.basyx.components.aas.configuration.AASEventBackend;
import org.eclipse.basyx.components.aas.configuration.AASXUploadBackend;
import org.eclipse.basyx.components.aas.configuration.BaSyxAASServerConfiguration;
import org.eclipse.basyx.components.configuration.BaSyxContextConfiguration;
import org.eclipse.basyx.components.configuration.BaSyxMqttConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Starts an HTTP server that is able to receive AAS and submodels pushed from
 * remote <br>
 * They are made available at
 * <i>localhost:4000/aasServer/shells/${aasId}/aas</i>. Submodels are available
 * at
 * <i>localhost:4000/aasServer/shells/${aasId}/submodels/${submodelId}/submodel</i><br>
 * 
 * @author schnicke, espen
 */
public class AASServerExecutable {
	// Creates a Logger based on the current class
	private static Logger logger = LoggerFactory.getLogger(AASServerExecutable.class);

	public static void main(String[] args) throws URISyntaxException {
		logger.info("Starting BaSyx AASServer component...");
		// Load context configuration
		BaSyxContextConfiguration contextConfig = new BaSyxContextConfiguration();
		contextConfig.loadFromDefaultSource();

		// Load aas configuration
		BaSyxAASServerConfiguration aasConfig = new BaSyxAASServerConfiguration();
		aasConfig.loadFromDefaultSource();

		// Load the additional file path relative to the executed jar file
		String rootPath = new File(AASServerExecutable.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile().getPath();
		String docBasePath = rootPath;
		contextConfig.setDocBasePath(docBasePath);

		AASServerComponent component = new AASServerComponent(contextConfig, aasConfig);

		// If enabled, load mqtt configuration
		if (aasConfig.getAASEvents().equals(AASEventBackend.MQTT)) {
			BaSyxMqttConfiguration mqttConfig = new BaSyxMqttConfiguration();
			mqttConfig.loadFromDefaultSource();
			component.enableMQTT(mqttConfig);
		}
		
		// if enabled, load AASX uploader functionality
		if (aasConfig.getAASXUpload().equals(AASXUploadBackend.ENABLED)) {
			component.enableAASXUpload();
		}

		component.startComponent();

		logger.info("BaSyx AAS Server component started");
	}
}

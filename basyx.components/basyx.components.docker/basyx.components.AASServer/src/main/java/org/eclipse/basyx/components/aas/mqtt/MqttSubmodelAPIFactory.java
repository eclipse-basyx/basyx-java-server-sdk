/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.components.aas.mqtt;

import java.util.Set;

import org.eclipse.basyx.components.configuration.BaSyxMqttConfiguration;
import org.eclipse.basyx.components.configuration.MqttPersistence;
import org.eclipse.basyx.extensions.submodel.mqtt.MqttSubmodelAPI;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.restapi.api.ISubmodelAPI;
import org.eclipse.basyx.submodel.restapi.api.ISubmodelAPIFactory;
import org.eclipse.basyx.submodel.restapi.vab.VABSubmodelAPI;
import org.eclipse.basyx.vab.modelprovider.api.IModelProvider;
import org.eclipse.basyx.vab.modelprovider.lambda.VABLambdaProvider;
import org.eclipse.paho.client.mqttv3.MqttClientPersistence;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Api provider for constructing a new Submodel API that emits MQTT events
 * 
 * @author espen
 */
public class MqttSubmodelAPIFactory implements ISubmodelAPIFactory {
	private static Logger logger = LoggerFactory.getLogger(MqttSubmodelAPIFactory.class);

	private BaSyxMqttConfiguration config;

	/**
	 * Constructor with MQTT configuration for providing submodel APIs
	 * 
	 * @param config
	 */
	public MqttSubmodelAPIFactory(BaSyxMqttConfiguration config) {
		this.config = config;
	}

	@Override
	public ISubmodelAPI getSubmodelAPI(Submodel sm) {
		// Get the submodel's id from the given provider
		String smId = sm.getIdentification().getId();
		
		// Create the API
		IModelProvider provider = new VABLambdaProvider(sm);
		VABSubmodelAPI observedApi = new VABSubmodelAPI(provider);

		// Configure the API according to the given configs
		String brokerEndpoint = config.getServer();
		String clientId = smId;

		MqttSubmodelAPI api;
		try {
			MqttClientPersistence persistence = getMqttPersistenceFromConfig(config);
			if (config.getUser() != null) {
				String user = config.getUser();
				String pass = config.getPass();
				api = new MqttSubmodelAPI(observedApi, brokerEndpoint, clientId, user, pass.toCharArray(), persistence);
			} else {
				api = new MqttSubmodelAPI(observedApi, brokerEndpoint, clientId, persistence);
			}
			setWhitelist(api, smId);
		} catch (MqttException e) {
			logger.error("Could not create MqttSubmodelApi", e);
			return observedApi;
		}
		return api;
	}

	private static MqttClientPersistence getMqttPersistenceFromConfig(BaSyxMqttConfiguration config) {
		String persistenceFilePath = config.getPersistencePath();
		MqttPersistence persistenceType = config.getPersistenceType();
		if (isFilePersistenceType(persistenceType)) {
			return createMqttFilePersistence(persistenceFilePath);
		} else {
			return new MemoryPersistence();
		}
	}

	private static MqttClientPersistence createMqttFilePersistence(String persistenceFilePath) {
		if (!isFilePathSet(persistenceFilePath)) {
			return new MqttDefaultFilePersistence();
		} else {
			return new MqttDefaultFilePersistence(persistenceFilePath);
		}
	}

	private static boolean isFilePathSet(String persistenceFilePath) {
		return persistenceFilePath != null && !persistenceFilePath.isEmpty();
	}

	private static boolean isFilePersistenceType(MqttPersistence persistenceType) {
		return persistenceType == MqttPersistence.FILE;
	}

	private void setWhitelist(MqttSubmodelAPI api, String smId) {
		if (!config.isWhitelistEnabled(smId)) {
			// Do not use the whitelist if it has been disabled
			api.disableWhitelist();
			return;
		}

		// Read whitelist from configuration
		Set<String> whitelist = config.getWhitelist(smId);

		logger.info("Set MQTT whitelist for " + smId + " with " + whitelist.size() + " entries");
		api.setWhitelist(whitelist);
		api.enableWhitelist();
	}
}

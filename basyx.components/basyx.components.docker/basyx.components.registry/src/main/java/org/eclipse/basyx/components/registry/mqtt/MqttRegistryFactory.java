/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/


package org.eclipse.basyx.components.registry.mqtt;

import org.eclipse.basyx.components.configuration.BaSyxMqttConfiguration;
import org.eclipse.basyx.components.configuration.MqttPersistence;
import org.eclipse.basyx.extensions.aas.registration.mqtt.MqttRegistryServiceObserver;
import org.eclipse.basyx.registry.api.IRegistry;
import org.eclipse.basyx.registry.observing.ObservableRegistryService;
import org.eclipse.paho.client.mqttv3.MqttClientPersistence;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Factory for building a Mqtt-Registry model provider
 *
 * @author espen
 *
 */
public class MqttRegistryFactory {

	private static Logger logger = LoggerFactory.getLogger(MqttRegistryFactory.class);
	private static final String REGISTRY_CLIENT_ID = "aasRegistryClient";

	public IRegistry create(IRegistry registry, BaSyxMqttConfiguration mqttConfig) {
		return wrapRegistryInMqttObserver(registry, mqttConfig);
	}

	private static IRegistry wrapRegistryInMqttObserver(IRegistry registry, BaSyxMqttConfiguration mqttConfig) {
		ObservableRegistryService observedAPI = new ObservableRegistryService(registry);
		String brokerEndpoint = mqttConfig.getServer();
		MqttClientPersistence mqttPersistence = getMqttPersistenceFromConfig(mqttConfig);
		try {
			MqttRegistryServiceObserver mqttObserver = new MqttRegistryServiceObserver(brokerEndpoint,
					REGISTRY_CLIENT_ID, mqttPersistence);
			observedAPI.addObserver(mqttObserver);
		} catch (MqttException e) {
			logger.error("Could not establish MQTT connection for MqttAASRegistry", e);
		}

		return observedAPI;
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

}

/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.regression.registry;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.eclipse.basyx.aas.metamodel.map.descriptor.AASDescriptor;
import org.eclipse.basyx.aas.registration.proxy.AASRegistryProxy;
import org.eclipse.basyx.components.configuration.BaSyxContextConfiguration;
import org.eclipse.basyx.components.configuration.BaSyxMqttConfiguration;
import org.eclipse.basyx.components.configuration.BaSyxSQLConfiguration;
import org.eclipse.basyx.components.configuration.MqttPersistence;
import org.eclipse.basyx.components.registry.RegistryComponent;
import org.eclipse.basyx.extensions.aas.registration.mqtt.MqttAASRegistryServiceObserver;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.map.identifier.Identifier;
import org.eclipse.basyx.testsuite.regression.extensions.shared.mqtt.MqttTestListener;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import io.moquette.broker.Server;
import io.moquette.broker.config.ClasspathResourceLoader;
import io.moquette.broker.config.IConfig;
import io.moquette.broker.config.IResourceLoader;
import io.moquette.broker.config.ResourceLoaderConfig;

/**
 * Tests, if the RegistryComponent Mqtt-Event feature is enabled for the
 * possible backend configurations
 * 
 * @author espen
 *
 */
public class TestMqttRegistryBackend {
	protected static String registryUrl;

	protected static BaSyxMqttConfiguration mqttConfig;
	protected static Server mqttBroker;

	protected static AASRegistryProxy aasRegistryProxy;

	protected MqttTestListener listener;

	/**
	 * Sets up the MQTT broker and AASRegistryService for tests
	 */
	@BeforeClass
	public static void setUpClass() throws MqttException, IOException {
		startMqttBroker();
		mqttConfig = createMqttConfig();
		aasRegistryProxy = createRegistryProxy();
	}

	@AfterClass
	public static void tearDownClass() {
		mqttBroker.stopServer();
	}

	@Before
	public void setUp() {
		listener = new MqttTestListener();
		mqttBroker.addInterceptHandler(listener);
	}

	@Test
	public void testEventsWithInMemoryBackend() {
		RegistryComponent inMemoryRegistryComponent = createInMemoryRegistryComponent();
		testMqttEventForRegistryComponent(inMemoryRegistryComponent);
	}

	@Test
	public void testEventsWithSQLBackend() {
		RegistryComponent sqlRegistryComponent = createSQLRegistryComponent();
		testMqttEventForRegistryComponent(sqlRegistryComponent);
	}

	protected void testMqttEventForRegistryComponent(RegistryComponent registryComponent) {
		registryComponent.enableMQTT(mqttConfig);
		registryComponent.startComponent();

		AASDescriptor aasDescriptor = createTestAASDescriptor();
		aasRegistryProxy.register(aasDescriptor);

		assertEquals(MqttAASRegistryServiceObserver.TOPIC_REGISTERAAS, listener.lastTopic);

		registryComponent.stopComponent();
	}

	private static AASRegistryProxy createRegistryProxy() {
		registryUrl = new BaSyxContextConfiguration().getUrl();
		return new AASRegistryProxy(registryUrl);
	}

	private static BaSyxMqttConfiguration createMqttConfig() {
		BaSyxMqttConfiguration config = new BaSyxMqttConfiguration();
		config.setServer("tcp://localhost:" + mqttBroker.getPort());
		config.setPersistenceType(MqttPersistence.INMEMORY);
		return config;
	}

	private static void startMqttBroker() throws IOException {
		mqttBroker = new Server();
		IResourceLoader classpathLoader = new ClasspathResourceLoader();
		final IConfig classPathConfig = new ResourceLoaderConfig(classpathLoader);
		mqttBroker.startServer(classPathConfig);
	}

	private RegistryComponent createSQLRegistryComponent() {
		BaSyxSQLConfiguration sqlConfig = new BaSyxSQLConfiguration();
		BaSyxContextConfiguration contextConfig = new BaSyxContextConfiguration();
		RegistryComponent registryComponent = new RegistryComponent(contextConfig, sqlConfig);
		return registryComponent;
	}

	private static AASDescriptor createTestAASDescriptor() {
		Identifier aasIdentifier = new Identifier(IdentifierType.CUSTOM, "testAAS");
		String aasEndpoint = "http://localhost:8080/aasList/" + aasIdentifier.getId() + "/aas";
		AASDescriptor aasDescriptor = new AASDescriptor(aasIdentifier, aasEndpoint);
		return aasDescriptor;
	}

	private RegistryComponent createInMemoryRegistryComponent() {
		RegistryComponent registryComponent = new RegistryComponent();
		registryComponent.enableMQTT(mqttConfig);
		return registryComponent;
	}
}

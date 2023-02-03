package org.eclipse.digitaltwin.basyx.aasrepository.feature.mqtt;

/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
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

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.eclipse.digitaltwin.aas4j.v3.dataformat.DeserializationException;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.json.JsonDeserializer;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultAssetAdministrationShell;
import org.eclipse.digitaltwin.basyx.aasrepository.AasRepository;
import org.eclipse.digitaltwin.basyx.aasrepository.AasRepositoryFactory;
import org.eclipse.digitaltwin.basyx.aasrepository.InMemoryAasRepositoryFactory;
import org.eclipse.digitaltwin.basyx.aasrepository.feature.mqtt.encoding.Base64URLEncoder;
import org.eclipse.digitaltwin.basyx.aasrepository.feature.mqtt.encoding.URLEncoder;
import org.eclipse.digitaltwin.basyx.aasservice.backend.InMemoryAasServiceFactory;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import io.moquette.broker.Server;
import io.moquette.broker.config.ClasspathResourceLoader;
import io.moquette.broker.config.IConfig;
import io.moquette.broker.config.IResourceLoader;
import io.moquette.broker.config.ResourceLoaderConfig;

/**
 * Tests events emitting with the MqttAASAggregatorObserver
 *
 * @author haque, siebert, schnicke
 *
 */
public class TestMqttV2AASAggregatorObserver {
	private static Server mqttBroker;
	private static MqttClient mqttClient;
	private static MqttTestListener listener;
	private static MqttAasRepositoryTopicFactory topicFactory = new MqttAasRepositoryTopicFactory(new Base64URLEncoder());

	private static AasRepository aasRepository;

	@BeforeClass
	public static void setUpClass() throws MqttException, IOException {
		mqttBroker = startBroker();

		listener = configureInterceptListener(mqttBroker);

		mqttClient = createAndConnectClient();

		aasRepository = createMqttAasRepository(mqttClient);
	}

	@AfterClass
	public static void tearDownClass() {
		mqttBroker.removeInterceptHandler(listener);
		mqttBroker.stopServer();
	}

	@Test
	public void createAasEvent() throws DeserializationException {
		AssetAdministrationShell shell = createAasDummy();
		aasRepository.createAas(shell);

		assertEquals(topicFactory.createCreateAASTopic(aasRepository.getName()), listener.lastTopic);
		assertEquals(shell, deserializePayload(listener.lastPayload));
	}

	@Test
	public void deleteAasEvent() throws DeserializationException {
		AssetAdministrationShell shell = createAasDummy();
		aasRepository.createAas(shell);
		aasRepository.deleteAas(shell.getId());

		assertEquals(topicFactory.createDeleteAASTopic(aasRepository.getName()), listener.lastTopic);
		assertEquals(shell, deserializePayload(listener.lastPayload));
	}

	private AssetAdministrationShell deserializePayload(String payload) throws DeserializationException {
		return new JsonDeserializer().readReferable(payload, AssetAdministrationShell.class);
	}

	private AssetAdministrationShell createAasDummy() {
		return new DefaultAssetAdministrationShell.Builder().id("arbitrary").build();
	}

	private static AasRepository createMqttAasRepository(MqttClient client) {
		AasRepositoryFactory repoFactory = new InMemoryAasRepositoryFactory(new InMemoryAasServiceFactory());

		return new MqttAasRepositoryFactory(repoFactory, client, new MqttAasRepositoryTopicFactory(new URLEncoder())).create();
	}

	private static MqttTestListener configureInterceptListener(Server broker) {
		MqttTestListener testListener = new MqttTestListener();
		broker.addInterceptHandler(testListener);

		return testListener;
	}

	private static MqttClient createAndConnectClient() throws MqttException, MqttSecurityException {
		MqttClient client = new MqttClient("tcp://localhost:1884", "testClient");
		client.connect();
		return client;
	}

	private static Server startBroker() throws IOException {
		Server broker = new Server();
		IResourceLoader classpathLoader = new ClasspathResourceLoader();

		IConfig classPathConfig = new ResourceLoaderConfig(classpathLoader);
		broker.startServer(classPathConfig);

		return broker;
	}
}

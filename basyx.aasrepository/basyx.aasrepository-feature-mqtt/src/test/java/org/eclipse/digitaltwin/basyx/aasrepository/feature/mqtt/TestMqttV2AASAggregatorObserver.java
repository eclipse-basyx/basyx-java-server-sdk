/*******************************************************************************
 * Copyright (C) 2025 the Eclipse BaSyx Authors
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
package org.eclipse.digitaltwin.basyx.aasrepository.feature.mqtt;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.dataformat.core.DeserializationException;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.json.JsonDeserializer;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultAssetAdministrationShell;
import org.eclipse.digitaltwin.basyx.aasrepository.AasRepository;
import org.eclipse.digitaltwin.basyx.aasrepository.AasRepositoryFactory;
import org.eclipse.digitaltwin.basyx.aasrepository.DummyAasFactory;
import org.eclipse.digitaltwin.basyx.aasrepository.backend.CrudAasRepositoryFactory;
import org.eclipse.digitaltwin.basyx.aasservice.backend.InMemoryAasBackend;
import org.eclipse.digitaltwin.basyx.common.mqttcore.encoding.Base64URLEncoder;
import org.eclipse.digitaltwin.basyx.common.mqttcore.encoding.URLEncoder;
import org.eclipse.digitaltwin.basyx.common.mqttcore.listener.MqttTestListener;
import org.eclipse.digitaltwin.basyx.core.filerepository.InMemoryFileRepository;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
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
 * @author haque, siebert, schnicke, danish
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
		AssetAdministrationShell shell = createAasDummy("createAasEventId");
		aasRepository.createAas(shell);
		
		assertEquals(topicFactory.createCreateAASTopic(aasRepository.getName()), listener.lastTopic);
		assertEquals(shell, deserializePayload(listener.lastPayload));
	}

	@Test
	public void updateAasEvent() throws DeserializationException {
		AssetAdministrationShell shell = createAasDummy("updateAasEventId");
		aasRepository.createAas(shell);

		addSubmodelReferenceToAas(shell);

		aasRepository.updateAas(shell.getId(), shell);

		assertEquals(topicFactory.createUpdateAASTopic(aasRepository.getName()), listener.lastTopic);
		assertEquals(shell, deserializePayload(listener.lastPayload));
	}

	@Test
	public void deleteAasEvent() throws DeserializationException {
		AssetAdministrationShell shell = createAasDummy("deleteAasEventId");
		aasRepository.createAas(shell);
		aasRepository.deleteAas(shell.getId());

		assertEquals(topicFactory.createDeleteAASTopic(aasRepository.getName()), listener.lastTopic);
		assertEquals(shell, deserializePayload(listener.lastPayload));
	}
	
	@Test
	public void addSubmodelReferenceEvent() throws DeserializationException {
		AssetAdministrationShell shell = createAasDummy("createAasSubmodelRefEventId");
		aasRepository.createAas(shell);
		
		Reference submodelReference = DummyAasFactory.createDummyReference(DummyAasFactory.DUMMY_SUBMODEL_ID);
		aasRepository.addSubmodelReference(shell.getId(), submodelReference);
		
		assertEquals(topicFactory.createCreateAASSubmodelReferenceTopic(aasRepository.getName(), DummyAasFactory.DUMMY_SUBMODEL_ID), listener.lastTopic);
		assertEquals(shell, deserializePayload(listener.lastPayload));
	}	
	
	@Test
	public void removeSubmodelReferenceEvent() throws DeserializationException {
		AssetAdministrationShell shell = createAasWithSubmodelReference("removeAasSubmodelRefEventId");
		aasRepository.createAas(shell);
		
		aasRepository.removeSubmodelReference(shell.getId(), DummyAasFactory.DUMMY_SUBMODEL_ID);
		
		assertEquals(topicFactory.createDeleteAASSubmodelReferenceTopic(aasRepository.getName(), DummyAasFactory.DUMMY_SUBMODEL_ID), listener.lastTopic);
		assertEquals(shell, deserializePayload(listener.lastPayload));
	}	

	private AssetAdministrationShell deserializePayload(String payload) throws DeserializationException {
		return new JsonDeserializer().read(payload, AssetAdministrationShell.class);
	}

	private void addSubmodelReferenceToAas(AssetAdministrationShell shell) {
		List<Reference> submodelReferences = Arrays.asList(DummyAasFactory.createDummyReference("dummySubmodelId1"));
		shell.setSubmodels(submodelReferences);
	}

	private AssetAdministrationShell createAasDummy(String aasId) {
		return new DefaultAssetAdministrationShell.Builder().id(aasId)
				.build();
	}
	
	private AssetAdministrationShell createAasWithSubmodelReference(String aasId) {
		Reference submodelReference = DummyAasFactory.createDummyReference(DummyAasFactory.DUMMY_SUBMODEL_ID);
		
		List<Reference> submodelReferences = new ArrayList<Reference>();
		submodelReferences.add(submodelReference);
		
		return new DefaultAssetAdministrationShell.Builder().id(aasId).submodels(submodelReferences)
				.build();
	}

	private static AasRepository createMqttAasRepository(MqttClient client) {
		AasRepositoryFactory repoFactory = CrudAasRepositoryFactory.builder().backend(new InMemoryAasBackend()).fileRepository(new InMemoryFileRepository()).buildFactory();

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

	@Test
	public void checkTCPConnectionWithoutCredentials() throws Exception {
		MqttAasRepositoryConfiguration config = new MqttAasRepositoryConfiguration();
		MqttConnectOptions options = config.mqttConnectOptions("", "");
		IMqttClient client = config.mqttClient(
				"test-client",
				"localhost",
				1884,
				"tcp",
				options);
		assertTrue(client.isConnected());
		client.disconnect();
		client.close();
	}

	@Test
	public void checkTCPConnectionWitCredentials() throws Exception {
		MqttAasRepositoryConfiguration config = new MqttAasRepositoryConfiguration();
		MqttConnectOptions options = config.mqttConnectOptions("testuser", "passwd");
		IMqttClient client = config.mqttClient(
				"test-client",
				"localhost",
				1884,
				"tcp",
				options);
		assertTrue(client.isConnected());
		client.disconnect();
		client.close();
	}

	@Test
	public void checkTCPConnectionWitWrongCredentials() throws Exception {
		MqttAasRepositoryConfiguration config = new MqttAasRepositoryConfiguration();
		MqttConnectOptions options = config.mqttConnectOptions("testuser", "false");
		boolean authentication_failed = false;
		try {
			IMqttClient client = config.mqttClient(
					"test-client",
					"localhost",
					1884,
					"tcp",
					options);
		} catch (MqttException e) {
			if (MqttException.REASON_CODE_FAILED_AUTHENTICATION == e.getReasonCode()) {
				authentication_failed = true;
			}
		}
		assertTrue(authentication_failed);
	}

	@Test
	public void checkWSConnectionWithoutCredentials() throws Exception {
		MqttAasRepositoryConfiguration config = new MqttAasRepositoryConfiguration();
		MqttConnectOptions options = config.mqttConnectOptions("", "");
		IMqttClient client = config.mqttClient(
				"test-client",
				"localhost",
				8080,
				"ws",
				options);
		assertTrue(client.isConnected());
		client.disconnect();
		client.close();
	}

	@Test
	public void checkWSConnectionWitCredentials() throws Exception {
		MqttAasRepositoryConfiguration config = new MqttAasRepositoryConfiguration();
		MqttConnectOptions options = config.mqttConnectOptions("testuser", "passwd");
		IMqttClient client = config.mqttClient(
				"test-client",
				"localhost",
				8080,
				"ws",
				options);
		assertTrue(client.isConnected());
		client.disconnect();
		client.close();
	}

}

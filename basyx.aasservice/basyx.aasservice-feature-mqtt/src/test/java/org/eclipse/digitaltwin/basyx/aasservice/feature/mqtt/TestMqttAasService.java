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

package org.eclipse.digitaltwin.basyx.aasservice.feature.mqtt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.moquette.broker.Server;
import io.moquette.broker.config.ClasspathResourceLoader;
import io.moquette.broker.config.IConfig;
import io.moquette.broker.config.IResourceLoader;
import io.moquette.broker.config.ResourceLoaderConfig;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.core.DeserializationException;
import org.eclipse.digitaltwin.aas4j.v3.model.*;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultAssetInformation;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultResource;
import org.eclipse.digitaltwin.basyx.aasrepository.AasRepository;
import org.eclipse.digitaltwin.basyx.aasrepository.backend.CrudAasRepositoryFactory;
import org.eclipse.digitaltwin.basyx.aasservice.AasService;
import org.eclipse.digitaltwin.basyx.aasservice.AasServiceFactory;
import org.eclipse.digitaltwin.basyx.aasservice.AasServiceSuite;
import org.eclipse.digitaltwin.basyx.aasservice.DummyAssetAdministrationShellFactory;
import org.eclipse.digitaltwin.basyx.aasservice.backend.CrudAasServiceFactory;
import org.eclipse.digitaltwin.basyx.aasservice.backend.InMemoryAasBackend;
import org.eclipse.digitaltwin.basyx.common.mqttcore.encoding.URLEncoder;
import org.eclipse.digitaltwin.basyx.common.mqttcore.listener.MqttTestListener;
import org.eclipse.digitaltwin.basyx.core.filerepository.FileMetadata;
import org.eclipse.digitaltwin.basyx.core.filerepository.FileRepository;
import org.eclipse.digitaltwin.basyx.core.filerepository.InMemoryFileRepository;
import org.eclipse.digitaltwin.basyx.http.Aas4JHTTPSerializationExtension;
import org.eclipse.digitaltwin.basyx.http.BaSyxHTTPConfiguration;
import org.eclipse.digitaltwin.basyx.http.SerializationExtension;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestMqttAasService extends AasServiceSuite {

	private static Server mqttBroker;
	private static MqttClient mqttClient;
	private static MqttTestListener listener;
	private static MqttAasServiceTopicFactory topicFactory = new MqttAasServiceTopicFactory(new URLEncoder());

	private static AasRepository aasRepository;
	private static AasServiceFactory mqttAasServiceFactory;

	private static ObjectMapper objectMapper;
	
	private static FileRepository fileRepository;
	
	@BeforeClass
	public static void setUpClass() throws MqttException, IOException {
		objectMapper = configureObjectMapper();
		mqttBroker = startBroker();
		listener = configureInterceptListener(mqttBroker);
		mqttClient = createAndConnectClient();

		aasRepository = createMqttAasRepository();
		mqttAasServiceFactory = createMqttAasServiceFactory(mqttClient);
	}

	@AfterClass
	public static void tearDownClass() {
		mqttBroker.removeInterceptHandler(listener);
		mqttBroker.stopServer();
	}

	@Override
	protected AasService getAasService(AssetAdministrationShell shell) {
		return mqttAasServiceFactory.create(shell);
	}
	
	@Override
	protected AasService getAasServiceWithThumbnail() throws IOException {
		AssetAdministrationShell expected = DummyAssetAdministrationShellFactory.createForThumbnail();
		AasService aasServiceWithThumbnail = getAasService(expected);

		FileMetadata defaultThumbnail = new FileMetadata("dummyImgA.jpeg", "", createDummyImageIS_A());
		
		String thumbnailFilePath = fileRepository.save(defaultThumbnail);
		
		Resource defaultResource = new DefaultResource.Builder().path(thumbnailFilePath).contentType("").build();
		AssetInformation defaultAasAssetInformation = aasServiceWithThumbnail.getAssetInformation();
		defaultAasAssetInformation.setDefaultThumbnail(defaultResource);
		
		aasServiceWithThumbnail.setAssetInformation(defaultAasAssetInformation);
	
		return aasServiceWithThumbnail;
	}
	
	private static AasServiceFactory createMqttAasServiceFactory(MqttClient client) {
		fileRepository = new InMemoryFileRepository();
		AasServiceFactory serviceFactory = new CrudAasServiceFactory(new InMemoryAasBackend(), fileRepository);
		MqttAasServiceFeature mqttFeature = new MqttAasServiceFeature(client, aasRepository, objectMapper);
		
		return mqttFeature.decorate(serviceFactory);
	}

	@Override
	@Test
	public void setAssetInformation() {
		AssetAdministrationShell shell = DummyAssetAdministrationShellFactory.create();
		AasService aasService = getAasService(shell);
		AssetInformation assetInfo = createDummyAssetInformation();
		aasService.setAssetInformation(assetInfo);
		String repoId = aasRepository.getName();

		assertEquals(topicFactory.createSetAssetInformationTopic(repoId, shell.getId()), listener.lastTopic);
		assertEquals(serialize(assetInfo), listener.lastPayload);
	}

	private AssetInformation createDummyAssetInformation() {
		AssetInformation assetInfo = new DefaultAssetInformation.Builder().assetKind(AssetKind.INSTANCE)
				.globalAssetId("assetIDTestKey")
				.build();
		return assetInfo;
	}

	@Test
	public void addSubmodelReferenceEvent() throws DeserializationException, JsonProcessingException {
		AssetAdministrationShell shell = DummyAssetAdministrationShellFactory.create();
		AasService aasService = getAasService(shell);

		Reference submodelReference = DummyAssetAdministrationShellFactory.submodelReference;
		aasService.addSubmodelReference(submodelReference);
		String repoId = aasRepository.getName();

		assertEquals(topicFactory.createAddSubmodelReferenceTopic(repoId, shell.getId()), listener.lastTopic);
		assertEquals(serialize(submodelReference), listener.lastPayload);
	}

	private String serialize(Object obj) {
		try {
			return objectMapper.writeValueAsString(obj);
		} catch (JsonProcessingException ignore) {
			ignore.printStackTrace();
			throw new RuntimeException();
		}
	}

	@Test
	public void removeSubmodelReferenceEvent() throws DeserializationException, JsonProcessingException {
		AssetAdministrationShell shell = DummyAssetAdministrationShellFactory.create();

		AasService aasService = getAasService(shell);
		String repoId = aasRepository.getName();

		DummyAssetAdministrationShellFactory.addDummySubmodelReference(aasService.getAAS());
		aasService.removeSubmodelReference(DummyAssetAdministrationShellFactory.SUBMODEL_ID);

		assertEquals(topicFactory.createRemoveSubmodelReferenceTopic(repoId, shell.getId()), listener.lastTopic);
		assertEquals(serialize(DummyAssetAdministrationShellFactory.submodelReference), listener.lastPayload);
	}
	
	private static ObjectMapper configureObjectMapper() {
		List<SerializationExtension> extensions = Arrays.asList(new Aas4JHTTPSerializationExtension());

		return new BaSyxHTTPConfiguration().jackson2ObjectMapperBuilder(extensions).build();
	}

	private static AasRepository createMqttAasRepository() {
		return CrudAasRepositoryFactory.builder().backend(new InMemoryAasBackend()).fileRepository(new InMemoryFileRepository()).create();
	}

	private static MqttClient createAndConnectClient() throws MqttException, MqttSecurityException {
		MqttClient client = new MqttClient("tcp://localhost:1884", "testClient");
		client.connect();
		return client;
	}

	private static MqttTestListener configureInterceptListener(Server broker) {
		MqttTestListener testListener = new MqttTestListener();
		broker.addInterceptHandler(testListener);

		return testListener;
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
		MqttConfiguration config = new MqttConfiguration();
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
		MqttConfiguration config = new MqttConfiguration();
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
		MqttConfiguration config = new MqttConfiguration();
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
		MqttConfiguration config = new MqttConfiguration();
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
		MqttConfiguration config = new MqttConfiguration();
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

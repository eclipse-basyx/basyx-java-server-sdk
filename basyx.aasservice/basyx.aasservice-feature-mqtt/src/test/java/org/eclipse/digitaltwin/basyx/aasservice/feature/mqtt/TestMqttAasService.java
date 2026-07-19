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
import org.eclipse.digitaltwin.basyx.common.mqttcore.MqttBrokerTestSupport;
import org.eclipse.digitaltwin.basyx.common.mqttcore.encoding.Base64URLEncoder;
import org.eclipse.digitaltwin.basyx.common.mqttcore.listener.MqttTestListener;
import org.eclipse.digitaltwin.basyx.common.mqttcore.listener.MqttTestListener.MqttEvent;
import org.eclipse.digitaltwin.basyx.core.filerepository.FileRepository;
import org.eclipse.digitaltwin.basyx.core.filerepository.FileRepositoryHelper;
import org.eclipse.digitaltwin.basyx.core.filerepository.InMemoryFileRepository;
import org.eclipse.digitaltwin.basyx.http.Aas4JHTTPSerializationExtension;
import org.eclipse.digitaltwin.basyx.http.BaSyxHTTPConfiguration;
import org.eclipse.digitaltwin.basyx.http.SerializationExtension;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TestMqttAasService extends AasServiceSuite {

	private static MqttBrokerTestSupport mqttBroker;
	private static MqttClient mqttClient;
	private static MqttTestListener listener;
	private static MqttAasServiceTopicFactory topicFactory = new MqttAasServiceTopicFactory(new Base64URLEncoder());

	private static AasRepository aasRepository;
	private static AasServiceFactory mqttAasServiceFactory;

	private static ObjectMapper objectMapper;
	
	private static FileRepository fileRepository;
	
	@BeforeClass
	public static void setUpClass() throws MqttException, IOException {
		objectMapper = configureObjectMapper();
		mqttBroker = MqttBrokerTestSupport.start();
		listener = mqttBroker.listener();
		mqttClient = mqttBroker.connectClient();

		aasRepository = createMqttAasRepository();
		mqttAasServiceFactory = createMqttAasServiceFactory(mqttClient);
	}

	@AfterClass
	public static void tearDownClass() throws MqttException {
		mqttBroker.close();
	}

	@Override
	protected AasService getAasService(AssetAdministrationShell shell) {
		return mqttAasServiceFactory.create(shell);
	}
	
	@Override
	protected AasService getAasServiceWithThumbnail() throws IOException {
		AssetAdministrationShell expected = DummyAssetAdministrationShellFactory.createForThumbnail();
		AasService aasServiceWithThumbnail = getAasService(expected);

		String thumbnailFilePath = FileRepositoryHelper.saveOrOverwriteFile(fileRepository, "dummyImgA.jpeg", "", createDummyImageIS_A());
		
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

		MqttEvent event = listener.awaitEvent(topicFactory.createSetAssetInformationTopic(repoId, shell.getId()));
		assertEquals(serialize(assetInfo), event.payload());
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

		MqttEvent event = listener.awaitEvent(topicFactory.createAddSubmodelReferenceTopic(repoId, shell.getId()));
		assertEquals(serialize(submodelReference), event.payload());
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

		MqttEvent event = listener.awaitEvent(topicFactory.createRemoveSubmodelReferenceTopic(repoId, shell.getId()));
		assertEquals(serialize(DummyAssetAdministrationShellFactory.submodelReference), event.payload());
	}
	
	private static ObjectMapper configureObjectMapper() {
		List<SerializationExtension> extensions = Arrays.asList(new Aas4JHTTPSerializationExtension());

		return new BaSyxHTTPConfiguration().jackson2ObjectMapperBuilder(extensions).build();
	}

	private static AasRepository createMqttAasRepository() {
		return CrudAasRepositoryFactory.builder().backend(new InMemoryAasBackend()).fileRepository(new InMemoryFileRepository()).create();
	}

}

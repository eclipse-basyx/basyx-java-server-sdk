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
import org.eclipse.digitaltwin.basyx.common.mqttcore.MqttBrokerTestSupport;
import org.eclipse.digitaltwin.basyx.common.mqttcore.encoding.Base64URLEncoder;
import org.eclipse.digitaltwin.basyx.common.mqttcore.listener.MqttTestListener;
import org.eclipse.digitaltwin.basyx.common.mqttcore.listener.MqttTestListener.MqttEvent;
import org.eclipse.digitaltwin.basyx.core.filerepository.InMemoryFileRepository;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;


/**
 * Tests events emitting with the MqttAASAggregatorObserver
 *
 * @author haque, siebert, schnicke, danish
 *
 */
public class TestMqttV2AASAggregatorObserver {
	private static MqttBrokerTestSupport mqttBroker;
	private static MqttClient mqttClient;
	private static MqttTestListener listener;
	private static MqttAasRepositoryTopicFactory topicFactory = new MqttAasRepositoryTopicFactory(new Base64URLEncoder());

	private static AasRepository aasRepository;

	@BeforeClass
	public static void setUpClass() throws MqttException, IOException {
		mqttBroker = MqttBrokerTestSupport.start();
		listener = mqttBroker.listener();
		mqttClient = mqttBroker.connectClient();

		aasRepository = createMqttAasRepository(mqttClient);
	}

	@AfterClass
	public static void tearDownClass() throws MqttException {
		mqttBroker.close();
	}

	@Test
	public void createAasEvent() throws DeserializationException {
		AssetAdministrationShell shell = createAasDummy("createAasEventId");
		aasRepository.createAas(shell);
		
		MqttEvent event = listener.awaitEvent(topicFactory.createCreateAASTopic(aasRepository.getName()));
		assertEquals(shell, deserializePayload(event.payload()));
	}

	@Test
	public void updateAasEvent() throws DeserializationException {
		AssetAdministrationShell shell = createAasDummy("updateAasEventId");
		aasRepository.createAas(shell);

		addSubmodelReferenceToAas(shell);

		aasRepository.updateAas(shell.getId(), shell);

		MqttEvent event = listener.awaitEvent(topicFactory.createUpdateAASTopic(aasRepository.getName()));
		assertEquals(shell, deserializePayload(event.payload()));
	}

	@Test
	public void deleteAasEvent() throws DeserializationException {
		AssetAdministrationShell shell = createAasDummy("deleteAasEventId");
		aasRepository.createAas(shell);
		aasRepository.deleteAas(shell.getId());

		MqttEvent event = listener.awaitEvent(topicFactory.createDeleteAASTopic(aasRepository.getName()));
		assertEquals(shell, deserializePayload(event.payload()));
	}
	
	@Test
	public void addSubmodelReferenceEvent() throws DeserializationException {
		AssetAdministrationShell shell = createAasDummy("createAasSubmodelRefEventId");
		aasRepository.createAas(shell);
		
		Reference submodelReference = DummyAasFactory.createDummyReference(DummyAasFactory.DUMMY_SUBMODEL_ID);
		aasRepository.addSubmodelReference(shell.getId(), submodelReference);
		
		MqttEvent event = listener.awaitEvent(topicFactory.createCreateAASSubmodelReferenceTopic(aasRepository.getName(), DummyAasFactory.DUMMY_SUBMODEL_ID));
		assertEquals(shell, deserializePayload(event.payload()));
	}	
	
	@Test
	public void removeSubmodelReferenceEvent() throws DeserializationException {
		AssetAdministrationShell shell = createAasWithSubmodelReference("removeAasSubmodelRefEventId");
		aasRepository.createAas(shell);
		
		aasRepository.removeSubmodelReference(shell.getId(), DummyAasFactory.DUMMY_SUBMODEL_ID);
		
		MqttEvent event = listener.awaitEvent(topicFactory.createDeleteAASSubmodelReferenceTopic(aasRepository.getName(), DummyAasFactory.DUMMY_SUBMODEL_ID));
		assertEquals(shell, deserializePayload(event.payload()));
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

		return new MqttAasRepositoryFactory(repoFactory, client, new MqttAasRepositoryTopicFactory(new Base64URLEncoder())).create();
	}
}

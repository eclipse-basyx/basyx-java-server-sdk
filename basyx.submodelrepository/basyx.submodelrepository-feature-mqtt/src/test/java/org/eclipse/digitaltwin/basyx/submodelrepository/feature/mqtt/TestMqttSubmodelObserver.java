package org.eclipse.digitaltwin.basyx.submodelrepository.feature.mqtt;

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
import static org.junit.Assert.assertNotEquals;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.dataformat.core.DeserializationException;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.json.JsonDeserializer;
import org.eclipse.digitaltwin.aas4j.v3.model.Property;
import org.eclipse.digitaltwin.aas4j.v3.model.Qualifier;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultProperty;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultQualifier;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSubmodel;
import org.eclipse.digitaltwin.basyx.common.mqttcore.encoding.Base64URLEncoder;
import org.eclipse.digitaltwin.basyx.common.mqttcore.serializer.SubmodelElementSerializer;
import org.eclipse.digitaltwin.basyx.submodelrepository.SubmodelInMemoryBackendProvider;
import org.eclipse.digitaltwin.basyx.submodelrepository.SubmodelRepository;
import org.eclipse.digitaltwin.basyx.submodelrepository.SubmodelRepositoryFactory;
import org.eclipse.digitaltwin.basyx.submodelrepository.backend.SimpleSubmodelRepositoryFactory;
import org.eclipse.digitaltwin.basyx.submodelservice.InMemorySubmodelServiceFactory;
import org.eclipse.digitaltwin.basyx.submodelservice.value.PropertyValue;
import org.eclipse.digitaltwin.basyx.submodelservice.value.SubmodelElementValue;
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
 * Tests events for submodels and submodelElements
 */
public class TestMqttSubmodelObserver {
	private static Server mqttBroker;
	private static MqttClient mqttClient;
	private static MqttTestListener listener;
	private static MqttSubmodelRepositoryTopicFactory topicFactory = new MqttSubmodelRepositoryTopicFactory(new Base64URLEncoder());

	private static SubmodelRepository submodelRepository;

	@BeforeClass
	public static void setUpClass() throws MqttException, IOException {
		mqttBroker = startBroker();

		listener = configureInterceptListener(mqttBroker);

		mqttClient = createAndConnectClient();

		submodelRepository = createMqttSubmodelRepository(mqttClient);
	}

	@AfterClass
	public static void tearDownClass() {
		mqttBroker.removeInterceptHandler(listener);
		mqttBroker.stopServer();
	}

	@Test
	public void createSubmodelEvent() throws DeserializationException {
		Submodel submodel = createSubmodelDummy("createSubmodelEventId");
		submodelRepository.createSubmodel(submodel);

		assertEquals(topicFactory.createCreateSubmodelTopic(submodelRepository.getName()), listener.lastTopic);
		assertEquals(submodel, deserializeSubmodelPayload(listener.lastPayload));
	}

	@Test
	public void updateSubmodelEvent() throws DeserializationException {
		Submodel submodel = createSubmodelDummy("updateSubmodelEventId");
		submodelRepository.createSubmodel(submodel);
		submodel.setSubmodelElements(Arrays.asList(createSubmodelElementDummy("submodelElementForUpdateSubmodelEventId")));
		submodelRepository.updateSubmodel(submodel.getId(), submodel);

		assertEquals(topicFactory.createUpdateSubmodelTopic(submodelRepository.getName()), listener.lastTopic);
		assertEquals(submodel, deserializeSubmodelPayload(listener.lastPayload));
	}

	@Test
	public void deleteSubmodelEvent() throws DeserializationException {
		Submodel submodel = createSubmodelDummy("deleteSubmodelEventId");
		submodelRepository.createSubmodel(submodel);
		submodelRepository.deleteSubmodel(submodel.getId());

		assertEquals(topicFactory.createDeleteSubmodelTopic(submodelRepository.getName()), listener.lastTopic);
		assertEquals(submodel, deserializeSubmodelPayload(listener.lastPayload));
	}

	@Test
	public void createSubmodelElementEvent() throws DeserializationException {
		Submodel submodel = createSubmodelDummy("createSubmodelForElementEventId");
		submodelRepository.createSubmodel(submodel);
		SubmodelElement submodelElement = createSubmodelElementDummy("createSubmodelElementEventId");
		submodelRepository.createSubmodelElement(submodel.getId(), submodelElement);

		assertEquals(topicFactory.createCreateSubmodelElementTopic(submodelRepository.getName(), submodel.getId(), submodelElement.getIdShort()), listener.lastTopic);
		assertEquals(submodelElement, deserializeSubmodelElementPayload(listener.lastPayload));
	}

	@Test
	public void updateSubmodelElementEvent() throws DeserializationException {
		Submodel submodel = createSubmodelDummy("updateSubmodelForElementEventId");
		submodelRepository.createSubmodel(submodel);
		SubmodelElement submodelElement = createSubmodelElementDummy("updateSubmodelElementEventId");
		submodelRepository.createSubmodelElement(submodel.getId(), submodelElement);
		SubmodelElementValue value = new PropertyValue("updatedValue");
		submodelRepository.setSubmodelElementValue(submodel.getId(), submodelElement.getIdShort(), value);

		assertEquals(topicFactory.createUpdateSubmodelElementTopic(submodelRepository.getName(), submodel.getId(), submodelElement.getIdShort()), listener.lastTopic);
		assertEquals(submodelElement, deserializeSubmodelElementPayload(listener.lastPayload));
	}

	@Test
	public void deleteSubmodelElementEvent() throws DeserializationException {
		Submodel submodel = createSubmodelDummy("deleteSubmodelForElementEventId");
		submodelRepository.createSubmodel(submodel);
		SubmodelElement submodelElement = createSubmodelElementDummy("deleteSubmodelElementEventId");
		submodelRepository.createSubmodelElement(submodel.getId(), submodelElement);
		submodelRepository.deleteSubmodelElement(submodel.getId(), submodelElement.getIdShort());

		assertEquals(topicFactory.createDeleteSubmodelElementTopic(submodelRepository.getName(), submodel.getId(), submodelElement.getIdShort()), listener.lastTopic);
		assertEquals(submodelElement, deserializeSubmodelElementPayload(listener.lastPayload));
	}

	@Test
	public void createSubmodelElementWithoutValueEvent() throws DeserializationException {
		Submodel submodel = createSubmodelDummy("withoutValueEventId");
		submodelRepository.createSubmodel(submodel);
		SubmodelElement submodelElement = createSubmodelElementDummy("noValueSubmodelElementEventId");
		List<Qualifier> qualifierList = createNoValueQualifierList();
		submodelElement.setQualifiers(qualifierList);
		submodelRepository.createSubmodelElement(submodel.getId(), submodelElement);

		assertEquals(topicFactory.createCreateSubmodelElementTopic(submodelRepository.getName(), submodel.getId(), submodelElement.getIdShort()), listener.lastTopic);
		assertNotEquals(submodelElement, deserializeSubmodelElementPayload(listener.lastPayload));

		// remove value for equality check
		((Property) submodelElement).setValue(null);
		assertEquals(submodelElement, deserializeSubmodelElementPayload(listener.lastPayload));
	}

	private List<Qualifier> createNoValueQualifierList() {
		Qualifier emptyValueQualifier = new DefaultQualifier.Builder().type(SubmodelElementSerializer.EMPTYVALUEUPDATE_TYPE).value("true").build();
		return Arrays.asList(emptyValueQualifier);
	}

	private Submodel deserializeSubmodelPayload(String payload) throws DeserializationException {
		return new JsonDeserializer().read(payload, Submodel.class);
	}

	private SubmodelElement deserializeSubmodelElementPayload(String payload) throws DeserializationException {
		return new JsonDeserializer().read(payload, SubmodelElement.class);
	}

	private Submodel createSubmodelDummy(String submodelId) {
		return new DefaultSubmodel.Builder().id(submodelId).build();
	}

	private SubmodelElement createSubmodelElementDummy(String submodelElementId) {
		return new DefaultProperty.Builder().idShort(submodelElementId).value("defaultValue").build();
	}

	private static SubmodelRepository createMqttSubmodelRepository(MqttClient client) {
		SubmodelRepositoryFactory repoFactory = new SimpleSubmodelRepositoryFactory(new SubmodelInMemoryBackendProvider(), new InMemorySubmodelServiceFactory());

		return new MqttSubmodelRepositoryFactory(repoFactory, client, new MqttSubmodelRepositoryTopicFactory(new Base64URLEncoder())).create();
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

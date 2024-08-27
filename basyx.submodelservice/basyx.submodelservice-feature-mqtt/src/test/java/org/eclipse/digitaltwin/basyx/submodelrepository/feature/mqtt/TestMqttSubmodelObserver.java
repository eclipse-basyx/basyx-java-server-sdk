/*******************************************************************************
 * Copyright (C) 2024 the Eclipse BaSyx Authors
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

package org.eclipse.digitaltwin.basyx.submodelrepository.feature.mqtt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.dataformat.core.DeserializationException;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.json.JsonDeserializer;
import org.eclipse.digitaltwin.aas4j.v3.model.Property;
import org.eclipse.digitaltwin.aas4j.v3.model.Qualifier;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultProperty;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultQualifier;
import org.eclipse.digitaltwin.basyx.common.mqttcore.encoding.Base64URLEncoder;
import org.eclipse.digitaltwin.basyx.common.mqttcore.serializer.SubmodelElementSerializer;
import org.eclipse.digitaltwin.basyx.core.filerepository.InMemoryFileRepository;
import org.eclipse.digitaltwin.basyx.submodelservice.DummySubmodelFactory;
import org.eclipse.digitaltwin.basyx.submodelservice.InMemorySubmodelServiceFactory;
import org.eclipse.digitaltwin.basyx.submodelservice.SubmodelService;
import org.eclipse.digitaltwin.basyx.submodelservice.SubmodelServiceFactory;
import org.eclipse.digitaltwin.basyx.submodelservice.feature.mqtt.MqttSubmodelServiceFactory;
import org.eclipse.digitaltwin.basyx.submodelservice.feature.mqtt.MqttSubmodelServiceTopicFactory;
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
 * Tests events for submodelElements in SM Service
 * 
 * @author rana
 */
public class TestMqttSubmodelObserver {

	private static Server mqttBroker;
	private static MqttClient mqttClient;
	private static MqttTestListener listener;
	private static MqttSubmodelServiceTopicFactory topicFactory = new MqttSubmodelServiceTopicFactory(new Base64URLEncoder());
	private static SubmodelService submodelService;

	@BeforeClass
	public static void setUpClass() throws MqttException, IOException {
		mqttBroker = startBroker();

		listener = configureInterceptListener(mqttBroker);

		mqttClient = createAndConnectClient();

		submodelService = createMqttSubmodelService(mqttClient);
	}

	@AfterClass
	public static void tearDownClass() {
		mqttBroker.removeInterceptHandler(listener);
		mqttBroker.stopServer();
	}

	@Test
	public void createSubmodelElementEvent() throws DeserializationException {

		SubmodelElement submodelElement = createSubmodelElementDummy("createSubmodelElementEventId");
		submodelService.createSubmodelElement(submodelElement);

		assertEquals(topicFactory.createCreateSubmodelElementTopic(submodelElement.getIdShort()), listener.lastTopic);
		assertEquals(submodelElement, deserializeSubmodelElementPayload(listener.lastPayload));
	}

	@Test
	public void updateSubmodelElementEvent() throws DeserializationException {

		SubmodelElement submodelElement = createSubmodelElementDummy("updateSubmodelElementEventId");
		submodelService.createSubmodelElement(submodelElement);

		SubmodelElementValue value = new PropertyValue("updatedValue");
		submodelService.setSubmodelElementValue(submodelElement.getIdShort(), value);

		assertEquals(topicFactory.createUpdateSubmodelElementTopic(submodelElement.getIdShort()), listener.lastTopic);
		assertEquals(submodelElement, deserializeSubmodelElementPayload(listener.lastPayload));
	}

	@Test
	public void deleteSubmodelElementEvent() throws DeserializationException {

		SubmodelElement submodelElement = createSubmodelElementDummy("deleteSubmodelElementEventId");
		submodelService.createSubmodelElement(submodelElement);
		submodelService.deleteSubmodelElement(submodelElement.getIdShort());

		assertEquals(topicFactory.createDeleteSubmodelElementTopic(submodelElement.getIdShort()), listener.lastTopic);
		assertEquals(submodelElement, deserializeSubmodelElementPayload(listener.lastPayload));
	}

	@Test
	public void createSubmodelElementWithoutValueEvent() throws DeserializationException {

		SubmodelElement submodelElement = createSubmodelElementDummy("noValueSubmodelElementEventId");
		List<Qualifier> qualifierList = createNoValueQualifierList();
		submodelElement.setQualifiers(qualifierList);
		submodelService.createSubmodelElement(submodelElement);

		assertEquals(topicFactory.createCreateSubmodelElementTopic(submodelElement.getIdShort()), listener.lastTopic);
		assertNotEquals(submodelElement, deserializeSubmodelElementPayload(listener.lastPayload));

		((Property) submodelElement).setValue(null);
		assertEquals(submodelElement, deserializeSubmodelElementPayload(listener.lastPayload));
	}

	private List<Qualifier> createNoValueQualifierList() {

		Qualifier emptyValueQualifier = new DefaultQualifier.Builder().type(SubmodelElementSerializer.EMPTYVALUEUPDATE_TYPE).value("true").build();
		return Arrays.asList(emptyValueQualifier);
	}

	private static SubmodelElement deserializeSubmodelElementPayload(String payload) throws DeserializationException {

		return new JsonDeserializer().read(payload, SubmodelElement.class);
	}

	private SubmodelElement createSubmodelElementDummy(String submodelElementId) {
		return new DefaultProperty.Builder().idShort(submodelElementId).value("defaultValue").build();
	}

	private static SubmodelService createMqttSubmodelService(MqttClient client) {

		SubmodelServiceFactory repoFactory = new InMemorySubmodelServiceFactory(new InMemoryFileRepository());
		return new MqttSubmodelServiceFactory(repoFactory, client, new MqttSubmodelServiceTopicFactory(new Base64URLEncoder())).create(DummySubmodelFactory.createSubmodelWithAllSubmodelElements());
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

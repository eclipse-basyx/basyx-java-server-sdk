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

package org.eclipse.digitaltwin.basyx.submodelrepository.feature.mqtt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.dataformat.core.DeserializationException;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.json.JsonDeserializer;
import org.eclipse.digitaltwin.aas4j.v3.model.Property;
import org.eclipse.digitaltwin.aas4j.v3.model.Qualifier;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultProperty;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultFile;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultQualifier;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSubmodelElementCollection;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSubmodelElementList;
import org.eclipse.digitaltwin.basyx.common.mqttcore.MqttBrokerTestSupport;
import org.eclipse.digitaltwin.basyx.common.mqttcore.encoding.Base64URLEncoder;
import org.eclipse.digitaltwin.basyx.common.mqttcore.listener.MqttTestListener;
import org.eclipse.digitaltwin.basyx.common.mqttcore.listener.MqttTestListener.MqttEvent;
import org.eclipse.digitaltwin.basyx.common.mqttcore.serializer.SubmodelElementSerializer;
import org.eclipse.digitaltwin.basyx.core.filerepository.InMemoryFileRepository;
import org.eclipse.digitaltwin.basyx.submodelservice.DummySubmodelFactory;
import org.eclipse.digitaltwin.basyx.submodelservice.InMemorySubmodelBackend;
import org.eclipse.digitaltwin.basyx.submodelservice.SubmodelService;
import org.eclipse.digitaltwin.basyx.submodelservice.SubmodelServiceFactory;
import org.eclipse.digitaltwin.basyx.submodelservice.feature.mqtt.MqttSubmodelServiceConfiguration;
import org.eclipse.digitaltwin.basyx.submodelservice.backend.CrudSubmodelServiceFactory;
import org.eclipse.digitaltwin.basyx.submodelservice.feature.mqtt.MqttSubmodelServiceFactory;
import org.eclipse.digitaltwin.basyx.submodelservice.feature.mqtt.MqttSubmodelServiceTopicFactory;
import org.eclipse.digitaltwin.basyx.submodelservice.value.PropertyValue;
import org.eclipse.digitaltwin.basyx.submodelservice.value.SubmodelElementValue;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


/**
 * Tests events for submodelElements in SM Service
 * 
 * @author rana
 */
public class TestMqttSubmodelObserver {

	private static MqttBrokerTestSupport mqttBroker;
	private static MqttClient mqttClient;
	private static MqttTestListener listener;
	private static MqttSubmodelServiceTopicFactory topicFactory = new MqttSubmodelServiceTopicFactory(new Base64URLEncoder());
	private static SubmodelService submodelService;

	@BeforeClass
	public static void setUpClass() throws MqttException, IOException {
		mqttBroker = MqttBrokerTestSupport.start();
		listener = mqttBroker.listener();
		mqttClient = mqttBroker.connectClient();

		submodelService = createMqttSubmodelService(mqttClient);
	}

	@AfterClass
	public static void tearDownClass() throws MqttException {
		mqttBroker.close();
	}

	@Before
	public void resetSubmodelService() {
		submodelService = createMqttSubmodelService(mqttClient);
	}

	@Test
	public void createSubmodelElementEvent() throws DeserializationException {

		SubmodelElement submodelElement = createSubmodelElementDummy("createSubmodelElementEventId");
		submodelService.createSubmodelElement(submodelElement);

		MqttEvent event = listener.awaitEvent(topicFactory.createCreateSubmodelElementTopic(submodelService.getSubmodel().getId(), submodelElement.getIdShort()));
		assertEquals(submodelElement, deserializeSubmodelElementPayload(event.payload()));
	}

	@Test
	public void createNestedSubmodelElementInCollectionEvent() throws DeserializationException {
		String collectionIdShort = "mqttNestedCollection";
		submodelService.createSubmodelElement(new DefaultSubmodelElementCollection.Builder().idShort(collectionIdShort).build());
		SubmodelElement child = createSubmodelElementDummy("nestedCollectionChild");
		String childPath = collectionIdShort + "." + child.getIdShort();

		submodelService.createSubmodelElement(collectionIdShort, child);

		MqttEvent event = listener.awaitEvent(topicFactory.createCreateSubmodelElementTopic(submodelService.getSubmodel().getId(), childPath));
		assertEquals(child, deserializeSubmodelElementPayload(event.payload()));
	}

	@Test
	public void createNestedSubmodelElementInListEvent() throws DeserializationException {
		String listIdShort = "mqttNestedList";
		SubmodelElement existingElement = createSubmodelElementDummy("existingListElement");
		submodelService.createSubmodelElement(new DefaultSubmodelElementList.Builder().idShort(listIdShort).value(existingElement).build());
		SubmodelElement child = createSubmodelElementDummy("nestedListChild");
		String childPath = listIdShort + "[1]";

		submodelService.createSubmodelElement(listIdShort, child);

		MqttEvent event = listener.awaitEvent(topicFactory.createCreateSubmodelElementTopic(submodelService.getSubmodel().getId(), childPath));
		assertEquals(child, deserializeSubmodelElementPayload(event.payload()));
	}

	@Test
	public void updateSubmodelElementEvent() throws DeserializationException {

		SubmodelElement submodelElement = createSubmodelElementDummy("updateSubmodelElementEventId");
		submodelService.createSubmodelElement(submodelElement);

		SubmodelElementValue value = new PropertyValue("updatedValue");
		submodelService.setSubmodelElementValue(submodelElement.getIdShort(), value);

		MqttEvent event = listener.awaitEvent(topicFactory.createUpdateSubmodelElementValueTopic(submodelService.getSubmodel().getId(), submodelElement.getIdShort()));
		assertEquals(submodelElement, deserializeSubmodelElementPayload(event.payload()));
	}

	@Test
	public void replaceSubmodelElementEvent() throws DeserializationException {
		SubmodelElement submodelElement = createSubmodelElementDummy("replaceSubmodelElementEventId");
		submodelService.createSubmodelElement(submodelElement);
		SubmodelElement replacement = new DefaultProperty.Builder().idShort(submodelElement.getIdShort()).value("replacementValue").build();

		submodelService.updateSubmodelElement(submodelElement.getIdShort(), replacement);

		MqttEvent event = listener.awaitEvent(topicFactory.createUpdateSubmodelElementTopic(submodelService.getSubmodel().getId(), submodelElement.getIdShort()));
		assertEquals(replacement, deserializeSubmodelElementPayload(event.payload()));
	}

	@Test
	public void patchSubmodelElementsEvent() throws DeserializationException {
		SubmodelElement first = createSubmodelElementDummy("patchSubmodelElementOne");
		SubmodelElement second = createSubmodelElementDummy("patchSubmodelElementTwo");
		submodelService.createSubmodelElement(first);
		submodelService.createSubmodelElement(second);

		List<SubmodelElement> patchedElements = Arrays.asList(first, second);
		submodelService.patchSubmodelElements(patchedElements);

		MqttEvent event = listener.awaitEvent(topicFactory.createPatchSubmodelElementsTopic(submodelService.getSubmodel().getId()));
		assertEquals(patchedElements, new JsonDeserializer().readList(event.payload(), SubmodelElement.class));
	}

	@Test
	public void attachmentUpdateAndDeleteEvents() throws Exception {
		org.eclipse.digitaltwin.aas4j.v3.model.File file = new DefaultFile.Builder().idShort("mqttAttachmentFile").contentType("text/plain").build();
		submodelService.createSubmodelElement(file);

		submodelService.setFileValue(file.getIdShort(), "test.txt", "text/plain", new ByteArrayInputStream("file-content".getBytes(StandardCharsets.UTF_8)));
		MqttEvent updateEvent = listener.awaitEvent(topicFactory.createUpdateFileValueTopic(submodelService.getSubmodel().getId(), file.getIdShort()));
		assertEquals(file.getIdShort(), deserializeSubmodelElementPayload(updateEvent.payload()).getIdShort());

		submodelService.deleteFileValue(file.getIdShort());
		MqttEvent deleteEvent = listener.awaitEvent(topicFactory.createDeleteFileValueTopic(submodelService.getSubmodel().getId(), file.getIdShort()));
		assertEquals(file.getIdShort(), deserializeSubmodelElementPayload(deleteEvent.payload()).getIdShort());
	}

	@Test
	public void deleteSubmodelElementEvent() throws DeserializationException {

		SubmodelElement submodelElement = createSubmodelElementDummy("deleteSubmodelElementEventId");
		submodelService.createSubmodelElement(submodelElement);
		submodelService.deleteSubmodelElement(submodelElement.getIdShort());

		MqttEvent event = listener.awaitEvent(topicFactory.createDeleteSubmodelElementTopic(submodelService.getSubmodel().getId(), submodelElement.getIdShort()));
		assertEquals(submodelElement, deserializeSubmodelElementPayload(event.payload()));
	}

	@Test
	public void createSubmodelElementWithoutValueEvent() throws DeserializationException {

		SubmodelElement submodelElement = createSubmodelElementDummy("noValueSubmodelElementEventId");
		List<Qualifier> qualifierList = createNoValueQualifierList();
		submodelElement.setQualifiers(qualifierList);
		submodelService.createSubmodelElement(submodelElement);

		MqttEvent event = listener.awaitEvent(topicFactory.createCreateSubmodelElementTopic(submodelService.getSubmodel().getId(), submodelElement.getIdShort()));
		assertNotEquals(submodelElement, deserializeSubmodelElementPayload(event.payload()));

		((Property) submodelElement).setValue(null);
		assertEquals(submodelElement, deserializeSubmodelElementPayload(event.payload()));
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

		SubmodelServiceFactory repoFactory = new CrudSubmodelServiceFactory(new InMemorySubmodelBackend(), new InMemoryFileRepository());
		return new MqttSubmodelServiceFactory(repoFactory, client, new MqttSubmodelServiceTopicFactory(new Base64URLEncoder())).create(DummySubmodelFactory.createSubmodelWithAllSubmodelElements());
	}

}

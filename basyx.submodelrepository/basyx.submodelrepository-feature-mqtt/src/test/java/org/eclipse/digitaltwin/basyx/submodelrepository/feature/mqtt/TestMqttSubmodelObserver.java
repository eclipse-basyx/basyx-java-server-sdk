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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.dataformat.core.DeserializationException;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.json.JsonDeserializer;
import org.eclipse.digitaltwin.aas4j.v3.model.File;
import org.eclipse.digitaltwin.aas4j.v3.model.Property;
import org.eclipse.digitaltwin.aas4j.v3.model.Qualifier;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultFile;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultProperty;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultQualifier;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSubmodel;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSubmodelElementCollection;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSubmodelElementList;
import org.eclipse.digitaltwin.basyx.common.mqttcore.MqttBrokerTestSupport;
import org.eclipse.digitaltwin.basyx.common.mqttcore.encoding.Base64URLEncoder;
import org.eclipse.digitaltwin.basyx.common.mqttcore.listener.MqttTestListener;
import org.eclipse.digitaltwin.basyx.common.mqttcore.listener.MqttTestListener.MqttEvent;
import org.eclipse.digitaltwin.basyx.common.mqttcore.serializer.SubmodelElementSerializer;
import org.eclipse.digitaltwin.basyx.core.exceptions.FileHandlingException;
import org.eclipse.digitaltwin.basyx.core.filerepository.FileMetadata;
import org.eclipse.digitaltwin.basyx.core.filerepository.FileRepository;
import org.eclipse.digitaltwin.basyx.core.filerepository.InMemoryFileRepository;
import org.eclipse.digitaltwin.basyx.submodelrepository.SubmodelRepository;
import org.eclipse.digitaltwin.basyx.submodelrepository.SubmodelRepositoryFactory;
import org.eclipse.digitaltwin.basyx.submodelrepository.backend.CrudSubmodelRepositoryFactory;
import org.eclipse.digitaltwin.basyx.submodelservice.InMemorySubmodelBackend;
import org.eclipse.digitaltwin.basyx.submodelservice.value.PropertyValue;
import org.eclipse.digitaltwin.basyx.submodelservice.value.SubmodelElementValue;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;


/**
 * Tests events for submodels and submodelElements
 */
public class TestMqttSubmodelObserver {
	private static MqttBrokerTestSupport mqttBroker;
	private static MqttClient mqttClient;
	private static MqttTestListener listener;
	private static MqttSubmodelRepositoryTopicFactory topicFactory = new MqttSubmodelRepositoryTopicFactory(new Base64URLEncoder());

	private static SubmodelRepository submodelRepository;
	
	private static JsonDeserializer deserializer = new JsonDeserializer();

	private static final String FILE_SUBMODEL_ELEMENT_NAME = "testFile.txt";
	private static final String FILE_SUBMODEL_ELEMENT_CONTENT = "This is a text file.";
	private static String SAVED_FILE_PATH = "";
	
	@BeforeClass
	public static void setUpClass() throws MqttException, IOException {
		mqttBroker = MqttBrokerTestSupport.start();
		listener = mqttBroker.listener();
		mqttClient = mqttBroker.connectClient();

		submodelRepository = createMqttSubmodelRepository(mqttClient);
	}

	@AfterClass
	public static void tearDownClass() throws MqttException {
		mqttBroker.close();
	}

	@Test
	public void createSubmodelEvent() throws DeserializationException {
		Submodel submodel = createSubmodelDummy("createSubmodelEventId");
		submodelRepository.createSubmodel(submodel);

		MqttEvent event = listener.awaitEvent(topicFactory.createCreateSubmodelTopic(submodelRepository.getName()));
		assertEquals(submodel, deserializeSubmodelPayload(event.payload()));
	}

	@Test
	public void updateSubmodelEvent() throws DeserializationException {
		Submodel submodel = createSubmodelDummy("updateSubmodelEventId");
		submodelRepository.createSubmodel(submodel);
		submodel.setSubmodelElements(Arrays.asList(createSubmodelElementDummy("submodelElementForUpdateSubmodelEventId")));
		submodelRepository.updateSubmodel(submodel.getId(), submodel);

		MqttEvent event = listener.awaitEvent(topicFactory.createUpdateSubmodelTopic(submodelRepository.getName()));
		assertEquals(submodel, deserializeSubmodelPayload(event.payload()));
	}

	@Test
	public void deleteSubmodelEvent() throws DeserializationException {
		Submodel submodel = createSubmodelDummy("deleteSubmodelEventId");
		submodelRepository.createSubmodel(submodel);
		submodelRepository.deleteSubmodel(submodel.getId());

		MqttEvent event = listener.awaitEvent(topicFactory.createDeleteSubmodelTopic(submodelRepository.getName()));
		assertEquals(submodel, deserializeSubmodelPayload(event.payload()));
	}

	@Test
	public void createSubmodelElementEvent() throws DeserializationException {
		Submodel submodel = createSubmodelDummy("createSubmodelForElementEventId");
		submodelRepository.createSubmodel(submodel);
		SubmodelElement submodelElement = createSubmodelElementDummy("createSubmodelElementEventId");
		submodelRepository.createSubmodelElement(submodel.getId(), submodelElement);

		MqttEvent event = listener.awaitEvent(topicFactory.createCreateSubmodelElementTopic(submodelRepository.getName(), submodel.getId(), submodelElement.getIdShort()));
		assertEquals(submodelElement, deserializeSubmodelElementPayload(event.payload()));
	}

	@Test
	public void createNestedSubmodelElementInCollectionEvent() throws DeserializationException {
		String collectionIdShort = "nestedCollection";
		Submodel submodel = new DefaultSubmodel.Builder().id("nestedCollectionSubmodel").submodelElements(new DefaultSubmodelElementCollection.Builder().idShort(collectionIdShort).build()).build();
		submodelRepository.createSubmodel(submodel);
		SubmodelElement child = createSubmodelElementDummy("nestedCollectionChild");
		String childPath = collectionIdShort + "." + child.getIdShort();

		submodelRepository.createSubmodelElement(submodel.getId(), collectionIdShort, child);

		MqttEvent event = listener.awaitEvent(topicFactory.createCreateSubmodelElementTopic(submodelRepository.getName(), submodel.getId(), childPath));
		assertEquals(child, deserializeSubmodelElementPayload(event.payload()));
	}

	@Test
	public void createNestedSubmodelElementInListEvent() throws DeserializationException {
		String listIdShort = "nestedList";
		SubmodelElement existingElement = createSubmodelElementDummy("existingListElement");
		Submodel submodel = new DefaultSubmodel.Builder().id("nestedListSubmodel").submodelElements(new DefaultSubmodelElementList.Builder().idShort(listIdShort).value(existingElement).build()).build();
		submodelRepository.createSubmodel(submodel);
		SubmodelElement child = createSubmodelElementDummy("nestedListChild");
		String childPath = listIdShort + "[1]";

		submodelRepository.createSubmodelElement(submodel.getId(), listIdShort, child);

		MqttEvent event = listener.awaitEvent(topicFactory.createCreateSubmodelElementTopic(submodelRepository.getName(), submodel.getId(), childPath));
		assertEquals(child, deserializeSubmodelElementPayload(event.payload()));
	}

	@Test
	public void updateSubmodelElementEvent() throws DeserializationException {
		Submodel submodel = createSubmodelDummyWithSubmodelElement("updateSubmodelForElementEventId", "updateSubmodelElementEventId");
		submodelRepository.createSubmodel(submodel);
		SubmodelElement submodelElement = submodel.getSubmodelElements().get(0);
		
		SubmodelElementValue value = new PropertyValue("updatedValue");
		submodelRepository.setSubmodelElementValue(submodel.getId(), submodelElement.getIdShort(), value);

		MqttEvent event = listener.awaitEvent(topicFactory.createUpdateSubmodelElementValueTopic(submodelRepository.getName(), submodel.getId(), submodelElement.getIdShort()));
		assertEquals(submodelElement, deserializeSubmodelElementPayload(event.payload()));
	}

	@Test
	public void replaceSubmodelElementEvent() throws DeserializationException {
		Submodel submodel = createSubmodelDummyWithSubmodelElement("replaceSubmodelForElementEventId", "replaceSubmodelElementEventId");
		submodelRepository.createSubmodel(submodel);
		SubmodelElement replacement = createSubmodelElementDummy("replaceSubmodelElementEventId");

		submodelRepository.updateSubmodelElement(submodel.getId(), replacement.getIdShort(), replacement);

		MqttEvent event = listener.awaitEvent(topicFactory.createUpdateSubmodelElementTopic(submodelRepository.getName(), submodel.getId(), replacement.getIdShort()));
		assertEquals(replacement, deserializeSubmodelElementPayload(event.payload()));
	}

	@Test
	public void deleteSubmodelElementEvent() throws DeserializationException {
		Submodel submodel = createSubmodelDummyWithSubmodelElement("deleteSubmodelForElementEventId", "deleteSubmodelElementEventId");
		submodelRepository.createSubmodel(submodel);
		
		SubmodelElement submodelElement = submodel.getSubmodelElements().get(0);
		
		submodelRepository.deleteSubmodelElement(submodel.getId(), submodelElement.getIdShort());

		MqttEvent event = listener.awaitEvent(topicFactory.createDeleteSubmodelElementTopic(submodelRepository.getName(), submodel.getId(), submodelElement.getIdShort()));
		assertEquals(submodelElement, deserializeSubmodelElementPayload(event.payload()));
	}

	@Test
	public void createSubmodelElementWithoutValueEvent() throws DeserializationException {
		Submodel submodel = createSubmodelDummy("withoutValueEventId");
		submodelRepository.createSubmodel(submodel);
		SubmodelElement submodelElement = createSubmodelElementDummy("noValueSubmodelElementEventId");
		List<Qualifier> qualifierList = createNoValueQualifierList();
		submodelElement.setQualifiers(qualifierList);
		submodelRepository.createSubmodelElement(submodel.getId(), submodelElement);

		MqttEvent event = listener.awaitEvent(topicFactory.createCreateSubmodelElementTopic(submodelRepository.getName(), submodel.getId(), submodelElement.getIdShort()));
		assertNotEquals(submodelElement, deserializeSubmodelElementPayload(event.payload()));

		// remove value for equality check
		((Property) submodelElement).setValue(null);
		assertEquals(submodelElement, deserializeSubmodelElementPayload(event.payload()));
	}

	@Test
	public void patchSubmodelElementsEvent() throws DeserializationException, JsonMappingException, JsonProcessingException {
		Submodel submodel = createSubmodelDummyWithSubmodelElements("patchSubmodelForElementEventId");
		submodelRepository.createSubmodel(submodel);
		
		List<SubmodelElement> submodelElements = submodel.getSubmodelElements();
		
		for (int i = 0; i < submodelElements.size(); i++) {
			SubmodelElement submodelElement = submodelElements.get(i);
			submodelElement.setIdShort("patchedSubmodelElementId_" + i);
		}

		submodelRepository.patchSubmodelElements(submodel.getId(), submodelElements);
		
		MqttEvent event = listener.awaitEvent(topicFactory.createPatchSubmodelElementsTopic(submodelRepository.getName(), submodel.getId()));
		assertEquals(submodelElements, deserializeSubmodelElementsListPayload(event.payload()));
	}
	
	@Test
	public void setFileValueEvent() throws DeserializationException, IOException {
		Submodel submodel = createSubmodelDummyWithFileSubmodelElement("setSubmodelFileValueEventId", "setFileValueSubmodelElementEventId");
		submodelRepository.createSubmodel(submodel);
	
		File submodelElement = (File) submodel.getSubmodelElements().get(0); 
		
		submodelRepository.setFileValue(submodel.getId(), submodelElement.getIdShort(), FILE_SUBMODEL_ELEMENT_NAME, "application/octet-stream", getInputStreamOfDummyFile(FILE_SUBMODEL_ELEMENT_CONTENT));
		
		MqttEvent event = listener.awaitEvent(topicFactory.createUpdateFileValueTopic(submodelRepository.getName(), submodel.getId(), submodelElement.getIdShort()));
		assertEquals(submodelElement, deserializeSubmodelElementPayload(event.payload()));
	}
	
	@Test
	public void deleteFileValueEvent() throws DeserializationException, IOException {
		Submodel submodel = createSubmodelDummyWithFileSubmodelElement("deleteSubmodelFileValueEventId", "deleteFileValueSubmodelElementEventId");
		submodelRepository.createSubmodel(submodel);
	
		File submodelElement = (File) submodel.getSubmodelElements().get(0);
		
		submodelRepository.deleteFileValue(submodel.getId(), submodelElement.getIdShort());

		MqttEvent event = listener.awaitEvent(topicFactory.createDeleteFileValueTopic(submodelRepository.getName(), submodel.getId(), submodelElement.getIdShort()));
		assertEquals(submodelElement, deserializeSubmodelElementPayload(event.payload()));
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
	 
	private List<SubmodelElement> deserializeSubmodelElementsListPayload(String payload) throws DeserializationException, JsonMappingException, JsonProcessingException {			
		return deserializer.readList(payload, SubmodelElement.class);
	}

	private Submodel createSubmodelDummy(String submodelId) {
		return new DefaultSubmodel.Builder().id(submodelId).build();
	}
	
	private Submodel createSubmodelDummyWithSubmodelElement(String submodelId, String submodelElementId) {
		List<SubmodelElement> submodelElements = new ArrayList<>();
		
		submodelElements.add(createSubmodelElementDummy(submodelElementId));
		
		return new DefaultSubmodel.Builder().id(submodelId).submodelElements(submodelElements).build();
	}
	
	private Submodel createSubmodelDummyWithFileSubmodelElement(String submodelId, String submodelElementId) {
		List<SubmodelElement> submodelElements = new ArrayList<>();
		
		submodelElements.add(createFileSubmodelElement(submodelElementId));
		
		return new DefaultSubmodel.Builder().id(submodelId).submodelElements(submodelElements).build();
	}
	
	private Submodel createSubmodelDummyWithSubmodelElements(String submodelId) {
		List<SubmodelElement> submodelElements = createSubmodelElementsListDummy(2);
		
		return new DefaultSubmodel.Builder().id(submodelId).submodelElements(submodelElements).build();
	}

	private SubmodelElement createSubmodelElementDummy(String submodelElementId) {
		Property defaultProp = new DefaultProperty.Builder().idShort(submodelElementId).value("defaultValue").build();
		
		return new DefaultProperty.Builder().idShort(submodelElementId).value("defaultValue").build();
	}
	
	public File createFileSubmodelElement(String submodelElementId) {
		return new DefaultFile.Builder().idShort(submodelElementId).value(SAVED_FILE_PATH).contentType("text/plain").build();
	}
	
	private static InputStream getInputStreamOfDummyFile(String fileContent) throws FileNotFoundException, IOException {
		return new ByteArrayInputStream(fileContent.getBytes());
	}
	
	private List<SubmodelElement> createSubmodelElementsListDummy(int count) {
		List<SubmodelElement> submodelElements = new ArrayList<SubmodelElement>();
		
		for (int i = 0; i < count; i++) {
			submodelElements.add(createSubmodelElementDummy("submodelElementId_" + i));
		}
		
		return submodelElements;
	}

	private static SubmodelRepository createMqttSubmodelRepository(MqttClient client) throws FileHandlingException, FileNotFoundException, IOException {
		FileRepository fileRepository = new InMemoryFileRepository();
		
		SAVED_FILE_PATH = fileRepository.save(new FileMetadata(FILE_SUBMODEL_ELEMENT_NAME, "", getInputStreamOfDummyFile(FILE_SUBMODEL_ELEMENT_CONTENT)));

		SubmodelRepositoryFactory repoFactory = CrudSubmodelRepositoryFactory.builder().backend(new InMemorySubmodelBackend()).fileRepository(fileRepository).buildFactory();

		return new MqttSubmodelRepositoryFactory(repoFactory, client, new MqttSubmodelRepositoryTopicFactory(new Base64URLEncoder())).create();
	}

}

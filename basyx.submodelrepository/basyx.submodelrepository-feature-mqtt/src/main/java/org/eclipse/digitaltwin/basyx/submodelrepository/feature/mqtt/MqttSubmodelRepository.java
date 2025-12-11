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

import java.io.File;
import java.io.InputStream;
import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.model.OperationVariable;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.basyx.common.mqttcore.serializer.SubmodelElementSerializer;
import org.eclipse.digitaltwin.basyx.common.mqttcore.serializer.SubmodelSerializer;
import org.eclipse.digitaltwin.basyx.core.exceptions.CollidingIdentifierException;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.eclipse.digitaltwin.basyx.submodelrepository.SubmodelRepository;
import org.eclipse.digitaltwin.basyx.submodelservice.value.SubmodelElementValue;
import org.eclipse.digitaltwin.basyx.submodelservice.value.SubmodelValueOnly;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Repository decorator for the MQTT eventing on the submodel level.
 * 
 * @author fischer
 */
public class MqttSubmodelRepository implements SubmodelRepository {
	private static Logger logger = LoggerFactory.getLogger(MqttSubmodelRepository.class);
	private MqttSubmodelRepositoryTopicFactory topicFactory;

	private SubmodelRepository decorated;

	private IMqttClient mqttClient;

	public MqttSubmodelRepository(SubmodelRepository decorated, IMqttClient mqttClient, MqttSubmodelRepositoryTopicFactory topicFactory) {
		this.topicFactory = topicFactory;
		this.decorated = decorated;
		this.mqttClient = mqttClient;
	}

	@Override
	public CursorResult<List<Submodel>> getAllSubmodels(PaginationInfo pInfo) {
		return decorated.getAllSubmodels(pInfo);
	}
	
	@Override
	public CursorResult<List<Submodel>> getAllSubmodels(String semanticId, PaginationInfo pInfo) {
		return decorated.getAllSubmodels(semanticId, pInfo);
	}

	@Override
	public Submodel getSubmodel(String submodelId) throws ElementDoesNotExistException {
		return decorated.getSubmodel(submodelId);
	}

	@Override
	public void updateSubmodel(String submodelId, Submodel submodel) throws ElementDoesNotExistException {
		decorated.updateSubmodel(submodelId, submodel);
		submodelUpdated(submodel, getName());
	}

	@Override
	public void createSubmodel(Submodel submodel) throws CollidingIdentifierException {
		decorated.createSubmodel(submodel);
		submodelCreated(submodel, getName());
	}

	@Override
	public void deleteSubmodel(String submodelId) throws ElementDoesNotExistException {
		Submodel submodel = decorated.getSubmodel(submodelId);
		decorated.deleteSubmodel(submodelId);
		submodelDeleted(submodel, getName());
	}

	@Override
	public CursorResult<List<SubmodelElement>> getSubmodelElements(String submodelId, PaginationInfo pInfo)
			throws ElementDoesNotExistException {
		return decorated.getSubmodelElements(submodelId, pInfo);
	}

	@Override
	public SubmodelElement getSubmodelElement(String submodelId, String smeIdShort) throws ElementDoesNotExistException {
		return decorated.getSubmodelElement(submodelId, smeIdShort);
	}

	@Override
	public SubmodelElementValue getSubmodelElementValue(String submodelId, String smeIdShort) throws ElementDoesNotExistException {
		return decorated.getSubmodelElementValue(submodelId, smeIdShort);
	}

	@Override
	public void setSubmodelElementValue(String submodelId, String idShortPath, SubmodelElementValue value) throws ElementDoesNotExistException {
		decorated.setSubmodelElementValue(submodelId, idShortPath, value);
		SubmodelElement submodelElement = decorated.getSubmodelElement(submodelId, idShortPath);
		submodelElementUpdated(submodelElement, getName(), submodelId, idShortPath);
	}

	@Override
	public void createSubmodelElement(String submodelId, SubmodelElement smElement) {
		decorated.createSubmodelElement(submodelId, smElement);
		SubmodelElement submodelElement = decorated.getSubmodelElement(submodelId, smElement.getIdShort());
		submodelElementCreated(submodelElement, getName(), submodelId, smElement.getIdShort());
	}

	@Override
	public void createSubmodelElement(String submodelId, String idShortPath, SubmodelElement smElement) throws ElementDoesNotExistException {
		decorated.createSubmodelElement(submodelId, idShortPath, smElement);
		SubmodelElement submodelElement = decorated.getSubmodelElement(submodelId, idShortPath);
		submodelElementCreated(submodelElement, getName(), submodelId, idShortPath);
	}
	
	@Override
	public void updateSubmodelElement(String submodelIdentifier, String idShortPath, SubmodelElement smElement) throws ElementDoesNotExistException {
		decorated.updateSubmodelElement(submodelIdentifier, idShortPath, smElement);
		SubmodelElement submodelElement = decorated.getSubmodelElement(submodelIdentifier, idShortPath);
		submodelElementUpdated(submodelElement, getName(), submodelIdentifier, idShortPath);
	}

	@Override
	public void deleteSubmodelElement(String submodelId, String idShortPath) throws ElementDoesNotExistException {
		SubmodelElement submodelElement = decorated.getSubmodelElement(submodelId, idShortPath);
		decorated.deleteSubmodelElement(submodelId, idShortPath);
		submodelElementDeleted(submodelElement, getName(), submodelId, idShortPath);
	}

	@Override
	public void patchSubmodelElements(String submodelId, List<SubmodelElement> submodelElementList) {
		decorated.patchSubmodelElements(submodelId, submodelElementList);
		submodelElementsPatched(submodelElementList, getName(), submodelId);
	}

	@Override
	public String getName() {
		return decorated.getName();
	}

	@Override
	public SubmodelValueOnly getSubmodelByIdValueOnly(String submodelId) {
		return decorated.getSubmodelByIdValueOnly(submodelId);
	}

	@Override
	public Submodel getSubmodelByIdMetadata(String submodelId) {
		return decorated.getSubmodelByIdMetadata(submodelId);
	}

	@Override
	public OperationVariable[] invokeOperation(String submodelId, String idShortPath, OperationVariable[] input) throws ElementDoesNotExistException {
		return decorated.invokeOperation(submodelId, idShortPath, input);
	}

	@Override
	public java.io.File getFileByPathSubmodel(String submodelId, String idShortPath) {
		return decorated.getFileByPathSubmodel(submodelId, idShortPath);
	}

	@Override
	public void deleteFileValue(String identifier, String idShortPath) {
		SubmodelElement submodelElement = decorated.getSubmodelElement(identifier, idShortPath);	
		decorated.deleteFileValue(identifier, idShortPath);
		fileValueDeleted(submodelElement, getName(), identifier, idShortPath);
	}

	@Override
	public void setFileValue(String submodelId, String idShortPath, String fileName, String contentType, InputStream inputStream){
		decorated.setFileValue(submodelId, idShortPath, fileName, contentType, inputStream);
		SubmodelElement submodelElement = decorated.getSubmodelElement(submodelId, idShortPath);
		fileValueUpdated(submodelElement, getName(), submodelId, idShortPath);
	}

	@Override
	public InputStream getFileByFilePath(String submodelId, String filePath) {
		return decorated.getFileByFilePath(submodelId, filePath);
	}

	private void submodelCreated(Submodel submodel, String repoId) {
		sendMqttMessage(topicFactory.createCreateSubmodelTopic(repoId), SubmodelSerializer.serializeSubmodel(submodel));
	}

	private void submodelUpdated(Submodel submodel, String repoId) {
		sendMqttMessage(topicFactory.createUpdateSubmodelTopic(repoId), SubmodelSerializer.serializeSubmodel(submodel));
	}

	private void submodelDeleted(Submodel submodel, String repoId) {
		sendMqttMessage(topicFactory.createDeleteSubmodelTopic(repoId), SubmodelSerializer.serializeSubmodel(submodel));
	}

	private void submodelElementCreated(SubmodelElement submodelElement, String repoId, String submodelId, String submodelElementId) {
		sendMqttMessage(topicFactory.createCreateSubmodelElementTopic(repoId, submodelId, submodelElementId), SubmodelElementSerializer.serializeSubmodelElement(submodelElement));
	}

	private void submodelElementUpdated(SubmodelElement submodelElement, String repoId, String submodelId, String submodelElementId) {
		sendMqttMessage(topicFactory.createUpdateSubmodelElementTopic(repoId, submodelId, submodelElementId), SubmodelElementSerializer.serializeSubmodelElement(submodelElement));
	}

	private void submodelElementDeleted(SubmodelElement submodelElement, String repoId, String submodelId, String submodelElementId) {
		sendMqttMessage(topicFactory.createDeleteSubmodelElementTopic(repoId, submodelId, submodelElementId), SubmodelElementSerializer.serializeSubmodelElement(submodelElement));
	}
	
	private void submodelElementsPatched(List<SubmodelElement> submodelElements, String repoId, String submodelId) {
		sendMqttMessage(topicFactory.createPatchSubmodelElementsTopic(repoId, submodelId), SubmodelElementSerializer.serializeSubmodelElements(submodelElements));
	}
	
	private void fileValueDeleted(SubmodelElement submodelElement, String repoId, String submodelId, String submodelElementId) {
		sendMqttMessage(topicFactory.createDeleteFileValueTopic(repoId, submodelId, submodelElementId), SubmodelElementSerializer.serializeSubmodelElement(submodelElement));
	}
	
	private void fileValueUpdated(SubmodelElement submodelElement, String repoId, String submodelId, String submodelElementId) {
		sendMqttMessage(topicFactory.createUpdateFileValueTopic(repoId, submodelId, submodelElementId), SubmodelElementSerializer.serializeSubmodelElement(submodelElement));
	}

	/**
	 * Sends MQTT message to connected broker
	 * 
	 * @param topic
	 *            in which the message will be published
	 * @param payload
	 *            the actual message
	 */
	private void sendMqttMessage(String topic, String payload) {
		MqttMessage msg = createMqttMessage(payload);

		try {
			logger.debug("Send MQTT message to " + topic + ": " + payload);
			mqttClient.publish(topic, msg);
		} catch (MqttPersistenceException e) {
			logger.error("Could not persist mqtt message", e);
		} catch (MqttException e) {
			logger.error("Could not send mqtt message", e);
		}
	}

	private MqttMessage createMqttMessage(String payload) {
		if (payload == null) {
			return new MqttMessage();
		} else {
			return new MqttMessage(payload.getBytes());
		}
	}

}

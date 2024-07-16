package org.eclipse.digitaltwin.basyx.submodelservice.feature.mqtt;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import javax.sound.midi.Soundbank;

import org.eclipse.digitaltwin.aas4j.v3.model.OperationVariable;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.basyx.common.mqttcore.serializer.SubmodelElementSerializer;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementNotAFileException;
import org.eclipse.digitaltwin.basyx.core.exceptions.FileDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.eclipse.digitaltwin.basyx.submodelservice.SubmodelService;
import org.eclipse.digitaltwin.basyx.submodelservice.value.SubmodelElementValue;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service decorator for the MQTT eventing on the submodel level.
 * 
 * @author rana
 */
public class MqttSubmodelService implements SubmodelService{
	
	private static Logger logger = LoggerFactory.getLogger(MqttSubmodelService.class);
	
	private MqttSubmodelServiceTopicFactory topicFactory;

	private SubmodelService decorated;

	private IMqttClient mqttClient;

	public MqttSubmodelService(SubmodelService decorated, IMqttClient mqttClient, MqttSubmodelServiceTopicFactory topicFactory) {
		this.topicFactory = topicFactory;
		this.decorated = decorated;
		this.mqttClient = mqttClient;
	}
	
	@Override
	public Submodel getSubmodel() {
		return decorated.getSubmodel();
	}

	@Override
	public CursorResult<List<SubmodelElement>> getSubmodelElements(PaginationInfo pInfo) {
		return decorated.getSubmodelElements(pInfo);
	}
	
	@Override
	public SubmodelElement getSubmodelElement(String idShortPath) throws ElementDoesNotExistException {
		return decorated.getSubmodelElement(idShortPath);
	}
	
	@Override
	public SubmodelElementValue getSubmodelElementValue(String idShortPath) throws ElementDoesNotExistException {
		return decorated.getSubmodelElementValue(idShortPath);
	}
	
	@Override
	public void setSubmodelElementValue(String idShortPath, SubmodelElementValue value) throws ElementDoesNotExistException {
		decorated.setSubmodelElementValue(idShortPath, value);
		SubmodelElement submodelElement = decorated.getSubmodelElement(idShortPath);
		submodelElementUpdated(submodelElement, idShortPath);
	}
	
	@Override
	public void createSubmodelElement(SubmodelElement submodelElement) {
		decorated.createSubmodelElement(submodelElement);
		SubmodelElement smElement = decorated.getSubmodelElement(submodelElement.getIdShort());
		submodelElementCreated(submodelElement, smElement.getIdShort());
	}

	@Override
	public void createSubmodelElement(String idShortPath, SubmodelElement submodelElement)
			throws ElementDoesNotExistException {
		
		decorated.createSubmodelElement(idShortPath, submodelElement);
		
		SubmodelElement smElement = decorated.getSubmodelElement(submodelElement.getIdShort());
		submodelElementCreated(smElement, idShortPath);
	}

	@Override
	public void updateSubmodelElement(String idShortPath, SubmodelElement submodelElement)
			throws ElementDoesNotExistException {
		
		decorated.updateSubmodelElement(idShortPath, submodelElement);
		SubmodelElement smElement = decorated.getSubmodelElement(submodelElement.getIdShort());
		submodelElementUpdated(smElement, submodelElement.getIdShort());
	}

	@Override
	public void deleteSubmodelElement(String idShortPath) throws ElementDoesNotExistException {
		
		SubmodelElement smElement = decorated.getSubmodelElement(idShortPath);
		decorated.deleteSubmodelElement(idShortPath);
		submodelElementDeleted(smElement, idShortPath);
	}

	@Override
	public void patchSubmodelElements(List<SubmodelElement> submodelElementList) {
		decorated.patchSubmodelElements(submodelElementList);
	}

	@Override
	public OperationVariable[] invokeOperation(String idShortPath, OperationVariable[] input) throws ElementDoesNotExistException {
		return decorated.invokeOperation(idShortPath, input);
	}

	@Override
	public File getFileByPath(String idShortPath) throws ElementDoesNotExistException, ElementNotAFileException, FileDoesNotExistException {
		return decorated.getFileByPath(idShortPath);
	}

	@Override
	public void setFileValue(String idShortPath, String fileName, InputStream inputStream) throws ElementDoesNotExistException, ElementNotAFileException {
		decorated.setFileValue(idShortPath, fileName, inputStream);
	}

	@Override
	public void deleteFileValue(String idShortPath) throws ElementDoesNotExistException, ElementNotAFileException, FileDoesNotExistException {
		decorated.deleteFileValue(idShortPath);
	}

	private void submodelElementCreated(SubmodelElement submodelElement, String idShort) {
		sendMqttMessage(topicFactory.createCreateSubmodelElementTopic(idShort), SubmodelElementSerializer.serializeSubmodelElement(submodelElement));
	}

	private void submodelElementUpdated(SubmodelElement submodelElement, String idShortPath) {
		sendMqttMessage(topicFactory.createUpdateSubmodelElementTopic(idShortPath), SubmodelElementSerializer.serializeSubmodelElement(submodelElement));
	}

	private void submodelElementDeleted(SubmodelElement submodelElement, String idShort) {
		sendMqttMessage(topicFactory.createDeleteSubmodelElementTopic(idShort), SubmodelElementSerializer.serializeSubmodelElement(submodelElement));
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

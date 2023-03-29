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
package org.eclipse.digitaltwin.basyx.aasrepository.feature.mqtt;

import java.util.Collection;
import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.dataformat.SerializationException;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.json.JsonSerializer;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetInformation;
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.basyx.aasrepository.AasRepository;
import org.eclipse.digitaltwin.basyx.core.exceptions.CollidingIdentifierException;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Observer for the AASAggregator that triggers MQTT events for different
 * operations on the aggregator.
 *
 * @author haque, jungjan, fischer, siebert
 *
 */
public class MqttAasRepository implements AasRepository {
	private static Logger logger = LoggerFactory.getLogger(MqttAasRepository.class);
	private MqttAasRepositoryTopicFactory topicFactory;

	private AasRepository decorated;

	private IMqttClient mqttClient;

	public MqttAasRepository(AasRepository decorated, IMqttClient mqttClient, MqttAasRepositoryTopicFactory topicFactory) {
		this.topicFactory = topicFactory;
		this.decorated = decorated;
		this.mqttClient = mqttClient;
	}

	@Override
	public Collection<AssetAdministrationShell> getAllAas() {
		return decorated.getAllAas();
	}

	@Override
	public AssetAdministrationShell getAas(String aasId) throws ElementDoesNotExistException {
		return decorated.getAas(aasId);
	}

	@Override
	public void createAas(AssetAdministrationShell aas) throws CollidingIdentifierException {
		decorated.createAas(aas);
		aasCreated(aas, getName());
	}

	@Override
	public void updateAas(String aasId, AssetAdministrationShell aas) {
		decorated.updateAas(aasId, aas);
		aasUpdated(aas, getName());
	}

	@Override
	public void deleteAas(String aasId) {
		AssetAdministrationShell shell = decorated.getAas(aasId);
		decorated.deleteAas(aasId);
		aasDeleted(shell, getName());
	}

	@Override
	public String getName() {
		return decorated.getName();
	}

	@Override
	public List<Reference> getSubmodelReferences(String aasId) {
		return decorated.getSubmodelReferences(aasId);
	}

	@Override
	public void addSubmodelReference(String aasId, Reference submodelReference) {
		decorated.addSubmodelReference(aasId, submodelReference);
	}

	@Override
	public void removeSubmodelReference(String aasId, String submodelId) {
		decorated.removeSubmodelReference(aasId, submodelId);
	}

	@Override
	public void setAssetInformation(String aasId, AssetInformation aasInfo) throws ElementDoesNotExistException {
		decorated.setAssetInformation(aasId, aasInfo);
	}
	
	@Override
	public AssetInformation getAssetInformation(String aasId) throws ElementDoesNotExistException{
		return decorated.getAssetInformation(aasId);
	}

	private void aasCreated(AssetAdministrationShell shell, String repoId) {
		sendMqttMessage(topicFactory.createCreateAASTopic(repoId), serializePayload(shell));
	}

	private void aasUpdated(AssetAdministrationShell shell, String repoId) {
		sendMqttMessage(topicFactory.createUpdateAASTopic(repoId), serializePayload(shell));
	}

	private void aasDeleted(AssetAdministrationShell shell, String repoId) {
		sendMqttMessage(topicFactory.createDeleteAASTopic(repoId), serializePayload(shell));
	}

	private String serializePayload(AssetAdministrationShell shell) {
		try {
			return new JsonSerializer().write(shell);
		} catch (SerializationException e) {
			throw new RuntimeException(e);
		}
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

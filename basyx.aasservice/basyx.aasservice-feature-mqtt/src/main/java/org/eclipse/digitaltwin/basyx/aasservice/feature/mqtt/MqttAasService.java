/*******************************************************************************
 * Copyright (C) 2023 the Eclipse BaSyx Authors
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

import java.io.File;
import java.io.InputStream;
import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetInformation;
import org.eclipse.digitaltwin.aas4j.v3.model.Key;
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.basyx.aasservice.AasService;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Decorator for the AasService that triggers MQTT events for different
 * operations on the service.
 *
 * @author jungjan
 *
 */
public class MqttAasService implements AasService {
	private static Logger logger = LoggerFactory.getLogger(MqttAasService.class);
	private MqttAasServiceTopicFactory topicFactory;

	private AasService decorated;

	private IMqttClient mqttClient;
	private String repoId;
	private ObjectMapper objectMapper;

	public MqttAasService(AasService decorated, IMqttClient mqttClient, MqttAasServiceTopicFactory topicFactory, String repoId, ObjectMapper objectMapper) {
		this.topicFactory = topicFactory;
		this.decorated = decorated;
		this.mqttClient = mqttClient;
		this.repoId = repoId;
		this.objectMapper = objectMapper;
	}

	public String serialize(Object obj) {
		try {
			return objectMapper.writeValueAsString(obj);
		} catch (JsonProcessingException e) {
			throw new RuntimeException("MqttAasService serialization");
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

	@Override
	public AssetAdministrationShell getAAS() {
		return decorated.getAAS();
	}

	@Override
	public CursorResult<List<Reference>> getSubmodelReferences(PaginationInfo pInfo) {
		return decorated.getSubmodelReferences(pInfo);
	}

	@Override
	public void setAssetInformation(AssetInformation aasInfo) {
		decorated.setAssetInformation(aasInfo);

		setAssetInfo(aasInfo);
	}

	private void setAssetInfo(AssetInformation aasInfo) {
		String shellId = extractShellId();

		sendMqttMessage(topicFactory.createSetAssetInformationTopic(repoId, shellId), serialize(aasInfo));
	}

	@Override
	public void addSubmodelReference(Reference submodelReference) {
		decorated.addSubmodelReference(submodelReference);

		addedSudmodelReference(submodelReference);
	}

	private void addedSudmodelReference(Reference submodelReference) {
		String shellId = extractShellId();

		sendMqttMessage(topicFactory.createAddSubmodelReferenceTopic(repoId, shellId), serialize(submodelReference));
	}

	@Override
	public void removeSubmodelReference(String submodelId) {
		Reference submodelReference = extractSubmodelReferenceById(submodelId);
		decorated.removeSubmodelReference(submodelId);

		removedSubmodelReference(submodelReference);
	}

	private Reference extractSubmodelReferenceById(String submodelId) {
		List<Reference> submodelsReferences = getSubmodelReferences(PaginationInfo.NO_LIMIT).getResult();

		return submodelsReferences.stream()
				.filter(reference -> containsSubmodelId(reference, submodelId))
				.findFirst()
				.orElseThrow(() -> new ElementDoesNotExistException(submodelId));
	}

	private boolean containsSubmodelId(Reference reference, String submodelId) {
		List<Key> keys = reference.getKeys();
		return keys.stream()
				.filter(key -> key.getValue()
						.equals(submodelId))
				.findFirst()
				.get() != null;
	}

	private void removedSubmodelReference(Reference submodelReference) {
		String shellId = extractShellId();

		sendMqttMessage(topicFactory.createRemoveSubmodelReferenceTopic(repoId, shellId), serialize(submodelReference));
	}

	private String extractShellId() {
		return getAAS().getId();
	}

	@Override
	public AssetInformation getAssetInformation() {
		return decorated.getAssetInformation();
	}

	@Override
	public File getThumbnail() {
		return decorated.getThumbnail();
	}

	@Override
	public void setThumbnail(String fileName, String contentType, InputStream inputStream) {
		decorated.setThumbnail(fileName, contentType, inputStream);
	}

	@Override
	public void deleteThumbnail() {
		decorated.deleteThumbnail();
	}
}

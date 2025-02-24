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

import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.basyx.aasservice.AasService;
import org.eclipse.digitaltwin.basyx.aasservice.AasServiceFactory;
import org.eclipse.paho.client.mqttv3.IMqttClient;

public class MqttAasServiceFactory implements AasServiceFactory {

	private AasServiceFactory decorated;
	private IMqttClient client;
	private MqttAasServiceTopicFactory topicFactory;
	private String repoId;
	private ObjectMapper objectMapper;

	public MqttAasServiceFactory(AasServiceFactory decorated, IMqttClient client, MqttAasServiceTopicFactory topicFactory, String repoId, ObjectMapper objectMapper) {
		this.decorated = decorated;
		this.client = client;
		this.topicFactory = topicFactory;
		this.repoId = repoId;
		this.objectMapper = objectMapper;
	}

	@Override
	public AasService create(AssetAdministrationShell aas) {
		return new MqttAasService(decorated.create(aas), client, topicFactory, repoId, objectMapper);
	}

	@Override
	public AasService create(String aasId) {
		return new MqttAasService(decorated.create(aasId), client, topicFactory, repoId, objectMapper);
	}

}

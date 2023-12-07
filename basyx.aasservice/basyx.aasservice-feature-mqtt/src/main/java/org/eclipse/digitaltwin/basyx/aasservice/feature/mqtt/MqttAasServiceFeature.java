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

import org.eclipse.digitaltwin.basyx.aasrepository.AasRepository;
import org.eclipse.digitaltwin.basyx.aasservice.AasServiceFactory;
import org.eclipse.digitaltwin.basyx.aasservice.feature.AasServiceFeature;
import org.eclipse.digitaltwin.basyx.common.mqttcore.encoding.URLEncoder;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

@ConditionalOnExpression("#{${" + MqttAasServiceFeature.FEATURENAME + ".enabled:false} or ${basyx.feature.mqtt.enabled:false}}")
@Component
public class MqttAasServiceFeature implements AasServiceFeature {
	public final static String FEATURENAME = "basyx.aasservice.feature.mqtt";

	@Value("#{${" + FEATURENAME + ".enabled:false} or ${basyx.feature.mqtt.enabled:false}}")
	private boolean enabled;

	private IMqttClient mqttClient;
	private String repoId;
	private ObjectMapper objectMapper;

	@Autowired
	public MqttAasServiceFeature(IMqttClient mqttClient, AasRepository repo, ObjectMapper objectMapper) {
		this.mqttClient = mqttClient;
		this.repoId = repo.getName();
		this.objectMapper = objectMapper;
	}

	@Override
	public AasServiceFactory decorate(AasServiceFactory aasServiceFactory) {
		return new MqttAasServiceFactory(aasServiceFactory, mqttClient, new MqttAasServiceTopicFactory(new URLEncoder()), repoId, objectMapper);
	}

	@Override
	public void initialize() {
	}

	@Override
	public void cleanUp() {
	}

	@Override
	public String getName() {
		return "AasService MQTT";
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

}

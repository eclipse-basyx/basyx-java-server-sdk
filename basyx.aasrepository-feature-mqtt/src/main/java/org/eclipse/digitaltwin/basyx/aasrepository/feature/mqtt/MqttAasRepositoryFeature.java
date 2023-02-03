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


package org.eclipse.digitaltwin.basyx.aasrepository.feature.mqtt;

import org.eclipse.digitaltwin.basyx.aasrepository.AasRepositoryFactory;
import org.eclipse.digitaltwin.basyx.aasrepository.feature.AasRepositoryFeature;
import org.eclipse.digitaltwin.basyx.aasrepository.feature.mqtt.encoding.URLEncoder;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MqttAasRepositoryFeature implements AasRepositoryFeature {
	public final static String FEATURENAME = "basyx.aasrepository.feature.mqtt";

	@Value("#{${" + FEATURENAME + ".enabled:false} or ${basyx.feature.mqtt.enabled:false}}")
	private boolean enabled;

	private IMqttClient mqttClient;

	@Autowired
	public MqttAasRepositoryFeature(IMqttClient mqttClient) {
		this.mqttClient = mqttClient;
	}

	@Override
	public AasRepositoryFactory decorate(AasRepositoryFactory aasServiceFactory) {
		return new MqttAasRepositoryFactory(aasServiceFactory, mqttClient, new MqttAasRepositoryTopicFactory(new URLEncoder()));
	}

	@Override
	public void initialize() {
	}

	@Override
	public void cleanUp() {

	}

	@Override
	public String getName() {
		return "AasRepository MQTT";
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}
}

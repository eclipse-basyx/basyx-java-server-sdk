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

package org.eclipse.digitaltwin.basyx.submodelservice.feature.mqtt;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MQTT configuration to allow for the automatic enable of the feature using the
 * config file.
 * 
 * @author rana
 */
@ConditionalOnExpression("#{${" + MqttSubmodelServiceFeature.FEATURENAME + ".enabled:false} or ${basyx.feature.mqtt.enabled:false}}")
@Configuration
public class MqttSubmodelServiceConfiguration {

	@ConditionalOnMissingBean
	@Bean
	public IMqttClient mqttClient(@Value("${mqtt.clientId}") String clientId, @Value("${mqtt.hostname}") String hostname, @Value("${mqtt.port}") int port) throws MqttException {
		IMqttClient mqttClient = new MqttClient("tcp://" + hostname + ":" + port, clientId, new MemoryPersistence());

		mqttClient.connect(mqttConnectOptions());

		return mqttClient;
	}

	@ConditionalOnMissingBean
	@Bean
	@ConfigurationProperties(prefix = "mqtt")
	public MqttConnectOptions mqttConnectOptions() {
		MqttConnectOptions mqttConceptOptions = new MqttConnectOptions();
		mqttConceptOptions.setAutomaticReconnect(true);
		return mqttConceptOptions;
	}
}

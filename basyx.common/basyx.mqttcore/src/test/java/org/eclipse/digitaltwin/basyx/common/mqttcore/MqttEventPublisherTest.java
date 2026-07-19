/*******************************************************************************
 * Copyright (C) 2026 the Eclipse BaSyx Authors
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
package org.eclipse.digitaltwin.basyx.common.mqttcore;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.nio.charset.StandardCharsets;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.slf4j.Logger;

public class MqttEventPublisherTest {
	@Test
	public void publishesPayloadAsUtf8() throws Exception {
		IMqttClient client = mock(IMqttClient.class);
		Logger logger = mock(Logger.class);
		ArgumentCaptor<MqttMessage> messageCaptor = ArgumentCaptor.forClass(MqttMessage.class);

		MqttEventPublisher.publish(client, "topic", "Grüße", logger);

		verify(client).publish(eq("topic"), messageCaptor.capture());
		assertEquals("Grüße", new String(messageCaptor.getValue().getPayload(), StandardCharsets.UTF_8));
	}

	@Test
	public void handlesPublishFailuresConsistently() throws Exception {
		IMqttClient client = mock(IMqttClient.class);
		Logger logger = mock(Logger.class);
		MqttException publishError = new MqttException(MqttException.REASON_CODE_CLIENT_NOT_CONNECTED);
		org.mockito.Mockito.doThrow(publishError).when(client).publish(eq("topic"), org.mockito.ArgumentMatchers.any(MqttMessage.class));

		MqttEventPublisher.publish(client, "topic", "payload", logger);

		verify(logger).error("Could not send MQTT message to topic 'topic'", publishError);
	}
}

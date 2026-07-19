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
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.eclipse.digitaltwin.basyx.common.mqttcore.listener.MqttTestListener.MqttEvent;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.junit.Test;

import io.moquette.broker.config.FluentConfig;

public class MqttClientConfigurationTest {
	private static final String USERNAME = "testuser";
	private static final String PASSWORD = "passwd";
	private static final String PASSWORD_HASH = "0d6be69b264717f2dd33652e212b173104b4a647b7c11ae72e9885f11cd312fb";
	private final MqttClientConfiguration configuration = new MqttClientConfiguration();

	@Test
	public void connectsAnonymouslyOverTcp() throws Exception {
		try (MqttBrokerTestSupport fixture = MqttBrokerTestSupport.start()) {
			IMqttClient client = fixture.trackClient(configuration.mqttClient(uniqueClientId(), "localhost", fixture.port(), "tcp", configuration.mqttConnectOptions("", "")));
			assertTrue(client.isConnected());
		}
	}

	@Test
	public void acceptsValidAndRejectsInvalidTcpCredentials() throws Exception {
		Path passwordFile = Files.createTempFile("moquette-passwords-", ".conf");
		Files.writeString(passwordFile, USERNAME + ":" + PASSWORD_HASH + System.lineSeparator(), StandardCharsets.UTF_8);
		try (MqttBrokerTestSupport fixture = MqttBrokerTestSupport.start(new FluentConfig().host("localhost").port(0).disallowAnonymous()
				.passwordFile(passwordFile.toAbsolutePath().toString()).disablePersistence().disableTelemetry().build())) {
			IMqttClient validClient = fixture.trackClient(
					configuration.mqttClient(uniqueClientId(), "localhost", fixture.port(), "tcp", configuration.mqttConnectOptions(USERNAME, PASSWORD)));
			assertTrue(validClient.isConnected());

			MqttConnectOptions invalidOptions = configuration.mqttConnectOptions(USERNAME, "wrong-password");
			invalidOptions.setAutomaticReconnect(false);
			MqttException error = assertThrows(MqttException.class,
					() -> configuration.mqttClient(uniqueClientId(), "localhost", fixture.port(), "tcp", invalidOptions));
			assertEquals(MqttException.REASON_CODE_FAILED_AUTHENTICATION, error.getReasonCode());
		} finally {
			Files.deleteIfExists(passwordFile);
		}
	}

	@Test
	public void connectsAnonymouslyOverWebSocket() throws Exception {
		try (MqttBrokerTestSupport fixture = MqttBrokerTestSupport.startWithWebSocket()) {
			IMqttClient client = fixture.trackClient(configuration.mqttClient(uniqueClientId(), "localhost", fixture.websocketPort(), "ws", configuration.mqttConnectOptions("", "")));
			assertTrue(client.isConnected());
		}
	}

	@Test
	public void publishesAndAwaitsOneHundredUtf8Events() throws Exception {
		try (MqttBrokerTestSupport fixture = MqttBrokerTestSupport.start()) {
			IMqttClient client = fixture.connectClient();
			for (int i = 0; i < 100; i++) {
				client.publish("stress/events/" + i, new MqttMessage(("payload-ä-" + i).getBytes(StandardCharsets.UTF_8)));
			}
			for (int i = 0; i < 100; i++) {
				MqttEvent event = fixture.listener().awaitEvent("stress/events/" + i);
				assertEquals("payload-ä-" + i, event.payload());
			}
		}
	}

	@Test
	public void publishesConcurrentlyFromMultipleClients() throws Exception {
		ExecutorService publishers = Executors.newFixedThreadPool(4);
		try (MqttBrokerTestSupport fixture = MqttBrokerTestSupport.start()) {
			Set<String> expectedEvents = new HashSet<>();
			Set<Future<?>> publishes = new HashSet<>();
			for (int clientIndex = 0; clientIndex < 4; clientIndex++) {
				IMqttClient client = fixture.connectClient();
				for (int messageIndex = 0; messageIndex < 25; messageIndex++) {
					String topic = "concurrent/" + clientIndex + "/" + messageIndex;
					String payload = "payload-ä-" + clientIndex + "-" + messageIndex;
					expectedEvents.add(topic + "\n" + payload);
					publishes.add(publishers.submit(() -> {
						client.publish(topic, new MqttMessage(payload.getBytes(StandardCharsets.UTF_8)));
						return null;
					}));
				}
			}
			for (Future<?> publish : publishes) {
				publish.get();
			}
			Set<String> actualEvents = new HashSet<>();
			for (int i = 0; i < expectedEvents.size(); i++) {
				MqttEvent event = fixture.listener().awaitNextEvent();
				actualEvents.add(event.topic() + "\n" + event.payload());
			}
			assertEquals(expectedEvents, actualEvents);
		} finally {
			publishers.shutdownNow();
		}
	}

	@Test
	public void reportsSessionLoopErrorsAfterBrokerShutdown() throws Exception {
		MqttBrokerTestSupport fixture = MqttBrokerTestSupport.start();
		fixture.listener().onSessionLoopError(new IllegalStateException("session-loop-failure"));

		AssertionError error = assertThrows(AssertionError.class, fixture::close);

		assertEquals("session-loop-failure", error.getCause().getMessage());
	}

	private static String uniqueClientId() {
		return "core-" + UUID.randomUUID().toString().substring(0, 16);
	}

}

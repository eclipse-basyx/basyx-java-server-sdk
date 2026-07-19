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

import java.io.IOException;
import java.lang.reflect.Field;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.IntSupplier;

import org.eclipse.digitaltwin.basyx.common.mqttcore.listener.MqttTestListener;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;

import io.moquette.broker.Server;
import io.moquette.broker.config.FluentConfig;
import io.moquette.broker.config.IConfig;

/**
 * Isolated, in-memory Moquette fixture shared by MQTT feature tests.
 */
public final class MqttBrokerTestSupport implements AutoCloseable {
	private static final Duration PORT_BIND_TIMEOUT = Duration.ofSeconds(10);
	private static final String WEBSOCKET_TRANSPORT_NAME = "Websocket MQTT";
	private final MqttTestListener listener = new MqttTestListener();
	private final Server broker;
	private final List<IMqttClient> clients = new ArrayList<>();
	private final int port;
	private final int websocketPort;

	private MqttBrokerTestSupport(IConfig config, boolean websocketEnabled) throws IOException {
		broker = new Server();
		try {
			broker.startServer(config, List.of(listener));
			port = awaitBoundPort(broker::getPort, "TCP");
			websocketPort = websocketEnabled ? awaitBoundPort(() -> readTransportPort(broker, WEBSOCKET_TRANSPORT_NAME), "WebSocket") : -1;
		} catch (IOException | RuntimeException | Error startupError) {
			try {
				broker.stopServer();
			} catch (Throwable cleanupError) {
				startupError.addSuppressed(cleanupError);
			}
			throw startupError;
		}
	}

	public static MqttBrokerTestSupport start() throws IOException {
		return start(new FluentConfig().host("localhost").port(0).allowAnonymous().disablePersistence().disableTelemetry().build());
	}

	public static MqttBrokerTestSupport start(IConfig config) throws IOException {
		return new MqttBrokerTestSupport(config, false);
	}

	public static MqttBrokerTestSupport startWithWebSocket() throws IOException {
		IConfig config = new FluentConfig().host("localhost").port(0).websocketPort(0).allowAnonymous().disablePersistence().disableTelemetry().build();
		return new MqttBrokerTestSupport(config, true);
	}

	public MqttClient connectClient() throws MqttException {
		MqttClient client = new MqttClient(serverUri(), uniqueClientId());
		clients.add(client);
		client.connect();
		return client;
	}

	public <T extends IMqttClient> T trackClient(T client) {
		clients.add(client);
		return client;
	}

	public String serverUri() {
		return "tcp://localhost:" + port;
	}

	public int port() {
		return port;
	}

	public int websocketPort() {
		if (websocketPort < 1) {
			throw new IllegalStateException("The fixture was not started with WebSocket support");
		}
		return websocketPort;
	}

	public MqttTestListener listener() {
		return listener;
	}

	private static String uniqueClientId() {
		return "mqtt-" + UUID.randomUUID().toString().substring(0, 16);
	}

	private static int awaitBoundPort(IntSupplier portSupplier, String transport) throws IOException {
		long deadline = System.nanoTime() + PORT_BIND_TIMEOUT.toNanos();
		do {
			int boundPort = portSupplier.getAsInt();
			if (boundPort > 0) {
				return boundPort;
			}
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				throw new IOException("Interrupted while awaiting Moquette " + transport + " port", e);
			}
		} while (System.nanoTime() < deadline);
		throw new IOException("Timed out awaiting Moquette " + transport + " port");
	}

	@SuppressWarnings("unchecked")
	private static int readTransportPort(Server broker, String transportName) {
		try {
			Field acceptorField = Server.class.getDeclaredField("acceptor");
			acceptorField.setAccessible(true);
			Object acceptor = acceptorField.get(broker);
			Field portsField = acceptor.getClass().getDeclaredField("ports");
			portsField.setAccessible(true);
			Map<String, Integer> ports = (Map<String, Integer>) portsField.get(acceptor);
			return ports.getOrDefault(transportName, -1);
		} catch (ReflectiveOperationException e) {
			throw new IllegalStateException("Could not read Moquette's bound " + transportName + " port", e);
		}
	}

	@Override
	public void close() throws MqttException {
		Throwable failure = null;
		for (IMqttClient client : clients) {
			try {
				if (client.isConnected()) {
					client.disconnect();
				}
			} catch (Throwable error) {
				failure = appendFailure(failure, error);
			} finally {
				try {
					client.close();
				} catch (Throwable error) {
					failure = appendFailure(failure, error);
				}
			}
		}
		try {
			broker.stopServer();
		} catch (Throwable error) {
			failure = appendFailure(failure, error);
		}
		try {
			listener.assertNoSessionLoopError();
		} catch (Throwable error) {
			failure = appendFailure(failure, error);
		}
		if (failure instanceof MqttException mqttException) {
			throw mqttException;
		}
		if (failure instanceof RuntimeException runtimeException) {
			throw runtimeException;
		}
		if (failure instanceof Error error) {
			throw error;
		}
		if (failure != null) {
			throw new AssertionError("Could not close MQTT broker test fixture", failure);
		}
	}

	private static Throwable appendFailure(Throwable failure, Throwable nextFailure) {
		if (failure == null) {
			return nextFailure;
		}
		failure.addSuppressed(nextFailure);
		return failure;
	}
}

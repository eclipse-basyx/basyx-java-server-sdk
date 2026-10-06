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
import java.net.BindException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.eclipse.digitaltwin.basyx.common.mqttcore.listener.MqttTestListener;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

import io.moquette.broker.Server;
import io.moquette.broker.config.FluentConfig;
import io.moquette.broker.config.IConfig;

/**
 * Isolated, in-memory Moquette fixture shared by MQTT feature tests.
 */
public final class MqttBrokerTestSupport implements AutoCloseable {
	private static final String BROKER_HOST = "localhost";
	private static final int MAX_START_ATTEMPTS = 5;
	/**
	 * Paho wakes a synchronous QoS 1 publish when the PUBACK arrives, but only
	 * releases the in-flight slot later on its callback thread. On slow machines
	 * these unreleased slots pile up and exceed Paho's default window of 10, which
	 * fails the next publish with "Too many publishes in progress (32202)". The
	 * window is therefore sized above the number of messages any test publishes
	 * through a single client.
	 */
	private static final int TEST_CLIENT_MAX_INFLIGHT = 1000;
	private final MqttTestListener listener;
	private final Server broker;
	private final List<IMqttClient> clients = new ArrayList<>();
	private final int port;
	private final int websocketPort;

	private MqttBrokerTestSupport(Server broker, MqttTestListener listener, int port, int websocketPort) {
		this.broker = broker;
		this.listener = listener;
		this.port = port;
		this.websocketPort = websocketPort;
	}

	public static MqttBrokerTestSupport start() throws IOException {
		return start(new FluentConfig().host(BROKER_HOST).allowAnonymous().disablePersistence().disableTelemetry().build());
	}

	/**
	 * Starts a broker with the given configuration. The TCP port is always
	 * assigned by the fixture and overrides any port in {@code config}.
	 */
	public static MqttBrokerTestSupport start(IConfig config) throws IOException {
		return startOnFreePorts(config, false);
	}

	public static MqttBrokerTestSupport startWithWebSocket() throws IOException {
		return startOnFreePorts(new FluentConfig().host(BROKER_HOST).allowAnonymous().disablePersistence().disableTelemetry().build(), true);
	}

	/**
	 * Moquette 0.17 cannot reliably report ephemeral ports: the acceptor stores
	 * them in a plain HashMap from a Netty thread, while Server#getPort() writes a
	 * 0 placeholder into the same map from the caller thread. Under contention the
	 * real port is lost and getPort() keeps returning 0. The fixture therefore
	 * reserves concrete ports up front and retries if another process grabs one
	 * before Moquette binds it.
	 */
	private static MqttBrokerTestSupport startOnFreePorts(IConfig config, boolean websocketEnabled) throws IOException {
		RuntimeException lastBindFailure = null;
		for (int attempt = 0; attempt < MAX_START_ATTEMPTS; attempt++) {
			int port = reserveFreePort();
			int websocketPort = websocketEnabled ? reserveFreePort() : -1;
			if (port == websocketPort) {
				continue;
			}
			config.setProperty(IConfig.PORT_PROPERTY_NAME, Integer.toString(port));
			if (websocketEnabled) {
				config.setProperty(IConfig.WEB_SOCKET_PORT_PROPERTY_NAME, Integer.toString(websocketPort));
			}
			MqttTestListener listener = new MqttTestListener();
			Server broker = new Server();
			try {
				broker.startServer(config, List.of(listener));
				return new MqttBrokerTestSupport(broker, listener, port, websocketPort);
			} catch (IOException | RuntimeException | Error startupError) {
				try {
					broker.stopServer();
				} catch (Throwable cleanupError) {
					startupError.addSuppressed(cleanupError);
				}
				if (!(startupError instanceof RuntimeException runtimeError) || !isBindFailure(runtimeError)) {
					throw startupError;
				}
				if (lastBindFailure != null) {
					runtimeError.addSuppressed(lastBindFailure);
				}
				lastBindFailure = runtimeError;
			}
		}
		throw new IOException("Could not bind Moquette to a free port after " + MAX_START_ATTEMPTS + " attempts", lastBindFailure);
	}

	private static int reserveFreePort() throws IOException {
		try (ServerSocket socket = new ServerSocket(0, 0, InetAddress.getByName(BROKER_HOST))) {
			return socket.getLocalPort();
		}
	}

	private static boolean isBindFailure(Throwable error) {
		for (Throwable cause = error; cause != null; cause = cause.getCause()) {
			if (cause instanceof BindException) {
				return true;
			}
		}
		return false;
	}

	public MqttClient connectClient() throws MqttException {
		MqttClient client = new MqttClient(serverUri(), uniqueClientId());
		clients.add(client);
		MqttConnectOptions options = new MqttConnectOptions();
		options.setMaxInflight(TEST_CLIENT_MAX_INFLIGHT);
		client.connect(options);
		return client;
	}

	public <T extends IMqttClient> T trackClient(T client) {
		clients.add(client);
		return client;
	}

	public String serverUri() {
		return "tcp://" + BROKER_HOST + ":" + port;
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

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
package org.eclipse.digitaltwin.basyx.common.mqttcore.listener;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import io.moquette.interception.InterceptHandler;
import io.moquette.interception.messages.InterceptAcknowledgedMessage;
import io.moquette.interception.messages.InterceptConnectMessage;
import io.moquette.interception.messages.InterceptConnectionLostMessage;
import io.moquette.interception.messages.InterceptDisconnectMessage;
import io.moquette.interception.messages.InterceptPublishMessage;
import io.moquette.interception.messages.InterceptSubscribeMessage;
import io.moquette.interception.messages.InterceptUnsubscribeMessage;

/**
 * Thread-safe embedded-broker listener for MQTT feature tests.
 */
public class MqttTestListener implements InterceptHandler {
	private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(10);
	private static final int EVENT_QUEUE_CAPACITY = 1024;
	private final BlockingQueue<MqttEvent> events = new ArrayBlockingQueue<>(EVENT_QUEUE_CAPACITY);
	private final AtomicReference<Throwable> sessionLoopError = new AtomicReference<>();

	@Override
	public String getID() {
		return MqttTestListener.class.getName();
	}

	@Override
	public Class<?>[] getInterceptedMessageTypes() {
		return InterceptHandler.ALL_MESSAGE_TYPES;
	}

	@Override
	public void onConnect(InterceptConnectMessage message) {
	}

	@Override
	public void onConnectionLost(InterceptConnectionLostMessage message) {
	}

	@Override
	public void onDisconnect(InterceptDisconnectMessage message) {
	}

	@Override
	public void onMessageAcknowledged(InterceptAcknowledgedMessage message) {
	}

	@Override
	public void onPublish(InterceptPublishMessage message) {
		MqttEvent event = new MqttEvent(message.getTopicName(), message.getPayload().toString(StandardCharsets.UTF_8));
		if (!events.offer(event)) {
			sessionLoopError.compareAndSet(null, new AssertionError("MQTT test event queue exceeded its capacity of " + EVENT_QUEUE_CAPACITY));
		}
	}

	@Override
	public void onSubscribe(InterceptSubscribeMessage message) {
	}

	@Override
	public void onUnsubscribe(InterceptUnsubscribeMessage message) {
	}

	@Override
	public void onSessionLoopError(Throwable error) {
		sessionLoopError.compareAndSet(null, error);
	}

	public MqttEvent awaitEvent(String expectedTopic) {
		return awaitEvent(expectedTopic, DEFAULT_TIMEOUT);
	}

	public MqttEvent awaitEvent(String expectedTopic, Duration timeout) {
		long deadline = System.nanoTime() + timeout.toNanos();
		try {
			while (System.nanoTime() < deadline) {
				assertNoSessionLoopError();
				long remaining = deadline - System.nanoTime();
				MqttEvent event = events.poll(Math.min(remaining, TimeUnit.MILLISECONDS.toNanos(100)), TimeUnit.NANOSECONDS);
				if (event != null && expectedTopic.equals(event.topic())) {
					assertNoSessionLoopError();
					return event;
				}
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new AssertionError("Interrupted while awaiting MQTT topic '" + expectedTopic + "'", e);
		}
		assertNoSessionLoopError();
		throw new AssertionError("Timed out awaiting MQTT topic '" + expectedTopic + "'");
	}

	public MqttEvent awaitNextEvent() {
		return awaitNextEvent(DEFAULT_TIMEOUT);
	}

	public MqttEvent awaitNextEvent(Duration timeout) {
		long deadline = System.nanoTime() + timeout.toNanos();
		try {
			while (System.nanoTime() < deadline) {
				assertNoSessionLoopError();
				long remaining = deadline - System.nanoTime();
				MqttEvent event = events.poll(Math.min(remaining, TimeUnit.MILLISECONDS.toNanos(100)), TimeUnit.NANOSECONDS);
				if (event != null) {
					assertNoSessionLoopError();
					return event;
				}
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new AssertionError("Interrupted while awaiting an MQTT event", e);
		}
		assertNoSessionLoopError();
		throw new AssertionError("Timed out awaiting an MQTT event");
	}

	public void assertNoSessionLoopError() {
		Throwable error = sessionLoopError.get();
		if (error != null) {
			throw new AssertionError("Moquette reported an asynchronous session-loop error", error);
		}
	}

	public record MqttEvent(String topic, String payload) {
	}
}

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

package org.eclipse.digitaltwin.basyx.submodelrepository.feature.mqtt;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import io.moquette.interception.InterceptHandler;
import io.moquette.interception.messages.InterceptAcknowledgedMessage;
import io.moquette.interception.messages.InterceptConnectMessage;
import io.moquette.interception.messages.InterceptConnectionLostMessage;
import io.moquette.interception.messages.InterceptDisconnectMessage;
import io.moquette.interception.messages.InterceptPublishMessage;
import io.moquette.interception.messages.InterceptSubscribeMessage;
import io.moquette.interception.messages.InterceptUnsubscribeMessage;

/**
 * Very simple MQTT broker listener for testing API events. Stores the last
 * received event and makes its topic and payload available for reading.
 * 
 * @author espen
 *
 */
public class MqttTestListener implements InterceptHandler {
	// Topic and payload of the most recent event
	public String lastTopic;
	public String lastPayload;
	private ArrayList<String> topics = new ArrayList<>();

	@Override
	public String getID() {
		return null;
	}

	@Override
	public Class<?>[] getInterceptedMessageTypes() {
		return null;
	}

	@Override
	public void onConnect(InterceptConnectMessage arg0) {
	}

	@Override
	public void onConnectionLost(InterceptConnectionLostMessage arg0) {
	}

	@Override
	public void onDisconnect(InterceptDisconnectMessage arg0) {
	}

	@Override
	public void onMessageAcknowledged(InterceptAcknowledgedMessage arg0) {
	}

	@Override
	public synchronized void onPublish(InterceptPublishMessage msg) {
		topics.add(msg.getTopicName());
		lastTopic = msg.getTopicName();
		lastPayload = msg.getPayload().toString(StandardCharsets.UTF_8);
	}

	@Override
	public void onSubscribe(InterceptSubscribeMessage arg0) {
	}

	@Override
	public void onUnsubscribe(InterceptUnsubscribeMessage arg0) {
	}

	public ArrayList<String> getTopics() {
		return topics;
	}
}

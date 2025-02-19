/*******************************************************************************
 * Copyright (C) 2024 DFKI GmbH (https://www.dfki.de/en/web)
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
 * 
 ******************************************************************************/
package org.eclipse.digitaltwin.basyx.aasrepository.feature.kafka;

import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.common.TopicPartition;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.core.DeserializationException;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.json.JsonDeserializer;
import org.eclipse.digitaltwin.basyx.aasrepository.feature.kafka.events.model.AasEvent;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.ConsumerSeekAware;
import org.springframework.stereotype.Component;
/**
 * @author geso02 (Sonnenberg DFKI GmbH)
 */
@KafkaListener(topics = AasEventKafkaListener.TOPIC_NAME, batch = "false", groupId = "kafka-test-aas", autoStartup = "true")
@Component 
public class AasEventKafkaListener implements ConsumerSeekAware {

	public static final String TOPIC_NAME = "aas-events";
	
	private final LinkedBlockingDeque<AasEvent> evt = new LinkedBlockingDeque<AasEvent>();
	private final JsonDeserializer deserializer;
	private final CountDownLatch latch = new CountDownLatch(1);
	private final long startupTime;

	public AasEventKafkaListener(JsonDeserializer deserializer) {
		this.deserializer = deserializer;
		startupTime = System.currentTimeMillis();
	}

	@KafkaHandler
	public void receiveMessage(String content) {
		try {
			AasEvent event = deserializer.read(content, AasEvent.class);
			evt.offerFirst(event);
		} catch (DeserializationException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public AasEvent next(int value, TimeUnit unit) throws InterruptedException {
		return evt.pollLast(value, unit);
	}

	public AasEvent next() throws InterruptedException {
		return next(1, TimeUnit.MINUTES);
	}

	@Override
	public void onPartitionsAssigned(Map<TopicPartition, Long> assignments, ConsumerSeekCallback callback) {
		for (TopicPartition eachPartition : assignments.keySet()) {
			if (TOPIC_NAME.equals(eachPartition.topic())) {
				callback.seekToTimestamp(TOPIC_NAME, 0, startupTime-1);
				latch.countDown();
			}
		}
	}

	public void awaitTopicAssignment() throws InterruptedException {
		if (!latch.await(1, TimeUnit.MINUTES)) {
			throw new RuntimeException("Timeout occured while waiting for partition assignment. Is kafka running?");
		}
	}
}
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
package org.eclipse.digitaltwin.basyx.submodelservice.feature.kafka;

import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.common.TopicPartition;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.core.DeserializationException;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.json.JsonDeserializer;
import org.eclipse.digitaltwin.basyx.submodelservice.feature.kafka.events.model.SubmodelEvent;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.ConsumerSeekAware;
/**
 * @author geso02 (Sonnenberg DFKI GmbH)
 */
@KafkaListener(topics = SubmodelEventKafkaListener.TOPIC_NAME, batch = "false", groupId = "kafka-test", autoStartup = "true")
@TestComponent
public class SubmodelEventKafkaListener implements ConsumerSeekAware {

	public static final String TOPIC_NAME = "submodel-events";
	
	private final LinkedBlockingDeque<SubmodelEvent> evt = new LinkedBlockingDeque<SubmodelEvent>();
	private final JsonDeserializer deserializer = new JsonDeserializer();
	private final CountDownLatch latch = new CountDownLatch(1);

	@KafkaHandler
	public void receiveMessage(String content) {
		try {
			SubmodelEvent event = deserializer.read(content, SubmodelEvent.class);
			evt.offerFirst(event);
		} catch (DeserializationException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public SubmodelEvent next(int value, TimeUnit unit) throws InterruptedException {
		return evt.pollLast(value, unit);
	}

	public SubmodelEvent next() throws InterruptedException {
		return next(5, TimeUnit.MINUTES);
	}

	@Override
	public void onPartitionsAssigned(Map<TopicPartition, Long> assignments, ConsumerSeekCallback callback) {
		for (TopicPartition eachPartition : assignments.keySet()) {
			if (TOPIC_NAME.equals(eachPartition.topic())) {
				latch.countDown();
			}
		}
	}

	public void awaitTopicAssignment() throws InterruptedException {
//		if (!latch.await(30, TimeUnit.MINUTES)) {
//			throw new RuntimeException("Timeout occured while waiting for partition assignment. Is kafka running?");
//		}
	}
}

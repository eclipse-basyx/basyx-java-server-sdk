/*******************************************************************************
 * Copyright (C) 2023 DFKI GmbH (https://www.dfki.de/en/web)
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
package org.eclipse.digitaltwin.basyx.aasregistry.service.storage.mongodb;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.common.TopicPartition;
import org.eclipse.digitaltwin.basyx.aasregistry.service.tests.integration.BaseIntegrationTest;
import org.eclipse.digitaltwin.basyx.aasregistry.service.tests.integration.EventQueue;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.ConsumerSeekAware;
import org.springframework.stereotype.Component;
import org.springframework.test.context.TestPropertySource;

import com.fasterxml.jackson.databind.ObjectMapper;

@TestPropertySource(properties = { "registry.type=mongodb", "events.sink=kafka",
		"spring.kafka.bootstrap-servers=PLAINTEXT_HOST://localhost:9092", "spring.data.mongodb.database=aasregistry",
		"spring.data.mongodb.uri=mongodb://mongoAdmin:mongoPassword@localhost:27017/" })
public class KafkaEventsMongoDbStorageIntegrationTest extends BaseIntegrationTest {

	@Autowired
	private RegistrationEventKafkaListener listener;

	@Before
	public void awaitAssignment() throws InterruptedException {
		listener.awaitTopicAssignment();
	}
	
	@Override
	public EventQueue queue() {
		return listener.queue;
	}
	

	@KafkaListener(topics = "aas-registry", batch = "false", groupId = "kafka-test", autoStartup = "true" )
	@Component
	private static class RegistrationEventKafkaListener implements ConsumerSeekAware {
		
		
		private final EventQueue queue;
		private final CountDownLatch latch = new CountDownLatch(1);
		
		@SuppressWarnings("unused")
		public RegistrationEventKafkaListener(ObjectMapper mapper) {
			this.queue = new EventQueue(mapper);
		}
		
		@KafkaHandler
		public void receiveMessage(byte[] content) {
			queue.offer(new String((byte[]) content, StandardCharsets.UTF_8));
		}

		@Override
		public void onPartitionsAssigned(Map<TopicPartition, Long> assignments,
					ConsumerSeekCallback callback) {
			for (TopicPartition eachPartition : assignments.keySet()) {
				if ("aas-registry".equals(eachPartition.topic())) {
					callback.seekToEnd(List.of(eachPartition));
					latch.countDown();
					System.out.println("Partition registered");
				}
			}		
		}
		
		public void awaitTopicAssignment() throws InterruptedException {
			if (!latch.await(5, TimeUnit.MINUTES)) {
				throw new RuntimeException("Timeout occured while waiting for partition assignment. Is kafka running?");
			}
		}
	}
}
/*******************************************************************************
 * Copyright (C) 2025 DFKI GmbH (https://www.dfki.de/en/web)
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
package org.eclipse.digitaltwin.basyx.kafka;

import java.time.Duration;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.Properties;
import java.util.UUID;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.core.DeserializationException;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.json.JsonDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KafkaAdapter<T> {
	
	private static final Logger LOG = LoggerFactory.getLogger(KafkaAdapter.class);
	
	private final KafkaConsumer<String, String> consumer;

	private final String bootstrapServers;
	private final String topic;
	private final String groupId;
	private final JsonDeserializer deserializer = new JsonDeserializer();
	private final Class<T> cls;

	private final Deque<String> deque = new ArrayDeque<>();

	private Duration assignmentTimeout = Duration.ofMinutes(30);
	private Duration pollTimeout = Duration.ofMinutes(10);

	public KafkaAdapter(String bootstrapServers, String topic, Class<T> cls) {
		this.bootstrapServers = bootstrapServers;
		this.topic = topic;
		this.groupId = "kafka-test" + UUID.randomUUID();
		this.consumer = init();
		this.cls = cls;
		awaitAssignment();
		consumer.seekToEnd(consumer.assignment()); 
	}


	public void setAssignmentTimeout(Duration assignmentTimeout) {
		this.assignmentTimeout = assignmentTimeout;
	}

	public void setPollTimeout(Duration pollTimeout) {
		this.pollTimeout = pollTimeout;
	}

	private KafkaConsumer<String, String> init() {
		Properties props = new Properties();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");// "earliest");
		props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
		KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
		consumer.subscribe(Collections.singletonList(topic));

		return consumer;
	}

	private void awaitAssignment() {
		LOG.info("Await Assignment");
		long start = System.currentTimeMillis();
		long deadline = start + assignmentTimeout.toMillis();
		while (consumer.assignment().isEmpty() && System.currentTimeMillis() < deadline) {
			consumer.poll(Duration.ofMillis(100));
		}
		if (consumer.assignment().isEmpty()) {
			throw new RuntimeException("Failed to wait for topic assignment. Is KAFKA running?");
		}
		LOG.info("Partitions {} assigned after {} ms." + consumer.assignment(), System.currentTimeMillis() - start);
	}

	private String nextMessage() {
		return nextMessage(pollTimeout);
	}
	
	private String nextMessage(Duration duration) {
		LOG.info("Reading Kafka message");
		long deadline = System.currentTimeMillis() + duration.toMillis();

		while (deque.isEmpty() && System.currentTimeMillis() < deadline) {
			ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
			for (ConsumerRecord<String, String> record : records) {
				this.deque.add(record.value());
				consumer.commitSync();
			}
		}
		if (!deque.isEmpty()) {
			LOG.info("Got message");	
			return deque.remove();
		}
		LOG.info("Failed to receive message");
		return null;
	}

	public T next() {
		String next = nextMessage();
		if (next != null) {
			try {
				return deserializer.read(next, cls);
			} catch (DeserializationException e) {
				throw new RuntimeException("Failed to deserialize event!", e);
			}
		} else {
			throw new RuntimeException("No message received for topic " + topic + " in " + pollTimeout);
		}
	}

	public void assertNoAdditionalMessages() {
		String next = nextMessage(Duration.ofMillis(100)); 
		if (next != null) {
			throw new RuntimeException("Got an additional message within 1 second: \n" + next); 
		}
	}

	public void close() {
		LOG.info("Dispose");
		this.consumer.close();
	}

	public void skipMessages() {
		LOG.info("SkipMessages");
		while (nextMessage(Duration.ofMillis(100)) != null);
	}

}
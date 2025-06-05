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
import java.util.Collections;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

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

	private final String topic;
	private final JsonDeserializer deserializer = new JsonDeserializer();
	private final Class<T> cls;

	private final LinkedBlockingDeque<String> deque = new LinkedBlockingDeque<>();

	private Duration pollTimeout = Duration.ofMinutes(10);
	private ExecutorService ex = Executors.newSingleThreadExecutor();
	private volatile boolean running = true;
	private final KafkaConsumer<String, String> consumer;
	private final CountDownLatch latch = new CountDownLatch(1);

	private KafkaAdapter(String bootstrapServers, String topic, Class<T> cls) {
		this.topic = topic;
		this.cls = cls;
		consumer = init(bootstrapServers);
	}
	
	public void start() {
		ex.submit(this::collectMessages);
	}
	
	public void awaitAssignment() {
		LOG.info("Waiting for assignment");
		try {
		boolean assigned = latch.await(30, TimeUnit.MINUTES);
		if (!assigned) {
			LOG.info("Partition not assigned within 30 minutes.");	
		}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new RuntimeException(e);
		}
		LOG.info("Assigned");
	}

	public void setPollTimeout(Duration pollTimeout) {
		this.pollTimeout = pollTimeout;
	}

	private KafkaConsumer<String, String> init(String bootstrapServers) {
		String groupId = "kafka-test" + UUID.randomUUID();
		Properties props = new Properties();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");// "earliest");
		props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
		KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
		consumer.subscribe(Collections.singletonList(topic));
		return consumer;
	}

	private void collectMessages() {
		LOG.info("Assign to {} ms.", topic);
		try {
			long start = System.currentTimeMillis();
			boolean assigned = !consumer.assignment().isEmpty();
			while (running) {
				ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
				if (!assigned && !consumer.assignment().isEmpty()) {
					LOG.info("Partitions {} assigned after {} ms.", consumer.assignment(), System.currentTimeMillis() - start);
					assigned = true;
					latch.countDown();
				}
				for (ConsumerRecord<String, String> record : records) {
					this.deque.offer(record.value());
					consumer.commitSync();
				}
			}
		} finally {
			if (consumer != null) {
				consumer.close();
			}
		}
	}

	private String nextMessage(Duration duration) {
		try {
			LOG.info("Reading message");
			return deque.poll(duration.toMillis(), TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new RuntimeException(e);
		}
	}

	public T next() {
		String next = nextMessage(pollTimeout);
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
		LOG.info("Assert no additional message");
		String next = nextMessage(Duration.ofMillis(100));
		if (next != null) {
			throw new RuntimeException("Got an additional message within 100 millis: \n" + next);
		}
	}

	void close() {
		LOG.info("Disposing kafka consumer");
		running = false;
		ex.shutdown();
	}

	public void skipMessages() {
		LOG.info("Skipping messages");
		while (nextMessage(Duration.ofMillis(0)) != null);
	}

	public static <T> KafkaAdapter<T> newRunningAdapter(String bootstrapServers, String topic, Class<T> cls) {
		KafkaAdapter<T> adapter = new KafkaAdapter<>(bootstrapServers, topic, cls);
		adapter.start();
		adapter.awaitAssignment();
		return adapter;
	}

}
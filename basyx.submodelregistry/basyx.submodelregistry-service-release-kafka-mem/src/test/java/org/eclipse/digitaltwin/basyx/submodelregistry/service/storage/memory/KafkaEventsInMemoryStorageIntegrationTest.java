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
package org.eclipse.digitaltwin.basyx.submodelregistry.service.storage.memory;

import java.time.Duration;

import org.eclipse.digitaltwin.basyx.submodelregistry.service.tests.integration.BaseEventListener;
import org.eclipse.digitaltwin.basyx.submodelregistry.service.tests.integration.BaseIntegrationTest;
import org.junit.ClassRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.containers.wait.strategy.WaitStrategy;
import org.testcontainers.utility.DockerImageName;

@TestPropertySource(properties = { "registry.type=inMemory", "events.sink=kafka" })

public class KafkaEventsInMemoryStorageIntegrationTest extends BaseIntegrationTest {

	private static Logger logger = LoggerFactory.getLogger("KAFKA");
	private static Slf4jLogConsumer logConsumer = new Slf4jLogConsumer(logger);

	@ClassRule
	public static KafkaContainer KAFKA = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:6.2.1")) {
		protected void waitUntilContainerStarted() {
			super.waitUntilContainerStarted();
			WaitStrategy strategy = Wait.forLogMessage(".*Kafka startTimeMs.*", 1);
			strategy.withStartupTimeout(Duration.ofMinutes(10));
			strategy.waitUntilReady(this);
		}
	}.withLogConsumer(logConsumer);
			
			
	@DynamicPropertySource
	static void assignAdditionalProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.kafka.bootstrap-servers", KAFKA::getBootstrapServers);
	}

	@Component
	public static class KafkaEventListener extends BaseEventListener {

		@KafkaListener(topics = "submodel-registry", groupId = "test")
		public void receive(String message) {
			super.offer(message);
		}
	}
}
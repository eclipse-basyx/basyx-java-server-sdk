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
package org.eclipse.digitaltwin.basyx.aasrepository.feature.kafka.events;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.eclipse.digitaltwin.aas4j.v3.dataformat.core.SerializationException;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.json.JsonSerializer;
import org.eclipse.digitaltwin.basyx.aasrepository.feature.kafka.events.model.AasEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;

/**
 * @author geso02 (Sonnenberg DFKI GmbH)
 */
public class KafkaAasEventDistributer implements AasEventDistributer {

	private static Logger LOGGER = LoggerFactory.getLogger(KafkaAasEventDistributer.class);

	private final JsonSerializer serializer;
	private final KafkaTemplate<String, String> template;
	private String topicName;

	public KafkaAasEventDistributer(JsonSerializer serializer, KafkaTemplate<String, String> template, String topicName) {
		this.serializer = serializer;
		this.template = template;
		this.topicName = topicName;
	}

	@Override
	public void distribute(AasEvent evt) {
		try {
			String payload = serializer.write(evt);
			LOGGER.debug("Send kafka message to " + topicName + ".");
			template.send(topicName, evt.getId(), payload).get(3, TimeUnit.SECONDS);
		} catch (InterruptedException | ExecutionException | TimeoutException | SerializationException e) {
			throw new RuntimeException(e);
		}
	}

}

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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class KafkaAdapters {

	private static final Map<String, KafkaAdapter<?>> adapters = new ConcurrentHashMap<>();

	private KafkaAdapters() {

		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				KafkaAdapters.close();
			}
		});
	}

	private static void close() {

		for (KafkaAdapter<?> eachAdapter : adapters.values()) {
			eachAdapter.close();
		}

	}

	@SuppressWarnings("unchecked")
	public static <T> KafkaAdapter<T> getAdapter(String topicName, Class<T> cls) {
		return (KafkaAdapter<T>) adapters.computeIfAbsent(topicName, t -> KafkaAdapter.newRunningAdapter("localhost:9092", t, cls));

	}

}

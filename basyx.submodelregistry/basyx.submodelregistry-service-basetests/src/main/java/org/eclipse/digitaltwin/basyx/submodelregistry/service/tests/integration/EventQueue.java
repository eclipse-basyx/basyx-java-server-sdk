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
package org.eclipse.digitaltwin.basyx.submodelregistry.service.tests.integration;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.eclipse.digitaltwin.basyx.submodelregistry.service.events.RegistryEvent;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EventQueue {

	private LinkedBlockingQueue<String> messageQueue = new LinkedBlockingQueue<>();

	private final ObjectMapper mapper;

	public boolean offer(String message) {
		return messageQueue.offer(message);
	}

	public void reset() {
		try {
			while (messageQueue.poll(1, TimeUnit.SECONDS) != null)
				;
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new EventListenerException(e);
		}
	}

	public void noAdditionalMessage() throws InterruptedException {
		while(messageQueue.poll(100, TimeUnit.MILLISECONDS) != null);
	}

	public RegistryEvent poll() {
		try {
			String message = messageQueue.poll(1, TimeUnit.MINUTES);
			if (message == null) {
				throw new EventListenerException("timeout");
			}
			return mapper.readValue(message, RegistryEvent.class);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new EventListenerException(e);
		} catch (JsonProcessingException e) {
			throw new EventListenerException(e);
		}
	}
	
	public static final class EventListenerException extends RuntimeException {

		private static final long serialVersionUID = 1L;
		
		public EventListenerException(Throwable e) {
			super(e);
		}

		public EventListenerException(String msg) {
			super(msg);
		}
	}
}

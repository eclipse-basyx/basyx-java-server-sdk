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

package org.eclipse.digitaltwin.basyx.aasenvironment.client.resolvers;

import java.net.HttpURLConnection;
import java.net.URI;
import java.util.List;
import java.util.Optional;

import org.eclipse.digitaltwin.basyx.aasenvironment.client.exceptions.NoValidEndpointFoundException;
import org.eclipse.digitaltwin.basyx.aasenvironment.client.resolvers.parsers.URIParser;

/**
 * Resolves a list of endpoints based on find first working strategy
 *
 * @author mateusmolina
 *
 */
public class EndpointResolver<T> {
	
	private final URIParser<T> parser;

	private int timeout = 3000;

	public EndpointResolver(URIParser<T> parser) {
		this.parser = parser;
	}

	public EndpointResolver(URIParser<T> parser, int timeout) {
		this(parser);
		this.timeout = timeout;
	}

	public String resolveFirst(List<T> endpoints) {
		List<URI> uris = endpoints.stream().map(parser::parse).flatMap(Optional::stream).toList();
		return findFirstWorkingURI(uris).orElseThrow(() -> new NoValidEndpointFoundException(endpoints.toString())).toString();
	}

	public List<String> resolveAll(List<T> endpoints) {
		return endpoints.stream().map(parser::parse).flatMap(Optional::stream).filter(this::isURIWorking).map(URI::toString).toList();
	}

	private Optional<URI> findFirstWorkingURI(List<URI> uris) {
		return uris.stream().filter(this::isURIWorking).findFirst();
	}

	private boolean isURIWorking(URI uri) {
		HttpURLConnection connection = null;
		try {
			connection = (HttpURLConnection) uri.toURL().openConnection();
			connection.setRequestMethod("HEAD");
			connection.setConnectTimeout(timeout);
			connection.setReadTimeout(timeout);
			int responseCode = connection.getResponseCode();
			return (200 <= responseCode && responseCode <= 399);
		} catch (Exception e) {
			return false;
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
	}
}
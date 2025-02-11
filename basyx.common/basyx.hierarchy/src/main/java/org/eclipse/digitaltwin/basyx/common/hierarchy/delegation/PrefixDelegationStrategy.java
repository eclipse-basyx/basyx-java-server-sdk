/*******************************************************************************
 * Copyright (C) 2025 the Eclipse BaSyx Authors
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

package org.eclipse.digitaltwin.basyx.common.hierarchy.delegation;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;

import org.eclipse.digitaltwin.basyx.common.hierarchy.CommonHierarchyProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Generates delegation URL based on value of the prefix attribute. Default
 * value is 'registry'.
 *
 * @author mateusmolina
 *
 */
@Component
public class PrefixDelegationStrategy implements DelegationStrategy {
	private final String prefix;

	public PrefixDelegationStrategy(@Value("${" + CommonHierarchyProperties.HIERARCHY_FEATURE_DELEGATION_PREFIX + ":registry}") String prefix) {
		this.prefix = prefix;
	}

	@Override
	public Optional<String> buildDelegatedRegistryUrl(String identifier) {
		return Optional.ofNullable(identifier).map(this::extractDelegationUrl);
	}

	private String extractDelegationUrl(String identifier) {
		URL url;

		try {
			url = new URL(identifier);
		} catch (MalformedURLException e) {
			return null;
		}

		String host = url.getHost();
		int port = url.getPort();
		String protocol = url.getProtocol();

		StringBuilder registryUrl = new StringBuilder();

		registryUrl.append(protocol).append("://");

		if (prefix != null && !prefix.isBlank())
			registryUrl.append(prefix).append(".");

		registryUrl.append(host);

		if (port != -1) {
			registryUrl.append(":").append(port);
		}

		return registryUrl.toString();
	}
}
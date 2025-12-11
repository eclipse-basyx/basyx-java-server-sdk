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

package org.eclipse.digitaltwin.basyx.aasregistry.feature.discovery.integration;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.digitaltwin.basyx.aasdiscoveryservice.core.AasDiscoveryService;
import org.eclipse.digitaltwin.basyx.aasregistry.service.storage.AasRegistryStorage;
import org.eclipse.digitaltwin.basyx.aasregistry.service.storage.AasRegistryStorageFeature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@ConditionalOnProperty(name = "basyx.aasregistry.feature.discoveryintegration.enabled", havingValue = "true", matchIfMissing = false)
public class DiscoveryIntegrationAasRegistryFeature implements AasRegistryStorageFeature {

	@Value("${basyx.aasregistry.feature.discoveryintegration.baseUrl:#{null}}")
	private String discoveryBaseURL;

	private final AasDiscoveryService discoveryApi;
	private final ApplicationContext applicationContext;

	@Autowired
	public DiscoveryIntegrationAasRegistryFeature(AasDiscoveryService discoveryApi, ApplicationContext applicationContext) {
		this.discoveryApi = discoveryApi;
		this.applicationContext = applicationContext;
	}

	@Override
	public AasRegistryStorage decorate(AasRegistryStorage decorated) {
		if (decorated instanceof DiscoveryIntegrationAasRegistry) {
			return decorated;
		}
		if (containsDiscoveryIntegrationInChain(decorated)) {
			return decorated;
		}
		return new DiscoveryIntegrationAasRegistry(discoveryApi, decorated);
	}

	private boolean containsDiscoveryIntegrationInChain(AasRegistryStorage storage) {
		AasRegistryStorage current = storage;
		while (current != null) {
			if (current instanceof DiscoveryIntegrationAasRegistry) {
				return true;
			}

			try {
				java.lang.reflect.Field decoratedField = current.getClass().getDeclaredField("decorated");
				decoratedField.setAccessible(true);
				current = (AasRegistryStorage) decoratedField.get(current);
			} catch (Exception e) {
				break;
			}
		}
		return false;
	}

	@Override
	public boolean isEnabled() {
		return discoveryBaseURL != null && !discoveryBaseURL.isBlank();
	}

	@Override
	public String getName() {
		return "AasRegistry Discovery Integration";
	}
}
/*******************************************************************************
 * Copyright (C) 2023 the Eclipse BaSyx Authors
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

package org.eclipse.digitaltwin.basyx.digitaltwinregistry.component;

import org.eclipse.digitaltwin.basyx.aasdiscoveryservice.backend.CrudAasDiscovery;
import org.eclipse.digitaltwin.basyx.aasdiscoveryservice.backend.inmemory.AasDiscoveryInMemoryBackendProvider;
import org.eclipse.digitaltwin.basyx.aasdiscoveryservice.core.AasDiscoveryService;
import org.eclipse.digitaltwin.basyx.aasdiscoveryservice.http.LookupApiController;
import org.eclipse.digitaltwin.basyx.aasregistry.service.api.BasyxDescriptionApiDelegate;
import org.eclipse.digitaltwin.basyx.aasregistry.service.api.BasyxRegistryApiDelegate;
import org.eclipse.digitaltwin.basyx.aasregistry.service.api.BasyxSearchApiDelegate;
import org.eclipse.digitaltwin.basyx.aasregistry.service.api.DescriptionApiDelegate;
import org.eclipse.digitaltwin.basyx.aasregistry.service.api.LocationBuilder;
import org.eclipse.digitaltwin.basyx.aasregistry.service.api.SearchApiDelegate;
import org.eclipse.digitaltwin.basyx.aasregistry.service.api.ShellDescriptorsApi;
import org.eclipse.digitaltwin.basyx.aasregistry.service.api.ShellDescriptorsApiController;
import org.eclipse.digitaltwin.basyx.aasregistry.service.api.ShellDescriptorsApiDelegate;
import org.eclipse.digitaltwin.basyx.aasregistry.service.events.RegistryEventLogSink;
import org.eclipse.digitaltwin.basyx.aasregistry.service.events.RegistryEventSink;
import org.eclipse.digitaltwin.basyx.aasregistry.service.storage.AasRegistryStorage;
import org.eclipse.digitaltwin.basyx.aasregistry.service.storage.memory.InMemoryAasRegistryStorage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Configuration for aas environment for dependency injection
 * 
 * @author zhangzai, mateusmolina
 *
 */
@Configuration
public class AasEnvironmentConfiguration {

	@Bean
	@Primary
	public static RegistryEventSink getAasEnvironment() {
		return new RegistryEventLogSink();
	}
	
	@Bean
	@Primary
	public static DescriptionApiDelegate getDescriptionApiDelegate() {
		return new BasyxDescriptionApiDelegate();
	}
	
	@Bean
	@Primary
	public static SearchApiDelegate getSearchApiDelegate(AasRegistryStorage storage, RegistryEventSink eventSink) {
		return new BasyxSearchApiDelegate(storage, eventSink);
	}
	
	@Bean
	@Primary
	public static AasRegistryStorage getAasRegistryStorage() {
		return new InMemoryAasRegistryStorage();
	}
	
	@Bean
	@Primary
	public static LocationBuilder getLocationBuilder() {
		return new DefaultLocationBuilder();
	}
	
	@Bean
	@Primary
	public static ShellDescriptorsApiDelegate getShellDescriptorsApiDelegate(AasRegistryStorage storage, RegistryEventSink eventSink, LocationBuilder builder) {
		return new BasyxRegistryApiDelegate(storage, eventSink, builder);
	}
	
	@Bean
	@Primary
	public static ShellDescriptorsApi getShellDescriptorsApi(ShellDescriptorsApiDelegate shellDescriptorsApiDelegate) {
		return new ShellDescriptorsApiController(shellDescriptorsApiDelegate);
	}
	
	@Bean
	@Primary
	public static LookupApiController getLookupApiController(AasDiscoveryService aasDiscoveryService, ObjectMapper mapper) {
		return new LookupApiController(aasDiscoveryService, mapper);
	}
	
	@Bean
	@Primary
	public static AasDiscoveryService getAasDiscoveryService() {
		return new CrudAasDiscovery(new AasDiscoveryInMemoryBackendProvider());
	}

}

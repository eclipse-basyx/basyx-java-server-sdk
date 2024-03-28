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
package org.eclipse.digitaltwin.basyx.aasregistry.service.configuration;

import java.util.List;

import org.eclipse.digitaltwin.basyx.aasregistry.service.storage.AasRegistryStorage;
import org.eclipse.digitaltwin.basyx.aasregistry.service.storage.AasRegistryStorageFeature;
import org.eclipse.digitaltwin.basyx.aasregistry.service.storage.CursorEncodingRegistryStorage;
import org.eclipse.digitaltwin.basyx.aasregistry.service.storage.memory.InMemoryAasRegistryStorage;
import org.eclipse.digitaltwin.basyx.aasregistry.service.storage.memory.ThreadSafeAasRegistryStorageDecorator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.log4j.Log4j2;

@Configuration
@Log4j2
public class InMemoryAasStorageConfiguration {

	
	
	@Bean
	@ConditionalOnProperty(prefix = "registry", name = "type", havingValue = "inMemory")
	public AasRegistryStorage storage(List<AasRegistryStorageFeature> features) {
		log.info("Creating in-memory storage");
		AasRegistryStorage storage = new ThreadSafeAasRegistryStorageDecorator(new CursorEncodingRegistryStorage(new InMemoryAasRegistryStorage()));
		return applyFeatures(storage, features);
	}

	private AasRegistryStorage applyFeatures(AasRegistryStorage storage, List<AasRegistryStorageFeature> features) {
		for (AasRegistryStorageFeature eachFeature : features) {
			log.info("Activating feature " + eachFeature.getName());
			storage = eachFeature.decorate(storage);
		}
		return storage;
	}
}
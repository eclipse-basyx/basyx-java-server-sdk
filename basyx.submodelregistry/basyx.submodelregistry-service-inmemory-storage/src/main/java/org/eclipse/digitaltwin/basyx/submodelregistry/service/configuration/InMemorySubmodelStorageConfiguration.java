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
package org.eclipse.digitaltwin.basyx.submodelregistry.service.configuration;

import lombok.extern.log4j.Log4j2;
import org.eclipse.digitaltwin.basyx.submodelregistry.service.storage.CursorEncodingRegistryStorage;
import org.eclipse.digitaltwin.basyx.submodelregistry.service.storage.SubmodelRegistryStorage;
import org.eclipse.digitaltwin.basyx.submodelregistry.service.storage.SubmodelRegistryStorageFeature;
import org.eclipse.digitaltwin.basyx.submodelregistry.service.storage.memory.InMemorySubmodelRegistryStorage;
import org.eclipse.digitaltwin.basyx.submodelregistry.service.storage.memory.ThreadSafeSubmodelRegistryStorageDecorator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@Log4j2
public class InMemorySubmodelStorageConfiguration {

	@Bean
	@ConditionalOnProperty(prefix = "registry", name = "type", havingValue = "inMemory")
	public SubmodelRegistryStorage storage(List<SubmodelRegistryStorageFeature> features) {
		log.info("Creating in-memory storage");

		SubmodelRegistryStorage storage = new ThreadSafeSubmodelRegistryStorageDecorator(new CursorEncodingRegistryStorage(new InMemorySubmodelRegistryStorage()));

		return applyFeatures(storage, features);
	}

	private SubmodelRegistryStorage applyFeatures(SubmodelRegistryStorage storage, List<SubmodelRegistryStorageFeature> features) {
		for (SubmodelRegistryStorageFeature eachFeature : features) {
			log.info("Activating feature " + eachFeature.getName());
			storage = eachFeature.decorate(storage);
		}

		return storage;
	}

}

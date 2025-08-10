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
package org.eclipse.digitaltwin.basyx.aasregistry.service.events;


import jakarta.validation.Valid;
import org.eclipse.digitaltwin.aas4j.v3.model.SpecificAssetId;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSpecificAssetId;
import org.eclipse.digitaltwin.basyx.aasdiscoveryservice.backend.mongodb.backend.MongoDBCrudAasDiscovery;


import org.eclipse.digitaltwin.basyx.aasregistry.model.AssetAdministrationShellDescriptor;
import org.slf4j.Marker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.log4j.Log4j2;

import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Component("RegistryEventLogSink")
@ConditionalOnProperty(prefix = "events", name = "sink", havingValue = "log")
public class RegistryEventLogSink implements RegistryEventSink {



	@Autowired MongoDBCrudAasDiscovery mongoDBCrudAasDiscovery;



	@Autowired
	@Qualifier("mappingJackson2HttpMessageConverter")
	private MappingJackson2HttpMessageConverter converter;



	@Override
	public void consumeEvent(RegistryEvent evt) {
		try {
			ObjectMapper objectMapper = converter.getObjectMapper();
			String msg = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(evt);
			String encodedId = Base64.getEncoder().encodeToString(evt.getId().getBytes());

			if(evt.getType().equals(RegistryEvent.EventType.AAS_REGISTERED)) {
				@Valid List<org.eclipse.digitaltwin.basyx.aasregistry.model.@Valid SpecificAssetId> ids = evt.getAasDescriptor().getSpecificAssetIds();

				List<SpecificAssetId> specificAssetIds = ids.stream()
						.map(rId -> {
							SpecificAssetId assetId = new DefaultSpecificAssetId();
							assetId.setName(rId.getName());
							assetId.setValue(rId.getValue());
							return assetId;
						}).collect(Collectors.toList());

				mongoDBCrudAasDiscovery.createAllAssetLinksById(encodedId, specificAssetIds);
			} else if (evt.getType().equals(RegistryEvent.EventType.AAS_UNREGISTERED)) mongoDBCrudAasDiscovery.deleteAllAssetLinksById(encodedId);
			log.debug("Event sent -> " + msg);
		} catch (JsonProcessingException e) {
			log.error(Marker.ANY_MARKER, "Failed to process json ", e);
		}
	}
}
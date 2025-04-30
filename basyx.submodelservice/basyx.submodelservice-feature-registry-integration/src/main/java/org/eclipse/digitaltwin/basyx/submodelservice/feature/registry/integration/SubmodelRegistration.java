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
package org.eclipse.digitaltwin.basyx.submodelservice.feature.registry.integration;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.basyx.core.exceptions.AccessTokenRetrievalException;
import org.eclipse.digitaltwin.basyx.core.exceptions.RepositoryRegistryUnlinkException;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.ApiException;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.api.SubmodelRegistryApi;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.factory.SubmodelServiceDescriptorFactory;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.mapper.AttributeMapper;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.model.SubmodelDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Service;

import jakarta.annotation.PreDestroy;
/**
 * Used for direct submodel registration
 * 
 * @author Gerhard Sonnenberg (DFKI GmbH)
 */
@Service
@ConditionalOnExpression("!T(org.springframework.util.StringUtils).isEmpty('${basyx.submodelservice.feature.registryintegration:}') && !T(org.springframework.util.StringUtils).isEmpty('${basyx.externalurl:}')")
public class SubmodelRegistration {

	private static Logger logger = LoggerFactory.getLogger(SubmodelRegistration.class);

	private final List<String> toUnregister = new CopyOnWriteArrayList<>();

	@Autowired
	private SubmodelServiceRegistryLink registryLink;

	@Autowired
	private AttributeMapper attributeMapper;

	public void register(Submodel submodel) {
		List<String> baseUrls = registryLink.getSubmodelServiceBaseURLs();
		SubmodelDescriptor descriptor = new SubmodelServiceDescriptorFactory(baseUrls, attributeMapper).create(submodel);
		SubmodelRegistryApi registryApi = registryLink.getRegistryApi();
		try {
			registryApi.postSubmodelDescriptor(descriptor);
			logger.info("Submodel '{}' has been automatically linked with the Registry", descriptor.getId());
			toUnregister.add(descriptor.getId());
		} catch (ApiException | AccessTokenRetrievalException e) {
			logger.warn("Submodel '" + descriptor.getId() + "' could not be registered with the Registry", e);
		}
	}

	private void unregisterSubmodel(String submodelId) {
		SubmodelRegistryApi registryApi = registryLink.getRegistryApi();
		try {
			registryApi.deleteSubmodelDescriptorById(submodelId);
			logger.info("Submodel '{}' has been automatically un-linked from the Registry.", submodelId);
		} catch (ApiException e) {
			if (e.getCode() == 404) {
				logger.error("Unable to un-link the Submodel descriptor '{}' from the Registry because it does not exist on the Registry.", submodelId);
			} else {
				throw new RepositoryRegistryUnlinkException(submodelId, e);
			}
		}
	}

	@PreDestroy
	public void onDestroy() {
		for (String smId : toUnregister) {
			unregisterSubmodel(smId);
		}
	}
}

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

package org.eclipse.digitaltwin.basyx.aasregistry.feature.hierarchy;

import java.util.List;

import org.eclipse.digitaltwin.basyx.aasregistry.client.ApiException;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.GetSubmodelDescriptorsResult;
import org.eclipse.digitaltwin.basyx.aasregistry.model.AssetAdministrationShellDescriptor;
import org.eclipse.digitaltwin.basyx.aasregistry.model.SubmodelDescriptor;
import org.eclipse.digitaltwin.basyx.aasregistry.service.errors.AasDescriptorNotFoundException;
import org.eclipse.digitaltwin.basyx.aasregistry.service.errors.SubmodelNotFoundException;
import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Handles internal model mapping for {@link HierarchicalAasRegistryStorage}
 *
 * @author mateusmolina
 *
 */
final class AasRegistryModelMapper {

	private static final ObjectMapper objectMapper = new ObjectMapper();

	private AasRegistryModelMapper() {
	}

	static org.eclipse.digitaltwin.basyx.aasregistry.client.model.AssetAdministrationShellDescriptor mapEqModel(AssetAdministrationShellDescriptor aasRegistryDescriptor) {
			return objectMapper.convertValue(aasRegistryDescriptor, org.eclipse.digitaltwin.basyx.aasregistry.client.model.AssetAdministrationShellDescriptor.class);
	}

	static AssetAdministrationShellDescriptor mapEqModel(org.eclipse.digitaltwin.basyx.aasregistry.client.model.AssetAdministrationShellDescriptor aasRegistryDescriptor) {
			return objectMapper.convertValue(aasRegistryDescriptor, AssetAdministrationShellDescriptor.class);
	}

	static org.eclipse.digitaltwin.basyx.aasregistry.client.model.SubmodelDescriptor mapEqModel(SubmodelDescriptor smRegistryDescriptor) {
			return objectMapper.convertValue(smRegistryDescriptor, org.eclipse.digitaltwin.basyx.aasregistry.client.model.SubmodelDescriptor.class);
	}

	static SubmodelDescriptor mapEqModel(org.eclipse.digitaltwin.basyx.aasregistry.client.model.SubmodelDescriptor smRegistryDescriptor) {
			return objectMapper.convertValue(smRegistryDescriptor, SubmodelDescriptor.class);
	}

	static CursorResult<List<SubmodelDescriptor>> mapEqModel(GetSubmodelDescriptorsResult descriptorResult) {
		List<SubmodelDescriptor> submodelDescs = objectMapper.convertValue(descriptorResult.getResult(), new TypeReference<List<SubmodelDescriptor>>() {
		});
		return new CursorResult<>(descriptorResult.getPagingMetadata().getCursor(), submodelDescs);
	}

	static RuntimeException mapApiException(ApiException e, String aasDescriptorId) {
		if (HttpStatusCode.valueOf(e.getCode()).equals(HttpStatus.NOT_FOUND))
			return new AasDescriptorNotFoundException(aasDescriptorId);

		return new RuntimeException(e);
	}

	static RuntimeException mapApiException(ApiException e, String aasDescriptorId, String smId) {
		if (HttpStatusCode.valueOf(e.getCode()).equals(HttpStatus.NOT_FOUND) && checkIfSubmodelNotFound(e, aasDescriptorId, smId))
			return new SubmodelNotFoundException(aasDescriptorId, smId);

		return mapApiException(e, aasDescriptorId);
	}

	private static boolean checkIfSubmodelNotFound(ApiException e, String aasDescriptorId, String smId) {
		SubmodelNotFoundException expectedException = new SubmodelNotFoundException(aasDescriptorId, smId);
		return e.getMessage().contains(expectedException.getReason());
	}

}

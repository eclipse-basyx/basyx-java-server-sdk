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

package org.eclipse.digitaltwin.basyx.submodelregistry.feature.hierarchy;

import java.util.List;

import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.ApiException;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.model.GetSubmodelDescriptorsResult;
import org.eclipse.digitaltwin.basyx.submodelregistry.model.SubmodelDescriptor;
import org.eclipse.digitaltwin.basyx.submodelregistry.service.errors.SubmodelNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Handles internal model mapping for
 * {@link HierarchicalsubmodelRegistryStorage}
 *
 * @author mateusmolina
 *
 */
final class SubmodelRegistryModelMapper {

	private final ObjectMapper objectMapper;

	SubmodelRegistryModelMapper(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	SubmodelDescriptor mapEqModel(org.eclipse.digitaltwin.basyx.submodelregistry.client.model.SubmodelDescriptor submodelRegistryDescriptor) {
		return objectMapper.convertValue(submodelRegistryDescriptor, SubmodelDescriptor.class);
	}

	org.eclipse.digitaltwin.basyx.submodelregistry.client.model.SubmodelDescriptor mapEqModel(SubmodelDescriptor smRegistryDescriptor) {
		return objectMapper.convertValue(smRegistryDescriptor, org.eclipse.digitaltwin.basyx.submodelregistry.client.model.SubmodelDescriptor.class);
	}

	CursorResult<List<SubmodelDescriptor>> mapEqModel(GetSubmodelDescriptorsResult descriptorResult) {
		List<SubmodelDescriptor> submodelDescs = objectMapper.convertValue(descriptorResult.getResult(), new TypeReference<List<SubmodelDescriptor>>() {
		});
		return new CursorResult<>(descriptorResult.getPagingMetadata().getCursor(), submodelDescs);
	}

	static RuntimeException mapApiException(ApiException e, String submodelDescriptorId) {
		if (HttpStatusCode.valueOf(e.getCode()).equals(HttpStatus.NOT_FOUND))
			return new SubmodelNotFoundException(submodelDescriptorId);

		return new RuntimeException(e);
	}

}

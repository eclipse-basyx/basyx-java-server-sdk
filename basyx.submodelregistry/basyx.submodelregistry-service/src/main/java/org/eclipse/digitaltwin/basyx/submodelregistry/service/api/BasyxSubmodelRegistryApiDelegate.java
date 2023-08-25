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
package org.eclipse.digitaltwin.basyx.submodelregistry.service.api;

import java.net.URI;
import java.util.List;

import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.eclipse.digitaltwin.basyx.submodelregistry.model.GetSubmodelDescriptorsResult;
import org.eclipse.digitaltwin.basyx.submodelregistry.model.PagedResultPagingMetadata;
import org.eclipse.digitaltwin.basyx.submodelregistry.model.SubmodelDescriptor;
import org.eclipse.digitaltwin.basyx.submodelregistry.service.events.RegistryEventSink;
import org.eclipse.digitaltwin.basyx.submodelregistry.service.storage.RegistrationEventSendingSubmodelRegistryStorage;
import org.eclipse.digitaltwin.basyx.submodelregistry.service.storage.SubmodelRegistryStorage;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class BasyxSubmodelRegistryApiDelegate implements SubmodelDescriptorsApiDelegate {

	private final SubmodelRegistryStorage storage;

	private final LocationBuilder locationBuilder;

	public BasyxSubmodelRegistryApiDelegate(SubmodelRegistryStorage storage, RegistryEventSink eventSink, LocationBuilder locationBuilder) {
		this.storage = new RegistrationEventSendingSubmodelRegistryStorage(storage, eventSink);
		this.locationBuilder = locationBuilder;
	}

	@Override
	public ResponseEntity<Void> deleteAllSubmodelDescriptors() {
		storage.clear();
		return ResponseEntity.noContent().build();
	}

	@Override
	public ResponseEntity<SubmodelDescriptor> getSubmodelDescriptorById(String submodelIdentifier) {
		SubmodelDescriptor submodelDescriptor = storage.getSubmodelDescriptor(submodelIdentifier);
		return ResponseEntity.ok(submodelDescriptor);
	}

	@Override
	public ResponseEntity<Void> deleteSubmodelDescriptorById(String submodelIdentifier) {
		storage.removeSubmodelDescriptor(submodelIdentifier);
		return ResponseEntity.noContent().build();
	}

	@Override
	public ResponseEntity<SubmodelDescriptor> postSubmodelDescriptor(SubmodelDescriptor submodelDescriptor) {
		storage.insertSubmodelDescriptor(submodelDescriptor);
		URI location = locationBuilder.getSubmodelLocation(submodelDescriptor.getId());		
		return ResponseEntity.created(location).body(submodelDescriptor);
	}

	@Override
	public ResponseEntity<Void> putSubmodelDescriptorById(String submodelIdentifier, SubmodelDescriptor submodelDescriptor) {
		storage.replaceSubmodelDescriptor(submodelIdentifier, submodelDescriptor);
		return ResponseEntity.noContent().build();
	}

	@Override
	public ResponseEntity<GetSubmodelDescriptorsResult> getAllSubmodelDescriptors(Integer limit, String cursor) {
		PaginationInfo pInfo = new PaginationInfo(limit, cursor);
		CursorResult<List<SubmodelDescriptor>> cResult = storage.getAllSubmodelDescriptors(pInfo);
		GetSubmodelDescriptorsResult gsdResult = new GetSubmodelDescriptorsResult();
		gsdResult.setPagingMetadata(new PagedResultPagingMetadata().cursor(cResult.getCursor()));
		gsdResult.setResult(cResult.getResult());
		return ResponseEntity.ok(gsdResult);
	}
}
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
package org.eclipse.digitaltwin.basyx.submodelregistry.service.storage;

import java.util.Set;

import org.eclipse.digitaltwin.basyx.submodelregistry.model.SubmodelDescriptor;
import org.eclipse.digitaltwin.basyx.submodelregistry.service.errors.SubmodelAlreadyExistsException;
import org.eclipse.digitaltwin.basyx.submodelregistry.service.errors.SubmodelNotFoundException;
import org.eclipse.digitaltwin.basyx.submodelregistry.service.events.RegistryEvent;
import org.eclipse.digitaltwin.basyx.submodelregistry.service.events.RegistryEventSink;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;


public class RegistrationEventSendingSubmodelRegistryStorage extends SubmodelRegistryStorageDecorator {

	@NonNull
	private final RegistryEventSink eventSink;
	
	public RegistrationEventSendingSubmodelRegistryStorage(SubmodelRegistryStorage storage, RegistryEventSink sink) {
		super(storage);
		this.eventSink = sink;
	}

	@Override
	public void insertSubmodelDescriptor(SubmodelDescriptor descr) throws SubmodelAlreadyExistsException {
		storage.insertSubmodelDescriptor(descr);
		submodelRegistered(descr);
	}
	
	@Override
	public void removeSubmodelDescriptor(String submodelId) throws SubmodelNotFoundException {
		storage.removeSubmodelDescriptor(submodelId);
		submodelUnregistered(submodelId);
	}

	@Override
	public void replaceSubmodelDescriptor(String submodelId, SubmodelDescriptor descr) throws SubmodelNotFoundException {
		storage.replaceSubmodelDescriptor(submodelId, descr);
		if (!submodelId.equals(descr.getId())) {
			submodelUnregistered(submodelId);
		}
		submodelRegistered(descr);
	}

	
	@Override
	public Set<String> clear() {
		Set<String> removed = storage.clear();
		removed.forEach(this::submodelUnregistered);
		return removed;
	}

	private void submodelRegistered(SubmodelDescriptor submodel) {
		RegistryEvent evt = RegistryEvent.builder().id(submodel.getId()).type(RegistryEvent.EventType.SUBMODEL_REGISTERED).submodelDescriptor(submodel).build();
		eventSink.consumeEvent(evt);
	}

	private void submodelUnregistered(@NonNull String submodelId) {
		RegistryEvent evt = RegistryEvent.builder().id(submodelId).type(RegistryEvent.EventType.SUBMODEL_UNREGISTERED).build();
		eventSink.consumeEvent(evt);
	}
}
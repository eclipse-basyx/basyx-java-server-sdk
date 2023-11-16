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
package org.eclipse.digitaltwin.basyx.aasregistry.service.storage;

import jakarta.validation.Valid;
import java.util.Set;
import lombok.NonNull;
import org.eclipse.digitaltwin.basyx.aasregistry.model.AssetAdministrationShellDescriptor;
import org.eclipse.digitaltwin.basyx.aasregistry.model.SubmodelDescriptor;
import org.eclipse.digitaltwin.basyx.aasregistry.service.errors.AasDescriptorAlreadyExistsException;
import org.eclipse.digitaltwin.basyx.aasregistry.service.errors.AasDescriptorNotFoundException;
import org.eclipse.digitaltwin.basyx.aasregistry.service.errors.SubmodelNotFoundException;
import org.eclipse.digitaltwin.basyx.aasregistry.service.events.RegistryEvent;
import org.eclipse.digitaltwin.basyx.aasregistry.service.events.RegistryEvent.EventType;
import org.eclipse.digitaltwin.basyx.aasregistry.service.events.RegistryEventSink;

public class RegistrationEventSendingAasRegistryStorage extends AasRegistryStorageDelegate {

	@NonNull
	private final RegistryEventSink eventSink;

	public RegistrationEventSendingAasRegistryStorage(AasRegistryStorage storage, RegistryEventSink eventSink) {
		super(storage);
		this.eventSink = eventSink;
	}

	@Override
	public void replaceAasDescriptor(@NonNull String aasDescriptorId, @NonNull AssetAdministrationShellDescriptor descriptor) throws AasDescriptorNotFoundException {
		storage.replaceAasDescriptor(aasDescriptorId, descriptor);
		if (!aasDescriptorId.equals(descriptor.getId())) {
			aasDescriptorUnregistered(aasDescriptorId);
		}
		aasDescriptorRegistered(descriptor);
	}

	@Override
	public void insertAasDescriptor(@Valid AssetAdministrationShellDescriptor descr) throws AasDescriptorAlreadyExistsException {
		storage.insertAasDescriptor(descr);
		aasDescriptorRegistered(descr);
	}

	@Override
	public void removeAasDescriptor(@NonNull String aasDescriptorId) throws AasDescriptorNotFoundException {
		storage.removeAasDescriptor(aasDescriptorId);
		aasDescriptorUnregistered(aasDescriptorId);
	}

	@Override
	public void replaceSubmodel(@NonNull String aasDescriptorId, @NonNull String submodelId, @NonNull SubmodelDescriptor submodel) throws AasDescriptorNotFoundException, SubmodelNotFoundException {
		storage.replaceSubmodel(aasDescriptorId, submodelId, submodel);
		if (!submodelId.equals(submodel.getId())) {
			submodelUnregistered(aasDescriptorId, submodelId);
		}
		submodelRegistered(aasDescriptorId, submodel);
	}

	@Override
	public void insertSubmodel(@NonNull String aasDescriptorId, @NonNull SubmodelDescriptor submodel) {
		storage.insertSubmodel(aasDescriptorId, submodel);
		// always update for now, even if it was an override with the same value
		RegistryEvent evt = RegistryEvent.builder().id(aasDescriptorId).submodelId(submodel.getId()).type(EventType.SUBMODEL_REGISTERED).submodelDescriptor(submodel).build();
		eventSink.consumeEvent(evt);
	}

	@Override
	public void removeSubmodel(@NonNull String aasDescrId, @NonNull String submodelId) {
		storage.removeSubmodel(aasDescrId, submodelId);
		submodelUnregistered(aasDescrId, submodelId);
	}

	@Override
	public Set<String> clear() {
		Set<String> unregistredDescriptors = storage.clear();
		for (String eachId : unregistredDescriptors) {
			aasDescriptorUnregistered(eachId);
		}
		return unregistredDescriptors;
	}

	private void aasDescriptorRegistered(@NonNull AssetAdministrationShellDescriptor descriptor) {
		RegistryEvent evt = RegistryEvent.builder().id(descriptor.getId()).type(RegistryEvent.EventType.AAS_REGISTERED).aasDescriptor(descriptor).build();
		eventSink.consumeEvent(evt);
	}

	private void aasDescriptorUnregistered(String aasDescriptorId) {
		RegistryEvent evt = RegistryEvent.builder().id(aasDescriptorId).type(RegistryEvent.EventType.AAS_UNREGISTERED).build();
		eventSink.consumeEvent(evt);
	}

	private void submodelRegistered(@NonNull String aasDescriptorId, @NonNull SubmodelDescriptor submodel) {
		RegistryEvent evt = RegistryEvent.builder().id(aasDescriptorId).submodelId(submodel.getId()).type(RegistryEvent.EventType.SUBMODEL_REGISTERED).submodelDescriptor(submodel).build();
		eventSink.consumeEvent(evt);
	}

	private void submodelUnregistered(@NonNull String aasDescriptorId, @NonNull String submodelId) {
		RegistryEvent evt = RegistryEvent.builder().id(aasDescriptorId).submodelId(submodelId).type(RegistryEvent.EventType.SUBMODEL_UNREGISTERED).build();
		eventSink.consumeEvent(evt);
	}
}

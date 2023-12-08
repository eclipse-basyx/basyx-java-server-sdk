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
package org.eclipse.digitaltwin.basyx.aasregistry.service.storage.memory;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Set;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.eclipse.digitaltwin.basyx.aasregistry.model.AssetAdministrationShellDescriptor;
import org.eclipse.digitaltwin.basyx.aasregistry.model.ShellDescriptorSearchRequest;
import org.eclipse.digitaltwin.basyx.aasregistry.model.ShellDescriptorSearchResponse;
import org.eclipse.digitaltwin.basyx.aasregistry.model.SubmodelDescriptor;
import org.eclipse.digitaltwin.basyx.aasregistry.service.errors.AasDescriptorAlreadyExistsException;
import org.eclipse.digitaltwin.basyx.aasregistry.service.errors.AasDescriptorNotFoundException;
import org.eclipse.digitaltwin.basyx.aasregistry.service.errors.SubmodelNotFoundException;
import org.eclipse.digitaltwin.basyx.aasregistry.service.storage.AasRegistryStorage;
import org.eclipse.digitaltwin.basyx.aasregistry.service.storage.DescriptorFilter;
import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;

@RequiredArgsConstructor
public class ThreadSafeAasRegistryStorageDecorator implements AasRegistryStorage {

	private final AasRegistryStorage storage;

	private final ThreadSafeAccess access = new ThreadSafeAccess();

	@Override
	public CursorResult<List<AssetAdministrationShellDescriptor>> getAllAasDescriptors(@NonNull PaginationInfo pRequest, @NonNull DescriptorFilter filter) {
		return access.read(storage::getAllAasDescriptors, pRequest, filter);
	}

	@Override
	public void removeAasDescriptor(@NonNull String aasDescriptorId) {
		access.write(storage::removeAasDescriptor, aasDescriptorId);
	}

	@Override
	public AssetAdministrationShellDescriptor getAasDescriptor(@NonNull String aasDescriptorId) throws AasDescriptorNotFoundException {
		return access.read(storage::getAasDescriptor, aasDescriptorId);
	}

	@Override
	public CursorResult<List<SubmodelDescriptor>> getAllSubmodels(@NonNull String aasDescriptorId, @NonNull PaginationInfo pRequest) throws AasDescriptorNotFoundException {
		return access.read(storage::getAllSubmodels, aasDescriptorId, pRequest);
	}

	@Override
	public SubmodelDescriptor getSubmodel(@NonNull String aasDescriptorId, @NonNull String submodelId) {
		return access.read(storage::getSubmodel, aasDescriptorId, submodelId);
	}

	@Override
	public void insertSubmodel(@NonNull String aasDescriptorId, @NonNull SubmodelDescriptor submodel) {
		access.write(storage::insertSubmodel, aasDescriptorId, submodel);
	}

	@Override
	public void removeSubmodel(@NonNull String aasDescrId, @NonNull String submodelId) {
		access.write(storage::removeSubmodel, aasDescrId, submodelId);
	}

	@Override
	public Set<String> clear() {
		return access.write(storage::clear);
	}

	@Override
	public ShellDescriptorSearchResponse searchAasDescriptors(ShellDescriptorSearchRequest request) {
		return access.read(storage::searchAasDescriptors, request);
	}

	@Override
	public void insertAasDescriptor(@Valid AssetAdministrationShellDescriptor descr) throws AasDescriptorAlreadyExistsException {
		access.write(storage::insertAasDescriptor, descr);
	}

	@Override
	public void replaceAasDescriptor(@NonNull String aasDescritorId, @NonNull AssetAdministrationShellDescriptor descriptor) throws AasDescriptorNotFoundException {
		access.write(storage::replaceAasDescriptor, aasDescritorId, descriptor);
	}

	@Override
	public void replaceSubmodel(@NonNull String aasDescriptorId, @NonNull String submodelId, @NonNull SubmodelDescriptor submodel) throws AasDescriptorNotFoundException, SubmodelNotFoundException {
		access.write(storage::replaceSubmodel, aasDescriptorId, submodelId, submodel);
	}
}

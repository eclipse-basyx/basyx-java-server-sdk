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
package org.eclipse.digitaltwin.basyx.submodelregistry.service.storage.memory;

import java.util.List;
import java.util.Set;

import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.eclipse.digitaltwin.basyx.submodelregistry.model.SubmodelDescriptor;
import org.eclipse.digitaltwin.basyx.submodelregistry.service.errors.SubmodelAlreadyExistsException;
import org.eclipse.digitaltwin.basyx.submodelregistry.service.errors.SubmodelNotFoundException;
import org.eclipse.digitaltwin.basyx.submodelregistry.service.storage.SubmodelRegistryStorage;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ThreadSafeSubmodelRegistryStorageDecorator implements SubmodelRegistryStorage {

	private final SubmodelRegistryStorage storage;

	private final ThreadSafeAccess access = new ThreadSafeAccess();
	
	@Override
	public CursorResult<List<SubmodelDescriptor>> getAllSubmodelDescriptors(PaginationInfo pRequest) {
		return access.read(storage::getAllSubmodelDescriptors, pRequest);
	}
	
	@Override
	public Set<String> clear() {
		return access.write(storage::clear);
	}
	
	@Override
	public SubmodelDescriptor getSubmodelDescriptor( String submodelId) throws SubmodelNotFoundException {
		return access.read(storage::getSubmodelDescriptor, submodelId);
	}
	
	@Override
	public void insertSubmodelDescriptor(SubmodelDescriptor descr) throws SubmodelAlreadyExistsException {
		access.write(storage::insertSubmodelDescriptor, descr);
	}
	
	@Override
	public void removeSubmodelDescriptor(String submodelId) throws SubmodelNotFoundException {
		access.write(storage::removeSubmodelDescriptor, submodelId);
	}
	
	@Override
	public void replaceSubmodelDescriptor(String submodelId, SubmodelDescriptor descr) throws SubmodelNotFoundException {
		access.write(storage::replaceSubmodelDescriptor, submodelId, descr);
	}
		
}
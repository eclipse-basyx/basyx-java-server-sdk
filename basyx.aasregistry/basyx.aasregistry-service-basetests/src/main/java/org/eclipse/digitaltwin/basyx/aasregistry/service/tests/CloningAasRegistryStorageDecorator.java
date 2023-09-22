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
package org.eclipse.digitaltwin.basyx.aasregistry.service.tests;

import java.util.List;

import org.eclipse.digitaltwin.basyx.aasregistry.model.AssetAdministrationShellDescriptor;
import org.eclipse.digitaltwin.basyx.aasregistry.model.SubmodelDescriptor;
import org.eclipse.digitaltwin.basyx.aasregistry.service.storage.AasRegistryStorage;
import org.eclipse.digitaltwin.basyx.aasregistry.service.storage.AasRegistryStorageDelegate;
import org.eclipse.digitaltwin.basyx.aasregistry.service.storage.DescriptorCopies;
import org.eclipse.digitaltwin.basyx.aasregistry.service.storage.DescriptorFilter;
import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;

import lombok.NonNull;

//performs additional cloning for in memory tests so that
//altering the objects during tests will then not affect the storage
public class CloningAasRegistryStorageDecorator extends AasRegistryStorageDelegate {

	public CloningAasRegistryStorageDecorator(AasRegistryStorage storage) {
		super(storage);
	}

	@Override
	public CursorResult<List<AssetAdministrationShellDescriptor>> getAllAasDescriptors(@NonNull PaginationInfo pRequest, @NonNull DescriptorFilter filter) {
		CursorResult<List<AssetAdministrationShellDescriptor>> result = storage.getAllAasDescriptors(pRequest, filter);
		List<AssetAdministrationShellDescriptor> listClone = DescriptorCopies.deepCloneCollection(result.getResult());
		return new CursorResult<>(result.getCursor(), listClone);
	}
	@Override
	public AssetAdministrationShellDescriptor getAasDescriptor(String aasId) {
		return DescriptorCopies.deepClone(storage.getAasDescriptor(aasId));
	}
	
	@Override
	public void replaceAasDescriptor(String id, AssetAdministrationShellDescriptor descriptor) {
		storage.replaceAasDescriptor(id, DescriptorCopies.deepClone(descriptor));
	}
	
	@Override
	public CursorResult<List<SubmodelDescriptor>> getAllSubmodels(String aasDescriptorId, PaginationInfo pRequest) {
		CursorResult<List<SubmodelDescriptor>> result = storage.getAllSubmodels(aasDescriptorId, pRequest);
		List<SubmodelDescriptor> submodelClone = DescriptorCopies.deepCloneCollection(result.getResult());
		return new CursorResult<>(result.getCursor(), submodelClone);
	}

	@Override
	public SubmodelDescriptor getSubmodel(String aasDescriptorId, String submodelId) {
		return DescriptorCopies.deepClone(storage.getSubmodel(aasDescriptorId, submodelId));
	}

	@Override
	public void insertSubmodel(String aasDescriptorId, SubmodelDescriptor submodel) {
		storage.insertSubmodel(aasDescriptorId, DescriptorCopies.deepClone(submodel));
	}
}

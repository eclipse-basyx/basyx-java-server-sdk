/*******************************************************************************
 * Copyright (C) 2024 the Eclipse BaSyx Authors
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

import java.util.List;

import org.eclipse.digitaltwin.basyx.aasregistry.model.AssetAdministrationShellDescriptor;
import org.eclipse.digitaltwin.basyx.aasregistry.service.errors.AasDescriptorAlreadyExistsException;
import org.eclipse.digitaltwin.basyx.aasregistry.service.errors.AasDescriptorNotFoundException;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default Implementation of the AAS Transactions Service
 * 
 * @author mateusmolina
 */
@Transactional(rollbackFor = { AasDescriptorAlreadyExistsException.class, AasDescriptorNotFoundException.class })
public class DefaultAasBulkOperationsService implements AasRegistryBulkOperationsService {

	private final AasRegistryStorage storage;

	public DefaultAasBulkOperationsService(AasRegistryStorage storage) {
		this.storage = storage;
	}

	@Override
	public void createBulkAasDescriptors(List<AssetAdministrationShellDescriptor> descriptors) {
		descriptors.forEach(storage::insertAasDescriptor);
	}

	@Override
	public void deleteBulkAasDescriptors(List<String> descriptorIdentifiers) {
		descriptorIdentifiers.forEach(storage::removeAasDescriptor);
	}

	@Override
	public void updateBulkAasDescriptors(List<AssetAdministrationShellDescriptor> descriptors) {
		descriptors.forEach(desc -> storage.replaceAasDescriptor(desc.getId(), desc));
	}
}

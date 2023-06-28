/*******************************************************************************
 * Copyright (C) 2022 DFKI GmbH
 * Author: Gerhard Sonnenberg (gerhard.sonnenberg@dfki.de)
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
package org.eclipse.digitaltwin.basyx.aasregistry.service.api;

import org.eclipse.digitaltwin.basyx.aasregistry.model.ShellDescriptorQuery;
import org.eclipse.digitaltwin.basyx.aasregistry.model.ShellDescriptorSearchRequest;
import org.eclipse.digitaltwin.basyx.aasregistry.model.ShellDescriptorSearchResponse;
import org.eclipse.digitaltwin.basyx.aasregistry.paths.AasRegistryPaths;
import org.eclipse.digitaltwin.basyx.aasregistry.service.errors.ExtensionPathInvalidException;
import org.eclipse.digitaltwin.basyx.aasregistry.service.events.RegistryEventSink;
import org.eclipse.digitaltwin.basyx.aasregistry.service.storage.AasRegistryStorage;
import org.eclipse.digitaltwin.basyx.aasregistry.service.storage.RegistrationEventSendingAasRegistryStorage;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class BasyxSearchApiDelegate implements SearchApiDelegate {

	private final AasRegistryStorage storage;

	public BasyxSearchApiDelegate(AasRegistryStorage storage, RegistryEventSink eventSink) {
		this.storage = new RegistrationEventSendingAasRegistryStorage(storage, eventSink);
	}

	@Override
	public ResponseEntity<ShellDescriptorSearchResponse> searchShellDescriptors(ShellDescriptorSearchRequest request) {
		assertSearchResultIsSetupCorrectly(request.getQuery());
		ShellDescriptorSearchResponse result = storage.searchAasDescriptors(request);
		return ResponseEntity.ok(result);
	}
	
	private void assertSearchResultIsSetupCorrectly(ShellDescriptorQuery query) {
		if (query == null) {
			return;
		}
		String extensionName = query.getExtensionName();
		if (extensionName == null) {
			return;
		}
		String path = query.getPath();
		String validExtensionValueSuffix = "." + AasRegistryPaths.SEGMENT_EXTENSIONS + "." + AasRegistryPaths.SEGMENT_VALUE; 
		if (!path.endsWith(validExtensionValueSuffix)) {
			throw new ExtensionPathInvalidException(path);
		}
		assertSearchResultIsSetupCorrectly(query.getCombinedWith());
	}
}
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

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.eclipse.digitaltwin.basyx.submodelregistry.model.SubmodelDescriptor;

import lombok.NonNull;
 

public class CursorEncodingRegistryStorage extends SubmodelRegistryStorageDecorator {

	public CursorEncodingRegistryStorage(SubmodelRegistryStorage storage) {
		super(storage);
	}
	
	@Override
	public CursorResult<List<SubmodelDescriptor>> getAllSubmodelDescriptors(@NonNull PaginationInfo pRequest) {
		PaginationInfo decoded = decodeCursor(pRequest);
		CursorResult<List<SubmodelDescriptor>> result = storage.getAllSubmodelDescriptors(decoded);
		return encodeCursor(result);
	}


	private CursorResult<List<SubmodelDescriptor>> encodeCursor(CursorResult<List<SubmodelDescriptor>> result) {
		String encodedCursor = encodeCursor(result.getCursor());
		return new CursorResult<List<SubmodelDescriptor>>(encodedCursor, result.getResult());
	}
	
	private PaginationInfo decodeCursor(PaginationInfo info) {
		String cursor = decodeCursor(info.getCursor());
		return new PaginationInfo(info.getLimit(), cursor);
	}

	private String decodeCursor(String cursor) {
		if (cursor == null) {
			return null;
		}
		return new String(java.util.Base64.getUrlDecoder().decode(cursor), StandardCharsets.UTF_8);
	}

	private String encodeCursor(String cursor) {
		if (cursor == null) {
			return null;
		}
		return new String(java.util.Base64.getUrlEncoder().encode(cursor.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);		
	}	
}
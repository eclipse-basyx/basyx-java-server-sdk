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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.digitaltwin.basyx.aasregistry.model.ShellDescriptorQuery;
import org.eclipse.digitaltwin.basyx.aasregistry.paths.AasRegistryPaths;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;


public class ShellDescriptorSearchRequests {

	private ShellDescriptorSearchRequests() {
	}

	public static GroupedQueries groupQueries(ShellDescriptorQuery query) {
		ArrayList<ShellDescriptorQuery> submodelQueries = new ArrayList<>();
		ArrayList<ShellDescriptorQuery> rootQueries = new ArrayList<>();
		String smPrefix = AasRegistryPaths.SEGMENT_SUBMODEL_DESCRIPTORS + ".";
		for (ShellDescriptorQuery currentQuery = query; currentQuery != null; currentQuery = currentQuery.getCombinedWith()) {
			if (currentQuery.getPath().startsWith(smPrefix)) {
				submodelQueries.add(currentQuery);
			} else {
				rootQueries.add(currentQuery);
			}
		}
		return new GroupedQueries(rootQueries, submodelQueries);
	}
	
	
	@Getter
	@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
	public static class GroupedQueries {
		
		
		private final List<ShellDescriptorQuery> queriesOutsideSubmodel;
		
		private final List<ShellDescriptorQuery> queriesInsideSubmodel;
		
		
	}
	
	
}

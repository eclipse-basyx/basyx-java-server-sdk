/*******************************************************************************
 * Copyright (C) 2025 the Eclipse BaSyx Authors
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

package org.eclipse.digitaltwin.basyx.submodelregistry.client.factory;

import java.util.List;

import org.eclipse.digitaltwin.basyx.submodelregistry.client.mapper.AttributeMapper;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.model.SubmodelDescriptor;

/**
 * Factory for creating the SubmodelDescriptor for submodel services
 * 
 * @author Gerhard Sonnenberg (DFKI GmbH)
 */
public class SubmodelServiceDescriptorFactory extends SubmodelDescriptorFactory {

	private static final String SUBMODEL_SERVICE_PATH = "submodel";

	public SubmodelServiceDescriptorFactory(List<String> submodelBaseURLs, AttributeMapper attributeMapper) {
		super(submodelBaseURLs, attributeMapper);
	}
	
	@Override
	protected String getSubmodelPathPrefix() {
		return SUBMODEL_SERVICE_PATH;
	}
	
	@Override
	protected String createHref(String submodelId, String url) {
		// the submodelId is not relevant for a submodel service
		// the href just ends with /submodel
		return url;
	}
}

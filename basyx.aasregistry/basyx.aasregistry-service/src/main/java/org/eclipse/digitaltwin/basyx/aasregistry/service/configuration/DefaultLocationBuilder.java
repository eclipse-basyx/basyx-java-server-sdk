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
package org.eclipse.digitaltwin.basyx.aasregistry.service.configuration;

import java.net.URI;
import java.nio.charset.StandardCharsets;

import org.eclipse.digitaltwin.basyx.aasregistry.service.api.LocationBuilder;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;


class DefaultLocationBuilder implements LocationBuilder {

	private static final String SM_ID_PARAM = "/{sm-id}";
	private static final String SUBMODEL_DESCRIPTORS = "/submodel-descriptors";
	private static final String SHELL_ID_PARAM = "/{shell-id}";
	private static final String SHELL_DESCRIPTORS = "/shell-descriptors";

	@Override
	public URI getAasLocation(String aasId) {
		String encodedAasId = encode(aasId);
		return ServletUriComponentsBuilder.fromCurrentContextPath().path(SHELL_DESCRIPTORS).path(SHELL_ID_PARAM).buildAndExpand(encodedAasId).toUri();
	}

	@Override
	public URI getSubmodelLocation(String aasId, String submodelId) {
		String encodedShellId = encode(aasId);
		String encodedSmId = encode(submodelId);
		return ServletUriComponentsBuilder.fromCurrentContextPath().path(SHELL_DESCRIPTORS).path(SHELL_ID_PARAM).path(SUBMODEL_DESCRIPTORS).path(SM_ID_PARAM).buildAndExpand(encodedShellId, encodedSmId).toUri();
	}

	private String encode(String value) {
		if (value == null) {
			return null;
		}
		return new String(java.util.Base64.getUrlEncoder().encode(value.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
	}
}

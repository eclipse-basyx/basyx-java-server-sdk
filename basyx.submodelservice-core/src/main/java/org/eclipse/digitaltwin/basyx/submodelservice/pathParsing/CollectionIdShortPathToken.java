/*******************************************************************************
 * Copyright (C) 2023 the Eclipse BaSyx Authors
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
package org.eclipse.digitaltwin.basyx.submodelservice.pathParsing;

import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElementCollection;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;

/**
 * Implementation of {@link PathToken} for SubmodelElementCollection idShort
 * tokens
 * 
 * @author fried
 *
 */
public class CollectionIdShortPathToken implements PathToken {

	private final String token;

	public CollectionIdShortPathToken(String token) {
		this.token = token;
	}

	@Override
	public SubmodelElement getSubmodelElement(SubmodelElement rootElement) {
		if (!(rootElement instanceof SubmodelElementCollection))
			throw new ElementDoesNotExistException(token);

		SubmodelElementCollection smc = (SubmodelElementCollection) rootElement;

		return smc.getValue().stream().filter(sme -> sme.getIdShort().equals(token)).findAny()
				.orElseThrow(() -> new ElementDoesNotExistException(token));
	}

	@Override
	public String getToken() {
		return token;
	}

}

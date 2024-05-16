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
package org.eclipse.digitaltwin.basyx.aasxfileserver.backend;

import org.eclipse.digitaltwin.basyx.aasxfileserver.AASXFileServer;
import org.eclipse.digitaltwin.basyx.aasxfileserver.AASXFileServerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

/**
 * Simple AAS Discovery factory that creates a {@link CrudAASXFileServer} with a
 * backend provider and a service factory
 * 
 * @author zielstor, fried
 * 
 */
@Component
@ConditionalOnExpression("!T(org.springframework.util.StringUtils).isEmpty('${basyx.backend:}')")
public class SimpleAASXFileServerFactory implements AASXFileServerFactory {

	private AASXFileServerBackendProvider aasxFileServerBackendProvider;

	private String aasxFileServerName = null;

	/**
	 * Constructor
	 * 
	 * @param aasxFileServerBackendProvider
	 *            The backend provider
	 */
	@Autowired(required = false)
	public SimpleAASXFileServerFactory(AASXFileServerBackendProvider aasxFileServerBackendProvider) {
		this.aasxFileServerBackendProvider = aasxFileServerBackendProvider;
	}

	/**
	 * Constructor
	 * 
	 * @param aasxFileServerBackendProvider
	 *            The backend provider
	 * @param aasRepositoryName
	 *            The AASX file server name
	 */
	@Autowired(required = false)
	public SimpleAASXFileServerFactory(AASXFileServerBackendProvider aasxFileServerBackendProvider,
			@Value("${basyx.aasxfileserver.name:aasx-fileserver}") String aasRepositoryName) {
		this(aasxFileServerBackendProvider);
		this.aasxFileServerName = aasRepositoryName;
	}

	@Override
	public AASXFileServer create() {
		return new CrudAASXFileServer(aasxFileServerBackendProvider, aasxFileServerName);
	}

}

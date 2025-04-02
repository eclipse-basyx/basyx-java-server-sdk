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

package org.eclipse.digitaltwin.basyx.aasxfileserver.backend;

import org.eclipse.digitaltwin.basyx.aasxfileserver.AASXFileServer;
import org.eclipse.digitaltwin.basyx.aasxfileserver.AASXFileServerFactory;
import org.eclipse.digitaltwin.basyx.core.filerepository.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

/**
 * Simple AAS Discovery factory that creates a {@link CrudAASXFileServer} with a
 * backend provider and a service factory
 * 
 * @author zielstor, fried, mateusmolina
 * 
 */
@Component
@ConditionalOnExpression("!T(org.springframework.util.StringUtils).isEmpty('${basyx.backend:}')")
public class CrudAASXFileServerFactory implements AASXFileServerFactory {

	static final String DEFAULT_AASX_FILE_SERVER_NAME = "aasx-fileserver";

	private final PackageBackend packageBackend;
	private final FileRepository fileRepository;
	private final String aasxFileServerName;

	@Autowired
	public CrudAASXFileServerFactory(PackageBackend aasxFileServerBackend, FileRepository fileRepository, @Value("${basyx.aasxfileserver.name:" + DEFAULT_AASX_FILE_SERVER_NAME + "}") String aasxFileServerName) {
		this.packageBackend = aasxFileServerBackend;
		this.fileRepository = fileRepository;
		this.aasxFileServerName = aasxFileServerName;
	}

	public CrudAASXFileServerFactory(PackageBackend aasxFileServerBackend, FileRepository fileRepository) {
		this(aasxFileServerBackend, fileRepository, DEFAULT_AASX_FILE_SERVER_NAME);
	}

	@Override
	public AASXFileServer create() {
		return new CrudAASXFileServer(packageBackend, fileRepository, aasxFileServerName);
	}

}

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

package org.eclipse.digitaltwin.basyx.aasxfileserver;

import static org.junit.Assert.*;

import org.eclipse.digitaltwin.basyx.aasxfileserver.backend.CrudAASXFileServerFactory;
import org.eclipse.digitaltwin.basyx.aasxfileserver.backend.InMemoryPackageBackend;
import org.eclipse.digitaltwin.basyx.aasxfileserver.core.AASXFileServerSuite;
import org.eclipse.digitaltwin.basyx.core.filerepository.InMemoryFileRepository;
import org.junit.Test;

/**
 * Tests the {@link AASXFileServerInMemoryBackendProvider}
 * 
 * @author chaithra
 *
 */
public class TestInMemoryAASXFileServer extends AASXFileServerSuite {

	private static final String CONFIGURED_AASX_SERVER_NAME = "configured-aasx-server-name";

	@Override
	protected AASXFileServer getAASXFileServer() {
		return new CrudAASXFileServerFactory(new InMemoryPackageBackend(), new InMemoryFileRepository()).create();
	}

	@Test
	public void getConfiguredInMemoryAASXFileServer() {
		AASXFileServer server = new CrudAASXFileServerFactory(new InMemoryPackageBackend(), new InMemoryFileRepository(), CONFIGURED_AASX_SERVER_NAME).create();

		assertEquals(CONFIGURED_AASX_SERVER_NAME, server.getName());
	}

}

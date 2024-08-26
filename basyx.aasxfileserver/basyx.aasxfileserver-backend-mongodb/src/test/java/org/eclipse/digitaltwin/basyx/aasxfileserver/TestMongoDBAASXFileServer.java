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

package org.eclipse.digitaltwin.basyx.aasxfileserver;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.eclipse.digitaltwin.basyx.aasxfileserver.backend.SimpleAASXFileServerFactory;
import org.eclipse.digitaltwin.basyx.aasxfileserver.core.AASXFileServerSuite;
import org.eclipse.digitaltwin.basyx.common.mongocore.MongoDBUtilities;
import org.junit.Test;
import org.springframework.data.mongodb.core.MongoTemplate;

import static org.junit.Assert.assertEquals;

/**
 * Tests the {@link AASXFileServerMongoDBBackendProvider}
 * 
 * @author zielstor,fried
 *
 */
public class TestMongoDBAASXFileServer extends AASXFileServerSuite {

	private static final String CONFIGURED_AASX_SERVER_NAME = "configured-aasx-server-name";
	private final String CONNECTION_URL = "mongodb://mongoAdmin:mongoPassword@localhost:27017";
	private final MongoClient CLIENT = MongoClients.create(CONNECTION_URL);
	private final MongoTemplate TEMPLATE = new MongoTemplate(CLIENT, "BaSyxTestDb");

	@Override
	protected AASXFileServer getAASXFileServer() {
		MongoDBUtilities.clearCollection(TEMPLATE, "BaSyxAASXFileServerTest");
		MongoDBUtilities.clearCollection(TEMPLATE, "BaSyxAASXFileServerTestFileBucket.chunks");
		MongoDBUtilities.clearCollection(TEMPLATE, "BaSyxAASXFileServerTestFileBucket.files");
		return new SimpleAASXFileServerFactory(new AASXFileServerMongoDBBackendProvider(TEMPLATE,"BaSyxAASXFileServerTest","BaSyxAASXFileServerTestFileBucket")).create();
	}

	@Test
	public void getConfiguredInMemoryAASXFileServer() {
		AASXFileServer server =  new SimpleAASXFileServerFactory(new AASXFileServerMongoDBBackendProvider(TEMPLATE,"BaSyxAASXFileServerTest","BaSyxAASXFileServerTestFileBucket"),CONFIGURED_AASX_SERVER_NAME).create();

		assertEquals(CONFIGURED_AASX_SERVER_NAME, server.getName());
	}

}

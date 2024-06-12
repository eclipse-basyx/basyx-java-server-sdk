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

package org.eclipse.digitaltwin.basyx.aasdiscoveryservice.backend.mongodb;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.SpecificAssetId;
import org.eclipse.digitaltwin.basyx.aasdiscoveryservice.backend.SimpleAasDiscoveryFactory;
import org.eclipse.digitaltwin.basyx.aasdiscoveryservice.core.AasDiscoveryService;
import org.eclipse.digitaltwin.basyx.aasdiscoveryservice.core.AasDiscoveryServiceFactory;
import org.eclipse.digitaltwin.basyx.aasdiscoveryservice.core.AasDiscoveryServiceSuite;
import org.eclipse.digitaltwin.basyx.common.mongocore.BasyxMongoMappingContext;
import org.eclipse.digitaltwin.basyx.common.mongocore.MongoDBUtilities;
import org.junit.Test;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

/**
 * Tests the {@link MongoDBAasDiscoveryService}
 * 
 * @author danish
 *
 */
public class TestMongoDBAasDiscoveryService extends AasDiscoveryServiceSuite {
	private static final String CONFIGURED_AAS_DISC_SERV_NAME = "configured-aas-discovery-service-name";
	private final String COLLECTION = "aasDiscoveryServiceTestCollection";

	@Override
	protected AasDiscoveryService getAasDiscoveryService() {
		MongoTemplate mongoTemplate = createTemplate();
		MongoDBUtilities.clearCollection(mongoTemplate, COLLECTION);
		AasDiscoveryMongoDBBackendProvider aasDiscoveryBackendProvider = new AasDiscoveryMongoDBBackendProvider(
				new BasyxMongoMappingContext(),
				COLLECTION, mongoTemplate);
		AasDiscoveryServiceFactory aasDiscoveryFactory = new SimpleAasDiscoveryFactory(aasDiscoveryBackendProvider);

		return aasDiscoveryFactory.create();
	}

	@Test
	public void configuredMongoDBAasDiscoveryServiceName() {
		MongoTemplate template = createTemplate();

		clearDatabase(template);

		AasDiscoveryMongoDBBackendProvider aasDiscoveryBackendProvider = new AasDiscoveryMongoDBBackendProvider(
				new BasyxMongoMappingContext(), COLLECTION, template);
		AasDiscoveryServiceFactory aasDiscoveryFactory = new SimpleAasDiscoveryFactory(aasDiscoveryBackendProvider,
				CONFIGURED_AAS_DISC_SERV_NAME);
		AasDiscoveryService service = aasDiscoveryFactory.create();

		assertEquals(CONFIGURED_AAS_DISC_SERV_NAME, service.getName());
	}

	@Test
	public void assetLinkIsPersisted() {
		AasDiscoveryService aasDiscoveryService = getAasDiscoveryService();

		String dummyShellIdentifier = "DummyShellID";

		List<SpecificAssetId> expectedAssetIDs = createDummyAssetLinkOnDiscoveryService(dummyShellIdentifier, aasDiscoveryService);

		List<SpecificAssetId> actualAssetIDs = aasDiscoveryService.getAllAssetLinksById(dummyShellIdentifier);

		assertEquals(expectedAssetIDs, actualAssetIDs);

		removeCreatedAssetLink(dummyShellIdentifier, aasDiscoveryService);
	}

	private List<SpecificAssetId> createDummyAssetLinkOnDiscoveryService(String testShellIdentifier, AasDiscoveryService aasDiscoveryService) {
		AssetAdministrationShell aas = getSingleDummyShell(testShellIdentifier);
		createAssetLink(aas, aasDiscoveryService);

		SpecificAssetId specificAssetId_1 = createDummySpecificAssetId("TestAsset1", "TestAssetValue1");
		SpecificAssetId specificAssetId_2 = createDummySpecificAssetId("TestAsset2", "TestAssetValue2");

		return Arrays.asList(specificAssetId_1, specificAssetId_2);
	}

	private void removeCreatedAssetLink(String dummyShellIdentifier, AasDiscoveryService aasDiscoveryService) {
		aasDiscoveryService.deleteAllAssetLinksById(dummyShellIdentifier);
	}

	private MongoTemplate createTemplate() {
		String connectionURL = "mongodb://mongoAdmin:mongoPassword@localhost:27017/";

		MongoClient client = MongoClients.create(connectionURL);

		return new MongoTemplate(client, "BaSyxTestDb");
	}

	private void clearDatabase(MongoTemplate template) {
		template.remove(new Query(), COLLECTION);
	}

}

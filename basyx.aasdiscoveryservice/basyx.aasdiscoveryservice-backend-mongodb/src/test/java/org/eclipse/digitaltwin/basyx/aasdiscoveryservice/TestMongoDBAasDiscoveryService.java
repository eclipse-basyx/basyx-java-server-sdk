/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
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


package org.eclipse.digitaltwin.basyx.aasdiscoveryservice;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.model.SpecificAssetID;
import org.eclipse.digitaltwin.basyx.aasdiscoveryservice.core.AasDiscoveryServiceSuite;
import org.junit.Test;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

/**
 * Tests the {@link MongoDBAasDiscoveryService}
 * 
 * @author danish, kammognie
 *
 */
public class TestMongoDBAasDiscoveryService extends AasDiscoveryServiceSuite {
	private static final String CONFIGURED_CD_REPO_NAME = "configured-aas-discovery-service-name";
	private final String COLLECTION = "aasDiscoveryServiceTestCollection";
	
	@Override
	protected AasDiscoveryService getAasDiscoveryService() {
		MongoTemplate template = createTemplate();

		clearDatabase(template);

		return new MongoDBAasDiscoveryServiceFactory(template, COLLECTION).create();
	}
	
	@Test
	public void configuredMongoDBAasDiscoveryServiceName() {
        MongoTemplate template = createTemplate();
		
		clearDatabase(template);
		
		AasDiscoveryService repo = new MongoDBAasDiscoveryService(template, COLLECTION, CONFIGURED_CD_REPO_NAME);
		
		assertEquals(CONFIGURED_CD_REPO_NAME, repo.getName());
	}

	@Test
	public void assetLinkIsPersisted() {
		AasDiscoveryService aasDiscoveryService = getAasDiscoveryService();
		
		String dummyShellIdentifier = "DummyShellID";
		
		List<SpecificAssetID> expectedAssetIDs = createDummyAssetLinkOnDiscoveryService(dummyShellIdentifier, aasDiscoveryService);
		
		List<SpecificAssetID> actualAssetIDs = aasDiscoveryService.getAllAssetLinksById(dummyShellIdentifier);

		assertEquals(expectedAssetIDs, actualAssetIDs);
	}
//	
//	@Test
//	public void updatedConceptDescriptionIsPersisted() {
//		ConceptDescriptionRepository mongoDBConceptDescriptionRepository = getConceptDescriptionRepository();
//		
//		ConceptDescription expectedConceptDescription = createDummyConceptDescriptionOnRepo(mongoDBConceptDescriptionRepository);
//		
//		addDescriptionToConceptDescription(expectedConceptDescription);
//		
//		mongoDBConceptDescriptionRepository.updateConceptDescription(expectedConceptDescription.getId(), expectedConceptDescription);
//		
//		ConceptDescription retrievedConceptDescription = getConceptDescriptionFromNewBackendInstance(mongoDBConceptDescriptionRepository, expectedConceptDescription.getId());
//
//		assertEquals(expectedConceptDescription, retrievedConceptDescription);
//	}
	
//	private void addDescriptionToConceptDescription(ConceptDescription expectedConceptDescription) {
//		expectedConceptDescription.setDescription(Arrays.asList(new DefaultLangStringTextType.Builder().text("description").language("en").build()));
//	}
//

//
	private List<SpecificAssetID> createDummyAssetLinkOnDiscoveryService(String testShellIdentifier, AasDiscoveryService aasDiscoveryService) {
		createSingleDummyAasAssetLink(testShellIdentifier, aasDiscoveryService);
		
		SpecificAssetID specificAssetID_1 = createDummySpecificAssetID("TestAsset1", "TestAssetValue1");
		SpecificAssetID specificAssetID_2 = createDummySpecificAssetID("TestAsset2", "TestAssetValue2");
		
		return Arrays.asList(specificAssetID_1, specificAssetID_2);
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

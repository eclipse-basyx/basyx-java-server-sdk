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

package org.eclipse.digitaltwin.basyx.aasdiscoveryservice.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.model.SpecificAssetID;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSpecificAssetID;
import org.eclipse.digitaltwin.basyx.aasdiscoveryservice.core.model.AssetLink;
import org.eclipse.digitaltwin.basyx.core.exceptions.AssetLinkDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.exceptions.CollidingAssetLinkException;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.junit.Test;

/**
 * Testsuite for implementations of the {@link AasDiscoveryService} interface
 * 
 * @author danish, zhangzai
 *
 */
public abstract class AasDiscoveryServiceSuite {

	protected abstract AasDiscoveryService getAasDiscoveryService();

	private final PaginationInfo noLimitPaginationInfo = new PaginationInfo(0, "");

	@Test
	public void getAllAssetAdministrationShellIdsByAssetLink() {
		AasDiscoveryService discoveryService = getAasDiscoveryService();

		List<AssetLink> assetLinks = getMultipleDummyAasAssetLink();

		assetLinks.stream()
				.forEach(assetLink -> createAssetLink(assetLink, discoveryService));

		List<String> expectedResult = new ArrayList<>(List.of("TestAasID1", "TestAasID2"));

		List<String> assetIds = new ArrayList<>(List.of("DummyAsset_1_Value", "DummyAsset_3_Value"));

		List<String> actualResult = discoveryService.getAllAssetAdministrationShellIdsByAssetLink(noLimitPaginationInfo, assetIds)
				.getResult();

		assertEquals(expectedResult.size(), actualResult.size());
		assertTrue(expectedResult.containsAll(actualResult) && actualResult.containsAll(expectedResult));
	}

	@Test
	public void getAllAssetLinksById() {
		AasDiscoveryService discoveryService = getAasDiscoveryService();

		List<AssetLink> assetLinks = getMultipleDummyAasAssetLink();

		assetLinks.stream()
				.forEach(assetLink -> createAssetLink(assetLink, discoveryService));

		List<SpecificAssetID> expectedResult = Arrays.asList(createDummySpecificAssetID("DummyAssetName3", "DummyAsset_3_Value"), createDummySpecificAssetID("DummyAssetName4", "DummyAsset_4_Value"));

		String shellIdentifier = "TestAasID2";

		List<SpecificAssetID> actualResult = discoveryService.getAllAssetLinksById(shellIdentifier);

		assertEquals(expectedResult.size(), actualResult.size());
		assertEquals(expectedResult, actualResult);
	}

	@Test
	public void createAllAssetLinksById() {
		AasDiscoveryService discoveryService = getAasDiscoveryService();

		String shellIdentifier = "TestAasID3";

		List<SpecificAssetID> expectedAssetIds = Arrays.asList(createDummySpecificAssetID("DummyAssetName4", "DummyAsset_4_Value"), createDummySpecificAssetID("DummyAssetName5", "DummyAsset_5_Value"));

		List<SpecificAssetID> createdAssetIds = discoveryService.createAllAssetLinksById(shellIdentifier, expectedAssetIds);
		assertEquals(expectedAssetIds, createdAssetIds);

		List<SpecificAssetID> actualAssetIds = discoveryService.getAllAssetLinksById(shellIdentifier);

		assertEquals(expectedAssetIds.size(), actualAssetIds.size());
		assertEquals(expectedAssetIds, actualAssetIds);
	}

	@Test(expected = CollidingAssetLinkException.class)
	public void createExistingAllAssetLinksById() {
		AasDiscoveryService discoveryService = getAasDiscoveryService();

		String shellIdentifier = "ExistingAASId";

		AssetLink assetLink = getSingleDummyAasAssetLink(shellIdentifier);
		createAssetLink(assetLink, discoveryService);

		discoveryService.createAllAssetLinksById(shellIdentifier, new ArrayList<>());
	}

	@Test
	public void deleteAllAssetLinksById() {
		AasDiscoveryService discoveryService = getAasDiscoveryService();

		String shellIdentifier = "TestAasID1";

		List<AssetLink> assetLinks = getMultipleDummyAasAssetLink();

		assetLinks.stream()
				.forEach(assetLink -> createAssetLink(assetLink, discoveryService));

		discoveryService.deleteAllAssetLinksById(shellIdentifier);

		try {
			discoveryService.getAllAssetLinksById(shellIdentifier);
			fail();
		} catch (AssetLinkDoesNotExistException e) {
		}

	}

	@Test(expected = AssetLinkDoesNotExistException.class)
	public void deleteNonExistingAllAssetLinksById() {
		String shellIdentifier = "NonExistingLink";

		getAasDiscoveryService().deleteAllAssetLinksById(shellIdentifier);
	}

	public static List<AssetLink> getMultipleDummyAasAssetLink() {
		String dummyShellIdentifier_1 = "TestAasID1";
		String dummyShellIdentifier_2 = "TestAasID2";

		SpecificAssetID specificAssetID_1 = createDummySpecificAssetID("DummyAssetName1", "DummyAsset_1_Value");
		SpecificAssetID specificAssetID_2 = createDummySpecificAssetID("DummyAssetName2", "DummyAsset_2_Value");

		SpecificAssetID specificAssetID_3 = createDummySpecificAssetID("DummyAssetName3", "DummyAsset_3_Value");
		SpecificAssetID specificAssetID_4 = createDummySpecificAssetID("DummyAssetName4", "DummyAsset_4_Value");

		return Arrays.asList(new AssetLink(dummyShellIdentifier_1, Arrays.asList(specificAssetID_1, specificAssetID_2)), new AssetLink(dummyShellIdentifier_2, Arrays.asList(specificAssetID_3, specificAssetID_4)));
	}

	public static void createAssetLink(AssetLink assetLink, AasDiscoveryService aasDiscoveryService) {
		aasDiscoveryService.createAllAssetLinksById(assetLink.getShellIdentifier(), assetLink.getSpecificAssetIDs());
	}

	public static AssetLink getSingleDummyAasAssetLink(String shellId) {

		SpecificAssetID specificAssetID_1 = createDummySpecificAssetID("TestAsset1", "TestAssetValue1");
		SpecificAssetID specificAssetID_2 = createDummySpecificAssetID("TestAsset2", "TestAssetValue2");

		return new AssetLink(shellId, Arrays.asList(specificAssetID_1, specificAssetID_2));
	}

	protected static SpecificAssetID createDummySpecificAssetID(String name, String value) {
		return new DefaultSpecificAssetID.Builder().name(name)
				.value(value)
				.build();
	}

}

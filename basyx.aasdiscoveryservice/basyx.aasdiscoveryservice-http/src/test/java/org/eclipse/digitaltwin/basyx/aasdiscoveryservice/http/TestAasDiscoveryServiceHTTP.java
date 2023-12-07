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

package org.eclipse.digitaltwin.basyx.aasdiscoveryservice.http;

import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.basyx.aasdiscoveryservice.core.AasDiscoveryService;
import org.eclipse.digitaltwin.basyx.aasdiscoveryservice.core.AasDiscoveryServiceSuite;
import org.eclipse.digitaltwin.basyx.aasdiscoveryservice.core.model.AssetLink;
import org.eclipse.digitaltwin.basyx.core.exceptions.CollidingAssetLinkException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Tests the {@link AssetAdministrationShell} specific parts of the
 * {@link AasRepository} HTTP/REST API
 * 
 * @author schnicke, danish
 *
 */
public class TestAasDiscoveryServiceHTTP extends AasDiscoveryServiceHTTPSuite {
	private static ConfigurableApplicationContext appContext;

	@BeforeClass
	public static void startConceptDescriptionRepo() throws Exception {
		appContext = new SpringApplication(DummyAasDiscoveryServiceComponent.class).run(new String[] {});
	}

	@Override
	public void resetService() {
		AasDiscoveryService aasDiscoveryService = appContext.getBean(AasDiscoveryService.class);

		List<AssetLink> dummyAssetLinks = AasDiscoveryServiceSuite.getMultipleDummyAasAssetLink();

		dummyAssetLinks.stream()
				.forEach(assetLink -> resetAssetLink(assetLink, aasDiscoveryService));
	}

	private void resetAssetLink(AssetLink assetLink, AasDiscoveryService aasDiscoveryService) {

		try {
			aasDiscoveryService.createAllAssetLinksById(assetLink.getShellIdentifier(), assetLink.getSpecificAssetIds());
		} catch (CollidingAssetLinkException e) {
			aasDiscoveryService.deleteAllAssetLinksById(assetLink.getShellIdentifier());
			aasDiscoveryService.createAllAssetLinksById(assetLink.getShellIdentifier(), assetLink.getSpecificAssetIds());
		}
	}

	@AfterClass
	public static void shutdownAasRepo() {
		appContext.close();
	}

	@Override
	protected String getURL() {
		return "http://localhost:8080/lookup/shells";
	}

}

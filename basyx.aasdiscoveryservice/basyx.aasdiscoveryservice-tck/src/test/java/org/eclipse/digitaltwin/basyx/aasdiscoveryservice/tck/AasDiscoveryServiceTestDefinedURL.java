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
package org.eclipse.digitaltwin.basyx.aasdiscoveryservice.tck;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.List;

import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.ParseException;
import org.eclipse.digitaltwin.basyx.aasdiscoveryservice.core.AasDiscoveryServiceSuite;
import org.eclipse.digitaltwin.basyx.aasdiscoveryservice.core.model.AssetLink;
import org.eclipse.digitaltwin.basyx.aasdiscoveryservice.http.AasDiscoveryServiceHTTPSuite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

/**
 * 
 * 
 * @author danish
 *
 */
public class AasDiscoveryServiceTestDefinedURL extends AasDiscoveryServiceHTTPSuite {
	private static final Logger logger = LoggerFactory.getLogger(AasDiscoveryServiceTestDefinedURL.class);
	public static String url = "http://localhost:8081/lookup/shells";
	private static Gson gson = new Gson();

	@Override
	protected String getURL() {
		return url;
	}

	@Override
	public void resetService() {
		createDummyAssetLinks();
	}

	private void createDummyAssetLinks() {
		List<AssetLink> dummyAssetLinks = AasDiscoveryServiceSuite.getMultipleDummyAasAssetLink();
		dummyAssetLinks.forEach(this::createAssetLink);
	}

	private void createAssetLink(AssetLink assetLink) {
		try {
			String specificAssetIdsJSON = gson.toJson(assetLink.getSpecificAssetIDs());
			CloseableHttpResponse creationResponse = createAssetLinks(assetLink.getShellIdentifier(), specificAssetIdsJSON);

			if (creationResponse.getCode() != 409) {
				logger.info("Creating Asset Link with shell id '{}', ResponseCode is '{}'", assetLink.getShellIdentifier(), creationResponse.getCode());
				return;
			}

			resetAssetLink(assetLink);
		} catch (IOException | ParseException e) {
			throw new RuntimeException(e);
		}
	}

	private void resetAssetLink(AssetLink assetLink) {
		deleteAssetLink(assetLink.getShellIdentifier());

		createAssetLink(assetLink);
	}

	private void deleteAssetLink(String shellId) {
		try {
			CloseableHttpResponse deleteResponse = deleteAssetLinkById(shellId);
			logger.info("Deleting Asset Link with shell id '{}', ResponseCode is '{}'", shellId, deleteResponse.getCode());
		} catch (IOException e) {
			fail(e.toString());
		}
	}

}

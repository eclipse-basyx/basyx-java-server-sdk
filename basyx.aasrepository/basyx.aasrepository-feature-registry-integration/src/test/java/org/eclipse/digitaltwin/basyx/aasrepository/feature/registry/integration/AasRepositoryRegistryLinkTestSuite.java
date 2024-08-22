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

package org.eclipse.digitaltwin.basyx.aasrepository.feature.registry.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.eclipse.digitaltwin.basyx.aasregistry.client.ApiException;
import org.eclipse.digitaltwin.basyx.aasregistry.client.api.RegistryAndDiscoveryInterfaceApi;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.AssetAdministrationShellDescriptor;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.GetAssetAdministrationShellDescriptorsResult;
import org.eclipse.digitaltwin.basyx.aasregistry.main.client.mapper.DummyAasDescriptorFactory;
import org.eclipse.digitaltwin.basyx.http.Base64UrlEncodedIdentifier;
import org.eclipse.digitaltwin.basyx.http.serialization.BaSyxHttpTestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;

/**
 * Test suite for {@link RegistryIntegrationAasRepository} feature
 * 
 * @author danish
 */
public abstract class AasRepositoryRegistryLinkTestSuite {

	private static final String AAS_REPOSITORY_PATH = "/shells";
	private static final String DUMMY_GLOBAL_ASSETID = "globalAssetId";
	private static final String DUMMY_IDSHORT = "ExampleMotor";
	private static final String DUMMY_AAS_ID = "customIdentifier";

	protected abstract String[] getAasRepoBaseUrls();

	protected abstract String getAasRegistryUrl();

	protected abstract RegistryAndDiscoveryInterfaceApi getAasRegistryApi();

	private final AssetAdministrationShellDescriptor DUMMY_DESCRIPTOR = DummyAasDescriptorFactory
			.createDummyDescriptor(DUMMY_AAS_ID, DUMMY_IDSHORT, DUMMY_GLOBAL_ASSETID, getAasRepoBaseUrls());

	@Test
	public void createAas() throws FileNotFoundException, IOException, ApiException {
		String aasJsonContent = getAas1JSONString();

		try (CloseableHttpResponse creationResponse = createAasOnRepo(aasJsonContent)) {
			assertEquals(HttpStatus.CREATED.value(), creationResponse.getCode());

			AssetAdministrationShellDescriptor actualDescriptor = retrieveDescriptorFromRegistry();

			assertEquals(DUMMY_DESCRIPTOR, actualDescriptor);

			resetRepository();
		}
	}

	@Test
	public void deleteAas() throws FileNotFoundException, IOException, ApiException {
		String aasJsonContent = getAas1JSONString();

		try (CloseableHttpResponse creationResponse = createAasOnRepo(aasJsonContent)) {
			assertEquals(HttpStatus.CREATED.value(), creationResponse.getCode());
		}

		try (CloseableHttpResponse deleteResponse = deleteAasFromRepo(DUMMY_AAS_ID)) {
			assertEquals(HttpStatus.NO_CONTENT.value(), deleteResponse.getCode());
		}
		assertDescriptionDeletionAtRegistry();
	}

	private AssetAdministrationShellDescriptor retrieveDescriptorFromRegistry() throws ApiException {
		RegistryAndDiscoveryInterfaceApi api = getAasRegistryApi();

		return api.getAssetAdministrationShellDescriptorById(DUMMY_AAS_ID);
	}

	private void resetRepository() throws IOException {
		try (CloseableHttpResponse deleteResponse = deleteAasFromRepo(DUMMY_AAS_ID)) {
			assertEquals(HttpStatus.NO_CONTENT.value(), deleteResponse.getCode());	
		}
	}

	private CloseableHttpResponse deleteAasFromRepo(String shellId) throws IOException {
		return BaSyxHttpTestUtils.executeDeleteOnURL(getSpecificAasAccessURL(shellId));
	}

	private void assertDescriptionDeletionAtRegistry() throws ApiException {
		RegistryAndDiscoveryInterfaceApi api = getAasRegistryApi();

		GetAssetAdministrationShellDescriptorsResult result = api.getAllAssetAdministrationShellDescriptors(null, null,
				null, null);

		List<AssetAdministrationShellDescriptor> actualDescriptors = result.getResult();

		assertTrue(actualDescriptors.isEmpty());
	}

	private String getAas1JSONString() throws FileNotFoundException, IOException {
		return BaSyxHttpTestUtils.readJSONStringFromClasspath("AasSimple_1.json");
	}

	private CloseableHttpResponse createAasOnRepo(String aasJsonContent) throws IOException {
		return BaSyxHttpTestUtils.executePostOnURL(createAasRepositoryUrl(getFirstAasRepoBaseUrl()), aasJsonContent);
	}

	private String getSpecificAasAccessURL(String aasId) {
		return createAasRepositoryUrl(getFirstAasRepoBaseUrl()) + "/"
				+ Base64UrlEncodedIdentifier.encodeIdentifier(aasId);
	}

	private String getFirstAasRepoBaseUrl() {
		return getAasRepoBaseUrls()[0];
	}

	private static String createAasRepositoryUrl(String aasRepositoryBaseURL) {

		try {
			return new URL(new URL(aasRepositoryBaseURL), AAS_REPOSITORY_PATH).toString();
		} catch (MalformedURLException e) {
			throw new RuntimeException("The AAS Repository Base url is malformed. " + e.getMessage());
		}
	}

}

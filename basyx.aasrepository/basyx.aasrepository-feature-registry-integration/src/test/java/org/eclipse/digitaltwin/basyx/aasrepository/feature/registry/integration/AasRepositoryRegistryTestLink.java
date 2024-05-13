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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.eclipse.digitaltwin.basyx.aasregistry.client.ApiException;
import org.eclipse.digitaltwin.basyx.aasregistry.client.api.RegistryAndDiscoveryInterfaceApi;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.AssetAdministrationShellDescriptor;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.AssetKind;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.Endpoint;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.ProtocolInformation;
import org.eclipse.digitaltwin.basyx.http.Base64UrlEncodedIdentifier;
import org.eclipse.digitaltwin.basyx.http.serialization.BaSyxHttpTestUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;

/**
 * Integration test for {@link RegistryIntegrationAasRepository} feature
 * 
 * @author danish
 */
public class AasRepositoryRegistryTestLink {

	private static final String AAS_REPOSITORY_PATH = "/shells";
	private static final String DUMMY_GLOBAL_ASSETID = "globalAssetId";
	private static final String DUMMY_IDSHORT = "ExampleMotor";
	private static final String DUMMY_AAS_ID = "customIdentifier";

	public static String aasRepoBaseUrl = "http://localhost:8081";
	public static String aasRegistryUrl = "http://localhost:8050";

	private AssetAdministrationShellDescriptor dummyDescriptor = createExpectedDescriptor(aasRepoBaseUrl);

	@Before
	public void setUp() throws IOException {
		deleteAasFromRepo(DUMMY_AAS_ID);
	}
	
	@Test
	public void createAas() throws FileNotFoundException, IOException, ApiException {
		String aasJsonContent = getAas1JSONString();

		CloseableHttpResponse creationResponse = createAasOnRepo(aasJsonContent);
		assertEquals(HttpStatus.CREATED.value(), creationResponse.getCode());

		AssetAdministrationShellDescriptor actualDescriptor = retrieveDescriptorFromRegistry();

		assertEquals(dummyDescriptor, actualDescriptor);

		resetRepository();
	}

	@Test
	public void createRepoEndpointsAas() throws FileNotFoundException, IOException, ApiException {
		String aasJsonContent = getAas1JSONString();

		CloseableHttpResponse creationResponse = createAasOnRepo(aasJsonContent);
		assertEquals(HttpStatus.CREATED.value(), creationResponse.getCode());

		AssetAdministrationShellDescriptor actualDescriptor = retrieveDescriptorFromRegistry();

		assertEquals(dummyDescriptor, actualDescriptor);

		resetRepository();
	}

	@Test
	public void deleteAas() throws FileNotFoundException, IOException, ApiException {
		String aasJsonContent = getAas1JSONString();

		CloseableHttpResponse creationResponse = createAasOnRepo(aasJsonContent);
		assertEquals(HttpStatus.CREATED.value(), creationResponse.getCode());

		CloseableHttpResponse deleteResponse = deleteAasFromRepo(DUMMY_AAS_ID);
		assertEquals(HttpStatus.NO_CONTENT.value(), deleteResponse.getCode());

		assertDescriptionDeletionAtRegistry();
	}

	private AssetAdministrationShellDescriptor retrieveDescriptorFromRegistry() throws ApiException {
		RegistryAndDiscoveryInterfaceApi api = new RegistryAndDiscoveryInterfaceApi(aasRegistryUrl);

		return api.getAssetAdministrationShellDescriptorById(DUMMY_AAS_ID);
	}

	private void resetRepository() throws IOException {
		CloseableHttpResponse deleteResponse = deleteAasFromRepo(DUMMY_AAS_ID);

		assertEquals(HttpStatus.NO_CONTENT.value(), deleteResponse.getCode());
	}

	private CloseableHttpResponse deleteAasFromRepo(String shellId) throws IOException {
		return BaSyxHttpTestUtils.executeDeleteOnURL(getSpecificAasAccessURL(shellId));
	}

	private void assertDescriptionDeletionAtRegistry() throws ApiException {
		RegistryAndDiscoveryInterfaceApi api = new RegistryAndDiscoveryInterfaceApi(aasRegistryUrl);
		try {
			api.getAssetAdministrationShellDescriptorById(DUMMY_AAS_ID);
			Assert.fail();
		} catch (ApiException ex) {
			if (ex.getCode() != 404) {
				Assert.fail();
			}
		}
	}

	private String getAas1JSONString() throws FileNotFoundException, IOException {
		return BaSyxHttpTestUtils.readJSONStringFromClasspath("AasSimple_1.json");
	}

	private CloseableHttpResponse createAasOnRepo(String aasJsonContent) throws IOException {
		String repoUrl = getFirstRepoUrl();
		return BaSyxHttpTestUtils.executePostOnURL(createAasRepositoryUrl(repoUrl), aasJsonContent);
	}


	private String getSpecificAasAccessURL(String aasId) {
		String repoUrl = getFirstRepoUrl();
		return createAasRepositoryUrl(repoUrl) + "/" + Base64UrlEncodedIdentifier.encodeIdentifier(aasId);
	}
	
	private static AssetAdministrationShellDescriptor createExpectedDescriptor(String repoUrlString) {

		AssetAdministrationShellDescriptor descriptor = new AssetAdministrationShellDescriptor();

		descriptor.setId(DUMMY_AAS_ID);
		descriptor.setIdShort(DUMMY_IDSHORT);
		descriptor.setAssetKind(AssetKind.INSTANCE);
		descriptor.setGlobalAssetId(DUMMY_GLOBAL_ASSETID);
		descriptor.setEndpoints(createEndpoints(List.of(repoUrlString.split(","))));

		return descriptor;
	}

	private static List<Endpoint> createEndpoints(List<String> urls) {
		List<Endpoint> endpoints = new ArrayList<>(urls.size());
		for (String eachUrl : urls) {
			Endpoint endpoint = new Endpoint();
			endpoint.setInterface("AAS-3.0");
			endpoint.setProtocolInformation(createProtocolInformation(eachUrl));
			endpoints.add(endpoint);
		}
		return endpoints;
	}

	private static ProtocolInformation createProtocolInformation(String url) {
		String href = createHref(url);

		ProtocolInformation protocolInformation = new ProtocolInformation();
		protocolInformation.setHref(href);
		protocolInformation.endpointProtocol(getProtocol(href));

		return protocolInformation;
	}

	private static String createHref(String url) {
		return String.format("%s/%s", createAasRepositoryUrl(url), Base64UrlEncodedIdentifier.encodeIdentifier(DUMMY_AAS_ID));
	}

	private static String getProtocol(String endpoint) {
		try {
			return new URL(endpoint).getProtocol();
		} catch (MalformedURLException e) {
			throw new RuntimeException();
		}
	}

	private static String createAasRepositoryUrl(String aasRepositoryBaseURL) {

		try {
			return new URL(new URL(aasRepositoryBaseURL), AAS_REPOSITORY_PATH).toString();
		} catch (MalformedURLException e) {
			throw new RuntimeException("The AAS Repository Base url is malformed. " + e.getMessage());
		}
	}
	
	private String getFirstRepoUrl() {
		return aasRepoBaseUrl.split(",")[0];
	}
}

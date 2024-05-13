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

package org.eclipse.digitaltwin.basyx.submodelrepository.feature.registry.integration;

import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.eclipse.digitaltwin.basyx.http.Base64UrlEncodedIdentifier;
import org.eclipse.digitaltwin.basyx.http.serialization.BaSyxHttpTestUtils;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.ApiException;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.api.SubmodelRegistryApi;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.model.Endpoint;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.model.Key;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.model.KeyTypes;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.model.ProtocolInformation;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.model.Reference;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.model.ReferenceTypes;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.model.SubmodelDescriptor;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;

/**
 * Integration test for {@link RegistryIntegrationSubmodelRepository} feature
 * 
 * @author danish
 */
public class SubmodelRepositoryRegistryTestLink {

	private static final String SUBMODEL_REPOSITORY_PATH = "/submodels";

	private static final String SUMMY_SUBMODEL_IDSHORT = "TechnicalData";
	private static final String DUMMY_SUBMODEL_ID = "7A7104BDAB57E184";

	public static String submodelRepoBaseUrl = "http://localhost:8081";
	public static String submodelRegistryUrl = "http://localhost:8060";

	private SubmodelDescriptor dummyDescriptor = createExpectedDescriptor();

	@Before
	public void setUp() throws IOException {
		deleteSubmodelFromRepo(DUMMY_SUBMODEL_ID);
	}

	@Test
	public void createSubmodel() throws FileNotFoundException, IOException, ApiException {
		String submodelJsonContent = getSubmodelJSONString();

		CloseableHttpResponse creationResponse = createSubmodelOnRepo(submodelJsonContent);
		assertEquals(HttpStatus.CREATED.value(), creationResponse.getCode());

		SubmodelDescriptor actualDescriptor = retrieveDescriptorFromRegistry();

		assertEquals(dummyDescriptor, actualDescriptor);

		resetRepository();
	}

	@Test
	public void deleteSubmodel() throws FileNotFoundException, IOException, ApiException {
		String submodelJsonContent = getSubmodelJSONString();

		CloseableHttpResponse creationResponse = createSubmodelOnRepo(submodelJsonContent);
		assertEquals(HttpStatus.CREATED.value(), creationResponse.getCode());

		CloseableHttpResponse deleteResponse = deleteSubmodelFromRepo(DUMMY_SUBMODEL_ID);
		assertEquals(HttpStatus.NO_CONTENT.value(), deleteResponse.getCode());

		assertDescriptionDeletionAtRegistry();
	}

	private SubmodelDescriptor retrieveDescriptorFromRegistry() throws ApiException {
		SubmodelRegistryApi api = new SubmodelRegistryApi(submodelRegistryUrl);

		return api.getSubmodelDescriptorById(DUMMY_SUBMODEL_ID);
	}

	private void resetRepository() throws IOException {
		CloseableHttpResponse deleteResponse = deleteSubmodelFromRepo(DUMMY_SUBMODEL_ID);

		assertEquals(HttpStatus.NO_CONTENT.value(), deleteResponse.getCode());
	}

	private CloseableHttpResponse deleteSubmodelFromRepo(String shellId) throws IOException {
		return BaSyxHttpTestUtils.executeDeleteOnURL(getSpecificSubmodelAccessURL(shellId));
	}

	private void assertDescriptionDeletionAtRegistry() throws ApiException {
		SubmodelRegistryApi api = new SubmodelRegistryApi(submodelRegistryUrl);
		try {
			api.getSubmodelDescriptorById(DUMMY_SUBMODEL_ID);
			Assert.fail();
		} catch (ApiException ex) {
			if (ex.getCode() != 404) {
				Assert.fail();
			}
		}
	}

	private String getSubmodelJSONString() throws FileNotFoundException, IOException {
		return BaSyxHttpTestUtils.readJSONStringFromClasspath("SingleSubmodel.json");
	}

	private CloseableHttpResponse createSubmodelOnRepo(String aasJsonContent) throws IOException {
		String repoUrl = getFirstRepoUrl();
		return BaSyxHttpTestUtils.executePostOnURL(createSubmodelRepositoryUrl(repoUrl), aasJsonContent);
	}

	private String getSpecificSubmodelAccessURL(String aasId) {
		String repoUrl = getFirstRepoUrl();
		return createSubmodelRepositoryUrl(repoUrl) + "/" + Base64UrlEncodedIdentifier.encodeIdentifier(aasId);
	}
	
	private static SubmodelDescriptor createExpectedDescriptor() {

		SubmodelDescriptor descriptor = new SubmodelDescriptor();

		descriptor.setId(DUMMY_SUBMODEL_ID);
		descriptor.setIdShort(SUMMY_SUBMODEL_IDSHORT);
		descriptor.setSemanticId(createSemanticId());
		descriptor.setEndpoints(createEndpoints());

		return descriptor;
	}

	private static Reference createSemanticId() {
		return new Reference().keys(Arrays.asList(new Key().type(KeyTypes.GLOBALREFERENCE).value("0173-1#01-AFZ615#016"))).type(ReferenceTypes.EXTERNALREFERENCE);
	}

	private static List<Endpoint> createEndpoints() {
		List<Endpoint> endpoints = new ArrayList<>();
		for (String url : List.of(submodelRepoBaseUrl.split(","))) {
			Endpoint endpoint = new Endpoint();
			endpoint.setInterface("SUBMODEL-3.0");
			endpoint.setProtocolInformation(createProtocolInformation(url));
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
		return String.format("%s/%s", createSubmodelRepositoryUrl(url), Base64UrlEncodedIdentifier.encodeIdentifier(DUMMY_SUBMODEL_ID));
	}

	private static String getProtocol(String endpoint) {
		try {
			return new URL(endpoint).getProtocol();
		} catch (MalformedURLException e) {
			throw new RuntimeException();
		}
	}

	private static String createSubmodelRepositoryUrl(String smRepositoryBaseURL) {

		try {
			return new URL(new URL(smRepositoryBaseURL), SUBMODEL_REPOSITORY_PATH).toString();
		} catch (MalformedURLException e) {
			throw new RuntimeException("The Submodel Repository Base url is malformed.\n " + e.getMessage());
		}
	}

	private String getFirstRepoUrl() {
		return submodelRepoBaseUrl.split(",")[0];
	}
}

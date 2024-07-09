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
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.ParseException;
import org.eclipse.digitaltwin.basyx.http.Base64UrlEncodedIdentifier;
import org.eclipse.digitaltwin.basyx.http.serialization.BaSyxHttpTestUtils;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.ApiException;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.api.SubmodelRegistryApi;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.model.GetSubmodelDescriptorsResult;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.model.SubmodelDescriptor;
import org.junit.Test;
import org.springframework.http.HttpStatus;

/**
 * Integration test for {@link RegistryIntegrationSubmodelRepository} feature
 * 
 * @author danish
 */
public class SubmodelRepositoryRegistryTestLink {
	
	private static final String SUBMODEL_REPOSITORY_PATH = "/submodels";

	private static final String DUMMY_SUBMODEL_IDSHORT = "TechnicalData";
	private static final String DUMMY_SUBMODEL_ID = "7A7104BDAB57E184";

	public static String submodelRepoBaseUrl = "http://localhost:8081";
	public static String submodelRegistryUrl = "http://localhost:8060";

	private static final SubmodelDescriptor DUMMY_DESCRIPTOR = DummySubmodelDescriptorFactory.createDummyDescriptor(DUMMY_SUBMODEL_ID, DUMMY_SUBMODEL_IDSHORT, submodelRepoBaseUrl, DummySubmodelDescriptorFactory.createSemanticId());

	@Test
	public void createSubmodel() throws FileNotFoundException, IOException, ApiException {
		String submodelJsonContent = getSubmodelJSONString();

		CloseableHttpResponse creationResponse = createSubmodelOnRepo(submodelJsonContent);
		assertEquals(HttpStatus.CREATED.value(), creationResponse.getCode());

		SubmodelDescriptor actualDescriptor = retrieveDescriptorFromRegistry();

		assertEquals(DUMMY_DESCRIPTOR, actualDescriptor);

		resetRepository();
	}

	@Test
	public void createSubmodelElementInSubmodelElementCollection() throws FileNotFoundException, IOException, ApiException, ParseException {
		String submodelJsonContent = getSubmodelJSONString();
		String submodelElementJsonContent = getSinglePropertyJSONString();

		CloseableHttpResponse creationResponse = createSubmodelOnRepo(submodelJsonContent);
		assertEquals(HttpStatus.CREATED.value(), creationResponse.getCode());

		CloseableHttpResponse submodelElementCreationResponse = createSubmodelElementOnRepo(submodelElementJsonContent);
		CloseableHttpResponse getResponse = BaSyxHttpTestUtils.executeGetOnURL(getSpecificSubmodelAccessURL(DUMMY_SUBMODEL_ID));

		BaSyxHttpTestUtils.assertSameJSONContent(getExpectedSubmodel(), BaSyxHttpTestUtils.getResponseAsString(getResponse));

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
	
	@Test
    public void testDummyAasDescriptorFactoryUrlWithTrailingSlash() {
        String baseURLWithSlash = submodelRepoBaseUrl + "/context/";
        String SUBMODEL_REPOSITORY_PATH_WITHOUT_SLASH = SUBMODEL_REPOSITORY_PATH.substring(1);

        assertEquals(baseURLWithSlash + SUBMODEL_REPOSITORY_PATH_WITHOUT_SLASH, DummySubmodelDescriptorFactory.createSubmodelRepositoryUrl(baseURLWithSlash));
    }

    @Test
    public void testDummyAasDescriptorFactoryUrlWithoutTrailingSlash() {
        String baseURLWithoutSlash = submodelRepoBaseUrl + "/context";

        assertEquals(baseURLWithoutSlash + "/" + SUBMODEL_REPOSITORY_PATH , DummySubmodelDescriptorFactory.createSubmodelRepositoryUrl(baseURLWithoutSlash));
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

		GetSubmodelDescriptorsResult result = api.getAllSubmodelDescriptors(null, null);

		List<SubmodelDescriptor> actualDescriptors = result.getResult();

		assertTrue(actualDescriptors.isEmpty());
	}

	private String getSubmodelJSONString() throws FileNotFoundException, IOException {
		return BaSyxHttpTestUtils.readJSONStringFromClasspath("SingleSubmodel.json");
	}

	private String getSinglePropertyJSONString() throws FileNotFoundException, IOException {
		return BaSyxHttpTestUtils.readJSONStringFromClasspath("SingleProperty.json");
	}

	private String getExpectedSubmodel() throws FileNotFoundException, IOException {
		return BaSyxHttpTestUtils.readJSONStringFromClasspath("ExpectedSubmodel.json");
	}

	private CloseableHttpResponse createSubmodelOnRepo(String submodelJsonContent) throws IOException {
		return BaSyxHttpTestUtils.executePostOnURL(DummySubmodelDescriptorFactory.createSubmodelRepositoryUrl(submodelRepoBaseUrl), submodelJsonContent);
	}

	private CloseableHttpResponse createSubmodelElementOnRepo(String submodelElementJsonContent) throws IOException {
		return BaSyxHttpTestUtils.executePostOnURL(
				getSpecificSubmodelAccessURL(DUMMY_SUBMODEL_ID) + "/submodel-elements/SubmodelElementCollection",
				submodelElementJsonContent);
	}

	private String getSpecificSubmodelAccessURL(String submodelId) {
		return DummySubmodelDescriptorFactory.createSubmodelRepositoryUrl(submodelRepoBaseUrl) + "/" + Base64UrlEncodedIdentifier.encodeIdentifier(submodelId);
	}

}

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

package org.eclipse.digitaltwin.basyx.aasregistry.feature.search;

import org.eclipse.digitaltwin.aas4j.v3.dataformat.core.DeserializationException;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.json.JsonDeserializer;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.Environment;
import org.eclipse.digitaltwin.basyx.aasregistry.model.*;
import org.eclipse.digitaltwin.basyx.aasregistry.service.storage.DescriptorFilter;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.eclipse.digitaltwin.basyx.http.Base64UrlEncodedIdentifier;
import org.eclipse.digitaltwin.basyx.http.pagination.Base64UrlEncodedCursor;
import org.eclipse.digitaltwin.basyx.querycore.query.model.AASQuery;
import org.eclipse.digitaltwin.basyx.querycore.query.model.QueryResponse;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.ResponseEntity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import static org.awaitility.Awaitility.await;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Tests for {@link org.eclipse.digitaltwin.basyx.aasregistry.feature.search.SearchAasRegistryStorage} feature
 *
 * @author zielstor, fried
 */
public class TestSearchAasRegistry {

	private static ConfigurableApplicationContext appContext;
	private static final String BASE_URL = "http://localhost:8080";
	private static SearchAasRegistryStorage storage;
	private static SearchAasRegistryApiHTTPController searchAPI;


	@BeforeClass
	public static void setUp() throws IOException, DeserializationException, InterruptedException {
		appContext = new SpringApplication(DummySearchAasRegistryComponent.class).run();
		storage = appContext.getBean(SearchAasRegistryStorage.class);
		searchAPI = appContext.getBean(SearchAasRegistryApiHTTPController.class);
		preloadAasdf();
		await().atMost(10, SECONDS).until(() ->
				!storage.getAllAasDescriptors(new PaginationInfo(0, ""), new DescriptorFilter(null, null)).getResult().isEmpty()
		);
	}

	@AfterClass
	public static void tearDown() {
		List<org.eclipse.digitaltwin.basyx.aasregistry.model.AssetAdministrationShellDescriptor> descriptors = storage.getAllAasDescriptors(PaginationInfo.NO_LIMIT, new DescriptorFilter(null, null)).getResult();

		descriptors.forEach(descriptor -> storage.removeAasDescriptor(descriptor.getId()));
		appContext.close();
	}

	@Test
	public void testRepo() throws FileNotFoundException, DeserializationException {
		File file = new File(Objects.requireNonNull(TestSearchAasRegistry.class.getResource("/query.json")).getFile());
		AASQuery query = queryFromFile(file);
		ResponseEntity<QueryResponse> result = searchAPI.queryAssetAdministrationShellDescriptors(100, new Base64UrlEncodedCursor(""), query);
		QueryResponse response = result.getBody();
		assert response != null;
		List<AssetAdministrationShellDescriptor> topHits = response.result.stream().map(o->(AssetAdministrationShellDescriptor)o).toList();
		Assert.assertEquals(2,topHits.size());
	}

	private static AASQuery queryFromFile(File file) throws FileNotFoundException, DeserializationException {
		JsonDeserializer deserializer = new JsonDeserializer();
		return deserializer.read(new FileInputStream(file), AASQuery.class);
	}

	private static Environment envFromFile(File file) throws FileNotFoundException, DeserializationException {
		JsonDeserializer deserializer = new JsonDeserializer();
		return deserializer.read(new FileInputStream(file), Environment.class);
	}

	private static void preloadAasdf() throws FileNotFoundException, DeserializationException {
		File file = new File(TestSearchAasRegistry.class.getResource("/Example-Full.json").getFile());
		Environment env = envFromFile(file);
		for(AssetAdministrationShell aas : env.getAssetAdministrationShells()) {
			Endpoint endpoint = new Endpoint("AAS-3.0", createProtocolInformation(aas.getId()));

			AssetAdministrationShellDescriptor descriptor = new AssetAdministrationShellDescriptor(aas.getId());
			descriptor.setEndpoints(List.of(endpoint));

			if(aas.getAdministration() != null) {
				AdministrativeInformation administration = new AdministrativeInformation();
				administration.setVersion(aas.getAdministration().getVersion());
				administration.setRevision(aas.getAdministration().getRevision());
				descriptor.setAdministration(administration);
			}
			descriptor.setIdShort(aas.getIdShort());

			List<LangStringTextType> descriptions = new ArrayList<>();
			for(org.eclipse.digitaltwin.aas4j.v3.model.LangStringTextType el : aas.getDescription()){
				LangStringTextType description = new LangStringTextType(el.getLanguage(), el.getText());
				descriptions.add(description);
			}

			List<LangStringNameType> displayNames = new ArrayList<>();
			for(org.eclipse.digitaltwin.aas4j.v3.model.LangStringNameType el : aas.getDisplayName()) {
				LangStringNameType displayName = new LangStringNameType(el.getLanguage(), el.getText());
				displayNames.add(displayName);
			}

			descriptor.setDescription(descriptions);
			descriptor.setDisplayName(displayNames);
			descriptor.setAssetKind(AssetKind.INSTANCE);

			storage.insertAasDescriptor(descriptor);
		}
	}

	private static ProtocolInformation createProtocolInformation(String shellId) {
		String href = String.format("%s/%s", BASE_URL + "/shells", Base64UrlEncodedIdentifier.encodeIdentifier(shellId));

		ProtocolInformation protocolInformation = new ProtocolInformation(href);
		protocolInformation.endpointProtocol(getProtocol(href));

		return protocolInformation;
	}

	private static String getProtocol(String endpoint) {
		try {
			return new URL(endpoint).getProtocol();
		} catch (MalformedURLException e) {
			throw new RuntimeException();
		}
	}

}

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

package org.eclipse.digitaltwin.basyx.submodelregistry.feature.search;

import org.eclipse.digitaltwin.aas4j.v3.dataformat.core.DeserializationException;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.json.JsonDeserializer;
import org.eclipse.digitaltwin.aas4j.v3.model.Environment;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.eclipse.digitaltwin.basyx.http.Base64UrlEncodedIdentifier;
import org.eclipse.digitaltwin.basyx.http.pagination.Base64UrlEncodedCursor;
import org.eclipse.digitaltwin.basyx.querycore.query.model.AASQuery;
import org.eclipse.digitaltwin.basyx.querycore.query.model.QueryResponse;
import org.eclipse.digitaltwin.basyx.submodelregistry.model.*;
import org.eclipse.digitaltwin.basyx.submodelregistry.service.storage.SubmodelRegistryStorage;
import org.junit.*;
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
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Tests for {@link SearchSubmodelRegistryStorage} feature
 *
 * @author danish
 */
public class TestSearchSubmodelRegistry {

	private static ConfigurableApplicationContext appContext;
	private static final String BASE_URL = "http://localhost:8080";
	public static String submodelRegistryBaseUrl = BASE_URL + "/submodel-descriptors";
	private static SubmodelRegistryStorage storage;
	private static SearchSubmodelRegistryApiHTTPController searchAPI;


	@BeforeClass
	public static void setUp() throws FileNotFoundException, IOException, DeserializationException {
		appContext = new SpringApplication(DummySearchSubmodelRegistryComponent.class).run(new String[] {});
		storage = appContext.getBean(SearchSubmodelRegistryStorage.class);
		searchAPI = appContext.getBean(SearchSubmodelRegistryApiHTTPController.class);
		preloadSmds();
	}

	@AfterClass
	public static void tearDown() {
		List<org.eclipse.digitaltwin.basyx.submodelregistry.model.SubmodelDescriptor> descriptors = storage.getAllSubmodelDescriptors(PaginationInfo.NO_LIMIT).getResult();

		descriptors.forEach(descriptor -> storage.removeSubmodelDescriptor(descriptor.getId()));
		appContext.close();
	}

	@Test
	public void testRepo() throws FileNotFoundException, DeserializationException {
		File file = new File(TestSearchSubmodelRegistry.class.getResource("/query.json").getFile());
		AASQuery query = queryFromFile(file);
		ResponseEntity<QueryResponse> result = searchAPI.querySubmodelDescriptors(100, new Base64UrlEncodedCursor(""), query);
		QueryResponse response = result.getBody();
		assert response != null;
		List<SubmodelDescriptor> topHits = response.result.stream().map(o->(SubmodelDescriptor)o).toList();
		Assert.assertEquals(4,topHits.size());
	}

	private static AASQuery queryFromFile(File file) throws FileNotFoundException, DeserializationException {
		JsonDeserializer deserializer = new JsonDeserializer();
		return deserializer.read(new FileInputStream(file), AASQuery.class);
	}

	private static Environment envFromFile(File file) throws FileNotFoundException, DeserializationException {
		JsonDeserializer deserializer = new JsonDeserializer();
		return deserializer.read(new FileInputStream(file), Environment.class);
	}

	private static void preloadSmds() throws FileNotFoundException, DeserializationException {
		File file = new File(TestSearchSubmodelRegistry.class.getResource("/Example-Full.json").getFile());
		Environment env = envFromFile(file);
		for(Submodel sm : env.getSubmodels()) {
			Endpoint endpoint = new Endpoint("AAS-3.0", createProtocolInformation(sm.getId()));
			SubmodelDescriptor descriptor = new SubmodelDescriptor(sm.getId(), Arrays.asList(endpoint));
			if(sm.getAdministration() != null) {
				AdministrativeInformation administration = new AdministrativeInformation();
				administration.setVersion(sm.getAdministration().getVersion());
				administration.setRevision(sm.getAdministration().getRevision());
				descriptor.setAdministration(administration);
			}
			descriptor.setIdShort(sm.getIdShort());

			List<LangStringTextType> descriptions = new ArrayList<>();

			for(org.eclipse.digitaltwin.aas4j.v3.model.LangStringTextType el : sm.getDescription()){
				LangStringTextType description = new LangStringTextType();
				description.setLanguage(el.getLanguage());
				description.setText(el.getText());
				descriptions.add(description);
			}

			List<LangStringNameType> displayNames = new ArrayList<>();
			for(org.eclipse.digitaltwin.aas4j.v3.model.LangStringNameType el : sm.getDisplayName()) {
				LangStringNameType displayName = new LangStringNameType();
				displayName.setLanguage(el.getLanguage());
				displayName.setText(el.getText());
				displayNames.add(displayName);
			}

			descriptor.setDescription(descriptions);
			descriptor.setDisplayName(displayNames);

			if(sm.getSemanticId() != null) {
				List<Key> keys = new ArrayList<>();
				for (org.eclipse.digitaltwin.aas4j.v3.model.Key key : sm.getSemanticId().getKeys()) {
					Key k = new Key();
					k.setType(KeyTypes.SUBMODEL);
					k.setValue(key.getValue());
					keys.add(k);
				}

				descriptor.setSemanticId(new Reference(ReferenceTypes.EXTERNALREFERENCE, keys));
			}

			storage.insertSubmodelDescriptor(descriptor);
		}
	}

	private static ProtocolInformation createProtocolInformation(String submodelId) {
		String href = String.format("%s/%s", BASE_URL + "/submodels", Base64UrlEncodedIdentifier.encodeIdentifier(submodelId));

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

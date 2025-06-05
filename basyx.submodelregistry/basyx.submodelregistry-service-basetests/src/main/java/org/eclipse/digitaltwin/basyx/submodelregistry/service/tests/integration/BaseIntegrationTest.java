/*******************************************************************************
 * Copyright (C) 2023 DFKI GmbH (https://www.dfki.de/en/web)
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
package org.eclipse.digitaltwin.basyx.submodelregistry.service.tests.integration;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.assertj.core.api.SoftAssertionsProvider.ThrowingRunnable;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.core.DeserializationException;
import org.eclipse.digitaltwin.basyx.kafka.KafkaAdapter;
import org.eclipse.digitaltwin.basyx.kafka.KafkaAdapters;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.ApiException;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.ApiResponse;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.api.SubmodelRegistryApi;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.model.AdministrativeInformation;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.model.DataSpecificationContent;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.model.DataSpecificationIec61360;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.model.DataTypeIec61360;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.model.EmbeddedDataSpecification;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.model.Endpoint;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.model.Extension;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.model.GetSubmodelDescriptorsResult;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.model.Key;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.model.KeyTypes;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.model.LangStringDefinitionTypeIec61360;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.model.LangStringNameType;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.model.LangStringPreferredNameTypeIec61360;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.model.LangStringShortNameTypeIec61360;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.model.LangStringTextType;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.model.LevelType;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.model.ProtocolInformation;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.model.ProtocolInformationSecurityAttributes;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.model.ProtocolInformationSecurityAttributes.TypeEnum;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.model.Reference;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.model.ReferenceTypes;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.model.ServiceDescription;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.model.ServiceDescription.ProfilesEnum;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.model.SubmodelDescriptor;
import org.eclipse.digitaltwin.basyx.submodelregistry.service.events.RegistryEvent;
import org.eclipse.digitaltwin.basyx.submodelregistry.service.events.RegistryEvent.EventType;
import org.eclipse.digitaltwin.basyx.submodelregistry.service.tests.TestResourcesLoader;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
public abstract class BaseIntegrationTest {

	private static final String LANG_DE_DE = "de-DE";

	private static final int BAD_REQUEST = 400;

	private static final String IDENTIFICATION_9 = "identification_9";

	private static final String IDENTIFICATION_7 = "identification_7";

	private static final int DELETE_ALL_TEST_INSTANCE_COUNT = 50;

	private static final int NO_CONTENT = 204;

	private static final int CREATED = 201;

	private static final int OK = 200;

	private static final int NOT_FOUND = 404;
	
	private static KafkaAdapter<RegistryEvent> adapter = KafkaAdapters.getAdapter("submodel-registry", RegistryEvent.class);


	@Value("${local.server.port}")
	private int port;

	private ObjectMapper mapper = new ObjectMapper();
	
	@Rule
	public TestResourcesLoader resourceLoader = new TestResourcesLoader(BaseIntegrationTest.class.getPackageName(), mapper);

	protected SubmodelRegistryApi api;
	
	
	@Before
	public void setUp() throws ApiException, InterruptedException, DeserializationException {
		adapter.skipMessages();
		initClient();
		cleanup();
		
	}
	
	@After
	public void tearDown() throws ApiException, InterruptedException, DeserializationException {
		cleanup();
	}

	protected void initClient() throws ApiException, InterruptedException {
		api = new SubmodelRegistryApi("http", "127.0.0.1", port);
	}
	
	protected void cleanup() throws ApiException, InterruptedException, DeserializationException {	
		adapter.assertNoAdditionalMessages();
		GetSubmodelDescriptorsResult result = api.getAllSubmodelDescriptors(null, null);
		for (SubmodelDescriptor eachDescriptor : result.getResult()) {
			api.deleteSubmodelDescriptorById(eachDescriptor.getId());
			assertThatEventWasSend(RegistryEvent.builder().id(eachDescriptor.getId()).type(EventType.SUBMODEL_UNREGISTERED).build());
		}
	}

	@Test
	public void whenGetDescription_thenDescriptionIsReturned() throws ApiException {
		ApiResponse<ServiceDescription> entity = api.getDescriptionWithHttpInfo();
		assertThat(entity.getStatusCode()).isEqualTo(OK);
		List<ProfilesEnum> profiles = entity.getData().getProfiles();
		assertThat(profiles).asList().hasSize(1);
		assertThat(profiles).asList().containsExactlyInAnyOrder(ProfilesEnum.SUBMODELREGISTRYSERVICESPECIFICATION_SSP_001);
	}

	@Test
	public void whenWritingParallel_transactionManagementWorks() throws ApiException, InterruptedException, DeserializationException {
		IntStream.iterate(0, i -> i + 1).limit(300).parallel().forEach(this::postSubmodel);		
		assertThat(api.getAllSubmodelDescriptors(null, null).getResult()).hasSize(300);
		for (int i = 0; i < 300; i++) {
			adapter.next();
		}
	}

	private void postSubmodel(int id) {
		SubmodelDescriptor descr = new SubmodelDescriptor().id(id + "").addEndpointsItem(defaultClientEndpoint());
		try {
			api.postSubmodelDescriptor(descr);
		} catch (ApiException e) {
			throw new RuntimeException(e);
		}
	}

	@Test
	public void whenDeleteAll_thenAllDescriptorsAreRemoved() throws ApiException, InterruptedException, DeserializationException {
		for (int i = 0; i < DELETE_ALL_TEST_INSTANCE_COUNT; i++) {
			SubmodelDescriptor descr = new SubmodelDescriptor();
			String id = "id_" + i;
			descr.setId(id);
			descr.addEndpointsItem(defaultClientEndpoint());
			ApiResponse<SubmodelDescriptor> response = api.postSubmodelDescriptorWithHttpInfo(descr);
			assertThat(response.getStatusCode()).isEqualTo(201);
			// we need a mapping here
			assertThatEventWasSend(
					RegistryEvent.builder().id(id).submodelDescriptor(new org.eclipse.digitaltwin.basyx.submodelregistry.model.SubmodelDescriptor(id, List.of(defaultServerEndpoint()))).type(EventType.SUBMODEL_REGISTERED).build());
		}
		List<SubmodelDescriptor> all = api.getAllSubmodelDescriptors(null, null).getResult();
		assertThat(all.size()).isEqualTo(DELETE_ALL_TEST_INSTANCE_COUNT);

		api.deleteAllSubmodelDescriptors();

		all = api.getAllSubmodelDescriptors(null, null).getResult();
		assertThat(all).isEmpty();

		HashSet<RegistryEvent> events = new HashSet<>();
		// we do not have a specific order, so read all events first
		for (int i = 0; i < DELETE_ALL_TEST_INSTANCE_COUNT; i++) {
			events.add(adapter.next());
		}
		for (int i = 0; i < DELETE_ALL_TEST_INSTANCE_COUNT; i++) {
			assertThat(events.remove(RegistryEvent.builder().id("id_" + i).type(EventType.SUBMODEL_UNREGISTERED).build())).isTrue();
		}
		adapter.assertNoAdditionalMessages();
	}

	@Test
	public void whenCreateAndDeleteDescriptors_thenAllDescriptorsAreRemoved() throws IOException, InterruptedException, TimeoutException, ApiException, DeserializationException {
		List<SubmodelDescriptor> deployed = initialize();
		List<SubmodelDescriptor> all = api.getAllSubmodelDescriptors(null, null).getResult();
		assertThat(all).containsExactlyInAnyOrderElementsOf(deployed);

		for (SubmodelDescriptor eachDescriptor : all) {
			deleteSubmodelDescriptor(eachDescriptor.getId());
		}

		all = api.getAllSubmodelDescriptors(null, null).getResult();
		assertThat(all).isEmpty();

		adapter.assertNoAdditionalMessages();
	}
	@Test
	public void whenInvalidInput_thenSuccessfullyValidated() throws Exception {
		initialize();
		assertThrowsApiException(() -> api.postSubmodelDescriptor(null), BAD_REQUEST);
		assertThrowsApiException(() -> api.putSubmodelDescriptorById(null, null), BAD_REQUEST);
		assertThrowsApiException(() -> api.putSubmodelDescriptorById("sm", null), BAD_REQUEST);
		SubmodelDescriptor descriptor = new SubmodelDescriptor();
		descriptor.setIdShort("shortId");
		assertThrowsApiException(() -> api.postSubmodelDescriptor(descriptor), BAD_REQUEST);

		descriptor.setId("identification");
		descriptor.addEndpointsItem(defaultClientEndpoint());
		int status = api.postSubmodelDescriptorWithHttpInfo(descriptor).getStatusCode();
		assertThat(status).isEqualTo(201);
		assertThatEventWasSend(RegistryEvent.builder().id(descriptor.getId())
				.submodelDescriptor(new org.eclipse.digitaltwin.basyx.submodelregistry.model.SubmodelDescriptor(descriptor.getId(), List.of(defaultServerEndpoint())).idShort(descriptor.getIdShort())).type(EventType.SUBMODEL_REGISTERED)
				.build());
	}

	@Test
	public void whenPutSubmodelDescriptorDifferentId_thenMoved() throws Exception {
		initialize();
		SubmodelDescriptor descr = new SubmodelDescriptor().id(IDENTIFICATION_9).addEndpointsItem(defaultClientEndpoint());
		ApiResponse<Void> putResult = api.putSubmodelDescriptorByIdWithHttpInfo(IDENTIFICATION_7, descr);
		assertThat(putResult.getStatusCode()).isEqualTo(NO_CONTENT);
		
		assertThatEventWasSend(new RegistryEvent(IDENTIFICATION_7, EventType.SUBMODEL_UNREGISTERED, null));
		assertThatEventWasSend(new RegistryEvent(descr.getId(), EventType.SUBMODEL_REGISTERED, convert(descr)));
		
		assertThrowsApiException(() -> api.getSubmodelDescriptorByIdWithHttpInfo(IDENTIFICATION_7), NOT_FOUND);
		ApiResponse<SubmodelDescriptor> getResult = api.getSubmodelDescriptorByIdWithHttpInfo(IDENTIFICATION_9);
		assertThat(getResult.getStatusCode()).isEqualTo(OK);
		assertThat(descr).isEqualTo(getResult.getData());
	}

	@Test
	public void whenPutUnknownSubmodelDescriptor_thenNotFound() throws Exception {
		initialize();
		SubmodelDescriptor descr = new SubmodelDescriptor().id("unknown").addEndpointsItem(defaultClientEndpoint());
		assertThrowsApiException(() -> api.putSubmodelDescriptorById(descr.getId(), descr), NOT_FOUND);

	}

	@Test
	public void whenUseSubmodelDescriptorPagination_thenUseRefetching() throws IOException, InterruptedException, TimeoutException, ApiException, DeserializationException {
		List<SubmodelDescriptor> postedDescriptors = initialize();
		List<SubmodelDescriptor> postedDescriptorsSorted = postedDescriptors.stream().sorted(Comparator.comparing(SubmodelDescriptor::getId)).collect(Collectors.toList());
		assertThat(postedDescriptors).hasSize(5);

		GetSubmodelDescriptorsResult result0 = api.getAllSubmodelDescriptors(2, null);
		List<SubmodelDescriptor> body0 = result0.getResult();
		assertThat(body0).hasSize(2);
		assertThat(postedDescriptorsSorted.get(0)).isEqualTo(body0.get(0));
		assertThat(postedDescriptorsSorted.get(1)).isEqualTo(body0.get(1));
		GetSubmodelDescriptorsResult result1 = api.getAllSubmodelDescriptors(2, result0.getPagingMetadata().getCursor());
		List<SubmodelDescriptor> body1 = result1.getResult();
		assertThat(body1).hasSize(2);
		assertThat(postedDescriptorsSorted.get(2)).isEqualTo(body1.get(0));
		assertThat(postedDescriptorsSorted.get(3)).isEqualTo(body1.get(1));
		GetSubmodelDescriptorsResult result2 = api.getAllSubmodelDescriptors(2, result1.getPagingMetadata().getCursor());
		List<SubmodelDescriptor> body2 = result2.getResult();
		assertThat(body2).hasSize(1);
		assertThat(postedDescriptorsSorted.get(4)).isEqualTo(body2.get(0));
	}

	@Test
	public void whenSendFullObjectStructure_ItemIsProcessedProperly() throws ApiException, JsonProcessingException, InterruptedException, DeserializationException {
		LangStringTextType dType = new LangStringTextType().language(LANG_DE_DE).text("description");
		LangStringNameType nType = new LangStringNameType().language(LANG_DE_DE).text("display");
		ProtocolInformation protInfo = new ProtocolInformation();
		protInfo.addEndpointProtocolVersionItem("23");
		protInfo.addSecurityAttributesItem(new ProtocolInformationSecurityAttributes().key("sec").type(TypeEnum.NONE).value("enabled"));
		protInfo.endpointProtocol("https").href("https://reference").subprotocol("sub").subprotocolBody("subBody").subprotocolBodyEncoding("UTF-8");
		Endpoint ep = new Endpoint()._interface("ep_interface").protocolInformation(protInfo);
		Reference reference = new Reference().addKeysItem(new Key().type(KeyTypes.FILE).value("./test.yml")).type(ReferenceTypes.EXTERNALREFERENCE);
		Extension ext = new Extension().addRefersToItem(reference).addSupplementalSemanticIdsItem(reference).name("ext1").semanticId(reference).value("val");
		DataSpecificationIec61360 dsContent = new DataSpecificationIec61360();
		dsContent.addDefinitionItem(new LangStringDefinitionTypeIec61360().language(LANG_DE_DE).text("def")).addPreferredNameItem(new LangStringPreferredNameTypeIec61360().language(LANG_DE_DE).text("prefName"))
				.addShortNameItem(new LangStringShortNameTypeIec61360().language(LANG_DE_DE).text("sn")).dataType(DataTypeIec61360.FILE).levelType(new LevelType().max(true).min(false).nom(false).typ(true)).sourceOfDefinition("sod")
				.symbol("$$");
		EmbeddedDataSpecification edSpec = new EmbeddedDataSpecification().dataSpecification(reference).dataSpecificationContent(new DataSpecificationContent(dsContent));
		AdministrativeInformation aInfo = new AdministrativeInformation().addEmbeddedDataSpecificationsItem(edSpec);
		SubmodelDescriptor sm = new SubmodelDescriptor().id("sm").id("short").addDescriptionItem(dType).addDisplayNameItem(nType).addEndpointsItem(ep).addExtensionsItem(ext).addSupplementalSemanticIdItem(reference);
		sm.setAdministration(aInfo);
		SubmodelDescriptor descr = api.postSubmodelDescriptor(sm);
		
		assertThat(descr).isEqualTo(sm);
		
		assertThatEventWasSend(new RegistryEvent(descr.getId(), EventType.SUBMODEL_REGISTERED, convert(sm)));
	}

	private org.eclipse.digitaltwin.basyx.submodelregistry.model.SubmodelDescriptor convert(SubmodelDescriptor sm) throws JsonProcessingException {
		String data = mapper.writerFor(SubmodelDescriptor.class).writeValueAsString(sm);
		return mapper.readerFor(org.eclipse.digitaltwin.basyx.submodelregistry.model.SubmodelDescriptor.class).readValue(data);
	}

	@Test
	public void whenPostSubmodelDescriptor_LocationIsReturned() throws ApiException, IOException, InterruptedException, DeserializationException {
		SubmodelDescriptor sm = new SubmodelDescriptor().id("https://sm.id").addEndpointsItem(defaultClientEndpoint());
		ApiResponse<SubmodelDescriptor> response = api.postSubmodelDescriptorWithHttpInfo(sm);
		assertThatEventWasSend(new RegistryEvent(sm.getId(), EventType.SUBMODEL_REGISTERED, convert(sm)));
		List<String> locations = response.getHeaders().get("Location");
		assertThat(locations).hasSize(1);
		String location = locations.get(0);
		
		String expectedSuffix = "/submodel-descriptors/aHR0cHM6Ly9zbS5pZA==";
		assertThat(location).endsWith(expectedSuffix);
		assertRestResourceAvailable(location);
	}

	private void assertRestResourceAvailable(String location) throws IOException {
		URL url = new URL(location);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("GET");
		int status = con.getResponseCode();
		assertThat(status).isEqualTo(200);			
	}	

	private void deleteSubmodelDescriptor(String submodelId) throws ApiException, InterruptedException, DeserializationException {
		
		int response = api.deleteSubmodelDescriptorByIdWithHttpInfo(submodelId).getStatusCode();
		assertThat(response).isEqualTo(NO_CONTENT);
		assertThatEventWasSend(RegistryEvent.builder().id(submodelId).type(EventType.SUBMODEL_UNREGISTERED).build());
	}

	private List<SubmodelDescriptor> initialize() throws IOException, InterruptedException, TimeoutException, ApiException, DeserializationException {
		List<SubmodelDescriptor> descriptors = resourceLoader.loadRepositoryDefinition(SubmodelDescriptor.class);
		List<org.eclipse.digitaltwin.basyx.submodelregistry.model.SubmodelDescriptor> repoContent = resourceLoader.loadRepositoryDefinition(org.eclipse.digitaltwin.basyx.submodelregistry.model.SubmodelDescriptor.class);

		for (int i = 0, len = descriptors.size(); i < len; i++) {
			SubmodelDescriptor eachDescriptor = descriptors.get(i);
			org.eclipse.digitaltwin.basyx.submodelregistry.model.SubmodelDescriptor eachEventDescriptor = repoContent.get(i);
			ApiResponse<SubmodelDescriptor> response = api.postSubmodelDescriptorWithHttpInfo(eachDescriptor);
			assertThat(response.getData()).isEqualTo(eachDescriptor);
			assertThat(response.getStatusCode()).isEqualTo(CREATED);
			assertThatEventWasSend(RegistryEvent.builder().id(eachDescriptor.getId()).submodelDescriptor(eachEventDescriptor).type(EventType.SUBMODEL_REGISTERED).build());
		}
		return descriptors;
	}

	private void assertThatEventWasSend(RegistryEvent build) throws InterruptedException, DeserializationException {
		RegistryEvent evt = adapter.next();
		assertThat(evt).isEqualTo(build);
	}

	public Endpoint defaultClientEndpoint() {
		ProtocolInformation protocolInfo = new ProtocolInformation().href("http://127.0.0.1:8099/submodel").endpointProtocol("HTTP").subprotocol("AAS");
		return new Endpoint()._interface("https://admin-shell.io/aas/API/3/0/SubmodelServiceSpecification/SSP-003").protocolInformation(protocolInfo);
	}
	
	public org.eclipse.digitaltwin.basyx.submodelregistry.model.Endpoint defaultServerEndpoint() {
		org.eclipse.digitaltwin.basyx.submodelregistry.model.ProtocolInformation protocolInfo = new org.eclipse.digitaltwin.basyx.submodelregistry.model.ProtocolInformation("http://127.0.0.1:8099/submodel").endpointProtocol("HTTP")
				.subprotocol("AAS");
		return new org.eclipse.digitaltwin.basyx.submodelregistry.model.Endpoint("https://admin-shell.io/aas/API/3/0/SubmodelServiceSpecification/SSP-003", protocolInfo);
	}

	public LangStringTextType description(String description) {
		return new LangStringTextType().language(LANG_DE_DE).text(description);
	}

	private void assertThrowsApiException(ThrowingRunnable runnable, int statusCode) throws Exception {
		try {
			runnable.run();
		} catch (ApiException ex) {
			assertThat(ex.getCode()).isEqualTo(statusCode);
		}
	}

}
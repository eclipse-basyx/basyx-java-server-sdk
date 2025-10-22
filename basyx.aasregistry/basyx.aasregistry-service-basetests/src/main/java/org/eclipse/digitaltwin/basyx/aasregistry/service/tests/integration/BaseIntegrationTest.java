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
package org.eclipse.digitaltwin.basyx.aasregistry.service.tests.integration;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeoutException;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.assertj.core.api.SoftAssertionsProvider.ThrowingRunnable;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.core.DeserializationException;
import org.eclipse.digitaltwin.basyx.aasregistry.client.ApiException;
import org.eclipse.digitaltwin.basyx.aasregistry.client.ApiResponse;
import org.eclipse.digitaltwin.basyx.aasregistry.client.api.RegistryAndDiscoveryInterfaceApi;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.AdministrativeInformation;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.AssetAdministrationShellDescriptor;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.AssetKind;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.DataSpecificationContent;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.DataSpecificationIec61360;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.DataTypeIec61360;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.EmbeddedDataSpecification;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.Endpoint;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.Extension;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.GetAssetAdministrationShellDescriptorsResult;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.GetSubmodelDescriptorsResult;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.Key;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.KeyTypes;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.LangStringDefinitionTypeIec61360;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.LangStringNameType;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.LangStringPreferredNameTypeIec61360;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.LangStringShortNameTypeIec61360;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.LangStringTextType;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.LevelType;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.Page;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.ProtocolInformation;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.ProtocolInformationSecurityAttributes;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.ProtocolInformationSecurityAttributes.TypeEnum;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.Reference;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.ReferenceTypes;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.ServiceDescription;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.ServiceDescription.ProfilesEnum;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.ShellDescriptorQuery;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.ShellDescriptorQuery.QueryTypeEnum;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.ShellDescriptorSearchRequest;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.ShellDescriptorSearchResponse;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.SortDirection;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.Sorting;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.SortingPath;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.SpecificAssetId;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.SubmodelDescriptor;
import org.eclipse.digitaltwin.basyx.aasregistry.paths.AasRegistryPaths;
import org.eclipse.digitaltwin.basyx.aasregistry.service.events.RegistryEvent;
import org.eclipse.digitaltwin.basyx.aasregistry.service.events.RegistryEvent.EventType;
import org.eclipse.digitaltwin.basyx.aasregistry.service.tests.TestResourcesLoader;
import org.eclipse.digitaltwin.basyx.kafka.KafkaAdapter;
import org.eclipse.digitaltwin.basyx.kafka.KafkaAdapters;
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

	private static final String SUBMODEL_0 = "submodel_0";

	private static final String SUBMODEL_9 = "submodel_9";

	private static final String IDENTIFICATION_9 = "identification_9";

	private static final String IDENTIFICATION_7 = "identification_7";

	private static final String IDENTIFICATION_5 = "identification_5";

	private static final int DELETE_ALL_TEST_INSTANCE_COUNT = 50;

	private static final int NO_CONTENT = 204;

	private static final int CREATED = 201;

	private static final int OK = 200;

	private static final int NOT_FOUND = 404;

	@Value("${local.server.port}")
	private int port;

	private ObjectMapper mapper = new ObjectMapper();
	

	private final KafkaAdapter<RegistryEvent> adapter = KafkaAdapters.getAdapter("aas-registry", RegistryEvent.class);
	
	@Rule
	public TestResourcesLoader resourceLoader = new TestResourcesLoader(BaseIntegrationTest.class.getPackageName(), mapper);

	protected RegistryAndDiscoveryInterfaceApi api;

	@Before
	public void setUp() throws Exception {
		initClient();
		adapter.skipMessages();
		cleanup();
		adapter.skipMessages();
	}
	
	
	protected void initClient() throws Exception {
		api = new RegistryAndDiscoveryInterfaceApi("http", "127.0.0.1", port);
	}

	protected void cleanup() throws ApiException, InterruptedException, DeserializationException {
		GetAssetAdministrationShellDescriptorsResult result = api.getAllAssetAdministrationShellDescriptors(null, null, null, null);
		for (AssetAdministrationShellDescriptor eachDescriptor : result.getResult()) {
			api.deleteAssetAdministrationShellDescriptorById(eachDescriptor.getId());
			assertThatEventWasSend(RegistryEvent.builder().id(eachDescriptor.getId()).type(EventType.AAS_UNREGISTERED).build());
		}
	}

	@Test
	public void whenGetDescription_thenDescriptionIsReturned() throws ApiException {
		ApiResponse<ServiceDescription> entity = api.getDescriptionWithHttpInfo();
		assertThat(entity.getStatusCode()).isEqualTo(OK);
		List<ProfilesEnum> profiles = entity.getData().getProfiles();
		assertThat(profiles).asList().hasSize(1);
		assertThat(profiles).asList().containsExactlyInAnyOrder(ProfilesEnum.ASSETADMINISTRATIONSHELLREGISTRYSERVICESPECIFICATION_SSP_001);
	}

	@Test
	public void whenWritingParallel_transactionManagementWorks() throws ApiException, JsonProcessingException {
		AssetAdministrationShellDescriptor descriptor = new AssetAdministrationShellDescriptor();
		descriptor.setId("descr");
		api.postAssetAdministrationShellDescriptor(descriptor);
		assertThatEventWasSend(RegistryEvent.builder().id(descriptor.getId()).aasDescriptor(convert(descriptor)).type(EventType.AAS_REGISTERED).build());
		IntFunction<Integer> op = idx -> writeSubModel(descriptor.getId(), idx);
		assertThat(IntStream.iterate(0, i -> i + 1).limit(300).parallel().mapToObj(op).filter(i -> i > 300).findAny()).isEmpty();
		assertThat(api.getAssetAdministrationShellDescriptorById(descriptor.getId()).getSubmodelDescriptors()).hasSize(300);
		for (int i = 0; i < 300; i++) {
			RegistryEvent evt = adapter.next();
			assertThat(evt.getId()).isEqualTo(descriptor.getId());
			assertThat(Integer.parseInt(evt.getSubmodelId())).isGreaterThanOrEqualTo(0).isLessThan(300);
			
		}
	}

	private Integer writeSubModel(String descriptorId, int idx) {
		SubmodelDescriptor sm = new SubmodelDescriptor();
		sm.setId(idx + "");
		Reference reference = new Reference();
		sm.setSemanticId(reference);
		if (idx % 2 == 0) {
			reference.setType(ReferenceTypes.EXTERNALREFERENCE);
			reference.addKeysItem(new Key().type(KeyTypes.PROPERTY).value("a"));
		} else {
			reference.setType(ReferenceTypes.MODELREFERENCE);
			reference.addKeysItem(new Key().type(KeyTypes.PROPERTY).value("aaa"));
		}
		ClientRegistryTestObjects.addDefaultEndpoint(sm);
		try {
			return api.postSubmodelDescriptorThroughSuperpathWithHttpInfo(descriptorId, sm).getStatusCode();
		} catch (ApiException ex) {
			return ex.getCode();
		}
	}

	@Test
	public void whenDeleteAll_thenAllDescriptorsAreRemoved() throws ApiException {

		for (int i = 0; i < DELETE_ALL_TEST_INSTANCE_COUNT; i++) {
			AssetAdministrationShellDescriptor descr = new AssetAdministrationShellDescriptor();
			String id = "id_" + i;
			descr.setId(id);
			ApiResponse<AssetAdministrationShellDescriptor> response = api.postAssetAdministrationShellDescriptorWithHttpInfo(descr);
			assertThat(response.getStatusCode()).isEqualTo(201);
			// we need a mapping here
			assertThatEventWasSend(RegistryEvent.builder().id(id).aasDescriptor(new org.eclipse.digitaltwin.basyx.aasregistry.model.AssetAdministrationShellDescriptor(id)).type(EventType.AAS_REGISTERED).build());
		}
		List<AssetAdministrationShellDescriptor> all = api.getAllAssetAdministrationShellDescriptors(null, null, null, null).getResult();
		assertThat(all.size()).isEqualTo(DELETE_ALL_TEST_INSTANCE_COUNT);

		api.deleteAllShellDescriptors();

		all = api.getAllAssetAdministrationShellDescriptors(null, null, null, null).getResult();
		assertThat(all).isEmpty();

		HashSet<RegistryEvent> events = new HashSet<>();
		// we do not have a specific order, so read all events first
		for (int i = 0; i < DELETE_ALL_TEST_INSTANCE_COUNT; i++) {
			events.add(adapter.next());
		}
		for (int i = 0; i < DELETE_ALL_TEST_INSTANCE_COUNT; i++) {
			assertThat(events.remove(RegistryEvent.builder().id("id_" + i).type(EventType.AAS_UNREGISTERED).build())).isTrue();
		}
		assertThat(events.isEmpty());
	}

	@Test
	public void whenCreateAndDeleteDescriptors_thenAllDescriptorsAreRemoved() throws IOException, InterruptedException, TimeoutException, ApiException, DeserializationException {
		List<AssetAdministrationShellDescriptor> deployed = initialize();
		List<AssetAdministrationShellDescriptor> all = api.getAllAssetAdministrationShellDescriptors(null, null, null, null).getResult();
		assertThat(all).containsExactlyInAnyOrderElementsOf(deployed);

		for (AssetAdministrationShellDescriptor eachDescriptor : all) {
			deleteAdminAssetShellDescriptor(eachDescriptor.getId());
		}

		all = api.getAllAssetAdministrationShellDescriptors(null, null, null, null).getResult();
		assertThat(all).isEmpty();

		adapter.assertNoAdditionalMessages();
	}

	@Test
	public void whenRegisterAndUnregisterSubmodel_thenSubmodelIsCreatedAndDeleted() throws IOException, InterruptedException, TimeoutException, ApiException, DeserializationException {
		List<AssetAdministrationShellDescriptor> deployed = initialize();
		List<AssetAdministrationShellDescriptor> all = api.getAllAssetAdministrationShellDescriptors(null, null, null, null).getResult();
		assertThat(all).asList().containsExactlyInAnyOrderElementsOf(deployed);

		SubmodelDescriptor toRegister = resourceLoader.load(SubmodelDescriptor.class, "toregister");
		String aasId = "identification_1";
		ApiResponse<SubmodelDescriptor> response = api.postSubmodelDescriptorThroughSuperpathWithHttpInfo(aasId, toRegister);

		assertThatEventWasSend(RegistryEvent.builder().id(aasId).submodelId(toRegister.getId()).submodelDescriptor(resourceLoader.load(org.eclipse.digitaltwin.basyx.aasregistry.model.SubmodelDescriptor.class, "toregister"))
				.type(EventType.SUBMODEL_REGISTERED).build());
		assertThat(response.getStatusCode()).isEqualTo(CREATED);
		SubmodelDescriptor registered = response.getData();
		assertThat(registered).isEqualTo(toRegister);

		SubmodelDescriptor resolved = api.getSubmodelDescriptorByIdThroughSuperpath(aasId, toRegister.getId());
		assertThat(resolved).isEqualTo(registered);

		AssetAdministrationShellDescriptor aasDescriptor = api.getAssetAdministrationShellDescriptorById(aasId);
		assertThat(aasDescriptor.getSubmodelDescriptors()).contains(toRegister);

		ApiResponse<Void> deleteResponse = api.deleteSubmodelDescriptorByIdThroughSuperpathWithHttpInfo(aasId, toRegister.getId());
		assertThat(deleteResponse.getStatusCode()).isEqualTo(NO_CONTENT);

		assertThatEventWasSend(RegistryEvent.builder().id(aasId).submodelId(toRegister.getId()).type(EventType.SUBMODEL_UNREGISTERED).build());

		aasDescriptor = api.getAssetAdministrationShellDescriptorById(aasId);
		assertThat(aasDescriptor.getSubmodelDescriptors()).doesNotContain(toRegister);
		adapter.assertNoAdditionalMessages();
	}

	@Test
	public void whenInvalidInput_thenSuccessfullyValidated() throws Exception {
		initialize();
		assertThrowsApiException(() -> api.deleteSubmodelDescriptorByIdThroughSuperpathWithHttpInfo(null, null), BAD_REQUEST);
		assertThrowsApiException(() -> api.deleteAssetAdministrationShellDescriptorById(null), BAD_REQUEST);
		assertThrowsApiException(() -> api.getAllSubmodelDescriptorsThroughSuperpath(null, null, null), BAD_REQUEST);
		assertThrowsApiException(() -> api.getAssetAdministrationShellDescriptorById(null), BAD_REQUEST);
		assertThrowsApiException(() -> api.putAssetAdministrationShellDescriptorById(null, null), BAD_REQUEST);
		assertThrowsApiException(() -> api.postAssetAdministrationShellDescriptor(null), BAD_REQUEST);
		assertThrowsApiException(() -> api.postSubmodelDescriptorThroughSuperpath(null, null), BAD_REQUEST);

		AssetAdministrationShellDescriptor descriptor = new AssetAdministrationShellDescriptor();
		descriptor.setIdShort("shortId");
		assertThrowsApiException(() -> api.postAssetAdministrationShellDescriptor(descriptor), BAD_REQUEST);

		descriptor.setId("identification");
		int status = api.postAssetAdministrationShellDescriptorWithHttpInfo(descriptor).getStatusCode();
		assertThat(status).isEqualTo(201);
		assertThatEventWasSend(RegistryEvent.builder().id(descriptor.getId()).aasDescriptor(new org.eclipse.digitaltwin.basyx.aasregistry.model.AssetAdministrationShellDescriptor(descriptor.getId()).idShort(descriptor.getIdShort()))
				.type(EventType.AAS_REGISTERED).build());
	}

	@Test
	public void whenMatchSearchBySubmodelDescriptorId_thenGotResult() throws IOException, InterruptedException, TimeoutException, ApiException {
		initialize();
		AssetAdministrationShellDescriptor expected = resourceLoader.load(AssetAdministrationShellDescriptor.class);
		String path = AasRegistryPaths.submodelDescriptors().idShort();
		ShellDescriptorSearchRequest request = new ShellDescriptorSearchRequest().query(new ShellDescriptorQuery().queryType(QueryTypeEnum.MATCH).path(path).value("sm3"));
		ApiResponse<ShellDescriptorSearchResponse> response = api.searchShellDescriptorsWithHttpInfo(request);

		assertThat(response.getStatusCode()).isEqualTo(OK);
		List<AssetAdministrationShellDescriptor> result = response.getData().getHits();
		assertThat(result.size()).isEqualTo(1);
		assertThat(result.get(0)).isEqualTo(expected);
	}

	@Test
	public void whenRegexSearchBySubmodelDescriptorShortId_thenGotResult() throws IOException, InterruptedException, TimeoutException, ApiException {
		initialize();
		AssetAdministrationShellDescriptor expected = resourceLoader.load(AssetAdministrationShellDescriptor.class);
		String path = AasRegistryPaths.submodelDescriptors().idShort();
		ShellDescriptorSearchRequest request = new ShellDescriptorSearchRequest().query(new ShellDescriptorQuery().queryType(QueryTypeEnum.REGEX).path(path).value("[st]{1}.*3"));
		ApiResponse<ShellDescriptorSearchResponse> response = api.searchShellDescriptorsWithHttpInfo(request);

		assertThat(response.getStatusCode()).isEqualTo(OK);
		List<AssetAdministrationShellDescriptor> result = response.getData().getHits();
		assertThat(result.size()).isEqualTo(1);
		assertThat(result.get(0)).isEqualTo(expected);
	}

	@Test
	public void whenPutShellDescriptorDifferentId_thenMoved() throws Exception {
		initialize();
		AssetAdministrationShellDescriptor descr = new AssetAdministrationShellDescriptor().id(IDENTIFICATION_9);
		ApiResponse<Void> putResult = api.putAssetAdministrationShellDescriptorByIdWithHttpInfo(IDENTIFICATION_7, descr);
		assertThat(putResult.getStatusCode()).isEqualTo(NO_CONTENT);
		assertThatEventWasSend(RegistryEvent.builder().type(EventType.AAS_UNREGISTERED).id(IDENTIFICATION_7).build());
		assertThatEventWasSend(RegistryEvent.builder().type(EventType.AAS_REGISTERED).id(IDENTIFICATION_9).aasDescriptor(convert(descr)).build());
		assertThrowsApiException(() -> api.getAssetAdministrationShellDescriptorByIdWithHttpInfo(IDENTIFICATION_7), NOT_FOUND);
		ApiResponse<AssetAdministrationShellDescriptor> getResult = api.getAssetAdministrationShellDescriptorByIdWithHttpInfo(IDENTIFICATION_9);
		assertThat(getResult.getStatusCode()).isEqualTo(OK);
		assertThat(descr).isEqualTo(getResult.getData());
	}

	@Test
	public void whenPutSubmodelDifferentId_thenMoved() throws Exception {
		initialize();
		SubmodelDescriptor descr = new SubmodelDescriptor().id(SUBMODEL_9).addEndpointsItem(defaultEndpoint());
		ApiResponse<Void> putResult = api.putSubmodelDescriptorByIdThroughSuperpathWithHttpInfo(IDENTIFICATION_5, SUBMODEL_0, descr);
		assertThatEventWasSend(new RegistryEvent(IDENTIFICATION_5, SUBMODEL_0, EventType.SUBMODEL_UNREGISTERED, null, null));;
		assertThatEventWasSend(new RegistryEvent(IDENTIFICATION_5, SUBMODEL_9, EventType.SUBMODEL_REGISTERED, null, convert(descr)));
		assertThat(putResult.getStatusCode()).isEqualTo(NO_CONTENT);

		assertThrowsApiException(() -> api.getSubmodelDescriptorByIdThroughSuperpath(IDENTIFICATION_5, SUBMODEL_0), NOT_FOUND);
		ApiResponse<SubmodelDescriptor> getResult = api.getSubmodelDescriptorByIdThroughSuperpathWithHttpInfo(IDENTIFICATION_5, SUBMODEL_9);
		assertThat(getResult.getStatusCode()).isEqualTo(OK);
		assertThat(descr).isEqualTo(getResult.getData());
	}

	
	@Test
	public void whenPutShellDescriptorSameId_thenUpdated() throws IOException, InterruptedException, TimeoutException, ApiException {
		initialize();
		AssetAdministrationShellDescriptor descr = new AssetAdministrationShellDescriptor().id(IDENTIFICATION_5);
		ApiResponse<Void> putResult = api.putAssetAdministrationShellDescriptorByIdWithHttpInfo(descr.getId(), descr);
		assertThat(putResult.getStatusCode()).isEqualTo(NO_CONTENT);
		assertThatEventWasSend(RegistryEvent.builder().id(IDENTIFICATION_5).aasDescriptor(convert(descr)).type(EventType.AAS_REGISTERED).build());
		
		ApiResponse<AssetAdministrationShellDescriptor> getResult = api.getAssetAdministrationShellDescriptorByIdWithHttpInfo(descr.getId());
		assertThat(getResult.getStatusCode()).isEqualTo(OK);
		assertThat(descr).isEqualTo(getResult.getData());
	}

	@Test
	public void whenPutSubmodelSameId_thenUpdated() throws IOException, InterruptedException, TimeoutException, ApiException {
		initialize();
		SubmodelDescriptor descr = new SubmodelDescriptor().id(SUBMODEL_0).addEndpointsItem(defaultEndpoint());
		ApiResponse<Void> putResult = api.putSubmodelDescriptorByIdThroughSuperpathWithHttpInfo(IDENTIFICATION_5, descr.getId(), descr);
		assertThat(putResult.getStatusCode()).isEqualTo(NO_CONTENT);
		assertThatEventWasSend(RegistryEvent.builder().id(IDENTIFICATION_5).type(EventType.SUBMODEL_REGISTERED).submodelId(descr.getId()).submodelDescriptor(convert(descr)).build());

		ApiResponse<SubmodelDescriptor> getResult = api.getSubmodelDescriptorByIdThroughSuperpathWithHttpInfo(IDENTIFICATION_5, descr.getId());
		assertThat(getResult.getStatusCode()).isEqualTo(OK);
		assertThat(descr).isEqualTo(getResult.getData());
	}

	@Test
	public void whenPutUnknownShellDescriptor_thenNotFound() throws Exception {
		initialize();
		AssetAdministrationShellDescriptor descr = new AssetAdministrationShellDescriptor().id("unknown");
		assertThrowsApiException(() -> api.putAssetAdministrationShellDescriptorById(descr.getId(), descr), NOT_FOUND);

	}

	@Test
	public void whenPutUnknownSubmodel_thenNotFound() throws Exception {
		initialize();
		SubmodelDescriptor descr = new SubmodelDescriptor().id(SUBMODEL_0).addDescriptionItem(description("test")).addEndpointsItem(defaultEndpoint());
		assertThrowsApiException(() -> api.putSubmodelDescriptorByIdThroughSuperpathWithHttpInfo(IDENTIFICATION_5, "unknown", descr), NOT_FOUND);
	}

	@Test
	public void whenUseDescriptorPagination_thenUseRefetching() throws IOException, InterruptedException, TimeoutException, ApiException {
		List<AssetAdministrationShellDescriptor> postedDescriptors = initialize();
		List<AssetAdministrationShellDescriptor> postedDescriptorsSorted = postedDescriptors.stream().sorted(Comparator.comparing(AssetAdministrationShellDescriptor::getId)).collect(Collectors.toList());
		assertThat(postedDescriptors).hasSize(5);

		GetAssetAdministrationShellDescriptorsResult result0 = api.getAllAssetAdministrationShellDescriptors(2, null, null, null);
		List<AssetAdministrationShellDescriptor> body0 = result0.getResult();
		assertThat(body0).hasSize(2);
		assertThat(postedDescriptorsSorted.get(0)).isEqualTo(body0.get(0));
		assertThat(postedDescriptorsSorted.get(1)).isEqualTo(body0.get(1));
		GetAssetAdministrationShellDescriptorsResult result1 = api.getAllAssetAdministrationShellDescriptors(2, result0.getPagingMetadata().getCursor(), null, null);
		List<AssetAdministrationShellDescriptor> body1 = result1.getResult();
		assertThat(body1).hasSize(2);
		assertThat(postedDescriptorsSorted.get(2)).isEqualTo(body1.get(0));
		assertThat(postedDescriptorsSorted.get(3)).isEqualTo(body1.get(1));
		GetAssetAdministrationShellDescriptorsResult result2 = api.getAllAssetAdministrationShellDescriptors(2, result1.getPagingMetadata().getCursor(), null, null);
		List<AssetAdministrationShellDescriptor> body2 = result2.getResult();
		assertThat(body2).hasSize(1);
		assertThat(postedDescriptorsSorted.get(4)).isEqualTo(body2.get(0));

	}

	@Test
	public void whenUseDescriptorFilter_thenFiltered() throws IOException, InterruptedException, TimeoutException, ApiException {
		List<AssetAdministrationShellDescriptor> postedDescriptors = initialize();
		assertThat(postedDescriptors).hasSize(5);

		GetAssetAdministrationShellDescriptorsResult result = api.getAllAssetAdministrationShellDescriptors(null, null, null, null);
		assertThat(result.getResult()).hasSize(5);

		result = api.getAllAssetAdministrationShellDescriptors(null, null, AssetKind.TYPE, "tp");
		assertThat(result.getResult()).hasSize(2);
		List<String> aasIds = result.getResult().stream().map(AssetAdministrationShellDescriptor::getId).collect(Collectors.toList());

		assertThat(aasIds).contains(IDENTIFICATION_7, IDENTIFICATION_5);

	}

	@Test
	public void whenUseSubmodelPagination_thenUseRefetching() throws IOException, InterruptedException, TimeoutException, ApiException {
		List<AssetAdministrationShellDescriptor> postedDescriptors = initialize();
		List<SubmodelDescriptor> postedDescriptorsSorted = postedDescriptors.stream().filter(a -> IDENTIFICATION_5.equals(a.getId())).map(AssetAdministrationShellDescriptor::getSubmodelDescriptors).filter(Objects::nonNull)
				.flatMap(List::stream).sorted(Comparator.comparing(SubmodelDescriptor::getId)).collect(Collectors.toList());

		assertThat(postedDescriptorsSorted).hasSize(4);

		GetSubmodelDescriptorsResult result0 = api.getAllSubmodelDescriptorsThroughSuperpath(IDENTIFICATION_5, 2, null);
		List<SubmodelDescriptor> body0 = result0.getResult();
		assertThat(body0).hasSize(2);
		assertThat(postedDescriptorsSorted.get(0)).isEqualTo(body0.get(0));
		assertThat(postedDescriptorsSorted.get(1)).isEqualTo(body0.get(1));
		GetSubmodelDescriptorsResult result1 = api.getAllSubmodelDescriptorsThroughSuperpath(IDENTIFICATION_5, 2, result0.getPagingMetadata().getCursor());
		List<SubmodelDescriptor> body1 = result1.getResult();
		assertThat(body1).hasSize(2);
		assertThat(postedDescriptorsSorted.get(2)).isEqualTo(body1.get(0));
		assertThat(postedDescriptorsSorted.get(3)).isEqualTo(body1.get(1));
	}

	@Test
	public void whenUsePagination_thenUseRefetching() throws IOException, InterruptedException, TimeoutException, ApiException {
		initialize();

		List<AssetAdministrationShellDescriptor> expected = resourceLoader.loadList(AssetAdministrationShellDescriptor.class);

		assertResultByPage(0, expected);
		assertResultByPage(1, expected);
		assertResultByPage(2, expected);
		assertResultByPage(3, expected);
	}

	private void assertResultByPage(int from, List<AssetAdministrationShellDescriptor> expected) throws ApiException {
		ShellDescriptorSearchRequest request = new ShellDescriptorSearchRequest().sortBy(new Sorting().addPathItem(SortingPath.IDSHORT).addPathItem(SortingPath.ID).direction(SortDirection.ASC)).page(new Page().index(from).size(2));
		ShellDescriptorSearchResponse response = api.searchShellDescriptors(request);
		int total = 5;
		assertThat(response.getTotal()).isEqualTo(total);
		List<AssetAdministrationShellDescriptor> hits = response.getHits();
		int position = from * 2;
		if (position < total) {
			AssetAdministrationShellDescriptor hit0 = hits.get(0);
			AssetAdministrationShellDescriptor expected0 = expected.get(position);
			assertThat(hit0).isEqualTo(expected0);
		} else {
			assertThat(hits).isEmpty();
		}
		position++;
		if (position < total) {
			AssetAdministrationShellDescriptor hit1 = hits.get(1);
			AssetAdministrationShellDescriptor expected1 = expected.get(position);
			assertThat(hit1).isEqualTo(expected1);
		}
	}

	@Test
	public void whenSearchWithSortingByIdShortAsc_thenReturnSortedAsc() throws IOException, InterruptedException, TimeoutException, ApiException {
		whenSearchWithSortingByIdShort_thenReturnSorted(SortDirection.ASC);
	}

	@Test
	public void whenSearchWithSortingByIdNoSortOrder_thenReturnSortedAsc() throws IOException, InterruptedException, TimeoutException, ApiException {
		whenSearchWithSortingByIdShort_thenReturnSorted(null);
	}

	@Test
	public void whenSearchWithSortingByIdShortDesc_thenReturnSortedDesc() throws IOException, InterruptedException, TimeoutException, ApiException {
		whenSearchWithSortingByIdShort_thenReturnSorted(SortDirection.DESC);
	}

	private void whenSearchWithSortingByIdShort_thenReturnSorted(SortDirection direction) throws IOException, InterruptedException, TimeoutException, ApiException {
		initialize();
		List<AssetAdministrationShellDescriptor> expected = resourceLoader.loadList(AssetAdministrationShellDescriptor.class);
		String path = AasRegistryPaths.description().language();
		ShellDescriptorSearchRequest request = new ShellDescriptorSearchRequest().query(new ShellDescriptorQuery().queryType(QueryTypeEnum.MATCH).path(path).value(LANG_DE_DE))
				.sortBy(new Sorting().addPathItem(SortingPath.IDSHORT).addPathItem(SortingPath.ADMINISTRATION_REVISION).direction(direction));
		ApiResponse<ShellDescriptorSearchResponse> response = api.searchShellDescriptorsWithHttpInfo(request);
		assertThat(response.getStatusCode()).isEqualTo(OK);
		List<AssetAdministrationShellDescriptor> result = response.getData().getHits();
		assertThat(result.toString()).isEqualTo(expected.toString());
		assertThat(result).asList().isEqualTo(expected);
	}

	@Test
	public void whenIllegalArguments_thenResult() throws IOException, InterruptedException, TimeoutException, ApiException {
		initialize();
		api.searchShellDescriptors(new ShellDescriptorSearchRequest());
	}

	@Test 
	public void whenInvalidExtensionSettings_thenException() throws Exception {
		initialize();
		ShellDescriptorQuery query1 = new ShellDescriptorQuery().extensionName("TAG").path(AasRegistryPaths.extensions().value()).value("A");
		ShellDescriptorQuery query2 = new ShellDescriptorQuery().extensionName("TAG").path(AasRegistryPaths.submodelDescriptors().endpoints().protocolInformation().endpointProtocolVersion()).value("B");
		
		ShellDescriptorSearchRequest request = new ShellDescriptorSearchRequest().query(query1.combinedWith(query2));
		assertThrowsApiException(()->api.searchShellDescriptors(request), BAD_REQUEST);
	}

	@Test
	public void whenSendFullObjectStructure_ItemIsProcessedProperly() throws ApiException, JsonProcessingException {
		LangStringTextType dType = new LangStringTextType().language(LANG_DE_DE).text("description");
		LangStringNameType nType = new LangStringNameType().language(LANG_DE_DE).text("display");
		ProtocolInformation protInfo = new ProtocolInformation();
		protInfo.addEndpointProtocolVersionItem("23");
		protInfo.addSecurityAttributesItem(new ProtocolInformationSecurityAttributes().key("sec").type(TypeEnum.NONE).value("enabled"));
		protInfo.endpointProtocol("https").href("https://reference").subprotocol("sub").subprotocolBody("subBody").subprotocolBodyEncoding("UTF-8");
		Endpoint ep = new Endpoint()._interface("ep_interface").protocolInformation(protInfo);
		Reference reference = new Reference().addKeysItem(new Key().type(KeyTypes.FILE).value("./test.yml")).type(ReferenceTypes.EXTERNALREFERENCE);
		Extension ext = new Extension().addRefersToItem(reference).addSupplementalSemanticIdsItem(reference).name("ext1").semanticId(reference).value("val");
		SpecificAssetId saId = new SpecificAssetId().addSupplementalSemanticIdsItem(reference).externalSubjectId(reference).name("said").semanticId(reference).value("value");
		DataSpecificationIec61360 dsContent = new DataSpecificationIec61360();
		dsContent.addDefinitionItem(new LangStringDefinitionTypeIec61360().language(LANG_DE_DE).text("def")).addPreferredNameItem(new LangStringPreferredNameTypeIec61360().language(LANG_DE_DE).text("prefName"))
				.addShortNameItem(new LangStringShortNameTypeIec61360().language(LANG_DE_DE).text("sn")).dataType(DataTypeIec61360.FILE).levelType(new LevelType().max(true).min(false).nom(false).typ(true)).sourceOfDefinition("sod")
				.symbol("$$");
		EmbeddedDataSpecification edSpec = new EmbeddedDataSpecification().dataSpecification(reference).dataSpecificationContent(new DataSpecificationContent(dsContent));

		AdministrativeInformation aInfo = new AdministrativeInformation().addEmbeddedDataSpecificationsItem(edSpec);
		SubmodelDescriptor sm = new SubmodelDescriptor().id("sm").id("short").addDescriptionItem(dType).addDisplayNameItem(nType).addEndpointsItem(ep).addExtensionsItem(ext).addSupplementalSemanticIdItem(reference);
		AssetAdministrationShellDescriptor descriptor = new AssetAdministrationShellDescriptor().id("id1").id("short").addDescriptionItem(dType).addDisplayNameItem(nType).addEndpointsItem(ep).addExtensionsItem(ext)
				.addSpecificAssetIdsItem(saId).administration(aInfo).assetKind(AssetKind.TYPE).assetType("tp1").globalAssetId("global1").addSubmodelDescriptorsItem(sm);

		AssetAdministrationShellDescriptor descr = api.postAssetAdministrationShellDescriptor(descriptor);
		assertThat(descr).isEqualTo(descriptor); 
		RegistryEvent evt = RegistryEvent.builder().id(descriptor.getId()).aasDescriptor(convert(descriptor)).type(EventType.AAS_REGISTERED).build();
		assertThatEventWasSend(evt);
	}
	
	@Test
	public void whenPostShellDescriptor_LocationIsReturned() throws ApiException, IOException {
		AssetAdministrationShellDescriptor descr = new AssetAdministrationShellDescriptor().id("https://test.id");
		ApiResponse<AssetAdministrationShellDescriptor>  response = api.postAssetAdministrationShellDescriptorWithHttpInfo(descr);
		List<String> locations = response.getHeaders().get("Location");
		assertThat(locations).hasSize(1);
		assertThatEventWasSend(RegistryEvent.builder().id(descr.getId()).aasDescriptor(convert(descr)).type(EventType.AAS_REGISTERED).build());
		String location = locations.get(0);
		
		String expectedSuffix = "/shell-descriptors/aHR0cHM6Ly90ZXN0Lmlk";
		assertThat(location).endsWith(expectedSuffix);
		assertRestResourceAvailable(location);
	}
	
	@Test
	public void whenPostSubmodelDescriptor_LocationIsReturned() throws ApiException, IOException {
		AssetAdministrationShellDescriptor shell = new AssetAdministrationShellDescriptor().id("https://shell.id");
		api.postAssetAdministrationShellDescriptor(shell);
		
		assertThatEventWasSend(RegistryEvent.builder().id(shell.getId()).aasDescriptor(convert(shell)).type(EventType.AAS_REGISTERED).build());
		
		SubmodelDescriptor sm =  new SubmodelDescriptor().id("https://sm.id").addEndpointsItem(defaultEndpoint());
		ApiResponse<SubmodelDescriptor>  response = api.postSubmodelDescriptorThroughSuperpathWithHttpInfo(shell.getId(), sm);
		List<String> locations = response.getHeaders().get("Location");
		assertThat(locations).hasSize(1);
		String location = locations.get(0);
		
		assertThatEventWasSend(RegistryEvent.builder().id(shell.getId()).submodelId(sm.getId()).submodelDescriptor(convert(sm)).type(EventType.SUBMODEL_REGISTERED).build());
		
		String expectedSuffix = "/shell-descriptors/aHR0cHM6Ly9zaGVsbC5pZA==/submodel-descriptors/aHR0cHM6Ly9zbS5pZA==";
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
	
	private void deleteAdminAssetShellDescriptor(String aasId) throws ApiException {
		adapter.assertNoAdditionalMessages();

		int response = api.deleteAssetAdministrationShellDescriptorByIdWithHttpInfo(URLEncoder.encode(aasId, StandardCharsets.UTF_8)).getStatusCode();
		assertThat(response).isEqualTo(NO_CONTENT);
		assertThatEventWasSend(RegistryEvent.builder().id(aasId).type(EventType.AAS_UNREGISTERED).build());
	}

	private List<AssetAdministrationShellDescriptor> initialize() throws IOException, TimeoutException, ApiException {
		List<AssetAdministrationShellDescriptor> descriptors = resourceLoader.loadRepositoryDefinition(AssetAdministrationShellDescriptor.class);
		List<org.eclipse.digitaltwin.basyx.aasregistry.model.AssetAdministrationShellDescriptor> eventContent = resourceLoader
				.loadRepositoryDefinition(org.eclipse.digitaltwin.basyx.aasregistry.model.AssetAdministrationShellDescriptor.class);

		for (int i = 0, len = descriptors.size(); i < len; i++) {
			AssetAdministrationShellDescriptor eachDescriptor = descriptors.get(i);
			org.eclipse.digitaltwin.basyx.aasregistry.model.AssetAdministrationShellDescriptor eachEventDescriptor = eventContent.get(i);
			ApiResponse<AssetAdministrationShellDescriptor> response = api.postAssetAdministrationShellDescriptorWithHttpInfo(eachDescriptor);
			assertThat(response.getData()).isEqualTo(eachDescriptor);
			assertThat(response.getStatusCode()).isEqualTo(CREATED);
			assertThatEventWasSend(RegistryEvent.builder().id(eachDescriptor.getId()).aasDescriptor(eachEventDescriptor).type(EventType.AAS_REGISTERED).build());
		}
		return descriptors;
	}

	private void assertThatEventWasSend(RegistryEvent expected) {
		RegistryEvent evt = adapter.next();
		assertThat(evt).isEqualTo(expected);
	}

	public Endpoint defaultEndpoint() {
		ProtocolInformation protocolInfo = new ProtocolInformation().href("http://127.0.0.1:8099/submodel").endpointProtocol("HTTP").subprotocol("AAS");
		return new Endpoint()._interface("https://admin-shell.io/aas/API/3/0/SubmodelServiceSpecification/SSP-003").protocolInformation(protocolInfo);
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

	private  org.eclipse.digitaltwin.basyx.aasregistry.model.SubmodelDescriptor convert(SubmodelDescriptor clientSm) throws JsonProcessingException {
		return convert(clientSm, SubmodelDescriptor.class, org.eclipse.digitaltwin.basyx.aasregistry.model.SubmodelDescriptor.class);
	}
	
	private  org.eclipse.digitaltwin.basyx.aasregistry.model.AssetAdministrationShellDescriptor convert(AssetAdministrationShellDescriptor clientShellDescr) throws JsonProcessingException {
		return convert(clientShellDescr, AssetAdministrationShellDescriptor.class, org.eclipse.digitaltwin.basyx.aasregistry.model.AssetAdministrationShellDescriptor.class);
	}
	
	
	private <O,I> O convert(I in, Class<I> inCls, Class<O> outCls) throws JsonProcessingException {
		String data = mapper.writerFor(inCls).writeValueAsString(in);
		return mapper.readerFor(outCls).readValue(data);
	}

}
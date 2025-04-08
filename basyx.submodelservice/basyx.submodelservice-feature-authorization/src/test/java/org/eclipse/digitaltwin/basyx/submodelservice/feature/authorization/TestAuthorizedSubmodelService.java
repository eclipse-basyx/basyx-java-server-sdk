/*******************************************************************************
 * Copyright (C) 2025 the Eclipse BaSyx Authors
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

package org.eclipse.digitaltwin.basyx.submodelservice.feature.authorization;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;

import org.eclipse.digitaltwin.basyx.authorization.AccessTokenProvider;
import org.eclipse.digitaltwin.basyx.authorization.DummyCredential;
import org.eclipse.digitaltwin.basyx.authorization.DummyCredentialStore;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.UseMainMethod;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.ResourceUtils;

/**
 * Integration test for {@link AuthorizedSubmodelService} feature
 * 
 * @author Gerhard Sonnenberg ( DFKI GmbH )
 */
@SpringBootTest(webEnvironment = WebEnvironment.MOCK, useMainMethod = UseMainMethod.NEVER, classes = SubmodelServiceTestConfiguration.class, properties = { "server.port=8081", "basyx.backend=InMemory",
		"basyx.feature.authorization.enabled=true", "basyx.feature.authorization.type=rbac", "basyx.feature.authorization.jwtBearerTokenProvider=keycloak", "basyx.feature.authorization.rbac.file=classpath:rbac_rules.json",
		"spring.security.oauth2.resourceserver.jwt.issuer-uri=http://localhost:9096/realms/BaSyx" })
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class TestAuthorizedSubmodelService {

	private static final String HEADER_AUTH = "Authorization";
	private static final String EP_HEALTH = "/actuator/health";
	private static final String EP_SUBMODEL = "/submodel";
	private static final String EP_SUBMODEL_VALUE = "/submodel/$value";
	private static final String EP_ALL_SMES = "/submodel/submodel-elements";
	private static final String EP_SME_1 = "/submodel/submodel-elements/SME_1";
	private static final String EP_SME_1_VALUE = "/submodel/submodel-elements/SME_1/$value";
	private static final String EP_SME_2 = "/submodel/submodel-elements/SME_2";
	private static final String EP_SME_2_1 = "/submodel/submodel-elements/SME_2.SME_2_1";
	private static final String EP_SME_4 = "/submodel/submodel-elements/SME_4";
	private static final String EP_SME_4_VALUE = "/submodel/submodel-elements/SME_4/$value";
	private static final String EP_SME_5 = "/submodel/submodel-elements/SME_5";
	private static final String EP_SME_6_ATTACHMENT = "/submodel/submodel-elements/SME_6/attachment";
	private static final String EP_SME_7_INVOKE = "/submodel/submodel-elements/SME_7/invoke";

	private static final String FILE_NAME = "BaSyx-Logo.png";

	@Autowired
	private AccessTokenProvider tokenProvider;

	@Autowired
	private MockMvc mockMvc;

	@Test
	public void testGetHealthStatusUnauthorized() throws Exception {
		String expectedBody = """
			{
				    "status": "UP"
			}
			""";
		mockMvc.perform(get(EP_HEALTH)).andExpect(status().isOk()).andExpect(content().json(expectedBody));
	}

	@Test
	public void testGetSubmodelsAutorized() throws Exception {
		mockMvc.perform(get(EP_SUBMODEL).header(HEADER_AUTH, readerBearer())).andExpect(status().isOk());
	}

	@Test
	public void testGetSubmodelForbidden() throws Exception {
		mockMvc.perform(get(EP_SUBMODEL).header(HEADER_AUTH, updaterBearer())).andExpect(status().isForbidden());
	}

	@Test
	public void testGetSubmodelUnauthorized() throws Exception {
		mockMvc.perform(get(EP_SUBMODEL)).andExpect(status().isUnauthorized());
	}

	@Test
	public void testGetSubmodelElementAutorized() throws Exception {
		mockMvc.perform(get(EP_SME_1).header(HEADER_AUTH, readerBearer())).andExpect(status().isOk());
	}

	@Test
	public void testGetSubmodelElementAutorized2() throws Exception {
		mockMvc.perform(get(EP_SME_4).header(HEADER_AUTH, readerSme3Sme4Bearer())).andExpect(status().isOk());
	}

	@Test
	public void testGetSubmodelElementForbidden() throws Exception {
		mockMvc.perform(get(EP_SME_1).header(HEADER_AUTH, updaterBearer())).andExpect(status().isForbidden());
	}

	@Test
	public void testGetSubmodelElementForbidden2() throws Exception {
		mockMvc.perform(get(EP_SME_1).header(HEADER_AUTH, readerSme3Sme4Bearer())).andExpect(status().isForbidden());
	}

	@Test
	public void testGetSubmodelElementForbidden3() throws Exception {
		mockMvc.perform(get(EP_SME_1).header(HEADER_AUTH, readerSm2Bearer())).andExpect(status().isForbidden());
	}
	
	@Test
	public void testGetSubmodelElementUnauthorized() throws Exception {
		mockMvc.perform(get(EP_SME_1)).andExpect(status().isUnauthorized());
	}

	@Test
	public void testGetSubmodelElementValueAutorized() throws Exception {
		mockMvc.perform(get(EP_SME_1_VALUE).header(HEADER_AUTH, readerBearer())).andExpect(status().isOk());
	}

	@Test
	public void testGetSubmodelElementValueAutorized2() throws Exception {
		mockMvc.perform(get(EP_SME_4_VALUE).header(HEADER_AUTH, readerSme3Sme4Bearer())).andExpect(status().isOk());
	}

	@Test
	public void testGetSubmodelElementValueForbidden() throws Exception {
		mockMvc.perform(get(EP_SME_1_VALUE).header(HEADER_AUTH, updaterBearer())).andExpect(status().isForbidden());
	}

	@Test
	public void testGetSubmodelElementValueForbidden2() throws Exception {
		mockMvc.perform(get(EP_SME_1_VALUE).header(HEADER_AUTH, readerSme3Sme4Bearer())).andExpect(status().isForbidden());
	}

	@Test
	public void testGetSubmodelElementValueUnauthorized() throws Exception {
		mockMvc.perform(get(EP_SME_1_VALUE)).andExpect(status().isUnauthorized());
	}

	@Test
	public void testGetAllTopLevelSubmodelElementsAutorized() throws Exception {
		mockMvc.perform(get(EP_ALL_SMES).header(HEADER_AUTH, readerBearer())).andExpect(status().isOk());
	}

	@Test
	public void testGetAllTopLevelSubmodelElementsForbidden() throws Exception {
		mockMvc.perform(get(EP_ALL_SMES).header(HEADER_AUTH, updaterBearer())).andExpect(status().isForbidden());
	}

	@Test
	public void testGetAllTopLevelSubmodelElementsSomeForbiddenFiltered() throws Exception {
		String expected = """
				{
					"result" : [
						{
							"modelType" : "Property",
							"idShort" : "SME_3",
							"value" : "3"
						},
						{
							"modelType" : "Property",
							"idShort" : "SME_4",
							"value" : "4"
						}
					],
					"paging_metadata":{
					}
				}
				""";
		mockMvc.perform(get(EP_ALL_SMES).header(HEADER_AUTH, readerSme3Sme4Bearer())).andExpect(status().isOk()).andExpect(content().json(expected));
	}

	@Test
	public void testGetNextTopLevelSubmodelElementsForbiddenFiltered() throws Exception {
		String expected = """
				{
					"result" : [
						{
							"modelType" : "Property",
							"idShort" : "SME_3",
							"value" : "3"
						}
					],
					"paging_metadata":{
						"cursor": "U01FXzM"
					}
				}
				""";
		mockMvc.perform(get(EP_ALL_SMES).header(HEADER_AUTH, readerSme3Sme4Bearer()).queryParam("limit", "1")).andExpect(status().isOk()).andExpect(content().json(expected));
	}

	@Test
	public void testGetNextTopLevelSubmodelElementsForbiddenFiltered2() throws Exception {
		String expected = """
				{
					"result" : [
						{
							"modelType" : "Property",
							"idShort" : "SME_3",
							"value" : "3"
						},
						{
							"modelType" : "Property",
							"idShort" : "SME_4",
							"value" : "4"
						}
					],
					"paging_metadata":{
						"cursor": "U01FXzQ"
					}
				}
				""";
		mockMvc.perform(get(EP_ALL_SMES).header(HEADER_AUTH, readerSme3Sme4Bearer()).queryParam("limit", "2")).andExpect(status().isOk()).andExpect(content().json(expected));
	}

	@Test
	public void testGetAllTopLevelSubmodelElementsUnauthorized() throws Exception {
		mockMvc.perform(get(EP_ALL_SMES)).andExpect(status().isUnauthorized());
	}

	@Test
	public void testPutFileAttachementUnauthorized() throws Exception {
		uploadBasyxImage(null, status().isUnauthorized());
	}

	@Test
	public void testPutFileAttachementInsufficientAuthorization() throws Exception {
		uploadBasyxImage(readerBearer(), status().isForbidden());
	}

	@Test
	public void testPutFileAttachementAutorized() throws Exception {
		uploadBasyxImage(updaterBearer(), status().is2xxSuccessful());
	}

	@Test
	public void testGetFileUnauthorized() throws Exception {
		uploadBasyxImage(updaterBearer());
		mockMvc.perform(get(EP_SME_6_ATTACHMENT)).andExpect(status().isUnauthorized());
	}

	@Test
	public void testGetFileForbidden() throws Exception {
		uploadBasyxImage(updaterBearer());
		mockMvc.perform(get(EP_SME_6_ATTACHMENT).header(HEADER_AUTH, readerSme3Sme4Bearer())).andExpect(status().isForbidden());
	}

	@Test
	public void testGetFileAuthorized() throws Exception {
		uploadBasyxImage(updaterBearer());
		mockMvc.perform(get(EP_SME_6_ATTACHMENT).header(HEADER_AUTH, readerBearer())).andExpect(status().isOk());
	}

	@Test
	public void testCreateSubmodelElementAuthorized() throws Exception {
		createSubmodelElement(EP_ALL_SMES, creatorBearer(), status().isCreated());
	}

	@Test
	public void testCreateSubmodelElementForbidden() throws Exception {
		createSubmodelElement(EP_ALL_SMES, updaterBearer(), status().isForbidden());
	}

	@Test
	public void testCreateSubmodelElementUnauthorized() throws Exception {
		createSubmodelElement(EP_ALL_SMES, null, status().isUnauthorized());
	}

	@Test
	public void testCreateSubmodelElementUnderPathAuthorized() throws Exception {
		createSubmodelElement(EP_SME_2, creatorBearer(), status().isCreated());
	}

	@Test
	public void testCreateSubmodelElementUnderPathForbidden() throws Exception {
		createSubmodelElement(EP_SME_2, updaterBearer(), status().isForbidden());
	}

	@Test
	public void testCreateSubmodelElementUnderPathUnauthorized() throws Exception {
		createSubmodelElement(EP_SME_2, null, status().isUnauthorized());
	}



	@Test
	public void testPatchSubmodelElementValueUnauthorized() throws Exception {
		patchSubmodelElementValueOnly(null, status().isUnauthorized(), false);
	}
	
	@Test
	public void testInvokeOperationAuthorized() throws Exception {
		invokeOperation(executorBearer(), status().isOk());
	}
	
	@Test
	public void testInvokeOperationForbidden2() throws Exception {
		invokeOperation(executorTwoBearer(), status().isForbidden());
	}

	@Test
	public void testInvokeOperationForbidden() throws Exception {
		invokeOperation(creatorBearer(), status().isForbidden());
	}

	@Test
	public void testInvokeOperationUnauthorized() throws Exception {
		invokeOperation(null, status().isUnauthorized());
	}
	
	public void invokeOperation(String token, ResultMatcher matcher) throws Exception {
		String body = "{}";
		MockHttpServletRequestBuilder builder = post(EP_SME_7_INVOKE).with(csrf()).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).content(body);
		if (token != null) {
			builder.header(HEADER_AUTH, token);
		}
		mockMvc.perform(builder).andExpect(matcher);
	}
	
	
	@Test
	public void testPatchSubmodelElementValueAuthorized() throws Exception {
		patchSubmodelElementValueOnly(updaterBearer(), status().isNoContent(), true);
	}

	@Test
	public void testPatchSubmodelElementValueForbidden() throws Exception {
		patchSubmodelElementValueOnly(creatorBearer(), status().isForbidden(), false);
	}

	@Test
	public void testUpdateSubmodelElementUnauthorized() throws Exception {
		updateSubmodelElement(null, status().isUnauthorized(), false);
	}
	
	@Test
	public void testUpdateSubmodelElementAuthorized() throws Exception {
		updateSubmodelElement(updaterBearer(), status().isNoContent(), true);
	}
	
	@Test
	public void testUpdateSubmodelElement2Authorized() throws Exception {
		updateSubmodelElement(smeUpdaterBearer(), status().isNoContent(), true);
	}
	
	@Test
	public void testUpdateSubmodelElement2Forbidden() throws Exception {
		updateSubmodelElement(smeUpdaterTwoBearer(), status().isForbidden(), false);
	}

	@Test
	public void testUpdateSubmodelElementForbidden() throws Exception {
		updateSubmodelElement(creatorBearer(), status().isForbidden(), false);
	}	

	public void updateSubmodelElement(String token, ResultMatcher matcher, boolean shouldBeUpdated) throws Exception {
		String body = propPayload("2_1", "55");
		MockHttpServletRequestBuilder builder = put(EP_SME_2_1).with(csrf()).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).content(body);
		if (token != null) {
			builder.header(HEADER_AUTH, token);
		}
		mockMvc.perform(builder).andExpect(matcher);
		if (shouldBeUpdated) {
			mockMvc.perform(get(EP_SME_2_1).header(HEADER_AUTH, readerBearer())).andExpect(status().isOk()).andExpect(content().json(body));
		} else {
			mockMvc.perform(get(EP_SME_2_1).header(HEADER_AUTH, readerBearer())).andExpect(status().isOk()).andExpect(content().json(propPayload("2_1", "2_1")));
		}
	}
	
	public void patchSubmodelElementValueOnly(String token, ResultMatcher matcher, boolean shouldBeUpdated) throws Exception {
		String body = "\"55\"";
		MockHttpServletRequestBuilder builder = patch(EP_SME_4_VALUE).with(csrf()).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).content(body);
		if (token != null) {
			builder.header(HEADER_AUTH, token);
		}
		mockMvc.perform(builder).andExpect(matcher);
		if (shouldBeUpdated) {
			mockMvc.perform(get(EP_SME_4).header(HEADER_AUTH, readerBearer())).andExpect(status().isOk()).andExpect(content().json(propPayload("4", "55")));
		} else {
			mockMvc.perform(get(EP_SME_4).header(HEADER_AUTH, readerBearer())).andExpect(status().isOk()).andExpect(content().json(propPayload("4", "4")));
		}
	}

	@Test
	public void testPatchSubmodelValueOnlyAuthorized() throws Exception {
		patchSubmodelValueOnly(updaterBearer(), status().isNoContent(), true);
	}

	@Test
	public void testPatchSubmodelValueOnlyForbidden() throws Exception {
		patchSubmodelValueOnly(creatorBearer(), status().isForbidden(), false);
	}

	@Test
	public void testPatchSubmodelValueOnlyUnauthorized() throws Exception {
		patchSubmodelValueOnly(null, status().isUnauthorized(), false);
	}

	public void patchSubmodelValueOnly(String token, ResultMatcher matcher, boolean shouldBeUpdated) throws Exception {
		String body1 = propPayload("1",  "__1");
		String body4 = propPayload("4",  "__4");
		String body5 = propPayload("5",  "__5");
		String body = "[" + String.join(", ", body1, body4, body5 )+ "]";
		MockHttpServletRequestBuilder builder = patch(EP_SUBMODEL_VALUE).with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(body);
		if (token != null) {
			builder.header(HEADER_AUTH, token);
		}
		mockMvc.perform(builder).andExpect(matcher);
		if (shouldBeUpdated) {
			mockMvc.perform(get(EP_SME_1).header(HEADER_AUTH, readerBearer())).andExpect(status().isOk()).andExpect(content().json(body1));
			mockMvc.perform(get(EP_SME_4).header(HEADER_AUTH, readerBearer())).andExpect(status().isOk()).andExpect(content().json(body4));
			mockMvc.perform(get(EP_SME_5).header(HEADER_AUTH, readerBearer())).andExpect(status().isOk()).andExpect(content().json(body5));
		} else {
			mockMvc.perform(get(EP_SME_1).header(HEADER_AUTH, readerBearer())).andExpect(status().isOk()).andExpect(content().json(propPayload("1", "1")));
			mockMvc.perform(get(EP_SME_4).header(HEADER_AUTH, readerBearer())).andExpect(status().isOk()).andExpect(content().json(propPayload("4", "4")));
			mockMvc.perform(get(EP_SME_5).header(HEADER_AUTH, readerBearer())).andExpect(status().isOk()).andExpect(content().json(propPayload("5", "5")));
		}
	}

	private void createSubmodelElement(String endpoint, String token, ResultMatcher matcher) throws Exception {
		MockHttpServletRequestBuilder builder = post(endpoint).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).content(propPayload("2_2", "2_2")).with(csrf());
		if (token != null) {
			builder.header(HEADER_AUTH, token);
		}
		mockMvc.perform(builder).andExpect(matcher);
	}

	@Test
	public void testDeleteSubmodelElementAuthorized() throws Exception {
		deleteSubmodelElement(EP_SME_1, deleterBearer(), status().isNoContent());
		mockMvc.perform(get(EP_SME_1).header(HEADER_AUTH, readerBearer()).with(csrf())).andExpect(status().isNotFound());
	}

	@Test
	public void testDeleteSubmodelElementForbidden() throws Exception {
		deleteSubmodelElement(EP_SME_1, updaterBearer(), status().isForbidden());
	}
	
	@Test
	public void testDeleteSubmodelElementForbidden2() throws Exception {
		deleteSubmodelElement(EP_SME_1, deleterTwoBearer(), status().isForbidden());
	}

	@Test
	public void testDeleteSubmodelElementUnauthorized() throws Exception {
		deleteSubmodelElement(EP_SME_1, null, status().isUnauthorized());
	}

	@Test
	public void testDeleteSubmodelElementUnderPathAuthorized() throws Exception {
		deleteSubmodelElement(EP_SME_2_1, deleterBearer(), status().isNoContent());
		mockMvc.perform(get(EP_SME_2_1).header(HEADER_AUTH, readerBearer()).with(csrf())).andExpect(status().isNotFound());
	}

	@Test
	public void testDeleteSubmodelElementUnderPathForbidden() throws Exception {
		deleteSubmodelElement(EP_SME_2_1, updaterBearer(), status().isForbidden());
	}

	@Test
	public void testDeleteSubmodelElementUnderPathUnauthorized() throws Exception {
		deleteSubmodelElement(EP_SME_2_1, null, status().isUnauthorized());
	}

	private void deleteSubmodelElement(String endpoint, String token, ResultMatcher matcher) throws Exception {
		MockHttpServletRequestBuilder builder = delete(endpoint).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).content(propPayload("2_2", "2_2")).with(csrf());
		if (token != null) {
			builder.header(HEADER_AUTH, token);
		}
		mockMvc.perform(builder).andExpect(matcher);
	}

	@Test
	public void testDeleteFileAuthorized() throws Exception {
		uploadBasyxImage(updaterBearer());
		// deletion of an attachment is an update of the element -> use updateBearer!
		mockMvc.perform(delete(EP_SME_6_ATTACHMENT).with(csrf()).header(HEADER_AUTH, updaterBearer())).andExpect(status().isOk());
	}

	@Test
	public void testDeleteFileUnauthorized() throws Exception {
		uploadBasyxImage(updaterBearer());
		mockMvc.perform(delete(EP_SME_6_ATTACHMENT).with(csrf())).andExpect(status().isUnauthorized());
	}

	@Test
	public void testDeleteFileForbidden() throws Exception {
		uploadBasyxImage(updaterBearer());
		mockMvc.perform(delete(EP_SME_6_ATTACHMENT).header(HEADER_AUTH, readerSme3Sme4Bearer())).andExpect(status().isForbidden());
	}

	private String creatorBearer() {
		return bearer(DummyCredentialStore.BASYX_CREATOR_CREDENTIAL);
	}

	private String readerSme3Sme4Bearer() {
		return bearer(DummyCredentialStore.BASYX_SME_READER_CREDENTIAL);
	}
	
	private String readerSm2Bearer() {
		return bearer(DummyCredentialStore.BASYX_SME_READER_TWO_CREDENTIAL);
	}

	private String readerBearer() {
		return bearer(DummyCredentialStore.BASYX_READER_CREDENTIAL);
	}

	private String executorBearer() {
		return bearer(DummyCredentialStore.BASYX_EXECUTOR_CREDENTIAL);
	}
	
	private String executorTwoBearer() {
		return bearer(DummyCredentialStore.BASYX_EXECUTOR_TWO_CREDENTIAL);
	}
	
	private String updaterBearer() {
		return bearer(DummyCredentialStore.BASYX_UPDATER_CREDENTIAL);
	}
	
	private String smeUpdaterBearer() {
		return bearer(DummyCredentialStore.BASYX_SME_UPDATER_CREDENTIAL);
	}
	
	private String smeUpdaterTwoBearer() {
		return bearer(DummyCredentialStore.BASYX_SME_UPDATER_TWO_CREDENTIAL);
	}


	private String deleterBearer() {
		return bearer(DummyCredentialStore.BASYX_DELETER_CREDENTIAL);
	}
	
	private String deleterTwoBearer() {
		return bearer(DummyCredentialStore.BASYX_DELETER_TWO_CREDENTIAL);
	}

	private String bearer(DummyCredential dummyCredential) {
		return "Bearer " + tokenProvider.getAccessToken(dummyCredential.getUsername(), dummyCredential.getPassword());
	}

	private void uploadBasyxImage(String token) throws Exception {
		uploadBasyxImage(token, status().is2xxSuccessful());
	}

	private void uploadBasyxImage(String token, ResultMatcher matcher) throws Exception {
		File file = ResourceUtils.getFile("classpath:" + FILE_NAME);
		byte[] content = FileCopyUtils.copyToByteArray(file);
		MockMultipartFile toSend = new MockMultipartFile("file", FILE_NAME, MediaType.IMAGE_PNG_VALUE, content);
		MockHttpServletRequestBuilder builder = multipart(EP_SME_6_ATTACHMENT).file(toSend).param("fileName", FILE_NAME).with(request -> {
			request.setMethod("PUT");
			return request;
		}).with(csrf());
		if (token != null) {
			builder.header(HEADER_AUTH, token);
		}
		mockMvc.perform(builder).andExpect(matcher);
	}

	public String propPayload(String id, String value) {
		return String.format("""
				{
				  "modelType" : "Property",
				  "idShort" : "SME_%s",
				  "value" : "%s"
				}
				""", id, value);
	}
}

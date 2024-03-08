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

package org.eclipse.digitaltwin.basyx.aasregistry.service.tests;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.eclipse.digitaltwin.basyx.aasregistry.service.api.AasRegistryBulkApiController;
import org.eclipse.digitaltwin.basyx.aasregistry.service.errors.BasyxControllerAdvice;
import org.eclipse.digitaltwin.basyx.aasregistry.service.errors.BulkOperationResultNotFoundException;
import org.eclipse.digitaltwin.basyx.aasregistry.service.events.RegistryEventSink;
import org.eclipse.digitaltwin.basyx.aasregistry.service.storage.AasRegistryBulkOperationsService;
import org.eclipse.digitaltwin.basyx.aasregistry.service.storage.AasRegistryStorage;
import org.eclipse.digitaltwin.basyx.aasregistry.service.storage.BulkOperationResult;
import org.eclipse.digitaltwin.basyx.aasregistry.service.storage.BulkOperationResultManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Test for the AAS Registry Bulk API Controller
 * 
 * @author mateusmolina
 */
@WebMvcTest(AasRegistryBulkApiController.class)
public class AasRegistryBulkApiControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private AasRegistryStorage storage;

	@MockBean
	public RegistryEventSink eventSink;

	@MockBean
	private AasRegistryBulkOperationsService bulkOperationsService;

	@MockBean
	private BulkOperationResultManager bulkResultManager;

	@Autowired
	private ObjectMapper objectMapper;

	@BeforeEach
	public void setup() {
		MockitoAnnotations.openMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(new AasRegistryBulkApiController(bulkOperationsService, bulkResultManager)).setControllerAdvice(new BasyxControllerAdvice()).build();
	}

	@Test
	public void postBulkShellDescriptors() throws Exception {
		long opId = 2L;
		when(bulkResultManager.runOperationAsync(any(Runnable.class))).thenReturn(opId);

		mockMvc.perform(post("/bulk/shell-descriptors").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(anyList()))).andExpect(status().isAccepted())
				.andExpect(content().string("http://localhost/bulk/status/" + opId));
	}

	@Test
	public void putBulkShellDescriptors() throws Exception {
		long opId = 2L;
		when(bulkResultManager.runOperationAsync(any(Runnable.class))).thenReturn(opId);

		mockMvc.perform(put("/bulk/shell-descriptors").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(anyList()))).andExpect(status().isAccepted())
				.andExpect(content().string("http://localhost/bulk/status/" + opId));
	}

	@Test
	public void deleteBulkShellDescriptors() throws Exception {
		long opId = 2L;
		List<String> listIdentifiers = List.of("desc1", "desc2");
		List<String> listEncodedIdentifiers = List.of("ZGVzYzE", "ZGVzYzI");

		ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);

		when(bulkResultManager.runOperationAsync(runnableCaptor.capture())).thenReturn(opId);

		mockMvc.perform(delete("/bulk/shell-descriptors").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(listEncodedIdentifiers))).andExpect(status().isAccepted())
				.andExpect(content().string("http://localhost/bulk/status/" + opId));

		runnableCaptor.getValue().run();

		verify(bulkOperationsService).deleteBulkAasDescriptors(eq(listIdentifiers));
	}

	@Test
	public void getBulkStatus_withCompletedTransaction() throws Exception {
		long opId = 2L;
		when(bulkResultManager.getBulkOperationResultStatus(opId)).thenReturn(BulkOperationResult.ExecutionState.COMPLETED);

		mockMvc.perform(get("/bulk/status/{handleId}", opId)).andExpect(status().isNoContent());
	}

	@Test
	public void getBulkStatus_withFailedTransaction() throws Exception {
		long opId = 2L;
		when(bulkResultManager.getBulkOperationResultStatus(opId)).thenReturn(BulkOperationResult.ExecutionState.FAILED);

		mockMvc.perform(get("/bulk/status/{handleId}", opId)).andExpect(status().isBadRequest());
	}

	@Test
	public void getBulkStatus_withTimedOutTransaction() throws Exception {
		long opId = 2L;
		when(bulkResultManager.getBulkOperationResultStatus(opId)).thenReturn(BulkOperationResult.ExecutionState.TIMEOUT);

		mockMvc.perform(get("/bulk/status/{handleId}", opId)).andExpect(status().isRequestTimeout());
	}

	@Test
	public void getBulkStatus_withInitiated() throws Exception {
		long opId = 2L;
		when(bulkResultManager.getBulkOperationResultStatus(opId)).thenReturn(BulkOperationResult.ExecutionState.INITIATED);

		mockMvc.perform(get("/bulk/status/{handleId}", opId)).andExpect(status().isOk());
	}

	@Test
	public void getBulkStatus_withRunning() throws Exception {
		long opId = 2L;
		when(bulkResultManager.getBulkOperationResultStatus(opId)).thenReturn(BulkOperationResult.ExecutionState.RUNNING);

		mockMvc.perform(get("/bulk/status/{handleId}", opId)).andExpect(status().isOk());
	}

	@Test
	public void getBulkStatus_withNonExistingTransaction() throws Exception {
		long opId = 2L;
		when(bulkResultManager.getBulkOperationResultStatus(opId)).thenThrow(BulkOperationResultNotFoundException.class);

		mockMvc.perform(get("/bulk/status/{handleId}", opId)).andExpect(status().isNotFound());
	}

	@Test
	public void getBulkResult() throws Exception {
		long opId = 2L;
		BulkOperationResult expectedResponse = new BulkOperationResult(opId, BulkOperationResult.ExecutionState.RUNNING, null);
		when(bulkResultManager.getBulkOperationResult(opId)).thenReturn(expectedResponse);

		mockMvc.perform(get("/bulk/result/{handleId}", opId)).andExpect(status().isOk()).andExpect(content().string(objectMapper.writeValueAsString(expectedResponse)));
	}

	@Test
	public void getBulkResult_withNonExistingTransaction() throws Exception {
		long opId = 2L;
		when(bulkResultManager.getBulkOperationResult(opId)).thenThrow(BulkOperationResultNotFoundException.class);

		mockMvc.perform(get("/bulk/result/{handleId}", opId)).andExpect(status().isNotFound());
	}

}
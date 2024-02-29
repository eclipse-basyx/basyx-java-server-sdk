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
import org.eclipse.digitaltwin.basyx.aasregistry.service.errors.TransactionResponseNotFoundException;
import org.eclipse.digitaltwin.basyx.aasregistry.service.events.RegistryEventSink;
import org.eclipse.digitaltwin.basyx.aasregistry.service.storage.AasRegistryStorage;
import org.eclipse.digitaltwin.basyx.aasregistry.service.storage.AasTransactionManager;
import org.eclipse.digitaltwin.basyx.aasregistry.service.storage.AasTransactionsService;
import org.eclipse.digitaltwin.basyx.aasregistry.service.storage.TransactionResponse;
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
    private AasTransactionsService aasTransactionsService;

    @MockBean
    private AasTransactionManager aasTransactionManager;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(new AasRegistryBulkApiController(aasTransactionsService, aasTransactionManager)).setControllerAdvice(new BasyxControllerAdvice()).build();
    }

    @Test
    public void postBulkShellDescriptorsTest() throws Exception {
        long transactionId = 2L;
        when(aasTransactionManager.runTransactionAsync(any(Runnable.class))).thenReturn(transactionId);

        mockMvc.perform(post("/bulk/shell-descriptors").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(anyList()))).andExpect(status().isOk())
                .andExpect(content().string("http://localhost/bulk/status/" + transactionId));
    }

    @Test
    public void putBulkShellDescriptorsTest() throws Exception {
        long transactionId = 2L;
        when(aasTransactionManager.runTransactionAsync(any(Runnable.class))).thenReturn(transactionId);

        mockMvc.perform(put("/bulk/shell-descriptors").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(anyList()))).andExpect(status().isOk())
                .andExpect(content().string("http://localhost/bulk/status/" + transactionId));
    }

    @Test
    public void deleteBulkShellDescriptorsTest() throws Exception {
        long transactionId = 2L;
        List<String> listIdentifiers = List.of("desc1", "desc2");
        List<String> listEncodedIdentifiers = List.of("ZGVzYzE", "ZGVzYzI");

        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);

        when(aasTransactionManager.runTransactionAsync(runnableCaptor.capture())).thenReturn(transactionId);

        mockMvc.perform(delete("/bulk/shell-descriptors").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(listEncodedIdentifiers))).andExpect(status().isOk())
                .andExpect(content().string("http://localhost/bulk/status/" + transactionId));

        runnableCaptor.getValue().run();

        verify(aasTransactionsService).deleteBulkAasDescriptors(eq(listIdentifiers));
    }

    @Test
    public void getBulkStatusTest() throws Exception {
        long transactionId = 2L;
        when(aasTransactionManager.getTransactionStatus(transactionId)).thenReturn("COMPLETE");

        mockMvc.perform(get("/bulk/status/{handleId}", transactionId)).andExpect(status().isOk()).andExpect(content().string("COMPLETE"));
    }

    @Test
    public void getBulkStatus_withNonExistingTransaction() throws Exception {
        long transactionId = 2L;
        when(aasTransactionManager.getTransactionStatus(transactionId)).thenThrow(TransactionResponseNotFoundException.class);

        mockMvc.perform(get("/bulk/status/{handleId}", transactionId)).andExpect(status().isNotFound());
    }

    @Test
    public void getBulkResultTest() throws Exception {
        long transactionId = 2L;
        TransactionResponse expectedResponse = new TransactionResponse(transactionId, TransactionResponse.ExecutionState.RUNNING, null);
        when(aasTransactionManager.getTransactionResponse(transactionId)).thenReturn(expectedResponse);

        mockMvc.perform(get("/bulk/result/{handleId}", transactionId)).andExpect(status().isOk()).andExpect(content().string(objectMapper.writeValueAsString(expectedResponse)));
    }

    @Test
    public void getBulkResult_withNonExistingTransaction() throws Exception {
        long transactionId = 2L;
        when(aasTransactionManager.getTransactionResponse(transactionId)).thenThrow(TransactionResponseNotFoundException.class);

        mockMvc.perform(get("/bulk/result/{handleId}", transactionId)).andExpect(status().isNotFound());
    }

}
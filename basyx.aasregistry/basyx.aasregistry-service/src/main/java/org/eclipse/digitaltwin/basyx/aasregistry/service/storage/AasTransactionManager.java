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

package org.eclipse.digitaltwin.basyx.aasregistry.service.storage;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

import org.eclipse.digitaltwin.basyx.aasregistry.service.errors.TransactionResponseNotFoundException;
import org.springframework.stereotype.Component;

/**
 * Manager for transactional tasks
 * 
 * @author mateusmolina
 */
@Component
public class AasTransactionManager {

    private final Map<Long, TransactionResponse> transactionResponseMap = new ConcurrentHashMap<>();
    private final AtomicLong transactionIdCounter = new AtomicLong(0);

    private final ExecutorService executorService = Executors.newCachedThreadPool();

    /**
     * Runs a Transactional Runnable async
     * 
     * @param transactionTask
     * @return transactionId, traceable using {@link #getTransactionResponse(long)}
     */
    public long runTransactionAsync(Runnable transactionTask) {
        long transactionId = transactionIdCounter.incrementAndGet();

        putTransaction(transactionId, TransactionResponse.ExecutionState.RUNNING, null);

        executorService.submit(() -> {
            putTransaction(transactionId, TransactionResponse.ExecutionState.INITIATED, null);
            try {
                transactionTask.run();
                putTransaction(transactionId, TransactionResponse.ExecutionState.COMPLETED, null);
            } catch (Exception e) {
                putTransaction(transactionId, TransactionResponse.ExecutionState.FAILED, e.getMessage());
            }
        });

        return transactionId;
    }

    public TransactionResponse getTransactionResponse(long transactionResponseId) throws TransactionResponseNotFoundException {
        if (!transactionResponseMap.containsKey(transactionResponseId))
            throw new TransactionResponseNotFoundException(transactionResponseId);

        return transactionResponseMap.get(transactionResponseId);
    }

    public String getTransactionStatus(long transactionResponseId) {
        if (!transactionResponseMap.containsKey(transactionResponseId))
            throw new TransactionResponseNotFoundException(transactionResponseId);

        return transactionResponseMap.get(transactionResponseId).transactionStatus().toString();
    }

    private void putTransaction(long transactionId, TransactionResponse.ExecutionState newExecutionState, String message) {
        transactionResponseMap.put(transactionId, new TransactionResponse(transactionId, newExecutionState, null));
    }
}

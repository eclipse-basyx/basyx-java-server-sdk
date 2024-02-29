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

import org.eclipse.digitaltwin.basyx.aasregistry.service.errors.BulkOperationResultNotFoundException;
import org.springframework.stereotype.Component;

/**
 * Manager for transactional tasks
 * 
 * @author mateusmolina
 */
@Component
public class BulkOperationResultManager {

    private final Map<Long, BulkOperationResult> bulkResultMap = new ConcurrentHashMap<>();
    private final AtomicLong bulkResultIdCounter = new AtomicLong(0);

    private final ExecutorService executorService = Executors.newCachedThreadPool();

    /**
     * Runs a Transactional Runnable async
     * 
     * @param operation
     * @return transactionId, traceable using {@link #getBulkOperationResult(long)}
     */
    public long runOperationAsync(Runnable operation) {
        long operationId = bulkResultIdCounter.incrementAndGet();

        putBulkOperationResult(operationId, BulkOperationResult.ExecutionState.INITIATED, null);

        executorService.submit(() -> {
            putBulkOperationResult(operationId, BulkOperationResult.ExecutionState.RUNNING, null);
            try {
                operation.run();
                putBulkOperationResult(operationId, BulkOperationResult.ExecutionState.COMPLETED, null);
            } catch (Exception e) {
                putBulkOperationResult(operationId, BulkOperationResult.ExecutionState.FAILED, e.getMessage());
            }
        });

        return operationId;
    }

    public BulkOperationResult getBulkOperationResult(long bulkResultId) throws BulkOperationResultNotFoundException {
        if (!bulkResultMap.containsKey(bulkResultId))
            throw new BulkOperationResultNotFoundException(bulkResultId);

        return bulkResultMap.get(bulkResultId);
    }

    public String getBulkOperationResultStatus(long transactionResponseId) {
        if (!bulkResultMap.containsKey(transactionResponseId))
            throw new BulkOperationResultNotFoundException(transactionResponseId);

        return bulkResultMap.get(transactionResponseId).transactionStatus().toString();
    }

    private void putBulkOperationResult(long transactionId, BulkOperationResult.ExecutionState newExecutionState, String message) {
        bulkResultMap.put(transactionId, new BulkOperationResult(transactionId, newExecutionState, null));
    }
}

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThrows;

import org.eclipse.digitaltwin.basyx.aasregistry.service.errors.BulkOperationResultNotFoundException;
import org.eclipse.digitaltwin.basyx.aasregistry.service.storage.BulkOperationResultManager;
import org.eclipse.digitaltwin.basyx.aasregistry.service.storage.BulkOperationResult;
import org.junit.Test;

import lombok.SneakyThrows;

/**
 * Test for the Bulk Operation Result Manager
 * 
 * @author mateusmolina
 */
public class BulkOperationResultManagerTest {

    @Test
    public void runOperationAsync_completed() throws InterruptedException {
        BulkOperationResultManager bulkResultManager = new BulkOperationResultManager();

        long transactionId = bulkResultManager.runOperationAsync(BulkOperationResultManagerTest::sleepFor500);
        assertEquals(BulkOperationResult.ExecutionState.INITIATED, bulkResultManager.getBulkOperationResultStatus(transactionId));
        Thread.sleep(250);
        assertEquals(BulkOperationResult.ExecutionState.RUNNING, bulkResultManager.getBulkOperationResultStatus(transactionId));
        Thread.sleep(300);
        assertEquals(BulkOperationResult.ExecutionState.COMPLETED, bulkResultManager.getBulkOperationResultStatus(transactionId));
    }

    @Test
    public void runOperationAsync_failed() throws InterruptedException {
        BulkOperationResultManager bulkResultManager = new BulkOperationResultManager();

        long transactionId = bulkResultManager.runOperationAsync(BulkOperationResultManagerTest::sleepFor500AndThrow);
        assertEquals(BulkOperationResult.ExecutionState.INITIATED, bulkResultManager.getBulkOperationResultStatus(transactionId));
        Thread.sleep(250);
        assertEquals(BulkOperationResult.ExecutionState.RUNNING, bulkResultManager.getBulkOperationResultStatus(transactionId));
        Thread.sleep(300);
        assertEquals(BulkOperationResult.ExecutionState.FAILED, bulkResultManager.getBulkOperationResultStatus(transactionId));
    }

    @Test
    public void testGetBulkOperationResult() throws InterruptedException {
        BulkOperationResultManager bulkResultManager = new BulkOperationResultManager();

        long transactionId = bulkResultManager.runOperationAsync(BulkOperationResultManagerTest::doNothing);
        Thread.sleep(100);

        assertEquals(new BulkOperationResult(transactionId, BulkOperationResult.ExecutionState.COMPLETED, null), bulkResultManager.getBulkOperationResult(transactionId));
        assertThrows(BulkOperationResultNotFoundException.class, () -> bulkResultManager.getBulkOperationResult(transactionId + 1));
    }

    @Test
    public void testGetBulkOperationResultStatus() throws InterruptedException {
        BulkOperationResultManager bulkResultManager = new BulkOperationResultManager();

        long transactionId = bulkResultManager.runOperationAsync(BulkOperationResultManagerTest::doNothing);
        Thread.sleep(100);

        assertEquals(BulkOperationResult.ExecutionState.COMPLETED, bulkResultManager.getBulkOperationResultStatus(transactionId));
        assertThrows(BulkOperationResultNotFoundException.class, () -> bulkResultManager.getBulkOperationResultStatus(transactionId + 1));
    }

    @Test
    public void testMultipleOperations() {
        BulkOperationResultManager bulkResultManager = new BulkOperationResultManager();

        long id1 = bulkResultManager.runOperationAsync(BulkOperationResultManagerTest::doNothing);
        long id2 = bulkResultManager.runOperationAsync(BulkOperationResultManagerTest::doNothing);

        assertNotEquals(id1, id2);
    }

    @SneakyThrows
    private static void sleepFor500() {
        Thread.sleep(500);
    }

    @SneakyThrows
    private static void sleepFor500AndThrow() {
        Thread.sleep(500);
        throw new RuntimeException();
    }

    private static void doNothing() {

    }
}
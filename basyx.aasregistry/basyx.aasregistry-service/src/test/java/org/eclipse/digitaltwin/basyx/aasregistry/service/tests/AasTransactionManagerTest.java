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

import org.eclipse.digitaltwin.basyx.aasregistry.service.errors.TransactionResponseNotFoundException;
import org.eclipse.digitaltwin.basyx.aasregistry.service.storage.AasTransactionManager;
import org.eclipse.digitaltwin.basyx.aasregistry.service.storage.TransactionResponse;
import org.junit.Test;

import lombok.SneakyThrows;

/**
 * Test for the AAS Transactions Manager
 * 
 * @author mateusmolina
 */
public class AasTransactionManagerTest {

    @Test
    public void testTransactionManager_completed() throws InterruptedException {
        AasTransactionManager transactionManager = new AasTransactionManager();

        long transactionId = transactionManager.runTransactionAsync(AasTransactionManagerTest::sleepFor500);
        assertEquals(TransactionResponse.ExecutionState.INITIATED.toString(), transactionManager.getTransactionStatus(transactionId));
        Thread.sleep(250);
        assertEquals(TransactionResponse.ExecutionState.RUNNING.toString(), transactionManager.getTransactionStatus(transactionId));
        Thread.sleep(300);
        assertEquals(TransactionResponse.ExecutionState.COMPLETED.toString(), transactionManager.getTransactionStatus(transactionId));
    }

    @Test
    public void testTransactionManager_failed() throws InterruptedException {
        AasTransactionManager transactionManager = new AasTransactionManager();

        long transactionId = transactionManager.runTransactionAsync(AasTransactionManagerTest::sleepFor500AndThrow);
        assertEquals(TransactionResponse.ExecutionState.INITIATED.toString(), transactionManager.getTransactionStatus(transactionId));
        Thread.sleep(250);
        assertEquals(TransactionResponse.ExecutionState.RUNNING.toString(), transactionManager.getTransactionStatus(transactionId));
        Thread.sleep(300);
        assertEquals(TransactionResponse.ExecutionState.FAILED.toString(), transactionManager.getTransactionStatus(transactionId));
    }

    @Test
    public void testGetTransactionResponse() throws InterruptedException {
        AasTransactionManager transactionManager = new AasTransactionManager();

        long transactionId = transactionManager.runTransactionAsync(AasTransactionManagerTest::doNothing);
        Thread.sleep(100);

        assertEquals(new TransactionResponse(transactionId, TransactionResponse.ExecutionState.COMPLETED, null), transactionManager.getTransactionResponse(transactionId));
        assertThrows(TransactionResponseNotFoundException.class, () -> transactionManager.getTransactionResponse(transactionId + 1));
    }

    @Test
    public void testGetTransactionStatus() throws InterruptedException {
        AasTransactionManager transactionManager = new AasTransactionManager();

        long transactionId = transactionManager.runTransactionAsync(AasTransactionManagerTest::doNothing);
        Thread.sleep(100);

        assertEquals(TransactionResponse.ExecutionState.COMPLETED.toString(), transactionManager.getTransactionStatus(transactionId));
        assertThrows(TransactionResponseNotFoundException.class, () -> transactionManager.getTransactionStatus(transactionId + 1));
    }

    @Test
    public void testMultipleTransactions() {
        AasTransactionManager transactionManager = new AasTransactionManager();

        long id1 = transactionManager.runTransactionAsync(AasTransactionManagerTest::doNothing);
        long id2 = transactionManager.runTransactionAsync(AasTransactionManagerTest::doNothing);

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
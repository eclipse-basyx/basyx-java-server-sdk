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

package org.eclipse.digitaltwin.basyx.aasenvironment.client;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.IntStream;

import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.Key;
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultAssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSubmodel;
import org.eclipse.digitaltwin.basyx.aasrepository.AasRepository;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.junit.Test;

public abstract class ConnectedAasManagerMultithreadingTestSuite {
    static final int N_THREADS = 20;

    protected abstract AasRepository getAasRepository();

    protected abstract ConnectedAasManager getConnectedAasManager();

    @Test
    public void testParallelSubmodelCreation() throws ExecutionException, InterruptedException {
        AssetAdministrationShell shell = createShell();

        ExecutorService executorService = Executors.newFixedThreadPool(N_THREADS);
        ConcurrentLinkedDeque<String> createdSubmodelIds = new ConcurrentLinkedDeque<>();

        List<Future<Boolean>> futures = IntStream.range(0, N_THREADS).mapToObj(i -> executorService.submit(() -> createdSubmodelIds.add(createSubmodel(shell.getId(), i)))).toList();

        try {
            for (int i = 0; i < N_THREADS; i++) {
                futures.get(i).get();
            }
        } finally {
            executorService.shutdown();
        }

        createdSubmodelIds.forEach(submodelId -> assertSubmodelWasCreatedAndRegistered(shell.getId(), submodelId));
    }

    void assertSubmodelWasCreatedAndRegistered(String shellId, String submodelId) {
        assertEquals("No submodel with id " + submodelId + " found by the client", submodelId, getConnectedAasManager().getSubmodelService(submodelId).getSubmodel().getId());
        assertTrue("SubmodelRef " + submodelId + " not found in shell " + shellId,
                getAasRepository().getSubmodelReferences(shellId, PaginationInfo.NO_LIMIT).getResult().stream().map(Reference::getKeys).flatMap(Collection::stream).map(Key::getValue).anyMatch(submodelId::equals));
    }

    private AssetAdministrationShell createShell() {
        String id = UUID.randomUUID().toString();
        DefaultAssetAdministrationShell shell = new DefaultAssetAdministrationShell.Builder().id(id).build();
        getConnectedAasManager().createAas(shell);
        return getConnectedAasManager().getAasService(id).getAAS();
    }

    private String createSubmodel(String aasId, int threadId) {
        try {
            String id = aasId + "-thread" + threadId;
            DefaultSubmodel submodel = new DefaultSubmodel.Builder().id(id).build();
            getConnectedAasManager().createSubmodelInAas(aasId, submodel);
            return id;
        } catch (Exception e) {
            throw new RuntimeException("Failed at thread " + threadId, e);
        }
    }

}

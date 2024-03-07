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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.eclipse.digitaltwin.basyx.aasregistry.model.AssetAdministrationShellDescriptor;
import org.eclipse.digitaltwin.basyx.aasregistry.service.storage.AasRegistryStorage;
import org.eclipse.digitaltwin.basyx.aasregistry.service.storage.AasRegistryBulkOperationsService;
import org.eclipse.digitaltwin.basyx.aasregistry.service.storage.DescriptorFilter;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test Suite for Bulk Operations Service
 * 
 * @author mateusmolina
 */
public abstract class AasRegistryBulkOperationsServiceTestSuite {
    private static final int NUM_GEN_DESCRIPTORS = 10;

    abstract AasRegistryStorage getAasRegistryStorage();

    abstract AasRegistryBulkOperationsService getBulkOperationsService();

    @BeforeEach
    public void cleanUpRepository() {
        getAasRegistryStorage().clear();
    }

    @Test
    public void whenBulkInsertCorrectAasDescriptors_ReturnsSuccessAndCommit() {

        List<AssetAdministrationShellDescriptor> descriptors = generateTestListOfDescriptors(NUM_GEN_DESCRIPTORS);

        getBulkOperationsService().createBulkAasDescriptors(descriptors);

        assertThat(getAllAasDescriptors()).containsExactlyInAnyOrderElementsOf(descriptors);
    }

    @Test
    public void whenBulkInsertIncorrectAasDescriptors_ReturnsFailureAndDoesntCommit() {
        List<AssetAdministrationShellDescriptor> descriptors = generateTestListOfDescriptors(NUM_GEN_DESCRIPTORS);
        descriptors.get(5).setId("descriptor7");

        assertThrows(Exception.class, () -> getBulkOperationsService().createBulkAasDescriptors(descriptors));

        assertThat(getAllAasDescriptors()).isEmpty();
    }

    @Test
    public void whenBulkDeleteCorrectAasDescriptors_ReturnsSuccessAndCommit() {

        List<AssetAdministrationShellDescriptor> descriptors = generateTestListOfDescriptors(NUM_GEN_DESCRIPTORS);

        addAllNonTransactionally(descriptors);
        List<AssetAdministrationShellDescriptor> toDeleteDescriptors = new ArrayList<>(descriptors.subList(0, 3));
        List<String> toDeleteDescriptorIds = toDeleteDescriptors.stream().map(desc -> desc.getId()).collect(Collectors.toList());

        getBulkOperationsService().deleteBulkAasDescriptors(toDeleteDescriptorIds);

        assertThat(getAllAasDescriptors()).doesNotContainAnyElementsOf(toDeleteDescriptors);
    }

    @Test
    public void whenBulkDeleteIncorrectAasDescriptors_ReturnsFailureAndDoesntCommit() {

        List<AssetAdministrationShellDescriptor> descriptors = generateTestListOfDescriptors(NUM_GEN_DESCRIPTORS);

        addAllNonTransactionally(descriptors);

        List<AssetAdministrationShellDescriptor> toDeleteDescriptors = new ArrayList<>(descriptors.subList(0, 3));
        toDeleteDescriptors.add(new AssetAdministrationShellDescriptor("incorrectDescriptor1"));
        List<String> toDeleteDescriptorIds = toDeleteDescriptors.stream().map(desc -> desc.getId()).collect(Collectors.toList());

        assertThrows(Exception.class, () -> getBulkOperationsService().deleteBulkAasDescriptors(toDeleteDescriptorIds));

        assertThat(getAllAasDescriptors()).containsExactlyInAnyOrderElementsOf(descriptors);
    }

    @Test
    public void whenBulkUpdateCorrectAasDescriptors_ReturnsSuccessAndCommit() {

        List<AssetAdministrationShellDescriptor> descriptors = generateTestListOfDescriptors(NUM_GEN_DESCRIPTORS);

        addAllNonTransactionally(descriptors);

        List<AssetAdministrationShellDescriptor> updatedDescriptors = generateTestListOfDescriptors(NUM_GEN_DESCRIPTORS);
        updatedDescriptors.forEach(desc -> desc.idShort(desc.getId() + "_idShort"));

        getBulkOperationsService().updateBulkAasDescriptors(updatedDescriptors);

        assertThat(getAllAasDescriptors()).containsExactlyInAnyOrderElementsOf(updatedDescriptors);
    }

    @Test
    public void whenBulkUpdateIncorrectAasDescriptors_ReturnsFailureAndDoesntCommit() {

        List<AssetAdministrationShellDescriptor> descriptors = generateTestListOfDescriptors(NUM_GEN_DESCRIPTORS);

        addAllNonTransactionally(descriptors);

        List<AssetAdministrationShellDescriptor> updatedDescriptors = generateTestListOfDescriptors(NUM_GEN_DESCRIPTORS);
        updatedDescriptors.forEach(desc -> desc.idShort(desc.getId() + "_idShort"));

        updatedDescriptors.get(2).setId("incorrectDescriptor");

        assertThrows(Exception.class, () -> getBulkOperationsService().updateBulkAasDescriptors(updatedDescriptors));

        assertThat(getAllAasDescriptors()).containsExactlyInAnyOrderElementsOf(descriptors);
    }

    protected List<AssetAdministrationShellDescriptor> getAllAasDescriptors() {
        return getAasRegistryStorage().getAllAasDescriptors(new PaginationInfo(null, null), new DescriptorFilter(null, null)).getResult();
    }

    private static List<AssetAdministrationShellDescriptor> generateTestListOfDescriptors(int howMany) {
        return IntStream.range(0, howMany).mapToObj(i -> new AssetAdministrationShellDescriptor("descriptor" + i)).collect(Collectors.toList());
    }

    private void addAllNonTransactionally(List<AssetAdministrationShellDescriptor> descriptors) {
        descriptors.forEach(desc -> getAasRegistryStorage().insertAasDescriptor(desc));
    }
}

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
import static org.junit.Assert.assertThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.eclipse.digitaltwin.basyx.aasregistry.model.AssetAdministrationShellDescriptor;
import org.eclipse.digitaltwin.basyx.aasregistry.service.configuration.MongoDbConfiguration;
import org.eclipse.digitaltwin.basyx.aasregistry.service.storage.AasRegistryStorage;
import org.eclipse.digitaltwin.basyx.aasregistry.service.storage.AasTransactionsService;
import org.eclipse.digitaltwin.basyx.aasregistry.service.storage.DescriptorFilter;
import org.eclipse.digitaltwin.basyx.aasregistry.service.storage.TransactionResponse;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * Test for MongoDb AAS Transactions Service
 * 
 * @author mateusmolina
 */
@TestPropertySource(properties = { "registry.type=mongodb", "spring.data.mongodb.database=aasregistry", "spring.data.mongodb.uri=mongodb://localhost:27018" })
@ContextConfiguration(classes = { MongoDbConfiguration.class })
@EnableAutoConfiguration
@ExtendWith(SpringExtension.class)
public class MongoDbAasTransactionsServiceTest {

        private static final int NUM_GEN_DESCRIPTORS = 10;

        @Autowired
        private AasRegistryStorage storage;

        @Autowired
        AasTransactionsService transactionsService;

        @BeforeEach
        public void cleanUpRepository() {
                storage.clear();
        }

        @Test
        public void whenBulkInsertCorrectAasDescriptors_ReturnsSuccessAndCommit() {

                List<AssetAdministrationShellDescriptor> descriptors = generateTestListOfDescriptors(NUM_GEN_DESCRIPTORS);

                TransactionResponse response = transactionsService.insertBulkAasDescriptors(descriptors);

                Assertions.assertEquals(TransactionResponse.TransactionStatus.SUCCESSFUL, response.transactionStatus());

                assertThat(getAllAasDescriptors()).containsExactlyInAnyOrderElementsOf(descriptors);
        }

        @Test
        public void whenBulkInsertIncorrectAasDescriptors_ReturnsFailureAndDoesntCommit() {
                List<AssetAdministrationShellDescriptor> descriptors = generateTestListOfDescriptors(NUM_GEN_DESCRIPTORS);
                descriptors.get(5).setId("descriptor7");

                assertThrows(Exception.class, () -> transactionsService.insertBulkAasDescriptors(descriptors));

                assertThat(getAllAasDescriptors()).isEmpty();
        }

        @Test
        public void whenBulkDeleteCorrectAasDescriptors_ReturnsSuccessAndCommit() {

                List<AssetAdministrationShellDescriptor> descriptors = generateTestListOfDescriptors(NUM_GEN_DESCRIPTORS);

                addAllNonTransactionally(descriptors);

                List<AssetAdministrationShellDescriptor> toDeleteDescriptors = new ArrayList<>(descriptors.subList(0, 3));

                TransactionResponse response = transactionsService.deleteBulkAasDescriptors(toDeleteDescriptors);

                Assertions.assertEquals(TransactionResponse.TransactionStatus.SUCCESSFUL, response.transactionStatus());

                assertThat(getAllAasDescriptors()).doesNotContainAnyElementsOf(toDeleteDescriptors);
        }

        @Test
        public void whenBulkDeleteIncorrectAasDescriptors_ReturnsFailureAndDoesntCommit() {

                List<AssetAdministrationShellDescriptor> descriptors = generateTestListOfDescriptors(NUM_GEN_DESCRIPTORS);

                addAllNonTransactionally(descriptors);

                List<AssetAdministrationShellDescriptor> toDeleteDescriptors = new ArrayList<>(descriptors.subList(0, 3));
                toDeleteDescriptors.add(new AssetAdministrationShellDescriptor("incorrectDescriptor1"));

                assertThrows(Exception.class, () -> transactionsService.deleteBulkAasDescriptors(toDeleteDescriptors));

                assertThat(getAllAasDescriptors()).containsExactlyInAnyOrderElementsOf(descriptors);
        }

        @Test
        public void whenBulkUpdateCorrectAasDescriptors_ReturnsSuccessAndCommit() {

                List<AssetAdministrationShellDescriptor> descriptors = generateTestListOfDescriptors(NUM_GEN_DESCRIPTORS);

                addAllNonTransactionally(descriptors);

                List<AssetAdministrationShellDescriptor> updatedDescriptors = generateTestListOfDescriptors(NUM_GEN_DESCRIPTORS);
                updatedDescriptors.forEach(desc -> desc.idShort(desc.getId() + "_idShort"));

                TransactionResponse response = transactionsService.putBulkAasDescriptors(updatedDescriptors);

                Assertions.assertEquals(TransactionResponse.TransactionStatus.SUCCESSFUL, response.transactionStatus());

                assertThat(getAllAasDescriptors()).containsExactlyInAnyOrderElementsOf(updatedDescriptors);
        }

        @Test
        public void whenBulkUpdateIncorrectAasDescriptors_ReturnsFailureAndDoesntCommit() {

                List<AssetAdministrationShellDescriptor> descriptors = generateTestListOfDescriptors(NUM_GEN_DESCRIPTORS);

                addAllNonTransactionally(descriptors);

                List<AssetAdministrationShellDescriptor> updatedDescriptors = generateTestListOfDescriptors(NUM_GEN_DESCRIPTORS);
                updatedDescriptors.forEach(desc -> desc.idShort(desc.getId() + "_idShort"));

                updatedDescriptors.get(2).setId("incorrectDescriptor");

                assertThrows(Exception.class, () -> transactionsService.putBulkAasDescriptors(updatedDescriptors));

                assertThat(getAllAasDescriptors()).containsExactlyInAnyOrderElementsOf(descriptors);
        }

        protected List<AssetAdministrationShellDescriptor> getAllAasDescriptors() {
                return storage.getAllAasDescriptors(new PaginationInfo(null, null), new DescriptorFilter(null, null)).getResult();
        }

        private static List<AssetAdministrationShellDescriptor> generateTestListOfDescriptors(int howMany) {
                return IntStream.range(0, howMany).mapToObj(i -> new AssetAdministrationShellDescriptor("descriptor" + i)).collect(Collectors.toList());
        }

        private void addAllNonTransactionally(List<AssetAdministrationShellDescriptor> descriptors) {
                descriptors.forEach(desc -> storage.insertAasDescriptor(desc));
        }

}
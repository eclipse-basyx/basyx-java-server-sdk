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

package org.eclipse.digitaltwin.basyx.aasregistry.service.storage.mongodb;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.eclipse.digitaltwin.basyx.aasregistry.model.AssetAdministrationShellDescriptor;
import org.eclipse.digitaltwin.basyx.aasregistry.service.errors.AasDescriptorAlreadyExistsException;
import org.eclipse.digitaltwin.basyx.aasregistry.service.errors.AasDescriptorNotFoundException;
import org.eclipse.digitaltwin.basyx.aasregistry.service.storage.AasRegistryStorage;
import org.eclipse.digitaltwin.basyx.aasregistry.service.storage.AasTransactionsService;
import org.eclipse.digitaltwin.basyx.aasregistry.service.storage.TransactionResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * MongoDb Implementation of the AAS Transactions Service
 * 
 * @author mateusmolina
 */
@Service
@Transactional(rollbackFor = { AasDescriptorAlreadyExistsException.class, AasDescriptorNotFoundException.class })
public class MongoDbAasTransactionsService implements AasTransactionsService {

	private final AasRegistryStorage storage;

	private final Map<Long, TransactionResponse> transactionStatusMap = new ConcurrentHashMap<>();
	private final AtomicLong transactionIdCounter = new AtomicLong(0);

	public MongoDbAasTransactionsService(AasRegistryStorage storage) {
		this.storage = storage;
	}

	@Override
	public TransactionResponse insertBulkAasDescriptors(List<AssetAdministrationShellDescriptor> descriptors) {
		long transactionId = transactionIdCounter.incrementAndGet();
		transactionStatusMap.put(transactionId, new TransactionResponse(transactionId, TransactionResponse.TransactionStatus.RUNNING, null));

		descriptors.forEach(desc -> storage.insertAasDescriptor(desc));

		return new TransactionResponse(transactionId, TransactionResponse.TransactionStatus.SUCCESSFUL, null);
	}

	@Override
	public TransactionResponse deleteBulkAasDescriptors(List<AssetAdministrationShellDescriptor> descriptors) {
		long transactionId = transactionIdCounter.incrementAndGet();
		transactionStatusMap.put(transactionId, new TransactionResponse(transactionId, TransactionResponse.TransactionStatus.RUNNING, null));

		descriptors.forEach(desc -> storage.removeAasDescriptor(desc.getId()));

		return new TransactionResponse(transactionId, TransactionResponse.TransactionStatus.SUCCESSFUL, null);
	}

	@Override
	public TransactionResponse putBulkAasDescriptors(List<AssetAdministrationShellDescriptor> descriptors) {
		long transactionId = transactionIdCounter.incrementAndGet();
		transactionStatusMap.put(transactionId, new TransactionResponse(transactionId, TransactionResponse.TransactionStatus.RUNNING, null));

		descriptors.forEach(desc -> storage.replaceAasDescriptor(desc.getId(), desc));

		return new TransactionResponse(transactionId, TransactionResponse.TransactionStatus.SUCCESSFUL, null);
	}

	@Override
	public TransactionResponse getTransactionResponse(long transactionResponseId) {
		return transactionStatusMap.get(transactionResponseId);
	}
}

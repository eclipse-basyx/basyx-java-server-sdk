/*******************************************************************************
 * Copyright (C) 2023 DFKI GmbH (https://www.dfki.de/en/web)
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
package org.eclipse.digitaltwin.basyx.submodelregistry.service.storage.memory;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

class ThreadSafeAccess {

	private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	private final ReadLock readLock = lock.readLock();
	private final WriteLock writeLock = lock.writeLock();

	public <A> void write(Consumer<A> consumer, A arg1) {
		runWithLock(consumer, arg1, readLock);
	}

	public <A, B> void write(BiConsumer<A, B> consumer, A arg1, B arg2) {
		runWithLock(consumer, arg1, arg2, writeLock);
	}

	public <A, T> T read(Function<A, T> func, A arg1) {
		return runWithLock(func, arg1, readLock);
	}
	
	public <T> T write(Supplier<T> supplier) {
		return runWithLock(supplier, writeLock);
	}

	private <T> T runWithLock(Supplier<T> supplier, Lock lock) {
		try {
			lock.lock();
			return supplier.get();
		} finally {
			lock.unlock();
		}
	}

	private <A> void runWithLock(Consumer<A> consumer, A arg1, Lock lock) {
		try {
			lock.lock();
			consumer.accept(arg1);
		} finally {
			lock.unlock();
		}
	}

	private <T, A> T runWithLock(Function<A, T> func, A arg1, Lock lock) {
		try {
			lock.lock();
			return func.apply(arg1);
		} finally {
			lock.unlock();
		}
	}
	
	private <A, B> void runWithLock(BiConsumer<A, B> consumer, A arg1, B arg2, Lock lock) {
		try {
			lock.lock();
			consumer.accept(arg1, arg2);
		} finally {
			lock.unlock();
		}
	}
}
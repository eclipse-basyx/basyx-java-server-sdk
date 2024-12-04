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

package org.eclipse.digitaltwin.basyx.common.backend;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * Utility class for thread-safe access at the instance level
 * 
 * @author mateusmolina
 */
public class InstanceScopedThreadSafeAccess {
    private final ConcurrentHashMap<Object, ThreadSafeAccess> accessMap = new ConcurrentHashMap<>();

    public <T> T read(Supplier<T> supplier, Object instanceLock) {
        return getAccess(instanceLock).read(supplier);
    }

    public void read(Runnable action, Object instanceLock) {
        getAccess(instanceLock).read(action);
    }

    public <T> T write(Supplier<T> supplier, Object instanceLock) {
        return getAccess(instanceLock).write(supplier);
    }

    public void write(Runnable action, Object instanceLock) {
        getAccess(instanceLock).write(action);
    }

    public void removeLock(Object instanceLock) {
        accessMap.remove(instanceLock);
    }

    private ThreadSafeAccess getAccess(Object instanceLock) {
        return accessMap.computeIfAbsent(instanceLock, k -> new ThreadSafeAccess());
    }
}

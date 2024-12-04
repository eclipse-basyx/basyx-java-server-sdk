package org.eclipse.digitaltwin.basyx.common.backend;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

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

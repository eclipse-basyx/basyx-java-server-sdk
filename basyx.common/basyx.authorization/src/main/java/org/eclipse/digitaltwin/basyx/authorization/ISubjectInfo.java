package org.eclipse.digitaltwin.basyx.authorization;

import org.springframework.security.core.context.SecurityContextHolder;

public interface ISubjectInfo<T> {
    public T get();
}

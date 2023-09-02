package org.eclipse.digitaltwin.basyx.authorization;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class SubjectInfoFromSecurityContextProvider implements ISubjectInfoProvider {
    public ISubjectInfo<?> get() {
        return (ISubjectInfo<Object>) () -> SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}

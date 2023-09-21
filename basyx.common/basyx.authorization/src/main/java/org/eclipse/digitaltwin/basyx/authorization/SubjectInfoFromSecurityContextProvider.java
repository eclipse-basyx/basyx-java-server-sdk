package org.eclipse.digitaltwin.basyx.authorization;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SubjectInfoFromSecurityContextProvider implements ISubjectInfoProvider {
    public ISubjectInfo<?> get() {
        return () -> Optional.ofNullable(SecurityContextHolder.getContext()).map(SecurityContext::getAuthentication).map(Authentication::getPrincipal).orElse(null);
    }
}

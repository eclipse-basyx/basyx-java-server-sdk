package org.eclipse.digitaltwin.basyx.authorization;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class JWTRoleAuthenticator implements IRoleAuthenticator {
    @Autowired
    private final ISubjectInfoProvider subjectInfoProvider;

    public JWTRoleAuthenticator(ISubjectInfoProvider subjectInfoProvider) {
        this.subjectInfoProvider = subjectInfoProvider;
    }

    @Override
    public List<String> getRoles() {
        final ISubjectInfo<?> subjectInfo = subjectInfoProvider.get();
        if (subjectInfo != null) {
            final Object obj = subjectInfo.get();
            if (obj instanceof Jwt) {
                final Jwt jwt = (Jwt) obj;
                final List<String> roles = jwt.getClaimAsStringList("roles");
                return roles;
            }
        } else {
            return Collections.emptyList();
        }
        return Collections.emptyList();
    }
}

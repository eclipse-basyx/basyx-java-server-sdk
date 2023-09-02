package org.eclipse.digitaltwin.basyx.authorization;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class JWTRoleAuthenticator implements IRoleAuthenticator {
    @Autowired
    private final ISubjectInfoProvider subjectInfoProvider;

    public JWTRoleAuthenticator(ISubjectInfoProvider subjectInfoProvider) {
        this.subjectInfoProvider = subjectInfoProvider;
    }

    @Override
    public List<String> getRoles() {
        final Object subjectInfo = subjectInfoProvider.get();
        if (subjectInfo instanceof Jwt) {
            final Jwt jwt = (Jwt) subjectInfo;
            final String scopeString = jwt.getClaimAsString("scope");
            final List<String> scopes = Arrays.asList(scopeString.split("\\s+"));
            return scopes;
        }
        return null;
    }
}

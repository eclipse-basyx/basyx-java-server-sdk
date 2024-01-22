/*******************************************************************************
 * Copyright (C) 2023 the Eclipse BaSyx Authors
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

package org.eclipse.digitaltwin.basyx.authorization.rbac;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.eclipse.digitaltwin.basyx.authorization.CommonAuthorizationConfig;
import org.eclipse.digitaltwin.basyx.authorization.SubjectInformation;
import org.eclipse.digitaltwin.basyx.authorization.SubjectInformationProvider;
import org.eclipse.digitaltwin.basyx.core.exceptions.NullSubjectException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

/**
 * Implementation of the {@link RoleProvider} for Keycloak based identity providers
 * 
 * @author danish
 */
@Service
@ConditionalOnExpression(value = "${" + CommonAuthorizationConfig.ENABLED_PROPERTY_KEY + ":false} and ('${" + CommonAuthorizationConfig.JWT_BEARER_TOKEN_PROVIDER_PROPERTY_KEY + "}'.equals('keycloak') or '${" + CommonAuthorizationConfig.JWT_BEARER_TOKEN_PROVIDER_PROPERTY_KEY + "}'.isEmpty())")
public class KeycloakRoleProvider implements RoleProvider {

	private static final String CLAIM_REALM_ACCESS = "realm_access";

	private static final String CLAIM_ROLES = "roles";

	private SubjectInformationProvider<Object> subjectInformationProvider;

	public KeycloakRoleProvider(SubjectInformationProvider<Object> subjectInformationProvider) {
		this.subjectInformationProvider = subjectInformationProvider;
	}

	@Override
	public List<String> getRoles() {
		SubjectInformation<Object> subjectInfo = getSubjectInformation();

		Jwt jwt = (Jwt) subjectInfo.get();

		validateJwt(jwt);
		
		Map<String, Collection<String>> realmAccess = jwt.getClaim(CLAIM_REALM_ACCESS);

		return getRolesFromRealmAccess(realmAccess);
	}

	private List<String> getRolesFromRealmAccess(Map<String, Collection<String>> realmAccess) {
		if (realmAccess == null || realmAccess.isEmpty())
			return new ArrayList<>();
		
		Collection<String> roles = realmAccess.get(CLAIM_ROLES);
			
		if (roles == null || roles.isEmpty())
			return new ArrayList<>(); 

		return new ArrayList<>(roles);
	}

	private void validateJwt(Jwt jwt) {
		if (jwt == null)
			throw new NullSubjectException("Jwt subject information is null.");
	}

	private SubjectInformation<Object> getSubjectInformation() {
		SubjectInformation<Object> subjectInfo = subjectInformationProvider.get();

		if (subjectInfo == null)
			throw new NullSubjectException("Subject information is null.");
		
		return subjectInfo;
	}

}

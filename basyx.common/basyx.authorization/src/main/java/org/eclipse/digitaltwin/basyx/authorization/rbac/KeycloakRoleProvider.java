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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.digitaltwin.basyx.authorization.CommonAuthorizationProperties;
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
@ConditionalOnExpression(value = "${" + CommonAuthorizationProperties.ENABLED_PROPERTY_KEY + ":false} and ('${" + CommonAuthorizationProperties.JWT_BEARER_TOKEN_PROVIDER_PROPERTY_KEY + "}'.equals('keycloak') or '${" + CommonAuthorizationProperties.JWT_BEARER_TOKEN_PROVIDER_PROPERTY_KEY + "}'.isEmpty())")
public class KeycloakRoleProvider implements RoleProvider {

	private static final String CLAIM_REALM_ACCESS = "realm_access";
	private static final String CLAIM_RESOURCE_ACCESS = "resource_access";

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
		
		Map<String, Collection<String>> realmAccess = new HashMap<>();
		Map<String, Collection<String>> resourceAccess = new HashMap<>();
		
		if (jwt.hasClaim(CLAIM_REALM_ACCESS))
			realmAccess = jwt.getClaim(CLAIM_REALM_ACCESS);
		
		if (jwt.hasClaim(CLAIM_RESOURCE_ACCESS))
			resourceAccess = jwt.getClaim(CLAIM_RESOURCE_ACCESS);

		return extractRolesFromClaims(realmAccess, resourceAccess);
	}

	private List<String> extractRolesFromClaims(Map<String, Collection<String>> realmAccess, Map<String, Collection<String>> resourceAccess) {
		if (realmAccess.isEmpty() && resourceAccess.isEmpty())
			return new ArrayList<>();
		
		Collection<String> realmRoles = realmAccess.get(CLAIM_ROLES);
		Collection<String> resourceRoles = resourceAccess.get(CLAIM_ROLES);
		
		if ((realmRoles == null || realmRoles.isEmpty()) && (resourceRoles == null || resourceRoles.isEmpty()))
			return new ArrayList<>();
		
		return mergeRoles(realmRoles, resourceRoles);		
	}

	private List<String> mergeRoles(Collection<String> realmRoles, Collection<String> resourceRoles) {
		
		if (realmRoles == null || realmRoles.isEmpty())
			return new ArrayList<>(resourceRoles);
		
		if (resourceRoles == null || resourceRoles.isEmpty())
			return new ArrayList<>(realmRoles);
		
		List<String> rolesUnion = new ArrayList<>(realmRoles);
		
		resourceRoles.stream().filter(resourceRole -> !rolesUnion.contains(resourceRole)).forEach(rolesUnion::add);

		return rolesUnion;
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

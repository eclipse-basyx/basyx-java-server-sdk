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

package org.eclipse.digitaltwin.basyx.authorization;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.eclipse.digitaltwin.basyx.authorization.rbac.KeycloakRoleProvider;
import org.eclipse.digitaltwin.basyx.core.exceptions.NullSubjectException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.*;

/**
 * Tests the behaviour of {@link KeycloakRoleProvider}
 * 
 * @author danish
 */
public class TestKeycloakRoleProvider {

	@Mock
    private SubjectInformationProvider<Object> subjectInformationProvider;

	@Mock
    private Jwt jwt;

	@InjectMocks
    private KeycloakRoleProvider keycloakRoleProvider;

	@Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        @SuppressWarnings("unchecked")
        SubjectInformation<Object> subjectInfo = mock(SubjectInformation.class);
        when(subjectInfo.get()).thenReturn(jwt);
        when(subjectInformationProvider.get()).thenReturn(subjectInfo);
    }

	@Test
    public void getRoles_whenBothRealmAndResourceRolesPresent() {
        Map<String, Collection<String>> realmAccess = new HashMap<>();
        realmAccess.put("roles", Arrays.asList("ROLE_USER", "ROLE_ADMIN"));

        Map<String, Map<String, Collection<String>>> resourceAccess = new HashMap<>();
        resourceAccess.put("client1", new HashMap<>(Collections.singletonMap("roles", Arrays.asList("ROLE_SUPERUSER", "ROLE_ADMIN"))));
        resourceAccess.put("client2", new HashMap<>(Collections.singletonMap("roles", Arrays.asList("ROLE_SUPPORT"))));

        when(jwt.hasClaim("realm_access")).thenReturn(true);
        when(jwt.hasClaim("resource_access")).thenReturn(true);
        when(jwt.getClaim("realm_access")).thenReturn(realmAccess);
        when(jwt.getClaim("resource_access")).thenReturn(resourceAccess);

        List<String> roles = keycloakRoleProvider.getRoles();

        assertEquals(4, roles.size());
        assertTrue(roles.contains("ROLE_USER"));
        assertTrue(roles.contains("ROLE_ADMIN"));
        assertTrue(roles.contains("ROLE_SUPERUSER"));
        assertTrue(roles.contains("ROLE_SUPPORT"));
    }

	@Test
    public void getRoles_whenOnlyRealmRolesPresent() {
        Map<String, Collection<String>> realmAccess = new HashMap<>();
        realmAccess.put("roles", Arrays.asList("ROLE_USER", "ROLE_ADMIN"));

        when(jwt.hasClaim("realm_access")).thenReturn(true);
        when(jwt.hasClaim("resource_access")).thenReturn(true);
        when(jwt.getClaim("realm_access")).thenReturn(realmAccess);
        when(jwt.getClaim("resource_access")).thenReturn(Collections.emptyMap());

        List<String> roles = keycloakRoleProvider.getRoles();

        assertEquals(2, roles.size());
        assertTrue(roles.contains("ROLE_USER"));
        assertTrue(roles.contains("ROLE_ADMIN"));
    }

	@Test
    public void getRoles_whenOnlyResourceRolesPresent() {
        Map<String, Map<String, Collection<String>>> resourceAccess = new HashMap<>();
        resourceAccess.put("client1", new HashMap<>(Collections.singletonMap("roles", Arrays.asList("ROLE_SUPERUSER", "ROLE_SUPPORT"))));

        when(jwt.hasClaim("realm_access")).thenReturn(true);
        when(jwt.hasClaim("resource_access")).thenReturn(true);
        when(jwt.getClaim("realm_access")).thenReturn(Collections.emptyMap());
        when(jwt.getClaim("resource_access")).thenReturn(resourceAccess);

        List<String> roles = keycloakRoleProvider.getRoles();

        assertEquals(2, roles.size());
        assertTrue(roles.contains("ROLE_SUPERUSER"));
        assertTrue(roles.contains("ROLE_SUPPORT"));
    }

	@Test
    public void getRoles_whenNoRolesPresent() {
        when(jwt.hasClaim("realm_access")).thenReturn(true);
        when(jwt.hasClaim("resource_access")).thenReturn(true);
        when(jwt.getClaim("realm_access")).thenReturn(Collections.emptyMap());
        when(jwt.getClaim("resource_access")).thenReturn(Collections.emptyMap());

        List<String> roles = keycloakRoleProvider.getRoles();

        assertTrue(roles.isEmpty());
    }

	@Test(expected = NullSubjectException.class)
    public void getRoles_whenJwtIsNull() {
        @SuppressWarnings("unchecked")
        SubjectInformation<Object> subjectInfo = mock(SubjectInformation.class);
        when(subjectInfo.get()).thenReturn(null);
        when(subjectInformationProvider.get()).thenReturn(subjectInfo);

        keycloakRoleProvider.getRoles();
    }

	@Test
    public void getRoles_whenRealmAccessNotPresentButResourceAccessPresent() {
        Map<String, Map<String, Collection<String>>> resourceAccess = new HashMap<>();
        resourceAccess.put("client1", new HashMap<>(Collections.singletonMap("roles", Arrays.asList("ROLE_SUPPORT", "ROLE_USER"))));

        when(jwt.hasClaim("realm_access")).thenReturn(false);
        when(jwt.hasClaim("resource_access")).thenReturn(true);
        when(jwt.getClaim("resource_access")).thenReturn(resourceAccess);

        List<String> roles = keycloakRoleProvider.getRoles();

        assertEquals(2, roles.size());
        assertTrue(roles.contains("ROLE_SUPPORT"));
        assertTrue(roles.contains("ROLE_USER"));
    }

	@Test
    public void getRoles_whenResourceAccessNotPresentButRealmAccessPresent() {
        Map<String, Collection<String>> realmAccess = new HashMap<>();
        realmAccess.put("roles", Arrays.asList("ROLE_USER", "ROLE_ADMIN"));

        when(jwt.hasClaim("resource_access")).thenReturn(false);
        when(jwt.hasClaim("realm_access")).thenReturn(true);
        when(jwt.getClaim("realm_access")).thenReturn(realmAccess);

        List<String> roles = keycloakRoleProvider.getRoles();

        assertEquals(2, roles.size());
        assertTrue(roles.contains("ROLE_USER"));
        assertTrue(roles.contains("ROLE_ADMIN"));
    }

	@Test
    public void getRoles_whenClaimNotPresent() {
        when(jwt.hasClaim("realm_access")).thenReturn(false);
        when(jwt.hasClaim("resource_access")).thenReturn(false);

        List<String> roles = keycloakRoleProvider.getRoles();

        assertTrue(roles.isEmpty());
    }
}

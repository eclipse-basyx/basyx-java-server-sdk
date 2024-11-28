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

package org.eclipse.digitaltwin.basyx.client.internal.authorization;

import org.eclipse.digitaltwin.basyx.client.internal.authorization.grant.AccessTokenProvider;
import org.eclipse.digitaltwin.basyx.client.internal.authorization.grant.ClientCredentialAccessTokenProvider;
import org.eclipse.digitaltwin.basyx.client.internal.authorization.grant.GrantType;
import org.eclipse.digitaltwin.basyx.client.internal.authorization.grant.PasswordCredentialAccessTokenProvider;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.*;

/**
 * Test for the{@link AccessTokenProviderFactory}
 *
 * @author fried, zielstor
 */
public class TestAccessTokenProviderFactory {

    private AccessTokenProviderFactory factory;
    private Collection<String> scopes;

    @Before
    public void setup() {
        scopes = Arrays.asList("scope1", "scope2");
    }

    @Test
    public void testCreateWithClientCredentials() {
        factory = new AccessTokenProviderFactory(GrantType.CLIENT_CREDENTIALS, scopes);
        factory.setClientCredentials("clientId", "clientSecret");

        AccessTokenProvider provider = factory.create();

        assertNotNull(provider);
        assertTrue(provider instanceof ClientCredentialAccessTokenProvider);
    }

    @Test
    public void testCreateWithPasswordCredentials() {
        factory = new AccessTokenProviderFactory(GrantType.PASSWORD, scopes);
        factory.setClientCredentials("clientId", "clientSecret");
        factory.setPasswordCredentials("username", "password");

        AccessTokenProvider provider = factory.create();

        assertNotNull(provider);
        assertTrue(provider instanceof PasswordCredentialAccessTokenProvider);
    }

    @Test
    public void testCreateWithMissingClientCredentials() {
        factory = new AccessTokenProviderFactory(GrantType.CLIENT_CREDENTIALS, scopes);

        assertThrows(IllegalArgumentException.class, () -> factory.create());
    }

    @Test
    public void testCreateWithMissingPasswordCredentials() {
        factory = new AccessTokenProviderFactory(GrantType.PASSWORD, scopes);
        factory.setClientCredentials("clientId", "clientSecret");

        assertThrows(IllegalArgumentException.class, () -> factory.create());
    }
}
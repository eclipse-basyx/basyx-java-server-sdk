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

package org.eclipse.digitaltwin.basyx.client.internal;

import static org.junit.Assert.*;
import org.eclipse.digitaltwin.basyx.client.internal.authorization.TokenManager;
import org.junit.Before;
import org.junit.Test;
import org.eclipse.digitaltwin.basyx.client.internal.authorization.credential.ClientCredential;
import org.eclipse.digitaltwin.basyx.client.internal.authorization.credential.PasswordCredential;
import org.eclipse.digitaltwin.basyx.client.internal.authorization.grant.PasswordCredentialAccessTokenProvider;

import java.io.IOException;

/**
 * Tests the behaviour of {@link TokenManager}
 * 
 * @author danish
 */
public class TokenManagerTest {

    private static final String TOKEN_ENDPOINT = "http://localhost:9096/realms/BaSyx/protocol/openid-connect/token";
    private TokenManager tokenManager;

    @Before
    public void setUp() {
        tokenManager = new TokenManager(TOKEN_ENDPOINT, new PasswordCredentialAccessTokenProvider(new PasswordCredential("basyx.reader", "basyxreader"), new ClientCredential("max-sso", "8ccc227xJflxGgtFkwwssHRZUh99nAAc")));
    }

    @Test
    public void retrieveNewAccessTokenWhenExpired() throws IOException, InterruptedException {        
        String initialAccessToken = tokenManager.getAccessToken();
        assertNotNull(initialAccessToken);

        long tokenLifetime = 2000;
        Thread.sleep(tokenLifetime + 5);

        String newAccessToken = tokenManager.getAccessToken();
        assertNotNull(newAccessToken);
        
        assertNotEquals(initialAccessToken, newAccessToken);
    }
    
    @Test
    public void provideSameAccessTokenWhenNotExpired() throws IOException, InterruptedException {        
        String initialAccessToken = tokenManager.getAccessToken();
        assertNotNull(initialAccessToken);

        long tokenLifetime = 800;
        Thread.sleep(tokenLifetime);

        String newAccessToken = tokenManager.getAccessToken();
        assertNotNull(newAccessToken);
        
        assertEquals(initialAccessToken, newAccessToken);
    }
    
    @Test
    public void retrieveNewAccessTokenUsingRefreshTokenWhenAccessTokenIsExpired() throws IOException, InterruptedException {
    	String initialAccessToken = tokenManager.getAccessToken();
        assertNotNull(initialAccessToken);

        long tokenLifetime = 2000;
        Thread.sleep(tokenLifetime + 15);

        String newAccessToken = tokenManager.getAccessToken();
        assertNotNull(newAccessToken);
        
        assertNotEquals(initialAccessToken, newAccessToken);
    }

    @Test
    public void retrieveNewAccessTokenUsingRefreshTokenWhenExpired() throws IOException, InterruptedException {
    	String initialAccessToken = tokenManager.getAccessToken();
        assertNotNull(initialAccessToken);

        long tokenLifetime = 4000;
        Thread.sleep(tokenLifetime + 5);

        String newAccessToken = tokenManager.getAccessToken();
        assertNotNull(newAccessToken);
        
        assertNotEquals(initialAccessToken, newAccessToken);
    }

    @Test
    // This test is for verifying the behavior described in the issue: https://github.com/eclipse-basyx/basyx-java-server-sdk/issues/530
    public void retrieveNewAccessTokenWhenSSOMaxReached() throws IOException, InterruptedException {
    	String initialAccessToken = tokenManager.getAccessToken();
        assertNotNull(initialAccessToken);
        
        long timeLimit = 6000;
        String intermediateAccessToken = null;
        
        while (timeLimit > 0) {
        	intermediateAccessToken = tokenManager.getAccessToken();
        	
        	Thread.sleep(2000);
        	
        	timeLimit -= 2000;
        }
        
        assertNotNull(intermediateAccessToken);
        assertNotEquals(initialAccessToken, intermediateAccessToken);
    }

}

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

import org.apache.commons.lang3.NotImplementedException;
import org.eclipse.digitaltwin.basyx.client.internal.authorization.credential.ClientCredential;
import org.eclipse.digitaltwin.basyx.client.internal.authorization.credential.PasswordCredential;
import org.eclipse.digitaltwin.basyx.client.internal.authorization.grant.AccessTokenProvider;
import org.eclipse.digitaltwin.basyx.client.internal.authorization.grant.ClientCredentialAccessTokenProvider;
import org.eclipse.digitaltwin.basyx.client.internal.authorization.grant.GrantType;
import org.eclipse.digitaltwin.basyx.client.internal.authorization.grant.PasswordCredentialAccessTokenProvider;

import java.util.ArrayList;
import java.util.Collection;

public class AccessTokenProviderFactory {

    private final GrantType grantType;
    private Collection<String> scopes;
    private String clientId;
    private String clientSecret;
    private String username;
    private String password;

    public AccessTokenProviderFactory(GrantType grantType, Collection<String> scopes){
        this.grantType = grantType;
        this.scopes = scopes;
    }

    public void setClientCredentials(String clientId, String clientSecret){
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    public void setPasswordCredentials(String username, String password){
        this.username = username;
        this.password = password;
    }

    public AccessTokenProvider create(){

        if(scopes == null)
            scopes = new ArrayList<>();

        switch(grantType){
            case CLIENT_CREDENTIALS:
                if(clientId == null || clientSecret == null)
                    throw new IllegalArgumentException("Client credentials credentials not set");

                return new ClientCredentialAccessTokenProvider(new ClientCredential(clientId, clientSecret),scopes);
            case PASSWORD:
                if(clientId == null)
                    throw new IllegalArgumentException("ClientId is not set");

                if(clientSecret == null)
                    clientSecret = "";

                if(username == null || password == null)
                    throw new IllegalArgumentException("Password credentials not set");

                return new PasswordCredentialAccessTokenProvider(new PasswordCredential(username,password),new ClientCredential(clientId, clientSecret),scopes);
            default:
                throw new NotImplementedException();
        }
    }
}
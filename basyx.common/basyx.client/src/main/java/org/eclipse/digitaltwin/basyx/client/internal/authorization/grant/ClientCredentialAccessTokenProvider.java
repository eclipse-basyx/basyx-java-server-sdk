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

package org.eclipse.digitaltwin.basyx.client.internal.authorization.grant;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;

import org.eclipse.digitaltwin.basyx.client.internal.authorization.credential.ClientCredential;
import org.eclipse.digitaltwin.basyx.core.exceptions.AccessTokenRetrievalException;

import com.nimbusds.oauth2.sdk.AccessTokenResponse;
import com.nimbusds.oauth2.sdk.Scope;
import com.nimbusds.oauth2.sdk.TokenRequest;
import com.nimbusds.oauth2.sdk.TokenResponse;
import com.nimbusds.oauth2.sdk.auth.Secret;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.token.RefreshToken;
import com.nimbusds.oauth2.sdk.AuthorizationGrant;
import com.nimbusds.oauth2.sdk.ClientCredentialsGrant;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.RefreshTokenGrant;
import com.nimbusds.oauth2.sdk.auth.ClientSecretBasic;
import com.nimbusds.oauth2.sdk.auth.ClientAuthentication;

/**
 * Access token provider for the Client Credentials flow
 * 
 * @author danish
 */
public class ClientCredentialAccessTokenProvider implements AccessTokenProvider {

	private final ClientCredential clientCredential;
	private Collection<String> scopes;

	public ClientCredentialAccessTokenProvider(ClientCredential clientCredential) {
		this.clientCredential = clientCredential;
	}

	public ClientCredentialAccessTokenProvider(ClientCredential clientCredential, Collection<String> scopes) {
		this(clientCredential);
		this.scopes = scopes;
	}

	@Override
	public AccessTokenResponse getAccessTokenResponse(String tokenEndpoint) {

		if (isPublicClient())
			throw new RuntimeException("The client cannnot be public client with Password Grant");

		AuthorizationGrant clientGrant = new ClientCredentialsGrant();

		ClientAuthentication clientAuth = new ClientSecretBasic(new ClientID(clientCredential.getClientId()), new Secret(clientCredential.getClientSecret()));

		URI tokenEndpointUri = getTokenEndpointUri(tokenEndpoint);

		TokenRequest request = new TokenRequest(tokenEndpointUri, clientAuth, clientGrant, Scope.parse(scopes));

		return getTokenResponse(request);
	}

	@Override
	public AccessTokenResponse getAccessTokenResponse(String tokenEndpoint, String refreshToken) {

		if (isPublicClient())
			throw new RuntimeException("The client cannnot be public client with Password Grant");

		RefreshToken refreshTokenObj = new RefreshToken(refreshToken);

		AuthorizationGrant refreshTokenGrant = new RefreshTokenGrant(refreshTokenObj);

		ClientAuthentication clientAuth = new ClientSecretBasic(new ClientID(clientCredential.getClientId()), new Secret(clientCredential.getClientSecret()));

		URI tokenEndpointUri = getTokenEndpointUri(tokenEndpoint);

		TokenRequest request = new TokenRequest(tokenEndpointUri, clientAuth, refreshTokenGrant);

		return getTokenResponse(request);
	}

	private AccessTokenResponse getTokenResponse(TokenRequest request) {
		TokenResponse response;
		
		try {
			response = TokenResponse.parse(request.toHTTPRequest().send());
		} catch (ParseException | IOException e) {
			throw new AccessTokenRetrievalException("Error occurred while retrieving access token" + e.getMessage());
		}
		
		if (!response.indicatesSuccess())
			throw new AccessTokenRetrievalException("Error occurred while retrieving access token" +  response.toErrorResponse().toString());

		return response.toSuccessResponse();
	}

	private boolean isPublicClient() {
		return clientCredential.getClientSecret().isBlank();
	}

	private URI getTokenEndpointUri(String tokenEndpoint) {
		try {
			return new URI(tokenEndpoint);
		} catch (URISyntaxException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

}

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

import java.io.IOException;

import org.eclipse.digitaltwin.basyx.client.internal.authorization.grant.AccessTokenProvider;
import org.eclipse.digitaltwin.basyx.core.exceptions.AccessTokenRetrievalException;

import com.nimbusds.oauth2.sdk.AccessTokenResponse;
import com.nimbusds.oauth2.sdk.token.AccessToken;
import com.nimbusds.oauth2.sdk.token.RefreshToken;

/**
 * Requests and manages the Access Tokens and Refresh Tokens.
 * 
 *  @author danish
 */
public class TokenManager {
	
	private String tokenEndpoint;
	private AccessTokenProvider accessTokenProvider;
	private String accessToken;
    private String refreshToken;
    private long accessTokenExpiryTime;
    private long refreshTokenExpiryTime;
	
	public TokenManager(String tokenEndpoint, AccessTokenProvider accessTokenProvider) {
		super();
		this.tokenEndpoint = tokenEndpoint;
		this.accessTokenProvider = accessTokenProvider;
	}

	public String getTokenEndpoint() {
		return tokenEndpoint;
	}

	public AccessTokenProvider getAccessTokenProvider() {
		return this.accessTokenProvider;
	}
	
	/**
	 * Provides access token
	 * 
	 * @return accessToken
	 * @throws IOException
	 */
	public synchronized String getAccessToken() throws IOException {

        if (accessToken != null && System.currentTimeMillis() < accessTokenExpiryTime)
            return accessToken;

        if (refreshToken != null && System.currentTimeMillis() < refreshTokenExpiryTime) {
            try {
				return requestAccessToken(accessTokenProvider.getAccessTokenResponse(tokenEndpoint, refreshToken));
			} catch (IOException e) {
				throw new AccessTokenRetrievalException("Error occurred while retrieving access token" + e.getMessage());
			}
        }

        try {
			return requestAccessToken(accessTokenProvider.getAccessTokenResponse(tokenEndpoint));
		} catch (IOException e) {
			throw new AccessTokenRetrievalException("Error occurred while retrieving access token" + e.getMessage());
		}
    }
	
	private String requestAccessToken(AccessTokenResponse accessTokenResponse) throws IOException {        
        AccessToken accessTokenObj = accessTokenResponse.getTokens().getAccessToken();
        accessToken = accessTokenObj.getValue();
        accessTokenExpiryTime = accessTokenObj.getLifetime();
        
        RefreshToken refreshTokenObj = accessTokenResponse.getTokens().getRefreshToken();
        
        if (refreshTokenObj != null) {
        	refreshToken = refreshTokenObj.getValue();
            refreshTokenExpiryTime = System.currentTimeMillis() + (30L * 24L * 60L * 60L * 1000L);
        }
        
        return accessToken;
    }
	
}

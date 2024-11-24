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

import net.minidev.json.JSONObject;

/**
 * Requests and manages the Access Tokens and Refresh Tokens.
 * 
 * @author danish
 */
public class TokenManager {

	private static final String REFRESH_EXPIRES_IN = "refresh_expires_in";
	private final String tokenEndpoint;
	private final AccessTokenProvider accessTokenProvider;
	private String accessToken;
	private String refreshToken;
	private long accessTokenExpiryTime;
	private long refreshTokenExpiryTime;

	public TokenManager(String tokenEndpoint, AccessTokenProvider accessTokenProvider) {
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
	 * Provides the access token, refreshing it if necessary.
	 * 
	 * @return the current valid access token
	 * @throws IOException
	 *             if an error occurs while retrieving the token
	 */
	public synchronized String getAccessToken() throws IOException {
		long currentTimeMillis = System.currentTimeMillis();

		if (accessToken != null && currentTimeMillis < accessTokenExpiryTime) {
			return accessToken;
		}

		if (refreshToken != null && currentTimeMillis < refreshTokenExpiryTime) {
			try {
				return updateTokens(accessTokenProvider.getAccessTokenResponse(tokenEndpoint, refreshToken), currentTimeMillis);
			} catch (IOException e) {
				throw new AccessTokenRetrievalException("Error occurred while retrieving access token: " + e.getMessage());
			}
		}

		try {
			return updateTokens(accessTokenProvider.getAccessTokenResponse(tokenEndpoint), currentTimeMillis);
		} catch (IOException e) {
			throw new AccessTokenRetrievalException("Error occurred while retrieving access token: " + e.getMessage());
		}
	}

	/**
	 * Updates the tokens and their expiry times.
	 * 
	 * @param accessTokenResponse
	 *            the response containing the new tokens
	 * @param currentTimeMillis
	 *            the current timestamp in milliseconds for consistency
	 * @return the new access token
	 * @throws IOException
	 *             if an error occurs while processing the response
	 */
	private String updateTokens(AccessTokenResponse accessTokenResponse, long currentTimeMillis) throws IOException {
		AccessToken accessTokenObj = accessTokenResponse.getTokens().getAccessToken();
		accessToken = accessTokenObj.getValue();
		accessTokenExpiryTime = currentTimeMillis + convertToMilliseconds(accessTokenObj.getLifetime());

		RefreshToken refreshTokenObj = accessTokenResponse.getTokens().getRefreshToken();

		if (refreshTokenObj != null) {
			refreshToken = refreshTokenObj.getValue();
			refreshTokenExpiryTime = calculateRefreshTokenExpiry(accessTokenResponse, currentTimeMillis);
		}

		return accessToken;
	}

	/**
	 * Extracts the refresh token's expiry time from the response.
	 * 
	 * @param accessTokenResponse
	 *            the response containing the refresh token
	 * @param currentTimeMillis
	 *            the current timestamp in milliseconds for consistency
	 * @return the expiry time in epoch millis, or 0 if not available
	 */
	private long calculateRefreshTokenExpiry(AccessTokenResponse accessTokenResponse, long currentTimeMillis) {
		JSONObject jsonObject = accessTokenResponse.toJSONObject();
		Number refreshExpiresInSeconds = jsonObject.getAsNumber(REFRESH_EXPIRES_IN);

		if (refreshExpiresInSeconds == null) {
			return 0;
		}

		return currentTimeMillis + convertToMilliseconds(refreshExpiresInSeconds.longValue());
	}

	private long convertToMilliseconds(long refreshExpiresInSeconds) {
		return refreshExpiresInSeconds * 1000L;
	}
}
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nimbusds.oauth2.sdk.AccessTokenResponse;
import com.nimbusds.oauth2.sdk.token.AccessToken;
import com.nimbusds.oauth2.sdk.token.RefreshToken;

import net.minidev.json.JSONObject;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTParser;
import com.nimbusds.jwt.SignedJWT;

import java.util.Date;

/**
 * Requests and manages the Access Tokens and Refresh Tokens.
 * 
 * @author danish
 */
public class TokenManager {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(TokenManager.class);

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
		accessTokenExpiryTime = calculateExpiryTime(accessTokenObj, currentTimeMillis);

		RefreshToken refreshTokenObj = accessTokenResponse.getTokens().getRefreshToken();

		if (refreshTokenObj != null) {
			refreshToken = refreshTokenObj.getValue();
			refreshTokenExpiryTime = calculateRefreshExpiryTime(refreshTokenObj, accessTokenResponse, currentTimeMillis);
		}

		return accessToken;
	}

	/**
	 * Calculates the expiry time for a JWT token.
	 * First checks the 'exp' field in the JWT, falling back to 'expires_in'.
	 * 
	 * @param tokenObj the AccessToken or RefreshToken object
	 * @param currentTimeMillis the current timestamp in milliseconds
	 * @return the calculated expiry time in epoch millis
	 */
	private long calculateExpiryTime(AccessToken tokenObj, long currentTimeMillis) {
		String tokenValue = tokenObj.getValue();
		try {
			JWT jwt = JWTParser.parse(tokenValue);
			if (jwt instanceof SignedJWT) {
				Date exp = ((SignedJWT) jwt).getJWTClaimsSet().getExpirationTime();
				if (exp != null) {
					return exp.getTime();
				}
			}
		} catch (Exception e) {
			LOGGER.error("Unable to find 'exp' claim inside Access Token! Falling back to the alternative, the 'expires_in' field");
		}
		
		return currentTimeMillis + convertToMilliseconds(tokenObj.getLifetime());
	}

	/**
	 * Calculates the expiry time for a refresh token.
	 * First checks the 'exp' field in the JWT refresh token, falling back to 'refresh_expires_in'.
	 * 
	 * @param refreshTokenObj the RefreshToken object
	 * @param accessTokenResponse the response containing the refresh token
	 * @param currentTimeMillis the current timestamp in milliseconds
	 * @return the calculated expiry time in epoch millis
	 */
	private long calculateRefreshExpiryTime(RefreshToken refreshTokenObj, AccessTokenResponse accessTokenResponse, long currentTimeMillis) {
		String tokenValue = refreshTokenObj.getValue();
		try {
			JWT jwt = JWTParser.parse(tokenValue);
			if (jwt instanceof SignedJWT) {
				Date exp = ((SignedJWT) jwt).getJWTClaimsSet().getExpirationTime();
				if (exp != null) {
					return exp.getTime();
				}
			}
		} catch (Exception e) {
			LOGGER.error("Unable to find 'exp' claim inside Refresh Token! Falling back to the alternative, the '{}' field", REFRESH_EXPIRES_IN);
		}
		
		JSONObject jsonObject = accessTokenResponse.toJSONObject();
		Number refreshExpiresInSeconds = jsonObject.getAsNumber(REFRESH_EXPIRES_IN);

		if (refreshExpiresInSeconds == null) {
			return 0;
		}

		return currentTimeMillis + convertToMilliseconds(refreshExpiresInSeconds.longValue());
	}

	private long convertToMilliseconds(long seconds) {
		return seconds * 1000L;
	}
}

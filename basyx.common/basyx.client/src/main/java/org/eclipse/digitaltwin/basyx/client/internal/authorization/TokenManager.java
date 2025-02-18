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
import java.text.ParseException;

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
import java.time.Instant;

public class TokenManager {

	private static final Logger LOGGER = LoggerFactory.getLogger(TokenManager.class);

	private static final String EXPIRES_IN = "expires_in";
	private static final String REFRESH_EXPIRES_IN = "refresh_expires_in";
	private final String tokenEndpoint;
	private final AccessTokenProvider accessTokenProvider;
	private String accessToken;
	private String refreshToken;
	private Instant accessTokenExpiryTime;
	private Instant refreshTokenExpiryTime;

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
	public String getAccessToken() throws IOException {
		Instant currentTime = Instant.now();
		
		if (accessToken != null && currentTime.isBefore(accessTokenExpiryTime))
			return accessToken;

		synchronized (this) {
			if (accessToken != null && currentTime.isBefore(accessTokenExpiryTime))
				return accessToken;

			if (refreshToken != null && currentTime.isBefore(refreshTokenExpiryTime))
				return refreshAccessToken(currentTime);

			return obtainNewAccessToken(currentTime);
		}
	}

	/**
	 * Updates the tokens and their expiry times.
	 * 
	 * @param accessTokenResponse
	 *            the response containing the new tokens
	 * @param currentTime
	 *            the current timestamp for consistency
	 * @return the new access token
	 * @throws IOException
	 *             if an error occurs while processing the response
	 */
	private String updateTokens(AccessTokenResponse accessTokenResponse, Instant currentTime) throws IOException {
		AccessToken accessTokenObj = accessTokenResponse.getTokens().getAccessToken();
		accessToken = accessTokenObj.getValue();
		accessTokenExpiryTime = calculateExpiryTime(accessTokenObj, currentTime);

		RefreshToken refreshTokenObj = accessTokenResponse.getTokens().getRefreshToken();

		if (refreshTokenObj != null) {
			refreshToken = refreshTokenObj.getValue();
			refreshTokenExpiryTime = calculateRefreshExpiryTime(refreshTokenObj, accessTokenResponse, currentTime);
		}

		return accessToken;
	}

	/**
	 * Calculates the expiry time for a JWT token. First checks the 'exp' field in
	 * the JWT, falling back to 'expires_in'.
	 * 
	 * @param tokenObj
	 *            the AccessToken or RefreshToken object
	 * @param currentTime
	 *            the current timestamp
	 * @return the calculated expiry time as Instant
	 */
	private Instant calculateExpiryTime(AccessToken tokenObj, Instant currentTime) {
		String tokenValue = tokenObj.getValue();
		Date expirationDate = extractExpirationTimeAsDateFromToken(tokenValue);

		if (expirationDate != null)
			return expirationDate.toInstant();

		LOGGER.info("Unable to find 'exp' claim inside Access Token! Falling back to the alternative, the '{}' field.", EXPIRES_IN);

		return currentTime.plusSeconds(tokenObj.getLifetime());
	}

	/**
	 * Calculates the expiry time for a refresh token. First checks the 'exp' field
	 * in the JWT refresh token, falling back to 'refresh_expires_in'.
	 * 
	 * @param refreshTokenObj
	 *            the RefreshToken object
	 * @param accessTokenResponse
	 *            the response containing the refresh token
	 * @param currentTime
	 *            the current timestamp
	 * @return the calculated expiry time as Instant
	 */
	private Instant calculateRefreshExpiryTime(RefreshToken refreshTokenObj, AccessTokenResponse accessTokenResponse, Instant currentTime) {
		String tokenValue = refreshTokenObj.getValue();
		Date expirationDate = extractExpirationTimeAsDateFromToken(tokenValue);

		if (expirationDate != null)
			return expirationDate.toInstant();

		LOGGER.info("Unable to find 'exp' claim inside Refresh Token! Falling back to the alternative, the '{}' field", REFRESH_EXPIRES_IN);

		JSONObject jsonObject = accessTokenResponse.toJSONObject();
		Number refreshExpiresInSeconds = jsonObject.getAsNumber(REFRESH_EXPIRES_IN);

		if (refreshExpiresInSeconds == null)
			return Instant.EPOCH;

		return currentTime.plusSeconds(refreshExpiresInSeconds.longValue());
	}

	private Date extractExpirationTimeAsDateFromToken(String tokenValue) {
		try {
			JWT jwt = JWTParser.parse(tokenValue);

			if (jwt instanceof SignedJWT) {
				SignedJWT signedJwt = (SignedJWT) jwt;
				return signedJwt.getJWTClaimsSet().getExpirationTime();
			}
		} catch (ParseException e) {
			LOGGER.error("Failed to parse the token. Invalid JWT format: " + e.getMessage());
		} catch (Exception e) {
			LOGGER.error("Unexpected error occurred while extracting expiration time from the Token: " + e.getMessage());
		}

		return null;
	}

	private String obtainNewAccessToken(Instant currentTime) {
		try {
			return updateTokens(accessTokenProvider.getAccessTokenResponse(tokenEndpoint), currentTime);
		} catch (IOException e) {
			throw new AccessTokenRetrievalException("Error occurred while retrieving access token: " + e.getMessage());
		}
	}

	private String refreshAccessToken(Instant currentTime) {
		try {
			return updateTokens(accessTokenProvider.getAccessTokenResponse(tokenEndpoint, refreshToken), currentTime);
		} catch (IOException e) {
			throw new AccessTokenRetrievalException("Error occurred while retrieving access token: " + e.getMessage());
		}
	}
}

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

package org.eclipse.digitaltwin.basyx.aasenvironment.feature.authorization;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.core.DeserializationException;
import org.eclipse.digitaltwin.basyx.aasenvironment.AasEnvironment;
import org.eclipse.digitaltwin.basyx.aasenvironment.preconfiguration.AasEnvironmentPreconfigurationLoader;
import org.eclipse.digitaltwin.basyx.authorization.jwt.JwtTokenDecoder;
import org.eclipse.digitaltwin.basyx.authorization.jwt.PublicKeyUtils;
import org.eclipse.digitaltwin.basyx.client.internal.authorization.AccessTokenProviderFactory;
import org.eclipse.digitaltwin.basyx.client.internal.authorization.TokenManager;
import org.eclipse.digitaltwin.basyx.client.internal.authorization.grant.AccessTokenProvider;
import org.eclipse.digitaltwin.basyx.client.internal.authorization.grant.GrantType;
import org.eclipse.digitaltwin.basyx.core.exceptions.ZipBombException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jwt.SignedJWT;

import java.io.IOException;
import java.security.interfaces.RSAPublicKey;
import java.text.ParseException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;

public class AuthorizedAASEnvironmentPreconfigurationLoader extends AasEnvironmentPreconfigurationLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthorizedAASEnvironmentPreconfigurationLoader.class);
    private static final String TOKEN_ENDPOINT_SUFFIX = "/protocol/openid-connect/token";
    private static final String OPEN_ID_ENDPOINT_SUFFIX = "/.well-known/openid-configuration";
    private static final String JWKS_ISSUER_URI = "jwks_uri";
    private static final String KEYS = "keys";
    private static final String KID = "kid";
    private static final String SIG = "sig";
    private static final String USE = "use";
    private static final String MODULUS_KEY = "n";
    private static final String EXPONENT_KEY = "e";

    @Value("${basyx.aasenvironment.authorization.preconfiguration.token-endpoint:#{null}}")
    private String authenticationServerTokenEndpoint;

    @Value("${basyx.aasenvironment.authorization.preconfiguration.client-id:#{null}}")
    private String clientId;

    @Value("${basyx.aasenvironment.authorization.preconfiguration.client-secret:#{null}}")
    private String clientSecret;

    @Value("${basyx.aasenvironment.authorization.preconfiguration.username:#{null}}")
    private String username;

    @Value("${basyx.aasenvironment.authorization.preconfiguration.password:#{null}}")
    private String password;

    @Value("${basyx.aasenvironment.authorization.preconfiguration.grant-type:#{null}}")
    private String grantType;

    @Value("${basyx.aasenvironment.authorization.preconfiguration.scopes:#{null}}")
    private Collection<String> scopes;

    @Value("${basyx.environment:#{null}}")
    private String basyxEnvironment;

    private AccessTokenProvider tokenProvider;

    public AuthorizedAASEnvironmentPreconfigurationLoader(ResourceLoader resourceLoader, List<String> pathsToLoad) {
        super(resourceLoader, pathsToLoad);
    }

    @Override
    public void loadPreconfiguredEnvironments(AasEnvironment aasEnvironment)
            throws IOException, InvalidFormatException, DeserializationException, ZipBombException {
        if (isEnvironmentSet()) {
            setUpTokenProvider();
            configureSecurityContext();
        }
        super.loadPreconfiguredEnvironments(aasEnvironment);
        SecurityContextHolder.clearContext();
    }

    private void setUpTokenProvider() {
        AccessTokenProviderFactory factory = new AccessTokenProviderFactory(GrantType.valueOf(grantType), scopes);
        factory.setClientCredentials(clientId, clientSecret);
        factory.setPasswordCredentials(username, password);
        this.tokenProvider = factory.create();
    }

    private void configureSecurityContext() throws IOException {
        TokenManager tokenManager = new TokenManager(authenticationServerTokenEndpoint, tokenProvider);
        String adminToken = tokenManager.getAccessToken();

        LOGGER.debug("Retrieved admin token: {}", adminToken);

        String jwksUri = getJwksUriFromOpenIdConfig(authenticationServerTokenEndpoint);
        LOGGER.debug("JWKS URI: {}", jwksUri);

        SignedJWT signedJWT = null;
        try {
            signedJWT = SignedJWT.parse(adminToken);
        } catch (ParseException e) {
            throw new RuntimeException("Unable to parse JWT token: " + e.getMessage());
        }

        String tokenKid = signedJWT.getHeader().getKeyID();
        LOGGER.debug("JWT Key ID (kid): {}", tokenKid);

        RSAPublicKey rsaPublicKey = getPublicKeyFromJWKS(jwksUri, tokenKid);
        LOGGER.debug("Using RSA Public Key: {}", rsaPublicKey);

        boolean isValid = verifyJwt(signedJWT, tokenKid, rsaPublicKey);
        LOGGER.debug("Is JWT valid? {}", isValid);

        if (!isValid)
            throw new RuntimeException("Invalid JWT token signature!");

        Jwt jwt = JwtTokenDecoder.decodeJwt(adminToken, rsaPublicKey);
        SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(jwt));
    }

    private boolean verifyJwt(SignedJWT signedJWT, String tokenKid, RSAPublicKey rsaPublicKey) {
        try {
            JWSVerifier verifier = new RSASSAVerifier(rsaPublicKey);

            LOGGER.debug("JWSVerifier: {}", verifier);

            if (!signedJWT.verify(verifier)) {
                LOGGER.debug("JWT signature verification failed!");
                return false;
            }

            if (!signedJWT.getHeader().getAlgorithm().equals(JWSAlgorithm.RS256)) {
                LOGGER.debug("Unexpected JWT algorithm: {}", signedJWT.getHeader().getAlgorithm());
                return false;
            }

            LOGGER.debug("JWT Signature is valid!");
            LOGGER.debug("JWT Claims: {}", signedJWT.getJWTClaimsSet().getClaims());

            return true;
        } catch (Exception e) {
            LOGGER.debug("Error verifying JWT: {}", e.getMessage());
            return false;
        }
    }

    private String getJwksUriFromOpenIdConfig(String tokenEndpoint) throws IOException {
        String openIdConfigUrl = tokenEndpoint.replace(TOKEN_ENDPOINT_SUFFIX,
                OPEN_ID_ENDPOINT_SUFFIX);

        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(openIdConfigUrl, String.class);

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(response);

        if (jsonNode.has(JWKS_ISSUER_URI))
            return jsonNode.get(JWKS_ISSUER_URI).asText();

        throw new RuntimeException("Failed to retrieve JWKS URI from OpenID Configuration");
    }

    private RSAPublicKey getPublicKeyFromJWKS(String jwksUrl, String tokenKid) throws IOException {
        RestTemplate restTemplate = new RestTemplate();
        @SuppressWarnings("unchecked")
        Map<String, Object> jwksResponse = restTemplate.getForObject(jwksUrl, Map.class);

        if (jwksResponse != null && jwksResponse.get(KEYS) instanceof List) {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> keys = (List<Map<String, Object>>) jwksResponse.get(KEYS);

            for (Map<String, Object> keyData : keys) {
                String keyKid = (String) keyData.get(KID);

                if (tokenKid.equals(keyKid) && SIG.equals(keyData.get(USE))) {
                    LOGGER.debug("Found matching JWKS key for kid: {}", keyKid);

                    String modulus = (String) keyData.get(MODULUS_KEY);
                    String exponent = (String) keyData.get(EXPONENT_KEY);

                    return PublicKeyUtils.buildPublicKey(modulus, exponent);
                }
            }
        }

        throw new RuntimeException("No matching JWKS key found for kid: " + tokenKid);
    }

    private boolean isEnvironmentSet() {
        return basyxEnvironment != null;
    }
}
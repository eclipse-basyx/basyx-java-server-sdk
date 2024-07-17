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

import org.apache.commons.io.IOUtils;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.interfaces.RSAPublicKey;
import java.util.Collection;
import java.util.List;

public class AuthorizedAASEnvironmentPreconfigurationLoader extends AasEnvironmentPreconfigurationLoader {

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
    public void loadPreconfiguredEnvironments(AasEnvironment aasEnvironment) throws IOException, InvalidFormatException, DeserializationException {
        if(isEnvironmentSet()) {
            setUpTokenProvider();
            configureSecurityContext();
        }
        super.loadPreconfiguredEnvironments(aasEnvironment);
        SecurityContextHolder.clearContext();
    }


    private void setUpTokenProvider() {
        AccessTokenProviderFactory factory = new AccessTokenProviderFactory(GrantType.valueOf(grantType),scopes);
        factory.setClientCredentials(clientId, clientSecret);
        factory.setPasswordCredentials(username, password);
        this.tokenProvider = factory.create();
    }

    private void configureSecurityContext() throws FileNotFoundException, IOException {
        TokenManager tokenManager = new TokenManager(authenticationServerTokenEndpoint, tokenProvider);
        String adminToken = tokenManager.getAccessToken();

        String modulus = getStringFromFile("authorization/modulus.txt");
        String exponent = "AQAB";

        RSAPublicKey rsaPublicKey = PublicKeyUtils.buildPublicKey(modulus, exponent);

        Jwt jwt = JwtTokenDecoder.decodeJwt(adminToken, rsaPublicKey);

        SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(jwt));
    }


    private String getStringFromFile(String fileName) throws FileNotFoundException, IOException {
        ClassPathResource classPathResource = new ClassPathResource(fileName);
        InputStream in = classPathResource.getInputStream();
        return IOUtils.toString(in, StandardCharsets.UTF_8.name());
    }

    private boolean isEnvironmentSet() {
        return basyxEnvironment != null;
    }
}

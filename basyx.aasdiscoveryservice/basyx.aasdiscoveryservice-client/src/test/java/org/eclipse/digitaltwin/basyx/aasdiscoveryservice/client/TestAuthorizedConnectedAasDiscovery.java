/*******************************************************************************
 * Copyright (C) 2025 the Eclipse BaSyx Authors
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

package org.eclipse.digitaltwin.basyx.aasdiscoveryservice.client;

import org.eclipse.digitaltwin.basyx.aasdiscoveryservice.core.AasDiscoveryService;
import org.eclipse.digitaltwin.basyx.aasdiscoveryservice.core.AasDiscoveryServiceSuite;
import org.eclipse.digitaltwin.basyx.client.internal.authorization.TokenManager;
import org.eclipse.digitaltwin.basyx.client.internal.authorization.credential.ClientCredential;
import org.eclipse.digitaltwin.basyx.client.internal.authorization.grant.ClientCredentialAccessTokenProvider;
import org.junit.After;
import org.junit.Before;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

public class TestAuthorizedConnectedAasDiscovery extends AasDiscoveryServiceSuite {

    private static ConfigurableApplicationContext appContext;
    private static final String PROFILE = "authorization";

    @Before
    public void startAasDiscoveryService() {
        SpringApplication application = new SpringApplication(DummyAasDiscoveryServiceComponent.class);
        application.setAdditionalProfiles(PROFILE);
        appContext = application.run(new String[] {});
    }

    @After
    public void stopAasDiscoveryService() {
        appContext.close();
    }

    @Override
    protected AasDiscoveryService getAasDiscoveryService() {
        return new AuthorizedConnectedAasDiscoveryService("http://localhost:7788", new TokenManager("http://localhost:9096/realms/BaSyx/protocol/openid-connect/token", new ClientCredentialAccessTokenProvider(new ClientCredential("workstation-1", "nY0mjyECF60DGzNmQUjL81XurSl8etom"))));
    }

}


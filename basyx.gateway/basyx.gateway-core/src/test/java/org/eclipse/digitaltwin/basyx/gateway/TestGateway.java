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

package org.eclipse.digitaltwin.basyx.gateway;

import org.apache.http.HttpStatus;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultAssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultAssetInformation;
import org.eclipse.digitaltwin.basyx.gateway.core.DefaultGateway;
import org.eclipse.digitaltwin.basyx.gateway.core.exception.BaSyxComponentNotHealthyException;
import org.eclipse.digitaltwin.basyx.gateway.core.exception.RegistryUnavailableException;
import org.eclipse.digitaltwin.basyx.gateway.core.Gateway;
import org.eclipse.digitaltwin.basyx.http.Base64UrlEncodedIdentifier;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockserver.client.MockServerClient;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.HttpResponse;
import org.mockserver.verify.VerificationTimes;

public class TestGateway {

    public static final String AAS_REPOSITORY_URL = "http://localhost:6006";
    public static final String AAS_REGISTRY_URL = "http://localhost:7007";
    public static ClientAndServer mockBaSyxAasRepository;
    public static ClientAndServer mockAasRegistry;
    private Gateway gateway;

    @Before
    public void setUp(){
        mockBaSyxAasRepository = ClientAndServer.startClientAndServer(6006);
        mockAasRegistry = ClientAndServer.startClientAndServer(7007);
        gateway = new DefaultGateway();
    }

    @After
    public void tearDown(){
        mockBaSyxAasRepository.stop();
        mockAasRegistry.stop();
    }

    @Test
    public void testCreateAASWithHealthyBaSyxComponent(){
        getExpectations(HttpResponse.response()
                .withStatusCode(HttpStatus.SC_OK)
                .withHeader("aas_middleware", "BaSyx"), "{'status':'UP'}");
        gateway.createAAS(getDummyShell(), AAS_REPOSITORY_URL, null);
        verifyGetCall("localhost",6006,"/shells",1);
        verifyGetCall("localhost",6006,"/actuator/health",1);
    }

    @Test(expected = BaSyxComponentNotHealthyException.class)
    public void testCreateAASWithUnhealthyBaSyxComponent(){
        getExpectations(HttpResponse.response()
                .withStatusCode(HttpStatus.SC_OK)
                .withHeader("aas_middleware", "BaSyx"), "{'status':'DOWN'}");
        gateway.createAAS(getDummyShell(), AAS_REPOSITORY_URL, null);
    }

    @Test
    public void testCreateAASWithNonBaSyxComponent(){
        getExpectations(org.mockserver.model.HttpResponse.response()
                .withStatusCode(HttpStatus.SC_OK), "{'status':'UP'}");
        gateway.createAAS(getDummyShell(), AAS_REPOSITORY_URL, null);
        verifyGetCall("localhost",6006,"/shells",1);
        verifyGetCall("localhost",6006,"/actuator/health",0);
    }
    @Test(expected = RegistryUnavailableException.class)
    public void testCreateAASWithOfflineRegistry(){
        getExpectations(org.mockserver.model.HttpResponse.response()
                .withStatusCode(HttpStatus.SC_OK), "{'status':'UP'}");
        gateway.createAAS(getDummyShell(), AAS_REPOSITORY_URL, "http://localhost:8008");
        verifyPostCall("localhost",6006,"/shells",1);
        verifyDeleteCall("localhost",6006,"/shells/"+getEncodedShellIdentifer(),1);
    }
    @Test
    public void testCreateAASWithRegistryIntegration(){
        getExpectations(org.mockserver.model.HttpResponse.response()
                .withStatusCode(HttpStatus.SC_OK), "{'status':'UP'}");
        gateway.createAAS(getDummyShell(), AAS_REPOSITORY_URL, AAS_REGISTRY_URL);
        verifyPostCall("localhost",6006,"/shells",1);
        verifyPostCall("localhost",7007,"/shell-descriptors",1);
        verifyDeleteCall("localhost",6006,"/shells/"+getEncodedShellIdentifer(),0);
    }

    private static void getExpectations(HttpResponse SC_OK, String responseBody) {
        new MockServerClient("localhost", 6006)
                .when(org.mockserver.model.HttpRequest.request()
                        .withMethod("GET")
                        .withPath("/shells"))
                .respond(SC_OK
                        .withBody("{}"));
        new MockServerClient("localhost", 6006)
                .when(org.mockserver.model.HttpRequest.request()
                        .withMethod("POST")
                        .withPath("/shells"))
                .respond(HttpResponse.response()
                        .withStatusCode(HttpStatus.SC_OK)
                        .withHeader("aas_middleware", "BaSyx")
                        .withBody("{}"));
        new MockServerClient("localhost", 7007)
                .when(org.mockserver.model.HttpRequest.request()
                        .withMethod("POST")
                        .withPath("/shell-descriptors"))
                .respond(HttpResponse.response()
                        .withStatusCode(HttpStatus.SC_OK)
                        .withHeader("aas_middleware", "BaSyx")
                        .withBody("{}"));
        new MockServerClient("localhost", 6006)
                .when(org.mockserver.model.HttpRequest.request()
                        .withMethod("DELETE")
                        .withPath("/shells/"+getEncodedShellIdentifer()))
                .respond(HttpResponse.response()
                        .withStatusCode(HttpStatus.SC_NO_CONTENT)
                        .withHeader("aas_middleware", "BaSyx")
                        .withBody("{}"));
        new MockServerClient("localhost", 6006)
                .when(org.mockserver.model.HttpRequest.request()
                        .withMethod("GET")
                        .withPath("/actuator/health"))
                .respond(HttpResponse.response()
                        .withStatusCode(HttpStatus.SC_OK)
                        .withBody(responseBody));
    }

    private void verifyGetCall(String host, int port, String path, int timesCalled) {
        new MockServerClient(host, port).verify(org.mockserver.model.HttpRequest.request().withMethod("GET")
                        .withPath(path)
                , VerificationTimes.exactly(timesCalled));
    }
    private void verifyPostCall(String host, int port, String path, int timesCalled) {
        new MockServerClient(host, port).verify(org.mockserver.model.HttpRequest.request().withMethod("POST")
                        .withPath(path)
                , VerificationTimes.exactly(timesCalled));
    }
    private void verifyDeleteCall(String host, int port, String path, int timesCalled) {
        new MockServerClient(host, port).verify(org.mockserver.model.HttpRequest.request().withMethod("DELETE")
                        .withPath(path)
                , VerificationTimes.exactly(timesCalled));
    }

    private static AssetAdministrationShell getDummyShell(){
        return new DefaultAssetAdministrationShell.Builder().id("TestId").idShort("test").assetInformation(new DefaultAssetInformation.Builder().build()).build();
    }

    private static String getEncodedShellIdentifer(){
        return new Base64UrlEncodedIdentifier(getDummyShell().getId()).getEncodedIdentifier();
    }
}

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

package org.eclipse.digitaltwin.basyx.client.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

import java.net.http.HttpClient;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Tests that {@link ApiClientPool} shares one {@link HttpClient} per origin
 *
 * @author zielstorff
 */
public class ApiClientPoolTest {

  private static final int ACCESSED_SUBMODEL_COUNT = 200;

  private final ApiClientPool pool = ApiClientPool.getInstance();
  private final ObjectMapper mapper = new ObjectMapper();

  @Test
  public void sharesHttpClientAcrossServiceUrlsOfSameOrigin() {
    HttpClient aasRepoClient = pool.getOrCreateAasRepoApiClient("http://shared-origin.test:8081", mapper).getHttpClient();
    HttpClient aasClient = pool.getOrCreateAasServiceApiClient("http://shared-origin.test:8081/shells/YWFz", mapper).getHttpClient();
    HttpClient submodelClient = pool.getOrCreateSubmodelServiceApiClient("http://shared-origin.test:8081/submodels/c20", mapper).getHttpClient();

    assertSame(aasRepoClient, aasClient);
    assertSame(aasRepoClient, submodelClient);
  }

  @Test
  public void treatsDefaultPortAndHostCaseAsSameOrigin() {
    HttpClient explicitPortClient = pool.getOrCreateSubmodelRepoApiClient("https://Default-Port.test:443", mapper).getHttpClient();
    HttpClient implicitPortClient = pool.getOrCreateSubmodelServiceApiClient("https://default-port.test/submodels/c20", mapper).getHttpClient();

    assertSame(explicitPortClient, implicitPortClient);
  }

  @Test
  public void separatesHttpClientsPerSchemeHostAndPort() {
    HttpClient reference = pool.getOrCreateSubmodelServiceApiClient("http://separate-origin.test:8081/submodels/a", mapper).getHttpClient();

    assertNotSame(reference, pool.getOrCreateSubmodelServiceApiClient("https://separate-origin.test:8081/submodels/a", mapper).getHttpClient());
    assertNotSame(reference, pool.getOrCreateSubmodelServiceApiClient("http://other-origin.test:8081/submodels/a", mapper).getHttpClient());
    assertNotSame(reference, pool.getOrCreateSubmodelServiceApiClient("http://separate-origin.test:8082/submodels/a", mapper).getHttpClient());
  }

  @Test
  public void createsSingleHttpClientForManySubmodelsOfOneServer() {
    Set<HttpClient> httpClients = Collections.newSetFromMap(new IdentityHashMap<>());

    for (int i = 0; i < ACCESSED_SUBMODEL_COUNT; i++) {
      httpClients.add(pool.getOrCreateSubmodelServiceApiClient("http://single-client.test:8081/submodels/sm" + i, mapper).getHttpClient());
    }

    assertEquals(1, httpClients.size());
  }
}

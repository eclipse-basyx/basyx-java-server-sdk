/*******************************************************************************
 * Copyright (C) 2026 the Eclipse BaSyx Authors
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

import java.net.URI;
import java.net.http.HttpClient;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import tools.jackson.databind.ObjectMapper;

/**
 * Thread-safe pool for reusing ApiClient instances per URI.
 * All ApiClients that target the same scheme, host and port share one
 * {@link HttpClient}, so the number of HTTP connection pools and selector
 * threads is bounded by the number of servers rather than by the number of
 * AAS or submodels that are accessed.
 * Clearing or removing ApiClients releases the HttpClient of a server once
 * no cached ApiClient uses it. HttpClients are never closed by the pool, so
 * previously returned ApiClients keep working.
 * 
 * @author koort
 */
public class ApiClientPool {

  private static final ApiClientPool INSTANCE = new ApiClientPool();

  private static final int DEFAULT_HTTP_PORT = 80;
  private static final int DEFAULT_HTTPS_PORT = 443;

  private final Map<String, HttpClient> httpClientsByOrigin = new ConcurrentHashMap<>();

  private final Map<String, ApiClient> aasRepoApiClients = new ConcurrentHashMap<>();
  private final Map<String, ApiClient> aasServiceApiClients = new ConcurrentHashMap<>();

  private final Map<String, ApiClient> submodelRepoApiClients = new ConcurrentHashMap<>();
  private final Map<String, ApiClient> submodelServiceApiClients = new ConcurrentHashMap<>();

  private ApiClientPool() {
  }

  /**
   * Gets the singleton instance of the ApiClientPool.
   * 
   * @return the singleton ApiClientPool instance
   */
  public static ApiClientPool getInstance() {
    return INSTANCE;
  }

  /**
   * Gets or creates an AAS repository ApiClient for the given base URI and mapper.
   * 
   * @param baseUri the base URI of the repository
   * @param mapper  the ObjectMapper
   * @return cached or new ApiClient for AAS operations
   */
  public ApiClient getOrCreateAasRepoApiClient(String baseUri, ObjectMapper mapper) {
    return getOrCreateApiClient(aasRepoApiClients, baseUri, mapper);
  }

  /**
   * Gets or creates an AAS service ApiClient for the given service URL and mapper.
   * 
   * @param serviceUrl the URL of the AAS service
   * @param mapper  the ObjectMapper
   * @return cached or new ApiClient for Submodel operations
   */
  public ApiClient getOrCreateAasServiceApiClient(String serviceUrl, ObjectMapper mapper) {
    return getOrCreateApiClient(aasServiceApiClients, serviceUrl, mapper);
  }

  /**
   * Gets or creates a Submodel repository ApiClient for the given base URI and mapper.
   * 
   * @param baseUri the base URI of the repository
   * @param mapper  the ObjectMapper
   * @return cached or new ApiClient for Submodel operations
   */
  public ApiClient getOrCreateSubmodelRepoApiClient(String baseUri, ObjectMapper mapper) {
    return getOrCreateApiClient(submodelRepoApiClients, baseUri, mapper);
  }

  /**
   * Gets or creates a Submodel service ApiClient for the given service URL and mapper.
   * 
   * @param serviceUrl the URL of the Submodel service
   * @param mapper  the ObjectMapper
   * @return cached or new ApiClient for Submodel operations
   */
  public ApiClient getOrCreateSubmodelServiceApiClient(String serviceUrl, ObjectMapper mapper) {
    return getOrCreateApiClient(submodelServiceApiClients, serviceUrl, mapper);
  }

  private ApiClient getOrCreateApiClient(Map<String, ApiClient> apiClients, String url, ObjectMapper mapper) {
    return apiClients.compute(url, (uri, existingApiClient) -> {
      if (existingApiClient != null) {
        existingApiClient.setObjectMapper(mapper);
        return existingApiClient;
      }
      return new ApiClient(getOrCreateHttpClient(uri), mapper, uri);
    });
  }

  private HttpClient getOrCreateHttpClient(String url) {
    return httpClientsByOrigin.computeIfAbsent(toOrigin(url), origin -> HttpClient.newHttpClient());
  }

  private static String toOrigin(String url) {
    URI uri = URI.create(url);
    String scheme = toLowerCase(uri.getScheme());
    return scheme + "://" + toLowerCase(uri.getHost()) + ":" + resolvePort(scheme, uri.getPort());
  }

  private static int resolvePort(String scheme, int port) {
    if (port != -1) {
      return port;
    }
    return "https".equals(scheme) ? DEFAULT_HTTPS_PORT : DEFAULT_HTTP_PORT;
  }

  private static String toLowerCase(String value) {
    return value == null ? "" : value.toLowerCase(Locale.ROOT);
  }

  /**
   * Clears all cached AAS repository ApiClients.
   */
  public void clearAasRepoApiClients() {
    aasRepoApiClients.clear();
    releaseUnusedHttpClients();
  }

  /**
   * Clears all cached AAS service ApiClients.
   */
  public void clearAasServiceApiClients() {
    aasServiceApiClients.clear();
    releaseUnusedHttpClients();
  }

  /**
   * Clears all cached Submodel repository ApiClients.
   */
  public void clearSubmodelRepoApiClients() {
    submodelRepoApiClients.clear();
    releaseUnusedHttpClients();
  }

  /**
   * Clears all cached Submodel service ApiClients.
   */
  public void clearSubmodelServiceApiClients() {
    submodelServiceApiClients.clear();
    releaseUnusedHttpClients();
  }

  /**
   * Clears all cached ApiClients and releases all cached HttpClients.
   * Previously returned ApiClients keep working with their HttpClient.
   */
  public void clearAll() {
    aasRepoApiClients.clear();
    aasServiceApiClients.clear();
    submodelRepoApiClients.clear();
    submodelServiceApiClients.clear();
    httpClientsByOrigin.clear();
  }

  /**
   * Removes a specific AAS repository ApiClient by base URI.
   * 
   * @param baseUri the base URI to remove
   */
  public void removeAasRepoApiClient(String baseUri) {
    aasRepoApiClients.remove(baseUri);
    releaseUnusedHttpClients();
  }

  /**
   * Removes a specific AAS service ApiClient by service URL.
   * 
   * @param serviceUrl the service URL to remove
   */
  public void removeAasServiceApiClient(String serviceUrl) {
    aasServiceApiClients.remove(serviceUrl);
    releaseUnusedHttpClients();
  }

  /**
   * Removes a specific Submodel repository ApiClient by base URI.
   * 
   * @param baseUri the base URI to remove
   */
  public void removeSubmodelRepoApiClient(String baseUri) {
    submodelRepoApiClients.remove(baseUri);
    releaseUnusedHttpClients();
  }

  /**
   * Removes a specific Submodel service ApiClient by service URL.
   * 
   * @param serviceUrl the service URL to remove
   */
  public void removeSubmodelServiceApiClient(String serviceUrl) {
    submodelServiceApiClients.remove(serviceUrl);
    releaseUnusedHttpClients();
  }

  private void releaseUnusedHttpClients() {
    Set<String> originsInUse = Stream.of(aasRepoApiClients, aasServiceApiClients, submodelRepoApiClients, submodelServiceApiClients)
        .flatMap(apiClients -> apiClients.keySet().stream())
        .map(ApiClientPool::toOrigin)
        .collect(Collectors.toSet());
    httpClientsByOrigin.keySet().retainAll(originsInUse);
  }
}

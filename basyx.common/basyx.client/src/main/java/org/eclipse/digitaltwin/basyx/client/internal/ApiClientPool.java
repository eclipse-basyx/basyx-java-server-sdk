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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import java.net.http.HttpClient;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Thread-safe pool for reusing ApiClient instances per URI.
 * This avoids creating new HttpClient instances for each operation.
 * 
 * @author koort
 */
public class ApiClientPool {

  private static final ApiClientPool INSTANCE = new ApiClientPool();

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
    return aasRepoApiClients.computeIfAbsent(baseUri, uri -> {
      return new ApiClient(HttpClient.newBuilder(), mapper, uri);
    });
  }

  /**
   * Gets or creates an AAS service ApiClient for the given service URL and mapper.
   * 
   * @param serviceUrl the URL of the AAS service
   * @param mapper  the ObjectMapper
   * @return cached or new ApiClient for Submodel operations
   */
  public ApiClient getOrCreateAasServiceApiClient(String serviceUrl, ObjectMapper mapper) {
    return aasServiceApiClients.computeIfAbsent(serviceUrl, uri -> {
      return new ApiClient(HttpClient.newBuilder(), mapper, uri);
    });
  }

  /**
   * Gets or creates a Submodel repository ApiClient for the given base URI and mapper.
   * 
   * @param baseUri the base URI of the repository
   * @param mapper  the ObjectMapper
   * @return cached or new ApiClient for Submodel operations
   */
  public ApiClient getOrCreateSubmodelRepoApiClient(String baseUri, ObjectMapper mapper) {
    return submodelRepoApiClients.computeIfAbsent(baseUri, uri -> {
      return new ApiClient(HttpClient.newBuilder(), mapper, uri);
    });
  }

  /**
   * Gets or creates a Submodel service ApiClient for the given service URL and mapper.
   * 
   * @param serviceUrl the URL of the Submodel service
   * @param mapper  the ObjectMapper
   * @return cached or new ApiClient for Submodel operations
   */
  public ApiClient getOrCreateSubmodelServiceApiClient(String serviceUrl, ObjectMapper mapper) {
    return submodelServiceApiClients.computeIfAbsent(serviceUrl, uri -> {
      return new ApiClient(HttpClient.newBuilder(), mapper, uri);
    });
  }

  /**
   * Clears all cached AAS repository ApiClients.
   */
  public void clearAasRepoApiClients() {
    aasRepoApiClients.clear();
  }

  /**
   * Clears all cached AAS service ApiClients.
   */
  public void clearAasServiceApiClients() {
    aasServiceApiClients.clear();
  }

  /**
   * Clears all cached Submodel repository ApiClients.
   */
  public void clearSubmodelRepoApiClients() {
    submodelRepoApiClients.clear();
  }

  /**
   * Clears all cached Submodel service ApiClients.
   */
  public void clearSubmodelServiceApiClients() {
    submodelServiceApiClients.clear();
  }

  /**
   * Clears all cached ApiClients.
   */
  public void clearAll() {
    aasRepoApiClients.clear();
    aasServiceApiClients.clear();
    submodelRepoApiClients.clear();
    submodelServiceApiClients.clear();
  }

  /**
   * Removes a specific AAS repository ApiClient by base URI.
   * 
   * @param baseUri the base URI to remove
   */
  public void removeAasRepoApiClient(String baseUri) {
    aasRepoApiClients.remove(baseUri);
  }

  /**
   * Removes a specific AAS service ApiClient by service URL.
   * 
   * @param serviceUrl the service URL to remove
   */
  public void removeAasServiceApiClient(String serviceUrl) {
    aasServiceApiClients.remove(serviceUrl);
  }

  /**
   * Removes a specific Submodel repository ApiClient by base URI.
   * 
   * @param baseUri the base URI to remove
   */
  public void removeSubmodelRepoApiClient(String baseUri) {
    submodelRepoApiClients.remove(baseUri);
  }

  /**
   * Removes a specific Submodel service ApiClient by service URL.
   * 
   * @param serviceUrl the service URL to remove
   */
  public void removeSubmodelServiceApiClient(String serviceUrl) {
    submodelServiceApiClients.remove(serviceUrl);
  }
}

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


package org.eclipse.digitaltwin.basyx.submodelservice.client.internal;

import org.eclipse.digitaltwin.aas4j.v3.dataformat.json.JsonMapperFactory;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.json.SimpleAbstractTypeResolverFactory;
import org.eclipse.digitaltwin.basyx.client.internal.ApiClient;
import org.eclipse.digitaltwin.basyx.client.internal.ApiClientPool;
import org.eclipse.digitaltwin.basyx.client.internal.authorization.TokenManager;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Factory class for creating instances of {@link SubmodelServiceApi} using an {@link ApiClientPool}.
 * 
 * @author koort
 */
public class SubmodelServiceApiFactory {

  private SubmodelServiceApiFactory() {
  }

  /**
   * Creates a new instance of {@link SubmodelServiceApi} with default configuration.
   *
   * @param submodelServiceUrl the base URL of the submodel service
   * @return a new SubmodelServiceApi instance
   */
  public static SubmodelServiceApi create(String submodelServiceUrl) {
    return create(submodelServiceUrl, null, null);
  }

  /**
   * Creates a new instance of {@link SubmodelServiceApi} with the specified TokenManager.
   *
   * @param submodelServiceUrl the base URL of the submodel service
   * @param tokenManager the TokenManager
   * @return a new SubmodelServiceApi instance
   */
  public static SubmodelServiceApi create(String submodelServiceUrl, TokenManager tokenManager) {
    return create(submodelServiceUrl, null, tokenManager);
  }

  /**
   * Creates a new instance of {@link SubmodelServiceApi} with the specified ObjectMapper.
   *
   * @param submodelServiceUrl the base URL of the submodel service
   * @param objectMapper the ObjectMapper
   * @return a new SubmodelServiceApi instance
   */
  public static SubmodelServiceApi create(String submodelServiceUrl, ObjectMapper objectMapper) {
    return create(submodelServiceUrl, objectMapper, null);
  }

  /**
   * Creates a new instance of {@link SubmodelServiceApi} with the specified ObjectMapper and TokenManager.
   *
   * @param submodelServiceUrl the base URL of the submodel service
   * @param objectMapper the ObjectMapper
   * @param tokenManager the TokenManager
   * @return a new SubmodelServiceApi instance
   */
  public static SubmodelServiceApi create(String submodelServiceUrl, ObjectMapper objectMapper, TokenManager tokenManager) {
    ObjectMapper mapper = objectMapper != null ? objectMapper : new JsonMapperFactory().create(new SimpleAbstractTypeResolverFactory().create());

    ApiClient apiClient = ApiClientPool.getInstance().getOrCreateSubmodelServiceApiClient(submodelServiceUrl, mapper);

    SubmodelServiceApi serviceApi = new SubmodelServiceApi(apiClient);

    if (tokenManager != null) {
      serviceApi.setTokenManager(tokenManager);
    }

    return serviceApi;
  }
}

/*******************************************************************************
 * Copyright (C) 2023 DFKI GmbH (https://www.dfki.de/en/web)
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
package org.eclipse.digitaltwin.basyx.aasenvironment.client.internal;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.function.Consumer;

import org.eclipse.digitaltwin.basyx.client.internal.ApiClient;
import org.eclipse.digitaltwin.basyx.client.internal.ApiException;
import org.eclipse.digitaltwin.basyx.client.internal.ApiResponse;
import org.eclipse.digitaltwin.basyx.client.internal.Pair;
import org.eclipse.digitaltwin.basyx.client.internal.authorization.TokenManager;
import org.eclipse.digitaltwin.basyx.core.exceptions.AccessTokenRetrievalException;
import org.eclipse.digitaltwin.basyx.http.Base64UrlEncodedIdentifier;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

import com.fasterxml.jackson.databind.ObjectMapper;

@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen", date = "2024-12-02T23:26:18.943744+01:00[Europe/Berlin]")
public class SerializationApi {
  private final HttpClient memberVarHttpClient;
  private final ObjectMapper memberVarObjectMapper;
  private final String memberVarBaseUri;
  private final Consumer<HttpRequest.Builder> memberVarInterceptor;
  private final Duration memberVarReadTimeout;
  private final Consumer<HttpResponse<InputStream>> memberVarResponseInterceptor;
  private final Consumer<HttpResponse<String>> memberVarAsyncResponseInterceptor;
  private TokenManager tokenManager;
  
  public SerializationApi() {
    this(new ApiClient());
  }
  
  public SerializationApi(TokenManager tokenManager) {
    this(new ApiClient());
    this.tokenManager = tokenManager;
  }
  
  public SerializationApi(String protocol, String host, int port) {
    this(protocol + "://" + host + ":" + port);
  }
  
  public SerializationApi(String protocol, String host, int port, TokenManager tokenManager) {
    this(protocol + "://" + host + ":" + port);
    this.tokenManager = tokenManager;
  }

  public SerializationApi(String basePath) {
     this(withBaseUri(new ApiClient(), basePath));
  }
  
  public SerializationApi(String basePath, TokenManager tokenManager) {
     this(withBaseUri(new ApiClient(), basePath));
     this.tokenManager = tokenManager;
  }

  private static ApiClient withBaseUri(ApiClient client, String uri) {
    client.updateBaseUri(uri);
    return client;
  }

  public SerializationApi(ApiClient apiClient) {
    memberVarHttpClient = apiClient.getHttpClient();
    memberVarObjectMapper = apiClient.getObjectMapper();
    memberVarBaseUri = apiClient.getBaseUri();
    memberVarInterceptor = apiClient.getRequestInterceptor();
    memberVarReadTimeout = apiClient.getReadTimeout();
    memberVarResponseInterceptor = apiClient.getResponseInterceptor();
    memberVarAsyncResponseInterceptor = apiClient.getAsyncResponseInterceptor();
  }

	protected ApiException getApiException(String operationId, HttpResponse<InputStream> response) throws IOException {
		String body = response.body() == null ? null : new String(response.body().readAllBytes());
		String message = formatExceptionMessage(operationId, response.statusCode(), body);
		return new ApiException(response.statusCode(), message, response.headers(), body);
  }

  private String formatExceptionMessage(String operationId, int statusCode, String body) {
    if (body == null || body.isEmpty()) {
      body = "[no body]";
    }
    return operationId + " call failed with: " + statusCode + " - " + body;
  }

  /**
   * Generate serialization by IDs
   * Returns an appropriate serialization based on the specified format.
   * @param serializationFormat Denotes the format in which the requested content shall be delivered. (required)
   * @param aasIds The unique IDs of the Asset Administration Shells to be contained in the serialization. (optional
   * @param submodelIds The unique IDs of the Submodels to be contained in the serialization. (optional
   * @param includeConceptDescriptions Include concept descriptions. (optional, default to true)
   * @return File
   * @throws ApiException if fails to make API call
   */
  public Resource generateSerializationByIds(String serializationFormat, List<String> aasIds, List<String> submodelIds, Boolean includeConceptDescriptions) throws ApiException {
    ApiResponse<Resource> localVarResponse = generateSerializationByIdsWithHttpInfo(serializationFormat, aasIds, submodelIds, includeConceptDescriptions);
    return localVarResponse.getData();
  }

  /**
   * Generate serialization by IDs
   * Returns an appropriate serialization based on the specified format.
   * @param serializationFormat Denotes the format in which the requested content shall be delivered. (required)
   * @param aasIds The unique IDs of the Asset Administration Shells to be contained in the serialization. (optional
   * @param submodelIds The unique IDs of the Submodels to be contained in the serialization. (optional
   * @param includeConceptDescriptions Include concept descriptions. (optional, default to true)
   * @return ApiResponse&lt;File&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<Resource> generateSerializationByIdsWithHttpInfo(String serializationFormat, List<String> aasIds, List<String> submodelIds, Boolean includeConceptDescriptions) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = generateSerializationByIdsRequestBuilder(serializationFormat, aasIds, submodelIds, includeConceptDescriptions);
    try {
      HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(
          localVarRequestBuilder.build(),
          HttpResponse.BodyHandlers.ofInputStream());
      if (memberVarResponseInterceptor != null) {
        memberVarResponseInterceptor.accept(localVarResponse);
      }
      try {
        if (localVarResponse.statusCode()/ 100 != 2) {
          throw getApiException("generateSerializationByIds", localVarResponse);
        }

        byte[] fileBytes = localVarResponse.body().readAllBytes();
        Resource resource = new ByteArrayResource(fileBytes);

        return new ApiResponse<>(
            localVarResponse.statusCode(),
            localVarResponse.headers().map(),
            resource
        );
      } finally {
      }
    } catch (IOException e) {
      throw new ApiException(e);
    }
    catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new ApiException(e);
    }
  }

  private HttpRequest.Builder generateSerializationByIdsRequestBuilder(String serializationFormat, List<String> aasIds, List<String> submodelIds, Boolean includeConceptDescriptions) throws ApiException {
    // verify the required parameter 'serializationFormat' is set
    if (serializationFormat == null) {
      throw new ApiException(400, "Missing the required parameter 'serializationFormat' when calling generateSerializationByIds");
    }

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/serialization";

    List<Pair> localVarQueryParams = new ArrayList<>();
    StringJoiner localVarQueryStringJoiner = new StringJoiner("&");
    String localVarQueryParameterBaseName;
    
    String aasIdsEncoded = "";
    for (int i = 0; i < aasIds.size(); i++) {
    	if (aasIdsEncoded.length() > 0) {
    		aasIdsEncoded += ",";
    	}
    	
    	aasIdsEncoded += Base64UrlEncodedIdentifier.encodeIdentifier(aasIds.get(i));
    }
    
    String submodelIdsEncoded = "";
    for (int i = 0; i < submodelIds.size(); i++) {
    	if (submodelIdsEncoded.length() > 0) {
    		submodelIdsEncoded += ",";
    	}
    	
    	submodelIdsEncoded += Base64UrlEncodedIdentifier.encodeIdentifier(submodelIds.get(i));
    }
     
    localVarQueryParameterBaseName = "aasIds";
    localVarQueryParams.addAll(ApiClient.parameterToPairs("aasIds", aasIdsEncoded));
    
    localVarQueryParameterBaseName = "submodelIds";
    localVarQueryParams.addAll(ApiClient.parameterToPairs("submodelIds", submodelIdsEncoded));
    
    localVarQueryParameterBaseName = "includeConceptDescriptions";
    localVarQueryParams.addAll(ApiClient.parameterToPairs("includeConceptDescriptions", includeConceptDescriptions));
    
    if (!localVarQueryParams.isEmpty() || localVarQueryStringJoiner.length() != 0) {
      StringJoiner queryJoiner = new StringJoiner("&");
      localVarQueryParams.forEach(p -> queryJoiner.add(p.getName() + '=' + p.getValue()));
      if (localVarQueryStringJoiner.length() != 0) {
        queryJoiner.add(localVarQueryStringJoiner.toString());
      }
      localVarRequestBuilder.uri(URI.create(memberVarBaseUri + localVarPath + '?' + queryJoiner.toString()));
    } else {
      localVarRequestBuilder.uri(URI.create(memberVarBaseUri + localVarPath));
    }
    
    localVarRequestBuilder.header("Accept", serializationFormat);

    addAuthorizationHeaderIfAuthIsEnabled(localVarRequestBuilder);
    
    localVarRequestBuilder.method("GET", HttpRequest.BodyPublishers.noBody());
    if (memberVarReadTimeout != null) {
      localVarRequestBuilder.timeout(memberVarReadTimeout);
    }
    if (memberVarInterceptor != null) {
      memberVarInterceptor.accept(localVarRequestBuilder);
    }
    return localVarRequestBuilder;
  }


  private void addAuthorizationHeaderIfAuthIsEnabled(HttpRequest.Builder localVarRequestBuilder) {
    if (tokenManager != null) {
        try {
          localVarRequestBuilder.header("Authorization", "Bearer " + tokenManager.getAccessToken());
      } catch (IOException e) {
        e.printStackTrace();
        throw new AccessTokenRetrievalException("Unable to request access token");
      }
      }
  }
}

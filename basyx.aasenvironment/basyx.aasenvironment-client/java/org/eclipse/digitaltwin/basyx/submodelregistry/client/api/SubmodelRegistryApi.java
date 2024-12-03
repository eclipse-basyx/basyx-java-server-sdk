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
package org.eclipse.digitaltwin.basyx.submodelregistry.client.api;

import org.eclipse.digitaltwin.basyx.submodelregistry.client.ApiClient;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.ApiException;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.ApiResponse;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.Pair;

import org.eclipse.digitaltwin.basyx.submodelregistry.client.model.GetSubmodelDescriptorsResult;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.model.Result;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.model.ServiceDescription;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.model.SubmodelDescriptor;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.http.HttpRequest;
import java.nio.channels.Channels;
import java.nio.channels.Pipe;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import java.util.ArrayList;
import java.util.StringJoiner;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import org.eclipse.digitaltwin.basyx.client.internal.authorization.TokenManager;
import org.eclipse.digitaltwin.basyx.core.exceptions.AccessTokenRetrievalException;

@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen", date = "2024-11-11T08:29:25.882305+01:00[Europe/Berlin]")
public class SubmodelRegistryApi {
  private final HttpClient memberVarHttpClient;
  private final ObjectMapper memberVarObjectMapper;
  private final String memberVarBaseUri;
  private final Consumer<HttpRequest.Builder> memberVarInterceptor;
  private final Duration memberVarReadTimeout;
  private final Consumer<HttpResponse<InputStream>> memberVarResponseInterceptor;
  private final Consumer<HttpResponse<String>> memberVarAsyncResponseInterceptor;
  private TokenManager tokenManager;
  
  public SubmodelRegistryApi() {
    this(new ApiClient());
  }
  
  public SubmodelRegistryApi(TokenManager tokenManager) {
    this(new ApiClient());
    this.tokenManager = tokenManager;
  }
  
  public SubmodelRegistryApi(String protocol, String host, int port) {
    this(protocol + "://" + host + ":" + port);
  }
  
  public SubmodelRegistryApi(String protocol, String host, int port, TokenManager tokenManager) {
    this(protocol + "://" + host + ":" + port);
    this.tokenManager = tokenManager;
  }

  public SubmodelRegistryApi(String basePath) {
     this(withBaseUri(new ApiClient(), basePath));
  }
  
  public SubmodelRegistryApi(String basePath, TokenManager tokenManager) {
     this(withBaseUri(new ApiClient(), basePath));
     this.tokenManager = tokenManager;
  }

  private static ApiClient withBaseUri(ApiClient client, String uri) {
    client.updateBaseUri(uri);
    return client;
  }

  public SubmodelRegistryApi(ApiClient apiClient) {
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
   * Deletes all Submodel Descriptors
   * 
   * @throws ApiException if fails to make API call
   */
  public void deleteAllSubmodelDescriptors() throws ApiException {
    deleteAllSubmodelDescriptorsWithHttpInfo();
  }

  /**
   * Deletes all Submodel Descriptors
   * 
   * @return ApiResponse&lt;Void&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<Void> deleteAllSubmodelDescriptorsWithHttpInfo() throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = deleteAllSubmodelDescriptorsRequestBuilder();
    try {
      HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(
          localVarRequestBuilder.build(),
          HttpResponse.BodyHandlers.ofInputStream());
      if (memberVarResponseInterceptor != null) {
        memberVarResponseInterceptor.accept(localVarResponse);
      }
      try {
        if (localVarResponse.statusCode()/ 100 != 2) {
          throw getApiException("deleteAllSubmodelDescriptors", localVarResponse);
        }
        return new ApiResponse<Void>(
          localVarResponse.statusCode(),
          localVarResponse.headers().map(),
          null
        );
      } finally {
        // Drain the InputStream
        while (localVarResponse.body().read() != -1) {
            // Ignore
        }
        localVarResponse.body().close();
      }
    } catch (IOException e) {
      throw new ApiException(e);
    }
    catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new ApiException(e);
    }
  }

  private HttpRequest.Builder deleteAllSubmodelDescriptorsRequestBuilder() throws ApiException {

    

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/submodel-descriptors";

    localVarRequestBuilder.uri(URI.create(memberVarBaseUri + localVarPath));

    localVarRequestBuilder.header("Accept", "application/json");

	  addAuthorizationHeaderIfAuthIsEnabled(localVarRequestBuilder);
    
    localVarRequestBuilder.method("DELETE", HttpRequest.BodyPublishers.noBody());
    if (memberVarReadTimeout != null) {
      localVarRequestBuilder.timeout(memberVarReadTimeout);
    }
    if (memberVarInterceptor != null) {
      memberVarInterceptor.accept(localVarRequestBuilder);
    }
    return localVarRequestBuilder;
  }

  /**
   * Deletes a Submodel Descriptor, i.e. de-registers a submodel
   * 
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @throws ApiException if fails to make API call
   */
  public void deleteSubmodelDescriptorById(String submodelIdentifier) throws ApiException {
    deleteSubmodelDescriptorByIdWithHttpInfo(submodelIdentifier);
  }

  /**
   * Deletes a Submodel Descriptor, i.e. de-registers a submodel
   * 
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @return ApiResponse&lt;Void&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<Void> deleteSubmodelDescriptorByIdWithHttpInfo(String submodelIdentifier) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = deleteSubmodelDescriptorByIdRequestBuilder(submodelIdentifier);
    try {
      HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(
          localVarRequestBuilder.build(),
          HttpResponse.BodyHandlers.ofInputStream());
      if (memberVarResponseInterceptor != null) {
        memberVarResponseInterceptor.accept(localVarResponse);
      }
      try {
        if (localVarResponse.statusCode()/ 100 != 2) {
          throw getApiException("deleteSubmodelDescriptorById", localVarResponse);
        }
        return new ApiResponse<Void>(
          localVarResponse.statusCode(),
          localVarResponse.headers().map(),
          null
        );
      } finally {
        // Drain the InputStream
        while (localVarResponse.body().read() != -1) {
            // Ignore
        }
        localVarResponse.body().close();
      }
    } catch (IOException e) {
      throw new ApiException(e);
    }
    catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new ApiException(e);
    }
  }

  private HttpRequest.Builder deleteSubmodelDescriptorByIdRequestBuilder(String submodelIdentifier) throws ApiException {
    // verify the required parameter 'submodelIdentifier' is set
    if (submodelIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'submodelIdentifier' when calling deleteSubmodelDescriptorById");
    }

    String submodelIdentifierAsBase64EncodedParam = submodelIdentifier == null ? null : new String(java.util.Base64.getUrlEncoder().encode(submodelIdentifier.getBytes(java.nio.charset.StandardCharsets.UTF_8)), java.nio.charset.StandardCharsets.UTF_8);
    

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/submodel-descriptors/{submodelIdentifier}"
        .replace("{submodelIdentifier}", ApiClient.urlEncode(submodelIdentifierAsBase64EncodedParam.toString()));

    localVarRequestBuilder.uri(URI.create(memberVarBaseUri + localVarPath));

    localVarRequestBuilder.header("Accept", "application/json");

	  addAuthorizationHeaderIfAuthIsEnabled(localVarRequestBuilder);
    
    localVarRequestBuilder.method("DELETE", HttpRequest.BodyPublishers.noBody());
    if (memberVarReadTimeout != null) {
      localVarRequestBuilder.timeout(memberVarReadTimeout);
    }
    if (memberVarInterceptor != null) {
      memberVarInterceptor.accept(localVarRequestBuilder);
    }
    return localVarRequestBuilder;
  }

  /**
   * Returns all Submodel Descriptors
   * 
   * @param limit The maximum number of elements in the response array (optional)
   * @param cursor A server-generated identifier retrieved from pagingMetadata that specifies from which position the result listing should continue (optional)
   * @return GetSubmodelDescriptorsResult
   * @throws ApiException if fails to make API call
   */
  public GetSubmodelDescriptorsResult getAllSubmodelDescriptors(Integer limit, String cursor) throws ApiException {
    ApiResponse<GetSubmodelDescriptorsResult> localVarResponse = getAllSubmodelDescriptorsWithHttpInfo(limit, cursor);
    return localVarResponse.getData();
  }

  /**
   * Returns all Submodel Descriptors
   * 
   * @param limit The maximum number of elements in the response array (optional)
   * @param cursor A server-generated identifier retrieved from pagingMetadata that specifies from which position the result listing should continue (optional)
   * @return ApiResponse&lt;GetSubmodelDescriptorsResult&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<GetSubmodelDescriptorsResult> getAllSubmodelDescriptorsWithHttpInfo(Integer limit, String cursor) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = getAllSubmodelDescriptorsRequestBuilder(limit, cursor);
    try {
      HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(
          localVarRequestBuilder.build(),
          HttpResponse.BodyHandlers.ofInputStream());
      if (memberVarResponseInterceptor != null) {
        memberVarResponseInterceptor.accept(localVarResponse);
      }
      try {
        if (localVarResponse.statusCode()/ 100 != 2) {
          throw getApiException("getAllSubmodelDescriptors", localVarResponse);
        }
        return new ApiResponse<GetSubmodelDescriptorsResult>(
          localVarResponse.statusCode(),
          localVarResponse.headers().map(),
          localVarResponse.body() == null ? null : memberVarObjectMapper.readValue(localVarResponse.body(), new TypeReference<GetSubmodelDescriptorsResult>() {}) // closes the InputStream
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

  private HttpRequest.Builder getAllSubmodelDescriptorsRequestBuilder(Integer limit, String cursor) throws ApiException {

    

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/submodel-descriptors";

    List<Pair> localVarQueryParams = new ArrayList<>();
    StringJoiner localVarQueryStringJoiner = new StringJoiner("&");
    String localVarQueryParameterBaseName;
    localVarQueryParameterBaseName = "limit";
    localVarQueryParams.addAll(ApiClient.parameterToPairs("limit", limit));
    localVarQueryParameterBaseName = "cursor";
    localVarQueryParams.addAll(ApiClient.parameterToPairs("cursor", cursor));

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

    localVarRequestBuilder.header("Accept", "application/json");

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

  /**
   * Returns the self-describing information of a network resource (ServiceDescription)
   * 
   * @return ServiceDescription
   * @throws ApiException if fails to make API call
   */
  public ServiceDescription getDescription() throws ApiException {
    ApiResponse<ServiceDescription> localVarResponse = getDescriptionWithHttpInfo();
    return localVarResponse.getData();
  }

  /**
   * Returns the self-describing information of a network resource (ServiceDescription)
   * 
   * @return ApiResponse&lt;ServiceDescription&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<ServiceDescription> getDescriptionWithHttpInfo() throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = getDescriptionRequestBuilder();
    try {
      HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(
          localVarRequestBuilder.build(),
          HttpResponse.BodyHandlers.ofInputStream());
      if (memberVarResponseInterceptor != null) {
        memberVarResponseInterceptor.accept(localVarResponse);
      }
      try {
        if (localVarResponse.statusCode()/ 100 != 2) {
          throw getApiException("getDescription", localVarResponse);
        }
        return new ApiResponse<ServiceDescription>(
          localVarResponse.statusCode(),
          localVarResponse.headers().map(),
          localVarResponse.body() == null ? null : memberVarObjectMapper.readValue(localVarResponse.body(), new TypeReference<ServiceDescription>() {}) // closes the InputStream
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

  private HttpRequest.Builder getDescriptionRequestBuilder() throws ApiException {

    

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/description";

    localVarRequestBuilder.uri(URI.create(memberVarBaseUri + localVarPath));

    localVarRequestBuilder.header("Accept", "application/json");

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

  /**
   * Returns a specific Submodel Descriptor
   * 
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @return SubmodelDescriptor
   * @throws ApiException if fails to make API call
   */
  public SubmodelDescriptor getSubmodelDescriptorById(String submodelIdentifier) throws ApiException {
    ApiResponse<SubmodelDescriptor> localVarResponse = getSubmodelDescriptorByIdWithHttpInfo(submodelIdentifier);
    return localVarResponse.getData();
  }

  /**
   * Returns a specific Submodel Descriptor
   * 
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @return ApiResponse&lt;SubmodelDescriptor&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<SubmodelDescriptor> getSubmodelDescriptorByIdWithHttpInfo(String submodelIdentifier) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = getSubmodelDescriptorByIdRequestBuilder(submodelIdentifier);
    try {
      HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(
          localVarRequestBuilder.build(),
          HttpResponse.BodyHandlers.ofInputStream());
      if (memberVarResponseInterceptor != null) {
        memberVarResponseInterceptor.accept(localVarResponse);
      }
      try {
        if (localVarResponse.statusCode()/ 100 != 2) {
          throw getApiException("getSubmodelDescriptorById", localVarResponse);
        }
        return new ApiResponse<SubmodelDescriptor>(
          localVarResponse.statusCode(),
          localVarResponse.headers().map(),
          localVarResponse.body() == null ? null : memberVarObjectMapper.readValue(localVarResponse.body(), new TypeReference<SubmodelDescriptor>() {}) // closes the InputStream
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

  private HttpRequest.Builder getSubmodelDescriptorByIdRequestBuilder(String submodelIdentifier) throws ApiException {
    // verify the required parameter 'submodelIdentifier' is set
    if (submodelIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'submodelIdentifier' when calling getSubmodelDescriptorById");
    }

    String submodelIdentifierAsBase64EncodedParam = submodelIdentifier == null ? null : new String(java.util.Base64.getUrlEncoder().encode(submodelIdentifier.getBytes(java.nio.charset.StandardCharsets.UTF_8)), java.nio.charset.StandardCharsets.UTF_8);
    

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/submodel-descriptors/{submodelIdentifier}"
        .replace("{submodelIdentifier}", ApiClient.urlEncode(submodelIdentifierAsBase64EncodedParam.toString()));

    localVarRequestBuilder.uri(URI.create(memberVarBaseUri + localVarPath));

    localVarRequestBuilder.header("Accept", "application/json");

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

  /**
   * Creates a new Submodel Descriptor, i.e. registers a submodel
   * 
   * @param submodelDescriptor Submodel Descriptor object (required)
   * @return SubmodelDescriptor
   * @throws ApiException if fails to make API call
   */
  public SubmodelDescriptor postSubmodelDescriptor(SubmodelDescriptor submodelDescriptor) throws ApiException {
    ApiResponse<SubmodelDescriptor> localVarResponse = postSubmodelDescriptorWithHttpInfo(submodelDescriptor);
    return localVarResponse.getData();
  }

  /**
   * Creates a new Submodel Descriptor, i.e. registers a submodel
   * 
   * @param submodelDescriptor Submodel Descriptor object (required)
   * @return ApiResponse&lt;SubmodelDescriptor&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<SubmodelDescriptor> postSubmodelDescriptorWithHttpInfo(SubmodelDescriptor submodelDescriptor) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = postSubmodelDescriptorRequestBuilder(submodelDescriptor);
    try {
      HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(
          localVarRequestBuilder.build(),
          HttpResponse.BodyHandlers.ofInputStream());
      if (memberVarResponseInterceptor != null) {
        memberVarResponseInterceptor.accept(localVarResponse);
      }
      try {
        if (localVarResponse.statusCode()/ 100 != 2) {
          throw getApiException("postSubmodelDescriptor", localVarResponse);
        }
        return new ApiResponse<SubmodelDescriptor>(
          localVarResponse.statusCode(),
          localVarResponse.headers().map(),
          localVarResponse.body() == null ? null : memberVarObjectMapper.readValue(localVarResponse.body(), new TypeReference<SubmodelDescriptor>() {}) // closes the InputStream
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

  private HttpRequest.Builder postSubmodelDescriptorRequestBuilder(SubmodelDescriptor submodelDescriptor) throws ApiException {
    // verify the required parameter 'submodelDescriptor' is set
    if (submodelDescriptor == null) {
      throw new ApiException(400, "Missing the required parameter 'submodelDescriptor' when calling postSubmodelDescriptor");
    }

    

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/submodel-descriptors";

    localVarRequestBuilder.uri(URI.create(memberVarBaseUri + localVarPath));

    localVarRequestBuilder.header("Content-Type", "application/json");
    localVarRequestBuilder.header("Accept", "application/json");

	  addAuthorizationHeaderIfAuthIsEnabled(localVarRequestBuilder);
    
    try {
      byte[] localVarPostBody = memberVarObjectMapper.writeValueAsBytes(submodelDescriptor);
      localVarRequestBuilder.method("POST", HttpRequest.BodyPublishers.ofByteArray(localVarPostBody));
    } catch (IOException e) {
      throw new ApiException(e);
    }
    if (memberVarReadTimeout != null) {
      localVarRequestBuilder.timeout(memberVarReadTimeout);
    }
    if (memberVarInterceptor != null) {
      memberVarInterceptor.accept(localVarRequestBuilder);
    }
    return localVarRequestBuilder;
  }

  /**
   * Updates an existing Submodel Descriptor
   * 
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelDescriptor Submodel Descriptor object (required)
   * @throws ApiException if fails to make API call
   */
  public void putSubmodelDescriptorById(String submodelIdentifier, SubmodelDescriptor submodelDescriptor) throws ApiException {
    putSubmodelDescriptorByIdWithHttpInfo(submodelIdentifier, submodelDescriptor);
  }

  /**
   * Updates an existing Submodel Descriptor
   * 
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelDescriptor Submodel Descriptor object (required)
   * @return ApiResponse&lt;Void&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<Void> putSubmodelDescriptorByIdWithHttpInfo(String submodelIdentifier, SubmodelDescriptor submodelDescriptor) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = putSubmodelDescriptorByIdRequestBuilder(submodelIdentifier, submodelDescriptor);
    try {
      HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(
          localVarRequestBuilder.build(),
          HttpResponse.BodyHandlers.ofInputStream());
      if (memberVarResponseInterceptor != null) {
        memberVarResponseInterceptor.accept(localVarResponse);
      }
      try {
        if (localVarResponse.statusCode()/ 100 != 2) {
          throw getApiException("putSubmodelDescriptorById", localVarResponse);
        }
        return new ApiResponse<Void>(
          localVarResponse.statusCode(),
          localVarResponse.headers().map(),
          null
        );
      } finally {
        // Drain the InputStream
        while (localVarResponse.body().read() != -1) {
            // Ignore
        }
        localVarResponse.body().close();
      }
    } catch (IOException e) {
      throw new ApiException(e);
    }
    catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new ApiException(e);
    }
  }

  private HttpRequest.Builder putSubmodelDescriptorByIdRequestBuilder(String submodelIdentifier, SubmodelDescriptor submodelDescriptor) throws ApiException {
    // verify the required parameter 'submodelIdentifier' is set
    if (submodelIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'submodelIdentifier' when calling putSubmodelDescriptorById");
    }
    // verify the required parameter 'submodelDescriptor' is set
    if (submodelDescriptor == null) {
      throw new ApiException(400, "Missing the required parameter 'submodelDescriptor' when calling putSubmodelDescriptorById");
    }

    String submodelIdentifierAsBase64EncodedParam = submodelIdentifier == null ? null : new String(java.util.Base64.getUrlEncoder().encode(submodelIdentifier.getBytes(java.nio.charset.StandardCharsets.UTF_8)), java.nio.charset.StandardCharsets.UTF_8);
    

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/submodel-descriptors/{submodelIdentifier}"
        .replace("{submodelIdentifier}", ApiClient.urlEncode(submodelIdentifierAsBase64EncodedParam.toString()));

    localVarRequestBuilder.uri(URI.create(memberVarBaseUri + localVarPath));

    localVarRequestBuilder.header("Content-Type", "application/json");
    localVarRequestBuilder.header("Accept", "application/json");

	  addAuthorizationHeaderIfAuthIsEnabled(localVarRequestBuilder);
    
    try {
      byte[] localVarPostBody = memberVarObjectMapper.writeValueAsBytes(submodelDescriptor);
      localVarRequestBuilder.method("PUT", HttpRequest.BodyPublishers.ofByteArray(localVarPostBody));
    } catch (IOException e) {
      throw new ApiException(e);
    }
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

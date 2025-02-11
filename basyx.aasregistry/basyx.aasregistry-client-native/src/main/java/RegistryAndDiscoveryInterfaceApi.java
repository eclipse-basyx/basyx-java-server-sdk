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
package org.eclipse.digitaltwin.basyx.aasregistry.client.api;

import org.eclipse.digitaltwin.basyx.aasregistry.client.ApiClient;
import org.eclipse.digitaltwin.basyx.aasregistry.client.ApiException;
import org.eclipse.digitaltwin.basyx.aasregistry.client.ApiResponse;
import org.eclipse.digitaltwin.basyx.aasregistry.client.Pair;

import org.eclipse.digitaltwin.basyx.aasregistry.client.model.AssetAdministrationShellDescriptor;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.AssetKind;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.GetAssetAdministrationShellDescriptorsResult;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.GetSubmodelDescriptorsResult;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.Result;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.ServiceDescription;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.ShellDescriptorSearchRequest;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.ShellDescriptorSearchResponse;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.SubmodelDescriptor;

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

@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen", date = "2025-02-10T17:05:52.413466700+01:00[Europe/Berlin]")
public class RegistryAndDiscoveryInterfaceApi {
  private final HttpClient memberVarHttpClient;
  private final ObjectMapper memberVarObjectMapper;
  private final String memberVarBaseUri;
  private final Consumer<HttpRequest.Builder> memberVarInterceptor;
  private final Duration memberVarReadTimeout;
  private final Consumer<HttpResponse<InputStream>> memberVarResponseInterceptor;
  private final Consumer<HttpResponse<String>> memberVarAsyncResponseInterceptor;
  private TokenManager tokenManager;

  public RegistryAndDiscoveryInterfaceApi() {
    this(new ApiClient());
  }
  
  public RegistryAndDiscoveryInterfaceApi(TokenManager tokenManager) {
    this(new ApiClient());
    this.tokenManager = tokenManager;
  }
  
  public RegistryAndDiscoveryInterfaceApi(String protocol, String host, int port) {
    this(protocol + "://" + host + ":" + port);
  }
  
  public RegistryAndDiscoveryInterfaceApi(String protocol, String host, int port, TokenManager tokenManager) {
    this(protocol + "://" + host + ":" + port);
    this.tokenManager = tokenManager;
  }

  public RegistryAndDiscoveryInterfaceApi(String basePath) {
     this(withBaseUri(new ApiClient(), basePath));
  }
  
  public RegistryAndDiscoveryInterfaceApi(String basePath, TokenManager tokenManager) {
     this(withBaseUri(new ApiClient(), basePath));
     this.tokenManager = tokenManager;
  }

  private static ApiClient withBaseUri(ApiClient client, String uri) {
    client.updateBaseUri(uri);
    return client;
  }

  public RegistryAndDiscoveryInterfaceApi(ApiClient apiClient) {
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
   * Deletes all Asset Administration Shell Descriptors
   * 
   * @throws ApiException if fails to make API call
   */
  public void deleteAllShellDescriptors() throws ApiException {
    deleteAllShellDescriptorsWithHttpInfo();
  }

  /**
   * Deletes all Asset Administration Shell Descriptors
   * 
   * @return ApiResponse&lt;Void&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<Void> deleteAllShellDescriptorsWithHttpInfo() throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = deleteAllShellDescriptorsRequestBuilder();
    try {
      HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(
          localVarRequestBuilder.build(),
          HttpResponse.BodyHandlers.ofInputStream());
      if (memberVarResponseInterceptor != null) {
        memberVarResponseInterceptor.accept(localVarResponse);
      }
      try {
        if (localVarResponse.statusCode()/ 100 != 2) {
          throw getApiException("deleteAllShellDescriptors", localVarResponse);
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

  private HttpRequest.Builder deleteAllShellDescriptorsRequestBuilder() throws ApiException {

    

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/shell-descriptors";

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
   * Deletes an Asset Administration Shell Descriptor, i.e. de-registers an AAS
   * 
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @throws ApiException if fails to make API call
   */
  public void deleteAssetAdministrationShellDescriptorById(String aasIdentifier) throws ApiException {
    deleteAssetAdministrationShellDescriptorByIdWithHttpInfo(aasIdentifier);
  }

  /**
   * Deletes an Asset Administration Shell Descriptor, i.e. de-registers an AAS
   * 
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @return ApiResponse&lt;Void&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<Void> deleteAssetAdministrationShellDescriptorByIdWithHttpInfo(String aasIdentifier) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = deleteAssetAdministrationShellDescriptorByIdRequestBuilder(aasIdentifier);
    try {
      HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(
          localVarRequestBuilder.build(),
          HttpResponse.BodyHandlers.ofInputStream());
      if (memberVarResponseInterceptor != null) {
        memberVarResponseInterceptor.accept(localVarResponse);
      }
      try {
        if (localVarResponse.statusCode()/ 100 != 2) {
          throw getApiException("deleteAssetAdministrationShellDescriptorById", localVarResponse);
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

  private HttpRequest.Builder deleteAssetAdministrationShellDescriptorByIdRequestBuilder(String aasIdentifier) throws ApiException {
    // verify the required parameter 'aasIdentifier' is set
    if (aasIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'aasIdentifier' when calling deleteAssetAdministrationShellDescriptorById");
    }

    String aasIdentifierAsBase64EncodedParam = aasIdentifier == null ? null : new String(java.util.Base64.getUrlEncoder().encode(aasIdentifier.getBytes(java.nio.charset.StandardCharsets.UTF_8)), java.nio.charset.StandardCharsets.UTF_8);
    

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/shell-descriptors/{aasIdentifier}"
        .replace("{aasIdentifier}", ApiClient.urlEncode(aasIdentifierAsBase64EncodedParam.toString()));

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
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @throws ApiException if fails to make API call
   */
  public void deleteSubmodelDescriptorByIdThroughSuperpath(String aasIdentifier, String submodelIdentifier) throws ApiException {
    deleteSubmodelDescriptorByIdThroughSuperpathWithHttpInfo(aasIdentifier, submodelIdentifier);
  }

  /**
   * Deletes a Submodel Descriptor, i.e. de-registers a submodel
   * 
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @return ApiResponse&lt;Void&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<Void> deleteSubmodelDescriptorByIdThroughSuperpathWithHttpInfo(String aasIdentifier, String submodelIdentifier) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = deleteSubmodelDescriptorByIdThroughSuperpathRequestBuilder(aasIdentifier, submodelIdentifier);
    try {
      HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(
          localVarRequestBuilder.build(),
          HttpResponse.BodyHandlers.ofInputStream());
      if (memberVarResponseInterceptor != null) {
        memberVarResponseInterceptor.accept(localVarResponse);
      }
      try {
        if (localVarResponse.statusCode()/ 100 != 2) {
          throw getApiException("deleteSubmodelDescriptorByIdThroughSuperpath", localVarResponse);
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

  private HttpRequest.Builder deleteSubmodelDescriptorByIdThroughSuperpathRequestBuilder(String aasIdentifier, String submodelIdentifier) throws ApiException {
    // verify the required parameter 'aasIdentifier' is set
    if (aasIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'aasIdentifier' when calling deleteSubmodelDescriptorByIdThroughSuperpath");
    }
    // verify the required parameter 'submodelIdentifier' is set
    if (submodelIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'submodelIdentifier' when calling deleteSubmodelDescriptorByIdThroughSuperpath");
    }

    String aasIdentifierAsBase64EncodedParam = aasIdentifier == null ? null : new String(java.util.Base64.getUrlEncoder().encode(aasIdentifier.getBytes(java.nio.charset.StandardCharsets.UTF_8)), java.nio.charset.StandardCharsets.UTF_8);
    String submodelIdentifierAsBase64EncodedParam = submodelIdentifier == null ? null : new String(java.util.Base64.getUrlEncoder().encode(submodelIdentifier.getBytes(java.nio.charset.StandardCharsets.UTF_8)), java.nio.charset.StandardCharsets.UTF_8);
    

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/shell-descriptors/{aasIdentifier}/submodel-descriptors/{submodelIdentifier}"
        .replace("{aasIdentifier}", ApiClient.urlEncode(aasIdentifierAsBase64EncodedParam.toString()))
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
   * Returns all Asset Administration Shell Descriptors
   * 
   * @param limit The maximum number of elements in the response array (optional)
   * @param cursor A server-generated identifier retrieved from pagingMetadata that specifies from which position the result listing should continue (optional)
   * @param assetKind The Asset&#39;s kind (Instance or Type) (optional)
   * @param assetType The Asset&#39;s type (UTF8-BASE64-URL-encoded) (optional)
   * @return GetAssetAdministrationShellDescriptorsResult
   * @throws ApiException if fails to make API call
   */
  public GetAssetAdministrationShellDescriptorsResult getAllAssetAdministrationShellDescriptors(Integer limit, String cursor, AssetKind assetKind, String assetType) throws ApiException {
    ApiResponse<GetAssetAdministrationShellDescriptorsResult> localVarResponse = getAllAssetAdministrationShellDescriptorsWithHttpInfo(limit, cursor, assetKind, assetType);
    return localVarResponse.getData();
  }

  /**
   * Returns all Asset Administration Shell Descriptors
   * 
   * @param limit The maximum number of elements in the response array (optional)
   * @param cursor A server-generated identifier retrieved from pagingMetadata that specifies from which position the result listing should continue (optional)
   * @param assetKind The Asset&#39;s kind (Instance or Type) (optional)
   * @param assetType The Asset&#39;s type (UTF8-BASE64-URL-encoded) (optional)
   * @return ApiResponse&lt;GetAssetAdministrationShellDescriptorsResult&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<GetAssetAdministrationShellDescriptorsResult> getAllAssetAdministrationShellDescriptorsWithHttpInfo(Integer limit, String cursor, AssetKind assetKind, String assetType) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = getAllAssetAdministrationShellDescriptorsRequestBuilder(limit, cursor, assetKind, assetType);
    try {
      HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(
          localVarRequestBuilder.build(),
          HttpResponse.BodyHandlers.ofInputStream());
      if (memberVarResponseInterceptor != null) {
        memberVarResponseInterceptor.accept(localVarResponse);
      }
      try {
        if (localVarResponse.statusCode()/ 100 != 2) {
          throw getApiException("getAllAssetAdministrationShellDescriptors", localVarResponse);
        }
        return new ApiResponse<GetAssetAdministrationShellDescriptorsResult>(
          localVarResponse.statusCode(),
          localVarResponse.headers().map(),
          localVarResponse.body() == null ? null : memberVarObjectMapper.readValue(localVarResponse.body(), new TypeReference<GetAssetAdministrationShellDescriptorsResult>() {}) // closes the InputStream
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

  private HttpRequest.Builder getAllAssetAdministrationShellDescriptorsRequestBuilder(Integer limit, String cursor, AssetKind assetKind, String assetType) throws ApiException {

    

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/shell-descriptors";

    List<Pair> localVarQueryParams = new ArrayList<>();
    StringJoiner localVarQueryStringJoiner = new StringJoiner("&");
    String localVarQueryParameterBaseName;
    localVarQueryParameterBaseName = "limit";
    localVarQueryParams.addAll(ApiClient.parameterToPairs("limit", limit));
    localVarQueryParameterBaseName = "cursor";
    localVarQueryParams.addAll(ApiClient.parameterToPairs("cursor", cursor));
    localVarQueryParameterBaseName = "assetKind";
    localVarQueryParams.addAll(ApiClient.parameterToPairs("assetKind", assetKind));
    localVarQueryParameterBaseName = "assetType";
    String assetTypeAsBase64EncodedQueryParam = assetType == null ? null : new String(java.util.Base64.getUrlEncoder().encode(assetType.getBytes(java.nio.charset.StandardCharsets.UTF_8)), java.nio.charset.StandardCharsets.UTF_8);
    localVarQueryParams.addAll(ApiClient.parameterToPairs("assetType", assetTypeAsBase64EncodedQueryParam));

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
   * Returns all Submodel Descriptors
   * 
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param limit The maximum number of elements in the response array (optional)
   * @param cursor A server-generated identifier retrieved from pagingMetadata that specifies from which position the result listing should continue (optional)
   * @return GetSubmodelDescriptorsResult
   * @throws ApiException if fails to make API call
   */
  public GetSubmodelDescriptorsResult getAllSubmodelDescriptorsThroughSuperpath(String aasIdentifier, Integer limit, String cursor) throws ApiException {
    ApiResponse<GetSubmodelDescriptorsResult> localVarResponse = getAllSubmodelDescriptorsThroughSuperpathWithHttpInfo(aasIdentifier, limit, cursor);
    return localVarResponse.getData();
  }

  /**
   * Returns all Submodel Descriptors
   * 
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param limit The maximum number of elements in the response array (optional)
   * @param cursor A server-generated identifier retrieved from pagingMetadata that specifies from which position the result listing should continue (optional)
   * @return ApiResponse&lt;GetSubmodelDescriptorsResult&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<GetSubmodelDescriptorsResult> getAllSubmodelDescriptorsThroughSuperpathWithHttpInfo(String aasIdentifier, Integer limit, String cursor) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = getAllSubmodelDescriptorsThroughSuperpathRequestBuilder(aasIdentifier, limit, cursor);
    try {
      HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(
          localVarRequestBuilder.build(),
          HttpResponse.BodyHandlers.ofInputStream());
      if (memberVarResponseInterceptor != null) {
        memberVarResponseInterceptor.accept(localVarResponse);
      }
      try {
        if (localVarResponse.statusCode()/ 100 != 2) {
          throw getApiException("getAllSubmodelDescriptorsThroughSuperpath", localVarResponse);
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

  private HttpRequest.Builder getAllSubmodelDescriptorsThroughSuperpathRequestBuilder(String aasIdentifier, Integer limit, String cursor) throws ApiException {
    // verify the required parameter 'aasIdentifier' is set
    if (aasIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'aasIdentifier' when calling getAllSubmodelDescriptorsThroughSuperpath");
    }

    String aasIdentifierAsBase64EncodedParam = aasIdentifier == null ? null : new String(java.util.Base64.getUrlEncoder().encode(aasIdentifier.getBytes(java.nio.charset.StandardCharsets.UTF_8)), java.nio.charset.StandardCharsets.UTF_8);
    

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/shell-descriptors/{aasIdentifier}/submodel-descriptors"
        .replace("{aasIdentifier}", ApiClient.urlEncode(aasIdentifierAsBase64EncodedParam.toString()));

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
   * Returns a specific Asset Administration Shell Descriptor
   * 
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @return AssetAdministrationShellDescriptor
   * @throws ApiException if fails to make API call
   */
  public AssetAdministrationShellDescriptor getAssetAdministrationShellDescriptorById(String aasIdentifier) throws ApiException {
    ApiResponse<AssetAdministrationShellDescriptor> localVarResponse = getAssetAdministrationShellDescriptorByIdWithHttpInfo(aasIdentifier);
    return localVarResponse.getData();
  }

  /**
   * Returns a specific Asset Administration Shell Descriptor
   * 
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @return ApiResponse&lt;AssetAdministrationShellDescriptor&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<AssetAdministrationShellDescriptor> getAssetAdministrationShellDescriptorByIdWithHttpInfo(String aasIdentifier) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = getAssetAdministrationShellDescriptorByIdRequestBuilder(aasIdentifier);
    try {
      HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(
          localVarRequestBuilder.build(),
          HttpResponse.BodyHandlers.ofInputStream());
      if (memberVarResponseInterceptor != null) {
        memberVarResponseInterceptor.accept(localVarResponse);
      }
      try {
        if (localVarResponse.statusCode()/ 100 != 2) {
          throw getApiException("getAssetAdministrationShellDescriptorById", localVarResponse);
        }
        return new ApiResponse<AssetAdministrationShellDescriptor>(
          localVarResponse.statusCode(),
          localVarResponse.headers().map(),
          localVarResponse.body() == null ? null : memberVarObjectMapper.readValue(localVarResponse.body(), new TypeReference<AssetAdministrationShellDescriptor>() {}) // closes the InputStream
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

  private HttpRequest.Builder getAssetAdministrationShellDescriptorByIdRequestBuilder(String aasIdentifier) throws ApiException {
    // verify the required parameter 'aasIdentifier' is set
    if (aasIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'aasIdentifier' when calling getAssetAdministrationShellDescriptorById");
    }

    String aasIdentifierAsBase64EncodedParam = aasIdentifier == null ? null : new String(java.util.Base64.getUrlEncoder().encode(aasIdentifier.getBytes(java.nio.charset.StandardCharsets.UTF_8)), java.nio.charset.StandardCharsets.UTF_8);
    

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/shell-descriptors/{aasIdentifier}"
        .replace("{aasIdentifier}", ApiClient.urlEncode(aasIdentifierAsBase64EncodedParam.toString()));

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
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @return SubmodelDescriptor
   * @throws ApiException if fails to make API call
   */
  public SubmodelDescriptor getSubmodelDescriptorByIdThroughSuperpath(String aasIdentifier, String submodelIdentifier) throws ApiException {
    ApiResponse<SubmodelDescriptor> localVarResponse = getSubmodelDescriptorByIdThroughSuperpathWithHttpInfo(aasIdentifier, submodelIdentifier);
    return localVarResponse.getData();
  }

  /**
   * Returns a specific Submodel Descriptor
   * 
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @return ApiResponse&lt;SubmodelDescriptor&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<SubmodelDescriptor> getSubmodelDescriptorByIdThroughSuperpathWithHttpInfo(String aasIdentifier, String submodelIdentifier) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = getSubmodelDescriptorByIdThroughSuperpathRequestBuilder(aasIdentifier, submodelIdentifier);
    try {
      HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(
          localVarRequestBuilder.build(),
          HttpResponse.BodyHandlers.ofInputStream());
      if (memberVarResponseInterceptor != null) {
        memberVarResponseInterceptor.accept(localVarResponse);
      }
      try {
        if (localVarResponse.statusCode()/ 100 != 2) {
          throw getApiException("getSubmodelDescriptorByIdThroughSuperpath", localVarResponse);
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

  private HttpRequest.Builder getSubmodelDescriptorByIdThroughSuperpathRequestBuilder(String aasIdentifier, String submodelIdentifier) throws ApiException {
    // verify the required parameter 'aasIdentifier' is set
    if (aasIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'aasIdentifier' when calling getSubmodelDescriptorByIdThroughSuperpath");
    }
    // verify the required parameter 'submodelIdentifier' is set
    if (submodelIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'submodelIdentifier' when calling getSubmodelDescriptorByIdThroughSuperpath");
    }

    String aasIdentifierAsBase64EncodedParam = aasIdentifier == null ? null : new String(java.util.Base64.getUrlEncoder().encode(aasIdentifier.getBytes(java.nio.charset.StandardCharsets.UTF_8)), java.nio.charset.StandardCharsets.UTF_8);
    String submodelIdentifierAsBase64EncodedParam = submodelIdentifier == null ? null : new String(java.util.Base64.getUrlEncoder().encode(submodelIdentifier.getBytes(java.nio.charset.StandardCharsets.UTF_8)), java.nio.charset.StandardCharsets.UTF_8);
    

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/shell-descriptors/{aasIdentifier}/submodel-descriptors/{submodelIdentifier}"
        .replace("{aasIdentifier}", ApiClient.urlEncode(aasIdentifierAsBase64EncodedParam.toString()))
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
   * Creates a new Asset Administration Shell Descriptor, i.e. registers an AAS
   * 
   * @param assetAdministrationShellDescriptor Asset Administration Shell Descriptor object (required)
   * @return AssetAdministrationShellDescriptor
   * @throws ApiException if fails to make API call
   */
  public AssetAdministrationShellDescriptor postAssetAdministrationShellDescriptor(AssetAdministrationShellDescriptor assetAdministrationShellDescriptor) throws ApiException {
    ApiResponse<AssetAdministrationShellDescriptor> localVarResponse = postAssetAdministrationShellDescriptorWithHttpInfo(assetAdministrationShellDescriptor);
    return localVarResponse.getData();
  }

  /**
   * Creates a new Asset Administration Shell Descriptor, i.e. registers an AAS
   * 
   * @param assetAdministrationShellDescriptor Asset Administration Shell Descriptor object (required)
   * @return ApiResponse&lt;AssetAdministrationShellDescriptor&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<AssetAdministrationShellDescriptor> postAssetAdministrationShellDescriptorWithHttpInfo(AssetAdministrationShellDescriptor assetAdministrationShellDescriptor) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = postAssetAdministrationShellDescriptorRequestBuilder(assetAdministrationShellDescriptor);
    try {
      HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(
          localVarRequestBuilder.build(),
          HttpResponse.BodyHandlers.ofInputStream());
      if (memberVarResponseInterceptor != null) {
        memberVarResponseInterceptor.accept(localVarResponse);
      }
      try {
        if (localVarResponse.statusCode()/ 100 != 2) {
          throw getApiException("postAssetAdministrationShellDescriptor", localVarResponse);
        }
        return new ApiResponse<AssetAdministrationShellDescriptor>(
          localVarResponse.statusCode(),
          localVarResponse.headers().map(),
          localVarResponse.body() == null ? null : memberVarObjectMapper.readValue(localVarResponse.body(), new TypeReference<AssetAdministrationShellDescriptor>() {}) // closes the InputStream
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

  private HttpRequest.Builder postAssetAdministrationShellDescriptorRequestBuilder(AssetAdministrationShellDescriptor assetAdministrationShellDescriptor) throws ApiException {
    // verify the required parameter 'assetAdministrationShellDescriptor' is set
    if (assetAdministrationShellDescriptor == null) {
      throw new ApiException(400, "Missing the required parameter 'assetAdministrationShellDescriptor' when calling postAssetAdministrationShellDescriptor");
    }

    

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/shell-descriptors";

    localVarRequestBuilder.uri(URI.create(memberVarBaseUri + localVarPath));

    localVarRequestBuilder.header("Content-Type", "application/json");
    localVarRequestBuilder.header("Accept", "application/json");
    
    addAuthorizationHeaderIfAuthIsEnabled(localVarRequestBuilder);

    try {
      byte[] localVarPostBody = memberVarObjectMapper.writeValueAsBytes(assetAdministrationShellDescriptor);
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
   * Creates a new Submodel Descriptor, i.e. registers a submodel
   * 
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelDescriptor Submodel Descriptor object (required)
   * @return SubmodelDescriptor
   * @throws ApiException if fails to make API call
   */
  public SubmodelDescriptor postSubmodelDescriptorThroughSuperpath(String aasIdentifier, SubmodelDescriptor submodelDescriptor) throws ApiException {
    ApiResponse<SubmodelDescriptor> localVarResponse = postSubmodelDescriptorThroughSuperpathWithHttpInfo(aasIdentifier, submodelDescriptor);
    return localVarResponse.getData();
  }

  /**
   * Creates a new Submodel Descriptor, i.e. registers a submodel
   * 
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelDescriptor Submodel Descriptor object (required)
   * @return ApiResponse&lt;SubmodelDescriptor&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<SubmodelDescriptor> postSubmodelDescriptorThroughSuperpathWithHttpInfo(String aasIdentifier, SubmodelDescriptor submodelDescriptor) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = postSubmodelDescriptorThroughSuperpathRequestBuilder(aasIdentifier, submodelDescriptor);
    try {
      HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(
          localVarRequestBuilder.build(),
          HttpResponse.BodyHandlers.ofInputStream());
      if (memberVarResponseInterceptor != null) {
        memberVarResponseInterceptor.accept(localVarResponse);
      }
      try {
        if (localVarResponse.statusCode()/ 100 != 2) {
          throw getApiException("postSubmodelDescriptorThroughSuperpath", localVarResponse);
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

  private HttpRequest.Builder postSubmodelDescriptorThroughSuperpathRequestBuilder(String aasIdentifier, SubmodelDescriptor submodelDescriptor) throws ApiException {
    // verify the required parameter 'aasIdentifier' is set
    if (aasIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'aasIdentifier' when calling postSubmodelDescriptorThroughSuperpath");
    }
    // verify the required parameter 'submodelDescriptor' is set
    if (submodelDescriptor == null) {
      throw new ApiException(400, "Missing the required parameter 'submodelDescriptor' when calling postSubmodelDescriptorThroughSuperpath");
    }

    String aasIdentifierAsBase64EncodedParam = aasIdentifier == null ? null : new String(java.util.Base64.getUrlEncoder().encode(aasIdentifier.getBytes(java.nio.charset.StandardCharsets.UTF_8)), java.nio.charset.StandardCharsets.UTF_8);
    

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/shell-descriptors/{aasIdentifier}/submodel-descriptors"
        .replace("{aasIdentifier}", ApiClient.urlEncode(aasIdentifierAsBase64EncodedParam.toString()));

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
   * Updates an existing Asset Administration Shell Descriptor
   * 
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param assetAdministrationShellDescriptor Asset Administration Shell Descriptor object (required)
   * @throws ApiException if fails to make API call
   */
  public void putAssetAdministrationShellDescriptorById(String aasIdentifier, AssetAdministrationShellDescriptor assetAdministrationShellDescriptor) throws ApiException {
    putAssetAdministrationShellDescriptorByIdWithHttpInfo(aasIdentifier, assetAdministrationShellDescriptor);
  }

  /**
   * Updates an existing Asset Administration Shell Descriptor
   * 
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param assetAdministrationShellDescriptor Asset Administration Shell Descriptor object (required)
   * @return ApiResponse&lt;Void&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<Void> putAssetAdministrationShellDescriptorByIdWithHttpInfo(String aasIdentifier, AssetAdministrationShellDescriptor assetAdministrationShellDescriptor) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = putAssetAdministrationShellDescriptorByIdRequestBuilder(aasIdentifier, assetAdministrationShellDescriptor);
    try {
      HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(
          localVarRequestBuilder.build(),
          HttpResponse.BodyHandlers.ofInputStream());
      if (memberVarResponseInterceptor != null) {
        memberVarResponseInterceptor.accept(localVarResponse);
      }
      try {
        if (localVarResponse.statusCode()/ 100 != 2) {
          throw getApiException("putAssetAdministrationShellDescriptorById", localVarResponse);
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

  private HttpRequest.Builder putAssetAdministrationShellDescriptorByIdRequestBuilder(String aasIdentifier, AssetAdministrationShellDescriptor assetAdministrationShellDescriptor) throws ApiException {
    // verify the required parameter 'aasIdentifier' is set
    if (aasIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'aasIdentifier' when calling putAssetAdministrationShellDescriptorById");
    }
    // verify the required parameter 'assetAdministrationShellDescriptor' is set
    if (assetAdministrationShellDescriptor == null) {
      throw new ApiException(400, "Missing the required parameter 'assetAdministrationShellDescriptor' when calling putAssetAdministrationShellDescriptorById");
    }

    String aasIdentifierAsBase64EncodedParam = aasIdentifier == null ? null : new String(java.util.Base64.getUrlEncoder().encode(aasIdentifier.getBytes(java.nio.charset.StandardCharsets.UTF_8)), java.nio.charset.StandardCharsets.UTF_8);
    

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/shell-descriptors/{aasIdentifier}"
        .replace("{aasIdentifier}", ApiClient.urlEncode(aasIdentifierAsBase64EncodedParam.toString()));

    localVarRequestBuilder.uri(URI.create(memberVarBaseUri + localVarPath));

    localVarRequestBuilder.header("Content-Type", "application/json");
    localVarRequestBuilder.header("Accept", "application/json");
    
    addAuthorizationHeaderIfAuthIsEnabled(localVarRequestBuilder);

    try {
      byte[] localVarPostBody = memberVarObjectMapper.writeValueAsBytes(assetAdministrationShellDescriptor);
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
  /**
   * Updates an existing Submodel Descriptor
   * 
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelDescriptor Submodel Descriptor object (required)
   * @throws ApiException if fails to make API call
   */
  public void putSubmodelDescriptorByIdThroughSuperpath(String aasIdentifier, String submodelIdentifier, SubmodelDescriptor submodelDescriptor) throws ApiException {
    putSubmodelDescriptorByIdThroughSuperpathWithHttpInfo(aasIdentifier, submodelIdentifier, submodelDescriptor);
  }

  /**
   * Updates an existing Submodel Descriptor
   * 
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelDescriptor Submodel Descriptor object (required)
   * @return ApiResponse&lt;Void&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<Void> putSubmodelDescriptorByIdThroughSuperpathWithHttpInfo(String aasIdentifier, String submodelIdentifier, SubmodelDescriptor submodelDescriptor) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = putSubmodelDescriptorByIdThroughSuperpathRequestBuilder(aasIdentifier, submodelIdentifier, submodelDescriptor);
    try {
      HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(
          localVarRequestBuilder.build(),
          HttpResponse.BodyHandlers.ofInputStream());
      if (memberVarResponseInterceptor != null) {
        memberVarResponseInterceptor.accept(localVarResponse);
      }
      try {
        if (localVarResponse.statusCode()/ 100 != 2) {
          throw getApiException("putSubmodelDescriptorByIdThroughSuperpath", localVarResponse);
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

  private HttpRequest.Builder putSubmodelDescriptorByIdThroughSuperpathRequestBuilder(String aasIdentifier, String submodelIdentifier, SubmodelDescriptor submodelDescriptor) throws ApiException {
    // verify the required parameter 'aasIdentifier' is set
    if (aasIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'aasIdentifier' when calling putSubmodelDescriptorByIdThroughSuperpath");
    }
    // verify the required parameter 'submodelIdentifier' is set
    if (submodelIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'submodelIdentifier' when calling putSubmodelDescriptorByIdThroughSuperpath");
    }
    // verify the required parameter 'submodelDescriptor' is set
    if (submodelDescriptor == null) {
      throw new ApiException(400, "Missing the required parameter 'submodelDescriptor' when calling putSubmodelDescriptorByIdThroughSuperpath");
    }

    String aasIdentifierAsBase64EncodedParam = aasIdentifier == null ? null : new String(java.util.Base64.getUrlEncoder().encode(aasIdentifier.getBytes(java.nio.charset.StandardCharsets.UTF_8)), java.nio.charset.StandardCharsets.UTF_8);
    String submodelIdentifierAsBase64EncodedParam = submodelIdentifier == null ? null : new String(java.util.Base64.getUrlEncoder().encode(submodelIdentifier.getBytes(java.nio.charset.StandardCharsets.UTF_8)), java.nio.charset.StandardCharsets.UTF_8);
    

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/shell-descriptors/{aasIdentifier}/submodel-descriptors/{submodelIdentifier}"
        .replace("{aasIdentifier}", ApiClient.urlEncode(aasIdentifierAsBase64EncodedParam.toString()))
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
  /**
   * 
   * 
   * @param shellDescriptorSearchRequest  (required)
   * @return ShellDescriptorSearchResponse
   * @throws ApiException if fails to make API call
   */
  public ShellDescriptorSearchResponse searchShellDescriptors(ShellDescriptorSearchRequest shellDescriptorSearchRequest) throws ApiException {
    ApiResponse<ShellDescriptorSearchResponse> localVarResponse = searchShellDescriptorsWithHttpInfo(shellDescriptorSearchRequest);
    return localVarResponse.getData();
  }

  /**
   * 
   * 
   * @param shellDescriptorSearchRequest  (required)
   * @return ApiResponse&lt;ShellDescriptorSearchResponse&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<ShellDescriptorSearchResponse> searchShellDescriptorsWithHttpInfo(ShellDescriptorSearchRequest shellDescriptorSearchRequest) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = searchShellDescriptorsRequestBuilder(shellDescriptorSearchRequest);
    try {
      HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(
          localVarRequestBuilder.build(),
          HttpResponse.BodyHandlers.ofInputStream());
      if (memberVarResponseInterceptor != null) {
        memberVarResponseInterceptor.accept(localVarResponse);
      }
      try {
        if (localVarResponse.statusCode()/ 100 != 2) {
          throw getApiException("searchShellDescriptors", localVarResponse);
        }
        return new ApiResponse<ShellDescriptorSearchResponse>(
          localVarResponse.statusCode(),
          localVarResponse.headers().map(),
          localVarResponse.body() == null ? null : memberVarObjectMapper.readValue(localVarResponse.body(), new TypeReference<ShellDescriptorSearchResponse>() {}) // closes the InputStream
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

  private HttpRequest.Builder searchShellDescriptorsRequestBuilder(ShellDescriptorSearchRequest shellDescriptorSearchRequest) throws ApiException {
    // verify the required parameter 'shellDescriptorSearchRequest' is set
    if (shellDescriptorSearchRequest == null) {
      throw new ApiException(400, "Missing the required parameter 'shellDescriptorSearchRequest' when calling searchShellDescriptors");
    }

    

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/search";

    localVarRequestBuilder.uri(URI.create(memberVarBaseUri + localVarPath));

    localVarRequestBuilder.header("Content-Type", "application/json");
    localVarRequestBuilder.header("Accept", "application/json");
    
    addAuthorizationHeaderIfAuthIsEnabled(localVarRequestBuilder);

    try {
      byte[] localVarPostBody = memberVarObjectMapper.writeValueAsBytes(shellDescriptorSearchRequest);
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

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
package org.eclipse.digitaltwin.basyx.v3.clients.api;

import org.eclipse.digitaltwin.basyx.v3.clients.ApiClient;
import org.eclipse.digitaltwin.basyx.v3.clients.ApiException;
import org.eclipse.digitaltwin.basyx.v3.clients.ApiResponse;
import org.eclipse.digitaltwin.basyx.v3.clients.Pair;
import org.eclipse.digitaltwin.basyx.v3.clients.JSON;

import org.eclipse.digitaltwin.basyx.v3.clients.model.part2.BaseOperationResult;
import org.eclipse.digitaltwin.basyx.v3.clients.model.part1.Environment;
import java.io.File;
import org.eclipse.digitaltwin.basyx.v3.clients.model.part2.GetPathItemsResult;
import org.eclipse.digitaltwin.basyx.v3.clients.model.part2.GetReferencesResult;
import org.eclipse.digitaltwin.basyx.v3.clients.model.part2.GetSubmodelElementsMetadataResult;
import org.eclipse.digitaltwin.basyx.v3.clients.model.part2.GetSubmodelElementsResult;
import org.eclipse.digitaltwin.basyx.v3.clients.model.part2.GetSubmodelElementsValueResult;
import org.eclipse.digitaltwin.basyx.v3.clients.model.part2.OperationRequest;
import org.eclipse.digitaltwin.basyx.v3.clients.model.part2.OperationRequestValueOnly;
import org.eclipse.digitaltwin.basyx.v3.clients.model.part2.OperationResult;
import org.eclipse.digitaltwin.basyx.v3.clients.model.part2.OperationResultValueOnly;
import org.eclipse.digitaltwin.basyx.v3.clients.model.part1.Reference;
import org.eclipse.digitaltwin.basyx.v3.clients.model.part2.Result;
import org.eclipse.digitaltwin.basyx.v3.clients.model.part2.ServiceDescription;
import org.eclipse.digitaltwin.basyx.v3.clients.model.part1.Submodel;
import org.eclipse.digitaltwin.basyx.v3.clients.model.part1.SubmodelElement;
import org.eclipse.digitaltwin.basyx.v3.clients.model.part2.SubmodelElementMetadata;
import org.eclipse.digitaltwin.basyx.v3.clients.model.part2.SubmodelElementValue;
import org.eclipse.digitaltwin.basyx.v3.clients.model.part2.SubmodelMetadata;
import org.eclipse.digitaltwin.basyx.v3.clients.model.part2.SubmodelValue;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;

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

@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen", date = "2024-02-08T10:09:10.597431+01:00[Europe/Berlin]")
public class SubmodelServiceApi {
  private final HttpClient memberVarHttpClient;
  private final ObjectMapper memberVarObjectMapper;
  private final String memberVarBaseUri;
  private final Consumer<HttpRequest.Builder> memberVarInterceptor;
  private final Duration memberVarReadTimeout;
  private final Consumer<HttpResponse<InputStream>> memberVarResponseInterceptor;
  private final Consumer<HttpResponse<String>> memberVarAsyncResponseInterceptor;

  public SubmodelServiceApi() {
    this(new ApiClient());
  }

  public SubmodelServiceApi(ObjectMapper mapper, String baseUri) {
    this(new ApiClient(HttpClient.newBuilder(), mapper, baseUri));
  }
  
  public SubmodelServiceApi(String baseUri) {
    this(new ApiClient(HttpClient.newBuilder(), JSON.getDefault().getMapper(), baseUri));
  }


  public SubmodelServiceApi(ApiClient apiClient) {
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
   * Deletes file content of an existing submodel element at a specified path within submodel elements hierarchy
   * 
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @throws ApiException if fails to make API call
   */
  public void deleteFileByPath(String idShortPath) throws ApiException {

    deleteFileByPathWithHttpInfo(idShortPath);
  }

  /**
   * Deletes file content of an existing submodel element at a specified path within submodel elements hierarchy
   * 
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @return ApiResponse&lt;Void&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<Void> deleteFileByPathWithHttpInfo(String idShortPath) throws ApiException {
  	return deleteFileByPathWithHttpInfoNoUrlEncoding(idShortPath);
 	
 }


  /**
   * Deletes file content of an existing submodel element at a specified path within submodel elements hierarchy
   * 
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @return ApiResponse&lt;Void&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<Void> deleteFileByPathWithHttpInfoNoUrlEncoding(String idShortPath) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = deleteFileByPathRequestBuilder(idShortPath);
    try {
      HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(
          localVarRequestBuilder.build(),
          HttpResponse.BodyHandlers.ofInputStream());
      if (memberVarResponseInterceptor != null) {
        memberVarResponseInterceptor.accept(localVarResponse);
      }
      try {
        if (localVarResponse.statusCode()/ 100 != 2) {
          throw getApiException("deleteFileByPath", localVarResponse);
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

  private HttpRequest.Builder deleteFileByPathRequestBuilder(String idShortPath) throws ApiException {
    // verify the required parameter 'idShortPath' is set
    if (idShortPath == null) {
      throw new ApiException(400, "Missing the required parameter 'idShortPath' when calling deleteFileByPath");
    }

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/submodel/submodel-elements/{idShortPath}/attachment"
        .replace("{idShortPath}", ApiClient.urlEncode(idShortPath.toString()));

    localVarRequestBuilder.uri(URI.create(memberVarBaseUri + localVarPath));

    localVarRequestBuilder.header("Accept", "application/json");

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
   * Deletes a submodel element at a specified path within the submodel elements hierarchy
   * 
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @throws ApiException if fails to make API call
   */
  public void deleteSubmodelElementByPath(String idShortPath) throws ApiException {

    deleteSubmodelElementByPathWithHttpInfo(idShortPath);
  }

  /**
   * Deletes a submodel element at a specified path within the submodel elements hierarchy
   * 
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @return ApiResponse&lt;Void&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<Void> deleteSubmodelElementByPathWithHttpInfo(String idShortPath) throws ApiException {
  	return deleteSubmodelElementByPathWithHttpInfoNoUrlEncoding(idShortPath);
 	
 }


  /**
   * Deletes a submodel element at a specified path within the submodel elements hierarchy
   * 
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @return ApiResponse&lt;Void&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<Void> deleteSubmodelElementByPathWithHttpInfoNoUrlEncoding(String idShortPath) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = deleteSubmodelElementByPathRequestBuilder(idShortPath);
    try {
      HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(
          localVarRequestBuilder.build(),
          HttpResponse.BodyHandlers.ofInputStream());
      if (memberVarResponseInterceptor != null) {
        memberVarResponseInterceptor.accept(localVarResponse);
      }
      try {
        if (localVarResponse.statusCode()/ 100 != 2) {
          throw getApiException("deleteSubmodelElementByPath", localVarResponse);
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

  private HttpRequest.Builder deleteSubmodelElementByPathRequestBuilder(String idShortPath) throws ApiException {
    // verify the required parameter 'idShortPath' is set
    if (idShortPath == null) {
      throw new ApiException(400, "Missing the required parameter 'idShortPath' when calling deleteSubmodelElementByPath");
    }

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/submodel/submodel-elements/{idShortPath}"
        .replace("{idShortPath}", ApiClient.urlEncode(idShortPath.toString()));

    localVarRequestBuilder.uri(URI.create(memberVarBaseUri + localVarPath));

    localVarRequestBuilder.header("Accept", "application/json");

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
   * Returns an appropriate serialization based on the specified format (see SerializationFormat)
   * 
   * @param aasIds The Asset Administration Shells&#39; unique ids (UTF8-BASE64-URL-encoded) (optional
   * @param submodelIds The Submodels&#39; unique ids (UTF8-BASE64-URL-encoded) (optional
   * @param includeConceptDescriptions Include Concept Descriptions? (optional, default to true)
   * @return File
   * @throws ApiException if fails to make API call
   */
  public File generateSerializationByIds(List<String> aasIds, List<String> submodelIds, Boolean includeConceptDescriptions) throws ApiException {

    ApiResponse<File> localVarResponse = generateSerializationByIdsWithHttpInfo(aasIds, submodelIds, includeConceptDescriptions);
    return localVarResponse.getData();
  }

  /**
   * Returns an appropriate serialization based on the specified format (see SerializationFormat)
   * 
   * @param aasIds The Asset Administration Shells&#39; unique ids (UTF8-BASE64-URL-encoded) (optional
   * @param submodelIds The Submodels&#39; unique ids (UTF8-BASE64-URL-encoded) (optional
   * @param includeConceptDescriptions Include Concept Descriptions? (optional, default to true)
   * @return ApiResponse&lt;File&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<File> generateSerializationByIdsWithHttpInfo(List<String> aasIds, List<String> submodelIds, Boolean includeConceptDescriptions) throws ApiException {
  	return generateSerializationByIdsWithHttpInfoNoUrlEncoding(aasIds, submodelIds, includeConceptDescriptions);
 	
 }


  /**
   * Returns an appropriate serialization based on the specified format (see SerializationFormat)
   * 
   * @param aasIds The Asset Administration Shells&#39; unique ids (UTF8-BASE64-URL-encoded) (optional
   * @param submodelIds The Submodels&#39; unique ids (UTF8-BASE64-URL-encoded) (optional
   * @param includeConceptDescriptions Include Concept Descriptions? (optional, default to true)
   * @return ApiResponse&lt;File&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<File> generateSerializationByIdsWithHttpInfoNoUrlEncoding(List<String> aasIds, List<String> submodelIds, Boolean includeConceptDescriptions) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = generateSerializationByIdsRequestBuilder(aasIds, submodelIds, includeConceptDescriptions);
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
        return new ApiResponse<File>(
          localVarResponse.statusCode(),
          localVarResponse.headers().map(),
          localVarResponse.body() == null ? null : memberVarObjectMapper.readValue(localVarResponse.body(), new TypeReference<File>() {}) // closes the InputStream
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

  private HttpRequest.Builder generateSerializationByIdsRequestBuilder(List<String> aasIds, List<String> submodelIds, Boolean includeConceptDescriptions) throws ApiException {

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/serialization";

    List<Pair> localVarQueryParams = new ArrayList<>();
    StringJoiner localVarQueryStringJoiner = new StringJoiner("&");
    String localVarQueryParameterBaseName;
    localVarQueryParameterBaseName = "aasIds";
    localVarQueryParams.addAll(ApiClient.parameterToPairs("multi", "aasIds", aasIds));
    localVarQueryParameterBaseName = "submodelIds";
    localVarQueryParams.addAll(ApiClient.parameterToPairs("multi", "submodelIds", submodelIds));
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

    localVarRequestBuilder.header("Accept", "application/asset-administration-shell-package+xml, application/json, application/xml");

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
   * Returns all submodel elements including their hierarchy
   * 
   * @param limit The maximum number of elements in the response array (optional)
   * @param cursor A server-generated identifier retrieved from pagingMetadata that specifies from which position the result listing should continue (optional)
   * @param level Determines the structural depth of the respective resource content (optional, default to deep)
   * @param extent Determines to which extent the resource is being serialized (optional, default to withoutBlobValue)
   * @return GetSubmodelElementsResult
   * @throws ApiException if fails to make API call
   */
  public GetSubmodelElementsResult getAllSubmodelElements(Integer limit, String cursor, String level, String extent) throws ApiException {

    ApiResponse<GetSubmodelElementsResult> localVarResponse = getAllSubmodelElementsWithHttpInfo(limit, cursor, level, extent);
    return localVarResponse.getData();
  }

  /**
   * Returns all submodel elements including their hierarchy
   * 
   * @param limit The maximum number of elements in the response array (optional)
   * @param cursor A server-generated identifier retrieved from pagingMetadata that specifies from which position the result listing should continue (optional)
   * @param level Determines the structural depth of the respective resource content (optional, default to deep)
   * @param extent Determines to which extent the resource is being serialized (optional, default to withoutBlobValue)
   * @return ApiResponse&lt;GetSubmodelElementsResult&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<GetSubmodelElementsResult> getAllSubmodelElementsWithHttpInfo(Integer limit, String cursor, String level, String extent) throws ApiException {
  	return getAllSubmodelElementsWithHttpInfoNoUrlEncoding(limit, cursor, level, extent);
 	
 }


  /**
   * Returns all submodel elements including their hierarchy
   * 
   * @param limit The maximum number of elements in the response array (optional)
   * @param cursor A server-generated identifier retrieved from pagingMetadata that specifies from which position the result listing should continue (optional)
   * @param level Determines the structural depth of the respective resource content (optional, default to deep)
   * @param extent Determines to which extent the resource is being serialized (optional, default to withoutBlobValue)
   * @return ApiResponse&lt;GetSubmodelElementsResult&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<GetSubmodelElementsResult> getAllSubmodelElementsWithHttpInfoNoUrlEncoding(Integer limit, String cursor, String level, String extent) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = getAllSubmodelElementsRequestBuilder(limit, cursor, level, extent);
    try {
      HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(
          localVarRequestBuilder.build(),
          HttpResponse.BodyHandlers.ofInputStream());
      if (memberVarResponseInterceptor != null) {
        memberVarResponseInterceptor.accept(localVarResponse);
      }
      try {
        if (localVarResponse.statusCode()/ 100 != 2) {
          throw getApiException("getAllSubmodelElements", localVarResponse);
        }
        return new ApiResponse<GetSubmodelElementsResult>(
          localVarResponse.statusCode(),
          localVarResponse.headers().map(),
          localVarResponse.body() == null ? null : memberVarObjectMapper.readValue(localVarResponse.body(), new TypeReference<GetSubmodelElementsResult>() {}) // closes the InputStream
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

  private HttpRequest.Builder getAllSubmodelElementsRequestBuilder(Integer limit, String cursor, String level, String extent) throws ApiException {

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/submodel/submodel-elements";

    List<Pair> localVarQueryParams = new ArrayList<>();
    StringJoiner localVarQueryStringJoiner = new StringJoiner("&");
    String localVarQueryParameterBaseName;
    localVarQueryParameterBaseName = "limit";
    localVarQueryParams.addAll(ApiClient.parameterToPairs("limit", limit));
    localVarQueryParameterBaseName = "cursor";
    localVarQueryParams.addAll(ApiClient.parameterToPairs("cursor", cursor));
    localVarQueryParameterBaseName = "level";
    localVarQueryParams.addAll(ApiClient.parameterToPairs("level", level));
    localVarQueryParameterBaseName = "extent";
    localVarQueryParams.addAll(ApiClient.parameterToPairs("extent", extent));

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
   * Returns the metadata attributes of all submodel elements including their hierarchy
   * 
   * @param limit The maximum number of elements in the response array (optional)
   * @param cursor A server-generated identifier retrieved from pagingMetadata that specifies from which position the result listing should continue (optional)
   * @param level Determines the structural depth of the respective resource content (optional, default to deep)
   * @return GetSubmodelElementsMetadataResult
   * @throws ApiException if fails to make API call
   */
  public GetSubmodelElementsMetadataResult getAllSubmodelElementsMetadata(Integer limit, String cursor, String level) throws ApiException {

    ApiResponse<GetSubmodelElementsMetadataResult> localVarResponse = getAllSubmodelElementsMetadataWithHttpInfo(limit, cursor, level);
    return localVarResponse.getData();
  }

  /**
   * Returns the metadata attributes of all submodel elements including their hierarchy
   * 
   * @param limit The maximum number of elements in the response array (optional)
   * @param cursor A server-generated identifier retrieved from pagingMetadata that specifies from which position the result listing should continue (optional)
   * @param level Determines the structural depth of the respective resource content (optional, default to deep)
   * @return ApiResponse&lt;GetSubmodelElementsMetadataResult&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<GetSubmodelElementsMetadataResult> getAllSubmodelElementsMetadataWithHttpInfo(Integer limit, String cursor, String level) throws ApiException {
  	return getAllSubmodelElementsMetadataWithHttpInfoNoUrlEncoding(limit, cursor, level);
 	
 }


  /**
   * Returns the metadata attributes of all submodel elements including their hierarchy
   * 
   * @param limit The maximum number of elements in the response array (optional)
   * @param cursor A server-generated identifier retrieved from pagingMetadata that specifies from which position the result listing should continue (optional)
   * @param level Determines the structural depth of the respective resource content (optional, default to deep)
   * @return ApiResponse&lt;GetSubmodelElementsMetadataResult&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<GetSubmodelElementsMetadataResult> getAllSubmodelElementsMetadataWithHttpInfoNoUrlEncoding(Integer limit, String cursor, String level) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = getAllSubmodelElementsMetadataRequestBuilder(limit, cursor, level);
    try {
      HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(
          localVarRequestBuilder.build(),
          HttpResponse.BodyHandlers.ofInputStream());
      if (memberVarResponseInterceptor != null) {
        memberVarResponseInterceptor.accept(localVarResponse);
      }
      try {
        if (localVarResponse.statusCode()/ 100 != 2) {
          throw getApiException("getAllSubmodelElementsMetadata", localVarResponse);
        }
        return new ApiResponse<GetSubmodelElementsMetadataResult>(
          localVarResponse.statusCode(),
          localVarResponse.headers().map(),
          localVarResponse.body() == null ? null : memberVarObjectMapper.readValue(localVarResponse.body(), new TypeReference<GetSubmodelElementsMetadataResult>() {}) // closes the InputStream
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

  private HttpRequest.Builder getAllSubmodelElementsMetadataRequestBuilder(Integer limit, String cursor, String level) throws ApiException {

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/submodel/submodel-elements/$metadata";

    List<Pair> localVarQueryParams = new ArrayList<>();
    StringJoiner localVarQueryStringJoiner = new StringJoiner("&");
    String localVarQueryParameterBaseName;
    localVarQueryParameterBaseName = "limit";
    localVarQueryParams.addAll(ApiClient.parameterToPairs("limit", limit));
    localVarQueryParameterBaseName = "cursor";
    localVarQueryParams.addAll(ApiClient.parameterToPairs("cursor", cursor));
    localVarQueryParameterBaseName = "level";
    localVarQueryParams.addAll(ApiClient.parameterToPairs("level", level));

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
   * Returns all submodel elements including their hierarchy in the Path notation
   * 
   * @param limit The maximum number of elements in the response array (optional)
   * @param cursor A server-generated identifier retrieved from pagingMetadata that specifies from which position the result listing should continue (optional)
   * @param level Determines the structural depth of the respective resource content (optional, default to deep)
   * @return GetPathItemsResult
   * @throws ApiException if fails to make API call
   */
  public GetPathItemsResult getAllSubmodelElementsPath(Integer limit, String cursor, String level) throws ApiException {

    ApiResponse<GetPathItemsResult> localVarResponse = getAllSubmodelElementsPathWithHttpInfo(limit, cursor, level);
    return localVarResponse.getData();
  }

  /**
   * Returns all submodel elements including their hierarchy in the Path notation
   * 
   * @param limit The maximum number of elements in the response array (optional)
   * @param cursor A server-generated identifier retrieved from pagingMetadata that specifies from which position the result listing should continue (optional)
   * @param level Determines the structural depth of the respective resource content (optional, default to deep)
   * @return ApiResponse&lt;GetPathItemsResult&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<GetPathItemsResult> getAllSubmodelElementsPathWithHttpInfo(Integer limit, String cursor, String level) throws ApiException {
  	return getAllSubmodelElementsPathWithHttpInfoNoUrlEncoding(limit, cursor, level);
 	
 }


  /**
   * Returns all submodel elements including their hierarchy in the Path notation
   * 
   * @param limit The maximum number of elements in the response array (optional)
   * @param cursor A server-generated identifier retrieved from pagingMetadata that specifies from which position the result listing should continue (optional)
   * @param level Determines the structural depth of the respective resource content (optional, default to deep)
   * @return ApiResponse&lt;GetPathItemsResult&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<GetPathItemsResult> getAllSubmodelElementsPathWithHttpInfoNoUrlEncoding(Integer limit, String cursor, String level) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = getAllSubmodelElementsPathRequestBuilder(limit, cursor, level);
    try {
      HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(
          localVarRequestBuilder.build(),
          HttpResponse.BodyHandlers.ofInputStream());
      if (memberVarResponseInterceptor != null) {
        memberVarResponseInterceptor.accept(localVarResponse);
      }
      try {
        if (localVarResponse.statusCode()/ 100 != 2) {
          throw getApiException("getAllSubmodelElementsPath", localVarResponse);
        }
        return new ApiResponse<GetPathItemsResult>(
          localVarResponse.statusCode(),
          localVarResponse.headers().map(),
          localVarResponse.body() == null ? null : memberVarObjectMapper.readValue(localVarResponse.body(), new TypeReference<GetPathItemsResult>() {}) // closes the InputStream
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

  private HttpRequest.Builder getAllSubmodelElementsPathRequestBuilder(Integer limit, String cursor, String level) throws ApiException {

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/submodel/submodel-elements/$path";

    List<Pair> localVarQueryParams = new ArrayList<>();
    StringJoiner localVarQueryStringJoiner = new StringJoiner("&");
    String localVarQueryParameterBaseName;
    localVarQueryParameterBaseName = "limit";
    localVarQueryParams.addAll(ApiClient.parameterToPairs("limit", limit));
    localVarQueryParameterBaseName = "cursor";
    localVarQueryParams.addAll(ApiClient.parameterToPairs("cursor", cursor));
    localVarQueryParameterBaseName = "level";
    localVarQueryParams.addAll(ApiClient.parameterToPairs("level", level));

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
   * Returns the References of all submodel elements
   * 
   * @param limit The maximum number of elements in the response array (optional)
   * @param cursor A server-generated identifier retrieved from pagingMetadata that specifies from which position the result listing should continue (optional)
   * @param level Determines the structural depth of the respective resource content (optional, default to core)
   * @return GetReferencesResult
   * @throws ApiException if fails to make API call
   */
  public GetReferencesResult getAllSubmodelElementsReference(Integer limit, String cursor, String level) throws ApiException {

    ApiResponse<GetReferencesResult> localVarResponse = getAllSubmodelElementsReferenceWithHttpInfo(limit, cursor, level);
    return localVarResponse.getData();
  }

  /**
   * Returns the References of all submodel elements
   * 
   * @param limit The maximum number of elements in the response array (optional)
   * @param cursor A server-generated identifier retrieved from pagingMetadata that specifies from which position the result listing should continue (optional)
   * @param level Determines the structural depth of the respective resource content (optional, default to core)
   * @return ApiResponse&lt;GetReferencesResult&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<GetReferencesResult> getAllSubmodelElementsReferenceWithHttpInfo(Integer limit, String cursor, String level) throws ApiException {
  	return getAllSubmodelElementsReferenceWithHttpInfoNoUrlEncoding(limit, cursor, level);
 	
 }


  /**
   * Returns the References of all submodel elements
   * 
   * @param limit The maximum number of elements in the response array (optional)
   * @param cursor A server-generated identifier retrieved from pagingMetadata that specifies from which position the result listing should continue (optional)
   * @param level Determines the structural depth of the respective resource content (optional, default to core)
   * @return ApiResponse&lt;GetReferencesResult&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<GetReferencesResult> getAllSubmodelElementsReferenceWithHttpInfoNoUrlEncoding(Integer limit, String cursor, String level) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = getAllSubmodelElementsReferenceRequestBuilder(limit, cursor, level);
    try {
      HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(
          localVarRequestBuilder.build(),
          HttpResponse.BodyHandlers.ofInputStream());
      if (memberVarResponseInterceptor != null) {
        memberVarResponseInterceptor.accept(localVarResponse);
      }
      try {
        if (localVarResponse.statusCode()/ 100 != 2) {
          throw getApiException("getAllSubmodelElementsReference", localVarResponse);
        }
        return new ApiResponse<GetReferencesResult>(
          localVarResponse.statusCode(),
          localVarResponse.headers().map(),
          localVarResponse.body() == null ? null : memberVarObjectMapper.readValue(localVarResponse.body(), new TypeReference<GetReferencesResult>() {}) // closes the InputStream
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

  private HttpRequest.Builder getAllSubmodelElementsReferenceRequestBuilder(Integer limit, String cursor, String level) throws ApiException {

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/submodel/submodel-elements/$reference";

    List<Pair> localVarQueryParams = new ArrayList<>();
    StringJoiner localVarQueryStringJoiner = new StringJoiner("&");
    String localVarQueryParameterBaseName;
    localVarQueryParameterBaseName = "limit";
    localVarQueryParams.addAll(ApiClient.parameterToPairs("limit", limit));
    localVarQueryParameterBaseName = "cursor";
    localVarQueryParams.addAll(ApiClient.parameterToPairs("cursor", cursor));
    localVarQueryParameterBaseName = "level";
    localVarQueryParams.addAll(ApiClient.parameterToPairs("level", level));

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
   * Returns all submodel elements including their hierarchy in the ValueOnly representation
   * 
   * @param limit The maximum number of elements in the response array (optional)
   * @param cursor A server-generated identifier retrieved from pagingMetadata that specifies from which position the result listing should continue (optional)
   * @param level Determines the structural depth of the respective resource content (optional, default to deep)
   * @param extent Determines to which extent the resource is being serialized (optional, default to withoutBlobValue)
   * @return GetSubmodelElementsValueResult
   * @throws ApiException if fails to make API call
   */
  public GetSubmodelElementsValueResult getAllSubmodelElementsValueOnly(Integer limit, String cursor, String level, String extent) throws ApiException {

    ApiResponse<GetSubmodelElementsValueResult> localVarResponse = getAllSubmodelElementsValueOnlyWithHttpInfo(limit, cursor, level, extent);
    return localVarResponse.getData();
  }

  /**
   * Returns all submodel elements including their hierarchy in the ValueOnly representation
   * 
   * @param limit The maximum number of elements in the response array (optional)
   * @param cursor A server-generated identifier retrieved from pagingMetadata that specifies from which position the result listing should continue (optional)
   * @param level Determines the structural depth of the respective resource content (optional, default to deep)
   * @param extent Determines to which extent the resource is being serialized (optional, default to withoutBlobValue)
   * @return ApiResponse&lt;GetSubmodelElementsValueResult&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<GetSubmodelElementsValueResult> getAllSubmodelElementsValueOnlyWithHttpInfo(Integer limit, String cursor, String level, String extent) throws ApiException {
  	return getAllSubmodelElementsValueOnlyWithHttpInfoNoUrlEncoding(limit, cursor, level, extent);
 	
 }


  /**
   * Returns all submodel elements including their hierarchy in the ValueOnly representation
   * 
   * @param limit The maximum number of elements in the response array (optional)
   * @param cursor A server-generated identifier retrieved from pagingMetadata that specifies from which position the result listing should continue (optional)
   * @param level Determines the structural depth of the respective resource content (optional, default to deep)
   * @param extent Determines to which extent the resource is being serialized (optional, default to withoutBlobValue)
   * @return ApiResponse&lt;GetSubmodelElementsValueResult&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<GetSubmodelElementsValueResult> getAllSubmodelElementsValueOnlyWithHttpInfoNoUrlEncoding(Integer limit, String cursor, String level, String extent) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = getAllSubmodelElementsValueOnlyRequestBuilder(limit, cursor, level, extent);
    try {
      HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(
          localVarRequestBuilder.build(),
          HttpResponse.BodyHandlers.ofInputStream());
      if (memberVarResponseInterceptor != null) {
        memberVarResponseInterceptor.accept(localVarResponse);
      }
      try {
        if (localVarResponse.statusCode()/ 100 != 2) {
          throw getApiException("getAllSubmodelElementsValueOnly", localVarResponse);
        }
        return new ApiResponse<GetSubmodelElementsValueResult>(
          localVarResponse.statusCode(),
          localVarResponse.headers().map(),
          localVarResponse.body() == null ? null : memberVarObjectMapper.readValue(localVarResponse.body(), new TypeReference<GetSubmodelElementsValueResult>() {}) // closes the InputStream
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

  private HttpRequest.Builder getAllSubmodelElementsValueOnlyRequestBuilder(Integer limit, String cursor, String level, String extent) throws ApiException {

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/submodel/submodel-elements/$value";

    List<Pair> localVarQueryParams = new ArrayList<>();
    StringJoiner localVarQueryStringJoiner = new StringJoiner("&");
    String localVarQueryParameterBaseName;
    localVarQueryParameterBaseName = "limit";
    localVarQueryParams.addAll(ApiClient.parameterToPairs("limit", limit));
    localVarQueryParameterBaseName = "cursor";
    localVarQueryParams.addAll(ApiClient.parameterToPairs("cursor", cursor));
    localVarQueryParameterBaseName = "level";
    localVarQueryParams.addAll(ApiClient.parameterToPairs("level", level));
    localVarQueryParameterBaseName = "extent";
    localVarQueryParams.addAll(ApiClient.parameterToPairs("extent", extent));

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
  	return getDescriptionWithHttpInfoNoUrlEncoding();
 	
 }


  /**
   * Returns the self-describing information of a network resource (ServiceDescription)
   * 
   * @return ApiResponse&lt;ServiceDescription&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<ServiceDescription> getDescriptionWithHttpInfoNoUrlEncoding() throws ApiException {
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
   * Downloads file content from a specific submodel element from the Submodel at a specified path
   * 
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @return File
   * @throws ApiException if fails to make API call
   */
  public File getFileByPath(String idShortPath) throws ApiException {

    ApiResponse<File> localVarResponse = getFileByPathWithHttpInfo(idShortPath);
    return localVarResponse.getData();
  }

  /**
   * Downloads file content from a specific submodel element from the Submodel at a specified path
   * 
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @return ApiResponse&lt;File&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<File> getFileByPathWithHttpInfo(String idShortPath) throws ApiException {
  	return getFileByPathWithHttpInfoNoUrlEncoding(idShortPath);
 	
 }


  /**
   * Downloads file content from a specific submodel element from the Submodel at a specified path
   * 
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @return ApiResponse&lt;File&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<File> getFileByPathWithHttpInfoNoUrlEncoding(String idShortPath) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = getFileByPathRequestBuilder(idShortPath);
    try {
      HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(
          localVarRequestBuilder.build(),
          HttpResponse.BodyHandlers.ofInputStream());
      if (memberVarResponseInterceptor != null) {
        memberVarResponseInterceptor.accept(localVarResponse);
      }
      try {
        if (localVarResponse.statusCode()/ 100 != 2) {
          throw getApiException("getFileByPath", localVarResponse);
        }
        return new ApiResponse<File>(
          localVarResponse.statusCode(),
          localVarResponse.headers().map(),
          localVarResponse.body() == null ? null : memberVarObjectMapper.readValue(localVarResponse.body(), new TypeReference<File>() {}) // closes the InputStream
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

  private HttpRequest.Builder getFileByPathRequestBuilder(String idShortPath) throws ApiException {
    // verify the required parameter 'idShortPath' is set
    if (idShortPath == null) {
      throw new ApiException(400, "Missing the required parameter 'idShortPath' when calling getFileByPath");
    }

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/submodel/submodel-elements/{idShortPath}/attachment"
        .replace("{idShortPath}", ApiClient.urlEncode(idShortPath.toString()));

    localVarRequestBuilder.uri(URI.create(memberVarBaseUri + localVarPath));

    localVarRequestBuilder.header("Accept", "application/octet-stream, application/json");

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
   * Returns the Operation result of an asynchronous invoked Operation
   * 
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param handleId The returned handle id of an operation’s asynchronous invocation used to request the current state of the operation’s execution (UTF8-BASE64-URL-encoded) (required)
   * @return OperationResult
   * @throws ApiException if fails to make API call
   */
  public OperationResult getOperationAsyncResult(String idShortPath, String handleId) throws ApiException {

    ApiResponse<OperationResult> localVarResponse = getOperationAsyncResultWithHttpInfo(idShortPath, handleId);
    return localVarResponse.getData();
  }

  /**
   * Returns the Operation result of an asynchronous invoked Operation
   * 
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param handleId The returned handle id of an operation’s asynchronous invocation used to request the current state of the operation’s execution (UTF8-BASE64-URL-encoded) (required)
   * @return ApiResponse&lt;OperationResult&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<OperationResult> getOperationAsyncResultWithHttpInfo(String idShortPath, String handleId) throws ApiException {
    String  handleIdAsBytes = ApiClient.base64UrlEncode(handleId);
  	return getOperationAsyncResultWithHttpInfoNoUrlEncoding(idShortPath, handleIdAsBytes);
 	
 }


  /**
   * Returns the Operation result of an asynchronous invoked Operation
   * 
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param handleId The returned handle id of an operation’s asynchronous invocation used to request the current state of the operation’s execution (UTF8-BASE64-URL-encoded) (required)
   * @return ApiResponse&lt;OperationResult&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<OperationResult> getOperationAsyncResultWithHttpInfoNoUrlEncoding(String idShortPath, String handleId) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = getOperationAsyncResultRequestBuilder(idShortPath, handleId);
    try {
      HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(
          localVarRequestBuilder.build(),
          HttpResponse.BodyHandlers.ofInputStream());
      if (memberVarResponseInterceptor != null) {
        memberVarResponseInterceptor.accept(localVarResponse);
      }
      try {
        if (localVarResponse.statusCode()/ 100 != 2) {
          throw getApiException("getOperationAsyncResult", localVarResponse);
        }
        return new ApiResponse<OperationResult>(
          localVarResponse.statusCode(),
          localVarResponse.headers().map(),
          localVarResponse.body() == null ? null : memberVarObjectMapper.readValue(localVarResponse.body(), new TypeReference<OperationResult>() {}) // closes the InputStream
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

  private HttpRequest.Builder getOperationAsyncResultRequestBuilder(String idShortPath, String handleId) throws ApiException {
    // verify the required parameter 'idShortPath' is set
    if (idShortPath == null) {
      throw new ApiException(400, "Missing the required parameter 'idShortPath' when calling getOperationAsyncResult");
    }
    // verify the required parameter 'handleId' is set
    if (handleId == null) {
      throw new ApiException(400, "Missing the required parameter 'handleId' when calling getOperationAsyncResult");
    }

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/submodel/submodel-elements/{idShortPath}/operation-results/{handleId}"
        .replace("{idShortPath}", ApiClient.urlEncode(idShortPath.toString()))
        .replace("{handleId}", ApiClient.urlEncode(handleId.toString()));

    localVarRequestBuilder.uri(URI.create(memberVarBaseUri + localVarPath));

    localVarRequestBuilder.header("Accept", "application/json");

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
   * Returns the value of the Operation result of an asynchronous invoked Operation
   * 
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param handleId The returned handle id of an operation’s asynchronous invocation used to request the current state of the operation’s execution (UTF8-BASE64-URL-encoded) (required)
   * @return OperationResultValueOnly
   * @throws ApiException if fails to make API call
   */
  public OperationResultValueOnly getOperationAsyncResultValueOnly(String idShortPath, String handleId) throws ApiException {

    ApiResponse<OperationResultValueOnly> localVarResponse = getOperationAsyncResultValueOnlyWithHttpInfo(idShortPath, handleId);
    return localVarResponse.getData();
  }

  /**
   * Returns the value of the Operation result of an asynchronous invoked Operation
   * 
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param handleId The returned handle id of an operation’s asynchronous invocation used to request the current state of the operation’s execution (UTF8-BASE64-URL-encoded) (required)
   * @return ApiResponse&lt;OperationResultValueOnly&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<OperationResultValueOnly> getOperationAsyncResultValueOnlyWithHttpInfo(String idShortPath, String handleId) throws ApiException {
    String  handleIdAsBytes = ApiClient.base64UrlEncode(handleId);
  	return getOperationAsyncResultValueOnlyWithHttpInfoNoUrlEncoding(idShortPath, handleIdAsBytes);
 	
 }


  /**
   * Returns the value of the Operation result of an asynchronous invoked Operation
   * 
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param handleId The returned handle id of an operation’s asynchronous invocation used to request the current state of the operation’s execution (UTF8-BASE64-URL-encoded) (required)
   * @return ApiResponse&lt;OperationResultValueOnly&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<OperationResultValueOnly> getOperationAsyncResultValueOnlyWithHttpInfoNoUrlEncoding(String idShortPath, String handleId) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = getOperationAsyncResultValueOnlyRequestBuilder(idShortPath, handleId);
    try {
      HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(
          localVarRequestBuilder.build(),
          HttpResponse.BodyHandlers.ofInputStream());
      if (memberVarResponseInterceptor != null) {
        memberVarResponseInterceptor.accept(localVarResponse);
      }
      try {
        if (localVarResponse.statusCode()/ 100 != 2) {
          throw getApiException("getOperationAsyncResultValueOnly", localVarResponse);
        }
        return new ApiResponse<OperationResultValueOnly>(
          localVarResponse.statusCode(),
          localVarResponse.headers().map(),
          localVarResponse.body() == null ? null : memberVarObjectMapper.readValue(localVarResponse.body(), new TypeReference<OperationResultValueOnly>() {}) // closes the InputStream
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

  private HttpRequest.Builder getOperationAsyncResultValueOnlyRequestBuilder(String idShortPath, String handleId) throws ApiException {
    // verify the required parameter 'idShortPath' is set
    if (idShortPath == null) {
      throw new ApiException(400, "Missing the required parameter 'idShortPath' when calling getOperationAsyncResultValueOnly");
    }
    // verify the required parameter 'handleId' is set
    if (handleId == null) {
      throw new ApiException(400, "Missing the required parameter 'handleId' when calling getOperationAsyncResultValueOnly");
    }

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/submodel/submodel-elements/{idShortPath}/operation-results/{handleId}/$value"
        .replace("{idShortPath}", ApiClient.urlEncode(idShortPath.toString()))
        .replace("{handleId}", ApiClient.urlEncode(handleId.toString()));

    localVarRequestBuilder.uri(URI.create(memberVarBaseUri + localVarPath));

    localVarRequestBuilder.header("Accept", "application/json");

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
   * Returns the status of an asynchronously invoked Operation
   * 
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param handleId The returned handle id of an operation’s asynchronous invocation used to request the current state of the operation’s execution (UTF8-BASE64-URL-encoded) (required)
   * @return BaseOperationResult
   * @throws ApiException if fails to make API call
   */
  public BaseOperationResult getOperationAsyncStatus(String idShortPath, String handleId) throws ApiException {

    ApiResponse<BaseOperationResult> localVarResponse = getOperationAsyncStatusWithHttpInfo(idShortPath, handleId);
    return localVarResponse.getData();
  }

  /**
   * Returns the status of an asynchronously invoked Operation
   * 
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param handleId The returned handle id of an operation’s asynchronous invocation used to request the current state of the operation’s execution (UTF8-BASE64-URL-encoded) (required)
   * @return ApiResponse&lt;BaseOperationResult&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<BaseOperationResult> getOperationAsyncStatusWithHttpInfo(String idShortPath, String handleId) throws ApiException {
    String  handleIdAsBytes = ApiClient.base64UrlEncode(handleId);
  	return getOperationAsyncStatusWithHttpInfoNoUrlEncoding(idShortPath, handleIdAsBytes);
 	
 }


  /**
   * Returns the status of an asynchronously invoked Operation
   * 
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param handleId The returned handle id of an operation’s asynchronous invocation used to request the current state of the operation’s execution (UTF8-BASE64-URL-encoded) (required)
   * @return ApiResponse&lt;BaseOperationResult&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<BaseOperationResult> getOperationAsyncStatusWithHttpInfoNoUrlEncoding(String idShortPath, String handleId) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = getOperationAsyncStatusRequestBuilder(idShortPath, handleId);
    try {
      HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(
          localVarRequestBuilder.build(),
          HttpResponse.BodyHandlers.ofInputStream());
      if (memberVarResponseInterceptor != null) {
        memberVarResponseInterceptor.accept(localVarResponse);
      }
      try {
        if (localVarResponse.statusCode()/ 100 != 2) {
          throw getApiException("getOperationAsyncStatus", localVarResponse);
        }
        return new ApiResponse<BaseOperationResult>(
          localVarResponse.statusCode(),
          localVarResponse.headers().map(),
          localVarResponse.body() == null ? null : memberVarObjectMapper.readValue(localVarResponse.body(), new TypeReference<BaseOperationResult>() {}) // closes the InputStream
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

  private HttpRequest.Builder getOperationAsyncStatusRequestBuilder(String idShortPath, String handleId) throws ApiException {
    // verify the required parameter 'idShortPath' is set
    if (idShortPath == null) {
      throw new ApiException(400, "Missing the required parameter 'idShortPath' when calling getOperationAsyncStatus");
    }
    // verify the required parameter 'handleId' is set
    if (handleId == null) {
      throw new ApiException(400, "Missing the required parameter 'handleId' when calling getOperationAsyncStatus");
    }

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/submodel/submodel-elements/{idShortPath}/operation-status/{handleId}"
        .replace("{idShortPath}", ApiClient.urlEncode(idShortPath.toString()))
        .replace("{handleId}", ApiClient.urlEncode(handleId.toString()));

    localVarRequestBuilder.uri(URI.create(memberVarBaseUri + localVarPath));

    localVarRequestBuilder.header("Accept", "application/json");

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
   * Returns the Submodel
   * 
   * @param level Determines the structural depth of the respective resource content (optional, default to deep)
   * @param extent Determines to which extent the resource is being serialized (optional, default to withoutBlobValue)
   * @return Submodel
   * @throws ApiException if fails to make API call
   */
  public Submodel getSubmodel(String level, String extent) throws ApiException {

    ApiResponse<Submodel> localVarResponse = getSubmodelWithHttpInfo(level, extent);
    return localVarResponse.getData();
  }

  /**
   * Returns the Submodel
   * 
   * @param level Determines the structural depth of the respective resource content (optional, default to deep)
   * @param extent Determines to which extent the resource is being serialized (optional, default to withoutBlobValue)
   * @return ApiResponse&lt;Submodel&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<Submodel> getSubmodelWithHttpInfo(String level, String extent) throws ApiException {
  	return getSubmodelWithHttpInfoNoUrlEncoding(level, extent);
 	
 }


  /**
   * Returns the Submodel
   * 
   * @param level Determines the structural depth of the respective resource content (optional, default to deep)
   * @param extent Determines to which extent the resource is being serialized (optional, default to withoutBlobValue)
   * @return ApiResponse&lt;Submodel&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<Submodel> getSubmodelWithHttpInfoNoUrlEncoding(String level, String extent) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = getSubmodelRequestBuilder(level, extent);
    try {
      HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(
          localVarRequestBuilder.build(),
          HttpResponse.BodyHandlers.ofInputStream());
      if (memberVarResponseInterceptor != null) {
        memberVarResponseInterceptor.accept(localVarResponse);
      }
      try {
        if (localVarResponse.statusCode()/ 100 != 2) {
          throw getApiException("getSubmodel", localVarResponse);
        }
        return new ApiResponse<Submodel>(
          localVarResponse.statusCode(),
          localVarResponse.headers().map(),
          localVarResponse.body() == null ? null : memberVarObjectMapper.readValue(localVarResponse.body(), new TypeReference<Submodel>() {}) // closes the InputStream
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

  private HttpRequest.Builder getSubmodelRequestBuilder(String level, String extent) throws ApiException {

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/submodel";

    List<Pair> localVarQueryParams = new ArrayList<>();
    StringJoiner localVarQueryStringJoiner = new StringJoiner("&");
    String localVarQueryParameterBaseName;
    localVarQueryParameterBaseName = "level";
    localVarQueryParams.addAll(ApiClient.parameterToPairs("level", level));
    localVarQueryParameterBaseName = "extent";
    localVarQueryParams.addAll(ApiClient.parameterToPairs("extent", extent));

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
   * Returns a specific submodel element from the Submodel at a specified path
   * 
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param level Determines the structural depth of the respective resource content (optional, default to deep)
   * @param extent Determines to which extent the resource is being serialized (optional, default to withoutBlobValue)
   * @return SubmodelElement
   * @throws ApiException if fails to make API call
   */
  public SubmodelElement getSubmodelElementByPath(String idShortPath, String level, String extent) throws ApiException {

    ApiResponse<SubmodelElement> localVarResponse = getSubmodelElementByPathWithHttpInfo(idShortPath, level, extent);
    return localVarResponse.getData();
  }

  /**
   * Returns a specific submodel element from the Submodel at a specified path
   * 
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param level Determines the structural depth of the respective resource content (optional, default to deep)
   * @param extent Determines to which extent the resource is being serialized (optional, default to withoutBlobValue)
   * @return ApiResponse&lt;SubmodelElement&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<SubmodelElement> getSubmodelElementByPathWithHttpInfo(String idShortPath, String level, String extent) throws ApiException {
  	return getSubmodelElementByPathWithHttpInfoNoUrlEncoding(idShortPath, level, extent);
 	
 }


  /**
   * Returns a specific submodel element from the Submodel at a specified path
   * 
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param level Determines the structural depth of the respective resource content (optional, default to deep)
   * @param extent Determines to which extent the resource is being serialized (optional, default to withoutBlobValue)
   * @return ApiResponse&lt;SubmodelElement&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<SubmodelElement> getSubmodelElementByPathWithHttpInfoNoUrlEncoding(String idShortPath, String level, String extent) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = getSubmodelElementByPathRequestBuilder(idShortPath, level, extent);
    try {
      HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(
          localVarRequestBuilder.build(),
          HttpResponse.BodyHandlers.ofInputStream());
      if (memberVarResponseInterceptor != null) {
        memberVarResponseInterceptor.accept(localVarResponse);
      }
      try {
        if (localVarResponse.statusCode()/ 100 != 2) {
          throw getApiException("getSubmodelElementByPath", localVarResponse);
        }
        return new ApiResponse<SubmodelElement>(
          localVarResponse.statusCode(),
          localVarResponse.headers().map(),
          localVarResponse.body() == null ? null : memberVarObjectMapper.readValue(localVarResponse.body(), new TypeReference<SubmodelElement>() {}) // closes the InputStream
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

  private HttpRequest.Builder getSubmodelElementByPathRequestBuilder(String idShortPath, String level, String extent) throws ApiException {
    // verify the required parameter 'idShortPath' is set
    if (idShortPath == null) {
      throw new ApiException(400, "Missing the required parameter 'idShortPath' when calling getSubmodelElementByPath");
    }

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/submodel/submodel-elements/{idShortPath}"
        .replace("{idShortPath}", ApiClient.urlEncode(idShortPath.toString()));

    List<Pair> localVarQueryParams = new ArrayList<>();
    StringJoiner localVarQueryStringJoiner = new StringJoiner("&");
    String localVarQueryParameterBaseName;
    localVarQueryParameterBaseName = "level";
    localVarQueryParams.addAll(ApiClient.parameterToPairs("level", level));
    localVarQueryParameterBaseName = "extent";
    localVarQueryParams.addAll(ApiClient.parameterToPairs("extent", extent));

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
   * Returns the matadata attributes of a specific submodel element from the Submodel at a specified path
   * 
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param cursor A server-generated identifier retrieved from pagingMetadata that specifies from which position the result listing should continue (optional)
   * @return SubmodelElementMetadata
   * @throws ApiException if fails to make API call
   */
  public SubmodelElementMetadata getSubmodelElementByPathMetadata(String idShortPath, String cursor) throws ApiException {

    ApiResponse<SubmodelElementMetadata> localVarResponse = getSubmodelElementByPathMetadataWithHttpInfo(idShortPath, cursor);
    return localVarResponse.getData();
  }

  /**
   * Returns the matadata attributes of a specific submodel element from the Submodel at a specified path
   * 
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param cursor A server-generated identifier retrieved from pagingMetadata that specifies from which position the result listing should continue (optional)
   * @return ApiResponse&lt;SubmodelElementMetadata&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<SubmodelElementMetadata> getSubmodelElementByPathMetadataWithHttpInfo(String idShortPath, String cursor) throws ApiException {
  	return getSubmodelElementByPathMetadataWithHttpInfoNoUrlEncoding(idShortPath, cursor);
 	
 }


  /**
   * Returns the matadata attributes of a specific submodel element from the Submodel at a specified path
   * 
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param cursor A server-generated identifier retrieved from pagingMetadata that specifies from which position the result listing should continue (optional)
   * @return ApiResponse&lt;SubmodelElementMetadata&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<SubmodelElementMetadata> getSubmodelElementByPathMetadataWithHttpInfoNoUrlEncoding(String idShortPath, String cursor) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = getSubmodelElementByPathMetadataRequestBuilder(idShortPath, cursor);
    try {
      HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(
          localVarRequestBuilder.build(),
          HttpResponse.BodyHandlers.ofInputStream());
      if (memberVarResponseInterceptor != null) {
        memberVarResponseInterceptor.accept(localVarResponse);
      }
      try {
        if (localVarResponse.statusCode()/ 100 != 2) {
          throw getApiException("getSubmodelElementByPathMetadata", localVarResponse);
        }
        return new ApiResponse<SubmodelElementMetadata>(
          localVarResponse.statusCode(),
          localVarResponse.headers().map(),
          localVarResponse.body() == null ? null : memberVarObjectMapper.readValue(localVarResponse.body(), new TypeReference<SubmodelElementMetadata>() {}) // closes the InputStream
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

  private HttpRequest.Builder getSubmodelElementByPathMetadataRequestBuilder(String idShortPath, String cursor) throws ApiException {
    // verify the required parameter 'idShortPath' is set
    if (idShortPath == null) {
      throw new ApiException(400, "Missing the required parameter 'idShortPath' when calling getSubmodelElementByPathMetadata");
    }

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/submodel/submodel-elements/{idShortPath}/$metadata"
        .replace("{idShortPath}", ApiClient.urlEncode(idShortPath.toString()));

    List<Pair> localVarQueryParams = new ArrayList<>();
    StringJoiner localVarQueryStringJoiner = new StringJoiner("&");
    String localVarQueryParameterBaseName;
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
   * Returns a specific submodel element from the Submodel at a specified path in the Path notation
   * 
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param level Determines the structural depth of the respective resource content (optional, default to deep)
   * @return String
   * @throws ApiException if fails to make API call
   */
  public String getSubmodelElementByPathPath(String idShortPath, String level) throws ApiException {

    ApiResponse<String> localVarResponse = getSubmodelElementByPathPathWithHttpInfo(idShortPath, level);
    return localVarResponse.getData();
  }

  /**
   * Returns a specific submodel element from the Submodel at a specified path in the Path notation
   * 
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param level Determines the structural depth of the respective resource content (optional, default to deep)
   * @return ApiResponse&lt;String&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<String> getSubmodelElementByPathPathWithHttpInfo(String idShortPath, String level) throws ApiException {
  	return getSubmodelElementByPathPathWithHttpInfoNoUrlEncoding(idShortPath, level);
 	
 }


  /**
   * Returns a specific submodel element from the Submodel at a specified path in the Path notation
   * 
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param level Determines the structural depth of the respective resource content (optional, default to deep)
   * @return ApiResponse&lt;String&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<String> getSubmodelElementByPathPathWithHttpInfoNoUrlEncoding(String idShortPath, String level) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = getSubmodelElementByPathPathRequestBuilder(idShortPath, level);
    try {
      HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(
          localVarRequestBuilder.build(),
          HttpResponse.BodyHandlers.ofInputStream());
      if (memberVarResponseInterceptor != null) {
        memberVarResponseInterceptor.accept(localVarResponse);
      }
      try {
        if (localVarResponse.statusCode()/ 100 != 2) {
          throw getApiException("getSubmodelElementByPathPath", localVarResponse);
        }
        return new ApiResponse<String>(
          localVarResponse.statusCode(),
          localVarResponse.headers().map(),
          localVarResponse.body() == null ? null : memberVarObjectMapper.readValue(localVarResponse.body(), new TypeReference<String>() {}) // closes the InputStream
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

  private HttpRequest.Builder getSubmodelElementByPathPathRequestBuilder(String idShortPath, String level) throws ApiException {
    // verify the required parameter 'idShortPath' is set
    if (idShortPath == null) {
      throw new ApiException(400, "Missing the required parameter 'idShortPath' when calling getSubmodelElementByPathPath");
    }

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/submodel/submodel-elements/{idShortPath}/$path"
        .replace("{idShortPath}", ApiClient.urlEncode(idShortPath.toString()));

    List<Pair> localVarQueryParams = new ArrayList<>();
    StringJoiner localVarQueryStringJoiner = new StringJoiner("&");
    String localVarQueryParameterBaseName;
    localVarQueryParameterBaseName = "level";
    localVarQueryParams.addAll(ApiClient.parameterToPairs("level", level));

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
   * Returns the Referene of a specific submodel element from the Submodel at a specified path
   * 
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param level Determines the structural depth of the respective resource content (optional, default to core)
   * @return Reference
   * @throws ApiException if fails to make API call
   */
  public Reference getSubmodelElementByPathReference(String idShortPath, String level) throws ApiException {

    ApiResponse<Reference> localVarResponse = getSubmodelElementByPathReferenceWithHttpInfo(idShortPath, level);
    return localVarResponse.getData();
  }

  /**
   * Returns the Referene of a specific submodel element from the Submodel at a specified path
   * 
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param level Determines the structural depth of the respective resource content (optional, default to core)
   * @return ApiResponse&lt;Reference&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<Reference> getSubmodelElementByPathReferenceWithHttpInfo(String idShortPath, String level) throws ApiException {
  	return getSubmodelElementByPathReferenceWithHttpInfoNoUrlEncoding(idShortPath, level);
 	
 }


  /**
   * Returns the Referene of a specific submodel element from the Submodel at a specified path
   * 
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param level Determines the structural depth of the respective resource content (optional, default to core)
   * @return ApiResponse&lt;Reference&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<Reference> getSubmodelElementByPathReferenceWithHttpInfoNoUrlEncoding(String idShortPath, String level) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = getSubmodelElementByPathReferenceRequestBuilder(idShortPath, level);
    try {
      HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(
          localVarRequestBuilder.build(),
          HttpResponse.BodyHandlers.ofInputStream());
      if (memberVarResponseInterceptor != null) {
        memberVarResponseInterceptor.accept(localVarResponse);
      }
      try {
        if (localVarResponse.statusCode()/ 100 != 2) {
          throw getApiException("getSubmodelElementByPathReference", localVarResponse);
        }
        return new ApiResponse<Reference>(
          localVarResponse.statusCode(),
          localVarResponse.headers().map(),
          localVarResponse.body() == null ? null : memberVarObjectMapper.readValue(localVarResponse.body(), new TypeReference<Reference>() {}) // closes the InputStream
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

  private HttpRequest.Builder getSubmodelElementByPathReferenceRequestBuilder(String idShortPath, String level) throws ApiException {
    // verify the required parameter 'idShortPath' is set
    if (idShortPath == null) {
      throw new ApiException(400, "Missing the required parameter 'idShortPath' when calling getSubmodelElementByPathReference");
    }

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/submodel/submodel-elements/{idShortPath}/$reference"
        .replace("{idShortPath}", ApiClient.urlEncode(idShortPath.toString()));

    List<Pair> localVarQueryParams = new ArrayList<>();
    StringJoiner localVarQueryStringJoiner = new StringJoiner("&");
    String localVarQueryParameterBaseName;
    localVarQueryParameterBaseName = "level";
    localVarQueryParams.addAll(ApiClient.parameterToPairs("level", level));

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
   * Returns a specific submodel element from the Submodel at a specified path in the ValueOnly representation
   * 
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param level Determines the structural depth of the respective resource content (optional, default to deep)
   * @param extent Determines to which extent the resource is being serialized (optional, default to withoutBlobValue)
   * @return SubmodelElementValue
   * @throws ApiException if fails to make API call
   */
  public SubmodelElementValue getSubmodelElementByPathValueOnly(String idShortPath, String level, String extent) throws ApiException {

    ApiResponse<SubmodelElementValue> localVarResponse = getSubmodelElementByPathValueOnlyWithHttpInfo(idShortPath, level, extent);
    return localVarResponse.getData();
  }

  /**
   * Returns a specific submodel element from the Submodel at a specified path in the ValueOnly representation
   * 
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param level Determines the structural depth of the respective resource content (optional, default to deep)
   * @param extent Determines to which extent the resource is being serialized (optional, default to withoutBlobValue)
   * @return ApiResponse&lt;SubmodelElementValue&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<SubmodelElementValue> getSubmodelElementByPathValueOnlyWithHttpInfo(String idShortPath, String level, String extent) throws ApiException {
  	return getSubmodelElementByPathValueOnlyWithHttpInfoNoUrlEncoding(idShortPath, level, extent);
 	
 }


  /**
   * Returns a specific submodel element from the Submodel at a specified path in the ValueOnly representation
   * 
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param level Determines the structural depth of the respective resource content (optional, default to deep)
   * @param extent Determines to which extent the resource is being serialized (optional, default to withoutBlobValue)
   * @return ApiResponse&lt;SubmodelElementValue&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<SubmodelElementValue> getSubmodelElementByPathValueOnlyWithHttpInfoNoUrlEncoding(String idShortPath, String level, String extent) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = getSubmodelElementByPathValueOnlyRequestBuilder(idShortPath, level, extent);
    try {
      HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(
          localVarRequestBuilder.build(),
          HttpResponse.BodyHandlers.ofInputStream());
      if (memberVarResponseInterceptor != null) {
        memberVarResponseInterceptor.accept(localVarResponse);
      }
      try {
        if (localVarResponse.statusCode()/ 100 != 2) {
          throw getApiException("getSubmodelElementByPathValueOnly", localVarResponse);
        }
        return new ApiResponse<SubmodelElementValue>(
          localVarResponse.statusCode(),
          localVarResponse.headers().map(),
          localVarResponse.body() == null ? null : memberVarObjectMapper.readValue(localVarResponse.body(), new TypeReference<SubmodelElementValue>() {}) // closes the InputStream
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

  private HttpRequest.Builder getSubmodelElementByPathValueOnlyRequestBuilder(String idShortPath, String level, String extent) throws ApiException {
    // verify the required parameter 'idShortPath' is set
    if (idShortPath == null) {
      throw new ApiException(400, "Missing the required parameter 'idShortPath' when calling getSubmodelElementByPathValueOnly");
    }

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/submodel/submodel-elements/{idShortPath}/$value"
        .replace("{idShortPath}", ApiClient.urlEncode(idShortPath.toString()));

    List<Pair> localVarQueryParams = new ArrayList<>();
    StringJoiner localVarQueryStringJoiner = new StringJoiner("&");
    String localVarQueryParameterBaseName;
    localVarQueryParameterBaseName = "level";
    localVarQueryParams.addAll(ApiClient.parameterToPairs("level", level));
    localVarQueryParameterBaseName = "extent";
    localVarQueryParams.addAll(ApiClient.parameterToPairs("extent", extent));

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
   * Returns the metadata attributes of a specific Submodel
   * 
   * @param level Determines the structural depth of the respective resource content (optional, default to deep)
   * @return SubmodelMetadata
   * @throws ApiException if fails to make API call
   */
  public SubmodelMetadata getSubmodelMetadata(String level) throws ApiException {

    ApiResponse<SubmodelMetadata> localVarResponse = getSubmodelMetadataWithHttpInfo(level);
    return localVarResponse.getData();
  }

  /**
   * Returns the metadata attributes of a specific Submodel
   * 
   * @param level Determines the structural depth of the respective resource content (optional, default to deep)
   * @return ApiResponse&lt;SubmodelMetadata&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<SubmodelMetadata> getSubmodelMetadataWithHttpInfo(String level) throws ApiException {
  	return getSubmodelMetadataWithHttpInfoNoUrlEncoding(level);
 	
 }


  /**
   * Returns the metadata attributes of a specific Submodel
   * 
   * @param level Determines the structural depth of the respective resource content (optional, default to deep)
   * @return ApiResponse&lt;SubmodelMetadata&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<SubmodelMetadata> getSubmodelMetadataWithHttpInfoNoUrlEncoding(String level) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = getSubmodelMetadataRequestBuilder(level);
    try {
      HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(
          localVarRequestBuilder.build(),
          HttpResponse.BodyHandlers.ofInputStream());
      if (memberVarResponseInterceptor != null) {
        memberVarResponseInterceptor.accept(localVarResponse);
      }
      try {
        if (localVarResponse.statusCode()/ 100 != 2) {
          throw getApiException("getSubmodelMetadata", localVarResponse);
        }
        return new ApiResponse<SubmodelMetadata>(
          localVarResponse.statusCode(),
          localVarResponse.headers().map(),
          localVarResponse.body() == null ? null : memberVarObjectMapper.readValue(localVarResponse.body(), new TypeReference<SubmodelMetadata>() {}) // closes the InputStream
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

  private HttpRequest.Builder getSubmodelMetadataRequestBuilder(String level) throws ApiException {

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/submodel/$metadata";

    List<Pair> localVarQueryParams = new ArrayList<>();
    StringJoiner localVarQueryStringJoiner = new StringJoiner("&");
    String localVarQueryParameterBaseName;
    localVarQueryParameterBaseName = "level";
    localVarQueryParams.addAll(ApiClient.parameterToPairs("level", level));

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
   * Returns the Submodel in the Path notation
   * 
   * @param level Determines the structural depth of the respective resource content (optional, default to deep)
   * @return List&lt;String&gt;
   * @throws ApiException if fails to make API call
   */
  public List<String> getSubmodelPath(String level) throws ApiException {

    ApiResponse<List<String>> localVarResponse = getSubmodelPathWithHttpInfo(level);
    return localVarResponse.getData();
  }

  /**
   * Returns the Submodel in the Path notation
   * 
   * @param level Determines the structural depth of the respective resource content (optional, default to deep)
   * @return ApiResponse&lt;List&lt;String&gt;&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<List<String>> getSubmodelPathWithHttpInfo(String level) throws ApiException {
  	return getSubmodelPathWithHttpInfoNoUrlEncoding(level);
 	
 }


  /**
   * Returns the Submodel in the Path notation
   * 
   * @param level Determines the structural depth of the respective resource content (optional, default to deep)
   * @return ApiResponse&lt;List&lt;String&gt;&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<List<String>> getSubmodelPathWithHttpInfoNoUrlEncoding(String level) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = getSubmodelPathRequestBuilder(level);
    try {
      HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(
          localVarRequestBuilder.build(),
          HttpResponse.BodyHandlers.ofInputStream());
      if (memberVarResponseInterceptor != null) {
        memberVarResponseInterceptor.accept(localVarResponse);
      }
      try {
        if (localVarResponse.statusCode()/ 100 != 2) {
          throw getApiException("getSubmodelPath", localVarResponse);
        }
        return new ApiResponse<List<String>>(
          localVarResponse.statusCode(),
          localVarResponse.headers().map(),
          localVarResponse.body() == null ? null : memberVarObjectMapper.readValue(localVarResponse.body(), new TypeReference<List<String>>() {}) // closes the InputStream
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

  private HttpRequest.Builder getSubmodelPathRequestBuilder(String level) throws ApiException {

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/submodel/$path";

    List<Pair> localVarQueryParams = new ArrayList<>();
    StringJoiner localVarQueryStringJoiner = new StringJoiner("&");
    String localVarQueryParameterBaseName;
    localVarQueryParameterBaseName = "level";
    localVarQueryParams.addAll(ApiClient.parameterToPairs("level", level));

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
   * Returns the Reference of the Submodel
   * 
   * @param level Determines the structural depth of the respective resource content (optional, default to core)
   * @return Reference
   * @throws ApiException if fails to make API call
   */
  public Reference getSubmodelReference(String level) throws ApiException {

    ApiResponse<Reference> localVarResponse = getSubmodelReferenceWithHttpInfo(level);
    return localVarResponse.getData();
  }

  /**
   * Returns the Reference of the Submodel
   * 
   * @param level Determines the structural depth of the respective resource content (optional, default to core)
   * @return ApiResponse&lt;Reference&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<Reference> getSubmodelReferenceWithHttpInfo(String level) throws ApiException {
  	return getSubmodelReferenceWithHttpInfoNoUrlEncoding(level);
 	
 }


  /**
   * Returns the Reference of the Submodel
   * 
   * @param level Determines the structural depth of the respective resource content (optional, default to core)
   * @return ApiResponse&lt;Reference&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<Reference> getSubmodelReferenceWithHttpInfoNoUrlEncoding(String level) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = getSubmodelReferenceRequestBuilder(level);
    try {
      HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(
          localVarRequestBuilder.build(),
          HttpResponse.BodyHandlers.ofInputStream());
      if (memberVarResponseInterceptor != null) {
        memberVarResponseInterceptor.accept(localVarResponse);
      }
      try {
        if (localVarResponse.statusCode()/ 100 != 2) {
          throw getApiException("getSubmodelReference", localVarResponse);
        }
        return new ApiResponse<Reference>(
          localVarResponse.statusCode(),
          localVarResponse.headers().map(),
          localVarResponse.body() == null ? null : memberVarObjectMapper.readValue(localVarResponse.body(), new TypeReference<Reference>() {}) // closes the InputStream
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

  private HttpRequest.Builder getSubmodelReferenceRequestBuilder(String level) throws ApiException {

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/submodel/$reference";

    List<Pair> localVarQueryParams = new ArrayList<>();
    StringJoiner localVarQueryStringJoiner = new StringJoiner("&");
    String localVarQueryParameterBaseName;
    localVarQueryParameterBaseName = "level";
    localVarQueryParams.addAll(ApiClient.parameterToPairs("level", level));

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
   * Returns the Submodel in the ValueOnly representation
   * 
   * @param level Determines the structural depth of the respective resource content (optional, default to deep)
   * @param extent Determines to which extent the resource is being serialized (optional, default to withoutBlobValue)
   * @return SubmodelValue
   * @throws ApiException if fails to make API call
   */
  public SubmodelValue getSubmodelValueOnly(String level, String extent) throws ApiException {

    ApiResponse<SubmodelValue> localVarResponse = getSubmodelValueOnlyWithHttpInfo(level, extent);
    return localVarResponse.getData();
  }

  /**
   * Returns the Submodel in the ValueOnly representation
   * 
   * @param level Determines the structural depth of the respective resource content (optional, default to deep)
   * @param extent Determines to which extent the resource is being serialized (optional, default to withoutBlobValue)
   * @return ApiResponse&lt;SubmodelValue&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<SubmodelValue> getSubmodelValueOnlyWithHttpInfo(String level, String extent) throws ApiException {
  	return getSubmodelValueOnlyWithHttpInfoNoUrlEncoding(level, extent);
 	
 }


  /**
   * Returns the Submodel in the ValueOnly representation
   * 
   * @param level Determines the structural depth of the respective resource content (optional, default to deep)
   * @param extent Determines to which extent the resource is being serialized (optional, default to withoutBlobValue)
   * @return ApiResponse&lt;SubmodelValue&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<SubmodelValue> getSubmodelValueOnlyWithHttpInfoNoUrlEncoding(String level, String extent) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = getSubmodelValueOnlyRequestBuilder(level, extent);
    try {
      HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(
          localVarRequestBuilder.build(),
          HttpResponse.BodyHandlers.ofInputStream());
      if (memberVarResponseInterceptor != null) {
        memberVarResponseInterceptor.accept(localVarResponse);
      }
      try {
        if (localVarResponse.statusCode()/ 100 != 2) {
          throw getApiException("getSubmodelValueOnly", localVarResponse);
        }
        return new ApiResponse<SubmodelValue>(
          localVarResponse.statusCode(),
          localVarResponse.headers().map(),
          localVarResponse.body() == null ? null : memberVarObjectMapper.readValue(localVarResponse.body(), new TypeReference<SubmodelValue>() {}) // closes the InputStream
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

  private HttpRequest.Builder getSubmodelValueOnlyRequestBuilder(String level, String extent) throws ApiException {

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/submodel/$value";

    List<Pair> localVarQueryParams = new ArrayList<>();
    StringJoiner localVarQueryStringJoiner = new StringJoiner("&");
    String localVarQueryParameterBaseName;
    localVarQueryParameterBaseName = "level";
    localVarQueryParams.addAll(ApiClient.parameterToPairs("level", level));
    localVarQueryParameterBaseName = "extent";
    localVarQueryParams.addAll(ApiClient.parameterToPairs("extent", extent));

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
   * Synchronously invokes an Operation at a specified path
   * 
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param operationRequest Operation request object (required)
   * @return OperationResult
   * @throws ApiException if fails to make API call
   */
  public OperationResult invokeOperation(String idShortPath, OperationRequest operationRequest) throws ApiException {

    ApiResponse<OperationResult> localVarResponse = invokeOperationWithHttpInfo(idShortPath, operationRequest);
    return localVarResponse.getData();
  }

  /**
   * Synchronously invokes an Operation at a specified path
   * 
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param operationRequest Operation request object (required)
   * @return ApiResponse&lt;OperationResult&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<OperationResult> invokeOperationWithHttpInfo(String idShortPath, OperationRequest operationRequest) throws ApiException {
  	return invokeOperationWithHttpInfoNoUrlEncoding(idShortPath, operationRequest);
 	
 }


  /**
   * Synchronously invokes an Operation at a specified path
   * 
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param operationRequest Operation request object (required)
   * @return ApiResponse&lt;OperationResult&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<OperationResult> invokeOperationWithHttpInfoNoUrlEncoding(String idShortPath, OperationRequest operationRequest) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = invokeOperationRequestBuilder(idShortPath, operationRequest);
    try {
      HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(
          localVarRequestBuilder.build(),
          HttpResponse.BodyHandlers.ofInputStream());
      if (memberVarResponseInterceptor != null) {
        memberVarResponseInterceptor.accept(localVarResponse);
      }
      try {
        if (localVarResponse.statusCode()/ 100 != 2) {
          throw getApiException("invokeOperation", localVarResponse);
        }
        return new ApiResponse<OperationResult>(
          localVarResponse.statusCode(),
          localVarResponse.headers().map(),
          localVarResponse.body() == null ? null : memberVarObjectMapper.readValue(localVarResponse.body(), new TypeReference<OperationResult>() {}) // closes the InputStream
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

  private HttpRequest.Builder invokeOperationRequestBuilder(String idShortPath, OperationRequest operationRequest) throws ApiException {
    // verify the required parameter 'idShortPath' is set
    if (idShortPath == null) {
      throw new ApiException(400, "Missing the required parameter 'idShortPath' when calling invokeOperation");
    }
    // verify the required parameter 'operationRequest' is set
    if (operationRequest == null) {
      throw new ApiException(400, "Missing the required parameter 'operationRequest' when calling invokeOperation");
    }

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/submodel/submodel-elements/{idShortPath}/invoke"
        .replace("{idShortPath}", ApiClient.urlEncode(idShortPath.toString()));

    localVarRequestBuilder.uri(URI.create(memberVarBaseUri + localVarPath));

    localVarRequestBuilder.header("Content-Type", "application/json");
    localVarRequestBuilder.header("Accept", "application/json");

    try {
      byte[] localVarPostBody = memberVarObjectMapper.writeValueAsBytes(operationRequest);
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
   * Asynchronously invokes an Operation at a specified path
   * 
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param operationRequest Operation request object (required)
   * @throws ApiException if fails to make API call
   */
  public void invokeOperationAsync(String idShortPath, OperationRequest operationRequest) throws ApiException {

    invokeOperationAsyncWithHttpInfo(idShortPath, operationRequest);
  }

  /**
   * Asynchronously invokes an Operation at a specified path
   * 
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param operationRequest Operation request object (required)
   * @return ApiResponse&lt;Void&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<Void> invokeOperationAsyncWithHttpInfo(String idShortPath, OperationRequest operationRequest) throws ApiException {
  	return invokeOperationAsyncWithHttpInfoNoUrlEncoding(idShortPath, operationRequest);
 	
 }


  /**
   * Asynchronously invokes an Operation at a specified path
   * 
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param operationRequest Operation request object (required)
   * @return ApiResponse&lt;Void&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<Void> invokeOperationAsyncWithHttpInfoNoUrlEncoding(String idShortPath, OperationRequest operationRequest) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = invokeOperationAsyncRequestBuilder(idShortPath, operationRequest);
    try {
      HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(
          localVarRequestBuilder.build(),
          HttpResponse.BodyHandlers.ofInputStream());
      if (memberVarResponseInterceptor != null) {
        memberVarResponseInterceptor.accept(localVarResponse);
      }
      try {
        if (localVarResponse.statusCode()/ 100 != 2) {
          throw getApiException("invokeOperationAsync", localVarResponse);
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

  private HttpRequest.Builder invokeOperationAsyncRequestBuilder(String idShortPath, OperationRequest operationRequest) throws ApiException {
    // verify the required parameter 'idShortPath' is set
    if (idShortPath == null) {
      throw new ApiException(400, "Missing the required parameter 'idShortPath' when calling invokeOperationAsync");
    }
    // verify the required parameter 'operationRequest' is set
    if (operationRequest == null) {
      throw new ApiException(400, "Missing the required parameter 'operationRequest' when calling invokeOperationAsync");
    }

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/submodel/submodel-elements/{idShortPath}/invoke-async"
        .replace("{idShortPath}", ApiClient.urlEncode(idShortPath.toString()));

    localVarRequestBuilder.uri(URI.create(memberVarBaseUri + localVarPath));

    localVarRequestBuilder.header("Content-Type", "application/json");
    localVarRequestBuilder.header("Accept", "application/json");

    try {
      byte[] localVarPostBody = memberVarObjectMapper.writeValueAsBytes(operationRequest);
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
   * Asynchronously invokes an Operation at a specified path
   * 
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param operationRequestValueOnly Operation request object (required)
   * @throws ApiException if fails to make API call
   */
  public void invokeOperationAsyncValueOnly(String idShortPath, OperationRequestValueOnly operationRequestValueOnly) throws ApiException {

    invokeOperationAsyncValueOnlyWithHttpInfo(idShortPath, operationRequestValueOnly);
  }

  /**
   * Asynchronously invokes an Operation at a specified path
   * 
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param operationRequestValueOnly Operation request object (required)
   * @return ApiResponse&lt;Void&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<Void> invokeOperationAsyncValueOnlyWithHttpInfo(String idShortPath, OperationRequestValueOnly operationRequestValueOnly) throws ApiException {
  	return invokeOperationAsyncValueOnlyWithHttpInfoNoUrlEncoding(idShortPath, operationRequestValueOnly);
 	
 }


  /**
   * Asynchronously invokes an Operation at a specified path
   * 
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param operationRequestValueOnly Operation request object (required)
   * @return ApiResponse&lt;Void&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<Void> invokeOperationAsyncValueOnlyWithHttpInfoNoUrlEncoding(String idShortPath, OperationRequestValueOnly operationRequestValueOnly) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = invokeOperationAsyncValueOnlyRequestBuilder(idShortPath, operationRequestValueOnly);
    try {
      HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(
          localVarRequestBuilder.build(),
          HttpResponse.BodyHandlers.ofInputStream());
      if (memberVarResponseInterceptor != null) {
        memberVarResponseInterceptor.accept(localVarResponse);
      }
      try {
        if (localVarResponse.statusCode()/ 100 != 2) {
          throw getApiException("invokeOperationAsyncValueOnly", localVarResponse);
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

  private HttpRequest.Builder invokeOperationAsyncValueOnlyRequestBuilder(String idShortPath, OperationRequestValueOnly operationRequestValueOnly) throws ApiException {
    // verify the required parameter 'idShortPath' is set
    if (idShortPath == null) {
      throw new ApiException(400, "Missing the required parameter 'idShortPath' when calling invokeOperationAsyncValueOnly");
    }
    // verify the required parameter 'operationRequestValueOnly' is set
    if (operationRequestValueOnly == null) {
      throw new ApiException(400, "Missing the required parameter 'operationRequestValueOnly' when calling invokeOperationAsyncValueOnly");
    }

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/submodel/submodel-elements/{idShortPath}/invoke-async/$value"
        .replace("{idShortPath}", ApiClient.urlEncode(idShortPath.toString()));

    localVarRequestBuilder.uri(URI.create(memberVarBaseUri + localVarPath));

    localVarRequestBuilder.header("Content-Type", "application/json");
    localVarRequestBuilder.header("Accept", "application/json");

    try {
      byte[] localVarPostBody = memberVarObjectMapper.writeValueAsBytes(operationRequestValueOnly);
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
   * Synchronously invokes an Operation at a specified path
   * 
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param operationRequestValueOnly Operation request object (required)
   * @return OperationResultValueOnly
   * @throws ApiException if fails to make API call
   */
  public OperationResultValueOnly invokeOperationSyncValueOnly(String idShortPath, OperationRequestValueOnly operationRequestValueOnly) throws ApiException {

    ApiResponse<OperationResultValueOnly> localVarResponse = invokeOperationSyncValueOnlyWithHttpInfo(idShortPath, operationRequestValueOnly);
    return localVarResponse.getData();
  }

  /**
   * Synchronously invokes an Operation at a specified path
   * 
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param operationRequestValueOnly Operation request object (required)
   * @return ApiResponse&lt;OperationResultValueOnly&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<OperationResultValueOnly> invokeOperationSyncValueOnlyWithHttpInfo(String idShortPath, OperationRequestValueOnly operationRequestValueOnly) throws ApiException {
  	return invokeOperationSyncValueOnlyWithHttpInfoNoUrlEncoding(idShortPath, operationRequestValueOnly);
 	
 }


  /**
   * Synchronously invokes an Operation at a specified path
   * 
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param operationRequestValueOnly Operation request object (required)
   * @return ApiResponse&lt;OperationResultValueOnly&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<OperationResultValueOnly> invokeOperationSyncValueOnlyWithHttpInfoNoUrlEncoding(String idShortPath, OperationRequestValueOnly operationRequestValueOnly) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = invokeOperationSyncValueOnlyRequestBuilder(idShortPath, operationRequestValueOnly);
    try {
      HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(
          localVarRequestBuilder.build(),
          HttpResponse.BodyHandlers.ofInputStream());
      if (memberVarResponseInterceptor != null) {
        memberVarResponseInterceptor.accept(localVarResponse);
      }
      try {
        if (localVarResponse.statusCode()/ 100 != 2) {
          throw getApiException("invokeOperationSyncValueOnly", localVarResponse);
        }
        return new ApiResponse<OperationResultValueOnly>(
          localVarResponse.statusCode(),
          localVarResponse.headers().map(),
          localVarResponse.body() == null ? null : memberVarObjectMapper.readValue(localVarResponse.body(), new TypeReference<OperationResultValueOnly>() {}) // closes the InputStream
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

  private HttpRequest.Builder invokeOperationSyncValueOnlyRequestBuilder(String idShortPath, OperationRequestValueOnly operationRequestValueOnly) throws ApiException {
    // verify the required parameter 'idShortPath' is set
    if (idShortPath == null) {
      throw new ApiException(400, "Missing the required parameter 'idShortPath' when calling invokeOperationSyncValueOnly");
    }
    // verify the required parameter 'operationRequestValueOnly' is set
    if (operationRequestValueOnly == null) {
      throw new ApiException(400, "Missing the required parameter 'operationRequestValueOnly' when calling invokeOperationSyncValueOnly");
    }

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/submodel/submodel-elements/{idShortPath}/invoke/$value"
        .replace("{idShortPath}", ApiClient.urlEncode(idShortPath.toString()));

    localVarRequestBuilder.uri(URI.create(memberVarBaseUri + localVarPath));

    localVarRequestBuilder.header("Content-Type", "application/json");
    localVarRequestBuilder.header("Accept", "application/json");

    try {
      byte[] localVarPostBody = memberVarObjectMapper.writeValueAsBytes(operationRequestValueOnly);
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
   * Updates the Submodel
   * 
   * @param submodel Submodel object (required)
   * @param level Determines the structural depth of the respective resource content (optional, default to core)
   * @throws ApiException if fails to make API call
   */
  public void patchSubmodel(Submodel submodel, String level) throws ApiException {

    patchSubmodelWithHttpInfo(submodel, level);
  }

  /**
   * Updates the Submodel
   * 
   * @param submodel Submodel object (required)
   * @param level Determines the structural depth of the respective resource content (optional, default to core)
   * @return ApiResponse&lt;Void&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<Void> patchSubmodelWithHttpInfo(Submodel submodel, String level) throws ApiException {
  	return patchSubmodelWithHttpInfoNoUrlEncoding(submodel, level);
 	
 }


  /**
   * Updates the Submodel
   * 
   * @param submodel Submodel object (required)
   * @param level Determines the structural depth of the respective resource content (optional, default to core)
   * @return ApiResponse&lt;Void&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<Void> patchSubmodelWithHttpInfoNoUrlEncoding(Submodel submodel, String level) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = patchSubmodelRequestBuilder(submodel, level);
    try {
      HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(
          localVarRequestBuilder.build(),
          HttpResponse.BodyHandlers.ofInputStream());
      if (memberVarResponseInterceptor != null) {
        memberVarResponseInterceptor.accept(localVarResponse);
      }
      try {
        if (localVarResponse.statusCode()/ 100 != 2) {
          throw getApiException("patchSubmodel", localVarResponse);
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

  private HttpRequest.Builder patchSubmodelRequestBuilder(Submodel submodel, String level) throws ApiException {
    // verify the required parameter 'submodel' is set
    if (submodel == null) {
      throw new ApiException(400, "Missing the required parameter 'submodel' when calling patchSubmodel");
    }

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/submodel";

    List<Pair> localVarQueryParams = new ArrayList<>();
    StringJoiner localVarQueryStringJoiner = new StringJoiner("&");
    String localVarQueryParameterBaseName;
    localVarQueryParameterBaseName = "level";
    localVarQueryParams.addAll(ApiClient.parameterToPairs("level", level));

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

    localVarRequestBuilder.header("Content-Type", "application/json");
    localVarRequestBuilder.header("Accept", "application/json");

    try {
      byte[] localVarPostBody = memberVarObjectMapper.writeValueAsBytes(submodel);
      localVarRequestBuilder.method("PATCH", HttpRequest.BodyPublishers.ofByteArray(localVarPostBody));
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
   * Updates an existing SubmodelElement
   * 
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param submodelElement SubmodelElement object (required)
   * @param level Determines the structural depth of the respective resource content (optional, default to core)
   * @throws ApiException if fails to make API call
   */
  public void patchSubmodelElementByPath(String idShortPath, SubmodelElement submodelElement, String level) throws ApiException {

    patchSubmodelElementByPathWithHttpInfo(idShortPath, submodelElement, level);
  }

  /**
   * Updates an existing SubmodelElement
   * 
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param submodelElement SubmodelElement object (required)
   * @param level Determines the structural depth of the respective resource content (optional, default to core)
   * @return ApiResponse&lt;Void&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<Void> patchSubmodelElementByPathWithHttpInfo(String idShortPath, SubmodelElement submodelElement, String level) throws ApiException {
  	return patchSubmodelElementByPathWithHttpInfoNoUrlEncoding(idShortPath, submodelElement, level);
 	
 }


  /**
   * Updates an existing SubmodelElement
   * 
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param submodelElement SubmodelElement object (required)
   * @param level Determines the structural depth of the respective resource content (optional, default to core)
   * @return ApiResponse&lt;Void&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<Void> patchSubmodelElementByPathWithHttpInfoNoUrlEncoding(String idShortPath, SubmodelElement submodelElement, String level) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = patchSubmodelElementByPathRequestBuilder(idShortPath, submodelElement, level);
    try {
      HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(
          localVarRequestBuilder.build(),
          HttpResponse.BodyHandlers.ofInputStream());
      if (memberVarResponseInterceptor != null) {
        memberVarResponseInterceptor.accept(localVarResponse);
      }
      try {
        if (localVarResponse.statusCode()/ 100 != 2) {
          throw getApiException("patchSubmodelElementByPath", localVarResponse);
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

  private HttpRequest.Builder patchSubmodelElementByPathRequestBuilder(String idShortPath, SubmodelElement submodelElement, String level) throws ApiException {
    // verify the required parameter 'idShortPath' is set
    if (idShortPath == null) {
      throw new ApiException(400, "Missing the required parameter 'idShortPath' when calling patchSubmodelElementByPath");
    }
    // verify the required parameter 'submodelElement' is set
    if (submodelElement == null) {
      throw new ApiException(400, "Missing the required parameter 'submodelElement' when calling patchSubmodelElementByPath");
    }

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/submodel/submodel-elements/{idShortPath}"
        .replace("{idShortPath}", ApiClient.urlEncode(idShortPath.toString()));

    List<Pair> localVarQueryParams = new ArrayList<>();
    StringJoiner localVarQueryStringJoiner = new StringJoiner("&");
    String localVarQueryParameterBaseName;
    localVarQueryParameterBaseName = "level";
    localVarQueryParams.addAll(ApiClient.parameterToPairs("level", level));

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

    localVarRequestBuilder.header("Content-Type", "application/json");
    localVarRequestBuilder.header("Accept", "application/json");

    try {
      byte[] localVarPostBody = memberVarObjectMapper.writeValueAsBytes(submodelElement);
      localVarRequestBuilder.method("PATCH", HttpRequest.BodyPublishers.ofByteArray(localVarPostBody));
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
   * Updates the metadata attributes an existing SubmodelElement
   * 
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param getSubmodelElementsMetadataResult Metadata attributes of the SubmodelElement (required)
   * @param limit The maximum number of elements in the response array (optional)
   * @param cursor A server-generated identifier retrieved from pagingMetadata that specifies from which position the result listing should continue (optional)
   * @param level Determines the structural depth of the respective resource content (optional, default to core)
   * @throws ApiException if fails to make API call
   */
  public void patchSubmodelElementByPathMetadata(String idShortPath, GetSubmodelElementsMetadataResult getSubmodelElementsMetadataResult, Integer limit, String cursor, String level) throws ApiException {

    patchSubmodelElementByPathMetadataWithHttpInfo(idShortPath, getSubmodelElementsMetadataResult, limit, cursor, level);
  }

  /**
   * Updates the metadata attributes an existing SubmodelElement
   * 
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param getSubmodelElementsMetadataResult Metadata attributes of the SubmodelElement (required)
   * @param limit The maximum number of elements in the response array (optional)
   * @param cursor A server-generated identifier retrieved from pagingMetadata that specifies from which position the result listing should continue (optional)
   * @param level Determines the structural depth of the respective resource content (optional, default to core)
   * @return ApiResponse&lt;Void&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<Void> patchSubmodelElementByPathMetadataWithHttpInfo(String idShortPath, GetSubmodelElementsMetadataResult getSubmodelElementsMetadataResult, Integer limit, String cursor, String level) throws ApiException {
  	return patchSubmodelElementByPathMetadataWithHttpInfoNoUrlEncoding(idShortPath, getSubmodelElementsMetadataResult, limit, cursor, level);
 	
 }


  /**
   * Updates the metadata attributes an existing SubmodelElement
   * 
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param getSubmodelElementsMetadataResult Metadata attributes of the SubmodelElement (required)
   * @param limit The maximum number of elements in the response array (optional)
   * @param cursor A server-generated identifier retrieved from pagingMetadata that specifies from which position the result listing should continue (optional)
   * @param level Determines the structural depth of the respective resource content (optional, default to core)
   * @return ApiResponse&lt;Void&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<Void> patchSubmodelElementByPathMetadataWithHttpInfoNoUrlEncoding(String idShortPath, GetSubmodelElementsMetadataResult getSubmodelElementsMetadataResult, Integer limit, String cursor, String level) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = patchSubmodelElementByPathMetadataRequestBuilder(idShortPath, getSubmodelElementsMetadataResult, limit, cursor, level);
    try {
      HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(
          localVarRequestBuilder.build(),
          HttpResponse.BodyHandlers.ofInputStream());
      if (memberVarResponseInterceptor != null) {
        memberVarResponseInterceptor.accept(localVarResponse);
      }
      try {
        if (localVarResponse.statusCode()/ 100 != 2) {
          throw getApiException("patchSubmodelElementByPathMetadata", localVarResponse);
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

  private HttpRequest.Builder patchSubmodelElementByPathMetadataRequestBuilder(String idShortPath, GetSubmodelElementsMetadataResult getSubmodelElementsMetadataResult, Integer limit, String cursor, String level) throws ApiException {
    // verify the required parameter 'idShortPath' is set
    if (idShortPath == null) {
      throw new ApiException(400, "Missing the required parameter 'idShortPath' when calling patchSubmodelElementByPathMetadata");
    }
    // verify the required parameter 'getSubmodelElementsMetadataResult' is set
    if (getSubmodelElementsMetadataResult == null) {
      throw new ApiException(400, "Missing the required parameter 'getSubmodelElementsMetadataResult' when calling patchSubmodelElementByPathMetadata");
    }

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/submodel/submodel-elements/{idShortPath}/$metadata"
        .replace("{idShortPath}", ApiClient.urlEncode(idShortPath.toString()));

    List<Pair> localVarQueryParams = new ArrayList<>();
    StringJoiner localVarQueryStringJoiner = new StringJoiner("&");
    String localVarQueryParameterBaseName;
    localVarQueryParameterBaseName = "limit";
    localVarQueryParams.addAll(ApiClient.parameterToPairs("limit", limit));
    localVarQueryParameterBaseName = "cursor";
    localVarQueryParams.addAll(ApiClient.parameterToPairs("cursor", cursor));
    localVarQueryParameterBaseName = "level";
    localVarQueryParams.addAll(ApiClient.parameterToPairs("level", level));

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

    localVarRequestBuilder.header("Content-Type", "application/json");
    localVarRequestBuilder.header("Accept", "application/json");

    try {
      byte[] localVarPostBody = memberVarObjectMapper.writeValueAsBytes(getSubmodelElementsMetadataResult);
      localVarRequestBuilder.method("PATCH", HttpRequest.BodyPublishers.ofByteArray(localVarPostBody));
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
   * Updates the value of an existing SubmodelElement
   * 
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param getSubmodelElementsValueResult The SubmodelElement in its ValueOnly representation (required)
   * @param limit The maximum number of elements in the response array (optional)
   * @param cursor A server-generated identifier retrieved from pagingMetadata that specifies from which position the result listing should continue (optional)
   * @param level Determines the structural depth of the respective resource content (optional, default to core)
   * @throws ApiException if fails to make API call
   */
  public void patchSubmodelElementByPathValueOnly(String idShortPath, GetSubmodelElementsValueResult getSubmodelElementsValueResult, Integer limit, String cursor, String level) throws ApiException {

    patchSubmodelElementByPathValueOnlyWithHttpInfo(idShortPath, getSubmodelElementsValueResult, limit, cursor, level);
  }

  /**
   * Updates the value of an existing SubmodelElement
   * 
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param getSubmodelElementsValueResult The SubmodelElement in its ValueOnly representation (required)
   * @param limit The maximum number of elements in the response array (optional)
   * @param cursor A server-generated identifier retrieved from pagingMetadata that specifies from which position the result listing should continue (optional)
   * @param level Determines the structural depth of the respective resource content (optional, default to core)
   * @return ApiResponse&lt;Void&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<Void> patchSubmodelElementByPathValueOnlyWithHttpInfo(String idShortPath, GetSubmodelElementsValueResult getSubmodelElementsValueResult, Integer limit, String cursor, String level) throws ApiException {
  	return patchSubmodelElementByPathValueOnlyWithHttpInfoNoUrlEncoding(idShortPath, getSubmodelElementsValueResult, limit, cursor, level);
 	
 }


  /**
   * Updates the value of an existing SubmodelElement
   * 
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param getSubmodelElementsValueResult The SubmodelElement in its ValueOnly representation (required)
   * @param limit The maximum number of elements in the response array (optional)
   * @param cursor A server-generated identifier retrieved from pagingMetadata that specifies from which position the result listing should continue (optional)
   * @param level Determines the structural depth of the respective resource content (optional, default to core)
   * @return ApiResponse&lt;Void&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<Void> patchSubmodelElementByPathValueOnlyWithHttpInfoNoUrlEncoding(String idShortPath, GetSubmodelElementsValueResult getSubmodelElementsValueResult, Integer limit, String cursor, String level) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = patchSubmodelElementByPathValueOnlyRequestBuilder(idShortPath, getSubmodelElementsValueResult, limit, cursor, level);
    try {
      HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(
          localVarRequestBuilder.build(),
          HttpResponse.BodyHandlers.ofInputStream());
      if (memberVarResponseInterceptor != null) {
        memberVarResponseInterceptor.accept(localVarResponse);
      }
      try {
        if (localVarResponse.statusCode()/ 100 != 2) {
          throw getApiException("patchSubmodelElementByPathValueOnly", localVarResponse);
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

  private HttpRequest.Builder patchSubmodelElementByPathValueOnlyRequestBuilder(String idShortPath, GetSubmodelElementsValueResult getSubmodelElementsValueResult, Integer limit, String cursor, String level) throws ApiException {
    // verify the required parameter 'idShortPath' is set
    if (idShortPath == null) {
      throw new ApiException(400, "Missing the required parameter 'idShortPath' when calling patchSubmodelElementByPathValueOnly");
    }
    // verify the required parameter 'getSubmodelElementsValueResult' is set
    if (getSubmodelElementsValueResult == null) {
      throw new ApiException(400, "Missing the required parameter 'getSubmodelElementsValueResult' when calling patchSubmodelElementByPathValueOnly");
    }

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/submodel/submodel-elements/{idShortPath}/$value"
        .replace("{idShortPath}", ApiClient.urlEncode(idShortPath.toString()));

    List<Pair> localVarQueryParams = new ArrayList<>();
    StringJoiner localVarQueryStringJoiner = new StringJoiner("&");
    String localVarQueryParameterBaseName;
    localVarQueryParameterBaseName = "limit";
    localVarQueryParams.addAll(ApiClient.parameterToPairs("limit", limit));
    localVarQueryParameterBaseName = "cursor";
    localVarQueryParams.addAll(ApiClient.parameterToPairs("cursor", cursor));
    localVarQueryParameterBaseName = "level";
    localVarQueryParams.addAll(ApiClient.parameterToPairs("level", level));

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

    localVarRequestBuilder.header("Content-Type", "application/json");
    localVarRequestBuilder.header("Accept", "application/json");

    try {
      byte[] localVarPostBody = memberVarObjectMapper.writeValueAsBytes(getSubmodelElementsValueResult);
      localVarRequestBuilder.method("PATCH", HttpRequest.BodyPublishers.ofByteArray(localVarPostBody));
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
   * Updates the metadata attributes of the Submodel
   * 
   * @param submodelMetadata The metadata attributes of the Submodel object (required)
   * @param level Determines the structural depth of the respective resource content (optional, default to core)
   * @throws ApiException if fails to make API call
   */
  public void patchSubmodelMetadata(SubmodelMetadata submodelMetadata, String level) throws ApiException {

    patchSubmodelMetadataWithHttpInfo(submodelMetadata, level);
  }

  /**
   * Updates the metadata attributes of the Submodel
   * 
   * @param submodelMetadata The metadata attributes of the Submodel object (required)
   * @param level Determines the structural depth of the respective resource content (optional, default to core)
   * @return ApiResponse&lt;Void&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<Void> patchSubmodelMetadataWithHttpInfo(SubmodelMetadata submodelMetadata, String level) throws ApiException {
  	return patchSubmodelMetadataWithHttpInfoNoUrlEncoding(submodelMetadata, level);
 	
 }


  /**
   * Updates the metadata attributes of the Submodel
   * 
   * @param submodelMetadata The metadata attributes of the Submodel object (required)
   * @param level Determines the structural depth of the respective resource content (optional, default to core)
   * @return ApiResponse&lt;Void&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<Void> patchSubmodelMetadataWithHttpInfoNoUrlEncoding(SubmodelMetadata submodelMetadata, String level) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = patchSubmodelMetadataRequestBuilder(submodelMetadata, level);
    try {
      HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(
          localVarRequestBuilder.build(),
          HttpResponse.BodyHandlers.ofInputStream());
      if (memberVarResponseInterceptor != null) {
        memberVarResponseInterceptor.accept(localVarResponse);
      }
      try {
        if (localVarResponse.statusCode()/ 100 != 2) {
          throw getApiException("patchSubmodelMetadata", localVarResponse);
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

  private HttpRequest.Builder patchSubmodelMetadataRequestBuilder(SubmodelMetadata submodelMetadata, String level) throws ApiException {
    // verify the required parameter 'submodelMetadata' is set
    if (submodelMetadata == null) {
      throw new ApiException(400, "Missing the required parameter 'submodelMetadata' when calling patchSubmodelMetadata");
    }

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/submodel/$metadata";

    List<Pair> localVarQueryParams = new ArrayList<>();
    StringJoiner localVarQueryStringJoiner = new StringJoiner("&");
    String localVarQueryParameterBaseName;
    localVarQueryParameterBaseName = "level";
    localVarQueryParams.addAll(ApiClient.parameterToPairs("level", level));

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

    localVarRequestBuilder.header("Content-Type", "application/json");
    localVarRequestBuilder.header("Accept", "application/json");

    try {
      byte[] localVarPostBody = memberVarObjectMapper.writeValueAsBytes(submodelMetadata);
      localVarRequestBuilder.method("PATCH", HttpRequest.BodyPublishers.ofByteArray(localVarPostBody));
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
   * Updates the values of the Submodel
   * 
   * @param submodelValue Submodel object in its ValueOnly representation (required)
   * @param level Determines the structural depth of the respective resource content (optional, default to core)
   * @throws ApiException if fails to make API call
   */
  public void patchSubmodelValueOnly(SubmodelValue submodelValue, String level) throws ApiException {

    patchSubmodelValueOnlyWithHttpInfo(submodelValue, level);
  }

  /**
   * Updates the values of the Submodel
   * 
   * @param submodelValue Submodel object in its ValueOnly representation (required)
   * @param level Determines the structural depth of the respective resource content (optional, default to core)
   * @return ApiResponse&lt;Void&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<Void> patchSubmodelValueOnlyWithHttpInfo(SubmodelValue submodelValue, String level) throws ApiException {
  	return patchSubmodelValueOnlyWithHttpInfoNoUrlEncoding(submodelValue, level);
 	
 }


  /**
   * Updates the values of the Submodel
   * 
   * @param submodelValue Submodel object in its ValueOnly representation (required)
   * @param level Determines the structural depth of the respective resource content (optional, default to core)
   * @return ApiResponse&lt;Void&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<Void> patchSubmodelValueOnlyWithHttpInfoNoUrlEncoding(SubmodelValue submodelValue, String level) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = patchSubmodelValueOnlyRequestBuilder(submodelValue, level);
    try {
      HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(
          localVarRequestBuilder.build(),
          HttpResponse.BodyHandlers.ofInputStream());
      if (memberVarResponseInterceptor != null) {
        memberVarResponseInterceptor.accept(localVarResponse);
      }
      try {
        if (localVarResponse.statusCode()/ 100 != 2) {
          throw getApiException("patchSubmodelValueOnly", localVarResponse);
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

  private HttpRequest.Builder patchSubmodelValueOnlyRequestBuilder(SubmodelValue submodelValue, String level) throws ApiException {
    // verify the required parameter 'submodelValue' is set
    if (submodelValue == null) {
      throw new ApiException(400, "Missing the required parameter 'submodelValue' when calling patchSubmodelValueOnly");
    }

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/submodel/$value";

    List<Pair> localVarQueryParams = new ArrayList<>();
    StringJoiner localVarQueryStringJoiner = new StringJoiner("&");
    String localVarQueryParameterBaseName;
    localVarQueryParameterBaseName = "level";
    localVarQueryParams.addAll(ApiClient.parameterToPairs("level", level));

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

    localVarRequestBuilder.header("Content-Type", "application/json");
    localVarRequestBuilder.header("Accept", "application/json");

    try {
      byte[] localVarPostBody = memberVarObjectMapper.writeValueAsBytes(submodelValue);
      localVarRequestBuilder.method("PATCH", HttpRequest.BodyPublishers.ofByteArray(localVarPostBody));
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
   * Creates a new submodel element
   * 
   * @param submodelElement Requested submodel element (required)
   * @return SubmodelElement
   * @throws ApiException if fails to make API call
   */
  public SubmodelElement postSubmodelElement(SubmodelElement submodelElement) throws ApiException {

    ApiResponse<SubmodelElement> localVarResponse = postSubmodelElementWithHttpInfo(submodelElement);
    return localVarResponse.getData();
  }

  /**
   * Creates a new submodel element
   * 
   * @param submodelElement Requested submodel element (required)
   * @return ApiResponse&lt;SubmodelElement&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<SubmodelElement> postSubmodelElementWithHttpInfo(SubmodelElement submodelElement) throws ApiException {
  	return postSubmodelElementWithHttpInfoNoUrlEncoding(submodelElement);
 	
 }


  /**
   * Creates a new submodel element
   * 
   * @param submodelElement Requested submodel element (required)
   * @return ApiResponse&lt;SubmodelElement&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<SubmodelElement> postSubmodelElementWithHttpInfoNoUrlEncoding(SubmodelElement submodelElement) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = postSubmodelElementRequestBuilder(submodelElement);
    try {
      HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(
          localVarRequestBuilder.build(),
          HttpResponse.BodyHandlers.ofInputStream());
      if (memberVarResponseInterceptor != null) {
        memberVarResponseInterceptor.accept(localVarResponse);
      }
      try {
        if (localVarResponse.statusCode()/ 100 != 2) {
          throw getApiException("postSubmodelElement", localVarResponse);
        }
        return new ApiResponse<SubmodelElement>(
          localVarResponse.statusCode(),
          localVarResponse.headers().map(),
          localVarResponse.body() == null ? null : memberVarObjectMapper.readValue(localVarResponse.body(), new TypeReference<SubmodelElement>() {}) // closes the InputStream
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

  private HttpRequest.Builder postSubmodelElementRequestBuilder(SubmodelElement submodelElement) throws ApiException {
    // verify the required parameter 'submodelElement' is set
    if (submodelElement == null) {
      throw new ApiException(400, "Missing the required parameter 'submodelElement' when calling postSubmodelElement");
    }

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/submodel/submodel-elements";

    localVarRequestBuilder.uri(URI.create(memberVarBaseUri + localVarPath));

    localVarRequestBuilder.header("Content-Type", "application/json");
    localVarRequestBuilder.header("Accept", "application/json");

    try {
      byte[] localVarPostBody = memberVarObjectMapper.writeValueAsBytes(submodelElement);
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
   * Creates a new submodel element at a specified path within submodel elements hierarchy
   * 
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param submodelElement Requested submodel element (required)
   * @return SubmodelElement
   * @throws ApiException if fails to make API call
   */
  public SubmodelElement postSubmodelElementByPath(String idShortPath, SubmodelElement submodelElement) throws ApiException {

    ApiResponse<SubmodelElement> localVarResponse = postSubmodelElementByPathWithHttpInfo(idShortPath, submodelElement);
    return localVarResponse.getData();
  }

  /**
   * Creates a new submodel element at a specified path within submodel elements hierarchy
   * 
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param submodelElement Requested submodel element (required)
   * @return ApiResponse&lt;SubmodelElement&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<SubmodelElement> postSubmodelElementByPathWithHttpInfo(String idShortPath, SubmodelElement submodelElement) throws ApiException {
  	return postSubmodelElementByPathWithHttpInfoNoUrlEncoding(idShortPath, submodelElement);
 	
 }


  /**
   * Creates a new submodel element at a specified path within submodel elements hierarchy
   * 
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param submodelElement Requested submodel element (required)
   * @return ApiResponse&lt;SubmodelElement&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<SubmodelElement> postSubmodelElementByPathWithHttpInfoNoUrlEncoding(String idShortPath, SubmodelElement submodelElement) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = postSubmodelElementByPathRequestBuilder(idShortPath, submodelElement);
    try {
      HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(
          localVarRequestBuilder.build(),
          HttpResponse.BodyHandlers.ofInputStream());
      if (memberVarResponseInterceptor != null) {
        memberVarResponseInterceptor.accept(localVarResponse);
      }
      try {
        if (localVarResponse.statusCode()/ 100 != 2) {
          throw getApiException("postSubmodelElementByPath", localVarResponse);
        }
        return new ApiResponse<SubmodelElement>(
          localVarResponse.statusCode(),
          localVarResponse.headers().map(),
          localVarResponse.body() == null ? null : memberVarObjectMapper.readValue(localVarResponse.body(), new TypeReference<SubmodelElement>() {}) // closes the InputStream
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

  private HttpRequest.Builder postSubmodelElementByPathRequestBuilder(String idShortPath, SubmodelElement submodelElement) throws ApiException {
    // verify the required parameter 'idShortPath' is set
    if (idShortPath == null) {
      throw new ApiException(400, "Missing the required parameter 'idShortPath' when calling postSubmodelElementByPath");
    }
    // verify the required parameter 'submodelElement' is set
    if (submodelElement == null) {
      throw new ApiException(400, "Missing the required parameter 'submodelElement' when calling postSubmodelElementByPath");
    }

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/submodel/submodel-elements/{idShortPath}"
        .replace("{idShortPath}", ApiClient.urlEncode(idShortPath.toString()));

    localVarRequestBuilder.uri(URI.create(memberVarBaseUri + localVarPath));

    localVarRequestBuilder.header("Content-Type", "application/json");
    localVarRequestBuilder.header("Accept", "application/json");

    try {
      byte[] localVarPostBody = memberVarObjectMapper.writeValueAsBytes(submodelElement);
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
   * Uploads file content to an existing submodel element at a specified path within submodel elements hierarchy
   * 
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param fileName  (optional)
   * @param _file  (optional)
   * @throws ApiException if fails to make API call
   */
  public void putFileByPath(String idShortPath, String fileName, File _file) throws ApiException {

    putFileByPathWithHttpInfo(idShortPath, fileName, _file);
  }

  /**
   * Uploads file content to an existing submodel element at a specified path within submodel elements hierarchy
   * 
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param fileName  (optional)
   * @param _file  (optional)
   * @return ApiResponse&lt;Void&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<Void> putFileByPathWithHttpInfo(String idShortPath, String fileName, File _file) throws ApiException {
  	return putFileByPathWithHttpInfoNoUrlEncoding(idShortPath, fileName, _file);
 	
 }


  /**
   * Uploads file content to an existing submodel element at a specified path within submodel elements hierarchy
   * 
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param fileName  (optional)
   * @param _file  (optional)
   * @return ApiResponse&lt;Void&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<Void> putFileByPathWithHttpInfoNoUrlEncoding(String idShortPath, String fileName, File _file) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = putFileByPathRequestBuilder(idShortPath, fileName, _file);
    try {
      HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(
          localVarRequestBuilder.build(),
          HttpResponse.BodyHandlers.ofInputStream());
      if (memberVarResponseInterceptor != null) {
        memberVarResponseInterceptor.accept(localVarResponse);
      }
      try {
        if (localVarResponse.statusCode()/ 100 != 2) {
          throw getApiException("putFileByPath", localVarResponse);
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

  private HttpRequest.Builder putFileByPathRequestBuilder(String idShortPath, String fileName, File _file) throws ApiException {
    // verify the required parameter 'idShortPath' is set
    if (idShortPath == null) {
      throw new ApiException(400, "Missing the required parameter 'idShortPath' when calling putFileByPath");
    }

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/submodel/submodel-elements/{idShortPath}/attachment"
        .replace("{idShortPath}", ApiClient.urlEncode(idShortPath.toString()));

    localVarRequestBuilder.uri(URI.create(memberVarBaseUri + localVarPath));

    localVarRequestBuilder.header("Accept", "application/json");

    MultipartEntityBuilder multiPartBuilder = MultipartEntityBuilder.create();
    boolean hasFiles = false;
    multiPartBuilder.addTextBody("fileName", fileName.toString());
    multiPartBuilder.addBinaryBody("file", _file);
    hasFiles = true;
    HttpEntity entity = multiPartBuilder.build();
    HttpRequest.BodyPublisher formDataPublisher;
    if (hasFiles) {
        Pipe pipe;
        try {
            pipe = Pipe.open();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        new Thread(() -> {
            try (OutputStream outputStream = Channels.newOutputStream(pipe.sink())) {
                entity.writeTo(outputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
        formDataPublisher = HttpRequest.BodyPublishers.ofInputStream(() -> Channels.newInputStream(pipe.source()));
    } else {
        ByteArrayOutputStream formOutputStream = new ByteArrayOutputStream();
        try {
            entity.writeTo(formOutputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        formDataPublisher = HttpRequest.BodyPublishers
            .ofInputStream(() -> new ByteArrayInputStream(formOutputStream.toByteArray()));
    }
    localVarRequestBuilder
        .header("Content-Type", entity.getContentType().getValue())
        .method("PUT", formDataPublisher);
    if (memberVarReadTimeout != null) {
      localVarRequestBuilder.timeout(memberVarReadTimeout);
    }
    if (memberVarInterceptor != null) {
      memberVarInterceptor.accept(localVarRequestBuilder);
    }
    return localVarRequestBuilder;
  }
  /**
   * Updates the Submodel
   * 
   * @param submodel Submodel object (required)
   * @param level Determines the structural depth of the respective resource content (optional, default to deep)
   * @throws ApiException if fails to make API call
   */
  public void putSubmodel(Submodel submodel, String level) throws ApiException {

    putSubmodelWithHttpInfo(submodel, level);
  }

  /**
   * Updates the Submodel
   * 
   * @param submodel Submodel object (required)
   * @param level Determines the structural depth of the respective resource content (optional, default to deep)
   * @return ApiResponse&lt;Void&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<Void> putSubmodelWithHttpInfo(Submodel submodel, String level) throws ApiException {
  	return putSubmodelWithHttpInfoNoUrlEncoding(submodel, level);
 	
 }


  /**
   * Updates the Submodel
   * 
   * @param submodel Submodel object (required)
   * @param level Determines the structural depth of the respective resource content (optional, default to deep)
   * @return ApiResponse&lt;Void&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<Void> putSubmodelWithHttpInfoNoUrlEncoding(Submodel submodel, String level) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = putSubmodelRequestBuilder(submodel, level);
    try {
      HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(
          localVarRequestBuilder.build(),
          HttpResponse.BodyHandlers.ofInputStream());
      if (memberVarResponseInterceptor != null) {
        memberVarResponseInterceptor.accept(localVarResponse);
      }
      try {
        if (localVarResponse.statusCode()/ 100 != 2) {
          throw getApiException("putSubmodel", localVarResponse);
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

  private HttpRequest.Builder putSubmodelRequestBuilder(Submodel submodel, String level) throws ApiException {
    // verify the required parameter 'submodel' is set
    if (submodel == null) {
      throw new ApiException(400, "Missing the required parameter 'submodel' when calling putSubmodel");
    }

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/submodel";

    List<Pair> localVarQueryParams = new ArrayList<>();
    StringJoiner localVarQueryStringJoiner = new StringJoiner("&");
    String localVarQueryParameterBaseName;
    localVarQueryParameterBaseName = "level";
    localVarQueryParams.addAll(ApiClient.parameterToPairs("level", level));

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

    localVarRequestBuilder.header("Content-Type", "application/json");
    localVarRequestBuilder.header("Accept", "application/json");

    try {
      byte[] localVarPostBody = memberVarObjectMapper.writeValueAsBytes(submodel);
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
   * Updates an existing submodel element at a specified path within submodel elements hierarchy
   * 
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param submodelElement Requested submodel element (required)
   * @param level Determines the structural depth of the respective resource content (optional, default to deep)
   * @throws ApiException if fails to make API call
   */
  public void putSubmodelElementByPath(String idShortPath, SubmodelElement submodelElement, String level) throws ApiException {

    putSubmodelElementByPathWithHttpInfo(idShortPath, submodelElement, level);
  }

  /**
   * Updates an existing submodel element at a specified path within submodel elements hierarchy
   * 
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param submodelElement Requested submodel element (required)
   * @param level Determines the structural depth of the respective resource content (optional, default to deep)
   * @return ApiResponse&lt;Void&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<Void> putSubmodelElementByPathWithHttpInfo(String idShortPath, SubmodelElement submodelElement, String level) throws ApiException {
  	return putSubmodelElementByPathWithHttpInfoNoUrlEncoding(idShortPath, submodelElement, level);
 	
 }


  /**
   * Updates an existing submodel element at a specified path within submodel elements hierarchy
   * 
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param submodelElement Requested submodel element (required)
   * @param level Determines the structural depth of the respective resource content (optional, default to deep)
   * @return ApiResponse&lt;Void&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<Void> putSubmodelElementByPathWithHttpInfoNoUrlEncoding(String idShortPath, SubmodelElement submodelElement, String level) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = putSubmodelElementByPathRequestBuilder(idShortPath, submodelElement, level);
    try {
      HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(
          localVarRequestBuilder.build(),
          HttpResponse.BodyHandlers.ofInputStream());
      if (memberVarResponseInterceptor != null) {
        memberVarResponseInterceptor.accept(localVarResponse);
      }
      try {
        if (localVarResponse.statusCode()/ 100 != 2) {
          throw getApiException("putSubmodelElementByPath", localVarResponse);
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

  private HttpRequest.Builder putSubmodelElementByPathRequestBuilder(String idShortPath, SubmodelElement submodelElement, String level) throws ApiException {
    // verify the required parameter 'idShortPath' is set
    if (idShortPath == null) {
      throw new ApiException(400, "Missing the required parameter 'idShortPath' when calling putSubmodelElementByPath");
    }
    // verify the required parameter 'submodelElement' is set
    if (submodelElement == null) {
      throw new ApiException(400, "Missing the required parameter 'submodelElement' when calling putSubmodelElementByPath");
    }

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/submodel/submodel-elements/{idShortPath}"
        .replace("{idShortPath}", ApiClient.urlEncode(idShortPath.toString()));

    List<Pair> localVarQueryParams = new ArrayList<>();
    StringJoiner localVarQueryStringJoiner = new StringJoiner("&");
    String localVarQueryParameterBaseName;
    localVarQueryParameterBaseName = "level";
    localVarQueryParams.addAll(ApiClient.parameterToPairs("level", level));

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

    localVarRequestBuilder.header("Content-Type", "application/json");
    localVarRequestBuilder.header("Accept", "application/json");

    try {
      byte[] localVarPostBody = memberVarObjectMapper.writeValueAsBytes(submodelElement);
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
  
}

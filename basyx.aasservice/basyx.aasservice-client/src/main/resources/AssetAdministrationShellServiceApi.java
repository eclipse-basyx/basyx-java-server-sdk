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

import org.eclipse.digitaltwin.basyx.v3.clients.model.part1.AssetAdministrationShell;
import org.eclipse.digitaltwin.basyx.v3.clients.model.part1.AssetInformation;
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

@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen", date = "2024-02-08T19:43:45.125130100+01:00[Europe/Berlin]")
public class AssetAdministrationShellServiceApi {
  private final HttpClient memberVarHttpClient;
  private final ObjectMapper memberVarObjectMapper;
  private final String memberVarBaseUri;
  private final Consumer<HttpRequest.Builder> memberVarInterceptor;
  private final Duration memberVarReadTimeout;
  private final Consumer<HttpResponse<InputStream>> memberVarResponseInterceptor;
  private final Consumer<HttpResponse<String>> memberVarAsyncResponseInterceptor;

  public AssetAdministrationShellServiceApi() {
    this(new ApiClient());
  }

  public AssetAdministrationShellServiceApi(ObjectMapper mapper, String baseUri) {
    this(new ApiClient(HttpClient.newBuilder(), mapper, baseUri));
  }
  
  public AssetAdministrationShellServiceApi(String baseUri) {
    this(new ApiClient(HttpClient.newBuilder(), JSON.getDefault().getMapper(), baseUri));
  }


  public AssetAdministrationShellServiceApi(ApiClient apiClient) {
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
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @throws ApiException if fails to make API call
   */
  public void deleteFileByPath(String submodelIdentifier, String idShortPath) throws ApiException {

    deleteFileByPathWithHttpInfo(submodelIdentifier, idShortPath);
  }

  /**
   * Deletes file content of an existing submodel element at a specified path within submodel elements hierarchy
   * 
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @return ApiResponse&lt;Void&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<Void> deleteFileByPathWithHttpInfo(String submodelIdentifier, String idShortPath) throws ApiException {
    String  submodelIdentifierAsBytes = ApiClient.base64UrlEncode(submodelIdentifier);
  	return deleteFileByPathWithHttpInfoNoUrlEncoding(submodelIdentifierAsBytes, idShortPath);
 	
 }


  /**
   * Deletes file content of an existing submodel element at a specified path within submodel elements hierarchy
   * 
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @return ApiResponse&lt;Void&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<Void> deleteFileByPathWithHttpInfoNoUrlEncoding(String submodelIdentifier, String idShortPath) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = deleteFileByPathRequestBuilder(submodelIdentifier, idShortPath);
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

  private HttpRequest.Builder deleteFileByPathRequestBuilder(String submodelIdentifier, String idShortPath) throws ApiException {
    // verify the required parameter 'submodelIdentifier' is set
    if (submodelIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'submodelIdentifier' when calling deleteFileByPath");
    }
    // verify the required parameter 'idShortPath' is set
    if (idShortPath == null) {
      throw new ApiException(400, "Missing the required parameter 'idShortPath' when calling deleteFileByPath");
    }

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/submodels/{submodelIdentifier}/submodel-elements/{idShortPath}/attachment"
        .replace("{submodelIdentifier}", ApiClient.urlEncode(submodelIdentifier.toString()))
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
   * Deletes the submodel from the Asset Administration Shell.
   * 
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @throws ApiException if fails to make API call
   */
  public void deleteSubmodelById(String submodelIdentifier) throws ApiException {

    deleteSubmodelByIdWithHttpInfo(submodelIdentifier);
  }

  /**
   * Deletes the submodel from the Asset Administration Shell.
   * 
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @return ApiResponse&lt;Void&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<Void> deleteSubmodelByIdWithHttpInfo(String submodelIdentifier) throws ApiException {
    String  submodelIdentifierAsBytes = ApiClient.base64UrlEncode(submodelIdentifier);
  	return deleteSubmodelByIdWithHttpInfoNoUrlEncoding(submodelIdentifierAsBytes);
 	
 }


  /**
   * Deletes the submodel from the Asset Administration Shell.
   * 
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @return ApiResponse&lt;Void&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<Void> deleteSubmodelByIdWithHttpInfoNoUrlEncoding(String submodelIdentifier) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = deleteSubmodelByIdRequestBuilder(submodelIdentifier);
    try {
      HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(
          localVarRequestBuilder.build(),
          HttpResponse.BodyHandlers.ofInputStream());
      if (memberVarResponseInterceptor != null) {
        memberVarResponseInterceptor.accept(localVarResponse);
      }
      try {
        if (localVarResponse.statusCode()/ 100 != 2) {
          throw getApiException("deleteSubmodelById", localVarResponse);
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

  private HttpRequest.Builder deleteSubmodelByIdRequestBuilder(String submodelIdentifier) throws ApiException {
    // verify the required parameter 'submodelIdentifier' is set
    if (submodelIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'submodelIdentifier' when calling deleteSubmodelById");
    }

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/submodels/{submodelIdentifier}"
        .replace("{submodelIdentifier}", ApiClient.urlEncode(submodelIdentifier.toString()));

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
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @throws ApiException if fails to make API call
   */
  public void deleteSubmodelElementByPath(String submodelIdentifier, String idShortPath) throws ApiException {

    deleteSubmodelElementByPathWithHttpInfo(submodelIdentifier, idShortPath);
  }

  /**
   * Deletes a submodel element at a specified path within the submodel elements hierarchy
   * 
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @return ApiResponse&lt;Void&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<Void> deleteSubmodelElementByPathWithHttpInfo(String submodelIdentifier, String idShortPath) throws ApiException {
    String  submodelIdentifierAsBytes = ApiClient.base64UrlEncode(submodelIdentifier);
  	return deleteSubmodelElementByPathWithHttpInfoNoUrlEncoding(submodelIdentifierAsBytes, idShortPath);
 	
 }


  /**
   * Deletes a submodel element at a specified path within the submodel elements hierarchy
   * 
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @return ApiResponse&lt;Void&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<Void> deleteSubmodelElementByPathWithHttpInfoNoUrlEncoding(String submodelIdentifier, String idShortPath) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = deleteSubmodelElementByPathRequestBuilder(submodelIdentifier, idShortPath);
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

  private HttpRequest.Builder deleteSubmodelElementByPathRequestBuilder(String submodelIdentifier, String idShortPath) throws ApiException {
    // verify the required parameter 'submodelIdentifier' is set
    if (submodelIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'submodelIdentifier' when calling deleteSubmodelElementByPath");
    }
    // verify the required parameter 'idShortPath' is set
    if (idShortPath == null) {
      throw new ApiException(400, "Missing the required parameter 'idShortPath' when calling deleteSubmodelElementByPath");
    }

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/submodels/{submodelIdentifier}/submodel-elements/{idShortPath}"
        .replace("{submodelIdentifier}", ApiClient.urlEncode(submodelIdentifier.toString()))
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
   * Deletes the submodel reference from the Asset Administration Shell. Does not delete the submodel itself!
   * 
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @throws ApiException if fails to make API call
   */
  public void deleteSubmodelReferenceById(String submodelIdentifier) throws ApiException {

    deleteSubmodelReferenceByIdWithHttpInfo(submodelIdentifier);
  }

  /**
   * Deletes the submodel reference from the Asset Administration Shell. Does not delete the submodel itself!
   * 
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @return ApiResponse&lt;Void&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<Void> deleteSubmodelReferenceByIdWithHttpInfo(String submodelIdentifier) throws ApiException {
    String  submodelIdentifierAsBytes = ApiClient.base64UrlEncode(submodelIdentifier);
  	return deleteSubmodelReferenceByIdWithHttpInfoNoUrlEncoding(submodelIdentifierAsBytes);
 	
 }


  /**
   * Deletes the submodel reference from the Asset Administration Shell. Does not delete the submodel itself!
   * 
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @return ApiResponse&lt;Void&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<Void> deleteSubmodelReferenceByIdWithHttpInfoNoUrlEncoding(String submodelIdentifier) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = deleteSubmodelReferenceByIdRequestBuilder(submodelIdentifier);
    try {
      HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(
          localVarRequestBuilder.build(),
          HttpResponse.BodyHandlers.ofInputStream());
      if (memberVarResponseInterceptor != null) {
        memberVarResponseInterceptor.accept(localVarResponse);
      }
      try {
        if (localVarResponse.statusCode()/ 100 != 2) {
          throw getApiException("deleteSubmodelReferenceById", localVarResponse);
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

  private HttpRequest.Builder deleteSubmodelReferenceByIdRequestBuilder(String submodelIdentifier) throws ApiException {
    // verify the required parameter 'submodelIdentifier' is set
    if (submodelIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'submodelIdentifier' when calling deleteSubmodelReferenceById");
    }

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/submodel-refs/{submodelIdentifier}"
        .replace("{submodelIdentifier}", ApiClient.urlEncode(submodelIdentifier.toString()));

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
   * 
   * 
   * @throws ApiException if fails to make API call
   */
  public void deleteThumbnail() throws ApiException {

    deleteThumbnailWithHttpInfo();
  }

  /**
   * 
   * 
   * @return ApiResponse&lt;Void&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<Void> deleteThumbnailWithHttpInfo() throws ApiException {
  	return deleteThumbnailWithHttpInfoNoUrlEncoding();
 	
 }


  /**
   * 
   * 
   * @return ApiResponse&lt;Void&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<Void> deleteThumbnailWithHttpInfoNoUrlEncoding() throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = deleteThumbnailRequestBuilder();
    try {
      HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(
          localVarRequestBuilder.build(),
          HttpResponse.BodyHandlers.ofInputStream());
      if (memberVarResponseInterceptor != null) {
        memberVarResponseInterceptor.accept(localVarResponse);
      }
      try {
        if (localVarResponse.statusCode()/ 100 != 2) {
          throw getApiException("deleteThumbnail", localVarResponse);
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

  private HttpRequest.Builder deleteThumbnailRequestBuilder() throws ApiException {

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/assetinformation/thumbnail";

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
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param limit The maximum number of elements in the response array (optional)
   * @param cursor A server-generated identifier retrieved from pagingMetadata that specifies from which position the result listing should continue (optional)
   * @param level Determines the structural depth of the respective resource content (optional, default to deep)
   * @param extent Determines to which extent the resource is being serialized (optional, default to withoutBlobValue)
   * @return GetSubmodelElementsResult
   * @throws ApiException if fails to make API call
   */
  public GetSubmodelElementsResult getAllSubmodelElements(String submodelIdentifier, Integer limit, String cursor, String level, String extent) throws ApiException {

    ApiResponse<GetSubmodelElementsResult> localVarResponse = getAllSubmodelElementsWithHttpInfo(submodelIdentifier, limit, cursor, level, extent);
    return localVarResponse.getData();
  }

  /**
   * Returns all submodel elements including their hierarchy
   * 
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param limit The maximum number of elements in the response array (optional)
   * @param cursor A server-generated identifier retrieved from pagingMetadata that specifies from which position the result listing should continue (optional)
   * @param level Determines the structural depth of the respective resource content (optional, default to deep)
   * @param extent Determines to which extent the resource is being serialized (optional, default to withoutBlobValue)
   * @return ApiResponse&lt;GetSubmodelElementsResult&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<GetSubmodelElementsResult> getAllSubmodelElementsWithHttpInfo(String submodelIdentifier, Integer limit, String cursor, String level, String extent) throws ApiException {
    String  submodelIdentifierAsBytes = ApiClient.base64UrlEncode(submodelIdentifier);
  	return getAllSubmodelElementsWithHttpInfoNoUrlEncoding(submodelIdentifierAsBytes, limit, cursor, level, extent);
 	
 }


  /**
   * Returns all submodel elements including their hierarchy
   * 
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param limit The maximum number of elements in the response array (optional)
   * @param cursor A server-generated identifier retrieved from pagingMetadata that specifies from which position the result listing should continue (optional)
   * @param level Determines the structural depth of the respective resource content (optional, default to deep)
   * @param extent Determines to which extent the resource is being serialized (optional, default to withoutBlobValue)
   * @return ApiResponse&lt;GetSubmodelElementsResult&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<GetSubmodelElementsResult> getAllSubmodelElementsWithHttpInfoNoUrlEncoding(String submodelIdentifier, Integer limit, String cursor, String level, String extent) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = getAllSubmodelElementsRequestBuilder(submodelIdentifier, limit, cursor, level, extent);
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

  private HttpRequest.Builder getAllSubmodelElementsRequestBuilder(String submodelIdentifier, Integer limit, String cursor, String level, String extent) throws ApiException {
    // verify the required parameter 'submodelIdentifier' is set
    if (submodelIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'submodelIdentifier' when calling getAllSubmodelElements");
    }

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/submodels/{submodelIdentifier}/submodel-elements"
        .replace("{submodelIdentifier}", ApiClient.urlEncode(submodelIdentifier.toString()));

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
   * Returns all submodel elements including their hierarchy
   * 
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param limit The maximum number of elements in the response array (optional)
   * @param cursor A server-generated identifier retrieved from pagingMetadata that specifies from which position the result listing should continue (optional)
   * @param level Determines the structural depth of the respective resource content (optional, default to deep)
   * @return GetSubmodelElementsMetadataResult
   * @throws ApiException if fails to make API call
   */
  public GetSubmodelElementsMetadataResult getAllSubmodelElementsMetadata(String submodelIdentifier, Integer limit, String cursor, String level) throws ApiException {

    ApiResponse<GetSubmodelElementsMetadataResult> localVarResponse = getAllSubmodelElementsMetadataWithHttpInfo(submodelIdentifier, limit, cursor, level);
    return localVarResponse.getData();
  }

  /**
   * Returns all submodel elements including their hierarchy
   * 
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param limit The maximum number of elements in the response array (optional)
   * @param cursor A server-generated identifier retrieved from pagingMetadata that specifies from which position the result listing should continue (optional)
   * @param level Determines the structural depth of the respective resource content (optional, default to deep)
   * @return ApiResponse&lt;GetSubmodelElementsMetadataResult&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<GetSubmodelElementsMetadataResult> getAllSubmodelElementsMetadataWithHttpInfo(String submodelIdentifier, Integer limit, String cursor, String level) throws ApiException {
    String  submodelIdentifierAsBytes = ApiClient.base64UrlEncode(submodelIdentifier);
  	return getAllSubmodelElementsMetadataWithHttpInfoNoUrlEncoding(submodelIdentifierAsBytes, limit, cursor, level);
 	
 }


  /**
   * Returns all submodel elements including their hierarchy
   * 
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param limit The maximum number of elements in the response array (optional)
   * @param cursor A server-generated identifier retrieved from pagingMetadata that specifies from which position the result listing should continue (optional)
   * @param level Determines the structural depth of the respective resource content (optional, default to deep)
   * @return ApiResponse&lt;GetSubmodelElementsMetadataResult&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<GetSubmodelElementsMetadataResult> getAllSubmodelElementsMetadataWithHttpInfoNoUrlEncoding(String submodelIdentifier, Integer limit, String cursor, String level) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = getAllSubmodelElementsMetadataRequestBuilder(submodelIdentifier, limit, cursor, level);
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

  private HttpRequest.Builder getAllSubmodelElementsMetadataRequestBuilder(String submodelIdentifier, Integer limit, String cursor, String level) throws ApiException {
    // verify the required parameter 'submodelIdentifier' is set
    if (submodelIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'submodelIdentifier' when calling getAllSubmodelElementsMetadata");
    }

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/submodels/{submodelIdentifier}/submodel-elements/$metadata"
        .replace("{submodelIdentifier}", ApiClient.urlEncode(submodelIdentifier.toString()));

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
   * Returns all submodel elements including their hierarchy
   * 
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param limit The maximum number of elements in the response array (optional)
   * @param cursor A server-generated identifier retrieved from pagingMetadata that specifies from which position the result listing should continue (optional)
   * @param level Determines the structural depth of the respective resource content (optional, default to deep)
   * @return GetPathItemsResult
   * @throws ApiException if fails to make API call
   */
  public GetPathItemsResult getAllSubmodelElementsPath(String submodelIdentifier, Integer limit, String cursor, String level) throws ApiException {

    ApiResponse<GetPathItemsResult> localVarResponse = getAllSubmodelElementsPathWithHttpInfo(submodelIdentifier, limit, cursor, level);
    return localVarResponse.getData();
  }

  /**
   * Returns all submodel elements including their hierarchy
   * 
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param limit The maximum number of elements in the response array (optional)
   * @param cursor A server-generated identifier retrieved from pagingMetadata that specifies from which position the result listing should continue (optional)
   * @param level Determines the structural depth of the respective resource content (optional, default to deep)
   * @return ApiResponse&lt;GetPathItemsResult&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<GetPathItemsResult> getAllSubmodelElementsPathWithHttpInfo(String submodelIdentifier, Integer limit, String cursor, String level) throws ApiException {
    String  submodelIdentifierAsBytes = ApiClient.base64UrlEncode(submodelIdentifier);
  	return getAllSubmodelElementsPathWithHttpInfoNoUrlEncoding(submodelIdentifierAsBytes, limit, cursor, level);
 	
 }


  /**
   * Returns all submodel elements including their hierarchy
   * 
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param limit The maximum number of elements in the response array (optional)
   * @param cursor A server-generated identifier retrieved from pagingMetadata that specifies from which position the result listing should continue (optional)
   * @param level Determines the structural depth of the respective resource content (optional, default to deep)
   * @return ApiResponse&lt;GetPathItemsResult&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<GetPathItemsResult> getAllSubmodelElementsPathWithHttpInfoNoUrlEncoding(String submodelIdentifier, Integer limit, String cursor, String level) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = getAllSubmodelElementsPathRequestBuilder(submodelIdentifier, limit, cursor, level);
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

  private HttpRequest.Builder getAllSubmodelElementsPathRequestBuilder(String submodelIdentifier, Integer limit, String cursor, String level) throws ApiException {
    // verify the required parameter 'submodelIdentifier' is set
    if (submodelIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'submodelIdentifier' when calling getAllSubmodelElementsPath");
    }

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/submodels/{submodelIdentifier}/submodel-elements/$path"
        .replace("{submodelIdentifier}", ApiClient.urlEncode(submodelIdentifier.toString()));

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
   * Returns all submodel elements as a list of References
   * 
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param limit The maximum number of elements in the response array (optional)
   * @param cursor A server-generated identifier retrieved from pagingMetadata that specifies from which position the result listing should continue (optional)
   * @param level Determines the structural depth of the respective resource content (optional, default to core)
   * @return GetReferencesResult
   * @throws ApiException if fails to make API call
   */
  public GetReferencesResult getAllSubmodelElementsReference(String submodelIdentifier, Integer limit, String cursor, String level) throws ApiException {

    ApiResponse<GetReferencesResult> localVarResponse = getAllSubmodelElementsReferenceWithHttpInfo(submodelIdentifier, limit, cursor, level);
    return localVarResponse.getData();
  }

  /**
   * Returns all submodel elements as a list of References
   * 
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param limit The maximum number of elements in the response array (optional)
   * @param cursor A server-generated identifier retrieved from pagingMetadata that specifies from which position the result listing should continue (optional)
   * @param level Determines the structural depth of the respective resource content (optional, default to core)
   * @return ApiResponse&lt;GetReferencesResult&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<GetReferencesResult> getAllSubmodelElementsReferenceWithHttpInfo(String submodelIdentifier, Integer limit, String cursor, String level) throws ApiException {
    String  submodelIdentifierAsBytes = ApiClient.base64UrlEncode(submodelIdentifier);
  	return getAllSubmodelElementsReferenceWithHttpInfoNoUrlEncoding(submodelIdentifierAsBytes, limit, cursor, level);
 	
 }


  /**
   * Returns all submodel elements as a list of References
   * 
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param limit The maximum number of elements in the response array (optional)
   * @param cursor A server-generated identifier retrieved from pagingMetadata that specifies from which position the result listing should continue (optional)
   * @param level Determines the structural depth of the respective resource content (optional, default to core)
   * @return ApiResponse&lt;GetReferencesResult&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<GetReferencesResult> getAllSubmodelElementsReferenceWithHttpInfoNoUrlEncoding(String submodelIdentifier, Integer limit, String cursor, String level) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = getAllSubmodelElementsReferenceRequestBuilder(submodelIdentifier, limit, cursor, level);
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

  private HttpRequest.Builder getAllSubmodelElementsReferenceRequestBuilder(String submodelIdentifier, Integer limit, String cursor, String level) throws ApiException {
    // verify the required parameter 'submodelIdentifier' is set
    if (submodelIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'submodelIdentifier' when calling getAllSubmodelElementsReference");
    }

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/submodels/{submodelIdentifier}/submodel-elements/$reference"
        .replace("{submodelIdentifier}", ApiClient.urlEncode(submodelIdentifier.toString()));

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
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param limit The maximum number of elements in the response array (optional)
   * @param cursor A server-generated identifier retrieved from pagingMetadata that specifies from which position the result listing should continue (optional)
   * @param level Determines the structural depth of the respective resource content (optional, default to deep)
   * @param extent Determines to which extent the resource is being serialized (optional, default to withoutBlobValue)
   * @return GetSubmodelElementsValueResult
   * @throws ApiException if fails to make API call
   */
  public GetSubmodelElementsValueResult getAllSubmodelElementsValueOnly(String submodelIdentifier, Integer limit, String cursor, String level, String extent) throws ApiException {

    ApiResponse<GetSubmodelElementsValueResult> localVarResponse = getAllSubmodelElementsValueOnlyWithHttpInfo(submodelIdentifier, limit, cursor, level, extent);
    return localVarResponse.getData();
  }

  /**
   * Returns all submodel elements including their hierarchy in the ValueOnly representation
   * 
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param limit The maximum number of elements in the response array (optional)
   * @param cursor A server-generated identifier retrieved from pagingMetadata that specifies from which position the result listing should continue (optional)
   * @param level Determines the structural depth of the respective resource content (optional, default to deep)
   * @param extent Determines to which extent the resource is being serialized (optional, default to withoutBlobValue)
   * @return ApiResponse&lt;GetSubmodelElementsValueResult&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<GetSubmodelElementsValueResult> getAllSubmodelElementsValueOnlyWithHttpInfo(String submodelIdentifier, Integer limit, String cursor, String level, String extent) throws ApiException {
    String  submodelIdentifierAsBytes = ApiClient.base64UrlEncode(submodelIdentifier);
  	return getAllSubmodelElementsValueOnlyWithHttpInfoNoUrlEncoding(submodelIdentifierAsBytes, limit, cursor, level, extent);
 	
 }


  /**
   * Returns all submodel elements including their hierarchy in the ValueOnly representation
   * 
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param limit The maximum number of elements in the response array (optional)
   * @param cursor A server-generated identifier retrieved from pagingMetadata that specifies from which position the result listing should continue (optional)
   * @param level Determines the structural depth of the respective resource content (optional, default to deep)
   * @param extent Determines to which extent the resource is being serialized (optional, default to withoutBlobValue)
   * @return ApiResponse&lt;GetSubmodelElementsValueResult&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<GetSubmodelElementsValueResult> getAllSubmodelElementsValueOnlyWithHttpInfoNoUrlEncoding(String submodelIdentifier, Integer limit, String cursor, String level, String extent) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = getAllSubmodelElementsValueOnlyRequestBuilder(submodelIdentifier, limit, cursor, level, extent);
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

  private HttpRequest.Builder getAllSubmodelElementsValueOnlyRequestBuilder(String submodelIdentifier, Integer limit, String cursor, String level, String extent) throws ApiException {
    // verify the required parameter 'submodelIdentifier' is set
    if (submodelIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'submodelIdentifier' when calling getAllSubmodelElementsValueOnly");
    }

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/submodels/{submodelIdentifier}/submodel-elements/$value"
        .replace("{submodelIdentifier}", ApiClient.urlEncode(submodelIdentifier.toString()));

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
   * Returns all submodel references
   * 
   * @param limit The maximum number of elements in the response array (optional)
   * @param cursor A server-generated identifier retrieved from pagingMetadata that specifies from which position the result listing should continue (optional)
   * @return GetReferencesResult
   * @throws ApiException if fails to make API call
   */
  public GetReferencesResult getAllSubmodelReferences(Integer limit, String cursor) throws ApiException {

    ApiResponse<GetReferencesResult> localVarResponse = getAllSubmodelReferencesWithHttpInfo(limit, cursor);
    return localVarResponse.getData();
  }

  /**
   * Returns all submodel references
   * 
   * @param limit The maximum number of elements in the response array (optional)
   * @param cursor A server-generated identifier retrieved from pagingMetadata that specifies from which position the result listing should continue (optional)
   * @return ApiResponse&lt;GetReferencesResult&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<GetReferencesResult> getAllSubmodelReferencesWithHttpInfo(Integer limit, String cursor) throws ApiException {
  	return getAllSubmodelReferencesWithHttpInfoNoUrlEncoding(limit, cursor);
 	
 }


  /**
   * Returns all submodel references
   * 
   * @param limit The maximum number of elements in the response array (optional)
   * @param cursor A server-generated identifier retrieved from pagingMetadata that specifies from which position the result listing should continue (optional)
   * @return ApiResponse&lt;GetReferencesResult&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<GetReferencesResult> getAllSubmodelReferencesWithHttpInfoNoUrlEncoding(Integer limit, String cursor) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = getAllSubmodelReferencesRequestBuilder(limit, cursor);
    try {
      HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(
          localVarRequestBuilder.build(),
          HttpResponse.BodyHandlers.ofInputStream());
      if (memberVarResponseInterceptor != null) {
        memberVarResponseInterceptor.accept(localVarResponse);
      }
      try {
        if (localVarResponse.statusCode()/ 100 != 2) {
          throw getApiException("getAllSubmodelReferences", localVarResponse);
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

  private HttpRequest.Builder getAllSubmodelReferencesRequestBuilder(Integer limit, String cursor) throws ApiException {

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/submodel-refs";

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
   * Returns a specific Asset Administration Shell
   * 
   * @return AssetAdministrationShell
   * @throws ApiException if fails to make API call
   */
  public AssetAdministrationShell getAssetAdministrationShell() throws ApiException {

    ApiResponse<AssetAdministrationShell> localVarResponse = getAssetAdministrationShellWithHttpInfo();
    return localVarResponse.getData();
  }

  /**
   * Returns a specific Asset Administration Shell
   * 
   * @return ApiResponse&lt;AssetAdministrationShell&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<AssetAdministrationShell> getAssetAdministrationShellWithHttpInfo() throws ApiException {
  	return getAssetAdministrationShellWithHttpInfoNoUrlEncoding();
 	
 }


  /**
   * Returns a specific Asset Administration Shell
   * 
   * @return ApiResponse&lt;AssetAdministrationShell&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<AssetAdministrationShell> getAssetAdministrationShellWithHttpInfoNoUrlEncoding() throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = getAssetAdministrationShellRequestBuilder();
    try {
      HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(
          localVarRequestBuilder.build(),
          HttpResponse.BodyHandlers.ofInputStream());
      if (memberVarResponseInterceptor != null) {
        memberVarResponseInterceptor.accept(localVarResponse);
      }
      try {
        if (localVarResponse.statusCode()/ 100 != 2) {
          throw getApiException("getAssetAdministrationShell", localVarResponse);
        }
        return new ApiResponse<AssetAdministrationShell>(
          localVarResponse.statusCode(),
          localVarResponse.headers().map(),
          localVarResponse.body() == null ? null : memberVarObjectMapper.readValue(localVarResponse.body(), new TypeReference<AssetAdministrationShell>() {}) // closes the InputStream
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

  private HttpRequest.Builder getAssetAdministrationShellRequestBuilder() throws ApiException {

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/";

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
   * Returns a specific Asset Administration Shell as a Reference
   * 
   * @return Reference
   * @throws ApiException if fails to make API call
   */
  public Reference getAssetAdministrationShellReference() throws ApiException {

    ApiResponse<Reference> localVarResponse = getAssetAdministrationShellReferenceWithHttpInfo();
    return localVarResponse.getData();
  }

  /**
   * Returns a specific Asset Administration Shell as a Reference
   * 
   * @return ApiResponse&lt;Reference&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<Reference> getAssetAdministrationShellReferenceWithHttpInfo() throws ApiException {
  	return getAssetAdministrationShellReferenceWithHttpInfoNoUrlEncoding();
 	
 }


  /**
   * Returns a specific Asset Administration Shell as a Reference
   * 
   * @return ApiResponse&lt;Reference&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<Reference> getAssetAdministrationShellReferenceWithHttpInfoNoUrlEncoding() throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = getAssetAdministrationShellReferenceRequestBuilder();
    try {
      HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(
          localVarRequestBuilder.build(),
          HttpResponse.BodyHandlers.ofInputStream());
      if (memberVarResponseInterceptor != null) {
        memberVarResponseInterceptor.accept(localVarResponse);
      }
      try {
        if (localVarResponse.statusCode()/ 100 != 2) {
          throw getApiException("getAssetAdministrationShellReference", localVarResponse);
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

  private HttpRequest.Builder getAssetAdministrationShellReferenceRequestBuilder() throws ApiException {

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/$reference";

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
   * Returns the Asset Information
   * 
   * @return AssetInformation
   * @throws ApiException if fails to make API call
   */
  public AssetInformation getAssetInformation() throws ApiException {

    ApiResponse<AssetInformation> localVarResponse = getAssetInformationWithHttpInfo();
    return localVarResponse.getData();
  }

  /**
   * Returns the Asset Information
   * 
   * @return ApiResponse&lt;AssetInformation&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<AssetInformation> getAssetInformationWithHttpInfo() throws ApiException {
  	return getAssetInformationWithHttpInfoNoUrlEncoding();
 	
 }


  /**
   * Returns the Asset Information
   * 
   * @return ApiResponse&lt;AssetInformation&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<AssetInformation> getAssetInformationWithHttpInfoNoUrlEncoding() throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = getAssetInformationRequestBuilder();
    try {
      HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(
          localVarRequestBuilder.build(),
          HttpResponse.BodyHandlers.ofInputStream());
      if (memberVarResponseInterceptor != null) {
        memberVarResponseInterceptor.accept(localVarResponse);
      }
      try {
        if (localVarResponse.statusCode()/ 100 != 2) {
          throw getApiException("getAssetInformation", localVarResponse);
        }
        return new ApiResponse<AssetInformation>(
          localVarResponse.statusCode(),
          localVarResponse.headers().map(),
          localVarResponse.body() == null ? null : memberVarObjectMapper.readValue(localVarResponse.body(), new TypeReference<AssetInformation>() {}) // closes the InputStream
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

  private HttpRequest.Builder getAssetInformationRequestBuilder() throws ApiException {

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/assetinformation";

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
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @return File
   * @throws ApiException if fails to make API call
   */
  public File getFileByPath(String submodelIdentifier, String idShortPath) throws ApiException {

    ApiResponse<File> localVarResponse = getFileByPathWithHttpInfo(submodelIdentifier, idShortPath);
    return localVarResponse.getData();
  }

  /**
   * Downloads file content from a specific submodel element from the Submodel at a specified path
   * 
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @return ApiResponse&lt;File&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<File> getFileByPathWithHttpInfo(String submodelIdentifier, String idShortPath) throws ApiException {
    String  submodelIdentifierAsBytes = ApiClient.base64UrlEncode(submodelIdentifier);
  	return getFileByPathWithHttpInfoNoUrlEncoding(submodelIdentifierAsBytes, idShortPath);
 	
 }


  /**
   * Downloads file content from a specific submodel element from the Submodel at a specified path
   * 
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @return ApiResponse&lt;File&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<File> getFileByPathWithHttpInfoNoUrlEncoding(String submodelIdentifier, String idShortPath) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = getFileByPathRequestBuilder(submodelIdentifier, idShortPath);
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

  private HttpRequest.Builder getFileByPathRequestBuilder(String submodelIdentifier, String idShortPath) throws ApiException {
    // verify the required parameter 'submodelIdentifier' is set
    if (submodelIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'submodelIdentifier' when calling getFileByPath");
    }
    // verify the required parameter 'idShortPath' is set
    if (idShortPath == null) {
      throw new ApiException(400, "Missing the required parameter 'idShortPath' when calling getFileByPath");
    }

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/submodels/{submodelIdentifier}/submodel-elements/{idShortPath}/attachment"
        .replace("{submodelIdentifier}", ApiClient.urlEncode(submodelIdentifier.toString()))
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
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param handleId The returned handle id of an operation’s asynchronous invocation used to request the current state of the operation’s execution (UTF8-BASE64-URL-encoded) (required)
   * @return OperationResult
   * @throws ApiException if fails to make API call
   */
  public OperationResult getOperationAsyncResult(String submodelIdentifier, String aasIdentifier, String idShortPath, String handleId) throws ApiException {

    ApiResponse<OperationResult> localVarResponse = getOperationAsyncResultWithHttpInfo(submodelIdentifier, aasIdentifier, idShortPath, handleId);
    return localVarResponse.getData();
  }

  /**
   * Returns the Operation result of an asynchronous invoked Operation
   * 
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param handleId The returned handle id of an operation’s asynchronous invocation used to request the current state of the operation’s execution (UTF8-BASE64-URL-encoded) (required)
   * @return ApiResponse&lt;OperationResult&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<OperationResult> getOperationAsyncResultWithHttpInfo(String submodelIdentifier, String aasIdentifier, String idShortPath, String handleId) throws ApiException {
    String  submodelIdentifierAsBytes = ApiClient.base64UrlEncode(submodelIdentifier);
    String  aasIdentifierAsBytes = ApiClient.base64UrlEncode(aasIdentifier);
    String  handleIdAsBytes = ApiClient.base64UrlEncode(handleId);
  	return getOperationAsyncResultWithHttpInfoNoUrlEncoding(submodelIdentifierAsBytes, aasIdentifierAsBytes, idShortPath, handleIdAsBytes);
 	
 }


  /**
   * Returns the Operation result of an asynchronous invoked Operation
   * 
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param handleId The returned handle id of an operation’s asynchronous invocation used to request the current state of the operation’s execution (UTF8-BASE64-URL-encoded) (required)
   * @return ApiResponse&lt;OperationResult&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<OperationResult> getOperationAsyncResultWithHttpInfoNoUrlEncoding(String submodelIdentifier, String aasIdentifier, String idShortPath, String handleId) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = getOperationAsyncResultRequestBuilder(submodelIdentifier, aasIdentifier, idShortPath, handleId);
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

  private HttpRequest.Builder getOperationAsyncResultRequestBuilder(String submodelIdentifier, String aasIdentifier, String idShortPath, String handleId) throws ApiException {
    // verify the required parameter 'submodelIdentifier' is set
    if (submodelIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'submodelIdentifier' when calling getOperationAsyncResult");
    }
    // verify the required parameter 'aasIdentifier' is set
    if (aasIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'aasIdentifier' when calling getOperationAsyncResult");
    }
    // verify the required parameter 'idShortPath' is set
    if (idShortPath == null) {
      throw new ApiException(400, "Missing the required parameter 'idShortPath' when calling getOperationAsyncResult");
    }
    // verify the required parameter 'handleId' is set
    if (handleId == null) {
      throw new ApiException(400, "Missing the required parameter 'handleId' when calling getOperationAsyncResult");
    }

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/submodels/{submodelIdentifier}/submodel-elements/{idShortPath}/operation-results/{handleId}"
        .replace("{submodelIdentifier}", ApiClient.urlEncode(submodelIdentifier.toString()))
        .replace("{aasIdentifier}", ApiClient.urlEncode(aasIdentifier.toString()))
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
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param handleId The returned handle id of an operation’s asynchronous invocation used to request the current state of the operation’s execution (UTF8-BASE64-URL-encoded) (required)
   * @return OperationResultValueOnly
   * @throws ApiException if fails to make API call
   */
  public OperationResultValueOnly getOperationAsyncResultValueOnly(String submodelIdentifier, String aasIdentifier, String idShortPath, String handleId) throws ApiException {

    ApiResponse<OperationResultValueOnly> localVarResponse = getOperationAsyncResultValueOnlyWithHttpInfo(submodelIdentifier, aasIdentifier, idShortPath, handleId);
    return localVarResponse.getData();
  }

  /**
   * Returns the value of the Operation result of an asynchronous invoked Operation
   * 
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param handleId The returned handle id of an operation’s asynchronous invocation used to request the current state of the operation’s execution (UTF8-BASE64-URL-encoded) (required)
   * @return ApiResponse&lt;OperationResultValueOnly&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<OperationResultValueOnly> getOperationAsyncResultValueOnlyWithHttpInfo(String submodelIdentifier, String aasIdentifier, String idShortPath, String handleId) throws ApiException {
    String  submodelIdentifierAsBytes = ApiClient.base64UrlEncode(submodelIdentifier);
    String  aasIdentifierAsBytes = ApiClient.base64UrlEncode(aasIdentifier);
    String  handleIdAsBytes = ApiClient.base64UrlEncode(handleId);
  	return getOperationAsyncResultValueOnlyWithHttpInfoNoUrlEncoding(submodelIdentifierAsBytes, aasIdentifierAsBytes, idShortPath, handleIdAsBytes);
 	
 }


  /**
   * Returns the value of the Operation result of an asynchronous invoked Operation
   * 
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param handleId The returned handle id of an operation’s asynchronous invocation used to request the current state of the operation’s execution (UTF8-BASE64-URL-encoded) (required)
   * @return ApiResponse&lt;OperationResultValueOnly&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<OperationResultValueOnly> getOperationAsyncResultValueOnlyWithHttpInfoNoUrlEncoding(String submodelIdentifier, String aasIdentifier, String idShortPath, String handleId) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = getOperationAsyncResultValueOnlyRequestBuilder(submodelIdentifier, aasIdentifier, idShortPath, handleId);
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

  private HttpRequest.Builder getOperationAsyncResultValueOnlyRequestBuilder(String submodelIdentifier, String aasIdentifier, String idShortPath, String handleId) throws ApiException {
    // verify the required parameter 'submodelIdentifier' is set
    if (submodelIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'submodelIdentifier' when calling getOperationAsyncResultValueOnly");
    }
    // verify the required parameter 'aasIdentifier' is set
    if (aasIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'aasIdentifier' when calling getOperationAsyncResultValueOnly");
    }
    // verify the required parameter 'idShortPath' is set
    if (idShortPath == null) {
      throw new ApiException(400, "Missing the required parameter 'idShortPath' when calling getOperationAsyncResultValueOnly");
    }
    // verify the required parameter 'handleId' is set
    if (handleId == null) {
      throw new ApiException(400, "Missing the required parameter 'handleId' when calling getOperationAsyncResultValueOnly");
    }

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/submodels/{submodelIdentifier}/submodel-elements/{idShortPath}/operation-results/{handleId}/$value"
        .replace("{submodelIdentifier}", ApiClient.urlEncode(submodelIdentifier.toString()))
        .replace("{aasIdentifier}", ApiClient.urlEncode(aasIdentifier.toString()))
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
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param handleId The returned handle id of an operation’s asynchronous invocation used to request the current state of the operation’s execution (UTF8-BASE64-URL-encoded) (required)
   * @return BaseOperationResult
   * @throws ApiException if fails to make API call
   */
  public BaseOperationResult getOperationAsyncStatus(String submodelIdentifier, String aasIdentifier, String idShortPath, String handleId) throws ApiException {

    ApiResponse<BaseOperationResult> localVarResponse = getOperationAsyncStatusWithHttpInfo(submodelIdentifier, aasIdentifier, idShortPath, handleId);
    return localVarResponse.getData();
  }

  /**
   * Returns the status of an asynchronously invoked Operation
   * 
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param handleId The returned handle id of an operation’s asynchronous invocation used to request the current state of the operation’s execution (UTF8-BASE64-URL-encoded) (required)
   * @return ApiResponse&lt;BaseOperationResult&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<BaseOperationResult> getOperationAsyncStatusWithHttpInfo(String submodelIdentifier, String aasIdentifier, String idShortPath, String handleId) throws ApiException {
    String  submodelIdentifierAsBytes = ApiClient.base64UrlEncode(submodelIdentifier);
    String  aasIdentifierAsBytes = ApiClient.base64UrlEncode(aasIdentifier);
    String  handleIdAsBytes = ApiClient.base64UrlEncode(handleId);
  	return getOperationAsyncStatusWithHttpInfoNoUrlEncoding(submodelIdentifierAsBytes, aasIdentifierAsBytes, idShortPath, handleIdAsBytes);
 	
 }


  /**
   * Returns the status of an asynchronously invoked Operation
   * 
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param handleId The returned handle id of an operation’s asynchronous invocation used to request the current state of the operation’s execution (UTF8-BASE64-URL-encoded) (required)
   * @return ApiResponse&lt;BaseOperationResult&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<BaseOperationResult> getOperationAsyncStatusWithHttpInfoNoUrlEncoding(String submodelIdentifier, String aasIdentifier, String idShortPath, String handleId) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = getOperationAsyncStatusRequestBuilder(submodelIdentifier, aasIdentifier, idShortPath, handleId);
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

  private HttpRequest.Builder getOperationAsyncStatusRequestBuilder(String submodelIdentifier, String aasIdentifier, String idShortPath, String handleId) throws ApiException {
    // verify the required parameter 'submodelIdentifier' is set
    if (submodelIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'submodelIdentifier' when calling getOperationAsyncStatus");
    }
    // verify the required parameter 'aasIdentifier' is set
    if (aasIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'aasIdentifier' when calling getOperationAsyncStatus");
    }
    // verify the required parameter 'idShortPath' is set
    if (idShortPath == null) {
      throw new ApiException(400, "Missing the required parameter 'idShortPath' when calling getOperationAsyncStatus");
    }
    // verify the required parameter 'handleId' is set
    if (handleId == null) {
      throw new ApiException(400, "Missing the required parameter 'handleId' when calling getOperationAsyncStatus");
    }

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/submodels/{submodelIdentifier}/submodel-elements/{idShortPath}/operation-status/{handleId}"
        .replace("{submodelIdentifier}", ApiClient.urlEncode(submodelIdentifier.toString()))
        .replace("{aasIdentifier}", ApiClient.urlEncode(aasIdentifier.toString()))
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
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param level Determines the structural depth of the respective resource content (optional, default to deep)
   * @param extent Determines to which extent the resource is being serialized (optional, default to withoutBlobValue)
   * @return Submodel
   * @throws ApiException if fails to make API call
   */
  public Submodel getSubmodel(String submodelIdentifier, String level, String extent) throws ApiException {

    ApiResponse<Submodel> localVarResponse = getSubmodelWithHttpInfo(submodelIdentifier, level, extent);
    return localVarResponse.getData();
  }

  /**
   * Returns the Submodel
   * 
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param level Determines the structural depth of the respective resource content (optional, default to deep)
   * @param extent Determines to which extent the resource is being serialized (optional, default to withoutBlobValue)
   * @return ApiResponse&lt;Submodel&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<Submodel> getSubmodelWithHttpInfo(String submodelIdentifier, String level, String extent) throws ApiException {
    String  submodelIdentifierAsBytes = ApiClient.base64UrlEncode(submodelIdentifier);
  	return getSubmodelWithHttpInfoNoUrlEncoding(submodelIdentifierAsBytes, level, extent);
 	
 }


  /**
   * Returns the Submodel
   * 
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param level Determines the structural depth of the respective resource content (optional, default to deep)
   * @param extent Determines to which extent the resource is being serialized (optional, default to withoutBlobValue)
   * @return ApiResponse&lt;Submodel&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<Submodel> getSubmodelWithHttpInfoNoUrlEncoding(String submodelIdentifier, String level, String extent) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = getSubmodelRequestBuilder(submodelIdentifier, level, extent);
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

  private HttpRequest.Builder getSubmodelRequestBuilder(String submodelIdentifier, String level, String extent) throws ApiException {
    // verify the required parameter 'submodelIdentifier' is set
    if (submodelIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'submodelIdentifier' when calling getSubmodel");
    }

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/submodels/{submodelIdentifier}"
        .replace("{submodelIdentifier}", ApiClient.urlEncode(submodelIdentifier.toString()));

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
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param level Determines the structural depth of the respective resource content (optional, default to deep)
   * @param extent Determines to which extent the resource is being serialized (optional, default to withoutBlobValue)
   * @return SubmodelElement
   * @throws ApiException if fails to make API call
   */
  public SubmodelElement getSubmodelElementByPath(String submodelIdentifier, String idShortPath, String level, String extent) throws ApiException {

    ApiResponse<SubmodelElement> localVarResponse = getSubmodelElementByPathWithHttpInfo(submodelIdentifier, idShortPath, level, extent);
    return localVarResponse.getData();
  }

  /**
   * Returns a specific submodel element from the Submodel at a specified path
   * 
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param level Determines the structural depth of the respective resource content (optional, default to deep)
   * @param extent Determines to which extent the resource is being serialized (optional, default to withoutBlobValue)
   * @return ApiResponse&lt;SubmodelElement&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<SubmodelElement> getSubmodelElementByPathWithHttpInfo(String submodelIdentifier, String idShortPath, String level, String extent) throws ApiException {
    String  submodelIdentifierAsBytes = ApiClient.base64UrlEncode(submodelIdentifier);
  	return getSubmodelElementByPathWithHttpInfoNoUrlEncoding(submodelIdentifierAsBytes, idShortPath, level, extent);
 	
 }


  /**
   * Returns a specific submodel element from the Submodel at a specified path
   * 
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param level Determines the structural depth of the respective resource content (optional, default to deep)
   * @param extent Determines to which extent the resource is being serialized (optional, default to withoutBlobValue)
   * @return ApiResponse&lt;SubmodelElement&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<SubmodelElement> getSubmodelElementByPathWithHttpInfoNoUrlEncoding(String submodelIdentifier, String idShortPath, String level, String extent) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = getSubmodelElementByPathRequestBuilder(submodelIdentifier, idShortPath, level, extent);
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

  private HttpRequest.Builder getSubmodelElementByPathRequestBuilder(String submodelIdentifier, String idShortPath, String level, String extent) throws ApiException {
    // verify the required parameter 'submodelIdentifier' is set
    if (submodelIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'submodelIdentifier' when calling getSubmodelElementByPath");
    }
    // verify the required parameter 'idShortPath' is set
    if (idShortPath == null) {
      throw new ApiException(400, "Missing the required parameter 'idShortPath' when calling getSubmodelElementByPath");
    }

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/submodels/{submodelIdentifier}/submodel-elements/{idShortPath}"
        .replace("{submodelIdentifier}", ApiClient.urlEncode(submodelIdentifier.toString()))
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
   * Returns the metadata attributes if a specific submodel element from the Submodel at a specified path
   * 
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param level Determines the structural depth of the respective resource content (optional, default to deep)
   * @return SubmodelElementMetadata
   * @throws ApiException if fails to make API call
   */
  public SubmodelElementMetadata getSubmodelElementByPathMetadata(String submodelIdentifier, String idShortPath, String level) throws ApiException {

    ApiResponse<SubmodelElementMetadata> localVarResponse = getSubmodelElementByPathMetadataWithHttpInfo(submodelIdentifier, idShortPath, level);
    return localVarResponse.getData();
  }

  /**
   * Returns the metadata attributes if a specific submodel element from the Submodel at a specified path
   * 
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param level Determines the structural depth of the respective resource content (optional, default to deep)
   * @return ApiResponse&lt;SubmodelElementMetadata&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<SubmodelElementMetadata> getSubmodelElementByPathMetadataWithHttpInfo(String submodelIdentifier, String idShortPath, String level) throws ApiException {
    String  submodelIdentifierAsBytes = ApiClient.base64UrlEncode(submodelIdentifier);
  	return getSubmodelElementByPathMetadataWithHttpInfoNoUrlEncoding(submodelIdentifierAsBytes, idShortPath, level);
 	
 }


  /**
   * Returns the metadata attributes if a specific submodel element from the Submodel at a specified path
   * 
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param level Determines the structural depth of the respective resource content (optional, default to deep)
   * @return ApiResponse&lt;SubmodelElementMetadata&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<SubmodelElementMetadata> getSubmodelElementByPathMetadataWithHttpInfoNoUrlEncoding(String submodelIdentifier, String idShortPath, String level) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = getSubmodelElementByPathMetadataRequestBuilder(submodelIdentifier, idShortPath, level);
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

  private HttpRequest.Builder getSubmodelElementByPathMetadataRequestBuilder(String submodelIdentifier, String idShortPath, String level) throws ApiException {
    // verify the required parameter 'submodelIdentifier' is set
    if (submodelIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'submodelIdentifier' when calling getSubmodelElementByPathMetadata");
    }
    // verify the required parameter 'idShortPath' is set
    if (idShortPath == null) {
      throw new ApiException(400, "Missing the required parameter 'idShortPath' when calling getSubmodelElementByPathMetadata");
    }

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/submodels/{submodelIdentifier}/submodel-elements/{idShortPath}/$metadata"
        .replace("{submodelIdentifier}", ApiClient.urlEncode(submodelIdentifier.toString()))
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
   * Returns a specific submodel element from the Submodel at a specified path in the Path notation
   * 
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param level Determines the structural depth of the respective resource content (optional, default to deep)
   * @return String
   * @throws ApiException if fails to make API call
   */
  public String getSubmodelElementByPathPath(String submodelIdentifier, String idShortPath, String level) throws ApiException {

    ApiResponse<String> localVarResponse = getSubmodelElementByPathPathWithHttpInfo(submodelIdentifier, idShortPath, level);
    return localVarResponse.getData();
  }

  /**
   * Returns a specific submodel element from the Submodel at a specified path in the Path notation
   * 
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param level Determines the structural depth of the respective resource content (optional, default to deep)
   * @return ApiResponse&lt;String&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<String> getSubmodelElementByPathPathWithHttpInfo(String submodelIdentifier, String idShortPath, String level) throws ApiException {
    String  submodelIdentifierAsBytes = ApiClient.base64UrlEncode(submodelIdentifier);
  	return getSubmodelElementByPathPathWithHttpInfoNoUrlEncoding(submodelIdentifierAsBytes, idShortPath, level);
 	
 }


  /**
   * Returns a specific submodel element from the Submodel at a specified path in the Path notation
   * 
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param level Determines the structural depth of the respective resource content (optional, default to deep)
   * @return ApiResponse&lt;String&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<String> getSubmodelElementByPathPathWithHttpInfoNoUrlEncoding(String submodelIdentifier, String idShortPath, String level) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = getSubmodelElementByPathPathRequestBuilder(submodelIdentifier, idShortPath, level);
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

  private HttpRequest.Builder getSubmodelElementByPathPathRequestBuilder(String submodelIdentifier, String idShortPath, String level) throws ApiException {
    // verify the required parameter 'submodelIdentifier' is set
    if (submodelIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'submodelIdentifier' when calling getSubmodelElementByPathPath");
    }
    // verify the required parameter 'idShortPath' is set
    if (idShortPath == null) {
      throw new ApiException(400, "Missing the required parameter 'idShortPath' when calling getSubmodelElementByPathPath");
    }

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/submodels/{submodelIdentifier}/submodel-elements/{idShortPath}/$path"
        .replace("{submodelIdentifier}", ApiClient.urlEncode(submodelIdentifier.toString()))
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
   * Returns the Reference of a specific submodel element from the Submodel at a specified path in the ValueOnly representation
   * 
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @return Reference
   * @throws ApiException if fails to make API call
   */
  public Reference getSubmodelElementByPathReference(String submodelIdentifier, String idShortPath) throws ApiException {

    ApiResponse<Reference> localVarResponse = getSubmodelElementByPathReferenceWithHttpInfo(submodelIdentifier, idShortPath);
    return localVarResponse.getData();
  }

  /**
   * Returns the Reference of a specific submodel element from the Submodel at a specified path in the ValueOnly representation
   * 
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @return ApiResponse&lt;Reference&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<Reference> getSubmodelElementByPathReferenceWithHttpInfo(String submodelIdentifier, String idShortPath) throws ApiException {
    String  submodelIdentifierAsBytes = ApiClient.base64UrlEncode(submodelIdentifier);
  	return getSubmodelElementByPathReferenceWithHttpInfoNoUrlEncoding(submodelIdentifierAsBytes, idShortPath);
 	
 }


  /**
   * Returns the Reference of a specific submodel element from the Submodel at a specified path in the ValueOnly representation
   * 
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @return ApiResponse&lt;Reference&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<Reference> getSubmodelElementByPathReferenceWithHttpInfoNoUrlEncoding(String submodelIdentifier, String idShortPath) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = getSubmodelElementByPathReferenceRequestBuilder(submodelIdentifier, idShortPath);
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

  private HttpRequest.Builder getSubmodelElementByPathReferenceRequestBuilder(String submodelIdentifier, String idShortPath) throws ApiException {
    // verify the required parameter 'submodelIdentifier' is set
    if (submodelIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'submodelIdentifier' when calling getSubmodelElementByPathReference");
    }
    // verify the required parameter 'idShortPath' is set
    if (idShortPath == null) {
      throw new ApiException(400, "Missing the required parameter 'idShortPath' when calling getSubmodelElementByPathReference");
    }

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/submodels/{submodelIdentifier}/submodel-elements/{idShortPath}/$reference"
        .replace("{submodelIdentifier}", ApiClient.urlEncode(submodelIdentifier.toString()))
        .replace("{idShortPath}", ApiClient.urlEncode(idShortPath.toString()));

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
   * Returns a specific submodel element from the Submodel at a specified path in the ValueOnly representation
   * 
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param level Determines the structural depth of the respective resource content (optional, default to deep)
   * @return SubmodelElementValue
   * @throws ApiException if fails to make API call
   */
  public SubmodelElementValue getSubmodelElementByPathValueOnly(String submodelIdentifier, String idShortPath, String level) throws ApiException {

    ApiResponse<SubmodelElementValue> localVarResponse = getSubmodelElementByPathValueOnlyWithHttpInfo(submodelIdentifier, idShortPath, level);
    return localVarResponse.getData();
  }

  /**
   * Returns a specific submodel element from the Submodel at a specified path in the ValueOnly representation
   * 
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param level Determines the structural depth of the respective resource content (optional, default to deep)
   * @return ApiResponse&lt;SubmodelElementValue&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<SubmodelElementValue> getSubmodelElementByPathValueOnlyWithHttpInfo(String submodelIdentifier, String idShortPath, String level) throws ApiException {
    String  submodelIdentifierAsBytes = ApiClient.base64UrlEncode(submodelIdentifier);
  	return getSubmodelElementByPathValueOnlyWithHttpInfoNoUrlEncoding(submodelIdentifierAsBytes, idShortPath, level);
 	
 }


  /**
   * Returns a specific submodel element from the Submodel at a specified path in the ValueOnly representation
   * 
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param level Determines the structural depth of the respective resource content (optional, default to deep)
   * @return ApiResponse&lt;SubmodelElementValue&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<SubmodelElementValue> getSubmodelElementByPathValueOnlyWithHttpInfoNoUrlEncoding(String submodelIdentifier, String idShortPath, String level) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = getSubmodelElementByPathValueOnlyRequestBuilder(submodelIdentifier, idShortPath, level);
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

  private HttpRequest.Builder getSubmodelElementByPathValueOnlyRequestBuilder(String submodelIdentifier, String idShortPath, String level) throws ApiException {
    // verify the required parameter 'submodelIdentifier' is set
    if (submodelIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'submodelIdentifier' when calling getSubmodelElementByPathValueOnly");
    }
    // verify the required parameter 'idShortPath' is set
    if (idShortPath == null) {
      throw new ApiException(400, "Missing the required parameter 'idShortPath' when calling getSubmodelElementByPathValueOnly");
    }

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/submodels/{submodelIdentifier}/submodel-elements/{idShortPath}/$value"
        .replace("{submodelIdentifier}", ApiClient.urlEncode(submodelIdentifier.toString()))
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
   * Returns the Submodel&#39;s metadata elements
   * 
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param level Determines the structural depth of the respective resource content (optional, default to deep)
   * @return SubmodelMetadata
   * @throws ApiException if fails to make API call
   */
  public SubmodelMetadata getSubmodelMetadata(String submodelIdentifier, String level) throws ApiException {

    ApiResponse<SubmodelMetadata> localVarResponse = getSubmodelMetadataWithHttpInfo(submodelIdentifier, level);
    return localVarResponse.getData();
  }

  /**
   * Returns the Submodel&#39;s metadata elements
   * 
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param level Determines the structural depth of the respective resource content (optional, default to deep)
   * @return ApiResponse&lt;SubmodelMetadata&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<SubmodelMetadata> getSubmodelMetadataWithHttpInfo(String submodelIdentifier, String level) throws ApiException {
    String  submodelIdentifierAsBytes = ApiClient.base64UrlEncode(submodelIdentifier);
  	return getSubmodelMetadataWithHttpInfoNoUrlEncoding(submodelIdentifierAsBytes, level);
 	
 }


  /**
   * Returns the Submodel&#39;s metadata elements
   * 
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param level Determines the structural depth of the respective resource content (optional, default to deep)
   * @return ApiResponse&lt;SubmodelMetadata&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<SubmodelMetadata> getSubmodelMetadataWithHttpInfoNoUrlEncoding(String submodelIdentifier, String level) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = getSubmodelMetadataRequestBuilder(submodelIdentifier, level);
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

  private HttpRequest.Builder getSubmodelMetadataRequestBuilder(String submodelIdentifier, String level) throws ApiException {
    // verify the required parameter 'submodelIdentifier' is set
    if (submodelIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'submodelIdentifier' when calling getSubmodelMetadata");
    }

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/submodels/{submodelIdentifier}/$metadata"
        .replace("{submodelIdentifier}", ApiClient.urlEncode(submodelIdentifier.toString()));

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
   * Returns the Submodel as a Reference
   * 
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param level Determines the structural depth of the respective resource content (optional, default to core)
   * @return Reference
   * @throws ApiException if fails to make API call
   */
  public Reference getSubmodelMetadataReference(String submodelIdentifier, String level) throws ApiException {

    ApiResponse<Reference> localVarResponse = getSubmodelMetadataReferenceWithHttpInfo(submodelIdentifier, level);
    return localVarResponse.getData();
  }

  /**
   * Returns the Submodel as a Reference
   * 
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param level Determines the structural depth of the respective resource content (optional, default to core)
   * @return ApiResponse&lt;Reference&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<Reference> getSubmodelMetadataReferenceWithHttpInfo(String submodelIdentifier, String level) throws ApiException {
    String  submodelIdentifierAsBytes = ApiClient.base64UrlEncode(submodelIdentifier);
  	return getSubmodelMetadataReferenceWithHttpInfoNoUrlEncoding(submodelIdentifierAsBytes, level);
 	
 }


  /**
   * Returns the Submodel as a Reference
   * 
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param level Determines the structural depth of the respective resource content (optional, default to core)
   * @return ApiResponse&lt;Reference&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<Reference> getSubmodelMetadataReferenceWithHttpInfoNoUrlEncoding(String submodelIdentifier, String level) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = getSubmodelMetadataReferenceRequestBuilder(submodelIdentifier, level);
    try {
      HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(
          localVarRequestBuilder.build(),
          HttpResponse.BodyHandlers.ofInputStream());
      if (memberVarResponseInterceptor != null) {
        memberVarResponseInterceptor.accept(localVarResponse);
      }
      try {
        if (localVarResponse.statusCode()/ 100 != 2) {
          throw getApiException("getSubmodelMetadataReference", localVarResponse);
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

  private HttpRequest.Builder getSubmodelMetadataReferenceRequestBuilder(String submodelIdentifier, String level) throws ApiException {
    // verify the required parameter 'submodelIdentifier' is set
    if (submodelIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'submodelIdentifier' when calling getSubmodelMetadataReference");
    }

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/submodels/{submodelIdentifier}/$reference"
        .replace("{submodelIdentifier}", ApiClient.urlEncode(submodelIdentifier.toString()));

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
   * Returns the Submodel&#39;s metadata elements
   * 
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param level Determines the structural depth of the respective resource content (optional, default to deep)
   * @return List&lt;String&gt;
   * @throws ApiException if fails to make API call
   */
  public List<String> getSubmodelPath(String submodelIdentifier, String level) throws ApiException {

    ApiResponse<List<String>> localVarResponse = getSubmodelPathWithHttpInfo(submodelIdentifier, level);
    return localVarResponse.getData();
  }

  /**
   * Returns the Submodel&#39;s metadata elements
   * 
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param level Determines the structural depth of the respective resource content (optional, default to deep)
   * @return ApiResponse&lt;List&lt;String&gt;&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<List<String>> getSubmodelPathWithHttpInfo(String submodelIdentifier, String level) throws ApiException {
    String  submodelIdentifierAsBytes = ApiClient.base64UrlEncode(submodelIdentifier);
  	return getSubmodelPathWithHttpInfoNoUrlEncoding(submodelIdentifierAsBytes, level);
 	
 }


  /**
   * Returns the Submodel&#39;s metadata elements
   * 
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param level Determines the structural depth of the respective resource content (optional, default to deep)
   * @return ApiResponse&lt;List&lt;String&gt;&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<List<String>> getSubmodelPathWithHttpInfoNoUrlEncoding(String submodelIdentifier, String level) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = getSubmodelPathRequestBuilder(submodelIdentifier, level);
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

  private HttpRequest.Builder getSubmodelPathRequestBuilder(String submodelIdentifier, String level) throws ApiException {
    // verify the required parameter 'submodelIdentifier' is set
    if (submodelIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'submodelIdentifier' when calling getSubmodelPath");
    }

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/submodels/{submodelIdentifier}/$path"
        .replace("{submodelIdentifier}", ApiClient.urlEncode(submodelIdentifier.toString()));

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
   * Returns the Submodel&#39;s ValueOnly representation
   * 
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param level Determines the structural depth of the respective resource content (optional, default to deep)
   * @param extent Determines to which extent the resource is being serialized (optional, default to withoutBlobValue)
   * @return SubmodelValue
   * @throws ApiException if fails to make API call
   */
  public SubmodelValue getSubmodelValueOnly(String submodelIdentifier, String level, String extent) throws ApiException {

    ApiResponse<SubmodelValue> localVarResponse = getSubmodelValueOnlyWithHttpInfo(submodelIdentifier, level, extent);
    return localVarResponse.getData();
  }

  /**
   * Returns the Submodel&#39;s ValueOnly representation
   * 
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param level Determines the structural depth of the respective resource content (optional, default to deep)
   * @param extent Determines to which extent the resource is being serialized (optional, default to withoutBlobValue)
   * @return ApiResponse&lt;SubmodelValue&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<SubmodelValue> getSubmodelValueOnlyWithHttpInfo(String submodelIdentifier, String level, String extent) throws ApiException {
    String  submodelIdentifierAsBytes = ApiClient.base64UrlEncode(submodelIdentifier);
  	return getSubmodelValueOnlyWithHttpInfoNoUrlEncoding(submodelIdentifierAsBytes, level, extent);
 	
 }


  /**
   * Returns the Submodel&#39;s ValueOnly representation
   * 
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param level Determines the structural depth of the respective resource content (optional, default to deep)
   * @param extent Determines to which extent the resource is being serialized (optional, default to withoutBlobValue)
   * @return ApiResponse&lt;SubmodelValue&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<SubmodelValue> getSubmodelValueOnlyWithHttpInfoNoUrlEncoding(String submodelIdentifier, String level, String extent) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = getSubmodelValueOnlyRequestBuilder(submodelIdentifier, level, extent);
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

  private HttpRequest.Builder getSubmodelValueOnlyRequestBuilder(String submodelIdentifier, String level, String extent) throws ApiException {
    // verify the required parameter 'submodelIdentifier' is set
    if (submodelIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'submodelIdentifier' when calling getSubmodelValueOnly");
    }

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/submodels/{submodelIdentifier}/$value"
        .replace("{submodelIdentifier}", ApiClient.urlEncode(submodelIdentifier.toString()));

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
   * 
   * 
   * @return File
   * @throws ApiException if fails to make API call
   */
  public File getThumbnail() throws ApiException {

    ApiResponse<File> localVarResponse = getThumbnailWithHttpInfo();
    return localVarResponse.getData();
  }

  /**
   * 
   * 
   * @return ApiResponse&lt;File&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<File> getThumbnailWithHttpInfo() throws ApiException {
  	return getThumbnailWithHttpInfoNoUrlEncoding();
 	
 }


  /**
   * 
   * 
   * @return ApiResponse&lt;File&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<File> getThumbnailWithHttpInfoNoUrlEncoding() throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = getThumbnailRequestBuilder();
    try {
      HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(
          localVarRequestBuilder.build(),
          HttpResponse.BodyHandlers.ofInputStream());
      if (memberVarResponseInterceptor != null) {
        memberVarResponseInterceptor.accept(localVarResponse);
      }
      try {
        if (localVarResponse.statusCode()/ 100 != 2) {
          throw getApiException("getThumbnail", localVarResponse);
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

  private HttpRequest.Builder getThumbnailRequestBuilder() throws ApiException {

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/assetinformation/thumbnail";

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
   * Asynchronously invokes an Operation at a specified path
   * 
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param operationRequest Operation request object (required)
   * @throws ApiException if fails to make API call
   */
  public void invokeOperationAsync(String submodelIdentifier, String idShortPath, OperationRequest operationRequest) throws ApiException {

    invokeOperationAsyncWithHttpInfo(submodelIdentifier, idShortPath, operationRequest);
  }

  /**
   * Asynchronously invokes an Operation at a specified path
   * 
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param operationRequest Operation request object (required)
   * @return ApiResponse&lt;Void&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<Void> invokeOperationAsyncWithHttpInfo(String submodelIdentifier, String idShortPath, OperationRequest operationRequest) throws ApiException {
    String  submodelIdentifierAsBytes = ApiClient.base64UrlEncode(submodelIdentifier);
  	return invokeOperationAsyncWithHttpInfoNoUrlEncoding(submodelIdentifierAsBytes, idShortPath, operationRequest);
 	
 }


  /**
   * Asynchronously invokes an Operation at a specified path
   * 
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param operationRequest Operation request object (required)
   * @return ApiResponse&lt;Void&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<Void> invokeOperationAsyncWithHttpInfoNoUrlEncoding(String submodelIdentifier, String idShortPath, OperationRequest operationRequest) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = invokeOperationAsyncRequestBuilder(submodelIdentifier, idShortPath, operationRequest);
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

  private HttpRequest.Builder invokeOperationAsyncRequestBuilder(String submodelIdentifier, String idShortPath, OperationRequest operationRequest) throws ApiException {
    // verify the required parameter 'submodelIdentifier' is set
    if (submodelIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'submodelIdentifier' when calling invokeOperationAsync");
    }
    // verify the required parameter 'idShortPath' is set
    if (idShortPath == null) {
      throw new ApiException(400, "Missing the required parameter 'idShortPath' when calling invokeOperationAsync");
    }
    // verify the required parameter 'operationRequest' is set
    if (operationRequest == null) {
      throw new ApiException(400, "Missing the required parameter 'operationRequest' when calling invokeOperationAsync");
    }

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/submodels/{submodelIdentifier}/submodel-elements/{idShortPath}/invoke-asnyc"
        .replace("{submodelIdentifier}", ApiClient.urlEncode(submodelIdentifier.toString()))
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
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param operationRequestValueOnly Operation request object (required)
   * @throws ApiException if fails to make API call
   */
  public void invokeOperationAsyncValueOnly(String submodelIdentifier, String idShortPath, OperationRequestValueOnly operationRequestValueOnly) throws ApiException {

    invokeOperationAsyncValueOnlyWithHttpInfo(submodelIdentifier, idShortPath, operationRequestValueOnly);
  }

  /**
   * Asynchronously invokes an Operation at a specified path
   * 
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param operationRequestValueOnly Operation request object (required)
   * @return ApiResponse&lt;Void&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<Void> invokeOperationAsyncValueOnlyWithHttpInfo(String submodelIdentifier, String idShortPath, OperationRequestValueOnly operationRequestValueOnly) throws ApiException {
    String  submodelIdentifierAsBytes = ApiClient.base64UrlEncode(submodelIdentifier);
  	return invokeOperationAsyncValueOnlyWithHttpInfoNoUrlEncoding(submodelIdentifierAsBytes, idShortPath, operationRequestValueOnly);
 	
 }


  /**
   * Asynchronously invokes an Operation at a specified path
   * 
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param operationRequestValueOnly Operation request object (required)
   * @return ApiResponse&lt;Void&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<Void> invokeOperationAsyncValueOnlyWithHttpInfoNoUrlEncoding(String submodelIdentifier, String idShortPath, OperationRequestValueOnly operationRequestValueOnly) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = invokeOperationAsyncValueOnlyRequestBuilder(submodelIdentifier, idShortPath, operationRequestValueOnly);
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

  private HttpRequest.Builder invokeOperationAsyncValueOnlyRequestBuilder(String submodelIdentifier, String idShortPath, OperationRequestValueOnly operationRequestValueOnly) throws ApiException {
    // verify the required parameter 'submodelIdentifier' is set
    if (submodelIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'submodelIdentifier' when calling invokeOperationAsyncValueOnly");
    }
    // verify the required parameter 'idShortPath' is set
    if (idShortPath == null) {
      throw new ApiException(400, "Missing the required parameter 'idShortPath' when calling invokeOperationAsyncValueOnly");
    }
    // verify the required parameter 'operationRequestValueOnly' is set
    if (operationRequestValueOnly == null) {
      throw new ApiException(400, "Missing the required parameter 'operationRequestValueOnly' when calling invokeOperationAsyncValueOnly");
    }

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/submodels/{submodelIdentifier}/submodel-elements/{idShortPath}/invoke-async/$value"
        .replace("{submodelIdentifier}", ApiClient.urlEncode(submodelIdentifier.toString()))
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
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param operationRequest Operation request object (required)
   * @return OperationResult
   * @throws ApiException if fails to make API call
   */
  public OperationResult invokeOperationSync(String submodelIdentifier, String idShortPath, OperationRequest operationRequest) throws ApiException {

    ApiResponse<OperationResult> localVarResponse = invokeOperationSyncWithHttpInfo(submodelIdentifier, idShortPath, operationRequest);
    return localVarResponse.getData();
  }

  /**
   * Synchronously invokes an Operation at a specified path
   * 
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param operationRequest Operation request object (required)
   * @return ApiResponse&lt;OperationResult&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<OperationResult> invokeOperationSyncWithHttpInfo(String submodelIdentifier, String idShortPath, OperationRequest operationRequest) throws ApiException {
    String  submodelIdentifierAsBytes = ApiClient.base64UrlEncode(submodelIdentifier);
  	return invokeOperationSyncWithHttpInfoNoUrlEncoding(submodelIdentifierAsBytes, idShortPath, operationRequest);
 	
 }


  /**
   * Synchronously invokes an Operation at a specified path
   * 
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param operationRequest Operation request object (required)
   * @return ApiResponse&lt;OperationResult&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<OperationResult> invokeOperationSyncWithHttpInfoNoUrlEncoding(String submodelIdentifier, String idShortPath, OperationRequest operationRequest) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = invokeOperationSyncRequestBuilder(submodelIdentifier, idShortPath, operationRequest);
    try {
      HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(
          localVarRequestBuilder.build(),
          HttpResponse.BodyHandlers.ofInputStream());
      if (memberVarResponseInterceptor != null) {
        memberVarResponseInterceptor.accept(localVarResponse);
      }
      try {
        if (localVarResponse.statusCode()/ 100 != 2) {
          throw getApiException("invokeOperationSync", localVarResponse);
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

  private HttpRequest.Builder invokeOperationSyncRequestBuilder(String submodelIdentifier, String idShortPath, OperationRequest operationRequest) throws ApiException {
    // verify the required parameter 'submodelIdentifier' is set
    if (submodelIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'submodelIdentifier' when calling invokeOperationSync");
    }
    // verify the required parameter 'idShortPath' is set
    if (idShortPath == null) {
      throw new ApiException(400, "Missing the required parameter 'idShortPath' when calling invokeOperationSync");
    }
    // verify the required parameter 'operationRequest' is set
    if (operationRequest == null) {
      throw new ApiException(400, "Missing the required parameter 'operationRequest' when calling invokeOperationSync");
    }

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/submodels/{submodelIdentifier}/submodel-elements/{idShortPath}/invoke"
        .replace("{submodelIdentifier}", ApiClient.urlEncode(submodelIdentifier.toString()))
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
   * Synchronously invokes an Operation at a specified path
   * 
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param operationRequestValueOnly Operation request object (required)
   * @return OperationResultValueOnly
   * @throws ApiException if fails to make API call
   */
  public OperationResultValueOnly invokeOperationSyncValueOnly(String submodelIdentifier, String idShortPath, OperationRequestValueOnly operationRequestValueOnly) throws ApiException {

    ApiResponse<OperationResultValueOnly> localVarResponse = invokeOperationSyncValueOnlyWithHttpInfo(submodelIdentifier, idShortPath, operationRequestValueOnly);
    return localVarResponse.getData();
  }

  /**
   * Synchronously invokes an Operation at a specified path
   * 
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param operationRequestValueOnly Operation request object (required)
   * @return ApiResponse&lt;OperationResultValueOnly&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<OperationResultValueOnly> invokeOperationSyncValueOnlyWithHttpInfo(String submodelIdentifier, String idShortPath, OperationRequestValueOnly operationRequestValueOnly) throws ApiException {
    String  submodelIdentifierAsBytes = ApiClient.base64UrlEncode(submodelIdentifier);
  	return invokeOperationSyncValueOnlyWithHttpInfoNoUrlEncoding(submodelIdentifierAsBytes, idShortPath, operationRequestValueOnly);
 	
 }


  /**
   * Synchronously invokes an Operation at a specified path
   * 
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param operationRequestValueOnly Operation request object (required)
   * @return ApiResponse&lt;OperationResultValueOnly&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<OperationResultValueOnly> invokeOperationSyncValueOnlyWithHttpInfoNoUrlEncoding(String submodelIdentifier, String idShortPath, OperationRequestValueOnly operationRequestValueOnly) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = invokeOperationSyncValueOnlyRequestBuilder(submodelIdentifier, idShortPath, operationRequestValueOnly);
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

  private HttpRequest.Builder invokeOperationSyncValueOnlyRequestBuilder(String submodelIdentifier, String idShortPath, OperationRequestValueOnly operationRequestValueOnly) throws ApiException {
    // verify the required parameter 'submodelIdentifier' is set
    if (submodelIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'submodelIdentifier' when calling invokeOperationSyncValueOnly");
    }
    // verify the required parameter 'idShortPath' is set
    if (idShortPath == null) {
      throw new ApiException(400, "Missing the required parameter 'idShortPath' when calling invokeOperationSyncValueOnly");
    }
    // verify the required parameter 'operationRequestValueOnly' is set
    if (operationRequestValueOnly == null) {
      throw new ApiException(400, "Missing the required parameter 'operationRequestValueOnly' when calling invokeOperationSyncValueOnly");
    }

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/submodels/{submodelIdentifier}/submodel-elements/{idShortPath}/invoke/$value"
        .replace("{submodelIdentifier}", ApiClient.urlEncode(submodelIdentifier.toString()))
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
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodel Submodel object (required)
   * @param level Determines the structural depth of the respective resource content (optional, default to core)
   * @throws ApiException if fails to make API call
   */
  public void patchSubmodel(String submodelIdentifier, Submodel submodel, String level) throws ApiException {

    patchSubmodelWithHttpInfo(submodelIdentifier, submodel, level);
  }

  /**
   * Updates the Submodel
   * 
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodel Submodel object (required)
   * @param level Determines the structural depth of the respective resource content (optional, default to core)
   * @return ApiResponse&lt;Void&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<Void> patchSubmodelWithHttpInfo(String submodelIdentifier, Submodel submodel, String level) throws ApiException {
    String  submodelIdentifierAsBytes = ApiClient.base64UrlEncode(submodelIdentifier);
  	return patchSubmodelWithHttpInfoNoUrlEncoding(submodelIdentifierAsBytes, submodel, level);
 	
 }


  /**
   * Updates the Submodel
   * 
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodel Submodel object (required)
   * @param level Determines the structural depth of the respective resource content (optional, default to core)
   * @return ApiResponse&lt;Void&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<Void> patchSubmodelWithHttpInfoNoUrlEncoding(String submodelIdentifier, Submodel submodel, String level) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = patchSubmodelRequestBuilder(submodelIdentifier, submodel, level);
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

  private HttpRequest.Builder patchSubmodelRequestBuilder(String submodelIdentifier, Submodel submodel, String level) throws ApiException {
    // verify the required parameter 'submodelIdentifier' is set
    if (submodelIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'submodelIdentifier' when calling patchSubmodel");
    }
    // verify the required parameter 'submodel' is set
    if (submodel == null) {
      throw new ApiException(400, "Missing the required parameter 'submodel' when calling patchSubmodel");
    }

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/submodels/{submodelIdentifier}"
        .replace("{submodelIdentifier}", ApiClient.urlEncode(submodelIdentifier.toString()));

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
   * Updates an existing submodel element value at a specified path within submodel elements hierarchy
   * 
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param submodelElement The updated value of the submodel element (required)
   * @param level Determines the structural depth of the respective resource content (optional, default to deep)
   * @throws ApiException if fails to make API call
   */
  public void patchSubmodelElementValueByPath(String submodelIdentifier, String idShortPath, SubmodelElement submodelElement, String level) throws ApiException {

    patchSubmodelElementValueByPathWithHttpInfo(submodelIdentifier, idShortPath, submodelElement, level);
  }

  /**
   * Updates an existing submodel element value at a specified path within submodel elements hierarchy
   * 
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param submodelElement The updated value of the submodel element (required)
   * @param level Determines the structural depth of the respective resource content (optional, default to deep)
   * @return ApiResponse&lt;Void&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<Void> patchSubmodelElementValueByPathWithHttpInfo(String submodelIdentifier, String idShortPath, SubmodelElement submodelElement, String level) throws ApiException {
    String  submodelIdentifierAsBytes = ApiClient.base64UrlEncode(submodelIdentifier);
  	return patchSubmodelElementValueByPathWithHttpInfoNoUrlEncoding(submodelIdentifierAsBytes, idShortPath, submodelElement, level);
 	
 }


  /**
   * Updates an existing submodel element value at a specified path within submodel elements hierarchy
   * 
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param submodelElement The updated value of the submodel element (required)
   * @param level Determines the structural depth of the respective resource content (optional, default to deep)
   * @return ApiResponse&lt;Void&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<Void> patchSubmodelElementValueByPathWithHttpInfoNoUrlEncoding(String submodelIdentifier, String idShortPath, SubmodelElement submodelElement, String level) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = patchSubmodelElementValueByPathRequestBuilder(submodelIdentifier, idShortPath, submodelElement, level);
    try {
      HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(
          localVarRequestBuilder.build(),
          HttpResponse.BodyHandlers.ofInputStream());
      if (memberVarResponseInterceptor != null) {
        memberVarResponseInterceptor.accept(localVarResponse);
      }
      try {
        if (localVarResponse.statusCode()/ 100 != 2) {
          throw getApiException("patchSubmodelElementValueByPath", localVarResponse);
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

  private HttpRequest.Builder patchSubmodelElementValueByPathRequestBuilder(String submodelIdentifier, String idShortPath, SubmodelElement submodelElement, String level) throws ApiException {
    // verify the required parameter 'submodelIdentifier' is set
    if (submodelIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'submodelIdentifier' when calling patchSubmodelElementValueByPath");
    }
    // verify the required parameter 'idShortPath' is set
    if (idShortPath == null) {
      throw new ApiException(400, "Missing the required parameter 'idShortPath' when calling patchSubmodelElementValueByPath");
    }
    // verify the required parameter 'submodelElement' is set
    if (submodelElement == null) {
      throw new ApiException(400, "Missing the required parameter 'submodelElement' when calling patchSubmodelElementValueByPath");
    }

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/submodels/{submodelIdentifier}/submodel-elements/{idShortPath}"
        .replace("{submodelIdentifier}", ApiClient.urlEncode(submodelIdentifier.toString()))
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
   * Updates the metadata attributes of an existing submodel element value at a specified path within submodel elements hierarchy
   * 
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param submodelElementMetadata The updated metadata attributes of the submodel element (required)
   * @param level Determines the structural depth of the respective resource content (optional, default to deep)
   * @throws ApiException if fails to make API call
   */
  public void patchSubmodelElementValueByPathMetadata(String submodelIdentifier, String idShortPath, SubmodelElementMetadata submodelElementMetadata, String level) throws ApiException {

    patchSubmodelElementValueByPathMetadataWithHttpInfo(submodelIdentifier, idShortPath, submodelElementMetadata, level);
  }

  /**
   * Updates the metadata attributes of an existing submodel element value at a specified path within submodel elements hierarchy
   * 
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param submodelElementMetadata The updated metadata attributes of the submodel element (required)
   * @param level Determines the structural depth of the respective resource content (optional, default to deep)
   * @return ApiResponse&lt;Void&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<Void> patchSubmodelElementValueByPathMetadataWithHttpInfo(String submodelIdentifier, String idShortPath, SubmodelElementMetadata submodelElementMetadata, String level) throws ApiException {
    String  submodelIdentifierAsBytes = ApiClient.base64UrlEncode(submodelIdentifier);
  	return patchSubmodelElementValueByPathMetadataWithHttpInfoNoUrlEncoding(submodelIdentifierAsBytes, idShortPath, submodelElementMetadata, level);
 	
 }


  /**
   * Updates the metadata attributes of an existing submodel element value at a specified path within submodel elements hierarchy
   * 
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param submodelElementMetadata The updated metadata attributes of the submodel element (required)
   * @param level Determines the structural depth of the respective resource content (optional, default to deep)
   * @return ApiResponse&lt;Void&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<Void> patchSubmodelElementValueByPathMetadataWithHttpInfoNoUrlEncoding(String submodelIdentifier, String idShortPath, SubmodelElementMetadata submodelElementMetadata, String level) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = patchSubmodelElementValueByPathMetadataRequestBuilder(submodelIdentifier, idShortPath, submodelElementMetadata, level);
    try {
      HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(
          localVarRequestBuilder.build(),
          HttpResponse.BodyHandlers.ofInputStream());
      if (memberVarResponseInterceptor != null) {
        memberVarResponseInterceptor.accept(localVarResponse);
      }
      try {
        if (localVarResponse.statusCode()/ 100 != 2) {
          throw getApiException("patchSubmodelElementValueByPathMetadata", localVarResponse);
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

  private HttpRequest.Builder patchSubmodelElementValueByPathMetadataRequestBuilder(String submodelIdentifier, String idShortPath, SubmodelElementMetadata submodelElementMetadata, String level) throws ApiException {
    // verify the required parameter 'submodelIdentifier' is set
    if (submodelIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'submodelIdentifier' when calling patchSubmodelElementValueByPathMetadata");
    }
    // verify the required parameter 'idShortPath' is set
    if (idShortPath == null) {
      throw new ApiException(400, "Missing the required parameter 'idShortPath' when calling patchSubmodelElementValueByPathMetadata");
    }
    // verify the required parameter 'submodelElementMetadata' is set
    if (submodelElementMetadata == null) {
      throw new ApiException(400, "Missing the required parameter 'submodelElementMetadata' when calling patchSubmodelElementValueByPathMetadata");
    }

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/submodels/{submodelIdentifier}/submodel-elements/{idShortPath}/$metadata"
        .replace("{submodelIdentifier}", ApiClient.urlEncode(submodelIdentifier.toString()))
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
      byte[] localVarPostBody = memberVarObjectMapper.writeValueAsBytes(submodelElementMetadata);
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
   * Updates the value of an existing submodel element value at a specified path within submodel elements hierarchy
   * 
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param submodelElementValue The updated value of the submodel element (required)
   * @param level Determines the structural depth of the respective resource content (optional, default to deep)
   * @throws ApiException if fails to make API call
   */
  public void patchSubmodelElementValueByPathValueOnly(String submodelIdentifier, String idShortPath, SubmodelElementValue submodelElementValue, String level) throws ApiException {

    patchSubmodelElementValueByPathValueOnlyWithHttpInfo(submodelIdentifier, idShortPath, submodelElementValue, level);
  }

  /**
   * Updates the value of an existing submodel element value at a specified path within submodel elements hierarchy
   * 
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param submodelElementValue The updated value of the submodel element (required)
   * @param level Determines the structural depth of the respective resource content (optional, default to deep)
   * @return ApiResponse&lt;Void&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<Void> patchSubmodelElementValueByPathValueOnlyWithHttpInfo(String submodelIdentifier, String idShortPath, SubmodelElementValue submodelElementValue, String level) throws ApiException {
    String  submodelIdentifierAsBytes = ApiClient.base64UrlEncode(submodelIdentifier);
  	return patchSubmodelElementValueByPathValueOnlyWithHttpInfoNoUrlEncoding(submodelIdentifierAsBytes, idShortPath, submodelElementValue, level);
 	
 }


  /**
   * Updates the value of an existing submodel element value at a specified path within submodel elements hierarchy
   * 
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param submodelElementValue The updated value of the submodel element (required)
   * @param level Determines the structural depth of the respective resource content (optional, default to deep)
   * @return ApiResponse&lt;Void&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<Void> patchSubmodelElementValueByPathValueOnlyWithHttpInfoNoUrlEncoding(String submodelIdentifier, String idShortPath, SubmodelElementValue submodelElementValue, String level) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = patchSubmodelElementValueByPathValueOnlyRequestBuilder(submodelIdentifier, idShortPath, submodelElementValue, level);
    try {
      HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(
          localVarRequestBuilder.build(),
          HttpResponse.BodyHandlers.ofInputStream());
      if (memberVarResponseInterceptor != null) {
        memberVarResponseInterceptor.accept(localVarResponse);
      }
      try {
        if (localVarResponse.statusCode()/ 100 != 2) {
          throw getApiException("patchSubmodelElementValueByPathValueOnly", localVarResponse);
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

  private HttpRequest.Builder patchSubmodelElementValueByPathValueOnlyRequestBuilder(String submodelIdentifier, String idShortPath, SubmodelElementValue submodelElementValue, String level) throws ApiException {
    // verify the required parameter 'submodelIdentifier' is set
    if (submodelIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'submodelIdentifier' when calling patchSubmodelElementValueByPathValueOnly");
    }
    // verify the required parameter 'idShortPath' is set
    if (idShortPath == null) {
      throw new ApiException(400, "Missing the required parameter 'idShortPath' when calling patchSubmodelElementValueByPathValueOnly");
    }
    // verify the required parameter 'submodelElementValue' is set
    if (submodelElementValue == null) {
      throw new ApiException(400, "Missing the required parameter 'submodelElementValue' when calling patchSubmodelElementValueByPathValueOnly");
    }

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/submodels/{submodelIdentifier}/submodel-elements/{idShortPath}/$value"
        .replace("{submodelIdentifier}", ApiClient.urlEncode(submodelIdentifier.toString()))
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
      byte[] localVarPostBody = memberVarObjectMapper.writeValueAsBytes(submodelElementValue);
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
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelMetadata Submodel object (required)
   * @param level Determines the structural depth of the respective resource content (optional, default to core)
   * @throws ApiException if fails to make API call
   */
  public void patchSubmodelMetadata(String submodelIdentifier, SubmodelMetadata submodelMetadata, String level) throws ApiException {

    patchSubmodelMetadataWithHttpInfo(submodelIdentifier, submodelMetadata, level);
  }

  /**
   * Updates the metadata attributes of the Submodel
   * 
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelMetadata Submodel object (required)
   * @param level Determines the structural depth of the respective resource content (optional, default to core)
   * @return ApiResponse&lt;Void&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<Void> patchSubmodelMetadataWithHttpInfo(String submodelIdentifier, SubmodelMetadata submodelMetadata, String level) throws ApiException {
    String  submodelIdentifierAsBytes = ApiClient.base64UrlEncode(submodelIdentifier);
  	return patchSubmodelMetadataWithHttpInfoNoUrlEncoding(submodelIdentifierAsBytes, submodelMetadata, level);
 	
 }


  /**
   * Updates the metadata attributes of the Submodel
   * 
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelMetadata Submodel object (required)
   * @param level Determines the structural depth of the respective resource content (optional, default to core)
   * @return ApiResponse&lt;Void&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<Void> patchSubmodelMetadataWithHttpInfoNoUrlEncoding(String submodelIdentifier, SubmodelMetadata submodelMetadata, String level) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = patchSubmodelMetadataRequestBuilder(submodelIdentifier, submodelMetadata, level);
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

  private HttpRequest.Builder patchSubmodelMetadataRequestBuilder(String submodelIdentifier, SubmodelMetadata submodelMetadata, String level) throws ApiException {
    // verify the required parameter 'submodelIdentifier' is set
    if (submodelIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'submodelIdentifier' when calling patchSubmodelMetadata");
    }
    // verify the required parameter 'submodelMetadata' is set
    if (submodelMetadata == null) {
      throw new ApiException(400, "Missing the required parameter 'submodelMetadata' when calling patchSubmodelMetadata");
    }

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/submodels/{submodelIdentifier}/$metadata"
        .replace("{submodelIdentifier}", ApiClient.urlEncode(submodelIdentifier.toString()));

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
   * Updates teh values of the Submodel
   * 
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelValue Submodel object in the ValueOnly representation (required)
   * @param level Determines the structural depth of the respective resource content (optional, default to core)
   * @throws ApiException if fails to make API call
   */
  public void patchSubmodelValueOnly(String submodelIdentifier, SubmodelValue submodelValue, String level) throws ApiException {

    patchSubmodelValueOnlyWithHttpInfo(submodelIdentifier, submodelValue, level);
  }

  /**
   * Updates teh values of the Submodel
   * 
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelValue Submodel object in the ValueOnly representation (required)
   * @param level Determines the structural depth of the respective resource content (optional, default to core)
   * @return ApiResponse&lt;Void&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<Void> patchSubmodelValueOnlyWithHttpInfo(String submodelIdentifier, SubmodelValue submodelValue, String level) throws ApiException {
    String  submodelIdentifierAsBytes = ApiClient.base64UrlEncode(submodelIdentifier);
  	return patchSubmodelValueOnlyWithHttpInfoNoUrlEncoding(submodelIdentifierAsBytes, submodelValue, level);
 	
 }


  /**
   * Updates teh values of the Submodel
   * 
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelValue Submodel object in the ValueOnly representation (required)
   * @param level Determines the structural depth of the respective resource content (optional, default to core)
   * @return ApiResponse&lt;Void&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<Void> patchSubmodelValueOnlyWithHttpInfoNoUrlEncoding(String submodelIdentifier, SubmodelValue submodelValue, String level) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = patchSubmodelValueOnlyRequestBuilder(submodelIdentifier, submodelValue, level);
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

  private HttpRequest.Builder patchSubmodelValueOnlyRequestBuilder(String submodelIdentifier, SubmodelValue submodelValue, String level) throws ApiException {
    // verify the required parameter 'submodelIdentifier' is set
    if (submodelIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'submodelIdentifier' when calling patchSubmodelValueOnly");
    }
    // verify the required parameter 'submodelValue' is set
    if (submodelValue == null) {
      throw new ApiException(400, "Missing the required parameter 'submodelValue' when calling patchSubmodelValueOnly");
    }

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/submodels/{submodelIdentifier}/$value"
        .replace("{submodelIdentifier}", ApiClient.urlEncode(submodelIdentifier.toString()));

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
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelElement Requested submodel element (required)
   * @return SubmodelElement
   * @throws ApiException if fails to make API call
   */
  public SubmodelElement postSubmodelElement(String submodelIdentifier, SubmodelElement submodelElement) throws ApiException {

    ApiResponse<SubmodelElement> localVarResponse = postSubmodelElementWithHttpInfo(submodelIdentifier, submodelElement);
    return localVarResponse.getData();
  }

  /**
   * Creates a new submodel element
   * 
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelElement Requested submodel element (required)
   * @return ApiResponse&lt;SubmodelElement&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<SubmodelElement> postSubmodelElementWithHttpInfo(String submodelIdentifier, SubmodelElement submodelElement) throws ApiException {
    String  submodelIdentifierAsBytes = ApiClient.base64UrlEncode(submodelIdentifier);
  	return postSubmodelElementWithHttpInfoNoUrlEncoding(submodelIdentifierAsBytes, submodelElement);
 	
 }


  /**
   * Creates a new submodel element
   * 
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelElement Requested submodel element (required)
   * @return ApiResponse&lt;SubmodelElement&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<SubmodelElement> postSubmodelElementWithHttpInfoNoUrlEncoding(String submodelIdentifier, SubmodelElement submodelElement) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = postSubmodelElementRequestBuilder(submodelIdentifier, submodelElement);
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

  private HttpRequest.Builder postSubmodelElementRequestBuilder(String submodelIdentifier, SubmodelElement submodelElement) throws ApiException {
    // verify the required parameter 'submodelIdentifier' is set
    if (submodelIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'submodelIdentifier' when calling postSubmodelElement");
    }
    // verify the required parameter 'submodelElement' is set
    if (submodelElement == null) {
      throw new ApiException(400, "Missing the required parameter 'submodelElement' when calling postSubmodelElement");
    }

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/submodels/{submodelIdentifier}/submodel-elements"
        .replace("{submodelIdentifier}", ApiClient.urlEncode(submodelIdentifier.toString()));

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
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param submodelElement Requested submodel element (required)
   * @return SubmodelElement
   * @throws ApiException if fails to make API call
   */
  public SubmodelElement postSubmodelElementByPath(String submodelIdentifier, String idShortPath, SubmodelElement submodelElement) throws ApiException {

    ApiResponse<SubmodelElement> localVarResponse = postSubmodelElementByPathWithHttpInfo(submodelIdentifier, idShortPath, submodelElement);
    return localVarResponse.getData();
  }

  /**
   * Creates a new submodel element at a specified path within submodel elements hierarchy
   * 
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param submodelElement Requested submodel element (required)
   * @return ApiResponse&lt;SubmodelElement&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<SubmodelElement> postSubmodelElementByPathWithHttpInfo(String submodelIdentifier, String idShortPath, SubmodelElement submodelElement) throws ApiException {
    String  submodelIdentifierAsBytes = ApiClient.base64UrlEncode(submodelIdentifier);
  	return postSubmodelElementByPathWithHttpInfoNoUrlEncoding(submodelIdentifierAsBytes, idShortPath, submodelElement);
 	
 }


  /**
   * Creates a new submodel element at a specified path within submodel elements hierarchy
   * 
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param submodelElement Requested submodel element (required)
   * @return ApiResponse&lt;SubmodelElement&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<SubmodelElement> postSubmodelElementByPathWithHttpInfoNoUrlEncoding(String submodelIdentifier, String idShortPath, SubmodelElement submodelElement) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = postSubmodelElementByPathRequestBuilder(submodelIdentifier, idShortPath, submodelElement);
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

  private HttpRequest.Builder postSubmodelElementByPathRequestBuilder(String submodelIdentifier, String idShortPath, SubmodelElement submodelElement) throws ApiException {
    // verify the required parameter 'submodelIdentifier' is set
    if (submodelIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'submodelIdentifier' when calling postSubmodelElementByPath");
    }
    // verify the required parameter 'idShortPath' is set
    if (idShortPath == null) {
      throw new ApiException(400, "Missing the required parameter 'idShortPath' when calling postSubmodelElementByPath");
    }
    // verify the required parameter 'submodelElement' is set
    if (submodelElement == null) {
      throw new ApiException(400, "Missing the required parameter 'submodelElement' when calling postSubmodelElementByPath");
    }

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/submodels/{submodelIdentifier}/submodel-elements/{idShortPath}"
        .replace("{submodelIdentifier}", ApiClient.urlEncode(submodelIdentifier.toString()))
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
   * Creates a submodel reference at the Asset Administration Shell
   * 
   * @param reference Reference to the Submodel (required)
   * @return Reference
   * @throws ApiException if fails to make API call
   */
  public Reference postSubmodelReference(Reference reference) throws ApiException {

    ApiResponse<Reference> localVarResponse = postSubmodelReferenceWithHttpInfo(reference);
    return localVarResponse.getData();
  }

  /**
   * Creates a submodel reference at the Asset Administration Shell
   * 
   * @param reference Reference to the Submodel (required)
   * @return ApiResponse&lt;Reference&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<Reference> postSubmodelReferenceWithHttpInfo(Reference reference) throws ApiException {
  	return postSubmodelReferenceWithHttpInfoNoUrlEncoding(reference);
 	
 }


  /**
   * Creates a submodel reference at the Asset Administration Shell
   * 
   * @param reference Reference to the Submodel (required)
   * @return ApiResponse&lt;Reference&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<Reference> postSubmodelReferenceWithHttpInfoNoUrlEncoding(Reference reference) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = postSubmodelReferenceRequestBuilder(reference);
    try {
      HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(
          localVarRequestBuilder.build(),
          HttpResponse.BodyHandlers.ofInputStream());
      if (memberVarResponseInterceptor != null) {
        memberVarResponseInterceptor.accept(localVarResponse);
      }
      try {
        if (localVarResponse.statusCode()/ 100 != 2) {
          throw getApiException("postSubmodelReference", localVarResponse);
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

  private HttpRequest.Builder postSubmodelReferenceRequestBuilder(Reference reference) throws ApiException {
    // verify the required parameter 'reference' is set
    if (reference == null) {
      throw new ApiException(400, "Missing the required parameter 'reference' when calling postSubmodelReference");
    }

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/submodel-refs";

    localVarRequestBuilder.uri(URI.create(memberVarBaseUri + localVarPath));

    localVarRequestBuilder.header("Content-Type", "application/json");
    localVarRequestBuilder.header("Accept", "application/json");

    try {
      byte[] localVarPostBody = memberVarObjectMapper.writeValueAsBytes(reference);
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
   * Updates an existing Asset Administration Shell
   * 
   * @param assetAdministrationShell Asset Administration Shell object (required)
   * @throws ApiException if fails to make API call
   */
  public void putAssetAdministrationShell(AssetAdministrationShell assetAdministrationShell) throws ApiException {

    putAssetAdministrationShellWithHttpInfo(assetAdministrationShell);
  }

  /**
   * Updates an existing Asset Administration Shell
   * 
   * @param assetAdministrationShell Asset Administration Shell object (required)
   * @return ApiResponse&lt;Void&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<Void> putAssetAdministrationShellWithHttpInfo(AssetAdministrationShell assetAdministrationShell) throws ApiException {
  	return putAssetAdministrationShellWithHttpInfoNoUrlEncoding(assetAdministrationShell);
 	
 }


  /**
   * Updates an existing Asset Administration Shell
   * 
   * @param assetAdministrationShell Asset Administration Shell object (required)
   * @return ApiResponse&lt;Void&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<Void> putAssetAdministrationShellWithHttpInfoNoUrlEncoding(AssetAdministrationShell assetAdministrationShell) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = putAssetAdministrationShellRequestBuilder(assetAdministrationShell);
    try {
      HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(
          localVarRequestBuilder.build(),
          HttpResponse.BodyHandlers.ofInputStream());
      if (memberVarResponseInterceptor != null) {
        memberVarResponseInterceptor.accept(localVarResponse);
      }
      try {
        if (localVarResponse.statusCode()/ 100 != 2) {
          throw getApiException("putAssetAdministrationShell", localVarResponse);
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

  private HttpRequest.Builder putAssetAdministrationShellRequestBuilder(AssetAdministrationShell assetAdministrationShell) throws ApiException {
    // verify the required parameter 'assetAdministrationShell' is set
    if (assetAdministrationShell == null) {
      throw new ApiException(400, "Missing the required parameter 'assetAdministrationShell' when calling putAssetAdministrationShell");
    }

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/";

    localVarRequestBuilder.uri(URI.create(memberVarBaseUri + localVarPath));

    localVarRequestBuilder.header("Content-Type", "application/json");
    localVarRequestBuilder.header("Accept", "application/json");

    try {
      byte[] localVarPostBody = memberVarObjectMapper.writeValueAsBytes(assetAdministrationShell);
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
   * Updates the Asset Information
   * 
   * @param assetInformation Asset Information object (required)
   * @throws ApiException if fails to make API call
   */
  public void putAssetInformation(AssetInformation assetInformation) throws ApiException {

    putAssetInformationWithHttpInfo(assetInformation);
  }

  /**
   * Updates the Asset Information
   * 
   * @param assetInformation Asset Information object (required)
   * @return ApiResponse&lt;Void&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<Void> putAssetInformationWithHttpInfo(AssetInformation assetInformation) throws ApiException {
  	return putAssetInformationWithHttpInfoNoUrlEncoding(assetInformation);
 	
 }


  /**
   * Updates the Asset Information
   * 
   * @param assetInformation Asset Information object (required)
   * @return ApiResponse&lt;Void&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<Void> putAssetInformationWithHttpInfoNoUrlEncoding(AssetInformation assetInformation) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = putAssetInformationRequestBuilder(assetInformation);
    try {
      HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(
          localVarRequestBuilder.build(),
          HttpResponse.BodyHandlers.ofInputStream());
      if (memberVarResponseInterceptor != null) {
        memberVarResponseInterceptor.accept(localVarResponse);
      }
      try {
        if (localVarResponse.statusCode()/ 100 != 2) {
          throw getApiException("putAssetInformation", localVarResponse);
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

  private HttpRequest.Builder putAssetInformationRequestBuilder(AssetInformation assetInformation) throws ApiException {
    // verify the required parameter 'assetInformation' is set
    if (assetInformation == null) {
      throw new ApiException(400, "Missing the required parameter 'assetInformation' when calling putAssetInformation");
    }

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/assetinformation";

    localVarRequestBuilder.uri(URI.create(memberVarBaseUri + localVarPath));

    localVarRequestBuilder.header("Content-Type", "application/json");
    localVarRequestBuilder.header("Accept", "application/json");

    try {
      byte[] localVarPostBody = memberVarObjectMapper.writeValueAsBytes(assetInformation);
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
   * Uploads file content to an existing submodel element at a specified path within submodel elements hierarchy
   * 
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param fileName  (optional)
   * @param _file  (optional)
   * @throws ApiException if fails to make API call
   */
  public void putFileByPath(String submodelIdentifier, String idShortPath, String fileName, File _file) throws ApiException {

    putFileByPathWithHttpInfo(submodelIdentifier, idShortPath, fileName, _file);
  }

  /**
   * Uploads file content to an existing submodel element at a specified path within submodel elements hierarchy
   * 
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param fileName  (optional)
   * @param _file  (optional)
   * @return ApiResponse&lt;Void&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<Void> putFileByPathWithHttpInfo(String submodelIdentifier, String idShortPath, String fileName, File _file) throws ApiException {
    String  submodelIdentifierAsBytes = ApiClient.base64UrlEncode(submodelIdentifier);
  	return putFileByPathWithHttpInfoNoUrlEncoding(submodelIdentifierAsBytes, idShortPath, fileName, _file);
 	
 }


  /**
   * Uploads file content to an existing submodel element at a specified path within submodel elements hierarchy
   * 
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param fileName  (optional)
   * @param _file  (optional)
   * @return ApiResponse&lt;Void&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<Void> putFileByPathWithHttpInfoNoUrlEncoding(String submodelIdentifier, String idShortPath, String fileName, File _file) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = putFileByPathRequestBuilder(submodelIdentifier, idShortPath, fileName, _file);
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

  private HttpRequest.Builder putFileByPathRequestBuilder(String submodelIdentifier, String idShortPath, String fileName, File _file) throws ApiException {
    // verify the required parameter 'submodelIdentifier' is set
    if (submodelIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'submodelIdentifier' when calling putFileByPath");
    }
    // verify the required parameter 'idShortPath' is set
    if (idShortPath == null) {
      throw new ApiException(400, "Missing the required parameter 'idShortPath' when calling putFileByPath");
    }

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/submodels/{submodelIdentifier}/submodel-elements/{idShortPath}/attachment"
        .replace("{submodelIdentifier}", ApiClient.urlEncode(submodelIdentifier.toString()))
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
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodel Submodel object (required)
   * @param level Determines the structural depth of the respective resource content (optional, default to deep)
   * @throws ApiException if fails to make API call
   */
  public void putSubmodel(String submodelIdentifier, Submodel submodel, String level) throws ApiException {

    putSubmodelWithHttpInfo(submodelIdentifier, submodel, level);
  }

  /**
   * Updates the Submodel
   * 
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodel Submodel object (required)
   * @param level Determines the structural depth of the respective resource content (optional, default to deep)
   * @return ApiResponse&lt;Void&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<Void> putSubmodelWithHttpInfo(String submodelIdentifier, Submodel submodel, String level) throws ApiException {
    String  submodelIdentifierAsBytes = ApiClient.base64UrlEncode(submodelIdentifier);
  	return putSubmodelWithHttpInfoNoUrlEncoding(submodelIdentifierAsBytes, submodel, level);
 	
 }


  /**
   * Updates the Submodel
   * 
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodel Submodel object (required)
   * @param level Determines the structural depth of the respective resource content (optional, default to deep)
   * @return ApiResponse&lt;Void&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<Void> putSubmodelWithHttpInfoNoUrlEncoding(String submodelIdentifier, Submodel submodel, String level) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = putSubmodelRequestBuilder(submodelIdentifier, submodel, level);
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

  private HttpRequest.Builder putSubmodelRequestBuilder(String submodelIdentifier, Submodel submodel, String level) throws ApiException {
    // verify the required parameter 'submodelIdentifier' is set
    if (submodelIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'submodelIdentifier' when calling putSubmodel");
    }
    // verify the required parameter 'submodel' is set
    if (submodel == null) {
      throw new ApiException(400, "Missing the required parameter 'submodel' when calling putSubmodel");
    }

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/submodels/{submodelIdentifier}"
        .replace("{submodelIdentifier}", ApiClient.urlEncode(submodelIdentifier.toString()));

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
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param submodelElement Requested submodel element (required)
   * @throws ApiException if fails to make API call
   */
  public void putSubmodelElementByPath(String submodelIdentifier, String idShortPath, SubmodelElement submodelElement) throws ApiException {

    putSubmodelElementByPathWithHttpInfo(submodelIdentifier, idShortPath, submodelElement);
  }

  /**
   * Updates an existing submodel element at a specified path within submodel elements hierarchy
   * 
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param submodelElement Requested submodel element (required)
   * @return ApiResponse&lt;Void&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<Void> putSubmodelElementByPathWithHttpInfo(String submodelIdentifier, String idShortPath, SubmodelElement submodelElement) throws ApiException {
    String  submodelIdentifierAsBytes = ApiClient.base64UrlEncode(submodelIdentifier);
  	return putSubmodelElementByPathWithHttpInfoNoUrlEncoding(submodelIdentifierAsBytes, idShortPath, submodelElement);
 	
 }


  /**
   * Updates an existing submodel element at a specified path within submodel elements hierarchy
   * 
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param submodelElement Requested submodel element (required)
   * @return ApiResponse&lt;Void&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<Void> putSubmodelElementByPathWithHttpInfoNoUrlEncoding(String submodelIdentifier, String idShortPath, SubmodelElement submodelElement) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = putSubmodelElementByPathRequestBuilder(submodelIdentifier, idShortPath, submodelElement);
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

  private HttpRequest.Builder putSubmodelElementByPathRequestBuilder(String submodelIdentifier, String idShortPath, SubmodelElement submodelElement) throws ApiException {
    // verify the required parameter 'submodelIdentifier' is set
    if (submodelIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'submodelIdentifier' when calling putSubmodelElementByPath");
    }
    // verify the required parameter 'idShortPath' is set
    if (idShortPath == null) {
      throw new ApiException(400, "Missing the required parameter 'idShortPath' when calling putSubmodelElementByPath");
    }
    // verify the required parameter 'submodelElement' is set
    if (submodelElement == null) {
      throw new ApiException(400, "Missing the required parameter 'submodelElement' when calling putSubmodelElementByPath");
    }

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/submodels/{submodelIdentifier}/submodel-elements/{idShortPath}"
        .replace("{submodelIdentifier}", ApiClient.urlEncode(submodelIdentifier.toString()))
        .replace("{idShortPath}", ApiClient.urlEncode(idShortPath.toString()));

    localVarRequestBuilder.uri(URI.create(memberVarBaseUri + localVarPath));

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
  /**
   * 
   * 
   * @param fileName  (optional)
   * @param _file  (optional)
   * @throws ApiException if fails to make API call
   */
  public void putThumbnail(String fileName, File _file) throws ApiException {

    putThumbnailWithHttpInfo(fileName, _file);
  }

  /**
   * 
   * 
   * @param fileName  (optional)
   * @param _file  (optional)
   * @return ApiResponse&lt;Void&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<Void> putThumbnailWithHttpInfo(String fileName, File _file) throws ApiException {
  	return putThumbnailWithHttpInfoNoUrlEncoding(fileName, _file);
 	
 }


  /**
   * 
   * 
   * @param fileName  (optional)
   * @param _file  (optional)
   * @return ApiResponse&lt;Void&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<Void> putThumbnailWithHttpInfoNoUrlEncoding(String fileName, File _file) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = putThumbnailRequestBuilder(fileName, _file);
    try {
      HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(
          localVarRequestBuilder.build(),
          HttpResponse.BodyHandlers.ofInputStream());
      if (memberVarResponseInterceptor != null) {
        memberVarResponseInterceptor.accept(localVarResponse);
      }
      try {
        if (localVarResponse.statusCode()/ 100 != 2) {
          throw getApiException("putThumbnail", localVarResponse);
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

  private HttpRequest.Builder putThumbnailRequestBuilder(String fileName, File _file) throws ApiException {

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/assetinformation/thumbnail";

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
  
}

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
import org.eclipse.digitaltwin.basyx.v3.clients.model.part2.GetAssetAdministrationShellsResult;
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

@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen", date = "2024-01-26T15:40:42.933294+01:00[Europe/Berlin]")
public class AssetAdministrationShellRepositoryApi {
  private final HttpClient memberVarHttpClient;
  private final ObjectMapper memberVarObjectMapper;
  private final String memberVarBaseUri;
  private final Consumer<HttpRequest.Builder> memberVarInterceptor;
  private final Duration memberVarReadTimeout;
  private final Consumer<HttpResponse<InputStream>> memberVarResponseInterceptor;
  private final Consumer<HttpResponse<String>> memberVarAsyncResponseInterceptor;

  public AssetAdministrationShellRepositoryApi() {
    this(new ApiClient());
  }

  public AssetAdministrationShellRepositoryApi(ObjectMapper mapper, String baseUri) {
    this(new ApiClient(HttpClient.newBuilder(), mapper, baseUri));
  }
  
  public AssetAdministrationShellRepositoryApi(String baseUri) {
    this(new ApiClient(HttpClient.newBuilder(), JSON.getDefault().getMapper(), baseUri));
  }


  public AssetAdministrationShellRepositoryApi(ApiClient apiClient) {
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
   * Deletes an Asset Administration Shell
   * 
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @throws ApiException if fails to make API call
   */
  public void deleteAssetAdministrationShellById(String aasIdentifier) throws ApiException {

    deleteAssetAdministrationShellByIdWithHttpInfo(aasIdentifier);
  }

  /**
   * Deletes an Asset Administration Shell
   * 
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @return ApiResponse&lt;Void&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<Void> deleteAssetAdministrationShellByIdWithHttpInfo(String aasIdentifier) throws ApiException {
    String  aasIdentifierAsBytes = ApiClient.base64UrlEncode(aasIdentifier);
  	return deleteAssetAdministrationShellByIdWithHttpInfoNoUrlEncoding(aasIdentifierAsBytes);
 	
 }


  /**
   * Deletes an Asset Administration Shell
   * 
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @return ApiResponse&lt;Void&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<Void> deleteAssetAdministrationShellByIdWithHttpInfoNoUrlEncoding(String aasIdentifier) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = deleteAssetAdministrationShellByIdRequestBuilder(aasIdentifier);
    try {
      HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(
          localVarRequestBuilder.build(),
          HttpResponse.BodyHandlers.ofInputStream());
      if (memberVarResponseInterceptor != null) {
        memberVarResponseInterceptor.accept(localVarResponse);
      }
      try {
        if (localVarResponse.statusCode()/ 100 != 2) {
          throw getApiException("deleteAssetAdministrationShellById", localVarResponse);
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

  private HttpRequest.Builder deleteAssetAdministrationShellByIdRequestBuilder(String aasIdentifier) throws ApiException {
    // verify the required parameter 'aasIdentifier' is set
    if (aasIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'aasIdentifier' when calling deleteAssetAdministrationShellById");
    }

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/shells/{aasIdentifier}"
        .replace("{aasIdentifier}", ApiClient.urlEncode(aasIdentifier.toString()));

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
   * Deletes file content of an existing submodel element at a specified path within submodel elements hierarchy
   * 
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @throws ApiException if fails to make API call
   */
  public void deleteFileByPathAasRepository(String aasIdentifier, String submodelIdentifier, String idShortPath) throws ApiException {

    deleteFileByPathAasRepositoryWithHttpInfo(aasIdentifier, submodelIdentifier, idShortPath);
  }

  /**
   * Deletes file content of an existing submodel element at a specified path within submodel elements hierarchy
   * 
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @return ApiResponse&lt;Void&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<Void> deleteFileByPathAasRepositoryWithHttpInfo(String aasIdentifier, String submodelIdentifier, String idShortPath) throws ApiException {
    String  aasIdentifierAsBytes = ApiClient.base64UrlEncode(aasIdentifier);
    String  submodelIdentifierAsBytes = ApiClient.base64UrlEncode(submodelIdentifier);
  	return deleteFileByPathAasRepositoryWithHttpInfoNoUrlEncoding(aasIdentifierAsBytes, submodelIdentifierAsBytes, idShortPath);
 	
 }


  /**
   * Deletes file content of an existing submodel element at a specified path within submodel elements hierarchy
   * 
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @return ApiResponse&lt;Void&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<Void> deleteFileByPathAasRepositoryWithHttpInfoNoUrlEncoding(String aasIdentifier, String submodelIdentifier, String idShortPath) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = deleteFileByPathAasRepositoryRequestBuilder(aasIdentifier, submodelIdentifier, idShortPath);
    try {
      HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(
          localVarRequestBuilder.build(),
          HttpResponse.BodyHandlers.ofInputStream());
      if (memberVarResponseInterceptor != null) {
        memberVarResponseInterceptor.accept(localVarResponse);
      }
      try {
        if (localVarResponse.statusCode()/ 100 != 2) {
          throw getApiException("deleteFileByPathAasRepository", localVarResponse);
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

  private HttpRequest.Builder deleteFileByPathAasRepositoryRequestBuilder(String aasIdentifier, String submodelIdentifier, String idShortPath) throws ApiException {
    // verify the required parameter 'aasIdentifier' is set
    if (aasIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'aasIdentifier' when calling deleteFileByPathAasRepository");
    }
    // verify the required parameter 'submodelIdentifier' is set
    if (submodelIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'submodelIdentifier' when calling deleteFileByPathAasRepository");
    }
    // verify the required parameter 'idShortPath' is set
    if (idShortPath == null) {
      throw new ApiException(400, "Missing the required parameter 'idShortPath' when calling deleteFileByPathAasRepository");
    }

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/shells/{aasIdentifier}/submodels/{submodelIdentifier}/submodel-elements/{idShortPath}/attachment"
        .replace("{aasIdentifier}", ApiClient.urlEncode(aasIdentifier.toString()))
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
   * Deletes the submodel from the Asset Administration Shell and the Repository.
   * 
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @throws ApiException if fails to make API call
   */
  public void deleteSubmodelByIdAasRepository(String aasIdentifier, String submodelIdentifier) throws ApiException {

    deleteSubmodelByIdAasRepositoryWithHttpInfo(aasIdentifier, submodelIdentifier);
  }

  /**
   * Deletes the submodel from the Asset Administration Shell and the Repository.
   * 
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @return ApiResponse&lt;Void&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<Void> deleteSubmodelByIdAasRepositoryWithHttpInfo(String aasIdentifier, String submodelIdentifier) throws ApiException {
    String  aasIdentifierAsBytes = ApiClient.base64UrlEncode(aasIdentifier);
    String  submodelIdentifierAsBytes = ApiClient.base64UrlEncode(submodelIdentifier);
  	return deleteSubmodelByIdAasRepositoryWithHttpInfoNoUrlEncoding(aasIdentifierAsBytes, submodelIdentifierAsBytes);
 	
 }


  /**
   * Deletes the submodel from the Asset Administration Shell and the Repository.
   * 
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @return ApiResponse&lt;Void&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<Void> deleteSubmodelByIdAasRepositoryWithHttpInfoNoUrlEncoding(String aasIdentifier, String submodelIdentifier) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = deleteSubmodelByIdAasRepositoryRequestBuilder(aasIdentifier, submodelIdentifier);
    try {
      HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(
          localVarRequestBuilder.build(),
          HttpResponse.BodyHandlers.ofInputStream());
      if (memberVarResponseInterceptor != null) {
        memberVarResponseInterceptor.accept(localVarResponse);
      }
      try {
        if (localVarResponse.statusCode()/ 100 != 2) {
          throw getApiException("deleteSubmodelByIdAasRepository", localVarResponse);
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

  private HttpRequest.Builder deleteSubmodelByIdAasRepositoryRequestBuilder(String aasIdentifier, String submodelIdentifier) throws ApiException {
    // verify the required parameter 'aasIdentifier' is set
    if (aasIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'aasIdentifier' when calling deleteSubmodelByIdAasRepository");
    }
    // verify the required parameter 'submodelIdentifier' is set
    if (submodelIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'submodelIdentifier' when calling deleteSubmodelByIdAasRepository");
    }

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/shells/{aasIdentifier}/submodels/{submodelIdentifier}"
        .replace("{aasIdentifier}", ApiClient.urlEncode(aasIdentifier.toString()))
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
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @throws ApiException if fails to make API call
   */
  public void deleteSubmodelElementByPathAasRepository(String aasIdentifier, String submodelIdentifier, String idShortPath) throws ApiException {

    deleteSubmodelElementByPathAasRepositoryWithHttpInfo(aasIdentifier, submodelIdentifier, idShortPath);
  }

  /**
   * Deletes a submodel element at a specified path within the submodel elements hierarchy
   * 
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @return ApiResponse&lt;Void&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<Void> deleteSubmodelElementByPathAasRepositoryWithHttpInfo(String aasIdentifier, String submodelIdentifier, String idShortPath) throws ApiException {
    String  aasIdentifierAsBytes = ApiClient.base64UrlEncode(aasIdentifier);
    String  submodelIdentifierAsBytes = ApiClient.base64UrlEncode(submodelIdentifier);
  	return deleteSubmodelElementByPathAasRepositoryWithHttpInfoNoUrlEncoding(aasIdentifierAsBytes, submodelIdentifierAsBytes, idShortPath);
 	
 }


  /**
   * Deletes a submodel element at a specified path within the submodel elements hierarchy
   * 
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @return ApiResponse&lt;Void&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<Void> deleteSubmodelElementByPathAasRepositoryWithHttpInfoNoUrlEncoding(String aasIdentifier, String submodelIdentifier, String idShortPath) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = deleteSubmodelElementByPathAasRepositoryRequestBuilder(aasIdentifier, submodelIdentifier, idShortPath);
    try {
      HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(
          localVarRequestBuilder.build(),
          HttpResponse.BodyHandlers.ofInputStream());
      if (memberVarResponseInterceptor != null) {
        memberVarResponseInterceptor.accept(localVarResponse);
      }
      try {
        if (localVarResponse.statusCode()/ 100 != 2) {
          throw getApiException("deleteSubmodelElementByPathAasRepository", localVarResponse);
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

  private HttpRequest.Builder deleteSubmodelElementByPathAasRepositoryRequestBuilder(String aasIdentifier, String submodelIdentifier, String idShortPath) throws ApiException {
    // verify the required parameter 'aasIdentifier' is set
    if (aasIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'aasIdentifier' when calling deleteSubmodelElementByPathAasRepository");
    }
    // verify the required parameter 'submodelIdentifier' is set
    if (submodelIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'submodelIdentifier' when calling deleteSubmodelElementByPathAasRepository");
    }
    // verify the required parameter 'idShortPath' is set
    if (idShortPath == null) {
      throw new ApiException(400, "Missing the required parameter 'idShortPath' when calling deleteSubmodelElementByPathAasRepository");
    }

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/shells/{aasIdentifier}/submodels/{submodelIdentifier}/submodel-elements/{idShortPath}"
        .replace("{aasIdentifier}", ApiClient.urlEncode(aasIdentifier.toString()))
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
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @throws ApiException if fails to make API call
   */
  public void deleteSubmodelReferenceByIdAasRepository(String aasIdentifier, String submodelIdentifier) throws ApiException {

    deleteSubmodelReferenceByIdAasRepositoryWithHttpInfo(aasIdentifier, submodelIdentifier);
  }

  /**
   * Deletes the submodel reference from the Asset Administration Shell. Does not delete the submodel itself!
   * 
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @return ApiResponse&lt;Void&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<Void> deleteSubmodelReferenceByIdAasRepositoryWithHttpInfo(String aasIdentifier, String submodelIdentifier) throws ApiException {
    String  aasIdentifierAsBytes = ApiClient.base64UrlEncode(aasIdentifier);
    String  submodelIdentifierAsBytes = ApiClient.base64UrlEncode(submodelIdentifier);
  	return deleteSubmodelReferenceByIdAasRepositoryWithHttpInfoNoUrlEncoding(aasIdentifierAsBytes, submodelIdentifierAsBytes);
 	
 }


  /**
   * Deletes the submodel reference from the Asset Administration Shell. Does not delete the submodel itself!
   * 
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @return ApiResponse&lt;Void&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<Void> deleteSubmodelReferenceByIdAasRepositoryWithHttpInfoNoUrlEncoding(String aasIdentifier, String submodelIdentifier) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = deleteSubmodelReferenceByIdAasRepositoryRequestBuilder(aasIdentifier, submodelIdentifier);
    try {
      HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(
          localVarRequestBuilder.build(),
          HttpResponse.BodyHandlers.ofInputStream());
      if (memberVarResponseInterceptor != null) {
        memberVarResponseInterceptor.accept(localVarResponse);
      }
      try {
        if (localVarResponse.statusCode()/ 100 != 2) {
          throw getApiException("deleteSubmodelReferenceByIdAasRepository", localVarResponse);
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

  private HttpRequest.Builder deleteSubmodelReferenceByIdAasRepositoryRequestBuilder(String aasIdentifier, String submodelIdentifier) throws ApiException {
    // verify the required parameter 'aasIdentifier' is set
    if (aasIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'aasIdentifier' when calling deleteSubmodelReferenceByIdAasRepository");
    }
    // verify the required parameter 'submodelIdentifier' is set
    if (submodelIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'submodelIdentifier' when calling deleteSubmodelReferenceByIdAasRepository");
    }

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/shells/{aasIdentifier}/submodel-refs/{submodelIdentifier}"
        .replace("{aasIdentifier}", ApiClient.urlEncode(aasIdentifier.toString()))
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
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @throws ApiException if fails to make API call
   */
  public void deleteThumbnailAasRepository(String aasIdentifier) throws ApiException {

    deleteThumbnailAasRepositoryWithHttpInfo(aasIdentifier);
  }

  /**
   * 
   * 
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @return ApiResponse&lt;Void&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<Void> deleteThumbnailAasRepositoryWithHttpInfo(String aasIdentifier) throws ApiException {
    String  aasIdentifierAsBytes = ApiClient.base64UrlEncode(aasIdentifier);
  	return deleteThumbnailAasRepositoryWithHttpInfoNoUrlEncoding(aasIdentifierAsBytes);
 	
 }


  /**
   * 
   * 
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @return ApiResponse&lt;Void&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<Void> deleteThumbnailAasRepositoryWithHttpInfoNoUrlEncoding(String aasIdentifier) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = deleteThumbnailAasRepositoryRequestBuilder(aasIdentifier);
    try {
      HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(
          localVarRequestBuilder.build(),
          HttpResponse.BodyHandlers.ofInputStream());
      if (memberVarResponseInterceptor != null) {
        memberVarResponseInterceptor.accept(localVarResponse);
      }
      try {
        if (localVarResponse.statusCode()/ 100 != 2) {
          throw getApiException("deleteThumbnailAasRepository", localVarResponse);
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

  private HttpRequest.Builder deleteThumbnailAasRepositoryRequestBuilder(String aasIdentifier) throws ApiException {
    // verify the required parameter 'aasIdentifier' is set
    if (aasIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'aasIdentifier' when calling deleteThumbnailAasRepository");
    }

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/shells/{aasIdentifier}/asset-information/thumbnail"
        .replace("{aasIdentifier}", ApiClient.urlEncode(aasIdentifier.toString()));

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
   * Returns all Asset Administration Shells
   * 
   * @param assetIds A list of specific Asset identifiers. Each Asset identifier is a base64-url-encoded [SpecificAssetId](./model-part1.yaml#/components/schemas/SpecificAssetId) (optional
   * @param idShort The Asset Administration Shell’s IdShort (optional)
   * @param limit The maximum number of elements in the response array (optional)
   * @param cursor A server-generated identifier retrieved from pagingMetadata that specifies from which position the result listing should continue (optional)
   * @return GetAssetAdministrationShellsResult
   * @throws ApiException if fails to make API call
   */
  public GetAssetAdministrationShellsResult getAllAssetAdministrationShells(List<String> assetIds, String idShort, Integer limit, String cursor) throws ApiException {

    ApiResponse<GetAssetAdministrationShellsResult> localVarResponse = getAllAssetAdministrationShellsWithHttpInfo(assetIds, idShort, limit, cursor);
    return localVarResponse.getData();
  }

  /**
   * Returns all Asset Administration Shells
   * 
   * @param assetIds A list of specific Asset identifiers. Each Asset identifier is a base64-url-encoded [SpecificAssetId](./model-part1.yaml#/components/schemas/SpecificAssetId) (optional
   * @param idShort The Asset Administration Shell’s IdShort (optional)
   * @param limit The maximum number of elements in the response array (optional)
   * @param cursor A server-generated identifier retrieved from pagingMetadata that specifies from which position the result listing should continue (optional)
   * @return ApiResponse&lt;GetAssetAdministrationShellsResult&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<GetAssetAdministrationShellsResult> getAllAssetAdministrationShellsWithHttpInfo(List<String> assetIds, String idShort, Integer limit, String cursor) throws ApiException {
    List<String>  assetIdsAsBytes = ApiClient.base64UrlEncode(assetIds);
  	return getAllAssetAdministrationShellsWithHttpInfoNoUrlEncoding(assetIdsAsBytes, idShort, limit, cursor);
 	
 }


  /**
   * Returns all Asset Administration Shells
   * 
   * @param assetIds A list of specific Asset identifiers. Each Asset identifier is a base64-url-encoded [SpecificAssetId](./model-part1.yaml#/components/schemas/SpecificAssetId) (optional
   * @param idShort The Asset Administration Shell’s IdShort (optional)
   * @param limit The maximum number of elements in the response array (optional)
   * @param cursor A server-generated identifier retrieved from pagingMetadata that specifies from which position the result listing should continue (optional)
   * @return ApiResponse&lt;GetAssetAdministrationShellsResult&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<GetAssetAdministrationShellsResult> getAllAssetAdministrationShellsWithHttpInfoNoUrlEncoding(List<String> assetIds, String idShort, Integer limit, String cursor) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = getAllAssetAdministrationShellsRequestBuilder(assetIds, idShort, limit, cursor);
    try {
      HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(
          localVarRequestBuilder.build(),
          HttpResponse.BodyHandlers.ofInputStream());
      if (memberVarResponseInterceptor != null) {
        memberVarResponseInterceptor.accept(localVarResponse);
      }
      try {
        if (localVarResponse.statusCode()/ 100 != 2) {
          throw getApiException("getAllAssetAdministrationShells", localVarResponse);
        }
        return new ApiResponse<GetAssetAdministrationShellsResult>(
          localVarResponse.statusCode(),
          localVarResponse.headers().map(),
          localVarResponse.body() == null ? null : memberVarObjectMapper.readValue(localVarResponse.body(), new TypeReference<GetAssetAdministrationShellsResult>() {}) // closes the InputStream
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

  private HttpRequest.Builder getAllAssetAdministrationShellsRequestBuilder(List<String> assetIds, String idShort, Integer limit, String cursor) throws ApiException {

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/shells";

    List<Pair> localVarQueryParams = new ArrayList<>();
    StringJoiner localVarQueryStringJoiner = new StringJoiner("&");
    String localVarQueryParameterBaseName;
    localVarQueryParameterBaseName = "assetIds";
    localVarQueryParams.addAll(ApiClient.parameterToPairs("multi", "assetIds", assetIds));
    localVarQueryParameterBaseName = "idShort";
    localVarQueryParams.addAll(ApiClient.parameterToPairs("idShort", idShort));
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
   * Returns References to all Asset Administration Shells
   * 
   * @param assetIds A list of specific Asset identifiers. Each Asset identifier is a base64-url-encoded [SpecificAssetId](./model-part1.yaml#/components/schemas/SpecificAssetId) (optional
   * @param idShort The Asset Administration Shell’s IdShort (optional)
   * @param limit The maximum number of elements in the response array (optional)
   * @param cursor A server-generated identifier retrieved from pagingMetadata that specifies from which position the result listing should continue (optional)
   * @return GetReferencesResult
   * @throws ApiException if fails to make API call
   */
  public GetReferencesResult getAllAssetAdministrationShellsReference(List<String> assetIds, String idShort, Integer limit, String cursor) throws ApiException {

    ApiResponse<GetReferencesResult> localVarResponse = getAllAssetAdministrationShellsReferenceWithHttpInfo(assetIds, idShort, limit, cursor);
    return localVarResponse.getData();
  }

  /**
   * Returns References to all Asset Administration Shells
   * 
   * @param assetIds A list of specific Asset identifiers. Each Asset identifier is a base64-url-encoded [SpecificAssetId](./model-part1.yaml#/components/schemas/SpecificAssetId) (optional
   * @param idShort The Asset Administration Shell’s IdShort (optional)
   * @param limit The maximum number of elements in the response array (optional)
   * @param cursor A server-generated identifier retrieved from pagingMetadata that specifies from which position the result listing should continue (optional)
   * @return ApiResponse&lt;GetReferencesResult&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<GetReferencesResult> getAllAssetAdministrationShellsReferenceWithHttpInfo(List<String> assetIds, String idShort, Integer limit, String cursor) throws ApiException {
    List<String>  assetIdsAsBytes = ApiClient.base64UrlEncode(assetIds);
  	return getAllAssetAdministrationShellsReferenceWithHttpInfoNoUrlEncoding(assetIdsAsBytes, idShort, limit, cursor);
 	
 }


  /**
   * Returns References to all Asset Administration Shells
   * 
   * @param assetIds A list of specific Asset identifiers. Each Asset identifier is a base64-url-encoded [SpecificAssetId](./model-part1.yaml#/components/schemas/SpecificAssetId) (optional
   * @param idShort The Asset Administration Shell’s IdShort (optional)
   * @param limit The maximum number of elements in the response array (optional)
   * @param cursor A server-generated identifier retrieved from pagingMetadata that specifies from which position the result listing should continue (optional)
   * @return ApiResponse&lt;GetReferencesResult&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<GetReferencesResult> getAllAssetAdministrationShellsReferenceWithHttpInfoNoUrlEncoding(List<String> assetIds, String idShort, Integer limit, String cursor) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = getAllAssetAdministrationShellsReferenceRequestBuilder(assetIds, idShort, limit, cursor);
    try {
      HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(
          localVarRequestBuilder.build(),
          HttpResponse.BodyHandlers.ofInputStream());
      if (memberVarResponseInterceptor != null) {
        memberVarResponseInterceptor.accept(localVarResponse);
      }
      try {
        if (localVarResponse.statusCode()/ 100 != 2) {
          throw getApiException("getAllAssetAdministrationShellsReference", localVarResponse);
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

  private HttpRequest.Builder getAllAssetAdministrationShellsReferenceRequestBuilder(List<String> assetIds, String idShort, Integer limit, String cursor) throws ApiException {

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/shells/$reference";

    List<Pair> localVarQueryParams = new ArrayList<>();
    StringJoiner localVarQueryStringJoiner = new StringJoiner("&");
    String localVarQueryParameterBaseName;
    localVarQueryParameterBaseName = "assetIds";
    localVarQueryParams.addAll(ApiClient.parameterToPairs("multi", "assetIds", assetIds));
    localVarQueryParameterBaseName = "idShort";
    localVarQueryParams.addAll(ApiClient.parameterToPairs("idShort", idShort));
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
   * Returns all submodel elements including their hierarchy
   * 
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param limit The maximum number of elements in the response array (optional)
   * @param cursor A server-generated identifier retrieved from pagingMetadata that specifies from which position the result listing should continue (optional)
   * @param level Determines the structural depth of the respective resource content (optional, default to deep)
   * @param extent Determines to which extent the resource is being serialized (optional, default to withoutBlobValue)
   * @return GetSubmodelElementsResult
   * @throws ApiException if fails to make API call
   */
  public GetSubmodelElementsResult getAllSubmodelElementsAasRepository(String aasIdentifier, String submodelIdentifier, Integer limit, String cursor, String level, String extent) throws ApiException {

    ApiResponse<GetSubmodelElementsResult> localVarResponse = getAllSubmodelElementsAasRepositoryWithHttpInfo(aasIdentifier, submodelIdentifier, limit, cursor, level, extent);
    return localVarResponse.getData();
  }

  /**
   * Returns all submodel elements including their hierarchy
   * 
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param limit The maximum number of elements in the response array (optional)
   * @param cursor A server-generated identifier retrieved from pagingMetadata that specifies from which position the result listing should continue (optional)
   * @param level Determines the structural depth of the respective resource content (optional, default to deep)
   * @param extent Determines to which extent the resource is being serialized (optional, default to withoutBlobValue)
   * @return ApiResponse&lt;GetSubmodelElementsResult&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<GetSubmodelElementsResult> getAllSubmodelElementsAasRepositoryWithHttpInfo(String aasIdentifier, String submodelIdentifier, Integer limit, String cursor, String level, String extent) throws ApiException {
    String  aasIdentifierAsBytes = ApiClient.base64UrlEncode(aasIdentifier);
    String  submodelIdentifierAsBytes = ApiClient.base64UrlEncode(submodelIdentifier);
  	return getAllSubmodelElementsAasRepositoryWithHttpInfoNoUrlEncoding(aasIdentifierAsBytes, submodelIdentifierAsBytes, limit, cursor, level, extent);
 	
 }


  /**
   * Returns all submodel elements including their hierarchy
   * 
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param limit The maximum number of elements in the response array (optional)
   * @param cursor A server-generated identifier retrieved from pagingMetadata that specifies from which position the result listing should continue (optional)
   * @param level Determines the structural depth of the respective resource content (optional, default to deep)
   * @param extent Determines to which extent the resource is being serialized (optional, default to withoutBlobValue)
   * @return ApiResponse&lt;GetSubmodelElementsResult&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<GetSubmodelElementsResult> getAllSubmodelElementsAasRepositoryWithHttpInfoNoUrlEncoding(String aasIdentifier, String submodelIdentifier, Integer limit, String cursor, String level, String extent) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = getAllSubmodelElementsAasRepositoryRequestBuilder(aasIdentifier, submodelIdentifier, limit, cursor, level, extent);
    try {
      HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(
          localVarRequestBuilder.build(),
          HttpResponse.BodyHandlers.ofInputStream());
      if (memberVarResponseInterceptor != null) {
        memberVarResponseInterceptor.accept(localVarResponse);
      }
      try {
        if (localVarResponse.statusCode()/ 100 != 2) {
          throw getApiException("getAllSubmodelElementsAasRepository", localVarResponse);
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

  private HttpRequest.Builder getAllSubmodelElementsAasRepositoryRequestBuilder(String aasIdentifier, String submodelIdentifier, Integer limit, String cursor, String level, String extent) throws ApiException {
    // verify the required parameter 'aasIdentifier' is set
    if (aasIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'aasIdentifier' when calling getAllSubmodelElementsAasRepository");
    }
    // verify the required parameter 'submodelIdentifier' is set
    if (submodelIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'submodelIdentifier' when calling getAllSubmodelElementsAasRepository");
    }

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/shells/{aasIdentifier}/submodels/{submodelIdentifier}/submodel-elements"
        .replace("{aasIdentifier}", ApiClient.urlEncode(aasIdentifier.toString()))
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
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param limit The maximum number of elements in the response array (optional)
   * @param cursor A server-generated identifier retrieved from pagingMetadata that specifies from which position the result listing should continue (optional)
   * @param level Determines the structural depth of the respective resource content (optional, default to deep)
   * @return GetSubmodelElementsMetadataResult
   * @throws ApiException if fails to make API call
   */
  public GetSubmodelElementsMetadataResult getAllSubmodelElementsMetadataAasRepository(String aasIdentifier, String submodelIdentifier, Integer limit, String cursor, String level) throws ApiException {

    ApiResponse<GetSubmodelElementsMetadataResult> localVarResponse = getAllSubmodelElementsMetadataAasRepositoryWithHttpInfo(aasIdentifier, submodelIdentifier, limit, cursor, level);
    return localVarResponse.getData();
  }

  /**
   * Returns all submodel elements including their hierarchy
   * 
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param limit The maximum number of elements in the response array (optional)
   * @param cursor A server-generated identifier retrieved from pagingMetadata that specifies from which position the result listing should continue (optional)
   * @param level Determines the structural depth of the respective resource content (optional, default to deep)
   * @return ApiResponse&lt;GetSubmodelElementsMetadataResult&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<GetSubmodelElementsMetadataResult> getAllSubmodelElementsMetadataAasRepositoryWithHttpInfo(String aasIdentifier, String submodelIdentifier, Integer limit, String cursor, String level) throws ApiException {
    String  aasIdentifierAsBytes = ApiClient.base64UrlEncode(aasIdentifier);
    String  submodelIdentifierAsBytes = ApiClient.base64UrlEncode(submodelIdentifier);
  	return getAllSubmodelElementsMetadataAasRepositoryWithHttpInfoNoUrlEncoding(aasIdentifierAsBytes, submodelIdentifierAsBytes, limit, cursor, level);
 	
 }


  /**
   * Returns all submodel elements including their hierarchy
   * 
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param limit The maximum number of elements in the response array (optional)
   * @param cursor A server-generated identifier retrieved from pagingMetadata that specifies from which position the result listing should continue (optional)
   * @param level Determines the structural depth of the respective resource content (optional, default to deep)
   * @return ApiResponse&lt;GetSubmodelElementsMetadataResult&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<GetSubmodelElementsMetadataResult> getAllSubmodelElementsMetadataAasRepositoryWithHttpInfoNoUrlEncoding(String aasIdentifier, String submodelIdentifier, Integer limit, String cursor, String level) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = getAllSubmodelElementsMetadataAasRepositoryRequestBuilder(aasIdentifier, submodelIdentifier, limit, cursor, level);
    try {
      HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(
          localVarRequestBuilder.build(),
          HttpResponse.BodyHandlers.ofInputStream());
      if (memberVarResponseInterceptor != null) {
        memberVarResponseInterceptor.accept(localVarResponse);
      }
      try {
        if (localVarResponse.statusCode()/ 100 != 2) {
          throw getApiException("getAllSubmodelElementsMetadataAasRepository", localVarResponse);
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

  private HttpRequest.Builder getAllSubmodelElementsMetadataAasRepositoryRequestBuilder(String aasIdentifier, String submodelIdentifier, Integer limit, String cursor, String level) throws ApiException {
    // verify the required parameter 'aasIdentifier' is set
    if (aasIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'aasIdentifier' when calling getAllSubmodelElementsMetadataAasRepository");
    }
    // verify the required parameter 'submodelIdentifier' is set
    if (submodelIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'submodelIdentifier' when calling getAllSubmodelElementsMetadataAasRepository");
    }

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/shells/{aasIdentifier}/submodels/{submodelIdentifier}/submodel-elements/$metadata"
        .replace("{aasIdentifier}", ApiClient.urlEncode(aasIdentifier.toString()))
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
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param limit The maximum number of elements in the response array (optional)
   * @param cursor A server-generated identifier retrieved from pagingMetadata that specifies from which position the result listing should continue (optional)
   * @param level Determines the structural depth of the respective resource content (optional, default to deep)
   * @param extent Determines to which extent the resource is being serialized (optional, default to withoutBlobValue)
   * @return GetPathItemsResult
   * @throws ApiException if fails to make API call
   */
  public GetPathItemsResult getAllSubmodelElementsPathAasRepository(String aasIdentifier, String submodelIdentifier, Integer limit, String cursor, String level, String extent) throws ApiException {

    ApiResponse<GetPathItemsResult> localVarResponse = getAllSubmodelElementsPathAasRepositoryWithHttpInfo(aasIdentifier, submodelIdentifier, limit, cursor, level, extent);
    return localVarResponse.getData();
  }

  /**
   * Returns all submodel elements including their hierarchy
   * 
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param limit The maximum number of elements in the response array (optional)
   * @param cursor A server-generated identifier retrieved from pagingMetadata that specifies from which position the result listing should continue (optional)
   * @param level Determines the structural depth of the respective resource content (optional, default to deep)
   * @param extent Determines to which extent the resource is being serialized (optional, default to withoutBlobValue)
   * @return ApiResponse&lt;GetPathItemsResult&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<GetPathItemsResult> getAllSubmodelElementsPathAasRepositoryWithHttpInfo(String aasIdentifier, String submodelIdentifier, Integer limit, String cursor, String level, String extent) throws ApiException {
    String  aasIdentifierAsBytes = ApiClient.base64UrlEncode(aasIdentifier);
    String  submodelIdentifierAsBytes = ApiClient.base64UrlEncode(submodelIdentifier);
  	return getAllSubmodelElementsPathAasRepositoryWithHttpInfoNoUrlEncoding(aasIdentifierAsBytes, submodelIdentifierAsBytes, limit, cursor, level, extent);
 	
 }


  /**
   * Returns all submodel elements including their hierarchy
   * 
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param limit The maximum number of elements in the response array (optional)
   * @param cursor A server-generated identifier retrieved from pagingMetadata that specifies from which position the result listing should continue (optional)
   * @param level Determines the structural depth of the respective resource content (optional, default to deep)
   * @param extent Determines to which extent the resource is being serialized (optional, default to withoutBlobValue)
   * @return ApiResponse&lt;GetPathItemsResult&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<GetPathItemsResult> getAllSubmodelElementsPathAasRepositoryWithHttpInfoNoUrlEncoding(String aasIdentifier, String submodelIdentifier, Integer limit, String cursor, String level, String extent) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = getAllSubmodelElementsPathAasRepositoryRequestBuilder(aasIdentifier, submodelIdentifier, limit, cursor, level, extent);
    try {
      HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(
          localVarRequestBuilder.build(),
          HttpResponse.BodyHandlers.ofInputStream());
      if (memberVarResponseInterceptor != null) {
        memberVarResponseInterceptor.accept(localVarResponse);
      }
      try {
        if (localVarResponse.statusCode()/ 100 != 2) {
          throw getApiException("getAllSubmodelElementsPathAasRepository", localVarResponse);
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

  private HttpRequest.Builder getAllSubmodelElementsPathAasRepositoryRequestBuilder(String aasIdentifier, String submodelIdentifier, Integer limit, String cursor, String level, String extent) throws ApiException {
    // verify the required parameter 'aasIdentifier' is set
    if (aasIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'aasIdentifier' when calling getAllSubmodelElementsPathAasRepository");
    }
    // verify the required parameter 'submodelIdentifier' is set
    if (submodelIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'submodelIdentifier' when calling getAllSubmodelElementsPathAasRepository");
    }

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/shells/{aasIdentifier}/submodels/{submodelIdentifier}/submodel-elements/$path"
        .replace("{aasIdentifier}", ApiClient.urlEncode(aasIdentifier.toString()))
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
   * Returns all submodel elements as a list of References
   * 
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param limit The maximum number of elements in the response array (optional)
   * @param cursor A server-generated identifier retrieved from pagingMetadata that specifies from which position the result listing should continue (optional)
   * @param level Determines the structural depth of the respective resource content (optional, default to core)
   * @return GetReferencesResult
   * @throws ApiException if fails to make API call
   */
  public GetReferencesResult getAllSubmodelElementsReferenceAasRepository(String aasIdentifier, String submodelIdentifier, Integer limit, String cursor, String level) throws ApiException {

    ApiResponse<GetReferencesResult> localVarResponse = getAllSubmodelElementsReferenceAasRepositoryWithHttpInfo(aasIdentifier, submodelIdentifier, limit, cursor, level);
    return localVarResponse.getData();
  }

  /**
   * Returns all submodel elements as a list of References
   * 
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param limit The maximum number of elements in the response array (optional)
   * @param cursor A server-generated identifier retrieved from pagingMetadata that specifies from which position the result listing should continue (optional)
   * @param level Determines the structural depth of the respective resource content (optional, default to core)
   * @return ApiResponse&lt;GetReferencesResult&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<GetReferencesResult> getAllSubmodelElementsReferenceAasRepositoryWithHttpInfo(String aasIdentifier, String submodelIdentifier, Integer limit, String cursor, String level) throws ApiException {
    String  aasIdentifierAsBytes = ApiClient.base64UrlEncode(aasIdentifier);
    String  submodelIdentifierAsBytes = ApiClient.base64UrlEncode(submodelIdentifier);
  	return getAllSubmodelElementsReferenceAasRepositoryWithHttpInfoNoUrlEncoding(aasIdentifierAsBytes, submodelIdentifierAsBytes, limit, cursor, level);
 	
 }


  /**
   * Returns all submodel elements as a list of References
   * 
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param limit The maximum number of elements in the response array (optional)
   * @param cursor A server-generated identifier retrieved from pagingMetadata that specifies from which position the result listing should continue (optional)
   * @param level Determines the structural depth of the respective resource content (optional, default to core)
   * @return ApiResponse&lt;GetReferencesResult&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<GetReferencesResult> getAllSubmodelElementsReferenceAasRepositoryWithHttpInfoNoUrlEncoding(String aasIdentifier, String submodelIdentifier, Integer limit, String cursor, String level) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = getAllSubmodelElementsReferenceAasRepositoryRequestBuilder(aasIdentifier, submodelIdentifier, limit, cursor, level);
    try {
      HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(
          localVarRequestBuilder.build(),
          HttpResponse.BodyHandlers.ofInputStream());
      if (memberVarResponseInterceptor != null) {
        memberVarResponseInterceptor.accept(localVarResponse);
      }
      try {
        if (localVarResponse.statusCode()/ 100 != 2) {
          throw getApiException("getAllSubmodelElementsReferenceAasRepository", localVarResponse);
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

  private HttpRequest.Builder getAllSubmodelElementsReferenceAasRepositoryRequestBuilder(String aasIdentifier, String submodelIdentifier, Integer limit, String cursor, String level) throws ApiException {
    // verify the required parameter 'aasIdentifier' is set
    if (aasIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'aasIdentifier' when calling getAllSubmodelElementsReferenceAasRepository");
    }
    // verify the required parameter 'submodelIdentifier' is set
    if (submodelIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'submodelIdentifier' when calling getAllSubmodelElementsReferenceAasRepository");
    }

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/shells/{aasIdentifier}/submodels/{submodelIdentifier}/submodel-elements/$reference"
        .replace("{aasIdentifier}", ApiClient.urlEncode(aasIdentifier.toString()))
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
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param limit The maximum number of elements in the response array (optional)
   * @param cursor A server-generated identifier retrieved from pagingMetadata that specifies from which position the result listing should continue (optional)
   * @param level Determines the structural depth of the respective resource content (optional, default to deep)
   * @return GetSubmodelElementsValueResult
   * @throws ApiException if fails to make API call
   */
  public GetSubmodelElementsValueResult getAllSubmodelElementsValueOnlyAasRepository(String aasIdentifier, String submodelIdentifier, Integer limit, String cursor, String level) throws ApiException {

    ApiResponse<GetSubmodelElementsValueResult> localVarResponse = getAllSubmodelElementsValueOnlyAasRepositoryWithHttpInfo(aasIdentifier, submodelIdentifier, limit, cursor, level);
    return localVarResponse.getData();
  }

  /**
   * Returns all submodel elements including their hierarchy in the ValueOnly representation
   * 
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param limit The maximum number of elements in the response array (optional)
   * @param cursor A server-generated identifier retrieved from pagingMetadata that specifies from which position the result listing should continue (optional)
   * @param level Determines the structural depth of the respective resource content (optional, default to deep)
   * @return ApiResponse&lt;GetSubmodelElementsValueResult&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<GetSubmodelElementsValueResult> getAllSubmodelElementsValueOnlyAasRepositoryWithHttpInfo(String aasIdentifier, String submodelIdentifier, Integer limit, String cursor, String level) throws ApiException {
    String  aasIdentifierAsBytes = ApiClient.base64UrlEncode(aasIdentifier);
    String  submodelIdentifierAsBytes = ApiClient.base64UrlEncode(submodelIdentifier);
  	return getAllSubmodelElementsValueOnlyAasRepositoryWithHttpInfoNoUrlEncoding(aasIdentifierAsBytes, submodelIdentifierAsBytes, limit, cursor, level);
 	
 }


  /**
   * Returns all submodel elements including their hierarchy in the ValueOnly representation
   * 
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param limit The maximum number of elements in the response array (optional)
   * @param cursor A server-generated identifier retrieved from pagingMetadata that specifies from which position the result listing should continue (optional)
   * @param level Determines the structural depth of the respective resource content (optional, default to deep)
   * @return ApiResponse&lt;GetSubmodelElementsValueResult&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<GetSubmodelElementsValueResult> getAllSubmodelElementsValueOnlyAasRepositoryWithHttpInfoNoUrlEncoding(String aasIdentifier, String submodelIdentifier, Integer limit, String cursor, String level) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = getAllSubmodelElementsValueOnlyAasRepositoryRequestBuilder(aasIdentifier, submodelIdentifier, limit, cursor, level);
    try {
      HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(
          localVarRequestBuilder.build(),
          HttpResponse.BodyHandlers.ofInputStream());
      if (memberVarResponseInterceptor != null) {
        memberVarResponseInterceptor.accept(localVarResponse);
      }
      try {
        if (localVarResponse.statusCode()/ 100 != 2) {
          throw getApiException("getAllSubmodelElementsValueOnlyAasRepository", localVarResponse);
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

  private HttpRequest.Builder getAllSubmodelElementsValueOnlyAasRepositoryRequestBuilder(String aasIdentifier, String submodelIdentifier, Integer limit, String cursor, String level) throws ApiException {
    // verify the required parameter 'aasIdentifier' is set
    if (aasIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'aasIdentifier' when calling getAllSubmodelElementsValueOnlyAasRepository");
    }
    // verify the required parameter 'submodelIdentifier' is set
    if (submodelIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'submodelIdentifier' when calling getAllSubmodelElementsValueOnlyAasRepository");
    }

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/shells/{aasIdentifier}/submodels/{submodelIdentifier}/submodel-elements/$value"
        .replace("{aasIdentifier}", ApiClient.urlEncode(aasIdentifier.toString()))
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
   * Returns all submodel references
   * 
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param limit The maximum number of elements in the response array (optional)
   * @param cursor A server-generated identifier retrieved from pagingMetadata that specifies from which position the result listing should continue (optional)
   * @return GetReferencesResult
   * @throws ApiException if fails to make API call
   */
  public GetReferencesResult getAllSubmodelReferencesAasRepository(String aasIdentifier, Integer limit, String cursor) throws ApiException {

    ApiResponse<GetReferencesResult> localVarResponse = getAllSubmodelReferencesAasRepositoryWithHttpInfo(aasIdentifier, limit, cursor);
    return localVarResponse.getData();
  }

  /**
   * Returns all submodel references
   * 
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param limit The maximum number of elements in the response array (optional)
   * @param cursor A server-generated identifier retrieved from pagingMetadata that specifies from which position the result listing should continue (optional)
   * @return ApiResponse&lt;GetReferencesResult&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<GetReferencesResult> getAllSubmodelReferencesAasRepositoryWithHttpInfo(String aasIdentifier, Integer limit, String cursor) throws ApiException {
    String  aasIdentifierAsBytes = ApiClient.base64UrlEncode(aasIdentifier);
  	return getAllSubmodelReferencesAasRepositoryWithHttpInfoNoUrlEncoding(aasIdentifierAsBytes, limit, cursor);
 	
 }


  /**
   * Returns all submodel references
   * 
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param limit The maximum number of elements in the response array (optional)
   * @param cursor A server-generated identifier retrieved from pagingMetadata that specifies from which position the result listing should continue (optional)
   * @return ApiResponse&lt;GetReferencesResult&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<GetReferencesResult> getAllSubmodelReferencesAasRepositoryWithHttpInfoNoUrlEncoding(String aasIdentifier, Integer limit, String cursor) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = getAllSubmodelReferencesAasRepositoryRequestBuilder(aasIdentifier, limit, cursor);
    try {
      HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(
          localVarRequestBuilder.build(),
          HttpResponse.BodyHandlers.ofInputStream());
      if (memberVarResponseInterceptor != null) {
        memberVarResponseInterceptor.accept(localVarResponse);
      }
      try {
        if (localVarResponse.statusCode()/ 100 != 2) {
          throw getApiException("getAllSubmodelReferencesAasRepository", localVarResponse);
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

  private HttpRequest.Builder getAllSubmodelReferencesAasRepositoryRequestBuilder(String aasIdentifier, Integer limit, String cursor) throws ApiException {
    // verify the required parameter 'aasIdentifier' is set
    if (aasIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'aasIdentifier' when calling getAllSubmodelReferencesAasRepository");
    }

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/shells/{aasIdentifier}/submodel-refs"
        .replace("{aasIdentifier}", ApiClient.urlEncode(aasIdentifier.toString()));

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
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @return AssetAdministrationShell
   * @throws ApiException if fails to make API call
   */
  public AssetAdministrationShell getAssetAdministrationShellById(String aasIdentifier) throws ApiException {

    ApiResponse<AssetAdministrationShell> localVarResponse = getAssetAdministrationShellByIdWithHttpInfo(aasIdentifier);
    return localVarResponse.getData();
  }

  /**
   * Returns a specific Asset Administration Shell
   * 
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @return ApiResponse&lt;AssetAdministrationShell&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<AssetAdministrationShell> getAssetAdministrationShellByIdWithHttpInfo(String aasIdentifier) throws ApiException {
    String  aasIdentifierAsBytes = ApiClient.base64UrlEncode(aasIdentifier);
  	return getAssetAdministrationShellByIdWithHttpInfoNoUrlEncoding(aasIdentifierAsBytes);
 	
 }


  /**
   * Returns a specific Asset Administration Shell
   * 
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @return ApiResponse&lt;AssetAdministrationShell&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<AssetAdministrationShell> getAssetAdministrationShellByIdWithHttpInfoNoUrlEncoding(String aasIdentifier) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = getAssetAdministrationShellByIdRequestBuilder(aasIdentifier);
    try {
      HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(
          localVarRequestBuilder.build(),
          HttpResponse.BodyHandlers.ofInputStream());
      if (memberVarResponseInterceptor != null) {
        memberVarResponseInterceptor.accept(localVarResponse);
      }
      try {
        if (localVarResponse.statusCode()/ 100 != 2) {
          throw getApiException("getAssetAdministrationShellById", localVarResponse);
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

  private HttpRequest.Builder getAssetAdministrationShellByIdRequestBuilder(String aasIdentifier) throws ApiException {
    // verify the required parameter 'aasIdentifier' is set
    if (aasIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'aasIdentifier' when calling getAssetAdministrationShellById");
    }

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/shells/{aasIdentifier}"
        .replace("{aasIdentifier}", ApiClient.urlEncode(aasIdentifier.toString()));

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
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @return Reference
   * @throws ApiException if fails to make API call
   */
  public Reference getAssetAdministrationShellByIdReferenceAasRepository(String aasIdentifier) throws ApiException {

    ApiResponse<Reference> localVarResponse = getAssetAdministrationShellByIdReferenceAasRepositoryWithHttpInfo(aasIdentifier);
    return localVarResponse.getData();
  }

  /**
   * Returns a specific Asset Administration Shell as a Reference
   * 
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @return ApiResponse&lt;Reference&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<Reference> getAssetAdministrationShellByIdReferenceAasRepositoryWithHttpInfo(String aasIdentifier) throws ApiException {
    String  aasIdentifierAsBytes = ApiClient.base64UrlEncode(aasIdentifier);
  	return getAssetAdministrationShellByIdReferenceAasRepositoryWithHttpInfoNoUrlEncoding(aasIdentifierAsBytes);
 	
 }


  /**
   * Returns a specific Asset Administration Shell as a Reference
   * 
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @return ApiResponse&lt;Reference&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<Reference> getAssetAdministrationShellByIdReferenceAasRepositoryWithHttpInfoNoUrlEncoding(String aasIdentifier) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = getAssetAdministrationShellByIdReferenceAasRepositoryRequestBuilder(aasIdentifier);
    try {
      HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(
          localVarRequestBuilder.build(),
          HttpResponse.BodyHandlers.ofInputStream());
      if (memberVarResponseInterceptor != null) {
        memberVarResponseInterceptor.accept(localVarResponse);
      }
      try {
        if (localVarResponse.statusCode()/ 100 != 2) {
          throw getApiException("getAssetAdministrationShellByIdReferenceAasRepository", localVarResponse);
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

  private HttpRequest.Builder getAssetAdministrationShellByIdReferenceAasRepositoryRequestBuilder(String aasIdentifier) throws ApiException {
    // verify the required parameter 'aasIdentifier' is set
    if (aasIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'aasIdentifier' when calling getAssetAdministrationShellByIdReferenceAasRepository");
    }

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/shells/{aasIdentifier}/$reference"
        .replace("{aasIdentifier}", ApiClient.urlEncode(aasIdentifier.toString()));

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
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @return AssetInformation
   * @throws ApiException if fails to make API call
   */
  public AssetInformation getAssetInformationAasRepository(String aasIdentifier) throws ApiException {

    ApiResponse<AssetInformation> localVarResponse = getAssetInformationAasRepositoryWithHttpInfo(aasIdentifier);
    return localVarResponse.getData();
  }

  /**
   * Returns the Asset Information
   * 
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @return ApiResponse&lt;AssetInformation&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<AssetInformation> getAssetInformationAasRepositoryWithHttpInfo(String aasIdentifier) throws ApiException {
    String  aasIdentifierAsBytes = ApiClient.base64UrlEncode(aasIdentifier);
  	return getAssetInformationAasRepositoryWithHttpInfoNoUrlEncoding(aasIdentifierAsBytes);
 	
 }


  /**
   * Returns the Asset Information
   * 
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @return ApiResponse&lt;AssetInformation&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<AssetInformation> getAssetInformationAasRepositoryWithHttpInfoNoUrlEncoding(String aasIdentifier) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = getAssetInformationAasRepositoryRequestBuilder(aasIdentifier);
    try {
      HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(
          localVarRequestBuilder.build(),
          HttpResponse.BodyHandlers.ofInputStream());
      if (memberVarResponseInterceptor != null) {
        memberVarResponseInterceptor.accept(localVarResponse);
      }
      try {
        if (localVarResponse.statusCode()/ 100 != 2) {
          throw getApiException("getAssetInformationAasRepository", localVarResponse);
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

  private HttpRequest.Builder getAssetInformationAasRepositoryRequestBuilder(String aasIdentifier) throws ApiException {
    // verify the required parameter 'aasIdentifier' is set
    if (aasIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'aasIdentifier' when calling getAssetInformationAasRepository");
    }

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/shells/{aasIdentifier}/asset-information"
        .replace("{aasIdentifier}", ApiClient.urlEncode(aasIdentifier.toString()));

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
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @return File
   * @throws ApiException if fails to make API call
   */
  public File getFileByPathAasRepository(String aasIdentifier, String submodelIdentifier, String idShortPath) throws ApiException {

    ApiResponse<File> localVarResponse = getFileByPathAasRepositoryWithHttpInfo(aasIdentifier, submodelIdentifier, idShortPath);
    return localVarResponse.getData();
  }

  /**
   * Downloads file content from a specific submodel element from the Submodel at a specified path
   * 
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @return ApiResponse&lt;File&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<File> getFileByPathAasRepositoryWithHttpInfo(String aasIdentifier, String submodelIdentifier, String idShortPath) throws ApiException {
    String  aasIdentifierAsBytes = ApiClient.base64UrlEncode(aasIdentifier);
    String  submodelIdentifierAsBytes = ApiClient.base64UrlEncode(submodelIdentifier);
  	return getFileByPathAasRepositoryWithHttpInfoNoUrlEncoding(aasIdentifierAsBytes, submodelIdentifierAsBytes, idShortPath);
 	
 }


  /**
   * Downloads file content from a specific submodel element from the Submodel at a specified path
   * 
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @return ApiResponse&lt;File&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<File> getFileByPathAasRepositoryWithHttpInfoNoUrlEncoding(String aasIdentifier, String submodelIdentifier, String idShortPath) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = getFileByPathAasRepositoryRequestBuilder(aasIdentifier, submodelIdentifier, idShortPath);
    try {
      HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(
          localVarRequestBuilder.build(),
          HttpResponse.BodyHandlers.ofInputStream());
      if (memberVarResponseInterceptor != null) {
        memberVarResponseInterceptor.accept(localVarResponse);
      }
      try {
        if (localVarResponse.statusCode()/ 100 != 2) {
          throw getApiException("getFileByPathAasRepository", localVarResponse);
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

  private HttpRequest.Builder getFileByPathAasRepositoryRequestBuilder(String aasIdentifier, String submodelIdentifier, String idShortPath) throws ApiException {
    // verify the required parameter 'aasIdentifier' is set
    if (aasIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'aasIdentifier' when calling getFileByPathAasRepository");
    }
    // verify the required parameter 'submodelIdentifier' is set
    if (submodelIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'submodelIdentifier' when calling getFileByPathAasRepository");
    }
    // verify the required parameter 'idShortPath' is set
    if (idShortPath == null) {
      throw new ApiException(400, "Missing the required parameter 'idShortPath' when calling getFileByPathAasRepository");
    }

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/shells/{aasIdentifier}/submodels/{submodelIdentifier}/submodel-elements/{idShortPath}/attachment"
        .replace("{aasIdentifier}", ApiClient.urlEncode(aasIdentifier.toString()))
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
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param handleId The returned handle id of an operation’s asynchronous invocation used to request the current state of the operation’s execution (UTF8-BASE64-URL-encoded) (required)
   * @return OperationResult
   * @throws ApiException if fails to make API call
   */
  public OperationResult getOperationAsyncResultAasRepository(String aasIdentifier, String submodelIdentifier, String idShortPath, String handleId) throws ApiException {

    ApiResponse<OperationResult> localVarResponse = getOperationAsyncResultAasRepositoryWithHttpInfo(aasIdentifier, submodelIdentifier, idShortPath, handleId);
    return localVarResponse.getData();
  }

  /**
   * Returns the Operation result of an asynchronous invoked Operation
   * 
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param handleId The returned handle id of an operation’s asynchronous invocation used to request the current state of the operation’s execution (UTF8-BASE64-URL-encoded) (required)
   * @return ApiResponse&lt;OperationResult&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<OperationResult> getOperationAsyncResultAasRepositoryWithHttpInfo(String aasIdentifier, String submodelIdentifier, String idShortPath, String handleId) throws ApiException {
    String  aasIdentifierAsBytes = ApiClient.base64UrlEncode(aasIdentifier);
    String  submodelIdentifierAsBytes = ApiClient.base64UrlEncode(submodelIdentifier);
    String  handleIdAsBytes = ApiClient.base64UrlEncode(handleId);
  	return getOperationAsyncResultAasRepositoryWithHttpInfoNoUrlEncoding(aasIdentifierAsBytes, submodelIdentifierAsBytes, idShortPath, handleIdAsBytes);
 	
 }


  /**
   * Returns the Operation result of an asynchronous invoked Operation
   * 
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param handleId The returned handle id of an operation’s asynchronous invocation used to request the current state of the operation’s execution (UTF8-BASE64-URL-encoded) (required)
   * @return ApiResponse&lt;OperationResult&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<OperationResult> getOperationAsyncResultAasRepositoryWithHttpInfoNoUrlEncoding(String aasIdentifier, String submodelIdentifier, String idShortPath, String handleId) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = getOperationAsyncResultAasRepositoryRequestBuilder(aasIdentifier, submodelIdentifier, idShortPath, handleId);
    try {
      HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(
          localVarRequestBuilder.build(),
          HttpResponse.BodyHandlers.ofInputStream());
      if (memberVarResponseInterceptor != null) {
        memberVarResponseInterceptor.accept(localVarResponse);
      }
      try {
        if (localVarResponse.statusCode()/ 100 != 2) {
          throw getApiException("getOperationAsyncResultAasRepository", localVarResponse);
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

  private HttpRequest.Builder getOperationAsyncResultAasRepositoryRequestBuilder(String aasIdentifier, String submodelIdentifier, String idShortPath, String handleId) throws ApiException {
    // verify the required parameter 'aasIdentifier' is set
    if (aasIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'aasIdentifier' when calling getOperationAsyncResultAasRepository");
    }
    // verify the required parameter 'submodelIdentifier' is set
    if (submodelIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'submodelIdentifier' when calling getOperationAsyncResultAasRepository");
    }
    // verify the required parameter 'idShortPath' is set
    if (idShortPath == null) {
      throw new ApiException(400, "Missing the required parameter 'idShortPath' when calling getOperationAsyncResultAasRepository");
    }
    // verify the required parameter 'handleId' is set
    if (handleId == null) {
      throw new ApiException(400, "Missing the required parameter 'handleId' when calling getOperationAsyncResultAasRepository");
    }

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/shells/{aasIdentifier}/submodels/{submodelIdentifier}/submodel-elements/{idShortPath}/operation-results/{handleId}"
        .replace("{aasIdentifier}", ApiClient.urlEncode(aasIdentifier.toString()))
        .replace("{submodelIdentifier}", ApiClient.urlEncode(submodelIdentifier.toString()))
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
   * Returns the ValueOnly notation of the Operation result of an asynchronous invoked Operation
   * 
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param handleId The returned handle id of an operation’s asynchronous invocation used to request the current state of the operation’s execution (UTF8-BASE64-URL-encoded) (required)
   * @return OperationResultValueOnly
   * @throws ApiException if fails to make API call
   */
  public OperationResultValueOnly getOperationAsyncResultValueOnlyAasRepository(String aasIdentifier, String submodelIdentifier, String idShortPath, String handleId) throws ApiException {

    ApiResponse<OperationResultValueOnly> localVarResponse = getOperationAsyncResultValueOnlyAasRepositoryWithHttpInfo(aasIdentifier, submodelIdentifier, idShortPath, handleId);
    return localVarResponse.getData();
  }

  /**
   * Returns the ValueOnly notation of the Operation result of an asynchronous invoked Operation
   * 
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param handleId The returned handle id of an operation’s asynchronous invocation used to request the current state of the operation’s execution (UTF8-BASE64-URL-encoded) (required)
   * @return ApiResponse&lt;OperationResultValueOnly&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<OperationResultValueOnly> getOperationAsyncResultValueOnlyAasRepositoryWithHttpInfo(String aasIdentifier, String submodelIdentifier, String idShortPath, String handleId) throws ApiException {
    String  aasIdentifierAsBytes = ApiClient.base64UrlEncode(aasIdentifier);
    String  submodelIdentifierAsBytes = ApiClient.base64UrlEncode(submodelIdentifier);
    String  handleIdAsBytes = ApiClient.base64UrlEncode(handleId);
  	return getOperationAsyncResultValueOnlyAasRepositoryWithHttpInfoNoUrlEncoding(aasIdentifierAsBytes, submodelIdentifierAsBytes, idShortPath, handleIdAsBytes);
 	
 }


  /**
   * Returns the ValueOnly notation of the Operation result of an asynchronous invoked Operation
   * 
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param handleId The returned handle id of an operation’s asynchronous invocation used to request the current state of the operation’s execution (UTF8-BASE64-URL-encoded) (required)
   * @return ApiResponse&lt;OperationResultValueOnly&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<OperationResultValueOnly> getOperationAsyncResultValueOnlyAasRepositoryWithHttpInfoNoUrlEncoding(String aasIdentifier, String submodelIdentifier, String idShortPath, String handleId) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = getOperationAsyncResultValueOnlyAasRepositoryRequestBuilder(aasIdentifier, submodelIdentifier, idShortPath, handleId);
    try {
      HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(
          localVarRequestBuilder.build(),
          HttpResponse.BodyHandlers.ofInputStream());
      if (memberVarResponseInterceptor != null) {
        memberVarResponseInterceptor.accept(localVarResponse);
      }
      try {
        if (localVarResponse.statusCode()/ 100 != 2) {
          throw getApiException("getOperationAsyncResultValueOnlyAasRepository", localVarResponse);
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

  private HttpRequest.Builder getOperationAsyncResultValueOnlyAasRepositoryRequestBuilder(String aasIdentifier, String submodelIdentifier, String idShortPath, String handleId) throws ApiException {
    // verify the required parameter 'aasIdentifier' is set
    if (aasIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'aasIdentifier' when calling getOperationAsyncResultValueOnlyAasRepository");
    }
    // verify the required parameter 'submodelIdentifier' is set
    if (submodelIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'submodelIdentifier' when calling getOperationAsyncResultValueOnlyAasRepository");
    }
    // verify the required parameter 'idShortPath' is set
    if (idShortPath == null) {
      throw new ApiException(400, "Missing the required parameter 'idShortPath' when calling getOperationAsyncResultValueOnlyAasRepository");
    }
    // verify the required parameter 'handleId' is set
    if (handleId == null) {
      throw new ApiException(400, "Missing the required parameter 'handleId' when calling getOperationAsyncResultValueOnlyAasRepository");
    }

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/shells/{aasIdentifier}/submodels/{submodelIdentifier}/submodel-elements/{idShortPath}/operation-results/{handleId}/$value"
        .replace("{aasIdentifier}", ApiClient.urlEncode(aasIdentifier.toString()))
        .replace("{submodelIdentifier}", ApiClient.urlEncode(submodelIdentifier.toString()))
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
   * Returns the Operation status of an asynchronous invoked Operation
   * 
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param handleId The returned handle id of an operation’s asynchronous invocation used to request the current state of the operation’s execution (UTF8-BASE64-URL-encoded) (required)
   * @return BaseOperationResult
   * @throws ApiException if fails to make API call
   */
  public BaseOperationResult getOperationAsyncStatusAasRepository(String aasIdentifier, String submodelIdentifier, String idShortPath, String handleId) throws ApiException {

    ApiResponse<BaseOperationResult> localVarResponse = getOperationAsyncStatusAasRepositoryWithHttpInfo(aasIdentifier, submodelIdentifier, idShortPath, handleId);
    return localVarResponse.getData();
  }

  /**
   * Returns the Operation status of an asynchronous invoked Operation
   * 
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param handleId The returned handle id of an operation’s asynchronous invocation used to request the current state of the operation’s execution (UTF8-BASE64-URL-encoded) (required)
   * @return ApiResponse&lt;BaseOperationResult&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<BaseOperationResult> getOperationAsyncStatusAasRepositoryWithHttpInfo(String aasIdentifier, String submodelIdentifier, String idShortPath, String handleId) throws ApiException {
    String  aasIdentifierAsBytes = ApiClient.base64UrlEncode(aasIdentifier);
    String  submodelIdentifierAsBytes = ApiClient.base64UrlEncode(submodelIdentifier);
    String  handleIdAsBytes = ApiClient.base64UrlEncode(handleId);
  	return getOperationAsyncStatusAasRepositoryWithHttpInfoNoUrlEncoding(aasIdentifierAsBytes, submodelIdentifierAsBytes, idShortPath, handleIdAsBytes);
 	
 }


  /**
   * Returns the Operation status of an asynchronous invoked Operation
   * 
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param handleId The returned handle id of an operation’s asynchronous invocation used to request the current state of the operation’s execution (UTF8-BASE64-URL-encoded) (required)
   * @return ApiResponse&lt;BaseOperationResult&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<BaseOperationResult> getOperationAsyncStatusAasRepositoryWithHttpInfoNoUrlEncoding(String aasIdentifier, String submodelIdentifier, String idShortPath, String handleId) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = getOperationAsyncStatusAasRepositoryRequestBuilder(aasIdentifier, submodelIdentifier, idShortPath, handleId);
    try {
      HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(
          localVarRequestBuilder.build(),
          HttpResponse.BodyHandlers.ofInputStream());
      if (memberVarResponseInterceptor != null) {
        memberVarResponseInterceptor.accept(localVarResponse);
      }
      try {
        if (localVarResponse.statusCode()/ 100 != 2) {
          throw getApiException("getOperationAsyncStatusAasRepository", localVarResponse);
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

  private HttpRequest.Builder getOperationAsyncStatusAasRepositoryRequestBuilder(String aasIdentifier, String submodelIdentifier, String idShortPath, String handleId) throws ApiException {
    // verify the required parameter 'aasIdentifier' is set
    if (aasIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'aasIdentifier' when calling getOperationAsyncStatusAasRepository");
    }
    // verify the required parameter 'submodelIdentifier' is set
    if (submodelIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'submodelIdentifier' when calling getOperationAsyncStatusAasRepository");
    }
    // verify the required parameter 'idShortPath' is set
    if (idShortPath == null) {
      throw new ApiException(400, "Missing the required parameter 'idShortPath' when calling getOperationAsyncStatusAasRepository");
    }
    // verify the required parameter 'handleId' is set
    if (handleId == null) {
      throw new ApiException(400, "Missing the required parameter 'handleId' when calling getOperationAsyncStatusAasRepository");
    }

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/shells/{aasIdentifier}/submodels/{submodelIdentifier}/submodel-elements/{idShortPath}/operation-status/{handleId}"
        .replace("{aasIdentifier}", ApiClient.urlEncode(aasIdentifier.toString()))
        .replace("{submodelIdentifier}", ApiClient.urlEncode(submodelIdentifier.toString()))
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
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param level Determines the structural depth of the respective resource content (optional, default to deep)
   * @param extent Determines to which extent the resource is being serialized (optional, default to withoutBlobValue)
   * @return Submodel
   * @throws ApiException if fails to make API call
   */
  public Submodel getSubmodelByIdAasRepository(String aasIdentifier, String submodelIdentifier, String level, String extent) throws ApiException {

    ApiResponse<Submodel> localVarResponse = getSubmodelByIdAasRepositoryWithHttpInfo(aasIdentifier, submodelIdentifier, level, extent);
    return localVarResponse.getData();
  }

  /**
   * Returns the Submodel
   * 
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param level Determines the structural depth of the respective resource content (optional, default to deep)
   * @param extent Determines to which extent the resource is being serialized (optional, default to withoutBlobValue)
   * @return ApiResponse&lt;Submodel&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<Submodel> getSubmodelByIdAasRepositoryWithHttpInfo(String aasIdentifier, String submodelIdentifier, String level, String extent) throws ApiException {
    String  aasIdentifierAsBytes = ApiClient.base64UrlEncode(aasIdentifier);
    String  submodelIdentifierAsBytes = ApiClient.base64UrlEncode(submodelIdentifier);
  	return getSubmodelByIdAasRepositoryWithHttpInfoNoUrlEncoding(aasIdentifierAsBytes, submodelIdentifierAsBytes, level, extent);
 	
 }


  /**
   * Returns the Submodel
   * 
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param level Determines the structural depth of the respective resource content (optional, default to deep)
   * @param extent Determines to which extent the resource is being serialized (optional, default to withoutBlobValue)
   * @return ApiResponse&lt;Submodel&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<Submodel> getSubmodelByIdAasRepositoryWithHttpInfoNoUrlEncoding(String aasIdentifier, String submodelIdentifier, String level, String extent) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = getSubmodelByIdAasRepositoryRequestBuilder(aasIdentifier, submodelIdentifier, level, extent);
    try {
      HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(
          localVarRequestBuilder.build(),
          HttpResponse.BodyHandlers.ofInputStream());
      if (memberVarResponseInterceptor != null) {
        memberVarResponseInterceptor.accept(localVarResponse);
      }
      try {
        if (localVarResponse.statusCode()/ 100 != 2) {
          throw getApiException("getSubmodelByIdAasRepository", localVarResponse);
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

  private HttpRequest.Builder getSubmodelByIdAasRepositoryRequestBuilder(String aasIdentifier, String submodelIdentifier, String level, String extent) throws ApiException {
    // verify the required parameter 'aasIdentifier' is set
    if (aasIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'aasIdentifier' when calling getSubmodelByIdAasRepository");
    }
    // verify the required parameter 'submodelIdentifier' is set
    if (submodelIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'submodelIdentifier' when calling getSubmodelByIdAasRepository");
    }

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/shells/{aasIdentifier}/submodels/{submodelIdentifier}"
        .replace("{aasIdentifier}", ApiClient.urlEncode(aasIdentifier.toString()))
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
   * Returns the Submodel&#39;s metadata elements
   * 
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param level Determines the structural depth of the respective resource content (optional, default to deep)
   * @return SubmodelMetadata
   * @throws ApiException if fails to make API call
   */
  public SubmodelMetadata getSubmodelByIdMetadataAasRepository(String aasIdentifier, String submodelIdentifier, String level) throws ApiException {

    ApiResponse<SubmodelMetadata> localVarResponse = getSubmodelByIdMetadataAasRepositoryWithHttpInfo(aasIdentifier, submodelIdentifier, level);
    return localVarResponse.getData();
  }

  /**
   * Returns the Submodel&#39;s metadata elements
   * 
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param level Determines the structural depth of the respective resource content (optional, default to deep)
   * @return ApiResponse&lt;SubmodelMetadata&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<SubmodelMetadata> getSubmodelByIdMetadataAasRepositoryWithHttpInfo(String aasIdentifier, String submodelIdentifier, String level) throws ApiException {
    String  aasIdentifierAsBytes = ApiClient.base64UrlEncode(aasIdentifier);
    String  submodelIdentifierAsBytes = ApiClient.base64UrlEncode(submodelIdentifier);
  	return getSubmodelByIdMetadataAasRepositoryWithHttpInfoNoUrlEncoding(aasIdentifierAsBytes, submodelIdentifierAsBytes, level);
 	
 }


  /**
   * Returns the Submodel&#39;s metadata elements
   * 
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param level Determines the structural depth of the respective resource content (optional, default to deep)
   * @return ApiResponse&lt;SubmodelMetadata&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<SubmodelMetadata> getSubmodelByIdMetadataAasRepositoryWithHttpInfoNoUrlEncoding(String aasIdentifier, String submodelIdentifier, String level) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = getSubmodelByIdMetadataAasRepositoryRequestBuilder(aasIdentifier, submodelIdentifier, level);
    try {
      HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(
          localVarRequestBuilder.build(),
          HttpResponse.BodyHandlers.ofInputStream());
      if (memberVarResponseInterceptor != null) {
        memberVarResponseInterceptor.accept(localVarResponse);
      }
      try {
        if (localVarResponse.statusCode()/ 100 != 2) {
          throw getApiException("getSubmodelByIdMetadataAasRepository", localVarResponse);
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

  private HttpRequest.Builder getSubmodelByIdMetadataAasRepositoryRequestBuilder(String aasIdentifier, String submodelIdentifier, String level) throws ApiException {
    // verify the required parameter 'aasIdentifier' is set
    if (aasIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'aasIdentifier' when calling getSubmodelByIdMetadataAasRepository");
    }
    // verify the required parameter 'submodelIdentifier' is set
    if (submodelIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'submodelIdentifier' when calling getSubmodelByIdMetadataAasRepository");
    }

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/shells/{aasIdentifier}/submodels/{submodelIdentifier}/$metadata"
        .replace("{aasIdentifier}", ApiClient.urlEncode(aasIdentifier.toString()))
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
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param level Determines the structural depth of the respective resource content (optional, default to deep)
   * @return List&lt;String&gt;
   * @throws ApiException if fails to make API call
   */
  public List<String> getSubmodelByIdPathAasRepository(String aasIdentifier, String submodelIdentifier, String level) throws ApiException {

    ApiResponse<List<String>> localVarResponse = getSubmodelByIdPathAasRepositoryWithHttpInfo(aasIdentifier, submodelIdentifier, level);
    return localVarResponse.getData();
  }

  /**
   * Returns the Submodel&#39;s metadata elements
   * 
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param level Determines the structural depth of the respective resource content (optional, default to deep)
   * @return ApiResponse&lt;List&lt;String&gt;&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<List<String>> getSubmodelByIdPathAasRepositoryWithHttpInfo(String aasIdentifier, String submodelIdentifier, String level) throws ApiException {
    String  aasIdentifierAsBytes = ApiClient.base64UrlEncode(aasIdentifier);
    String  submodelIdentifierAsBytes = ApiClient.base64UrlEncode(submodelIdentifier);
  	return getSubmodelByIdPathAasRepositoryWithHttpInfoNoUrlEncoding(aasIdentifierAsBytes, submodelIdentifierAsBytes, level);
 	
 }


  /**
   * Returns the Submodel&#39;s metadata elements
   * 
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param level Determines the structural depth of the respective resource content (optional, default to deep)
   * @return ApiResponse&lt;List&lt;String&gt;&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<List<String>> getSubmodelByIdPathAasRepositoryWithHttpInfoNoUrlEncoding(String aasIdentifier, String submodelIdentifier, String level) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = getSubmodelByIdPathAasRepositoryRequestBuilder(aasIdentifier, submodelIdentifier, level);
    try {
      HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(
          localVarRequestBuilder.build(),
          HttpResponse.BodyHandlers.ofInputStream());
      if (memberVarResponseInterceptor != null) {
        memberVarResponseInterceptor.accept(localVarResponse);
      }
      try {
        if (localVarResponse.statusCode()/ 100 != 2) {
          throw getApiException("getSubmodelByIdPathAasRepository", localVarResponse);
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

  private HttpRequest.Builder getSubmodelByIdPathAasRepositoryRequestBuilder(String aasIdentifier, String submodelIdentifier, String level) throws ApiException {
    // verify the required parameter 'aasIdentifier' is set
    if (aasIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'aasIdentifier' when calling getSubmodelByIdPathAasRepository");
    }
    // verify the required parameter 'submodelIdentifier' is set
    if (submodelIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'submodelIdentifier' when calling getSubmodelByIdPathAasRepository");
    }

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/shells/{aasIdentifier}/submodels/{submodelIdentifier}/$path"
        .replace("{aasIdentifier}", ApiClient.urlEncode(aasIdentifier.toString()))
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
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @return Reference
   * @throws ApiException if fails to make API call
   */
  public Reference getSubmodelByIdReferenceAasRepository(String aasIdentifier, String submodelIdentifier) throws ApiException {

    ApiResponse<Reference> localVarResponse = getSubmodelByIdReferenceAasRepositoryWithHttpInfo(aasIdentifier, submodelIdentifier);
    return localVarResponse.getData();
  }

  /**
   * Returns the Submodel as a Reference
   * 
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @return ApiResponse&lt;Reference&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<Reference> getSubmodelByIdReferenceAasRepositoryWithHttpInfo(String aasIdentifier, String submodelIdentifier) throws ApiException {
    String  aasIdentifierAsBytes = ApiClient.base64UrlEncode(aasIdentifier);
    String  submodelIdentifierAsBytes = ApiClient.base64UrlEncode(submodelIdentifier);
  	return getSubmodelByIdReferenceAasRepositoryWithHttpInfoNoUrlEncoding(aasIdentifierAsBytes, submodelIdentifierAsBytes);
 	
 }


  /**
   * Returns the Submodel as a Reference
   * 
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @return ApiResponse&lt;Reference&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<Reference> getSubmodelByIdReferenceAasRepositoryWithHttpInfoNoUrlEncoding(String aasIdentifier, String submodelIdentifier) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = getSubmodelByIdReferenceAasRepositoryRequestBuilder(aasIdentifier, submodelIdentifier);
    try {
      HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(
          localVarRequestBuilder.build(),
          HttpResponse.BodyHandlers.ofInputStream());
      if (memberVarResponseInterceptor != null) {
        memberVarResponseInterceptor.accept(localVarResponse);
      }
      try {
        if (localVarResponse.statusCode()/ 100 != 2) {
          throw getApiException("getSubmodelByIdReferenceAasRepository", localVarResponse);
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

  private HttpRequest.Builder getSubmodelByIdReferenceAasRepositoryRequestBuilder(String aasIdentifier, String submodelIdentifier) throws ApiException {
    // verify the required parameter 'aasIdentifier' is set
    if (aasIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'aasIdentifier' when calling getSubmodelByIdReferenceAasRepository");
    }
    // verify the required parameter 'submodelIdentifier' is set
    if (submodelIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'submodelIdentifier' when calling getSubmodelByIdReferenceAasRepository");
    }

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/shells/{aasIdentifier}/submodels/{submodelIdentifier}/$reference"
        .replace("{aasIdentifier}", ApiClient.urlEncode(aasIdentifier.toString()))
        .replace("{submodelIdentifier}", ApiClient.urlEncode(submodelIdentifier.toString()));

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
   * Returns the Submodel&#39;s ValueOnly representation
   * 
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param level Determines the structural depth of the respective resource content (optional, default to deep)
   * @param extent Determines to which extent the resource is being serialized (optional, default to withoutBlobValue)
   * @return SubmodelValue
   * @throws ApiException if fails to make API call
   */
  public SubmodelValue getSubmodelByIdValueOnlyAasRepository(String aasIdentifier, String submodelIdentifier, String level, String extent) throws ApiException {

    ApiResponse<SubmodelValue> localVarResponse = getSubmodelByIdValueOnlyAasRepositoryWithHttpInfo(aasIdentifier, submodelIdentifier, level, extent);
    return localVarResponse.getData();
  }

  /**
   * Returns the Submodel&#39;s ValueOnly representation
   * 
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param level Determines the structural depth of the respective resource content (optional, default to deep)
   * @param extent Determines to which extent the resource is being serialized (optional, default to withoutBlobValue)
   * @return ApiResponse&lt;SubmodelValue&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<SubmodelValue> getSubmodelByIdValueOnlyAasRepositoryWithHttpInfo(String aasIdentifier, String submodelIdentifier, String level, String extent) throws ApiException {
    String  aasIdentifierAsBytes = ApiClient.base64UrlEncode(aasIdentifier);
    String  submodelIdentifierAsBytes = ApiClient.base64UrlEncode(submodelIdentifier);
  	return getSubmodelByIdValueOnlyAasRepositoryWithHttpInfoNoUrlEncoding(aasIdentifierAsBytes, submodelIdentifierAsBytes, level, extent);
 	
 }


  /**
   * Returns the Submodel&#39;s ValueOnly representation
   * 
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param level Determines the structural depth of the respective resource content (optional, default to deep)
   * @param extent Determines to which extent the resource is being serialized (optional, default to withoutBlobValue)
   * @return ApiResponse&lt;SubmodelValue&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<SubmodelValue> getSubmodelByIdValueOnlyAasRepositoryWithHttpInfoNoUrlEncoding(String aasIdentifier, String submodelIdentifier, String level, String extent) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = getSubmodelByIdValueOnlyAasRepositoryRequestBuilder(aasIdentifier, submodelIdentifier, level, extent);
    try {
      HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(
          localVarRequestBuilder.build(),
          HttpResponse.BodyHandlers.ofInputStream());
      if (memberVarResponseInterceptor != null) {
        memberVarResponseInterceptor.accept(localVarResponse);
      }
      try {
        if (localVarResponse.statusCode()/ 100 != 2) {
          throw getApiException("getSubmodelByIdValueOnlyAasRepository", localVarResponse);
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

  private HttpRequest.Builder getSubmodelByIdValueOnlyAasRepositoryRequestBuilder(String aasIdentifier, String submodelIdentifier, String level, String extent) throws ApiException {
    // verify the required parameter 'aasIdentifier' is set
    if (aasIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'aasIdentifier' when calling getSubmodelByIdValueOnlyAasRepository");
    }
    // verify the required parameter 'submodelIdentifier' is set
    if (submodelIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'submodelIdentifier' when calling getSubmodelByIdValueOnlyAasRepository");
    }

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/shells/{aasIdentifier}/submodels/{submodelIdentifier}/$value"
        .replace("{aasIdentifier}", ApiClient.urlEncode(aasIdentifier.toString()))
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
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param level Determines the structural depth of the respective resource content (optional, default to deep)
   * @param extent Determines to which extent the resource is being serialized (optional, default to withoutBlobValue)
   * @return SubmodelElement
   * @throws ApiException if fails to make API call
   */
  public SubmodelElement getSubmodelElementByPathAasRepository(String aasIdentifier, String submodelIdentifier, String idShortPath, String level, String extent) throws ApiException {

    ApiResponse<SubmodelElement> localVarResponse = getSubmodelElementByPathAasRepositoryWithHttpInfo(aasIdentifier, submodelIdentifier, idShortPath, level, extent);
    return localVarResponse.getData();
  }

  /**
   * Returns a specific submodel element from the Submodel at a specified path
   * 
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param level Determines the structural depth of the respective resource content (optional, default to deep)
   * @param extent Determines to which extent the resource is being serialized (optional, default to withoutBlobValue)
   * @return ApiResponse&lt;SubmodelElement&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<SubmodelElement> getSubmodelElementByPathAasRepositoryWithHttpInfo(String aasIdentifier, String submodelIdentifier, String idShortPath, String level, String extent) throws ApiException {
    String  aasIdentifierAsBytes = ApiClient.base64UrlEncode(aasIdentifier);
    String  submodelIdentifierAsBytes = ApiClient.base64UrlEncode(submodelIdentifier);
  	return getSubmodelElementByPathAasRepositoryWithHttpInfoNoUrlEncoding(aasIdentifierAsBytes, submodelIdentifierAsBytes, idShortPath, level, extent);
 	
 }


  /**
   * Returns a specific submodel element from the Submodel at a specified path
   * 
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param level Determines the structural depth of the respective resource content (optional, default to deep)
   * @param extent Determines to which extent the resource is being serialized (optional, default to withoutBlobValue)
   * @return ApiResponse&lt;SubmodelElement&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<SubmodelElement> getSubmodelElementByPathAasRepositoryWithHttpInfoNoUrlEncoding(String aasIdentifier, String submodelIdentifier, String idShortPath, String level, String extent) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = getSubmodelElementByPathAasRepositoryRequestBuilder(aasIdentifier, submodelIdentifier, idShortPath, level, extent);
    try {
      HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(
          localVarRequestBuilder.build(),
          HttpResponse.BodyHandlers.ofInputStream());
      if (memberVarResponseInterceptor != null) {
        memberVarResponseInterceptor.accept(localVarResponse);
      }
      try {
        if (localVarResponse.statusCode()/ 100 != 2) {
          throw getApiException("getSubmodelElementByPathAasRepository", localVarResponse);
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

  private HttpRequest.Builder getSubmodelElementByPathAasRepositoryRequestBuilder(String aasIdentifier, String submodelIdentifier, String idShortPath, String level, String extent) throws ApiException {
    // verify the required parameter 'aasIdentifier' is set
    if (aasIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'aasIdentifier' when calling getSubmodelElementByPathAasRepository");
    }
    // verify the required parameter 'submodelIdentifier' is set
    if (submodelIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'submodelIdentifier' when calling getSubmodelElementByPathAasRepository");
    }
    // verify the required parameter 'idShortPath' is set
    if (idShortPath == null) {
      throw new ApiException(400, "Missing the required parameter 'idShortPath' when calling getSubmodelElementByPathAasRepository");
    }

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/shells/{aasIdentifier}/submodels/{submodelIdentifier}/submodel-elements/{idShortPath}"
        .replace("{aasIdentifier}", ApiClient.urlEncode(aasIdentifier.toString()))
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
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param level Determines the structural depth of the respective resource content (optional, default to deep)
   * @return SubmodelElementMetadata
   * @throws ApiException if fails to make API call
   */
  public SubmodelElementMetadata getSubmodelElementByPathMetadataAasRepository(String aasIdentifier, String submodelIdentifier, String idShortPath, String level) throws ApiException {

    ApiResponse<SubmodelElementMetadata> localVarResponse = getSubmodelElementByPathMetadataAasRepositoryWithHttpInfo(aasIdentifier, submodelIdentifier, idShortPath, level);
    return localVarResponse.getData();
  }

  /**
   * Returns the metadata attributes if a specific submodel element from the Submodel at a specified path
   * 
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param level Determines the structural depth of the respective resource content (optional, default to deep)
   * @return ApiResponse&lt;SubmodelElementMetadata&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<SubmodelElementMetadata> getSubmodelElementByPathMetadataAasRepositoryWithHttpInfo(String aasIdentifier, String submodelIdentifier, String idShortPath, String level) throws ApiException {
    String  aasIdentifierAsBytes = ApiClient.base64UrlEncode(aasIdentifier);
    String  submodelIdentifierAsBytes = ApiClient.base64UrlEncode(submodelIdentifier);
  	return getSubmodelElementByPathMetadataAasRepositoryWithHttpInfoNoUrlEncoding(aasIdentifierAsBytes, submodelIdentifierAsBytes, idShortPath, level);
 	
 }


  /**
   * Returns the metadata attributes if a specific submodel element from the Submodel at a specified path
   * 
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param level Determines the structural depth of the respective resource content (optional, default to deep)
   * @return ApiResponse&lt;SubmodelElementMetadata&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<SubmodelElementMetadata> getSubmodelElementByPathMetadataAasRepositoryWithHttpInfoNoUrlEncoding(String aasIdentifier, String submodelIdentifier, String idShortPath, String level) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = getSubmodelElementByPathMetadataAasRepositoryRequestBuilder(aasIdentifier, submodelIdentifier, idShortPath, level);
    try {
      HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(
          localVarRequestBuilder.build(),
          HttpResponse.BodyHandlers.ofInputStream());
      if (memberVarResponseInterceptor != null) {
        memberVarResponseInterceptor.accept(localVarResponse);
      }
      try {
        if (localVarResponse.statusCode()/ 100 != 2) {
          throw getApiException("getSubmodelElementByPathMetadataAasRepository", localVarResponse);
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

  private HttpRequest.Builder getSubmodelElementByPathMetadataAasRepositoryRequestBuilder(String aasIdentifier, String submodelIdentifier, String idShortPath, String level) throws ApiException {
    // verify the required parameter 'aasIdentifier' is set
    if (aasIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'aasIdentifier' when calling getSubmodelElementByPathMetadataAasRepository");
    }
    // verify the required parameter 'submodelIdentifier' is set
    if (submodelIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'submodelIdentifier' when calling getSubmodelElementByPathMetadataAasRepository");
    }
    // verify the required parameter 'idShortPath' is set
    if (idShortPath == null) {
      throw new ApiException(400, "Missing the required parameter 'idShortPath' when calling getSubmodelElementByPathMetadataAasRepository");
    }

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/shells/{aasIdentifier}/submodels/{submodelIdentifier}/submodel-elements/{idShortPath}/$metadata"
        .replace("{aasIdentifier}", ApiClient.urlEncode(aasIdentifier.toString()))
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
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param level Determines the structural depth of the respective resource content (optional, default to deep)
   * @return String
   * @throws ApiException if fails to make API call
   */
  public String getSubmodelElementByPathPathAasRepository(String aasIdentifier, String submodelIdentifier, String idShortPath, String level) throws ApiException {

    ApiResponse<String> localVarResponse = getSubmodelElementByPathPathAasRepositoryWithHttpInfo(aasIdentifier, submodelIdentifier, idShortPath, level);
    return localVarResponse.getData();
  }

  /**
   * Returns a specific submodel element from the Submodel at a specified path in the Path notation
   * 
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param level Determines the structural depth of the respective resource content (optional, default to deep)
   * @return ApiResponse&lt;String&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<String> getSubmodelElementByPathPathAasRepositoryWithHttpInfo(String aasIdentifier, String submodelIdentifier, String idShortPath, String level) throws ApiException {
    String  aasIdentifierAsBytes = ApiClient.base64UrlEncode(aasIdentifier);
    String  submodelIdentifierAsBytes = ApiClient.base64UrlEncode(submodelIdentifier);
  	return getSubmodelElementByPathPathAasRepositoryWithHttpInfoNoUrlEncoding(aasIdentifierAsBytes, submodelIdentifierAsBytes, idShortPath, level);
 	
 }


  /**
   * Returns a specific submodel element from the Submodel at a specified path in the Path notation
   * 
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param level Determines the structural depth of the respective resource content (optional, default to deep)
   * @return ApiResponse&lt;String&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<String> getSubmodelElementByPathPathAasRepositoryWithHttpInfoNoUrlEncoding(String aasIdentifier, String submodelIdentifier, String idShortPath, String level) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = getSubmodelElementByPathPathAasRepositoryRequestBuilder(aasIdentifier, submodelIdentifier, idShortPath, level);
    try {
      HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(
          localVarRequestBuilder.build(),
          HttpResponse.BodyHandlers.ofInputStream());
      if (memberVarResponseInterceptor != null) {
        memberVarResponseInterceptor.accept(localVarResponse);
      }
      try {
        if (localVarResponse.statusCode()/ 100 != 2) {
          throw getApiException("getSubmodelElementByPathPathAasRepository", localVarResponse);
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

  private HttpRequest.Builder getSubmodelElementByPathPathAasRepositoryRequestBuilder(String aasIdentifier, String submodelIdentifier, String idShortPath, String level) throws ApiException {
    // verify the required parameter 'aasIdentifier' is set
    if (aasIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'aasIdentifier' when calling getSubmodelElementByPathPathAasRepository");
    }
    // verify the required parameter 'submodelIdentifier' is set
    if (submodelIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'submodelIdentifier' when calling getSubmodelElementByPathPathAasRepository");
    }
    // verify the required parameter 'idShortPath' is set
    if (idShortPath == null) {
      throw new ApiException(400, "Missing the required parameter 'idShortPath' when calling getSubmodelElementByPathPathAasRepository");
    }

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/shells/{aasIdentifier}/submodels/{submodelIdentifier}/submodel-elements/{idShortPath}/$path"
        .replace("{aasIdentifier}", ApiClient.urlEncode(aasIdentifier.toString()))
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
   * Returns the Reference of a specific submodel element from the Submodel at a specified path
   * 
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param level Determines the structural depth of the respective resource content (optional, default to core)
   * @return Reference
   * @throws ApiException if fails to make API call
   */
  public Reference getSubmodelElementByPathReferenceAasRepository(String aasIdentifier, String submodelIdentifier, String idShortPath, String level) throws ApiException {

    ApiResponse<Reference> localVarResponse = getSubmodelElementByPathReferenceAasRepositoryWithHttpInfo(aasIdentifier, submodelIdentifier, idShortPath, level);
    return localVarResponse.getData();
  }

  /**
   * Returns the Reference of a specific submodel element from the Submodel at a specified path
   * 
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param level Determines the structural depth of the respective resource content (optional, default to core)
   * @return ApiResponse&lt;Reference&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<Reference> getSubmodelElementByPathReferenceAasRepositoryWithHttpInfo(String aasIdentifier, String submodelIdentifier, String idShortPath, String level) throws ApiException {
    String  aasIdentifierAsBytes = ApiClient.base64UrlEncode(aasIdentifier);
    String  submodelIdentifierAsBytes = ApiClient.base64UrlEncode(submodelIdentifier);
  	return getSubmodelElementByPathReferenceAasRepositoryWithHttpInfoNoUrlEncoding(aasIdentifierAsBytes, submodelIdentifierAsBytes, idShortPath, level);
 	
 }


  /**
   * Returns the Reference of a specific submodel element from the Submodel at a specified path
   * 
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param level Determines the structural depth of the respective resource content (optional, default to core)
   * @return ApiResponse&lt;Reference&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<Reference> getSubmodelElementByPathReferenceAasRepositoryWithHttpInfoNoUrlEncoding(String aasIdentifier, String submodelIdentifier, String idShortPath, String level) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = getSubmodelElementByPathReferenceAasRepositoryRequestBuilder(aasIdentifier, submodelIdentifier, idShortPath, level);
    try {
      HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(
          localVarRequestBuilder.build(),
          HttpResponse.BodyHandlers.ofInputStream());
      if (memberVarResponseInterceptor != null) {
        memberVarResponseInterceptor.accept(localVarResponse);
      }
      try {
        if (localVarResponse.statusCode()/ 100 != 2) {
          throw getApiException("getSubmodelElementByPathReferenceAasRepository", localVarResponse);
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

  private HttpRequest.Builder getSubmodelElementByPathReferenceAasRepositoryRequestBuilder(String aasIdentifier, String submodelIdentifier, String idShortPath, String level) throws ApiException {
    // verify the required parameter 'aasIdentifier' is set
    if (aasIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'aasIdentifier' when calling getSubmodelElementByPathReferenceAasRepository");
    }
    // verify the required parameter 'submodelIdentifier' is set
    if (submodelIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'submodelIdentifier' when calling getSubmodelElementByPathReferenceAasRepository");
    }
    // verify the required parameter 'idShortPath' is set
    if (idShortPath == null) {
      throw new ApiException(400, "Missing the required parameter 'idShortPath' when calling getSubmodelElementByPathReferenceAasRepository");
    }

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/shells/{aasIdentifier}/submodels/{submodelIdentifier}/submodel-elements/{idShortPath}/$reference"
        .replace("{aasIdentifier}", ApiClient.urlEncode(aasIdentifier.toString()))
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
   * Returns a specific submodel element from the Submodel at a specified path in the ValueOnly representation
   * 
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param level Determines the structural depth of the respective resource content (optional, default to deep)
   * @param extent Determines to which extent the resource is being serialized (optional, default to withoutBlobValue)
   * @return SubmodelElementValue
   * @throws ApiException if fails to make API call
   */
  public SubmodelElementValue getSubmodelElementByPathValueOnlyAasRepository(String aasIdentifier, String submodelIdentifier, String idShortPath, String level, String extent) throws ApiException {

    ApiResponse<SubmodelElementValue> localVarResponse = getSubmodelElementByPathValueOnlyAasRepositoryWithHttpInfo(aasIdentifier, submodelIdentifier, idShortPath, level, extent);
    return localVarResponse.getData();
  }

  /**
   * Returns a specific submodel element from the Submodel at a specified path in the ValueOnly representation
   * 
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param level Determines the structural depth of the respective resource content (optional, default to deep)
   * @param extent Determines to which extent the resource is being serialized (optional, default to withoutBlobValue)
   * @return ApiResponse&lt;SubmodelElementValue&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<SubmodelElementValue> getSubmodelElementByPathValueOnlyAasRepositoryWithHttpInfo(String aasIdentifier, String submodelIdentifier, String idShortPath, String level, String extent) throws ApiException {
    String  aasIdentifierAsBytes = ApiClient.base64UrlEncode(aasIdentifier);
    String  submodelIdentifierAsBytes = ApiClient.base64UrlEncode(submodelIdentifier);
  	return getSubmodelElementByPathValueOnlyAasRepositoryWithHttpInfoNoUrlEncoding(aasIdentifierAsBytes, submodelIdentifierAsBytes, idShortPath, level, extent);
 	
 }


  /**
   * Returns a specific submodel element from the Submodel at a specified path in the ValueOnly representation
   * 
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param level Determines the structural depth of the respective resource content (optional, default to deep)
   * @param extent Determines to which extent the resource is being serialized (optional, default to withoutBlobValue)
   * @return ApiResponse&lt;SubmodelElementValue&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<SubmodelElementValue> getSubmodelElementByPathValueOnlyAasRepositoryWithHttpInfoNoUrlEncoding(String aasIdentifier, String submodelIdentifier, String idShortPath, String level, String extent) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = getSubmodelElementByPathValueOnlyAasRepositoryRequestBuilder(aasIdentifier, submodelIdentifier, idShortPath, level, extent);
    try {
      HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(
          localVarRequestBuilder.build(),
          HttpResponse.BodyHandlers.ofInputStream());
      if (memberVarResponseInterceptor != null) {
        memberVarResponseInterceptor.accept(localVarResponse);
      }
      try {
        if (localVarResponse.statusCode()/ 100 != 2) {
          throw getApiException("getSubmodelElementByPathValueOnlyAasRepository", localVarResponse);
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

  private HttpRequest.Builder getSubmodelElementByPathValueOnlyAasRepositoryRequestBuilder(String aasIdentifier, String submodelIdentifier, String idShortPath, String level, String extent) throws ApiException {
    // verify the required parameter 'aasIdentifier' is set
    if (aasIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'aasIdentifier' when calling getSubmodelElementByPathValueOnlyAasRepository");
    }
    // verify the required parameter 'submodelIdentifier' is set
    if (submodelIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'submodelIdentifier' when calling getSubmodelElementByPathValueOnlyAasRepository");
    }
    // verify the required parameter 'idShortPath' is set
    if (idShortPath == null) {
      throw new ApiException(400, "Missing the required parameter 'idShortPath' when calling getSubmodelElementByPathValueOnlyAasRepository");
    }

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/shells/{aasIdentifier}/submodels/{submodelIdentifier}/submodel-elements/{idShortPath}/$value"
        .replace("{aasIdentifier}", ApiClient.urlEncode(aasIdentifier.toString()))
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
   * 
   * 
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @return File
   * @throws ApiException if fails to make API call
   */
  public File getThumbnailAasRepository(String aasIdentifier) throws ApiException {

    ApiResponse<File> localVarResponse = getThumbnailAasRepositoryWithHttpInfo(aasIdentifier);
    return localVarResponse.getData();
  }

  /**
   * 
   * 
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @return ApiResponse&lt;File&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<File> getThumbnailAasRepositoryWithHttpInfo(String aasIdentifier) throws ApiException {
    String  aasIdentifierAsBytes = ApiClient.base64UrlEncode(aasIdentifier);
  	return getThumbnailAasRepositoryWithHttpInfoNoUrlEncoding(aasIdentifierAsBytes);
 	
 }


  /**
   * 
   * 
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @return ApiResponse&lt;File&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<File> getThumbnailAasRepositoryWithHttpInfoNoUrlEncoding(String aasIdentifier) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = getThumbnailAasRepositoryRequestBuilder(aasIdentifier);
    try {
      HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(
          localVarRequestBuilder.build(),
          HttpResponse.BodyHandlers.ofInputStream());
      if (memberVarResponseInterceptor != null) {
        memberVarResponseInterceptor.accept(localVarResponse);
      }
      try {
        if (localVarResponse.statusCode()/ 100 != 2) {
          throw getApiException("getThumbnailAasRepository", localVarResponse);
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

  private HttpRequest.Builder getThumbnailAasRepositoryRequestBuilder(String aasIdentifier) throws ApiException {
    // verify the required parameter 'aasIdentifier' is set
    if (aasIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'aasIdentifier' when calling getThumbnailAasRepository");
    }

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/shells/{aasIdentifier}/asset-information/thumbnail"
        .replace("{aasIdentifier}", ApiClient.urlEncode(aasIdentifier.toString()));

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
   * Synchronously invokes an Operation at a specified path
   * 
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param operationRequest Operation request object (required)
   * @return OperationResult
   * @throws ApiException if fails to make API call
   */
  public OperationResult invokeOperationAasRepository(String aasIdentifier, String submodelIdentifier, String idShortPath, OperationRequest operationRequest) throws ApiException {

    ApiResponse<OperationResult> localVarResponse = invokeOperationAasRepositoryWithHttpInfo(aasIdentifier, submodelIdentifier, idShortPath, operationRequest);
    return localVarResponse.getData();
  }

  /**
   * Synchronously invokes an Operation at a specified path
   * 
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param operationRequest Operation request object (required)
   * @return ApiResponse&lt;OperationResult&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<OperationResult> invokeOperationAasRepositoryWithHttpInfo(String aasIdentifier, String submodelIdentifier, String idShortPath, OperationRequest operationRequest) throws ApiException {
    String  aasIdentifierAsBytes = ApiClient.base64UrlEncode(aasIdentifier);
    String  submodelIdentifierAsBytes = ApiClient.base64UrlEncode(submodelIdentifier);
  	return invokeOperationAasRepositoryWithHttpInfoNoUrlEncoding(aasIdentifierAsBytes, submodelIdentifierAsBytes, idShortPath, operationRequest);
 	
 }


  /**
   * Synchronously invokes an Operation at a specified path
   * 
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param operationRequest Operation request object (required)
   * @return ApiResponse&lt;OperationResult&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<OperationResult> invokeOperationAasRepositoryWithHttpInfoNoUrlEncoding(String aasIdentifier, String submodelIdentifier, String idShortPath, OperationRequest operationRequest) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = invokeOperationAasRepositoryRequestBuilder(aasIdentifier, submodelIdentifier, idShortPath, operationRequest);
    try {
      HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(
          localVarRequestBuilder.build(),
          HttpResponse.BodyHandlers.ofInputStream());
      if (memberVarResponseInterceptor != null) {
        memberVarResponseInterceptor.accept(localVarResponse);
      }
      try {
        if (localVarResponse.statusCode()/ 100 != 2) {
          throw getApiException("invokeOperationAasRepository", localVarResponse);
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

  private HttpRequest.Builder invokeOperationAasRepositoryRequestBuilder(String aasIdentifier, String submodelIdentifier, String idShortPath, OperationRequest operationRequest) throws ApiException {
    // verify the required parameter 'aasIdentifier' is set
    if (aasIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'aasIdentifier' when calling invokeOperationAasRepository");
    }
    // verify the required parameter 'submodelIdentifier' is set
    if (submodelIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'submodelIdentifier' when calling invokeOperationAasRepository");
    }
    // verify the required parameter 'idShortPath' is set
    if (idShortPath == null) {
      throw new ApiException(400, "Missing the required parameter 'idShortPath' when calling invokeOperationAasRepository");
    }
    // verify the required parameter 'operationRequest' is set
    if (operationRequest == null) {
      throw new ApiException(400, "Missing the required parameter 'operationRequest' when calling invokeOperationAasRepository");
    }

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/shells/{aasIdentifier}/submodels/{submodelIdentifier}/submodel-elements/{idShortPath}/invoke"
        .replace("{aasIdentifier}", ApiClient.urlEncode(aasIdentifier.toString()))
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
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param operationRequest Operation request object (required)
   * @throws ApiException if fails to make API call
   */
  public void invokeOperationAsyncAasRepository(String aasIdentifier, String submodelIdentifier, String idShortPath, OperationRequest operationRequest) throws ApiException {

    invokeOperationAsyncAasRepositoryWithHttpInfo(aasIdentifier, submodelIdentifier, idShortPath, operationRequest);
  }

  /**
   * Asynchronously invokes an Operation at a specified path
   * 
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param operationRequest Operation request object (required)
   * @return ApiResponse&lt;Void&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<Void> invokeOperationAsyncAasRepositoryWithHttpInfo(String aasIdentifier, String submodelIdentifier, String idShortPath, OperationRequest operationRequest) throws ApiException {
    String  aasIdentifierAsBytes = ApiClient.base64UrlEncode(aasIdentifier);
    String  submodelIdentifierAsBytes = ApiClient.base64UrlEncode(submodelIdentifier);
  	return invokeOperationAsyncAasRepositoryWithHttpInfoNoUrlEncoding(aasIdentifierAsBytes, submodelIdentifierAsBytes, idShortPath, operationRequest);
 	
 }


  /**
   * Asynchronously invokes an Operation at a specified path
   * 
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param operationRequest Operation request object (required)
   * @return ApiResponse&lt;Void&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<Void> invokeOperationAsyncAasRepositoryWithHttpInfoNoUrlEncoding(String aasIdentifier, String submodelIdentifier, String idShortPath, OperationRequest operationRequest) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = invokeOperationAsyncAasRepositoryRequestBuilder(aasIdentifier, submodelIdentifier, idShortPath, operationRequest);
    try {
      HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(
          localVarRequestBuilder.build(),
          HttpResponse.BodyHandlers.ofInputStream());
      if (memberVarResponseInterceptor != null) {
        memberVarResponseInterceptor.accept(localVarResponse);
      }
      try {
        if (localVarResponse.statusCode()/ 100 != 2) {
          throw getApiException("invokeOperationAsyncAasRepository", localVarResponse);
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

  private HttpRequest.Builder invokeOperationAsyncAasRepositoryRequestBuilder(String aasIdentifier, String submodelIdentifier, String idShortPath, OperationRequest operationRequest) throws ApiException {
    // verify the required parameter 'aasIdentifier' is set
    if (aasIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'aasIdentifier' when calling invokeOperationAsyncAasRepository");
    }
    // verify the required parameter 'submodelIdentifier' is set
    if (submodelIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'submodelIdentifier' when calling invokeOperationAsyncAasRepository");
    }
    // verify the required parameter 'idShortPath' is set
    if (idShortPath == null) {
      throw new ApiException(400, "Missing the required parameter 'idShortPath' when calling invokeOperationAsyncAasRepository");
    }
    // verify the required parameter 'operationRequest' is set
    if (operationRequest == null) {
      throw new ApiException(400, "Missing the required parameter 'operationRequest' when calling invokeOperationAsyncAasRepository");
    }

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/shells/{aasIdentifier}/submodels/{submodelIdentifier}/submodel-elements/{idShortPath}/invoke-async"
        .replace("{aasIdentifier}", ApiClient.urlEncode(aasIdentifier.toString()))
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
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param operationRequestValueOnly Operation request object (required)
   * @throws ApiException if fails to make API call
   */
  public void invokeOperationAsyncValueOnlyAasRepository(String aasIdentifier, String submodelIdentifier, String idShortPath, OperationRequestValueOnly operationRequestValueOnly) throws ApiException {

    invokeOperationAsyncValueOnlyAasRepositoryWithHttpInfo(aasIdentifier, submodelIdentifier, idShortPath, operationRequestValueOnly);
  }

  /**
   * Asynchronously invokes an Operation at a specified path
   * 
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param operationRequestValueOnly Operation request object (required)
   * @return ApiResponse&lt;Void&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<Void> invokeOperationAsyncValueOnlyAasRepositoryWithHttpInfo(String aasIdentifier, String submodelIdentifier, String idShortPath, OperationRequestValueOnly operationRequestValueOnly) throws ApiException {
    String  aasIdentifierAsBytes = ApiClient.base64UrlEncode(aasIdentifier);
    String  submodelIdentifierAsBytes = ApiClient.base64UrlEncode(submodelIdentifier);
  	return invokeOperationAsyncValueOnlyAasRepositoryWithHttpInfoNoUrlEncoding(aasIdentifierAsBytes, submodelIdentifierAsBytes, idShortPath, operationRequestValueOnly);
 	
 }


  /**
   * Asynchronously invokes an Operation at a specified path
   * 
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param operationRequestValueOnly Operation request object (required)
   * @return ApiResponse&lt;Void&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<Void> invokeOperationAsyncValueOnlyAasRepositoryWithHttpInfoNoUrlEncoding(String aasIdentifier, String submodelIdentifier, String idShortPath, OperationRequestValueOnly operationRequestValueOnly) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = invokeOperationAsyncValueOnlyAasRepositoryRequestBuilder(aasIdentifier, submodelIdentifier, idShortPath, operationRequestValueOnly);
    try {
      HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(
          localVarRequestBuilder.build(),
          HttpResponse.BodyHandlers.ofInputStream());
      if (memberVarResponseInterceptor != null) {
        memberVarResponseInterceptor.accept(localVarResponse);
      }
      try {
        if (localVarResponse.statusCode()/ 100 != 2) {
          throw getApiException("invokeOperationAsyncValueOnlyAasRepository", localVarResponse);
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

  private HttpRequest.Builder invokeOperationAsyncValueOnlyAasRepositoryRequestBuilder(String aasIdentifier, String submodelIdentifier, String idShortPath, OperationRequestValueOnly operationRequestValueOnly) throws ApiException {
    // verify the required parameter 'aasIdentifier' is set
    if (aasIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'aasIdentifier' when calling invokeOperationAsyncValueOnlyAasRepository");
    }
    // verify the required parameter 'submodelIdentifier' is set
    if (submodelIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'submodelIdentifier' when calling invokeOperationAsyncValueOnlyAasRepository");
    }
    // verify the required parameter 'idShortPath' is set
    if (idShortPath == null) {
      throw new ApiException(400, "Missing the required parameter 'idShortPath' when calling invokeOperationAsyncValueOnlyAasRepository");
    }
    // verify the required parameter 'operationRequestValueOnly' is set
    if (operationRequestValueOnly == null) {
      throw new ApiException(400, "Missing the required parameter 'operationRequestValueOnly' when calling invokeOperationAsyncValueOnlyAasRepository");
    }

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/shells/{aasIdentifier}/submodels/{submodelIdentifier}/submodel-elements/{idShortPath}/invoke-async/$value"
        .replace("{aasIdentifier}", ApiClient.urlEncode(aasIdentifier.toString()))
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
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param operationRequestValueOnly Operation request object (required)
   * @return OperationResultValueOnly
   * @throws ApiException if fails to make API call
   */
  public OperationResultValueOnly invokeOperationValueOnlyAasRepository(String aasIdentifier, String submodelIdentifier, String idShortPath, OperationRequestValueOnly operationRequestValueOnly) throws ApiException {

    ApiResponse<OperationResultValueOnly> localVarResponse = invokeOperationValueOnlyAasRepositoryWithHttpInfo(aasIdentifier, submodelIdentifier, idShortPath, operationRequestValueOnly);
    return localVarResponse.getData();
  }

  /**
   * Synchronously invokes an Operation at a specified path
   * 
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param operationRequestValueOnly Operation request object (required)
   * @return ApiResponse&lt;OperationResultValueOnly&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<OperationResultValueOnly> invokeOperationValueOnlyAasRepositoryWithHttpInfo(String aasIdentifier, String submodelIdentifier, String idShortPath, OperationRequestValueOnly operationRequestValueOnly) throws ApiException {
    String  aasIdentifierAsBytes = ApiClient.base64UrlEncode(aasIdentifier);
    String  submodelIdentifierAsBytes = ApiClient.base64UrlEncode(submodelIdentifier);
  	return invokeOperationValueOnlyAasRepositoryWithHttpInfoNoUrlEncoding(aasIdentifierAsBytes, submodelIdentifierAsBytes, idShortPath, operationRequestValueOnly);
 	
 }


  /**
   * Synchronously invokes an Operation at a specified path
   * 
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param operationRequestValueOnly Operation request object (required)
   * @return ApiResponse&lt;OperationResultValueOnly&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<OperationResultValueOnly> invokeOperationValueOnlyAasRepositoryWithHttpInfoNoUrlEncoding(String aasIdentifier, String submodelIdentifier, String idShortPath, OperationRequestValueOnly operationRequestValueOnly) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = invokeOperationValueOnlyAasRepositoryRequestBuilder(aasIdentifier, submodelIdentifier, idShortPath, operationRequestValueOnly);
    try {
      HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(
          localVarRequestBuilder.build(),
          HttpResponse.BodyHandlers.ofInputStream());
      if (memberVarResponseInterceptor != null) {
        memberVarResponseInterceptor.accept(localVarResponse);
      }
      try {
        if (localVarResponse.statusCode()/ 100 != 2) {
          throw getApiException("invokeOperationValueOnlyAasRepository", localVarResponse);
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

  private HttpRequest.Builder invokeOperationValueOnlyAasRepositoryRequestBuilder(String aasIdentifier, String submodelIdentifier, String idShortPath, OperationRequestValueOnly operationRequestValueOnly) throws ApiException {
    // verify the required parameter 'aasIdentifier' is set
    if (aasIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'aasIdentifier' when calling invokeOperationValueOnlyAasRepository");
    }
    // verify the required parameter 'submodelIdentifier' is set
    if (submodelIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'submodelIdentifier' when calling invokeOperationValueOnlyAasRepository");
    }
    // verify the required parameter 'idShortPath' is set
    if (idShortPath == null) {
      throw new ApiException(400, "Missing the required parameter 'idShortPath' when calling invokeOperationValueOnlyAasRepository");
    }
    // verify the required parameter 'operationRequestValueOnly' is set
    if (operationRequestValueOnly == null) {
      throw new ApiException(400, "Missing the required parameter 'operationRequestValueOnly' when calling invokeOperationValueOnlyAasRepository");
    }

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/shells/{aasIdentifier}/submodels/{submodelIdentifier}/submodel-elements/{idShortPath}/invoke/$value"
        .replace("{aasIdentifier}", ApiClient.urlEncode(aasIdentifier.toString()))
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
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodel Submodel object (required)
   * @param level Determines the structural depth of the respective resource content (optional, default to core)
   * @throws ApiException if fails to make API call
   */
  public void patchSubmodelAasRepository(String aasIdentifier, String submodelIdentifier, Submodel submodel, String level) throws ApiException {

    patchSubmodelAasRepositoryWithHttpInfo(aasIdentifier, submodelIdentifier, submodel, level);
  }

  /**
   * Updates the Submodel
   * 
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodel Submodel object (required)
   * @param level Determines the structural depth of the respective resource content (optional, default to core)
   * @return ApiResponse&lt;Void&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<Void> patchSubmodelAasRepositoryWithHttpInfo(String aasIdentifier, String submodelIdentifier, Submodel submodel, String level) throws ApiException {
    String  aasIdentifierAsBytes = ApiClient.base64UrlEncode(aasIdentifier);
    String  submodelIdentifierAsBytes = ApiClient.base64UrlEncode(submodelIdentifier);
  	return patchSubmodelAasRepositoryWithHttpInfoNoUrlEncoding(aasIdentifierAsBytes, submodelIdentifierAsBytes, submodel, level);
 	
 }


  /**
   * Updates the Submodel
   * 
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodel Submodel object (required)
   * @param level Determines the structural depth of the respective resource content (optional, default to core)
   * @return ApiResponse&lt;Void&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<Void> patchSubmodelAasRepositoryWithHttpInfoNoUrlEncoding(String aasIdentifier, String submodelIdentifier, Submodel submodel, String level) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = patchSubmodelAasRepositoryRequestBuilder(aasIdentifier, submodelIdentifier, submodel, level);
    try {
      HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(
          localVarRequestBuilder.build(),
          HttpResponse.BodyHandlers.ofInputStream());
      if (memberVarResponseInterceptor != null) {
        memberVarResponseInterceptor.accept(localVarResponse);
      }
      try {
        if (localVarResponse.statusCode()/ 100 != 2) {
          throw getApiException("patchSubmodelAasRepository", localVarResponse);
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

  private HttpRequest.Builder patchSubmodelAasRepositoryRequestBuilder(String aasIdentifier, String submodelIdentifier, Submodel submodel, String level) throws ApiException {
    // verify the required parameter 'aasIdentifier' is set
    if (aasIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'aasIdentifier' when calling patchSubmodelAasRepository");
    }
    // verify the required parameter 'submodelIdentifier' is set
    if (submodelIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'submodelIdentifier' when calling patchSubmodelAasRepository");
    }
    // verify the required parameter 'submodel' is set
    if (submodel == null) {
      throw new ApiException(400, "Missing the required parameter 'submodel' when calling patchSubmodelAasRepository");
    }

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/shells/{aasIdentifier}/submodels/{submodelIdentifier}"
        .replace("{aasIdentifier}", ApiClient.urlEncode(aasIdentifier.toString()))
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
   * Updates the metadata attributes of the Submodel
   * 
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelMetadata Submodel object (required)
   * @param level Determines the structural depth of the respective resource content (optional, default to core)
   * @throws ApiException if fails to make API call
   */
  public void patchSubmodelByIdMetadataAasRepository(String aasIdentifier, String submodelIdentifier, SubmodelMetadata submodelMetadata, String level) throws ApiException {

    patchSubmodelByIdMetadataAasRepositoryWithHttpInfo(aasIdentifier, submodelIdentifier, submodelMetadata, level);
  }

  /**
   * Updates the metadata attributes of the Submodel
   * 
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelMetadata Submodel object (required)
   * @param level Determines the structural depth of the respective resource content (optional, default to core)
   * @return ApiResponse&lt;Void&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<Void> patchSubmodelByIdMetadataAasRepositoryWithHttpInfo(String aasIdentifier, String submodelIdentifier, SubmodelMetadata submodelMetadata, String level) throws ApiException {
    String  aasIdentifierAsBytes = ApiClient.base64UrlEncode(aasIdentifier);
    String  submodelIdentifierAsBytes = ApiClient.base64UrlEncode(submodelIdentifier);
  	return patchSubmodelByIdMetadataAasRepositoryWithHttpInfoNoUrlEncoding(aasIdentifierAsBytes, submodelIdentifierAsBytes, submodelMetadata, level);
 	
 }


  /**
   * Updates the metadata attributes of the Submodel
   * 
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelMetadata Submodel object (required)
   * @param level Determines the structural depth of the respective resource content (optional, default to core)
   * @return ApiResponse&lt;Void&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<Void> patchSubmodelByIdMetadataAasRepositoryWithHttpInfoNoUrlEncoding(String aasIdentifier, String submodelIdentifier, SubmodelMetadata submodelMetadata, String level) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = patchSubmodelByIdMetadataAasRepositoryRequestBuilder(aasIdentifier, submodelIdentifier, submodelMetadata, level);
    try {
      HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(
          localVarRequestBuilder.build(),
          HttpResponse.BodyHandlers.ofInputStream());
      if (memberVarResponseInterceptor != null) {
        memberVarResponseInterceptor.accept(localVarResponse);
      }
      try {
        if (localVarResponse.statusCode()/ 100 != 2) {
          throw getApiException("patchSubmodelByIdMetadataAasRepository", localVarResponse);
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

  private HttpRequest.Builder patchSubmodelByIdMetadataAasRepositoryRequestBuilder(String aasIdentifier, String submodelIdentifier, SubmodelMetadata submodelMetadata, String level) throws ApiException {
    // verify the required parameter 'aasIdentifier' is set
    if (aasIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'aasIdentifier' when calling patchSubmodelByIdMetadataAasRepository");
    }
    // verify the required parameter 'submodelIdentifier' is set
    if (submodelIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'submodelIdentifier' when calling patchSubmodelByIdMetadataAasRepository");
    }
    // verify the required parameter 'submodelMetadata' is set
    if (submodelMetadata == null) {
      throw new ApiException(400, "Missing the required parameter 'submodelMetadata' when calling patchSubmodelByIdMetadataAasRepository");
    }

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/shells/{aasIdentifier}/submodels/{submodelIdentifier}/$metadata"
        .replace("{aasIdentifier}", ApiClient.urlEncode(aasIdentifier.toString()))
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
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelValue Submodel object in the ValueOnly representation (required)
   * @param level Determines the structural depth of the respective resource content (optional, default to core)
   * @throws ApiException if fails to make API call
   */
  public void patchSubmodelByIdValueOnlyAasRepository(String aasIdentifier, String submodelIdentifier, SubmodelValue submodelValue, String level) throws ApiException {

    patchSubmodelByIdValueOnlyAasRepositoryWithHttpInfo(aasIdentifier, submodelIdentifier, submodelValue, level);
  }

  /**
   * Updates teh values of the Submodel
   * 
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelValue Submodel object in the ValueOnly representation (required)
   * @param level Determines the structural depth of the respective resource content (optional, default to core)
   * @return ApiResponse&lt;Void&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<Void> patchSubmodelByIdValueOnlyAasRepositoryWithHttpInfo(String aasIdentifier, String submodelIdentifier, SubmodelValue submodelValue, String level) throws ApiException {
    String  aasIdentifierAsBytes = ApiClient.base64UrlEncode(aasIdentifier);
    String  submodelIdentifierAsBytes = ApiClient.base64UrlEncode(submodelIdentifier);
  	return patchSubmodelByIdValueOnlyAasRepositoryWithHttpInfoNoUrlEncoding(aasIdentifierAsBytes, submodelIdentifierAsBytes, submodelValue, level);
 	
 }


  /**
   * Updates teh values of the Submodel
   * 
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelValue Submodel object in the ValueOnly representation (required)
   * @param level Determines the structural depth of the respective resource content (optional, default to core)
   * @return ApiResponse&lt;Void&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<Void> patchSubmodelByIdValueOnlyAasRepositoryWithHttpInfoNoUrlEncoding(String aasIdentifier, String submodelIdentifier, SubmodelValue submodelValue, String level) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = patchSubmodelByIdValueOnlyAasRepositoryRequestBuilder(aasIdentifier, submodelIdentifier, submodelValue, level);
    try {
      HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(
          localVarRequestBuilder.build(),
          HttpResponse.BodyHandlers.ofInputStream());
      if (memberVarResponseInterceptor != null) {
        memberVarResponseInterceptor.accept(localVarResponse);
      }
      try {
        if (localVarResponse.statusCode()/ 100 != 2) {
          throw getApiException("patchSubmodelByIdValueOnlyAasRepository", localVarResponse);
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

  private HttpRequest.Builder patchSubmodelByIdValueOnlyAasRepositoryRequestBuilder(String aasIdentifier, String submodelIdentifier, SubmodelValue submodelValue, String level) throws ApiException {
    // verify the required parameter 'aasIdentifier' is set
    if (aasIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'aasIdentifier' when calling patchSubmodelByIdValueOnlyAasRepository");
    }
    // verify the required parameter 'submodelIdentifier' is set
    if (submodelIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'submodelIdentifier' when calling patchSubmodelByIdValueOnlyAasRepository");
    }
    // verify the required parameter 'submodelValue' is set
    if (submodelValue == null) {
      throw new ApiException(400, "Missing the required parameter 'submodelValue' when calling patchSubmodelByIdValueOnlyAasRepository");
    }

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/shells/{aasIdentifier}/submodels/{submodelIdentifier}/$value"
        .replace("{aasIdentifier}", ApiClient.urlEncode(aasIdentifier.toString()))
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
   * Updates an existing submodel element value at a specified path within submodel elements hierarchy
   * 
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param submodelElement The updated value of the submodel element (required)
   * @param level Determines the structural depth of the respective resource content (optional, default to deep)
   * @throws ApiException if fails to make API call
   */
  public void patchSubmodelElementValueByPathAasRepository(String aasIdentifier, String submodelIdentifier, String idShortPath, SubmodelElement submodelElement, String level) throws ApiException {

    patchSubmodelElementValueByPathAasRepositoryWithHttpInfo(aasIdentifier, submodelIdentifier, idShortPath, submodelElement, level);
  }

  /**
   * Updates an existing submodel element value at a specified path within submodel elements hierarchy
   * 
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param submodelElement The updated value of the submodel element (required)
   * @param level Determines the structural depth of the respective resource content (optional, default to deep)
   * @return ApiResponse&lt;Void&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<Void> patchSubmodelElementValueByPathAasRepositoryWithHttpInfo(String aasIdentifier, String submodelIdentifier, String idShortPath, SubmodelElement submodelElement, String level) throws ApiException {
    String  aasIdentifierAsBytes = ApiClient.base64UrlEncode(aasIdentifier);
    String  submodelIdentifierAsBytes = ApiClient.base64UrlEncode(submodelIdentifier);
  	return patchSubmodelElementValueByPathAasRepositoryWithHttpInfoNoUrlEncoding(aasIdentifierAsBytes, submodelIdentifierAsBytes, idShortPath, submodelElement, level);
 	
 }


  /**
   * Updates an existing submodel element value at a specified path within submodel elements hierarchy
   * 
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param submodelElement The updated value of the submodel element (required)
   * @param level Determines the structural depth of the respective resource content (optional, default to deep)
   * @return ApiResponse&lt;Void&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<Void> patchSubmodelElementValueByPathAasRepositoryWithHttpInfoNoUrlEncoding(String aasIdentifier, String submodelIdentifier, String idShortPath, SubmodelElement submodelElement, String level) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = patchSubmodelElementValueByPathAasRepositoryRequestBuilder(aasIdentifier, submodelIdentifier, idShortPath, submodelElement, level);
    try {
      HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(
          localVarRequestBuilder.build(),
          HttpResponse.BodyHandlers.ofInputStream());
      if (memberVarResponseInterceptor != null) {
        memberVarResponseInterceptor.accept(localVarResponse);
      }
      try {
        if (localVarResponse.statusCode()/ 100 != 2) {
          throw getApiException("patchSubmodelElementValueByPathAasRepository", localVarResponse);
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

  private HttpRequest.Builder patchSubmodelElementValueByPathAasRepositoryRequestBuilder(String aasIdentifier, String submodelIdentifier, String idShortPath, SubmodelElement submodelElement, String level) throws ApiException {
    // verify the required parameter 'aasIdentifier' is set
    if (aasIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'aasIdentifier' when calling patchSubmodelElementValueByPathAasRepository");
    }
    // verify the required parameter 'submodelIdentifier' is set
    if (submodelIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'submodelIdentifier' when calling patchSubmodelElementValueByPathAasRepository");
    }
    // verify the required parameter 'idShortPath' is set
    if (idShortPath == null) {
      throw new ApiException(400, "Missing the required parameter 'idShortPath' when calling patchSubmodelElementValueByPathAasRepository");
    }
    // verify the required parameter 'submodelElement' is set
    if (submodelElement == null) {
      throw new ApiException(400, "Missing the required parameter 'submodelElement' when calling patchSubmodelElementValueByPathAasRepository");
    }

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/shells/{aasIdentifier}/submodels/{submodelIdentifier}/submodel-elements/{idShortPath}"
        .replace("{aasIdentifier}", ApiClient.urlEncode(aasIdentifier.toString()))
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
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param submodelElementMetadata The updated metadata attributes of the submodel element (required)
   * @param level Determines the structural depth of the respective resource content (optional, default to deep)
   * @throws ApiException if fails to make API call
   */
  public void patchSubmodelElementValueByPathMetadata(String aasIdentifier, String submodelIdentifier, String idShortPath, SubmodelElementMetadata submodelElementMetadata, String level) throws ApiException {

    patchSubmodelElementValueByPathMetadataWithHttpInfo(aasIdentifier, submodelIdentifier, idShortPath, submodelElementMetadata, level);
  }

  /**
   * Updates the metadata attributes of an existing submodel element value at a specified path within submodel elements hierarchy
   * 
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param submodelElementMetadata The updated metadata attributes of the submodel element (required)
   * @param level Determines the structural depth of the respective resource content (optional, default to deep)
   * @return ApiResponse&lt;Void&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<Void> patchSubmodelElementValueByPathMetadataWithHttpInfo(String aasIdentifier, String submodelIdentifier, String idShortPath, SubmodelElementMetadata submodelElementMetadata, String level) throws ApiException {
    String  aasIdentifierAsBytes = ApiClient.base64UrlEncode(aasIdentifier);
    String  submodelIdentifierAsBytes = ApiClient.base64UrlEncode(submodelIdentifier);
  	return patchSubmodelElementValueByPathMetadataWithHttpInfoNoUrlEncoding(aasIdentifierAsBytes, submodelIdentifierAsBytes, idShortPath, submodelElementMetadata, level);
 	
 }


  /**
   * Updates the metadata attributes of an existing submodel element value at a specified path within submodel elements hierarchy
   * 
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param submodelElementMetadata The updated metadata attributes of the submodel element (required)
   * @param level Determines the structural depth of the respective resource content (optional, default to deep)
   * @return ApiResponse&lt;Void&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<Void> patchSubmodelElementValueByPathMetadataWithHttpInfoNoUrlEncoding(String aasIdentifier, String submodelIdentifier, String idShortPath, SubmodelElementMetadata submodelElementMetadata, String level) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = patchSubmodelElementValueByPathMetadataRequestBuilder(aasIdentifier, submodelIdentifier, idShortPath, submodelElementMetadata, level);
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

  private HttpRequest.Builder patchSubmodelElementValueByPathMetadataRequestBuilder(String aasIdentifier, String submodelIdentifier, String idShortPath, SubmodelElementMetadata submodelElementMetadata, String level) throws ApiException {
    // verify the required parameter 'aasIdentifier' is set
    if (aasIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'aasIdentifier' when calling patchSubmodelElementValueByPathMetadata");
    }
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

    String localVarPath = "/shells/{aasIdentifier}/submodels/{submodelIdentifier}/submodel-elements/{idShortPath}/$metadata"
        .replace("{aasIdentifier}", ApiClient.urlEncode(aasIdentifier.toString()))
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
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param submodelElementValue The updated value of the submodel element (required)
   * @param level Determines the structural depth of the respective resource content (optional, default to deep)
   * @throws ApiException if fails to make API call
   */
  public void patchSubmodelElementValueByPathValueOnly(String aasIdentifier, String submodelIdentifier, String idShortPath, SubmodelElementValue submodelElementValue, String level) throws ApiException {

    patchSubmodelElementValueByPathValueOnlyWithHttpInfo(aasIdentifier, submodelIdentifier, idShortPath, submodelElementValue, level);
  }

  /**
   * Updates the value of an existing submodel element value at a specified path within submodel elements hierarchy
   * 
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param submodelElementValue The updated value of the submodel element (required)
   * @param level Determines the structural depth of the respective resource content (optional, default to deep)
   * @return ApiResponse&lt;Void&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<Void> patchSubmodelElementValueByPathValueOnlyWithHttpInfo(String aasIdentifier, String submodelIdentifier, String idShortPath, SubmodelElementValue submodelElementValue, String level) throws ApiException {
    String  aasIdentifierAsBytes = ApiClient.base64UrlEncode(aasIdentifier);
    String  submodelIdentifierAsBytes = ApiClient.base64UrlEncode(submodelIdentifier);
  	return patchSubmodelElementValueByPathValueOnlyWithHttpInfoNoUrlEncoding(aasIdentifierAsBytes, submodelIdentifierAsBytes, idShortPath, submodelElementValue, level);
 	
 }


  /**
   * Updates the value of an existing submodel element value at a specified path within submodel elements hierarchy
   * 
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param submodelElementValue The updated value of the submodel element (required)
   * @param level Determines the structural depth of the respective resource content (optional, default to deep)
   * @return ApiResponse&lt;Void&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<Void> patchSubmodelElementValueByPathValueOnlyWithHttpInfoNoUrlEncoding(String aasIdentifier, String submodelIdentifier, String idShortPath, SubmodelElementValue submodelElementValue, String level) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = patchSubmodelElementValueByPathValueOnlyRequestBuilder(aasIdentifier, submodelIdentifier, idShortPath, submodelElementValue, level);
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

  private HttpRequest.Builder patchSubmodelElementValueByPathValueOnlyRequestBuilder(String aasIdentifier, String submodelIdentifier, String idShortPath, SubmodelElementValue submodelElementValue, String level) throws ApiException {
    // verify the required parameter 'aasIdentifier' is set
    if (aasIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'aasIdentifier' when calling patchSubmodelElementValueByPathValueOnly");
    }
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

    String localVarPath = "/shells/{aasIdentifier}/submodels/{submodelIdentifier}/submodel-elements/{idShortPath}/$value"
        .replace("{aasIdentifier}", ApiClient.urlEncode(aasIdentifier.toString()))
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
   * Creates a new Asset Administration Shell
   * 
   * @param assetAdministrationShell Asset Administration Shell object (required)
   * @return AssetAdministrationShell
   * @throws ApiException if fails to make API call
   */
  public AssetAdministrationShell postAssetAdministrationShell(AssetAdministrationShell assetAdministrationShell) throws ApiException {

    ApiResponse<AssetAdministrationShell> localVarResponse = postAssetAdministrationShellWithHttpInfo(assetAdministrationShell);
    return localVarResponse.getData();
  }

  /**
   * Creates a new Asset Administration Shell
   * 
   * @param assetAdministrationShell Asset Administration Shell object (required)
   * @return ApiResponse&lt;AssetAdministrationShell&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<AssetAdministrationShell> postAssetAdministrationShellWithHttpInfo(AssetAdministrationShell assetAdministrationShell) throws ApiException {
  	return postAssetAdministrationShellWithHttpInfoNoUrlEncoding(assetAdministrationShell);
 	
 }


  /**
   * Creates a new Asset Administration Shell
   * 
   * @param assetAdministrationShell Asset Administration Shell object (required)
   * @return ApiResponse&lt;AssetAdministrationShell&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<AssetAdministrationShell> postAssetAdministrationShellWithHttpInfoNoUrlEncoding(AssetAdministrationShell assetAdministrationShell) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = postAssetAdministrationShellRequestBuilder(assetAdministrationShell);
    try {
      HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(
          localVarRequestBuilder.build(),
          HttpResponse.BodyHandlers.ofInputStream());
      if (memberVarResponseInterceptor != null) {
        memberVarResponseInterceptor.accept(localVarResponse);
      }
      try {
        if (localVarResponse.statusCode()/ 100 != 2) {
          throw getApiException("postAssetAdministrationShell", localVarResponse);
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

  private HttpRequest.Builder postAssetAdministrationShellRequestBuilder(AssetAdministrationShell assetAdministrationShell) throws ApiException {
    // verify the required parameter 'assetAdministrationShell' is set
    if (assetAdministrationShell == null) {
      throw new ApiException(400, "Missing the required parameter 'assetAdministrationShell' when calling postAssetAdministrationShell");
    }

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/shells";

    localVarRequestBuilder.uri(URI.create(memberVarBaseUri + localVarPath));

    localVarRequestBuilder.header("Content-Type", "application/json");
    localVarRequestBuilder.header("Accept", "application/json");

    try {
      byte[] localVarPostBody = memberVarObjectMapper.writeValueAsBytes(assetAdministrationShell);
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
   * Creates a new submodel element
   * 
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelElement Requested submodel element (required)
   * @return SubmodelElement
   * @throws ApiException if fails to make API call
   */
  public SubmodelElement postSubmodelElementAasRepository(String aasIdentifier, String submodelIdentifier, SubmodelElement submodelElement) throws ApiException {

    ApiResponse<SubmodelElement> localVarResponse = postSubmodelElementAasRepositoryWithHttpInfo(aasIdentifier, submodelIdentifier, submodelElement);
    return localVarResponse.getData();
  }

  /**
   * Creates a new submodel element
   * 
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelElement Requested submodel element (required)
   * @return ApiResponse&lt;SubmodelElement&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<SubmodelElement> postSubmodelElementAasRepositoryWithHttpInfo(String aasIdentifier, String submodelIdentifier, SubmodelElement submodelElement) throws ApiException {
    String  aasIdentifierAsBytes = ApiClient.base64UrlEncode(aasIdentifier);
    String  submodelIdentifierAsBytes = ApiClient.base64UrlEncode(submodelIdentifier);
  	return postSubmodelElementAasRepositoryWithHttpInfoNoUrlEncoding(aasIdentifierAsBytes, submodelIdentifierAsBytes, submodelElement);
 	
 }


  /**
   * Creates a new submodel element
   * 
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelElement Requested submodel element (required)
   * @return ApiResponse&lt;SubmodelElement&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<SubmodelElement> postSubmodelElementAasRepositoryWithHttpInfoNoUrlEncoding(String aasIdentifier, String submodelIdentifier, SubmodelElement submodelElement) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = postSubmodelElementAasRepositoryRequestBuilder(aasIdentifier, submodelIdentifier, submodelElement);
    try {
      HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(
          localVarRequestBuilder.build(),
          HttpResponse.BodyHandlers.ofInputStream());
      if (memberVarResponseInterceptor != null) {
        memberVarResponseInterceptor.accept(localVarResponse);
      }
      try {
        if (localVarResponse.statusCode()/ 100 != 2) {
          throw getApiException("postSubmodelElementAasRepository", localVarResponse);
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

  private HttpRequest.Builder postSubmodelElementAasRepositoryRequestBuilder(String aasIdentifier, String submodelIdentifier, SubmodelElement submodelElement) throws ApiException {
    // verify the required parameter 'aasIdentifier' is set
    if (aasIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'aasIdentifier' when calling postSubmodelElementAasRepository");
    }
    // verify the required parameter 'submodelIdentifier' is set
    if (submodelIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'submodelIdentifier' when calling postSubmodelElementAasRepository");
    }
    // verify the required parameter 'submodelElement' is set
    if (submodelElement == null) {
      throw new ApiException(400, "Missing the required parameter 'submodelElement' when calling postSubmodelElementAasRepository");
    }

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/shells/{aasIdentifier}/submodels/{submodelIdentifier}/submodel-elements"
        .replace("{aasIdentifier}", ApiClient.urlEncode(aasIdentifier.toString()))
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
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param submodelElement Requested submodel element (required)
   * @return SubmodelElement
   * @throws ApiException if fails to make API call
   */
  public SubmodelElement postSubmodelElementByPathAasRepository(String aasIdentifier, String submodelIdentifier, String idShortPath, SubmodelElement submodelElement) throws ApiException {

    ApiResponse<SubmodelElement> localVarResponse = postSubmodelElementByPathAasRepositoryWithHttpInfo(aasIdentifier, submodelIdentifier, idShortPath, submodelElement);
    return localVarResponse.getData();
  }

  /**
   * Creates a new submodel element at a specified path within submodel elements hierarchy
   * 
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param submodelElement Requested submodel element (required)
   * @return ApiResponse&lt;SubmodelElement&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<SubmodelElement> postSubmodelElementByPathAasRepositoryWithHttpInfo(String aasIdentifier, String submodelIdentifier, String idShortPath, SubmodelElement submodelElement) throws ApiException {
    String  aasIdentifierAsBytes = ApiClient.base64UrlEncode(aasIdentifier);
    String  submodelIdentifierAsBytes = ApiClient.base64UrlEncode(submodelIdentifier);
  	return postSubmodelElementByPathAasRepositoryWithHttpInfoNoUrlEncoding(aasIdentifierAsBytes, submodelIdentifierAsBytes, idShortPath, submodelElement);
 	
 }


  /**
   * Creates a new submodel element at a specified path within submodel elements hierarchy
   * 
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param submodelElement Requested submodel element (required)
   * @return ApiResponse&lt;SubmodelElement&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<SubmodelElement> postSubmodelElementByPathAasRepositoryWithHttpInfoNoUrlEncoding(String aasIdentifier, String submodelIdentifier, String idShortPath, SubmodelElement submodelElement) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = postSubmodelElementByPathAasRepositoryRequestBuilder(aasIdentifier, submodelIdentifier, idShortPath, submodelElement);
    try {
      HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(
          localVarRequestBuilder.build(),
          HttpResponse.BodyHandlers.ofInputStream());
      if (memberVarResponseInterceptor != null) {
        memberVarResponseInterceptor.accept(localVarResponse);
      }
      try {
        if (localVarResponse.statusCode()/ 100 != 2) {
          throw getApiException("postSubmodelElementByPathAasRepository", localVarResponse);
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

  private HttpRequest.Builder postSubmodelElementByPathAasRepositoryRequestBuilder(String aasIdentifier, String submodelIdentifier, String idShortPath, SubmodelElement submodelElement) throws ApiException {
    // verify the required parameter 'aasIdentifier' is set
    if (aasIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'aasIdentifier' when calling postSubmodelElementByPathAasRepository");
    }
    // verify the required parameter 'submodelIdentifier' is set
    if (submodelIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'submodelIdentifier' when calling postSubmodelElementByPathAasRepository");
    }
    // verify the required parameter 'idShortPath' is set
    if (idShortPath == null) {
      throw new ApiException(400, "Missing the required parameter 'idShortPath' when calling postSubmodelElementByPathAasRepository");
    }
    // verify the required parameter 'submodelElement' is set
    if (submodelElement == null) {
      throw new ApiException(400, "Missing the required parameter 'submodelElement' when calling postSubmodelElementByPathAasRepository");
    }

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/shells/{aasIdentifier}/submodels/{submodelIdentifier}/submodel-elements/{idShortPath}"
        .replace("{aasIdentifier}", ApiClient.urlEncode(aasIdentifier.toString()))
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
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param reference Reference to the Submodel (required)
   * @return Reference
   * @throws ApiException if fails to make API call
   */
  public Reference postSubmodelReferenceAasRepository(String aasIdentifier, Reference reference) throws ApiException {

    ApiResponse<Reference> localVarResponse = postSubmodelReferenceAasRepositoryWithHttpInfo(aasIdentifier, reference);
    return localVarResponse.getData();
  }

  /**
   * Creates a submodel reference at the Asset Administration Shell
   * 
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param reference Reference to the Submodel (required)
   * @return ApiResponse&lt;Reference&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<Reference> postSubmodelReferenceAasRepositoryWithHttpInfo(String aasIdentifier, Reference reference) throws ApiException {
    String  aasIdentifierAsBytes = ApiClient.base64UrlEncode(aasIdentifier);
  	return postSubmodelReferenceAasRepositoryWithHttpInfoNoUrlEncoding(aasIdentifierAsBytes, reference);
 	
 }


  /**
   * Creates a submodel reference at the Asset Administration Shell
   * 
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param reference Reference to the Submodel (required)
   * @return ApiResponse&lt;Reference&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<Reference> postSubmodelReferenceAasRepositoryWithHttpInfoNoUrlEncoding(String aasIdentifier, Reference reference) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = postSubmodelReferenceAasRepositoryRequestBuilder(aasIdentifier, reference);
    try {
      HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(
          localVarRequestBuilder.build(),
          HttpResponse.BodyHandlers.ofInputStream());
      if (memberVarResponseInterceptor != null) {
        memberVarResponseInterceptor.accept(localVarResponse);
      }
      try {
        if (localVarResponse.statusCode()/ 100 != 2) {
          throw getApiException("postSubmodelReferenceAasRepository", localVarResponse);
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

  private HttpRequest.Builder postSubmodelReferenceAasRepositoryRequestBuilder(String aasIdentifier, Reference reference) throws ApiException {
    // verify the required parameter 'aasIdentifier' is set
    if (aasIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'aasIdentifier' when calling postSubmodelReferenceAasRepository");
    }
    // verify the required parameter 'reference' is set
    if (reference == null) {
      throw new ApiException(400, "Missing the required parameter 'reference' when calling postSubmodelReferenceAasRepository");
    }

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/shells/{aasIdentifier}/submodel-refs"
        .replace("{aasIdentifier}", ApiClient.urlEncode(aasIdentifier.toString()));

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
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param assetAdministrationShell Asset Administration Shell object (required)
   * @throws ApiException if fails to make API call
   */
  public void putAssetAdministrationShellById(String aasIdentifier, AssetAdministrationShell assetAdministrationShell) throws ApiException {

    putAssetAdministrationShellByIdWithHttpInfo(aasIdentifier, assetAdministrationShell);
  }

  /**
   * Updates an existing Asset Administration Shell
   * 
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param assetAdministrationShell Asset Administration Shell object (required)
   * @return ApiResponse&lt;Void&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<Void> putAssetAdministrationShellByIdWithHttpInfo(String aasIdentifier, AssetAdministrationShell assetAdministrationShell) throws ApiException {
    String  aasIdentifierAsBytes = ApiClient.base64UrlEncode(aasIdentifier);
  	return putAssetAdministrationShellByIdWithHttpInfoNoUrlEncoding(aasIdentifierAsBytes, assetAdministrationShell);
 	
 }


  /**
   * Updates an existing Asset Administration Shell
   * 
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param assetAdministrationShell Asset Administration Shell object (required)
   * @return ApiResponse&lt;Void&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<Void> putAssetAdministrationShellByIdWithHttpInfoNoUrlEncoding(String aasIdentifier, AssetAdministrationShell assetAdministrationShell) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = putAssetAdministrationShellByIdRequestBuilder(aasIdentifier, assetAdministrationShell);
    try {
      HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(
          localVarRequestBuilder.build(),
          HttpResponse.BodyHandlers.ofInputStream());
      if (memberVarResponseInterceptor != null) {
        memberVarResponseInterceptor.accept(localVarResponse);
      }
      try {
        if (localVarResponse.statusCode()/ 100 != 2) {
          throw getApiException("putAssetAdministrationShellById", localVarResponse);
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

  private HttpRequest.Builder putAssetAdministrationShellByIdRequestBuilder(String aasIdentifier, AssetAdministrationShell assetAdministrationShell) throws ApiException {
    // verify the required parameter 'aasIdentifier' is set
    if (aasIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'aasIdentifier' when calling putAssetAdministrationShellById");
    }
    // verify the required parameter 'assetAdministrationShell' is set
    if (assetAdministrationShell == null) {
      throw new ApiException(400, "Missing the required parameter 'assetAdministrationShell' when calling putAssetAdministrationShellById");
    }

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/shells/{aasIdentifier}"
        .replace("{aasIdentifier}", ApiClient.urlEncode(aasIdentifier.toString()));

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
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param assetInformation Asset Information object (required)
   * @throws ApiException if fails to make API call
   */
  public void putAssetInformationAasRepository(String aasIdentifier, AssetInformation assetInformation) throws ApiException {

    putAssetInformationAasRepositoryWithHttpInfo(aasIdentifier, assetInformation);
  }

  /**
   * Updates the Asset Information
   * 
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param assetInformation Asset Information object (required)
   * @return ApiResponse&lt;Void&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<Void> putAssetInformationAasRepositoryWithHttpInfo(String aasIdentifier, AssetInformation assetInformation) throws ApiException {
    String  aasIdentifierAsBytes = ApiClient.base64UrlEncode(aasIdentifier);
  	return putAssetInformationAasRepositoryWithHttpInfoNoUrlEncoding(aasIdentifierAsBytes, assetInformation);
 	
 }


  /**
   * Updates the Asset Information
   * 
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param assetInformation Asset Information object (required)
   * @return ApiResponse&lt;Void&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<Void> putAssetInformationAasRepositoryWithHttpInfoNoUrlEncoding(String aasIdentifier, AssetInformation assetInformation) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = putAssetInformationAasRepositoryRequestBuilder(aasIdentifier, assetInformation);
    try {
      HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(
          localVarRequestBuilder.build(),
          HttpResponse.BodyHandlers.ofInputStream());
      if (memberVarResponseInterceptor != null) {
        memberVarResponseInterceptor.accept(localVarResponse);
      }
      try {
        if (localVarResponse.statusCode()/ 100 != 2) {
          throw getApiException("putAssetInformationAasRepository", localVarResponse);
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

  private HttpRequest.Builder putAssetInformationAasRepositoryRequestBuilder(String aasIdentifier, AssetInformation assetInformation) throws ApiException {
    // verify the required parameter 'aasIdentifier' is set
    if (aasIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'aasIdentifier' when calling putAssetInformationAasRepository");
    }
    // verify the required parameter 'assetInformation' is set
    if (assetInformation == null) {
      throw new ApiException(400, "Missing the required parameter 'assetInformation' when calling putAssetInformationAasRepository");
    }

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/shells/{aasIdentifier}/asset-information"
        .replace("{aasIdentifier}", ApiClient.urlEncode(aasIdentifier.toString()));

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
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param fileName  (optional)
   * @param _file  (optional)
   * @throws ApiException if fails to make API call
   */
  public void putFileByPathAasRepository(String aasIdentifier, String submodelIdentifier, String idShortPath, String fileName, File _file) throws ApiException {

    putFileByPathAasRepositoryWithHttpInfo(aasIdentifier, submodelIdentifier, idShortPath, fileName, _file);
  }

  /**
   * Uploads file content to an existing submodel element at a specified path within submodel elements hierarchy
   * 
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param fileName  (optional)
   * @param _file  (optional)
   * @return ApiResponse&lt;Void&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<Void> putFileByPathAasRepositoryWithHttpInfo(String aasIdentifier, String submodelIdentifier, String idShortPath, String fileName, File _file) throws ApiException {
    String  aasIdentifierAsBytes = ApiClient.base64UrlEncode(aasIdentifier);
    String  submodelIdentifierAsBytes = ApiClient.base64UrlEncode(submodelIdentifier);
  	return putFileByPathAasRepositoryWithHttpInfoNoUrlEncoding(aasIdentifierAsBytes, submodelIdentifierAsBytes, idShortPath, fileName, _file);
 	
 }


  /**
   * Uploads file content to an existing submodel element at a specified path within submodel elements hierarchy
   * 
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param fileName  (optional)
   * @param _file  (optional)
   * @return ApiResponse&lt;Void&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<Void> putFileByPathAasRepositoryWithHttpInfoNoUrlEncoding(String aasIdentifier, String submodelIdentifier, String idShortPath, String fileName, File _file) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = putFileByPathAasRepositoryRequestBuilder(aasIdentifier, submodelIdentifier, idShortPath, fileName, _file);
    try {
      HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(
          localVarRequestBuilder.build(),
          HttpResponse.BodyHandlers.ofInputStream());
      if (memberVarResponseInterceptor != null) {
        memberVarResponseInterceptor.accept(localVarResponse);
      }
      try {
        if (localVarResponse.statusCode()/ 100 != 2) {
          throw getApiException("putFileByPathAasRepository", localVarResponse);
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

  private HttpRequest.Builder putFileByPathAasRepositoryRequestBuilder(String aasIdentifier, String submodelIdentifier, String idShortPath, String fileName, File _file) throws ApiException {
    // verify the required parameter 'aasIdentifier' is set
    if (aasIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'aasIdentifier' when calling putFileByPathAasRepository");
    }
    // verify the required parameter 'submodelIdentifier' is set
    if (submodelIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'submodelIdentifier' when calling putFileByPathAasRepository");
    }
    // verify the required parameter 'idShortPath' is set
    if (idShortPath == null) {
      throw new ApiException(400, "Missing the required parameter 'idShortPath' when calling putFileByPathAasRepository");
    }

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/shells/{aasIdentifier}/submodels/{submodelIdentifier}/submodel-elements/{idShortPath}/attachment"
        .replace("{aasIdentifier}", ApiClient.urlEncode(aasIdentifier.toString()))
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
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodel Submodel object (required)
   * @throws ApiException if fails to make API call
   */
  public void putSubmodelByIdAasRepository(String aasIdentifier, String submodelIdentifier, Submodel submodel) throws ApiException {

    putSubmodelByIdAasRepositoryWithHttpInfo(aasIdentifier, submodelIdentifier, submodel);
  }

  /**
   * Updates the Submodel
   * 
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodel Submodel object (required)
   * @return ApiResponse&lt;Void&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<Void> putSubmodelByIdAasRepositoryWithHttpInfo(String aasIdentifier, String submodelIdentifier, Submodel submodel) throws ApiException {
    String  aasIdentifierAsBytes = ApiClient.base64UrlEncode(aasIdentifier);
    String  submodelIdentifierAsBytes = ApiClient.base64UrlEncode(submodelIdentifier);
  	return putSubmodelByIdAasRepositoryWithHttpInfoNoUrlEncoding(aasIdentifierAsBytes, submodelIdentifierAsBytes, submodel);
 	
 }


  /**
   * Updates the Submodel
   * 
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodel Submodel object (required)
   * @return ApiResponse&lt;Void&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<Void> putSubmodelByIdAasRepositoryWithHttpInfoNoUrlEncoding(String aasIdentifier, String submodelIdentifier, Submodel submodel) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = putSubmodelByIdAasRepositoryRequestBuilder(aasIdentifier, submodelIdentifier, submodel);
    try {
      HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(
          localVarRequestBuilder.build(),
          HttpResponse.BodyHandlers.ofInputStream());
      if (memberVarResponseInterceptor != null) {
        memberVarResponseInterceptor.accept(localVarResponse);
      }
      try {
        if (localVarResponse.statusCode()/ 100 != 2) {
          throw getApiException("putSubmodelByIdAasRepository", localVarResponse);
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

  private HttpRequest.Builder putSubmodelByIdAasRepositoryRequestBuilder(String aasIdentifier, String submodelIdentifier, Submodel submodel) throws ApiException {
    // verify the required parameter 'aasIdentifier' is set
    if (aasIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'aasIdentifier' when calling putSubmodelByIdAasRepository");
    }
    // verify the required parameter 'submodelIdentifier' is set
    if (submodelIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'submodelIdentifier' when calling putSubmodelByIdAasRepository");
    }
    // verify the required parameter 'submodel' is set
    if (submodel == null) {
      throw new ApiException(400, "Missing the required parameter 'submodel' when calling putSubmodelByIdAasRepository");
    }

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/shells/{aasIdentifier}/submodels/{submodelIdentifier}"
        .replace("{aasIdentifier}", ApiClient.urlEncode(aasIdentifier.toString()))
        .replace("{submodelIdentifier}", ApiClient.urlEncode(submodelIdentifier.toString()));

    localVarRequestBuilder.uri(URI.create(memberVarBaseUri + localVarPath));

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
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param submodelElement Requested submodel element (required)
   * @throws ApiException if fails to make API call
   */
  public void putSubmodelElementByPathAasRepository(String aasIdentifier, String submodelIdentifier, String idShortPath, SubmodelElement submodelElement) throws ApiException {

    putSubmodelElementByPathAasRepositoryWithHttpInfo(aasIdentifier, submodelIdentifier, idShortPath, submodelElement);
  }

  /**
   * Updates an existing submodel element at a specified path within submodel elements hierarchy
   * 
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param submodelElement Requested submodel element (required)
   * @return ApiResponse&lt;Void&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<Void> putSubmodelElementByPathAasRepositoryWithHttpInfo(String aasIdentifier, String submodelIdentifier, String idShortPath, SubmodelElement submodelElement) throws ApiException {
    String  aasIdentifierAsBytes = ApiClient.base64UrlEncode(aasIdentifier);
    String  submodelIdentifierAsBytes = ApiClient.base64UrlEncode(submodelIdentifier);
  	return putSubmodelElementByPathAasRepositoryWithHttpInfoNoUrlEncoding(aasIdentifierAsBytes, submodelIdentifierAsBytes, idShortPath, submodelElement);
 	
 }


  /**
   * Updates an existing submodel element at a specified path within submodel elements hierarchy
   * 
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param idShortPath IdShort path to the submodel element (dot-separated) (required)
   * @param submodelElement Requested submodel element (required)
   * @return ApiResponse&lt;Void&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<Void> putSubmodelElementByPathAasRepositoryWithHttpInfoNoUrlEncoding(String aasIdentifier, String submodelIdentifier, String idShortPath, SubmodelElement submodelElement) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = putSubmodelElementByPathAasRepositoryRequestBuilder(aasIdentifier, submodelIdentifier, idShortPath, submodelElement);
    try {
      HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(
          localVarRequestBuilder.build(),
          HttpResponse.BodyHandlers.ofInputStream());
      if (memberVarResponseInterceptor != null) {
        memberVarResponseInterceptor.accept(localVarResponse);
      }
      try {
        if (localVarResponse.statusCode()/ 100 != 2) {
          throw getApiException("putSubmodelElementByPathAasRepository", localVarResponse);
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

  private HttpRequest.Builder putSubmodelElementByPathAasRepositoryRequestBuilder(String aasIdentifier, String submodelIdentifier, String idShortPath, SubmodelElement submodelElement) throws ApiException {
    // verify the required parameter 'aasIdentifier' is set
    if (aasIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'aasIdentifier' when calling putSubmodelElementByPathAasRepository");
    }
    // verify the required parameter 'submodelIdentifier' is set
    if (submodelIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'submodelIdentifier' when calling putSubmodelElementByPathAasRepository");
    }
    // verify the required parameter 'idShortPath' is set
    if (idShortPath == null) {
      throw new ApiException(400, "Missing the required parameter 'idShortPath' when calling putSubmodelElementByPathAasRepository");
    }
    // verify the required parameter 'submodelElement' is set
    if (submodelElement == null) {
      throw new ApiException(400, "Missing the required parameter 'submodelElement' when calling putSubmodelElementByPathAasRepository");
    }

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/shells/{aasIdentifier}/submodels/{submodelIdentifier}/submodel-elements/{idShortPath}"
        .replace("{aasIdentifier}", ApiClient.urlEncode(aasIdentifier.toString()))
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
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param fileName  (optional)
   * @param _file  (optional)
   * @throws ApiException if fails to make API call
   */
  public void putThumbnailAasRepository(String aasIdentifier, String fileName, File _file) throws ApiException {

    putThumbnailAasRepositoryWithHttpInfo(aasIdentifier, fileName, _file);
  }

  /**
   * 
   * 
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param fileName  (optional)
   * @param _file  (optional)
   * @return ApiResponse&lt;Void&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<Void> putThumbnailAasRepositoryWithHttpInfo(String aasIdentifier, String fileName, File _file) throws ApiException {
    String  aasIdentifierAsBytes = ApiClient.base64UrlEncode(aasIdentifier);
  	return putThumbnailAasRepositoryWithHttpInfoNoUrlEncoding(aasIdentifierAsBytes, fileName, _file);
 	
 }


  /**
   * 
   * 
   * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
   * @param fileName  (optional)
   * @param _file  (optional)
   * @return ApiResponse&lt;Void&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<Void> putThumbnailAasRepositoryWithHttpInfoNoUrlEncoding(String aasIdentifier, String fileName, File _file) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = putThumbnailAasRepositoryRequestBuilder(aasIdentifier, fileName, _file);
    try {
      HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(
          localVarRequestBuilder.build(),
          HttpResponse.BodyHandlers.ofInputStream());
      if (memberVarResponseInterceptor != null) {
        memberVarResponseInterceptor.accept(localVarResponse);
      }
      try {
        if (localVarResponse.statusCode()/ 100 != 2) {
          throw getApiException("putThumbnailAasRepository", localVarResponse);
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

  private HttpRequest.Builder putThumbnailAasRepositoryRequestBuilder(String aasIdentifier, String fileName, File _file) throws ApiException {
    // verify the required parameter 'aasIdentifier' is set
    if (aasIdentifier == null) {
      throw new ApiException(400, "Missing the required parameter 'aasIdentifier' when calling putThumbnailAasRepository");
    }

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/shells/{aasIdentifier}/asset-information/thumbnail"
        .replace("{aasIdentifier}", ApiClient.urlEncode(aasIdentifier.toString()));

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

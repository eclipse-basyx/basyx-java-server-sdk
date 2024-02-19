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
package org.eclipse.digitaltwin.basyx.aasrepository.client.internal;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.channels.Channels;
import java.nio.channels.Pipe;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.function.Consumer;

import org.apache.http.HttpEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.json.JsonMapperFactory;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.json.SimpleAbstractTypeResolverFactory;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetInformation;
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.basyx.client.internal.ApiClient;
import org.eclipse.digitaltwin.basyx.client.internal.ApiException;
import org.eclipse.digitaltwin.basyx.client.internal.ApiResponse;
import org.eclipse.digitaltwin.basyx.client.internal.Pair;
import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.Generated;

@SuppressWarnings("unused")
@Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen", date = "2024-01-26T15:40:42.933294+01:00[Europe/Berlin]")
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
		this(new ApiClient(HttpClient.newBuilder(), new JsonMapperFactory().create(new SimpleAbstractTypeResolverFactory().create()), baseUri));
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
	 * Returns all Asset Administration Shells
	 * 
	 * @param assetIds
	 *            A list of specific Asset identifiers. Each Asset identifier is a
	 *            base64-url-encoded
	 *            [SpecificAssetId](./model-part1.yaml#/components/schemas/SpecificAssetId)
	 *            (optional
	 * @param idShort
	 *            The Asset Administration Shell’s IdShort (optional)
	 * @param limit
	 *            The maximum number of elements in the response array (optional)
	 * @param cursor
	 *            A server-generated identifier retrieved from pagingMetadata that
	 *            specifies from which position the result listing should continue
	 *            (optional)
	 * @return GetAssetAdministrationShellsResult
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public CursorResult<List<AssetAdministrationShell>> getAllAssetAdministrationShells(List<String> assetIds, String idShort, Integer limit, String cursor) throws ApiException {

		ApiResponse<CursorResult<List<AssetAdministrationShell>>> localVarResponse = getAllAssetAdministrationShellsWithHttpInfo(assetIds, idShort, limit, cursor);
		return localVarResponse.getData();
	}

	/**
	 * Returns all Asset Administration Shells
	 * 
	 * @param assetIds
	 *            A list of specific Asset identifiers. Each Asset identifier is a
	 *            base64-url-encoded
	 *            [SpecificAssetId](./model-part1.yaml#/components/schemas/SpecificAssetId)
	 *            (optional
	 * @param idShort
	 *            The Asset Administration Shell’s IdShort (optional)
	 * @param limit
	 *            The maximum number of elements in the response array (optional)
	 * @param cursor
	 *            A server-generated identifier retrieved from pagingMetadata that
	 *            specifies from which position the result listing should continue
	 *            (optional)
	 * @return ApiResponse&lt;GetAssetAdministrationShellsResult&gt;
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public ApiResponse<CursorResult<List<AssetAdministrationShell>>> getAllAssetAdministrationShellsWithHttpInfo(List<String> assetIds, String idShort, Integer limit, String cursor) throws ApiException {
		List<String> assetIdsAsBytes = ApiClient.base64UrlEncode(assetIds);
		return getAllAssetAdministrationShellsWithHttpInfoNoUrlEncoding(assetIdsAsBytes, idShort, limit, cursor);

	}

	/**
	 * Returns all Asset Administration Shells
	 * 
	 * @param assetIds
	 *            A list of specific Asset identifiers. Each Asset identifier is a
	 *            base64-url-encoded
	 *            [SpecificAssetId](./model-part1.yaml#/components/schemas/SpecificAssetId)
	 *            (optional
	 * @param idShort
	 *            The Asset Administration Shell’s IdShort (optional)
	 * @param limit
	 *            The maximum number of elements in the response array (optional)
	 * @param cursor
	 *            A server-generated identifier retrieved from pagingMetadata that
	 *            specifies from which position the result listing should continue
	 *            (optional)
	 * @return ApiResponse&lt;GetAssetAdministrationShellsResult&gt;
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public ApiResponse<CursorResult<List<AssetAdministrationShell>>> getAllAssetAdministrationShellsWithHttpInfoNoUrlEncoding(List<String> assetIds, String idShort, Integer limit, String cursor) throws ApiException {
		HttpRequest.Builder localVarRequestBuilder = getAllAssetAdministrationShellsRequestBuilder(assetIds, idShort, limit, cursor);
		try {
			HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(localVarRequestBuilder.build(), HttpResponse.BodyHandlers.ofInputStream());
			if (memberVarResponseInterceptor != null) {
				memberVarResponseInterceptor.accept(localVarResponse);
			}
			try {
				if (localVarResponse.statusCode() / 100 != 2) {
					throw getApiException("getAllAssetAdministrationShells", localVarResponse);
				}
				return new ApiResponse<CursorResult<List<AssetAdministrationShell>>>(localVarResponse.statusCode(), localVarResponse.headers().map(),
						localVarResponse.body() == null ? null : memberVarObjectMapper.readValue(localVarResponse.body(), new TypeReference<CursorResult<List<AssetAdministrationShell>>>() {
						}) // closes the InputStream
				);
			} finally {
			}
		} catch (IOException e) {
			throw new ApiException(e);
		} catch (InterruptedException e) {
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
	 * Returns all submodel references
	 * 
	 * @param aasIdentifier
	 *            The Asset Administration Shell’s unique id
	 *            (UTF8-BASE64-URL-encoded) (required)
	 * @param limit
	 *            The maximum number of elements in the response array (optional)
	 * @param cursor
	 *            A server-generated identifier retrieved from pagingMetadata that
	 *            specifies from which position the result listing should continue
	 *            (optional)
	 * @return GetReferencesResult
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public CursorResult<List<Reference>> getAllSubmodelReferencesAasRepository(String aasIdentifier, Integer limit, String cursor) throws ApiException {

		ApiResponse<CursorResult<List<Reference>>> localVarResponse = getAllSubmodelReferencesAasRepositoryWithHttpInfo(aasIdentifier, limit, cursor);
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
	public ApiResponse<CursorResult<List<Reference>>> getAllSubmodelReferencesAasRepositoryWithHttpInfo(String aasIdentifier, Integer limit, String cursor) throws ApiException {
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
	public ApiResponse<CursorResult<List<Reference>>> getAllSubmodelReferencesAasRepositoryWithHttpInfoNoUrlEncoding(String aasIdentifier, Integer limit, String cursor) throws ApiException {
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
		return new ApiResponse<CursorResult<List<Reference>>>(
          localVarResponse.statusCode(),
          localVarResponse.headers().map(),
          localVarResponse.body() == null ? null : memberVarObjectMapper.readValue(localVarResponse.body(), new TypeReference<CursorResult<List<Reference>>>() {}) // closes the InputStream
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

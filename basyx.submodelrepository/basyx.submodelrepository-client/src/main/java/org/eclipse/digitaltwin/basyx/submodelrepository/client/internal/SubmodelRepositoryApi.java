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
package org.eclipse.digitaltwin.basyx.submodelrepository.client.internal;

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

import org.eclipse.digitaltwin.aas4j.v3.dataformat.json.JsonMapperFactory;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.json.SimpleAbstractTypeResolverFactory;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.basyx.client.internal.ApiClient;
import org.eclipse.digitaltwin.basyx.client.internal.ApiException;
import org.eclipse.digitaltwin.basyx.client.internal.ApiResponse;
import org.eclipse.digitaltwin.basyx.client.internal.Pair;
import org.eclipse.digitaltwin.basyx.client.internal.authorization.TokenManager;
import org.eclipse.digitaltwin.basyx.core.exceptions.AccessTokenRetrievalException;
import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.eclipse.digitaltwin.basyx.http.pagination.Base64UrlEncodedCursorResult;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.Generated;

@SuppressWarnings("unused")
@Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen", date = "2024-01-26T15:40:43.909837100+01:00[Europe/Berlin]")
public class SubmodelRepositoryApi {
	private final HttpClient memberVarHttpClient;
	private final ObjectMapper memberVarObjectMapper;
	private final String memberVarBaseUri;
	private final Consumer<HttpRequest.Builder> memberVarInterceptor;
	private final Duration memberVarReadTimeout;
	private final Consumer<HttpResponse<InputStream>> memberVarResponseInterceptor;
	private final Consumer<HttpResponse<String>> memberVarAsyncResponseInterceptor;
	private TokenManager tokenManager;
	
	public SubmodelRepositoryApi() {
		this(new ApiClient());
	}
	
	public SubmodelRepositoryApi(TokenManager tokenManager) {
		this(new ApiClient());
		this.tokenManager = tokenManager;
	}

	public SubmodelRepositoryApi(ObjectMapper mapper, String baseUri) {
		this(new ApiClient(HttpClient.newBuilder(), mapper, baseUri));
	}
	
	public SubmodelRepositoryApi(ObjectMapper mapper, String baseUri, TokenManager tokenManager) {
		this(new ApiClient(HttpClient.newBuilder(), mapper, baseUri));
		this.tokenManager = tokenManager;
	}

	public SubmodelRepositoryApi(String baseUri) {
		this(new ApiClient(HttpClient.newBuilder(), new JsonMapperFactory().create(new SimpleAbstractTypeResolverFactory().create()), baseUri));
	}
	
	public SubmodelRepositoryApi(String baseUri, TokenManager tokenManager) {
		  this(new ApiClient(HttpClient.newBuilder(), new JsonMapperFactory().create(new SimpleAbstractTypeResolverFactory().create()), baseUri));
		  this.tokenManager = tokenManager;
	}

	public SubmodelRepositoryApi(ApiClient apiClient) {
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
	 * Returns a specific Submodel
	 * 
	 * @param submodelIdentifier
	 *            The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
	 * @param level
	 *            Determines the structural depth of the respective resource content
	 *            (optional, default to deep)
	 * @param extent
	 *            Determines to which extent the resource is being serialized
	 *            (optional, default to withoutBlobValue)
	 * @return Submodel
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public Submodel getSubmodelById(String submodelIdentifier, String level, String extent) throws ApiException {
		ApiResponse<Submodel> localVarResponse = getSubmodelByIdWithHttpInfo(submodelIdentifier, level, extent);
		return localVarResponse.getData();
	}

	/**
	 * Returns a specific Submodel
	 * 
	 * @param submodelIdentifier
	 *            The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
	 * @param level
	 *            Determines the structural depth of the respective resource content
	 *            (optional, default to deep)
	 * @param extent
	 *            Determines to which extent the resource is being serialized
	 *            (optional, default to withoutBlobValue)
	 * @return ApiResponse&lt;Submodel&gt;
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public ApiResponse<Submodel> getSubmodelByIdWithHttpInfo(String submodelIdentifier, String level, String extent) throws ApiException {
		String submodelIdentifierAsBytes = ApiClient.base64UrlEncode(submodelIdentifier);
		return getSubmodelByIdWithHttpInfoNoUrlEncoding(submodelIdentifierAsBytes, level, extent);
	}

	/**
	 * Returns a specific Submodel
	 * 
	 * @param submodelIdentifier
	 *            The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
	 * @param level
	 *            Determines the structural depth of the respective resource content
	 *            (optional, default to deep)
	 * @param extent
	 *            Determines to which extent the resource is being serialized
	 *            (optional, default to withoutBlobValue)
	 * @return ApiResponse&lt;Submodel&gt;
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public ApiResponse<Submodel> getSubmodelByIdWithHttpInfoNoUrlEncoding(String submodelIdentifier, String level, String extent) throws ApiException {
		HttpRequest.Builder localVarRequestBuilder = getSubmodelByIdRequestBuilder(submodelIdentifier, level, extent);
		try {
			HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(localVarRequestBuilder.build(), HttpResponse.BodyHandlers.ofInputStream());
			if (memberVarResponseInterceptor != null) {
				memberVarResponseInterceptor.accept(localVarResponse);
			}
			try {
				if (localVarResponse.statusCode() / 100 != 2) {
					throw getApiException("getSubmodelById", localVarResponse);
				}
				return new ApiResponse<Submodel>(localVarResponse.statusCode(), localVarResponse.headers().map(),
						localVarResponse.body() == null ? null : memberVarObjectMapper.readValue(localVarResponse.body(), new TypeReference<Submodel>() {
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

	private HttpRequest.Builder getSubmodelByIdRequestBuilder(String submodelIdentifier, String level, String extent) throws ApiException {
		// verify the required parameter 'submodelIdentifier' is set
		if (submodelIdentifier == null) {
			throw new ApiException(400, "Missing the required parameter 'submodelIdentifier' when calling getSubmodelById");
		}

		HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

		String localVarPath = "/submodels/{submodelIdentifier}".replace("{submodelIdentifier}", ApiClient.urlEncode(submodelIdentifier.toString()));

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
	 * Creates a new Submodel
	 * 
	 * @param submodel
	 *            Submodel object (required)
	 * @return Submodel
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public Submodel postSubmodel(Submodel submodel) throws ApiException {

		ApiResponse<Submodel> localVarResponse = postSubmodelWithHttpInfo(submodel);
		return localVarResponse.getData();
	}

	/**
	 * Creates a new Submodel
	 * 
	 * @param submodel
	 *            Submodel object (required)
	 * @return ApiResponse&lt;Submodel&gt;
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public ApiResponse<Submodel> postSubmodelWithHttpInfo(Submodel submodel) throws ApiException {
		return postSubmodelWithHttpInfoNoUrlEncoding(submodel);

	}

	/**
	 * Creates a new Submodel
	 * 
	 * @param submodel
	 *            Submodel object (required)
	 * @return ApiResponse&lt;Submodel&gt;
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public ApiResponse<Submodel> postSubmodelWithHttpInfoNoUrlEncoding(Submodel submodel) throws ApiException {
		HttpRequest.Builder localVarRequestBuilder = postSubmodelRequestBuilder(submodel);
		try {
			HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(localVarRequestBuilder.build(), HttpResponse.BodyHandlers.ofInputStream());
			if (memberVarResponseInterceptor != null) {
				memberVarResponseInterceptor.accept(localVarResponse);
			}
			try {
				if (localVarResponse.statusCode() / 100 != 2) {
					throw getApiException("postSubmodel", localVarResponse);
				}
				return new ApiResponse<Submodel>(localVarResponse.statusCode(), localVarResponse.headers().map(),
						localVarResponse.body() == null ? null : memberVarObjectMapper.readValue(localVarResponse.body(), new TypeReference<Submodel>() {
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

	private HttpRequest.Builder postSubmodelRequestBuilder(Submodel submodel) throws ApiException {
		// verify the required parameter 'submodel' is set
		if (submodel == null) {
			throw new ApiException(400, "Missing the required parameter 'submodel' when calling postSubmodel");
		}

		HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

		String localVarPath = "/submodels";

		localVarRequestBuilder.uri(URI.create(memberVarBaseUri + localVarPath));

		localVarRequestBuilder.header("Content-Type", "application/json");
		localVarRequestBuilder.header("Accept", "application/json");
		
		addAuthorizationHeaderIfAuthIsEnabled(localVarRequestBuilder);

		try {
			byte[] localVarPostBody = memberVarObjectMapper.writeValueAsBytes(submodel);
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
	 * Updates an existing Submodel
	 * 
	 * @param submodelIdentifier
	 *            The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
	 * @param submodel
	 *            Submodel object (required)
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public void putSubmodelById(String submodelIdentifier, Submodel submodel) throws ApiException {

		putSubmodelByIdWithHttpInfo(submodelIdentifier, submodel);
	}

	/**
	 * Updates an existing Submodel
	 * 
	 * @param submodelIdentifier
	 *            The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
	 * @param submodel
	 *            Submodel object (required)
	 * @return ApiResponse&lt;Void&gt;
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public ApiResponse<Void> putSubmodelByIdWithHttpInfo(String submodelIdentifier, Submodel submodel) throws ApiException {
		String submodelIdentifierAsBytes = ApiClient.base64UrlEncode(submodelIdentifier);
		return putSubmodelByIdWithHttpInfoNoUrlEncoding(submodelIdentifierAsBytes, submodel);

	}

	/**
	 * Updates an existing Submodel
	 * 
	 * @param submodelIdentifier
	 *            The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
	 * @param submodel
	 *            Submodel object (required)
	 * @return ApiResponse&lt;Void&gt;
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public ApiResponse<Void> putSubmodelByIdWithHttpInfoNoUrlEncoding(String submodelIdentifier, Submodel submodel) throws ApiException {
		HttpRequest.Builder localVarRequestBuilder = putSubmodelByIdRequestBuilder(submodelIdentifier, submodel);
		try {
			HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(localVarRequestBuilder.build(), HttpResponse.BodyHandlers.ofInputStream());
			if (memberVarResponseInterceptor != null) {
				memberVarResponseInterceptor.accept(localVarResponse);
			}
			try {
				if (localVarResponse.statusCode() / 100 != 2) {
					throw getApiException("putSubmodelById", localVarResponse);
				}
				return new ApiResponse<Void>(localVarResponse.statusCode(), localVarResponse.headers().map(), null);
			} finally {
				// Drain the InputStream
				while (localVarResponse.body().read() != -1) {
					// Ignore
				}
				localVarResponse.body().close();
			}
		} catch (IOException e) {
			throw new ApiException(e);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new ApiException(e);
		}
	}

	private HttpRequest.Builder putSubmodelByIdRequestBuilder(String submodelIdentifier, Submodel submodel) throws ApiException {
		// verify the required parameter 'submodelIdentifier' is set
		if (submodelIdentifier == null) {
			throw new ApiException(400, "Missing the required parameter 'submodelIdentifier' when calling putSubmodelById");
		}
		// verify the required parameter 'submodel' is set
		if (submodel == null) {
			throw new ApiException(400, "Missing the required parameter 'submodel' when calling putSubmodelById");
		}

		HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

		String localVarPath = "/submodels/{submodelIdentifier}".replace("{submodelIdentifier}", ApiClient.urlEncode(submodelIdentifier.toString()));

		localVarRequestBuilder.uri(URI.create(memberVarBaseUri + localVarPath));

		localVarRequestBuilder.header("Content-Type", "application/json");
		localVarRequestBuilder.header("Accept", "application/json");
		
		addAuthorizationHeaderIfAuthIsEnabled(localVarRequestBuilder);

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
	 * Deletes a Submodel
	 * 
	 * @param submodelIdentifier
	 *            The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public void deleteSubmodelById(String submodelIdentifier) throws ApiException {

		deleteSubmodelByIdWithHttpInfo(submodelIdentifier);
	}

	/**
	 * Deletes a Submodel
	 * 
	 * @param submodelIdentifier
	 *            The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
	 * @return ApiResponse&lt;Void&gt;
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public ApiResponse<Void> deleteSubmodelByIdWithHttpInfo(String submodelIdentifier) throws ApiException {
		String submodelIdentifierAsBytes = ApiClient.base64UrlEncode(submodelIdentifier);
		return deleteSubmodelByIdWithHttpInfoNoUrlEncoding(submodelIdentifierAsBytes);

	}

	/**
	 * Deletes a Submodel
	 * 
	 * @param submodelIdentifier
	 *            The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
	 * @return ApiResponse&lt;Void&gt;
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public ApiResponse<Void> deleteSubmodelByIdWithHttpInfoNoUrlEncoding(String submodelIdentifier) throws ApiException {
		HttpRequest.Builder localVarRequestBuilder = deleteSubmodelByIdRequestBuilder(submodelIdentifier);
		try {
			HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(localVarRequestBuilder.build(), HttpResponse.BodyHandlers.ofInputStream());
			if (memberVarResponseInterceptor != null) {
				memberVarResponseInterceptor.accept(localVarResponse);
			}
			try {
				if (localVarResponse.statusCode() / 100 != 2) {
					throw getApiException("deleteSubmodelById", localVarResponse);
				}
				return new ApiResponse<Void>(localVarResponse.statusCode(), localVarResponse.headers().map(), null);
			} finally {
				// Drain the InputStream
				while (localVarResponse.body().read() != -1) {
					// Ignore
				}
				localVarResponse.body().close();
			}
		} catch (IOException e) {
			throw new ApiException(e);
		} catch (InterruptedException e) {
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

		String localVarPath = "/submodels/{submodelIdentifier}".replace("{submodelIdentifier}", ApiClient.urlEncode(submodelIdentifier.toString()));

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
	 * Returns all Submodels
	 * 
	 * @param semanticId
	 *            The value of the semantic id reference (BASE64-URL-encoded)
	 *            (optional)
	 * @param idShort
	 *            The Asset Administration Shell’s IdShort (optional)
	 * @param limit
	 *            The maximum number of elements in the response array (optional)
	 * @param cursor
	 *            A server-generated identifier retrieved from pagingMetadata that
	 *            specifies from which position the result listing should continue
	 *            (optional)
	 * @param level
	 *            Determines the structural depth of the respective resource content
	 *            (optional, default to deep)
	 * @param extent
	 *            Determines to which extent the resource is being serialized
	 *            (optional, default to withoutBlobValue)
	 * @return GetSubmodelsResult
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public CursorResult<List<Submodel>> getAllSubmodels(String semanticId, String idShort, Integer limit, String cursor, String level, String extent) throws ApiException {
		ApiResponse<Base64UrlEncodedCursorResult<List<Submodel>>> localVarResponse = getAllSubmodelsApiResponse(semanticId, idShort, limit, cursor, level, extent);

		return localVarResponse.getData();
	}

	private ApiResponse<Base64UrlEncodedCursorResult<List<Submodel>>> getAllSubmodelsApiResponse(String semanticId, String idShort, Integer limit, String cursor, String level, String extent) throws ApiException {
		HttpRequest.Builder localVarRequestBuilder = getAllSubmodelsRequestBuilder(semanticId, idShort, limit, cursor, level, extent);
		try {
			HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(localVarRequestBuilder.build(), HttpResponse.BodyHandlers.ofInputStream());
			if (memberVarResponseInterceptor != null) {
				memberVarResponseInterceptor.accept(localVarResponse);
			}
			try {
				if (localVarResponse.statusCode() / 100 != 2) {
					throw getApiException("getAllSubmodels", localVarResponse);
				}
				return new ApiResponse<>(localVarResponse.statusCode(), localVarResponse.headers().map(),
						localVarResponse.body() == null ? null : memberVarObjectMapper.readValue(localVarResponse.body(), new TypeReference<Base64UrlEncodedCursorResult<List<Submodel>>>() {
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

	private HttpRequest.Builder getAllSubmodelsRequestBuilder(String semanticId, String idShort, Integer limit, String cursor, String level, String extent) throws ApiException {

		HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

		String localVarPath = "/submodels";

		List<Pair> localVarQueryParams = new ArrayList<>();
		StringJoiner localVarQueryStringJoiner = new StringJoiner("&");
		String localVarQueryParameterBaseName;
		localVarQueryParameterBaseName = "semanticId";
		localVarQueryParams.addAll(ApiClient.parameterToPairs("semanticId", semanticId));
		localVarQueryParameterBaseName = "idShort";
		localVarQueryParams.addAll(ApiClient.parameterToPairs("idShort", idShort));
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
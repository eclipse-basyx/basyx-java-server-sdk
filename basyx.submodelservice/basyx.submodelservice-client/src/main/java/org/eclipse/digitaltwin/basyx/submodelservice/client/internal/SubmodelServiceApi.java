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
package org.eclipse.digitaltwin.basyx.submodelservice.client.internal;

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

import javax.annotation.processing.Generated;

import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.basyx.client.internal.ApiClient;
import org.eclipse.digitaltwin.basyx.client.internal.ApiException;
import org.eclipse.digitaltwin.basyx.client.internal.ApiResponse;
import org.eclipse.digitaltwin.basyx.client.internal.Pair;
import org.eclipse.digitaltwin.basyx.submodelservice.value.SubmodelElementValue;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@SuppressWarnings("unused")
@Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen", date = "2024-02-08T10:09:10.597431+01:00[Europe/Berlin]")
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
	this(new ApiClient(HttpClient.newBuilder(), new SubmodelSpecificJsonMapperFactory().create(), baseUri));
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
	 * Returns the Submodel
	 * 
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
	public Submodel getSubmodel(String level, String extent) throws ApiException {

		ApiResponse<Submodel> localVarResponse = getSubmodelWithHttpInfo(level, extent);
		return localVarResponse.getData();
	}

	/**
	 * Returns the Submodel
	 * 
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
	public ApiResponse<Submodel> getSubmodelWithHttpInfo(String level, String extent) throws ApiException {
		return getSubmodelWithHttpInfoNoUrlEncoding(level, extent);

	}

	/**
	 * Returns the Submodel
	 * 
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
	public ApiResponse<Submodel> getSubmodelWithHttpInfoNoUrlEncoding(String level, String extent) throws ApiException {
		HttpRequest.Builder localVarRequestBuilder = getSubmodelRequestBuilder(level, extent);
		try {
			HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(localVarRequestBuilder.build(), HttpResponse.BodyHandlers.ofInputStream());
			if (memberVarResponseInterceptor != null) {
				memberVarResponseInterceptor.accept(localVarResponse);
			}
			try {
				if (localVarResponse.statusCode() / 100 != 2) {
					throw getApiException("getSubmodel", localVarResponse);
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

	private HttpRequest.Builder getSubmodelRequestBuilder(String level, String extent) throws ApiException {

		HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

		String localVarPath = "";

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
	 * @param idShortPath
	 *            IdShort path to the submodel element (dot-separated) (required)
	 * @param level
	 *            Determines the structural depth of the respective resource content
	 *            (optional, default to deep)
	 * @param extent
	 *            Determines to which extent the resource is being serialized
	 *            (optional, default to withoutBlobValue)
	 * @return SubmodelElement
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public SubmodelElement getSubmodelElementByPath(String idShortPath, String level, String extent) throws ApiException {

		ApiResponse<SubmodelElement> localVarResponse = getSubmodelElementByPathWithHttpInfo(idShortPath, level, extent);
		return localVarResponse.getData();
	}

	/**
	 * Returns a specific submodel element from the Submodel at a specified path
	 * 
	 * @param idShortPath
	 *            IdShort path to the submodel element (dot-separated) (required)
	 * @param level
	 *            Determines the structural depth of the respective resource content
	 *            (optional, default to deep)
	 * @param extent
	 *            Determines to which extent the resource is being serialized
	 *            (optional, default to withoutBlobValue)
	 * @return ApiResponse&lt;SubmodelElement&gt;
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public ApiResponse<SubmodelElement> getSubmodelElementByPathWithHttpInfo(String idShortPath, String level, String extent) throws ApiException {
		return getSubmodelElementByPathWithHttpInfoNoUrlEncoding(idShortPath, level, extent);

	}

	/**
	 * Returns a specific submodel element from the Submodel at a specified path
	 * 
	 * @param idShortPath
	 *            IdShort path to the submodel element (dot-separated) (required)
	 * @param level
	 *            Determines the structural depth of the respective resource content
	 *            (optional, default to deep)
	 * @param extent
	 *            Determines to which extent the resource is being serialized
	 *            (optional, default to withoutBlobValue)
	 * @return ApiResponse&lt;SubmodelElement&gt;
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public ApiResponse<SubmodelElement> getSubmodelElementByPathWithHttpInfoNoUrlEncoding(String idShortPath, String level, String extent) throws ApiException {
		HttpRequest.Builder localVarRequestBuilder = getSubmodelElementByPathRequestBuilder(idShortPath, level, extent);
		try {
			HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(localVarRequestBuilder.build(), HttpResponse.BodyHandlers.ofInputStream());
			if (memberVarResponseInterceptor != null) {
				memberVarResponseInterceptor.accept(localVarResponse);
			}
			try {
				if (localVarResponse.statusCode() / 100 != 2) {
					throw getApiException("getSubmodelElementByPath", localVarResponse);
				}
				return new ApiResponse<SubmodelElement>(localVarResponse.statusCode(), localVarResponse.headers().map(),
						localVarResponse.body() == null ? null : memberVarObjectMapper.readValue(localVarResponse.body(), new TypeReference<SubmodelElement>() {
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

	private HttpRequest.Builder getSubmodelElementByPathRequestBuilder(String idShortPath, String level, String extent) throws ApiException {
		// verify the required parameter 'idShortPath' is set
		if (idShortPath == null) {
			throw new ApiException(400, "Missing the required parameter 'idShortPath' when calling getSubmodelElementByPath");
		}

		HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

		String localVarPath = "/submodel-elements/{idShortPath}".replace("{idShortPath}", ApiClient.urlEncode(idShortPath.toString()));

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
	 * Returns a specific submodel element from the Submodel at a specified path in
	 * the ValueOnly representation
	 * 
	 * @param idShortPath
	 *            IdShort path to the submodel element (dot-separated) (required)
	 * @param level
	 *            Determines the structural depth of the respective resource content
	 *            (optional, default to deep)
	 * @param extent
	 *            Determines to which extent the resource is being serialized
	 *            (optional, default to withoutBlobValue)
	 * @return SubmodelElementValue
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public SubmodelElementValue getSubmodelElementByPathValueOnly(String idShortPath, String level, String extent) throws ApiException {

		ApiResponse<SubmodelElementValue> localVarResponse = getSubmodelElementByPathValueOnlyWithHttpInfo(idShortPath, level, extent);
		return localVarResponse.getData();
	}

	/**
	 * Returns a specific submodel element from the Submodel at a specified path in
	 * the ValueOnly representation
	 * 
	 * @param idShortPath
	 *            IdShort path to the submodel element (dot-separated) (required)
	 * @param level
	 *            Determines the structural depth of the respective resource content
	 *            (optional, default to deep)
	 * @param extent
	 *            Determines to which extent the resource is being serialized
	 *            (optional, default to withoutBlobValue)
	 * @return ApiResponse&lt;SubmodelElementValue&gt;
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public ApiResponse<SubmodelElementValue> getSubmodelElementByPathValueOnlyWithHttpInfo(String idShortPath, String level, String extent) throws ApiException {
		return getSubmodelElementByPathValueOnlyWithHttpInfoNoUrlEncoding(idShortPath, level, extent);

	}

	/**
	 * Returns a specific submodel element from the Submodel at a specified path in
	 * the ValueOnly representation
	 * 
	 * @param idShortPath
	 *            IdShort path to the submodel element (dot-separated) (required)
	 * @param level
	 *            Determines the structural depth of the respective resource content
	 *            (optional, default to deep)
	 * @param extent
	 *            Determines to which extent the resource is being serialized
	 *            (optional, default to withoutBlobValue)
	 * @return ApiResponse&lt;SubmodelElementValue&gt;
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public ApiResponse<SubmodelElementValue> getSubmodelElementByPathValueOnlyWithHttpInfoNoUrlEncoding(String idShortPath, String level, String extent) throws ApiException {
		HttpRequest.Builder localVarRequestBuilder = getSubmodelElementByPathValueOnlyRequestBuilder(idShortPath, level, extent);
		try {
			HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(localVarRequestBuilder.build(), HttpResponse.BodyHandlers.ofInputStream());
			if (memberVarResponseInterceptor != null) {
				memberVarResponseInterceptor.accept(localVarResponse);
			}
			try {
				if (localVarResponse.statusCode() / 100 != 2) {
					throw getApiException("getSubmodelElementByPathValueOnly", localVarResponse);
				}
				return new ApiResponse<SubmodelElementValue>(localVarResponse.statusCode(), localVarResponse.headers().map(),
						localVarResponse.body() == null ? null : memberVarObjectMapper.readValue(localVarResponse.body(), new TypeReference<SubmodelElementValue>() {
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

	private HttpRequest.Builder getSubmodelElementByPathValueOnlyRequestBuilder(String idShortPath, String level, String extent) throws ApiException {
		// verify the required parameter 'idShortPath' is set
		if (idShortPath == null) {
			throw new ApiException(400, "Missing the required parameter 'idShortPath' when calling getSubmodelElementByPathValueOnly");
		}

		HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

		String localVarPath = "/submodel-elements/{idShortPath}/$value".replace("{idShortPath}", ApiClient.urlEncode(idShortPath.toString()));

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
	 * Updates the value of an existing SubmodelElement
	 * 
	 * @param idShortPath
	 *            IdShort path to the submodel element (dot-separated) (required)
	 * @param getSubmodelElementsValueResult
	 *            The SubmodelElement in its ValueOnly representation (required)
	 * @param limit
	 *            The maximum number of elements in the response array (optional)
	 * @param cursor
	 *            A server-generated identifier retrieved from pagingMetadata that
	 *            specifies from which position the result listing should continue
	 *            (optional)
	 * @param level
	 *            Determines the structural depth of the respective resource content
	 *            (optional, default to core)
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public void patchSubmodelElementByPathValueOnly(String idShortPath, SubmodelElementValue getSubmodelElementsValueResult, Integer limit, String cursor, String level) throws ApiException {

		patchSubmodelElementByPathValueOnlyWithHttpInfo(idShortPath, getSubmodelElementsValueResult, limit, cursor, level);
	}

	/**
	 * Updates the value of an existing SubmodelElement
	 * 
	 * @param idShortPath
	 *            IdShort path to the submodel element (dot-separated) (required)
	 * @param getSubmodelElementsValueResult
	 *            The SubmodelElement in its ValueOnly representation (required)
	 * @param limit
	 *            The maximum number of elements in the response array (optional)
	 * @param cursor
	 *            A server-generated identifier retrieved from pagingMetadata that
	 *            specifies from which position the result listing should continue
	 *            (optional)
	 * @param level
	 *            Determines the structural depth of the respective resource content
	 *            (optional, default to core)
	 * @return ApiResponse&lt;Void&gt;
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public ApiResponse<Void> patchSubmodelElementByPathValueOnlyWithHttpInfo(String idShortPath, SubmodelElementValue getSubmodelElementsValueResult, Integer limit, String cursor, String level) throws ApiException {
		return patchSubmodelElementByPathValueOnlyWithHttpInfoNoUrlEncoding(idShortPath, getSubmodelElementsValueResult, limit, cursor, level);

	}

	/**
	 * Updates the value of an existing SubmodelElement
	 * 
	 * @param idShortPath
	 *            IdShort path to the submodel element (dot-separated) (required)
	 * @param getSubmodelElementsValueResult
	 *            The SubmodelElement in its ValueOnly representation (required)
	 * @param limit
	 *            The maximum number of elements in the response array (optional)
	 * @param cursor
	 *            A server-generated identifier retrieved from pagingMetadata that
	 *            specifies from which position the result listing should continue
	 *            (optional)
	 * @param level
	 *            Determines the structural depth of the respective resource content
	 *            (optional, default to core)
	 * @return ApiResponse&lt;Void&gt;
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public ApiResponse<Void> patchSubmodelElementByPathValueOnlyWithHttpInfoNoUrlEncoding(String idShortPath, SubmodelElementValue getSubmodelElementsValueResult, Integer limit, String cursor, String level) throws ApiException {
		HttpRequest.Builder localVarRequestBuilder = patchSubmodelElementByPathValueOnlyRequestBuilder(idShortPath, getSubmodelElementsValueResult, limit, cursor, level);
		try {
			HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(localVarRequestBuilder.build(), HttpResponse.BodyHandlers.ofInputStream());
			if (memberVarResponseInterceptor != null) {
				memberVarResponseInterceptor.accept(localVarResponse);
			}
			try {
				if (localVarResponse.statusCode() / 100 != 2) {
					throw getApiException("patchSubmodelElementByPathValueOnly", localVarResponse);
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

	private HttpRequest.Builder patchSubmodelElementByPathValueOnlyRequestBuilder(String idShortPath, SubmodelElementValue getSubmodelElementsValueResult, Integer limit, String cursor, String level) throws ApiException {
		// verify the required parameter 'idShortPath' is set
		if (idShortPath == null) {
			throw new ApiException(400, "Missing the required parameter 'idShortPath' when calling patchSubmodelElementByPathValueOnly");
		}
		// verify the required parameter 'getSubmodelElementsValueResult' is set
		if (getSubmodelElementsValueResult == null) {
			throw new ApiException(400, "Missing the required parameter 'getSubmodelElementsValueResult' when calling patchSubmodelElementByPathValueOnly");
		}

		HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

		String localVarPath = "/submodel-elements/{idShortPath}/$value".replace("{idShortPath}", ApiClient.urlEncode(idShortPath.toString()));

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
	          null // closes the InputStream
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

		String localVarPath = "/submodel-elements";

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
		 * Creates a new submodel element at a specified path within submodel elements
		 * hierarchy
		 * 
		 * @param idShortPath
		 *            IdShort path to the submodel element (dot-separated) (required)
		 * @param submodelElement
		 *            Requested submodel element (required)
		 * @return SubmodelElement
		 * @throws ApiException
		 *             if fails to make API call
		 */
		public SubmodelElement postSubmodelElementByPath(String idShortPath, SubmodelElement submodelElement) throws ApiException {

			ApiResponse<SubmodelElement> localVarResponse = postSubmodelElementByPathWithHttpInfo(idShortPath, submodelElement);
			return localVarResponse.getData();
		}

		/**
		 * Creates a new submodel element at a specified path within submodel elements
		 * hierarchy
		 * 
		 * @param idShortPath
		 *            IdShort path to the submodel element (dot-separated) (required)
		 * @param submodelElement
		 *            Requested submodel element (required)
		 * @return ApiResponse&lt;SubmodelElement&gt;
		 * @throws ApiException
		 *             if fails to make API call
		 */
		public ApiResponse<SubmodelElement> postSubmodelElementByPathWithHttpInfo(String idShortPath, SubmodelElement submodelElement) throws ApiException {
			return postSubmodelElementByPathWithHttpInfoNoUrlEncoding(idShortPath, submodelElement);

		}

		/**
		 * Creates a new submodel element at a specified path within submodel elements
		 * hierarchy
		 * 
		 * @param idShortPath
		 *            IdShort path to the submodel element (dot-separated) (required)
		 * @param submodelElement
		 *            Requested submodel element (required)
		 * @return ApiResponse&lt;SubmodelElement&gt;
		 * @throws ApiException
		 *             if fails to make API call
		 */
		public ApiResponse<SubmodelElement> postSubmodelElementByPathWithHttpInfoNoUrlEncoding(String idShortPath, SubmodelElement submodelElement) throws ApiException {
			HttpRequest.Builder localVarRequestBuilder = postSubmodelElementByPathRequestBuilder(idShortPath, submodelElement);
			try {
				HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(localVarRequestBuilder.build(), HttpResponse.BodyHandlers.ofInputStream());
				if (memberVarResponseInterceptor != null) {
					memberVarResponseInterceptor.accept(localVarResponse);
				}
				try {
					if (localVarResponse.statusCode() / 100 != 2) {
						throw getApiException("postSubmodelElementByPath", localVarResponse);
					}
					return new ApiResponse<SubmodelElement>(localVarResponse.statusCode(), localVarResponse.headers().map(),
							null // closes the InputStream
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

			String localVarPath = "/submodel-elements/{idShortPath}".replace("{idShortPath}", ApiClient.urlEncode(idShortPath.toString()));

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
		 * Updates an existing submodel element at a specified path within submodel
		 * elements hierarchy
		 * 
		 * @param idShortPath
		 *            IdShort path to the submodel element (dot-separated) (required)
		 * @param submodelElement
		 *            Requested submodel element (required)
		 * @param level
		 *            Determines the structural depth of the respective resource content
		 *            (optional, default to deep)
		 * @throws ApiException
		 *             if fails to make API call
		 */
		public void putSubmodelElementByPath(String idShortPath, SubmodelElement submodelElement, String level) throws ApiException {

			putSubmodelElementByPathWithHttpInfo(idShortPath, submodelElement, level);
		}

		/**
		 * Updates an existing submodel element at a specified path within submodel
		 * elements hierarchy
		 * 
		 * @param idShortPath
		 *            IdShort path to the submodel element (dot-separated) (required)
		 * @param submodelElement
		 *            Requested submodel element (required)
		 * @param level
		 *            Determines the structural depth of the respective resource content
		 *            (optional, default to deep)
		 * @return ApiResponse&lt;Void&gt;
		 * @throws ApiException
		 *             if fails to make API call
		 */
		public ApiResponse<Void> putSubmodelElementByPathWithHttpInfo(String idShortPath, SubmodelElement submodelElement, String level) throws ApiException {
			return putSubmodelElementByPathWithHttpInfoNoUrlEncoding(idShortPath, submodelElement, level);

		}

		/**
		 * Updates an existing submodel element at a specified path within submodel
		 * elements hierarchy
		 * 
		 * @param idShortPath
		 *            IdShort path to the submodel element (dot-separated) (required)
		 * @param submodelElement
		 *            Requested submodel element (required)
		 * @param level
		 *            Determines the structural depth of the respective resource content
		 *            (optional, default to deep)
		 * @return ApiResponse&lt;Void&gt;
		 * @throws ApiException
		 *             if fails to make API call
		 */
		public ApiResponse<Void> putSubmodelElementByPathWithHttpInfoNoUrlEncoding(String idShortPath, SubmodelElement submodelElement, String level) throws ApiException {
			HttpRequest.Builder localVarRequestBuilder = putSubmodelElementByPathRequestBuilder(idShortPath, submodelElement, level);
			try {
				HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(localVarRequestBuilder.build(), HttpResponse.BodyHandlers.ofInputStream());
				if (memberVarResponseInterceptor != null) {
					memberVarResponseInterceptor.accept(localVarResponse);
				}
				try {
					if (localVarResponse.statusCode() / 100 != 2) {
						throw getApiException("putSubmodelElementByPath", localVarResponse);
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

			String localVarPath = "/submodel-elements/{idShortPath}".replace("{idShortPath}", ApiClient.urlEncode(idShortPath.toString()));

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

		/**
		 * Deletes a submodel element at a specified path within the submodel elements
		 * hierarchy
		 * 
		 * @param idShortPath
		 *            IdShort path to the submodel element (dot-separated) (required)
		 * @throws ApiException
		 *             if fails to make API call
		 */
		public void deleteSubmodelElementByPath(String idShortPath) throws ApiException {

			deleteSubmodelElementByPathWithHttpInfo(idShortPath);
		}

		/**
		 * Deletes a submodel element at a specified path within the submodel elements
		 * hierarchy
		 * 
		 * @param idShortPath
		 *            IdShort path to the submodel element (dot-separated) (required)
		 * @return ApiResponse&lt;Void&gt;
		 * @throws ApiException
		 *             if fails to make API call
		 */
		public ApiResponse<Void> deleteSubmodelElementByPathWithHttpInfo(String idShortPath) throws ApiException {
			return deleteSubmodelElementByPathWithHttpInfoNoUrlEncoding(idShortPath);

		}

		/**
		 * Deletes a submodel element at a specified path within the submodel elements
		 * hierarchy
		 * 
		 * @param idShortPath
		 *            IdShort path to the submodel element (dot-separated) (required)
		 * @return ApiResponse&lt;Void&gt;
		 * @throws ApiException
		 *             if fails to make API call
		 */
		public ApiResponse<Void> deleteSubmodelElementByPathWithHttpInfoNoUrlEncoding(String idShortPath) throws ApiException {
			HttpRequest.Builder localVarRequestBuilder = deleteSubmodelElementByPathRequestBuilder(idShortPath);
			try {
				HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(localVarRequestBuilder.build(), HttpResponse.BodyHandlers.ofInputStream());
				if (memberVarResponseInterceptor != null) {
					memberVarResponseInterceptor.accept(localVarResponse);
				}
				try {
					if (localVarResponse.statusCode() / 100 != 2) {
						throw getApiException("deleteSubmodelElementByPath", localVarResponse);
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

		private HttpRequest.Builder deleteSubmodelElementByPathRequestBuilder(String idShortPath) throws ApiException {
			// verify the required parameter 'idShortPath' is set
			if (idShortPath == null) {
				throw new ApiException(400, "Missing the required parameter 'idShortPath' when calling deleteSubmodelElementByPath");
			}

			HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

			String localVarPath = "/submodel-elements/{idShortPath}".replace("{idShortPath}", ApiClient.urlEncode(idShortPath.toString()));

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
}

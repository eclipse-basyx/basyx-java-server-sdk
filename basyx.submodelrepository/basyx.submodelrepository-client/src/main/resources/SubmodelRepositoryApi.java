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
import org.eclipse.digitaltwin.aas4j.v3.model.BaseOperationResult;
import org.eclipse.digitaltwin.aas4j.v3.model.OperationRequest;
import org.eclipse.digitaltwin.aas4j.v3.model.OperationResult;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.basyx.http.description.ServiceDescription;
import org.eclipse.digitaltwin.basyx.pagination.GetSubmodelElementsResult;
import org.eclipse.digitaltwin.basyx.submodelservice.value.SubmodelElementValue;
import org.eclipse.digitaltwin.basyx.v3.clients.model.part2.GetPathItemsResult;
import org.eclipse.digitaltwin.basyx.v3.clients.model.part2.GetReferencesResult;
import org.eclipse.digitaltwin.basyx.v3.clients.model.part2.GetSubmodelElementsMetadataResult;
import org.eclipse.digitaltwin.basyx.v3.clients.model.part2.GetSubmodelElementsValueResult;
import org.eclipse.digitaltwin.basyx.v3.clients.model.part2.GetSubmodelsMetadataResult;
import org.eclipse.digitaltwin.basyx.v3.clients.model.part2.GetSubmodelsResult;
import org.eclipse.digitaltwin.basyx.v3.clients.model.part2.GetSubmodelsValueResult;
import org.eclipse.digitaltwin.basyx.v3.clients.model.part2.OperationRequestValueOnly;
import org.eclipse.digitaltwin.basyx.v3.clients.model.part2.OperationResultValueOnly;
import org.eclipse.digitaltwin.basyx.v3.clients.model.part2.SubmodelElementMetadata;
import org.eclipse.digitaltwin.basyx.v3.clients.model.part2.SubmodelMetadata;
import org.eclipse.digitaltwin.basyx.v3.clients.model.part2.SubmodelValue;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen", date = "2024-01-26T15:40:43.909837100+01:00[Europe/Berlin]")
public class RepoAPITemp {
	private final HttpClient memberVarHttpClient;
	private final ObjectMapper memberVarObjectMapper;
	private final String memberVarBaseUri;
	private final Consumer<HttpRequest.Builder> memberVarInterceptor;
	private final Duration memberVarReadTimeout;
	private final Consumer<HttpResponse<InputStream>> memberVarResponseInterceptor;
	private final Consumer<HttpResponse<String>> memberVarAsyncResponseInterceptor;

	public RepoAPITemp() {
		this(new ApiClient());
	}

	public RepoAPITemp(ObjectMapper mapper, String baseUri) {
		this(new ApiClient(HttpClient.newBuilder(), mapper, baseUri));
	}

	public RepoAPITemp(ApiClient apiClient) {
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
	 * Deletes file content of an existing submodel element at a specified path
	 * within submodel elements hierarchy
	 * 
	 * @param submodelIdentifier
	 *            The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
	 * @param idShortPath
	 *            IdShort path to the submodel element (dot-separated) (required)
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public void deleteFileByPathSubmodelRepo(String submodelIdentifier, String idShortPath) throws ApiException {

		deleteFileByPathSubmodelRepoWithHttpInfo(submodelIdentifier, idShortPath);
	}

	/**
	 * Deletes file content of an existing submodel element at a specified path
	 * within submodel elements hierarchy
	 * 
	 * @param submodelIdentifier
	 *            The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
	 * @param idShortPath
	 *            IdShort path to the submodel element (dot-separated) (required)
	 * @return ApiResponse&lt;Void&gt;
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public ApiResponse<Void> deleteFileByPathSubmodelRepoWithHttpInfo(String submodelIdentifier, String idShortPath) throws ApiException {
		String submodelIdentifierAsBytes = ApiClient.base64UrlEncode(submodelIdentifier);
		return deleteFileByPathSubmodelRepoWithHttpInfoNoUrlEncoding(submodelIdentifierAsBytes, idShortPath);

	}

	/**
	 * Deletes file content of an existing submodel element at a specified path
	 * within submodel elements hierarchy
	 * 
	 * @param submodelIdentifier
	 *            The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
	 * @param idShortPath
	 *            IdShort path to the submodel element (dot-separated) (required)
	 * @return ApiResponse&lt;Void&gt;
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public ApiResponse<Void> deleteFileByPathSubmodelRepoWithHttpInfoNoUrlEncoding(String submodelIdentifier, String idShortPath) throws ApiException {
		HttpRequest.Builder localVarRequestBuilder = deleteFileByPathSubmodelRepoRequestBuilder(submodelIdentifier, idShortPath);
		try {
			HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(localVarRequestBuilder.build(), HttpResponse.BodyHandlers.ofInputStream());
			if (memberVarResponseInterceptor != null) {
				memberVarResponseInterceptor.accept(localVarResponse);
			}
			try {
				if (localVarResponse.statusCode() / 100 != 2) {
					throw getApiException("deleteFileByPathSubmodelRepo", localVarResponse);
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

	private HttpRequest.Builder deleteFileByPathSubmodelRepoRequestBuilder(String submodelIdentifier, String idShortPath) throws ApiException {
		// verify the required parameter 'submodelIdentifier' is set
		if (submodelIdentifier == null) {
			throw new ApiException(400, "Missing the required parameter 'submodelIdentifier' when calling deleteFileByPathSubmodelRepo");
		}
		// verify the required parameter 'idShortPath' is set
		if (idShortPath == null) {
			throw new ApiException(400, "Missing the required parameter 'idShortPath' when calling deleteFileByPathSubmodelRepo");
		}

		HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

		String localVarPath = "/submodels/{submodelIdentifier}/submodel-elements/{idShortPath}/attachment".replace("{submodelIdentifier}", ApiClient.urlEncode(submodelIdentifier.toString())).replace("{idShortPath}",
				ApiClient.urlEncode(idShortPath.toString()));

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
	 * Deletes a submodel element at a specified path within the submodel elements
	 * hierarchy
	 * 
	 * @param submodelIdentifier
	 *            The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
	 * @param idShortPath
	 *            IdShort path to the submodel element (dot-separated) (required)
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public void deleteSubmodelElementByPathSubmodelRepo(String submodelIdentifier, String idShortPath) throws ApiException {

		deleteSubmodelElementByPathSubmodelRepoWithHttpInfo(submodelIdentifier, idShortPath);
	}

	/**
	 * Deletes a submodel element at a specified path within the submodel elements
	 * hierarchy
	 * 
	 * @param submodelIdentifier
	 *            The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
	 * @param idShortPath
	 *            IdShort path to the submodel element (dot-separated) (required)
	 * @return ApiResponse&lt;Void&gt;
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public ApiResponse<Void> deleteSubmodelElementByPathSubmodelRepoWithHttpInfo(String submodelIdentifier, String idShortPath) throws ApiException {
		String submodelIdentifierAsBytes = ApiClient.base64UrlEncode(submodelIdentifier);
		return deleteSubmodelElementByPathSubmodelRepoWithHttpInfoNoUrlEncoding(submodelIdentifierAsBytes, idShortPath);

	}

	/**
	 * Deletes a submodel element at a specified path within the submodel elements
	 * hierarchy
	 * 
	 * @param submodelIdentifier
	 *            The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
	 * @param idShortPath
	 *            IdShort path to the submodel element (dot-separated) (required)
	 * @return ApiResponse&lt;Void&gt;
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public ApiResponse<Void> deleteSubmodelElementByPathSubmodelRepoWithHttpInfoNoUrlEncoding(String submodelIdentifier, String idShortPath) throws ApiException {
		HttpRequest.Builder localVarRequestBuilder = deleteSubmodelElementByPathSubmodelRepoRequestBuilder(submodelIdentifier, idShortPath);
		try {
			HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(localVarRequestBuilder.build(), HttpResponse.BodyHandlers.ofInputStream());
			if (memberVarResponseInterceptor != null) {
				memberVarResponseInterceptor.accept(localVarResponse);
			}
			try {
				if (localVarResponse.statusCode() / 100 != 2) {
					throw getApiException("deleteSubmodelElementByPathSubmodelRepo", localVarResponse);
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

	private HttpRequest.Builder deleteSubmodelElementByPathSubmodelRepoRequestBuilder(String submodelIdentifier, String idShortPath) throws ApiException {
		// verify the required parameter 'submodelIdentifier' is set
		if (submodelIdentifier == null) {
			throw new ApiException(400, "Missing the required parameter 'submodelIdentifier' when calling deleteSubmodelElementByPathSubmodelRepo");
		}
		// verify the required parameter 'idShortPath' is set
		if (idShortPath == null) {
			throw new ApiException(400, "Missing the required parameter 'idShortPath' when calling deleteSubmodelElementByPathSubmodelRepo");
		}

		HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

		String localVarPath = "/submodels/{submodelIdentifier}/submodel-elements/{idShortPath}".replace("{submodelIdentifier}", ApiClient.urlEncode(submodelIdentifier.toString())).replace("{idShortPath}",
				ApiClient.urlEncode(idShortPath.toString()));

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
	 * Returns an appropriate serialization based on the specified format (see
	 * SerializationFormat)
	 * 
	 * @param aasIds
	 *            The Asset Administration Shells&#39; unique ids
	 *            (UTF8-BASE64-URL-encoded) (optional
	 * @param submodelIds
	 *            The Submodels&#39; unique ids (UTF8-BASE64-URL-encoded) (optional
	 * @param includeConceptDescriptions
	 *            Include Concept Descriptions? (optional, default to true)
	 * @return File
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public File generateSerializationByIds(List<String> aasIds, List<String> submodelIds, Boolean includeConceptDescriptions) throws ApiException {

		ApiResponse<File> localVarResponse = generateSerializationByIdsWithHttpInfo(aasIds, submodelIds, includeConceptDescriptions);
		return localVarResponse.getData();
	}

	/**
	 * Returns an appropriate serialization based on the specified format (see
	 * SerializationFormat)
	 * 
	 * @param aasIds
	 *            The Asset Administration Shells&#39; unique ids
	 *            (UTF8-BASE64-URL-encoded) (optional
	 * @param submodelIds
	 *            The Submodels&#39; unique ids (UTF8-BASE64-URL-encoded) (optional
	 * @param includeConceptDescriptions
	 *            Include Concept Descriptions? (optional, default to true)
	 * @return ApiResponse&lt;File&gt;
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public ApiResponse<File> generateSerializationByIdsWithHttpInfo(List<String> aasIds, List<String> submodelIds, Boolean includeConceptDescriptions) throws ApiException {
		return generateSerializationByIdsWithHttpInfoNoUrlEncoding(aasIds, submodelIds, includeConceptDescriptions);

	}

	/**
	 * Returns an appropriate serialization based on the specified format (see
	 * SerializationFormat)
	 * 
	 * @param aasIds
	 *            The Asset Administration Shells&#39; unique ids
	 *            (UTF8-BASE64-URL-encoded) (optional
	 * @param submodelIds
	 *            The Submodels&#39; unique ids (UTF8-BASE64-URL-encoded) (optional
	 * @param includeConceptDescriptions
	 *            Include Concept Descriptions? (optional, default to true)
	 * @return ApiResponse&lt;File&gt;
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public ApiResponse<File> generateSerializationByIdsWithHttpInfoNoUrlEncoding(List<String> aasIds, List<String> submodelIds, Boolean includeConceptDescriptions) throws ApiException {
		HttpRequest.Builder localVarRequestBuilder = generateSerializationByIdsRequestBuilder(aasIds, submodelIds, includeConceptDescriptions);
		try {
			HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(localVarRequestBuilder.build(), HttpResponse.BodyHandlers.ofInputStream());
			if (memberVarResponseInterceptor != null) {
				memberVarResponseInterceptor.accept(localVarResponse);
			}
			try {
				if (localVarResponse.statusCode() / 100 != 2) {
					throw getApiException("generateSerializationByIds", localVarResponse);
				}
				return new ApiResponse<File>(localVarResponse.statusCode(), localVarResponse.headers().map(), localVarResponse.body() == null ? null : memberVarObjectMapper.readValue(localVarResponse.body(), new TypeReference<File>() {
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
	 * @param submodelIdentifier
	 *            The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
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
	 * @return GetSubmodelElementsResult
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public GetSubmodelElementsResult getAllSubmodelElements(String submodelIdentifier, Integer limit, String cursor, String level, String extent) throws ApiException {

		ApiResponse<GetSubmodelElementsResult> localVarResponse = getAllSubmodelElementsWithHttpInfo(submodelIdentifier, limit, cursor, level, extent);
		return localVarResponse.getData();
	}

	/**
	 * Returns all submodel elements including their hierarchy
	 * 
	 * @param submodelIdentifier
	 *            The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
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
	 * @return ApiResponse&lt;GetSubmodelElementsResult&gt;
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public ApiResponse<GetSubmodelElementsResult> getAllSubmodelElementsWithHttpInfo(String submodelIdentifier, Integer limit, String cursor, String level, String extent) throws ApiException {
		String submodelIdentifierAsBytes = ApiClient.base64UrlEncode(submodelIdentifier);
		return getAllSubmodelElementsWithHttpInfoNoUrlEncoding(submodelIdentifierAsBytes, limit, cursor, level, extent);

	}

	/**
	 * Returns all submodel elements including their hierarchy
	 * 
	 * @param submodelIdentifier
	 *            The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
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
	 * @return ApiResponse&lt;GetSubmodelElementsResult&gt;
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public ApiResponse<GetSubmodelElementsResult> getAllSubmodelElementsWithHttpInfoNoUrlEncoding(String submodelIdentifier, Integer limit, String cursor, String level, String extent) throws ApiException {
		HttpRequest.Builder localVarRequestBuilder = getAllSubmodelElementsRequestBuilder(submodelIdentifier, limit, cursor, level, extent);
		try {
			HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(localVarRequestBuilder.build(), HttpResponse.BodyHandlers.ofInputStream());
			if (memberVarResponseInterceptor != null) {
				memberVarResponseInterceptor.accept(localVarResponse);
			}
			try {
				if (localVarResponse.statusCode() / 100 != 2) {
					throw getApiException("getAllSubmodelElements", localVarResponse);
				}
				return new ApiResponse<GetSubmodelElementsResult>(localVarResponse.statusCode(), localVarResponse.headers().map(),
						localVarResponse.body() == null ? null : memberVarObjectMapper.readValue(localVarResponse.body(), new TypeReference<GetSubmodelElementsResult>() {
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

	private HttpRequest.Builder getAllSubmodelElementsRequestBuilder(String submodelIdentifier, Integer limit, String cursor, String level, String extent) throws ApiException {
		// verify the required parameter 'submodelIdentifier' is set
		if (submodelIdentifier == null) {
			throw new ApiException(400, "Missing the required parameter 'submodelIdentifier' when calling getAllSubmodelElements");
		}

		HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

		String localVarPath = "/submodels/{submodelIdentifier}/submodel-elements".replace("{submodelIdentifier}", ApiClient.urlEncode(submodelIdentifier.toString()));

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
	 * Returns the metadata attributes of all submodel elements including their
	 * hierarchy
	 * 
	 * @param submodelIdentifier
	 *            The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
	 * @param limit
	 *            The maximum number of elements in the response array (optional)
	 * @param cursor
	 *            A server-generated identifier retrieved from pagingMetadata that
	 *            specifies from which position the result listing should continue
	 *            (optional)
	 * @param level
	 *            Determines the structural depth of the respective resource content
	 *            (optional, default to deep)
	 * @return GetSubmodelElementsMetadataResult
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public GetSubmodelElementsMetadataResult getAllSubmodelElementsMetadataSubmodelRepo(String submodelIdentifier, Integer limit, String cursor, String level) throws ApiException {

		ApiResponse<GetSubmodelElementsMetadataResult> localVarResponse = getAllSubmodelElementsMetadataSubmodelRepoWithHttpInfo(submodelIdentifier, limit, cursor, level);
		return localVarResponse.getData();
	}

	/**
	 * Returns the metadata attributes of all submodel elements including their
	 * hierarchy
	 * 
	 * @param submodelIdentifier
	 *            The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
	 * @param limit
	 *            The maximum number of elements in the response array (optional)
	 * @param cursor
	 *            A server-generated identifier retrieved from pagingMetadata that
	 *            specifies from which position the result listing should continue
	 *            (optional)
	 * @param level
	 *            Determines the structural depth of the respective resource content
	 *            (optional, default to deep)
	 * @return ApiResponse&lt;GetSubmodelElementsMetadataResult&gt;
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public ApiResponse<GetSubmodelElementsMetadataResult> getAllSubmodelElementsMetadataSubmodelRepoWithHttpInfo(String submodelIdentifier, Integer limit, String cursor, String level) throws ApiException {
		String submodelIdentifierAsBytes = ApiClient.base64UrlEncode(submodelIdentifier);
		return getAllSubmodelElementsMetadataSubmodelRepoWithHttpInfoNoUrlEncoding(submodelIdentifierAsBytes, limit, cursor, level);

	}

	/**
	 * Returns the metadata attributes of all submodel elements including their
	 * hierarchy
	 * 
	 * @param submodelIdentifier
	 *            The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
	 * @param limit
	 *            The maximum number of elements in the response array (optional)
	 * @param cursor
	 *            A server-generated identifier retrieved from pagingMetadata that
	 *            specifies from which position the result listing should continue
	 *            (optional)
	 * @param level
	 *            Determines the structural depth of the respective resource content
	 *            (optional, default to deep)
	 * @return ApiResponse&lt;GetSubmodelElementsMetadataResult&gt;
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public ApiResponse<GetSubmodelElementsMetadataResult> getAllSubmodelElementsMetadataSubmodelRepoWithHttpInfoNoUrlEncoding(String submodelIdentifier, Integer limit, String cursor, String level) throws ApiException {
		HttpRequest.Builder localVarRequestBuilder = getAllSubmodelElementsMetadataSubmodelRepoRequestBuilder(submodelIdentifier, limit, cursor, level);
		try {
			HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(localVarRequestBuilder.build(), HttpResponse.BodyHandlers.ofInputStream());
			if (memberVarResponseInterceptor != null) {
				memberVarResponseInterceptor.accept(localVarResponse);
			}
			try {
				if (localVarResponse.statusCode() / 100 != 2) {
					throw getApiException("getAllSubmodelElementsMetadataSubmodelRepo", localVarResponse);
				}
				return new ApiResponse<GetSubmodelElementsMetadataResult>(localVarResponse.statusCode(), localVarResponse.headers().map(),
						localVarResponse.body() == null ? null : memberVarObjectMapper.readValue(localVarResponse.body(), new TypeReference<GetSubmodelElementsMetadataResult>() {
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

	private HttpRequest.Builder getAllSubmodelElementsMetadataSubmodelRepoRequestBuilder(String submodelIdentifier, Integer limit, String cursor, String level) throws ApiException {
		// verify the required parameter 'submodelIdentifier' is set
		if (submodelIdentifier == null) {
			throw new ApiException(400, "Missing the required parameter 'submodelIdentifier' when calling getAllSubmodelElementsMetadataSubmodelRepo");
		}

		HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

		String localVarPath = "/submodels/{submodelIdentifier}/submodel-elements/$metadata".replace("{submodelIdentifier}", ApiClient.urlEncode(submodelIdentifier.toString()));

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
	 * @param submodelIdentifier
	 *            The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
	 * @param limit
	 *            The maximum number of elements in the response array (optional)
	 * @param cursor
	 *            A server-generated identifier retrieved from pagingMetadata that
	 *            specifies from which position the result listing should continue
	 *            (optional)
	 * @param level
	 *            Determines the structural depth of the respective resource content
	 *            (optional, default to deep)
	 * @return GetPathItemsResult
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public GetPathItemsResult getAllSubmodelElementsPathSubmodelRepo(String submodelIdentifier, Integer limit, String cursor, String level) throws ApiException {

		ApiResponse<GetPathItemsResult> localVarResponse = getAllSubmodelElementsPathSubmodelRepoWithHttpInfo(submodelIdentifier, limit, cursor, level);
		return localVarResponse.getData();
	}

	/**
	 * Returns all submodel elements including their hierarchy in the Path notation
	 * 
	 * @param submodelIdentifier
	 *            The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
	 * @param limit
	 *            The maximum number of elements in the response array (optional)
	 * @param cursor
	 *            A server-generated identifier retrieved from pagingMetadata that
	 *            specifies from which position the result listing should continue
	 *            (optional)
	 * @param level
	 *            Determines the structural depth of the respective resource content
	 *            (optional, default to deep)
	 * @return ApiResponse&lt;GetPathItemsResult&gt;
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public ApiResponse<GetPathItemsResult> getAllSubmodelElementsPathSubmodelRepoWithHttpInfo(String submodelIdentifier, Integer limit, String cursor, String level) throws ApiException {
		String submodelIdentifierAsBytes = ApiClient.base64UrlEncode(submodelIdentifier);
		return getAllSubmodelElementsPathSubmodelRepoWithHttpInfoNoUrlEncoding(submodelIdentifierAsBytes, limit, cursor, level);

	}

	/**
	 * Returns all submodel elements including their hierarchy in the Path notation
	 * 
	 * @param submodelIdentifier
	 *            The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
	 * @param limit
	 *            The maximum number of elements in the response array (optional)
	 * @param cursor
	 *            A server-generated identifier retrieved from pagingMetadata that
	 *            specifies from which position the result listing should continue
	 *            (optional)
	 * @param level
	 *            Determines the structural depth of the respective resource content
	 *            (optional, default to deep)
	 * @return ApiResponse&lt;GetPathItemsResult&gt;
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public ApiResponse<GetPathItemsResult> getAllSubmodelElementsPathSubmodelRepoWithHttpInfoNoUrlEncoding(String submodelIdentifier, Integer limit, String cursor, String level) throws ApiException {
		HttpRequest.Builder localVarRequestBuilder = getAllSubmodelElementsPathSubmodelRepoRequestBuilder(submodelIdentifier, limit, cursor, level);
		try {
			HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(localVarRequestBuilder.build(), HttpResponse.BodyHandlers.ofInputStream());
			if (memberVarResponseInterceptor != null) {
				memberVarResponseInterceptor.accept(localVarResponse);
			}
			try {
				if (localVarResponse.statusCode() / 100 != 2) {
					throw getApiException("getAllSubmodelElementsPathSubmodelRepo", localVarResponse);
				}
				return new ApiResponse<GetPathItemsResult>(localVarResponse.statusCode(), localVarResponse.headers().map(),
						localVarResponse.body() == null ? null : memberVarObjectMapper.readValue(localVarResponse.body(), new TypeReference<GetPathItemsResult>() {
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

	private HttpRequest.Builder getAllSubmodelElementsPathSubmodelRepoRequestBuilder(String submodelIdentifier, Integer limit, String cursor, String level) throws ApiException {
		// verify the required parameter 'submodelIdentifier' is set
		if (submodelIdentifier == null) {
			throw new ApiException(400, "Missing the required parameter 'submodelIdentifier' when calling getAllSubmodelElementsPathSubmodelRepo");
		}

		HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

		String localVarPath = "/submodels/{submodelIdentifier}/submodel-elements/$path".replace("{submodelIdentifier}", ApiClient.urlEncode(submodelIdentifier.toString()));

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
	 * @param submodelIdentifier
	 *            The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
	 * @param limit
	 *            The maximum number of elements in the response array (optional)
	 * @param cursor
	 *            A server-generated identifier retrieved from pagingMetadata that
	 *            specifies from which position the result listing should continue
	 *            (optional)
	 * @param level
	 *            Determines the structural depth of the respective resource content
	 *            (optional, default to core)
	 * @return GetReferencesResult
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public GetReferencesResult getAllSubmodelElementsReferenceSubmodelRepo(String submodelIdentifier, Integer limit, String cursor, String level) throws ApiException {

		ApiResponse<GetReferencesResult> localVarResponse = getAllSubmodelElementsReferenceSubmodelRepoWithHttpInfo(submodelIdentifier, limit, cursor, level);
		return localVarResponse.getData();
	}

	/**
	 * Returns the References of all submodel elements
	 * 
	 * @param submodelIdentifier
	 *            The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
	 * @param limit
	 *            The maximum number of elements in the response array (optional)
	 * @param cursor
	 *            A server-generated identifier retrieved from pagingMetadata that
	 *            specifies from which position the result listing should continue
	 *            (optional)
	 * @param level
	 *            Determines the structural depth of the respective resource content
	 *            (optional, default to core)
	 * @return ApiResponse&lt;GetReferencesResult&gt;
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public ApiResponse<GetReferencesResult> getAllSubmodelElementsReferenceSubmodelRepoWithHttpInfo(String submodelIdentifier, Integer limit, String cursor, String level) throws ApiException {
		String submodelIdentifierAsBytes = ApiClient.base64UrlEncode(submodelIdentifier);
		return getAllSubmodelElementsReferenceSubmodelRepoWithHttpInfoNoUrlEncoding(submodelIdentifierAsBytes, limit, cursor, level);

	}

	/**
	 * Returns the References of all submodel elements
	 * 
	 * @param submodelIdentifier
	 *            The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
	 * @param limit
	 *            The maximum number of elements in the response array (optional)
	 * @param cursor
	 *            A server-generated identifier retrieved from pagingMetadata that
	 *            specifies from which position the result listing should continue
	 *            (optional)
	 * @param level
	 *            Determines the structural depth of the respective resource content
	 *            (optional, default to core)
	 * @return ApiResponse&lt;GetReferencesResult&gt;
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public ApiResponse<GetReferencesResult> getAllSubmodelElementsReferenceSubmodelRepoWithHttpInfoNoUrlEncoding(String submodelIdentifier, Integer limit, String cursor, String level) throws ApiException {
		HttpRequest.Builder localVarRequestBuilder = getAllSubmodelElementsReferenceSubmodelRepoRequestBuilder(submodelIdentifier, limit, cursor, level);
		try {
			HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(localVarRequestBuilder.build(), HttpResponse.BodyHandlers.ofInputStream());
			if (memberVarResponseInterceptor != null) {
				memberVarResponseInterceptor.accept(localVarResponse);
			}
			try {
				if (localVarResponse.statusCode() / 100 != 2) {
					throw getApiException("getAllSubmodelElementsReferenceSubmodelRepo", localVarResponse);
				}
				return new ApiResponse<GetReferencesResult>(localVarResponse.statusCode(), localVarResponse.headers().map(),
						localVarResponse.body() == null ? null : memberVarObjectMapper.readValue(localVarResponse.body(), new TypeReference<GetReferencesResult>() {
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

	private HttpRequest.Builder getAllSubmodelElementsReferenceSubmodelRepoRequestBuilder(String submodelIdentifier, Integer limit, String cursor, String level) throws ApiException {
		// verify the required parameter 'submodelIdentifier' is set
		if (submodelIdentifier == null) {
			throw new ApiException(400, "Missing the required parameter 'submodelIdentifier' when calling getAllSubmodelElementsReferenceSubmodelRepo");
		}

		HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

		String localVarPath = "/submodels/{submodelIdentifier}/submodel-elements/$reference".replace("{submodelIdentifier}", ApiClient.urlEncode(submodelIdentifier.toString()));

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
	 * Returns all submodel elements including their hierarchy in the ValueOnly
	 * representation
	 * 
	 * @param submodelIdentifier
	 *            The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
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
	 * @return GetSubmodelElementsValueResult
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public GetSubmodelElementsValueResult getAllSubmodelElementsValueOnlySubmodelRepo(String submodelIdentifier, Integer limit, String cursor, String level, String extent) throws ApiException {

		ApiResponse<GetSubmodelElementsValueResult> localVarResponse = getAllSubmodelElementsValueOnlySubmodelRepoWithHttpInfo(submodelIdentifier, limit, cursor, level, extent);
		return localVarResponse.getData();
	}

	/**
	 * Returns all submodel elements including their hierarchy in the ValueOnly
	 * representation
	 * 
	 * @param submodelIdentifier
	 *            The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
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
	 * @return ApiResponse&lt;GetSubmodelElementsValueResult&gt;
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public ApiResponse<GetSubmodelElementsValueResult> getAllSubmodelElementsValueOnlySubmodelRepoWithHttpInfo(String submodelIdentifier, Integer limit, String cursor, String level, String extent) throws ApiException {
		String submodelIdentifierAsBytes = ApiClient.base64UrlEncode(submodelIdentifier);
		return getAllSubmodelElementsValueOnlySubmodelRepoWithHttpInfoNoUrlEncoding(submodelIdentifierAsBytes, limit, cursor, level, extent);

	}

	/**
	 * Returns all submodel elements including their hierarchy in the ValueOnly
	 * representation
	 * 
	 * @param submodelIdentifier
	 *            The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
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
	 * @return ApiResponse&lt;GetSubmodelElementsValueResult&gt;
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public ApiResponse<GetSubmodelElementsValueResult> getAllSubmodelElementsValueOnlySubmodelRepoWithHttpInfoNoUrlEncoding(String submodelIdentifier, Integer limit, String cursor, String level, String extent) throws ApiException {
		HttpRequest.Builder localVarRequestBuilder = getAllSubmodelElementsValueOnlySubmodelRepoRequestBuilder(submodelIdentifier, limit, cursor, level, extent);
		try {
			HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(localVarRequestBuilder.build(), HttpResponse.BodyHandlers.ofInputStream());
			if (memberVarResponseInterceptor != null) {
				memberVarResponseInterceptor.accept(localVarResponse);
			}
			try {
				if (localVarResponse.statusCode() / 100 != 2) {
					throw getApiException("getAllSubmodelElementsValueOnlySubmodelRepo", localVarResponse);
				}
				return new ApiResponse<GetSubmodelElementsValueResult>(localVarResponse.statusCode(), localVarResponse.headers().map(),
						localVarResponse.body() == null ? null : memberVarObjectMapper.readValue(localVarResponse.body(), new TypeReference<GetSubmodelElementsValueResult>() {
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

	private HttpRequest.Builder getAllSubmodelElementsValueOnlySubmodelRepoRequestBuilder(String submodelIdentifier, Integer limit, String cursor, String level, String extent) throws ApiException {
		// verify the required parameter 'submodelIdentifier' is set
		if (submodelIdentifier == null) {
			throw new ApiException(400, "Missing the required parameter 'submodelIdentifier' when calling getAllSubmodelElementsValueOnlySubmodelRepo");
		}

		HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

		String localVarPath = "/submodels/{submodelIdentifier}/submodel-elements/$value".replace("{submodelIdentifier}", ApiClient.urlEncode(submodelIdentifier.toString()));

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
	public GetSubmodelsResult getAllSubmodels(String semanticId, String idShort, Integer limit, String cursor, String level, String extent) throws ApiException {

		ApiResponse<GetSubmodelsResult> localVarResponse = getAllSubmodelsWithHttpInfo(semanticId, idShort, limit, cursor, level, extent);
		return localVarResponse.getData();
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
	 * @return ApiResponse&lt;GetSubmodelsResult&gt;
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public ApiResponse<GetSubmodelsResult> getAllSubmodelsWithHttpInfo(String semanticId, String idShort, Integer limit, String cursor, String level, String extent) throws ApiException {
		return getAllSubmodelsWithHttpInfoNoUrlEncoding(semanticId, idShort, limit, cursor, level, extent);

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
	 * @return ApiResponse&lt;GetSubmodelsResult&gt;
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public ApiResponse<GetSubmodelsResult> getAllSubmodelsWithHttpInfoNoUrlEncoding(String semanticId, String idShort, Integer limit, String cursor, String level, String extent) throws ApiException {
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
				return new ApiResponse<GetSubmodelsResult>(localVarResponse.statusCode(), localVarResponse.headers().map(),
						localVarResponse.body() == null ? null : memberVarObjectMapper.readValue(localVarResponse.body(), new TypeReference<GetSubmodelsResult>() {
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
	 * Returns the metadata attributes of all Submodels
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
	 * @return GetSubmodelsMetadataResult
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public GetSubmodelsMetadataResult getAllSubmodelsMetadata(String semanticId, String idShort, Integer limit, String cursor, String level) throws ApiException {

		ApiResponse<GetSubmodelsMetadataResult> localVarResponse = getAllSubmodelsMetadataWithHttpInfo(semanticId, idShort, limit, cursor, level);
		return localVarResponse.getData();
	}

	/**
	 * Returns the metadata attributes of all Submodels
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
	 * @return ApiResponse&lt;GetSubmodelsMetadataResult&gt;
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public ApiResponse<GetSubmodelsMetadataResult> getAllSubmodelsMetadataWithHttpInfo(String semanticId, String idShort, Integer limit, String cursor, String level) throws ApiException {
		return getAllSubmodelsMetadataWithHttpInfoNoUrlEncoding(semanticId, idShort, limit, cursor, level);

	}

	/**
	 * Returns the metadata attributes of all Submodels
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
	 * @return ApiResponse&lt;GetSubmodelsMetadataResult&gt;
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public ApiResponse<GetSubmodelsMetadataResult> getAllSubmodelsMetadataWithHttpInfoNoUrlEncoding(String semanticId, String idShort, Integer limit, String cursor, String level) throws ApiException {
		HttpRequest.Builder localVarRequestBuilder = getAllSubmodelsMetadataRequestBuilder(semanticId, idShort, limit, cursor, level);
		try {
			HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(localVarRequestBuilder.build(), HttpResponse.BodyHandlers.ofInputStream());
			if (memberVarResponseInterceptor != null) {
				memberVarResponseInterceptor.accept(localVarResponse);
			}
			try {
				if (localVarResponse.statusCode() / 100 != 2) {
					throw getApiException("getAllSubmodelsMetadata", localVarResponse);
				}
				return new ApiResponse<GetSubmodelsMetadataResult>(localVarResponse.statusCode(), localVarResponse.headers().map(),
						localVarResponse.body() == null ? null : memberVarObjectMapper.readValue(localVarResponse.body(), new TypeReference<GetSubmodelsMetadataResult>() {
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

	private HttpRequest.Builder getAllSubmodelsMetadataRequestBuilder(String semanticId, String idShort, Integer limit, String cursor, String level) throws ApiException {

		HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

		String localVarPath = "/submodels/$metadata";

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
	 * Returns all Submodels in the Path notation
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
	 * @return GetPathItemsResult
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public GetPathItemsResult getAllSubmodelsPath(String semanticId, String idShort, Integer limit, String cursor, String level) throws ApiException {

		ApiResponse<GetPathItemsResult> localVarResponse = getAllSubmodelsPathWithHttpInfo(semanticId, idShort, limit, cursor, level);
		return localVarResponse.getData();
	}

	/**
	 * Returns all Submodels in the Path notation
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
	 * @return ApiResponse&lt;GetPathItemsResult&gt;
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public ApiResponse<GetPathItemsResult> getAllSubmodelsPathWithHttpInfo(String semanticId, String idShort, Integer limit, String cursor, String level) throws ApiException {
		return getAllSubmodelsPathWithHttpInfoNoUrlEncoding(semanticId, idShort, limit, cursor, level);

	}

	/**
	 * Returns all Submodels in the Path notation
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
	 * @return ApiResponse&lt;GetPathItemsResult&gt;
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public ApiResponse<GetPathItemsResult> getAllSubmodelsPathWithHttpInfoNoUrlEncoding(String semanticId, String idShort, Integer limit, String cursor, String level) throws ApiException {
		HttpRequest.Builder localVarRequestBuilder = getAllSubmodelsPathRequestBuilder(semanticId, idShort, limit, cursor, level);
		try {
			HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(localVarRequestBuilder.build(), HttpResponse.BodyHandlers.ofInputStream());
			if (memberVarResponseInterceptor != null) {
				memberVarResponseInterceptor.accept(localVarResponse);
			}
			try {
				if (localVarResponse.statusCode() / 100 != 2) {
					throw getApiException("getAllSubmodelsPath", localVarResponse);
				}
				return new ApiResponse<GetPathItemsResult>(localVarResponse.statusCode(), localVarResponse.headers().map(),
						localVarResponse.body() == null ? null : memberVarObjectMapper.readValue(localVarResponse.body(), new TypeReference<GetPathItemsResult>() {
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

	private HttpRequest.Builder getAllSubmodelsPathRequestBuilder(String semanticId, String idShort, Integer limit, String cursor, String level) throws ApiException {

		HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

		String localVarPath = "/submodels/$path";

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
	 * Returns the References for all Submodels
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
	 *            (optional, default to core)
	 * @return GetReferencesResult
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public GetReferencesResult getAllSubmodelsReference(String semanticId, String idShort, Integer limit, String cursor, String level) throws ApiException {

		ApiResponse<GetReferencesResult> localVarResponse = getAllSubmodelsReferenceWithHttpInfo(semanticId, idShort, limit, cursor, level);
		return localVarResponse.getData();
	}

	/**
	 * Returns the References for all Submodels
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
	 *            (optional, default to core)
	 * @return ApiResponse&lt;GetReferencesResult&gt;
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public ApiResponse<GetReferencesResult> getAllSubmodelsReferenceWithHttpInfo(String semanticId, String idShort, Integer limit, String cursor, String level) throws ApiException {
		return getAllSubmodelsReferenceWithHttpInfoNoUrlEncoding(semanticId, idShort, limit, cursor, level);

	}

	/**
	 * Returns the References for all Submodels
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
	 *            (optional, default to core)
	 * @return ApiResponse&lt;GetReferencesResult&gt;
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public ApiResponse<GetReferencesResult> getAllSubmodelsReferenceWithHttpInfoNoUrlEncoding(String semanticId, String idShort, Integer limit, String cursor, String level) throws ApiException {
		HttpRequest.Builder localVarRequestBuilder = getAllSubmodelsReferenceRequestBuilder(semanticId, idShort, limit, cursor, level);
		try {
			HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(localVarRequestBuilder.build(), HttpResponse.BodyHandlers.ofInputStream());
			if (memberVarResponseInterceptor != null) {
				memberVarResponseInterceptor.accept(localVarResponse);
			}
			try {
				if (localVarResponse.statusCode() / 100 != 2) {
					throw getApiException("getAllSubmodelsReference", localVarResponse);
				}
				return new ApiResponse<GetReferencesResult>(localVarResponse.statusCode(), localVarResponse.headers().map(),
						localVarResponse.body() == null ? null : memberVarObjectMapper.readValue(localVarResponse.body(), new TypeReference<GetReferencesResult>() {
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

	private HttpRequest.Builder getAllSubmodelsReferenceRequestBuilder(String semanticId, String idShort, Integer limit, String cursor, String level) throws ApiException {

		HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

		String localVarPath = "/submodels/$reference";

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
	 * Returns all Submodels in their ValueOnly representation
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
	 * @return GetSubmodelsValueResult
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public GetSubmodelsValueResult getAllSubmodelsValueOnly(String semanticId, String idShort, Integer limit, String cursor, String level, String extent) throws ApiException {

		ApiResponse<GetSubmodelsValueResult> localVarResponse = getAllSubmodelsValueOnlyWithHttpInfo(semanticId, idShort, limit, cursor, level, extent);
		return localVarResponse.getData();
	}

	/**
	 * Returns all Submodels in their ValueOnly representation
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
	 * @return ApiResponse&lt;GetSubmodelsValueResult&gt;
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public ApiResponse<GetSubmodelsValueResult> getAllSubmodelsValueOnlyWithHttpInfo(String semanticId, String idShort, Integer limit, String cursor, String level, String extent) throws ApiException {
		return getAllSubmodelsValueOnlyWithHttpInfoNoUrlEncoding(semanticId, idShort, limit, cursor, level, extent);

	}

	/**
	 * Returns all Submodels in their ValueOnly representation
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
	 * @return ApiResponse&lt;GetSubmodelsValueResult&gt;
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public ApiResponse<GetSubmodelsValueResult> getAllSubmodelsValueOnlyWithHttpInfoNoUrlEncoding(String semanticId, String idShort, Integer limit, String cursor, String level, String extent) throws ApiException {
		HttpRequest.Builder localVarRequestBuilder = getAllSubmodelsValueOnlyRequestBuilder(semanticId, idShort, limit, cursor, level, extent);
		try {
			HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(localVarRequestBuilder.build(), HttpResponse.BodyHandlers.ofInputStream());
			if (memberVarResponseInterceptor != null) {
				memberVarResponseInterceptor.accept(localVarResponse);
			}
			try {
				if (localVarResponse.statusCode() / 100 != 2) {
					throw getApiException("getAllSubmodelsValueOnly", localVarResponse);
				}
				return new ApiResponse<GetSubmodelsValueResult>(localVarResponse.statusCode(), localVarResponse.headers().map(),
						localVarResponse.body() == null ? null : memberVarObjectMapper.readValue(localVarResponse.body(), new TypeReference<GetSubmodelsValueResult>() {
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

	private HttpRequest.Builder getAllSubmodelsValueOnlyRequestBuilder(String semanticId, String idShort, Integer limit, String cursor, String level, String extent) throws ApiException {

		HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

		String localVarPath = "/submodels/$value";

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
	 * Returns the self-describing information of a network resource
	 * (ServiceDescription)
	 * 
	 * @return ServiceDescription
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public ServiceDescription getDescription() throws ApiException {

		ApiResponse<ServiceDescription> localVarResponse = getDescriptionWithHttpInfo();
		return localVarResponse.getData();
	}

	/**
	 * Returns the self-describing information of a network resource
	 * (ServiceDescription)
	 * 
	 * @return ApiResponse&lt;ServiceDescription&gt;
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public ApiResponse<ServiceDescription> getDescriptionWithHttpInfo() throws ApiException {
		return getDescriptionWithHttpInfoNoUrlEncoding();

	}

	/**
	 * Returns the self-describing information of a network resource
	 * (ServiceDescription)
	 * 
	 * @return ApiResponse&lt;ServiceDescription&gt;
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public ApiResponse<ServiceDescription> getDescriptionWithHttpInfoNoUrlEncoding() throws ApiException {
		HttpRequest.Builder localVarRequestBuilder = getDescriptionRequestBuilder();
		try {
			HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(localVarRequestBuilder.build(), HttpResponse.BodyHandlers.ofInputStream());
			if (memberVarResponseInterceptor != null) {
				memberVarResponseInterceptor.accept(localVarResponse);
			}
			try {
				if (localVarResponse.statusCode() / 100 != 2) {
					throw getApiException("getDescription", localVarResponse);
				}
				return new ApiResponse<ServiceDescription>(localVarResponse.statusCode(), localVarResponse.headers().map(),
						localVarResponse.body() == null ? null : memberVarObjectMapper.readValue(localVarResponse.body(), new TypeReference<ServiceDescription>() {
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
	 * Downloads file content from a specific submodel element from the Submodel at
	 * a specified path
	 * 
	 * @param submodelIdentifier
	 *            The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
	 * @param idShortPath
	 *            IdShort path to the submodel element (dot-separated) (required)
	 * @return File
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public File getFileByPathSubmodelRepo(String submodelIdentifier, String idShortPath) throws ApiException {

		ApiResponse<File> localVarResponse = getFileByPathSubmodelRepoWithHttpInfo(submodelIdentifier, idShortPath);
		return localVarResponse.getData();
	}

	/**
	 * Downloads file content from a specific submodel element from the Submodel at
	 * a specified path
	 * 
	 * @param submodelIdentifier
	 *            The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
	 * @param idShortPath
	 *            IdShort path to the submodel element (dot-separated) (required)
	 * @return ApiResponse&lt;File&gt;
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public ApiResponse<File> getFileByPathSubmodelRepoWithHttpInfo(String submodelIdentifier, String idShortPath) throws ApiException {
		String submodelIdentifierAsBytes = ApiClient.base64UrlEncode(submodelIdentifier);
		return getFileByPathSubmodelRepoWithHttpInfoNoUrlEncoding(submodelIdentifierAsBytes, idShortPath);

	}

	/**
	 * Downloads file content from a specific submodel element from the Submodel at
	 * a specified path
	 * 
	 * @param submodelIdentifier
	 *            The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
	 * @param idShortPath
	 *            IdShort path to the submodel element (dot-separated) (required)
	 * @return ApiResponse&lt;File&gt;
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public ApiResponse<File> getFileByPathSubmodelRepoWithHttpInfoNoUrlEncoding(String submodelIdentifier, String idShortPath) throws ApiException {
		HttpRequest.Builder localVarRequestBuilder = getFileByPathSubmodelRepoRequestBuilder(submodelIdentifier, idShortPath);
		try {
			HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(localVarRequestBuilder.build(), HttpResponse.BodyHandlers.ofInputStream());
			if (memberVarResponseInterceptor != null) {
				memberVarResponseInterceptor.accept(localVarResponse);
			}
			try {
				if (localVarResponse.statusCode() / 100 != 2) {
					throw getApiException("getFileByPathSubmodelRepo", localVarResponse);
				}
				return new ApiResponse<File>(localVarResponse.statusCode(), localVarResponse.headers().map(), localVarResponse.body() == null ? null : memberVarObjectMapper.readValue(localVarResponse.body(), new TypeReference<File>() {
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

	private HttpRequest.Builder getFileByPathSubmodelRepoRequestBuilder(String submodelIdentifier, String idShortPath) throws ApiException {
		// verify the required parameter 'submodelIdentifier' is set
		if (submodelIdentifier == null) {
			throw new ApiException(400, "Missing the required parameter 'submodelIdentifier' when calling getFileByPathSubmodelRepo");
		}
		// verify the required parameter 'idShortPath' is set
		if (idShortPath == null) {
			throw new ApiException(400, "Missing the required parameter 'idShortPath' when calling getFileByPathSubmodelRepo");
		}

		HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

		String localVarPath = "/submodels/{submodelIdentifier}/submodel-elements/{idShortPath}/attachment".replace("{submodelIdentifier}", ApiClient.urlEncode(submodelIdentifier.toString())).replace("{idShortPath}",
				ApiClient.urlEncode(idShortPath.toString()));

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
	 * Returns the Operation result of an asynchronously invoked Operation
	 * 
	 * @param submodelIdentifier
	 *            The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
	 * @param idShortPath
	 *            IdShort path to the submodel element (dot-separated) (required)
	 * @param handleId
	 *            The returned handle id of an operation’s asynchronous invocation
	 *            used to request the current state of the operation’s execution
	 *            (UTF8-BASE64-URL-encoded) (required)
	 * @return OperationResult
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public OperationResult getOperationAsyncResult(String submodelIdentifier, String idShortPath, String handleId) throws ApiException {

		ApiResponse<OperationResult> localVarResponse = getOperationAsyncResultWithHttpInfo(submodelIdentifier, idShortPath, handleId);
		return localVarResponse.getData();
	}

	/**
	 * Returns the Operation result of an asynchronously invoked Operation
	 * 
	 * @param submodelIdentifier
	 *            The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
	 * @param idShortPath
	 *            IdShort path to the submodel element (dot-separated) (required)
	 * @param handleId
	 *            The returned handle id of an operation’s asynchronous invocation
	 *            used to request the current state of the operation’s execution
	 *            (UTF8-BASE64-URL-encoded) (required)
	 * @return ApiResponse&lt;OperationResult&gt;
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public ApiResponse<OperationResult> getOperationAsyncResultWithHttpInfo(String submodelIdentifier, String idShortPath, String handleId) throws ApiException {
		String submodelIdentifierAsBytes = ApiClient.base64UrlEncode(submodelIdentifier);
		String handleIdAsBytes = ApiClient.base64UrlEncode(handleId);
		return getOperationAsyncResultWithHttpInfoNoUrlEncoding(submodelIdentifierAsBytes, idShortPath, handleIdAsBytes);

	}

	/**
	 * Returns the Operation result of an asynchronously invoked Operation
	 * 
	 * @param submodelIdentifier
	 *            The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
	 * @param idShortPath
	 *            IdShort path to the submodel element (dot-separated) (required)
	 * @param handleId
	 *            The returned handle id of an operation’s asynchronous invocation
	 *            used to request the current state of the operation’s execution
	 *            (UTF8-BASE64-URL-encoded) (required)
	 * @return ApiResponse&lt;OperationResult&gt;
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public ApiResponse<OperationResult> getOperationAsyncResultWithHttpInfoNoUrlEncoding(String submodelIdentifier, String idShortPath, String handleId) throws ApiException {
		HttpRequest.Builder localVarRequestBuilder = getOperationAsyncResultRequestBuilder(submodelIdentifier, idShortPath, handleId);
		try {
			HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(localVarRequestBuilder.build(), HttpResponse.BodyHandlers.ofInputStream());
			if (memberVarResponseInterceptor != null) {
				memberVarResponseInterceptor.accept(localVarResponse);
			}
			try {
				if (localVarResponse.statusCode() / 100 != 2) {
					throw getApiException("getOperationAsyncResult", localVarResponse);
				}
				return new ApiResponse<OperationResult>(localVarResponse.statusCode(), localVarResponse.headers().map(),
						localVarResponse.body() == null ? null : memberVarObjectMapper.readValue(localVarResponse.body(), new TypeReference<OperationResult>() {
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

	private HttpRequest.Builder getOperationAsyncResultRequestBuilder(String submodelIdentifier, String idShortPath, String handleId) throws ApiException {
		// verify the required parameter 'submodelIdentifier' is set
		if (submodelIdentifier == null) {
			throw new ApiException(400, "Missing the required parameter 'submodelIdentifier' when calling getOperationAsyncResult");
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

		String localVarPath = "/submodels/{submodelIdentifier}/submodel-elements/{idShortPath}/operation-results/{handleId}".replace("{submodelIdentifier}", ApiClient.urlEncode(submodelIdentifier.toString()))
				.replace("{idShortPath}", ApiClient.urlEncode(idShortPath.toString())).replace("{handleId}", ApiClient.urlEncode(handleId.toString()));

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
	 * Returns the Operation result of an asynchronously invoked Operation
	 * 
	 * @param submodelIdentifier
	 *            The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
	 * @param idShortPath
	 *            IdShort path to the submodel element (dot-separated) (required)
	 * @param handleId
	 *            The returned handle id of an operation’s asynchronous invocation
	 *            used to request the current state of the operation’s execution
	 *            (UTF8-BASE64-URL-encoded) (required)
	 * @return OperationResultValueOnly
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public OperationResultValueOnly getOperationAsyncResultValueOnly(String submodelIdentifier, String idShortPath, String handleId) throws ApiException {

		ApiResponse<OperationResultValueOnly> localVarResponse = getOperationAsyncResultValueOnlyWithHttpInfo(submodelIdentifier, idShortPath, handleId);
		return localVarResponse.getData();
	}

	/**
	 * Returns the Operation result of an asynchronously invoked Operation
	 * 
	 * @param submodelIdentifier
	 *            The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
	 * @param idShortPath
	 *            IdShort path to the submodel element (dot-separated) (required)
	 * @param handleId
	 *            The returned handle id of an operation’s asynchronous invocation
	 *            used to request the current state of the operation’s execution
	 *            (UTF8-BASE64-URL-encoded) (required)
	 * @return ApiResponse&lt;OperationResultValueOnly&gt;
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public ApiResponse<OperationResultValueOnly> getOperationAsyncResultValueOnlyWithHttpInfo(String submodelIdentifier, String idShortPath, String handleId) throws ApiException {
		String submodelIdentifierAsBytes = ApiClient.base64UrlEncode(submodelIdentifier);
		String handleIdAsBytes = ApiClient.base64UrlEncode(handleId);
		return getOperationAsyncResultValueOnlyWithHttpInfoNoUrlEncoding(submodelIdentifierAsBytes, idShortPath, handleIdAsBytes);

	}

	/**
	 * Returns the Operation result of an asynchronously invoked Operation
	 * 
	 * @param submodelIdentifier
	 *            The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
	 * @param idShortPath
	 *            IdShort path to the submodel element (dot-separated) (required)
	 * @param handleId
	 *            The returned handle id of an operation’s asynchronous invocation
	 *            used to request the current state of the operation’s execution
	 *            (UTF8-BASE64-URL-encoded) (required)
	 * @return ApiResponse&lt;OperationResultValueOnly&gt;
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public ApiResponse<OperationResultValueOnly> getOperationAsyncResultValueOnlyWithHttpInfoNoUrlEncoding(String submodelIdentifier, String idShortPath, String handleId) throws ApiException {
		HttpRequest.Builder localVarRequestBuilder = getOperationAsyncResultValueOnlyRequestBuilder(submodelIdentifier, idShortPath, handleId);
		try {
			HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(localVarRequestBuilder.build(), HttpResponse.BodyHandlers.ofInputStream());
			if (memberVarResponseInterceptor != null) {
				memberVarResponseInterceptor.accept(localVarResponse);
			}
			try {
				if (localVarResponse.statusCode() / 100 != 2) {
					throw getApiException("getOperationAsyncResultValueOnly", localVarResponse);
				}
				return new ApiResponse<OperationResultValueOnly>(localVarResponse.statusCode(), localVarResponse.headers().map(),
						localVarResponse.body() == null ? null : memberVarObjectMapper.readValue(localVarResponse.body(), new TypeReference<OperationResultValueOnly>() {
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

	private HttpRequest.Builder getOperationAsyncResultValueOnlyRequestBuilder(String submodelIdentifier, String idShortPath, String handleId) throws ApiException {
		// verify the required parameter 'submodelIdentifier' is set
		if (submodelIdentifier == null) {
			throw new ApiException(400, "Missing the required parameter 'submodelIdentifier' when calling getOperationAsyncResultValueOnly");
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

		String localVarPath = "/submodels/{submodelIdentifier}/submodel-elements/{idShortPath}/operation-results/{handleId}/$value".replace("{submodelIdentifier}", ApiClient.urlEncode(submodelIdentifier.toString()))
				.replace("{idShortPath}", ApiClient.urlEncode(idShortPath.toString())).replace("{handleId}", ApiClient.urlEncode(handleId.toString()));

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
	 * @param submodelIdentifier
	 *            The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
	 * @param idShortPath
	 *            IdShort path to the submodel element (dot-separated) (required)
	 * @param handleId
	 *            The returned handle id of an operation’s asynchronous invocation
	 *            used to request the current state of the operation’s execution
	 *            (UTF8-BASE64-URL-encoded) (required)
	 * @return BaseOperationResult
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public BaseOperationResult getOperationAsyncStatus(String submodelIdentifier, String idShortPath, String handleId) throws ApiException {

		ApiResponse<BaseOperationResult> localVarResponse = getOperationAsyncStatusWithHttpInfo(submodelIdentifier, idShortPath, handleId);
		return localVarResponse.getData();
	}

	/**
	 * Returns the status of an asynchronously invoked Operation
	 * 
	 * @param submodelIdentifier
	 *            The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
	 * @param idShortPath
	 *            IdShort path to the submodel element (dot-separated) (required)
	 * @param handleId
	 *            The returned handle id of an operation’s asynchronous invocation
	 *            used to request the current state of the operation’s execution
	 *            (UTF8-BASE64-URL-encoded) (required)
	 * @return ApiResponse&lt;BaseOperationResult&gt;
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public ApiResponse<BaseOperationResult> getOperationAsyncStatusWithHttpInfo(String submodelIdentifier, String idShortPath, String handleId) throws ApiException {
		String submodelIdentifierAsBytes = ApiClient.base64UrlEncode(submodelIdentifier);
		String handleIdAsBytes = ApiClient.base64UrlEncode(handleId);
		return getOperationAsyncStatusWithHttpInfoNoUrlEncoding(submodelIdentifierAsBytes, idShortPath, handleIdAsBytes);

	}

	/**
	 * Returns the status of an asynchronously invoked Operation
	 * 
	 * @param submodelIdentifier
	 *            The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
	 * @param idShortPath
	 *            IdShort path to the submodel element (dot-separated) (required)
	 * @param handleId
	 *            The returned handle id of an operation’s asynchronous invocation
	 *            used to request the current state of the operation’s execution
	 *            (UTF8-BASE64-URL-encoded) (required)
	 * @return ApiResponse&lt;BaseOperationResult&gt;
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public ApiResponse<BaseOperationResult> getOperationAsyncStatusWithHttpInfoNoUrlEncoding(String submodelIdentifier, String idShortPath, String handleId) throws ApiException {
		HttpRequest.Builder localVarRequestBuilder = getOperationAsyncStatusRequestBuilder(submodelIdentifier, idShortPath, handleId);
		try {
			HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(localVarRequestBuilder.build(), HttpResponse.BodyHandlers.ofInputStream());
			if (memberVarResponseInterceptor != null) {
				memberVarResponseInterceptor.accept(localVarResponse);
			}
			try {
				if (localVarResponse.statusCode() / 100 != 2) {
					throw getApiException("getOperationAsyncStatus", localVarResponse);
				}
				return new ApiResponse<BaseOperationResult>(localVarResponse.statusCode(), localVarResponse.headers().map(),
						localVarResponse.body() == null ? null : memberVarObjectMapper.readValue(localVarResponse.body(), new TypeReference<BaseOperationResult>() {
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

	private HttpRequest.Builder getOperationAsyncStatusRequestBuilder(String submodelIdentifier, String idShortPath, String handleId) throws ApiException {
		// verify the required parameter 'submodelIdentifier' is set
		if (submodelIdentifier == null) {
			throw new ApiException(400, "Missing the required parameter 'submodelIdentifier' when calling getOperationAsyncStatus");
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

		String localVarPath = "/submodels/{submodelIdentifier}/submodel-elements/{idShortPath}/operation-status/{handleId}".replace("{submodelIdentifier}", ApiClient.urlEncode(submodelIdentifier.toString()))
				.replace("{idShortPath}", ApiClient.urlEncode(idShortPath.toString())).replace("{handleId}", ApiClient.urlEncode(handleId.toString()));

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
	 * @param submodelIdentifier
	 *            The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
	 * @param level
	 *            Determines the structural depth of the respective resource content
	 *            (optional, default to deep)
	 * @return SubmodelMetadata
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public SubmodelMetadata getSubmodelByIdMetadata(String submodelIdentifier, String level) throws ApiException {

		ApiResponse<SubmodelMetadata> localVarResponse = getSubmodelByIdMetadataWithHttpInfo(submodelIdentifier, level);
		return localVarResponse.getData();
	}

	/**
	 * Returns the metadata attributes of a specific Submodel
	 * 
	 * @param submodelIdentifier
	 *            The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
	 * @param level
	 *            Determines the structural depth of the respective resource content
	 *            (optional, default to deep)
	 * @return ApiResponse&lt;SubmodelMetadata&gt;
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public ApiResponse<SubmodelMetadata> getSubmodelByIdMetadataWithHttpInfo(String submodelIdentifier, String level) throws ApiException {
		String submodelIdentifierAsBytes = ApiClient.base64UrlEncode(submodelIdentifier);
		return getSubmodelByIdMetadataWithHttpInfoNoUrlEncoding(submodelIdentifierAsBytes, level);

	}

	/**
	 * Returns the metadata attributes of a specific Submodel
	 * 
	 * @param submodelIdentifier
	 *            The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
	 * @param level
	 *            Determines the structural depth of the respective resource content
	 *            (optional, default to deep)
	 * @return ApiResponse&lt;SubmodelMetadata&gt;
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public ApiResponse<SubmodelMetadata> getSubmodelByIdMetadataWithHttpInfoNoUrlEncoding(String submodelIdentifier, String level) throws ApiException {
		HttpRequest.Builder localVarRequestBuilder = getSubmodelByIdMetadataRequestBuilder(submodelIdentifier, level);
		try {
			HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(localVarRequestBuilder.build(), HttpResponse.BodyHandlers.ofInputStream());
			if (memberVarResponseInterceptor != null) {
				memberVarResponseInterceptor.accept(localVarResponse);
			}
			try {
				if (localVarResponse.statusCode() / 100 != 2) {
					throw getApiException("getSubmodelByIdMetadata", localVarResponse);
				}
				return new ApiResponse<SubmodelMetadata>(localVarResponse.statusCode(), localVarResponse.headers().map(),
						localVarResponse.body() == null ? null : memberVarObjectMapper.readValue(localVarResponse.body(), new TypeReference<SubmodelMetadata>() {
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

	private HttpRequest.Builder getSubmodelByIdMetadataRequestBuilder(String submodelIdentifier, String level) throws ApiException {
		// verify the required parameter 'submodelIdentifier' is set
		if (submodelIdentifier == null) {
			throw new ApiException(400, "Missing the required parameter 'submodelIdentifier' when calling getSubmodelByIdMetadata");
		}

		HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

		String localVarPath = "/submodels/{submodelIdentifier}/$metadata".replace("{submodelIdentifier}", ApiClient.urlEncode(submodelIdentifier.toString()));

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
	 * Returns a specific Submodel in the Path notation
	 * 
	 * @param submodelIdentifier
	 *            The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
	 * @param level
	 *            Determines the structural depth of the respective resource content
	 *            (optional, default to deep)
	 * @return List&lt;String&gt;
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public List<String> getSubmodelByIdPath(String submodelIdentifier, String level) throws ApiException {

		ApiResponse<List<String>> localVarResponse = getSubmodelByIdPathWithHttpInfo(submodelIdentifier, level);
		return localVarResponse.getData();
	}

	/**
	 * Returns a specific Submodel in the Path notation
	 * 
	 * @param submodelIdentifier
	 *            The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
	 * @param level
	 *            Determines the structural depth of the respective resource content
	 *            (optional, default to deep)
	 * @return ApiResponse&lt;List&lt;String&gt;&gt;
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public ApiResponse<List<String>> getSubmodelByIdPathWithHttpInfo(String submodelIdentifier, String level) throws ApiException {
		String submodelIdentifierAsBytes = ApiClient.base64UrlEncode(submodelIdentifier);
		return getSubmodelByIdPathWithHttpInfoNoUrlEncoding(submodelIdentifierAsBytes, level);

	}

	/**
	 * Returns a specific Submodel in the Path notation
	 * 
	 * @param submodelIdentifier
	 *            The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
	 * @param level
	 *            Determines the structural depth of the respective resource content
	 *            (optional, default to deep)
	 * @return ApiResponse&lt;List&lt;String&gt;&gt;
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public ApiResponse<List<String>> getSubmodelByIdPathWithHttpInfoNoUrlEncoding(String submodelIdentifier, String level) throws ApiException {
		HttpRequest.Builder localVarRequestBuilder = getSubmodelByIdPathRequestBuilder(submodelIdentifier, level);
		try {
			HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(localVarRequestBuilder.build(), HttpResponse.BodyHandlers.ofInputStream());
			if (memberVarResponseInterceptor != null) {
				memberVarResponseInterceptor.accept(localVarResponse);
			}
			try {
				if (localVarResponse.statusCode() / 100 != 2) {
					throw getApiException("getSubmodelByIdPath", localVarResponse);
				}
				return new ApiResponse<List<String>>(localVarResponse.statusCode(), localVarResponse.headers().map(),
						localVarResponse.body() == null ? null : memberVarObjectMapper.readValue(localVarResponse.body(), new TypeReference<List<String>>() {
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

	private HttpRequest.Builder getSubmodelByIdPathRequestBuilder(String submodelIdentifier, String level) throws ApiException {
		// verify the required parameter 'submodelIdentifier' is set
		if (submodelIdentifier == null) {
			throw new ApiException(400, "Missing the required parameter 'submodelIdentifier' when calling getSubmodelByIdPath");
		}

		HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

		String localVarPath = "/submodels/{submodelIdentifier}/$path".replace("{submodelIdentifier}", ApiClient.urlEncode(submodelIdentifier.toString()));

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
	 * Returns the Reference of a specific Submodel
	 * 
	 * @param submodelIdentifier
	 *            The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
	 * @return Reference
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public Reference getSubmodelByIdReference(String submodelIdentifier) throws ApiException {

		ApiResponse<Reference> localVarResponse = getSubmodelByIdReferenceWithHttpInfo(submodelIdentifier);
		return localVarResponse.getData();
	}

	/**
	 * Returns the Reference of a specific Submodel
	 * 
	 * @param submodelIdentifier
	 *            The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
	 * @return ApiResponse&lt;Reference&gt;
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public ApiResponse<Reference> getSubmodelByIdReferenceWithHttpInfo(String submodelIdentifier) throws ApiException {
		String submodelIdentifierAsBytes = ApiClient.base64UrlEncode(submodelIdentifier);
		return getSubmodelByIdReferenceWithHttpInfoNoUrlEncoding(submodelIdentifierAsBytes);

	}

	/**
	 * Returns the Reference of a specific Submodel
	 * 
	 * @param submodelIdentifier
	 *            The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
	 * @return ApiResponse&lt;Reference&gt;
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public ApiResponse<Reference> getSubmodelByIdReferenceWithHttpInfoNoUrlEncoding(String submodelIdentifier) throws ApiException {
		HttpRequest.Builder localVarRequestBuilder = getSubmodelByIdReferenceRequestBuilder(submodelIdentifier);
		try {
			HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(localVarRequestBuilder.build(), HttpResponse.BodyHandlers.ofInputStream());
			if (memberVarResponseInterceptor != null) {
				memberVarResponseInterceptor.accept(localVarResponse);
			}
			try {
				if (localVarResponse.statusCode() / 100 != 2) {
					throw getApiException("getSubmodelByIdReference", localVarResponse);
				}
				return new ApiResponse<Reference>(localVarResponse.statusCode(), localVarResponse.headers().map(),
						localVarResponse.body() == null ? null : memberVarObjectMapper.readValue(localVarResponse.body(), new TypeReference<Reference>() {
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

	private HttpRequest.Builder getSubmodelByIdReferenceRequestBuilder(String submodelIdentifier) throws ApiException {
		// verify the required parameter 'submodelIdentifier' is set
		if (submodelIdentifier == null) {
			throw new ApiException(400, "Missing the required parameter 'submodelIdentifier' when calling getSubmodelByIdReference");
		}

		HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

		String localVarPath = "/submodels/{submodelIdentifier}/$reference".replace("{submodelIdentifier}", ApiClient.urlEncode(submodelIdentifier.toString()));

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
	 * Returns a specific Submodel in the ValueOnly representation
	 * 
	 * @param submodelIdentifier
	 *            The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
	 * @param level
	 *            Determines the structural depth of the respective resource content
	 *            (optional, default to deep)
	 * @param extent
	 *            Determines to which extent the resource is being serialized
	 *            (optional, default to withoutBlobValue)
	 * @return SubmodelValue
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public SubmodelValue getSubmodelByIdValueOnly(String submodelIdentifier, String level, String extent) throws ApiException {

		ApiResponse<SubmodelValue> localVarResponse = getSubmodelByIdValueOnlyWithHttpInfo(submodelIdentifier, level, extent);
		return localVarResponse.getData();
	}

	/**
	 * Returns a specific Submodel in the ValueOnly representation
	 * 
	 * @param submodelIdentifier
	 *            The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
	 * @param level
	 *            Determines the structural depth of the respective resource content
	 *            (optional, default to deep)
	 * @param extent
	 *            Determines to which extent the resource is being serialized
	 *            (optional, default to withoutBlobValue)
	 * @return ApiResponse&lt;SubmodelValue&gt;
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public ApiResponse<SubmodelValue> getSubmodelByIdValueOnlyWithHttpInfo(String submodelIdentifier, String level, String extent) throws ApiException {
		String submodelIdentifierAsBytes = ApiClient.base64UrlEncode(submodelIdentifier);
		return getSubmodelByIdValueOnlyWithHttpInfoNoUrlEncoding(submodelIdentifierAsBytes, level, extent);

	}

	/**
	 * Returns a specific Submodel in the ValueOnly representation
	 * 
	 * @param submodelIdentifier
	 *            The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
	 * @param level
	 *            Determines the structural depth of the respective resource content
	 *            (optional, default to deep)
	 * @param extent
	 *            Determines to which extent the resource is being serialized
	 *            (optional, default to withoutBlobValue)
	 * @return ApiResponse&lt;SubmodelValue&gt;
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public ApiResponse<SubmodelValue> getSubmodelByIdValueOnlyWithHttpInfoNoUrlEncoding(String submodelIdentifier, String level, String extent) throws ApiException {
		HttpRequest.Builder localVarRequestBuilder = getSubmodelByIdValueOnlyRequestBuilder(submodelIdentifier, level, extent);
		try {
			HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(localVarRequestBuilder.build(), HttpResponse.BodyHandlers.ofInputStream());
			if (memberVarResponseInterceptor != null) {
				memberVarResponseInterceptor.accept(localVarResponse);
			}
			try {
				if (localVarResponse.statusCode() / 100 != 2) {
					throw getApiException("getSubmodelByIdValueOnly", localVarResponse);
				}
				return new ApiResponse<SubmodelValue>(localVarResponse.statusCode(), localVarResponse.headers().map(),
						localVarResponse.body() == null ? null : memberVarObjectMapper.readValue(localVarResponse.body(), new TypeReference<SubmodelValue>() {
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

	private HttpRequest.Builder getSubmodelByIdValueOnlyRequestBuilder(String submodelIdentifier, String level, String extent) throws ApiException {
		// verify the required parameter 'submodelIdentifier' is set
		if (submodelIdentifier == null) {
			throw new ApiException(400, "Missing the required parameter 'submodelIdentifier' when calling getSubmodelByIdValueOnly");
		}

		HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

		String localVarPath = "/submodels/{submodelIdentifier}/$value".replace("{submodelIdentifier}", ApiClient.urlEncode(submodelIdentifier.toString()));

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
	 * Returns the matadata attributes of a specific submodel element from the
	 * Submodel at a specified path
	 * 
	 * @param submodelIdentifier
	 *            The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
	 * @param idShortPath
	 *            IdShort path to the submodel element (dot-separated) (required)
	 * @param level
	 *            Determines the structural depth of the respective resource content
	 *            (optional, default to deep)
	 * @return SubmodelElementMetadata
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public SubmodelElementMetadata getSubmodelElementByPathMetadataSubmodelRepo(String submodelIdentifier, String idShortPath, String level) throws ApiException {

		ApiResponse<SubmodelElementMetadata> localVarResponse = getSubmodelElementByPathMetadataSubmodelRepoWithHttpInfo(submodelIdentifier, idShortPath, level);
		return localVarResponse.getData();
	}

	/**
	 * Returns the matadata attributes of a specific submodel element from the
	 * Submodel at a specified path
	 * 
	 * @param submodelIdentifier
	 *            The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
	 * @param idShortPath
	 *            IdShort path to the submodel element (dot-separated) (required)
	 * @param level
	 *            Determines the structural depth of the respective resource content
	 *            (optional, default to deep)
	 * @return ApiResponse&lt;SubmodelElementMetadata&gt;
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public ApiResponse<SubmodelElementMetadata> getSubmodelElementByPathMetadataSubmodelRepoWithHttpInfo(String submodelIdentifier, String idShortPath, String level) throws ApiException {
		String submodelIdentifierAsBytes = ApiClient.base64UrlEncode(submodelIdentifier);
		return getSubmodelElementByPathMetadataSubmodelRepoWithHttpInfoNoUrlEncoding(submodelIdentifierAsBytes, idShortPath, level);

	}

	/**
	 * Returns the matadata attributes of a specific submodel element from the
	 * Submodel at a specified path
	 * 
	 * @param submodelIdentifier
	 *            The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
	 * @param idShortPath
	 *            IdShort path to the submodel element (dot-separated) (required)
	 * @param level
	 *            Determines the structural depth of the respective resource content
	 *            (optional, default to deep)
	 * @return ApiResponse&lt;SubmodelElementMetadata&gt;
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public ApiResponse<SubmodelElementMetadata> getSubmodelElementByPathMetadataSubmodelRepoWithHttpInfoNoUrlEncoding(String submodelIdentifier, String idShortPath, String level) throws ApiException {
		HttpRequest.Builder localVarRequestBuilder = getSubmodelElementByPathMetadataSubmodelRepoRequestBuilder(submodelIdentifier, idShortPath, level);
		try {
			HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(localVarRequestBuilder.build(), HttpResponse.BodyHandlers.ofInputStream());
			if (memberVarResponseInterceptor != null) {
				memberVarResponseInterceptor.accept(localVarResponse);
			}
			try {
				if (localVarResponse.statusCode() / 100 != 2) {
					throw getApiException("getSubmodelElementByPathMetadataSubmodelRepo", localVarResponse);
				}
				return new ApiResponse<SubmodelElementMetadata>(localVarResponse.statusCode(), localVarResponse.headers().map(),
						localVarResponse.body() == null ? null : memberVarObjectMapper.readValue(localVarResponse.body(), new TypeReference<SubmodelElementMetadata>() {
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

	private HttpRequest.Builder getSubmodelElementByPathMetadataSubmodelRepoRequestBuilder(String submodelIdentifier, String idShortPath, String level) throws ApiException {
		// verify the required parameter 'submodelIdentifier' is set
		if (submodelIdentifier == null) {
			throw new ApiException(400, "Missing the required parameter 'submodelIdentifier' when calling getSubmodelElementByPathMetadataSubmodelRepo");
		}
		// verify the required parameter 'idShortPath' is set
		if (idShortPath == null) {
			throw new ApiException(400, "Missing the required parameter 'idShortPath' when calling getSubmodelElementByPathMetadataSubmodelRepo");
		}

		HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

		String localVarPath = "/submodels/{submodelIdentifier}/submodel-elements/{idShortPath}/$metadata".replace("{submodelIdentifier}", ApiClient.urlEncode(submodelIdentifier.toString())).replace("{idShortPath}",
				ApiClient.urlEncode(idShortPath.toString()));

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
	 * Returns a specific submodel element from the Submodel at a specified path in
	 * the Path notation
	 * 
	 * @param submodelIdentifier
	 *            The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
	 * @param idShortPath
	 *            IdShort path to the submodel element (dot-separated) (required)
	 * @param level
	 *            Determines the structural depth of the respective resource content
	 *            (optional, default to deep)
	 * @return List&lt;String&gt;
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public List<String> getSubmodelElementByPathPathSubmodelRepo(String submodelIdentifier, String idShortPath, String level) throws ApiException {

		ApiResponse<List<String>> localVarResponse = getSubmodelElementByPathPathSubmodelRepoWithHttpInfo(submodelIdentifier, idShortPath, level);
		return localVarResponse.getData();
	}

	/**
	 * Returns a specific submodel element from the Submodel at a specified path in
	 * the Path notation
	 * 
	 * @param submodelIdentifier
	 *            The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
	 * @param idShortPath
	 *            IdShort path to the submodel element (dot-separated) (required)
	 * @param level
	 *            Determines the structural depth of the respective resource content
	 *            (optional, default to deep)
	 * @return ApiResponse&lt;List&lt;String&gt;&gt;
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public ApiResponse<List<String>> getSubmodelElementByPathPathSubmodelRepoWithHttpInfo(String submodelIdentifier, String idShortPath, String level) throws ApiException {
		String submodelIdentifierAsBytes = ApiClient.base64UrlEncode(submodelIdentifier);
		return getSubmodelElementByPathPathSubmodelRepoWithHttpInfoNoUrlEncoding(submodelIdentifierAsBytes, idShortPath, level);

	}

	/**
	 * Returns a specific submodel element from the Submodel at a specified path in
	 * the Path notation
	 * 
	 * @param submodelIdentifier
	 *            The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
	 * @param idShortPath
	 *            IdShort path to the submodel element (dot-separated) (required)
	 * @param level
	 *            Determines the structural depth of the respective resource content
	 *            (optional, default to deep)
	 * @return ApiResponse&lt;List&lt;String&gt;&gt;
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public ApiResponse<List<String>> getSubmodelElementByPathPathSubmodelRepoWithHttpInfoNoUrlEncoding(String submodelIdentifier, String idShortPath, String level) throws ApiException {
		HttpRequest.Builder localVarRequestBuilder = getSubmodelElementByPathPathSubmodelRepoRequestBuilder(submodelIdentifier, idShortPath, level);
		try {
			HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(localVarRequestBuilder.build(), HttpResponse.BodyHandlers.ofInputStream());
			if (memberVarResponseInterceptor != null) {
				memberVarResponseInterceptor.accept(localVarResponse);
			}
			try {
				if (localVarResponse.statusCode() / 100 != 2) {
					throw getApiException("getSubmodelElementByPathPathSubmodelRepo", localVarResponse);
				}
				return new ApiResponse<List<String>>(localVarResponse.statusCode(), localVarResponse.headers().map(),
						localVarResponse.body() == null ? null : memberVarObjectMapper.readValue(localVarResponse.body(), new TypeReference<List<String>>() {
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

	private HttpRequest.Builder getSubmodelElementByPathPathSubmodelRepoRequestBuilder(String submodelIdentifier, String idShortPath, String level) throws ApiException {
		// verify the required parameter 'submodelIdentifier' is set
		if (submodelIdentifier == null) {
			throw new ApiException(400, "Missing the required parameter 'submodelIdentifier' when calling getSubmodelElementByPathPathSubmodelRepo");
		}
		// verify the required parameter 'idShortPath' is set
		if (idShortPath == null) {
			throw new ApiException(400, "Missing the required parameter 'idShortPath' when calling getSubmodelElementByPathPathSubmodelRepo");
		}

		HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

		String localVarPath = "/submodels/{submodelIdentifier}/submodel-elements/{idShortPath}/$path".replace("{submodelIdentifier}", ApiClient.urlEncode(submodelIdentifier.toString())).replace("{idShortPath}",
				ApiClient.urlEncode(idShortPath.toString()));

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
	 * Returns the Referene of a specific submodel element from the Submodel at a
	 * specified path
	 * 
	 * @param submodelIdentifier
	 *            The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
	 * @param idShortPath
	 *            IdShort path to the submodel element (dot-separated) (required)
	 * @param level
	 *            Determines the structural depth of the respective resource content
	 *            (optional, default to core)
	 * @return Reference
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public Reference getSubmodelElementByPathReferenceSubmodelRepo(String submodelIdentifier, String idShortPath, String level) throws ApiException {

		ApiResponse<Reference> localVarResponse = getSubmodelElementByPathReferenceSubmodelRepoWithHttpInfo(submodelIdentifier, idShortPath, level);
		return localVarResponse.getData();
	}

	/**
	 * Returns the Referene of a specific submodel element from the Submodel at a
	 * specified path
	 * 
	 * @param submodelIdentifier
	 *            The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
	 * @param idShortPath
	 *            IdShort path to the submodel element (dot-separated) (required)
	 * @param level
	 *            Determines the structural depth of the respective resource content
	 *            (optional, default to core)
	 * @return ApiResponse&lt;Reference&gt;
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public ApiResponse<Reference> getSubmodelElementByPathReferenceSubmodelRepoWithHttpInfo(String submodelIdentifier, String idShortPath, String level) throws ApiException {
		String submodelIdentifierAsBytes = ApiClient.base64UrlEncode(submodelIdentifier);
		return getSubmodelElementByPathReferenceSubmodelRepoWithHttpInfoNoUrlEncoding(submodelIdentifierAsBytes, idShortPath, level);

	}

	/**
	 * Returns the Referene of a specific submodel element from the Submodel at a
	 * specified path
	 * 
	 * @param submodelIdentifier
	 *            The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
	 * @param idShortPath
	 *            IdShort path to the submodel element (dot-separated) (required)
	 * @param level
	 *            Determines the structural depth of the respective resource content
	 *            (optional, default to core)
	 * @return ApiResponse&lt;Reference&gt;
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public ApiResponse<Reference> getSubmodelElementByPathReferenceSubmodelRepoWithHttpInfoNoUrlEncoding(String submodelIdentifier, String idShortPath, String level) throws ApiException {
		HttpRequest.Builder localVarRequestBuilder = getSubmodelElementByPathReferenceSubmodelRepoRequestBuilder(submodelIdentifier, idShortPath, level);
		try {
			HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(localVarRequestBuilder.build(), HttpResponse.BodyHandlers.ofInputStream());
			if (memberVarResponseInterceptor != null) {
				memberVarResponseInterceptor.accept(localVarResponse);
			}
			try {
				if (localVarResponse.statusCode() / 100 != 2) {
					throw getApiException("getSubmodelElementByPathReferenceSubmodelRepo", localVarResponse);
				}
				return new ApiResponse<Reference>(localVarResponse.statusCode(), localVarResponse.headers().map(),
						localVarResponse.body() == null ? null : memberVarObjectMapper.readValue(localVarResponse.body(), new TypeReference<Reference>() {
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

	private HttpRequest.Builder getSubmodelElementByPathReferenceSubmodelRepoRequestBuilder(String submodelIdentifier, String idShortPath, String level) throws ApiException {
		// verify the required parameter 'submodelIdentifier' is set
		if (submodelIdentifier == null) {
			throw new ApiException(400, "Missing the required parameter 'submodelIdentifier' when calling getSubmodelElementByPathReferenceSubmodelRepo");
		}
		// verify the required parameter 'idShortPath' is set
		if (idShortPath == null) {
			throw new ApiException(400, "Missing the required parameter 'idShortPath' when calling getSubmodelElementByPathReferenceSubmodelRepo");
		}

		HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

		String localVarPath = "/submodels/{submodelIdentifier}/submodel-elements/{idShortPath}/$reference".replace("{submodelIdentifier}", ApiClient.urlEncode(submodelIdentifier.toString())).replace("{idShortPath}",
				ApiClient.urlEncode(idShortPath.toString()));

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
	 * Returns a specific submodel element from the Submodel at a specified path
	 * 
	 * @param submodelIdentifier
	 *            The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
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
	public SubmodelElement getSubmodelElementByPathSubmodelRepo(String submodelIdentifier, String idShortPath, String level, String extent) throws ApiException {

		ApiResponse<SubmodelElement> localVarResponse = getSubmodelElementByPathSubmodelRepoWithHttpInfo(submodelIdentifier, idShortPath, level, extent);
		return localVarResponse.getData();
	}

	/**
	 * Returns a specific submodel element from the Submodel at a specified path
	 * 
	 * @param submodelIdentifier
	 *            The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
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
	public ApiResponse<SubmodelElement> getSubmodelElementByPathSubmodelRepoWithHttpInfo(String submodelIdentifier, String idShortPath, String level, String extent) throws ApiException {
		String submodelIdentifierAsBytes = ApiClient.base64UrlEncode(submodelIdentifier);
		return getSubmodelElementByPathSubmodelRepoWithHttpInfoNoUrlEncoding(submodelIdentifierAsBytes, idShortPath, level, extent);

	}

	/**
	 * Returns a specific submodel element from the Submodel at a specified path
	 * 
	 * @param submodelIdentifier
	 *            The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
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
	public ApiResponse<SubmodelElement> getSubmodelElementByPathSubmodelRepoWithHttpInfoNoUrlEncoding(String submodelIdentifier, String idShortPath, String level, String extent) throws ApiException {
		HttpRequest.Builder localVarRequestBuilder = getSubmodelElementByPathSubmodelRepoRequestBuilder(submodelIdentifier, idShortPath, level, extent);
		try {
			HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(localVarRequestBuilder.build(), HttpResponse.BodyHandlers.ofInputStream());
			if (memberVarResponseInterceptor != null) {
				memberVarResponseInterceptor.accept(localVarResponse);
			}
			try {
				if (localVarResponse.statusCode() / 100 != 2) {
					throw getApiException("getSubmodelElementByPathSubmodelRepo", localVarResponse);
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

	private HttpRequest.Builder getSubmodelElementByPathSubmodelRepoRequestBuilder(String submodelIdentifier, String idShortPath, String level, String extent) throws ApiException {
		// verify the required parameter 'submodelIdentifier' is set
		if (submodelIdentifier == null) {
			throw new ApiException(400, "Missing the required parameter 'submodelIdentifier' when calling getSubmodelElementByPathSubmodelRepo");
		}
		// verify the required parameter 'idShortPath' is set
		if (idShortPath == null) {
			throw new ApiException(400, "Missing the required parameter 'idShortPath' when calling getSubmodelElementByPathSubmodelRepo");
		}

		HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

		String localVarPath = "/submodels/{submodelIdentifier}/submodel-elements/{idShortPath}".replace("{submodelIdentifier}", ApiClient.urlEncode(submodelIdentifier.toString())).replace("{idShortPath}",
				ApiClient.urlEncode(idShortPath.toString()));

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
	 * @param submodelIdentifier
	 *            The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
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
	public SubmodelElementValue getSubmodelElementByPathValueOnlySubmodelRepo(String submodelIdentifier, String idShortPath, String level, String extent) throws ApiException {

		ApiResponse<SubmodelElementValue> localVarResponse = getSubmodelElementByPathValueOnlySubmodelRepoWithHttpInfo(submodelIdentifier, idShortPath, level, extent);
		return localVarResponse.getData();
	}

	/**
	 * Returns a specific submodel element from the Submodel at a specified path in
	 * the ValueOnly representation
	 * 
	 * @param submodelIdentifier
	 *            The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
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
	public ApiResponse<SubmodelElementValue> getSubmodelElementByPathValueOnlySubmodelRepoWithHttpInfo(String submodelIdentifier, String idShortPath, String level, String extent) throws ApiException {
		String submodelIdentifierAsBytes = ApiClient.base64UrlEncode(submodelIdentifier);
		return getSubmodelElementByPathValueOnlySubmodelRepoWithHttpInfoNoUrlEncoding(submodelIdentifierAsBytes, idShortPath, level, extent);

	}

	/**
	 * Returns a specific submodel element from the Submodel at a specified path in
	 * the ValueOnly representation
	 * 
	 * @param submodelIdentifier
	 *            The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
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
	public ApiResponse<SubmodelElementValue> getSubmodelElementByPathValueOnlySubmodelRepoWithHttpInfoNoUrlEncoding(String submodelIdentifier, String idShortPath, String level, String extent) throws ApiException {
		HttpRequest.Builder localVarRequestBuilder = getSubmodelElementByPathValueOnlySubmodelRepoRequestBuilder(submodelIdentifier, idShortPath, level, extent);
		try {
			HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(localVarRequestBuilder.build(), HttpResponse.BodyHandlers.ofInputStream());
			if (memberVarResponseInterceptor != null) {
				memberVarResponseInterceptor.accept(localVarResponse);
			}
			try {
				if (localVarResponse.statusCode() / 100 != 2) {
					throw getApiException("getSubmodelElementByPathValueOnlySubmodelRepo", localVarResponse);
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

	private HttpRequest.Builder getSubmodelElementByPathValueOnlySubmodelRepoRequestBuilder(String submodelIdentifier, String idShortPath, String level, String extent) throws ApiException {
		// verify the required parameter 'submodelIdentifier' is set
		if (submodelIdentifier == null) {
			throw new ApiException(400, "Missing the required parameter 'submodelIdentifier' when calling getSubmodelElementByPathValueOnlySubmodelRepo");
		}
		// verify the required parameter 'idShortPath' is set
		if (idShortPath == null) {
			throw new ApiException(400, "Missing the required parameter 'idShortPath' when calling getSubmodelElementByPathValueOnlySubmodelRepo");
		}

		HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

		String localVarPath = "/submodels/{submodelIdentifier}/submodel-elements/{idShortPath}/$value".replace("{submodelIdentifier}", ApiClient.urlEncode(submodelIdentifier.toString())).replace("{idShortPath}",
				ApiClient.urlEncode(idShortPath.toString()));

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
	 * Asynchronously invokes an Operation at a specified path
	 * 
	 * @param submodelIdentifier
	 *            The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
	 * @param idShortPath
	 *            IdShort path to the submodel element (dot-separated) (required)
	 * @param operationRequest
	 *            Operation request object (required)
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public void invokeOperationAsync(String submodelIdentifier, String idShortPath, OperationRequest operationRequest) throws ApiException {

		invokeOperationAsyncWithHttpInfo(submodelIdentifier, idShortPath, operationRequest);
	}

	/**
	 * Asynchronously invokes an Operation at a specified path
	 * 
	 * @param submodelIdentifier
	 *            The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
	 * @param idShortPath
	 *            IdShort path to the submodel element (dot-separated) (required)
	 * @param operationRequest
	 *            Operation request object (required)
	 * @return ApiResponse&lt;Void&gt;
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public ApiResponse<Void> invokeOperationAsyncWithHttpInfo(String submodelIdentifier, String idShortPath, OperationRequest operationRequest) throws ApiException {
		String submodelIdentifierAsBytes = ApiClient.base64UrlEncode(submodelIdentifier);
		return invokeOperationAsyncWithHttpInfoNoUrlEncoding(submodelIdentifierAsBytes, idShortPath, operationRequest);

	}

	/**
	 * Asynchronously invokes an Operation at a specified path
	 * 
	 * @param submodelIdentifier
	 *            The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
	 * @param idShortPath
	 *            IdShort path to the submodel element (dot-separated) (required)
	 * @param operationRequest
	 *            Operation request object (required)
	 * @return ApiResponse&lt;Void&gt;
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public ApiResponse<Void> invokeOperationAsyncWithHttpInfoNoUrlEncoding(String submodelIdentifier, String idShortPath, OperationRequest operationRequest) throws ApiException {
		HttpRequest.Builder localVarRequestBuilder = invokeOperationAsyncRequestBuilder(submodelIdentifier, idShortPath, operationRequest);
		try {
			HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(localVarRequestBuilder.build(), HttpResponse.BodyHandlers.ofInputStream());
			if (memberVarResponseInterceptor != null) {
				memberVarResponseInterceptor.accept(localVarResponse);
			}
			try {
				if (localVarResponse.statusCode() / 100 != 2) {
					throw getApiException("invokeOperationAsync", localVarResponse);
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

		String localVarPath = "/submodels/{submodelIdentifier}/submodel-elements/{idShortPath}/invoke-async".replace("{submodelIdentifier}", ApiClient.urlEncode(submodelIdentifier.toString())).replace("{idShortPath}",
				ApiClient.urlEncode(idShortPath.toString()));

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
	 * @param aasIdentifier
	 *            The Asset Administration Shell’s unique id
	 *            (UTF8-BASE64-URL-encoded) (required)
	 * @param submodelIdentifier
	 *            The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
	 * @param idShortPath
	 *            IdShort path to the submodel element (dot-separated) (required)
	 * @param operationRequestValueOnly
	 *            Operation request object (required)
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public void invokeOperationAsyncValueOnly(String aasIdentifier, String submodelIdentifier, String idShortPath, OperationRequestValueOnly operationRequestValueOnly) throws ApiException {

		invokeOperationAsyncValueOnlyWithHttpInfo(aasIdentifier, submodelIdentifier, idShortPath, operationRequestValueOnly);
	}

	/**
	 * Asynchronously invokes an Operation at a specified path
	 * 
	 * @param aasIdentifier
	 *            The Asset Administration Shell’s unique id
	 *            (UTF8-BASE64-URL-encoded) (required)
	 * @param submodelIdentifier
	 *            The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
	 * @param idShortPath
	 *            IdShort path to the submodel element (dot-separated) (required)
	 * @param operationRequestValueOnly
	 *            Operation request object (required)
	 * @return ApiResponse&lt;Void&gt;
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public ApiResponse<Void> invokeOperationAsyncValueOnlyWithHttpInfo(String aasIdentifier, String submodelIdentifier, String idShortPath, OperationRequestValueOnly operationRequestValueOnly) throws ApiException {
		String aasIdentifierAsBytes = ApiClient.base64UrlEncode(aasIdentifier);
		String submodelIdentifierAsBytes = ApiClient.base64UrlEncode(submodelIdentifier);
		return invokeOperationAsyncValueOnlyWithHttpInfoNoUrlEncoding(aasIdentifierAsBytes, submodelIdentifierAsBytes, idShortPath, operationRequestValueOnly);

	}

	/**
	 * Asynchronously invokes an Operation at a specified path
	 * 
	 * @param aasIdentifier
	 *            The Asset Administration Shell’s unique id
	 *            (UTF8-BASE64-URL-encoded) (required)
	 * @param submodelIdentifier
	 *            The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
	 * @param idShortPath
	 *            IdShort path to the submodel element (dot-separated) (required)
	 * @param operationRequestValueOnly
	 *            Operation request object (required)
	 * @return ApiResponse&lt;Void&gt;
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public ApiResponse<Void> invokeOperationAsyncValueOnlyWithHttpInfoNoUrlEncoding(String aasIdentifier, String submodelIdentifier, String idShortPath, OperationRequestValueOnly operationRequestValueOnly) throws ApiException {
		HttpRequest.Builder localVarRequestBuilder = invokeOperationAsyncValueOnlyRequestBuilder(aasIdentifier, submodelIdentifier, idShortPath, operationRequestValueOnly);
		try {
			HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(localVarRequestBuilder.build(), HttpResponse.BodyHandlers.ofInputStream());
			if (memberVarResponseInterceptor != null) {
				memberVarResponseInterceptor.accept(localVarResponse);
			}
			try {
				if (localVarResponse.statusCode() / 100 != 2) {
					throw getApiException("invokeOperationAsyncValueOnly", localVarResponse);
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

	private HttpRequest.Builder invokeOperationAsyncValueOnlyRequestBuilder(String aasIdentifier, String submodelIdentifier, String idShortPath, OperationRequestValueOnly operationRequestValueOnly) throws ApiException {
		// verify the required parameter 'aasIdentifier' is set
		if (aasIdentifier == null) {
			throw new ApiException(400, "Missing the required parameter 'aasIdentifier' when calling invokeOperationAsyncValueOnly");
		}
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

		String localVarPath = "/submodels/{submodelIdentifier}/submodel-elements/{idShortPath}/invoke-async/$value".replace("{aasIdentifier}", ApiClient.urlEncode(aasIdentifier.toString()))
				.replace("{submodelIdentifier}", ApiClient.urlEncode(submodelIdentifier.toString())).replace("{idShortPath}", ApiClient.urlEncode(idShortPath.toString()));

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
	 * Synchronously or asynchronously invokes an Operation at a specified path
	 * 
	 * @param submodelIdentifier
	 *            The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
	 * @param idShortPath
	 *            IdShort path to the submodel element (dot-separated) (required)
	 * @param operationRequest
	 *            Operation request object (required)
	 * @param async
	 *            Determines whether an operation invocation is performed
	 *            asynchronously or synchronously (optional, default to false)
	 * @return OperationResult
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public OperationResult invokeOperationSubmodelRepo(String submodelIdentifier, String idShortPath, OperationRequest operationRequest, Boolean async) throws ApiException {

		ApiResponse<OperationResult> localVarResponse = invokeOperationSubmodelRepoWithHttpInfo(submodelIdentifier, idShortPath, operationRequest, async);
		return localVarResponse.getData();
	}

	/**
	 * Synchronously or asynchronously invokes an Operation at a specified path
	 * 
	 * @param submodelIdentifier
	 *            The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
	 * @param idShortPath
	 *            IdShort path to the submodel element (dot-separated) (required)
	 * @param operationRequest
	 *            Operation request object (required)
	 * @param async
	 *            Determines whether an operation invocation is performed
	 *            asynchronously or synchronously (optional, default to false)
	 * @return ApiResponse&lt;OperationResult&gt;
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public ApiResponse<OperationResult> invokeOperationSubmodelRepoWithHttpInfo(String submodelIdentifier, String idShortPath, OperationRequest operationRequest, Boolean async) throws ApiException {
		String submodelIdentifierAsBytes = ApiClient.base64UrlEncode(submodelIdentifier);
		return invokeOperationSubmodelRepoWithHttpInfoNoUrlEncoding(submodelIdentifierAsBytes, idShortPath, operationRequest, async);

	}

	/**
	 * Synchronously or asynchronously invokes an Operation at a specified path
	 * 
	 * @param submodelIdentifier
	 *            The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
	 * @param idShortPath
	 *            IdShort path to the submodel element (dot-separated) (required)
	 * @param operationRequest
	 *            Operation request object (required)
	 * @param async
	 *            Determines whether an operation invocation is performed
	 *            asynchronously or synchronously (optional, default to false)
	 * @return ApiResponse&lt;OperationResult&gt;
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public ApiResponse<OperationResult> invokeOperationSubmodelRepoWithHttpInfoNoUrlEncoding(String submodelIdentifier, String idShortPath, OperationRequest operationRequest, Boolean async) throws ApiException {
		HttpRequest.Builder localVarRequestBuilder = invokeOperationSubmodelRepoRequestBuilder(submodelIdentifier, idShortPath, operationRequest, async);
		try {
			HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(localVarRequestBuilder.build(), HttpResponse.BodyHandlers.ofInputStream());
			if (memberVarResponseInterceptor != null) {
				memberVarResponseInterceptor.accept(localVarResponse);
			}
			try {
				if (localVarResponse.statusCode() / 100 != 2) {
					throw getApiException("invokeOperationSubmodelRepo", localVarResponse);
				}
				return new ApiResponse<OperationResult>(localVarResponse.statusCode(), localVarResponse.headers().map(),
						localVarResponse.body() == null ? null : memberVarObjectMapper.readValue(localVarResponse.body(), new TypeReference<OperationResult>() {
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

	private HttpRequest.Builder invokeOperationSubmodelRepoRequestBuilder(String submodelIdentifier, String idShortPath, OperationRequest operationRequest, Boolean async) throws ApiException {
		// verify the required parameter 'submodelIdentifier' is set
		if (submodelIdentifier == null) {
			throw new ApiException(400, "Missing the required parameter 'submodelIdentifier' when calling invokeOperationSubmodelRepo");
		}
		// verify the required parameter 'idShortPath' is set
		if (idShortPath == null) {
			throw new ApiException(400, "Missing the required parameter 'idShortPath' when calling invokeOperationSubmodelRepo");
		}
		// verify the required parameter 'operationRequest' is set
		if (operationRequest == null) {
			throw new ApiException(400, "Missing the required parameter 'operationRequest' when calling invokeOperationSubmodelRepo");
		}

		HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

		String localVarPath = "/submodels/{submodelIdentifier}/submodel-elements/{idShortPath}/invoke".replace("{submodelIdentifier}", ApiClient.urlEncode(submodelIdentifier.toString())).replace("{idShortPath}",
				ApiClient.urlEncode(idShortPath.toString()));

		List<Pair> localVarQueryParams = new ArrayList<>();
		StringJoiner localVarQueryStringJoiner = new StringJoiner("&");
		String localVarQueryParameterBaseName;
		localVarQueryParameterBaseName = "async";
		localVarQueryParams.addAll(ApiClient.parameterToPairs("async", async));

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
	 * Synchronously or asynchronously invokes an Operation at a specified path
	 * 
	 * @param aasIdentifier
	 *            The Asset Administration Shell’s unique id
	 *            (UTF8-BASE64-URL-encoded) (required)
	 * @param submodelIdentifier
	 *            The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
	 * @param idShortPath
	 *            IdShort path to the submodel element (dot-separated) (required)
	 * @param operationRequestValueOnly
	 *            Operation request object (required)
	 * @param async
	 *            Determines whether an operation invocation is performed
	 *            asynchronously or synchronously (optional, default to false)
	 * @return OperationResultValueOnly
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public OperationResultValueOnly invokeOperationValueOnly(String aasIdentifier, String submodelIdentifier, String idShortPath, OperationRequestValueOnly operationRequestValueOnly, Boolean async) throws ApiException {

		ApiResponse<OperationResultValueOnly> localVarResponse = invokeOperationValueOnlyWithHttpInfo(aasIdentifier, submodelIdentifier, idShortPath, operationRequestValueOnly, async);
		return localVarResponse.getData();
	}

	/**
	 * Synchronously or asynchronously invokes an Operation at a specified path
	 * 
	 * @param aasIdentifier
	 *            The Asset Administration Shell’s unique id
	 *            (UTF8-BASE64-URL-encoded) (required)
	 * @param submodelIdentifier
	 *            The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
	 * @param idShortPath
	 *            IdShort path to the submodel element (dot-separated) (required)
	 * @param operationRequestValueOnly
	 *            Operation request object (required)
	 * @param async
	 *            Determines whether an operation invocation is performed
	 *            asynchronously or synchronously (optional, default to false)
	 * @return ApiResponse&lt;OperationResultValueOnly&gt;
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public ApiResponse<OperationResultValueOnly> invokeOperationValueOnlyWithHttpInfo(String aasIdentifier, String submodelIdentifier, String idShortPath, OperationRequestValueOnly operationRequestValueOnly, Boolean async)
			throws ApiException {
		String aasIdentifierAsBytes = ApiClient.base64UrlEncode(aasIdentifier);
		String submodelIdentifierAsBytes = ApiClient.base64UrlEncode(submodelIdentifier);
		return invokeOperationValueOnlyWithHttpInfoNoUrlEncoding(aasIdentifierAsBytes, submodelIdentifierAsBytes, idShortPath, operationRequestValueOnly, async);

	}


	/**
	 * Synchronously or asynchronously invokes an Operation at a specified path
	 * 
	 * @param aasIdentifier
	 *            The Asset Administration Shell’s unique id
	 *            (UTF8-BASE64-URL-encoded) (required)
	 * @param submodelIdentifier
	 *            The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
	 * @param idShortPath
	 *            IdShort path to the submodel element (dot-separated) (required)
	 * @param operationRequestValueOnly
	 *            Operation request object (required)
	 * @param async
	 *            Determines whether an operation invocation is performed
	 *            asynchronously or synchronously (optional, default to false)
	 * @return ApiResponse&lt;OperationResultValueOnly&gt;
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public ApiResponse<OperationResultValueOnly> invokeOperationValueOnlyWithHttpInfoNoUrlEncoding(String aasIdentifier, String submodelIdentifier, String idShortPath, OperationRequestValueOnly operationRequestValueOnly, Boolean async)
			throws ApiException {
		HttpRequest.Builder localVarRequestBuilder = invokeOperationValueOnlyRequestBuilder(aasIdentifier, submodelIdentifier, idShortPath, operationRequestValueOnly, async);
		try {
			HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(localVarRequestBuilder.build(), HttpResponse.BodyHandlers.ofInputStream());
			if (memberVarResponseInterceptor != null) {
				memberVarResponseInterceptor.accept(localVarResponse);
			}
			try {
				if (localVarResponse.statusCode() / 100 != 2) {
					throw getApiException("invokeOperationValueOnly", localVarResponse);
				}
				return new ApiResponse<OperationResultValueOnly>(localVarResponse.statusCode(), localVarResponse.headers().map(),
						localVarResponse.body() == null ? null : memberVarObjectMapper.readValue(localVarResponse.body(), new TypeReference<OperationResultValueOnly>() {
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

	private HttpRequest.Builder invokeOperationValueOnlyRequestBuilder(String aasIdentifier, String submodelIdentifier, String idShortPath, OperationRequestValueOnly operationRequestValueOnly, Boolean async) throws ApiException {
		// verify the required parameter 'aasIdentifier' is set
		if (aasIdentifier == null) {
			throw new ApiException(400, "Missing the required parameter 'aasIdentifier' when calling invokeOperationValueOnly");
		}
		// verify the required parameter 'submodelIdentifier' is set
		if (submodelIdentifier == null) {
			throw new ApiException(400, "Missing the required parameter 'submodelIdentifier' when calling invokeOperationValueOnly");
		}
		// verify the required parameter 'idShortPath' is set
		if (idShortPath == null) {
			throw new ApiException(400, "Missing the required parameter 'idShortPath' when calling invokeOperationValueOnly");
		}
		// verify the required parameter 'operationRequestValueOnly' is set
		if (operationRequestValueOnly == null) {
			throw new ApiException(400, "Missing the required parameter 'operationRequestValueOnly' when calling invokeOperationValueOnly");
		}

		HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

		String localVarPath = "/submodels/{submodelIdentifier}/submodel-elements/{idShortPath}/invoke/$value".replace("{aasIdentifier}", ApiClient.urlEncode(aasIdentifier.toString()))
				.replace("{submodelIdentifier}", ApiClient.urlEncode(submodelIdentifier.toString())).replace("{idShortPath}", ApiClient.urlEncode(idShortPath.toString()));

		List<Pair> localVarQueryParams = new ArrayList<>();
		StringJoiner localVarQueryStringJoiner = new StringJoiner("&");
		String localVarQueryParameterBaseName;
		localVarQueryParameterBaseName = "async";
		localVarQueryParams.addAll(ApiClient.parameterToPairs("async", async));

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
	 * Updates an existing Submodel
	 * 
	 * @param submodelIdentifier
	 *            The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
	 * @param submodel
	 *            Submodel object (required)
	 * @param level
	 *            Determines the structural depth of the respective resource content
	 *            (optional, default to core)
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public void patchSubmodelById(String submodelIdentifier, Submodel submodel, String level) throws ApiException {

		patchSubmodelByIdWithHttpInfo(submodelIdentifier, submodel, level);
	}

	/**
	 * Updates an existing Submodel
	 * 
	 * @param submodelIdentifier
	 *            The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
	 * @param submodel
	 *            Submodel object (required)
	 * @param level
	 *            Determines the structural depth of the respective resource content
	 *            (optional, default to core)
	 * @return ApiResponse&lt;Void&gt;
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public ApiResponse<Void> patchSubmodelByIdWithHttpInfo(String submodelIdentifier, Submodel submodel, String level) throws ApiException {
		String submodelIdentifierAsBytes = ApiClient.base64UrlEncode(submodelIdentifier);
		return patchSubmodelByIdWithHttpInfoNoUrlEncoding(submodelIdentifierAsBytes, submodel, level);

	}

	/**
	 * Updates an existing Submodel
	 * 
	 * @param submodelIdentifier
	 *            The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
	 * @param submodel
	 *            Submodel object (required)
	 * @param level
	 *            Determines the structural depth of the respective resource content
	 *            (optional, default to core)
	 * @return ApiResponse&lt;Void&gt;
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public ApiResponse<Void> patchSubmodelByIdWithHttpInfoNoUrlEncoding(String submodelIdentifier, Submodel submodel, String level) throws ApiException {
		HttpRequest.Builder localVarRequestBuilder = patchSubmodelByIdRequestBuilder(submodelIdentifier, submodel, level);
		try {
			HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(localVarRequestBuilder.build(), HttpResponse.BodyHandlers.ofInputStream());
			if (memberVarResponseInterceptor != null) {
				memberVarResponseInterceptor.accept(localVarResponse);
			}
			try {
				if (localVarResponse.statusCode() / 100 != 2) {
					throw getApiException("patchSubmodelById", localVarResponse);
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

	private HttpRequest.Builder patchSubmodelByIdRequestBuilder(String submodelIdentifier, Submodel submodel, String level) throws ApiException {
		// verify the required parameter 'submodelIdentifier' is set
		if (submodelIdentifier == null) {
			throw new ApiException(400, "Missing the required parameter 'submodelIdentifier' when calling patchSubmodelById");
		}
		// verify the required parameter 'submodel' is set
		if (submodel == null) {
			throw new ApiException(400, "Missing the required parameter 'submodel' when calling patchSubmodelById");
		}

		HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

		String localVarPath = "/submodels/{submodelIdentifier}".replace("{submodelIdentifier}", ApiClient.urlEncode(submodelIdentifier.toString()));

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
	 * Updates the metadata attributes of an existing Submodel
	 * 
	 * @param submodelIdentifier
	 *            The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
	 * @param submodelMetadata
	 *            The metadata attributes of the Submodel object (required)
	 * @param level
	 *            Determines the structural depth of the respective resource content
	 *            (optional, default to core)
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public void patchSubmodelByIdMetadata(String submodelIdentifier, SubmodelMetadata submodelMetadata, String level) throws ApiException {

		patchSubmodelByIdMetadataWithHttpInfo(submodelIdentifier, submodelMetadata, level);
	}

	/**
	 * Updates the metadata attributes of an existing Submodel
	 * 
	 * @param submodelIdentifier
	 *            The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
	 * @param submodelMetadata
	 *            The metadata attributes of the Submodel object (required)
	 * @param level
	 *            Determines the structural depth of the respective resource content
	 *            (optional, default to core)
	 * @return ApiResponse&lt;Void&gt;
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public ApiResponse<Void> patchSubmodelByIdMetadataWithHttpInfo(String submodelIdentifier, SubmodelMetadata submodelMetadata, String level) throws ApiException {
		String submodelIdentifierAsBytes = ApiClient.base64UrlEncode(submodelIdentifier);
		return patchSubmodelByIdMetadataWithHttpInfoNoUrlEncoding(submodelIdentifierAsBytes, submodelMetadata, level);

	}

	/**
	 * Updates the metadata attributes of an existing Submodel
	 * 
	 * @param submodelIdentifier
	 *            The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
	 * @param submodelMetadata
	 *            The metadata attributes of the Submodel object (required)
	 * @param level
	 *            Determines the structural depth of the respective resource content
	 *            (optional, default to core)
	 * @return ApiResponse&lt;Void&gt;
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public ApiResponse<Void> patchSubmodelByIdMetadataWithHttpInfoNoUrlEncoding(String submodelIdentifier, SubmodelMetadata submodelMetadata, String level) throws ApiException {
		HttpRequest.Builder localVarRequestBuilder = patchSubmodelByIdMetadataRequestBuilder(submodelIdentifier, submodelMetadata, level);
		try {
			HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(localVarRequestBuilder.build(), HttpResponse.BodyHandlers.ofInputStream());
			if (memberVarResponseInterceptor != null) {
				memberVarResponseInterceptor.accept(localVarResponse);
			}
			try {
				if (localVarResponse.statusCode() / 100 != 2) {
					throw getApiException("patchSubmodelByIdMetadata", localVarResponse);
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

	private HttpRequest.Builder patchSubmodelByIdMetadataRequestBuilder(String submodelIdentifier, SubmodelMetadata submodelMetadata, String level) throws ApiException {
		// verify the required parameter 'submodelIdentifier' is set
		if (submodelIdentifier == null) {
			throw new ApiException(400, "Missing the required parameter 'submodelIdentifier' when calling patchSubmodelByIdMetadata");
		}
		// verify the required parameter 'submodelMetadata' is set
		if (submodelMetadata == null) {
			throw new ApiException(400, "Missing the required parameter 'submodelMetadata' when calling patchSubmodelByIdMetadata");
		}

		HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

		String localVarPath = "/submodels/{submodelIdentifier}/$metadata".replace("{submodelIdentifier}", ApiClient.urlEncode(submodelIdentifier.toString()));

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
	 * Updates the values of an existing Submodel
	 * 
	 * @param submodelIdentifier
	 *            The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
	 * @param submodelValue
	 *            Submodel object in its ValueOnly representation (required)
	 * @param level
	 *            Determines the structural depth of the respective resource content
	 *            (optional, default to core)
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public void patchSubmodelByIdValueOnly(String submodelIdentifier, SubmodelValue submodelValue, String level) throws ApiException {

		patchSubmodelByIdValueOnlyWithHttpInfo(submodelIdentifier, submodelValue, level);
	}

	/**
	 * Updates the values of an existing Submodel
	 * 
	 * @param submodelIdentifier
	 *            The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
	 * @param submodelValue
	 *            Submodel object in its ValueOnly representation (required)
	 * @param level
	 *            Determines the structural depth of the respective resource content
	 *            (optional, default to core)
	 * @return ApiResponse&lt;Void&gt;
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public ApiResponse<Void> patchSubmodelByIdValueOnlyWithHttpInfo(String submodelIdentifier, SubmodelValue submodelValue, String level) throws ApiException {
		String submodelIdentifierAsBytes = ApiClient.base64UrlEncode(submodelIdentifier);
		return patchSubmodelByIdValueOnlyWithHttpInfoNoUrlEncoding(submodelIdentifierAsBytes, submodelValue, level);

	}

	/**
	 * Updates the values of an existing Submodel
	 * 
	 * @param submodelIdentifier
	 *            The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
	 * @param submodelValue
	 *            Submodel object in its ValueOnly representation (required)
	 * @param level
	 *            Determines the structural depth of the respective resource content
	 *            (optional, default to core)
	 * @return ApiResponse&lt;Void&gt;
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public ApiResponse<Void> patchSubmodelByIdValueOnlyWithHttpInfoNoUrlEncoding(String submodelIdentifier, SubmodelValue submodelValue, String level) throws ApiException {
		HttpRequest.Builder localVarRequestBuilder = patchSubmodelByIdValueOnlyRequestBuilder(submodelIdentifier, submodelValue, level);
		try {
			HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(localVarRequestBuilder.build(), HttpResponse.BodyHandlers.ofInputStream());
			if (memberVarResponseInterceptor != null) {
				memberVarResponseInterceptor.accept(localVarResponse);
			}
			try {
				if (localVarResponse.statusCode() / 100 != 2) {
					throw getApiException("patchSubmodelByIdValueOnly", localVarResponse);
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

	private HttpRequest.Builder patchSubmodelByIdValueOnlyRequestBuilder(String submodelIdentifier, SubmodelValue submodelValue, String level) throws ApiException {
		// verify the required parameter 'submodelIdentifier' is set
		if (submodelIdentifier == null) {
			throw new ApiException(400, "Missing the required parameter 'submodelIdentifier' when calling patchSubmodelByIdValueOnly");
		}
		// verify the required parameter 'submodelValue' is set
		if (submodelValue == null) {
			throw new ApiException(400, "Missing the required parameter 'submodelValue' when calling patchSubmodelByIdValueOnly");
		}

		HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

		String localVarPath = "/submodels/{submodelIdentifier}/$value".replace("{submodelIdentifier}", ApiClient.urlEncode(submodelIdentifier.toString()));

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
	 * Updates the metadata attributes an existing SubmodelElement
	 * 
	 * @param submodelIdentifier
	 *            The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
	 * @param idShortPath
	 *            IdShort path to the submodel element (dot-separated) (required)
	 * @param submodelElementMetadata
	 *            Metadata attributes of the SubmodelElement (required)
	 * @param level
	 *            Determines the structural depth of the respective resource content
	 *            (optional, default to core)
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public void patchSubmodelElementByPathMetadataSubmodelRepo(String submodelIdentifier, String idShortPath, SubmodelElementMetadata submodelElementMetadata, String level) throws ApiException {

		patchSubmodelElementByPathMetadataSubmodelRepoWithHttpInfo(submodelIdentifier, idShortPath, submodelElementMetadata, level);
	}

	/**
	 * Updates the metadata attributes an existing SubmodelElement
	 * 
	 * @param submodelIdentifier
	 *            The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
	 * @param idShortPath
	 *            IdShort path to the submodel element (dot-separated) (required)
	 * @param submodelElementMetadata
	 *            Metadata attributes of the SubmodelElement (required)
	 * @param level
	 *            Determines the structural depth of the respective resource content
	 *            (optional, default to core)
	 * @return ApiResponse&lt;Void&gt;
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public ApiResponse<Void> patchSubmodelElementByPathMetadataSubmodelRepoWithHttpInfo(String submodelIdentifier, String idShortPath, SubmodelElementMetadata submodelElementMetadata, String level) throws ApiException {
		String submodelIdentifierAsBytes = ApiClient.base64UrlEncode(submodelIdentifier);
		return patchSubmodelElementByPathMetadataSubmodelRepoWithHttpInfoNoUrlEncoding(submodelIdentifierAsBytes, idShortPath, submodelElementMetadata, level);

	}

	/**
	 * Updates the metadata attributes an existing SubmodelElement
	 * 
	 * @param submodelIdentifier
	 *            The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
	 * @param idShortPath
	 *            IdShort path to the submodel element (dot-separated) (required)
	 * @param submodelElementMetadata
	 *            Metadata attributes of the SubmodelElement (required)
	 * @param level
	 *            Determines the structural depth of the respective resource content
	 *            (optional, default to core)
	 * @return ApiResponse&lt;Void&gt;
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public ApiResponse<Void> patchSubmodelElementByPathMetadataSubmodelRepoWithHttpInfoNoUrlEncoding(String submodelIdentifier, String idShortPath, SubmodelElementMetadata submodelElementMetadata, String level) throws ApiException {
		HttpRequest.Builder localVarRequestBuilder = patchSubmodelElementByPathMetadataSubmodelRepoRequestBuilder(submodelIdentifier, idShortPath, submodelElementMetadata, level);
		try {
			HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(localVarRequestBuilder.build(), HttpResponse.BodyHandlers.ofInputStream());
			if (memberVarResponseInterceptor != null) {
				memberVarResponseInterceptor.accept(localVarResponse);
			}
			try {
				if (localVarResponse.statusCode() / 100 != 2) {
					throw getApiException("patchSubmodelElementByPathMetadataSubmodelRepo", localVarResponse);
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

	private HttpRequest.Builder patchSubmodelElementByPathMetadataSubmodelRepoRequestBuilder(String submodelIdentifier, String idShortPath, SubmodelElementMetadata submodelElementMetadata, String level) throws ApiException {
		// verify the required parameter 'submodelIdentifier' is set
		if (submodelIdentifier == null) {
			throw new ApiException(400, "Missing the required parameter 'submodelIdentifier' when calling patchSubmodelElementByPathMetadataSubmodelRepo");
		}
		// verify the required parameter 'idShortPath' is set
		if (idShortPath == null) {
			throw new ApiException(400, "Missing the required parameter 'idShortPath' when calling patchSubmodelElementByPathMetadataSubmodelRepo");
		}
		// verify the required parameter 'submodelElementMetadata' is set
		if (submodelElementMetadata == null) {
			throw new ApiException(400, "Missing the required parameter 'submodelElementMetadata' when calling patchSubmodelElementByPathMetadataSubmodelRepo");
		}

		HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

		String localVarPath = "/submodels/{submodelIdentifier}/submodel-elements/{idShortPath}/$metadata".replace("{submodelIdentifier}", ApiClient.urlEncode(submodelIdentifier.toString())).replace("{idShortPath}",
				ApiClient.urlEncode(idShortPath.toString()));

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
	 * Updates an existing SubmodelElement
	 * 
	 * @param submodelIdentifier
	 *            The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
	 * @param idShortPath
	 *            IdShort path to the submodel element (dot-separated) (required)
	 * @param submodelElement
	 *            SubmodelElement object (required)
	 * @param level
	 *            Determines the structural depth of the respective resource content
	 *            (optional, default to core)
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public void patchSubmodelElementByPathSubmodelRepo(String submodelIdentifier, String idShortPath, SubmodelElement submodelElement, String level) throws ApiException {

		patchSubmodelElementByPathSubmodelRepoWithHttpInfo(submodelIdentifier, idShortPath, submodelElement, level);
	}

	/**
	 * Updates an existing SubmodelElement
	 * 
	 * @param submodelIdentifier
	 *            The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
	 * @param idShortPath
	 *            IdShort path to the submodel element (dot-separated) (required)
	 * @param submodelElement
	 *            SubmodelElement object (required)
	 * @param level
	 *            Determines the structural depth of the respective resource content
	 *            (optional, default to core)
	 * @return ApiResponse&lt;Void&gt;
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public ApiResponse<Void> patchSubmodelElementByPathSubmodelRepoWithHttpInfo(String submodelIdentifier, String idShortPath, SubmodelElement submodelElement, String level) throws ApiException {
		String submodelIdentifierAsBytes = ApiClient.base64UrlEncode(submodelIdentifier);
		return patchSubmodelElementByPathSubmodelRepoWithHttpInfoNoUrlEncoding(submodelIdentifierAsBytes, idShortPath, submodelElement, level);

	}

	/**
	 * Updates an existing SubmodelElement
	 * 
	 * @param submodelIdentifier
	 *            The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
	 * @param idShortPath
	 *            IdShort path to the submodel element (dot-separated) (required)
	 * @param submodelElement
	 *            SubmodelElement object (required)
	 * @param level
	 *            Determines the structural depth of the respective resource content
	 *            (optional, default to core)
	 * @return ApiResponse&lt;Void&gt;
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public ApiResponse<Void> patchSubmodelElementByPathSubmodelRepoWithHttpInfoNoUrlEncoding(String submodelIdentifier, String idShortPath, SubmodelElement submodelElement, String level) throws ApiException {
		HttpRequest.Builder localVarRequestBuilder = patchSubmodelElementByPathSubmodelRepoRequestBuilder(submodelIdentifier, idShortPath, submodelElement, level);
		try {
			HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(localVarRequestBuilder.build(), HttpResponse.BodyHandlers.ofInputStream());
			if (memberVarResponseInterceptor != null) {
				memberVarResponseInterceptor.accept(localVarResponse);
			}
			try {
				if (localVarResponse.statusCode() / 100 != 2) {
					throw getApiException("patchSubmodelElementByPathSubmodelRepo", localVarResponse);
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

	private HttpRequest.Builder patchSubmodelElementByPathSubmodelRepoRequestBuilder(String submodelIdentifier, String idShortPath, SubmodelElement submodelElement, String level) throws ApiException {
		// verify the required parameter 'submodelIdentifier' is set
		if (submodelIdentifier == null) {
			throw new ApiException(400, "Missing the required parameter 'submodelIdentifier' when calling patchSubmodelElementByPathSubmodelRepo");
		}
		// verify the required parameter 'idShortPath' is set
		if (idShortPath == null) {
			throw new ApiException(400, "Missing the required parameter 'idShortPath' when calling patchSubmodelElementByPathSubmodelRepo");
		}
		// verify the required parameter 'submodelElement' is set
		if (submodelElement == null) {
			throw new ApiException(400, "Missing the required parameter 'submodelElement' when calling patchSubmodelElementByPathSubmodelRepo");
		}

		HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

		String localVarPath = "/submodels/{submodelIdentifier}/submodel-elements/{idShortPath}".replace("{submodelIdentifier}", ApiClient.urlEncode(submodelIdentifier.toString())).replace("{idShortPath}",
				ApiClient.urlEncode(idShortPath.toString()));

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
	 * Updates the value of an existing SubmodelElement
	 * 
	 * @param submodelIdentifier
	 *            The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
	 * @param idShortPath
	 *            IdShort path to the submodel element (dot-separated) (required)
	 * @param submodelElementValue
	 *            The SubmodelElement in its ValueOnly representation (required)
	 * @param level
	 *            Determines the structural depth of the respective resource content
	 *            (optional, default to core)
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public void patchSubmodelElementByPathValueOnlySubmodelRepo(String submodelIdentifier, String idShortPath, SubmodelElementValue submodelElementValue, String level) throws ApiException {

		patchSubmodelElementByPathValueOnlySubmodelRepoWithHttpInfo(submodelIdentifier, idShortPath, submodelElementValue, level);
	}

	/**
	 * Updates the value of an existing SubmodelElement
	 * 
	 * @param submodelIdentifier
	 *            The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
	 * @param idShortPath
	 *            IdShort path to the submodel element (dot-separated) (required)
	 * @param submodelElementValue
	 *            The SubmodelElement in its ValueOnly representation (required)
	 * @param level
	 *            Determines the structural depth of the respective resource content
	 *            (optional, default to core)
	 * @return ApiResponse&lt;Void&gt;
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public ApiResponse<Void> patchSubmodelElementByPathValueOnlySubmodelRepoWithHttpInfo(String submodelIdentifier, String idShortPath, SubmodelElementValue submodelElementValue, String level) throws ApiException {
		String submodelIdentifierAsBytes = ApiClient.base64UrlEncode(submodelIdentifier);
		return patchSubmodelElementByPathValueOnlySubmodelRepoWithHttpInfoNoUrlEncoding(submodelIdentifierAsBytes, idShortPath, submodelElementValue, level);

	}

	/**
	 * Updates the value of an existing SubmodelElement
	 * 
	 * @param submodelIdentifier
	 *            The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
	 * @param idShortPath
	 *            IdShort path to the submodel element (dot-separated) (required)
	 * @param submodelElementValue
	 *            The SubmodelElement in its ValueOnly representation (required)
	 * @param level
	 *            Determines the structural depth of the respective resource content
	 *            (optional, default to core)
	 * @return ApiResponse&lt;Void&gt;
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public ApiResponse<Void> patchSubmodelElementByPathValueOnlySubmodelRepoWithHttpInfoNoUrlEncoding(String submodelIdentifier, String idShortPath, SubmodelElementValue submodelElementValue, String level) throws ApiException {
		HttpRequest.Builder localVarRequestBuilder = patchSubmodelElementByPathValueOnlySubmodelRepoRequestBuilder(submodelIdentifier, idShortPath, submodelElementValue, level);
		try {
			HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(localVarRequestBuilder.build(), HttpResponse.BodyHandlers.ofInputStream());
			if (memberVarResponseInterceptor != null) {
				memberVarResponseInterceptor.accept(localVarResponse);
			}
			try {
				if (localVarResponse.statusCode() / 100 != 2) {
					throw getApiException("patchSubmodelElementByPathValueOnlySubmodelRepo", localVarResponse);
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

	private HttpRequest.Builder patchSubmodelElementByPathValueOnlySubmodelRepoRequestBuilder(String submodelIdentifier, String idShortPath, SubmodelElementValue submodelElementValue, String level) throws ApiException {
		// verify the required parameter 'submodelIdentifier' is set
		if (submodelIdentifier == null) {
			throw new ApiException(400, "Missing the required parameter 'submodelIdentifier' when calling patchSubmodelElementByPathValueOnlySubmodelRepo");
		}
		// verify the required parameter 'idShortPath' is set
		if (idShortPath == null) {
			throw new ApiException(400, "Missing the required parameter 'idShortPath' when calling patchSubmodelElementByPathValueOnlySubmodelRepo");
		}
		// verify the required parameter 'submodelElementValue' is set
		if (submodelElementValue == null) {
			throw new ApiException(400, "Missing the required parameter 'submodelElementValue' when calling patchSubmodelElementByPathValueOnlySubmodelRepo");
		}

		HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

		String localVarPath = "/submodels/{submodelIdentifier}/submodel-elements/{idShortPath}/$value".replace("{submodelIdentifier}", ApiClient.urlEncode(submodelIdentifier.toString())).replace("{idShortPath}",
				ApiClient.urlEncode(idShortPath.toString()));

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
	 * Creates a new submodel element at a specified path within submodel elements
	 * hierarchy
	 * 
	 * @param submodelIdentifier
	 *            The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
	 * @param idShortPath
	 *            IdShort path to the submodel element (dot-separated) (required)
	 * @param submodelElement
	 *            Requested submodel element (required)
	 * @return SubmodelElement
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public SubmodelElement postSubmodelElementByPathSubmodelRepo(String submodelIdentifier, String idShortPath, SubmodelElement submodelElement) throws ApiException {

		ApiResponse<SubmodelElement> localVarResponse = postSubmodelElementByPathSubmodelRepoWithHttpInfo(submodelIdentifier, idShortPath, submodelElement);
		return localVarResponse.getData();
	}

	/**
	 * Creates a new submodel element at a specified path within submodel elements
	 * hierarchy
	 * 
	 * @param submodelIdentifier
	 *            The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
	 * @param idShortPath
	 *            IdShort path to the submodel element (dot-separated) (required)
	 * @param submodelElement
	 *            Requested submodel element (required)
	 * @return ApiResponse&lt;SubmodelElement&gt;
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public ApiResponse<SubmodelElement> postSubmodelElementByPathSubmodelRepoWithHttpInfo(String submodelIdentifier, String idShortPath, SubmodelElement submodelElement) throws ApiException {
		String submodelIdentifierAsBytes = ApiClient.base64UrlEncode(submodelIdentifier);
		return postSubmodelElementByPathSubmodelRepoWithHttpInfoNoUrlEncoding(submodelIdentifierAsBytes, idShortPath, submodelElement);

	}

	/**
	 * Creates a new submodel element at a specified path within submodel elements
	 * hierarchy
	 * 
	 * @param submodelIdentifier
	 *            The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
	 * @param idShortPath
	 *            IdShort path to the submodel element (dot-separated) (required)
	 * @param submodelElement
	 *            Requested submodel element (required)
	 * @return ApiResponse&lt;SubmodelElement&gt;
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public ApiResponse<SubmodelElement> postSubmodelElementByPathSubmodelRepoWithHttpInfoNoUrlEncoding(String submodelIdentifier, String idShortPath, SubmodelElement submodelElement) throws ApiException {
		HttpRequest.Builder localVarRequestBuilder = postSubmodelElementByPathSubmodelRepoRequestBuilder(submodelIdentifier, idShortPath, submodelElement);
		try {
			HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(localVarRequestBuilder.build(), HttpResponse.BodyHandlers.ofInputStream());
			if (memberVarResponseInterceptor != null) {
				memberVarResponseInterceptor.accept(localVarResponse);
			}
			try {
				if (localVarResponse.statusCode() / 100 != 2) {
					throw getApiException("postSubmodelElementByPathSubmodelRepo", localVarResponse);
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

	private HttpRequest.Builder postSubmodelElementByPathSubmodelRepoRequestBuilder(String submodelIdentifier, String idShortPath, SubmodelElement submodelElement) throws ApiException {
		// verify the required parameter 'submodelIdentifier' is set
		if (submodelIdentifier == null) {
			throw new ApiException(400, "Missing the required parameter 'submodelIdentifier' when calling postSubmodelElementByPathSubmodelRepo");
		}
		// verify the required parameter 'idShortPath' is set
		if (idShortPath == null) {
			throw new ApiException(400, "Missing the required parameter 'idShortPath' when calling postSubmodelElementByPathSubmodelRepo");
		}
		// verify the required parameter 'submodelElement' is set
		if (submodelElement == null) {
			throw new ApiException(400, "Missing the required parameter 'submodelElement' when calling postSubmodelElementByPathSubmodelRepo");
		}

		HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

		String localVarPath = "/submodels/{submodelIdentifier}/submodel-elements/{idShortPath}".replace("{submodelIdentifier}", ApiClient.urlEncode(submodelIdentifier.toString())).replace("{idShortPath}",
				ApiClient.urlEncode(idShortPath.toString()));

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
	 * Creates a new submodel element
	 * 
	 * @param submodelIdentifier
	 *            The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
	 * @param submodelElement
	 *            Requested submodel element (required)
	 * @return SubmodelElement
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public SubmodelElement postSubmodelElementSubmodelRepo(String submodelIdentifier, SubmodelElement submodelElement) throws ApiException {

		ApiResponse<SubmodelElement> localVarResponse = postSubmodelElementSubmodelRepoWithHttpInfo(submodelIdentifier, submodelElement);
		return localVarResponse.getData();
	}

	/**
	 * Creates a new submodel element
	 * 
	 * @param submodelIdentifier
	 *            The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
	 * @param submodelElement
	 *            Requested submodel element (required)
	 * @return ApiResponse&lt;SubmodelElement&gt;
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public ApiResponse<SubmodelElement> postSubmodelElementSubmodelRepoWithHttpInfo(String submodelIdentifier, SubmodelElement submodelElement) throws ApiException {
		String submodelIdentifierAsBytes = ApiClient.base64UrlEncode(submodelIdentifier);
		return postSubmodelElementSubmodelRepoWithHttpInfoNoUrlEncoding(submodelIdentifierAsBytes, submodelElement);

	}

	/**
	 * Creates a new submodel element
	 * 
	 * @param submodelIdentifier
	 *            The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
	 * @param submodelElement
	 *            Requested submodel element (required)
	 * @return ApiResponse&lt;SubmodelElement&gt;
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public ApiResponse<SubmodelElement> postSubmodelElementSubmodelRepoWithHttpInfoNoUrlEncoding(String submodelIdentifier, SubmodelElement submodelElement) throws ApiException {
		HttpRequest.Builder localVarRequestBuilder = postSubmodelElementSubmodelRepoRequestBuilder(submodelIdentifier, submodelElement);
		try {
			HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(localVarRequestBuilder.build(), HttpResponse.BodyHandlers.ofInputStream());
			if (memberVarResponseInterceptor != null) {
				memberVarResponseInterceptor.accept(localVarResponse);
			}
			try {
				if (localVarResponse.statusCode() / 100 != 2) {
					throw getApiException("postSubmodelElementSubmodelRepo", localVarResponse);
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

	private HttpRequest.Builder postSubmodelElementSubmodelRepoRequestBuilder(String submodelIdentifier, SubmodelElement submodelElement) throws ApiException {
		// verify the required parameter 'submodelIdentifier' is set
		if (submodelIdentifier == null) {
			throw new ApiException(400, "Missing the required parameter 'submodelIdentifier' when calling postSubmodelElementSubmodelRepo");
		}
		// verify the required parameter 'submodelElement' is set
		if (submodelElement == null) {
			throw new ApiException(400, "Missing the required parameter 'submodelElement' when calling postSubmodelElementSubmodelRepo");
		}

		HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

		String localVarPath = "/submodels/{submodelIdentifier}/submodel-elements".replace("{submodelIdentifier}", ApiClient.urlEncode(submodelIdentifier.toString()));

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
	 * Uploads file content to an existing submodel element at a specified path
	 * within submodel elements hierarchy
	 * 
	 * @param submodelIdentifier
	 *            The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
	 * @param idShortPath
	 *            IdShort path to the submodel element (dot-separated) (required)
	 * @param fileName
	 *            (optional)
	 * @param _file
	 *            (optional)
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public void putFileByPathSubmodelRepo(String submodelIdentifier, String idShortPath, String fileName, File _file) throws ApiException {

		putFileByPathSubmodelRepoWithHttpInfo(submodelIdentifier, idShortPath, fileName, _file);
	}

	/**
	 * Uploads file content to an existing submodel element at a specified path
	 * within submodel elements hierarchy
	 * 
	 * @param submodelIdentifier
	 *            The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
	 * @param idShortPath
	 *            IdShort path to the submodel element (dot-separated) (required)
	 * @param fileName
	 *            (optional)
	 * @param _file
	 *            (optional)
	 * @return ApiResponse&lt;Void&gt;
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public ApiResponse<Void> putFileByPathSubmodelRepoWithHttpInfo(String submodelIdentifier, String idShortPath, String fileName, File _file) throws ApiException {
		String submodelIdentifierAsBytes = ApiClient.base64UrlEncode(submodelIdentifier);
		return putFileByPathSubmodelRepoWithHttpInfoNoUrlEncoding(submodelIdentifierAsBytes, idShortPath, fileName, _file);

	}

	/**
	 * Uploads file content to an existing submodel element at a specified path
	 * within submodel elements hierarchy
	 * 
	 * @param submodelIdentifier
	 *            The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
	 * @param idShortPath
	 *            IdShort path to the submodel element (dot-separated) (required)
	 * @param fileName
	 *            (optional)
	 * @param _file
	 *            (optional)
	 * @return ApiResponse&lt;Void&gt;
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public ApiResponse<Void> putFileByPathSubmodelRepoWithHttpInfoNoUrlEncoding(String submodelIdentifier, String idShortPath, String fileName, File _file) throws ApiException {
		HttpRequest.Builder localVarRequestBuilder = putFileByPathSubmodelRepoRequestBuilder(submodelIdentifier, idShortPath, fileName, _file);
		try {
			HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(localVarRequestBuilder.build(), HttpResponse.BodyHandlers.ofInputStream());
			if (memberVarResponseInterceptor != null) {
				memberVarResponseInterceptor.accept(localVarResponse);
			}
			try {
				if (localVarResponse.statusCode() / 100 != 2) {
					throw getApiException("putFileByPathSubmodelRepo", localVarResponse);
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

	private HttpRequest.Builder putFileByPathSubmodelRepoRequestBuilder(String submodelIdentifier, String idShortPath, String fileName, File _file) throws ApiException {
		// verify the required parameter 'submodelIdentifier' is set
		if (submodelIdentifier == null) {
			throw new ApiException(400, "Missing the required parameter 'submodelIdentifier' when calling putFileByPathSubmodelRepo");
		}
		// verify the required parameter 'idShortPath' is set
		if (idShortPath == null) {
			throw new ApiException(400, "Missing the required parameter 'idShortPath' when calling putFileByPathSubmodelRepo");
		}

		HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

		String localVarPath = "/submodels/{submodelIdentifier}/submodel-elements/{idShortPath}/attachment".replace("{submodelIdentifier}", ApiClient.urlEncode(submodelIdentifier.toString())).replace("{idShortPath}",
				ApiClient.urlEncode(idShortPath.toString()));

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
			formDataPublisher = HttpRequest.BodyPublishers.ofInputStream(() -> new ByteArrayInputStream(formOutputStream.toByteArray()));
		}
		localVarRequestBuilder.header("Content-Type", entity.getContentType().getValue()).method("PUT", formDataPublisher);
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
	 * Updates an existing submodel element at a specified path within submodel
	 * elements hierarchy
	 * 
	 * @param submodelIdentifier
	 *            The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
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
	public void putSubmodelElementByPathSubmodelRepo(String submodelIdentifier, String idShortPath, SubmodelElement submodelElement, String level) throws ApiException {

		putSubmodelElementByPathSubmodelRepoWithHttpInfo(submodelIdentifier, idShortPath, submodelElement, level);
	}

	/**
	 * Updates an existing submodel element at a specified path within submodel
	 * elements hierarchy
	 * 
	 * @param submodelIdentifier
	 *            The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
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
	public ApiResponse<Void> putSubmodelElementByPathSubmodelRepoWithHttpInfo(String submodelIdentifier, String idShortPath, SubmodelElement submodelElement, String level) throws ApiException {
		String submodelIdentifierAsBytes = ApiClient.base64UrlEncode(submodelIdentifier);
		return putSubmodelElementByPathSubmodelRepoWithHttpInfoNoUrlEncoding(submodelIdentifierAsBytes, idShortPath, submodelElement, level);

	}

	/**
	 * Updates an existing submodel element at a specified path within submodel
	 * elements hierarchy
	 * 
	 * @param submodelIdentifier
	 *            The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
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
	public ApiResponse<Void> putSubmodelElementByPathSubmodelRepoWithHttpInfoNoUrlEncoding(String submodelIdentifier, String idShortPath, SubmodelElement submodelElement, String level) throws ApiException {
		HttpRequest.Builder localVarRequestBuilder = putSubmodelElementByPathSubmodelRepoRequestBuilder(submodelIdentifier, idShortPath, submodelElement, level);
		try {
			HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(localVarRequestBuilder.build(), HttpResponse.BodyHandlers.ofInputStream());
			if (memberVarResponseInterceptor != null) {
				memberVarResponseInterceptor.accept(localVarResponse);
			}
			try {
				if (localVarResponse.statusCode() / 100 != 2) {
					throw getApiException("putSubmodelElementByPathSubmodelRepo", localVarResponse);
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

	private HttpRequest.Builder putSubmodelElementByPathSubmodelRepoRequestBuilder(String submodelIdentifier, String idShortPath, SubmodelElement submodelElement, String level) throws ApiException {
		// verify the required parameter 'submodelIdentifier' is set
		if (submodelIdentifier == null) {
			throw new ApiException(400, "Missing the required parameter 'submodelIdentifier' when calling putSubmodelElementByPathSubmodelRepo");
		}
		// verify the required parameter 'idShortPath' is set
		if (idShortPath == null) {
			throw new ApiException(400, "Missing the required parameter 'idShortPath' when calling putSubmodelElementByPathSubmodelRepo");
		}
		// verify the required parameter 'submodelElement' is set
		if (submodelElement == null) {
			throw new ApiException(400, "Missing the required parameter 'submodelElement' when calling putSubmodelElementByPathSubmodelRepo");
		}

		HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

		String localVarPath = "/submodels/{submodelIdentifier}/submodel-elements/{idShortPath}".replace("{submodelIdentifier}", ApiClient.urlEncode(submodelIdentifier.toString())).replace("{idShortPath}",
				ApiClient.urlEncode(idShortPath.toString()));

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

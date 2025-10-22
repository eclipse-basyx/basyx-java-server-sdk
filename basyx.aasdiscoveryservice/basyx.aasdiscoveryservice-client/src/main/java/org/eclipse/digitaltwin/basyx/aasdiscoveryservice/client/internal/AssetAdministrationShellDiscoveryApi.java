/*******************************************************************************
 * Copyright (C) 2025 the Eclipse BaSyx Authors
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
package org.eclipse.digitaltwin.basyx.aasdiscoveryservice.client.internal;

import java.io.IOException;
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
import org.eclipse.digitaltwin.aas4j.v3.model.SpecificAssetId;
import org.eclipse.digitaltwin.basyx.aasdiscoveryservice.core.model.AssetLink;
import org.eclipse.digitaltwin.basyx.client.internal.ApiClient;
import org.eclipse.digitaltwin.basyx.client.internal.ApiException;
import org.eclipse.digitaltwin.basyx.client.internal.ApiResponse;
import org.eclipse.digitaltwin.basyx.client.internal.Pair;
import org.eclipse.digitaltwin.basyx.client.internal.authorization.TokenManager;
import org.eclipse.digitaltwin.basyx.core.exceptions.AccessTokenRetrievalException;
import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.eclipse.digitaltwin.basyx.http.Base64UrlEncodedIdentifier;
import org.eclipse.digitaltwin.basyx.http.pagination.Base64UrlEncodedCursorResult;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.Generated;

@Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class AssetAdministrationShellDiscoveryApi {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String baseUri;
    private final Consumer<HttpRequest.Builder> requestInterceptor;
    private final Duration readTimeout;
    private TokenManager tokenManager;

    public AssetAdministrationShellDiscoveryApi() {
        this(new ApiClient());
    }

    public AssetAdministrationShellDiscoveryApi(TokenManager tokenManager) {
        this(new ApiClient());
        this.tokenManager = tokenManager;
    }

    public AssetAdministrationShellDiscoveryApi(ApiClient apiClient) {
        this.httpClient = apiClient.getHttpClient();
        this.objectMapper = apiClient.getObjectMapper();
        this.baseUri = apiClient.getBaseUri();
        this.requestInterceptor = apiClient.getRequestInterceptor();
        this.readTimeout = apiClient.getReadTimeout();
    }

    public AssetAdministrationShellDiscoveryApi(String baseUri) {
        this(new ApiClient(HttpClient.newBuilder(), new JsonMapperFactory().create(new SimpleAbstractTypeResolverFactory().create()), baseUri));
    }

    public AssetAdministrationShellDiscoveryApi(String baseUri, TokenManager tokenManager) {
        this(new ApiClient(HttpClient.newBuilder(), new JsonMapperFactory().create(new SimpleAbstractTypeResolverFactory().create()), baseUri));
        this.tokenManager = tokenManager;
    }

    public CursorResult<List<String>> getAllAssetAdministrationShellIdsByAssetLink(List<AssetLink> assetIds, Integer limit, String cursor) throws ApiException {
        ApiResponse<Base64UrlEncodedCursorResult<List<String>>> response = getAllAssetAdministrationShellIdsByAssetLinkWithHttpInfo(assetIds, limit, cursor);
        return response.getData();
    }

    public ApiResponse<Base64UrlEncodedCursorResult<List<String>>> getAllAssetAdministrationShellIdsByAssetLinkWithHttpInfo(List<AssetLink> assetIds, Integer limit, String cursor) throws ApiException {
        HttpRequest.Builder requestBuilder = getAllShellIdsRequestBuilder(assetIds, limit, cursor);
        try {
			HttpResponse<String> response = httpClient.send(requestBuilder.build(),
					HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() / 100 != 2) {
				throw getApiException("getAllAssetAdministrationShellIdsByAssetLink", response);
			}
            return new ApiResponse<>(
                    response.statusCode(),
                    response.headers().map(),
                    objectMapper.readValue(response.body(), new TypeReference<Base64UrlEncodedCursorResult<List<String>>>() {})
            );
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ApiException(e);
        }
    }

    private HttpRequest.Builder getAllShellIdsRequestBuilder(List<AssetLink> assetIds, Integer limit, String cursor) throws ApiException {
        HttpRequest.Builder builder = HttpRequest.newBuilder();
        String path = "/lookup/shells";

        List<String> encodedAssetIds = new ArrayList<>();
        assetIds.forEach(assetLink -> {
            try {
                String assetLinkJson = objectMapper.writeValueAsString(assetLink);
                encodedAssetIds.add(new Base64UrlEncodedIdentifier(assetLinkJson).getEncodedIdentifier());
            } catch (IOException e) {
                throw new ApiException(e);
            }
        });

        List<Pair> queryParams = ApiClient.parameterToPairs("multi", "assetIds", encodedAssetIds);
        queryParams.addAll(ApiClient.parameterToPairs("limit", limit));
        queryParams.addAll(ApiClient.parameterToPairs("cursor", cursor));

        StringJoiner query = new StringJoiner("&");
        for (Pair p : queryParams) {
			query.add(p.getName() + "=" + p.getValue());
		}

        builder.uri(URI.create(baseUri + path + (query.length() > 0 ? "?" + query : "")));
        builder.header("Accept", "application/json");
        addAuthorizationHeaderIfAuthIsEnabled(builder);
        builder.method("GET", HttpRequest.BodyPublishers.noBody());
        if (readTimeout != null) {
			builder.timeout(readTimeout);
		}
        if (requestInterceptor != null) {
			requestInterceptor.accept(builder);
		}
        return builder;
    }

    public List<SpecificAssetId> getAllAssetLinksById(String aasIdentifier) throws ApiException {
        HttpRequest.Builder builder = HttpRequest.newBuilder();
        builder.uri(URI.create(baseUri + "/lookup/shells/" + new Base64UrlEncodedIdentifier(aasIdentifier).getEncodedIdentifier()));
        builder.header("Accept", "application/json");
        addAuthorizationHeaderIfAuthIsEnabled(builder);
        builder.method("GET", HttpRequest.BodyPublishers.noBody());
        if (readTimeout != null) {
			builder.timeout(readTimeout);
		}
        if (requestInterceptor != null) {
			requestInterceptor.accept(builder);
		}

        try {
			HttpResponse<String> response = httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() / 100 != 2) {
				throw getApiException("getAllAssetLinksById", response);
			}
            return objectMapper.readValue(response.body(), new TypeReference<List<SpecificAssetId>>() {});
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ApiException(e);
        }
    }

    public List<SpecificAssetId> postAllAssetLinksById(String aasIdentifier, List<SpecificAssetId> assetIds) throws ApiException {
        HttpRequest.Builder builder = HttpRequest.newBuilder();
        builder.uri(URI.create(baseUri + "/lookup/shells/" + new Base64UrlEncodedIdentifier(aasIdentifier).getEncodedIdentifier()));
        builder.header("Content-Type", "application/json");
        builder.header("Accept", "application/json");
        addAuthorizationHeaderIfAuthIsEnabled(builder);

        try {
            byte[] body = objectMapper.writeValueAsBytes(assetIds);
            builder.method("POST", HttpRequest.BodyPublishers.ofByteArray(body));
        } catch (IOException e) {
            throw new ApiException(e);
        }

        if (readTimeout != null) {
			builder.timeout(readTimeout);
		}
        if (requestInterceptor != null) {
			requestInterceptor.accept(builder);
		}

        try {
			HttpResponse<String> response = httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() / 100 != 2) {
				throw getApiException("postAllAssetLinksById", response);
			}
            return objectMapper.readValue(response.body(), new TypeReference<List<SpecificAssetId>>() {});
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ApiException(e);
        }
    }

    public void deleteAllAssetLinksById(String aasIdentifier) throws ApiException {
        HttpRequest.Builder builder = HttpRequest.newBuilder();
        builder.uri(URI.create(baseUri + "/lookup/shells/" + new Base64UrlEncodedIdentifier(aasIdentifier).getEncodedIdentifier()));
        builder.header("Accept", "application/json");
        addAuthorizationHeaderIfAuthIsEnabled(builder);
        builder.method("DELETE", HttpRequest.BodyPublishers.noBody());
        if (readTimeout != null) {
			builder.timeout(readTimeout);
		}
        if (requestInterceptor != null) {
			requestInterceptor.accept(builder);
		}

        try {
			HttpResponse<String> response = httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() / 100 != 2) {
				throw getApiException("deleteAllAssetLinksById", response);
			}
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ApiException(e);
        }
    }

    public Object getDescription() throws ApiException {
        HttpRequest.Builder builder = HttpRequest.newBuilder();
        builder.uri(URI.create(baseUri + "/description"));
        builder.header("Accept", "application/json");
        addAuthorizationHeaderIfAuthIsEnabled(builder);
        builder.method("GET", HttpRequest.BodyPublishers.noBody());
        if (readTimeout != null) {
			builder.timeout(readTimeout);
		}
        if (requestInterceptor != null) {
			requestInterceptor.accept(builder);
		}

        try {
			HttpResponse<String> response = httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() / 100 != 2) {
				throw getApiException("getDescription", response);
			}
            return objectMapper.readValue(response.body(), Object.class);
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ApiException(e);
        }
    }

	private ApiException getApiException(String operationId, HttpResponse<String> response) throws IOException {
		return new ApiException(response.statusCode(),
				operationId + " call failed with: " + response.statusCode() + " - " + response.body(),
				response.headers(), response.body());
    }

    private void addAuthorizationHeaderIfAuthIsEnabled(HttpRequest.Builder builder) {
        if (tokenManager != null) {
            try {
                builder.header("Authorization", "Bearer " + tokenManager.getAccessToken());
            } catch (IOException e) {
                throw new AccessTokenRetrievalException("Unable to request access token");
            }
        }
    }
}
